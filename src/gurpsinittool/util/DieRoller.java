package gurpsinittool.util;

import java.util.Random;

/**
 * Die Rolling utility class
 * @author dcsmall
 *
 */
public final class DieRoller {

	/**
	 * Random number generator
	 */
	private static Random generator = new Random();
	
	private DieRoller() {} // Utility class
	
	/**
	 * Roll some dice
	 * @param dice - the number of d6 to roll
	 * @param adds - modifier to the roll
	 * @return the random value resulting from the roll
	 */
	public static int rollDiceAdds(int dice, final int adds) {
		//String type = matcher.group(3);
		int result = 0;
		for(;dice>0;--dice) {
			result+=rolld6();
		}
		result+=adds;
		return result;
	}
	
	/**
	 * Roll 3d6
	 * @return the random result from the roll
	 */
	public static int roll3d6() {
		return rolld6() + rolld6() + rolld6();
	}
	
	/**
	 * Roll 1d6
	 * @return
	 */
	public static int rolld6() {
		return 1 + generator.nextInt(6);
	}
	
	/**
	 * Determine whether a given roll is successful
	 * @param roll - the value of the roll
	 * @param effSkill - the effective skill to check against
	 * @return whether the roll was successful or not
	 */
	public static boolean isSuccess(final int roll, final int effSkill) {
		return (roll <= 4) || (roll <= effSkill && roll < 17);
	}

	/**
	 * Determine whether a given roll was a failure
	 * @param roll - the value of the roll
	 * @param effSkill - the effective skill to check against
	 * @return whether the roll was a failure or not
	 */
	public static boolean isFailure(final int roll, final int effSkill) {
		return (roll > 4 && roll > effSkill) || roll > 16;
	}
	
	/**
	 * Determine whether a given roll was a critical success
	 * @param roll - the value of the roll
	 * @param effSkill - the effective skill to check against
	 * @return whether the roll was a critical success or not
	 */
	public static boolean isCritSuccess(final int roll, final int effSkill) {
		// Rules state that 3 or 4 is ALWAYS a crit success!
		return (roll <= 4 || (roll <= 6 && (roll+10) <= effSkill));
	}
	
	/**
	 * Determine whether a given roll was a critical failure
	 * @param roll - the value of the roll
	 * @param effSkill - the effective skill to check against
	 * @return whether the roll was a critical failure or not
	 */
	public static boolean isCritFailure(final int roll, final int effSkill) {
		return (roll >= 18 || (effSkill < 16 && roll >= 17) || (roll > 4 && (roll-10) >= effSkill));
	}
}
