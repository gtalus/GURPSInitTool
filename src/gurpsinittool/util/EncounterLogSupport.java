package gurpsinittool.util;

import javax.swing.event.EventListenerList;

/**
 * Support class to help implement Encounter Log events
 * @author dcsmall
 *
 */
public class EncounterLogSupport {
	/**
	 * List of event listeners
	 */
	protected transient EventListenerList listenerList = new EventListenerList();
	/**
	 * Object listed as firing the events
	 */
	private final Object source;
	
	/**
	 * Constructor
	 * @param source - the source object firing the events
	 */
	public EncounterLogSupport(final Object source) {
		this.source = source;
	}
	/**
	 * Add an event listener for EncounterLogEvents
	 * @param listener - the listener to add
	 */
	public void addEncounterLogEventListener(final EncounterLogListener listener) {
		listenerList.add(EncounterLogListener.class, listener);
	}
	
	/**
	 * Remove an event listener for EncounterLogEvents
	 * @param listener - the listener to remove
	 */
	public void removeEncounterLogEventListener(final EncounterLogListener listener) {
		listenerList.remove(EncounterLogListener.class, listener);
	}
	
	/**
	 * Fire an event with a log message from the default source
	 * @param message - the event message
	 */
	public void fireEncounterLogEvent(final String message) {
		fireEncounterLogEvent(new EncounterLogEvent(source, message));
	}
	/**
	 * Fire an event with a log message
	 * @param evt - the event details
	 */
	public void fireEncounterLogEvent(final EncounterLogEvent evt) {
		final Object[] listeners = listenerList.getListenerList(); 
		// Each listener occupies two elements - the first is the listener class 
		// and the second is the listener instance 
		for (int i=0; i<listeners.length; i+=2) { 
			if (listeners[i]==EncounterLogListener.class) { 
				((EncounterLogListener)listeners[i+1]).encounterLogMessageSent(evt); 
			} 
		} 
	}
}