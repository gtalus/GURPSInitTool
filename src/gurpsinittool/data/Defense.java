package gurpsinittool.data;

import java.awt.Color;

import gurpsinittool.data.Actor.BasicTrait;
import gurpsinittool.data.Defense.DefenseType;
import gurpsinittool.data.HitLocations.HitLocation;
import gurpsinittool.util.DieRoller;

public class Defense {
	// List of valid defense types
	public enum DefenseType {Parry, Block, Dodge, None};
	// Possible defense results
	public enum DefenseResult {CritSuccess, Success, ShieldHit, Failure}; 

	// General Options
	public DefenseType type;
	public boolean ee = false;
	public boolean retreat = false;
	public boolean side = false;
	public boolean stunned = false;
	public boolean shield = false;
	public String position = ""; // TODO: should probably not be string!
	public int otherMod = 0;
	
	// Key Inputs
	public int roll; // The defense roll
	public int override_dr; // Override the actor's base DR
	public Damage damage; // The amount of damage inflicted
	public HitLocation location;
	
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
    	int ShieldDB = actor.getValueInt(BasicTrait.Shield_DB);
    	int ShieldHP = actor.getValueInt(BasicTrait.Shield_HP);
    	
		// Pick the best by default
    	if ((effParry > effDodge) && (effParry >= effBlock)) {
   			type = DefenseType.Parry;
    	} else if ((effBlock > effDodge) && (effBlock > effParry)) {
    		type = DefenseType.Block;
    	} else {
    		type = DefenseType.Dodge;
    	}
    	
    	// Set default options
    	shield = (ShieldDB > 0 && actor.ShieldDamage < ShieldHP);
    	if (actor.Status.contains(Actor.ActorStatus.Stunned))
    		stunned = true;
    	// Set position
    	if (actor.Status.contains(Actor.ActorStatus.Kneeling))
    		position = "Kneeling";
    	if (actor.Status.contains(Actor.ActorStatus.Prone))
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
			}
			if (retreat)
				effectiveDefense += (type == DefenseType.Dodge) ? 3 : 1;
			if (side)
				effectiveDefense -= 2;
			if (stunned)
				effectiveDefense -= 4;
			if (shield)
				effectiveDefense += actor.getValueInt(BasicTrait.Shield_DB);

			//Position
			if (position.equals("Kneeling"))
				effectiveDefense -= 2;
			if (position.equals("Prone"))
				effectiveDefense -= 3;

			// Other
			effectiveDefense += otherMod;
		}
	}
	
	private void calcDefenseResult(Actor actor) {
		int shield_db = shield ? actor.getValueInt(BasicTrait.Shield_DB) : 0;

		// CritSuccess, Success, ShieldHit, Failure
		if (type == DefenseType.None) { // No attempted defense
			result = DefenseResult.Failure;
		} else if (DieRoller.isCritSuccess(roll, effectiveDefense)) { // Crit Defense!
			result = DefenseResult.CritSuccess;
		} else if (DieRoller.isFailure(roll, effectiveDefense)) { // Hit
			result = DefenseResult.Failure;
		} else if (DieRoller.isFailure(roll, effectiveDefense - shield_db)) { // Shield Hit
			result = DefenseResult.ShieldHit;
		}  else {
			result = DefenseResult.Success;
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
    	int ShieldDR = actor.getValueInt(BasicTrait.Shield_DR);
    	int ShieldHP = actor.getValueInt(BasicTrait.Shield_HP);
    	int HP = actor.getValueInt(BasicTrait.HP);
    	switch (result) {
    	case CritSuccess:
    	case Success:
    		return;
    	case ShieldHit:
    		// Calculate shield damage
    		int shieldBasicDamage = (int) (damage.BasicDamage - Math.floor(ShieldDR/damage.ArmorDivisor));
    		// Apply min/max values
    		shieldBasicDamage = Math.min((int)Math.ceil(ShieldHP/4), shieldBasicDamage);
    		shieldBasicDamage = Math.max(0, shieldBasicDamage);
    		shieldDamage = (int) (Math.floor(shieldBasicDamage*damage.DamageMultiplierHomogenous()));
    		// Min damage 1 if any got through DR
    		shieldDamage = (shieldDamage <= 0 && shieldBasicDamage > 0)?1:shieldDamage; 
    		// Calculate total cover DR provided (including armor divisor)
    		coverDR = (int) (Math.floor(ShieldDR/damage.ArmorDivisor) + Math.ceil(ShieldHP/4));
    	case Failure:
    		// Calculate actual basic damage to the target, including any cover DR
			int totalDR = override_dr + location.extraDR;
    		int basicDamage = (int) (damage.BasicDamage - coverDR - Math.floor(totalDR/damage.ArmorDivisor));
			basicDamage = Math.max(0, basicDamage);
			// Calculate injury to the target
			double damageMultiplier = location.DamageMultiplier(damage.Type);
 			injury = (int) (basicDamage*damageMultiplier); 
    		injury = (injury <= 0 && basicDamage > 0)?1:injury; // Min damage 1 if any got through DR
    		// Check for crippling
    		if (location.cripplingThreshold != 0) {
    			int cripplingThreshold = (int) Math.floor(HP * location.cripplingThreshold + 1.00001);
    			if (injury >= cripplingThreshold) {
    				injury = cripplingThreshold;
    				cripplingInjury = true;
    				majorWound = true;
    			}
    		} 
    		else { // Check for major wound
    			int majorWoundThreshold = (int) Math.floor(HP * 1/2 + 1.00001);
    			if (injury >= majorWoundThreshold) {
    				majorWound = true;
    			}
    		}
    	}
    }
}
