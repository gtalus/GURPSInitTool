package gurpsinittool.app;

import gurpsinittool.test.RandomData;

import javax.swing.tree.DefaultMutableTreeNode;

public class GroupTreeNode extends DefaultMutableTreeNode {

	private static final boolean DEBUG = true;
	
	private boolean isFolder;
	private ActorTableModel tableModel;
	
	public GroupTreeNode(String name, boolean isFolder) {
		super(name, isFolder);
		this.isFolder = isFolder;
		if (!isFolder) {
			tableModel = new ActorTableModel();
			RandomData.RandomActors(tableModel);
		}
	}
	
	@Override
	public boolean isLeaf() {
	    return !isFolder;
	}
	
	/**
	 * Get the ActorTableModel associated with the node. Returns null if none exists.
	 */
	public ActorTableModel getActorModel() {
		if (!isFolder) { return tableModel; }
		else return null;
	}
	
	/**
	 * Accessor method for isFolder boolean
	 * @return Whether this node is a folder.
	 */
	public boolean isFolder() {
		return isFolder;
	}

}
