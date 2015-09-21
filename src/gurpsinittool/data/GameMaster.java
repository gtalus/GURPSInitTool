package gurpsinittool.data;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import gurpsinittool.app.InitTable;
import gurpsinittool.data.ActorBase.ActorStatus;
import gurpsinittool.data.ActorBase.ActorType;
import gurpsinittool.data.ActorBase.BasicTrait;
import gurpsinittool.ui.ActorDetailsPanel_v2;
import gurpsinittool.ui.DefenseDialog;
import gurpsinittool.util.MiscUtil;
import gurpsinittool.util.EncounterLogEvent;
import gurpsinittool.util.EncounterLogEventSource;
import gurpsinittool.util.GAction;

public class GameMaster implements UndoableEditListener, PropertyChangeListener {
	// Game logic members
	private Integer round = 0;
	private Integer activeActor = -1;
	
	private static final boolean DEBUG = false;
	private PropertyChangeSupport mPcs = new PropertyChangeSupport(this); // Change reporting
	public EncounterLogEventSource LogEventSource = new EncounterLogEventSource(); // where to send log messages

	private UndoManager undoManager = new UndoManager();
	private CompoundEdit compoundEdit;
	
	// Linked objects
	public InitTable initTable;
	public ActorDetailsPanel_v2 detailsPanel;
	public DefenseDialog defenseDialog;
	
	// Actions	
	public Action actionUndo;
	public Action actionRedo;
	
	public Action actionNextActor;
	public Action actionEndRound;
	public Action actionNextRound;
	public Action actionResetRound;
	public Action actionTagActors;
	public Action actionAttack;
	public class AttackNumAction extends AbstractAction {
		int num;
		public AttackNumAction(int num) {this.num = num; }
		public void actionPerformed(ActionEvent arg0) {
			flushInteractiveEdits();
			for (Actor a : initTable.getSelectedActors())
				a.Attack(num);
		}
	};
	public Action actionDefend;
	
	public Action actionPostureStanding;
	public Action actionPostureKneeling;
	public Action actionPostureProne;
	
	public Action actionStatusTogglePhysicalStun;
	public Action actionStatusToggleMentalStun;
	public Action actionStatusToggleRecoveringStun;
	public Action actionStatusToggleAttacking;
	public Action actionStatusToggleDisarmed;
	public Action actionStatusToggleUnconscious;
	public Action actionStatusToggleDead;
	
	public Action actionCoordinateSelectedStatusPhysicalStun;
	public Action actionCoordinateSelectedStatusMentalStun;
	public Action actionCoordinateSelectedStatusRecoveringStun;
	public Action actionCoordinateSelectedStatusAttacking;
	public Action actionCoordinateSelectedStatusDisarmed;
	public Action actionCoordinateSelectedStatusUnconscious;
	public Action actionCoordinateSelectedStatusDead;
	public Action actionCoordinateSelectedStatusWaiting;
	public Action actionCoordinateSelectedStatusDisabled;
	
	public Action actionSetSelectedTypePC;
	public Action actionSetSelectedTypeAlly;
	public Action actionSetSelectedTypeNeutral;
	public Action actionSetSelectedTypeEnemy;
	public Action actionSetSelectedTypeSpecial;

	public Action actionDeleteSelectedActors;
	public Action actionResetSelectedActors;
	public Action actionSetSelectedActorActive;
	public Action actionTagSelectedActors;
	public Action actionRemoveTagSelectedActors;
	
	public GameMaster() {
		initializeActions();
		updateUndoRedo();	
		this.addPropertyChangeListener(this); // Yes, I'm listening to myself :)
	}
	
	public int getActiveActor() {
		return activeActor;
	}
	public int getRound() {
		return round;
	}
	private void selectActiveActor() {
		if (activeActor != -1)
			initTable.setRowSelectionInterval(activeActor, activeActor);
		else
			initTable.clearSelection();
	}
	private void setActiveActor(int newValue) {
		int oldValue = activeActor.intValue();
		activeActor = newValue;
		mPcs.firePropertyChange("ActiveActor", oldValue, newValue);
	}	
	private void setRound(int newValue) {
		int oldValue = round.intValue();
		round = newValue;
		mPcs.firePropertyChange("Round", oldValue, newValue);
	}
	
	public void addActor(Actor actor, int position) {
		postUndoableEdit(new AddActorEdit(actor, position)); // directly post edit to undo manager		
		_addActor(actor,position);
	}
	
	private void _addActor(Actor actor, int position) {
		actor.addPropertyChangeListener(this);
		if (position <= activeActor) { activeActor++; } // Track active actor
		initTable.getActorTableModel().addActor(actor, position);
	}
	
    public void removeActor(int row) {
		postUndoableEdit(new RemoveActorEdit(initTable.getActorTableModel().getActor(row), row)); // directly post edit to undo manager			
    	_removeActor(row);
   }
    
    public void removeActor(Actor actor) {
    	int[] rows = initTable.getActorTableModel().getActorRows(actor);
    	for (int i = rows.length-1; i >= 0; i--) {
    		removeActor(rows[i]);
    	}
    }
    
    private void _removeActor(int row) {
    	if (row < activeActor) { activeActor--; } // track active Actor
		else if (row == activeActor) { activeActor--; nextActor(); }
		initTable.getActorTableModel().removeActor(row);
    }
	
    /**
     * Step to the next actor, taking auto-actions for the new actor if appropriate
     * @return Whether the round has ended
     */
    private boolean nextActor() {
    	// Check for start of round
    	if (activeActor == -1) {		
    		logEvent("<b>** Round " + round + " **</b>");
    		setRound(round+1);
    	}
		boolean retval = false;
		int actorRow = activeActor;
		Actor currentActor;
		do {
			actorRow++;
			if (actorRow >= initTable.getActorTableModel().getRowCount() - 1) { // Remember that the last entry is 'new...'
				actorRow = -1;
				retval = true;
				break;
			}
			currentActor = initTable.getActorTableModel().getActor(actorRow);
			currentActor.NextTurn();
		// Skip over disabled/unconscious/dead/waiting actors
		} while (currentActor.hasStatus(ActorStatus.Disabled)
				|| currentActor.hasStatus(ActorStatus.Unconscious)
				|| currentActor.hasStatus(ActorStatus.Dead)
				|| currentActor.hasStatus(ActorStatus.Waiting));
				
		setActiveActor(actorRow);
    	return retval;
    }
    
    /**
     * Step through the actors until reaching the next round
     */
    protected void nextRound() {
    	if (activeActor != -1) endRound(); // Step to the end of the round if not there already
    	nextActor(); // And then into the new round    	    	
    }
    
    /**
     * Step through the actors until reaching the end of the current round
     */
    protected void endRound() {
    	while(!nextActor()) {}
    }
    

    private void flushInteractiveEdits() {
    	detailsPanel.flushEdits();
    	initTable.stopCellEditing();
    }
    
	/**
	 * Adjust all the statuses in a coordinated fashion.
	 * If any are unset: all are set
	 * If all are set: unset
	 * @param status The status to toggle
	 */
	private void coordinatedChangeStatusOfSelectedActors(ActorStatus status) {
		flushInteractiveEdits();
		// First, determine which way to go
		boolean all_set = true;
    	Actor[] actors = initTable.getSelectedActors();
    	for (Actor a : actors) {
    		if (!a.hasStatus(status)) {
				all_set = false;
				break;
			}
    	}
    	for (Actor a : actors) {
    		if (all_set)
				a.removeStatus(status);
			else
				a.addStatus(status);
    	}
	}	
	
	/**
	 * Toggle the specified status indicator to all selected actors individually
	 * @param status The status to toggle
	 */
	private void toggleStatusOfSelectedActors(ActorStatus status) {
		flushInteractiveEdits();
    	for (Actor a : initTable.getSelectedActors()) {
    		if (a.hasStatus(status))
				a.removeStatus(status);
			else
				a.addStatus(status);
    	}
	}
	
	/**
	 * Add or remove the specified status indicator to all selected actors
	 * @param status The status to add or remove
	 * @param add True if add, false if remove
	 */
	private void modifyStatusOfSelectedActors(ActorStatus status, boolean add) {
		flushInteractiveEdits();
    	for (Actor a : initTable.getSelectedActors()) {
    		if (add)
				a.addStatus(status);
			else 
				a.removeStatus(status);
    	}    	
	}
	
    private void setSelectedType(ActorType t) {
    	flushInteractiveEdits();
    	for (Actor a : initTable.getSelectedActors()) {
    		a.setType(t);
    	}
    }
    
    /**
     * Helper function to iterate through possible tags and select the next unused one
     * @param tags Hash of all used tags
     * @return the next unused tag
     */
	private static Pattern nameTag = Pattern.compile("^(.*) \\[([^\\s]+)\\]$");
	private static enum flagColors {R, B};
	private static int flagMaxNum = 6;
    private static String getNextTag(HashSet<String> tags) {
    	for (flagColors color: flagColors.values()) {
    		for (int i = 1; i <= flagMaxNum; ++i) {
    			String tag = color.toString() + i;
    			if (!tags.contains(tag))
    				return tag;
    		}
    	}
    	System.out.println("-W- GameMaster:getNextTag: no free tags!");
    	return "S99";
    }
	
    // Tag active, non-tagged, enemy actors with available tags
    private void autoTagActors() {
    	// Generate list of tags, and clean unneeded tags
    	HashSet<String> tags = catalogTags(true);
    	
    	// Now go thorough and add tags to non-unconscious/dead enemy actors
    	
    	for (int i = 0; i < initTable.getActorTableModel().getRowCount()-1; ++i) {
    		Actor a = initTable.getActorTableModel().getActor(i);
    		if (a.isTypeAutomated() && !(a.hasStatus(ActorStatus.Unconscious)
    											|| a.hasStatus(ActorStatus.Disabled)
     											|| a.hasStatus(ActorStatus.Dead)
    											|| a.hasStatus(ActorStatus.Waiting))) {
	    		tagActor(a, tags);
    		}
    	}
    }
    
    /**
     * Remove the tag from an actor
     * @param actor The actor to remove the tag from
     */
    private void removeTag(Actor actor) {
    	Matcher matcher;
		Pattern nameTag = Pattern.compile("^(.*) \\[([^\\s]+)\\]$");
		String aName = actor.getTraitValue(BasicTrait.Name);
		if ((matcher = nameTag.matcher(aName)).matches()) {
    		String name = matcher.group(1);
    		actor.setTrait(BasicTrait.Name, name);
		}
    }
    
    /**
     * Tag an actor with the next available tag. This version automatically generates the list of tags in current use.
     * @param actor The actor to tag
     */
    private void tagActor(Actor actor) {
    	tagActor(actor, catalogTags(false));
    }
    
    /**
     * Tag an actor with the next available tag if not already tagged
     * @param actor The actor to tag
     * @param tags A list of all tags currently in use
     */
    private void tagActor(Actor actor, HashSet<String> tags) {
    	String aName = actor.getTraitValue(BasicTrait.Name);
		if (!nameTag.matcher(aName).matches()) {
			String tag = getNextTag(tags);
			if (DEBUG) System.out.println("GameMaster:tagActor: Tagging actor " + aName + " with " + "[" + tag + "]");
			actor.setTrait(BasicTrait.Name, aName + " [" + tag + "]");
			tags.add(tag);
		}
    }
    
    /**
     * Helper method to catalog all tags currently in use
     * @param clean Whether to clear out stale tags (from enemies that are dead/unconscious)
     * @return a HashSet of all tags currently in use
     */
    private HashSet<String> catalogTags(boolean clean) {
		Matcher matcher;
		Pattern nameTag = Pattern.compile("^(.*) \\[([^\\s]+)\\]$");
		HashSet<String> tags = new HashSet<String>();
		
    	// First go through actors, removing unneeded tags (unconscious/dead) and logging existing ones
    	for (int i = 0; i < initTable.getActorTableModel().getRowCount()-1; ++i) {
    		Actor a = initTable.getActorTableModel().getActor(i);
    		String aName = a.getTraitValue(BasicTrait.Name);
    		if (DEBUG) System.out.println("GameMaster:catalogTags: Cataloging actor: " + aName);
    		if ((matcher = nameTag.matcher(aName)).matches()) {
    			String name = matcher.group(1);
    			String tag = matcher.group(2);
    			// Check if tag should be cleared
    			if (clean && a.isTypeAutomated() && (a.hasStatus(ActorStatus.Unconscious)
    														|| a.hasStatus(ActorStatus.Disabled)
    														|| a.hasStatus(ActorStatus.Dead))) {
    				System.out.println("GameMaster:catalogTags: cleaning tag: " + tag);
    				a.setTrait(BasicTrait.Name, name);
    			} else {
	    			if (tags.contains(tag)) 
	    				System.out.println("-W- GameMaster:catalogTags: Duplicate tag detected! " + tag);
	    			tags.add(tag);
    			}
    		}
    	}
    	return tags;
    }
    
 
	@SuppressWarnings("serial")
	private void initializeActions() {
		// Undo / Redo
		actionUndo = new GAction("Undo", "Undo the most recent edit (Ctrl+Z)", new ImageIcon("src/resources/images/arrow_undo.png")) {
			public void actionPerformed(ActionEvent arg0) {	
				try {
					undoManager.undo();			
				} catch (CannotUndoException e) {
					System.out.println("-W- Cannot undo: " + e);
					e.printStackTrace();
				}
				updateUndoRedo();
			}
		};
		actionRedo = new GAction("Redo", "Redo the most recent undo (Ctrl+Y)", new ImageIcon("src/resources/images/arrow_redo.png")) {
			public void actionPerformed(ActionEvent arg0) {	
				try {
					undoManager.redo();
				} catch (CannotRedoException e) {
					System.out.println("-W- Cannot redo: " + e);
					e.printStackTrace();
				}

				updateUndoRedo();		
			}
		};
	
		// Round Management
		actionNextActor = new GAction("Next Combatant", "Move to the next active combatant in the turn sequence (Ctrl+N)", new ImageIcon("src/resources/images/control_play_blue.png")) {
			public void actionPerformed(ActionEvent arg0) {	startCompoundEdit(); nextActor(); selectActiveActor(); endCompoundEdit();}
		};
		actionEndRound = new GAction("End Round", "Step to the end of the turn sequence (Ctrl+E)", new ImageIcon("src/resources/images/control_end_blue.png")) {
			public void actionPerformed(ActionEvent arg0) {	startCompoundEdit(); endRound(); selectActiveActor(); endCompoundEdit();}
		};
		actionNextRound = new GAction("Next Round", "Step to the start of the next round sequence (Ctrl+R)", new ImageIcon("src/resources/images/control_fastforward_blue.png")) {
			public void actionPerformed(ActionEvent arg0) {	startCompoundEdit(); nextRound(); selectActiveActor(); endCompoundEdit();}
		};
		actionResetRound = new GAction("Reset Round Counter", "Reset the round counter (Alt+R)", new ImageIcon("src/resources/images/control_start_blue.png")) {
			public void actionPerformed(ActionEvent arg0) {	
				setRound(0);
				setActiveActor(-1);		
			}
		};	
		// Actor management
		actionDeleteSelectedActors = new GAction("Delete", "Delete the selected combatants", null) {
			public void actionPerformed(ActionEvent arg0) { 
				flushInteractiveEdits();
				int result = JOptionPane.showConfirmDialog(initTable, "Are you sure you want to delete these rows?", "Confirm Row Delete", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					int[] rows = initTable.getSelectedRows();
					for (int i = rows.length-1; i >= 0; i--) {  // Go from bottom up to preserve numbering	
						removeActor(rows[i]);
					}
				} 
			}
		};
		actionResetSelectedActors = new GAction("Reset", "Reset the state of the selected combatants", null) {
			public void actionPerformed(ActionEvent arg0) { 
				flushInteractiveEdits();
		    	for (Actor a : initTable.getSelectedActors()) {
		    		a.Reset();
		    	}		  
			}
		};
		actionSetSelectedActorActive = new GAction("Set Active", "Set the selected combatant to be currently active", null) {
			public void actionPerformed(ActionEvent arg0) { 
				flushInteractiveEdits();
				int[] rows = initTable.getSelectedRows();
				setActiveActor(rows[0]);
				initTable.getActorTableModel().getActor(activeActor).removeStatus(ActorStatus.Waiting); // auto-remove waiting
			}
		};
		actionTagActors = new GAction("Update Tags", "Update all NPC tags (Ctrl+T)", new ImageIcon("src/resources/images/tag_blue_add.png")) {
			public void actionPerformed(ActionEvent arg0) {	
				flushInteractiveEdits();
				autoTagActors(); 
				initTable.autoSizeColumns(); 
			}
		};
		actionTagSelectedActors = new GAction("Tag", "Add tags to the selected combatants", null) {
			public void actionPerformed(ActionEvent arg0) { 
				flushInteractiveEdits();
		    	for (Actor a : initTable.getSelectedActors()) {
		    		tagActor(a);
		    	}
			}
		};
		actionRemoveTagSelectedActors = new GAction("Remove Tag", "Remove tags from the selected combatants", null) {
			public void actionPerformed(ActionEvent arg0) { 
				flushInteractiveEdits();
		    	for (Actor a : initTable.getSelectedActors()) {
		    		removeTag(a);
		    	}
			}
		};
		// Actor actions
		actionAttack = new GAction("Attack", "Selected combatants attack (Ctrl+K)", new ImageIcon("src/resources/images/sword.png")) {
			public void actionPerformed(ActionEvent arg0) { 
				flushInteractiveEdits();
				for (Actor a : initTable.getSelectedActors()) {
					a.Attack();
				}
			}
		};
		actionDefend = new GAction("Defend", "Selected combatant defends (Ctrl+D)", new ImageIcon("src/resources/images/shield.png")) {
			public void actionPerformed(ActionEvent arg0) { 
				// Verify valid actor
				Actor actor = initTable.getSelectedActor();
				if (actor == null)
					return;    			 
				flushInteractiveEdits(); // Clear out edits in progress    			 
				defenseDialog.setActor(actor); // Initialize values with actor
				MiscUtil.validateOnScreen(defenseDialog);
				defenseDialog.setVisible(true); // Modal call    			 
				if (defenseDialog.valid) { // Process and log result!
					actor.Defend(defenseDialog.defense);
				}
			}
		};
		// Actor Editing
		actionPostureStanding = new GAction("Standing", "Set posture to standing", new ImageIcon("src/resources/images/arrow_up.png")) {
			public void actionPerformed(ActionEvent arg0) { 
				modifyStatusOfSelectedActors(ActorStatus.Kneeling, false);
				modifyStatusOfSelectedActors(ActorStatus.Prone, false);
			}
		};
		actionPostureKneeling = new GAction("Kneeling", "Set posture to kneeling", new ImageIcon("src/resources/images/arrow_down_right.png")) {
			public void actionPerformed(ActionEvent arg0) { 
				modifyStatusOfSelectedActors(ActorStatus.Kneeling, true);
				modifyStatusOfSelectedActors(ActorStatus.Prone, false);
			}
		};
		actionPostureProne = new GAction("Prone", "Set posture to prone", new ImageIcon("src/resources/images/arrow_down.png")) {
			public void actionPerformed(ActionEvent arg0) { 
				modifyStatusOfSelectedActors(ActorStatus.Kneeling, false);
				modifyStatusOfSelectedActors(ActorStatus.Prone, true);
			}
		};
		actionStatusTogglePhysicalStun = new GAction("Physical Stun", "Toggle Status: Physically Stunned", new ImageIcon("src/resources/images/stun_phys.png")) {
			public void actionPerformed(ActionEvent arg0) { toggleStatusOfSelectedActors(ActorStatus.StunPhys); }
		};
		actionStatusToggleMentalStun = new GAction("Mental Stun", "Toggle Status: Mentally Stunned", new ImageIcon("src/resources/images/stun_mental.png")) {
			public void actionPerformed(ActionEvent arg0) { toggleStatusOfSelectedActors(ActorStatus.StunMental); }
		};
		actionStatusToggleRecoveringStun = new GAction("Recovering from Stun", "Toggle Status: Recovering from Stun", new ImageIcon("src/resources/images/stun_recover.png")) {
			public void actionPerformed(ActionEvent arg0) { toggleStatusOfSelectedActors(ActorStatus.StunRecovr); }
		};
		actionStatusToggleAttacking = new GAction("Attacking", "Toggle Status: Attacking", new ImageIcon("src/resources/images/sword_rotate.png")) {
			public void actionPerformed(ActionEvent arg0) { toggleStatusOfSelectedActors(ActorStatus.Attacking); }
		};
		actionStatusToggleDisarmed = new GAction("Disarmed", "Toggle Status: Disarmed", new ImageIcon("src/resources/images/plus_blue.png")) {
			public void actionPerformed(ActionEvent arg0) { toggleStatusOfSelectedActors(ActorStatus.Disarmed); }
		};
		actionStatusToggleUnconscious = new GAction("Unconscious", "Toggle Status: Unconscious", new ImageIcon("src/resources/images/cross_yellow.png")) {
			public void actionPerformed(ActionEvent arg0) { toggleStatusOfSelectedActors(ActorStatus.Unconscious); }
		};
		actionStatusToggleDead = new GAction("Dead", "Toggle Status: Dead", new ImageIcon("src/resources/images/cross.png")) {
			public void actionPerformed(ActionEvent arg0) { toggleStatusOfSelectedActors(ActorStatus.Dead); }
		};
		
		actionCoordinateSelectedStatusAttacking = new GAction("Attacking", "Change selected combatants' Attacking status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.Attacking); }
		};
		actionCoordinateSelectedStatusDead = new GAction("Dead", "Change selected combatants' Dead status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.Dead); }
		};
		actionCoordinateSelectedStatusDisabled = new GAction("Disabled", "Change selected combatants' Disabled status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.Disabled); }
		};
		actionCoordinateSelectedStatusDisarmed = new GAction("Disarmed", "Change selected combatants' Disarmed status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.Disarmed); }
		};
		actionCoordinateSelectedStatusUnconscious = new GAction("Unconscious", "Change selected combatants' Unconscious status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.Unconscious); }
		};		
		actionCoordinateSelectedStatusWaiting = new GAction("Waiting", "Change selected combatants' Waiting status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.Waiting); }
		};
		actionCoordinateSelectedStatusMentalStun = new GAction("Mental Stun", "Change selected combatants' Mental Stun status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.StunMental); }
		};		
		actionCoordinateSelectedStatusPhysicalStun = new GAction("Physical Stun", "Change selected combatants' Physical Stun status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.StunPhys); }
		};		
		actionCoordinateSelectedStatusRecoveringStun = new GAction("Recovering Stun", "Change selected combatants' Recovering Stun status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.StunRecovr); }
		};
		
		actionSetSelectedTypePC = new GAction("PC", "Set type to PC", null) {
			public void actionPerformed(ActionEvent arg0) { setSelectedType(ActorType.PC); }
		};
		actionSetSelectedTypeAlly = new GAction("Ally", "Set type to Ally", null) {
			public void actionPerformed(ActionEvent arg0) { setSelectedType(ActorType.Ally); }
		};
		actionSetSelectedTypeNeutral = new GAction("Neutral", "Set type to Neutral", null) {
			public void actionPerformed(ActionEvent arg0) { setSelectedType(ActorType.Neutral); }
		};
		actionSetSelectedTypeEnemy = new GAction("Enemy", "Set type to Enemy", null) {
			public void actionPerformed(ActionEvent arg0) { setSelectedType(ActorType.Enemy); }
		};
		actionSetSelectedTypeSpecial = new GAction("Special", "Set type to Special", null) {
			public void actionPerformed(ActionEvent arg0) { setSelectedType(ActorType.Special); }
		};
	
	}
	
	// Log Support
	private void logEvent(String text) {
		System.out.println("HERE: log event");
		if (LogEventSource != null)
			LogEventSource.fireEncounterLogEvent(new EncounterLogEvent(this, text));
	}
		
	// Change Support
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        mPcs.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        mPcs.removePropertyChangeListener(listener);
    }

	// Undo/Redo support
    private void updateUndoRedo() {
		actionUndo.setEnabled(undoManager.canUndo());
		actionRedo.setEnabled(undoManager.canRedo());
		if (undoManager.canUndo()) {
			actionUndo.putValue(Action.NAME, undoManager.getUndoPresentationName());
    	} else {
    		actionUndo.putValue(Action.NAME, "Undo");
    	}
		if (undoManager.canRedo()) {
			actionRedo.putValue(Action.NAME, undoManager.getRedoPresentationName());
    	} else {
    		actionRedo.putValue(Action.NAME, "Redo");
    	}
    }
    
    @Override    
	public void undoableEditHappened(UndoableEditEvent e) {
    	postUndoableEdit(e.getEdit());
	}
    
	private void postUndoableEdit(UndoableEdit e) {
		if (compoundEdit != null) {
			//System.out.println("HERE: postUndoableEdit: adding to compound edit: " + e.getPresentationName());
			compoundEdit.addEdit(e);
		} else {
			//System.out.println("HERE: postUndoableEdit: adding edit: " + e.getPresentationName());
			undoManager.addEdit(e);
		}
    	updateUndoRedo();
    	
	}
	
	private void startCompoundEdit() {
		if (compoundEdit != null) {
			System.err.println("-E- GameMaster: startCompoundEdit: compound edit already started!!");
			// TODO: print stack trace
			return;
		} else {
			//System.out.println("HERE: startCompoundEdit");
			compoundEdit = new CompoundEdit();
		}
	}
	
	private void endCompoundEdit() {
		if (compoundEdit == null) {
			System.err.println("-E- GameMaster: endCompoundEdit: compound edit not started!!");
			// TODO: print stack trace
			return;
		} else { // End it and post it
			//System.out.println("HERE: endCompoundEdit");
			// TODO: add fake presentation name edit for display purposes?
			compoundEdit.end();
			CompoundEdit temp = compoundEdit;
			compoundEdit = null; // so we don't add it to itself
			postUndoableEdit(temp);
		}
	}
    
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (Actor.class.isInstance(e.getSource())) {
			Actor actor = (Actor) e.getSource();
			System.out.println("GameMaster: propertyChange: saw property change for actor " + actor.getTraitValue(BasicTrait.Name));
			String propertyName = e.getPropertyName();
			if (propertyName.startsWith("trait.")) {
				String traitName = propertyName.substring(6);
				postUndoableEdit(new ActorTraitEdit(actor, traitName, (String)e.getOldValue(), (String)e.getNewValue()));
			} else 
				System.err.println("GameMaster: propertyChange: unrecognized Actor property: " + propertyName);
		} else if (GameMaster.class.isInstance(e.getSource())) {
			// Assume I'm the source
			System.out.println("GameMaster: propertyChange: saw property change for Gamemaster");
			String propertyName = e.getPropertyName();
			if (propertyName.equals("ActiveActor")) {
				postUndoableEdit(new ActiveActorEdit((Integer) e.getOldValue(), (Integer) e.getNewValue()));
			} else if (propertyName.equals("Round")) {
				postUndoableEdit(new RoundEdit((Integer) e.getOldValue(), (Integer) e.getNewValue()));		
			} else 
				System.err.println("GameMaster: propertyChange: unrecognized GameMaster property: " + propertyName);
		} else {
			System.err.println("GameMaster: propertyChange: unexpected source type: " + e.getSource().getClass().toString());
		}
	}

	private class RoundEdit extends AbstractUndoableEdit {
		private int newValue;
		private int oldValue;
		public RoundEdit(int oldValue, int newValue) {
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
		public String getPresentationName() { return "Advance"; }

		public void undo() {
			super.undo();
			removePropertyChangeListener(GameMaster.this);
			setRound(oldValue);
			addPropertyChangeListener(GameMaster.this);
		}

		public void redo() {
			super.redo();
			removePropertyChangeListener(GameMaster.this);
			setRound(newValue);
			addPropertyChangeListener(GameMaster.this);
		}        	
	}
	   
    private class ActiveActorEdit extends AbstractUndoableEdit {
    	private int newValue;
    	private int oldValue;
    	public ActiveActorEdit(int oldValue, int newValue) {
    		this.oldValue = oldValue;
    		this.newValue = newValue;
    	}
    	public String getPresentationName() { return "Advance"; }
    	
    	public void undo() {
			super.undo();
    		removePropertyChangeListener(GameMaster.this);
    		setActiveActor(oldValue);
    		addPropertyChangeListener(GameMaster.this);
    	}
    	
    	public void redo() {
    		super.redo();
    		removePropertyChangeListener(GameMaster.this);
    		setActiveActor(newValue);
    		addPropertyChangeListener(GameMaster.this);
    	}        	
    }
    
    private class AddActorEdit extends AbstractUndoableEdit {
    	private Actor actor;
    	private int index;
    	public AddActorEdit(Actor actor, int index) {
    		this.actor = actor;
    		this.index = index;
    	}
    	public String getPresentationName() { return "Add"; }
    	
    	public void undo() {
			super.undo();
    		_removeActor(index);
    	}
    	
    	public void redo() {
    		super.redo();
    		_addActor(actor,index);
    	}        	
    }
    
    private class RemoveActorEdit extends AbstractUndoableEdit {
    	private Actor actor;
    	private int index;
    	public RemoveActorEdit(Actor actor, int index) {
    		this.actor = actor;
    		this.index = index;
    	}
    	public String getPresentationName() { return "Delete"; }
    	
    	public void undo() {
			super.undo();
    		_addActor(actor,index);
    	}
    	
    	public void redo() {
    		super.redo();
    		_removeActor(index);
    	}        	
    }

    private class ActorTraitEdit extends AbstractUndoableEdit {
    	private Actor actor;
    	private String traitName;
    	private String oldValue;
    	private String newValue;
    	public ActorTraitEdit(Actor actor, String traitName, String oldValue, String newValue) {
    		this.actor = actor;
    		this.traitName = traitName;
    		this.oldValue = oldValue;
    		this.newValue = newValue;
    	}
    	public String getPresentationName() { return "Change"; }
    	
    	public void undo() {
			super.undo();
    		// need to suppress undoable edit adds while this is happening
    		actor.removePropertyChangeListener(GameMaster.this);
    		actor.setTrait(traitName, oldValue);    		
    		actor.addPropertyChangeListener(GameMaster.this);
    	}
    	
    	public void redo() {
    		super.redo();
    		actor.removePropertyChangeListener(GameMaster.this);
    		actor.setTrait(traitName, newValue);  
    		actor.addPropertyChangeListener(GameMaster.this);
    	}        	
    }
}
