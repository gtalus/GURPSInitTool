package gurpsinittool.util;

import java.util.EventObject;

public class FileChangeEvent extends EventObject {
	// Default serialization UID
	private static final long serialVersionUID = 1L;

	public boolean isClean = false;
	
	public FileChangeEvent(Object source) {
		super(source);
	}
	
	public FileChangeEvent(Object source, boolean clean) {
		super(source);
		isClean = clean;
	}
}
