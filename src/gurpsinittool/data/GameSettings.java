package gurpsinittool.data;

public class GameSettings  {

	// Combat automation Options:
	/**
	 * Each enemy actor automatically attacks at the end of his turn
	 */
	public boolean autoAttack = true;
	public boolean autoUnconscious = true; //Check for unconsciousness at the start of each enemy turn
	public boolean autoKnockdownStun = true; // Apply stun results when taking damage
	public boolean autoStunRecovery = true; // Automatically check for stun recovery for each enemy
	public boolean autoShock = true; // Track shock and apply as penalty to attack skill
	
	// Whether or not to automate each type of combatant:
	public boolean automatePC = false;
	public boolean automateEnemy = true;
	public boolean automateAlly = true;
	public boolean automateNeutral = true;
	public boolean automateSpecial = false;
	
	// Interface / Logging
	public boolean logStatusChanges = false; // Log all status changes
	public boolean logDefenseDetails = false; // Log defense details (retreat/EE/side/etc.)
}
