package gurpsinittool.data;

import gurpsinittool.util.DieRoller;

import java.io.Serializable;
import java.util.ArrayList;

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
	
	public enum ActorState {Active, Waiting, Stunned, Disabled, Unconscious, Dead};
	public ActorState State;

	public enum ActorType {PC, Enemy, Ally, Neutral, Special};
	public ActorType Type;

	//public String Target; /* maybe this should be a pointer? */
	
	public String Notes; /* Free-form notes field */
	
	//public Boolean Active; // Is this actor the current active actor
	
	public int HT; // Health (for saving rolls)
	public int HP; // total Hit points
	public int Injury; // Amount of damage taken (injury)
	
	//public int Will; // Will/IQ
	//public int ST;
	public int FP; // what about MP?
	public int Fatigue; // Amount of FP damage taken
	public int Move;
	// Defenses
	public int Parry;
	public int Block;
	public int Dodge;
	public int DR;
	
	// Shield
	public int DB;
	public int ShieldDR;
	public int ShieldHP;
	
	// Volatile (not currently stored)
	// public int Shock = 0;
	public int ShieldDamage = 0;
	public int numParry = 0;
	public int numBlock = 0;
	//public int numDodge;
	//public int turn = 0; // Before the first round (round 0)
	
	//public float speed; // probably not useful
	//public int MP; // probably not useful, but more likely than FP
	//public int SP; // shock points, probably don't need 
	//public int DR; // Damage Resistance. Only useful if automating attacks.
	
	/* also need extra data collection*/
	
	/* also need attacks info */
	public ArrayList<Attack> Attacks;

	/**
	 * Basic constructor specifying all options
	 */
	public Actor(String name, ActorState state, ActorType type, int ht, int hp, int damage, int fp, int fatigue, int move, int parry, int block, int dodge, int dr, int db, int shield_dr, int shield_hp)
	{
		Name = name;
		State = state;
		Type = type;
		HP = hp;
		HT = ht;
		Injury = damage;
		FP = fp;
		Fatigue = fatigue;
		Move = move;
		Parry = parry;
		Block = block;
		Dodge = dodge;
		DR = dr;

		DB = db;
		ShieldDR = shield_dr;
		ShieldHP = shield_hp;
		//Active = false;
		Attacks = new ArrayList<Attack>();
	}
	
	/**
	 * Basic constructor providing default options for hp, health and damage
	 * @param name : Actor's name
	 * @param state : Actor's state (Active, Dead, etc)
	 * @param type : Actor type (PC, NPC, etc)
	 */
	public Actor(String name, ActorState state, ActorType type) {
		this(name,state,type,10,10,0,10,0,5,9,9,8,0,2,4,20);
	}
	
	/**
	 * Copy constructor
	 * @param anActor reference Actor
	 */
	public Actor(Actor anActor) {
		this(anActor.Name,anActor.State,anActor.Type,anActor.HT,anActor.HP,anActor.Injury,anActor.FP,anActor.Fatigue,anActor.Move,anActor.Parry,anActor.Block,anActor.Dodge,anActor.DR,anActor.DB,anActor.ShieldDR,anActor.ShieldHP);
		for(int i = 0; i < anActor.Attacks.size(); ++i) {
			Attacks.add(anActor.Attacks.get(i));
		}
	}

	public void NextTurn() {
		numParry = 0;
		numBlock = 0;
		// Shock
	}
	
	public String Attack() {
		if (Attacks.size() < 1) {
			return "<i><font color=gray>" + Name + " has no attacks defined!</font></i>";
		}
		// Always use first attack (eventually get some method of picking which attack to use)
		Attack attack = Attacks.get(0);
		int strike = DieRoller.roll3d6();
		int margin = attack.Skill - strike;
		String hit_miss = (margin >= 0)?"<b>hit</b>":"miss";
		Damage damage = Damage.ParseDamage(attack.Damage);
		
		return "<b> " + Name + "</b> attacks with " + attack.Name + ": " + hit_miss +  " (" + strike + "/" + attack.Skill + "=" + margin + ") for damage <font color=red><b>" + damage.BasicDamage + " " + damage.Type + "</b></font> (" + attack.Damage + ")";
	}
	
	/**
	 * Reset actor state- Active, 0 damage, 0 fatigue, numParry/numBlock/ShieldDamage = 0
	 */
	public void Reset() {
		State = ActorState.Active;
		Injury = 0;
		Fatigue = 0;
		numParry = 0;
		numBlock = 0;
		ShieldDamage = 0;
	}
	
}
