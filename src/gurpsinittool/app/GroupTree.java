package gurpsinittool.app;

import gurpsinittool.util.CleanFileChangeEventSource;
import gurpsinittool.util.FileChangeEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DropMode;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEditSupport;

@SuppressWarnings("serial")
public class GroupTree extends JTree implements ActionListener {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(GroupTree.class.getName());
	
	private UndoableEditSupport mUes = new UndoableEditSupport();
	protected CleanFileChangeEventSource mCfces = new CleanFileChangeEventSource(this);
	
	private DefaultMutableTreeNode rootNode;
	private DefaultTreeModel treeModel;
	
	private InitTable groupTable;
	//private ActorDetailsPanel actorPanel;
	private JPopupMenu popupMenu;
	
	public GroupTree(InitTable groupTable) {
		super();
		setNewModel();
		this.groupTable = groupTable;
		
		treeModel.nodeStructureChanged(rootNode); // Very important. Issues with isLeaf returning true for 0 children nodes if this is not called.

		// Tree settings
		setInvokesStopCellEditing(true); // Call StopCellEditing by default instead of Cancel
		setEditable(true);
		setRootVisible(false);
		setShowsRootHandles(true);
		setTransferHandler(new GroupTreeTransferHandler(this, "name"));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setDragEnabled(true);
        setDropMode(DropMode.INSERT);
        //setDropMode(DropMode.ON_OR_INSERT);
        
		// Table popup menu
        popupMenu = new JPopupMenu();
        popupMenu.add(createMenuItem("New Folder", KeyEvent.VK_F));
        popupMenu.add(createMenuItem("New Group", KeyEvent.VK_G));
        popupMenu.add(createMenuItem("Delete", KeyEvent.VK_DELETE));
        popupMenu.add(createMenuItem("Rename", KeyEvent.VK_R));
        MousePopupListener popupListener = new MousePopupListener();
        addMouseListener(popupListener);        
	}
	
    /**
     * Convenience method to create menu items for the table's menus.
     * @param text - Text of the menu item
     * @return
     */
    private JMenuItem createMenuItem(String text, int mnemonic) {
    	JMenuItem menuItem = new JMenuItem(text, mnemonic);
    	menuItem.addActionListener(this);
    	return menuItem;
    }
	
	public void actionPerformed(ActionEvent e) {
		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Received action command " + e.getActionCommand()); }
    	if ("New Folder".equals(e.getActionCommand())) { // Add folder
			DefaultMutableTreeNode newFolder = addNode("New Folder...", false);
			TreePath folderPath = new TreePath(newFolder.getPath());
			selectionModel.setSelectionPath(folderPath);
			if (LOG.isLoggable(Level.FINE)) {LOG.fine("Added new node. User object " + newFolder.getUserObject().getClass()); }
	    	startEditingAtPath(folderPath);	
    	}
		else if ("New Group".equals(e.getActionCommand())) { // Add group
			DefaultMutableTreeNode newGroup = addNode("New Group...", true);
			TreePath groupPath = new TreePath(newGroup.getPath());
			selectionModel.setSelectionPath(groupPath);
			if (LOG.isLoggable(Level.FINE)) {LOG.fine("Added new node. User object " + newGroup.getUserObject().getClass()); }
	    	startEditingAtPath(groupPath);
		}
		else if ("Delete".equals(e.getActionCommand())) { // Delete selected rows
			if (isSelectionEmpty()) return;
			
			GroupTreeNode currentNode = (GroupTreeNode) (getSelectionPath().getLastPathComponent());
			String nodeType = currentNode.getAllowsChildren()?"Folder":"Group";
    		int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this " + nodeType + "?", "Confirm " + nodeType + " Deletion", JOptionPane.OK_CANCEL_OPTION);
    		if (result == JOptionPane.OK_OPTION) {
    			removeCurrentNode();
    		}
		}
		else if ("Rename".equals(e.getActionCommand())) { // Delete selected rows
			if (isSelectionEmpty()) return;
			startEditingAtPath(getSelectionPath());		
		}
	}
	  

    public InitTable getGroupTable() {
    	return groupTable;
    }
    
	/**
	 * Add a group to the tree. Current selection is used as parent, or the root node.
	 * @param name : The name of the new group
	 * @return The newly created group
	 */
	public GroupTreeNode addNode(String name, boolean leaf) {
		GroupTreeNode newNode = new GroupTreeNode(name,leaf);
	    insertObjectAtSelection(newNode);
	    scrollPathToVisible(new TreePath(newNode.getPath()));
	    mUes.postEdit(new NodeEdit(newNode, true));
	    return newNode;
	}
	
	/**
	 * Add a group to the tree. Current selection is used as parent, or the root node.
	 * @param name : The name of the new group
	 * @return The newly created group
	 */
	public void insertNode(GroupTreeNode node, GroupTreeNode parentNode, int position) {
		treeModel.insertNodeInto(node, parentNode, position);
	    mUes.postEdit(new NodeEdit(node, true));
	}
    
    /**
     * Determine where a new node should be placed, based on the current selection
     * @return The TreePath where the new component should be inserted
     */
    private void insertObjectAtSelection(GroupTreeNode child) {
    	//TreePath insertPath = new TreePath();

    	TreePath selectionPath = getSelectionPath();
    	if (selectionPath == null) {
	        //There is no selection. Insert after the last child of the root node
    		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Inserting child at root node " + rootNode.getChildCount()); }
    		treeModel.insertNodeInto(child, rootNode, rootNode.getChildCount());
	    } 
    	else {
    		GroupTreeNode node = (GroupTreeNode) selectionPath.getLastPathComponent();
    		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Inserting child. Selection is node: " + node.toString()); }
   		
    		if (node.getAllowsChildren()) { // insert after last child node
    			treeModel.insertNodeInto(child, node, node.getChildCount());
    		}
    		else { // Insert after current selection
    			GroupTreeNode parentNode = (GroupTreeNode) node.getParent();
    			treeModel.insertNodeInto(child, parentNode, parentNode.getChildCount());
    		}
	    }
    }
    
    /**
     * Remove the currently selected node
     */
    public void removeCurrentNode() {
        TreePath currentSelection = getSelectionPath();
        if (currentSelection != null) {
            GroupTreeNode currentNode = (GroupTreeNode) (currentSelection.getLastPathComponent());
            removeNode(currentNode);
        }
    }
    
    /**
     * Remove the currently selected node
     */
    public void removeNode(GroupTreeNode node) {
    	MutableTreeNode parent = (MutableTreeNode)(node.getParent());
    	if (parent != null) {
    		mUes.postEdit(new NodeEdit(node, false));
    		treeModel.removeNodeFromParent(node);
    		setDirty();
    		return;
        } 
    }
     
    /**
     * Create a new, empty, GroupTreeModel and set that as the TreeModel 
     * used by this component
     * @return the newly created GroupTreeModel
     */
    public final GroupTreeModel setNewModel() {
    	GroupTreeModel model = new GroupTreeModel(new GroupTreeNode("Groups",false));
    	setModel(model);
    	return model;
    }
    
    /**
     * Set the TreeModel used by this component
     */
 	@Override
 	public final void setModel(final TreeModel newModel) {
 		if (treeModel != null && !GroupTreeModel.class.isInstance(newModel)) { // Allow if model is null (as in constructor)
 			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Model must be a GroupTreeModel!");}
 			//return;
 		}
 		super.setModel(newModel);
 		this.treeModel = (DefaultTreeModel) newModel;
 		this.rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
 		treeModel.addTreeModelListener(new GroupTreeModelListener());
 	}
	   
 	// Clean file change support
    /**
     * Method to get the clean status of the groupTree
     * @return whether the group tree has had changes since the last checkpoint
     */
    public boolean isClean() {
    	return mCfces.isClean();
    }    
    /**
     * Set the status of the groupTree as clean. Should be called after saving the file.
     */
    public void setClean() {
    	mCfces.setClean();
    }   
    /**
     * Set the status of the groupTree as dirty. Should be called after making any changes.
     */
    public void setDirty() {
    	mCfces.setDirty();
    }    
	/**
	 * Add an event listener for FileChangeEvents
	 * @param listener - the listener to add
	 */
	public void addFileChangeEventListener(FileChangeEventListener listener) {
		mCfces.addFileChangeEventListener(listener);
	}	
	/**
	 * Remove an event listener for FileChangeEvents
	 * @param listener - the listener to remove
	 */
	public void removeFileChangeEventListener(FileChangeEventListener listener) {
		mCfces.removeFileChangeEventListener(listener);
	}

	// Undoable Edit support
	public void addUndoableEditListener(UndoableEditListener listener) {
		mUes.addUndoableEditListener(listener);
	}
	public void removeUndoableEditListener(UndoableEditListener listener) {
		mUes.removeUndoableEditListener(listener);
	}
    private class NodeEdit extends AbstractUndoableEdit {
		private static final long serialVersionUID = 1L;
		private GroupTreeNode node;
    	private GroupTreeNode parent;
    	private int index;
    	boolean isInsert;
    	public NodeEdit(GroupTreeNode node, boolean isInsert) { // Insert
    		this.node = node;
    		parent = (GroupTreeNode) node.getParent();
    		index = parent.getIndex(node);
    		this.isInsert = isInsert;
    	}
    	public String getPresentationName() { return "Tree"; }    	
    	public void undo() {
			super.undo();
			if (isInsert) {
				treeModel.removeNodeFromParent(node);
				setDirty();
			} else {
				treeModel.insertNodeInto(node, parent, index);
				scrollPathToVisible(new TreePath(node.getPath()));
			}
    	}
    	public void redo() {
    		super.redo();
			if (isInsert) {
				treeModel.insertNodeInto(node, parent, index);
				scrollPathToVisible(new TreePath(node.getPath()));
			} else {
				treeModel.removeNodeFromParent(node);
				setDirty();
			}
    	}
    }
    private class NodeRenameEdit extends AbstractUndoableEdit {
		private static final long serialVersionUID = 1L;
		private GroupTreeNode node;
    	private String oldName;
    	private String newName;
    	public NodeRenameEdit(GroupTreeNode node, String oldName, String newName) {
    		this.node = node;
    		this.oldName = oldName;
    		this.newName = newName;
    	}
    	public String getPresentationName() { return "Rename"; }    	
    	public void undo() {
			super.undo();
			node.setUserObject(oldName);
			treeModel.nodeChanged(node);
    	}    	
    	public void redo() {
    		super.redo();
    		node.setUserObject(newName);
			treeModel.nodeChanged(node);
    	}        	
    }
    /**
     * An Inner class to monitor the treeModel changes
     */
    class GroupTreeModelListener implements TreeModelListener {

		@Override
		public void treeNodesChanged(TreeModelEvent evt) {
			if (LOG.isLoggable(Level.FINER)) {LOG.finer("treeNodesChanged."); }
			setDirty();
		}
		@Override
		public void treeNodesInserted(TreeModelEvent evt) {
			if (LOG.isLoggable(Level.FINER)) {LOG.finer("treeNodesInserted."); }
			setDirty();
		}
		@Override
		public void treeNodesRemoved(TreeModelEvent evt) {
			if (LOG.isLoggable(Level.FINER)) {LOG.finer("treeNodesRemoved."); }
			setDirty();
		}
		@Override
		public void treeStructureChanged(TreeModelEvent evt) {
			if (LOG.isLoggable(Level.FINER)) {LOG.finer("treeStructureChanged."); }
			//setDirty();
		}
    }
    
	/**
	 * An inner class to check whether mouse events are the pop-up trigger
	 */
	class MousePopupListener extends MouseAdapter {
	    	
	    @Override
		public void mousePressed(MouseEvent e) { checkPopup(e); }
	    @Override
		public void mouseClicked(MouseEvent e) { checkPopup(e); }
	    @Override
		public void mouseReleased(MouseEvent e) { checkPopup(e); }
	 
	    private void checkPopup(MouseEvent e) {
	    	if (e.isPopupTrigger()) {
	   			popupMenu.show(e.getComponent(), e.getX(), e.getY());
	        }
	    }
	}
	
	protected class GroupTreeModel extends DefaultTreeModel {

		public GroupTreeModel(TreeNode root) {
			super(root);
		}
		
		public void valueForPathChanged(TreePath path, Object userObj) {
			GroupTreeNode node = (GroupTreeNode) path.getLastPathComponent();
			String oldName = (String) node.getUserObject();
			String newName = (String) userObj;
			if (oldName.equals(newName))
				return;
			mUes.postEdit(new NodeRenameEdit(node, oldName, newName));
			super.valueForPathChanged(path, userObj);
		}
		
	}
}
