package gurpsinittool.app;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;
import gurpsinittool.data.*;

public class TraitTableModel extends AbstractTableModel {

	/**
	 * Default UID
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = false;

	private String[] columnNames = {"Name", "Value"};
	private Class<?>[] columnClasses = {String.class, String.class};
	public enum columns {Name, Value};
	private static int numColumns = 2;
	
	private boolean isTemp; // Monitor the temp vars instead of the regular traits
	private Actor currentActor = null;
	private ArrayList<String> displayedTraitKeys = new ArrayList<String>();

	
	public TraitTableModel(boolean isTemp) {
		super();
		this.isTemp = isTemp;
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
		return displayedTraitKeys.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (currentActor == null)
			return null;
		String traitName = displayedTraitKeys.get(rowIndex);
		switch (columns.values()[columnIndex]) {
		case Name:
			return traitName;
		case Value:
			if (isTemp)
				return currentActor.getTempValue(traitName);
			else
				return currentActor.getTraitValue(traitName);
		default:
			return null;
		}
	}
	
	/**
	 * Set the value at the row/col specified
	 * @param value : String
	 * @param row : Row of trait, as listed in displayedTraits
	 * @param col : The column of the value to edit: use 'columns' enum to address.
	 */
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (DEBUG) {
            System.out.println("TraitTableModel: setValueAt: Setting value at " + row + "," + col
                               + " to " + value
                               + " (an instance of "
                               + value.getClass() + ")");
        }

        if (isTemp) return;
        
        // Check if there is any actual change
        if (getValueAt(row, col).equals(value)) {
    		if (DEBUG) { System.out.println("TraitTableModel: setValueAt: values are identical. Exiting."); }
    		return;
        }
        
        String traitName = displayedTraitKeys.get(row);;//currentActor.Attacks.get(row);
		switch (columns.values()[col]) {
		case Name:
			String newName = (String) value;
			if (!currentActor.renameTrait(traitName, newName)) {
				System.out.println("TraitTableModel: setValueAt: rename failed!");
			} else { // Also need to change the name in our array
				displayedTraitKeys.set(row, newName);
			}
			break;
		case Value:
			currentActor.setTrait(traitName, (String) value);
			break;
		}
		// Update the entire row, since changing state or type may affect formatting for all cells in the row.
		fireTableRowsUpdated(row, row);
    }
    
	@Override
    public boolean isCellEditable(int row, int col) {
		if (!isTemp && currentActor != null)
			return true;
		return false;
    }
	
	/**
	 * Set the list of actors to the ArrayList<Actor> specified. Redraw table.
	 * @param actorList : the new ArrayList<Actor> to use as the base for the ActorTableModel
	 */
	public void setActor(Actor actor) {
		currentActor = actor;
		// Build displayedTraits list
		displayedTraitKeys.clear();
		if (isTemp) {
			displayedTraitKeys.addAll(actor.getAllTempNames());
		} else {
			for (String traitName : actor.getAllTraitNames()) {
				if (Actor.isBasicTrait(traitName)) continue;
				displayedTraitKeys.add(traitName);
			}
		}
		fireTableDataChanged();
	}

	/**
	 * Add new trait row
	 */
    public void addTrait() {
    	if (!isTemp && currentActor != null) {
    		// Figure out a new name
    		int num = 1;
    		while (currentActor.hasTrait("newTrait" + String.valueOf(num))) {num++;}
    		// Add the new trait
    		String name = "newTrait" + String.valueOf(num);
    		if (currentActor.addTrait(name, "")) {
    			displayedTraitKeys.add(name);	
    			fireTableRowsInserted(getRowCount()-1, getRowCount()-1);
    		}
    	}
    }

    /**
	 * Remove all selected rows
	 */
    public void removeTraits(int[] rows) {
    	if (!isTemp && currentActor != null && rows.length > 0) {
			for (int i = rows.length-1; i >= 0; i--) {  // Go from bottom up to preserve numbering
				if (DEBUG) { System.out.println("TraitTableModel: Deleting row: " + rows[i]); }   	
				String traitName = displayedTraitKeys.get(rows[i]);
				if(currentActor.removeTrait(traitName)) {
					displayedTraitKeys.remove(rows[i]);
				} else {
					System.out.println("-W- TraitTableModel: skipping trait removal due to failure in base actor");
				}
			}
    		fireTableRowsDeleted(rows[0], rows[rows.length-1]);
    	}
    }
}
