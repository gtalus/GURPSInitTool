package gurpsinittool.data;

import java.util.ArrayList;

import gurpsinittool.data.ActorBase.ActorStatus;
import gurpsinittool.data.ActorBase.BasicTrait;
import gurpsinittool.data.HitLocations.HitLocation;
import gurpsinittool.data.HitLocations.LocationType;
import gurpsinittool.util.DieRoller;

public class Defense {
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
	public Damage damage; // The amount of damage inflicted
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
	public void calcDefenseResults(Actor actor) {
		calcEffectiveDefense(actor); // Calculate effectiveDefense
		calcDefenseResult(actor); // Calcuate the 'result'
		checkLocation(actor); // correct location for any Injury Tolerances
		calcInjury(actor); // Figure out all the injury items
	}
	
	/**
	 * Calculate effective defense value
	 */
	private void calcEffectiveDefense(Actor actor) {
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
			if (position.equals("Kneeling"))
				effectiveDefense -= 2;
			if (position.equals("Prone"))
				effectiveDefense -= 3;

			// Other
			effectiveDefense += otherMod;
		}
	}
	
	/** 
	 * Determine the defense success/failure/etc
	 * @param actor
	 */
	private void calcDefenseResult(Actor actor) {
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
     * Check if location is valid for this actor and adjust if necessary
     * @param actor
     */
    private void checkLocation(Actor actor) {
      	if (actor.hasTrait("Injury Tolerance")) {
    		ArrayList<String> tolerances = actor.getTraitValueArray("Injury Tolerance");
    		// No Blood - nothing currently (also Diffuse and Homogenous)
    		// No Brain
    		if (tolerances.contains("no brain") || tolerances.contains("diffuse") || tolerances.contains("homogenous")) {
    			// "a blow to the skull or eye is treated no differently than a blow to the face (except that an eye injury can still cripple that eye)"
    			// This program currently doesn't track crippling for eyes, so this is the same in every way
    			if (location.type == LocationType.Skull || location.type == LocationType.Eye)
    				location = HitLocations.getLocation(LocationType.Face);
    		}
    		// No Eyes
    		if (tolerances.contains("no eyes")) {
    			if (location.type == LocationType.Eye)
    				location = HitLocations.getLocation(LocationType.Face);
    		}
    		// No Head
    		if (tolerances.contains("no head")) {
    			if (location.type == LocationType.Face || location.type == LocationType.Skull)
    				location = HitLocations.getLocation(LocationType.Torso);
    		}
    		// No Neck
    		if (tolerances.contains("no neck")) {
    			if (location.type == LocationType.Neck)
    				location = HitLocations.getLocation(LocationType.Torso);
    		}
    		// No Vitals
    		if (tolerances.contains("no vitals") || tolerances.contains("diffuse") || tolerances.contains("homogenous")) {
    			if (location.type == LocationType.Vitals || location.type == LocationType.Groin)
    				location = HitLocations.getLocation(LocationType.Torso);
    		}
    	}
    }
    
	/** 
     * Calculate any injury
     */
    private void calcInjury(Actor actor) {
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
    	ArrayList<String> tolerances = actor.getTraitValueArray("Injury Tolerance"); // Empty array if actor does not have this trait
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
    		shieldDamage = (int) (Math.floor(shieldBasicDamage*damage.damageMultiplierHomogenous()));
    		// Min damage 1 if any got through DR
    		shieldDamage = (shieldDamage <= 0 && shieldBasicDamage > 0)?1:shieldDamage; 
    		// Calculate total cover DR provided (including armor divisor)
    		coverDR = (int) (Math.floor((shieldDR + Math.ceil(shieldHP/4))/damage.armorDivisor));
    	case Failure:
    	case CritFailure:
    		// Calculate actual basic damage to the target, including any cover DR
			int totalDR = overrideDR.getDRforType(damage.type) + location.extraDR;
    		int basicDamage = (int) (damage.basicDamage - coverDR - Math.floor(totalDR/damage.armorDivisor));
			basicDamage = Math.max(0, basicDamage);
			// Calculate injury to the target
			double damageMultiplier = damage.damageMultiplier(location);
			if (tolerances.contains("homogenous"))
				damageMultiplier = damage.damageMultiplierHomogenous(location);
			else if (tolerances.contains("unliving"))
				damageMultiplier = damage.damageMultiplierUnliving(location);
 			injury = (int) (basicDamage*damageMultiplier); 
    		injury = (injury <= 0 && basicDamage > 0)?1:injury; // Min damage 1 if any got through DR
    		// Diffuse
    		if(tolerances.contains("diffuse")) {
    			int maxDamage = damage.damageMaxDiffuse();
    			injury = (injury > maxDamage)?maxDamage:injury;
    		}
    		// Check for crippling
    		if (location.cripplingThreshold != 0) {
    			int cripplingThreshold = (int) Math.floor(hitPoints * location.cripplingThreshold + 1.00001);
    			if (injury >= cripplingThreshold) {
    				injury = cripplingThreshold;
    				cripplingInjury = true;
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
}
