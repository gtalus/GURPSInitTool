package gurpsinittool.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class GameSettings  implements Serializable {
	
	private static final long serialVersionUID = 1L; // Default serial ID
	private PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
	
	// Combat automation Options:
	private boolean AUTO_ATTACK = true;// Each enemy actor automatically attacks at the end of his turn
	private boolean AUTO_UNCONSCIOUS = true; //Check for unconsciousness at the start of each enemy turn
	private boolean AUTO_KNOCKDOWNSTUN = true; // Apply stun results when taking damage
	private boolean AUTO_STUNRECOVERY = true; // Automatically check for stun recovery for each enemy
	
	/**
	 * Change all the settings in this object to match the given object
	 * @param otherSettings - the object to copy the settings from
	 */
	public void syncFrom(GameSettings otherSettings) {
		setAUTO_ATTACK(otherSettings.AUTO_ATTACK);
		setAUTO_KNOCKDOWNSTUN(otherSettings.AUTO_KNOCKDOWNSTUN);
		setAUTO_STUNRECOVERY(otherSettings.AUTO_STUNRECOVERY);
		setAUTO_UNCONSCIOUS(otherSettings.AUTO_UNCONSCIOUS);
	}
	
	public boolean isAUTO_ATTACK() {
		return AUTO_ATTACK;
	}
	public void setAUTO_ATTACK(boolean aUTO_ATTACK) {
		boolean oldVal = AUTO_ATTACK;
		AUTO_ATTACK = aUTO_ATTACK;
		propertySupport.firePropertyChange("AUTO_ATTACK", oldVal, AUTO_ATTACK);
	}
	
	public boolean isAUTO_UNCONSCIOUS() {
		return AUTO_UNCONSCIOUS;
	}
	public void setAUTO_UNCONSCIOUS(boolean aUTO_UNCONSCIOUS) {
		boolean oldVal = AUTO_UNCONSCIOUS;
		AUTO_UNCONSCIOUS = aUTO_UNCONSCIOUS;
		propertySupport.firePropertyChange("AUTO_UNCONSCIOUS", oldVal, AUTO_UNCONSCIOUS);
	}
	
	public boolean isAUTO_KNOCKDOWNSTUN() {
		return AUTO_KNOCKDOWNSTUN;
	}
	public void setAUTO_KNOCKDOWNSTUN(boolean aUTO_KNOCKDOWNSTUN) {
		boolean oldVal = AUTO_KNOCKDOWNSTUN;
		AUTO_KNOCKDOWNSTUN = aUTO_KNOCKDOWNSTUN;
		propertySupport.firePropertyChange("AUTO_KNOCKDOWNSTUN", oldVal, AUTO_KNOCKDOWNSTUN);
	}
	
	public boolean isAUTO_STUNRECOVERY() {
		return AUTO_STUNRECOVERY;
	}
	public void setAUTO_STUNRECOVERY(boolean aUTO_STUNRECOVERY) {
		boolean oldVal = AUTO_STUNRECOVERY;
		AUTO_STUNRECOVERY = aUTO_STUNRECOVERY;
		propertySupport.firePropertyChange("AUTO_STUNRECOVERY", oldVal, AUTO_STUNRECOVERY);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(listener);
	}
}
