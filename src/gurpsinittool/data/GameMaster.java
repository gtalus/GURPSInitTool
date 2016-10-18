package gurpsinittool.data;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
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

import gurpsinittool.app.GITApp;
import gurpsinittool.app.InitTable;
import gurpsinittool.data.ActorBase.ActorStatus;
import gurpsinittool.data.ActorBase.ActorType;
import gurpsinittool.data.ActorBase.BasicTrait;
import gurpsinittool.ui.ActorDetailsPanel_v2;
import gurpsinittool.ui.DefenseDialog;
import gurpsinittool.util.MiscUtil;
import gurpsinittool.util.EncounterLogEvent;
import gurpsinittool.util.EncounterLogListener;
import gurpsinittool.util.EncounterLogSupport;
import gurpsinittool.util.AbstractGAction;

@SuppressWarnings("serial")
public class GameMaster implements EncounterLogListener, UndoableEditListener, PropertyChangeListener {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(GameMaster.class.getName());
	
	// Game logic members
	private Integer round = 0;
	private Integer activeActor = -1;
	
	private PropertyChangeSupport mPcs = new PropertyChangeSupport(this); // Change reporting
	private EncounterLogSupport mEls = new EncounterLogSupport(this); // where to send log messages

	private UndoManager undoManager = new UndoManager();
	private Deque<CompoundEdit> compoundEdits = new LinkedList<CompoundEdit>();
	
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
				a.attack(num);
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
		startCompoundEdit();
		if (position <= activeActor) { setActiveActor(activeActor+1); } // Track active actor
		initTable.getActorTableModel().addActor(actor, position); // InitTableModel takes care of listener management
		endCompoundEdit("Add");
	}
	
    public void removeActor(int row) {
    	startCompoundEdit();
    	if (row == activeActor) { nextActor(); } // If deleting the currently active actor, move on (can't move backwards due to issue deleting first actor)
    	initTable.getActorTableModel().removeActor(row);
    	if (row < activeActor) { setActiveActor(activeActor-1); } // track active Actor
    	endCompoundEdit("Remove");
    }
    
    public void removeActor(Actor actor) {
    	int[] rows = initTable.getActorTableModel().getActorRows(actor);
    	for (int i = rows.length-1; i >= 0; i--) {
    		removeActor(rows[i]);
    	}
    }
   
    /**
     * Step to the next actor, taking auto-actions for the new actor if appropriate
     * @return Whether the round has ended
     */
    private boolean nextActor() {
    	// Check for start of round
    	if (activeActor == -1) {
    		setRound(round+1);
    		logEvent("<b>** Round " + round + " **</b>");
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
			currentActor.nextTurn();
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
		startCompoundEdit();
		// First, determine which way to go
		boolean allSet = true;
    	Actor[] actors = initTable.getSelectedActors();
    	for (Actor a : actors) {
    		if (!a.hasStatus(status)) {
				allSet = false;
				break;
			}
    	}
    	for (Actor a : actors) {
    		if (allSet)
				a.removeStatus(status);
			else
				a.addStatus(status);
    	}
    	endCompoundEdit("Status");
	}	
	
	/**
	 * Toggle the specified status indicator to all selected actors individually
	 * @param status The status to toggle
	 */
	private void toggleStatusOfSelectedActors(ActorStatus status) {
		flushInteractiveEdits();
		startCompoundEdit();
    	for (Actor a : initTable.getSelectedActors()) {
    		if (a.hasStatus(status))
				a.removeStatus(status);
			else
				a.addStatus(status);
    	}
    	endCompoundEdit("Status");
	}
	
	/**
	 * Add or remove the specified status indicator to all selected actors
	 * @param status The status to add or remove
	 * @param add True if add, false if remove
	 */
	private void modifyStatusOfSelectedActors(ActorStatus status, boolean add) {
		flushInteractiveEdits();
		startCompoundEdit();
    	for (Actor a : initTable.getSelectedActors()) {
    		if (add)
				a.addStatus(status);
			else 
				a.removeStatus(status);
    	}    
    	endCompoundEdit("Status");
	}
	
    private void setSelectedType(ActorType t) {
    	flushInteractiveEdits();
		startCompoundEdit();
    	for (Actor a : initTable.getSelectedActors()) {
    		a.setType(t);
    	}
    	endCompoundEdit("Type");
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
    	if (LOG.isLoggable(Level.INFO)) { LOG.info("No free tags!");}
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
			if (LOG.isLoggable(Level.FINE)) { LOG.fine("Tagging actor " + aName + " with " + "[" + tag + "]");}
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
    		if (LOG.isLoggable(Level.FINE)) { LOG.fine("Cataloging actor: " + aName);}
    		if ((matcher = nameTag.matcher(aName)).matches()) {
    			String name = matcher.group(1);
    			String tag = matcher.group(2);
    			// Check if tag should be cleared
    			if (clean && a.isTypeAutomated() && (a.hasStatus(ActorStatus.Unconscious)
    														|| a.hasStatus(ActorStatus.Disabled)
    														|| a.hasStatus(ActorStatus.Dead))) {
    				if (LOG.isLoggable(Level.FINE)) { LOG.fine("Cleaning tag: " + tag);}
    				a.setTrait(BasicTrait.Name, name);
    			} else {
	    			if (tags.contains(tag)) 
	    				if (LOG.isLoggable(Level.INFO)) { LOG.info("Duplicate tag detected! " + tag);}
	    			tags.add(tag);
    			}
    		}
    	}
    	return tags;
    }
    
	private void initializeActions() {
		// Undo / Redo
		actionUndo = new AbstractGAction("Undo", "Undo the most recent edit (Ctrl+Z)", KeyEvent.VK_U, new ImageIcon(GITApp.class.getResource("/resources/images/arrow_undo.png"), "Undo")) {
			public void actionPerformed(ActionEvent arg0) {	
				flushInteractiveEdits();
				if (getCompoundLevel() > 0)
					if (LOG.isLoggable(Level.WARNING)) { LOG.warning("Undo action: triggered while compound edit being built!");}
				try {
					undoManager.undo();			
				} catch (CannotUndoException e) {
					if (LOG.isLoggable(Level.WARNING)) { LOG.log(Level.WARNING, "Cannot undo: " + e, e);}
				}
				updateUndoRedo();
			}
		};
		actionRedo = new AbstractGAction("Redo", "Redo the most recent undo (Ctrl+Y)", KeyEvent.VK_R, new ImageIcon(GITApp.class.getResource("/resources/images/arrow_redo.png"), "Redo")) {
			public void actionPerformed(ActionEvent arg0) {	
				// TODO: what about interactive edits in progress?
				if (getCompoundLevel() > 0)
					if (LOG.isLoggable(Level.WARNING)) { LOG.warning("Redo action: triggered while compound edit being built!");}
				try {
					undoManager.redo();
				} catch (CannotRedoException e) {
					if (LOG.isLoggable(Level.WARNING)) { LOG.log(Level.WARNING, "Cannot redo: " + e, e);}
				}
				updateUndoRedo();		
			}
		};
	
		// Round Management
		actionNextActor = new AbstractGAction("Next Combatant", "Move to the next active combatant in the turn sequence (Ctrl+N)", new ImageIcon(GITApp.class.getResource("/resources/images/control_play_blue.png"))) {
			public void actionPerformed(ActionEvent arg0) {	startCompoundEdit(); nextActor(); selectActiveActor(); endCompoundEdit("Advance");}
		};
		actionEndRound = new AbstractGAction("End Round", "Step to the end of the turn sequence (Ctrl+E)", new ImageIcon(GITApp.class.getResource("/resources/images/control_end_blue.png"))) {
			public void actionPerformed(ActionEvent arg0) {	startCompoundEdit(); endRound(); selectActiveActor(); endCompoundEdit("Advance");}
		};
		actionNextRound = new AbstractGAction("Next Round", "Step to the start of the next round sequence (Ctrl+R)", new ImageIcon(GITApp.class.getResource("/resources/images/control_fastforward_blue.png"))) {
			public void actionPerformed(ActionEvent arg0) {	startCompoundEdit(); nextRound(); selectActiveActor(); endCompoundEdit("Advance");}
		};
		actionResetRound = new AbstractGAction("Reset Round Counter", "Reset the round counter (Alt+R)", new ImageIcon(GITApp.class.getResource("/resources/images/control_start_blue.png"))) {
			public void actionPerformed(ActionEvent arg0) {	
				startCompoundEdit();
				setRound(0);
				setActiveActor(-1);	
				endCompoundEdit("Reset");
			}
		};	
		// Actor management
		actionDeleteSelectedActors = new AbstractGAction("Delete", "Delete the selected combatants", null) {
			public void actionPerformed(ActionEvent arg0) { 
				flushInteractiveEdits();
				int result = JOptionPane.showConfirmDialog(initTable, "Are you sure you want to delete these rows?", "Confirm Row Delete", JOptionPane.OK_CANCEL_OPTION);
				startCompoundEdit();
				if (result == JOptionPane.OK_OPTION) {
					int[] rows = initTable.getSelectedRows();
					for (int i = rows.length-1; i >= 0; i--) {  // Go from bottom up to preserve numbering	
						removeActor(rows[i]);
					}
				} 
				endCompoundEdit("Delete");
			}
		};
		actionResetSelectedActors = new AbstractGAction("Reset", "Reset the state of the selected combatants", null) {
			public void actionPerformed(ActionEvent arg0) { 
				flushInteractiveEdits();
				startCompoundEdit();
		    	for (Actor a : initTable.getSelectedActors()) {
		    		a.reset();
		    	}		  
		    	endCompoundEdit("Reset");
			}
		};
		actionSetSelectedActorActive = new AbstractGAction("Set Active", "Set the selected combatant to be currently active", null) {
			public void actionPerformed(ActionEvent arg0) { 
				flushInteractiveEdits();
				startCompoundEdit();
				int[] rows = initTable.getSelectedRows();
				setActiveActor(rows[0]);
				initTable.getActorTableModel().getActor(activeActor).removeStatus(ActorStatus.Waiting); // auto-remove waiting
				endCompoundEdit("Set Act");
			}
		};
		actionTagActors = new AbstractGAction("Update Tags", "Update all NPC tags (Ctrl+T)", new ImageIcon(GITApp.class.getResource("/resources/images/tag_blue_add.png"))) {
			public void actionPerformed(ActionEvent arg0) {	
				flushInteractiveEdits();
				startCompoundEdit();
				autoTagActors(); 
				endCompoundEdit("Tag");
				initTable.autoSizeColumns(); 
			}
		};
		actionTagSelectedActors = new AbstractGAction("Tag", "Add tags to the selected combatants", null) {
			public void actionPerformed(ActionEvent arg0) { 
				flushInteractiveEdits();
				startCompoundEdit();
		    	for (Actor a : initTable.getSelectedActors()) {
		    		tagActor(a);
		    	}
				endCompoundEdit("Tag");
			}
		};
		actionRemoveTagSelectedActors = new AbstractGAction("Remove Tag", "Remove tags from the selected combatants", null) {
			public void actionPerformed(ActionEvent arg0) { 
				flushInteractiveEdits();
				startCompoundEdit();
		    	for (Actor a : initTable.getSelectedActors()) {
		    		removeTag(a);
		    	}
				endCompoundEdit("Tag");
			}
		};
		// Actor actions
		actionAttack = new AbstractGAction("Attack", "Selected combatants attack (Ctrl+K)", new ImageIcon(GITApp.class.getResource("/resources/images/sword.png"))) {
			public void actionPerformed(ActionEvent arg0) { 
				flushInteractiveEdits();
				startCompoundEdit();
				for (Actor a : initTable.getSelectedActors()) {
					a.attack();
				}
				endCompoundEdit("Attack");
			}
		};
		actionDefend = new AbstractGAction("Defend", "Selected combatant defends (Ctrl+D)", new ImageIcon(GITApp.class.getResource("/resources/images/shield.png"))) {
			public void actionPerformed(ActionEvent arg0) { 
				// Verify valid actor
				Actor[] selected = initTable.getSelectedActors();
				if (selected.length == 0)
					return;    	
				Actor actor = selected[0];
				flushInteractiveEdits(); // Clear out edits in progress    			 
				defenseDialog.setActor(actor); // Initialize values with actor
				MiscUtil.validateOnScreen(defenseDialog);
				defenseDialog.setVisible(true); // Modal call    			 
				if (defenseDialog.valid) { // Process and log result!
					actor.defend(defenseDialog.defense);
				}
			}
		};
		// Actor Editing
		actionPostureStanding = new AbstractGAction("Standing", "Set posture to standing", new ImageIcon(GITApp.class.getResource("/resources/images/arrow_up.png"))) {
			public void actionPerformed(ActionEvent arg0) { 
				flushInteractiveEdits();
				startCompoundEdit();
				modifyStatusOfSelectedActors(ActorStatus.Kneeling, false);
				modifyStatusOfSelectedActors(ActorStatus.Prone, false);
				endCompoundEdit("Posture");
			}
		};
		actionPostureKneeling = new AbstractGAction("Kneeling", "Set posture to kneeling", new ImageIcon(GITApp.class.getResource("/resources/images/arrow_down_right.png"))) {
			public void actionPerformed(ActionEvent arg0) { 
				flushInteractiveEdits();
				startCompoundEdit();
				modifyStatusOfSelectedActors(ActorStatus.Kneeling, true);
				modifyStatusOfSelectedActors(ActorStatus.Prone, false);
				endCompoundEdit("Posture");
			}
		};
		actionPostureProne = new AbstractGAction("Prone", "Set posture to prone", new ImageIcon(GITApp.class.getResource("/resources/images/arrow_down.png"))) {
			public void actionPerformed(ActionEvent arg0) {
				flushInteractiveEdits();
				startCompoundEdit();
				modifyStatusOfSelectedActors(ActorStatus.Kneeling, false);
				modifyStatusOfSelectedActors(ActorStatus.Prone, true);
				endCompoundEdit("Posture");
			}
		};
		actionStatusTogglePhysicalStun = new AbstractGAction("Physical Stun", "Toggle Status: Physically Stunned", new ImageIcon(GITApp.class.getResource("/resources/images/stun_phys.png"))) {
			public void actionPerformed(ActionEvent arg0) { toggleStatusOfSelectedActors(ActorStatus.StunPhys); }
		};
		actionStatusToggleMentalStun = new AbstractGAction("Mental Stun", "Toggle Status: Mentally Stunned", new ImageIcon(GITApp.class.getResource("/resources/images/stun_mental.png"))) {
			public void actionPerformed(ActionEvent arg0) { toggleStatusOfSelectedActors(ActorStatus.StunMental); }
		};
		actionStatusToggleRecoveringStun = new AbstractGAction("Recovering from Stun", "Toggle Status: Recovering from Stun", new ImageIcon(GITApp.class.getResource("/resources/images/stun_recover.png"))) {
			public void actionPerformed(ActionEvent arg0) { toggleStatusOfSelectedActors(ActorStatus.StunRecovr); }
		};
		actionStatusToggleAttacking = new AbstractGAction("Attacking", "Toggle Status: Attacking", new ImageIcon(GITApp.class.getResource("/resources/images/sword_rotate.png"))) {
			public void actionPerformed(ActionEvent arg0) { toggleStatusOfSelectedActors(ActorStatus.Attacking); }
		};
		actionStatusToggleDisarmed = new AbstractGAction("Disarmed", "Toggle Status: Disarmed", new ImageIcon(GITApp.class.getResource("/resources/images/plus_blue.png"))) {
			public void actionPerformed(ActionEvent arg0) { toggleStatusOfSelectedActors(ActorStatus.Disarmed); }
		};
		actionStatusToggleUnconscious = new AbstractGAction("Unconscious", "Toggle Status: Unconscious", new ImageIcon(GITApp.class.getResource("/resources/images/cross_yellow.png"))) {
			public void actionPerformed(ActionEvent arg0) { toggleStatusOfSelectedActors(ActorStatus.Unconscious); }
		};
		actionStatusToggleDead = new AbstractGAction("Dead", "Toggle Status: Dead", new ImageIcon(GITApp.class.getResource("/resources/images/cross.png"))) {
			public void actionPerformed(ActionEvent arg0) { toggleStatusOfSelectedActors(ActorStatus.Dead); }
		};
		
		actionCoordinateSelectedStatusAttacking = new AbstractGAction("Attacking", "Change selected combatants' Attacking status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.Attacking); }
		};
		actionCoordinateSelectedStatusDead = new AbstractGAction("Dead", "Change selected combatants' Dead status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.Dead); }
		};
		actionCoordinateSelectedStatusDisabled = new AbstractGAction("Disabled", "Change selected combatants' Disabled status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.Disabled); }
		};
		actionCoordinateSelectedStatusDisarmed = new AbstractGAction("Disarmed", "Change selected combatants' Disarmed status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.Disarmed); }
		};
		actionCoordinateSelectedStatusUnconscious = new AbstractGAction("Unconscious", "Change selected combatants' Unconscious status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.Unconscious); }
		};		
		actionCoordinateSelectedStatusWaiting = new AbstractGAction("Waiting", "Change selected combatants' Waiting status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.Waiting); }
		};
		actionCoordinateSelectedStatusMentalStun = new AbstractGAction("Mental Stun", "Change selected combatants' Mental Stun status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.StunMental); }
		};		
		actionCoordinateSelectedStatusPhysicalStun = new AbstractGAction("Physical Stun", "Change selected combatants' Physical Stun status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.StunPhys); }
		};		
		actionCoordinateSelectedStatusRecoveringStun = new AbstractGAction("Recovering Stun", "Change selected combatants' Recovering Stun status to be the same", null) {
			public void actionPerformed(ActionEvent arg0) { coordinatedChangeStatusOfSelectedActors(ActorStatus.StunRecovr); }
		};
		
		actionSetSelectedTypePC = new AbstractGAction("PC", "Set type to PC", null) {
			public void actionPerformed(ActionEvent arg0) { setSelectedType(ActorType.PC); }
		};
		actionSetSelectedTypeAlly = new AbstractGAction("Ally", "Set type to Ally", null) {
			public void actionPerformed(ActionEvent arg0) { setSelectedType(ActorType.Ally); }
		};
		actionSetSelectedTypeNeutral = new AbstractGAction("Neutral", "Set type to Neutral", null) {
			public void actionPerformed(ActionEvent arg0) { setSelectedType(ActorType.Neutral); }
		};
		actionSetSelectedTypeEnemy = new AbstractGAction("Enemy", "Set type to Enemy", null) {
			public void actionPerformed(ActionEvent arg0) { setSelectedType(ActorType.Enemy); }
		};
		actionSetSelectedTypeSpecial = new AbstractGAction("Special", "Set type to Special", null) {
			public void actionPerformed(ActionEvent arg0) { setSelectedType(ActorType.Special); }
		};
	
	}
	
	// Log Support
	public void addEncounterLogEventListener(EncounterLogListener listener) {
		mEls.addEncounterLogEventListener(listener);
	}
	public void removeEncounterLogEventListener(EncounterLogListener listener) {
		mEls.removeEncounterLogEventListener(listener);
	}
	private void logEvent(String text) {
		mEls.fireEncounterLogEvent(text);
	}
	public void encounterLogMessageSent(EncounterLogEvent evt) {
		if (round <= 0)
			return; // Ignore log messages before the encounter has started
		// Forward
   		mEls.fireEncounterLogEvent(evt);
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
    	if (ActorBase.HackStartCompound.class.isInstance(e.getEdit()))
    		startCompoundEdit();
    	else if (ActorBase.HackEndCompound.class.isInstance(e.getEdit()))
    		endCompoundEdit(e.getEdit().getPresentationName());
    	else
    		postUndoableEdit(e.getEdit());
	}
    
	private void postUndoableEdit(UndoableEdit e) {		
		if (getCompoundLevel() > 0) {
			compoundEdits.getLast().addEdit(e);
		} else {
			undoManager.addEdit(e);
		}
    	updateUndoRedo();    	
	}
	
	private int getCompoundLevel() {
		return compoundEdits.size();
	}	
	public void startCompoundEdit() {
		//new Exception().printStackTrace();
		compoundEdits.push(new CompoundEdit());	
	}	
	public void endCompoundEdit(String display) {
		if (getCompoundLevel() == 0) {
			if (LOG.isLoggable(Level.WARNING)) { LOG.log(Level.WARNING, "Compound edit not started!", new Exception());}
			return;
		} else { // End it and post it
			CompoundEdit edit = compoundEdits.pop();
			edit.addEdit(new DisplayEdit(display));
			edit.end();
			postUndoableEdit(edit);
		}
	}
    
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (GameMaster.class.isInstance(e.getSource())) {
			// Assume I'm the source
			if (LOG.isLoggable(Level.FINER)) { LOG.finer("Saw property change for Gamemaster");}
			String propertyName = e.getPropertyName();
			if (propertyName.equals("ActiveActor")) {
				postUndoableEdit(new ActiveActorEdit((Integer) e.getOldValue(), (Integer) e.getNewValue()));
			} else if (propertyName.equals("Round")) {
				postUndoableEdit(new RoundEdit((Integer) e.getOldValue(), (Integer) e.getNewValue()));		
			} else 
				if (LOG.isLoggable(Level.WARNING)) { LOG.warning("Unrecognized GameMaster property: " + propertyName);}
		} else {
			if (LOG.isLoggable(Level.WARNING)) { LOG.warning("Unexpected source type: " + e.getSource().getClass().toString());}
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
    
    private class DisplayEdit extends AbstractUndoableEdit {
    	String presentationName;
    	public DisplayEdit(String presentationName) {
    		this.presentationName = presentationName;
    	}
    	public String getPresentationName() { return presentationName; }
    }
}
