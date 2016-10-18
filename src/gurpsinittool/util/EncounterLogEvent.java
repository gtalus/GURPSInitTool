package gurpsinittool.util;

import java.util.EventObject;

/**
 * Event object which encapsulates encounter log messages
 * @author dcsmall
 *
 */
public class EncounterLogEvent extends EventObject {
	// Default serialization UID
	private static final long serialVersionUID = 1L;

	/**
	 * The log message
	 */
	private final String logMsg;
	
	/**
	 * Get the event log message
	 * @return the event log message
	 */
	public String getLogMsg() {
		return logMsg;
	}
	/**
	 * Constructor
	 * @param source - source of the event
	 * @param message - log message
	 */
	public EncounterLogEvent(final Object source, final String message) {
		super(source);
		logMsg = message;
	}
}
