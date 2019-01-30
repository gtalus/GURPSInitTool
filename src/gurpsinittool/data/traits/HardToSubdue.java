package gurpsinittool.data.traits;

import java.util.logging.Logger;

import gurpsinittool.data.Actor;

public class HardToSubdue {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(HardToSubdue.class.getName());

	/**
	 * Report debug information to the actor about how this trait is parsed
	 * @param actor
	 */
	public static void reportDebugTrait(Actor actor) {
		actor.setTemp("_HardToSubdue", getValue(actor));
	}

	public static int getValue(Actor actor) {
		if (actor.hasTrait("Hard To Subdue")) {
			return actor.getTraitValueInt("Hard To Subdue");
		} else 
			return 0;
	}
}
