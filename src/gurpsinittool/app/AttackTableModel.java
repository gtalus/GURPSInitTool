package gurpsinittool.app;

import javax.swing.table.AbstractTableModel;

import gurpsinittool.data.*;

public class AttackTableModel extends AbstractTableModel {

	/**
	 * Default UID
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = true;

	private String[] columnNames = {"Name", "Skill", "Damage", "U"};
	private Class<?>[] columnClasses = {String.class, Integer.class, String.class, Boolean.class};
	public enum columns {Name, Skill, Damage, Unbalanced};
	private static int numColumns = 4;
	
	private InitTableModel actorModel = null;
	private Actor currentActor = null;
	private int activeAttack = -1;

	
	public AttackTableModel(InitTableModel actorModel) {
		super();
		this.actorModel = actorModel;
	}
	/**
	 * Get index of current active actor
	 * @return
	 */
	public int getActiveAttack() {
		return activeAttack;
	}

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
		return currentActor.Attacks.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (currentActor == null)
			return null;
		Attack attack = currentActor.Attacks.get(rowIndex);
		switch (columns.values()[columnIndex]) {
		case Name:
			return attack.Name;
		case Skill:
			return attack.Skill;
		case Damage:
			return attack.Damage;
		case Unbalanced:
			return attack.Unbalanced;
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
        if (DEBUG) {
            System.out.println("AttackTableModel: setValueAt: Setting value at " + row + "," + col
                               + " to " + value
                               + " (an instance of "
                               + value.getClass() + ")");
        }

        // Check if there is any actual change
        if (getValueAt(row, col).equals(value)) {
    		if (DEBUG) { System.out.println("AttackTableModel: setValueAt: values are identical. Exiting."); }
    		return;
        }
        
        Attack a = currentActor.Attacks.get(row);
		switch (columns.values()[col]) {
		case Name:
			a.Name = (String) value;
			break;
		case Skill:
			a.Skill = (Integer) value;
			break;
		case Damage:
			a.Damage = (String) value;
			break;
		case Unbalanced:
			a.Unbalanced = (Boolean) value;
		}
		// Update the entire row, since changing state or type may affect formatting for all cells in the row.
		setDirty();
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
		fireTableRowsDeleted(0, getRowCount()); // remove current selection
		fireTableDataChanged();
	}

	/**
	 * Add new attack row
	 */
    public void addAttack() {
    	if (currentActor != null) {
    		currentActor.Attacks.add(new Attack());
    		fireTableRowsInserted(getRowCount(), getRowCount());
    	}
    }

    /**
	 * Remove all selected rows
	 */
    public void removeAttacks(int[] rows) {
    	if (currentActor != null && rows.length > 0) {
			for (int i = rows.length-1; i >= 0; i--) {  // Go from bottom up to preserve numbering
				if (DEBUG) { System.out.println("AttackTableModel: Deleting row: " + rows[i]); }   			
				currentActor.Attacks.remove(rows[i]); // Just remove the rows indicated: not all instances of clones
			}
    		fireTableRowsDeleted(rows[0], rows[rows.length-1]);
    	}
    }
 
    /**
     * Set the status of the initTable as dirty. Should be called after making any changes.
     */
    public void setDirty() {
    	actorModel.setDirty();
    }
}
