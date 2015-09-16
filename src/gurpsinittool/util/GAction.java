package gurpsinittool.util;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 * Wrapper class to make creating actions easier
 * @author dcsmall
 *
 */
@SuppressWarnings("serial")
abstract public class GAction extends AbstractAction{

	public GAction(String text, String tooltip, ImageIcon icon) {
		super(text,icon);
		putValue(SHORT_DESCRIPTION, tooltip);
	}
	public GAction(String text, String tooltip, int mnemonic, ImageIcon icon) {
		super(text,icon);
		putValue(SHORT_DESCRIPTION, tooltip);
		putValue(MNEMONIC_KEY, mnemonic);
	}
}
