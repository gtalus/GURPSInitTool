package gurpsinittool.data;

import java.io.Serializable;

//TODO: implement change tracking? Only way to actually track actor changes all the way down
public class Attack implements Serializable {
	// Default SVUID
	private static final long serialVersionUID = 1L;

	public String Name;
	public int Skill;
	public String Damage; // Ex: 2d+2 cut, 2d(2) pi-
	public boolean Unbalanced;
	
	//public DiceAdds Damage;
	// _ => -
	// 4 => +
	//public enum DamageType {aff, burn, cor, cr, cut, fat, imp, pi_, pi, pi4, pi44, spec, tox};

	// Other? Reach, ready, unbalanced
	
	/**
	 * Default constructor
	 */
	public Attack() {
		Name = "New Attack";
		Skill = 10;
		Damage = "1d cut";
		Unbalanced = false;
	}
	
	/**
	 * Basic constructor specifying all options
	 */	
	public Attack(String name, int skill, String damage, boolean unbalanced) {
		Name = name;
		Skill = skill;
		Damage = damage;
		Unbalanced = unbalanced;
	}
}
