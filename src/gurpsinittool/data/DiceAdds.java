package gurpsinittool.data;

import gurpsinittool.util.DieRoller;

/**
 * Simple class to hold dice+adds
 * @author dcsmall
 *
 */
public class DiceAdds {

	public int dice = 0;
	public int adds = 0;
	
	/**
	 * How many adds convert to two extra dice
	 */
	public static final int TWO_DICE_ADDS = 7;
	/**
	 * How many adds convert to one extra die
	 */
	public static final int ONE_DIE_ADDS = 4;
	/**
	 * Convert adds to dice based on the ONE_DIE_ADDS and TWO_DICE_ADDS values
	 */
	public static final boolean CONVERT_ADDS = true;
	
	/**
	 * Default constructor
	 */
	public DiceAdds() {
	}
	
	/**
	 * Basic constructor with number of dice and adds
	 * @param dice
	 * @param adds - can be negative or positive
	 */
	public DiceAdds(final int dice, final int adds) {
		this.dice = dice;
		this.adds = adds;
	}
	
	public void add(DiceAdds add) {
		this.dice += add.dice;
		this.adds += add.adds;
	}
	
	public void simplify() {
		if (CONVERT_ADDS) { // Convert adds to dice, if enabled
			while(adds >= TWO_DICE_ADDS) {
				dice += 2;
				adds -= TWO_DICE_ADDS;
			}
			while(adds >= ONE_DIE_ADDS) {
				dice += 1;
				adds -= ONE_DIE_ADDS;
			}
		}
	}
	
	/**
	 * Convert to a string
	 */
	public String toString() {
		if (dice == 0) {
			if (adds == 0)
				return "";
			else
				return String.valueOf(adds);
		} else {
			String value = String.valueOf(dice) + "d";
			if (adds > 0) 
				value += "+" + String.valueOf(adds);
			else if (adds < 0)
				value += String.valueOf(adds);
			return value;
		}
	}
	
	/**
	 * Roll these dice+adds
	 * @return the random value resulting from the roll
	 */
	public int roll() {
		int result = 0;
		if (dice < 0) { // negative dice ???
			result = -1*DieRoller.rollDice(-1*dice); 
		} else {
			result = DieRoller.rollDice(dice);
		}
		result += adds;
		return result;
	}
}
