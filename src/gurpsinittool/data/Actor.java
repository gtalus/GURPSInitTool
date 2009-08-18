package gurpsinittool.data;

import java.io.Serializable;

/** 
 * Encapsulates a single actor in the current encounter.
 * Contains all status information.
 *
 */
public class Actor 
	implements Serializable {

	public String Name;
	//public int Order; // what is this?
	
	public enum ActorState {Active, Waiting, Disabled, Unconscious, Dead};
	public ActorState State;

	public enum ActorType {PC, Enemy, Ally, Neutral, Special};
	public ActorType Type;

	public String Target; /* maybe this should be a pointer? */
	
	//public Boolean Active; // Is this actor the current active actor
	
	public int HP; // total Hit points
	public int Health; // Health (for saving rolls)
	public int Damage; // Amount of damage taken
	
	//public int FP; // probably not useful
	//public int MP; // probably not useful, but more likely than FP
	//public int SP; // shock points, probably don't need 
	
	/* also need extra data collection*/
	
	/* also need attacks info */

	/**
	 * Basic constructor specifying all options
	 */
	public Actor(String name, ActorState state, ActorType type, int hp, int health, int damage)
	{
		Name = name;
		State = state;
		Type = type;
		HP = hp;
		Health = health;
		Damage = damage;
		//Active = false;
	}
	
	/**
	 * Basic constructor providing default options for hp, health and damage
	 * @param name : Actor's name
	 * @param state : Actor's state (Active, Dead, etc)
	 * @param type : Actor type (PC, NPC, etc)
	 */
	public Actor(String name, ActorState state, ActorType type) {
		this(name,state,type,10,10,0);
	}
	
	/**
	 * Copy constructor
	 * @param anActor reference Actor
	 */
	public Actor(Actor anActor) {
		this(anActor.Name,anActor.State,anActor.Type,anActor.HP,anActor.Health,anActor.Damage);
	}


	
}
