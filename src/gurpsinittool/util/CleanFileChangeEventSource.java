package gurpsinittool.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;

/**
 * Support class for managing clean/dirty file data
 * @author dcsmall
 *
 */
public class CleanFileChangeEventSource {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(CleanFileChangeEventSource.class.getName());
	
	/**
	 * Whether or not the file is currently clean
	 */
	private boolean clean = true;
	/**
	 * The object that is being tracked
	 */
	private final Object eventSource;
	/**
	 * List of state listeners
	 */
	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * Constructor
	 * @param source - the source object being tracked
	 */
	public CleanFileChangeEventSource(final Object source) {
		eventSource = source;
	}
	
	/**
     * Method to get the clean status of the groupTree
     * @return whether the group tree has had changes since the last checkpoint
     */
    public boolean isClean() {
    	return clean;
    }
    
    /**
     * Set the status of the groupTree as clean. 
     * Should be called after saving the file.
     */
    public void setClean() {
    	if(!clean) { fireFileCleanStatusChangedEvent(new FileChangeEvent(eventSource, true)); }
    	clean = true;
    }
    
    /**
     * Set the status of the groupTree as dirty. 
     * Should be called after making any changes.
     */
    public void setDirty() {
		if (LOG.isLoggable(Level.FINE)) { LOG.fine("setDirty"); }

    	if(clean) { fireFileCleanStatusChangedEvent(new FileChangeEvent(eventSource, false)); }
    	fireFileChangedEvent(new FileChangeEvent(this, false));
    	clean = false;
    }
    
	/**
	 * Add an event listener for FileChangeEvents
	 * @param listener - the listener to add
	 */
	public void addFileChangeEventListener(final FileChangeEventListener listener) {
		listenerList.add(FileChangeEventListener.class, listener);
	}
	
	/**
	 * Remove an event listener for FileChangeEvents
	 * @param listener - the listener to remove
	 */
	public void removeFileChangeEventListener(final FileChangeEventListener listener) {
		listenerList.remove(FileChangeEventListener.class, listener);
	}
	
	/**
	 * Fire an event indicating that the file has changed
	 * @param evt - the event details
	 */
	protected void fireFileChangedEvent(final FileChangeEvent evt) {
		final Object[] listeners = listenerList.getListenerList(); 
		// Each listener occupies two elements - the first is the listener class 
		// and the second is the listener instance 
		for (int i=0; i<listeners.length; i+=2) { 
			if (listeners[i]==FileChangeEventListener.class) { 
				((FileChangeEventListener)listeners[i+1]).fileChangeOccured(evt); 
			} 
		} 
	}
	
	/**
	 * Fire an event indicating that the file clean status has changed 
	 * (clean -> dirty or dirty -> clean)
	 * @param evt - the event details
	 */
	protected void fireFileCleanStatusChangedEvent(final FileChangeEvent evt) {
		final Object[] listeners = listenerList.getListenerList(); 
		// Each listener occupies two elements - the first is the listener class 
		// and the second is the listener instance 
		for (int i=0; i<listeners.length; i+=2) { 
			if (listeners[i]==FileChangeEventListener.class) { 
				((FileChangeEventListener)listeners[i+1]).fileCleanStatusChanged(evt); 
			} 
		} 
	}
}
