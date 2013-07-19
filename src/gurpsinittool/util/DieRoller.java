package gurpsinittool.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
