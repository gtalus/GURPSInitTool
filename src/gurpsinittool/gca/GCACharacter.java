package gurpsinittool.gca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GCA Character model
 * @author dcsmall
 *
 */
public class GCACharacter {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(GCACharacter.class.getName());
	
	// Basic values. Just a key => value pair
	private HashMap<String, String> basicValues; 
	
	// idKeys / traits: contains all traits, including child traits
	private HashMap<Integer, GCATrait> traits;
	
	public GCACharacter() {
		basicValues = new HashMap<String, String>();
		traits = new HashMap<Integer, GCATrait>();
	}
	
	/**
	 * Set a basic value
	 * @param name
	 * @param value
	 */
	public void setBasicValue(final String name, final String value) {
		if (basicValues.containsKey(name)) {
			if (LOG.isLoggable(Level.INFO)) {LOG.info("Already have basicValue key '" + name 
					+ ". Value: " + basicValues.get(name) + ", new value (discarded): " + value);}
		} else {
			if (LOG.isLoggable(Level.FINER)) {LOG.finer("Setting Key: '" + name + "', Value: '" + value + "'");}		
			basicValues.put(name, value);
		}
	}
	/**
	 * Determine whether the specified key is present
	 * @param name
	 * @return true if the key is present
	 */
	public boolean hasBasicValue(final String name) {
		return basicValues.containsKey(name);
	}
	/**
	 * Get a basic value
	 * @param name
	 * @return the value, or null if it can't be found
	 */
	public String getBasicValue(final String name) {
		if (basicValues.containsKey(name)) {
			return basicValues.get(name);		
		} else {
			if (LOG.isLoggable(Level.INFO)) {LOG.info("No basicValue key '" + name + "'"); }
			return null;
		}
	}
	
	/**
	 * Add a new trait to the character
	 * @param trait
	 */
	public void addTrait(final GCATrait trait) {
		if (traits.containsKey(trait.getIdNum())) {
			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Already have a trait # " + trait.getIdNum() + "!");}			
		} else {
			traits.put(trait.getIdNum(), trait);
		}
	}
	
	/**
	 * Add a new child trait to the parent
	 * @param parentID - id of parent trait
	 * @param trait
	 */
	public void addChildTrait(final Integer parentID, final GCATrait trait) {
		final GCATrait parent = getTrait(parentID);
		if (parent == null) {
			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Parent trait id '" + parentID + "' not found! Unable to add child!");}
		} else {
			parent.addChild(trait);
			addTrait(trait); // Also add to master trait list (!!?)
		}
	}
	
	/**
	 * Get a trait from the ID num
	 * @param traitID
	 * @return a GCATrait, or null if not found
	 */
	public GCATrait getTrait(final Integer traitID) {
		if (traits.containsKey(traitID)) {
			return traits.get(traitID);
		} else {
			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Trait id '" + traitID + "' not found!");}
			return null;
		}
	}
	
	/**
	 * Search for a trait with the specified section and trait names.
	 * Returns the first trait found which matches, or null if none found.
	 * @param sectionName
	 * @param traitName
	 * @return
	 */
	public GCATrait findTraitByName(final String sectionName, final String traitName) {
		for (final GCATrait trait : traits.values()) {
			if (sectionName.equals(trait.getSection()) && traitName.equals(trait.getName()))
				return trait;
		}
		return null;
	}
	
	/**
	 * Get a list of all traits in the specified section
	 * @param sectionName
	 * @return
	 */
	public Iterable<GCATrait> findTraitsInSection(final String sectionName) {
		final ArrayList<GCATrait> foundTraits = new ArrayList<GCATrait>();
		for (final GCATrait trait : traits.values()) {
			if (sectionName.equals(trait.getSection()))
				foundTraits.add(trait);
		}
		return foundTraits;
	}
}
