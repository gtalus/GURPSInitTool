package gurpsinittool.data;

import gurpsinittool.util.DieRoller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public class Damage {

	public int BasicDamage;
	public double ArmorDivisor;

	// _ => -
	// 4 => +
	public enum DamageType {aff, burn, cor, cr, cut, fat, imp, pi_, pi, pi4, pi44, spec, tbb, tox};
	public DamageType Type;
	
	
	
	public Damage(int basic, DamageType type) {
		this(basic,1,type);
	}

	public Damage(int basic, double divisor, DamageType type) {
		BasicDamage=basic;
		ArmorDivisor=divisor;
		Type = type;
	}
	
	public static Damage ParseDamage(String damage) throws ParseException {
		Matcher matcher;
		Pattern empty = Pattern.compile("^$");
		Pattern numdivtype = Pattern.compile("^(\\d+)\\s*(\\(([\\d\\.]+)\\))?\\s*([^d\\d\\s]+)?$");
		Pattern dicedivtype = Pattern.compile("^(\\d+)d[+]?([-]?\\d+)?\\s*(\\(([\\d\\.]+)\\))?\\s*([^d\\d\\s]+)?$");

		if ((matcher = empty.matcher(damage)).matches()) {
			return new Damage(0, DamageType.cr);
		}
		else if ((matcher = numdivtype.matcher(damage)).matches()) {
			int num = Integer.parseInt(matcher.group(1));
			double div = (matcher.group(3) != null)?Double.parseDouble(matcher.group(3)):1;
			DamageType type = (matcher.group(4) != null)?ParseType(matcher.group(4)):DamageType.cut;
			return new Damage(num, div, type);
		}
		else if ((matcher = dicedivtype.matcher(damage)).matches()) {
			int dice = Integer.parseInt(matcher.group(1));
			int adds = (matcher.group(2)==null)?0:Integer.parseInt(matcher.group(2));
			double div = (matcher.group(4) != null)?Double.parseDouble(matcher.group(4)):1;
			DamageType type = (matcher.group(5) != null)?ParseType(matcher.group(5)):DamageType.cut;
			return new Damage(DieRoller.rollDiceAdds(dice, adds), div, type);
		}
		else {
			System.out.println("-E- Damage:ParseDamage: unable to parse string! " + damage);
			throw new ParseException("ParseDamage: Unable to parse string: " + damage, 0);
		}
	}
	
	public double DamageMultiplier() {
		return DamageMultiplier(Type);
	}

	public double DamageMultiplierHomogenous() {
		return DamageMultiplierHomogenous(Type);
	}

	
	public static DamageType ParseType(String type) throws ParseException {
		type = type.replace('-', '_');
		type = type.replace('+', '4');

		try {
			return DamageType.valueOf(type);
		} catch (Exception e) {
			System.out.println("-E- ParseType: unable to parse string! " + type);
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
			return 0; // Fatigue, Special, Affliction
		}
		System.out.println("-E- Damage:DamageMultiplier: unhandled type! " + type.toString());
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
		case cor:
		case tox:
		case cr:
			return 1;
		case cut:
			return 1.5;
		case fat:
		case spec:
		case aff:
			return 0; // Fatigue, Special, Affliction
		}
		System.out.println("-E- Damage:DamageMultiplierHomogenous: unhandled type! " + type.toString());
		return 0;
	}
}
