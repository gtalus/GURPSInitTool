package gurpsinittool.data;

public class GameSettings  {
	
	// Combat automation Options:
	public boolean AUTO_ATTACK = true;// Each enemy actor automatically attacks at the end of his turn
	public boolean AUTO_UNCONSCIOUS = true; //Check for unconsciousness at the start of each enemy turn
	public boolean AUTO_KNOCKDOWNSTUN = true; // Apply stun results when taking damage
	public boolean AUTO_STUNRECOVERY = true; // Automatically check for stun recovery for each enemy
	public boolean AUTO_SHOCK = true; // Track shock and apply as penalty to attack skill
	
	// Whether or not to automate each type of combatant:
	public boolean AUTOMATE_PC = false;
	public boolean AUTOMATE_ENEMY = true;
	public boolean AUTOMATE_ALLY = true;
	public boolean AUTOMATE_NEUTRAL = true;
	public boolean AUTOMATE_SPECIAL = false;
	
	// Interface / Logging
	public boolean LOG_STATUSCHANGES = false; // Log all status changes
	public boolean LOG_DEFENSEDETAILS = false; // Log defense details (retreat/EE/side/etc.)
}
