package gurpsinittool.app.textfield;

/**
 * Abstract class defining field parsing interface
 * @author dcsmall
 *
 */
public abstract class ParsingFieldParser {
	public abstract boolean parseIsValid(String text);
	public abstract Object parseText(String text);
}
