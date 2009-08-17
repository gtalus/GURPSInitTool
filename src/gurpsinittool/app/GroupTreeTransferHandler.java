package gurpsinittool.app;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

public class GroupTreeTransferHandler extends TransferHandler {

	/**
	 * Default serialization UID
	 */
	private static final long serialVersionUID = 1L;
	
	private static final boolean DEBUG = true;

	protected static DataFlavor actorGroupFlavor = new DataFlavor(String.class, "GURPS Actor Group Object");
	
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
        DefaultMutableTreeNode transferNode;
        try {
        	transferNode = (DefaultMutableTreeNode) t.getTransferData(actorGroupFlavor);
        } catch (UnsupportedFlavorException e) {
    		if (DEBUG) { System.out.println("Unsupported Flavor Exception"); }
        	return false;
        } catch (IOException e) {
    		if (DEBUG) { System.out.println("IO Exception"); }
        	return false;
        }
		if (DEBUG) { System.out.println("Inserting node " + transferNode.toString() + " @ " + insertIndex); }
        // parentNode.add(transferNode);
        treeModel.insertNodeInto(transferNode, parentNode, insertIndex);
        
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
			DefaultMutableTreeNode transferNode = (DefaultMutableTreeNode) (transferPath.getLastPathComponent());
			return new TransferableActorGroup(transferNode);
	    }
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if (DEBUG) { System.out.println("export done: " + action); }
 		if (action == MOVE) {
        	GroupTree tree = (GroupTree) source;
        	DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        	DefaultMutableTreeNode transferNode;
        	try {
        		transferNode = (DefaultMutableTreeNode) data.getTransferData(actorGroupFlavor);
        	} catch (UnsupportedFlavorException e) {
	        	return;
	        } catch (IOException e) {
	        	return;
	        }
    		if (DEBUG) { System.out.println(" Removing node that was transfered: " + transferNode.toString()); }
	        treeModel.removeNodeFromParent(transferNode);
        }
	}
	
	class TransferableActorGroup implements Transferable {
		
		DefaultMutableTreeNode transferNode;
		
		public TransferableActorGroup(DefaultMutableTreeNode transferNode) { this.transferNode = transferNode; }
		  
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
		    if (flavor.equals(actorGroupFlavor)) return transferNode;
		    else throw new UnsupportedFlavorException(flavor);
		  }

		/** Check whether a specified DataFlavor is available */
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if (flavor.equals(actorGroupFlavor)) return true;
		    return false;
		}
	}
}
