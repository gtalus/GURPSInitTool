package gurpsinittool.app;

import gurpsinittool.ui.ActorDetailsPanel;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.DropMode;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class GroupTree extends JTree 
	implements ActionListener, DragSourceListener{

	private static final boolean DEBUG = true;
	
	private DefaultMutableTreeNode rootNode;
	private DefaultTreeModel treeModel;
	
	private InitTable groupTable;
	private ActorDetailsPanel actorPanel;
	private JPopupMenu popupMenu;
	
	public GroupTree(InitTable groupTable, ActorDetailsPanel actorPanel) {
		super(new DefaultTreeModel(new GroupTreeNode("Groups",true)));
		
		this.treeModel = (DefaultTreeModel) super.treeModel;
		this.rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
		this.groupTable = groupTable;
		this.actorPanel = actorPanel;
		
		// Add some default nodes
		GroupTreeNode PC_group = new GroupTreeNode("PC Groups",true);
		GroupTreeNode NPC_group = new GroupTreeNode("NPC Groups",true);
		GroupTreeNode Monster_group = new GroupTreeNode("Monster Groups",true);
		PC_group.add(new GroupTreeNode("Default",false));
		PC_group.add(new GroupTreeNode("Adventure 1",false));
		PC_group.add(new GroupTreeNode("Group 2",false));
		PC_group.add(new GroupTreeNode("I don't know",false));
		NPC_group.add(new GroupTreeNode("Town Guard",false));
		NPC_group.add(new GroupTreeNode("Nobles",false));
		NPC_group.add(new GroupTreeNode("Scum",false));
		NPC_group.add(new GroupTreeNode("Pirates",false));
		NPC_group.add(new GroupTreeNode("Ninjas",false));
		Monster_group.add(new GroupTreeNode("Goblins",false));
		Monster_group.add(new GroupTreeNode("Dragons",false));
		Monster_group.add(new GroupTreeNode("Orcs",false));
		Monster_group.add(new GroupTreeNode("Encounter 1",false));
		Monster_group.add(new GroupTreeNode("Encounter 2",false));
		Monster_group.add(new GroupTreeNode("Ogres",false));
		rootNode.add(PC_group);
		rootNode.add(NPC_group);
		rootNode.add(Monster_group);
		treeModel.nodeStructureChanged(rootNode); // Very important. Issues with isLeaf returning true for 0 children nodes if this is not called.
		addGroup("test");

		// Tree settings
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
        MousePopupListener popupListener = new MousePopupListener();
        addMouseListener(popupListener);
	}
	
	public void actionPerformed(ActionEvent e) {
    	if (DEBUG) { System.out.println("Received action command " + e.getActionCommand()); }
    	if ("New Folder".equals(e.getActionCommand())) { // Add folder
			DefaultMutableTreeNode newFolder = addFolder("New Folder...");
			TreePath folderPath = new TreePath(newFolder.getPath());
			selectionModel.setSelectionPath(folderPath);
	    	if (DEBUG) { System.out.println("Added new node. User object " + newFolder.getUserObject().getClass()); }
	    	startEditingAtPath(folderPath);	
    	}
		else if ("New Group".equals(e.getActionCommand())) { // Add group
			DefaultMutableTreeNode newGroup = addGroup("New Group...");
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
	public GroupTreeNode addGroup(String name) {
		GroupTreeNode newGroup = new GroupTreeNode(name,false);
	    insertObjectAtSelection(newGroup);
	    scrollPathToVisible(new TreePath(newGroup.getPath()));
	    
	    return newGroup;
	}
	
	/**
	 * Add a folder to the tree. Current selection is used as parent, or the root node.
	 * @param name : The name of the new folder
	 * @return The newly created folder
	 */
	public GroupTreeNode addFolder(String name) {
		GroupTreeNode newFolder = new GroupTreeNode(name,true);
	    insertObjectAtSelection(newFolder);
	    scrollPathToVisible(new TreePath(newFolder.getPath()));
	 
	    return newFolder;
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
    private void insertObjectAtSelection(GroupTreeNode child) {
    	//TreePath insertPath = new TreePath();

    	TreePath selectionPath = getSelectionPath();
    	if (selectionPath == null) {
	        //There is no selection. Insert after the last child of the root node
        	if (DEBUG) { System.out.println("Inserting child at root node " + rootNode.getChildCount()); }
    		treeModel.insertNodeInto(child, rootNode, rootNode.getChildCount());
	    } 
    	else {
    		GroupTreeNode node = (GroupTreeNode) selectionPath.getLastPathComponent();
        	if (DEBUG) { System.out.println("Inserting child. Selection is node: " + node.toString()); }
   		
    		if (node.isFolder()) { // insert after last child node
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
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
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

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dragEnter(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub
   		if (DEBUG) { System.out.println("Drag has entered the tree..."); }

		Transferable t = dsde.getDragSourceContext().getTransferable();
		if (t.isDataFlavorSupported(GroupTreeTransferHandler.actorGroupFlavor)) 
			setDropMode(DropMode.INSERT);
		else if ( t.isDataFlavorSupported(InitTableTransferHandler.initTableActorFlavor)
				|| t.isDataFlavorSupported(InitTableTransferHandler.groupTableActorFlavor)){
			setDropMode(DropMode.ON);
		}
	}

	@Override
	public void dragExit(DragSourceEvent dse) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dragOver(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub
	}
}
