package gurpsinittool.data;

import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StrengthTables {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(StrengthTables.class.getName());
	
	public static String getBasicLift(int strength) {
		return (new DecimalFormat("#.#").format(basicLift(strength))); // + " lbs";
	}
	
	public static Double basicLift(int strength) {
		double bl = strength*strength/5.0;
		if (bl > 10) {
			bl = Math.round(bl);
		}
		return bl;
	}

	public static String getEncumbrance(int encLevel, Double basicLift) {
		double lift = 0;
		switch (encLevel) {
		case 0:
			lift = basicLift;
			break;
		case 1:
			lift = basicLift*2;
			break;
		case 2:
			lift = basicLift*3;
			break;
		case 3:
			lift = basicLift*6;
			break;
		case 4:
			lift = basicLift*10;
			break;
		default:
			if (LOG.isLoggable(Level.WARNING)) { LOG.warning("Invalid enc_level: " + encLevel);	}
		}
		return (new DecimalFormat("#.#").format(lift)); // + " lbs";
	}

	public static String getBasicDamageThrust(int strength) {
		if (strength == 0) {
			return diceAdds(0,0);
		} else if(strength <= 10) {
			return diceAdds(1,-6+(strength-1)/2);
		} else if (strength <= 40) {
			return diceAdds(1+(strength-11)/8,((strength-11)%8)/2-1);
		} else if (strength > 40 && strength < 45 ) {
			return diceAdds(4,1);
		} else if (strength >= 45 && strength < 50) {
			return diceAdds(5,0);
		} else if (strength >= 50 && strength < 55) {
			return diceAdds(5,2);
		} else if (strength >= 55 && strength < 60) {
			return diceAdds(6,0);
		} else if (strength >= 60 && strength < 65) {
			return diceAdds(7,-1);
		} else if (strength >= 65 && strength < 70) {
			return diceAdds(7,1);
		} else if (strength >= 70) {
			return diceAdds(1+strength/10,(strength%10<5?0:2));
		} else {
			if (LOG.isLoggable(Level.WARNING)) { LOG.warning("Unsupported strength: " + strength);}
			return "err";
		}
	}
	
	public static String getBasicDamageSwing(int strength) {
		if (strength == 0) {
			return diceAdds(0,0);
		} else if(strength <= 8) {
			return diceAdds(1,-5+(strength-1)/2);
		} else if (strength <= 40) {
			return diceAdds(1+(strength-9)/4,((strength-9)%4)-1);
		} else if (strength > 40 && strength < 45 ) {
			return diceAdds(7,-1);
		} else if (strength >= 45 && strength < 50) {
			return diceAdds(7,1);
		} else if (strength >= 50 && strength < 55) {
			return diceAdds(8,-1);
		} else if (strength >= 55 && strength < 60) {
			return diceAdds(8,1);
		} else if (strength >= 60) {
			return diceAdds(3+strength/10,(strength%10<5?0:2));
		} else {
			if (LOG.isLoggable(Level.WARNING)) { LOG.warning("Unsupported strength: " + strength);}
			return "err";
		}
	}
	
	public static String diceAdds(int dice, int adds) {
		if (dice == 0 && adds == 0) {
			return "";
		} else if (adds == 0) {
			return dice + "d";
		} else if (adds < 0) {
			return dice + "d" + adds;
		} else {
			return dice + "d+" + adds;
		}
	}
}
