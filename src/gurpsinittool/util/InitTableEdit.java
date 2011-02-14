package gurpsinittool.util;

import gurpsinittool.app.InitTable;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class InitTableEdit extends AbstractUndoableEdit {

	// Default SVUID
	private static final long serialVersionUID = 1L;

	private InitTable table;

	public InitTableEdit(InitTable table) {
		this.table = table;
	}

	public void undo() throws CannotUndoException {
		//table.getCellEditor();
		// model_.removeElementAt( index_ );
	}

	public void redo() throws CannotRedoException {
		// model_.insertElementAt( element_, index_ );
	}

	public boolean canUndo() {
		return true;
	}

	public boolean canRedo() {
		return true;
	}

	public String getPresentationName() {
		return "Something";
	}



}
