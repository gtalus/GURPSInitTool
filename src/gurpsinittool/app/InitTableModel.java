package gurpsinittool.app;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.JTextComponent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import gurpsinittool.data.*;
import gurpsinittool.data.ActorBase.ActorStatus;
import gurpsinittool.data.ActorBase.ActorType;
import gurpsinittool.data.ActorBase.BasicTrait;
import gurpsinittool.util.CleanFileChangeEventSource;
import gurpsinittool.util.FileChangeEventListener;
import gurpsinittool.util.SearchSupport;

@SuppressWarnings("serial")
public class InitTableModel extends AbstractTableModel implements PropertyChangeListener {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(InitTableModel.class.getName());

	private UndoableEditSupport mUes = new UndoableEditSupport();

	// Removed: Dodge, type
	// TODO: consolidate HP/Damage, FP/Fatigue
	//private String[] columnNames = {"Act", "Name", "Speed", "Move", "HT", "HP", "Injury", "FP", "Fatigue", "Status"};
	//private Class<?>[] columnClasses = {String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class};
	//public enum columns {Act, Name, Speed, Move, HT, HP, Injury, FP, Fatigue, Status};
	//private static int numColumns = 10;
	
	// Use dynamic column list
	private ArrayList<String> columnNames = new ArrayList<String>();
	
	Actor newActor = new Actor();
	private ArrayList<Actor> actorList = new ArrayList<Actor>();	
	
	private GameMaster gameMaster;
	
	public InitTableModel(GameMaster gameMaster) {
		this.gameMaster = gameMaster;
		// This is a special row which allows new actors to be added.
		addNewActor();
	}
	
	/**
	 * Add a new actor to the specified slot
	 * @param source : the actor object to add
	 * @param destRow : the index of the slot to insert into (pushes current actor down one)
	 */
	public void addActor(Actor actor, int destRow) {
		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Start: " + actor.getTraitValue(BasicTrait.Name));}
		if (destRow > actorList.size()-1) // Don't put anything after the 'new' row
			destRow = actorList.size()-1;
		else if (destRow < 0) // Don't try to put stuff before the beginning!
			destRow = 0;
		
		mUes.postEdit(new AddActorEdit(actor, destRow));
		addActorToList(actorList, actor, destRow);
		setDirty();
	}
	/**
	 * Add an actor to the specified list and perform appropriate 
	 * The list argument is to make undo/redo easier as there with multiple 
	 * actorLists that may not be currently loaded.
	 * @param list - the list to add the actor to
	 * @param actor - the actor to add
	 * @param position - the position in the list to add the actor at
	 */
	private void addActorToList(ArrayList<Actor> list, Actor actor, int position) {
		list.add(position, actor);
		actor.setGameLogicEnabled(gameMaster.isGameLogicEnabled());
		if (list.equals(actorList)) { // If this is the currently loaded actor list
			actor.addPropertyChangeListener(this);
			actor.addEncounterLogEventListener(gameMaster);
			actor.addUndoableEditListener(gameMaster);
			fireTableRowsInserted(position,position);
		}
	}
	/**
	 * Add a new Actor based on newActor to the end of the list 
	 */
	private void addNewActor() {
		addActorToList(actorList, new Actor(newActor), actorList.size());
	}
	
	/**
	 * Remove an actor from the table (only the specific row)
	 * @param row : the actor to remove
	 */
	public void removeActor(int row) {
		Actor actor = getActor(row);
		
		mUes.postEdit(new RemoveActorEdit(actor, row));
		removeActorFromList(actorList, actor, row);
	}
	/**
	 * Internal method to make undo/redo easier with multiple actorLists that may not be currently loaded
	 * @param list
	 * @param actor
	 * @param position
	 */
	private void removeActorFromList(ArrayList<Actor> list, Actor actor, int position) {
		list.remove(position);	
		if (list.equals(actorList)) { // If this is the currently loaded actor list
			actor.removePropertyChangeListener(this);
			actor.removeUndoableEditListener(gameMaster);
			actor.removeEncounterLogEventListener(gameMaster);
			setDirty();
			fireTableRowsDeleted(position,position);
		}		
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
		return String.class; // All strings!
	}
	
	public int getColumnCount() {
		return columnNames.size();
	}
	
	@Override
    public String getColumnName(int col) {
        return columnNames.get(col);
    }
	
	public ArrayList<String> getColumnNames() {
		return new ArrayList<String>(columnNames);
	}

	public int getRowCount() {
		return actorList.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Actor actor = (Actor) actorList.get(rowIndex);
		String traitName = columnNames.get(columnIndex);
		// Special cases first:
		if (traitName.equals("Act")) {
			return null;
		} else if (traitName.equals("Status")) {
			return actor.getAllStatuses();
		} else if (traitName.equals("Type")) {
			return actor.getType();
		} else {
			if (!actor.hasTrait(traitName))
				return null;
			return actor.getTraitValue(traitName);
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
    	if (LOG.isLoggable(Level.FINER)) {LOG.finer("Setting value at " + row + "," + col
    			+ " to " + value + " (an instance of " + value.getClass() + ")");}       
        
        Actor a = (Actor) actorList.get(row);
        String traitName = columnNames.get(col);
        if (traitName.equals("Act")) {
			// do nothing
		} else if (traitName.equals("Status")) {
			a.setAllStatuses((HashSet<ActorStatus>) value);
		} else if (traitName.equals("Type")) {
			a.setType((ActorType)value);
		} else {
			a.setTrait(traitName, (String) value); 
		}
    }
    
	@Override
    public boolean isCellEditable(int row, int col) {
		String columnName = columnNames.get(col);
		if ("Status".equals(columnName) || "Type".equals(columnName)) {
			return true;
		} else if (columnName.equals("Act") || columnName.equals("Notes") || !Actor.isBasicTrait(columnName)) {
			return false;
		} else {
			return true;
		}
    }

	/**
	 * Set the list of columns, which will trigger a full refresh of the table.
	 * @param columnList : the new list of column names, in order, either matching 
	 * the name of traits or special value "Act"
	 */
	public void setColumnList(ArrayList<String> columnList) {
		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Setting list to " + columnList); }
		columnNames.clear();
		columnNames.addAll(columnList);
		super.fireTableStructureChanged();
	}
	
	/**
	 * Set the list of actors to the ArrayList<Actor> specified. Redraw table.
	 * @param actorList : the new ArrayList<Actor> to use as the base for the ActorTableModel
	 */
	public void setActorList(ArrayList<Actor> actorList) {
		for(Actor a: this.actorList) {
			a.removePropertyChangeListener(this);
			a.removeUndoableEditListener(gameMaster);
			a.removeEncounterLogEventListener(gameMaster);
		}
		fireTableRowsDeleted(0, getRowCount()); // remove current selection
		
		if (actorList == null) {
			//This is an empty table, so it has NO ACTORS! EMPTY!
			this.actorList = new ArrayList<Actor>();
		} else { 
			this.actorList = actorList;
			for (int i = 0; i < this.actorList.size(); i++) {
				Actor a = this.actorList.get(i);
				a.addPropertyChangeListener(this);
				a.addUndoableEditListener(gameMaster);
				a.addEncounterLogEventListener(gameMaster);
			}
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
			final Actor actor = (Actor)e.getSource();
			if (LOG.isLoggable(Level.FINER)) {LOG.finer("Got notification from actor " + actor.getTraitValue(BasicTrait.Name));}
			int [] rows = getActorRows(actor);
			for (int i=0; i < rows.length; i++) {
				if (rows[i] == getRowCount() -1) { // changed last row, send request for a new Actor to the game master
					addNewActor(); // Add a new 'newActor' to the end of the list
					mUes.postEdit(new AddActorEdit(actorList.get(actorList.size()-2), actorList.size()-2));
				}
				fireTableRowsUpdated(rows[i], rows[i]);
			}
			if (LOG.isLoggable(Level.FINER)) {LOG.finer("Done with actor " + actor.getTraitValue(BasicTrait.Name));}
		} else if (e.getPropertyName().equals("ActiveActor")) {
				int oldValue = (Integer) e.getOldValue();
				int newValue = (Integer) e.getNewValue();
				if (oldValue != -1)
					fireTableRowsUpdated(oldValue, oldValue);
				if (newValue != -1)
					fireTableRowsUpdated(newValue, newValue);
		}
	}
	
	// Undo/Redo suport
	public void addUndoableEditListener(UndoableEditListener listener) {
		mUes.addUndoableEditListener(listener);
	}
	public void removeUndoableEditListener(UndoableEditListener listener) {
		mUes.removeUndoableEditListener(listener);
	}
	
    private class AddActorEdit extends AbstractUndoableEdit {
    	private Actor actor;
    	private int index;
    	private ArrayList<Actor> list;
    	public AddActorEdit(Actor actor, int index) {
    		this.actor = actor;
    		this.index = index;
    		list = actorList; // Keep track of what the current list was
    	}
    	public String getPresentationName() { return "Add"; }
    	
    	public void undo() {
			super.undo();
			removeActorFromList(list, actor, index);
    	}    	
    	public void redo() {
			super.redo();			
			addActorToList(list, actor, index);
    	}        	
    }   
    
    private class RemoveActorEdit extends AbstractUndoableEdit {
    	private Actor actor;
    	private int index;
    	private ArrayList<Actor> list;
    	public RemoveActorEdit(Actor actor, int index) {
    		this.actor = actor;
    		this.index = index;
    		list = actorList; // Keep track of what the current list was
    	}
    	public String getPresentationName() { return "Delete"; }
    	
    	public void undo() {
			super.undo();
			addActorToList(list, actor, index);
    	}    	
    	public void redo() {
    		super.redo();
    		removeActorFromList(list, actor, index);
    	}        	
    }
    
}
