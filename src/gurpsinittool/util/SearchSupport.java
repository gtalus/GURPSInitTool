package gurpsinittool.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreePath;

import gurpsinittool.app.GITApp;
import gurpsinittool.app.GroupTree;
import gurpsinittool.app.GroupTreeNode;
import gurpsinittool.app.InitTable;
import gurpsinittool.data.Actor;
import gurpsinittool.data.ActorBase.BasicTrait;
/**
 * Provides search support for InitTable and GroupTree
 * @author dcsmall
 *
 */
public class SearchSupport {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(SearchSupport.class.getName());

	private JToolBar searchBar;
	private JTextField searchField;
	private boolean incrementalSearchInProgress = false;
	/*
	 * RootPane used for registering global shortcuts
	 */
	private JRootPane rootPane;
	private InitTable table;
	private int searchStartingTableIndex;
	private Pattern pattern;
	
	private enum SearchMode {Table, Tree}
	private SearchMode mode;

	// GroupManager mode
	private GroupTree groupTree;
	private GroupTreeNode searchStartingNode;
	//private boolean searchNodeListValid = false;
	private ArrayList<GroupTreeNode> searchNodeList;

	//Actions
	public Action actionNextMatch;
	public Action actionPrevMatch;
	public Action actionSearchFocus;
	
	/**
	 * Constructor - initiative table mode
	 * @param rootPane - pane to register global shortcuts under
	 * @param table - table to run search on
	 */
	public SearchSupport(final JRootPane rootPane, final InitTable table) {
		mode = SearchMode.Table;
		this.rootPane = rootPane;
		this.table = table;
		initComponents();
	}
	/**
	 * Constructor - group tree mode
	 * @param rootPane - pane to register global shortcuts under
	 * @param tree - tree to run search on
	 * @param table - table to run search on
	 */
	public SearchSupport(final JRootPane rootPane, final GroupTree tree, final InitTable table) {
		this(rootPane,table);
		mode = SearchMode.Tree;
		this.groupTree = tree;
	}
	
	private void initComponents() {
		 // The top tool bar (search)
		searchBar = new JToolBar();
        final JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setToolTipText("Search combatant names (F3)");
        //MiscUtil.setLabelBold(searchLabel);
        searchBar.add(searchLabel);
        searchField = new JTextField();
        searchField.setToolTipText("Search combatant names (F3)");
        searchField.setColumns(20);
        searchField.setMaximumSize(searchField.getPreferredSize());
        searchField.getDocument().addDocumentListener(new DocumentListener() {
        	public void insertUpdate(DocumentEvent e) {
    	        processTextChanges(e);
    	    }
    	    public void removeUpdate(DocumentEvent e) {
    	    	processTextChanges(e);
    	    }
    	    public void changedUpdate(DocumentEvent e) {
    	    	processTextChanges(e);
    	    }
    	    private void processTextChanges(DocumentEvent e) {
    	    	incrementalSearch();
    	    }
		});
        searchField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				incrementalSearchInProgress = false;
				searchField.setForeground(Color.gray);
				searchField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
				MiscUtil.setTextFieldFontStyle(searchField, Font.ITALIC);
			}
			@Override
			public void focusGained(FocusEvent e) {
				incrementalSearchInProgress = false;
				searchField.setForeground(Color.black);
				MiscUtil.setTextFieldFontStyle(searchField, Font.PLAIN);
				searchField.selectAll();
			}
		});
        // TODO: improve styling, add icon, 'search...' text, add margin, wrap indicator
        //MatteBorder searchInnerBorder = new MatteBorder(new ImageIcon(GITApp.class.getResource("/resources/images/magnifier.png"), "Search"));
        //searchInnerBorder.
        //CompoundBorder searchBorder = new CompoundBorder(searchField.getBorder(), searchInnerBorder);        
        //searchField.setBorder(searchBorder);
        searchBar.add(searchField);
        // Next Actor (first button)
        actionPrevMatch = new AbstractGAction("Search Up", "Find previous match (Ctrl+Shift+G)", new ImageIcon(GITApp.class.getResource("/resources/images/bullet_arrow_up.png"))) {
			public void actionPerformed(ActionEvent arg0) { searchNext(true); }        	
        };
        actionNextMatch = new AbstractGAction("Search Down", "Find next match (Ctrl+G)", new ImageIcon(GITApp.class.getResource("/resources/images/bullet_arrow_down.png"))) {
			public void actionPerformed(ActionEvent arg0) { searchNext(false); }        	
        };
        actionSearchFocus = new AbstractGAction("Search", "Search (F3)", null) {
 			public void actionPerformed(ActionEvent arg0) { searchField.requestFocusInWindow(); }        	
         };
        rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control G"), "SearchNext"); 
        rootPane.getActionMap().put("SearchNext", actionNextMatch); 
        rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control shift G"), "SearchPrev"); 
        rootPane.getActionMap().put("SearchPrev", actionPrevMatch); 
        rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("F3"), "SearchFocus"); 
        rootPane.getActionMap().put("SearchFocus", actionSearchFocus); 

        searchBar.add(MiscUtil.noTextButton(actionPrevMatch));
        searchBar.add(MiscUtil.noTextButton(actionNextMatch));
	}
	
	/**
	 * Populate starting location for search
	 */
	private void setStartingLocation() {
		switch (mode) {
		case Tree:
			if (groupTree.getLastSelectedPathComponent() == null) { // start at the beginning
				searchStartingNode = (GroupTreeNode) groupTree.getModel().getRoot();
				if (LOG.isLoggable(Level.FINE)) { LOG.fine("Starting at the beginning"); }
			} else {
				searchStartingNode = (GroupTreeNode) groupTree.getLastSelectedPathComponent();
				if (LOG.isLoggable(Level.FINE)) { LOG.fine("Current group selection: " + groupTree.getLastSelectedPathComponent().toString()); }
			}
		case Table:
			searchStartingTableIndex = table.getSelectedRow();
			break;
		
		}
		// TODO: check table works for non-selected tree and fallthrough works as expected
	}
	
	/**
	 * Generate search node list for Tree mode
	 */
	private void generateSearchNodeList() {
		if (mode == SearchMode.Tree) {
			// Get the enumeration and generate a list of nodes
			if (LOG.isLoggable(Level.FINE)) { LOG.fine("Generating list from enumeration"); }
			GroupTreeNode rootNode = (GroupTreeNode) groupTree.getModel().getRoot();
			Enumeration<GroupTreeNode> en = rootNode.preorderEnumeration();
			searchNodeList = new ArrayList<GroupTreeNode>();
			while (en.hasMoreElements()) {
				searchNodeList.add(en.nextElement());
			}
			if (LOG.isLoggable(Level.FINE)) { LOG.fine("Done generating list from enumeration"); }
		}
		
	}
	
	/**
	 * Perform next/previous search
	 * @param reverse - search in reverse
	 */
	private void searchNext(boolean reverse) {
		if (!updatePattern()) return;

		setStartingLocation();
		generateSearchNodeList(); // For tree mode
		
		if (performSearch(true, reverse)) {
			setStartingLocation();
			return;
		}
		
		searchField.setBorder(BorderFactory.createLineBorder(Color.red));	
		if (LOG.isLoggable(Level.FINER)) { LOG.finer("incrementalSearch: nothing found"); }
	}
	
	/**
	 * Perform incremental search
	 */
	private void incrementalSearch() {
		if (!updatePattern()) return;
		
		// what is our starting location?
		if (!incrementalSearchInProgress) { // set to current location
			setStartingLocation();
			generateSearchNodeList(); // For tree mode
			incrementalSearchInProgress = true;
		}
		
		if (performSearch(false, false))
			return;
		
		searchField.setBorder(BorderFactory.createLineBorder(Color.red));	
		if (LOG.isLoggable(Level.FINE)) { LOG.fine("Nothing found"); }
	}
	
	private boolean updatePattern() {
		String searchText = searchField.getText();
		if (searchText.equals("")) {
			searchField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			return false; // Don't process blank search
		}
		if (LOG.isLoggable(Level.FINE)) { LOG.fine("Searching for text '"+ searchText + "'"); }
		pattern = Pattern.compile(searchText,Pattern.CASE_INSENSITIVE | Pattern.LITERAL);
		return true;
	}
	
	private boolean performSearch(boolean next, boolean reverse) {
		// Now do the search
		switch (mode) {
		case Tree:
			// Check we have a valid node to start
			if (searchStartingNode == null) {
				if (LOG.isLoggable(Level.WARNING)) { LOG.warning("currentNode is null!"); }
				return false;
			} 
			// Now move through the list
			int startingNodeIndex = searchNodeList.indexOf(searchStartingNode);
			if (startingNodeIndex == -1) {
				if (LOG.isLoggable(Level.WARNING)) { LOG.warning("startingNodeIndex is invalid!"); }
				return false;
			}
			int i = startingNodeIndex;
			// First do partial search of startingNode
			if (searchStartingNode.isGroup() && searchGroupNode(searchStartingNode, searchStartingTableIndex, -1, next, reverse, pattern))
				return true; 
			GroupTreeNode currentNode;
			do {
				if (reverse) {
					i--;
					if (i < 0) i = searchNodeList.size()-1; // wrap around
				} else {
					i++;
					if (i >= searchNodeList.size()) i = 0; // wrap around
				}
				if (i == startingNodeIndex) break;
				currentNode = searchNodeList.get(i);
				if (currentNode.isGroup() && searchGroupNode(currentNode, -1, -1, false, reverse, pattern))
					return true;
			} while (true);
			// Finally do second half of partial search of the startingNode
			if (searchStartingNode.isGroup() && searchGroupNode(searchStartingNode, -1, searchStartingTableIndex, false, reverse, pattern))
				return true; 
			break;
		case Table:
			// Search starting at the current index
			if (searchTable(searchStartingTableIndex, -1, next, reverse, pattern))
				return true; 
			// Finally do second half of partial search of the startingNode
			if (searchTable(-1, searchStartingTableIndex, false, reverse, pattern))
				return true; 
			break;
		}
		return false;
	}
	
	/**
	 * Helper function for incremental search. Passes on most arguments to 
	 * searchActorList(...). This function handles any UI actions needed if 
	 * something is found.
	 * @param currentNode
	 * @param startingIndex
	 * @param endingIndex
	 * @param next
	 * @param reverse
	 * @param pattern
	 * @return whether something was found
	 */
	private boolean searchGroupNode(final GroupTreeNode currentNode, final int startingIndex, final int endingIndex, final boolean next, final boolean reverse, final Pattern pattern) {
		// Search the current node
		ArrayList<Actor> actorList = currentNode.getActorList();
		if (LOG.isLoggable(Level.FINER)) { LOG.finer("searching node " + currentNode.getUserObject().toString() + " starting at index " + startingIndex); }
		int index = searchActorList(actorList, startingIndex, endingIndex, next, reverse, pattern);
		if (index != -1) { // found something!
			if (LOG.isLoggable(Level.FINER)) { LOG.finer("Found something!"); }
			// Set the selection
			TreePath path = new TreePath(currentNode.getPath());
			groupTree.setSelectionPath(path);
			groupTree.scrollPathToVisible(path);
			table.getSelectionModel().setSelectionInterval(index, index);
			table.scrollRectToVisible(table.getCellRect(index, 0, true));
			searchField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			return true;
		}

		if (LOG.isLoggable(Level.FINER)) { LOG.finer("done searching node"); }
		return false;
	}
	
	private boolean searchTable(int startingIndex, int endingIndex, boolean next, boolean reverse, Pattern pattern) {
		if (LOG.isLoggable(Level.FINER)) { LOG.finer("searching initTable starting at index " + startingIndex); }
		int index = table.getActorTableModel().searchActors(startingIndex, endingIndex, next, reverse, pattern);
		if (index != -1) { // found something!
			if (LOG.isLoggable(Level.FINER)) { LOG.finer("Found something!"); }
			// Set the selection
			table.getSelectionModel().setSelectionInterval(index, index);
			table.scrollRectToVisible(table.getCellRect(index, 0, true));
			searchField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			return true;
		}

		if (LOG.isLoggable(Level.FINER)) { LOG.finer("done searching"); }
		return false;
	}
	
	/**
	 * Returns the index of the first match (which could be the startingIndex) or -1 if no matches are found
	 * @param tableModel - the model to search
	 * @param startingIndex - the starting index (-1 to start at the beginning/end based on direction)
	 * @param endingIndex - the ending index (-1 to start at the beginning/end based on direction)
	 * @param next - whether to force moving past the starting index (only takes effect if startingIndex is not -1)
	 * @param reverse - search in reverse
	 * @param searchPattern - the pattern to search for
	 * @return the index of the first match, or -1 if no matches found
	 */
	public static int searchActorList(ArrayList<Actor> actorList, int startingIndex, int endingIndex, boolean next, boolean reverse, Pattern searchPattern) {
		int arraySize = actorList.size();
		if (LOG.isLoggable(Level.FINER)) { LOG.finer("Array size is " + arraySize); }
		if (startingIndex == -1) {
			if (reverse) startingIndex = arraySize -1;
			else startingIndex = 0;
		} else if (next) {
			if (reverse) startingIndex--;
			else startingIndex++;
		}
		if (endingIndex == -1) {
			if (reverse) endingIndex = -1;
			else endingIndex = arraySize;
		} else { // allow to wrap around back to the start
			if (reverse) endingIndex--;
			else endingIndex++;
		}
		int index = startingIndex;
		while (index != endingIndex && index < arraySize && index >= 0) {
			// TODO: filter out '' / new... records
			String actorName = actorList.get(index).getTraitValue(BasicTrait.Name);
			if (searchPattern.matcher(actorName).find()) {
				if (LOG.isLoggable(Level.FINER)) { LOG.finer("Found match at index " + index + ", name=" + actorName); }
				return index;
			}
			if (reverse)
				index--;
			else
				index++;
		}
		if (LOG.isLoggable(Level.FINER)) { LOG.finer("Reached end of array"); }
		return -1;
	}
	
	public JToolBar getSearchToolBar() {
		return searchBar;
	}
	
	public boolean hasFocus() {
		return searchField.hasFocus();
	}
	
	public boolean requestFocus() {
		return searchField.requestFocusInWindow();
	}
}
