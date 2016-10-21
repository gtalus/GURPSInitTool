package gurpsinittool.app.textfield;

import java.text.ParseException;

import gurpsinittool.data.DR;
import gurpsinittool.data.Damage;

/**
 * Factory class to generate AbstractParsingFieldParser object
 * @author dcsmall
 *
 */
public final class ParsingFieldParserFactory {
	private ParsingFieldParserFactory() {}; // Utility class
	
	/**
	 * Integer parser
	 * @return Integer parser
	 */
	public static AbstractParsingFieldParser IntegerParser() {
		return new AbstractParsingFieldParser() {
			@Override
			public boolean parseIsValid(final String text) {
				try {
					Integer.parseInt(text);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}

			@Override
			public Object parseText(final String text) {
				try {					
					return Integer.parseInt(text);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		};
	}
	/**
	 * Floating point parser
	 * @return
	 */
	public static AbstractParsingFieldParser FloatParser() {
		return new AbstractParsingFieldParser() {
			@Override
			public boolean parseIsValid(final String text) {
				try {
					Float.parseFloat(text);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}

			@Override
			public Object parseText(final String text) {
				try {					
					return Float.parseFloat(text);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		};
	}
	/**
	 * DR string parser
	 * @return
	 */
	public static AbstractParsingFieldParser DRParser() {
		return new AbstractParsingFieldParser() {
			@Override
			public boolean parseIsValid(final String text) {
				try {
					DR.parseDR(text);
					return true;
				} catch (ParseException e) {
					return false;
				}
			}

			@Override
			public Object parseText(final String text) {
				try {					
					return DR.parseDR(text);
				} catch (ParseException e) {
					return null;
				}
			}
		};
	}
	/**
	 * Damage string parser
	 * @return
	 */
	public static AbstractParsingFieldParser DamageParser() {
		return new AbstractParsingFieldParser() {
			@Override
			public boolean parseIsValid(final String text) {
				try {
					Damage.parseDamage(text);
					return true;
				} catch (ParseException e) {
					return false;
				}
			}

			@Override
			public Object parseText(final String text) {
				try {					
					return Damage.parseDamage(text);
				} catch (ParseException e) {
					return null;
				}
			}
		};
	}
}
