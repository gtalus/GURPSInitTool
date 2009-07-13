package gurpsinittool.app;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import java.awt.Component;

public class InitTableComboCellEditor extends DefaultCellEditor {

	/**
	 * Default serial UID
	 */
	private static final long serialVersionUID = 1L;
	
	private static final boolean DEBUG = false;

	public InitTableComboCellEditor(JComboBox comboBox) {
		super(comboBox);
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
		
		((JComboBox) c).setSelectedItem(value.toString());
		if (DEBUG) {
			System.out.println("getting editor component at " + row + "," + column
                 + " to " + value
                 + " (an instance of "
                 + value.getClass() + ")");
		}
		
		return c;
	}
	
}
