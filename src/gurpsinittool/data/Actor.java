package gurpsinittool.data;

import gurpsinittool.util.DieRoller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

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
	
	public enum ActorStatus {Attacking, Waiting, Disarmed, Stunned, Prone, Kneeling, Disabled, Unconscious, Dead};
	// Position, Status, Action, Disarmed
	public HashSet<ActorStatus> Status;

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
	//public String tag;
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
	public int DefaultAttack = 0;
	public ArrayList<Attack> Attacks;

	/**
	 * Basic constructor specifying all options
	 */
	public Actor(String name, HashSet<ActorStatus> status, ActorType type, int ht, int hp, int damage, int fp, int fatigue, int move, int parry, int block, int dodge, int dr, int db, int shield_dr, int shield_hp, int default_attack)
	{
		Name = name;
		Status = new HashSet<ActorStatus>();
		Status.addAll(status);
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
		DefaultAttack = default_attack;
		Attacks = new ArrayList<Attack>();
	}
	
	/**
	 * Basic constructor providing default options for hp, health and damage
	 * @param name : Actor's name
	 * @param type : Actor type (PC, NPC, etc)
	 */
	public Actor(String name, ActorType type) {
		this(name,new HashSet<ActorStatus>(),type,10,10,0,10,0,5,9,9,8,0,2,4,20,0);
	}
	
	/**
	 * Copy constructor
	 * @param anActor reference Actor
	 */
	public Actor(Actor anActor) {
		this(anActor.Name,anActor.Status,anActor.Type,anActor.HT,anActor.HP,anActor.Injury,anActor.FP,anActor.Fatigue,anActor.Move,anActor.Parry,anActor.Block,anActor.Dodge,anActor.DR,anActor.DB,anActor.ShieldDR,anActor.ShieldHP,anActor.DefaultAttack);
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
		if (DefaultAttack < 0 || DefaultAttack >= Attacks.size()) {
			return "<i><font color=gray>" + Name + " has invalid default attack: " + DefaultAttack + "</font></i>";
		}
		Attack attack = Attacks.get(DefaultAttack);
		int roll = DieRoller.roll3d6();
		int margin = attack.Skill - roll;
		String hit_miss;
		String crit_string = "";
		if (DieRoller.isCritFailure(roll, attack.Skill)) {
			hit_miss = "<b><font color=red>Critical miss</font></b>";
			CriticalTables.Entry crit_result = CriticalTables.getRandomEntry(CriticalTables.critical_miss);
			crit_string = "<br/> <i><font color=gray><b>Critical Miss Table Result:</b>" + crit_result.notes + "</font></i>";
		}
		else if (DieRoller.isCritSuccess(roll, attack.Skill)) {
			hit_miss = "<b><font color=blue>Critical hit</font></b>";
			CriticalTables.Entry crit_result = CriticalTables.getRandomEntry(CriticalTables.critical_hit);
			crit_string = "<br/> <i><font color=gray><b>Critical Hit Table Result:</b>" + crit_result.notes + "</font></i>";
		}
		else if (DieRoller.isSuccess(roll, attack.Skill))
			hit_miss = "<b>hit</b>";
		else 
			hit_miss = "miss";
		Damage damage = Damage.ParseDamage(attack.Damage);
		if (attack.Unbalanced) {
			++numParry;
		}
		String armorDivStr = "";
		if (damage.ArmorDivisor != 1) {
			if (damage.ArmorDivisor == (int) damage.ArmorDivisor)
				armorDivStr = "(" + String.format("%d", (int)damage.ArmorDivisor) + ")";
			else
				armorDivStr = "(" + String.format("%s", damage.ArmorDivisor) + ")";
		}			
		return "<b> " + Name + "</b> attacks with " + attack.Name + ": " + hit_miss +  " (" + roll + "/" + attack.Skill + "=" + margin + ") for damage <font color=red><b>" + damage.BasicDamage + armorDivStr + " " + damage.Type + "</b></font> (" + attack.Damage + ")" + crit_string;
	}
	
	/**
	 * Reset actor state- Active, 0 damage, 0 fatigue, numParry/numBlock/ShieldDamage = 0
	 */
	public void Reset() {
		Status.clear();
		Injury = 0;
		Fatigue = 0;
		numParry = 0;
		numBlock = 0;
		ShieldDamage = 0;
	}
	
}
