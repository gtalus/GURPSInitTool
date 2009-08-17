package gurpsinittool.app;


import java.io.IOException;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import gurpsinittool.data.Actor;

public class InitTableTransferHandler extends TransferHandler {

	/**
	 * Default serialization UID
	 */
	private static final long serialVersionUID = 1L;
	
	private static final boolean DEBUG = true;

	protected static DataFlavor actorFlavor = new DataFlavor(Actor.class, "GURPS Actor Object");
	
	protected static DataFlavor[] supportedFlavors = {
		actorFlavor
	};

	public InitTableTransferHandler(String property){
        super(property);
    }

	@Override
	public boolean canImport(TransferSupport support) {
		
		/*if (DEBUG) {
		  	System.out.println("Receiving import request");
		  	DataFlavor[] flavors = support.getDataFlavors();
		  	for (int i =0; i < flavors.length; i++) {
			  	System.out.println(" import flavor: " + flavors[i].getHumanPresentableName());
		  	}
		}*/
		
		if (!support.isDataFlavorSupported(actorFlavor))
			return false;
		
		// Don't allow dropping below the 'new...' row
	    JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
	    InitTable table = (InitTable) support.getComponent();
	    int row = dl.getRow(); 
	    if (row == table.getRowCount())
	    	return false;
	      
		return true;
	}
	
	@Override
	public boolean importData(TransferSupport support) {
		
		// if we can't handle the import, say so
        if (!canImport(support)) {
          return false;
        }
        
        JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
        int row = dl.getRow(); 
        int col = dl.getColumn();
        
        Transferable t = support.getTransferable();
        int action = support.getDropAction();
        
        // Do in-process & in-table import: everything is done for you.
        int[] actorRows;
        try {
        	actorRows = (int[]) t.getTransferData(actorFlavor);
        } catch (UnsupportedFlavorException e) {
        	return false;
        } catch (IOException e) {
        	return false;
        }

        InitTable table = (InitTable) support.getComponent();
        ActorTableModel tableModel = (ActorTableModel) table.getModel();
        Actor[] actors = tableModel.getActors(actorRows);
    
        // Don't try to put items after the 'new' row
        // needed to preserve actor order
        if (row >= tableModel.getRowCount()) { row = tableModel.getRowCount() - 1; }

        for (int i = actors.length-1; i >= 0; i--) { // Actors added to same 'row', so go from bottom up to preserve order
        	if (action == MOVE) {
               	if (DEBUG) { System.out.println("Cloning actor @ row: " + row); }
               	tableModel.addActor(actors[i], row);
               	table.getSelectionModel().addSelectionInterval(row, row);
        	}
        	else { // For copy: make copies of the actors 
              	if (DEBUG) { System.out.println("Copying actor @ row: " + row); }
        		tableModel.addActor(new Actor(actors[i]), row);
        	}	
        }
        
        if (action == MOVE) { // For move, delete old rows, then add new ones
            for (int i = actorRows.length-1; i >= 0; i--) {  // Go from bottom up to preserve order
            	if (actorRows[i] >= row) { actorRows[i] += actorRows.length; } // adjust for inserted records
             	tableModel.removeActor(actorRows[i]);
            	//if (actorRows[i] < row) { row--; } // Shift target up if removed row is above it
            }
        }

        
        if (DEBUG) {
			System.out.println("Getting import data request: " + row + "," + col
                 + " String " + dl.toString()
                 );
			DataFlavor[] flavors = support.getDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				System.out.println("Debug flavor: " + flavors[i].toString());
			}
		}
        
        return true;
	}
	
	@Override
	public int getSourceActions(JComponent c) {
		return COPY | MOVE;
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		// Allows multiple selection rows
		JTable table = (JTable) c;
		int[] rows = table.getSelectedRows();
		// Because the 'new...' row is filtered out of the selection, it is possible to try to drag 0 rows.
		if (rows.length == 0)
			return null;
		java.util.Arrays.sort(rows);
		return new TransferableActor(rows);
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		/*if (action == MOVE) {
			JTable table = (JTable) source;
			
			// TODO: reduce dependency on selected row
			Actor[] actors;
	        try {
	        	actors = (Actor[]) data.getTransferData(actorFlavor);
	        } catch (UnsupportedFlavorException e) {
	        	return;
	        } catch (IOException e) {
	        	return;
	        }
	        for (int i = 0; i < actors.length; i++) {
	        	if (DEBUG) { System.out.println("After move, deleting actor " + actors[i].Name + "..."); }
	        	((ActorTableModel) table.getModel()).removeActor(actors[i]);
	        }
	        
			//int[] rows =  table.getSelectedRows();
			//java.util.Arrays.sort(rows);
			//for (int i = rows.length - 1; i >= 0; i--) { // need to delete rows from bottom up
			//	if (DEBUG) { System.out.println("After move, deleting row " + rows[i] + "..."); }
			//	((ActorTableModel) table.getModel()).removeActor(rows[i]);
			//}
		}*/
		if (DEBUG) {
			System.out.println("export done: " + action);
		}
	}
	
	class TransferableActor implements Transferable {
		
		int[] actorRows;
		
		public TransferableActor(int[] actorRows) { this.actorRows = actorRows; }
		  
		/** Return a list of DataFlavors we can support */
		public DataFlavor[] getTransferDataFlavors() { return supportedFlavors; }

		/** 
		   * Transfer the data.  Given a specified DataFlavor, return an Object
		   * appropriate for that flavor.  Throw UnsupportedFlavorException if we
		   * don't support the requested flavor.
		   */
		public Object getTransferData(DataFlavor flavor) 
		       throws UnsupportedFlavorException, IOException
		  {
		    if (flavor.equals(actorFlavor)) return actorRows;
		    else throw new UnsupportedFlavorException(flavor);
		  }

		/** Check whether a specified DataFlavor is available */
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if (flavor.equals(actorFlavor)) return true;
		    return false;
		}
	}
}
