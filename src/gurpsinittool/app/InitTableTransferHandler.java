package gurpsinittool.app;


import java.io.IOException;
import java.util.ArrayList;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import gurpsinittool.data.Actor;
import gurpsinittool.data.Actor.BasicTrait;

public class InitTableTransferHandler extends TransferHandler {

	/**
	 * Default serialization UID
	 */
	private static final long serialVersionUID = 1L;
	
	private static final boolean DEBUG = false;

	// Must hack class to be different: string difference does not make unique DataFlavor
	protected static DataFlavor initTableActorFlavor = new DataFlavor(InitTable.class, "GURPS Actor Object from init table");
	protected static DataFlavor groupTableActorFlavor = new DataFlavor(GroupTree.class, "GURPS Actor Object from group table");
	
	protected static DataFlavor[] supportedFlavors = {
		initTableActorFlavor, groupTableActorFlavor
	};

	public InitTableTransferHandler(String property){
        super(property);
    }

	@Override
	public boolean canImport(TransferSupport support) {
		
		if (!support.isDataFlavorSupported(initTableActorFlavor) 
				&& !support.isDataFlavorSupported(groupTableActorFlavor) 
				&& !support.isDataFlavorSupported(GroupTreeTransferHandler.actorGroupFlavor))
			return false;

		// Don't allow dropping below the 'new...' row 
		// Actually allow it, but just put them before the new row
	    JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
	    InitTable table = (InitTable) support.getComponent();
	    int row = dl.getRow(); 
	    if (row == table.getRowCount())
	    	support.setShowDropLocation(false);
	      
		// Set Drop Action based on whether this is a cross-table drag.
		// If the table types do not match, then set action to copy
	    if (support.isDataFlavorSupported(GroupTreeTransferHandler.actorGroupFlavor)) {
			support.setDropAction(COPY);
	    }
 		if (table.isInitTable() != support.isDataFlavorSupported(initTableActorFlavor)) {
			support.setDropAction(COPY);
		}

		return true;
	}
	
	@Override
	public boolean importData(TransferSupport support) {		
		// if we can't handle the import, say so
        if (!canImport(support)) {
          return false;
        }
          
        // Do in-process & in-table import: everything is done for you.
    	if (DEBUG) { System.out.println("InitTreeTransferHandler: Getting transferable data for InitTable import..."); }
    	Transferable t = support.getTransferable();
        Actor[] actorRows;
        try {
        	if (t.isDataFlavorSupported(GroupTreeTransferHandler.actorGroupFlavor)) {
        		GroupTreeNode node = (GroupTreeNode) t.getTransferData(GroupTreeTransferHandler.actorGroupFlavor); 
        		ArrayList<Actor> actors = node.getActorList();
        		actorRows = actors.subList(0, actors.size()-1).toArray(new Actor[0]);
        	}
        	else 
        		actorRows = (Actor[]) t.getTransferData(initTableActorFlavor); // Don't really care which flavor it is
        } catch (UnsupportedFlavorException e) {
    		if (DEBUG) { System.out.println("InitTreeTransferHandler: -E- Unsupported Flavor Exception"); }
        	return false;
        } catch (IOException e) {
    		if (DEBUG) { System.out.println("InitTreeTransferHandler: -E- IO Exception:" + e.toString()); }
        	return false;
        }
        
    	if (DEBUG) { System.out.println("InitTreeTransferHandler:  Transferable data retrieved."); }

        InitTable table = (InitTable) support.getComponent();
        InitTableModel tableModel = (InitTableModel) table.getModel();
        
        // Don't try to put items after the 'new' row
        // needed to preserve actor order
        JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
        table.stopCellEditing(); // Don't allow the table to be in edit mode while we change the structure
        int row = dl.getRow(); 
        if (row >= tableModel.getRowCount()) { row = tableModel.getRowCount() - 1; }

        for (int i = actorRows.length-1; i >= 0; i--) { // Actors added to same 'row', so go from bottom up to preserve order
        	if (DEBUG) { System.out.println("InitTreeTransferHandler: Adding actor # " + i + " @ row: " + row); }
            tableModel.addActor(actorRows[i], row);
            table.getSelectionModel().addSelectionInterval(row, row);
        }
        
        if (DEBUG) {
			System.out.println("InitTreeTransferHandler: Getting import data request: " + row + " String " + dl.toString());
			DataFlavor[] flavors = support.getDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				System.out.println("InitTreeTransferHandler: Debug flavor: " + flavors[i].toString());
			}
		}
        
        return true;
	}
	
	@Override
	public int getSourceActions(JComponent c) {
		//if (((InitTable) c).isInitTable()) 
			return COPY | MOVE;
		//else 
			//return COPY;
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		// Allows multiple selection rows
		InitTable table = (InitTable) c;
		int[] rows = table.getSelectedRows();
		// Because the 'new...' row is filtered out of the selection, it is possible to try to drag 0 rows.
		if (rows.length == 0)
			return null;
		
		java.util.Arrays.sort(rows);
		InitTableModel tableModel = (InitTableModel) table.getModel();
		Actor[] actorRows = tableModel.getActors(rows);
		return new TransferableActor(actorRows, table.isInitTable());
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if (action == MOVE) {
			JTable table = (JTable) source;
			
			Actor[] actors;
	        try {
	        	actors = (Actor[]) data.getTransferData(initTableActorFlavor); // Don't really care which flavor it is
	        } catch (UnsupportedFlavorException e) {
	        	return;
	        } catch (IOException e) {
	        	return;
	        }
	        for (int i = 0; i < actors.length; i++) {
	        	if (DEBUG) { System.out.println("InitTreeTransferHandler: After move, deleting actor " + actors[i].getValue(BasicTrait.Name) + "..."); }
	        	((InitTableModel) table.getModel()).removeActor(actors[i]);
	        }
		}
		if (DEBUG) {
			System.out.println("InitTreeTransferHandler: export done: " + action);
		}
	}
	
	class TransferableActor implements Transferable {
		
		Actor[] actorRows;
		boolean isSourceInitTable;
		
		public TransferableActor(Actor[] actorRows, boolean isSourceInitTable) { 
			this.actorRows = actorRows; 
			this.isSourceInitTable = isSourceInitTable;
		}
		  
		/** Return a list of DataFlavors we can support */
		public DataFlavor[] getTransferDataFlavors() { 
			if (isSourceInitTable) {
				return new DataFlavor[] {initTableActorFlavor}; 
			}
			else { return new DataFlavor[] {groupTableActorFlavor}; }
		}

		/** 
		   * Transfer the data.  Given a specified DataFlavor, return an Object
		   * appropriate for that flavor.  Throw UnsupportedFlavorException if we
		   * don't support the requested flavor.
		   */
		public Object getTransferData(DataFlavor flavor) 
		       throws UnsupportedFlavorException, IOException
		  {
		    if (flavor.equals(initTableActorFlavor) || flavor.equals(groupTableActorFlavor)) return actorRows;
		    else throw new UnsupportedFlavorException(flavor);
		  }

		/** Check whether a specified DataFlavor is available */
		public boolean isDataFlavorSupported(DataFlavor flavor) {
 			if ((flavor.equals(initTableActorFlavor) && isSourceInitTable) || (flavor.equals(groupTableActorFlavor) && !isSourceInitTable)) return true;
		    return false;
		}
		
	}
}
