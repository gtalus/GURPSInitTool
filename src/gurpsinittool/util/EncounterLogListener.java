package gurpsinittool.util;

import java.util.EventListener;

public interface EncounterLogListener extends EventListener {
	public void encounterLogMessageSent(EncounterLogEvent evt);
}

