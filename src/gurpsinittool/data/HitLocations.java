package gurpsinittool.data;

import gurpsinittool.data.Damage.DamageType;

import java.util.HashMap;

public class HitLocations {
	
	public enum LocationType {torso, skull, face, leg, knee, arm, hand, foot, neck, vitals, eye, groin};
	// rleg, rarm, lleg, larm
	//abdomen, ear, nose, jaw, spine, limb_vasc, neck_vasc, limb_joint (knee/elbow), extremity_joint (wrist/ankle), pelvis, digestive, heart};

	public static HitLocation getLocationFromName(String name) {
		for (HitLocations.LocationType location: HitLocations.LocationType.values()) {
			if (locations.get(location).description == name) {
				return locations.get(location);
			}
		}
		System.out.println("-E- HitLocations.getLocationFromName: unable to find location based on name: " + name);
		return null;
	}
	
	public static HashMap<LocationType, HitLocation> locations = new HashMap<LocationType, HitLocation>();
	static {
		locations.put(LocationType.skull, new HitLocation("Skull", -7, "Penalty: -7(f)/-5(b). Attack that misses by 1 hits the torso instead. "
				+ "The skull gets an extra DR 2. Wounding modifier is ×4. "
				+ "Knockdown rolls are at -10. Critical hits use the Critical Head Blow Table (B556). "
				+ "Exception: These special effects do not apply to tox damage.", -10, true, 2) {
			public double DamageMultiplier(DamageType type) {
				return 4;
			}
		});
		locations.put(LocationType.face, new HitLocation("Face", -5, "Penalty: -5(f)/-7(b). Attack that misses by 1 hits the torso instead. "
				+ "Jaw, cheeks, nose, ears, etc. If the target has an open-faced helmet, ignore its DR. Knockdown rolls are at -5. "
				+ "Critical hits use the Critical Head Blow Table (B556). "
				+ "Cor damage gets a ×1.5 wounding modifier, and if it inflicts a major wound, it also blinds one eye (both eyes on damage over full HP).",
				-5, true) {
			public double DamageMultiplier(DamageType type) {
				switch (type) {
				case cor:
					return 1.5;
				default:
					return super.DamageMultiplier(type);
				}
			}
		});
		locations.put(LocationType.leg, new HitLocation("Leg", -2, "Reduce the wounding multiplier of large pi, huge pi and imp damage to ×1. "
				+ "Any major wound (loss of over 1/2 HP from one blow) cripples the limb. "
				+ "Damage beyond that threshold is lost. Crit hits a joint.", 0, 1.0/2)  {
			public double DamageMultiplier(DamageType type) {
				switch (type) {
				case pi4:
				case pi44:
				case imp:
					return 1;
				default:
					return super.DamageMultiplier(type);
				}
			}
		});
		locations.put(LocationType.knee, new HitLocation("Knee", -5, "Reduce the wounding multiplier of large pi, huge pi and imp damage to ×1. "
				+ "Any major wound (loss of over 1/3 HP from one blow) cripples the limb. HT rolls to recover from crippling are at -2. "
				+ "Damage beyond that threshold is lost. Dismemberment requires same amount as Leg. Miss by 1 hits the leg.", 0, 1.0/3)  {
			public double DamageMultiplier(DamageType type) {
				switch (type) {
				case pi4:
				case pi44:
				case imp:
					return 1;
				default:
					return super.DamageMultiplier(type);
				}
			}
		});
		locations.put(LocationType.arm, new HitLocation("Arm", -2, "If holding a shield, double the penalty to hit. Reduce the wounding multiplier of large pi, huge pi and imp damage to ×1. "
				+ "Any major wound (loss of over 1/2 HP from one blow) cripples the limb. "
				+ "Damage beyond that threshold is lost. Crit hits a joint.", 0, 1.0/2) {
			public double DamageMultiplier(DamageType type) {
				switch (type) {
				case pi4:
				case pi44:
				case imp:
					return 1;
				default:
					return super.DamageMultiplier(type);
				}
			}
		});
		locations.put(LocationType.torso, new HitLocation("Torso", 0, ""));
		locations.put(LocationType.hand, new HitLocation("Hand", -4, "If holding a shield, double the penalty to hit. Reduce the wounding multiplier of large pi, huge pi and imp damage to ×1. "
				+ "Any major wound (loss of over 1/3 HP from one blow) cripples the extremity. "
				+ "Damage beyond that threshold is lost. Crit hits a joint.", 0, 1.0/3) {
			public double DamageMultiplier(DamageType type) {
				switch (type) {
				case pi4:
				case pi44:
				case imp:
					return 1;
				default:
					return super.DamageMultiplier(type);
				}
			}
		});
		locations.put(LocationType.foot, new HitLocation("Foot", -4, "Reduce the wounding multiplier of large pi, huge pi and imp damage to ×1. "
				+ "Any major wound (loss of over 1/3 HP from one blow) cripples the extremity. "
				+ "Damage beyond that threshold is lost. Crit hits a joint.", 0, 1.0/3) {
			public double DamageMultiplier(DamageType type) {
				switch (type) {
				case pi4:
				case pi44:
				case imp:
					return 1;
				default:
					return super.DamageMultiplier(type);
				}
			}
		});
		locations.put(LocationType.neck, new HitLocation("Neck", -5, "Attack that misses by 1 hits the torso instead. "
				+ "Increase the wounding multiplier of cr and cor attacks to ×1.5, and that of cutting damage to ×2. "
				+ "At the GM’s option, anyone killed by a cutting blow to the neck is decapitated!") {
			public double DamageMultiplier(DamageType type) {
				switch (type) {
				case cr:
				case cor:
					return 1.5;
				case cut:
					return 2;
				default:
					return super.DamageMultiplier(type);
				}
			}
		});
		locations.put(LocationType.vitals, new HitLocation("Vitals", -3, "Attack that misses by 1 hits the torso instead. "
				+ "Heart, lungs, kidneys, etc. Increase wounding modifier for imp or pi attack to ×3. "
				+ "Increase wounding modifier for tbb attack to ×2. cr is only ×1. "
				+ "If caused shock roll vs. knockdown, -5 if major wound, other attacks cannot target the vitals.", -5) {
			public double DamageMultiplier(DamageType type) {
				switch (type) {
				case imp:
				case pi:
					return 3;
				case tbb:
					return 2;
				default:
					return super.DamageMultiplier(type);
				}
			}
		});
		locations.put(LocationType.eye, new HitLocation("Eye", -9, "Attack that misses by 1 hits the torso instead. "
				+ "Only imp, pi, and tbb attacks can target the eye – and only from the front or sides. "
				+ "Injury over HP/10 blinds the eye. Wounding modifier is ×4. "
				+ "Knockdown rolls are at -10. Critical hits use the Critical Head Blow Table (B556). "
				+ "Exception: These special effects do not apply to tox damage.", -10, true) {
			public double DamageMultiplier(DamageType type) {
				return 4;
			}
		});
		locations.put(LocationType.groin, new HitLocation("Groin", -3, "Attack that misses by 1 hits the torso instead. "
				+ "Human males/males of similar species suffer double shock from cr damage, and get -5 to knockdown rolls. "
				+ "Otherwise, treat as a torso hit.", -5));
	}
	
	
	public static class HitLocation {

		//public int roll;
		public String description;
		public int penalty;
		public String notes;
		
		public int extraDR;
		public int knockdownPenalty;
		public boolean headWound;
		
		// TODO: add crippling threshold (also knockdown/stunning threshold)
		public double cripplingThreshold = 0;
		
		HitLocation(String description, int penalty, String notes) {
			this(description, penalty, notes, 0, false, 0);
		}

		HitLocation(String description, int penalty, String notes, int knockdownPenalty) {
			this(description, penalty, notes, knockdownPenalty, false, 0);
		}

		HitLocation(String description, int penalty, String notes, int knockdownPenalty, double cripplingThreshold) {
			this(description, penalty, notes, knockdownPenalty, false, 0);
			this.cripplingThreshold = cripplingThreshold;
		}

		HitLocation(String description, int penalty, String notes, int knockdownPenalty, boolean headWound) {
			this(description, penalty, notes, knockdownPenalty, headWound, 0);
		}
		
		HitLocation(String description, int penalty, String notes, int knockdownPenalty, boolean headWound, int extraDR) {
			//this.roll = roll;
			this.description = description;
			this.penalty = penalty;
			this.notes = notes;
			this.knockdownPenalty = knockdownPenalty;
			this.headWound = headWound;
			this.extraDR = extraDR;
		}
		
		public double DamageMultiplier(DamageType type) {
			return Damage.DamageMultiplier(type);
		}
		
	}

}
