package gurpsinittool.app;

import java.awt.Color;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import gurpsinittool.app.textfield.ParsingField;
import gurpsinittool.app.textfield.ParsingFieldParser;
import gurpsinittool.app.textfield.ParsingFieldParserFactory;
import gurpsinittool.data.Actor;
import gurpsinittool.data.Attack;
import gurpsinittool.util.MiscUtil;

@SuppressWarnings("serial")
public class AttackTableModel extends AbstractTableModel {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(AttackTableModel.class.getName());

	private String[] columnNames = {"Name", "Skill", "Damage", "U"};
	private Class<?>[] columnClasses = {String.class, Integer.class, String.class, Boolean.class};
	public enum columns {Name, Skill, Damage, Unbalanced};
	private static int numColumns = 4;
	
	private Actor currentActor = null;

	@Override
	public Class<?> getColumnClass(int c) {
		return columnClasses[c];
	}
	
	public int getColumnCount() {
		return numColumns;
	}
	
	@Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

	public int getRowCount() {
		if (currentActor == null)
			return 0;
		return currentActor.getNumAttacks();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (currentActor == null)
			return null;
		Attack attack = currentActor.getAttack(rowIndex);
		switch (columns.values()[columnIndex]) {
		case Name:
			return attack.name;
		case Skill:
			return attack.skill;
		case Damage:
			return attack.damage;
		case Unbalanced:
			return attack.unbalanced;
		default:
			return null;
		}
	}
	
	/**
	 * Set the value at the row/col specified
	 * For cloned actors, this will update all clones (and fire table updated events)
	 * @param value : Must be either int (for hp/damage/health) or String (for name/State/Type) 
	 * @param row : The row where the actor lives. Any clones will be updated also.
	 * @param col : The column of the value to edit: use 'columns' enum to address.
	 */
    @Override
    public void setValueAt(Object value, int row, int col) {
    	if (LOG.isLoggable(Level.FINER)) {LOG.finer("Setting value at " + row + "," + col
    			+ " to " + value + " (an instance of " + value.getClass() + ")");}     

        // Check if there is any actual change
        if (getValueAt(row, col).equals(value)) {
        	if (LOG.isLoggable(Level.FINER)) {LOG.finer("Values are identical. Exiting."); }
    		return;
        }
        
        Attack a = currentActor.getAttack(row);
		switch (columns.values()[col]) {
		case Name:
			a.name = (String) value;
			break;
		case Skill:
			a.skill = (Integer) value;
			break;
		case Damage:
			a.damage = (String) value;
			break;
		case Unbalanced:
			a.unbalanced = (Boolean) value;
		}
		currentActor.setAttack(a, row);
		// Update the entire row, since changing state or type may affect formatting for all cells in the row.
		fireTableRowsUpdated(row, row);
    }
    
	@Override
    public boolean isCellEditable(int row, int col) {
		if (currentActor != null)
			return true;
		return false;
    }
	
	/**
	 * Set the list of actors to the ArrayList<Actor> specified. Redraw table.
	 * @param actorList : the new ArrayList<Actor> to use as the base for the ActorTableModel
	 */
	public void setActor(Actor actor) {
		currentActor = actor;
		fireTableDataChanged();
	}

	/**
	 * Add new attack row
	 */
    public void addAttack() {
    	if (currentActor != null) {
    		currentActor.addAttack(new Attack());
    		//fireTableRowsInserted(getRowCount()-1, getRowCount()-1);
    	}
    }

    /**
	 * Remove all selected rows
	 */
    public void removeAttacks(int[] rows) {
    	if (currentActor != null && rows.length > 0) {
			for (int i = rows.length-1; i >= 0; i--) {  // Go from bottom up to preserve numbering
				if (LOG.isLoggable(Level.FINE)) {LOG.fine("Deleting row: " + rows[i]); }   			
				currentActor.removeAttack(rows[i]); // Just remove the rows indicated: not all instances of clones	
			}
    		//fireTableRowsDeleted(rows[0], rows[rows.length-1]);
    	}
    }
    
    /**
     * Set the current actor's default attack
     * @param row the model row to set as default
     */
    public void setDefaultAttack(int row) {
    	if (row < 0 || row >= currentActor.getNumAttacks()) {
    		if (LOG.isLoggable(Level.INFO)) {LOG.info("Row out of range! " + row);}
    		return;
    	}
    	//int oldDefault = currentActor.getDefaultAttack();
    	currentActor.setDefaultAttack(row);
    	//fireTableRowsUpdated(oldDefault, oldDefault);
    	//fireTableRowsUpdated(row, row);
    }
 
	/**
	 * Inner class to provide CellEditor functionality
	 * Allow modification of the text cell editor
	 * @author dsmall
	 */
	public class AttackTableCellEditor extends DefaultCellEditor {
		
		/**
		 * Super does not define default constructor, so must define one.
		 * @param comboBox
		 */
		public AttackTableCellEditor(ParsingFieldParser parser) {
			super(new ParsingField(parser));
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			ParsingField c = (ParsingField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
			if (isSelected)
			    c.selectAll();
//			if (table.getRowSorter().convertRowIndexToModel(row) == currentActor.getDefaultAttack()) {
//				MiscUtil.setTextFieldFontStyle(c, Font.BOLD);
//			}
			return c;
		}
	}
	
	/**
	 * Renderer to deal with all the customizations based on Actor state/type/etc.
	 * Assumes that the table model being used is an ActorTableModel.
	 * @author dsmall
	 *
	 */
	public class AttackTableCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (table.getRowSorter().convertRowIndexToModel(row) == currentActor.getDefaultAttack()) {
				MiscUtil.setLabelBold(c);
			}
			// Check parsing: at some point may want to create 'ParsingComponent' which has label and textfield versions
			AttackTableModel.columns col = AttackTableModel.columns.valueOf(table.getColumnName(column));		
			if (col == columns.Damage && !ParsingFieldParserFactory.DamageParser().parseIsValid(c.getText()))
				c.setForeground(Color.red);
			else
				c.setForeground(Color.black);
			return c;
		}
	}
}
