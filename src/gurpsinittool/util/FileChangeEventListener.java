package gurpsinittool.util;

import java.util.EventListener;

/**
 * Interface for file change event listeners
 * @author dcsmall
 *
 */
public interface FileChangeEventListener extends EventListener{
	/**
	 * Called when any file change occurs
	 * @param evt - the event object
	 */
	void fileChangeOccured(FileChangeEvent evt);
	/**
	 * Called when the file clean status changes
	 * @param evt - the event object
	 */
	void fileCleanStatusChanged(FileChangeEvent evt);
}
