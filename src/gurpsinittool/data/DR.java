package gurpsinittool.data;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gurpsinittool.data.DamageExpression.DamageType;

public class DR {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(DR.class.getName());
	
	private	int base;
	private int crMod;
	private int hardeningLevels;
	private boolean flexible; // is the armor flexible. Currently has no effect
	
	//private int imp_mod;
	// something more flexible: based on DamageType enum
	//public enum DamageType {aff, burn, cor, cr, cut, fat, imp, pi_, pi, pi4, pi44, spec, tbb, tox};
	
	public DR(final int base) {
		this(base, 0, 0, false);
	}

	public DR(final int base, final boolean flexible) {
		this(base, 0, 0, flexible);
	}

	public DR(final int base, final int crMod, final int hardeningLevels, final boolean flexible) {
		this.base = base;
		this.crMod = crMod;
		this.hardeningLevels = hardeningLevels;
		this.flexible = flexible;
		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Created DR: " + base + ", " + crMod + ", " + flexible);}
	}
	
	public static DR parseDR(String drString) throws ParseException {	
		Matcher matcher;
		final Pattern empty = Pattern.compile("^$");
		final Pattern drPat = Pattern.compile("^(\\d+)(?:/(\\d+))?(\\*)?(?:\\+(\\d+))?(?:\\((\\d+)\\))?$"); // 2, 2*, 2*+2, 2/3*+2,2/3*+2(1)
		//Pattern num = Pattern.compile("^(\\d+)(\\*)?$");
		//Pattern split = Pattern.compile("^(\\d+)/(\\d+)(\\*)?$");

		if ((matcher = empty.matcher(drString)).matches()) {
			return new DR(0);
		}
//		else if ((matcher = num.matcher(drString)).matches()) {
//			int base = Integer.parseInt(matcher.group(1));
//			boolean flexible = "*".equals(matcher.group(2));
//			return new DR(base, flexible);
//		}
//		else if ((matcher = split.matcher(drString)).matches()) {
//			int base = Integer.parseInt(matcher.group(1));
//			int crNum = Integer.parseInt(matcher.group(2));
//			boolean flexible = "*".equals(matcher.group(3));
//			return new DR(base,crNum-base,flexible);
//		}
		else if ((matcher = drPat.matcher(drString)).matches()) {
			int base = Integer.parseInt(matcher.group(1));
			int crMod = 0;
			int hardeningLevels = 0;
			if (matcher.group(2) != null) {
				final int crNum = Integer.parseInt(matcher.group(2));
				crMod = crNum-base;
			}
			boolean flexible = "*".equals(matcher.group(3));
			if (matcher.group(4) != null) {
				final int bonus = Integer.parseInt(matcher.group(4));
				base += bonus;
			}
			if (matcher.group(5) != null) {
				hardeningLevels = Integer.parseInt(matcher.group(5));
			}
			return new DR(base,crMod,hardeningLevels,flexible);
		}
		else {
			throw new ParseException("ParseDR: Unable to parse string: " + drString, 0);
		}
	}
	
	/**
	 * Calculate the effective DR for this damage, including type and armor divisor / hardening
	 * @param damage - the damage being applied to this DR
	 * @return - the effective DR
	 */
	public int getDRforDamage(Damage damage) {
		int effDR = getDRforType(damage.type);
		// Handle hardening / armor divisor
		if (damage.armorDivisor <= 1 || hardeningLevels == 0) { // Just apply AD if 1 or less (which is not impacted by hardening), or if we have no hardening
			effDR = (int)Math.floor(effDR/damage.armorDivisor);
		} else {
			// Find AD index, based on the standard progression. If AD is not a standard level, round up to the next level.
			int ADIndex = Damage.armorDivisorLevels.size();
			for(int i = 0; i < Damage.armorDivisorLevels.size(); i++) {
				if (damage.armorDivisor <= Damage.armorDivisorLevels.get(i)) {
					ADIndex = i;
					break;
				}
			}
			ADIndex -= hardeningLevels;
			int effAD = (ADIndex < 0?1:Damage.armorDivisorLevels.get(ADIndex));
			effDR = (int)Math.floor(effDR/effAD);
		}
		return effDR;
	}
	
	private int getDRforType(DamageType type) {
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
			return base+crMod;
		}
		if (LOG.isLoggable(Level.SEVERE)) {LOG.severe("Unhandled type! " + type.toString());}
		return 0;
	}	
}
