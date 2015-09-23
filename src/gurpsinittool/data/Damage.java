package gurpsinittool.data;

import gurpsinittool.data.HitLocations.HitLocation;
import gurpsinittool.util.DieRoller;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Damage {

	public int BasicDamage;
	public double ArmorDivisor;
	public boolean Explosive; // 'ex' keyword. Only used for diffuse max damage currently. Stand-in for Explosive, Wide Area and Cone attacks

	// _ => -
	// 4 => +
	public enum DamageType {aff, burn, cor, cr, cut, fat, imp, pi_, pi, pi4, pi44, spec, tbb, tox};
	public DamageType Type;
	
	public Damage(int basic, DamageType type) {
		this(basic,1,type,false);
	}

	public Damage(int basic, double divisor, DamageType type, boolean explosive) {
		BasicDamage=basic;
		ArmorDivisor=divisor;
		Type = type;
		Explosive = explosive;
		
		// Apply minimum damage based on type
		int min_damage = (type==DamageType.cr)?0:1;
		BasicDamage = (BasicDamage < min_damage)?min_damage:BasicDamage;
	}
	
	public static Damage ParseDamage(String damage) throws ParseException {
		Matcher matcher;
		Pattern empty = Pattern.compile("^$");
		Pattern numdivtype = Pattern.compile("^(\\d+)\\s*(\\(([\\d\\.]+)\\))?\\s*([^d\\d\\s]+)?\\s*(ex)?$"); // x (2) cr 
		Pattern dicedivtype = Pattern.compile("^(\\d+)d[+]?([-]?\\d+)?\\s*(\\(([\\d\\.]+)\\))?\\s*([^d\\d\\s]+)?\\s*(ex)?$");

		if ((matcher = empty.matcher(damage)).matches()) {
			return new Damage(0, DamageType.cr);
		}
		else if ((matcher = numdivtype.matcher(damage)).matches()) {
			int num = Integer.parseInt(matcher.group(1));
			double div = (matcher.group(3) != null)?Double.parseDouble(matcher.group(3)):1;
			DamageType type = (matcher.group(4) != null)?ParseType(matcher.group(4)):DamageType.cut;
			boolean explosive = (matcher.group(5) != null)?true:false;
			return new Damage(num, div, type, explosive);
		}
		else if ((matcher = dicedivtype.matcher(damage)).matches()) {
			int dice = Integer.parseInt(matcher.group(1));
			int adds = (matcher.group(2)==null)?0:Integer.parseInt(matcher.group(2));
			double div = (matcher.group(4) != null)?Double.parseDouble(matcher.group(4)):1;
			DamageType type = (matcher.group(5) != null)?ParseType(matcher.group(5)):DamageType.cut;
			boolean explosive = (matcher.group(6) != null)?true:false;
			return new Damage(DieRoller.rollDiceAdds(dice, adds), div, type, explosive);
		}
		else {
			//System.out.println("-W- Damage:ParseDamage: unable to parse string! " + damage);
			throw new ParseException("ParseDamage: Unable to parse string: " + damage, 0);
		}
	}
	
	public double DamageMultiplier() {
		return DamageMultiplier(Type);
	}
	public double DamageMultiplier(HitLocation location) {
		return location.DamageMultiplier(Type);
	}
	public double DamageMultiplierHomogenous() {
		return DamageMultiplierHomogenous(Type);
	}
	public double DamageMultiplierHomogenous(HitLocation location) {
		double locmult = location.DamageMultiplier(Type);
		if (locmult > DamageMultiplier()) // If the hit location increases the multiplier, use it
			return locmult;
		return DamageMultiplierHomogenous();
	}
	public double DamageMultiplierUnliving() {
		return DamageMultiplierUnliving(Type);
	}
	public double DamageMultiplierUnliving(HitLocation location) {
		double locmult = location.DamageMultiplier(Type);
		if (locmult > DamageMultiplier()) // If the hit location increases the multiplier, use it
			return locmult;
		return DamageMultiplierUnliving();
	}
	public int DamageMaxDiffuse() {
		// TODO: support ex/cone/wide area attacks
		if (Explosive)
			return Integer.MAX_VALUE;
		else 
			return DamageMaxDiffuse(Type);
	}
	
	public static DamageType ParseType(String type) throws ParseException {
		type = type.replace('-', '_');
		type = type.replace('+', '4');

		try {
			return DamageType.valueOf(type);
		} catch (Exception e) {
			//System.out.println("-W- ParseType: unable to parse string! " + type);
			throw new ParseException("ParseType: Unable to parse string: " + type, 0);
		}
	}	
	
	public static double DamageMultiplier(DamageType type) {
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
		System.out.println("-E- Damage:DamageMultiplier: unhandled type! " + type.toString());
		return 0;
	}
	
	public static double DamageMultiplierUnliving(DamageType type) {
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
		System.out.println("-E- DamageMultiplierUnliving: unhandled type! " + type.toString());
		return 0;
	}
	
	public static double DamageMultiplierHomogenous(DamageType type) {
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
		System.out.println("-E- Damage:DamageMultiplierHomogenous: unhandled type! " + type.toString());
		return 0;
	}
	
	public static int DamageMaxDiffuse(DamageType type) {
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
		System.out.println("-E- DamageMaxDiffuse: unhandled type! " + type.toString());
		return 0;
	}
}
