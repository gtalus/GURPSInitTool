package gurpsinittool.util;

import java.util.EventListener;

public interface FileChangeEventListener extends EventListener{
	public void fileChangeOccured(FileChangeEvent evt);
	public void fileCleanStatusChanged(FileChangeEvent evt);
}
