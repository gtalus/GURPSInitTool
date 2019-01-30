package gurpsinittool.data.traits;

import java.util.logging.Logger;

import gurpsinittool.data.Actor;

public class HighPainThreshold {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(HighPainThreshold.class.getName());

	/**
	 * Report debug information to the actor about how this trait is parsed
	 * @param actor
	 */
	public static void reportDebugTrait(Actor actor) {
		actor.setTemp("_HighPainThreshild", hasTrait(actor)?"true":"false");
	}

	public static boolean hasTrait(Actor actor) {
		return (actor.hasTrait("High Pain Threshold") || actor.hasTrait("Supernatural Durability"));
	}
}
