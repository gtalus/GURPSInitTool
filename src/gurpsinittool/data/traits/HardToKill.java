package gurpsinittool.data.traits;

import java.util.logging.Logger;
import gurpsinittool.data.Actor;

public class HardToKill {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(HardToKill.class.getName());

	/**
	 * Report debug information to the actor about how this trait is parsed
	 * @param actor
	 */
	public static void reportDebugTrait(Actor actor) {
		actor.setTemp("_HardToKill", getHardToKill(actor));
	}

	public static int getHardToKill(Actor actor) {
		if (actor.hasTrait("Hard To Kill")) {
			return actor.getTraitValueInt("Hard To Kill");
		} else 
			return 0;
	}
}
