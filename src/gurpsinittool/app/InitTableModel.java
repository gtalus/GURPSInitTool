package gurpsinittool.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale.Category;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.AbstractTableModel;

import gurpsinittool.data.*;
import gurpsinittool.data.Actor.ActorState;
import gurpsinittool.data.Actor.ActorType;
import gurpsinittool.util.CleanFileChangeEventSource;
import gurpsinittool.util.EncounterLogEvent;
import gurpsinittool.util.EncounterLogEventListener;
import gurpsinittool.util.EncounterLogEventSource;
import gurpsinittool.util.FileChangeEventListener;

public class InitTableModel extends AbstractTableModel {

	/**
	 * Default UID
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = true;

	private String[] columnNames = {"Act", "Name", "Move", "Dodge", "HT", "HP", "Damage", "FP", "Fatigue", "State", "Type"};
	private Class<?>[] columnClasses = {String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class, String.class};
	public enum columns {Act, Name, Move, Dodge, HT, HP, Damage, FP, Fatigue, State, Type};
	private static int numColumns = 11;
	
	Actor newActor = new Actor("", ActorState.Active, ActorType.Enemy);
	private ArrayList<Actor> actorList = new ArrayList<Actor>(Arrays.asList(
			// This is a special row which allows new actors to be added.
			new Actor(newActor)));	
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
	public int getActiveActorIndex() {
		return activeActor;
	}
	
	/**
	 * Retrieve the currently active Actor
	 * @return The currently active Actor
	 */
	public Actor getActiveActor() {
		if (activeActor < 0)
			return null;
		return getActor(activeActor);
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
			return actor.Injury;
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
        
        // Convert to actual enums to allow comparison with previous value
        switch (columns.values()[col]) {
        case State:
        	value = ActorState.valueOf(value.toString());
        	break;
        case Type:
        	value = ActorType.valueOf(value.toString());
        default:
        }
        
        // Check if there is any actual change
        if (getValueAt(row, col).equals(value)) {
    		if (DEBUG) { System.out.println("ActorTableModel: setValueAt: values are identical. Exiting."); }
    		return;
        }

        // Create a new row if necessary (editing the 'new...' row)
        if (row == actorList.size() -1) {
        	actorList.add(new Actor(newActor));
    		setDirty();
        	fireTableRowsInserted(row+1,row+1);
        }
        
        Actor a = (Actor) actorList.get(row);
        int newValue;
        int diff;
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
			newValue = (Integer) value;
			diff = a.Injury - newValue;
			if (diff < 0) {
				encounterLogEventSource.fireEncounterLogEvent(new EncounterLogEvent(this, "<b>" + a.Name + "</b> took <b><font color=red>" + (-1*diff) + "</font></b> damage (now " + (a.HP - newValue) + " HP)."));
			} else {
				encounterLogEventSource.fireEncounterLogEvent(new EncounterLogEvent(this, "<b>" + a.Name + "</b> healed <b><font color=blue>" + diff + "</font></b> (now " + (a.HP - newValue) + " HP)."));		
			}
			a.Injury = newValue;
			break;
		case FP:
			a.FP = (Integer) value;
			break;
		case Fatigue:
			newValue = (Integer) value;
			diff = a.Fatigue - newValue;
			if (diff < 0) {
				encounterLogEventSource.fireEncounterLogEvent(new EncounterLogEvent(this, "<b>" + a.Name + "</b> lost <b>" + (-1*diff) + "</b> fatigue (now " + (a.FP - newValue) + " FP)."));
			} else {
				encounterLogEventSource.fireEncounterLogEvent(new EncounterLogEvent(this, "<b>" + a.Name + "</b> recoverd <b>" + diff + "</b> fatigue (now " + (a.FP - newValue) + " FP)."));		
			}
			a.Fatigue = newValue;
			break;
		case State:
			ActorState newState = (ActorState) value;
			encounterLogEventSource.fireEncounterLogEvent(new EncounterLogEvent(this, "<b>" + a.Name + "</b> status changed to <b>" + newState + "</b>"));
			a.State = newState;
			break;
		case Type:
			ActorType newType = (ActorType) value;
			encounterLogEventSource.fireEncounterLogEvent(new EncounterLogEvent(this, "<b>" + a.Name + "</b> type changed to <b>" + newType + "</b>"));
			a.Type = newType;
		default:
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
        switch (columns.values()[col]) {
        case Act:
        	return false;
        default:
        	return true;
        }
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
		if (activeActor != -1) {
			fireTableCellUpdated(activeActor, 0);
		}
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
				if (isNewRound) { // Went around again - no valid actors found
					activeActor = -1;
					return true;
				}
				isNewRound = true;
			}
			getActiveActor().NextTurn();
		} while (actorList.get(activeActor).State != Actor.ActorState.Active 
				& actorList.get(activeActor).State != Actor.ActorState.Disabled
				& actorList.get(activeActor).State != Actor.ActorState.Stunned);
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
			removeActor(row);
		}
	}

	/**
	 * Reset the encounter. Set the active actor to -1
	 */
	public void resetEncounter() {
		setDirty();
		if (activeActor != -1) {
			fireTableCellUpdated(activeActor, 0);	
			activeActor = -1;
		}
	}
	
	/**
	 * Set the list of actors to the ArrayList<Actor> specified. Redraw table.
	 * @param actorList : the new ArrayList<Actor> to use as the base for the ActorTableModel
	 */
	public void setActorList(ArrayList<Actor> actorList) {
		fireTableRowsDeleted(0, getRowCount()); // remove current selection
		if (actorList == null) 
			this.actorList = new ArrayList<Actor>(Arrays.asList(new Actor(newActor)));
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
	
    // Tag active, non-tagged, enemy actors with available tags
    public void autoTagActors() {
    	// Generate list of tags, and clean unneeded tags
    	HashSet<String> tags = catalogTags(true);
    	
    	// Now go thorough and add tags to non-unconscious/dead enemy actors
    	for (int i = 0; i < actorList.size()-1; ++i) {
    		Actor a = actorList.get(i);
    		if (a.Type == ActorType.Enemy && (a.State != ActorState.Unconscious && a.State != ActorState.Dead && a.State != ActorState.Waiting)) {
	    		tagActor(a, tags);
    		}
    	}
    }
    
    /**
     * Remove the tag from an actor
     * @param actor The actor to remove the tag from
     */
    public void removeTag(Actor actor) {
    	Matcher matcher;
		Pattern nameTag = Pattern.compile("^(.*) \\[([^\\s]+)\\]$");
		if ((matcher = nameTag.matcher(actor.Name)).matches()) {
    		String name = matcher.group(1);
    		actor.Name = name;
    		fireRefresh(actor);
		}
    }
    
    /**
     * Tag an actor with the next available tag. This version automatically generates the list of tags in current use.
     * @param actor The actor to tag
     */
    public void tagActor(Actor actor) {
    	tagActor(actor, catalogTags(false));
    }
    
    /**
     * Tag an actor with the next available tag if not already tagged
     * @param actor The actor to tag
     * @param tags A list of all tags currently in use
     */
    public void tagActor(Actor actor, HashSet<String> tags) {
		if (!nameTag.matcher(actor.Name).matches()) {
			String tag = getNextTag(tags);
			if (DEBUG) System.out.println("InitTableModel:tagActor: Tagging actor " + actor.Name + " with " + "[" + tag + "]");
			actor.Name += " [" + tag + "]";
			tags.add(tag);
			fireRefresh(actor);
		}
    }
    
    /**
     * Helper method to catalog all tags currently in use
     * @param clean Whether to clear out stale tags (from enemies that are dead/unconscious)
     * @return a HashSet of all tags currently in use
     */
    public HashSet<String> catalogTags(boolean clean) {
		Matcher matcher;
		Pattern nameTag = Pattern.compile("^(.*) \\[([^\\s]+)\\]$");
		HashSet<String> tags = new HashSet<String>();
		
    	// First go through actors, removing unneeded tags (unconscious/dead) and logging existing ones
    	for (int i = 0; i < actorList.size()-1; ++i) {
    		Actor a = actorList.get(i);
    		if (DEBUG) System.out.println("catalogTags: Cataloging actor: " + a.Name);
    		if ((matcher = nameTag.matcher(a.Name)).matches()) {
    			String name = matcher.group(1);
    			String tag = matcher.group(2);
    			// Check if tag should be cleared
    			if (clean && a.Type == ActorType.Enemy && (a.State == ActorState.Unconscious || a.State == ActorState.Dead)) {
    	    		System.out.println("catalogTags: cleaning tag: " + tag);
    				a.Name = name;
    				fireRefresh(a);
    			} else {
	    			if (tags.contains(tag)) 
	    				System.out.println("-W- InitTableModel::catalogTags: Duplicate tag detected! " + tag);
	    			tags.add(tag);
    			}
    		}
    	}
    	return tags;
    }
    
    /**
     * Helper function to iterate through possible tags and select the next unused one
     * @param tags Hash of all used tags
     * @return the next unused tag
     */
	public static Pattern nameTag = Pattern.compile("^(.*) \\[([^\\s]+)\\]$");
	public static enum flagColors {R, B};
	public static int flagMaxNum = 6;
    private static String getNextTag(HashSet<String> tags) {
    	for (flagColors color: flagColors.values()) {
    		for (int i = 1; i <= flagMaxNum; ++i) {
    			String tag = color.toString() + i;
    			if (!tags.contains(tag))
    				return tag;
    		}
    	}
    	System.out.println("-W- InitTableModel:getNextTag: no free tags!");
    	return "S99";
    }
	
	protected EncounterLogEventSource encounterLogEventSource = new EncounterLogEventSource();
	
	/**
	 * Add an event listener for EncounterLogEvents
	 * @param listener - the listener to add
	 */
	public void addEncounterLogEventListener(EncounterLogEventListener listener) {
		encounterLogEventSource.addEncounterLogEventListener(listener);
	}
	
	/**
	 * Remove an event listener for EncounterLogEvents
	 * @param listener - the listener to remove
	 */
	public void removeEncounterLogEventListener(EncounterLogEventListener listener) {
		encounterLogEventSource.removeEncounterLogEventListener(listener);
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
}
