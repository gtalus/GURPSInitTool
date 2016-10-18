package gurpsinittool.util;

import java.util.EventListener;

/**
 * Interface for encounter log event listeners
 * @author dcsmall
 *
 */
public interface EncounterLogListener extends EventListener {
	/**
	 * Called when an encounter log message is sent
	 * @param evt - the encounter log event
	 */
	void encounterLogMessageSent(EncounterLogEvent evt);
}

