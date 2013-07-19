package gurpsinittool.app;

import gurpsinittool.data.Actor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class GroupTreeTransferHandler extends TransferHandler {

	// Default serialization UID
	private static final long serialVersionUID = 1L;
	
	private static final boolean DEBUG = true;
	
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
			if (!parentNode.isFolder()) { return true; }
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
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        TreePath insertPath = dl.getPath();
        int insertIndex = dl.getChildIndex();  
        GroupTreeNode parentNode = (GroupTreeNode) (insertPath.getLastPathComponent());
        Transferable t = support.getTransferable();
 		int action = support.getDropAction();
       
 		if (support.isDataFlavorSupported(actorGroupFlavor)) { // Get node
 			if (DEBUG) { System.out.println("GroupTreeTransferHandler: Retrieving node data..."); }
			//tree.clearSelection(); // Make sure that no one keeps a reference to the old node
			GroupTreeNode transferNode;
			try {
				transferNode = (GroupTreeNode) t.getTransferData(actorGroupFlavor);
	        } catch (UnsupportedFlavorException e) {
	    		if (DEBUG) { System.out.println("GroupTreeTransferHandler: -E- Unsupported Flavor Exception"); }
	        	return false;
	        } catch (IOException e) {
	    		if (DEBUG) { System.out.println("GroupTreeTransferHandler: -E- IO Exception"); }
	        	return false;
	        }
			TreePath transferPath = new TreePath(transferNode.getPath());

			if (action == MOVE && !tree.isSelectionEmpty()) {
				TreePath selectionPath = tree.getSelectionPath();
		        // Check whether the destination is within the source, and disallow for move (will cause entire tree to be deleted)
		 		if (transferPath.getPathCount() == selectionPath.getPathCount() &&
		 				insertPath.getPathCount() >= selectionPath.getPathCount()) { // Insert path must be longer or equal
		 	        if (DEBUG) { System.out.println("GroupTreeTransferHandler: Comparing paths: " + insertPath + " vs " + selectionPath + " vs " + transferPath); }
		            for (int i = 0; i < selectionPath.getPathCount(); i++) {
			            if (DEBUG) { System.out.println("GroupTreeTransferHandler: Checking node path: " + (insertPath.getPath())[i] + " vs " + (selectionPath.getPath())[i]); }
			            if (!(transferPath.getPathComponent(i).toString().equals(selectionPath.getPathComponent(i).toString()))) {
				            if (DEBUG) { System.out.println("GroupTreeTransferHandler: Paths are NOT identical (transfer != selection): allowing move"); }
			        		break;
			        	}
			            if (!(insertPath.getPathComponent(i).equals(selectionPath.getPathComponent(i)))) {
				            if (DEBUG) { System.out.println("GroupTreeTransferHandler: Paths are NOT identical (insert != selection): allowing move"); }
			        		break;
			        	}
			        	if (i == selectionPath.getPathCount()-1) {
				            if (DEBUG) { System.out.println("GroupTreeTransferHandler: Paths are identical: disallowing move"); }
			        		return false;
			        	}
			        }
		 		}
			}
       
			if (DEBUG) { System.out.println("GroupTreeTransferHandler: Inserting node " + transferPath.toString() + " @ " + insertIndex); }
			// Detect whether to insert at end, or in the middle of the list
	 		if (insertIndex >= 0) {
				treeModel.insertNodeInto(transferNode, parentNode, insertIndex);
			}
			else {
				treeModel.insertNodeInto(transferNode, parentNode, parentNode.getChildCount());
			}
	 		if (action == MOVE)
	 			tree.setSelectionPath(new TreePath(transferNode.getPath())); // Select the newly moved group
 		}
 		else { // Get actors 
	    	if (parentNode.isFolder()) { System.out.println("GroupTreeTransferHandler: -E- attempting to drop rows on folder!"); return false; }
			Actor[] actorRows;
 			if (DEBUG) { System.out.println("GroupTreeTransferHandler: Retrieving actor data..."); }
			try {
	       		actorRows = (Actor[]) t.getTransferData(InitTableTransferHandler.initTableActorFlavor); // Don't really care which flavor it is
	        } catch (UnsupportedFlavorException e) {
	    		if (DEBUG) { System.out.println("GroupTreeTransferHandler: -E- Unsupported Flavor Exception"); }
	        	return false;
	        } catch (IOException e) {
	    		if (DEBUG) { System.out.println("GroupTreeTransferHandler: -E- IO Exception"); }
	        	return false;
	        }
	    	if (DEBUG) { System.out.println("GroupTreeTransferHandler:  Transferable data retrieved."); }
	
	        ArrayList<Actor> actorList = parentNode.getActorList();
	        // Refresh current group table, since it might be selected
	        if (parent.getSelectionPath().getLastPathComponent().equals(parentNode)) { // If data is being displayed, add new Actors through the ActorTableModel
	        	if (DEBUG) { System.out.println("GroupTreeTransferHandler: Selected node is changing"); }
	        	InitTableModel model = (InitTableModel) parent.getGroupTable().getModel();
	        	for (int i = actorRows.length-1; i >= 0; i--) { // Actors added from bottom up, excluding new row
		        	if (DEBUG) { System.out.println("GroupTreeTransferHandler: Adding actor through ActorTableModel # " + i); }
		        	model.addActor(actorRows[i], model.getRowCount()-1);
		        }	        	
	        }
	        else {
		        for (int i = actorRows.length-1; i >= 0; i--) { // Actors added from bottom up, excluding new row
		        	if (DEBUG) { System.out.println("GroupTreeTransferHandler: Adding actor # " + i); }
		        	actorList.add(actorList.size()-1, actorRows[i]);
		        }
		        tree.setDirty(); // must set the tree as dirty, since we didn't add the actors through the regular interface.
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
		if (DEBUG) { System.out.println("GroupTreeTransferHandler: export done: " + action); }
    	GroupTree tree = (GroupTree) source;
    	DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
    	GroupTreeNode transferNode;
    	try {
    		transferNode = (GroupTreeNode) data.getTransferData(actorGroupFlavor);
    	} catch (UnsupportedFlavorException e) {
        	return;
        } catch (IOException e) {
        	return;
        }
        TreePath transferPath = new TreePath(transferNode.getPath());
 		if (action == MOVE) {
    		if (DEBUG) { System.out.println("GroupTreeTransferHandler:  Removing path that was transfered: " + transferPath.toString()); }
	        treeModel.removeNodeFromParent(transferNode);
        }
 		else if (action == COPY) {
 			tree.setSelectionPath(transferPath); // Select the previously copied node
 		}
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
		   		if (DEBUG) { System.out.println("GroupTreeTransferHandler:  Exporting data to " + flavor + " flavor, path: " + (new TreePath(transferNode.getPath())).toString()); }
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
