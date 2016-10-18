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
	
	HashMap<Integer, Action> pressedMap = new HashMap<Integer, Action>();
	HashMap<Character, Action> typedMap = new HashMap<Character, Action>();
	HashMap<Character, Action> defenseMap = new HashMap<Character, Action>();


	public CommandMode(GITApp theApp, DefenseDialog defense, SearchSupport search, GameMaster gameMaster) {
		this.theApp = theApp;
		this.search = search;
		this.defense = defense;
		this.gameMaster = gameMaster;
		
		//TypedMap.put('a', theApp.actionAttack);
		typedMap.put('k', gameMaster.actionAttack);
		// different types of attacks (non-default, manual table selection)
		// Currently indexed starting at 1
		typedMap.put('1', gameMaster.new AttackNumAction(0));
		typedMap.put('2', gameMaster.new AttackNumAction(1));
		typedMap.put('3', gameMaster.new AttackNumAction(2));
		typedMap.put('4', gameMaster.new AttackNumAction(3));
		typedMap.put('5', gameMaster.new AttackNumAction(4));
		typedMap.put('6', gameMaster.new AttackNumAction(5));
		typedMap.put('7', gameMaster.new AttackNumAction(6));
		typedMap.put('8', gameMaster.new AttackNumAction(7));
		typedMap.put('9', gameMaster.new AttackNumAction(8));
		typedMap.put('0', gameMaster.new AttackNumAction(9));
		typedMap.put('d', gameMaster.actionDefend);
		
		typedMap.put('s', gameMaster.actionPostureStanding);
		typedMap.put('n', gameMaster.actionPostureKneeling);
		typedMap.put('p', gameMaster.actionPostureProne);
		typedMap.put('h', gameMaster.actionStatusTogglePhysicalStun);
		typedMap.put('m', gameMaster.actionStatusToggleMentalStun);
		typedMap.put('r', gameMaster.actionStatusToggleRecoveringStun);
		typedMap.put('a', gameMaster.actionStatusToggleAttacking);
		typedMap.put('i', gameMaster.actionStatusToggleDisarmed);
		typedMap.put('u', gameMaster.actionStatusToggleUnconscious);
		typedMap.put('x', gameMaster.actionStatusToggleDead);
		
		//actionTagSelectedActors
		//actionRemoveTagSelectedActors
		typedMap.put('t', gameMaster.actionTagActors);
		//TypedMap.put('c', theApp.actionToggleCommandMode); // BROKEN!i
		
		//TypedMap.put('', theApp.actionOpenCriticalTables);
		typedMap.put('g', theApp.actionOpenGroupManager);	
		
		// Search: '/' (start/end)
		typedMap.put('.', search.actionNextMatch);
		typedMap.put('>', search.actionNextMatch);
		typedMap.put(',', search.actionPrevMatch);
		typedMap.put('<', search.actionPrevMatch);
	
		pressedMap.put(KeyEvent.VK_RIGHT, gameMaster.actionNextActor);
		pressedMap.put(KeyEvent.VK_DOWN, gameMaster.actionEndRound);
		pressedMap.put(KeyEvent.VK_UP, gameMaster.actionNextRound);
		
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
		defenseMap.put('p', defense.parryDefenseAction);
		defenseMap.put('b', defense.blockDefenseAction);
		defenseMap.put('g', defense.dodgeDefenseAction);
		defenseMap.put('n', defense.noDefenseAction);

		defenseMap.put('e', defense.eeAction);
		defenseMap.put('r', defense.retreatAction);
		defenseMap.put('s', defense.sideAction);
		defenseMap.put('t', defense.stunAction);
		defenseMap.put('h', defense.shieldAction);
		defenseMap.put('d', defense.deceptiveAction);
		defenseMap.put('o', defense.deceptiveAction);
		defenseMap.put('D', defense.reverseDeceptiveAction);
		defenseMap.put('O', defense.reverseDeceptiveAction);
		
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
					if (defenseMap.containsKey(key.getKeyChar())) {			
						defenseMap.get(key.getKeyChar()).actionPerformed(null);
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
					String tableColumnNameSelected = gameMaster.initTable.getColumnName(tableColumnSelected);
					if (tableColumnSelected != -1 &&  (tableColumnNameSelected == "Fatigue" || 
													   tableColumnNameSelected == "Injury")) {
						if (key.getKeyChar() == '-' || key.getKeyChar() == '+' || key.getKeyChar() == '=')
							return;
					} 
					if(key.getID()==KeyEvent.KEY_PRESSED){ //Handle key presses
						if (defense.isVisible()) {
						} else if (pressedMap.containsKey(key.getKeyCode())) {
							pressedMap.get(key.getKeyCode()).actionPerformed(null);
						}
						key.consume();
					} else if (key.getID()==KeyEvent.KEY_TYPED) {
						if (key.getKeyChar() == '/') {
							search.requestFocus();
						} else if (typedMap.containsKey(key.getKeyChar())) {
							typedMap.get(key.getKeyChar()).actionPerformed(null);
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
