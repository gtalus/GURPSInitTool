package gurpsinittool.data;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import gurpsinittool.data.Defense.DefenseResult;
import gurpsinittool.data.Defense.DefenseType;
import gurpsinittool.util.DieRoller;

/** 
 * Encapsulates a single actor in the current encounter.
 * Includes all game logic
 */
public class Actor extends ActorBase {
	
	private static final long serialVersionUID = 1L;
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(Actor.class.getName());
	private transient boolean gameLogicEnabled = false;
	
	public Actor() {
		super();
	}
	public Actor(Actor anActor) {
		super(anActor);
	}
	
	/**
	 * Indicates whether game logic is active for this actor. Game logic is not
	 * active when the actor is loading, undo/redo replay is in progress, and 
	 * in the GroupTable. It is active under normal conditions in the InitTable
	 * @return true if game logic is active
	 */
	public boolean isGameLogicActive() {
		return gameLogicEnabled && !undoRedoInProgress;
	}
	/**
	 * Enable or disable game logic.
	 * If enabled, game logic may still not be active (as when undo/redo replay 
	 * is in progress).
	 * @param isEnabled - true to enable, false to disable
	 */
	public void setGameLogicEnabled(boolean isEnabled) {
		gameLogicEnabled = isEnabled;
	}
	
	/**
	 * Called by the GameMaster when this actor starts a new turn, and becomes active
	 */
	public void startTurnActive() {
		setTemp("numParry", 0);
		setTemp("numBlock", 0);
		// Shock
		if (settings.autoShock.isSet()) {
			int injury =  getTempInt("shock.next");
			int injuryPerShock = (int) getTraitValueInt(BasicTrait.HP) / 10;
			injuryPerShock = (injuryPerShock==0)?1:injuryPerShock; // Minimum 1
			int shockMultiplier = hasTrait("High Pain Threshold")?0:(hasTrait("Low Pain Threshold")?2:1);
			setTemp("shock", shockMultiplier * Math.min((int)injury/injuryPerShock,4));
		}
		setTemp("shock.next", 0);
		int injury = getTraitValueInt(BasicTrait.Injury);
		int hitPoints = getTraitValueInt(BasicTrait.HP);
		int health = getTraitValueInt(BasicTrait.HT);
		int fatigue = getTraitValueInt(BasicTrait.Fatigue);
		int fatiguePoints = getTraitValueInt(BasicTrait.FP);
		int will = getTraitValueInt(BasicTrait.Will);
		
		// Resolve auto actions at start of actor's turn:
		if (isTypeAutomated() && !(hasStatus(ActorStatus.Unconscious) 
				|| hasStatus(ActorStatus.Disabled) 
				|| hasStatus(ActorStatus.Dead)
				|| hasStatus(ActorStatus.Waiting))) { // Do AUTO actions
			// Check for unconsciousness when HP <= 0
			if (settings.autoUnconscious.isSet() && injury >= hitPoints) { 
				int penalty = (int) (-1*(Math.floor((double)injury/hitPoints)-1));
				int result = DieRoller.roll3d6();
				String details = "(HT: " + health + ", penalty: " + penalty + ", roll: " + result + ")";
				if (DieRoller.isFailure(result, health+penalty)) {
					logEventTypeName("<b><font color=red>failed</font></b> consciousness roll " + details);
					setAllStatuses(new HashSet<ActorStatus>(Arrays.asList(ActorStatus.Unconscious, ActorStatus.Prone, ActorStatus.Disarmed)));
				} else {
					logEventTypeName("passed consciousness roll " + details);
				}
			}
			// Check for incapacitation when FP <= 0, unless we just fell unconscious from injury
			if (settings.autoIncapacitation.isSet() && fatiguePoints != 0 && fatigue >= fatiguePoints && !hasStatus(ActorStatus.Unconscious)) { 
				int result = DieRoller.roll3d6();
				String details = "(Will: " + will + ", roll: " + result + ")";
				if (DieRoller.isFailure(result, will)) {
					logEventTypeName("<b><font color=red>failed</font></b> incapacitation roll " + details);
					setAllStatuses(new HashSet<ActorStatus>(Arrays.asList(ActorStatus.Disabled, ActorStatus.Prone)));
				} else {
					logEventTypeName("passed incapacitation roll " + details);
				}
			}
			if (settings.autoStunRecovery.isSet()) {
				removeStatus(ActorStatus.StunRecovr); // This was from last turn, so recovered this turn
				if (hasStatus(ActorStatus.StunMental)) {
					int recoverTarget = getTraitValueInt(BasicTrait.IQ);
					if (hasTrait("Combat Reflexes")) recoverTarget += 6;
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
		}
		if (!settings.attackDelay.isSet())
			handleAutoAttack();
	}
	/**
	 * Called by the GameMaster when this actor is loosing active status
	 */
	public void loosingActive() {
		// Auto attack, if appropriate
		if (settings.attackDelay.isSet())
			handleAutoAttack();
	}


	/**
	 * Reset actor state- Active, 0 Injury, 0 fatigue, numParry/numBlock/shieldDamage = 0
	 */
	public void reset() {
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
	
	/**
	 * Is the actor currently some variety of stunned?
	 * @return true if stunned
	 */
	public boolean isStunned() {
		return (hasStatus(Actor.ActorStatus.StunPhys)
    			|| hasStatus(Actor.ActorStatus.StunMental)
    			|| hasStatus(Actor.ActorStatus.StunRecovr));
	}
	/**
	 * Is the actor currently disabled, unconscious, or dead?
	 * @return true if disabled
	 */
	public boolean isDisabled() {
		return (hasStatus(Actor.ActorStatus.Unconscious)
    			|| hasStatus(Actor.ActorStatus.Disabled)
    			|| hasStatus(Actor.ActorStatus.Dead));
	}
	
    //================================================================================
    // Attack Support
    //================================================================================
	/**
	 * Execute the specified attack
	 * @param num - the index of the attack to execute
	 */
	public void attack(int num) {
		if (num < 0 || num >= attacks.size()) {
			if (LOG.isLoggable(Level.INFO)) {LOG.info("Invalid attack attempted: id # " +String.valueOf(num));}
			return;
		}
		Attack attack = attacks.get(num);
		int effSkill = attack.skill;
		// Position
		if (hasStatus(ActorStatus.Prone))
			effSkill -= 4;
		else if (hasStatus(ActorStatus.Kneeling)) 
			effSkill -= 2;
		if(settings.autoShock.isSet())
			effSkill -= getTempInt("shock");
		int roll = DieRoller.roll3d6();
		int margin = effSkill - roll;
		String hitOrMiss;
		String critString = "";
		if (DieRoller.isCritFailure(roll, effSkill)) {
			hitOrMiss = "<b><font color=red>Critical miss</font></b>";
			CriticalTables.Entry critResult = CriticalTables.getRandomEntry(CriticalTables.criticalMiss);
			critString = "<br/> <i><font color=gray><b>Critical Miss Table Result:</b>" + critResult.notes + "</font></i>";
		}
		else if (DieRoller.isCritSuccess(roll, effSkill)) {
			hitOrMiss = "<b><font color=blue>Critical hit</font></b>";
			CriticalTables.Entry critResult = CriticalTables.getRandomEntry(CriticalTables.criticalHit);
			critString = "<br/> <i><font color=gray><b>Critical Hit Table Result:</b>" + critResult.notes + "</font></i>";
		}
		else if (DieRoller.isSuccess(roll, effSkill))
			hitOrMiss = "<b>hit</b>";
		else 
			hitOrMiss = "miss";
		boolean isHit = DieRoller.isSuccess(roll, effSkill);
		if (attack.unbalanced) {
			setTemp("numParry", getTempInt("numParry")+1);
		}
		

		String damageStr;
		try {
			final DamageExpression damageExpression = DamageExpression.parseDamageExpression(attack.damage);
			Damage damage = damageExpression.getDamage(this);
			String armorDivStr = "";
			if (damage.armorDivisor != 1) {
				if (damage.armorDivisor == (int) damage.armorDivisor)
					armorDivStr = "(" + String.format("%d", (int)damage.armorDivisor) + ")";
				else
					armorDivStr = "(" + String.format("%s", damage.armorDivisor) + ")";
			}	
			damageStr = (isHit?"<font color=red><b>":"") + damage.basicDamage + armorDivStr + " " + damage.type + (isHit?"</b></font>":"") + " (" + attack.damage + ")";
		} catch (ParseException e) {
			damageStr = (isHit?"<font color=red><b>":"") + "'" + attack.damage + "'" + (isHit?"</b></font>":"");
			//logEventError("Unable to parse damage string in attack '" + attack.Name + "'");
			//return;
		}
		
				
		logEventTypeName("attacks with " + attack.name + ": " + hitOrMiss +  " (" + roll + "/" + effSkill + "=" + margin + ") for damage " + damageStr + critString);
	}
	/**
	 * Execute the default attack
	 */
	public void attack() {
		if (attacks.size() < 1) {
			logEventTypeName("<i><font color=gray>has no attacks defined!</font></i>");
			return;
		}	
		if (defaultAttack < 0 || defaultAttack >= attacks.size()) {
			logEventError("has invalid default attack: " + defaultAttack);
			return;
		}
		attack(defaultAttack);		
	}
	
	/**
	 * check if we should auto-attack, and execute the default attack if so
	 */
	private void handleAutoAttack() {
		if (isTypeAutomated() && settings.autoAttack.isSet() && hasStatus(ActorStatus.Attacking) && 
				!isStunned() && !isDisabled() && !hasStatus(ActorStatus.Waiting)) {// Ok to attack :)
			attack();
		}
	}
	
    //================================================================================
    // Defense Support
    //================================================================================
	/**
	 * Process a defense and apply to the actor
	 * @param defense - the Defense object to process
	 * @return a string describing the result
	 */
	public String defend(Defense defense) {
		startCompoundEdit();			
		recordDefenseAttempt(defense);
		logDefenseResults(defense);
		processDefenseResults(defense);
		knockdownStunningCheck(defense);
		endCompoundEdit("Defend");
		return "";
	}
	
	/**
	 * Process the impact of attempting the defense itself (fatigue, number of Parries this turn, etc)
	 * @param defense
	 */
	private void recordDefenseAttempt(Defense defense) {
		int fatigue = getTraitValueInt(BasicTrait.Fatigue);
		setTrait(BasicTrait.Fatigue, String.valueOf(fatigue+defense.fatigue));

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

	private void logDefenseResults(Defense defense) {
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
		if (settings.logDefenseDetails.isSet()) {
			// Defense details: #/turn, posture, EE, retreat, side, stunned, shield, other. effective/roll
			String details = "defense details: " + defense.type.toString() + defenseNumberReport(defense.type);
			ArrayList<String> additional = new ArrayList<String>();
			if (!"Standing".equals(defense.position)) additional.add("position: " + defense.position);
			if (defense.ee) additional.add("EE");
			if (defense.retreat) additional.add("retreating");
			if (defense.side) additional.add("side attack");
			if (defense.stunned) additional.add("stunned");
			if (defense.shield) additional.add("shield used");
			if (defense.otherMod != 0) additional.add("other:" + String.valueOf(defense.otherMod));
			if (!additional.isEmpty())
				details += ", " + String.join(";", additional);
			details += ", effective defense: " + String.valueOf(defense.effectiveDefense) + "/ roll: " + String.valueOf(defense.roll) + ".";
			logEventTypeName(details);
		}
	}
	
	private String defenseNumberReport(DefenseType type) {
		int num = 0;
		switch (type) {
		case Parry:
			num = getTempInt("numParry");
			break;
		case Block:
			num = getTempInt("numBlock");
			break;
		case Dodge:
		case None:
		}
		String result = "";
		if (num > 1) {
			result = " (" + String.valueOf(num);
			switch (num) {
			case 2: result += "nd"; break;
			case 3: result += "rd"; break;
			default: result += "th"; break;
			}
			result += " this turn)";
		}
		return result;
	}

	private void processDefenseResults(Defense defense) {
		// Apply injury
		setTrait(BasicTrait.Injury, String.valueOf(getTraitValueInt(BasicTrait.Injury)+defense.injury));

		// Crit failure
		if (defense.result == DefenseResult.CritFailure) {
			
			switch (defense.type) {
			case Parry:
				CriticalTables.Entry critResult = CriticalTables.getRandomEntry(CriticalTables.criticalMiss);
				String critString = "<i><font color=gray><b>Critical Miss Table Result:</b>" + critResult.notes + "</font></i>";
				logEventTypeName(critString);
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
				if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Unexpected 'None' defense type for critical failure!");}
				break;
			}
			
			if (defense.ee) {
				// Apply extra injury
				setTrait(BasicTrait.Injury, String.valueOf(getTraitValueInt(BasicTrait.Injury)+1));
			}
		}
	}
	
    private void knockdownStunningCheck(Defense defense) {
    	int health = getTraitValueInt(BasicTrait.HT);
    	int hitPoints = getTraitValueInt(BasicTrait.HP);
    	//Whenever you suffer a major wound, and whenever you are struck in the head (skull, face, or eye) 
    	// or vitals for enough injury to cause a shock penalty, you must make an immediate HT roll to avoid 
    	// knockdown and stunning.
    	// (Also including groin here, based on common house-rule)
 		if (defense.cripplingInjury || 
 				defense.majorWound ||
 				(defense.injury >= hitPoints/10.0 && (defense.location.headWound || defense.location.knockdownPenalty < 0))) {
 
 			int effHT = health;
 			String knockdownDescription = "";
 			if (defense.cripplingInjury) {
 				effHT += defense.location.knockdownPenalty;
 				knockdownDescription = " for crippling injury";
 			} else if (defense.majorWound) {
 				effHT += defense.location.knockdownPenalty;
 				knockdownDescription = " for major wound";
 			}
				
 			if (hasTrait("High Pain Threshold")) effHT += 3;
 			if (hasTrait("Low Pain Threshold")) effHT -= 4;
 			int roll = DieRoller.roll3d6();
 			boolean success = DieRoller.isSuccess(roll, effHT);
 			logEventTypeName("Knockdown/Stunning check" + knockdownDescription + ": rolled " + roll + " against " + effHT + " => " + (!success?"<b>failed</b>":"succeeded"));
 			if (isTypeAutomated() && settings.autoKnockdownStun.isSet()) {
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
    	int currentDefense = 0;
    	switch (type) {
    	case Parry:
    		currentDefense = getTraitValueInt(BasicTrait.Parry) - getTempInt("numParry") * 4;
    		break;
    	case Block:
    		currentDefense = getTraitValueInt(BasicTrait.Block) - getTempInt("numBlock") * 5;
    		break;
    	case Dodge:
    		currentDefense = getTraitValueInt(BasicTrait.Dodge);   
    		if (getTraitValueInt(BasicTrait.Injury) > 2*getTraitValueInt(BasicTrait.HP)/3)
    			currentDefense = (int) Math.ceil(currentDefense/2.0);
        	if (getTraitValueInt(BasicTrait.Fatigue) > 2*getTraitValueInt(BasicTrait.FP)/3)
        		currentDefense = (int) Math.ceil(currentDefense/2.0);
		default:
			break;
    	}
    	return currentDefense;
    }
    
	/**
	 * Set the value of a trait, adding if that trait does not exist
	 * @param name - the name of the trait
	 * @param value - the value of the trait
	 */
    @Override
	public void setTrait(String name, String value) {
    	startCompoundEdit();
    	// Handle cases where we want to limit the value- mostly related to Injury and Fatigue
    	int injuryFromFatigue = 0; // To handle injury from fatigue loss
    	if (isGameLogicActive()) {
    		if (LOG.isLoggable(Level.FINER)) {LOG.finer("Evaluating pre-edit game logic for trait '" + name + "'");}
    		if (name.equals("Injury") || name.equals("Fatigue")) { 		
    			int intValue = 0;
    			try { // Enforce >= 0
    				intValue = Integer.parseInt(value);
    				intValue = Math.max(intValue, 0);
    				value = String.valueOf(intValue);
    			} catch (NumberFormatException e) {
    				if (LOG.isLoggable(Level.INFO)) {LOG.info("Error parsing Injury/Fatigue value: " + value);}
    			}

    			if (name.equals("Fatigue")) {
    				int fatiguePoints = getTraitValueInt(BasicTrait.FP);
    				if (fatiguePoints == 0) { // Skip if FP is 0!
    					value = "0";
    				} else {
    					// Fatigue / Injury interaction
    					int oldFatigue = getTraitValueInt(BasicTrait.Fatigue);
    					int diff =  intValue - oldFatigue;
    					if (diff > 0 && intValue > fatiguePoints) { // Taking Fatigue, and resulting in less than 0 FP
    						injuryFromFatigue = diff + ((oldFatigue < fatiguePoints)?(oldFatigue-fatiguePoints):0);
    					}					
    					// Minimum of -1xFP
    					if (intValue > 2*fatiguePoints) { 
    						intValue = 2*fatiguePoints;
    						value = String.valueOf(intValue);
    					}
    				}
    			}
    		}
    	}
    	// Set the value
    	super.setTrait(name, value);
    	if (injuryFromFatigue > 0) {
    		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Taking additional " + injuryFromFatigue + " injury due to fatigue loss");}
    		setTrait(BasicTrait.Injury, getTraitValueInt(BasicTrait.Injury) + injuryFromFatigue);
    	}
		endCompoundEdit("Edit");
    }
    
    @Override
    protected void internalSetTrait(final Trait trait, final String value) {
    	if (isGameLogicActive()) {
    		if (LOG.isLoggable(Level.FINER)) {LOG.finer("Evaluating game logic for trait '" + trait.name + "'");}
    		if ("Injury".equals(trait.name) || "Fatigue".equals(trait.name)) { 		
    			int intValue = 0;
    			int oldValue = 0;
    			try { 
    				intValue = Integer.valueOf(value);
    				oldValue = Integer.valueOf(trait.value);
    			} catch (NumberFormatException e) {
    				if (LOG.isLoggable(Level.INFO)) {LOG.info("Error parsing trait '" + trait.name + "' value");}
    			}
    			int diff = intValue - oldValue;
    			if ("Injury".equals(trait.name)) {  // Report injury/healing and calculate shock		
    				int hitPoints = getTraitValueInt(BasicTrait.HP);
    				if (diff > 0) {
    					setTemp("shock.next", getTempInt("shock.next") + diff);
    					logEventTypeName("took <b><font color=red>" + diff + "</font></b> damage (now " + (hitPoints - intValue) + " HP).");
    				} else {
    					logEventTypeName("healed <b><font color=blue>" + (-1*diff) + "</font></b> (now " + (hitPoints - intValue) + " HP).");		
    				}
    				// Check for death check
    				int previousMultiple = Math.max(1, oldValue/hitPoints); // Doesn't start until at -1xHP
    				int newMultiple = intValue/hitPoints;
    				if (!hasStatus(ActorStatus.Dead) // Not already dead
    						&& newMultiple > previousMultiple // Crossed new threshold
    						&& previousMultiple < 6) { // Don't print out anything more past -5xHP
    					if (newMultiple >= 6) { // Automatic death
    						logEventTypeName("reached <b><font color=red>-5xHP</font>: automatic death!</b>");
    						if (isTypeAutomated() && settings.autoDeath.isSet()) {
    							addStatus(ActorStatus.Dead); // TODO: clear out unconscious / stunned /disabled?
    						}
    					} else {
    						for (int i = previousMultiple+1; i <= newMultiple; i++) {
    							String logPrefix = "reached <b><font color=red>-" + (i-1) + "xHP</font></b>: ";
    							if (isTypeAutomated() && settings.autoDeath.isSet()) {
    								// Perform death check
    								final int health = getTraitValueInt(BasicTrait.HT);
    								final int result = DieRoller.roll3d6();
    								String details = "(HT: " + health + ", roll: " + result + ")";
    								if (DieRoller.isFailure(result, health)) {
    									// TODO: determine whether mortally wounded or not!
    									if (result - health < 3) {
    										addStatus(ActorStatus.Disabled);
    										details = "<b>mortally wounded.</b> " + details;
    									} else {
    										addStatus(ActorStatus.Dead);
    										details = "<b>died.</b> " + details;
    										i = newMultiple; // don't make any more checks
    									}
    									logEventTypeName(logPrefix + "<b><font color=red>failed</font></b> death check, " + details);
    								} else {
    									logEventTypeName(logPrefix + "passed death check " + details);
    								}
    							} else {
    								logEventTypeName(logPrefix + ": <b>death check required!</b>");
    							}
    						}
    					}
    				}
    			} else if ("Fatigue".equals(trait.name)) {
    				int fatiguePoints = getTraitValueInt(BasicTrait.FP);
    				// Skip if FP is 0!?
    				if (diff > 0) {
    					logEventTypeName("lost <b>" + diff + "</b> fatigue (now " + (fatiguePoints - intValue) + " FP).");
    				} else {
    					logEventTypeName("recoverd <b>" + (-1*diff) + "</b> fatigue (now " + (fatiguePoints - intValue) + " FP).");		
    				}
    				// Check for falling unconscious when reaching -1xFP
    				if (isTypeAutomated() && !hasStatus(ActorStatus.Unconscious) && settings.autoIncapacitation.isSet() && intValue == 2*fatiguePoints) { 
    					logEventTypeName("<b>reached -1xFP: fell unconscious.</b>");
    					setAllStatuses(new HashSet<ActorStatus>(Arrays.asList(ActorStatus.Unconscious, ActorStatus.Prone, ActorStatus.Disarmed)));
    				}
    			}
    		}
    	}
    	super.internalSetTrait(trait, value);
    }
    
    protected String calculateTrait(CalculatedTrait calcTrait) {
    	int strength = getTraitValueInt(BasicTrait.ST);
    	int strikingST = hasTrait("Striking ST")?getTraitValueInt("Striking ST"):0;
    	int liftingST = hasTrait("Lifting ST")?getTraitValueInt("Lifting ST"):0;

    	switch (calcTrait) {
		case BasicLift:
			return StrengthTables.getBasicLift(strength+liftingST);
		case BasicSwing:
			return StrengthTables.getBasicDamageSwing(strength+strikingST);
		case BasicThrust:
			return StrengthTables.getBasicDamageThrust(strength+strikingST);
		case CurrFP:
			return String.valueOf(getTraitValueInt(BasicTrait.FP) - getTraitValueInt(BasicTrait.Fatigue));
		case CurrHP:
			return String.valueOf(getTraitValueInt(BasicTrait.HP) - getTraitValueInt(BasicTrait.Injury));
		default:
			return "unk";
    	}
    }
}
