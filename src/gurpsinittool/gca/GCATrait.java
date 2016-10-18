package gurpsinittool.gca;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A GCA Trait, includes a trait number, name, section and a hash of values
 * Can also have child traits
 * @author dcsmall
 *
 */
public class GCATrait {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(GCATrait.class.getName());
	
	private String section;
	private Integer idNum;
	private String name;

	private HashMap<String,String> values;
	private ArrayList<GCATrait> children;
	
	public GCATrait(String section, HashMap<String,String> idKey) {
		this.section = section;		
		if (!idKey.containsKey("idkey")) {
			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("idKey hash does not contain 'idkey' value!");}
		}
		if (!idKey.containsKey("name")) {
			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("idKey hash does not contain 'name' value!");}
		}
		this.idNum = Integer.valueOf(idKey.get("idkey"));
		this.name = idKey.get("name");

		idKey.remove("idkey");
		idKey.remove("name");
		values = new HashMap<String, String>(idKey);
		children = new ArrayList<GCATrait>();
	}
	
	/**
	 * Return true if the value exists
	 * @param name
	 * @return true if the value exists
	 */
	public boolean hasValue(final String name) {
		return values.containsKey(name);
	}
	/**
	 * Get a value by name
	 * @param name
	 * @return the value, or null if not found
	 */
	public String getValue(final String name) {
		return values.get(name);
	}
	/**
	 * Get a value by name, as a list (split by '|')
	 * @param name
	 * @return an array of strings, or an empty list if not found
	 */
	public List<String> getValueAsList(final String name) {
		String value = values.get(name);
		if (value == null) {
			return new ArrayList<String>();
		} else {
			return Arrays.asList(value.split("\\|"));
		}
	}
	/**
	 * Add a child trait to this one
	 * @param child
	 */
	public void addChild(final GCATrait child) {
		// TODO: check groups match?
		//if (idKey.containsKey("mods")) {
		//	String group = idKey.get("mods");
		//if (LOG.isLoggable(Level.FINEST)) {LOG.finest("Processing: " + name + ", got group: " + group);}
		children.add(child);
	}
	public Iterable<GCATrait> getChildren() {
		return children;
	}

	/**
	 * Get the section name
	 * @return
	 */
	public String getSection() {
		return section;
	}
	/**
	 * Get the id number
	 * @return
	 */
	public Integer getIdNum() {
		return idNum;
	}
	/**
	 * Get the trait name
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	
}
