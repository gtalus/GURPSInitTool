package gurpsinittool.data;

import java.io.Serializable;

//TODO: implement change tracking? Only way to actually track actor changes all the way down
public class Attack implements Serializable {
	// Default SVUID
	private static final long serialVersionUID = 1L;

	public String name;
	public int skill;
	public String damage; // Ex: 2d+2 cut, 2d(2) pi-
	public boolean unbalanced;
	
	// Other? Reach, ready, unbalanced
	
	/**
	 * Default constructor
	 */
	public Attack() {
		name = "New Attack";
		skill = 10;
		damage = "1d cut";
		unbalanced = false;
	}
	
	/**
	 * Basic constructor specifying all options
	 */	
	public Attack(String name, int skill, String damage, boolean unbalanced) {
		this.name = name;
		this.skill = skill;
		this.damage = damage;
		this.unbalanced = unbalanced;
	}

	public Attack(Attack attack) {
		this(attack.name, attack.skill, attack.damage, attack.unbalanced);
	}
}
