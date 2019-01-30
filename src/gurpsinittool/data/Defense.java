package gurpsinittool.data;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gurpsinittool.data.ActorBase.ActorStatus;
import gurpsinittool.data.ActorBase.BasicTrait;
import gurpsinittool.data.HitLocations.HitLocation;
import gurpsinittool.data.HitLocations.LocationType;
import gurpsinittool.data.traits.InjuryTolerance;
import gurpsinittool.data.traits.SupernaturalDurability;
import gurpsinittool.data.traits.Vulnerability;
import gurpsinittool.util.DieRoller;

public class Defense {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(Defense.class.getName());
	
	// List of valid defense types
	public enum DefenseType {Parry, Block, Dodge, None};
	// Possible defense results
	public enum DefenseResult {CritSuccess, Success, ShieldHit, Failure, CritFailure}; 

	// General Options
	public DefenseType type;
	public boolean ee = false;
	public boolean retreat = false;
	public boolean side = false;
	public boolean stunned = false;
	public boolean shield = false;
	public String position = ""; // TODO: should probably not be string, but ENUM!
	public int otherMod = 0;
	
	// Key Inputs
	public int roll; // The defense roll
	public DR overrideDR; // Override the actor's base DR
	public Damage damage; // The amount of damage accompanying the attack
	public HitLocation location; // The location of the attack and the location of any injury sustained. 
	// NOTE: location may change based on injury tolerance (ie attack to vitals against 'No Vitals' 
	// target will be reported as injury to the torso)
	
	// Results
	public int effectiveDefense;
	public DefenseResult result;
	public int fatigue;
	public int injury;
	public int shieldDamage;
	public boolean majorWound;
	public boolean cripplingInjury;
	
	/**
	 * Set the initial defense options based on the actor state
	 * @param actor - the actor whose state to use when setting initial options
	 */
	public void setInitialOptions(Actor actor) {
		int effParry = actor.getCurrentDefenseValue(DefenseType.Parry);
    	int effBlock = actor.getCurrentDefenseValue(DefenseType.Block);
    	int effDodge = actor.getCurrentDefenseValue(DefenseType.Dodge);
    	int shieldDB = actor.getTraitValueInt(BasicTrait.Shield_DB);
    	int shieldHP = actor.getTraitValueInt(BasicTrait.Shield_HP);
    	
		// Pick the best by default
    	if ((effParry > effDodge) && (effParry >= effBlock)) {
   			type = DefenseType.Parry;
    	} else if ((effBlock > effDodge) && (effBlock > effParry)) {
    		type = DefenseType.Block;
    	} else {
    		type = DefenseType.Dodge;
    	}
    	
    	// Set default options
    	shield = (shieldDB > 0 && actor.getTempInt("shieldDamage") < shieldHP);
    	// Disabled is the same as 'incapacitated' and sometimes allows active defenses as through stunned
    	if (actor.isStunned() || actor.hasStatus(ActorStatus.Disabled))
    		stunned = true;
    	// Set position
    	if (actor.hasStatus(Actor.ActorStatus.Kneeling))
    		position = "Kneeling";
    	if (actor.hasStatus(Actor.ActorStatus.Prone))
    		position = "Prone";
	}

	/**
	 * Calculate defense result variables
	 * @param actor - the actor making the defense
	 * @param roll - the dice roll value for this defense
	 */
	public void calcDefenseResults(final Actor actor) {
		calcEffectiveDefense(actor); // Calculate effectiveDefense
		calcDefenseResult(actor); // Calcuate the 'result'
		location = InjuryTolerance.getAdjustedLocation(actor, location); // correct location for any Injury Tolerances
		determineImpact(actor); // Figure out all the injury items: injury, shieldDamage, cripplingInjury, and majorWound
	}
	
	/**
	 * Calculate effective defense value
	 */
	private void calcEffectiveDefense(final Actor actor) {
		effectiveDefense = actor.getCurrentDefenseValue(type);

		if (type != DefenseType.None) {
			if (ee) {
				effectiveDefense += 2;
				fatigue = 1;
			} else { fatigue = 0; }
			if (retreat)
				effectiveDefense += (type == DefenseType.Dodge) ? 3 : 1;
			if (side)
				effectiveDefense -= 2;
			if (stunned)
				effectiveDefense -= 4;
			if (shield)
				effectiveDefense += actor.getTraitValueInt(BasicTrait.Shield_DB);

			//Position
			if ("Kneeling".equals(position))
				effectiveDefense -= 2;
			if ("Prone".equals(position))
				effectiveDefense -= 3;

			// Other
			effectiveDefense += otherMod;
		}
	}
	
	/** 
	 * Determine the defense success/failure/etc
	 * @param actor
	 */
	private void calcDefenseResult(final Actor actor) {
		int shieldDB = shield ? actor.getTraitValueInt(BasicTrait.Shield_DB) : 0;

		// CritSuccess, Success, ShieldHit, Failure
		if (type == DefenseType.None) { // No attempted defense
			result = DefenseResult.Failure;
		} else if (DieRoller.isCritSuccess(roll, effectiveDefense)) { // Crit Defense!
			result = DefenseResult.CritSuccess;
		} else if (DieRoller.isCritFailure(roll, effectiveDefense)) { // Crit Failed Defense!
			result = DefenseResult.CritFailure;
		} else if (DieRoller.isFailure(roll, effectiveDefense)) { // Hit
			result = DefenseResult.Failure;
		} else if (DieRoller.isFailure(roll, effectiveDefense - shieldDB)) { // Shield Hit
			result = DefenseResult.ShieldHit;
		}  else {
			result = DefenseResult.Success;
		}
	}
    
	/** 
     * Calculate values for injury, shieldDamage, cripplingInjury, and majorWound
     */
    private void determineImpact(final Actor actor) {
		int coverDR = 0;
		
		// Set defaults
    	injury = 0;
    	shieldDamage = 0;
    	majorWound = false;
    	cripplingInjury = false;

    	// Calculate injury
    	// 
    	int shieldDR = actor.getTraitValueInt(BasicTrait.Shield_DR);
    	int shieldHP = actor.getTraitValueInt(BasicTrait.Shield_HP);
    	int hitPoints = actor.getTraitValueInt(BasicTrait.HP);
    	switch (result) {
    	case CritSuccess:
    	case Success:
    		return;
    	case ShieldHit:
    		// Calculate shield damage
    		int shieldBasicDamage = (int) (damage.basicDamage - Math.floor(shieldDR/damage.armorDivisor));
    		// Apply min/max values
    		shieldBasicDamage = Math.min((int)Math.ceil(shieldHP/4), shieldBasicDamage);
    		shieldBasicDamage = Math.max(0, shieldBasicDamage);
    		shieldDamage = (int) (Math.floor(shieldBasicDamage*InjuryTolerance.woundingModifierHomogenous(damage.type)));
    		// Min damage 1 if any got through DR
    		shieldDamage = (shieldDamage <= 0 && shieldBasicDamage > 0)?1:shieldDamage; 
    		// Calculate total cover DR provided (including armor divisor)
    		coverDR = (int) (Math.floor((shieldDR + Math.ceil(shieldHP/4))/damage.armorDivisor));
    	case Failure:
    	case CritFailure:
    		// Calculate actual basic damage to the target, including any cover DR
			int totalDR = overrideDR.getDRforType(damage.type) + location.extraDR;
    		int penetratingDamage = (int) (damage.basicDamage - coverDR - Math.floor(totalDR/damage.armorDivisor));
    		penetratingDamage = Math.max(0,penetratingDamage);
			// Calculate injury to the target
			calculateInjury(actor, penetratingDamage);
    		
    		// Check for crippling
    		if (location.cripplingThreshold != 0) {
    			int cripplingThreshold = (int) Math.floor(hitPoints * location.cripplingThreshold + 1.00001);
    			if (injury >= cripplingThreshold) {
    				injury = cripplingThreshold;
    				cripplingInjury = SupernaturalDurability.canCripple(actor);
    				majorWound = true;
    			}
    		} 
    		else { // Check for major wound
    			int majorWoundThreshold = (int) Math.floor(hitPoints * 1/2 + 1.00001);
    			if (injury >= majorWoundThreshold) {
    				majorWound = true;
    			}
    		}
    	}
    }
    
    /**
     * Determine the injury to the actor resulting from the specified penetrating damage, including Injury Tolerance and other factors
     * @param actor - the actor to use when calculating injury
     * @param penetratingDamage - the amount of damage that penetrated DR
     */
    private void calculateInjury(final Actor actor, int penetratingDamage) {
    	// Use double for calculations
    	Double calcInjury = (double) penetratingDamage;
    	
    	// Apply any vulnerability's wounding multiplier
    	calcInjury *= Vulnerability.getVulnerabilityMultiplier(actor, damage.type);
		
    	// Apply wounding modifier based on hit location, damage type, and any injury tolerances
		calcInjury *= getWoundingModifier(actor);
		
		// Apply damage reduction if present
		calcInjury /= InjuryTolerance.getDamageReduction(actor);
		
		// Apply injury limit based on injury tolerance (diffuse)
		calcInjury = Math.min(calcInjury, InjuryTolerance.getInjuryLimit(actor, damage));
		
		// Convert to integer, enforce minimum damage (1 if any got through DR)
		injury = (calcInjury <= 1 && penetratingDamage > 0)?1:calcInjury.intValue();
    }
    
    /**
     * Get the wounding modifier based on location and injury tolerances
     * @param actor - the actor to use
     * @return the calculated wounding modifier
     */
    private double getWoundingModifier(final Actor actor) {
    	Double woundingModifier = location.getWoundingModifier(damage.type);
    	return InjuryTolerance.getWoundingModifier(actor, damage.type, woundingModifier);
    }
}
