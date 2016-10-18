package gurpsinittool.gca;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class encapsulates all file IO for importing GCS files
 * @author dcsmall
 *
 */
public final class GCAFile {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(GCAFile.class.getName());

	private GCAFile() {} // Utility class
	
	/**
	 * Import a GCS4 File
	 * @param openFile - the file to read
	 * @return a GCACharacter object with the values parsed from the file
	 * @throws FileNotFoundException 
	 */
	public static GCACharacter loadGCA4File(final File openFile) throws FileNotFoundException {		
		// Open the file
		FileReader file;
		try {
			file = new FileReader(openFile);			
		} catch (FileNotFoundException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		final BufferedReader input = new BufferedReader(file);
		
		if (LOG.isLoggable(Level.INFO)) { LOG.info("Reading file " + openFile.getPath()); }
		final GCACharacter gcaCharacter = new GCACharacter();
		
		try {
			parseGCA4File(input, gcaCharacter);
			input.close();
			file.close();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
		
		return gcaCharacter;
	}
	
	private static void parseGCA4File(final BufferedReader input, final GCACharacter gcaCharacter) throws IOException {
	
		String line;
		Matcher matcher;	
		
		final Pattern sectionPattern = Pattern.compile("^\\[(\\w*)\\]$");	
		final Pattern specialIgnored = Pattern.compile("^(datafile|altcharactersheet)\\(.*");
		final Pattern simpleValue = Pattern.compile("^(\\w+)\\(([^,]*)\\)$");
		final Pattern nonsimpleValue = Pattern.compile("^(appearance|weight|charname)\\((.*)\\)$"); // For cases with ',' and '()' in the string
		final Pattern idkey = Pattern.compile("^idkey\\((\\d+)\\)\\|,(.*)");
		final Pattern idkeyChild = Pattern.compile("^idkey\\((\\d+)\\),parentkey\\((k?\\d+)\\)\\|,(.*)");
		final Pattern idkeyGroup = Pattern.compile("^idkey\\((\\d+)\\),childkeylist\\(((?:k\\d+,?)+)\\)\\|,(.*)");
		final Pattern idkeyChildGroup = Pattern.compile("^idkey\\((\\d+)\\),parentkey\\((k\\d+)\\),childkeylist\\(((?:k\\d+,?)+)\\)\\|,(.*)");
		final Pattern rtfStart = Pattern.compile("^\\{\\\\rtf1");
		final Pattern other = Pattern.compile("^(name|datecreated|facing|group|ownerid|st|break|op)\\(.*");
		final Pattern encoding = Pattern.compile("^Encoding=UTF-8$");
		final Pattern empty = Pattern.compile("^\\s*$");		
				
		String sectionName = null;
		
		while( (line = input.readLine()) != null) {
			if ((matcher = sectionPattern.matcher(line)).matches()) {
				sectionName = matcher.group(1);
				if (LOG.isLoggable(Level.FINE)) {LOG.fine("Found section: " + sectionName);}		
			} else if ("Author".equals(sectionName) || "LogEntry".equals(sectionName)) { // Ignore these sections for now
			} else if ((matcher = specialIgnored.matcher(line)).matches()) {
				if (LOG.isLoggable(Level.FINEST)) { LOG.finest("Found specialIgnored: " + matcher.group(1)); }					
			} else if ((matcher = simpleValue.matcher(line)).matches()) {
				if (LOG.isLoggable(Level.FINER)) {LOG.finer("Found simple Key: " + matcher.group(1) + ", Value: " + matcher.group(2));}
				final String key = matcher.group(1);
				final String value = matcher.group(2);
				gcaCharacter.setBasicValue(key, value);			
			} else if ((matcher = nonsimpleValue.matcher(line)).matches()) {
				if (LOG.isLoggable(Level.FINER)) {LOG.finer("Found non-simple Key: " + matcher.group(1) + ", Value: " + matcher.group(2));}
				final String key = matcher.group(1);
				final String value = matcher.group(2);
				gcaCharacter.setBasicValue(key, value);			
			} else if ((matcher = idkey.matcher(line)).matches()) {
				if (LOG.isLoggable(Level.FINE)) {LOG.fine("Found idkey. Num: " + matcher.group(1));}
				final HashMap<String, String> keyHash = parseIDKey(Integer.valueOf(matcher.group(1)), new StringBuilder(matcher.group(2)), input);
				GCATrait trait = new GCATrait(sectionName, keyHash);
				gcaCharacter.addTrait(trait);
			} else if ((matcher = idkeyChild.matcher(line)).matches()) {
				if (LOG.isLoggable(Level.FINE)) {LOG.fine("Found idkey+parentkey. Num: " + matcher.group(1) + ", parent Num: " + matcher.group(2));}
				final HashMap<String, String> keyHash = parseIDKey(Integer.valueOf(matcher.group(1)), new StringBuilder(matcher.group(3)), input);				
				GCATrait trait = new GCATrait(sectionName, keyHash);
				if(matcher.group(2).startsWith("k")) { // Not really a child: part of a group
					// TODO: keep track of GCA trait groups?
					gcaCharacter.addTrait(trait);
				} else {
					gcaCharacter.addChildTrait(Integer.valueOf(matcher.group(2)), trait);
				}				
			} else if ((matcher = idkeyGroup.matcher(line)).matches()) {
				if (LOG.isLoggable(Level.FINE)) {LOG.fine("Found idkey+childkeylist. Num: " + matcher.group(1) + ", childkeylist: '" + matcher.group(2) + "'");}
				final HashMap<String, String> keyHash = parseIDKey(Integer.valueOf(matcher.group(1)), new StringBuilder(matcher.group(3)), input);				
				GCATrait trait = new GCATrait(sectionName, keyHash);
				// TODO: keep track of GCA trait groups?
				gcaCharacter.addTrait(trait);
			} else if ((matcher = idkeyChildGroup.matcher(line)).matches()) {
				if (LOG.isLoggable(Level.FINE)) {LOG.fine("Found idkey+parentkey+childkeylist. Num: " + matcher.group(1) + ", parentkey: '" + matcher.group(2) + ", childkeylist: '" + matcher.group(3) + "'");}
				final HashMap<String, String> keyHash = parseIDKey(Integer.valueOf(matcher.group(1)), new StringBuilder(matcher.group(4)), input);				
				GCATrait trait = new GCATrait(sectionName, keyHash);
				// TODO: keep track of GCA trait groups?
				gcaCharacter.addTrait(trait);
			} else if ((matcher = other.matcher(line)).matches()) {
				if (LOG.isLoggable(Level.FINEST)) { LOG.finest("Found other: " + matcher.group(1)); }
			} else if ((matcher = rtfStart.matcher(line)).lookingAt()) {
				// Don't care about the line remnants!
				// Have to add back in wrapping brackets!
				String rtfString = "{" + parseNestedMultiline(new StringBuilder(line), input, "\\{", "\\}") + "}";
				if ("Notes".equals(sectionName) | "Description".equals(sectionName)) {
					gcaCharacter.setBasicValue("RTF" + sectionName, rtfString);
				} else {
					if (LOG.isLoggable(Level.WARNING)) { LOG.warning("Unexpectedly found RTF data in section: '" + sectionName + "', line: " + matcher.group(0)); }
				}
			} else if ((matcher = empty.matcher(line)).matches()) {			
			} else if ((matcher = encoding.matcher(line)).matches()) {			
			} else { if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Cannot parse line: " + line);} }   	
		}
	}
	
	/**
	 * Parse a line starting with idkey(\d+).
	 * @param num - the id number from the beginning of the line
	 * @param line - the rest of the line, after the '|' character
	 * @param input - input buffer from which additional lines can be retrieved if needed
	 * @return
	 * @throws IOException 
	 */
	private static HashMap<String, String> parseIDKey(final int num, StringBuilder line, final BufferedReader input) throws IOException {
		final HashMap<String, String> keyHash = new HashMap<String, String>();
		if (num < 1) { if (LOG.isLoggable(Level.WARNING)) { LOG.warning("Illegal IDKey number! " + num); } }
		if (LOG.isLoggable(Level.FINER)) {LOG.finer("IDKey " + num + " line: " + line);}

		keyHash.put("idkey", String.valueOf(num));
		// Parse the idkey!
		// can be empty, which is fine
		// can have nested (), which is the annoying bit
		// can have nested ',', which prevents us from using split :(
		// can have newlines!
		// Consumes full lines :)

		// The simple, no-extra-parens case:
		final Pattern idkeySimple = Pattern.compile("^(\\w+)\\(([^\\(\\)]*)\\)(?:,)?");
		
		// Complex parsing!
		final Pattern idkeyStart = Pattern.compile("^(\\w+)\\(");
		final Pattern idkeyEnd = Pattern.compile("^(?:,)?");
		
		Matcher matcher;
		while (line.length() > 0) {
			if ((matcher = idkeySimple.matcher(line)).lookingAt()) {	
				final String key = matcher.group(1);
				final String value = matcher.group(2);
				if (LOG.isLoggable(Level.FINEST)) { LOG.finest("Pulled off : " + key + " => " + value); }  
				keyHash.put(key, value);
				line.delete(0, matcher.end());
			} else if ((matcher = idkeyStart.matcher(line)).lookingAt()) {
				final String key = matcher.group(1);
				line.delete(0, matcher.end(1));
				String value = parseNestedMultiline(line, input, "\\(", "\\)");
				if (LOG.isLoggable(Level.FINEST)) { LOG.finest("Pulled off (nested) : " + key + " => " + value); }  
				keyHash.put(key, value);
				if ((matcher = idkeyEnd.matcher(line)).lookingAt()) { // Trailing "," or end of line
					line.delete(0, matcher.end());
				} else {
					if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Got to end of nested parens, but see unexpected result! " + line);}
				}
			} else {
				if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Finished parsing with non-empty line! '" + line + "'");}
				break; // no match!
			}
		}
		return keyHash;
	}
	
	/**
	 * Extract the text between matching delimiters, which may be nested and spread over multiple lines
	 * @param line - The first line to parse. Must start with the start delimiter. 
	 * Will contain remaining text after parser finishes
	 * @param input - where to get more lines if needed
	 * @param startDelim - regular expression for start delimiter
	 * @param endDelim - regular expression for end delimiter
	 * @return text between matching delimiters, not including the delimiters
	 * @throws IOException 
	 */
	private static String parseNestedMultiline(final StringBuilder line, final BufferedReader input, final String startDelim, final String endDelim) throws IOException {
		StringBuilder result = new StringBuilder();
		
		final Pattern start = Pattern.compile("^(" + startDelim + ")");
		final Pattern open = Pattern.compile("^([^" + startDelim + endDelim + "]*" + startDelim + ")");
		final Pattern close = Pattern.compile("^([^" + startDelim + endDelim + "]*)(" + endDelim + ")");
		final Pattern newline = Pattern.compile("^([^" + startDelim + endDelim + "]*$)");
		
		Matcher matcher;
		if (LOG.isLoggable(Level.FINEST)) {LOG.finest("Starting parser: startDelim: '" + startDelim + "' endDelim: '" + endDelim + "', line: '" + line + "'");}
		if ((matcher = start.matcher(line)).lookingAt()) { // Kick off the parsing with a startDelim
			int parenLevel = 1;
			line.delete(0, matcher.end());
			if (LOG.isLoggable(Level.FINEST)) {LOG.finest("Got expected start delim, line: '" + line + "'");}
			while(parenLevel > 0) { // Run through the nested parser
				if ((matcher = open.matcher(line)).lookingAt()) {
					parenLevel++;
					result.append(matcher.group(1));
					line.delete(0, matcher.end());
					if (LOG.isLoggable(Level.FINEST)) {LOG.finest("Got start delim, line: '" + line + "'");}
				} else if ((matcher = close.matcher(line)).lookingAt()) {
					parenLevel--;
					result.append(matcher.group(1));
					if (parenLevel > 0) // Don't append final endDelim
						result.append(matcher.group(2));
					line.delete(0, matcher.end());
					if (LOG.isLoggable(Level.FINEST)) {LOG.finest("Got end delim, line: '" + line + "'");}
				} else if ((matcher = newline.matcher(line)).matches()) {
					result.append(matcher.group(1));
					line.delete(0, matcher.end());
					String nextLine = input.readLine();
					if (nextLine == null) {
						if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Got null value when reading multi-line value!");}
						break;
					}
					line.append(nextLine);
					if (LOG.isLoggable(Level.FINEST)) {LOG.finest("Got newline, line: '" + line + "'");}
				} else {
					if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Can't parse nested parens for line: '" + line + "'");}
					break;
				}
			}
		} else {
			if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Line does not start with start delim! StartDelim: '" + startDelim + "', line: '" + line + "'");}
		}
		if (LOG.isLoggable(Level.FINEST)) {LOG.finest("Final Result: '" + result + "'");}
		return result.toString();
	}


}
