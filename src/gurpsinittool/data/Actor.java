package gurpsinittool.data;

import gurpsinittool.data.Defense.DefenseResult;
import gurpsinittool.data.Defense.DefenseType;
import gurpsinittool.util.DieRoller;
import gurpsinittool.util.EncounterLogEvent;
import gurpsinittool.util.EncounterLogEventSource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/** 
 * Encapsulates a single actor in the current encounter.
 * Contains all status information.
 *
 */
public class Actor 
	implements Serializable {

	// Default SVUID
	private static final long serialVersionUID = 1L;
	public static EncounterLogEventSource LogEventSource; // where to send log messages
	public static GameSettings settings; // Settings which determine game behavior, including automation
	
	public enum ActorStatus {Attacking, Waiting, Disarmed, Stunned, Prone, Kneeling, Disabled, Unconscious, Dead};
	// Position, Status, Action, Disarmed
	// TODO: hide behind interface and log changes
	public HashSet<ActorStatus> Status;

	public enum ActorType {PC, Enemy, Ally, Neutral, Special};
	// TODO: hide behind interface, and log changes
	public ActorType Type;
	
	// All actors must have these traits defined:
	public enum BasicTrait {Name, ST, HP, Speed, DX, Will, Move, IQ, Per, HT, FP, SM, Fatigue, Injury, Dodge, Parry, Block, DR, Shield_DB, Shield_DR, Shield_HP, Notes}
	//private Class<?>[] basicTraitClasses = {String.class, Integer.class, Integer.class, Float.class, Integer.class, Integer.class, Integer.class, 
	//		Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, 
	//		Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class};
	// Default values?
	
	public class Trait implements Serializable {
		// Default SVUID
		private static final long serialVersionUID = 1L;

		// type?
		// String, Integer, Float, calculated?, boolean?, valueless?
		// Need to think about how to support traits that modify other traits
		// value?
		// Currently store as a string, do some conversions
		public String name;
		public String value;
		
		public Trait() { name = ""; value = ""; }
		public Trait(String name, String value) {this.name = name; this.value = value; }
		public Trait(Trait aTrait) {name = aTrait.name; value = aTrait.value; }
		
		public int valueAsInt() {
			// do the parsing/conversion here
			return 0;
		}
	}
	protected HashMap<String, Trait> traits = new HashMap<String, Trait>();
	
	// Attack info
	// TODO: hide behind interface?
	public int DefaultAttack = 0;
	public ArrayList<Attack> Attacks;

	// Volatile (not currently stored)
	// public int Shock = 0;
	//public String tag;
	public int ShieldDamage = 0;
	public int numParry = 0;
	public int numBlock = 0;

    //================================================================================
    // Constructors
    //================================================================================

	/**
	 * Basic constructor providing default options for all BasicTraits
	 * @param name : Actor's name
	 * @param type : Actor type (PC, NPC, etc)
	 */
	public Actor(String name, ActorType type) {
		Status = new HashSet<ActorStatus>();
		this.Type = type;
		
		InitializeBasicTraits();
		setTrait(BasicTrait.Name, name);
		
		Attacks = new ArrayList<Attack>();
	}
	
	/**
	 * Copy constructor
	 * @param anActor reference Actor
	 */
	public Actor(Actor anActor) {
		this(anActor.getTrait(BasicTrait.Name).value,anActor.Type);
		Status.addAll(anActor.Status);
		
		// Deep copy of traits
		for (Object value : anActor.traits.values()) {
			Trait trait = (Trait) value;
			setTrait(trait.name, trait.value);
		}
		
		for(int i = 0; i < anActor.Attacks.size(); ++i) {
			Attacks.add(anActor.Attacks.get(i));
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
			setTrait(traitName, traitValue);			
		}
	}
	
    //================================================================================
    // Trait Accessors
    //================================================================================

	public boolean hasTrait(String name) {
		return traits.containsKey(name);
	}
	
	public Trait getTrait(BasicTrait trait) {
		if (!traits.containsKey(trait.toString())) { // This is a serious error
			System.err.println("ERROR in Actor:getTrait unable to find basic trait " + trait);
			return null; // Consider returning a new trait with no value?
		} else {
			return traits.get(trait.toString());
		}
	}
	
	public String getValue(BasicTrait trait) {
		return getTrait(trait).value;
	}
	
	public int getValueInt(BasicTrait trait) {
		try {
			return Integer.parseInt(getTrait(trait).value);
		} catch (NumberFormatException e) {
			System.out.println("Actor: getValueInt: Error parsing value: " + getTrait(trait).value);
			e.printStackTrace();
			return 0;
		}
	}
	
	public Trait getTrait(String name) {
		if (!traits.containsKey(name)) { // This is not a serious error?
			System.out.println("-E- Actor:GetTrait: requested trait that does not exist: " + name);
			return null; // Consider returning a new trait with no value?
		} else {
			return traits.get(name);
		}
	}
	
	public void setTrait(BasicTrait trait, String value) {
		Trait theTrait = getTrait(trait);
		theTrait.value = value;
	}
	
	public void setTrait(BasicTrait trait, int value) {
		setTrait(trait, String.valueOf(value));
	}
	
	public void setTrait(String name, String value) {
		if (hasTrait(name)) {
			Trait theTrait = getTrait(name);
			theTrait.value = value;
		} else {
			traits.put(name, new Trait(name, value));
		}
	}
	
	public void setTrait(String name, int value) {
		setTrait(name, String.valueOf(value));
	}

    //================================================================================
    // Basic Operations
    //================================================================================
	
	public void NextTurn() {
		numParry = 0;
		numBlock = 0;
		// Shock
		String Name = getValue(BasicTrait.Name);
		int Injury = getValueInt(BasicTrait.Injury);
		int HP = getValueInt(BasicTrait.HP);
		int HT = getValueInt(BasicTrait.HT);
		
		// Resolve auto actions at start of actor's turn:
    	if (Type == ActorType.Enemy) { // Do AUTO actions
			if (settings.AUTO_UNCONSCIOUS 
					&& Injury >= HP
					&& !(Status.contains(ActorStatus.Unconscious) 
						|| Status.contains(ActorStatus.Disabled) 
						|| Status.contains(ActorStatus.Dead) 
						|| Status.contains(ActorStatus.Waiting))) { 
				int penalty = (int) (-1*(Math.floor((double)Injury/HP)-1));
				int result = DieRoller.roll3d6();
				String details = "(HT: " + HT + ", penalty: " + penalty + ", roll: " + result + ")";
				if (result > HT+penalty) {
					logEvent("<b>" + Name + "</b> <b><font color=red>failed</font></b> consciousness roll " + details);
					Status.clear(); // Clear other status' (Attacking/whatever)
					Status.add(ActorStatus.Unconscious);
					Status.add(ActorStatus.Prone);
					Status.add(ActorStatus.Disarmed);
				} else {
					logEvent("<b>" + Name + "</b> passed consciousness roll " + details);
				}
			}
			if (settings.AUTO_ATTACK && Status.contains(ActorStatus.Attacking))
				Attack();
		}
	}
	
	/**
	 * Reset actor state- Active, 0 Injury, 0 fatigue, numParry/numBlock/ShieldDamage = 0
	 */
	public void Reset() {
		Status.clear();
		setTrait(BasicTrait.Injury, "0");
		setTrait(BasicTrait.Fatigue, "0");
		numParry = 0;
		numBlock = 0;
		ShieldDamage = 0;
	}
	
	private void logEvent(String text) {
		if (LogEventSource != null)
			LogEventSource.fireEncounterLogEvent(new EncounterLogEvent(this, text));
	}
	
    //================================================================================
    // Attack Support
    //================================================================================

	public void Attack() {
		String Name = getValue(BasicTrait.Name);
		if (Attacks.size() < 1) {
			logEvent("<i><font color=gray>" + Name + " has no attacks defined!</font></i>");
			return;
		}	
		if (DefaultAttack < 0 || DefaultAttack >= Attacks.size()) {
			logEvent("<i><font color=gray>" + Name + " has invalid default attack: " + DefaultAttack + "</font></i>");
			return;
		}
		Attack attack = Attacks.get(DefaultAttack);
		int roll = DieRoller.roll3d6();
		int margin = attack.Skill - roll;
		String hit_miss;
		String crit_string = "";
		if (DieRoller.isCritFailure(roll, attack.Skill)) {
			hit_miss = "<b><font color=red>Critical miss</font></b>";
			CriticalTables.Entry crit_result = CriticalTables.getRandomEntry(CriticalTables.critical_miss);
			crit_string = "<br/> <i><font color=gray><b>Critical Miss Table Result:</b>" + crit_result.notes + "</font></i>";
		}
		else if (DieRoller.isCritSuccess(roll, attack.Skill)) {
			hit_miss = "<b><font color=blue>Critical hit</font></b>";
			CriticalTables.Entry crit_result = CriticalTables.getRandomEntry(CriticalTables.critical_hit);
			crit_string = "<br/> <i><font color=gray><b>Critical Hit Table Result:</b>" + crit_result.notes + "</font></i>";
		}
		else if (DieRoller.isSuccess(roll, attack.Skill))
			hit_miss = "<b>hit</b>";
		else 
			hit_miss = "miss";
		Damage damage = Damage.ParseDamage(attack.Damage);
		if (attack.Unbalanced) {
			++numParry;
		}
		String armorDivStr = "";
		if (damage.ArmorDivisor != 1) {
			if (damage.ArmorDivisor == (int) damage.ArmorDivisor)
				armorDivStr = "(" + String.format("%d", (int)damage.ArmorDivisor) + ")";
			else
				armorDivStr = "(" + String.format("%s", damage.ArmorDivisor) + ")";
		}			
		logEvent("<b> " + Name + "</b> attacks with " + attack.Name + ": " + hit_miss +  " (" + roll + "/" + attack.Skill + "=" + margin + ") for damage <font color=red><b>" + damage.BasicDamage + armorDivStr + " " + damage.Type + "</b></font> (" + attack.Damage + ")" + crit_string);
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
		int Fatigue = getValueInt(BasicTrait.Fatigue);
		int Injury = getValueInt(BasicTrait.Injury);
		// Process shield damage/injury/fatigue
		ShieldDamage += defense.shieldDamage;
		setTrait(BasicTrait.Injury, String.valueOf(Injury+defense.injury));
		setTrait(BasicTrait.Fatigue, String.valueOf(Fatigue+defense.fatigue));
		switch (defense.type) { // Record defense attempts
		case Parry:
			++numParry;
			break;
		case Block:
			++numBlock;
			break;
		default:
		}
    }
	    
    private void KnockdownStunningCheck(Defense defense) {
    	String Name = getValue(BasicTrait.Name);
    	int HT = getValueInt(BasicTrait.HT);
 		if (defense.cripplingInjury || defense.majorWound) {
 			int effHT = HT + defense.location.knockdownPenalty;
 			int roll = DieRoller.roll3d6();
 			int MoS = effHT - roll;
 			String success = (MoS<0)?"<b>failed</b>":"succeeded";
 			logEvent("<b>" + Name + "</b> Knockdown/Stunning check: rolled " + roll + " against " + effHT + " (" + success + " by " + Math.abs(MoS) + ")");
 		}
    }
	    
    private void GenerateDefenseLog(Defense defense) {
    	String Name = getValue(BasicTrait.Name);
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
    	int Parry = getValueInt(BasicTrait.Parry);
    	int Block = getValueInt(BasicTrait.Block);
    	int Dodge = getValueInt(BasicTrait.Dodge);
    	int Injury = getValueInt(BasicTrait.Injury);
    	int Fatigue = getValueInt(BasicTrait.Fatigue);
    	int HP = getValueInt(BasicTrait.HP);
    	int FP = getValueInt(BasicTrait.FP);
    	int currentDefense = 0;
    	switch (type) {
    	case Parry:
    		currentDefense = Parry - numParry * 4;
    		break;
    	case Block:
    		currentDefense = Block - numBlock*5;
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
