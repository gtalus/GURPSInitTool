package gurpsinittool.data;

public class GameSettings  {
	
	// Combat automation Options:
	public boolean AUTO_ATTACK = true;// Each enemy actor automatically attacks at the end of his turn
	public boolean AUTO_UNCONSCIOUS = true; //Check for unconsciousness at the start of each enemy turn
	public boolean AUTO_KNOCKDOWNSTUN = true; // Apply stun results when taking damage
	public boolean AUTO_STUNRECOVERY = true; // Automatically check for stun recovery for each enemy

}
