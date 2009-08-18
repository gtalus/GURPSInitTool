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
        
		if (DEBUG) { System.out.println("Retrieving node data..."); }
		tree.startDrop(); // Make sure that no one keeps a reference to the old node
		TreePath transferPath;
        try {
        	transferPath = (TreePath) t.getTransferData(actorGroupFlavor);
        } catch (UnsupportedFlavorException e) {
    		if (DEBUG) { System.out.println("Unsupported Flavor Exception"); }
        	return false;
        } catch (IOException e) {
    		if (DEBUG) { System.out.println("IO Exception"); }
        	return false;
        }
		if (DEBUG) { System.out.println("Inserting node " + transferPath.toString() + " @ " + insertIndex); }
        // parentNode.add(transferNode);
		DefaultMutableTreeNode transferNode = (DefaultMutableTreeNode) transferPath.getLastPathComponent();
		if (insertIndex >= 0) {
			treeModel.insertNodeInto(transferNode, parentNode, insertIndex);
		}
		else {
			treeModel.insertNodeInto(transferNode, parentNode, parentNode.getChildCount());
		}
		tree.endDrop(); // Re-load table selection
		
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
 		if (action == MOVE) {
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
    		if (DEBUG) { System.out.println(" Removing path that was transfered: " + transferPath.toString()); }
    		DefaultMutableTreeNode transferNode = (DefaultMutableTreeNode) transferPath.getLastPathComponent();
	        treeModel.removeNodeFromParent(transferNode);
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
		   		if (DEBUG) { System.out.println(" Transfering path: " + transferPath.toString()); }		    	
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
