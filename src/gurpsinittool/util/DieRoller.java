package gurpsinittool.util;

import java.util.Random;

public class DieRoller {

	static Random generator = new Random();
	
	public static int rollDiceAdds(int dice, int adds) {
		//String type = matcher.group(3);
		int result = 0;
		for(;dice>0;--dice) {
			result+=rolld6();
		}
		result+=adds;
		return result;
	}
	
	public static int roll3d6() {
		return rolld6() + rolld6() + rolld6();
	}
	
	public static int rolld6() {
		return 1 + generator.nextInt(6);
	}
	
	public static boolean isSuccess(int roll, int eff_skill) {
		return (roll <= 4) || (roll <= eff_skill && roll < 17);
	}

	public static boolean isFailure(int roll, int eff_skill) {
		return (roll > 4 && roll > eff_skill) || roll > 16;
	}
	
	public static boolean isCritSuccess(int roll, int eff_skill) {
		// Rules state that 3 or 4 is ALWAYS a crit success!
		return (roll <= 4 || (roll <= 6 && (roll+10) <= eff_skill));
	}
	
	public static boolean isCritFailure(int roll, int eff_skill) {
		return (roll >= 18 || (eff_skill < 16 && roll >= 17) || (roll > 4 && (roll-10) >= eff_skill));
	}
}
