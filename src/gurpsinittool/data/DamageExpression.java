package gurpsinittool.data;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gurpsinittool.data.ActorBase.CalculatedTrait;
import gurpsinittool.util.MiscUtil;

/**
 * Holds a parsed damage expression: 'sw+2 cr', '2d+2(2) pi', 
 * 'sw+1d-2(10) cut' and the like
 * @author dcsmall
 *
 */
public final class DamageExpression {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(DamageExpression.class.getName());
	
	private boolean addBasicThrust = false;
	private boolean addBasicSwing = false;
	private DiceAdds diceAdds = new DiceAdds();
	private Double divisor = 1.0;
	
	// _ => -
	// 4 => +
	public enum DamageType {aff, burn, cor, cr, cut, fat, imp, pi_, pi, pi4, pi44, spec, tbb, tox};
	private DamageType type = DamageType.cr;
	
	private boolean explosive = false;
	
	private DamageExpression() {
	}
	private DamageExpression(DamageExpression copy) {
		this.addBasicSwing = copy.addBasicSwing;
		this.addBasicThrust = copy.addBasicThrust;
		this.diceAdds.dice = copy.diceAdds.dice;
		this.diceAdds.adds = copy.diceAdds.adds;
		this.divisor = copy.divisor;
		this.type = copy.type;
		this.explosive = copy.explosive;
	}

	/**
	 * Convert the damage expression into a specific damage value, 
	 * rolling dice if necessary
	 * @param attacker - attacking character. Can be null if expression 
	 * does not contain 'thr' or 'sw'
	 * @return the specific damage result
	 * @throws ParseException - thrown if the expression contains 'thr' 
	 * or 'sw' and the actor is null
	 */
	public Damage getDamage(Actor attacker) throws ParseException {
		DamageExpression simplified = convertBasicDamage(attacker); // get rid of basicThrust/swing
		return new Damage(simplified.diceAdds.roll(), simplified.divisor, simplified.type, simplified.explosive);
	}
	/**
	 * Provide a string format
	 */
	public String toString() {
		ArrayList<String> elements = new ArrayList<String>();
		if (addBasicThrust) 
			elements.add("thr");
		if (addBasicSwing)
			elements.add("sw");
		String diceAddString = diceAdds.toString();
		if (!diceAddString.isEmpty())
			elements.add(diceAddString);
		String output = String.join("+", elements);
		if (output.isEmpty())
			output = "0";
		//output = output.replace("\\+-", "-");
		if (divisor != 1)
			output += "(" + MiscUtil.formatDoubleNicely(divisor) + ")";
		output += " " + damageTypeToString(type);
		if (explosive)
			output += " ex";
		return output;
	}
	/**
	 * Converts basic damage (thr/sw) into actual damage, and adjusts Dice+adds 
	 * if that was done. No change is made if there is no basic damage to convert.
	 * @param attacker - the attacker who's basic damage values to use
	 * @return A new DamageExpression with the basic damage converted
	 * @throws ParseException if the attacker is null and basicDamage information 
	 * is required
	 */
	public DamageExpression convertBasicDamage(Actor attacker) throws ParseException {
		DamageExpression simplified = new DamageExpression(this);
		if (addBasicThrust || addBasicSwing) { // Only go through this if we have to 
			if (attacker == null) {
				ParseException ex = new ParseException("Can't simplify expression with thr/sw and null attacker!", 0);
				if (LOG.isLoggable(Level.WARNING)) {LOG.log(Level.WARNING,ex.getMessage(), ex);}
				throw ex;
			}
			try {
				if (addBasicThrust) {
					simplified.addBasicThrust = false;
					String thrustString = attacker.getTraitValue(CalculatedTrait.BasicThrust.toString());
					DamageExpression thrust = parseDamageExpression(thrustString);
					simplified.diceAdds.add(thrust.diceAdds);
				}
				if (addBasicSwing) {
					simplified.addBasicSwing = false;
					String swingString = attacker.getTraitValue(CalculatedTrait.BasicSwing.toString());
					DamageExpression swing = parseDamageExpression(swingString);
					simplified.diceAdds.add(swing.diceAdds);
				}
			} catch (ParseException ex) {
				if (LOG.isLoggable(Level.WARNING)) {LOG.log(Level.WARNING, "Unexpected parser error while evaluating basic damage! " + ex.getMessage(), ex);}
				throw ex;
			}
			simplified.diceAdds.simplify();
		}
		return simplified;
	}
	
	/**
	 * Parse a string damage expression, returning a DamageExpression object or 
	 * throwing a ParseException
	 * @param damageExpression - a valid damage string
	 * @return a valid DamageExpression object
	 * @throws ParseException - throws a ParseException if the input string is invalid
	 */
	public static DamageExpression parseDamageExpression(final String damageExpression) throws ParseException {
		if (damageExpression.isEmpty()) {
			return new DamageExpression(); // Just return the default empty expression
		}
		Matcher matcher;
		Pattern thrsw = Pattern.compile("^(thr|sw)");
		Pattern dice = Pattern.compile("^(\\d+)d");
		Pattern adds = Pattern.compile("^(\\d+)");
		Pattern more = Pattern.compile("^\\s*(\\+|-)\\s*");
		
		Pattern divisor = Pattern.compile("^\\s*\\((\\d+\\.?\\d*|\\.\\d+)\\)\\s*");
		Pattern type = Pattern.compile("^\\s*([^d\\d\\s]+)\\s*");
		Pattern explosive = Pattern.compile("^\\s*ex\\s*");
		
		//Pattern numdivtype = Pattern.compile("^(\\d+)\\s*(\\(([\\d\\.]+)\\))?\\s*([^d\\d\\s]+)?\\s*(ex)?$"); // x (2) cr 
		//Pattern dicedivtype = Pattern.compile("^(\\d+)d[+]?([-]?\\d+)?\\s*(\\(([\\d\\.]+)\\))?\\s*([^d\\d\\s]+)?\\s*(ex)?$");
		
		final StringBuilder line = new StringBuilder(damageExpression);
		
		// First check thrsw, dice, and adds
		DamageExpression parsedExpression = new DamageExpression();
		parsedExpression.type = DamageType.cut; // Default type for non-empty expressions
		int multiplier = 1; // +1 or -1
		while(true) {
			if ((matcher = thrsw.matcher(line)).lookingAt()) {
				if (multiplier == -1) {
					throw new ParseException("parseDamageExpression: Parsing '" + damageExpression + "': can't have -thr or -sw", 0);
				}
				String value = matcher.group(1);
				if ("thr".equals(value)) {
					if (parsedExpression.addBasicThrust) {
						if (LOG.isLoggable(Level.INFO)) {LOG.info("Damage expression has multiple 'thr' strings!");}
						throw new ParseException("parseDamageExpression: Parsing '" + damageExpression + "': more than one 'thr'!", 0);
					}
					parsedExpression.addBasicThrust = true;
				} else {
					if (parsedExpression.addBasicSwing) {
						if (LOG.isLoggable(Level.INFO)) {LOG.info("Damage expression has multiple 'sw' strings!");}
						throw new ParseException("parseDamageExpression: Parsing '" + damageExpression + "': more than one 'sw'!", 0);
					}
					parsedExpression.addBasicSwing = true;
				} 
			} else if ((matcher = dice.matcher(line)).lookingAt()) {
				parsedExpression.diceAdds.dice += Integer.valueOf(matcher.group(1))*multiplier;
			} else if ((matcher = adds.matcher(line)).lookingAt()) {
				parsedExpression.diceAdds.adds += Integer.valueOf(matcher.group(1))*multiplier;
			} else {
				throw new ParseException("parseDamageExpression: Parsing '" + damageExpression + "': unexpected end of first section!", 0);
			}
			
			line.delete(0, matcher.end()); // get rid of parsed portion
			
			if ((matcher = more.matcher(line)).lookingAt()) {				
				String value = matcher.group(1);
				if ("+".equals(value)) {
					multiplier = 1;
				} else {
					multiplier = -1;
				}
				line.delete(0, matcher.end()); // get rid of parsed portion
			} else {
				break;
			}			
		}
		
		// Second set...
		if ((matcher = divisor.matcher(line)).lookingAt()) {
			parsedExpression.divisor = Double.valueOf(matcher.group(1));
			line.delete(0, matcher.end()); // get rid of parsed portion
		}
		if ((matcher = type.matcher(line)).lookingAt()) {
			parsedExpression.type = parseType(matcher.group(1));
			line.delete(0, matcher.end()); // get rid of parsed portion
		}
		if ((matcher = explosive.matcher(line)).lookingAt()) {
			parsedExpression.explosive = true;
			line.delete(0, matcher.end()); // get rid of parsed portion
		}
		
		// Final check
		if (line.length()!=0) {
			throw new ParseException("parseDamageExpression: Parsing '" + damageExpression + "': text left over! '" + line + "'", 0);
		}
		
		return parsedExpression;
	}

	/**
	 * Parse a damage type string
	 * @param type - the string to parse
	 * @return - a DamageType value, if the string is valid
	 * @throws ParseException if the string is not valid
	 */
	public static DamageType parseType(String type) throws ParseException {
		type = type.replace('-', '_');
		type = type.replace('+', '4');

		try {
			return DamageType.valueOf(type);
		} catch (Exception e) {
			throw new ParseException("ParseType: Unable to parse string: " + type, 0);
		}
	}
	
	public static String damageTypeToString(DamageType type) {
		String output = type.toString();
		output = output.replace('_', '-');
		output = output.replace('4', '+');
		return output;
	}
}
