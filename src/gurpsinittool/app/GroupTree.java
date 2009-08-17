package gurpsinittool.app;

import gurpsinittool.app.InitTable.MousePopupListener;
import gurpsinittool.data.Actor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class GroupTree extends JTree 
	implements ActionListener {

	private static final boolean DEBUG = true;
	
	private DefaultMutableTreeNode rootNode;
	private DefaultTreeModel treeModel;
	
	private InitTable initTable;
	private JPopupMenu popupMenu;
	
	public GroupTree(InitTable initTable) {
		super(new DefaultTreeModel(new DefaultMutableTreeNode("Groups")));
		
		this.treeModel = (DefaultTreeModel) super.treeModel;
		this.rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
		this.initTable = initTable;
		
		// Add some default nodes
		DefaultMutableTreeNode PC_group = new DefaultMutableTreeNode("PC Groups");
		DefaultMutableTreeNode NPC_group = new DefaultMutableTreeNode("NPC Groups");
		DefaultMutableTreeNode Monster_group = new DefaultMutableTreeNode("Monster Groups");
		PC_group.add(new DefaultMutableTreeNode("Default"));
		PC_group.add(new DefaultMutableTreeNode("Adventure 1"));
		PC_group.add(new DefaultMutableTreeNode("Group 2"));
		PC_group.add(new DefaultMutableTreeNode("I don't know"));
		NPC_group.add(new DefaultMutableTreeNode("Town Guard"));
		NPC_group.add(new DefaultMutableTreeNode("Nobles"));
		NPC_group.add(new DefaultMutableTreeNode("Scum"));
		NPC_group.add(new DefaultMutableTreeNode("Pirates"));
		NPC_group.add(new DefaultMutableTreeNode("Ninjas"));
		Monster_group.add(new DefaultMutableTreeNode("Goblins"));
		Monster_group.add(new DefaultMutableTreeNode("Dragons"));
		Monster_group.add(new DefaultMutableTreeNode("Orcs"));
		Monster_group.add(new DefaultMutableTreeNode("Encounter 1"));
		Monster_group.add(new DefaultMutableTreeNode("Encounter 2"));
		Monster_group.add(new DefaultMutableTreeNode("Ogres"));
		rootNode.add(PC_group);
		rootNode.add(NPC_group);
		rootNode.add(Monster_group);
		addObject(new DefaultMutableTreeNode("test"));
		
		// Tree settings
		setEditable(true);
		setRootVisible(false);
		setShowsRootHandles(true);
		setTransferHandler(new GroupTreeTransferHandler("name"));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setDragEnabled(true);
        setDropMode(DropMode.INSERT);
        
		// Table popup menu
        popupMenu = new JPopupMenu();
        popupMenu.add(createMenuItem("New Folder", KeyEvent.VK_F));
        popupMenu.add(createMenuItem("New Group", KeyEvent.VK_G));
        popupMenu.add(createMenuItem("Delete", KeyEvent.VK_DELETE));
        MousePopupListener popupListener = new MousePopupListener();
        addMouseListener(popupListener);
	}
	
	public void actionPerformed(ActionEvent e) {
    	if (DEBUG) { System.out.println("Received action command " + e.getActionCommand()); }
    	if ("New Folder".equals(e.getActionCommand())) { // Add folder
	
    	}
		else if ("New Group".equals(e.getActionCommand())) { // Add group
			DefaultMutableTreeNode newGroup = addObject("New Group...");
			TreePath groupPath = new TreePath(newGroup.getPath());
			selectionModel.setSelectionPath(groupPath);
	    	if (DEBUG) { System.out.println("Added new node. User object " + newGroup.getUserObject().getClass()); }
	    	startEditingAtPath(groupPath);
		}
		else if ("Delete".equals(e.getActionCommand())) { // Delete selected rows
    		int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this group?", "Confirm Group Delete", JOptionPane.OK_CANCEL_OPTION);
    		if (result == JOptionPane.OK_OPTION) {
    			removeCurrentNode();
    		}
		}
	}
	  
	/**
	 * Add a group to the tree. Current selection is used as parent, or the root node.
	 * @param name : The name of the new group
	 * @return The newly created group
	 */
	public GroupTreeGroup addGroup(String name) {
	    GroupTreeGroup newGroup = new GroupTreeGroup(name);
	    insertObjectAtSelection(newGroup);
	    scrollPathToVisible(new TreePath(newGroup.getPath()));
	    
	    return newGroup;
	}
	
	/**
	 * Add a folder to the tree. Current selection is used as parent, or the root node.
	 * @param name : The name of the new folder
	 * @return The newly created folder
	 */
	public GroupTreeFolder addFolder(String name) {
	    GroupTreeFolder newFolder = new GroupTreeFolder(name);
	    insertObjectAtSelection(newFolder);
	    scrollPathToVisible(new TreePath(newFolder.getPath()));
	 
	    return newFolder;
	}

	/**
	 * Add an object to the tree. Current selection is used as parent, or the root node.
	 * @param child : The new object to be added
	 * @return The newly created tree node
	 */
	public DefaultMutableTreeNode addObject(Object child) {
	    DefaultMutableTreeNode parentNode = null;
	    TreePath parentPath = getSelectionPath();

	    if (parentPath == null) {
	        //There is no selection. Default to the root node.
	        parentNode = rootNode;
	    } else {
	        parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
	    }

	    return addObject(parentNode, child, true);
	}
	
	/**
	 * Add an object to the tree
	 * @param parent : Parent node of the new object
	 * @param child : The new object
	 * @param shouldBeVisible : Scroll to the added object
	 * @return The newly created TreeNode
	 */
	public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean shouldBeVisible) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
		((DefaultTreeModel) treeModel).insertNodeInto(childNode, parent, parent.getChildCount());
	
		//Make sure the user can see the lovely new node.
		if (shouldBeVisible) {
			scrollPathToVisible(new TreePath(childNode.getPath()));
		}
		return childNode;
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
    
    /**
     * Determine where a new node should be placed, based on the current selection
     * @return The TreePath where the new component should be inserted
     */
    private void insertObjectAtSelection(MutableTreeNode child) {
    	//TreePath insertPath = new TreePath();

    	TreePath selectionPath = getSelectionPath();
    	if (selectionPath == null) {
	        //There is no selection. Insert after the last child of the root node
        	if (DEBUG) { System.out.println("Inserting child at root node " + rootNode.getChildCount()); }
    		treeModel.insertNodeInto(child, rootNode, rootNode.getChildCount());
	    } 
    	else {
    		Object node = selectionPath.getLastPathComponent();
        	if (DEBUG) { System.out.println("Inserting child. Selection is node: " + node.getClass().toString()); }
   		
    		if (node.getClass().equals(GroupTreeFolder.class)) { // insert after last child node
    			GroupTreeFolder parentNode = (GroupTreeFolder) node;
    			treeModel.insertNodeInto(child, parentNode, parentNode.getChildCount());
    		}
    		else { // Insert after current selection
    			MutableTreeNode parentNode = (MutableTreeNode) selectionPath.getParentPath().getLastPathComponent();
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
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
        } 
        // Either there was no selection, or the root was selected.
        //toolkit.beep();
    }

    /**
	 * Inner class to encapsulate group folders as a tree node
	 * @author dsmall
	 *
	 */
	class GroupTreeFolder extends DefaultMutableTreeNode {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -1099896752894307302L;

		/**
		 * Constructor for GroupTreeGroup
		 * @param name : Name of group
		 * @param initTable : InitTable which holds group Actors
		 */
		public GroupTreeFolder(String name) {
			super(name);
		}
		public GroupTreeFolder(GroupTreeFolder copy) {
			super(copy);
		}
	}
	
	/**
	 * Inner class to encapsulate groups as a tree node
	 * @author dsmall
	 *
	 */
	class GroupTreeGroup extends DefaultMutableTreeNode {

		private InitTable initTable;
		
		/**
		 * Basic Constructor for GroupTreeGroup
		 * @param name : Name of group
		 */
		public GroupTreeGroup(String name) {
			super(name, false);
			this.initTable = new InitTable();
		}

		/**
		 * Constructor for GroupTreeGroup which specifies InitTable to use
		 * @param name : Name of group
		 * @param initTable : InitTable which holds group Actors
		 */
		public GroupTreeGroup(String name, InitTable initTable) {
			super(name, false);
			this.initTable = initTable;
		}
	}
	
	/**
	 * An inner class to check whether mouse events are the pop-up trigger
	 */
	class MousePopupListener extends MouseAdapter {
	    	
	    public void mousePressed(MouseEvent e) { checkPopup(e); }
	    public void mouseClicked(MouseEvent e) { checkPopup(e); }
	    public void mouseReleased(MouseEvent e) { checkPopup(e); }
	 
	    private void checkPopup(MouseEvent e) {
	    	if (e.isPopupTrigger()) {
	   			popupMenu.show(e.getComponent(), e.getX(), e.getY());
	        }
	    }
	}
}
