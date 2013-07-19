package gurpsinittool.util;

import javax.swing.event.EventListenerList;

public class EncounterLogEventSource {
	protected EventListenerList encounterLogEventListenerList = new EventListenerList();

	/**
	 * Add an event listener for EncounterLogEvents
	 * @param listener - the listener to add
	 */
	public void addEncounterLogEventListener(EncounterLogEventListener listener) {
		encounterLogEventListenerList.add(EncounterLogEventListener.class, listener);
	}
	
	/**
	 * Remove an event listener for EncounterLogEvents
	 * @param listener - the listener to remove
	 */
	public void removeEncounterLogEventListener(EncounterLogEventListener listener) {
		encounterLogEventListenerList.remove(EncounterLogEventListener.class, listener);
	}
	
	/**
	 * Fire an event with a log message
	 * @param evt - the event details
	 */
	public void fireEncounterLogEvent(EncounterLogEvent evt) {
		Object[] listeners = encounterLogEventListenerList.getListenerList(); 
		// Each listener occupies two elements - the first is the listener class 
		// and the second is the listener instance 
		for (int i=0; i<listeners.length; i+=2) { 
			if (listeners[i]==EncounterLogEventListener.class) { 
				((EncounterLogEventListener)listeners[i+1]).encounterLogMessageSent(evt); 
			} 
		} 
	}
}