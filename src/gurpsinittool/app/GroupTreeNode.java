package gurpsinittool.app;

import java.util.ArrayList;
import java.util.Arrays;

import gurpsinittool.data.Actor;
import gurpsinittool.data.Actor.ActorState;
import gurpsinittool.data.Actor.ActorType;
import gurpsinittool.test.RandomData;

import javax.swing.tree.DefaultMutableTreeNode;

public class GroupTreeNode extends DefaultMutableTreeNode {
	
	private boolean isFolder;
	private ArrayList<Actor> actorList;
	
	public GroupTreeNode(String name, boolean isFolder) {
		super(name, isFolder);
		this.isFolder = isFolder;
		if (!isFolder) {
			actorList = new ArrayList<Actor>(Arrays.asList(new Actor("new...", ActorState.Active, ActorType.Enemy)));
			//RandomData.RandomActors(actorList);
		}
	}
	
	@Override
	public boolean isLeaf() {
	    return !isFolder;
	}
	
	/**
	 * Get the ArrayList<Actor> associated with the node. Returns null if none exists.
	 */
	public ArrayList<Actor> getActorList() {
		if (!isFolder) { return actorList; }
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
