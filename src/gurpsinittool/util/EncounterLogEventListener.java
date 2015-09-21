package gurpsinittool.util;

import java.util.EventListener;

public interface EncounterLogEventListener extends EventListener {
	public void encounterLogMessageSent(EncounterLogEvent evt);
}

