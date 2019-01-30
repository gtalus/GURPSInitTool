package gurpsinittool.data.traits;

import java.util.logging.Logger;

import gurpsinittool.data.Actor;

/**
 * A SpecialTrait provides handling for one particular trait which modifies automation
 * functionality. This is a virtual base class which will be extended by each individual trait.
 * @author dcsmall
 *
 */
public abstract class SpecialTraits {
	/**
	 * Logger
	 */
	protected final static Logger LOG = Logger.getLogger(SpecialTraits.class.getName());
	
	public static void reportDebugTraits(Actor actor) {
		Vulnerability.reportDebugTrait(actor);
		InjuryTolerance.reportDebugTrait(actor);
		reportOtherDebugTraits(actor);
	}

	/**
	 * Report simple debug traits
	 * @param actor
	 */
	private static void reportOtherDebugTraits(Actor actor) {
		actor.setTemp("_CombatReflexes", actor.hasTrait("Combat Reflexes")?"true":"false");
		actor.setTemp("_HighPainThreshold", actor.hasTrait("High Pain Threshold")?"true":"false");
		actor.setTemp("_LowPainThreshold", actor.hasTrait("Low Pain Threshold")?"true":"false");
	}
}
