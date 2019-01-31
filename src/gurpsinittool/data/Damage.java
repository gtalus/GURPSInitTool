package gurpsinittool.data;

import gurpsinittool.data.DamageExpression.DamageType;
import gurpsinittool.data.HitLocations.HitLocation;
import gurpsinittool.data.traits.InjuryTolerance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Damage {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(Damage.class.getName());
	
	public int basicDamage;
	public double armorDivisor;
	public boolean explosive; // 'ex' keyword. Only used for diffuse max damage currently. Stand-in for Explosive, Wide Area and Cone attacks
	public DamageType type;
	
	// Ordered list of valid armor divisor levels. Below these levels is '1' and above is 'ignores DR'. It's also legal for the divisor of a weapon to be less than one, but hardening cannot cause the divisor to drop below one.
	public static ArrayList<Integer> armorDivisorLevels = new ArrayList<Integer>(Arrays.asList(2, 3, 5, 10, 100));
	
	public Damage(int basic, DamageType type) {
		this(basic,1,type,false);
	}

	public Damage(int basic, double divisor, DamageType type, boolean explosive) {
		basicDamage=basic;
		armorDivisor=divisor;
		this.type = type;
		this.explosive = explosive;
		
		// Apply minimum damage based on type
		int minDamage = (type==DamageType.cr)?0:1;
		basicDamage = (basicDamage < minDamage)?minDamage:basicDamage;
	}
	
	public double getWoundingModifier() {
		return getWoundingModifier(type);
	}
	
	public static double getWoundingModifier(DamageType type) {
		switch (type) {
		case pi_:
			return 0.5;
		case burn:
		case cor:
		case tox:
		case cr:
		case pi:
		case tbb:
			return 1;
		case cut:
		case pi4:
			return 1.5;
		case pi44:
		case imp:
			return 2;
		case fat:
		case spec:
		case aff:
			return 0; // Fatigue, Special, Affliction not current supported
		}
		if (LOG.isLoggable(Level.SEVERE)) {LOG.severe("getWoundingModifier: Unhandled type! " + type.toString());}
		return 0;
	}
}
