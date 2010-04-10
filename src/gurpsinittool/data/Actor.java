package gurpsinittool.data;

import java.io.Serializable;

/** 
 * Encapsulates a single actor in the current encounter.
 * Contains all status information.
 *
 */
public class Actor 
	implements Serializable {

	// Default SVUID
	private static final long serialVersionUID = 1L;

	public String Name;
	//public int Order; // what is this?
	
	public enum ActorState {Active, Waiting, Disabled, Unconscious, Dead};
	public ActorState State;

	public enum ActorType {PC, Enemy, Ally, Neutral, Special};
	public ActorType Type;

	public String Target; /* maybe this should be a pointer? */
	
	public String Notes; /* Free-form notes field */
	
	//public Boolean Active; // Is this actor the current active actor
	
	public int HT; // Health (for saving rolls)
	public int HP; // total Hit points
	public int Damage; // Amount of damage taken
	
	//public int Will; // Will/IQ
	//public int ST;
	public int FP; // what about MP?
	public int Fatigue; // Amount of FP damage taken
	public int Move;
	public int Dodge;
	//public float speed; // probably not useful
	//public int MP; // probably not useful, but more likely than FP
	//public int SP; // shock points, probably don't need 
	//public int DR; // Damage Resistance. Only useful if automating attacks.
	
	/* also need extra data collection*/
	
	/* also need attacks info */

	/**
	 * Basic constructor specifying all options
	 */
	public Actor(String name, ActorState state, ActorType type, int ht, int hp, int damage, int fp, int fatigue, int move, int dodge)
	{
		Name = name;
		State = state;
		Type = type;
		HP = hp;
		HT = ht;
		Damage = damage;
		FP = fp;
		Fatigue = fatigue;
		Move = move;
		Dodge = dodge;
		//Active = false;
	}
	
	/**
	 * Basic constructor providing default options for hp, health and damage
	 * @param name : Actor's name
	 * @param state : Actor's state (Active, Dead, etc)
	 * @param type : Actor type (PC, NPC, etc)
	 */
	public Actor(String name, ActorState state, ActorType type) {
		this(name,state,type,10,10,0,10,0,5,8);
	}
	
	/**
	 * Copy constructor
	 * @param anActor reference Actor
	 */
	public Actor(Actor anActor) {
		this(anActor.Name,anActor.State,anActor.Type,anActor.HT,anActor.HP,anActor.Damage,anActor.FP,anActor.Fatigue,anActor.Move,anActor.Dodge);
	}


	
}
