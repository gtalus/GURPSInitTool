package gurpsinittool.app.textfield;

import java.text.ParseException;

import gurpsinittool.data.DR;
import gurpsinittool.data.Damage;

public class ParsingFieldParserFactory {

	public static ParsingFieldParser IntegerParser() {
		return new ParsingFieldParser() {
			@Override
			public boolean parseIsValid(String text) {
				try {
					Integer.parseInt(text);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}

			@Override
			public Object parseText(String text) {
				try {					
					return Integer.parseInt(text);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		};
	}
	
	public static ParsingFieldParser FloatParser() {
		return new ParsingFieldParser() {
			@Override
			public boolean parseIsValid(String text) {
				try {
					Float.parseFloat(text);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}

			@Override
			public Object parseText(String text) {
				try {					
					return Float.parseFloat(text);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		};
	}
	
	public static ParsingFieldParser DRParser() {
		return new ParsingFieldParser() {
			@Override
			public boolean parseIsValid(String text) {
				try {
					DR.ParseDR(text);
					return true;
				} catch (ParseException e) {
					return false;
				}
			}

			@Override
			public Object parseText(String text) {
				try {					
					return DR.ParseDR(text);
				} catch (ParseException e) {
					return null;
				}
			}
		};
	}
	
	public static ParsingFieldParser DamageParser() {
		return new ParsingFieldParser() {
			@Override
			public boolean parseIsValid(String text) {
				try {
					Damage.ParseDamage(text);
					return true;
				} catch (ParseException e) {
					return false;
				}
			}

			@Override
			public Object parseText(String text) {
				try {					
					return Damage.ParseDamage(text);
				} catch (ParseException e) {
					return null;
				}
			}
		};
	}
}
