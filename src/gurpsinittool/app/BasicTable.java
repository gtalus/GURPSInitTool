package gurpsinittool.app;

import java.awt.event.KeyEvent;

import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class BasicTable  extends JTable  {

	public BasicTable() {
		super();
	}
	public BasicTable(AbstractTableModel model) {
		super(model);
	}
	
	/**
	 * Override this method to fix a bug in the JVM which causes the table to
	 * start editing when a mnemonic key or function key is pressed.
	 */
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
		if (getInputMap(condition).get(ks) != null) {  } // Allow any key that is part of the input map
		else if (e.isControlDown() || e.isAltDown() || e.isMetaDown()) { // ignore potential accelerators and mnemonics
			return false;
		}
		return super.processKeyBinding(ks, e, condition, pressed);
	}
}
