package gurpsinittool.app.textfield;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ParsingField extends JTextField {
	private ParsingFieldParser parser;
	private Color foregroundColor = Color.black;
	
	public ParsingFieldParser getParser() {
		return parser;
	}
	public void setParser(ParsingFieldParser parser) {
		this.parser = parser;
	}
	public Color getForegroundColor() {
		return foregroundColor;
	}
	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	} 
	public void refreshForeground() {
		if (parser.parseIsValid(getText()))	    			
    		setForeground(foregroundColor);
    	else 
    		setForeground(Color.red);
	}
	
	public ParsingField() {
		super();
		this.getDocument().addDocumentListener(new ActorDocumentListener());
	}
	
	public ParsingField(ParsingFieldParser parser) {
		super();
		this.parser = parser;
		this.getDocument().addDocumentListener(new ActorDocumentListener());
	}
	
	public Object getParsedValue() {
		return parser.parseText(getText());
	}
	
	/**
	 * Internal class to listen to the changes in text components
	 */
	protected class ActorDocumentListener implements DocumentListener {
	    public void insertUpdate(DocumentEvent e) { parse_component_text(); }
	    public void removeUpdate(DocumentEvent e) {	parse_component_text(); }
	    public void changedUpdate(DocumentEvent e) { parse_component_text(); }
	    
	    private void parse_component_text() {
	    	if (parser.parseIsValid(getText()))	    			
	    		setForeground(foregroundColor);
	    	else 
	    		setForeground(Color.red);
	    }
	}
}