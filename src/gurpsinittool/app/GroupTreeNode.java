package gurpsinittool.app;

import java.util.ArrayList;
import java.util.Arrays;

import gurpsinittool.data.Actor;
import javax.swing.tree.DefaultMutableTreeNode;

public class GroupTreeNode extends DefaultMutableTreeNode {

	// Default SVUID
	private static final long serialVersionUID = 1L;
	
	private boolean isGroup;
	private ArrayList<Actor> actorList;
	
	// isGroup is equivalent to allowsChildren
	public GroupTreeNode(String name, boolean isGroup) {
		super(name, !isGroup);
		this.isGroup = isGroup;
		if (isGroup) {
			actorList = new ArrayList<Actor>(Arrays.asList(new Actor()));
		}
	}
	
	public boolean isGroup() {
	    return isGroup;
	}
	
	// Java has confused the concept of leaf and allowsChildren: some code 
	// seems to think those two should be the same, some thinks it should not.
	// Making it the same is least bad it seems.
	@Override
	public boolean isLeaf() {
	    return isGroup;
	}
	
	/**
	 * Get the ArrayList<Actor> associated with the node. Returns null if none exists.
	 */
	public ArrayList<Actor> getActorList() {
		if (isGroup) { return actorList; }
		else return null;
	}
}
