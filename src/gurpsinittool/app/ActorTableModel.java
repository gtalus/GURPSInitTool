package gurpsinittool.app;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;
import gurpsinittool.data.*;
import gurpsinittool.data.Actor.ActorState;
import gurpsinittool.data.Actor.ActorType;
import gurpsinittool.util.FileChangeEvent;
import gurpsinittool.util.FileChangeEventListener;

public class ActorTableModel extends AbstractTableModel {

	/**
	 * Default UID
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = true;
	
	private boolean clean = true;
	protected EventListenerList fileChangeListenerList = new EventListenerList();

	private String[] columnNames = {"Name", "Move", "Dodge", "HT", "HP", "Damage", "FP", "Fatigue", "State", "Type"};
	private Class<?>[] columnClasses = {String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class, String.class};
	public enum columns {Name, Move, Dodge, HT, HP, Damage, FP, Fatigue, State, Type};
	private static int numColumns = 10;
	
	private ArrayList<Actor> actorList = new ArrayList<Actor>(Arrays.asList(
//			new Actor("Damian", ActorState.Active, ActorType.PC),
//			new Actor("Galen", ActorState.Active, ActorType.PC),	
//			new Actor("Guard", ActorState.Active, ActorType.Ally),
//			new Actor("Guard 2", ActorState.Active, ActorType.Ally),
//			new Actor("Villager", ActorState.Active, ActorType.Neutral),
//			new Actor("Vorpal Blade", ActorState.Active, ActorType.Special),
//			new Actor("Monster", ActorState.Active, ActorType.Enemy),
//			new Actor("Big Monster", ActorState.Active, ActorType.Enemy),
//			new Actor("Demon", ActorState.Active, ActorType.Enemy),
			new Actor("new...", ActorState.Active, ActorType.Enemy))
			);
	private int activeActor = -1;
	
	/**
	 * Add a new actor to the specified slot
	 * @param source : the actor object to add
	 * @param dest : the index of the slot to insert into (pushes current actor down one)
	 */
	public void addActor(Actor source, int dest) {
		if (dest > actorList.size()-1) // Don't put anything after the 'new' row
			dest = actorList.size()-1;
		else if (dest < 0) // Don't try to put stuff before the beginning!
			dest = 0;
			
		if (dest <= activeActor) { activeActor++; } // Track active actor
		actorList.add(dest, source);
		setDirty();
		fireTableRowsInserted(dest,dest);
	}
	
	/**
	 * Create a copy of an actor to another slot
	 * @param source : the index of the actor to copy
	 * @param dest : the index of the slot to copy into (pushes current actor down one)
	 *
	public void copyActor(int source, int dest) {
		if (dest >= actorList.size()-1) // Don't put anything after the 'new' row
			dest = actorList.size()-2;
			
		Actor movingActor = getActor(source);
		actorList.add(dest, movingActor);
	}*/
	
	/**
	 * Fires a row updated for the listed actor.
	 * @param actor
	 */
	public void fireRefresh(Actor actor) {
		int [] rows = getActorRows(actor);
		for (int i=0; i < rows.length; i++) {
			setDirty();
			fireTableRowsUpdated(rows[i], rows[i]);
		}
	}
	
	/**
	 * Get index of current active actor
	 * @return
	 */
	public int getActiveActor() {
		return activeActor;
	}
	
	/**
	 * Get the actor in a particular row.
	 * Be careful about editing the actor: update table rows will not be refreshed!
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
	 * Be careful about editing the Actors: update table rows will not be refreshed!
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
			return actor.Name;
		case Move:
			return actor.Move;
		case Dodge:
			return actor.Dodge;
		case HT:
			return actor.HT;
		case HP:
			return actor.HP;
		case Damage:
			return actor.Damage;
		case FP:
			return actor.FP;
		case Fatigue:
			return actor.Fatigue;
		case State:
			return actor.State;
		case Type:
			return actor.Type;
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
            System.out.println("ActorTableModel: setValueAt: Setting value at " + row + "," + col
                               + " to " + value
                               + " (an instance of "
                               + value.getClass() + ")");
        }
        
        // Check if there is any actual change
        if (getValueAt(row, col).equals(value)) {
    		if (DEBUG) { System.out.println("ActorTableModel: setValueAt: values are identical. Exiting."); }
    		return;
        }

        // Create a new row if necessary (editing the 'new...' row)
        if (row == actorList.size() -1) {
        	actorList.add(new Actor("new...", ActorState.Active, ActorType.Enemy));
    		setDirty();
        	fireTableRowsInserted(row+1,row+1);
        }
        
        Actor a = (Actor) actorList.get(row);
		switch (columns.values()[col]) {
		case Name:
			a.Name = (String) value;
			break;
		case Move:
			a.Move = (Integer) value;
			break;
		case Dodge:
			a.Dodge = (Integer) value;
			break;
		case HT:
			a.HT = (Integer) value;
			break;
		case HP:
			a.HP = (Integer) value;
			break;
		case Damage:
			a.Damage = (Integer) value;
			break;
		case FP:
			a.FP = (Integer) value;
			break;
		case Fatigue:
			a.Fatigue = (Integer) value;
			break;
		case State:
			a.State = ActorState.valueOf((String) value);
			break;
		case Type:
			a.Type = ActorType.valueOf((String) value);
			break;
		}
		// Update the entire row, since changing state or type may affect formatting for all cells in the row.
		setDirty();
		int [] rows = getActorRows(a);
		for (int i=0; i < rows.length; i++) {
			fireTableRowsUpdated(rows[i], rows[i]);
		}
    }
    
	@Override
    public boolean isCellEditable(int row, int col) {
       return true;
    }
	
	/**
	 * Move the actor from one slot to another
	 * @param source : the index of the actor to move
	 * @param dest : the index of the slot to move into (pushes current actor down one)
	 *
	public void moveActor(int source, int dest) {
		if (dest == actorList.size()-1) // Don't move anything after the 'new' row
			dest--;
		
		if (source == dest) // Don't do anything if nothing is necessary  
			return;

		if (source < dest) // when we remove source, it will cause all the later actors to move up one, so we need to adjust the dest#
			dest--;
		
		Actor movingActor = getActor(source);
		actorList.remove(source);
		actorList.add(dest, movingActor);
	}*/
	
	/**
	 * Set next actor as active
	 * @return Whether a new round has started.
	 */
	public boolean nextActor() {
		setDirty();
		if (activeActor != -1) {
			fireTableCellUpdated(activeActor, 0);
		}
		boolean isNewRound = nextActorInternal();
		fireTableCellUpdated(activeActor, 0);
		return isNewRound;
	}
	
	/**
	 * Calculate the next actor without updating the table
	 * @return Whether a new round has started.
	 */
	protected boolean nextActorInternal() {
		boolean isNewRound = (activeActor == -1);
		do {
			activeActor++;
			if (activeActor >= actorList.size() - 1) { // Remember that the last entry is 'new...'
				activeActor = 0;
				isNewRound = true;
			}
		} while (actorList.get(activeActor).State != Actor.ActorState.Active & actorList.get(activeActor).State != Actor.ActorState.Disabled);
		return isNewRound;
	}

	/**
	 * Remove an actor from the table (only the specific row)
	 * @param row : the actor to remove
	 */
	public void removeActor(int row) {
		actorList.remove(row);
		if (row < activeActor) { activeActor--; } // track active Actor
		else if (row == activeActor) { activeActor--; nextActor(); }
		setDirty();
		fireTableRowsDeleted(row, row);
	}
	
	/**
	 * Remove an actor from the table based on the reference
	 * Removes ALL instances of that actor
	 * @param actor : the actor to remove
	 */
	public void removeActor(Actor actor) {
		while (actorList.contains(actor)) {
			int row = actorList.indexOf(actor);
			actorList.remove(row);
			setDirty();
			fireTableRowsDeleted(row, row);
		}
	}

	/**
	 * Reset the encounter. Set the active actor to -1
	 */
	public void resetEncounter() {
		setDirty();
		fireTableCellUpdated(activeActor, 0);
		activeActor = -1;
	}
	
	/**
	 * Set the list of actors to the ArrayList<Actor> specified. Redraw table.
	 * @param actorList : the new ArrayList<Actor> to use as the base for the ActorTableModel
	 */
	public void setActorList(ArrayList<Actor> actorList) {
		fireTableRowsDeleted(0, getRowCount()); // remove current selection
		if (actorList == null) 
			this.actorList = new ArrayList<Actor>(Arrays.asList(new Actor("new...", ActorState.Active, ActorType.Enemy)));
		else 
			this.actorList = actorList;
		fireTableDataChanged();
	}
	
	/**
	 * Set the active actor by row #
	 * @param row : the row which is now active
	 */
	public void setActiveRow(int row) {
		if (row > getRowCount()-2) { // Error if trying to set 'new...' row active or non-existant row
			throw new IndexOutOfBoundsException("Specified row is not an actor");
		}
		if (activeActor != -1) {
			fireTableCellUpdated(activeActor, 0);
		}
		activeActor = row;
		setValueAt(ActorState.Active.toString(), row, columns.State.ordinal());
		setDirty();
		fireTableCellUpdated(activeActor, 0);
	}
	

    
    /**
     * Method to get the clean status of the groupTree
     * @return whether the group tree has had changes since the last checkpoint
     */
    public boolean isClean() {
    	return clean;
    }
    
    /**
     * Set the status of the groupTree as clean. Should be called after saving the file.
     */
    public void setClean() {
    	if(!clean) { fireFileCleanStatusChangedEvent(new FileChangeEvent(this, true)); }
    	clean = true;
    }
    
    /**
     * Set the status of the groupTree as dirty. Should be called after making any changes.
     */
    public void setDirty() {
		if (DEBUG) { System.out.println("GroupTree: setDirty"); }

    	if(clean) { fireFileCleanStatusChangedEvent(new FileChangeEvent(this, false)); }
    	fireFileChangedEvent(new FileChangeEvent(this));
    	clean = false;
    }
    
	/**
	 * Add an event listener for FileChangeEvents
	 * @param listener - the listener to add
	 */
	public void addFileChangeEventListener(FileChangeEventListener listener) {
		fileChangeListenerList.add(FileChangeEventListener.class, listener);
	}
	
	/**
	 * Remove an event listener for FileChangeEvents
	 * @param listener - the listener to remove
	 */
	public void removeFileChangeEventListener(FileChangeEventListener listener) {
		fileChangeListenerList.remove(FileChangeEventListener.class, listener);
	}
	
	/**
	 * Fire an event indicating that the file has changed
	 * @param evt - the event details
	 */
	void fireFileChangedEvent(FileChangeEvent evt) {
		Object[] listeners = fileChangeListenerList.getListenerList(); 
		// Each listener occupies two elements - the first is the listener class 
		// and the second is the listener instance 
		for (int i=0; i<listeners.length; i+=2) { 
			if (listeners[i]==FileChangeEventListener.class) { 
				((FileChangeEventListener)listeners[i+1]).fileChangeOccured(evt); 
			} 
		} 
	}
	
	/**
	 * Fire an event indicating that the file clean status has changed (clean -> dirty or dirty -> clean)
	 * @param evt - the event details
	 */
	void fireFileCleanStatusChangedEvent(FileChangeEvent evt) {
		Object[] listeners = fileChangeListenerList.getListenerList(); 
		// Each listener occupies two elements - the first is the listener class 
		// and the second is the listener instance 
		for (int i=0; i<listeners.length; i+=2) { 
			if (listeners[i]==FileChangeEventListener.class) { 
				((FileChangeEventListener)listeners[i+1]).fileCleanStatusChanged(evt); 
			} 
		} 
	}

}
