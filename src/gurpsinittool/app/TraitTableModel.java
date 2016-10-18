package gurpsinittool.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.table.AbstractTableModel;
import gurpsinittool.data.*;

public class TraitTableModel extends AbstractTableModel implements PropertyChangeListener {

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
        //new Exception().printStackTrace();

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
			}
//			else { // Also need to change the name in our array
//				displayedTraitKeys.set(row, newName);
//			}
			break;
		case Value:
			currentActor.setTrait(traitName, (String) value);
			break;
		}
		// Update the entire row, since changing state or type may affect formatting for all cells in the row.
		//fireTableRowsUpdated(row, row);
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
		if (actor == currentActor)
			return; // Not going to refresh same actor!
		
		// Property change events
		if (currentActor != null)
			currentActor.removePropertyChangeListener(this);
		currentActor = actor;
		if (currentActor != null)
			currentActor.addPropertyChangeListener(this);
		
		rebuildTable();
	}
	
	/**
	 * Fully refresh the table
	 */
	public void rebuildTable() {
		// Build initial displayedTraits list
		displayedTraitKeys.clear();
		displayedTraitKeys.addAll(getTraitNames());		
		fireTableDataChanged();
	}
	
	/**
	 * Get a sorted list of trait names
	 * @return a sorted list of trait names
	 */
	private ArrayList<String> getTraitNames() {
		ArrayList<String> theList = new ArrayList<String>();
		if (currentActor != null) {
			if (isTemp) {
				theList.addAll(currentActor.getAllTempNames());
			} else {
				for (String traitName : currentActor.getAllTraitNames()) {
					if (Actor.isBasicTrait(traitName)) continue;
					theList.add(traitName);
				}
			}
		}
		Collections.sort(theList);
		return theList;
	}

	/**
	 * Add new trait row
	 * @return the name of the added trait
	 */
    public String addTrait() {
    	if (!isTemp && currentActor != null) {
    		// Figure out a new name
    		int num = 1;
    		String prefix = "_new trait";
    		while (currentActor.hasTrait(prefix + String.valueOf(num))) {num++;}
    		// Add the new trait
    		String name = prefix + String.valueOf(num);
    		currentActor.addTrait(name, ""); // Rely on automatic refresh
    		return name;
    	} else {
    		return null;
    	}
    }
    
    /**
     * Get the row number of the specified trait
     * @param name : the name of the trait to be found
     * @return the row that the trait is on, or -1 if it's not found
     */
    public int getTraitRow(String name) {
    	for (int i = 0; i < displayedTraitKeys.size(); i++) {
    		if (name.equals(displayedTraitKeys.get(i))) {
    			return i;
    		}
    	}
    	return -1;
    }

    /**
	 * Remove all selected rows
	 */
    public void removeTraits(int[] rows) {
    	if (!isTemp && currentActor != null && rows.length > 0) {
			for (int i = rows.length-1; i >= 0; i--) {  // Go from bottom up to preserve numbering
				if (DEBUG) { System.out.println("TraitTableModel: Deleting row: " + rows[i]); }   	
				String traitName = displayedTraitKeys.get(rows[i]);
				currentActor.removeTrait(traitName);// rely on automatic refresh
				//if(currentActor.removeTrait(traitName)) {
				//	displayedTraitKeys.remove(rows[i]);
				//} else {
				//	System.out.println("-W- TraitTableModel: skipping trait removal due to failure in base actor");
				//}
			}
    		//fireTableRowsDeleted(rows[0], rows[rows.length-1]);
    	}
    }

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (isTemp)
			return; // for now!
		// Listen to events and fire appropriate events
		if (e.getSource() != currentActor) {
			System.err.println ("TraitTableModel:propertyChange: source of event is not current actor! Shouldn't happen!");
		} else {		
			// See if it's a trait
			if(e.getPropertyName().startsWith("trait.")) { // New trait or value changed
				String name = e.getPropertyName().replaceFirst("^trait.", "");
				if (e.getOldValue() == null) { // new trait
					// Error checking
					if (!Actor.isCustomTrait(name)) 
						System.err.println("TraitTableModel: propertyChange: newly created trait is not custom! " + name);
					if (displayedTraitKeys.contains(name))
						System.err.println("TraitTableModel: propertyChange: newly created trait already in list of displayed traits! " + name);
					displayedTraitKeys.add(name);					
					fireTableRowsInserted(displayedTraitKeys.size()-1, displayedTraitKeys.size()-1);
				} else if (e.getNewValue() == null) { // deleted trait
					if (!displayedTraitKeys.contains(name))
						System.err.println("TraitTableModel: propertyChange: deleted trait not in display traits list! " + name);
					int index = displayedTraitKeys.indexOf(name);
					displayedTraitKeys.remove(index);
					fireTableRowsDeleted(index, index);
				} else if (Actor.isCustomTrait(name)) { // Value changed for custom trait
					if (!displayedTraitKeys.contains(name))
						System.err.println("TraitTableModel: propertyChange: changed trait not in display traits list! " + name);
					fireTableCellUpdated(displayedTraitKeys.indexOf(name), columns.Value.ordinal());				
				}
			} else if ("traitName".equals(e.getPropertyName())) { // Trait name change
				if (!displayedTraitKeys.contains(e.getOldValue()))
					System.err.println("TraitTableModel: propertyChange: renamed trait original name not in display traits list! " + e.getOldValue());
				int index = displayedTraitKeys.indexOf(e.getOldValue());
				displayedTraitKeys.set(index, (String)e.getNewValue());
				fireTableCellUpdated(index, columns.Name.ordinal());			
			}			
		}
	}
}
