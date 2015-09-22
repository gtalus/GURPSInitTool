package gurpsinittool.app;

import java.util.ArrayList;
import java.util.Arrays;

import gurpsinittool.data.Actor;
import javax.swing.tree.DefaultMutableTreeNode;

public class GroupTreeNode extends DefaultMutableTreeNode {

	// Default SVUID
	private static final long serialVersionUID = 1L;
	
	private boolean isLeaf;
	private ArrayList<Actor> actorList;
	
	public GroupTreeNode(String name, boolean isLeaf) {
		super(name, !isLeaf);
		this.isLeaf = isLeaf;
		if (isLeaf) {
			actorList = new ArrayList<Actor>(Arrays.asList(new Actor()));
		}
	}
	
	@Override
	public boolean isLeaf() {
	    return isLeaf;
	}
	
	/**
	 * Get the ArrayList<Actor> associated with the node. Returns null if none exists.
	 */
	public ArrayList<Actor> getActorList() {
		if (isLeaf) { return actorList; }
		else return null;
	}
}
