package gurpsinittool.data;

import gurpsinittool.data.Damage.DamageType;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HitLocations {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(HitLocations.class.getName());
	
	public enum LocationType {Torso, Skull, Face, Leg, Knee, Arm, Hand, Foot, Neck, Vitals, Eye, Groin};
	// rleg, rarm, lleg, larm
	//abdomen, ear, nose, jaw, spine, limb_vasc, neck_vasc, limb_joint (knee/elbow), extremity_joint (wrist/ankle), pelvis, digestive, heart};

	public static HitLocation getLocation(LocationType type) {
		return locations.get(type);
	}
	public static HitLocation getLocationFromName(String name) {
		for (HitLocations.LocationType location: HitLocations.LocationType.values()) {
			if (location.name().equals(name)) {
				return locations.get(location);
			}
		}
		if (LOG.isLoggable(Level.INFO)) {LOG.info("unable to find location based on name: " + name);}
		return null;
	}
	
	private static HashMap<LocationType, HitLocation> locations = new HashMap<LocationType, HitLocation>();
	private static void addLocation(HitLocation location) {
		locations.put(location.type, location);
	}
	static {
		addLocation(new HitLocation(LocationType.Skull, -7, "Penalty: -7(f)/-5(b). Attack that misses by 1 hits the torso instead. "
				+ "The skull gets an extra DR 2. Wounding modifier is ×4. "
				+ "Knockdown rolls are at -10. Critical hits use the Critical Head Blow Table (B556). "
				+ "Exception: These special effects do not apply to tox damage.", -10, true, 2) {
			public double damageMultiplier(DamageType type) {
				return 4;
			}
		});
		addLocation(new HitLocation(LocationType.Face, -5, "Penalty: -5(f)/-7(b). Attack that misses by 1 hits the torso instead. "
				+ "Jaw, cheeks, nose, ears, etc. If the target has an open-faced helmet, ignore its DR. Knockdown rolls are at -5. "
				+ "Critical hits use the Critical Head Blow Table (B556). "
				+ "Cor damage gets a ×1.5 wounding modifier, and if it inflicts a major wound, it also blinds one eye (both eyes on damage over full HP).",
				-5, true) {
			public double damageMultiplier(DamageType type) {
				switch (type) {
				case cor:
					return 1.5;
				default:
					return super.damageMultiplier(type);
				}
			}
		});
		addLocation(new HitLocation(LocationType.Leg, -2, "Reduce the wounding multiplier of large pi, huge pi and imp damage to ×1. "
				+ "Any major wound (loss of over 1/2 HP from one blow) cripples the limb. "
				+ "Damage beyond that threshold is lost. Crit hits a joint.", 0, 1.0/2)  {
			public double damageMultiplier(DamageType type) {
				switch (type) {
				case pi4:
				case pi44:
				case imp:
					return 1;
				default:
					return super.damageMultiplier(type);
				}
			}
		});
		addLocation(new HitLocation(LocationType.Knee, -5, "Reduce the wounding multiplier of large pi, huge pi and imp damage to ×1. "
				+ "Any major wound (loss of over 1/3 HP from one blow) cripples the limb. HT rolls to recover from crippling are at -2. "
				+ "Damage beyond that threshold is lost. Dismemberment requires same amount as Leg. Miss by 1 hits the leg.", 0, 1.0/3)  {
			public double damageMultiplier(DamageType type) {
				switch (type) {
				case pi4:
				case pi44:
				case imp:
					return 1;
				default:
					return super.damageMultiplier(type);
				}
			}
		});
		addLocation(new HitLocation(LocationType.Arm, -2, "If holding a shield, double the penalty to hit. Reduce the wounding multiplier of large pi, huge pi and imp damage to ×1. "
				+ "Any major wound (loss of over 1/2 HP from one blow) cripples the limb. "
				+ "Damage beyond that threshold is lost. Crit hits a joint.", 0, 1.0/2) {
			public double damageMultiplier(DamageType type) {
				switch (type) {
				case pi4:
				case pi44:
				case imp:
					return 1;
				default:
					return super.damageMultiplier(type);
				}
			}
		});
		addLocation(new HitLocation(LocationType.Torso, 0, ""));
		addLocation(new HitLocation(LocationType.Hand, -4, "If holding a shield, double the penalty to hit. Reduce the wounding multiplier of large pi, huge pi and imp damage to ×1. "
				+ "Any major wound (loss of over 1/3 HP from one blow) cripples the extremity. "
				+ "Damage beyond that threshold is lost. Crit hits a joint.", 0, 1.0/3) {
			public double damageMultiplier(DamageType type) {
				switch (type) {
				case pi4:
				case pi44:
				case imp:
					return 1;
				default:
					return super.damageMultiplier(type);
				}
			}
		});
		addLocation(new HitLocation(LocationType.Foot, -4, "Reduce the wounding multiplier of large pi, huge pi and imp damage to ×1. "
				+ "Any major wound (loss of over 1/3 HP from one blow) cripples the extremity. "
				+ "Damage beyond that threshold is lost. Crit hits a joint.", 0, 1.0/3) {
			public double damageMultiplier(DamageType type) {
				switch (type) {
				case pi4:
				case pi44:
				case imp:
					return 1;
				default:
					return super.damageMultiplier(type);
				}
			}
		});
		addLocation(new HitLocation(LocationType.Neck, -5, "Attack that misses by 1 hits the torso instead. "
				+ "Increase the wounding multiplier of cr and cor attacks to ×1.5, and that of cutting damage to ×2. "
				+ "At the GM’s option, anyone killed by a cutting blow to the neck is decapitated!") {
			public double damageMultiplier(DamageType type) {
				switch (type) {
				case cr:
				case cor:
					return 1.5;
				case cut:
					return 2;
				default:
					return super.damageMultiplier(type);
				}
			}
		});
		addLocation(new HitLocation(LocationType.Vitals, -3, "Attack that misses by 1 hits the torso instead. "
				+ "Heart, lungs, kidneys, etc. Increase wounding modifier for imp or pi attack to ×3. "
				+ "Increase wounding modifier for tbb attack to ×2. cr is only ×1. "
				+ "If caused shock roll vs. knockdown, -5 if major wound, other attacks cannot target the vitals.", -5) {
			public double damageMultiplier(DamageType type) {
				switch (type) {
				case imp:
				case pi:
					return 3;
				case tbb:
					return 2;
				default:
					return super.damageMultiplier(type);
				}
			}
		});
		addLocation(new HitLocation(LocationType.Eye, -9, "Attack that misses by 1 hits the torso instead. "
				+ "Only imp, pi, and tbb attacks can target the eye – and only from the front or sides. "
				+ "Injury over HP/10 blinds the eye. Wounding modifier is ×4. "
				+ "Knockdown rolls are at -10. Critical hits use the Critical Head Blow Table (B556). "
				+ "Exception: These special effects do not apply to tox damage.", -10, true) {
			public double damageMultiplier(DamageType type) {
				return 4;
			}
		});
		addLocation(new HitLocation(LocationType.Groin, -3, "Attack that misses by 1 hits the torso instead. "
				+ "Human males/males of similar species suffer double shock from cr damage, and get -5 to knockdown rolls. "
				+ "Otherwise, treat as a torso hit.", -5));
	}
	
	
	public static class HitLocation {

		//public int roll;
		public LocationType type;
		public int penalty;
		public String notes;
		
		public int extraDR;
		public int knockdownPenalty;
		public boolean headWound;
		
		// If non-0, the crippling threshold is used instead of the base major wound threshold for knockdown/stunning
		public double cripplingThreshold = 0;
		
		HitLocation(LocationType type, int penalty, String notes) {
			this(type, penalty, notes, 0, false, 0);
		}

		HitLocation(LocationType type, int penalty, String notes, int knockdownPenalty) {
			this(type, penalty, notes, knockdownPenalty, false, 0);
		}

		HitLocation(LocationType type, int penalty, String notes, int knockdownPenalty, double cripplingThreshold) {
			this(type, penalty, notes, knockdownPenalty, false, 0);
			this.cripplingThreshold = cripplingThreshold;
		}

		HitLocation(LocationType type, int penalty, String notes, int knockdownPenalty, boolean headWound) {
			this(type, penalty, notes, knockdownPenalty, headWound, 0);
		}
		
		HitLocation(LocationType type, int penalty, String notes, int knockdownPenalty, boolean headWound, int extraDR) {
			//this.roll = roll;
			this.type = type;
			this.penalty = penalty;
			this.notes = notes;
			this.knockdownPenalty = knockdownPenalty;
			this.headWound = headWound;
			this.extraDR = extraDR;
		}
		
		public double damageMultiplier(DamageType type) {
			return Damage.damageMultiplier(type);
		}
		
	}

}
