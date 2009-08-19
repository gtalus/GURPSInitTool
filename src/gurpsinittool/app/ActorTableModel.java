package gurpsinittool.app;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.table.AbstractTableModel;
import gurpsinittool.data.*;
import gurpsinittool.data.Actor.ActorState;
import gurpsinittool.data.Actor.ActorType;

public class ActorTableModel extends AbstractTableModel {

	/**
	 * Default UID
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = true;
	
	private String[] columnNames = {"Name", "HP", "Damage", "Health", "State", "Type"};
	public enum columns {Name, HP, Damage, Health, State, Type};
	//private class<?>[] columnClasses = {new String().getClass(), new Integer(0).getClass(), new Integer(0).getClass(), new String().getClass(), new String().getClass()};
	private static int numColumns = 6;
	
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
	 * Fires a row updated for the listed actor. Does not currently support multiple instances of the same Actor!
	 * @param actor
	 */
	public void fireRefresh(Actor actor) {
		// TODO: support multiple instances of the same actor
		int row = actorList.indexOf(actor);
		fireTableRowsUpdated(row, row);
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
	
	public Class<?> getColumnClass(int c) {
		switch (columns.values()[c]) {
		case Name:
			return new String().getClass();
		case HP:
			return new Integer(0).getClass();
		case Damage:
			return new Integer(0).getClass();
		case Health:
			return new Integer(0).getClass();
		case State:
			return new String().getClass();
		case Type:
			return new String().getClass();
		default:
			return null;
		}
	}
	
	//@Override
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
		case HP:
			return actor.HP;
		case Damage:
			return actor.Damage;
		case Health:
			return actor.Health;
		case State:
			return actor.State;
		case Type:
			return actor.Type;
		default:
			return null;
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
	 */
	public void nextActor() {
		if (activeActor != -1) {
			fireTableCellUpdated(activeActor, 0);
		}
		nextActorInternal();
		fireTableCellUpdated(activeActor, 0);
	}
	
	/**
	 * Calculate the next actor without updating the table
	 */
	protected void nextActorInternal() {
		do {
			activeActor++;
			if (activeActor >= actorList.size() - 1) // Remember that the last entry is 'new...'
				activeActor = 0;
		} while (actorList.get(activeActor).State != Actor.ActorState.Active & actorList.get(activeActor).State != Actor.ActorState.Disabled);
	}

	/**
	 * Remove an actor from the table (only the specific row)
	 * @param row : the actor to remove
	 */
	public void removeActor(int row) {
		actorList.remove(row);
		if (row < activeActor) { activeActor--; } // track active Actor
		else if (row == activeActor) { activeActor--; nextActor(); }
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
			fireTableRowsDeleted(row, row);
		}
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
		fireTableCellUpdated(activeActor, 0);
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
            System.out.println("ActorTableModel: Setting value at " + row + "," + col
                               + " to " + value
                               + " (an instance of "
                               + value.getClass() + ")");
        }
        Actor a = (Actor) actorList.get(row);
        
        if (row == actorList.size() -1) { // new row
        	actorList.add(new Actor("new...", ActorState.Active, ActorType.Enemy));
        	fireTableRowsInserted(row+1,row+1);
        }

		switch (columns.values()[col]) {
		case Name:
			a.Name = (String) value;
			break;
		case HP:
			a.HP = (Integer) value;
			break;
		case Damage:
			a.Damage = (Integer) value;
			break;
		case Health:
			a.Health = (Integer) value;
			break;
		case State:
			a.State = ActorState.valueOf((String) value);
			break;
		case Type:
			a.Type = ActorType.valueOf((String) value);
			break;
		}
		// Update the entire row, since changing state or type may affect formatting for all cells in the row.
		// What about multiple copies of the same actor?
		int [] rows = getActorRows(a);
		for (int i=0; i < rows.length; i++) {
			fireTableRowsUpdated(rows[i], rows[i]);
		}

    }

}
