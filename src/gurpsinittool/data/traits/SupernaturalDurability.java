package gurpsinittool.data.traits;

import java.util.logging.Logger;

import gurpsinittool.data.Actor;
import gurpsinittool.data.ActorBase.BasicTrait;

public class SupernaturalDurability {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(HardToSubdue.class.getName());

	/**
	 * Report debug information to the actor about how this trait is parsed
	 * @param actor
	 */
	public static void reportDebugTrait(Actor actor) {
		actor.setTemp("_SupernaturalDurability", hasTrait(actor)?"true":"false");
	}

	public static boolean hasTrait(Actor actor) {
		return actor.hasTrait("Supernatural Durability");
	}
	
	/**
	 * Can the actor be crippled? True if it doesn't have the trait or Injury > HP
	 * @param actor - the actor to check
	 * @return Whether the actor can be crippled.
	 */
	public static boolean canCripple(Actor actor) {
		if (hasTrait(actor)) {
			return (actor.getTraitValueInt(BasicTrait.Injury) > actor.getTraitValueInt(BasicTrait.HP));
		}
		return true;
	}
	
	// Included:
	// includes High Pain Threshold (immune to shock)
	// Immune to physical stun
 	// No unconsciousness due to injury
	// As long as you have 0 or more HP, have your full Move, immune to crippling
	// Below 0 HP at half move, can be crippled

	// Not included:
	// Below 0 HP, you won't die unless you are wounded by an attack to which you are specifically vulnerable (see below). 
	// A single attack that inflicts an injury of 10×HP or more will kill you

	// To die, you must first be wounded to -HP or worse. After that, one specific item can kill you. You must specify this when you buy Supernatural Durability.
	// a normal human would have to make HT rolls to survive, you must make those HT rolls or die. If this item wounds you to -5×HP, you die automatically. 
	// If you are already below -5×HP from other damage, any wound from this item will kill you. Any item to which you have a Vulnerability can also kill you in this way.
}
