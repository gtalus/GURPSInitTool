package gurpsinittool.data;

import gurpsinittool.data.Defense.DefenseResult;
import gurpsinittool.data.Defense.DefenseType;
import gurpsinittool.util.DieRoller;

/** 
 * Encapsulates a single actor in the current encounter.
 * Contains all status information.
 * Provides change notification services.
 */
public class Actor extends ActorBase {
	
	private static final long serialVersionUID = 1L;

	public Actor() {
		super();
	}
	public Actor(Actor anActor) {
		super(anActor);
	}
	
	public void NextTurn() {
		setTemp("numParry", 0);
		setTemp("numBlock", 0);
		// Shock
		if (settings.AUTO_SHOCK) {
			int injury =  getTempInt("shock.next");
			int injury_per_shock = (int) getTraitValueInt(BasicTrait.HP) / 10;
			int LHPT = hasTrait("HPT")?0:(hasTrait("LPT")?2:1);
			setTemp("shock", LHPT * Math.min((int)injury/injury_per_shock,4));
		}
		setTemp("shock.next", 0);
		int Injury = getTraitValueInt(BasicTrait.Injury);
		int HP = getTraitValueInt(BasicTrait.HP);
		int HT = getTraitValueInt(BasicTrait.HT);

		// Resolve auto actions at start of actor's turn:
		if (isTypeAutomated() && !(hasStatus(ActorStatus.Unconscious) 
				|| hasStatus(ActorStatus.Disabled) 
				|| hasStatus(ActorStatus.Dead)
				|| hasStatus(ActorStatus.Waiting))) { // Do AUTO actions
			if (settings.AUTO_UNCONSCIOUS && Injury >= HP) { 
				int penalty = (int) (-1*(Math.floor((double)Injury/HP)-1));
				int result = DieRoller.roll3d6();
				String details = "(HT: " + HT + ", penalty: " + penalty + ", roll: " + result + ")";
				if (DieRoller.isFailure(result, HT+penalty)) {
					logEventTypeName("<b><font color=red>failed</font></b> consciousness roll " + details);
					clearStatuses(); // Clear other status' (Attacking/whatever)
					addStatus(ActorStatus.Unconscious);
					setPosture(ActorStatus.Prone);
					addStatus(ActorStatus.Disarmed);
				} else {
					logEventTypeName("passed consciousness roll " + details);
				}
			}
			if (settings.AUTO_STUNRECOVERY) {
				removeStatus(ActorStatus.StunRecovr); // This was from last turn, so recovered this turn
				if (hasStatus(ActorStatus.StunMental)) {
					int recoverTarget = getTraitValueInt(BasicTrait.IQ);
					if (hasTrait("CR")) recoverTarget += 6;
					int roll = DieRoller.roll3d6();
					if (DieRoller.isSuccess(roll, recoverTarget)) {
						logEventTypeName("is now recovering from Mental Stun (rolled " + roll + " against " + recoverTarget + ")"); 
						removeStatus(ActorStatus.StunMental);
						addStatus(ActorStatus.StunRecovr);
					} else {
						logEventTypeName("<b>failed</b> recovery roll for Mental Stun (rolled " + roll + " against " + recoverTarget + ")"); 
					}
				} 
				if (hasStatus(ActorStatus.StunPhys)) {
					int recoverTarget = getTraitValueInt(BasicTrait.HT);
					int roll = DieRoller.roll3d6();
					if (DieRoller.isSuccess(roll, recoverTarget)) {
						logEventTypeName("is now recovering from Physical Stun (rolled " + roll + " against " + recoverTarget + ")");
						removeStatus(ActorStatus.StunPhys);
						addStatus(ActorStatus.StunRecovr);
					} else {
						logEventTypeName("<b>failed</b> recovery roll for Physical Stun (rolled " + roll + " against " + recoverTarget + ")"); 
					}
				}
			}
			if (settings.AUTO_ATTACK && hasStatus(ActorStatus.Attacking) && !isStunned())
				Attack();
		}
	}

	/**
	 * Reset actor state- Active, 0 Injury, 0 fatigue, numParry/numBlock/shieldDamage = 0
	 */
	public void Reset() {
		clearStatuses();
		setTrait(BasicTrait.Injury, "0");
		setTrait(BasicTrait.Fatigue, "0");
		// TODO: reset all temp vars?
		setTemp("numParry", 0);
		setTemp("numBlock", 0);
		setTemp("shieldDamage", 0);
		setTemp("shock", 0);
		setTemp("shock.next", 0);
	}
	
	// Helper method to deal with all the varieties of stun
	public boolean isStunned() {
		return (hasStatus(Actor.ActorStatus.StunPhys)
    			|| hasStatus(Actor.ActorStatus.StunMental)
    			|| hasStatus(Actor.ActorStatus.StunRecovr));
	}
	
    //================================================================================
    // Attack Support
    //================================================================================
	public void Attack(int num) {
		if (num < 0 || num >= attacks.size()) {
			System.err.println("-E- Actor:Attack: invalid attack attempted: id # " +String.valueOf(num));
			return;
		}
		Attack attack = attacks.get(num);
		int eff_skill = attack.Skill;
		// Positoin
		if (hasStatus(ActorStatus.Prone))
			eff_skill -= 4;
		else if (hasStatus(ActorStatus.Kneeling)) 
			eff_skill -= 2;
		if(settings.AUTO_SHOCK)
			eff_skill -= getTempInt("shock");
		int roll = DieRoller.roll3d6();
		int margin = eff_skill - roll;
		String hit_miss;
		String crit_string = "";
		if (DieRoller.isCritFailure(roll, eff_skill)) {
			hit_miss = "<b><font color=red>Critical miss</font></b>";
			CriticalTables.Entry crit_result = CriticalTables.getRandomEntry(CriticalTables.critical_miss);
			crit_string = "<br/> <i><font color=gray><b>Critical Miss Table Result:</b>" + crit_result.notes + "</font></i>";
		}
		else if (DieRoller.isCritSuccess(roll, eff_skill)) {
			hit_miss = "<b><font color=blue>Critical hit</font></b>";
			CriticalTables.Entry crit_result = CriticalTables.getRandomEntry(CriticalTables.critical_hit);
			crit_string = "<br/> <i><font color=gray><b>Critical Hit Table Result:</b>" + crit_result.notes + "</font></i>";
		}
		else if (DieRoller.isSuccess(roll, eff_skill))
			hit_miss = "<b>hit</b>";
		else 
			hit_miss = "miss";
		boolean isHit = DieRoller.isSuccess(roll, eff_skill);
		Damage damage = Damage.ParseDamage(attack.Damage);
		if (attack.Unbalanced) {
			setTemp("numParry", getTempInt("numParry")+1);
		}
		String armorDivStr = "";
		if (damage.ArmorDivisor != 1) {
			if (damage.ArmorDivisor == (int) damage.ArmorDivisor)
				armorDivStr = "(" + String.format("%d", (int)damage.ArmorDivisor) + ")";
			else
				armorDivStr = "(" + String.format("%s", damage.ArmorDivisor) + ")";
		}			
		logEventTypeName("attacks with " + attack.Name + ": " + hit_miss +  " (" + roll + "/" + eff_skill + "=" + margin + ") for damage " + (isHit?"<font color=red><b>":"") + damage.BasicDamage + armorDivStr + " " + damage.Type + (isHit?"</b></font>":"") + " (" + attack.Damage + ")" + crit_string);
	}
	
	public void Attack() {
		if (attacks.size() < 1) {
			logEventTypeName("<i><font color=gray>has no attacks defined!</font></i>");
			return;
		}	
		if (defaultAttack < 0 || defaultAttack >= attacks.size()) {
			logEventError("has invalid default attack: " + defaultAttack);
			return;
		}
		Attack(defaultAttack);		
	}
	
    //================================================================================
    // Defense Support
    //================================================================================
	public String Defend(Defense defense) {
		RecordDefenseAttempt(defense);
		LogDefenseResults(defense);
		ProcessDefenseResults(defense);
		KnockdownStunningCheck(defense);
		return "";
	}
	
	// Process the impact of attempting the defense itself (fatigue, number of Parries this turn, etc)
	private void RecordDefenseAttempt(Defense defense) {
		int Fatigue = getTraitValueInt(BasicTrait.Fatigue);
		setTrait(BasicTrait.Fatigue, String.valueOf(Fatigue+defense.fatigue));

		// Process shield damage/injury/fatigue
		setTemp("shieldDamage", getTempInt("shieldDamage") + defense.shieldDamage);
		switch (defense.type) { // Record defense attempts
		case Parry:
			setTemp("numParry", getTempInt("numParry")+1);
			break;
		case Block:
			setTemp("numBlock", getTempInt("numBlock")+1);
			break;
		default:
		}
	}

	private void LogDefenseResults(Defense defense) {
		// Defense description
		String resultType = (defense.result == DefenseResult.CritSuccess)?"<b><font color=blue>critically</font></b>"
				:(defense.result == DefenseResult.Success)?"successfully"
						:(defense.result == DefenseResult.ShieldHit)?"partially"
								:"unsuccessfully";

		String defenseDescription = "";
		switch (defense.type) {
		case Parry:
			defenseDescription = resultType + " parried blow.";
			break;
		case Block:
			defenseDescription = resultType + " blocked blow.";
			break;
		case Dodge:
			defenseDescription = resultType + " dodged blow.";
			break;
		case None:
			defenseDescription = "made no defense against blow.";
		}
		
		if (defense.result == DefenseResult.CritFailure)
			defenseDescription = "<b><font color=red>critically</font></b> failed " + defense.type.toString().toLowerCase() + ".";

		String damageDescription = "";
		if (defense.injury != 0) {
			damageDescription = " Sustained <b><font color=red>" + defense.injury + "</font></b> injury to the " + defense.location.type.name();
			String knockdownstunningPenalty = (defense.location.knockdownPenalty != 0)?" @ " + defense.location.knockdownPenalty:"";
			if (defense.cripplingInjury)
				damageDescription += " <b>(crippling" + knockdownstunningPenalty + ")</br>";
			else if (defense.majorWound)
				damageDescription += " <b>(major" + knockdownstunningPenalty + ")</br>";
			damageDescription += ".";
		}
		else if (defense.result == DefenseResult.ShieldHit || defense.result == DefenseResult.Failure) 
			damageDescription = " But took no injury.";
		if (defense.shieldDamage != 0) 
			damageDescription += " <b>Shield damaged " + defense.shieldDamage + ".</b>";

		logEventTypeName(defenseDescription + damageDescription);
	}

	private void ProcessDefenseResults(Defense defense) {
		// Apply injury
		setTrait(BasicTrait.Injury, String.valueOf(getTraitValueInt(BasicTrait.Injury)+defense.injury));

		// Crit failure
		if (defense.result == DefenseResult.CritFailure) {
			switch (defense.type) {
			case Parry:
				CriticalTables.Entry crit_result = CriticalTables.getRandomEntry(CriticalTables.critical_miss);
				String crit_string = "<i><font color=gray><b>Critical Miss Table Result:</b>" + crit_result.notes + "</font></i>";
				logEventTypeName(crit_string);
				break;
			case Block:
				logEventTypeName("Loses grip on shield, must ready before next block");
				break;
			case Dodge:
				if (!hasStatus(ActorStatus.Prone))
					logEventTypeName("Falls prone.");				
				setPosture(ActorStatus.Prone);
				break;
			case None:
				System.err.println("-E- unexpected 'None' defense type for critical failure!");
				break;
			}
		}
	}
	
    private void KnockdownStunningCheck(Defense defense) {
    	int HT = getTraitValueInt(BasicTrait.HT);
    	int HP = getTraitValueInt(BasicTrait.HP);
    	//Whenever you suffer a major wound, and whenever you are struck in the head (skull, face, or eye) 
    	// or vitals for enough injury to cause a shock penalty, you must make an immediate HT roll to avoid 
    	// knockdown and stunning.
    	// (Also including groin here, based on common house-rule)
 		if (defense.cripplingInjury || 
 				defense.majorWound ||
 				(defense.injury >= HP/10.0 && (defense.location.headWound || defense.location.knockdownPenalty < 0))) {
 
 			int effHT = HT;
 			String knockdownDescription = "";
 			if (defense.cripplingInjury) {
 				effHT += defense.location.knockdownPenalty;
 				knockdownDescription = " for crippling injury";
 			} else if (defense.majorWound) {
 				effHT += defense.location.knockdownPenalty;
 				knockdownDescription = " for major wound";
 			}
				
 			if (hasTrait("HPT")) effHT += 3;
 			if (hasTrait("LPT")) effHT -= 4;
 			int roll = DieRoller.roll3d6();
 			boolean success = DieRoller.isSuccess(roll, effHT);
 			logEventTypeName("Knockdown/Stunning check" + knockdownDescription + ": rolled " + roll + " against " + effHT + " => " + (!success?"<b>failed</b>":"succeeded"));
 			if (isTypeAutomated() && settings.AUTO_KNOCKDOWNSTUN) {
 				if (!success) {
 					addStatus(ActorStatus.StunPhys); // Check for mental stun and don't add this in that case?
 					removeStatus(ActorStatus.StunRecovr);
 					setPosture(ActorStatus.Prone);
 					addStatus(ActorStatus.Disarmed);
 				}
 			}
 		}
    }
	    
    /**
     * Get the current defense value of a particular DefenseType
     * @param type - the type of defense to report
     * @return the current value of that defense
     */
    public int getCurrentDefenseValue(DefenseType type) {
    	int Parry = getTraitValueInt(BasicTrait.Parry);
    	int Block = getTraitValueInt(BasicTrait.Block);
    	int Dodge = getTraitValueInt(BasicTrait.Dodge);
    	int Injury = getTraitValueInt(BasicTrait.Injury);
    	int Fatigue = getTraitValueInt(BasicTrait.Fatigue);
    	int HP = getTraitValueInt(BasicTrait.HP);
    	int FP = getTraitValueInt(BasicTrait.FP);
    	int currentDefense = 0;
    	switch (type) {
    	case Parry:
    		currentDefense = Parry - getTempInt("numParry") * 4;
    		break;
    	case Block:
    		currentDefense = Block - getTempInt("numBlock") * 5;
    		break;
    	case Dodge:
    		currentDefense = Dodge;   
    		if (Injury > 2*HP/3)
    			currentDefense = (int) Math.ceil(currentDefense/2.0);
        	if (Fatigue > 2*FP/3)
        		currentDefense = (int) Math.ceil(currentDefense/2.0);
		default:
			break;
    	}
    	return currentDefense;
    }
}
