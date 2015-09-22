package gurpsinittool.util;

import javax.swing.event.EventListenerList;

public class EncounterLogSupport {
	protected EventListenerList encounterLogEventListenerList = new EventListenerList();
	private Object source;
	
	public EncounterLogSupport(Object source) {
		this.source = source;
	}
	/**
	 * Add an event listener for EncounterLogEvents
	 * @param listener - the listener to add
	 */
	public void addEncounterLogEventListener(EncounterLogListener listener) {
		encounterLogEventListenerList.add(EncounterLogListener.class, listener);
	}
	
	/**
	 * Remove an event listener for EncounterLogEvents
	 * @param listener - the listener to remove
	 */
	public void removeEncounterLogEventListener(EncounterLogListener listener) {
		encounterLogEventListenerList.remove(EncounterLogListener.class, listener);
	}
	
	/**
	 * Fire an event with a log message from the default source
	 * @param message - the event message
	 */
	public void fireEncounterLogEvent(String message) {
		fireEncounterLogEvent(new EncounterLogEvent(source, message));
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
			if (listeners[i]==EncounterLogListener.class) { 
				((EncounterLogListener)listeners[i+1]).encounterLogMessageSent(evt); 
			} 
		} 
	}
}