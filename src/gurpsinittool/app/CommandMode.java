package gurpsinittool.app;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.text.JTextComponent;

import gurpsinittool.data.GameMaster;
import gurpsinittool.ui.DefenseDialog;
import gurpsinittool.util.SearchSupport;

public class CommandMode implements AWTEventListener {

	private boolean modeOn = false;
	GITApp theApp;
	SearchSupport search;
	DefenseDialog defense;
	GameMaster gameMaster;
	
	HashMap<Integer, Action> PressedMap = new HashMap<Integer, Action>();
	HashMap<Character, Action> TypedMap = new HashMap<Character, Action>();
	HashMap<Character, Action> DefenseMap = new HashMap<Character, Action>();


	public CommandMode(GITApp theApp, DefenseDialog defense, SearchSupport search, GameMaster gameMaster) {
		this.theApp = theApp;
		this.search = search;
		this.defense = defense;
		this.gameMaster = gameMaster;
		
		//TypedMap.put('a', theApp.actionAttack);
		TypedMap.put('k', gameMaster.actionAttack);
		// different types of attacks (non-default, manual table selection)
		// Currently indexed starting at 1
		TypedMap.put('1', gameMaster.new AttackNumAction(0));
		TypedMap.put('2', gameMaster.new AttackNumAction(1));
		TypedMap.put('3', gameMaster.new AttackNumAction(2));
		TypedMap.put('4', gameMaster.new AttackNumAction(3));
		TypedMap.put('5', gameMaster.new AttackNumAction(4));
		TypedMap.put('6', gameMaster.new AttackNumAction(5));
		TypedMap.put('7', gameMaster.new AttackNumAction(6));
		TypedMap.put('8', gameMaster.new AttackNumAction(7));
		TypedMap.put('9', gameMaster.new AttackNumAction(8));
		TypedMap.put('0', gameMaster.new AttackNumAction(9));
		TypedMap.put('d', gameMaster.actionDefend);
		
		TypedMap.put('s', gameMaster.actionPostureStanding);
		TypedMap.put('n', gameMaster.actionPostureKneeling);
		TypedMap.put('p', gameMaster.actionPostureProne);
		TypedMap.put('h', gameMaster.actionStatusTogglePhysicalStun);
		TypedMap.put('m', gameMaster.actionStatusToggleMentalStun);
		TypedMap.put('r', gameMaster.actionStatusToggleRecoveringStun);
		TypedMap.put('a', gameMaster.actionStatusToggleAttacking);
		TypedMap.put('i', gameMaster.actionStatusToggleDisarmed);
		TypedMap.put('u', gameMaster.actionStatusToggleUnconscious);
		TypedMap.put('x', gameMaster.actionStatusToggleDead);
		
		//actionTagSelectedActors
		//actionRemoveTagSelectedActors
		TypedMap.put('t', gameMaster.actionTagActors);
		//TypedMap.put('c', theApp.actionToggleCommandMode); // BROKEN!i
		
		//TypedMap.put('', theApp.actionOpenCriticalTables);
		TypedMap.put('g', theApp.actionOpenGroupManager);	
		
		// Search: '/' (start/end)
		TypedMap.put('.', search.actionNextMatch);
		TypedMap.put('>', search.actionNextMatch);
		TypedMap.put(',', search.actionPrevMatch);
		TypedMap.put('<', search.actionPrevMatch);
	
		PressedMap.put(KeyEvent.VK_RIGHT, gameMaster.actionNextActor);
		PressedMap.put(KeyEvent.VK_DOWN, gameMaster.actionEndRound);
		PressedMap.put(KeyEvent.VK_UP, gameMaster.actionNextRound);
		
		// Defense dialog
//		
//		// posture: Standing Kneeling Prone - No command
//		
//		// roll edit/reroll - No Command
//		
//	    General: allow Backspace, Tab, Shift-Tab, numeric '/'		
//		DR edit
//		damage edit
//		location: Torso Skull Face Leg Knee Arm Hand Foot Neck Vitals Eye Groin
//		
		DefenseMap.put('p', defense.parryDefenseAction);
		DefenseMap.put('b', defense.blockDefenseAction);
		DefenseMap.put('g', defense.dodgeDefenseAction);
		DefenseMap.put('n', defense.noDefenseAction);

		DefenseMap.put('e', defense.eeAction);
		DefenseMap.put('r', defense.retreatAction);
		DefenseMap.put('s', defense.sideAction);
		DefenseMap.put('t', defense.stunAction);
		DefenseMap.put('h', defense.shieldAction);
		DefenseMap.put('d', defense.deceptiveAction);
		DefenseMap.put('o', defense.deceptiveAction);
		DefenseMap.put('D', defense.reverseDeceptiveAction);
		DefenseMap.put('O', defense.reverseDeceptiveAction);
		
		//
//		// Actions
//		public Action actionOptions;
//		public Action actionResetRound;
//		public Action actionSizeColumns;
//		public Action actionAbout;
	}

	public void eventDispatched(AWTEvent event) {
		if(modeOn && event instanceof KeyEvent){
			KeyEvent key = (KeyEvent)event;
			if (!theApp.isFocused() && !defense.isFocused()) return;
			
			if (key.isControlDown() || key.isAltDown() || key.isMetaDown()) { // ignore potential accelerators and mnemonics
				return;
			} else if (key.getKeyCode() == KeyEvent.VK_ENTER || key.getKeyCode() == KeyEvent.VK_ESCAPE || key.getKeyCode() == KeyEvent.VK_TAB ) { // allow special keys
				return;
			} else if (search.hasFocus()) {
				if (key.getKeyChar() == '/' && key.getID()==KeyEvent.KEY_TYPED) {
					theApp.requestFocus();
					key.consume();
				}
				return;
			} else if (defense.isVisible()) { // Defense mode
				if (defense.locationCombo.hasFocus() || 
						(defense.damageTextField.hasFocus() && !defense.damageTextField.getText().isEmpty())) {
					return; // let the key go through
				} else if (key.getID()==KeyEvent.KEY_TYPED) {
					if (DefenseMap.containsKey(key.getKeyChar())) {			
						DefenseMap.get(key.getKeyChar()).actionPerformed(null);
						key.consume();
					}
				}				
			} else { // Main initiative window
				// TODO: allow editing if text component is focused
				Component fcomponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				if (fcomponent != null && JTextComponent.class.isInstance(fcomponent) && ((JTextComponent)fcomponent).isEditable()) {
					// Allow editing?
				} else {

					// allow -/+/=/digit edit without focus in the injury/fatigue columns
					int tableColumnSelected = gameMaster.initTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
					if (tableColumnSelected != -1 &&  (tableColumnSelected == InitTableModel.columns.Fatigue.ordinal() || 
														tableColumnSelected == InitTableModel.columns.Injury.ordinal())) {
						if (key.getKeyChar() == '-' || key.getKeyChar() == '+' || key.getKeyChar() == '=')
							return;
					} 
					if(key.getID()==KeyEvent.KEY_PRESSED){ //Handle key presses
						if (defense.isVisible()) {
						} else if (PressedMap.containsKey(key.getKeyCode())) {
							PressedMap.get(key.getKeyCode()).actionPerformed(null);
						}
						key.consume();
					} else if (key.getID()==KeyEvent.KEY_TYPED) {
						if (key.getKeyChar() == '/') {
							search.requestFocus();
						} else if (TypedMap.containsKey(key.getKeyChar())) {
							TypedMap.get(key.getKeyChar()).actionPerformed(null);
						} 
						key.consume();
					} else if (key.getID()==KeyEvent.KEY_RELEASED) {
						key.consume();
					}
				}
			}
		}
	}

	public void attachCommandMode(JFrame component) {
		component.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
	}

	public void setModeEnabled(boolean enable) {
		modeOn = enable;
	}
	
	public boolean getModeEnabled() {
		return modeOn;
	}

}