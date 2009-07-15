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
	
	private String[] columnNames = {"Name", "HP", "Health", "State", "Type"};
	private ArrayList<Actor> actorList = new ArrayList<Actor>(Arrays.asList(
			new Actor("Damian", ActorState.Active, ActorType.PC),
			new Actor("Galen", ActorState.Active, ActorType.PC),	
			new Actor("Guard", ActorState.Active, ActorType.Ally),
			new Actor("Guard 2", ActorState.Active, ActorType.Ally),
			new Actor("Villager", ActorState.Active, ActorType.Neutral),
			new Actor("Vorpal Blade", ActorState.Active, ActorType.Special),
			new Actor("Monster", ActorState.Active, ActorType.Enemy),
			new Actor("Big Monster", ActorState.Active, ActorType.Enemy),
			new Actor("Demon", ActorState.Active, ActorType.Enemy),
			new Actor("new...", ActorState.Active, ActorType.Enemy))
			);
	private int activeActor = -1;
	
	/**
	 * Get index of current active actor
	 * @return
	 */
	public int getActiveActor() {
		return activeActor;
	}
	
	/**
	 * Set next actor as active
	 */
	public void nextActor() {
		if (activeActor != -1) {
			actorList.get(activeActor).Active = false;
			fireTableCellUpdated(activeActor, 0);
		}
		activeActor++;
		if (activeActor >= actorList.size() - 1) // Remember that the last entry is 'new...'
			activeActor = 0;
		actorList.get(activeActor).Active = true;
		fireTableCellUpdated(activeActor, 0);
	}

	/**
	 * Move the actor from one slot to another
	 * @param source : the index of the actor to move
	 * @param dest : the index of the slot to move into (pushes current actor down one)
	 */
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
	}
	
	/**
	 * Create a copy of an actor to another slot
	 * @param source : the index of the actor to copy
	 * @param dest : the index of the slot to copy into (pushes current actor down one)
	 */
	public void copyActor(int source, int dest) {
		if (dest >= actorList.size()-1) // Don't put anything after the 'new' row
			dest = actorList.size()-2;
			
		Actor movingActor = getActor(source);
		actorList.add(dest, movingActor);
	}
	
	/**
	 * Add a new actor to the specified slot
	 * @param source : the actor object to add
	 * @param dest : the index of the slot to insert into (pushes current actor down one)
	 */
	public void addActor(Actor source, int dest) {
		if (dest > actorList.size()-1) // Don't put anything after the 'new' row
			dest = actorList.size()-1;
			
		actorList.add(dest, source);
		fireTableRowsInserted(dest,dest);
	}
 
	/**
	 * Remove an actor from the table
	 * @param row : the actor to remove
	 */
	public void removeActor(int row) {
		actorList.remove(row);
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

	public Actor getActor(int row) {
		return (Actor) actorList.get(row);
	}
	
	//@Override
	public int getColumnCount() {
		return 5;
	}
	
	@Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

	//@Override
	public int getRowCount() {
		return actorList.size();
	}

	//@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Actor actor = (Actor) actorList.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return actor.Name;
		case 1:
			return actor.HP;
		case 2:
			return actor.Health;
		case 3:
			return actor.State;
		case 4:
			return actor.Type;
		default:
			return null;
		}
	}
	
	public Class<?> getColumnClass(int c) {
		switch (c) {
		case 0:
			return new String().getClass();
		case 1:
			return new Integer(0).getClass();
		case 2:
			return new Integer(0).getClass();
		case 3:
			return new String().getClass();
		case 4:
			return new String().getClass();
		default:
			return null;
		}
	}
	@Override
    public boolean isCellEditable(int row, int col) {
       return true;
    }
	
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (DEBUG) {
            System.out.println("Setting value at " + row + "," + col
                               + " to " + value
                               + " (an instance of "
                               + value.getClass() + ")");
        }
        Actor a = (Actor) actorList.get(row);
        
        if (row == actorList.size() -1) { // new row
        	actorList.add(new Actor("new...", ActorState.Active, ActorType.Enemy));
        	fireTableRowsInserted(row+1,row+1);
        }

		switch (col) {
		case 0:
			a.Name = (String) value;
			break;
		case 1:
			a.HP = (Integer) value;
			break;
		case 2:
			a.Health = (Integer) value;
			break;
		case 3:
			a.State = ActorState.valueOf((String) value);
			break;
		case 4:
			a.Type = ActorType.valueOf((String) value);
			break;
		}
		// Update the entire row, since changing state or type may affect formatting for all cells in the row.
		fireTableRowsUpdated(row, row);
    }

}
