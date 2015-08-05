package gurpsinittool.data;

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

import gurpsinittool.util.EncounterLogEvent;
import gurpsinittool.util.EncounterLogEventSource;

public class ActorBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static EncounterLogEventSource LogEventSource; // where to send log messages
	public static GameSettings settings; // Settings which determine game behavior, including automation

	private transient PropertyChangeSupport mPcs = new PropertyChangeSupport(this); // Change reporting, don't serialize!
	
	public enum ActorStatus {Attacking, Disarmed, StunPhys, StunMental, StunRecovr, Kneeling, Prone, Waiting, Disabled, Unconscious, Dead};
	private HashSet<ActorStatus> statuses;

	public enum ActorType {PC, Enemy, Ally, Neutral, Special};
	protected ActorType type;
	
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
	protected int defaultAttack = 0;
	protected ArrayList<Attack> attacks;

	// Volatile (not currently stored or changes reported)
	protected HashMap<String, Trait> temps = new HashMap<String, Trait>();
	 
    //================================================================================
    // Constructors
    //================================================================================

	// Default Constructor
	public ActorBase() {
		this("", ActorType.Enemy);
	}
	/**
	 * Basic constructor providing default options for all BasicTraits
	 * @param name : Actor's name
	 * @param type : Actor type (PC, NPC, etc)
	 */
	public ActorBase(String name, ActorType type) {
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
	public ActorBase(ActorBase anActor) {
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
		String[] defaultBasicTraitValues = {"unnamed", "10", "10", "5.00", "10", "10", "5", "10", "10", "10", "10", "0", "0", "0", "8", "9", "9", "0", "2", "4", "20", ""};
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
			if (settings.LOG_STATUSCHANGES) logEventTypeName("added status <b>" + status + "</b>, now has [" + getStatusesString() + "]");
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
			if (settings.LOG_STATUSCHANGES) logEventTypeName("status set to <b>[" + getStatusesString() + "]</b>");
			mPcs.firePropertyChange("Statuses", oldStatuses, getStatusesString());
		}
	}
	public void removeStatus(ActorStatus status) {
		if (statuses.contains(status)) {
			statuses.remove(status);
			if (settings.LOG_STATUSCHANGES) logEventTypeName("removed status <b>" + status + "</b>, now has [" + getStatusesString() + "]");
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
	// Use 'null' for standing
	public void setPosture(ActorStatus posture) {
		if (posture == ActorStatus.Kneeling) {
			removeStatus(ActorStatus.Prone);
			addStatus(posture);
		} else if (posture == ActorStatus.Prone) {
			removeStatus(ActorStatus.Kneeling);
			addStatus(posture);
		} else if (posture == null) {
			removeStatus(ActorStatus.Kneeling);
			removeStatus(ActorStatus.Prone);
		}
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
	public boolean isTypeAutomated() {
		return (type == ActorType.Enemy || type == ActorType.Ally || type == ActorType.Neutral);
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
			e.printStackTrace();
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
				newValue = Math.max(newValue, 0);
				value = String.valueOf(newValue); // ... and back to string
				int HP = getTraitValueInt(BasicTrait.HP);
				int diff = getTraitValueInt(BasicTrait.Injury) - newValue;
				if (diff < 0) {
					setTemp("shock.next", getTempInt("shock.next") - diff);
					logEventTypeName("took <b><font color=red>" + (-1*diff) + "</font></b> damage (now " + (HP - newValue) + " HP).");
				} else {
					logEventTypeName("healed <b><font color=blue>" + diff + "</font></b> (now " + (HP - newValue) + " HP).");		
				}
			} else if (name == "Fatigue") {
				int newValue = Integer.valueOf(value);
				newValue = Math.max(newValue, 0);
				value = String.valueOf(newValue); // ... and back to string
				int FP = getTraitValueInt(BasicTrait.FP);
				int diff = getTraitValueInt(BasicTrait.Fatigue) - newValue;
				if (diff < 0) {
					logEventTypeName("lost <b>" + (-1*diff) + "</b> fatigue (now " + (FP - newValue) + " FP).");
				} else {
					logEventTypeName("recoverd <b>" + diff + "</b> fatigue (now " + (FP - newValue) + " FP).");		
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
	
	// Log Support
	protected void logEvent(String text) {
		if (LogEventSource != null)
			LogEventSource.fireEncounterLogEvent(new EncounterLogEvent(this, text));
	}
	protected void logEventTypeName(String text) {
		String prepend = "";
		String color = "";
		switch (type) {
		case Enemy:
			color = "red";
			break;
		case Ally:
			color = "blue";
			break;
		case Neutral:
			color = "grey";
			break;
		case PC:
			color = "green";
			break;
		case Special:
			color = "purple";
			break;
		}
		prepend += "[<b><font color=" + color + ">" + type.toString() + "</font></b>] ";
		prepend += "<b>" + getTraitValue(BasicTrait.Name) + "</b> ";
		logEvent(prepend+text);
	}
	protected void logEventError(String text) {
		logEventTypeName("<i><b><font color=red>-E-: " + text + "</font></b></i>");
	}
	
	// Change Support
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        mPcs.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        mPcs.removePropertyChangeListener(listener);
    }
}
