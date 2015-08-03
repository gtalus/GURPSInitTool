package gurpsinittool.app;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;
import gurpsinittool.data.*;
import gurpsinittool.data.Actor.Trait;

public class TraitTableModel extends AbstractTableModel {

	/**
	 * Default UID
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = true;

	private String[] columnNames = {"Name", "Value"};
	private Class<?>[] columnClasses = {String.class, String.class};
	public enum columns {Name, Value};
	private static int numColumns = 2;
	
	private InitTableModel actorModel = null;
	private Actor currentActor = null;
	private ArrayList<Trait> displayedTraits = new ArrayList<Trait>();

	
	public TraitTableModel(InitTableModel actorModel) {
		super();
		this.actorModel = actorModel;
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
		return displayedTraits.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (currentActor == null)
			return null;
		Trait trait = displayedTraits.get(rowIndex);
		switch (columns.values()[columnIndex]) {
		case Name:
			return trait.name;
		case Value:
			return trait.value;
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
        
        Trait trait = displayedTraits.get(row);;//currentActor.Attacks.get(row);
		switch (columns.values()[col]) {
		case Name:
			String newName = (String) value;
			if (!currentActor.renameTrait(trait.name, newName)) {
				System.out.println("AttackTableModel: setValueAt: rename failed!");
			}
			break;
		case Value:
			trait.value = (String) value;
			break;
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
		// Build displayedTraits list
		displayedTraits.clear();
		for (Object value : actor.getAllTraits()) {
			Trait trait = (Trait) value;
			if (Actor.isBasicTrait(trait.name)) continue;
			displayedTraits.add(trait);
		}
		fireTableDataChanged();
	}

	/**
	 * Add new trait row
	 */
    public void addTrait() {
    	if (currentActor != null) {
    		// Figure out a new name
    		int num = 1;
    		while (currentActor.hasTrait("newTrait" + String.valueOf(num))) {num++;}
    		// Add the new trait
    		System.out.println("HERE: preparing to add new trait: " + num);
    		Trait newTrait = currentActor.setTrait("newTrait" + String.valueOf(num), "");
    		displayedTraits.add(newTrait);
    		System.out.println("HERE: added new trait: " + newTrait.name);
    		fireTableRowsInserted(getRowCount()-1, getRowCount()-1);
    	}
    }

    /**
	 * Remove all selected rows
	 */
    public void removeTraits(int[] rows) {
    	if (currentActor != null && rows.length > 0) {
			for (int i = rows.length-1; i >= 0; i--) {  // Go from bottom up to preserve numbering
				if (DEBUG) { System.out.println("TraitTableModel: Deleting row: " + rows[i]); }   	
				Trait trait = displayedTraits.get(rows[i]);
				if(currentActor.removeTrait(trait.name)) {
					displayedTraits.remove(rows[i]);
				} else {
					System.out.println("-W- TraitTableModel: skipping trait removal due to failure in base actor");
				}
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
