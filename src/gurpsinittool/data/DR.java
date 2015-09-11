package gurpsinittool.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import gurpsinittool.data.Damage.DamageType;

public class DR {

	private	int base;
	private int cr_mod;
	//private int imp_mod;
	// something more flexible: based on DamageType enum
	//public enum DamageType {aff, burn, cor, cr, cut, fat, imp, pi_, pi, pi4, pi44, spec, tbb, tox};
	
	public DR(int base) {
		this(base, 0);
	}

	public DR(int base, int cr_mod) {
		this.base = base;
		this.cr_mod = cr_mod;
	}
	
	public static DR ParseDR(String DRString) throws ParseException {
		Matcher matcher;
		Pattern empty = Pattern.compile("^$");
		Pattern num = Pattern.compile("^(\\d+)$");
		Pattern split = Pattern.compile("^(\\d+)/(\\d+)$");

		if ((matcher = empty.matcher(DRString)).matches()) {
			return new DR(0);
		}
		else if ((matcher = num.matcher(DRString)).matches()) {
			int base = Integer.parseInt(matcher.group(1));
			return new DR(base);
		}
		else if ((matcher = split.matcher(DRString)).matches()) {
			int base = Integer.parseInt(matcher.group(1));
			int cr_num = Integer.parseInt(matcher.group(2));
			return new DR(base,cr_num-base);
		}
		else {
			System.out.println("-E- DR:ParseDR: unable to parse string! " + DRString);
			throw new ParseException("ParseDR: Unable to parse string: " + DRString, 0);
		}
	}
	
	public int getDRforType(DamageType type) {
		switch (type) {
		case pi_:
		case burn:
		case cor:
		case tox:
		case pi:
		case tbb:
		case cut:
		case pi4:
		case pi44:
		case fat:
		case spec:
		case aff:
		case imp:
			return base;
		case cr:
			return base+cr_mod;
		}
		System.out.println("-E- DR:getDRforType: unhandled type! " + type.toString());
		return 0;
	}	
}
