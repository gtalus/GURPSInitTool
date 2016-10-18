package gurpsinittool.util;

import java.util.EventObject;

/**
 * Event object which encapsulates file change notification
 * @author dcsmall
 *
 */
public class FileChangeEvent extends EventObject {
	// Default serialization UID
	private static final long serialVersionUID = 1L;

	/**
	 * Whether the file is clean or not
	 */
	private final boolean clean;
	
	/**
	 * Get clean status
	 * @return true if clean, false otherwise
	 */
	public boolean isClean() {
		return clean;
	}

	/**
	 * Constructor
	 * @param source - object sending the event
	 * @param clean - clean status
	 */
	public FileChangeEvent(final Object source, final boolean clean) {
		super(source);
		this.clean = clean;
	}
}
