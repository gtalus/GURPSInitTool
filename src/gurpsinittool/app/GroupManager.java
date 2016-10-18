package gurpsinittool.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultEditorKit;
import javax.swing.tree.TreePath;

import gurpsinittool.data.Actor;
import gurpsinittool.data.ActorGroupFile;
import gurpsinittool.data.GameMaster;
import gurpsinittool.gca.GCACharacter;
import gurpsinittool.gca.GCAFile;
import gurpsinittool.gca.GCAImporter;
import gurpsinittool.ui.ActorDetailsPanel_v2;
import gurpsinittool.util.FileChangeEvent;
import gurpsinittool.util.FileChangeEventListener;
import gurpsinittool.util.MiscUtil;
import gurpsinittool.util.SearchSupport;

public class GroupManager extends JFrame 
	implements TreeSelectionListener, ActionListener, ItemListener, ListSelectionListener {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(GroupManager.class.getName());
	
	// Default SVUID
	private static final long serialVersionUID = 1L;

	private JSplitPane jSplitPaneVertical;
	private JSplitPane jSplitPaneHorizontal;
	private JScrollPane jScrollPaneTable;
	private JScrollPane jScrollPaneTree;
	private JScrollPane jScrollPaneDetails;
	private InitTable groupTable;
	private ActorDetailsPanel_v2 actorDetailsPanel;
	private JMenuBar jMenuBar;
	private JMenu jMenu;
	private JToolBar toolbar;
	private GroupTree groupTree;
	
	private JFileChooser groupFileChooser;
	private JFileChooser gcaFileChooser;
	private File saveAsFile;
	private boolean tableIsClean = true;
	private boolean treeIsClean = true;
	private Properties propertyBag;
	private GameMaster gameMaster;
	
	//Search support
	SearchSupport searchSupport;
	
	public GroupManager(Properties propertyBag) {
		super("Groups Manager");
		this.propertyBag = propertyBag;
		setDefaultProperties();
		
		 // Set some UI options:
        UIManager.put("Tree.leafIcon", new ImageIcon(GITApp.class.getResource("/resources/images/bullet_black.png")));
        UIManager.put("Tree.closedIcon", new ImageIcon(GITApp.class.getResource("/resources/images/folder.png")));
        UIManager.put("Tree.openIcon", new ImageIcon(GITApp.class.getResource("/resources/images/folder.png")));
        
        
		// Create core components
		gameMaster = new GameMaster();
		actorDetailsPanel = new ActorDetailsPanel_v2(false);
		groupTable = new InitTable(gameMaster, false, propertyBag);
		groupTable.getActorTableModel().setActorList(null);
		groupTree = new GroupTree(groupTable);
		searchSupport = new SearchSupport(getRootPane(), groupTree, groupTable);
		
		// Initialize GameMaster
		gameMaster.initTable = groupTable;
		gameMaster.detailsPanel = actorDetailsPanel;
		groupTable.getActorTableModel().addUndoableEditListener(gameMaster);
        groupTree.addUndoableEditListener(gameMaster);
        
        // Create and set up the window.	
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        // The file chooser
        groupFileChooser = new JFileChooser();
        groupFileChooser.setCurrentDirectory(new File(propertyBag.getProperty("Manager.groupFileChooser.defaultLocation"))); 
        GroupFilter groupFilter = new GroupFilter();
        groupFileChooser.addChoosableFileFilter(groupFilter);
        groupFileChooser.setFileFilter(groupFilter);
        
        gcaFileChooser = new JFileChooser();
        gcaFileChooser.setCurrentDirectory(new File(propertyBag.getProperty("Manager.gcaFileChooser.defaultLocation"))); 
        GCAFilter gcaFilter = new GCAFilter();
        gcaFileChooser.addChoosableFileFilter(gcaFilter);
        gcaFileChooser.setFileFilter(gcaFilter);        
        gcaFileChooser.setMultiSelectionEnabled(true);
 
        // The menu bar
        jMenuBar = new JMenuBar();
        jMenu = new JMenu("File");
        jMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem menuItem = new JMenuItem("New", KeyEvent.VK_N);
        //menuItem.setEnabled(false);
        menuItem.addActionListener(this);
        jMenu.add(menuItem);
        menuItem = new JMenuItem("Open", KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        jMenu.add(menuItem);
        menuItem = new JMenuItem("Save As...", KeyEvent.VK_A);
        menuItem.getAccessibleContext().setAccessibleDescription("Select the file to save the group list to");
        menuItem.addActionListener(this);
        jMenu.add(menuItem);
        menuItem = new JMenuItem("Save", KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Save the group list");
        menuItem.addActionListener(this);
        jMenu.add(menuItem);
        jMenu.addSeparator();
        menuItem = new JMenuItem("Import GCA4...", KeyEvent.VK_I);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Import a GCA4 character file");
        menuItem.addActionListener(this);
        jMenu.add(menuItem);
        jMenuBar.add(jMenu);
        
        jMenu = new JMenu("Edit");
        jMenu.setMnemonic(KeyEvent.VK_E);
        menuItem = new JMenuItem(gameMaster.actionUndo);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        jMenu.add(menuItem);
        menuItem = new JMenuItem(gameMaster.actionRedo);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        jMenu.add(menuItem);
           
        menuItem = new JMenuItem(new DefaultEditorKit.CutAction());
        menuItem.setText("Cut");
        menuItem.setMnemonic(KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        menuItem.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/cut.png"), "Cut"));
        jMenu.add(menuItem);
        menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
        menuItem.setText("Copy");
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        menuItem.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/page_copy.png"), "Copy"));
        jMenu.add(menuItem);
        menuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
        menuItem.setText("Paste");
        menuItem.setMnemonic(KeyEvent.VK_V);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        menuItem.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/paste_plain.png"), "Paste"));
        jMenu.add(menuItem);        
        jMenuBar.add(jMenu);
        
        jMenu = new JMenu("View");
        jMenu.setMnemonic(KeyEvent.VK_V);
        menuItem = new JCheckBoxMenuItem("Auto-fit columns");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.setSelected(Boolean.valueOf(propertyBag.getProperty("Manager.groupTable.autoResize")));
        menuItem.getAccessibleContext().setAccessibleDescription("Table columns auto-fit automatically");
        menuItem.addItemListener(this);
        jMenu.add(menuItem);
        menuItem = new JCheckBoxMenuItem("Actor Details");
        //((JCheckBoxMenuItem) menuItem).setSelected(true);
        menuItem.setSelected(Boolean.valueOf(propertyBag.getProperty("Manager.actorDetails.visible")));
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.addItemListener(this);
        jMenu.add(menuItem);
        menuItem = new JMenuItem(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				groupTable.showColumnCustomizer();
			}
		});
        menuItem.setText("Customize Columns");
        menuItem.setMnemonic(KeyEvent.VK_C);
        jMenu.add(menuItem);     
        jMenuBar.add(jMenu);

        setJMenuBar(jMenuBar);
        
        // The top tool bar (search)
        toolbar = searchSupport.getSearchToolBar();
        toolbar.add(Box.createHorizontalGlue());
		getContentPane().add(toolbar, BorderLayout.PAGE_START);

        
        groupTable.setVisible(false);
        groupTable.getSelectionModel().addListSelectionListener(this);
        groupTable.getActorTableModel().addFileChangeEventListener(new GroupFileChangeEventListener());
        groupTable.getActorTableModel().addTableModelListener(new GroupInitTableModelListener());
        
        groupTree.addTreeSelectionListener(this);
        groupTree.addFileChangeEventListener(new GroupFileChangeEventListener());
        jScrollPaneTable = new JScrollPane(groupTable);
        jScrollPaneDetails = new JScrollPane(actorDetailsPanel);
        jScrollPaneDetails.setMinimumSize(new Dimension(actorDetailsPanel.getPreferredSize().width+20,0));

        jScrollPaneTree = new JScrollPane(groupTree);
        jSplitPaneVertical= new JSplitPane(JSplitPane.VERTICAL_SPLIT, jScrollPaneTree, jScrollPaneTable);
        jSplitPaneHorizontal= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jSplitPaneVertical, jScrollPaneDetails);
        
        jSplitPaneVertical.setDividerLocation(Integer.valueOf(propertyBag.getProperty("Manager.splitVertical.dividerLocation")));
        jSplitPaneVertical.setContinuousLayout(true);
        jSplitPaneVertical.setResizeWeight(.95);
 
        jSplitPaneHorizontal.setDividerLocation(Integer.valueOf(propertyBag.getProperty("Manager.splitHorizontal.dividerLocation")));
        jSplitPaneHorizontal.setContinuousLayout(true);
        jSplitPaneHorizontal.setResizeWeight(.95);
        
        layoutActorPanel();
        
        setLocation(Integer.valueOf(propertyBag.getProperty("Manager.location.x")),
                Integer.valueOf(propertyBag.getProperty("Manager.location.y")));
        setSize(Integer.valueOf(propertyBag.getProperty("Manager.size.width")),
        		Integer.valueOf(propertyBag.getProperty("Manager.size.height")));    
	}

	@Override
	public void actionPerformed(ActionEvent e) {
    	if (LOG.isLoggable(Level.FINE)) {LOG.fine("Received action command " + e.getActionCommand()); }
    	if ("Save As...".equals(e.getActionCommand())) { // Save group list with prompt for the file name
    		saveGroupFile(null);
    	}
    	else if ("Save".equals(e.getActionCommand())) { // Save group list
         	saveGroupFile(saveAsFile);
    	}
       	else if ("Open".equals(e.getActionCommand()) && querySaveChanges()) { // Load group list
    		loadGroupFile(null);
    	}
    	else if ("New".equals(e.getActionCommand()) && querySaveChanges()) { // Create new group list
          	newGroupFile();
    	}
    	else if ("Import GCA4...".equals(e.getActionCommand())) { // Import GCA4 character
    		userImportGCA4Files();
    	}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (LOG.isLoggable(Level.FINE)) {LOG.fine(" Received item state changed " + e.toString()); }
    	JMenuItem source = (JMenuItem) e.getSource();
    	if ("Actor Details".equals(source.getText())) { // Show/hide the actor details panel
         	boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
         	if (LOG.isLoggable(Level.FINER)) {LOG.finer("View/Actor Details item state changed. Selected = " + selected); }
         	propertyBag.setProperty("Manager.actorDetails.visible", String.valueOf(selected));
         	layoutActorPanel();
    	}
    	else if ("Auto-fit columns".equals(source.getText())) { // Auto-fit columns automatically
         	boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
         	if (LOG.isLoggable(Level.FINER)) {LOG.finer("Tools/Auto-fit columns item state changed. Selected = " + selected); }
         	propertyBag.setProperty("Manager.groupTable.autoResize", String.valueOf(selected));
         	if (selected)
         		groupTable.autoSizeColumns();
    	}
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (LOG.isLoggable(Level.FINER)) {LOG.finer("TreeSelectionEvent: " + e.toString()); }
		// Stop editing the table, if editing is currently in progress
		if(groupTable.getCellEditor() != null) { groupTable.getCellEditor().stopCellEditing(); }
		InitTableModel tableModel = groupTable.getActorTableModel();
		if (groupTree.getLastSelectedPathComponent() != null) {
			if (LOG.isLoggable(Level.FINER)) {LOG.finer("Current Selection: " + groupTree.getLastSelectedPathComponent().toString()); }
			GroupTreeNode node = (GroupTreeNode) groupTree.getLastSelectedPathComponent();
			if (node.isGroup()) { // If current selection is a group, display the actors
				tableModel.setActorList(node.getActorList());
				groupTable.setVisible(true);
			}
			else { // Otherwise don't show any actors
				groupTable.setVisible(false);
				tableModel.setActorList(null);
			}
		}
		else {
			if (LOG.isLoggable(Level.FINER)) {LOG.finer("Current Selection: null"); }
			groupTable.setVisible(false);
			tableModel.setActorList(null);
		}
	}

	/**
	 * Check if the file is clean. If not, query the user if we should continue.
	 * The user may choose to save the file (also managed by this routine).
	 * @return Whether or not it is ok to continue.
	 */
	public boolean querySaveChanges() {
		if (!tableIsClean || !treeIsClean) {
   			int n = JOptionPane.showOptionDialog(this, "Groups have been modified. Save Changes?", "Groups Changed", 
   					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
   			if (n == 0) { // save the file, then continue
   				if (!saveGroupFile(saveAsFile)) { return false;}
   			}
   			else if (n == 2) { // Cancel
   				return false;
   			}
   		}
		return true;
	}
	
	/**
	 * Load a default group file, if one is specified.
	 * Intended for use at start-up
	 */
	public void loadDefaultGroupFile() {
	    // Auto-load a group file if requested:
        if (propertyBag.containsKey("Manager.currentLoadedFile")) {
        	saveAsFile = new File(propertyBag.getProperty("Manager.currentLoadedFile"));
        	loadGroupFile(saveAsFile);
        }
	}
	/**
	 * Create a new group file. Will discard all unsaved changes WITHOUT PROMPTING!
	 * use querySaveChanges() if you want to query the user to save changes.
	 */
	public void newGroupFile() {
		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Creating new group list"); }
    	groupTree.setNewModel();
    	saveAsFile = null;
    	groupTree.setClean();
		groupTable.getActorTableModel().setClean();
    	super.setTitle("Groups Manager");
	}
	
	/**
	 * Load the group from a file
	 * @param file - the file to load from. Open File dialog used if file is null.
	 * @return whether the load was completed successfully.
	 */
	public boolean loadGroupFile(File file) {
		if (file == null) {
			if (LOG.isLoggable(Level.FINE)) {LOG.fine("Opening file: Displaying file chooser"); }        	
			int retVal = groupFileChooser.showOpenDialog(this);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				file = groupFileChooser.getSelectedFile();
			}
			else { return false; }
		}
		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Opening file: " + file.getName()); }
    	ActorGroupFile.openActorGroupTree(groupTree, file);
    	saveAsFile = file;
		groupTree.setClean();
		groupTable.getActorTableModel().setClean();
		super.setTitle("Groups Manager - " + saveAsFile.getName());
		return true;
	}
	
	/**
	 * Save the group list to a file.
	 * @param file - the file to save as. Save As dialog used if file is null.
	 * @return whether the save was completed successfully.
	 */
	 public boolean saveGroupFile(File file) {
	    	if (file == null) { // Pick file	    		
	    		int retVal = groupFileChooser.showSaveDialog(this);
	    		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Displaying file chooser"); }
	    		if (retVal == JFileChooser.APPROVE_OPTION) {
	    			file = groupFileChooser.getSelectedFile();
	        		if (!file.toString().contains(".")) { file = new File(file.toString() + ".igroup"); }
	        		if (file.exists ()) {
	                    int response = JOptionPane.showConfirmDialog (null,
	                      "Overwrite existing file?","Confirm Overwrite",
	                       JOptionPane.OK_CANCEL_OPTION,
	                       JOptionPane.QUESTION_MESSAGE);
	                    if (response == JOptionPane.CANCEL_OPTION) return false;
	                }
	    		}
	    		else {
	    			return false;
	    		}
	    	}
	    	// save group list
	    	if (LOG.isLoggable(Level.FINE)) {LOG.fine("Saving group list as file: " + file.getName()); }
    		ActorGroupFile.saveActorGroupTree(groupTree, file);
    		groupTree.setClean();
    		groupTable.getActorTableModel().setClean();
    		super.setTitle("Groups Manager - " + file.getName());
    		saveAsFile = file;
    		
    		return true;
	 }
	 
	 /**
	  * Show the user a file chooser to import GCA4 files
	  * @return if any files were imported
	  */
	 public boolean userImportGCA4Files() {
		 if (LOG.isLoggable(Level.FINE)) {LOG.fine("Opening file: Displaying file chooser"); }			
		 int retVal = gcaFileChooser.showOpenDialog(this);
		 if (retVal == JFileChooser.APPROVE_OPTION) {
			 File [] files = gcaFileChooser.getSelectedFiles();
			 if (files.length == 0) {
				 if (LOG.isLoggable(Level.FINE)) {LOG.fine("No files selected!"); }
				 return false;
			 }
			 boolean successful = false;
			 for (int i = 0; i < files.length; i++) {
				 if (importGCA4File(files[i])) {
					 successful = true;
				 } else {
					 if (LOG.isLoggable(Level.INFO)) {LOG.info("Unable to import file '" + files[i].getPath() + "'"); }
				 }
			 }
			 return successful;
		 }
		 else { 
			 if (LOG.isLoggable(Level.FINE)) {LOG.fine("User cancelled operation"); }
			 return false; 
		 }
	 }
	 /**
	  * Import a GCA4 character file
	  * @param file - the file to load from. Open File dialog used if file is null.
	  * @return whether the load was completed successfully.
	  */
	 public boolean importGCA4File(File file) {
		 if (file == null) {
			 if (LOG.isLoggable(Level.WARNING)) {LOG.warning("Given null file!"); }			
			 return false;
		 }
		 if (LOG.isLoggable(Level.FINE)) {LOG.fine("Opening file: " + file.getName()); }
		 Actor importedActor;
		 try {
			 importedActor = GCAImporter.importActor(file);
		 } catch (FileNotFoundException e) {
			 if (LOG.isLoggable(Level.SEVERE)) {LOG.log(Level.SEVERE, e.getMessage(), e);}
			 return false;
		 }

		 // Check if there is no valid selected group currently
		 if (groupTable.getActorTableModel().getRowCount() == 0) {
			 if (LOG.isLoggable(Level.FINE)) {LOG.fine("Creating new group to hold imported actor!"); }
			 GroupTreeNode newGroup = groupTree.addNode("GCA4 Import", true);
			 TreePath groupPath = new TreePath(newGroup.getPath());
			 groupTree.getSelectionModel().setSelectionPath(groupPath);			 			
		 } 		
		 if (LOG.isLoggable(Level.FINE)) {LOG.fine("Adding imported actor to currently selected group"); }
		 groupTable.getActorTableModel().addActor(importedActor, groupTable.getActorTableModel().getRowCount()-1);
		 return true;
	 }
	    	
	 /**
	  * Show/Hide the actor panel
	  */
	 private void layoutActorPanel() {
		 if(Boolean.valueOf(propertyBag.getProperty("Manager.actorDetails.visible"))) {			 
	         getContentPane().remove(jSplitPaneVertical);
	         // Refresh settings for left component & divider since they seem to be cleared when the jSplitPaneHorizontal is added to another container
	         jSplitPaneHorizontal.setLeftComponent(jSplitPaneVertical);
	         jSplitPaneHorizontal.setDividerLocation(Integer.valueOf(propertyBag.getProperty("Manager.splitHorizontal.dividerLocation")));	     
			 getContentPane().add(jSplitPaneHorizontal, BorderLayout.CENTER);
			 validate();
		 }
		 else {
			 propertyBag.setProperty("Manager.splitHorizontal.dividerLocation", String.valueOf(jSplitPaneHorizontal.getDividerLocation()));
		 	 getContentPane().remove(jSplitPaneHorizontal);
			 getContentPane().add(jSplitPaneVertical, BorderLayout.CENTER);
			 validate();
		 }
	 }
	 
	 /**
	  * Set default properties if they are not already defined.
	  */
	 private void setDefaultProperties() {
		 if (!propertyBag.containsKey("Manager.actorDetails.visible")) {
			 propertyBag.setProperty("Manager.actorDetails.visible", "true"); }
		 if (!propertyBag.containsKey("Manager.groupTable.autoResize")) {
			 propertyBag.setProperty("Manager.groupTable.autoResize", "true"); }
		 if (!propertyBag.containsKey("Manager.splitHorizontal.dividerLocation")) {
			 propertyBag.setProperty("Manager.splitHorizontal.dividerLocation", "340"); }
		 if (!propertyBag.containsKey("Manager.splitVertical.dividerLocation")) {
			 propertyBag.setProperty("Manager.splitVertical.dividerLocation", "200"); }
		 if (!propertyBag.containsKey("Manager.location.x")) {
			 propertyBag.setProperty("Manager.location.x", "500"); }
		 if (!propertyBag.containsKey("Manager.location.y")) {
			 propertyBag.setProperty("Manager.location.y", "300"); }
		 if (!propertyBag.containsKey("Manager.size.width")) {
			 propertyBag.setProperty("Manager.size.width", "620"); }
		 if (!propertyBag.containsKey("Manager.size.height")) {
			 propertyBag.setProperty("Manager.size.height", "450"); }
		 if (!propertyBag.containsKey("Manager.groupFileChooser.defaultLocation") 
				 || !Files.isDirectory(Paths.get(propertyBag.getProperty("Manager.groupFileChooser.defaultLocation")))) {
			 propertyBag.setProperty("Manager.groupFileChooser.defaultLocation", "."); }
		 if (!propertyBag.containsKey("Manager.gcaFileChooser.defaultLocation") 
				 || !Files.isDirectory(Paths.get(propertyBag.getProperty("Manager.gcaFileChooser.defaultLocation")))) {
			 propertyBag.setProperty("Manager.gcaFileChooser.defaultLocation", "."); }
	 }
	 
	 /**
	  * Update all the store-able properties to their current values
	  */
	 public void updateProperties() {
		 // Kept up-to-date with event listeners
		 // Manager.actorDetails.visible
		 // Manager.groupTable.autoResize
		 propertyBag.setProperty("Manager.splitHorizontal.dividerLocation", String.valueOf(jSplitPaneHorizontal.getDividerLocation()));
		 propertyBag.setProperty("Manager.splitVertical.dividerLocation", String.valueOf(jSplitPaneVertical.getDividerLocation()));
		 propertyBag.setProperty("Manager.location.x", String.valueOf(getLocation().x));
		 propertyBag.setProperty("Manager.location.y", String.valueOf(getLocation().y));
		 propertyBag.setProperty("Manager.size.width", String.valueOf(getSize().width));
		 propertyBag.setProperty("Manager.size.height", String.valueOf(getSize().height));
		 propertyBag.setProperty("Manager.groupFileChooser.defaultLocation", groupFileChooser.getCurrentDirectory().getPath());
		 propertyBag.setProperty("Manager.gcaFileChooser.defaultLocation", gcaFileChooser.getCurrentDirectory().getPath());
		 // Optional properties
		 if (saveAsFile != null) { propertyBag.setProperty("Manager.currentLoadedFile", saveAsFile.getAbsolutePath());}
		 else { propertyBag.remove("Manager.currentLoadedFile");}
		 groupTable.updateProperties(); // Update properties for the table
	 }
	 
	 /**
     * An Inner class to monitor the file changes
     */
    class GroupFileChangeEventListener implements FileChangeEventListener {

		@Override
		public void fileChangeOccured(FileChangeEvent evt) {
			if (LOG.isLoggable(Level.FINER)) {LOG.finer("FileChangeOccured."); }
		}
	
		@Override
		public void fileCleanStatusChanged(FileChangeEvent evt) {
			Object source = evt.getSource();
			if (source instanceof InitTableModel) {
				if (LOG.isLoggable(Level.FINE)) {LOG.fine("ActorTableModel " + evt.isClean()); }
				tableIsClean = evt.isClean();
			}
			else if (source instanceof GroupTree) {
				if (LOG.isLoggable(Level.FINE)) {LOG.fine("GroupTree " + evt.isClean()); }
				treeIsClean = evt.isClean();
			}
			else {
				if (LOG.isLoggable(Level.WARNING)) {LOG.warning("UNKNOWN " + evt.isClean()); }
			}
	
			if (tableIsClean && treeIsClean) {
				if (saveAsFile != null) {
					setTitle("Groups Manager - " + saveAsFile.getName());
				}
				else {
					setTitle("Groups Manager");
				}
			}
			else {
				if (saveAsFile != null) {
					setTitle("Groups Manager - " + saveAsFile.getName() + "*");
				}
				else {
					setTitle("Groups Manager *");
				}
			}
		}
    }
    
	 /**
     * An Inner class to monitor the table changes
     */
    class GroupInitTableModelListener implements TableModelListener {

		@Override
		public void tableChanged(TableModelEvent evt) {
			if (LOG.isLoggable(Level.FINER)) {LOG.finer("TableModelChange occured."); }
			// Check for auto-resize
			if (evt.getType() == TableModelEvent.UPDATE && evt.getFirstRow() == TableModelEvent.HEADER_ROW 
					&& evt.getColumn() == TableModelEvent.ALL_COLUMNS) { // Changed column structure
				// skip
				if (LOG.isLoggable(Level.FINER)) {LOG.finer("    Detected structure change- skipping"); }
			} else {
				if(Boolean.valueOf(propertyBag.getProperty("Manager.groupTable.autoResize")))
					groupTable.autoSizeColumns();
				//actorDetailsPanel.setActor(groupTable.getSelectedActor());
			}
		}
    }
    
	private class GroupFilter extends FileFilter {
		@Override
		public boolean accept(File f) { 
			return (f.isDirectory() || f.toString().endsWith(".igroup"));
		}
		@Override
		public String getDescription() {
			return "InitTool Group Set (*.igroup)";
		}
	}
	
	private class GCAFilter extends FileFilter {
		@Override
		public boolean accept(File f) { 
			return (f.isDirectory() || f.toString().endsWith(".gca4"));
		}
		@Override
		public String getDescription() {
			return "GURPS Character Assistant 4 (*.gca4)";
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			actorDetailsPanel.setActor(groupTable.getSelectedActor());
		}
	}
}
