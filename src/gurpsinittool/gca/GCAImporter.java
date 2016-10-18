package gurpsinittool.gca;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gurpsinittool.data.Actor;
import gurpsinittool.data.ActorBase.ActorType;
import gurpsinittool.data.ActorBase.BasicTrait;
import gurpsinittool.util.MiscUtil;
import gurpsinittool.data.Attack;

/**
 * Utility class to convert GCACharacter to Actor
 * @author dcsmall
 *
 */
public class GCAImporter {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(GCAImporter.class.getName());
	
	// Convenience method
	public static Actor importActor(File file) throws FileNotFoundException {
		GCACharacter character = GCAFile.loadGCA4File(file);
		return convertToActor(character);
	}
	
	public static Actor convertToActor(GCACharacter character) {
		Actor actor = new Actor();
		actor.setType(ActorType.Special);
		
		// First go through the basic values
		processBasicTraits(actor, character);
		// Then get the optional Traits
		processOptionalTraits(actor, character);
		// Load up the Attacks
		processAttacks(actor, character);
		// Notes from various sources
		processNotes(actor, character);

		
		// TODO: damage resistance!
		
		return actor;
	}	
	
	private static void processBasicTraits(final Actor actor, final GCACharacter character) {
		processSimpleKey(actor, character, "charname", BasicTrait.Name);
		processStat(actor, character, "ST", BasicTrait.ST);
		processStat(actor, character, "Hit Points", BasicTrait.HP);
		processStat(actor, character, "HT", BasicTrait.HT);
		processStat(actor, character, "Fatigue Points", BasicTrait.FP);
		processStat(actor, character, "IQ", BasicTrait.IQ);
		processStat(actor, character, "Will", BasicTrait.Will);
		processStat(actor, character, "Perception", BasicTrait.Per);
		processStat(actor, character, "DX", BasicTrait.DX);
		processStat(actor, character, "Basic Speed", BasicTrait.Speed);
		processStat(actor, character, "Basic Move", BasicTrait.Move);
		processStat(actor, character, "Size Modifier", BasicTrait.SM);

		processStat(actor, character, "Dodge", BasicTrait.Dodge);
		processSimpleKey(actor, character, "parryscore", BasicTrait.Parry);
		processSimpleKey(actor, character, "blockscore", BasicTrait.Block);	
		
		processDR(actor, character);
		processShield(actor, character);
	}
	
	private static void processDR(final Actor actor, final GCACharacter character) {
		String value = "";
		if (character.hasBasicValue("drbody"))
			value += character.getBasicValue("drbody");
		final GCATrait drTrait = character.findTraitByName("Stats", "DR");
		if (drTrait != null) {
			final Integer drValue = MiscUtil.parseIntSafe(drTrait.getValue("score"));
			if (drValue > 0) {
				value += (value.length()>0?"+":"") + drValue;
			}			
		}
		actor.setTrait(BasicTrait.DR, value);
	}
	
	private static void processShield(final Actor actor, final GCACharacter character) {
		// Shield - seems to be more reliably based off of equipment? See data in shieldArc after re-saving
		actor.setTrait(BasicTrait.Shield_DB, "0");
		actor.setTrait(BasicTrait.Shield_DR, "0");
		actor.setTrait(BasicTrait.Shield_HP, "0");
		// get equipment traits
		for( GCATrait trait : character.findTraitsInSection("Equipment")) {			
			if (trait.hasValue("shieldarc")) {
				if (LOG.isLoggable(Level.FINER)) {LOG.finer("Using shield arc from trait: " + trait.getName());}
				if (trait.hasValue("db")) 
					actor.setTrait(BasicTrait.Shield_DB, trait.getValue("db"));
				if (trait.hasValue("dr")) {
					String dr = trait.getValue("dr");
					Pattern drPattern = Pattern.compile("^(\\d+)/(\\d+)$");
					Matcher matcher = drPattern.matcher(dr);
					if (matcher.matches()) {
						actor.setTrait(BasicTrait.Shield_DR, matcher.group(1));
						actor.setTrait(BasicTrait.Shield_HP, matcher.group(2));
					}
				}
			}						
		} 
	}
	
	private static void processOptionalTraits(final Actor actor, final GCACharacter character) {		
		processBooleanTrait(actor, character, "Advantages", "High Pain Threshold");
		processBooleanTrait(actor, character, "Disadvantages", "Low Pain Threshold");
		//Defense values should already have bonus, 
		// but it does seem that they can be left in a stale state by GCA :( 
		processBooleanTrait(actor, character, "Advantages", "Combat Reflexes");		
		processIntTrait(actor, character, "Advantages", "Striking ST");
		processIntTrait(actor, character, "Advantages", "Lifting ST");
		processModsTrait(actor, character, "Advantages", "Injury Tolerance");

		//encumbrancelevel/current load?
		// penalize dodge?		


	}
	
	private static void processAttacks(final Actor actor, final GCACharacter character) {		
		// Equipment based
		for( GCATrait trait : character.findTraitsInSection("Equipment")) {
			processTraitAttacks(actor, trait);
		}
		
		// non-equipment based
		for( GCATrait trait : character.findTraitsInSection("Skills")) {
			processTraitAttacks(actor, trait);
		}
		for( GCATrait trait : character.findTraitsInSection("Stats")) {
			processTraitAttacks(actor, trait);
		}
	}
	
	private static void processNotes(final Actor actor, final GCACharacter character) {
		String notes = "";
		// Appearance
		if (character.hasBasicValue("appearance")) {
			final String appearance = character.getBasicValue("appearance");
			if (!appearance.isEmpty())
				notes = "Appearance: " + appearance + "\n\n";
		}
		
		// RTF Description
		if (character.hasBasicValue("RTFDescription")) {
			String rtfString = character.getBasicValue("RTFDescription");
			String decodedString = MiscUtil.decodeRTFNotes(rtfString);
			if (decodedString != null) {
				notes += "Description:\n" + decodedString + "\n";
			} 
		}
		
		// RTF Notes
		if (character.hasBasicValue("RTFNotes")) {
			String rtfString = character.getBasicValue("RTFNotes");
			String decodedString = MiscUtil.decodeRTFNotes(rtfString);
			if (decodedString != null) {
				notes += "Notes:\n" + decodedString + "\n";
			} 
		}
		
		actor.setTrait(BasicTrait.Notes, notes);
	}
	
	private static void processSimpleKey(final Actor actor, final GCACharacter character, final String key, final BasicTrait trait) {
		if (character.hasBasicValue(key)) {
			actor.setTrait(trait, character.getBasicValue(key));
		} else {
			if (LOG.isLoggable(Level.INFO)) {LOG.info("Unable to find key '" + key + "' to set trait " + trait);}
			actor.setTrait(trait, "");			
		}		
	}
	
	private static void processTraitAttacks(final Actor actor, final GCATrait trait) {
		boolean hasAttacks = false;
		// Melee attacks are based on 'reach'
		/**
		 * charskillscore, chardamage, chardamtype, charparry
		 */
		if(trait.hasValue("reach") && !trait.getValue("reach").isEmpty()) {
			if (LOG.isLoggable(Level.FINE)) {LOG.fine("Trait '" + trait.getName() + "' has melee attacks.");}
			hasAttacks = true;
		}		
		// Ranged attacks are based on 'rangemax'
		/**
		 * charrangehalfdam, charrangemax, charskillscore, chardamtype, chardamage
		 */
		if(trait.hasValue("rangemax") && !trait.getValue("rangemax").isEmpty()) {
			if (LOG.isLoggable(Level.FINE)) {LOG.fine("Trait '" + trait.getName() + "' has ranged attacks.");}
			hasAttacks = true;
		}
		// Process the attacks
		if (hasAttacks) {
			final List<String> modes = trait.getValueAsList("mode");
			//List<String> reaches = trait.getValueAsList("charreach");
			//List<String> rangemaxs = trait.getValueAsList("charrangemax");
			final List<String> skills = trait.getValueAsList("charskillscore");
			final List<String> damages = trait.getValueAsList("chardamage");
			final List<String> damageTypes = trait.getValueAsList("chardamtype");
			final List<String> parrys = trait.getValueAsList("parry");
			final Integer numAttacks = (modes.isEmpty()?1:modes.size());
			if (LOG.isLoggable(Level.FINER)) {LOG.finer("Trait '" + trait.getName() + " has " + numAttacks + " attacks");}			
			for(int i = 0; i < numAttacks; i++) {
				String name = trait.getName();
				// Don't append mode if just one attack, it's not present or same as the trait name
				if (numAttacks > 1 && modes.size()>i && !modes.get(i).equals(name)) 
					name += ": " + modes.get(i);
				final String skill = (skills.size()>i)?skills.get(i):"";
				final Integer intSkill = MiscUtil.parseIntSafe(skill);
				final String damage = (damages.size()>i)?damages.get(i):"";
				final String damageType = (damageTypes.size()>i)?damageTypes.get(i):"";
				final Boolean unbalanced = (parrys.size()>i)?parrys.get(i).endsWith("U"):false;
				// TODO: Add reach/range note?
				if (LOG.isLoggable(Level.FINEST)) {LOG.finest("Adding attack '" + name + "'");}
				actor.addAttack(new Attack(name, intSkill, damage + " " + damageType, unbalanced));
			}
		}
	}
	


	
	// Process trait with mods (children)
	private static void processModsTrait(final Actor actor, final GCACharacter character, final String sectionName, final String traitName) {
		GCATrait trait = character.findTraitByName(sectionName, traitName);
		if (trait == null) {
			if (LOG.isLoggable(Level.FINEST)) {LOG.finest("Found no trait named '" + traitName + "' with section '" + sectionName + "'");}
		}  else {
			List<String> values = new ArrayList<String>();
			for (GCATrait child : trait.getChildren()) {
				if (LOG.isLoggable(Level.FINEST)) {LOG.finest("Processing: child '" + child.getName() + "'");}
				values.add(child.getName());		
			}
			String value = String.join(";", values);
			actor.addTrait(traitName, value);
		} 
	}
	
	// Process simple yes/no optional traits
	private static boolean processBooleanTrait(final Actor actor, final GCACharacter character, final String sectionName, final String traitName) {
		if (character.findTraitByName(sectionName, traitName) != null) {
			actor.addTrait(traitName, "");
			return true;
		}
		return false;
	}
	
	// Process traits where we expect an integer level
	private static void processIntTrait(final Actor actor, final GCACharacter character, final String sectionName, final String traitName) {
		GCATrait trait = character.findTraitByName(sectionName, traitName);
		if (trait == null) {
			if (LOG.isLoggable(Level.FINEST)) {LOG.finest("Found no trait named '" + traitName + "' with section '" + sectionName + "'");}
		} else {
			if (trait.hasValue("level")) {
				String value = trait.getValue("level");
				if (value.endsWith(".0"))
					value = value.substring(0, value.length()-2);
				actor.addTrait(traitName, value);
			} else {
				if (LOG.isLoggable(Level.INFO)) {LOG.info("Trait '" + traitName + "' does not have 'level' value");}
			}
		} 
	}
	
	private static void processStat(final Actor actor, final GCACharacter character, final String traitName, final BasicTrait basicTrait) {
		GCATrait trait = character.findTraitByName("Stats", traitName);
		if (trait == null) {
			if (LOG.isLoggable(Level.INFO)) {LOG.info("Unable to find trait named '" + traitName + "' to set trait " + basicTrait);}
		} else {
			if (trait.hasValue("score")) {
				String value = trait.getValue("score");
				if (value.endsWith(".0"))
					value = value.substring(0, value.length()-2);
				actor.setTrait(basicTrait, value);
			} else {
				if (LOG.isLoggable(Level.INFO)) {LOG.info("Trait '" + traitName + "' does not have 'score' value to set trait " + basicTrait);}
				actor.setTrait(basicTrait, 0);
			} 
		}
	}
}
