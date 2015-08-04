package gurpsinittool.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultTreeModel;

import gurpsinittool.app.GroupTree;
import gurpsinittool.app.GroupTreeNode;
import gurpsinittool.data.Actor.ActorStatus;
import gurpsinittool.data.Actor.ActorType;
import gurpsinittool.data.Actor.BasicTrait;

/**
 * This class encapsulates all file IO for saving/loading actor groups & lists
 * @author dcsmall
 *
 */
public class ActorGroupFile {

	private static final boolean DEBUG = false;

	public static int currentSchemaVer = 5; // Schema version. Incremented when major changes to format are made
	
	/**
	 * Open a file and populate the GroupTree with its contents
	 * @param groupTree : the GroupTree to populate. Overwrites previous contents.
	 * @param openFile : the file to load
	 */
	public static void OpenActorGroupTree(GroupTree groupTree, File openFile) {
		// Open the file
		FileReader file;
		BufferedReader input;
		try {
			file = new FileReader(openFile);
			input = new BufferedReader(file);
		}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return;
		}
		
		DefaultTreeModel treeModel = new DefaultTreeModel(new GroupTreeNode("Groups",true));
		// Start reading the file contents
  		if (DEBUG) { System.out.println("ActorGroupFile: Reading input file: " + openFile.getPath()); }   	
		try {
			String line;
			Matcher matcher;
			Pattern startFile = Pattern.compile("^<GURPSActorGroupList schemaVer=\"(\\d+)\">$");
				
			line = input.readLine();
			if (!(matcher = startFile.matcher(line)).matches()) {
				input.close();
				System.err.println("Error: first line does not specify start of ActorGroupList!");
				return;
			}
			int schemaVer = Integer.parseInt(matcher.group(1));
			if (DEBUG) { System.out.println("ActorGroupFile: Found start of Group List, schema version: " + schemaVer); }   
			
			switch (schemaVer) {
				case 0:
					readSchema0(input,treeModel);
					break;
				case 1:
					readSchema1(input,treeModel);
					break;
				case 2:
					readSchema2(input,treeModel);
					break;
				case 3:
					readSchema3(input,treeModel);
					break;
				case 4:
					readSchema4(input,treeModel);
					break;
				case 5:
					readSchema5(input,treeModel);
					break;
				default:
					System.err.println("ActorGroupFile: Unknown schema version: " + schemaVer + "!");
			}
			
			input.close();
			file.close();
		}
		catch (IOException ex){
			ex.printStackTrace();
			return;
		}
		
		// Set Tree model
	    groupTree.setModel(treeModel);
	}
	
	/**
	 * Save the data in the GroupTree to a file on the disk.
	 * @param groupTree : The GroupTree to get the Actor & hierarchy data from
	 * @param filename : File to save to
	 */
	public static void SaveActorGroupTree(GroupTree groupTree, File saveFile) {
		StringBuffer buffer = new StringBuffer();
		
		// Traverse tree
		GroupTreeNode rootNode = (GroupTreeNode) groupTree.getModel().getRoot();
		ArrayList<GroupTreeNode> currentNodes = new ArrayList<GroupTreeNode>(Arrays.asList(rootNode));
		ArrayList<Integer> currentPositions = new ArrayList<Integer>(Arrays.asList(0));
		buffer.append("<GURPSActorGroupList schemaVer=\"" + currentSchemaVer + "\">\n");
		buffer.append("<GroupFolder name=\"" + rootNode.toString() + "\">\n");
		while (currentNodes.size() > 0) {
			if (currentPositions.get(0) >= currentNodes.get(0).getChildCount())	{
				currentNodes.remove(0);
				currentPositions.remove(0);
				buffer.append("</GroupFolder>\n");
	      		if (DEBUG) { System.out.println("ActorGroupFile: End of folder"); }   	
			}
			else {
				GroupTreeNode node = (GroupTreeNode) currentNodes.get(0).getChildAt(currentPositions.get(0));
				currentPositions.set(0, currentPositions.get(0)+1); // Increment position
				if (node.isFolder()) { // GroupFolder
					buffer.append("<GroupFolder name=\"" + node.toString() + "\">\n");
					currentNodes.add(0, node);
					currentPositions.add(0,0);
		      		if (DEBUG) { System.out.println("ActorGroupFile: Start of folder: " + node.toString()); }   	
				}
				else { // Group
					buffer.append("<ActorGroup name=\"" + node.toString() + "\">\n");
					ArrayList<Actor> actorList = node.getActorList();
					for(int i = 0; i < actorList.size()-1; i++) {
						buffer.append(SerializeActor(actorList.get(i)));
					}
					buffer.append("</ActorGroup>\n");
				}
			}
		}
		buffer.append("</GURPSActorGroupList>");
		
		// Write the file
		String filename = saveFile.getPath();
		try {
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(buffer.toString());
			out.close();
		}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	/**
	 * Serialize an actor into a string in XML format
	 * @param actor : the Actor to serialize
	 * @return A string with the serialized Actor
	 */
	public static String SerializeActor(Actor actor) {
		StringWriter stringWriter = new StringWriter();
		stringWriter.append("<Actor"
				+ " name=\"" + actor.getTraitValue(BasicTrait.Name) + "\""
				+ " default_attack=\"" + actor.getDefaultAttack() + "\""
				+ " state=\"[" + actor.getStatusesString() + "]\""
				+ " type=\"" + actor.getType().toString() + "\""
				+ ">\n");
		
		// Serialize traits
		for (Object value : actor.traits.values()) {
			stringWriter.append(SerializeTrait((Actor.Trait) value));
		}
				
		for (int i=0; i < actor.getNumAttacks(); ++i) {
			stringWriter.append(SerializeAttack(actor.getAttack(i)));
		}
		
		stringWriter.append("</Actor>\n");
		return stringWriter.toString();
	}
	
	public static String SerializeTrait(Actor.Trait trait) {
		return "<" + trait.name + ">" + trait.value + "</" + trait.name + ">\n";
	}
	
	public static String SerializeAttack(Attack attack) {
		StringWriter stringWriter = new StringWriter();
		stringWriter.append("<Attack name=\"" + attack.Name
				+ "\" skill=\"" + attack.Skill
				+ "\" damage=\"" + attack.Damage
				+ "\" unbalanced=\"" + attack.Unbalanced
				+ "\" />\n");
		return stringWriter.toString();
	}

	/**
	 * Helper function to parse the old 'State' field
	 * @param state - String containing state
	 * @return value suitable for passing to Actor constructor
	 */
	public static HashSet<ActorStatus> parseOldState(String state) {
		HashSet<ActorStatus> status = new HashSet<ActorStatus>();
		if (!state.equals("Active")) 
			status.add(ActorStatus.valueOf(state));
		return status;
	}
	
	/**
	 * Helper function to parse the new 'State' field
	 * @param state - String containing state
	 * @return value suitable for passing to Actor constructor
	 */
	public static HashSet<ActorStatus> parseState(String stateString) {
		HashSet<ActorStatus> status = new HashSet<ActorStatus>();
		Matcher matcher;
		Pattern states = Pattern.compile("^\\[(.*)\\]$");
		if (!(matcher = states.matcher(stateString)).matches()) {
			System.err.println("Error: stateString does not conform to pattern! " + stateString);
		} else {
			for (String part: matcher.group(1).split(", ")) {
				if (part.length() > 0)
					status.add(ActorStatus.valueOf(part));
			}
		}
		return status;
	}
	
	/**
	 * Read data formatted using schema 5
	 * @param input : The data to read, starting after the schema data
	 * @param treeModel : DefaultTreeModel to populate
	 */
	public static void readSchema5(BufferedReader input, DefaultTreeModel treeModel) {
		GroupTreeNode currentNode = (GroupTreeNode) treeModel.getRoot();

		try {
			String line;
			Matcher matcher;
			Pattern endFile = Pattern.compile("^</GURPSActorGroupList>$");
			Pattern startFolder = Pattern.compile("^<GroupFolder name=\"([^\"]+)\">$");
			Pattern endFolder = Pattern.compile("^</GroupFolder>$");
			Pattern startGroup = Pattern.compile("^<ActorGroup name=\"([^\"]+)\">$");
			Pattern endGroup = Pattern.compile("^</ActorGroup>$");
			Pattern startActor = Pattern.compile("^<Actor name=\"([^\"]+)\" default_attack=\"([^\"]+)\" state=\"([^\"]+)\" type=\"([^\"]+)\">$");
			Pattern attack = Pattern.compile("^<Attack name=\"([^\"]+)\" skill=\"([^\"]+)\" damage=\"([^\"]+)\" unbalanced=\"([^\"]+)\" />$");
			Pattern endActor = Pattern.compile("^</Actor>$");
			Pattern trait = Pattern.compile("^<([^\"]+)>(.*)</([^\"]+)>$");
			Pattern startTrait = Pattern.compile("^<([^\"]+)>(.*)$");
			Pattern endTrait = Pattern.compile("^(.*)</([^\"]+)>$");
				
			line = input.readLine();
			if (!(matcher = startFolder.matcher(line)).matches()) {
				System.err.println("Error: second line does not specify start of base folder!");
				return;
			}
			while( (line = input.readLine()) != null) {
				if ((matcher = startFolder.matcher(line)).matches()) {
					String name = matcher.group(1);
					GroupTreeNode newNode = new GroupTreeNode(name, true);
					currentNode.add(newNode);
					currentNode = newNode;
				}
				else if ((matcher = startGroup.matcher(line)).matches()) {
					String name = matcher.group(1);
					GroupTreeNode newNode = new GroupTreeNode(name, false);
					currentNode.add(newNode);
					currentNode = newNode;
				}
				else if ((matcher = startActor.matcher(line)).matches()) {
					Actor currentActor = new Actor(matcher.group(1), ActorType.valueOf(matcher.group(4)));
					currentActor.setAllStatuses(parseState(matcher.group(3)));
					currentActor.setDefaultAttack(Integer.parseInt(matcher.group(2)));
					String aName = currentActor.getTraitValue(BasicTrait.Name);
					ArrayList<Actor> actorList = currentNode.getActorList();
					actorList.add(actorList.size()-1, currentActor);
					// Inside Actor
					while ((line = input.readLine()) != null) {
						if ((matcher = endActor.matcher(line)).matches()) {
							break;
						}
						else if ((matcher = attack.matcher(line)).matches()) {
							currentActor.addAttack(new Attack(matcher.group(1), Integer.parseInt(matcher.group(2)), matcher.group(3), Boolean.parseBoolean(matcher.group(4))));
							if (DEBUG) { System.out.println("ActorGroupFile: Found attack for actor. Name: " + aName); }   	
						}
						else if ((matcher = trait.matcher(line)).matches()) {
							if (matcher.group(1).equals(matcher.group(3))) {
								currentActor.setTrait(matcher.group(1), matcher.group(2));
								if (DEBUG) { System.out.println("ActorGroupFile: Found trait for actor. Actor: " + aName + ", Trait: " + matcher.group(1) + ", Value: " + matcher.group(2)); }
							} else {
								System.err.println("ActorGroupFile: start/end tags do not match! " + line);
							}
						}
						else if ((matcher = startTrait.matcher(line)).matches()) {
							String traitName = matcher.group(1);
							StringBuilder traitValue = new StringBuilder(matcher.group(2) + "\n");
							// Inside Notes
							while ((line = input.readLine()) != null) {
								if ((matcher = endTrait.matcher(line)).matches()) {
									traitValue.append(matcher.group(1));
									if (!traitName.equals(matcher.group(2))) {
										System.err.println("ActorGroupFile: multiline start/end tags do not match! Start: " + traitName + ", End: " + matcher.group(2));
									}
									currentActor.setTrait(traitName, traitValue.toString());
									if (DEBUG) { System.out.println("ActorGroupFile: Found multiline trait for actor. Actor: " + aName + ", Trait: " + traitName + ", Value: " + traitValue); }   	
									break;
								}	
								traitValue.append(line + "\n");
							}
						}
						else { System.out.println("ActorGroupFile: -W- Cannot parse line inside Actor: " + line); }   	
					}
				}
				else if ((matcher = endFolder.matcher(line)).matches()) {
					currentNode = (GroupTreeNode) currentNode.getParent();
				}
				else if ((matcher = endGroup.matcher(line)).matches()) {
					currentNode = (GroupTreeNode) currentNode.getParent();
				}
				else if ((matcher = endFile.matcher(line)).matches()) {
		      		if (DEBUG) { System.out.println("ActorGroupFile: Found end of Group List"); }   	
		      		break;
				}
				else { System.out.println("ActorGroupFile: -W- Cannot parse line: " + line); }   	
			}
		}
	    catch (IOException ex){
	        ex.printStackTrace();
	        return;
	    }
	}
	
	/**
	 * Read data formatted using schema 4
	 * @param input : The data to read, starting after the schema data
	 * @param treeModel : DefaultTreeModel to populate
	 */
	public static void readSchema4(BufferedReader input, DefaultTreeModel treeModel) {
		GroupTreeNode currentNode = (GroupTreeNode) treeModel.getRoot();

		try {
			String line;
			Matcher matcher;
			Pattern endFile = Pattern.compile("^</GURPSActorGroupList>$");
			Pattern startFolder = Pattern.compile("^<GroupFolder name=\"([^\"]+)\">$");
			Pattern endFolder = Pattern.compile("^</GroupFolder>$");
			Pattern startGroup = Pattern.compile("^<ActorGroup name=\"([^\"]+)\">$");
			Pattern endGroup = Pattern.compile("^</ActorGroup>$");
			Pattern startActor = Pattern.compile("^<Actor name=\"([^\"]+)\" ht=\"([^\"]+)\" hp=\"([^\"]+)\" damage=\"([^\"]+)\" fp=\"([^\"]+)\" fatigue=\"([^\"]+)\" move=\"([^\"]+)\" parry=\"([^\"]+)\" block=\"([^\"]+)\" dodge=\"([^\"]+)\" dr=\"([^\"]+)\" db=\"([^\"]+)\" shield_dr=\"([^\"]+)\" shield_hp=\"([^\"]+)\" default_attack=\"([^\"]+)\" state=\"([^\"]+)\" type=\"([^\"]+)\">$");
			Pattern attack = Pattern.compile("^<Attack name=\"([^\"]+)\" skill=\"([^\"]+)\" damage=\"([^\"]+)\" unbalanced=\"([^\"]+)\" />$");
			Pattern endActor = Pattern.compile("^</Actor>$");
			Pattern notes = Pattern.compile("^<notes>(.*)</notes>$");
			Pattern startNotes = Pattern.compile("^<notes>(.*)$");
			Pattern endNotes = Pattern.compile("^(.*)</notes>$");
				
			line = input.readLine();
			if (!(matcher = startFolder.matcher(line)).matches()) {
				System.err.println("Error: second line does not specify start of base folder!");
				return;
			}
			while( (line = input.readLine()) != null) {
				if ((matcher = startFolder.matcher(line)).matches()) {
					String name = matcher.group(1);
					GroupTreeNode newNode = new GroupTreeNode(name, true);
					currentNode.add(newNode);
					currentNode = newNode;
				}
				else if ((matcher = startGroup.matcher(line)).matches()) {
					String name = matcher.group(1);
					GroupTreeNode newNode = new GroupTreeNode(name, false);
					currentNode.add(newNode);
					currentNode = newNode;
				}
				else if ((matcher = startActor.matcher(line)).matches()) {
					Actor currentActor = createLegacyActor(matcher.group(1), parseState(matcher.group(16)), ActorType.valueOf(matcher.group(17)), 
							Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)), 
							Integer.parseInt(matcher.group(5)), Integer.parseInt(matcher.group(6)), Integer.parseInt(matcher.group(7)), 
							Integer.parseInt(matcher.group(8)), Integer.parseInt(matcher.group(9)), Integer.parseInt(matcher.group(10)), 
							Integer.parseInt(matcher.group(11)), Integer.parseInt(matcher.group(12)), Integer.parseInt(matcher.group(13)), 
							Integer.parseInt(matcher.group(14)), Integer.parseInt(matcher.group(15)));
					String aName = currentActor.getTraitValue(BasicTrait.Name);
					ArrayList<Actor> actorList = currentNode.getActorList();
					actorList.add(actorList.size()-1, currentActor);
					// Inside Actor
					while ((line = input.readLine()) != null) {
						if ((matcher = endActor.matcher(line)).matches()) {
							break;
						}
						else if ((matcher = attack.matcher(line)).matches()) {
							currentActor.addAttack(new Attack(matcher.group(1), Integer.parseInt(matcher.group(2)), matcher.group(3), Boolean.parseBoolean(matcher.group(4))));
							if (DEBUG) { System.out.println("ActorGroupFile: Found attack for actor. Name: " + aName); }   	
						}
						else if ((matcher = notes.matcher(line)).matches()) {
							currentActor.setTrait(BasicTrait.Notes, matcher.group(1));
							if (DEBUG) { System.out.println("ActorGroupFile: Found notes for actor. Name: " + aName + ", Notes: " + currentActor.getTraitValue(BasicTrait.Notes)); }   	
						}
						else if ((matcher = startNotes.matcher(line)).matches()) {
							StringBuilder actorNotes = new StringBuilder(matcher.group(1) + "\n");
							// Inside Notes
							while ((line = input.readLine()) != null) {
								if ((matcher = endNotes.matcher(line)).matches()) {
									actorNotes.append(matcher.group(1));
									currentActor.setTrait(BasicTrait.Notes, actorNotes.toString());
									if (DEBUG) { System.out.println("ActorGroupFile: Found notes for actor. Name: " + aName + ", Notes: " + currentActor.getTraitValue(BasicTrait.Notes)); }   	
									break;
								}	
								actorNotes.append(line + "\n");
							}
						}
						else { System.out.println("ActorGroupFile: -W- Cannot parse line inside Actor: " + line); }   	
					}
				}
				else if ((matcher = endFolder.matcher(line)).matches()) {
					currentNode = (GroupTreeNode) currentNode.getParent();
				}
				else if ((matcher = endGroup.matcher(line)).matches()) {
					currentNode = (GroupTreeNode) currentNode.getParent();
				}
				else if ((matcher = endFile.matcher(line)).matches()) {
		      		if (DEBUG) { System.out.println("ActorGroupFile: Found end of Group List"); }   	
		      		break;
				}
				else { System.out.println("ActorGroupFile: -W- Cannot parse line: " + line); }   	
			}
		}
	    catch (IOException ex){
	        ex.printStackTrace();
	        return;
	    }
	}
	
	/**
	 * Read data formatted using schema 3
	 * @param input : The data to read, starting after the schema data
	 * @param treeModel : DefaultTreeModel to populate
	 */
	public static void readSchema3(BufferedReader input, DefaultTreeModel treeModel) {
		GroupTreeNode currentNode = (GroupTreeNode) treeModel.getRoot();

		try {
			String line;
			Matcher matcher;
			Pattern endFile = Pattern.compile("^</GURPSActorGroupList>$");
			Pattern startFolder = Pattern.compile("^<GroupFolder name=\"([^\"]+)\">$");
			Pattern endFolder = Pattern.compile("^</GroupFolder>$");
			Pattern startGroup = Pattern.compile("^<ActorGroup name=\"([^\"]+)\">$");
			Pattern endGroup = Pattern.compile("^</ActorGroup>$");
			Pattern startActor = Pattern.compile("^<Actor name=\"([^\"]+)\" ht=\"([^\"]+)\" hp=\"([^\"]+)\" damage=\"([^\"]+)\" fp=\"([^\"]+)\" fatigue=\"([^\"]+)\" move=\"([^\"]+)\" parry=\"([^\"]+)\" block=\"([^\"]+)\" dodge=\"([^\"]+)\" dr=\"([^\"]+)\" db=\"([^\"]+)\" shield_dr=\"([^\"]+)\" shield_hp=\"([^\"]+)\" default_attack=\"([^\"]+)\" state=\"([^\"]+)\" type=\"([^\"]+)\">$");
			Pattern attack = Pattern.compile("^<Attack name=\"([^\"]+)\" skill=\"([^\"]+)\" damage=\"([^\"]+)\" unbalanced=\"([^\"]+)\" />$");
			Pattern endActor = Pattern.compile("^</Actor>$");
			Pattern notes = Pattern.compile("^<notes>(.*)</notes>$");
			Pattern startNotes = Pattern.compile("^<notes>(.*)$");
			Pattern endNotes = Pattern.compile("^(.*)</notes>$");
				
			line = input.readLine();
			if (!(matcher = startFolder.matcher(line)).matches()) {
				System.err.println("Error: second line does not specify start of base folder!");
				return;
			}
			while( (line = input.readLine()) != null) {
				if ((matcher = startFolder.matcher(line)).matches()) {
					String name = matcher.group(1);
					GroupTreeNode newNode = new GroupTreeNode(name, true);
					currentNode.add(newNode);
					currentNode = newNode;
				}
				else if ((matcher = startGroup.matcher(line)).matches()) {
					String name = matcher.group(1);
					GroupTreeNode newNode = new GroupTreeNode(name, false);
					currentNode.add(newNode);
					currentNode = newNode;
				}
				else if ((matcher = startActor.matcher(line)).matches()) {
					Actor currentActor = createLegacyActor(matcher.group(1), parseOldState(matcher.group(16)), ActorType.valueOf(matcher.group(17)), 
							Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)), 
							Integer.parseInt(matcher.group(5)), Integer.parseInt(matcher.group(6)), Integer.parseInt(matcher.group(7)), 
							Integer.parseInt(matcher.group(8)), Integer.parseInt(matcher.group(9)), Integer.parseInt(matcher.group(10)), 
							Integer.parseInt(matcher.group(11)), Integer.parseInt(matcher.group(12)), Integer.parseInt(matcher.group(13)), 
							Integer.parseInt(matcher.group(14)), Integer.parseInt(matcher.group(15)));
					String aName = currentActor.getTraitValue(BasicTrait.Name);
					ArrayList<Actor> actorList = currentNode.getActorList();
					actorList.add(actorList.size()-1, currentActor);
					// Inside Actor
					while ((line = input.readLine()) != null) {
						if ((matcher = endActor.matcher(line)).matches()) {
							break;
						}
						else if ((matcher = attack.matcher(line)).matches()) {
							currentActor.addAttack(new Attack(matcher.group(1), Integer.parseInt(matcher.group(2)), matcher.group(3), Boolean.parseBoolean(matcher.group(4))));
							if (DEBUG) { System.out.println("ActorGroupFile: Found attack for actor. Name: " + aName); }   	
						}
						else if ((matcher = notes.matcher(line)).matches()) {
							currentActor.setTrait(BasicTrait.Notes, matcher.group(1));
							if (DEBUG) { System.out.println("ActorGroupFile: Found notes for actor. Name: " + aName + ", Notes: " + currentActor.getTraitValue(BasicTrait.Notes)); }   	
						}
						else if ((matcher = startNotes.matcher(line)).matches()) {
							StringBuilder actorNotes = new StringBuilder(matcher.group(1) + "\n");
							// Inside Notes
							while ((line = input.readLine()) != null) {
								if ((matcher = endNotes.matcher(line)).matches()) {
									actorNotes.append(matcher.group(1));
									currentActor.setTrait(BasicTrait.Notes,  actorNotes.toString());
									if (DEBUG) { System.out.println("ActorGroupFile: Found notes for actor. Name: " + aName + ", Notes: " + currentActor.getTraitValue(BasicTrait.Notes)); }   	
									break;
								}	
								actorNotes.append(line + "\n");
							}
						}
						else { System.out.println("ActorGroupFile: -W- Cannot parse line inside Actor: " + line); }   	
					}
				}
				else if ((matcher = endFolder.matcher(line)).matches()) {
					currentNode = (GroupTreeNode) currentNode.getParent();
				}
				else if ((matcher = endGroup.matcher(line)).matches()) {
					currentNode = (GroupTreeNode) currentNode.getParent();
				}
				else if ((matcher = endFile.matcher(line)).matches()) {
		      		if (DEBUG) { System.out.println("ActorGroupFile: Found end of Group List"); }   	
		      		break;
				}
				else { System.out.println("ActorGroupFile: -W- Cannot parse line: " + line); }   	
			}
		}
	    catch (IOException ex){
	        ex.printStackTrace();
	        return;
	    }
	}
	
	/**
	 * Read data formatted using schema 2
	 * @param input : The data to read, starting after the schema data
	 * @param treeModel : DefaultTreeModel to populate
	 */
	public static void readSchema2(BufferedReader input, DefaultTreeModel treeModel) {
		GroupTreeNode currentNode = (GroupTreeNode) treeModel.getRoot();

		try {
			String line;
			Matcher matcher;
			Pattern endFile = Pattern.compile("^</GURPSActorGroupList>$");
			Pattern startFolder = Pattern.compile("^<GroupFolder name=\"([^\"]+)\">$");
			Pattern endFolder = Pattern.compile("^</GroupFolder>$");
			Pattern startGroup = Pattern.compile("^<ActorGroup name=\"([^\"]+)\">$");
			Pattern endGroup = Pattern.compile("^</ActorGroup>$");
			Pattern startActor = Pattern.compile("^<Actor name=\"([^\"]+)\" ht=\"([^\"]+)\" hp=\"([^\"]+)\" damage=\"([^\"]+)\" fp=\"([^\"]+)\" fatigue=\"([^\"]+)\" move=\"([^\"]+)\" dodge=\"([^\"]+)\" state=\"([^\"]+)\" type=\"([^\"]+)\">$");
			Pattern endActor = Pattern.compile("^</Actor>$");
			Pattern notes = Pattern.compile("^<notes>(.*)</notes>$");
			Pattern startNotes = Pattern.compile("^<notes>(.*)$");
			Pattern endNotes = Pattern.compile("^(.*)</notes>$");
				
			line = input.readLine();
			if (!(matcher = startFolder.matcher(line)).matches()) {
				System.err.println("Error: second line does not specify start of base folder!");
				return;
			}
			while( (line = input.readLine()) != null) {
				if ((matcher = startFolder.matcher(line)).matches()) {
					String name = matcher.group(1);
					GroupTreeNode newNode = new GroupTreeNode(name, true);
					currentNode.add(newNode);
					currentNode = newNode;
				}
				else if ((matcher = startGroup.matcher(line)).matches()) {
					String name = matcher.group(1);
					GroupTreeNode newNode = new GroupTreeNode(name, false);
					currentNode.add(newNode);
					currentNode = newNode;
				}
				else if ((matcher = startActor.matcher(line)).matches()) {
					Actor currentActor = createLegacyActor(matcher.group(1), parseOldState(matcher.group(9)), ActorType.valueOf(matcher.group(10)), 
							Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)), 
							Integer.parseInt(matcher.group(5)), Integer.parseInt(matcher.group(6)), Integer.parseInt(matcher.group(7)), 9, 9, Integer.parseInt(matcher.group(8)), 0, 0, 4, 20, 0);
					String aName = currentActor.getTraitValue(BasicTrait.Name);
					ArrayList<Actor> actorList = currentNode.getActorList();
					actorList.add(actorList.size()-1, currentActor);
					// Inside Actor
					while ((line = input.readLine()) != null) {
						if ((matcher = endActor.matcher(line)).matches()) {
							break;
						}
						else if ((matcher = notes.matcher(line)).matches()) {
							currentActor.setTrait(BasicTrait.Notes, matcher.group(1));
							if (DEBUG) { System.out.println("ActorGroupFile: Found notes for actor. Name: " + aName + ", Notes: " + currentActor.getTraitValue(BasicTrait.Notes)); }   	
						}
						else if ((matcher = startNotes.matcher(line)).matches()) {
							StringBuilder actorNotes = new StringBuilder(matcher.group(1) + "\n");
							// Inside Notes
							while ((line = input.readLine()) != null) {
								if ((matcher = endNotes.matcher(line)).matches()) {
									actorNotes.append(matcher.group(1));
									currentActor.setTrait(BasicTrait.Notes, actorNotes.toString());
									if (DEBUG) { System.out.println("ActorGroupFile: Found notes for actor. Name: " + aName + ", Notes: " + currentActor.getTraitValue(BasicTrait.Notes)); }   	
									break;
								}	
								actorNotes.append(line + "\n");
							}
						}
						else if (DEBUG) { System.out.println("ActorGroupFile: Cannot parse line inside Actor: " + line); }   	
					}
				}
				else if ((matcher = endFolder.matcher(line)).matches()) {
					currentNode = (GroupTreeNode) currentNode.getParent();
				}
				else if ((matcher = endGroup.matcher(line)).matches()) {
					currentNode = (GroupTreeNode) currentNode.getParent();
				}
				else if ((matcher = endFile.matcher(line)).matches()) {
		      		if (DEBUG) { System.out.println("ActorGroupFile: Found end of Group List"); }   	
		      		break;
				}
				else if (DEBUG) { System.out.println("ActorGroupFile: Cannot parse line: " + line); }   	
			}
		}
	    catch (IOException ex){
	        ex.printStackTrace();
	        return;
	    }
	}
	
	/**
	 * Read data formatted using schema 1
	 * @param input : The data to read, starting after the schema data
	 * @param treeModel : DefaultTreeModel to populate
	 */
	public static void readSchema1(BufferedReader input, DefaultTreeModel treeModel) {
		GroupTreeNode currentNode = (GroupTreeNode) treeModel.getRoot();

		try {
			String line;
			Matcher matcher;
			Pattern endFile = Pattern.compile("^</GURPSActorGroupList>$");
			Pattern startFolder = Pattern.compile("^<GroupFolder name=\"([^\"]+)\">$");
			Pattern endFolder = Pattern.compile("^</GroupFolder>$");
			Pattern startGroup = Pattern.compile("^<ActorGroup name=\"([^\"]+)\">$");
			Pattern endGroup = Pattern.compile("^</ActorGroup>$");
			Pattern startActor = Pattern.compile("^<Actor name=\"([^\"]+)\" hp=\"([^\"]+)\" damage=\"([^\"]+)\" health=\"([^\"]+)\" state=\"([^\"]+)\" type=\"([^\"]+)\">$");
			Pattern endActor = Pattern.compile("^</Actor>$");
			Pattern notes = Pattern.compile("^<notes>(.*)</notes>$");
			Pattern startNotes = Pattern.compile("^<notes>(.*)$");
			Pattern endNotes = Pattern.compile("^(.*)</notes>$");
				
			line = input.readLine();
			if (!(matcher = startFolder.matcher(line)).matches()) {
				System.err.println("Error: second line does not specify start of base folder!");
				return;
			}
			while( (line = input.readLine()) != null) {
				if ((matcher = startFolder.matcher(line)).matches()) {
					String name = matcher.group(1);
					GroupTreeNode newNode = new GroupTreeNode(name, true);
					currentNode.add(newNode);
					currentNode = newNode;
				}
				else if ((matcher = startGroup.matcher(line)).matches()) {
					String name = matcher.group(1);
					GroupTreeNode newNode = new GroupTreeNode(name, false);
					currentNode.add(newNode);
					currentNode = newNode;
				}
				else if ((matcher = startActor.matcher(line)).matches()) {
					Actor currentActor = createLegacyActor(matcher.group(1), parseOldState(matcher.group(5)), ActorType.valueOf(matcher.group(6)), 
							Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)),10,0,5,9,9,8,0,0,4,20,0);
					String aName = currentActor.getTraitValue(BasicTrait.Name);
					ArrayList<Actor> actorList = currentNode.getActorList();
					actorList.add(actorList.size()-1, currentActor);
					// Inside Actor
					while ((line = input.readLine()) != null) {
						if ((matcher = endActor.matcher(line)).matches()) {
							break;
						}
						else if ((matcher = notes.matcher(line)).matches()) {
							currentActor.setTrait(BasicTrait.Notes, matcher.group(1));
							if (DEBUG) { System.out.println("ActorGroupFile: Found notes for actor. Name: " + aName + ", Notes: " + currentActor.getTraitValue(BasicTrait.Notes)); }   	
						}
						else if ((matcher = startNotes.matcher(line)).matches()) {
							StringBuilder actorNotes = new StringBuilder(matcher.group(1) + "\n");
							// Inside Notes
							while ((line = input.readLine()) != null) {
								if ((matcher = endNotes.matcher(line)).matches()) {
									actorNotes.append(matcher.group(1));
									currentActor.setTrait(BasicTrait.Notes, actorNotes.toString());
									if (DEBUG) { System.out.println("ActorGroupFile: Found notes for actor. Name: " + aName + ", Notes: " + currentActor.getTraitValue(BasicTrait.Notes)); }   	
									break;
								}	
								actorNotes.append(line + "\n");
							}
						}
						else if (DEBUG) { System.out.println("ActorGroupFile: Cannot parse line inside Actor: " + line); }   	
					}
				}
				else if ((matcher = endFolder.matcher(line)).matches()) {
					currentNode = (GroupTreeNode) currentNode.getParent();
				}
				else if ((matcher = endGroup.matcher(line)).matches()) {
					currentNode = (GroupTreeNode) currentNode.getParent();
				}
				else if ((matcher = endFile.matcher(line)).matches()) {
		      		if (DEBUG) { System.out.println("ActorGroupFile: Found end of Group List"); }   	
		      		break;
				}
				else if (DEBUG) { System.out.println("ActorGroupFile: Cannot parse line: " + line); }   	
			}
		}
	    catch (IOException ex){
	        ex.printStackTrace();
	        return;
	    }
	}

	/**
	 * Read data formatted using schema 0
	 * @param input : The data to read, starting after the schema data
	 * @param treeModel : DefaultTreeModel to populate
	 */
	public static void readSchema0(BufferedReader input, DefaultTreeModel treeModel) {

		GroupTreeNode currentNode = (GroupTreeNode) treeModel.getRoot();

		try {
			String line;
			Matcher matcher;
			Pattern endFile = Pattern.compile("^</GURPSActorGroupList>$");
			Pattern startFolder = Pattern.compile("^<GroupFolder name=\"([^\"]+)\">$");
			Pattern endFolder = Pattern.compile("^</GroupFolder>$");
			Pattern startGroup = Pattern.compile("^<ActorGroup name=\"([^\"]+)\">$");
			Pattern endGroup = Pattern.compile("^</ActorGroup>$");
			Pattern actor = Pattern.compile("^<Actor name=\"([^\"]+)\" hp=\"([^\"]+)\" damage=\"([^\"]+)\" health=\"([^\"]+)\" state=\"([^\"]+)\" type=\"([^\"]+)\"></Actor>$");
				
			line = input.readLine();
			if (!(matcher = startFolder.matcher(line)).matches()) {
				System.err.println("Error: second line does not specify start of base folder!");
				return;
			}
			while( (line = input.readLine()) != null) {
				if ((matcher = startFolder.matcher(line)).matches()) {
					String name = matcher.group(1);
					GroupTreeNode newNode = new GroupTreeNode(name, true);
					currentNode.add(newNode);
					currentNode = newNode;
				}
				else if ((matcher = startGroup.matcher(line)).matches()) {
					String name = matcher.group(1);
					GroupTreeNode newNode = new GroupTreeNode(name, false);
					currentNode.add(newNode);
					currentNode = newNode;
				}
				else if ((matcher = actor.matcher(line)).matches()) {
					Actor currentActor = createLegacyActor(matcher.group(1), parseOldState(matcher.group(5)), ActorType.valueOf(matcher.group(6)), 
							Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)),10,0,5,9,9,8,0,0,4,20,0);
					ArrayList<Actor> actorList = currentNode.getActorList();
					actorList.add(actorList.size()-1, currentActor);
				}
				else if ((matcher = endFolder.matcher(line)).matches()) {
					currentNode = (GroupTreeNode) currentNode.getParent();
				}
				else if ((matcher = endGroup.matcher(line)).matches()) {
					currentNode = (GroupTreeNode) currentNode.getParent();
				}
				else if ((matcher = endFile.matcher(line)).matches()) {
		      		if (DEBUG) { System.out.println("ActorGroupFile: Found end of Group List"); }   	
		      		break;
				}
				else if (DEBUG) { System.out.println("ActorGroupFile: Cannot parse line: " + line); }   	
			}
		}
	    catch (IOException ex){
	        ex.printStackTrace();
	        return;
	    }
	}
	
	/**
	 * Basic legacy constructor specifying 'all' options
	 */
	public static Actor createLegacyActor(String name, HashSet<ActorStatus> status, ActorType type, int ht, int hp, int damage, int fp, int fatigue, 
			int move, int parry, int block, int dodge, int dr, int db, int shield_dr, int shield_hp, int default_attack) {
		Actor a = new Actor(name, type);
		a.setAllStatuses(status);
		a.setDefaultAttack(default_attack);
		
		a.setTrait(BasicTrait.HT, ht);
		a.setTrait(BasicTrait.HP, hp);
		a.setTrait(BasicTrait.Injury, damage);
		a.setTrait(BasicTrait.FP, fp);
		a.setTrait(BasicTrait.Fatigue, fatigue);
		a.setTrait(BasicTrait.Move, move);
		a.setTrait(BasicTrait.Parry, parry);
		a.setTrait(BasicTrait.Block, block);
		a.setTrait(BasicTrait.Dodge, dodge);
		a.setTrait(BasicTrait.DR, dr);
		a.setTrait(BasicTrait.Shield_DB, db);
		a.setTrait(BasicTrait.Shield_DR, shield_dr);
		a.setTrait(BasicTrait.Shield_HP, shield_hp);
		
		return a;
	}
}
