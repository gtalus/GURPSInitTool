package gurpsinittool.app.textfield;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Customized ParsingField class used to add validity 
 * check functionality to JTextField
 * @author dcsmall
 *
 */
@SuppressWarnings("serial")
public class ParsingField extends JTextField {
	/**
	 * The parser used to check validity of the field contents
	 */
	private AbstractParsingFieldParser parser;
	/**
	 * The color of the text when valid
	 */
	private Color foregroundColor = Color.black;
	/**
	 * Get the ParsingFieldParser used by this ParsingField
	 * @return the ParsingFieldParser used by this ParsingField
	 */
	public AbstractParsingFieldParser getParser() {
		return parser;
	}
	/**
	 * Set the ParsingFieldParser used by this ParsingField
	 * @param parser - the ParsingFieldParser to be used by this ParsingField
	 */
	public void setParser(final AbstractParsingFieldParser parser) {
		this.parser = parser;
	}
	/**
	 * Get the color used by this component when text is valid
	 * @return the color used by this component when text is valid
	 */
	public Color getForegroundColor() {
		return foregroundColor;
	}
	/**
	 * Set the color used by this component when text is valid
	 * @param foregroundColor - the color to use when text is valid
	 */
	public void setForegroundColor(final Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	} 
	/**
	 * Check is the text is valid and set the text color
	 */
	public void refreshForeground() {
		if (parser.parseIsValid(getText()))	    			
    		setForeground(foregroundColor);
    	else 
    		setForeground(Color.red);
	}
	/**
	 * Basic constructor
	 */
	public ParsingField() {
		super();
		this.getDocument().addDocumentListener(new ActorDocumentListener());
	}
	/**
	 * Constructor with parser
	 * @param parser - the ParsingFieldParser to be used by this ParsingField
	 */
	public ParsingField(final AbstractParsingFieldParser parser) {
		super();
		this.parser = parser;
		this.getDocument().addDocumentListener(new ActorDocumentListener());
	}
	/**
	 * Get the parsed value of the text after evaluation by the parser
	 * @return - the result after parsing
	 */
	public Object getParsedValue() {
		return parser.parseText(getText());
	}
	
	/**
	 * Internal class to listen to the changes in text components
	 */
	protected class ActorDocumentListener implements DocumentListener {
	    public void insertUpdate(final DocumentEvent evt) { parseComponentText(); }
	    public void removeUpdate(final DocumentEvent evt) {	parseComponentText(); }
	    public void changedUpdate(final DocumentEvent evt) { parseComponentText(); }
	    
	    private void parseComponentText() {
	    	if (parser.parseIsValid(getText()))	    			
	    		setForeground(foregroundColor);
	    	else 
	    		setForeground(Color.red);
	    }
	}
}
