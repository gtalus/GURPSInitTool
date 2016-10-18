package gurpsinittool.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import gurpsinittool.data.Actor;
import gurpsinittool.data.ActorBase;
import gurpsinittool.data.ActorBase.ActorStatus;

@SuppressWarnings("serial")
public class PopupStatusEditList {

	protected JList<ActorStatus> list;
	protected JPanel editorPanel;
	protected JPopupMenu popupStatusMenu;
	protected Actor currentActor;
	
	public PopupStatusEditList() {
		initializeList();
		editorPanel = new JPanel();
		editorPanel.setLayout(new BorderLayout());
		editorPanel.add(list, BorderLayout.CENTER);
		popupStatusMenu = new JPopupMenu();    	
    	popupStatusMenu.add(editorPanel);  
    	//popupStatusMenu.addSeparator();
    	JMenuItem menuItem = new JMenuItem(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setActorStatuses();
			}
		});
        menuItem.setText("Set");
        menuItem.setMnemonic(KeyEvent.VK_S);
    	popupStatusMenu.add(menuItem);
    	menuItem = new JMenuItem(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clearActorStatuses();
			}
		});
        menuItem.setText("Clear All");
        menuItem.setMnemonic(KeyEvent.VK_C);
    	popupStatusMenu.add(menuItem);

    	// Make list blend in better with popup menu
    	list.setBackground(popupStatusMenu.getBackground());
    	//list.setBorder(BorderFactory.createLineBorder(Color.gray));
    	//list.setBorder(BorderFactory.createDashedBorder(paint)(Color.gray));
    	list.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
	}
	
	private void initializeList() {
		list = new JList<ActorStatus>(ActorBase.ActorStatus.values());
		list.setVisibleRowCount(Actor.ActorStatus.values().length);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setSelectionModel(new DefaultListSelectionModel() {
			boolean gestureStarted = false;
			boolean adding = true;
		    @Override
		    public void setSelectionInterval(int index0, int index1) {
		    	if (getValueIsAdjusting() && !gestureStarted && index0 != -1 && index1 != -1) {
		    		adding = super.isSelectedIndex(index0)?false:true;
		    		gestureStarted = true;
		    	}
		    
		        if(!getValueIsAdjusting() || adding) {
		            super.addSelectionInterval(index0, index1);
		        }
		        else {
		            super.removeSelectionInterval(index0, index1);
		        }
		    }
		    @Override
		    public void setValueIsAdjusting(boolean isAdjusting) {
		    	if (isAdjusting == false) {
		    	    gestureStarted = false;
		    	}
		    	super.setValueIsAdjusting(isAdjusting);
		    }
		});
	}
	
	/**
	 * Show the popup menu
	 * @param actor - the Actor who's statusto edit
	 * @param comp - the invoking component
	 * @param x - x offset from component where menu should be shown
	 * @param y - y offset from component where menu should be shown
	 */
	public void show(Actor actor, Component comp, int x, int y) {
		currentActor = actor;
		refreshStatusSelection();
    	popupStatusMenu.setPreferredSize(new Dimension(comp.getSize().width, popupStatusMenu.getPreferredSize().height));
		popupStatusMenu.show(comp, x, y);
	}
	
	/**
	 * Based on currentActor
	 */
	protected void refreshStatusSelection() {
		list.clearSelection();
		for (ActorStatus status : currentActor.getAllStatuses()) {
			//int index = status.ordinal();
			list.setSelectedValue(status, true);
			//list.addSelectionInterval(index, index);
		}
	}
	
	protected void setActorStatuses() {
		HashSet<ActorStatus> statuses = new HashSet<ActorStatus>(list.getSelectedValuesList());
		currentActor.setAllStatuses(statuses);
	}
	
	protected void clearActorStatuses() {
		currentActor.setAllStatuses(new HashSet<ActorStatus>());
	}
}
