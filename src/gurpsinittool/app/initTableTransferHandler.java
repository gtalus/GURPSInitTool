package gurpsinittool.app;


import java.io.IOException;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import gurpsinittool.data.Actor;

public class initTableTransferHandler extends TransferHandler {

	/**
	 * Default serialization UID
	 */
	private static final long serialVersionUID = 1L;
	
	private static final boolean DEBUG = true;

	public initTableTransferHandler(String property){
        super(property);
    }

	@Override
	public boolean canImport(TransferSupport support) {
		if (!support.isDataFlavorSupported(TransferableActor.actorFlavor))
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
        
        Actor[] actors;
        try {
        	actors = (Actor[]) t.getTransferData(TransferableActor.actorFlavor);
        } catch (UnsupportedFlavorException e) {
        	return false;
        } catch (IOException e) {
        	return false;
        }

        JTable table = (JTable) support.getComponent();
        // Don't try to put items after the 'new' row
        if (row >= ((ActorTableModel) table.getModel()).getRowCount()) { row = ((ActorTableModel) table.getModel()).getRowCount() - 1; }
        for (int i = actors.length-1; i >= 0; i--) { // Actors added to same 'row', so go from bottom up to preserve order
        	if (DEBUG) { System.out.println("Adding actor @ row: " + row); }
        	// When importing data, make copies of the actors instead of accepting the references
        	((ActorTableModel) table.getModel()).addActor(new Actor(actors[i]), row);
        }
        
        if (DEBUG) {
			System.out.println("Getting import data request: " + row + "," + col
                 + " String " + dl.toString()
                 );
			
			Component target = support.getComponent();
			//System.out.println("Debug data: " + target.toString());
			DataFlavor[] flavors = support.getDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				System.out.println("Debug flavor: " + flavors[i].toString());
			}

			//System.out.println("Debug support: " + support.toString());
//			System.out.println("Debug data: " + target.toString());
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
		java.util.Arrays.sort(rows);
		return new TransferableActor(((ActorTableModel) table.getModel()).getActors(rows));
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if (action == MOVE) {
			JTable table = (JTable) source;
			
			// TODO: reduce dependency on selected row
			Actor[] actors;
	        try {
	        	actors = (Actor[]) data.getTransferData(TransferableActor.actorFlavor);
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
		}
		if (DEBUG) {
			System.out.println("export done: " + action);
		}
	}
}
