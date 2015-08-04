package gurpsinittool.util;

import javax.swing.event.EventListenerList;

public class CleanFileChangeEventSource {
	private static final boolean DEBUG = false;
	private boolean clean = true;
	
	private Object Source;
	protected EventListenerList fileChangeListenerList = new EventListenerList();

	public CleanFileChangeEventSource(Object source) {
		Source = source;
	}
	
	/**
     * Method to get the clean status of the groupTree
     * @return whether the group tree has had changes since the last checkpoint
     */
    public boolean isClean() {
    	return clean;
    }
    
    /**
     * Set the status of the groupTree as clean. Should be called after saving the file.
     */
    public void setClean() {
    	if(!clean) { fireFileCleanStatusChangedEvent(new FileChangeEvent(Source, true)); }
    	clean = true;
    }
    
    /**
     * Set the status of the groupTree as dirty. Should be called after making any changes.
     */
    public void setDirty() {
		if (DEBUG) { System.out.println("CleanFileChangeEventSource: setDirty"); }

    	if(clean) { fireFileCleanStatusChangedEvent(new FileChangeEvent(Source, false)); }
    	fireFileChangedEvent(new FileChangeEvent(this));
    	clean = false;
    }
    
	/**
	 * Add an event listener for FileChangeEvents
	 * @param listener - the listener to add
	 */
	public void addFileChangeEventListener(FileChangeEventListener listener) {
		fileChangeListenerList.add(FileChangeEventListener.class, listener);
	}
	
	/**
	 * Remove an event listener for FileChangeEvents
	 * @param listener - the listener to remove
	 */
	public void removeFileChangeEventListener(FileChangeEventListener listener) {
		fileChangeListenerList.remove(FileChangeEventListener.class, listener);
	}
	
	/**
	 * Fire an event indicating that the file has changed
	 * @param evt - the event details
	 */
	void fireFileChangedEvent(FileChangeEvent evt) {
		Object[] listeners = fileChangeListenerList.getListenerList(); 
		// Each listener occupies two elements - the first is the listener class 
		// and the second is the listener instance 
		for (int i=0; i<listeners.length; i+=2) { 
			if (listeners[i]==FileChangeEventListener.class) { 
				((FileChangeEventListener)listeners[i+1]).fileChangeOccured(evt); 
			} 
		} 
	}
	
	/**
	 * Fire an event indicating that the file clean status has changed (clean -> dirty or dirty -> clean)
	 * @param evt - the event details
	 */
	void fireFileCleanStatusChangedEvent(FileChangeEvent evt) {
		Object[] listeners = fileChangeListenerList.getListenerList(); 
		// Each listener occupies two elements - the first is the listener class 
		// and the second is the listener instance 
		for (int i=0; i<listeners.length; i+=2) { 
			if (listeners[i]==FileChangeEventListener.class) { 
				((FileChangeEventListener)listeners[i+1]).fileCleanStatusChanged(evt); 
			} 
		} 
	}
}
