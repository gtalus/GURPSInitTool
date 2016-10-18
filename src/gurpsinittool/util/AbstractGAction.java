package gurpsinittool.util;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 * Wrapper class to make creating actions easier
 * @author dcsmall
 *
 */
@SuppressWarnings("serial")
abstract public class AbstractGAction extends AbstractAction{

	/**
	 * Create an AbstractGAction
	 * @param text - action text
	 * @param tooltip - action tooltip
	 * @param icon - action icon
	 */
	public AbstractGAction(final String text, final String tooltip, final ImageIcon icon) {
		super(text,icon);
		putValue(SHORT_DESCRIPTION, tooltip);
	}
	/**
	 * Create an AbstractGAction
	 * @param text - action text
	 * @param tooltip - action tooltip
	 * @param mnemonic - action mnemonic
	 * @param icon - action icon
	 */
	public AbstractGAction(final String text, final String tooltip, final int mnemonic, final ImageIcon icon) {
		super(text,icon);
		putValue(SHORT_DESCRIPTION, tooltip);
		putValue(MNEMONIC_KEY, mnemonic);
	}
}
