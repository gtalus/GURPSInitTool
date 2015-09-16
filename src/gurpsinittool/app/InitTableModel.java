package gurpsinittool.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

import javax.swing.table.AbstractTableModel;

import gurpsinittool.data.*;
import gurpsinittool.data.ActorBase.ActorStatus;
import gurpsinittool.data.ActorBase.BasicTrait;
import gurpsinittool.util.CleanFileChangeEventSource;
import gurpsinittool.util.FileChangeEventListener;
import gurpsinittool.util.SearchSupport;

public class InitTableModel extends AbstractTableModel implements PropertyChangeListener {

	/**
	 * Default UID
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = true;

	// Removed: Dodge, type
	// TODO: consolidate HP/Damage, FP/Fatigue
	private String[] columnNames = {"Act", "Name", "Speed", "Move", "HT", "HP", "Injury", "FP", "Fatigue", "Status"};
	private Class<?>[] columnClasses = {String.class, String.class, Float.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class};
	public enum columns {Act, Name, Speed, Move, HT, HP, Injury, FP, Fatigue, Status};
	private static int numColumns = 10;
	
	Actor newActor = new Actor();
	private ArrayList<Actor> actorList = new ArrayList<Actor>();	
	
	public InitTableModel() {
		// This is a special row which allows new actors to be added.
		addNewActor();
	}
	
	/**
	 * Add a new actor to the specified slot
	 * @param source : the actor object to add
	 * @param destRow : the index of the slot to insert into (pushes current actor down one)
	 */
	public void addActor(Actor actor, int destRow) {
		System.out.println("table model.addActor: start: " + actor.getTraitValue(BasicTrait.Name));
		//Actor addedActor = new Actor(source);
		actor.addPropertyChangeListener(this);
		if (destRow > actorList.size()-1) // Don't put anything after the 'new' row
			destRow = actorList.size()-1;
		else if (destRow < 0) // Don't try to put stuff before the beginning!
			destRow = 0;
			
		actorList.add(destRow, actor);
		setDirty();
		fireTableRowsInserted(destRow,destRow);
		System.out.println("table model.addActor: done");
	}
	
	/**
	 * Add the newActor to the list (used when editing the previous newActor)
	 */
	private void addNewActor() {
		Actor actor = new Actor(newActor);
		actor.addPropertyChangeListener(this);
		actorList.add(actorList.size(),actor);
		setDirty();
    	fireTableRowsInserted(actorList.size()-1,actorList.size()-1);
	}

	/**
	 * Get the actor in a particular row.
	 * @param row - table row
	 * @return Actor used in specified row
	 */
	public Actor getActor(int row) {
		return (Actor) actorList.get(row);
	}
	
	/**
	 * Get all row indexes that the actor is located at
	 * @param actor - actor to search for
	 * @return Array of row numbers at which the specified Actor appears
	 */
	public int[] getActorRows(Actor actor) {
		ArrayList<Integer> rows = new ArrayList<Integer>();
		for (int i = 0; i < actorList.size(); i++) {
			if (actorList.get(i).equals(actor)) {
				rows.add(i);
			}
		}
		int[] intRows = new int[rows.size()];
		for (int i=0; i < intRows.length; i++) {
			intRows[i] = rows.get(i).intValue();
		}
		return intRows;
	}
		

	/**
	 * Get an array of Actors based on an array of indexes
	 * @param rows - array of row numbers
	 * @return Array of Actors corresponding to the row numbers
	 */
	public Actor[] getActors(int[] rows) {
		Actor[] actors = new Actor[rows.length];
		for (int i = 0; i < rows.length; i++) {
			actors[i] = actorList.get(rows[i]);
		}
		return actors;
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
		return actorList.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Actor actor = (Actor) actorList.get(rowIndex);
		switch (columns.values()[columnIndex]) {
		case Name:
			return actor.getTraitValue(BasicTrait.Name);
		case Speed:
			return Float.parseFloat(actor.getTraitValue(BasicTrait.Speed));
		case Move:
			return actor.getTraitValueInt(BasicTrait.Move);
		//case Dodge:
		//	return actor.getTraitValueInt(BasicTrait.Dodge);
		case HT:
			return actor.getTraitValueInt(BasicTrait.HT);
		case HP:
			return actor.getTraitValueInt(BasicTrait.HP);
		case Injury:
			return actor.getTraitValueInt(BasicTrait.Injury);
		case FP:
			return actor.getTraitValueInt(BasicTrait.FP);
		case Fatigue:
			return actor.getTraitValueInt(BasicTrait.Fatigue);
		case Status:
			return actor.getAllStatuses();
		//case Type:
		//	return actor.getType();
		default:
			return null;
		}
	}
	
	/**
	 * Set the value at the row/col specified
	 * For cloned actors, this will update all clones (and fire table updated events)
	 * @param value : Must be either int (for hp/damage/health) or String (for name/Type) or HashSet<ActorState> for Status 
	 * @param row : The row where the actor lives. Any clones will be updated also.
	 * @param col : The column of the value to edit: use 'columns' enum to address.
	 */
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (DEBUG) {
            System.out.println("ActorTableModel: setValueAt: Setting value at " + row + "," + col
                               + " to " + value
                               + " (an instance of "
                               + value.getClass() + ")");
        }
        
        // Convert to actual enums to allow comparison with previous value
        switch (columns.values()[col]) {
        //case Type:
        // 	value = ActorType.valueOf(value.toString());
        default:
        }
        
        Actor a = (Actor) actorList.get(row);
		switch (columns.values()[col]) {
		case Name:
			a.setTrait(BasicTrait.Name, (String) value);
			break;
		case Speed:
			a.setTrait(BasicTrait.Speed, String.valueOf((Float) value));
			break;
		case Move:
			a.setTrait(BasicTrait.Move, String.valueOf((Integer) value));
			break;
		//case Dodge:
		//	a.setTrait(BasicTrait.Dodge, String.valueOf((Integer) value));
		//	break;
		case HT:
			a.setTrait(BasicTrait.HT, String.valueOf((Integer) value));
			break;
		case HP:
			a.setTrait(BasicTrait.HP, String.valueOf((Integer) value));
			break;
		case Injury:
			a.setTrait(BasicTrait.Injury, String.valueOf((Integer) value));
			break;
		case FP:
			a.setTrait(BasicTrait.FP, String.valueOf((Integer) value));
			break;
		case Fatigue:
			a.setTrait(BasicTrait.Fatigue, String.valueOf((Integer) value));
			break;
		case Status:
			a.setAllStatuses((HashSet<ActorStatus>) value);
			break;
		//case Type:
		//	a.setType((ActorType) value);
		//	break;
		default:
		}
    }
    
	@Override
    public boolean isCellEditable(int row, int col) {
        switch (columns.values()[col]) {
        case Act:
        	return false;
        default:
        	return true;
        }
    }

	/**
	 * Remove an actor from the table (only the specific row)
	 * @param row : the actor to remove
	 */
	public void removeActor(int row) {
		Actor actor = getActor(row);
		actor.removePropertyChangeListener(this);
		actorList.remove(row);
		setDirty();
		fireTableRowsDeleted(row, row);
	}
	
	/**
	 * Set the list of actors to the ArrayList<Actor> specified. Redraw table.
	 * @param actorList : the new ArrayList<Actor> to use as the base for the ActorTableModel
	 */
	public void setActorList(ArrayList<Actor> actorList) {
		for(Actor a: this.actorList) {
			a.removePropertyChangeListener(this);
		}
		fireTableRowsDeleted(0, getRowCount()); // remove current selection
		
		if (actorList == null) {
			this.actorList = new ArrayList<Actor>();
			//addNewActor(); This is an empty table, so it has NO ACTORS! EMPTY!
		} else { 
			this.actorList = actorList;
			for(Actor a: this.actorList)
				a.addPropertyChangeListener(this);
		}
		fireTableDataChanged();
	}
	

    /**
     * Helper function to wrap search functionality acting on private actorList data member. Arguments are passed directly to 
     * MiscUtil.searchActorList and return value is passed back.
     * @return index of found record, or -1 if nothing found
     */
    public int searchActors(int startingIndex, int endingIndex, boolean next, boolean reverse, Pattern pattern){
    	return SearchSupport.searchActorList(actorList, startingIndex, endingIndex, next, reverse, pattern);
    }
  
	protected CleanFileChangeEventSource cleanFileChangeEventSource = new CleanFileChangeEventSource(this);
   
    /**
     * Method to get the clean status of the groupTree
     * @return whether the group tree has had changes since the last checkpoint
     */
    public boolean isClean() {
    	return cleanFileChangeEventSource.isClean();
    }
    
    /**
     * Set the status of the groupTree as clean. Should be called after saving the file.
     */
    public void setClean() {
    	cleanFileChangeEventSource.setClean();
    }
    
    /**
     * Set the status of the groupTree as dirty. Should be called after making any changes.
     */
    public void setDirty() {
    	cleanFileChangeEventSource.setDirty();
    }
    
	/**
	 * Add an event listener for FileChangeEvents
	 * @param listener - the listener to add
	 */
	public void addFileChangeEventListener(FileChangeEventListener listener) {
		cleanFileChangeEventSource.addFileChangeEventListener(listener);
	}
	
	/**
	 * Remove an event listener for FileChangeEvents
	 * @param listener - the listener to remove
	 */
	public void removeFileChangeEventListener(FileChangeEventListener listener) {
		cleanFileChangeEventSource.removeFileChangeEventListener(listener);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if(Actor.class.isInstance(e.getSource())) {
			setDirty();
			Actor actor = (Actor)e.getSource();
			System.out.println("InitTableModel: propertyChange: got notification from actor " + actor.getTraitValue(BasicTrait.Name));
			int [] rows = getActorRows(actor);
			for (int i=0; i < rows.length; i++) {
				if (rows[i] == getRowCount() -1) { // changed last row
					addNewActor();
				}
				fireTableRowsUpdated(rows[i], rows[i]);
			}
			System.out.println("InitTableModel: propertyChange: done with actor " + actor.getTraitValue(BasicTrait.Name));
		} else if (e.getPropertyName().equals("ActiveActor")) {
				int oldValue = (Integer) e.getOldValue();
				int newValue = (Integer) e.getNewValue();
				if (oldValue != -1)
					fireTableRowsUpdated(oldValue, oldValue);
				if (newValue != -1)
					fireTableRowsUpdated(newValue, newValue);
		}
	}
}
