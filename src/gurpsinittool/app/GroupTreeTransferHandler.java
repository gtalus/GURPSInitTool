package gurpsinittool.app;

import gurpsinittool.data.Actor;
import gurpsinittool.data.GameMaster;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

@SuppressWarnings("serial")
public class GroupTreeTransferHandler extends TransferHandler {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(GroupTreeTransferHandler.class.getName());

	
	private GroupTree parent;

	protected static DataFlavor actorGroupFlavor = new DataFlavor(GroupTreeNode.class, "GURPS Actor Group Object");
	
	protected static DataFlavor[] supportedFlavors = {
		actorGroupFlavor
	};

	public GroupTreeTransferHandler(GroupTree parent, String property){
        super(property);
        
        this.parent = parent;
    }

	@Override
	public boolean canImport(TransferSupport support) {
		
		// We need to check to see if the drop location is acceptable based on the flavor
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        TreePath insertPath = dl.getPath();
        //int insertIndex = dl.getChildIndex();  
        if (insertPath == null) {return false; }
        GroupTreeNode parentNode = (GroupTreeNode) (insertPath.getLastPathComponent());

        if (support.isDataFlavorSupported(actorGroupFlavor)) {
        	parent.setDropMode(DropMode.INSERT);
        	return true;
	        // if (parentNode.isFolder() || insertIndex != -1) {return true;}
	        //else {support.setShowDropLocation(false); }
	        //return true; // not sure whether should say can't import, or setShowDropLocation(false). when using the SSDL, user can drop group on group, but the insert will fail
		}
		else if ( support.isDataFlavorSupported(InitTableTransferHandler.initTableActorFlavor)
				|| support.isDataFlavorSupported(InitTableTransferHandler.groupTableActorFlavor)){
			parent.setDropMode(DropMode.ON);
			support.setDropAction(COPY);
			if (parentNode.isGroup()) { return true; }
		}
		
		return false;
	}
	
	@Override
	public boolean importData(TransferSupport support) {
		
		// if we can't handle the import, say so
        if (!canImport(support)) {
          return false;
        }
        
        GroupTree tree = (GroupTree) support.getComponent();
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        TreePath insertPath = dl.getPath();
        int insertIndex = dl.getChildIndex();  
        GroupTreeNode parentNode = (GroupTreeNode) (insertPath.getLastPathComponent());
        Transferable t = support.getTransferable();
 		int action = support.getDropAction();
 		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Starting action " + action); }
 		
 		if (support.isDataFlavorSupported(actorGroupFlavor)) { // Get node
 			if (LOG.isLoggable(Level.FINE)) {LOG.fine("Retrieving node data..."); }
			GroupTreeNode transferNode;
			try {
				transferNode = (GroupTreeNode) t.getTransferData(actorGroupFlavor);
	        } catch (UnsupportedFlavorException e) {
	           	if (LOG.isLoggable(Level.WARNING)) {LOG.log(Level.WARNING, "Unsupported Flavor Exception: " + e.getMessage(), e);}
	        	return false;
	        } catch (IOException e) {
	        	if (LOG.isLoggable(Level.WARNING)) {LOG.log(Level.WARNING, "IOException: " + e.getMessage(), e);}
	        	return false;
	        }
			TreePath transferPath = new TreePath(transferNode.getPath());

			if (action == MOVE && !tree.isSelectionEmpty()) {
				TreePath selectionPath = tree.getSelectionPath();
		        // Check whether the destination is within the source, and disallow for move (will cause entire tree to be deleted)
		 		if (transferPath.getPathCount() == selectionPath.getPathCount() &&
		 				insertPath.getPathCount() >= selectionPath.getPathCount()) { // Insert path must be longer or equal
		 			if (LOG.isLoggable(Level.FINE)) {LOG.fine("Comparing paths: " + insertPath + " vs " + selectionPath + " vs " + transferPath); }
		            for (int i = 0; i < selectionPath.getPathCount(); i++) {
		            	if (LOG.isLoggable(Level.FINER)) {LOG.finer("Checking node path: " + (insertPath.getPath())[i] + " vs " + (selectionPath.getPath())[i]); }
			            if (!(transferPath.getPathComponent(i).toString().equals(selectionPath.getPathComponent(i).toString()))) {
			            	if (LOG.isLoggable(Level.FINE)) {LOG.fine("Paths are NOT identical (transfer != selection): allowing move"); }
			        		break;
			        	}
			            if (!(insertPath.getPathComponent(i).equals(selectionPath.getPathComponent(i)))) {
			            	if (LOG.isLoggable(Level.FINE)) {LOG.fine("Paths are NOT identical (insert != selection): allowing move"); }
			        		break;
			        	}
			        	if (i == selectionPath.getPathCount()-1) {
			        		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Paths are identical: disallowing move"); }
			        		return false;
			        	}
			        }
		 		}
		 		tree.getGroupTable().getGameMaster().startCompoundEdit();
			}
       
			if (LOG.isLoggable(Level.FINE)) {LOG.fine("Inserting node " + transferPath.toString() + " @ " + insertIndex); }
			// Detect whether to insert at end, or in the middle of the list
	 		if (insertIndex >= 0) {
	 			tree.insertNode(transferNode, parentNode, insertIndex);
			}
			else {
				tree.insertNode(transferNode, parentNode, parentNode.getChildCount());
			}
	 		if (action == MOVE)
	 			tree.setSelectionPath(new TreePath(transferNode.getPath())); // Select the newly moved group
 		}
 		else { // Get actors 
	    	if (!parentNode.isGroup()) { 
	    		if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Attempting to drop rows on folder!");} 
	    		return false; 
	    	}
			Actor[] actorRows;
			if (LOG.isLoggable(Level.FINE)) {LOG.fine("Retrieving actor data..."); }
			try {
	       		actorRows = (Actor[]) t.getTransferData(InitTableTransferHandler.initTableActorFlavor); // Don't really care which flavor it is
	        } catch (UnsupportedFlavorException e) {
	        	if (LOG.isLoggable(Level.WARNING)) {LOG.log(Level.WARNING, "Unsupported Flavor Exception: " + e.getMessage(), e);}
	        	return false;
	        } catch (IOException e) {
	        	if (LOG.isLoggable(Level.WARNING)) {LOG.log(Level.WARNING, "IOException: " + e.getMessage(), e);}
	        	return false;
	        }
			
			if (LOG.isLoggable(Level.FINE)) {LOG.fine("Transferable data retrieved."); }
			InitTableModel model = parent.getGroupTable().getActorTableModel();
	        ArrayList<Actor> actorList = parentNode.getActorList();
        	GameMaster gameMaster = parent.getGroupTable().getGameMaster();
        	gameMaster.startCompoundEdit();
	        for (int i = 0; i < actorRows.length; i++) { // Actors added top down
	        	if (LOG.isLoggable(Level.FINE)) {LOG.fine("Adding actor # " + i); }
	        	model.addActorToList(actorList, actorRows[i], actorList.size()-1);
	        }
        	gameMaster.endCompoundEdit("Copy");
        	tree.setDirty();
 		}
 		
 		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Done"); }
        return true;
	}
		
	@Override
	public int getSourceActions(JComponent c) {
		return COPY | MOVE;
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		GroupTree tree = (GroupTree) c;
		TreePath transferPath = tree.getSelectionPath();
	    if (transferPath == null) { //There is no selection.     
	        return null;
	    } else {
			return new TransferableActorGroup((GroupTreeNode) transferPath.getLastPathComponent());
	    }
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Starting action " + action); }
    	GroupTree tree = (GroupTree) source;
    	GroupTreeNode transferNode;
    	try {
    		transferNode = (GroupTreeNode) data.getTransferData(actorGroupFlavor);
    	} catch (UnsupportedFlavorException e) {
        	if (LOG.isLoggable(Level.WARNING)) {LOG.log(Level.WARNING, "UnsupportedFlavorException: " + e.getMessage(), e);}
        	return;
        } catch (IOException e) {
        	if (LOG.isLoggable(Level.WARNING)) {LOG.log(Level.WARNING, "IOException: " + e.getMessage(), e);}
        	return;
        }
    	
        TreePath transferPath = new TreePath(transferNode.getPath());
 		if (action == MOVE) {
 			if (LOG.isLoggable(Level.FINE)) {LOG.fine("Removing path that was transfered: " + transferPath.toString()); }
	        tree.removeNode(transferNode);
	 		tree.getGroupTable().getGameMaster().endCompoundEdit("Move");
        }
 		else if (action == COPY) {
 			tree.setSelectionPath(transferPath); // Select the previously copied node
 		}
 		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Done"); }
	}
	
	class TransferableActorGroup implements Transferable {
		
		GroupTreeNode transferNode;
		
		public TransferableActorGroup(GroupTreeNode transferNode) { this.transferNode = transferNode; }
		  
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
		    if (flavor.equals(actorGroupFlavor)) {
		    	if (LOG.isLoggable(Level.FINE)) {LOG.fine("Exporting data to " + flavor + " flavor, path: " + (new TreePath(transferNode.getPath())).toString()); }
		    	return transferNode;
		    }
		    else throw new UnsupportedFlavorException(flavor);
		  }

		/** Check whether a specified DataFlavor is available */
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if (flavor.equals(actorGroupFlavor)) return true;
		    return false;
		}
	}
}
