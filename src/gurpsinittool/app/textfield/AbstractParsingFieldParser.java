package gurpsinittool.app.textfield;

/**
 * Abstract class defining field parsing interface
 * @author dcsmall
 *
 */
public abstract class AbstractParsingFieldParser {
	/**
	 * Determine whether text is parsable by this parser
	 * @param text - string to parse
	 * @return true if the parser is successful
	 */
	public abstract boolean parseIsValid(String text);
	/**
	 * Parse the text and return the result
	 * @param text - the string to parse
	 * @return the resulting object
	 */
	public abstract Object parseText(String text);
}
