package gurpsinittool.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import gurpsinittool.util.EncounterLogEvent;
import gurpsinittool.util.EncounterLogListener;
import gurpsinittool.util.EncounterLogSupport;
import gurpsinittool.util.MiscUtil;

/** 
 * Base class for actors.
 * Contains all status/trait information.
 * Provides change notification services.
 */
@SuppressWarnings("serial")
public class ActorBase implements Serializable {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(ActorBase.class.getName());
	
	public static GameSettings settings; // Settings which determine game behavior, including automation

	private transient EncounterLogSupport mEls = new EncounterLogSupport(this); // where to send log messages
	private transient PropertyChangeSupport mPcs = new PropertyChangeSupport(this); // Change reporting, don't serialize!
	protected transient UndoableEditSupport mUes = new UndoableEditSupport(); // Undo/Redo reporting 
	protected transient boolean undoRedoInProgress = false; // Suppress undoableEdit creation while true
	
	public enum ActorStatus {Attacking, Disarmed, StunPhys, StunMental, StunRecovr, Kneeling, Prone, Waiting, Disabled, Unconscious, Dead};
	private HashSet<ActorStatus> statuses;

	public enum ActorType {PC, Enemy, Ally, Neutral, Special};
	protected ActorType type;
	
	// All actors must have these traits defined:
	public enum BasicTrait {Name, ST, HP, Speed, DX, Will, Move, IQ, Per, HT, FP, SM, Fatigue, Injury, Dodge, Parry, Block, DR, Shield_DB, Shield_DR, Shield_HP, Notes}
	
	// These traits are calculated using some special function based on other traits. They have no value on their own.
	public enum CalculatedTrait { CurrHP, CurrFP, BasicThrust, BasicSwing, BasicLift }
	
	// TODO: implement change tracking in the trait? If we want to allow other classes to hold a Trait object
	protected class Trait implements Serializable {
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
		public Trait(Trait aTrait) {name = aTrait.name; value = aTrait.value; }
		
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
	 
	// Trait Aliases
	// Only apply to non-basic and non-calculated traits!!
	// Alias is a list of names that are all the same thing
	// All aliases are created equal
	protected static final Map<String, ArrayList<String>> TRAIT_ALIASES;
	static {
		ArrayList<ArrayList<String>> aliases = new ArrayList<ArrayList<String>>();
		aliases.add(new ArrayList<String>(Arrays.asList("Combat Reflexes", "CR")));
		aliases.add(new ArrayList<String>(Arrays.asList("High Pain Threshold", "HPT")));
		aliases.add(new ArrayList<String>(Arrays.asList("Low Pain Threshold", "LPT")));
		aliases.add(new ArrayList<String>(Arrays.asList("Injury Tolerance", "IT")));
		aliases.add(new ArrayList<String>(Arrays.asList("Immunity to Metabolic Hazards", "IMH")));
		
		Map<String, ArrayList<String>> aMap = new HashMap<String, ArrayList<String>>();
		for (ArrayList<String> aliasSet: aliases) {
			for (String alias: aliasSet) {
				if (aMap.containsKey(alias)) {
					if (LOG.isLoggable(Level.SEVERE)) {LOG.severe("static initialization of traitAliases: alias already exists! '" + alias + "'");}
				} else {
					aMap.put(alias, aliasSet);
				}
			}
		}		
		TRAIT_ALIASES = Collections.unmodifiableMap(aMap);
	}
	
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
		attacks = new ArrayList<Attack>();
		
		initializeBasicTraits(name);
	}
	
	/**
	 * Copy constructor
	 * @param anActor reference Actor
	 */
	public ActorBase(ActorBase anActor) {
		this(anActor.getTrait(BasicTrait.Name).value,anActor.type);
		statuses.addAll(anActor.statuses);
		
		// Deep copy of traits
		traits.clear();
		for (Object value : anActor.traits.values()) {
			Trait trait = new Trait((Trait) value);
			traits.put(trait.name, trait);
		}
		
		for(int i = 0; i < anActor.attacks.size(); ++i) {
			attacks.add(anActor.attacks.get(i));
		}
	}
	// Initialize all the basic traits with some defaults
	private void initializeBasicTraits(String name) {
		// Set Basic Trait initial Values
		String[] defaultBasicTraitValues = {name, "10", "10", "5.00", "10", "10", "5", "10", "10", "10", "10", "0", "0", "0", "8", "9", "9", "0", "2", "4", "20", ""};
		BasicTrait[] traitNames = BasicTrait.values();
		for (int i=0; i<traitNames.length; i++) {
			String traitValue = defaultBasicTraitValues[i];
			String traitName = traitNames[i].toString();
			internalAddTrait(traitName, traitValue);			
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
		mEls = new EncounterLogSupport(this);
		mPcs = new PropertyChangeSupport(this);
		mUes = new UndoableEditSupport(this);
		undoRedoInProgress = false;
	}
	
    //================================================================================
    // Accessors
    //================================================================================

	// Statuses
	/**
	 * Check if the Actor has a particular status
	 * @param status - the ActorStatus to check
	 * @return true if the Actor has the status
	 */
	public boolean hasStatus(final ActorStatus status) {
		return statuses.contains(status);
	}
	/**
	 * Add a status to the actor
	 * @param status - the ActorStatus to add
	 */
	public void addStatus(final ActorStatus status) {
		if (!statuses.contains(status)) {
			final HashSet<ActorStatus> oldValue = new HashSet<ActorStatus>(statuses);
			statuses.add(status);
			mPcs.firePropertyChange("Status", null, status);
			if (!undoRedoInProgress) {
				startCompoundEdit();
				if (settings.logStatusChanges.isSet()) logEventTypeName("added status <b>" + status + "</b>, now has [" + getStatusesString() + "]");
				mUes.postEdit(new StatusEdit(oldValue, new HashSet<ActorStatus>(statuses)));
				endCompoundEdit("Status");
			}
		}
	}
	/**
	 * Get a Set of all ActorStatuses the Actor has
	 * @return All ActorStatus flags set for this actor
	 */
	public Set<ActorStatus> getAllStatuses() {
		return new HashSet<ActorStatus>(statuses);
	}
	/**
	 * Replace all currently set ActorStatus flags with the ones provided
	 * @param newStatuses - the set of ActorStatus flags which will be 
	 * set for this actor
	 */
	public void setAllStatuses(final Set<ActorStatus> newStatuses) {
		if (!statuses.containsAll(newStatuses) || !newStatuses.containsAll(statuses)) {
			final HashSet<ActorStatus> oldValue = new HashSet<ActorStatus>(statuses);
			final String oldStatuses = getStatusesString();
			statuses.clear();
			statuses.addAll(newStatuses);
			mPcs.firePropertyChange("Statuses", oldStatuses, getStatusesString());
			if (!undoRedoInProgress) {
				startCompoundEdit();	
				if (settings.logStatusChanges.isSet()) logEventTypeName("status set to <b>[" + getStatusesString() + "]</b>");
				mUes.postEdit(new StatusEdit(oldValue, new HashSet<ActorStatus>(statuses)));
				endCompoundEdit("Status");
			}			
		}
	}
	/**
	 * Remove the specified ActorStatus from this actor
	 * @param status - the ActorStatus flag to remove
	 */
	public void removeStatus(final ActorStatus status) {
		if (statuses.contains(status)) {
			final HashSet<ActorStatus> oldValue = new HashSet<ActorStatus>(statuses);
			statuses.remove(status);
			mPcs.firePropertyChange("Status", status, null);	
			if (!undoRedoInProgress) {
				startCompoundEdit();
				if (settings.logStatusChanges.isSet()) logEventTypeName("removed status <b>" + status + "</b>, now has [" + getStatusesString() + "]");
				mUes.postEdit(new StatusEdit(oldValue, new HashSet<ActorStatus>(statuses)));
				endCompoundEdit("Status");
			}
		}
	}
	/**
	 * Remove all ActorStatus flags for this actor
	 */
	protected void clearStatuses() {
		setAllStatuses(new HashSet<ActorStatus>());
	}
	/**
	 * Get a comma-separated list of all ActorStatus flags set for this actor
	 * @return A comma-separated list of all ActorStatus flags set for this actor
	 */
	public String getStatusesString() {
		int[] scodes = new int[statuses.size()];
		int index = 0;
		for (final ActorStatus as: statuses) 
			scodes[index++] = as.ordinal();
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
	/**
	 * Set the actor's posture to the specified value, or null for Standing
	 * @param posture - the desired posture: Kneeling, Prone, or null for Standing
	 */
	public void setPosture(final ActorStatus posture) {
		// TODO: should be a compound edit?
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

	/**
	 * Get the type of the actor
	 * @return the type of the actor
	 */
	public ActorType getType() {
		return type;
	}
	/**
	 * Set the type of the actor
	 * @param type - the new type of the actor
	 */
	public void setType(final ActorType type) {
		if (this.type != type) {
			final ActorType oldType = this.type;
			this.type = type;
			if (!undoRedoInProgress) { // If this is a replay, don't do this stuff
				startCompoundEdit();
				logEvent("<b>" + getTraitValue(BasicTrait.Name) + "</b> type changed to <b>" + type + "</b>");
				mUes.postEdit(new TypeEdit(oldType, type));
				endCompoundEdit("Type");
			}
			mPcs.firePropertyChange("Type", oldType, type);	
		}
	}
	/**
	 * Report whether the actor is of a type that has automation enabled
	 * @return whether the actor is of a type that has automation enabled
	 */
	public boolean isTypeAutomated() {
		switch (type) {
		case Ally:
			return settings.automateAlly.isSet();
		case Enemy:
			return settings.automateEnemy.isSet();
		case Neutral:
			return settings.automateNeutral.isSet();
		case PC:
			return settings.automatePC.isSet();
		case Special:
			return settings.automateSpecial.isSet();
		default:
			return false;			
		}
	}
	
	// Attack Table
	/**
	 * Get the index of the default attack
	 * @return the index of the default attack
	 */
	public int getDefaultAttack() {
		return defaultAttack;
	}
	/**
	 * Set the default attack index
	 * @param index - the new default attack index
	 */
	public void setDefaultAttack(final int index) {
		if (defaultAttack != index && index < attacks.size() && index >= 0) {
			final int oldDefault = defaultAttack;
			defaultAttack = index;
			mPcs.firePropertyChange("DefaultAttack", oldDefault, defaultAttack);
			if (!undoRedoInProgress)
				mUes.postEdit(new DefaultAttackEdit(oldDefault, index));
		} else if (defaultAttack != index) {
			if (LOG.isLoggable(Level.INFO)) {LOG.info("Actor '" + getTraitValue(BasicTrait.Name) + "': Attempted to set invalid attack id # " + index);}
		}
	}
	/**
	 * Get the number of attacks defined for this actor
	 * @return the number of attacks defined for this actor
	 */
	public int getNumAttacks() {
		return attacks.size();
	}
	/**
	 * Get the Attack object for the specified index
	 * @param index - the index of the Attack object to get
	 * @return the Attack object for the specified index
	 */
	public Attack getAttack(final int index) {
		return new Attack(attacks.get(index));
	}
	/**
	 * Set the attack object at the given index
	 * @param attack - the new attack object
	 * @param index - the index to set
	 */
	public void setAttack(final Attack attack, final int index) {
		if (index < attacks.size()) {
			Attack oldAttack = attacks.get(index);
			attacks.set(index, attack);
			mPcs.firePropertyChange("Attacks", index, index);
			if (!undoRedoInProgress)
				mUes.postEdit(new AttackEdit(oldAttack, attack, index));
		}
	}
	/**
	 * Add a new attack to the end of the attack list
	 * @param attack - the new attack to add
	 */
	public void addAttack(final Attack attack) {
		attacks.add(attack);
		mPcs.firePropertyChange("Attacks", attacks.size()-1 ,attacks.size());
		if (!undoRedoInProgress)
			mUes.postEdit(new AttackEdit(null, attack, attacks.size()-1));
	}
	/**
	 * Remove an attack from the attack list
	 * @param index - the index of the attack to remove
	 */
	public void removeAttack(final int index) {
		if (index < attacks.size()) {
			startCompoundEdit();
			if (index < defaultAttack)
				setDefaultAttack(defaultAttack-1);
			if (index == defaultAttack)
				setDefaultAttack(0);
			final Attack attack = attacks.get(index);
			attacks.remove(index);
			mPcs.firePropertyChange("Attacks", attacks.size()+1, attacks.size());
			if (!undoRedoInProgress)
				mUes.postEdit(new AttackEdit(attack, null, index));
			endCompoundEdit("Edit");
		}
	}
	
    //================================================================================
    // Traits
    //================================================================================
	/**
	 * Determine whether the actor has the specified trait
	 * @param name - the trait to check
	 * @return true if the actor has the specified trait
	 */
	public boolean hasTrait(final String name) {
		if (!traits.containsKey(name)) {
			// Support calculated traits
			if (isCalculatedTrait(name)) {
				return true;
			}
			// Support aliases
			if (TRAIT_ALIASES.containsKey(name)) {
				for (final String alias: TRAIT_ALIASES.get(name)) {
					if (traits.containsKey(alias))
						return true;
				}
			}
			return false;
		}
		return true; 
	}
	/**
	 * Check if this trait is one of the basic traits
	 * @param name - the trait to check
	 * @return true if the trait is a BasicTrait
	 */
	public static boolean isBasicTrait(final String name) {
		try { // See if the string is a valid Calculated trait
			BasicTrait.valueOf(name);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * Check if this trait is one of the calculated traits
	 * @param name - the trait to check
	 * @return true if the trait is a CalculatedTrait
	 */
	public static boolean isCalculatedTrait(final String name) {
		try { // See if the string is a valid Calculated trait
			CalculatedTrait.valueOf(name);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * Check if this trait is not a calculated or basic trait
	 * @param name - the trait to check
	 * @return true if the trait is a custom trait
	 */
	public static boolean isCustomTrait(final String name) {
		return !isCalculatedTrait(name) && !isBasicTrait(name);
	}
	/**
	 * Placeholder for evaluating calculated traits
	 * @param calcTrait - the trait to calculate
	 * @return the value of the specified trait
	 */
	protected String calculateTrait(CalculatedTrait calcTrait) {
		return "0";
	}
	
	// ---------------------
	// Get Trait functions:
	// ---------------------
	private Trait getTrait(String name) {
		if (!traits.containsKey(name)) { // This is not a serious error?
			// Support calculated traits
			if (isCalculatedTrait(name)) {
				return getTrait(CalculatedTrait.valueOf(name));
			}
			// Support aliases
			if (TRAIT_ALIASES.containsKey(name)) {
				for (String alias: TRAIT_ALIASES.get(name)) {
					if (traits.containsKey(alias))
						return traits.get(alias);
				}
			}
			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Trait does not exist: '" + name + "'");}
			return null; // Consider returning a new trait with no value? Probably not with the change support.
		} 
		return traits.get(name);
	}
	private Trait getTrait(BasicTrait trait) {
		return traits.get(trait.toString());
	}
	private Trait getTrait(CalculatedTrait trait) {
		return new Trait(trait.name(), calculateTrait(trait));
	}
	public String getTraitValue(String name) {
		return getTrait(name).value;
	}
	public String getTraitValue(BasicTrait trait) {
		return getTrait(trait).value;
	}
	public int getTraitValueInt(String trait) {
		return MiscUtil.parseIntSafe(getTrait(trait).value);	
	}
	public int getTraitValueInt(BasicTrait trait) {
		return MiscUtil.parseIntSafe(getTrait(trait).value);	
	}
	public ArrayList<String> getTraitValueArray(String name) {
		if (hasTrait(name))
			return new ArrayList<String>(Arrays.asList(getTrait(name).value.toLowerCase().split("\\s*;\\s*")));
		else 
			return new ArrayList<String>();
	}
	public Collection<String> getAllTraitNames() {
		return traits.keySet();
	}
	
	// --------------------
	// SetTrait functions:
	// --------------------
	/**
	 * Set the value of a trait this actor has
	 * @param name - the name of the trait
	 * @param value - the value to set it to
	 */
	public void setTrait(final String name, final String value) {
		if (!hasTrait(name)) { if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Trait does not exist: " + name);} return; }
		if (isCalculatedTrait(name)) { if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Cannot set calculated trait: " + name);} return; }
		if (LOG.isLoggable(Level.FINER)) {LOG.finer("Setting trait: '" + name + "' to '" + value + "'");}
		final Trait trait = getTrait(name);
		if (!trait.value.equals(value)) { // Skip if old value is same as new
			internalSetTrait(trait, value);
		}
	}
	/**
	 * Perform the actual setting, after all the checks
	 * @param trait - trait object to set
	 * @param value - new value to set the trait's value to
	 */
	protected void internalSetTrait(final Trait trait, final String value) {
		final String oldValue = trait.value;
		trait.value = value;
		mPcs.firePropertyChange("trait." + trait.name, oldValue, value);		
		if (!undoRedoInProgress)
			mUes.postEdit(new TraitEdit(trait.name, oldValue, value));
	}
	
	/**
	 * Set the value of a BasicTrait this actor has
	 * @param trait - the trait to set
	 * @param value - the value to set it to
	 */
	public void setTrait(final BasicTrait trait, final String value) {
		setTrait(trait.toString(), value);
	}
	/**
	 * Set the value of a BasicTrait this actor has
	 * @param trait - the trait to set
	 * @param value - the value to set it to
	 */
	public void setTrait(final BasicTrait trait, final int value) {
		setTrait(trait, String.valueOf(value));
	}
	/**
	 * Set the value of a trait this actor has
	 * @param name - the trait to set
	 * @param value - the value to set it to
	 */
	public void setTrait(final String name, final int value) {
		setTrait(name, String.valueOf(value));
	}
	
	/**
	 * Add a trait
	 * @param name - the name of the trait to add
	 * @param value - the new trait's value
	 * @return whether the operation was successful
	 */
	public boolean addTrait(final String name, final String value) {
		if (hasTrait(name)) {
			if (LOG.isLoggable(Level.INFO)) {LOG.info("Trait already exists! " + name);} 
			return false;
		}
		internalAddTrait(name, value);
		mPcs.firePropertyChange("trait." + name, null, value);
		if (!undoRedoInProgress)
			mUes.postEdit(new TraitEdit(name, null, value));		
		return true;
	}
	/**
	 * Internal add trait method: no checks or calls to overridable methods
	 * @param name - name of the trait to add
	 * @param value - the new trait's value
	 */
	private void internalAddTrait(String name, String value) {
		Trait newTrait = new Trait(name, value);
		traits.put(name, newTrait);
	}
	/**
	 * Remove a trait (BasicTraits cannot be removed)
	 * @param name - the name of the trait to remove
	 * @return success
	 */
	public boolean removeTrait(final String name) {
		if (isBasicTrait(name)) {
			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Cannot remove basic traits! => " + name); }
			return false;
		} if (isCalculatedTrait(name)) {
			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Cannot remove calculated traits! => " + name); }
			return false;
		} else if (!hasTrait(name)) {
			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Cannot remove non-existant trait! => " + name); }
			return false;
		} else {
			final Trait trait = getTrait(name);
			traits.remove(trait.name);
			mPcs.firePropertyChange("trait." + trait.name, trait.value, null);
			if (!undoRedoInProgress)
				mUes.postEdit(new TraitEdit(trait.name, trait.value, null));
			return true;
		}
	}
	
	/**
	 * Rename a trait, checking for the new name already taken or a BasicTrait
	 * @param oldName - the current trait name
	 * @param newName - the proposed new trait name
	 * @return success
	 */
	public boolean renameTrait(final String oldName, final String newName) {
		if (isBasicTrait(oldName)) {
			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Cannot rename BasicTrait! oldName: " + oldName + ", newName: " + newName);}
			return false;
		} else if (isCalculatedTrait(oldName)) {
			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Cannot rename CalculatedTrait! oldName: " + oldName + ", newName: " + newName);}
			return false;
		} else if (traits.containsKey(newName)) {
			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("New trait name already exists! oldName: " + oldName + ", newName: " + newName);}
			return false;
		} else if (!traits.containsKey(oldName)) {
			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Old name does not exist! oldName: " + oldName + ", newName: " + newName);}
			return false;
		} else {
			// Alias support
			if (hasTrait(newName) && (getTrait(newName) != getTrait(oldName))) { // Alias exists for different trait!				
				if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Trait alias exists for new name! oldName: " + oldName + ", newName: " + newName);}
				return false;
			}
			final Trait trait = getTrait(oldName);
			trait.name = newName;
			traits.remove(oldName);
			traits.put(newName, trait);
			mPcs.firePropertyChange("traitName", oldName, newName);
			if (!undoRedoInProgress)
				mUes.postEdit(new TraitRenameEdit(oldName, newName));
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
			Trait temp = getTemp(name);
			String oldValue = temp.value;
			temp.value = value;
			if (!undoRedoInProgress)
				mUes.postEdit(new TempEdit(name, oldValue, value));
		} else {
			Trait newTrait = new Trait(name, value);
			temps.put(name, newTrait);
			if (!undoRedoInProgress)
				mUes.postEdit(new TempEdit(name, "0", value)); // TODO: actual add/remove edit support for temps
		}
	}
	public void setTemp(String name, int value) {
		setTemp(name, String.valueOf(value));
	}
	public Collection<String> getAllTempNames() {
		return temps.keySet();
	}
	
	// Log Support
	public void addEncounterLogEventListener(EncounterLogListener listener) {
		mEls.addEncounterLogEventListener(listener);
	}
	public void removeEncounterLogEventListener(EncounterLogListener listener) {
		mEls.removeEncounterLogEventListener(listener);
	}
	protected void logEvent(String text) {
		mEls.fireEncounterLogEvent(new EncounterLogEvent(this, text));
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
	
	/**
	 * Add a property change listener, which will be notified when any aspect of the Actor changes.
	 * Property changes listeners should not implement any game logic, or change other actor
	 * properties in response to property change events. This is because these events are sent for
	 * undo/redo, loading, etc. type actions which should not trigger game logic.
	 *
	 * @param listener - the listener to be notified of changes
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        mPcs.addPropertyChangeListener(listener);
    }
	/**
	 * Remove a property change listener.
	 * @param listener - the listener to be removed
	 */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        mPcs.removePropertyChangeListener(listener);
    }
    
    // UndoableEdit support
	public void addUndoableEditListener(UndoableEditListener listener) {
		mUes.addUndoableEditListener(listener);
	}
	public void removeUndoableEditListener(UndoableEditListener listener) {
		mUes.removeUndoableEditListener(listener);
	}
	/**
	 * Start a compound edit if an undo/redo replay is not in progress
	 */
	protected void startCompoundEdit() { if(!undoRedoInProgress) mUes.postEdit(new HackStartCompound()); }
	/**
	 * End a compound edit if an undo/redo replay is not in progress
	 * @param name - the name of the compound edit to display in the undo/redo menu
	 */
	protected void endCompoundEdit(String name) {  if(!undoRedoInProgress) mUes.postEdit(new HackEndCompound(name)); }
    private class TypeEdit extends AbstractUndoableEdit {
		private static final long serialVersionUID = 1L;
		private ActorType oldValue;
    	private ActorType newValue;
    	public TypeEdit(ActorType oldValue, ActorType newValue) {
    		this.oldValue = oldValue;
    		this.newValue = newValue;
    	}
    	public String getPresentationName() { return "Type"; }    	
    	public void undo() {
			super.undo();
    		// need to suppress undoable edits, but not property change, while this is happening
			undoRedoInProgress = true;
    		setType(oldValue);
			undoRedoInProgress = false;
    	}    	
    	public void redo() {
    		super.redo();
			undoRedoInProgress = true;
    		setType(newValue);  
			undoRedoInProgress = false;
    	}        	
    }
    private class StatusEdit extends AbstractUndoableEdit {
		private static final long serialVersionUID = 1L;
		private HashSet<ActorStatus> oldValue;
    	private HashSet<ActorStatus> newValue;
    	public StatusEdit(HashSet<ActorStatus> oldValue, HashSet<ActorStatus> newValue) {
    		this.oldValue = oldValue;
    		this.newValue = newValue;
    	}
    	public String getPresentationName() { return "Status"; }    	
    	public void undo() {
			super.undo();
    		// need to suppress undoable edits, but not property change, while this is happening
			undoRedoInProgress = true;
    		setAllStatuses(oldValue);
			undoRedoInProgress = false;
    	}    	
    	public void redo() {
    		super.redo();
			undoRedoInProgress = true;
    		setAllStatuses(newValue);
			undoRedoInProgress = false;
    	}        	
    }
    private class TraitRenameEdit extends AbstractUndoableEdit {
    	private String traitOldName;
    	private String traitNewName;
    	public TraitRenameEdit(String traitOldName, String traitNewName) {
    		this.traitOldName = traitOldName;
    		this.traitNewName = traitNewName;
    	}
    	public String getPresentationName() { return "Edit"; }    	
    	public void undo() {
			super.undo();
    		undoRedoInProgress = true;
    		renameTrait(traitNewName, traitOldName);
    		undoRedoInProgress = false;	
    	}    	
    	public void redo() {
    		super.redo();
    		undoRedoInProgress = true;
    		renameTrait(traitOldName, traitNewName);
    		undoRedoInProgress = false;
    	}        	
    }
    private class TraitEdit extends AbstractUndoableEdit {
    	private final String traitName;
    	private final String oldValue;
    	private final String newValue;
    	public TraitEdit(String traitName, String oldValue, String newValue) {
    		this.traitName = traitName;
    		this.oldValue = oldValue;
    		this.newValue = newValue;
    	}
    	public String getPresentationName() { return "Edit"; }
    	
    	public void undo() {
			super.undo();
			undoRedoInProgress = true;
			if (oldValue == null) { // Trait Added				
				removeTrait(traitName);				
			} else if (newValue == null) { //Trait removed
				addTrait(traitName, oldValue);
			} else {// Simple edit
				setTrait(traitName, oldValue);
			}
			undoRedoInProgress = false;
    	}    	
    	public void redo() {
    		super.redo();
			undoRedoInProgress = true;
    		if (oldValue == null) { // Trait Added
    			addTrait(traitName, newValue);
			} else if (newValue == null) { //Trait removed
				removeTrait(traitName);				
			} else {// Simple edit
				setTrait(traitName, newValue);
			}
			undoRedoInProgress = false;
    	}        	
    }
    private class TempEdit extends AbstractUndoableEdit {
    	private String tempName;
    	private String oldValue;
    	private String newValue;
    	public TempEdit(String tempName, String oldValue, String newValue) {
    		super();
    		this.tempName = tempName;
    		this.oldValue = oldValue;
    		this.newValue = newValue;
    	}
    	public String getPresentationName() { return "TEMP"; }
    	
    	public void undo() {
			super.undo();
			undoRedoInProgress = true;
			setTemp(tempName, oldValue);
			undoRedoInProgress = false;
    	}    	
    	public void redo() {
    		super.redo();
    		undoRedoInProgress = true;
    		setTemp(tempName, newValue);
    		undoRedoInProgress = false;
    	}        	
    }
    private class AttackEdit extends AbstractUndoableEdit {
    	private Attack oldAttack;
    	private Attack newAttack;
    	private int position;
    	public AttackEdit(Attack oldAttack, Attack newAttack, int position) {
    		this.oldAttack = oldAttack;
    		this.newAttack = newAttack;
    		this.position = position;
    	}
    	public String getPresentationName() { return "Edit"; }
    	
    	public void undo() {
			super.undo();
			undoRedoInProgress = true;
			//TODO: use ActorBase methods for this!
			if (oldAttack == null) { // Add attack
				attacks.remove(position);
				mPcs.firePropertyChange("Attacks", attacks.size()+1, attacks.size());    	
    		} else if (newAttack == null) {
    			attacks.add(position, oldAttack);
    			mPcs.firePropertyChange("Attacks", attacks.size()-1 ,attacks.size());
    		} else {
    			attacks.set(position, oldAttack);
    			mPcs.firePropertyChange("Attacks", position, null);
    		}
			undoRedoInProgress = false;
    	}    	
    	public void redo() {
    		super.redo();
    		undoRedoInProgress = true;
    		//TODO: use ActorBase methods for this!
    		if (oldAttack == null) { // Add attack
    			attacks.add(position, newAttack);
    			mPcs.firePropertyChange("Attacks", attacks.size()-1 ,attacks.size());
    		} else if (newAttack == null) {
    			attacks.remove(position);
				mPcs.firePropertyChange("Attacks", attacks.size()+1, attacks.size());  
    		} else {
    			attacks.set(position, newAttack);
    			mPcs.firePropertyChange("Attacks", position, null);
    		}
    		undoRedoInProgress = false;
    	}        	
    }    
    private class DefaultAttackEdit extends AbstractUndoableEdit {
    	private int oldValue;
    	private int newValue;
    	public DefaultAttackEdit(int oldValue, int newValue) {
    		super();
    		this.oldValue = oldValue;
    		this.newValue = newValue;
    	}
    	public String getPresentationName() { return "Edit"; }
    	
    	public void undo() {
			super.undo();
			undoRedoInProgress = true;
    		setDefaultAttack(oldValue);
    		undoRedoInProgress = false;
    	}    	
    	public void redo() {
    		super.redo();
			undoRedoInProgress = true;
    		setDefaultAttack(newValue);
    		undoRedoInProgress = false;
    	}        	
    }
    // A signal to the undoManager/GameMaster that it should start a new compound edit
    public class HackStartCompound extends AbstractUndoableEdit {
		private static final long serialVersionUID = 1L;
		public boolean isSignificant() { return false; }
    }
    // A signal to the undoManager/GameMaster that it should end the most recently started compound edit
	public class HackEndCompound extends AbstractUndoableEdit {
 		private static final long serialVersionUID = 1L;
		String display;
    	public HackEndCompound(String display) { this.display = display; }
    	public String getPresentationName() { return display; }
    	public boolean isSignificant() { return false; }
    }
}
