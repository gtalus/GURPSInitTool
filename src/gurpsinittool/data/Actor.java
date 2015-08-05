package gurpsinittool.data;

import gurpsinittool.data.Actor.BasicTrait;
import gurpsinittool.data.Defense.DefenseResult;
import gurpsinittool.data.Defense.DefenseType;
import gurpsinittool.util.DieRoller;
import gurpsinittool.util.EncounterLogEvent;
import gurpsinittool.util.EncounterLogEventSource;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/** 
 * Encapsulates a single actor in the current encounter.
 * Contains all status information.
 * Provides change notification services.
 */
public class Actor 
	implements Serializable {

	// Default SVUID
	private static final long serialVersionUID = 1L;
	public static EncounterLogEventSource LogEventSource; // where to send log messages
	public static GameSettings settings; // Settings which determine game behavior, including automation

	private transient PropertyChangeSupport mPcs = new PropertyChangeSupport(this); // Change reporting, don't serialize!
	
	public enum ActorStatus {Attacking, Waiting, Disarmed, StunPhys, StunMental, StunRecovr, Prone, Kneeling, Disabled, Unconscious, Dead};
	private HashSet<ActorStatus> statuses;

	public enum ActorType {PC, Enemy, Ally, Neutral, Special};
	private ActorType type;
	
	// All actors must have these traits defined:
	public enum BasicTrait {Name, ST, HP, Speed, DX, Will, Move, IQ, Per, HT, FP, SM, Fatigue, Injury, Dodge, Parry, Block, DR, Shield_DB, Shield_DR, Shield_HP, Notes}
	
	// TODO: implement change tracking in the trait? If we want to allow other classes to hold a Trait object
	private class Trait implements Serializable {
		// Default SVUID
		private static final long serialVersionUID = 1L;

		// type?
		// String, Integer, Float, calculated?, boolean?, valueless?
		// Need to think about how to support traits that modify other traits
		// value?
		// Currently store as a string, do some conversions
		public String name;
		public String value;
		
		//public Trait() { name = ""; value = ""; }
		public Trait(String name, String value) {this.name = name; this.value = value; }
		//public Trait(Trait aTrait) {name = aTrait.name; value = aTrait.value; }
		
		//public int valueAsInt() {
		//	// do the parsing/conversion here
		//	return 0;
		//}
	}
	protected HashMap<String, Trait> traits = new HashMap<String, Trait>();
	
	// Attack info
	private int defaultAttack = 0;
	private ArrayList<Attack> attacks;

	// Volatile (not currently stored or changes reported)
	protected HashMap<String, Trait> temps = new HashMap<String, Trait>();
	 
    //================================================================================
    // Constructors
    //================================================================================

	/**
	 * Basic constructor providing default options for all BasicTraits
	 * @param name : Actor's name
	 * @param type : Actor type (PC, NPC, etc)
	 */
	public Actor(String name, ActorType type) {
		statuses = new HashSet<ActorStatus>();
		this.type = type;
		
		InitializeBasicTraits();
		setTrait(BasicTrait.Name, name);
		
		attacks = new ArrayList<Attack>();
	}
	
	/**
	 * Copy constructor
	 * @param anActor reference Actor
	 */
	public Actor(Actor anActor) {
		this(anActor.getTrait(BasicTrait.Name).value,anActor.type);
		statuses.addAll(anActor.statuses);
		
		// Deep copy of traits
		for (Object value : anActor.traits.values()) {
			Trait trait = (Trait) value;
			setTrait(trait.name, trait.value);
		}
		
		for(int i = 0; i < anActor.attacks.size(); ++i) {
			attacks.add(anActor.attacks.get(i));
		}
	}
	
	private void InitializeBasicTraits() {
		// Set Basic Trait initial Values
		// TODO: Speed should be 5.00 (float) not 5 (int)
		String[] defaultBasicTraitValues = {"unnamed", "10", "10", "5", "10", "10", "5", "10", "10", "10", "10", "0", "0", "0", "8", "9", "9", "0", "2", "4", "20", ""};
		BasicTrait[] traitNames = BasicTrait.values();
		for (int i=0; i<traitNames.length; i++) {
			String traitValue = defaultBasicTraitValues[i];
			String traitName = traitNames[i].toString();
			addTrait(traitName, traitValue);			
		}
		setTemp("numParry", 0);
		setTemp("numBlock", 0);
		setTemp("shieldDamage", 0);
		setTemp("shock", 0);
		setTemp("shock.next", 0);
	}
	
	// post- de-serialize/serialize helper
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		// default implementation
		in.defaultReadObject();
		// Re-constitute transient properties
		mPcs = new PropertyChangeSupport(this);
	}
	
    //================================================================================
    // Accessors
    //================================================================================

	// Statuses
	public boolean hasStatus(ActorStatus status) {
		return statuses.contains(status);
	}
	public void addStatus(ActorStatus status) {
		if (!statuses.contains(status)) {
			statuses.add(status);
			if (settings.LOG_STATUSCHANGES) logEvent("<b>" + getTraitValue(BasicTrait.Name) + "</b> added status <b>" + status + "</b>, now has [" + getStatusesString() + "]");
			mPcs.firePropertyChange("Status", null, status);
		}
	}
	public HashSet<ActorStatus> getAllStatuses() {
		return new HashSet<ActorStatus>(statuses);
	}
	public void setAllStatuses(HashSet<ActorStatus> newStatuses) {
		if (!statuses.containsAll(newStatuses) || !newStatuses.containsAll(statuses)) {
			String oldStatuses = getStatusesString();
			statuses.clear();
			statuses.addAll(newStatuses);
			if (settings.LOG_STATUSCHANGES) logEvent("<b>" + getTraitValue(BasicTrait.Name) + "</b> status set to <b>[" + getStatusesString() + "]</b>");
			mPcs.firePropertyChange("Statuses", oldStatuses, getStatusesString());
		}
	}
	public void removeStatus(ActorStatus status) {
		if (statuses.contains(status)) {
			statuses.remove(status);
			if (settings.LOG_STATUSCHANGES) logEvent("<b>" + getTraitValue(BasicTrait.Name) + "</b> removed status <b>" + status + "</b>, now has [" + getStatusesString() + "]");
			mPcs.firePropertyChange("Status", status, null);
		}
	}
	public void clearStatuses() {
		if (statuses.size() > 0) {
			int size = statuses.size();
			statuses.clear();
			mPcs.firePropertyChange("Status", size, null);
		}
	}
	public String getStatusesString() {
		int[] scodes = new int[statuses.size()];
		int i = 0;
		for (ActorStatus as: statuses) 
			scodes[i++] = as.ordinal();
		Arrays.sort(scodes);
		String text = "";
		for (int j = 0; j < scodes.length; ++j) {
			if (j != 0) {
				text += ", ";
			}
			text += ActorStatus.values()[scodes[j]].toString();
		}
		return text;
	}

	// Type
	public ActorType getType() {
		return type;
	}
	public void setType(ActorType type) {
		if (this.type != type) {
			ActorType oldType = this.type;
			logEvent("<b>" + getTraitValue(BasicTrait.Name) + "</b> type changed to <b>" + type + "</b>");
			this.type = type;
			mPcs.firePropertyChange("Type", oldType, type);	
		}
	}
	
	// Attack Table
	public int getDefaultAttack() {
		return defaultAttack;
	}
	public void setDefaultAttack(int index) {
		if (defaultAttack != index && index < attacks.size()) {
			int oldDefault = defaultAttack;
			defaultAttack = index;
			mPcs.firePropertyChange("DefaultAttack", oldDefault, defaultAttack);
		}
	}
	public int getNumAttacks() {
		return attacks.size();
	}
	public Attack getAttack(int index) {
		return attacks.get(index);
	}
	public void addAttack(Attack attack) {
		attacks.add(attack);
		mPcs.firePropertyChange("Attacks", attacks.size()-1 ,attacks.size());
	}
	public void removeAttack(int index) {
		if (index < attacks.size()) {
			if (index < defaultAttack)
				--defaultAttack;
			if (index == defaultAttack)
				defaultAttack = 0;
			attacks.remove(index);
			mPcs.firePropertyChange("Attacks", attacks.size()+1, attacks.size());
		}
	}
	
	// Traits
	public boolean hasTrait(String name) {
		return traits.containsKey(name);
	}
	public static boolean isBasicTrait(String name) {
		for (BasicTrait b: BasicTrait.values()) {
			if (b.name().equals(name)) return true;
		}
		return false;
	}
	
	// Get functions
	private Trait getTrait(String name) {
		if (!traits.containsKey(name)) { // This is not a serious error?
			System.out.println("-E- Actor:GetTrait: requested trait that does not exist: " + name);
			return null; // Consider returning a new trait with no value? Probably not with the change support.
		} else {
			return traits.get(name);
		}
	}
	private Trait getTrait(BasicTrait trait) {
		return traits.get(trait.toString());
	}
	public String getTraitValue(String name) {
		return getTrait(name).value;
	}
	public String getTraitValue(BasicTrait trait) {
		return getTrait(trait).value;
	}
	public int getTraitValueInt(BasicTrait trait) {
		try {
			return Integer.parseInt(getTrait(trait).value);
		} catch (NumberFormatException e) {
			System.err.println("Actor: getValueInt: Error parsing value: " + getTrait(trait).value);
			return 0;
		}
	}
	public Collection<String> getAllTraitNames() {
		return traits.keySet();
	}
	
	// SetTrait functions
	/**
	 * Set the value of a trait, adding if that trait does not exist
	 * @param name - the name of the trait
	 * @param value - the value of the trait
	 */
	public void setTrait(String name, String value) {
		if (!hasTrait(name)) { System.err.println("Actor.setTrait: trait does not exist: " + name); return; }
		Trait trait = getTrait(name);
		if (!trait.value.equals(value)) {
			String oldValue = trait.value;
			// And here is all the magic reporting!
			if (name == "Injury") {
				int newValue = Integer.valueOf(value);
				int HP = getTraitValueInt(BasicTrait.HP);
				int diff = getTraitValueInt(BasicTrait.Injury) - newValue;
				if (diff < 0) {
					setTemp("shock.next", getTempInt("shock.next") - diff);
					logEvent("<b>" + getTraitValue(BasicTrait.Name) + "</b> took <b><font color=red>" + (-1*diff) + "</font></b> damage (now " + (HP - newValue) + " HP).");
				} else {
					logEvent("<b>" + getTraitValue(BasicTrait.Name) + "</b> healed <b><font color=blue>" + diff + "</font></b> (now " + (HP - newValue) + " HP).");		
				}
				
			} else if (name == "Fatigue") {
				int newValue = Integer.valueOf(value);
				int FP = getTraitValueInt(BasicTrait.FP);
				int diff = getTraitValueInt(BasicTrait.Fatigue) - newValue;
				if (diff < 0) {
					logEvent("<b>" + getTraitValue(BasicTrait.Name) + "</b> lost <b>" + (-1*diff) + "</b> fatigue (now " + (FP - newValue) + " FP).");
				} else {
					logEvent("<b>" + getTraitValue(BasicTrait.Name) + "</b> recoverd <b>" + diff + "</b> fatigue (now " + (FP - newValue) + " FP).");		
				}
			}
			
			trait.value = value;
			mPcs.firePropertyChange("trait." + name, oldValue, value);
		}
	}
	public void setTrait(BasicTrait trait, String value) {
		setTrait(trait.toString(), value);
	}
	public void setTrait(BasicTrait trait, int value) {
		setTrait(trait, String.valueOf(value));
	}
	public void setTrait(String name, int value) {
		setTrait(name, String.valueOf(value));
	}
	
	// Add
	public boolean addTrait(String name, String value) {
		if (hasTrait(name)) { 
			System.err.println("Actor.addTrait: trait already exists! " + name); 
			return false;
		}
		Trait newTrait = new Trait(name, value);
		traits.put(name, newTrait);
		mPcs.firePropertyChange("trait." + name, null, value);
		return true;
	}
	
	/**
	 * Remove a trait (BasicTraits cannot be removed)
	 * @param name - the name of the trait to remove
	 * @return success
	 */
	public boolean removeTrait(String name) {
		if (isBasicTrait(name)) {
			System.out.println("-E- removeTrait: cannot remove basic traits! => " + name);
			return false;
		} else if (!hasTrait(name)) {
			System.out.println("-E- removeTrait: cannot remove non-existant trait! => " + name);
			return false;
		} else {
			String oldValue = getTraitValue(name);
			traits.remove(name);
			mPcs.firePropertyChange("trait." + name, oldValue, null);
			return true;
		}
	}
	
	/**
	 * Rename a trait, checking for the new name already taken or a BasicTrait
	 * @param oldName - the current trait name
	 * @param newName - the proposed new trait name
	 * @return success
	 */
	public boolean renameTrait(String oldName, String newName) {
		if (isBasicTrait(oldName)) {
			System.out.println("-E- renameTrait: Cannot rename BasicTrait! oldName: " + oldName + ", newName: " + newName);
			return false;
		} else if (hasTrait(newName)) {
			System.out.println("-E- renameTrait: New trait name already exists! oldName: " + oldName + ", newName: " + newName);
			return false;
		} else if (!hasTrait(oldName)) {
			System.out.println("-E- renameTrait: Old name does not exist! oldName: " + oldName + ", newName: " + newName);
			return false;
		} else {
			Trait trait = getTrait(oldName);
			String value = trait.value;
			trait.name = newName;
			traits.remove(oldName);
			mPcs.firePropertyChange("trait." + oldName, value, null);
			traits.put(newName, trait);
			mPcs.firePropertyChange("trait." + newName, null, value);
			return true;
		}
	}

	// Temps
	public boolean hasTemp(String name) {
		return temps.containsKey(name);
	}
	private Trait getTemp(String name) {
		return temps.get(name);
	}
	public String getTempValue(String name) {
		return temps.get(name).value;
	}
	public int getTempInt(String name) {
		return Integer.parseInt(getTemp(name).value);
	}
	public void setTemp(String name, String value) {
		if (hasTemp(name)) {
			getTemp(name).value = value;
		} else {
			Trait newTrait = new Trait(name, value);
			temps.put(name, newTrait);
		}
	}
	public void setTemp(String name, int value) {
		setTemp(name, String.valueOf(value));
	}
	public Collection<String> getAllTempNames() {
		return temps.keySet();
	}
	
	// Change Support
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        mPcs.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        mPcs.removePropertyChangeListener(listener);
    }
    //================================================================================
    // Basic Operations
    //================================================================================
	
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
		String Name = getTraitValue(BasicTrait.Name);
		int Injury = getTraitValueInt(BasicTrait.Injury);
		int HP = getTraitValueInt(BasicTrait.HP);
		int HT = getTraitValueInt(BasicTrait.HT);
		
		// Resolve auto actions at start of actor's turn:
    	if (type == ActorType.Enemy) { // Do AUTO actions
			if (settings.AUTO_UNCONSCIOUS 
					&& Injury >= HP
					&& !(hasStatus(ActorStatus.Unconscious) 
						|| hasStatus(ActorStatus.Disabled) 
						|| hasStatus(ActorStatus.Dead) 
						|| hasStatus(ActorStatus.Waiting))) { 
				int penalty = (int) (-1*(Math.floor((double)Injury/HP)-1));
				int result = DieRoller.roll3d6();
				String details = "(HT: " + HT + ", penalty: " + penalty + ", roll: " + result + ")";
				if (DieRoller.isFailure(result, HT+penalty)) {
					logEvent("<b>" + Name + "</b> <b><font color=red>failed</font></b> consciousness roll " + details);
					clearStatuses(); // Clear other status' (Attacking/whatever)
					addStatus(ActorStatus.Unconscious);
					addStatus(ActorStatus.Prone);
					addStatus(ActorStatus.Disarmed);
				} else {
					logEvent("<b>" + Name + "</b> passed consciousness roll " + details);
				}
			}
			if (settings.AUTO_STUNRECOVERY) {
				removeStatus(ActorStatus.StunRecovr); // This was from last turn, so recovered this turn
				if (hasStatus(ActorStatus.StunMental)) {
					int recoverTarget = getTraitValueInt(BasicTrait.IQ);
					if (hasTrait("CR")) recoverTarget += 6;
					int roll = DieRoller.roll3d6();
					if (DieRoller.isSuccess(roll, recoverTarget)) {
						logEvent("<b>" + Name + "</b> is now recovering from Mental Stun (rolled " + roll + " against " + recoverTarget + ")"); 
						removeStatus(ActorStatus.StunMental);
						addStatus(ActorStatus.StunRecovr);
					} else {
						logEvent("<b>" + Name + "</b> <b>failed</b> recovery roll for Mental Stun (rolled " + roll + " against " + recoverTarget + ")"); 
					}
				} else if (hasStatus(ActorStatus.StunPhys)) {
					int recoverTarget = getTraitValueInt(BasicTrait.HT);
					int roll = DieRoller.roll3d6();
					if (DieRoller.isSuccess(roll, recoverTarget)) {
						logEvent("<b>" + Name + "</b> is now recovering from Physical Stun (rolled " + roll + " against " + recoverTarget + ")");
						removeStatus(ActorStatus.StunPhys);
						addStatus(ActorStatus.StunRecovr);
					} else {
						logEvent("<b>" + Name + "</b> <b>failed</b> recovery roll for Physical Stun (rolled " + roll + " against " + recoverTarget + ")"); 
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
	
	private void logEvent(String text) {
		if (LogEventSource != null)
			LogEventSource.fireEncounterLogEvent(new EncounterLogEvent(this, text));
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

	public void Attack() {
		String Name = getTraitValue(BasicTrait.Name);
		if (attacks.size() < 1) {
			logEvent("<i><font color=gray>" + Name + " has no attacks defined!</font></i>");
			return;
		}	
		if (defaultAttack < 0 || defaultAttack >= attacks.size()) {
			logEvent("<i><font color=gray>" + Name + " has invalid default attack: " + defaultAttack + "</font></i>");
			return;
		}
		Attack attack = attacks.get(defaultAttack);
		int eff_skill = attack.Skill;
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
		logEvent("<b> " + Name + "</b> attacks with " + attack.Name + ": " + hit_miss +  " (" + roll + "/" + eff_skill + "=" + margin + ") for damage " + (isHit?"<font color=red><b>":"") + damage.BasicDamage + armorDivStr + " " + damage.Type + (isHit?"</b></font>":"") + " (" + attack.Damage + ")" + crit_string);
	}
	
    //================================================================================
    // Defense Support
    //================================================================================

	public String Defend(Defense defense) {
		ProcessDefense(defense);
		GenerateDefenseLog(defense);
		KnockdownStunningCheck(defense);
		return "";
	}
	
	 private void ProcessDefense(Defense defense) {
		int Fatigue = getTraitValueInt(BasicTrait.Fatigue);
		int Injury = getTraitValueInt(BasicTrait.Injury);
		// Process shield damage/injury/fatigue
		setTemp("shieldDamage", getTempInt("shieldDamage") + defense.shieldDamage);
		setTrait(BasicTrait.Injury, String.valueOf(Injury+defense.injury));
		setTrait(BasicTrait.Fatigue, String.valueOf(Fatigue+defense.fatigue));
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
	    
    private void KnockdownStunningCheck(Defense defense) {
    	String Name = getTraitValue(BasicTrait.Name);
    	int HT = getTraitValueInt(BasicTrait.HT);
 		if (defense.cripplingInjury || defense.majorWound) {
 			int effHT = HT + defense.location.knockdownPenalty;
 			if (hasTrait("HPT")) effHT += 3;
 			if (hasTrait("LPT")) effHT -= 4;
 			int roll = DieRoller.roll3d6();
 			boolean success = DieRoller.isSuccess(roll, effHT);
 			logEvent("<b>" + Name + "</b> Knockdown/Stunning check: rolled " + roll + " against " + effHT + " => " + (!success?"<b>failed</b>":"succeeded") + (settings.AUTO_KNOCKDOWNSTUN?"":" (not applied)"));
 			if (settings.AUTO_KNOCKDOWNSTUN) {
 				if (!success) {
 					addStatus(ActorStatus.StunPhys); // Check for mental stun and don't add this in that case?
 					removeStatus(ActorStatus.StunRecovr);
 					addStatus(ActorStatus.Prone);
 					removeStatus(ActorStatus.Kneeling);
 					addStatus(ActorStatus.Disarmed);
 				}
 			}
 		}
    }
	    
    private void GenerateDefenseLog(Defense defense) {
    	String Name = getTraitValue(BasicTrait.Name);
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
		
		String damageDescription = "";
		if (defense.injury != 0) {
			damageDescription = " Sustained <b><font color=red>" + defense.injury + "</font></b> injury to the " + defense.location.description;
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
			
		logEvent("<b>" + Name + "</b> " + defenseDescription + damageDescription);
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
