package gurpsinittool.data;

import gurpsinittool.util.DieRoller;

import java.util.ArrayList;

public class CriticalTables {

	public static class Entry {
		public ArrayList<Integer> rolls;
		public String notes;
		
		Entry(String notes, int ... rolls ) {
			this.rolls = new ArrayList<Integer>();
			this.notes = notes;
			for (int i = 0; i < rolls.length; ++i)
				this.rolls.add(rolls[i]);
		}
	}
	
	public static Entry getRandomEntry(ArrayList<Entry> entryTable) {
		int result = DieRoller.roll3d6();
    	
    	// Search through table for entry that matches result
    	for(int i = 0; i < entryTable.size(); ++i) {
    		for (int j = 0; j < entryTable.get(i).rolls.size(); ++j) {
    			if (entryTable.get(i).rolls.get(j) == result) {
    				return entryTable.get(i);
    			}
    		}
    	}
    	return new Entry("None Found for roll", result);
	}
	
	public static ArrayList<Entry> criticalHit = new ArrayList<Entry>();
	static {
		criticalHit.add(new Entry("The blow does triple damage.", 
				3, 18));
		criticalHit.add(new Entry("The target's DR protects at half value (round down) after applying any armor divisors.",
				4, 17));
		criticalHit.add(new Entry("The blow does double damage.",
				5, 16));
		criticalHit.add(new Entry("The blow does maximum normal damage.",
				6, 15));
		criticalHit.add(new Entry("If any damage penetrates DR, treat it as if it were a major wound, regardless of the actual injury inflicted.",
				7, 13, 14));
		criticalHit.add(new Entry("If any damage penetrates DR, it inflicts double normal shock (to a maximum penalty of -8). If the injury is to a limb or extremity, that body part is crippled as well. This is only a 'funny-bone' injury: crippling wears off in (16 - HT) seconds, minimum two seconds, unless the injury was enough to cripple the body part anyway.",
				8));
		criticalHit.add(new Entry("Normal damage only.",
				9, 10, 11));
		criticalHit.add(new Entry("Normal damage, and the victim drops anything he is holding, regardless of whether any damage penetrates DR.",
				12));
		//critical_hit.add(new Entry(10, "Normal damage only.");
		//critical_hit.add(new Entry(11, "Normal damage only.");
		//critical_hit.add(new Entry(13, "If any damage penetrates DR, treat it as if it were a major wound, regardless of the actual injury inflicted.");
		//critical_hit.add(new Entry(14, "If any damage penetrates DR, treat it as if it were a major wound, regardless of the actual injury inflicted.");
		//critical_hit.add(new Entry(15, "The blow does maximum normal damage.");
		//critical_hit.add(new Entry(16, "The blow does double damage.");
		//critical_hit.add(new Entry(17, "The target's DR protects at half value (round down) after applying any armor divisors.");
		//critical_hit.add(new Entry(18, "The blow does triple damage.");
	}
	
	public static ArrayList<Entry> criticalHeadHit = new ArrayList<Entry>();
	static {
		criticalHeadHit.add(new Entry("The blow does maximum normal damage and ignores the target's DR.",
				3));
		criticalHeadHit.add(new Entry("The target's DR protects at half value (round up) after applying any armor divisors. If any damage penetrates, treat it as if it were a major wound, regardless of the actual injury inflicted.",
				4, 5));
		criticalHeadHit.add(new Entry("If the attack targeted the face or skull, treat it as an eye hit instead, even if the attack could not normally target the eye! If an eye hit is impossible (e.g., from behind), treat as 4.",
				6, 7));
		criticalHeadHit.add(new Entry("Normal head-blow damage, and the victim is knocked off balance: he must Do Nothing next turn (but may defend normally).",
				8));
		criticalHeadHit.add(new Entry("Normal head-blow damage only.",
				9, 10, 11));
		criticalHeadHit.add(new Entry("Normal head-blow damage, and if any damage penetrates DR, a crushing attack deafens the victim (for recovery, see Duration of Crippling Injuries, p. 422), while any other attack causes severe scarring (the victim loses one appearance level, or two levels if a burning or corrosion attack).",
				12, 13));
		criticalHeadHit.add(new Entry("Normal head-blow damage, and the victim drops his weapon (if he has two weapons, roll randomly to see which one he drops).",
				14));
		criticalHeadHit.add(new Entry("The blow does maximum normal damage.",
				15));
		criticalHeadHit.add(new Entry("The blow does double damage.",
				16));
		criticalHeadHit.add(new Entry("The target's DR protects at half value (round up) after applying any armor divisors.",
				17));
		criticalHeadHit.add(new Entry("The blow does triple damage.",
				18));		 
		//critical_head_hit.add(new Entry(5, "The target's DR protects at half value (round up) after applying any armor divisors. If any damage penetrates, treat it as if it were a major wound, regardless of the actual injury inflicted.");
		//critical_head_hit.add(new Entry(7, "If the attack targeted the face or skull, treat it as an eye hit instead, even if the attack could not normally target the eye! If an eye hit is impossible (e.g., from behind), treat as 4.");
		//critical_head_hit.add(new Entry(10, "Normal head-blow damage only.");
		//critical_head_hit.add(new Entry(11, "Normal head-blow damage only.");
		//critical_head_hit.add(new Entry(13, "Normal head-blow damage, and if any damage penetrates DR, a crushing attack deafens the victim (for recovery, see Duration of Crippling Injuries, p. 422), while any other attack causes severe scarring (the victim loses one appearance level, or two levels if a burning or corrosion attack).");
	}	
	
	public static ArrayList<Entry> criticalMiss = new ArrayList<Entry>();
	static {
		criticalMiss.add(new Entry("Your weapon breaks and is useless. Exception: Certain weapons are resistant to breakage. These include solid crushing weapons (maces, flails, mauls, metal bars, etc.); magic weapons; firearms (other than wheel-locks, guided missiles, and beam weapons); and fine and very fine weapons of all kinds. If you have a weapon like that, roll again. Only if you get a 'broken weapon' result a second time does the weapon really break. If you get any other result, you drop the weapon instead. See Broken Weapons (p. 485).",
				3, 4, 17, 18));
		criticalMiss.add(new Entry("You manage to hit yourself in the arm or leg (50% chance each way). Exception: If making an impaling or piercing melee attack, or any kind of ranged attack, roll again. If you get a 'hit yourself' result a second time, use that result - half or full damage, as the case may be. If you get something other than 'hit yourself,' use that result.",
				5));
		criticalMiss.add(new Entry("As 5, but half damage only.",
				6));
		criticalMiss.add(new Entry("You lose your balance. You can do nothing else (not even a free action) until your next turn, and all your active defenses are at -2 until then.",
				7, 13));
		criticalMiss.add(new Entry("The weapon turns in your hand. You must take an extra Ready maneuver before you can use it again.",
				8, 12));
		criticalMiss.add(new Entry("You drop the weapon. Exception: A cheap weapon breaks; see 3.",
				9, 10, 11));
		criticalMiss.add(new Entry("If making a swinging melee attack, your weapon flies 1d yards from your hand - 50% chance straight forward or straight back. Anyone on the target spot must make a DX roll or take half damage from the falling weapon! If making a thrusting melee attack or any kind of ranged attack, or parrying, you simply drop the weapon, as in 9.",
				14));
		criticalMiss.add(new Entry("You strain your shoulder! Your weapon arm is 'crippled.' You do not have to drop your weapon, but you cannot use it, either to attack or defend, for 30 minutes.",
				15));
		criticalMiss.add(new Entry("You fall down! If making a ranged attack, see 7 instead.",
				16));
		
		//critical_miss.add(new Entry(4, "Your weapon breaks and is useless. Exception: Certain weapons are resistant to breakage. These include solid crushing weapons (maces, flails, mauls, metal bars, etc.); magic weapons; firearms (other than wheel-locks, guided missiles, and beam weapons); and fine and very fine weapons of all kinds. If you have a weapon like that, roll again. Only if you get a 'broken weapon' result a second time does the weapon really break. If you get any other result, you drop the weapon instead. See Broken Weapons (p. 485).");
		//critical_miss.add(new Entry(10, "You drop the weapon. Exception: A cheap weapon breaks; see 3.");
		//critical_miss.add(new Entry(11, "You drop the weapon. Exception: A cheap weapon breaks; see 3.");
		//critical_miss.add(new Entry(12, "The weapon turns in your hand. You must take an extra Ready maneuver before you can use it again.");
		//critical_miss.add(new Entry(13, "You lose your balance. You can do nothing else (not even a free action) until your next turn, and all your active defenses are at -2 until then.");
		//critical_miss.add(new Entry(17, "Your weapon breaks; see 3.");
		//critical_miss.add(new Entry(18, "Your weapon breaks; see 3.");
	}
	
	public static ArrayList<Entry> criticalMissUnarmed = new ArrayList<Entry>();
	static {
		criticalMissUnarmed.add(new Entry("You knock yourself out! Details are up to the GM - perhaps you trip and fall on your head, or walk facefirst into an opponent's fist or shield. Roll vs. HT every 30 minutes to recover.",
				3, 18));
		criticalMissUnarmed.add(new Entry("If attacking or parrying with a limb, you strain it: take 1 HP of injury and the limb is 'crippled.' You cannot use it, either to attack or defend, for 30 minutes. If biting, butting, etc., you pull a muscle and suffer moderate pain (see Irritating Conditions, p. 428) for the next (20 - HT) minutes, minimum one minute.",
				4));
		criticalMissUnarmed.add(new Entry("You hit a solid object (wall, floor, etc.) instead of striking your foe or parrying his attack. You take crushing damage equal to your thrusting damage to the body part you were using; DR protects normally. Exception: If attacking a foe armed with a ready impaling weapon, you fall on his weapon! You suffer the weapon's damage, but based on your ST rather than his.",
				5, 16));
		criticalMissUnarmed.add(new Entry("As 5, but half damage only. Exception: If attacking with natural weapons, such as claws or teeth, they break: -1 damage on future attacks until you heal (for recovery, see Duration of Crippling Injuries, p. 422).",
				6));
		criticalMissUnarmed.add(new Entry("You stumble. On an attack, you advance one yard past your opponent and end your turn facing away from him; he is now behind you! On a parry, you fall down; see 8.",
				7, 14));
		criticalMissUnarmed.add(new Entry("You fall down!",
				8));
		criticalMissUnarmed.add(new Entry("You lose your balance. You can do nothing else (not even a free action) until your next turn, and all your active defenses are at -2 until then.",
				9, 10, 11));
		criticalMissUnarmed.add(new Entry("You trip. Make a DX roll to avoid falling down. Roll at DX-4 if kicking, or at twice the usual DX penalty for a technique that requires a DX roll to avoid mishap even on a normal failure (e.g., DX-8 for a Jump Kick).",
				12));
		criticalMissUnarmed.add(new Entry("You drop your guard. All your active defenses are at -2 for the next turn, and any Evaluate bonus or Feint penalty against you until your next turn counts double! This is obvious to nearby opponents.",
				13));
		criticalMissUnarmed.add(new Entry("You tear a muscle. Take 1d-3 of injury to the limb you used (to one limb, if you used two), or to your neck if biting, butting, etc. You are off balance and at -1 to all attacks and defenses for the next turn. You are at -3 to any action involving that limb (or to any action, if you injure your neck!) until this damage heals. Reduce this penalty to -1 if you have High Pain Threshold.",
				15));
		criticalMissUnarmed.add(new Entry("You strain a limb or pull a muscle, as in 4. Exception: An IQ 3-5 animal fails so miserably that it loses its nerve. It will turn and flee on its next turn, if possible. If backed into a corner, it will assume a surrender position (throat bared, belly exposed, etc.).",
				17));
		//Fighters that cannot fall down (e.g., snakes, and anyone already on the ground): Treat any "fall down" result as 1d-3 of general injury instead. Details are up to the GM - perhaps your opponent steps on you!
		//Fliers and swimmers: Treat any "fall down" result as being forced into an awkward flying or swimming position with the same effective results (-4 to attack, -3 to defend).\
		
		//critical_miss_unarmed.add(new Entry(10, "You lose your balance. You can do nothing else (not even a free action) until your next turn, and all your active defenses are at -2 until then.");
		//critical_miss_unarmed.add(new Entry(11, "You lose your balance. You can do nothing else (not even a free action) until your next turn, and all your active defenses are at -2 until then.");
		//critical_miss_unarmed.add(new Entry(14, "You stumble; see 7.");
		//critical_miss_unarmed.add(new Entry(16, "You hit a solid object; see 5.");
		//critical_miss_unarmed.add(new Entry(18, "You knock yourself out; see 3.");
	}
	
	public static ArrayList<Entry> hitLocation = new ArrayList<Entry>();
	static {
		//hit_location.add(new Entry("Eye", 3, 18));
		hitLocation.add(new Entry("Skull", 3, 4));
		hitLocation.add(new Entry("Face", 5));
		hitLocation.add(new Entry("Right Leg", 6, 7));
		hitLocation.add(new Entry("Right Arm", 8));
		hitLocation.add(new Entry("Torso", 9, 10));
		hitLocation.add(new Entry("Groin", 11));
		hitLocation.add(new Entry("Left Arm", 12));
		hitLocation.add(new Entry("Left Leg", 13, 14));
		hitLocation.add(new Entry("Hand", 15));
		hitLocation.add(new Entry("Foot", 16));
		hitLocation.add(new Entry("Neck", 17, 18));
		//hit_location.add(new Entry("Vitals", ));
		
	}
}
