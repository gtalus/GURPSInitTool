package gurpsinittool.data.traits;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import gurpsinittool.data.Actor;
import gurpsinittool.data.Damage;
import gurpsinittool.data.HitLocations;
import gurpsinittool.data.DamageExpression.DamageType;
import gurpsinittool.data.HitLocations.HitLocation;
import gurpsinittool.data.HitLocations.LocationType;

public class InjuryTolerance {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(Vulnerability.class.getName());
	
	private enum TT {NoBrain, NoEyes, NoHead, NoNeck, NoVitals, Diffuse, Homeogenous, Unliving, DR2, DR3, DR4};
	
	private static HashSet<TT> toleranceSet = new HashSet<TT>();
	
	/**
	 * Report debug information to the actor about how this trait is parsed
	 * @param actor
	 */
	public static void reportDebugTrait(Actor actor) {
		getTolerances(actor);
		String value = "";
		for(TT tolerance : toleranceSet) {
			value += tolerance + ", ";
		}
		value =  value.equals("")?"none":value;
		actor.setTemp("_InjuryTolerance", value);
	}
	
	/**
	 * Refresh the vulns hash based on the specified actor
	 * @param actor
	 */
	private static void getTolerances(final Actor actor) {
		final ArrayList<String> tolerances = actor.getTraitValueArray("Injury Tolerance"); // Empty array if actor does not have this trait
    	
		toleranceSet.clear();
    	for (String tolerance : tolerances) {
    		if (tolerance.equals("no brain")) {
    			toleranceSet.add(TT.NoBrain);
    		} else if (tolerance.equals("no eyes")) {
    			toleranceSet.add(TT.NoEyes);
    		} else if (tolerance.equals("no head")) {
    			toleranceSet.add(TT.NoHead);
    		} else if (tolerance.equals("no neck")) {
    			toleranceSet.add(TT.NoNeck);
    		} else if (tolerance.equals("no vitals")) {
    			toleranceSet.add(TT.NoVitals);
    		} else if (tolerance.equals("diffuse")) {
    			toleranceSet.add(TT.Diffuse);
    		} else if (tolerance.equals("homogenous")) {
    			toleranceSet.add(TT.Homeogenous);
    		} else if (tolerance.equals("unliving")) {
    			toleranceSet.add(TT.Unliving);
    		} else if (tolerance.equals("damage reduction 2")) {
    			toleranceSet.add(TT.DR2);
    		} else if (tolerance.equals("damage reduction 3")) {
    			toleranceSet.add(TT.DR3);
    		} else if (tolerance.equals("damage reduction 4")) {
    			toleranceSet.add(TT.DR4);
    		} else {
    			if(LOG.isLoggable(Level.INFO)) {LOG.info("Unrecognized injury tolerance type: '" + tolerance + "'"); }
    		}
    	}
	}
	
	/**
	 * Get the injury limit for this actor based on the type of damage
	 * @param actor - the actor to get the injury limit for
	 * @param damage - the damage being received
	 * @return - the limit of injury from this damage
	 */
	public static int getInjuryLimit(final Actor actor, Damage damage) {
		getTolerances(actor);
		// Diffuse
		if(toleranceSet.contains(TT.Diffuse)) {
			if (damage.explosive)
				return Integer.MAX_VALUE;
			else
				return damageMaxDiffuse(damage.type);
		}
		return Integer.MAX_VALUE;
	}
	
	private static int damageMaxDiffuse(DamageType type) {
		switch (type) {
		case pi_:
		case pi:
		case pi4:
		case pi44:
		case imp:
			return 1;	
		case burn:
		case tbb:
		case cor:
		case tox:
		case cr:
		case cut:
			return 2;
		case fat:
		case spec:
		case aff:
			return 0; // Fatigue, Special, Affliction not current supported
		}
		if (LOG.isLoggable(Level.SEVERE)) {LOG.severe("damageMaxDiffuse: Unhandled type! " + type.toString());}
		return 0;
	}
	
	/**
	 * Get value of any damage reduction
	 * @param actor - the actor to use
	 * @return - the damage reduction to apply
	 */
	public static double getDamageReduction(final Actor actor) {
		getTolerances(actor);
		// Injury Tolerance: Damage Reduction
		if (toleranceSet.contains(TT.DR2)) {
			return 2;
		}
		else if (toleranceSet.contains(TT.DR3)) {
			return 3;
		}
		else if (toleranceSet.contains(TT.DR4)) {
			return 4;
		}
		return 1;
	}
			
	/**
     * Check if location is valid for this actor and adjust if necessary
     * @param actor
     */
    public static HitLocation getAdjustedLocation(final Actor actor, HitLocation location) {
    	getTolerances(actor);
    	// No Blood - nothing currently (also Diffuse and Homogenous)
		// No Brain
		if (toleranceSet.contains(TT.NoBrain) || toleranceSet.contains(TT.Diffuse) || toleranceSet.contains(TT.Homeogenous)) {
			// "a blow to the skull or eye is treated no differently than a blow to the face (except that an eye injury can still cripple that eye)"
			// This program currently doesn't track crippling for eyes, so this is the same in every way
			if (location.type == LocationType.Skull || location.type == LocationType.Eye)
				location = HitLocations.getLocation(LocationType.Face);
		}
		// No Eyes
		if (toleranceSet.contains(TT.NoEyes)) {
			if (location.type == LocationType.Eye)
				location = HitLocations.getLocation(LocationType.Face);
		}
		// No Head
		if (toleranceSet.contains(TT.NoHead)) {
			if (location.type == LocationType.Face || location.type == LocationType.Skull)
				location = HitLocations.getLocation(LocationType.Torso);
		}
		// No Neck
		if (toleranceSet.contains(TT.NoNeck)) {
			if (location.type == LocationType.Neck)
				location = HitLocations.getLocation(LocationType.Torso);
		}
		// No Vitals
		if (toleranceSet.contains(TT.NoVitals) || toleranceSet.contains(TT.Diffuse) || toleranceSet.contains(TT.Homeogenous)) {
			if (location.type == LocationType.Vitals || location.type == LocationType.Groin)
				location = HitLocations.getLocation(LocationType.Torso);
		}
		return location;
	}
    
    /**
     * Adjust the specified location-based wounding modifier based on the actor's tolerances
     * @param actor - the actor to use
     * @param type - the type of damage being applied
     * @param woundingModifier - the location based injury modifier
     * @return - the adjusted wounding modifier
     */
	public static double getWoundingModifier(final Actor actor, DamageType type, Double locationWoundingModifier) {
		getTolerances(actor);
		if (locationWoundingModifier > Damage.getWoundingModifier(type)) {
			return locationWoundingModifier; // If the hit location increases the multiplier over base, use it
		} else if (toleranceSet.contains(TT.Homeogenous)) {
			return woundingModifierHomogenous(type);
		} else if (toleranceSet.contains(TT.Unliving)) {
			return woundingModifierUnliving(type);
		} 
		return locationWoundingModifier;
	}
	
	public static double woundingModifierUnliving(DamageType type) {
		switch (type) {
		case pi_:
			return 0.2;
		case pi:
			return 0.334;
		case pi4:
			return 0.5;
		case pi44:
		case imp:
			return 1;	
		case burn:
		case tbb:
		case cor:
		case tox:
		case cr:
			return 1;
		case cut:
			return 1.5;
		case fat:
		case spec:
		case aff:
			return 0; // Fatigue, Special, Affliction not current supported
		}
		if (LOG.isLoggable(Level.SEVERE)) {LOG.severe("Unhandled type! " + type.toString());}
		return 0;
	}
	
	public static double woundingModifierHomogenous(DamageType type) {
		switch (type) {
		case pi_:
			return 0.1;
		case pi:
			return 0.2;
		case pi4:
			return 0.334;
		case pi44:
		case imp:
			return 0.5;	
		case burn:
		case tbb:
		case cor:
		case tox:
		case cr:
			return 1;
		case cut:
			return 1.5;
		case fat:
		case spec:
		case aff:
			return 0; // Fatigue, Special, Affliction not current supported
		}
		if (LOG.isLoggable(Level.SEVERE)) {LOG.severe("Unhandled type! " + type.toString());}
		return 0;
	}
	
}

