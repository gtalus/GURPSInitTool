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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class GroupTreeTransferHandler extends TransferHandler {

	/**
	 * Default serialization UID
	 */
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
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) (insertPath.getLastPathComponent());
        Transferable t = support.getTransferable();
 		int action = support.getDropAction();
       
		if (DEBUG) { System.out.println("Retrieving node data..."); }
		//tree.clearSelection(); // Make sure that no one keeps a reference to the old node
		GroupTreeNode transferNode;
		try {
			transferNode = (GroupTreeNode) t.getTransferData(actorGroupFlavor);
        } catch (UnsupportedFlavorException e) {
    		if (DEBUG) { System.out.println("-E- Unsupported Flavor Exception"); }
        	return false;
        } catch (IOException e) {
    		if (DEBUG) { System.out.println("-E- IO Exception"); }
        	return false;
        }
		TreePath transferPath = new TreePath(transferNode.getPath());

        // Check whether the destination is within the source, and disallow for move (will cause entire tree to be deleted)
 		if (action == MOVE && insertPath.getPathCount() >= transferPath.getPathCount()) { // Insert path must be longer or equal
 	        if (DEBUG) { System.out.println("Comparing paths: " + insertPath + " vs " + transferPath); }
	        for (int i = 0; i < transferPath.getPathCount(); i++) {
	            if (DEBUG) { System.out.println("Checking node path: " + (insertPath.getPath())[i] + " vs " + (transferPath.getPath())[i]); }
	        	if (!(insertPath.getPath())[i].toString().equals((transferPath.getPath())[i].toString())) {
		            if (DEBUG) { System.out.println(" Paths are NOT identical: allowing move"); }
	        		break;
	        	}
	        	if (i == transferPath.getPathCount()-1) {
		            if (DEBUG) { System.out.println(" Paths are identical: disallowing move"); }
	        		return false;
	        	}
	        }
 		}
        
        if (DEBUG) { System.out.println("Inserting node " + transferPath.toString() + " @ " + insertIndex); }
		// Detect whether to insert at end, or in the middle of the list
 		if (insertIndex >= 0) {
			treeModel.insertNodeInto(transferNode, parentNode, insertIndex);
		}
		else {
			treeModel.insertNodeInto(transferNode, parentNode, parentNode.getChildCount());
		}
 		if (action == MOVE)
 			tree.setSelectionPath(new TreePath(transferNode.getPath())); // Select the newly moved group
		
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
		if (DEBUG) { System.out.println("export done: " + action); }
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
    		if (DEBUG) { System.out.println(" Removing path that was transfered: " + transferPath.toString()); }
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
		   		if (DEBUG) { System.out.println(" Exporting data to " + flavor + " flavor, path: " + (new TreePath(transferNode.getPath())).toString()); }
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
