package gurpsinittool.data;

public class GameSettings  {

    // TODO: keeping names with legacy values for now, probably want to update them to be nicer later on

	// Combat automation Options:
	public final GameSetting autoAttack = new GameSetting(true, "AUTO_ATTACK", "Execute default attack if not stunned or otherwise incapacatated.");
	//public final GameSetting attackDelay = new GameSetting(false, "ATTACK_DELAYED", "Delay automatic attack until moving to subsequent combatant (allows adjusting attack while combatant is active)");
	public final GameSetting autoUnconscious = new GameSetting(true, "AUTO_UNCONSCIOUS", "Perform consciousness check at the start of each turn.");
	public final GameSetting autoKnockdownStun = new GameSetting(true, "AUTO_KNOCKDOWNSTUN", "Perform knockdown/stunning check when taking damage.");
	public final GameSetting autoStunRecovery = new GameSetting(true, "AUTO_STUNRECOVERY", "Perform stun recovery check when stunned.");
	public final GameSetting autoShock = new GameSetting(true, "AUTO_SHOCK", "Apply shock to attack rolls.");
    
	// Whether or not to automate each type of combatant:
	public final GameSetting automatePC = new GameSetting(false, "AUTOMATE_PC", "Apply automations to PCs");
	public final GameSetting automateEnemy = new GameSetting(true, "AUTOMATE_ENEMY", "Apply automations to Enemies");
	public final GameSetting automateAlly = new GameSetting(true, "AUTOMATE_ALLY", "Apply automations to Allies");
	public final GameSetting automateNeutral = new GameSetting(true, "AUTOMATE_NEUTRAL", "Apply automations to Neutrals");
	public final GameSetting automateSpecial = new GameSetting(false, "AUTOMATE_SPECIAL", "Apply automations to Specials");

	// Interface / Logging
	public final GameSetting logStatusChanges = new GameSetting(false, "LOG_STATUSCHANGES", "Log defense details (retreat/EE/side/etc.)");
	public final GameSetting logDefenseDetails = new GameSetting(false, "LOG_DEFENSEDETAILS", "Log all status/posture changes");	

	/**
	 * Encapsulation of a single boolean game setting
	 * @author dcsmall
	 *
	 */
	public class GameSetting {
		/**
		 * The value of the setting
		 */
		private boolean value;
		/**
		 * The name of the setting
		 */
		final private String name;
		/**
		 * The description of the setting
		 */
		final private String description;
		
		/**
		 * Constructor for a GameSetting
		 * @param defaultValue - default value
		 * @param description - a description of the value
		 */
		public GameSetting(final boolean defaultValue, final String name, final String description) {
			this.setValue(defaultValue);
			this.name = name;
			this.description = description;
		}
		/**
		 * Get the value of the setting
		 * @return
		 */
		public boolean isSet() {
			return value;
		}
		/**
		 * Set the value of the setting
		 * @param value
		 */
		public void setValue(final boolean value) {
			this.value = value;
		}
		/**
		 * Get the description of the setting
		 * @return the description of the setting
		 */
		public String getName() {
			return name;
		}		
		/**
		 * Get the description of the setting
		 * @return the description of the setting
		 */
		public String getDescription() {
			return description;
		}		
	}
}
