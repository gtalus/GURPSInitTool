package gurpsinittool.util;

import java.util.EventObject;

public class EncounterLogEvent extends EventObject {
	// Default serialization UID
	private static final long serialVersionUID = 1L;

	public String logMsg;
	
	public EncounterLogEvent(Object source) {
		super(source);
	}
	
	public EncounterLogEvent(Object source, String message) {
		super(source);
		logMsg = message;
	}
}
