package gurpsinittool.app;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

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

	protected static DataFlavor actorGroupFlavor = new DataFlavor(GroupTreeNode.class, "GURPS Actor Group Object");
	
	protected static DataFlavor[] supportedFlavors = {
		actorGroupFlavor
	};

	public GroupTreeTransferHandler(String property){
        super(property);
    }

	@Override
	public boolean canImport(TransferSupport support) {
		
		if (!support.isDataFlavorSupported(actorGroupFlavor))
			return false;
		
		return true;
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
		tree.clearSelection(); // Make sure that no one keeps a reference to the old node
		TreePath transferPath;
        try {
        	transferPath = (TreePath) t.getTransferData(actorGroupFlavor);
        } catch (UnsupportedFlavorException e) {
    		if (DEBUG) { System.out.println("-E- Unsupported Flavor Exception"); }
        	return false;
        } catch (IOException e) {
    		if (DEBUG) { System.out.println("-E- IO Exception"); }
        	return false;
        }

 		DefaultMutableTreeNode transferNode = (DefaultMutableTreeNode) transferPath.getLastPathComponent();
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
		tree.setSelectionPath(new TreePath(transferNode.getPath())); // Select the newly inserted group
		
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
			return new TransferableActorGroup(transferPath);
	    }
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if (DEBUG) { System.out.println("export done: " + action); }
    	GroupTree tree = (GroupTree) source;
    	DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
    	TreePath transferPath;
    	try {
    		transferPath = (TreePath) data.getTransferData(actorGroupFlavor);
    	} catch (UnsupportedFlavorException e) {
        	return;
        } catch (IOException e) {
        	return;
        }
 		if (action == MOVE) {
    		if (DEBUG) { System.out.println(" Removing path that was transfered: " + transferPath.toString()); }
    		DefaultMutableTreeNode transferNode = (DefaultMutableTreeNode) transferPath.getLastPathComponent();
	        treeModel.removeNodeFromParent(transferNode);
        }
 		else if (action == COPY) {
 			tree.setSelectionPath(transferPath); // Select the previously copied node
 		}
	}
	
	class TransferableActorGroup implements Transferable {
		
		TreePath transferPath;
		
		public TransferableActorGroup(TreePath transferPath) { this.transferPath = transferPath; }
		  
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
		   		//if (DEBUG) { System.out.println(" Transfering path: " + transferPath.toString()); }
		    	//GroupTreeNode node = (GroupTreeNode) transferPath.getLastPathComponent();
		    	//node.getActorModel().rem
		    	return transferPath;
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
