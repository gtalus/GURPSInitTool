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
	      
		// Assume that if actorFlavor is supported, that this is a TransferableActor
		//TransferableActor t = (TransferableActor) support.getTransferable();
		// If the table types do not match, then set action to copy
		//if (t.isSourceInitTable() != table.isInitTable()) {
		//	support.setDropAction(COPY);
		//}

		return true;
	}
	
	@Override
	public boolean importData(TransferSupport support) {		
		// if we can't handle the import, say so
        if (!canImport(support)) {
          return false;
        }
          
        // Do in-process & in-table import: everything is done for you.
        Transferable t = support.getTransferable();
        Actor[] actorRows;
        try {
        	actorRows = (Actor[]) t.getTransferData(actorFlavor);
        } catch (UnsupportedFlavorException e) {
        	return false;
        } catch (IOException e) {
        	return false;
        }

        InitTable table = (InitTable) support.getComponent();
        ActorTableModel tableModel = (ActorTableModel) table.getModel();
        
        // Don't try to put items after the 'new' row
        // needed to preserve actor order
        JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
        int row = dl.getRow(); 
        if (row >= tableModel.getRowCount()) { row = tableModel.getRowCount() - 1; }

        for (int i = actorRows.length-1; i >= 0; i--) { // Actors added to same 'row', so go from bottom up to preserve order
        	if (DEBUG) { System.out.println("Adding actor # " + i + " @ row: " + row); }
            tableModel.addActor(actorRows[i], row);
            table.getSelectionModel().addSelectionInterval(row, row);
        }
        
        if (DEBUG) {
			System.out.println("Getting import data request: " + row + " String " + dl.toString());
			DataFlavor[] flavors = support.getDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				System.out.println("Debug flavor: " + flavors[i].toString());
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
		ActorTableModel tableModel = (ActorTableModel) table.getModel();
		Actor[] actorRows = tableModel.getActors(rows);
		return new TransferableActor(actorRows, table.isInitTable());
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if (action == MOVE) {
			JTable table = (JTable) source;
			
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
		}
		if (DEBUG) {
			System.out.println("export done: " + action);
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
		
		/** Return true if the source was a group table **/
		public boolean isSourceInitTable() {
			return this.isSourceInitTable;
		}
	}
}
