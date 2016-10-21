package gurpsinittool.app;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;


import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import gurpsinittool.data.Actor;
import gurpsinittool.data.GameMaster;
import gurpsinittool.ui.*;
import gurpsinittool.util.EncounterLogEvent;
import gurpsinittool.util.EncounterLogListener;
import gurpsinittool.util.AbstractGAction;
import gurpsinittool.util.MiscUtil;
import gurpsinittool.util.SearchSupport;

@SuppressWarnings("serial")
public class GITApp extends JFrame 
	implements PropertyChangeListener, EncounterLogListener, ListSelectionListener, TableModelListener {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(GITApp.class.getName());
	
	public static final String GIT_VERSION = "1.6.0";
	
	private InitTable initTable;
	private JTextPane logTextArea;
	private HTMLDocument logTextDocument;
	private HTMLEditorKit kit;
	private ActorDetailsPanel_v2 detailsPanel;
	private DefenseDialog defenseDialog;
	private GroupManager groupManager;
	private OptionsWindow optionsWindow;
	private CriticalTablesDialog criticalTables;
	private Properties propertyBag = new Properties();
	private JLabel roundCounter;
	private JSplitPane jSplitPaneVertical;
	private JSplitPane jSplitPaneHorizontal;
	
	// Search support
	private SearchSupport searchSupport;
	
	// Command mode
	private JToggleButton commandModeButton;
	private CommandMode commandMode;

	// GameMaster logic
	private GameMaster gameMaster;
	
	// Actions
	public Action actionOptions;
	public Action actionSizeColumns; // but this applies to the initTable, not sure if it has to be in the GameMaster class
	public Action actionOpenCriticalTables;
	public Action actionOpenGroupManager;
	public Action actionAbout;
	public Action actionToggleCommandMode;
	
	/**
	 * Entry point to the main program
	 * @param args
	 */
    public static void main(String[] args) {
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {  
            	// Setup logging format 	
            	//System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
            	System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s %2$s: %5$s%6$s%n");            
                createAndShowGUI();                
            }
        });
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */   
    private static void createAndShowGUI() {
        //Create and set up the window.
    	if(LOG.isLoggable(Level.INFO)) {LOG.info("Starting GURPS Initiative Tool");}
        final GITApp mainApp = new GITApp("GURPS Initiative Tool");
        mainApp.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // previously EXIT_ON_CLOSE
        mainApp.loadProperties();
        // Here because the defaults depend on the crit table's default location
        mainApp.setDefaultProperties(); // TODO: figure out when we actually set the defaults

        mainApp.initializeActions();
        mainApp.setAccelerators();
        mainApp.addComponentsToPane();
        
        mainApp.groupManager.loadDefaultGroupFile(); // Load the group file
        
        //Display the window.
        if (Boolean.valueOf(mainApp.propertyBag.getProperty("GITApp.Manager.visible"))) {
        	mainApp.groupManager.setVisible(true); }
        mainApp.setVisible(true);
        if(LOG.isLoggable(Level.INFO)) {LOG.info("Tool Started");}
    	
    	// Setup logging levels    	
    	// This has massive problems related to the non-persistence of Logger instances. 
    	// I can't figure out how to adjust the log manager base configuration which would apply to newly created logger objects
    	// If I want to specify logging levels, then I have to keep a static reference I think 
    	// I might be able to get away with a higher-level logger though (like 'gurpsinittool'    	
    	//Logger.getLogger("gurpsinittool").setLevel(Level.FINE);
    	//Logger.getLogger("gurpsinittool.data").setLevel(Level.FINER); 
    	//Logger.getLogger("gurpsinittool.app").setLevel(Level.FINEST); 
    	for(Handler h : Logger.getLogger("").getHandlers()) {
    	    if(h instanceof ConsoleHandler){
    	        h.setLevel(Level.ALL);
    	    }
    	}
    	
//        // Test rig
//		try {
//			final Actor importActor = GCAImporter.importActor(new File("C:\\Repos\\GURPS\\character assistant\\characters\\test.gca4"));
//			mainApp.initTable.getActorTableModel().addActor(importActor, 0);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//        

    }
    
    private GITApp(String name) {
    	super(name);
        gameMaster = new GameMaster(true);
    }

    private void addComponentsToPane() {
    	// Core components
    	initTable = new InitTable(gameMaster, true, propertyBag);
        searchSupport = new SearchSupport(getRootPane(), initTable);
        defenseDialog = new DefenseDialog(this);
        detailsPanel = new ActorDetailsPanel_v2(true);
        kit = new HTMLEditorKit();
        commandMode = new CommandMode(this, defenseDialog, searchSupport, gameMaster);
        commandMode.attachCommandMode(this);     
        
        // Game Master
        gameMaster.initTable = initTable;
        gameMaster.detailsPanel = detailsPanel;
        gameMaster.defenseDialog = defenseDialog;
        gameMaster.addPropertyChangeListener(this);
        initTable.getActorTableModel().addUndoableEditListener(gameMaster);
        
        // Encounter Logging
        logTextDocument = new HTMLDocument();
        logTextArea = new JTextPane();
        logTextArea.setEditorKit(kit);
        logTextArea.setDocument(logTextDocument);
        logTextArea.setEditable(false);
        logTextArea.setFont(new java.awt.Font("Tahoma", 0, 11));
        addLogLine("<i><font color=gray>Encounter Log</color></i>");
        logTextDocument.addUndoableEditListener(gameMaster);
        gameMaster.addEncounterLogEventListener(this);

        // Defense Dialog
        defenseDialog.setLocation(Integer.valueOf(propertyBag.getProperty("GITApp.defense.location.x")),
				 Integer.valueOf(propertyBag.getProperty("GITApp.defense.location.y")));		 
		 
    	// Options window
      	optionsWindow = new OptionsWindow(propertyBag);
        optionsWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // Don't close the program when closing the options window
      	Actor.settings = optionsWindow.currentSettings;
    	
        // The group Manager
        groupManager = new GroupManager(propertyBag);
        criticalTables = new CriticalTablesDialog(this, false);
        criticalTables.setLocation(Integer.valueOf(propertyBag.getProperty("GITApp.crittables.location.x")),
                Integer.valueOf(propertyBag.getProperty("GITApp.crittables.location.y")));
        criticalTables.setSize(Integer.valueOf(propertyBag.getProperty("GITApp.crittables.size.width")),
        		Integer.valueOf(propertyBag.getProperty("GITApp.crittables.size.height")));
        
        addMenuBar();
        addToolBar();
             
        // The actor table
        initTable.getSelectionModel().addListSelectionListener(this);
        initTable.getActorTableModel().addTableModelListener(this);
        
        // Connect Details Panel to the table/tableModel
        JScrollPane tableScrollPane = new JScrollPane(initTable); 
        JScrollPane logScrollPane = new JScrollPane(logTextArea);
        
        // The actor info pane
        JScrollPane actorDetailsPane = new JScrollPane(detailsPanel);
        actorDetailsPane.setMinimumSize(new Dimension(detailsPanel.getPreferredSize().width+21,0));

        // Overall layout
        jSplitPaneVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, logScrollPane);
        jSplitPaneVertical.setDividerLocation(Integer.valueOf(propertyBag.getProperty("GITApp.splitVertical.dividerLocation")));
        jSplitPaneVertical.setResizeWeight(.8);
 
        jSplitPaneHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jSplitPaneVertical, actorDetailsPane);
        jSplitPaneHorizontal.setDividerLocation(Integer.valueOf(propertyBag.getProperty("GITApp.splitHorizontal.dividerLocation")));
        jSplitPaneHorizontal.setResizeWeight(.95);

        getContentPane().add(jSplitPaneHorizontal, BorderLayout.CENTER);
       
        // Undo support
        //undoManager = new UndoManager();
        //refreshUndoRedo();
              
        //Display the window.
        setLocation(Integer.valueOf(propertyBag.getProperty("GITApp.location.x")),
                Integer.valueOf(propertyBag.getProperty("GITApp.location.y")));
        setSize(Integer.valueOf(propertyBag.getProperty("GITApp.size.width")),
        		Integer.valueOf(propertyBag.getProperty("GITApp.size.height")));
        MiscUtil.validateOnScreen(this); // Make sure it's on the screen!
        addWindowListener(new GITAppWindowListener());
    }
   
    private void initializeActions() {
    	actionSizeColumns = new AbstractGAction("Auto-size columns", "Auto re-size the table columns to best fit (Alt+A)", new ImageIcon(GITApp.class.getResource("/resources/images/script_code.png"))) {
    		public void actionPerformed(ActionEvent arg0) {	initTable.autoSizeColumns(); }
    	};
    	actionOptions = new AbstractGAction("Options", "Open the options dialog (Ctrl+O)", KeyEvent.VK_O, null) {
    		public void actionPerformed(ActionEvent arg0) { optionsWindow.setVisible(true); }
    	};
    	actionAbout = new AbstractGAction("About", "Information about the program", null) {
    		public void actionPerformed(ActionEvent arg0) { showAboutDialog(); }
    	};
    	actionOpenCriticalTables = new AbstractGAction("Critical Tables", "Open Critical Tables (Alt+C)", new ImageIcon(GITApp.class.getResource("/resources/images/table_error.png"))) {
    		public void actionPerformed(ActionEvent arg0) { 
    			MiscUtil.validateOnScreen(criticalTables);
        		criticalTables.setVisible(true);
    		}
    	};
    	actionOpenGroupManager = new AbstractGAction("Groups Manager", "Open Groups Manager (Alt+G)", new ImageIcon(GITApp.class.getResource("/resources/images/group.png"))) {
    		public void actionPerformed(ActionEvent arg0) { 
    			MiscUtil.validateOnScreen(groupManager);
        		groupManager.setVisible(true);
    		}
    	};  
    	actionToggleCommandMode = new AbstractGAction("Command Mode: Off", "Toggle Command Mode (Ctrl+Q)", null) {
			public void actionPerformed(ActionEvent arg0) { 
				commandModeButton.setSelected(!(commandMode.getModeEnabled()));
				commandMode.setModeEnabled(commandModeButton.isSelected());
				if (commandModeButton.isSelected()) {
					commandModeButton.setText("Command Mode: On");
				} else {
					commandModeButton.setText("Command Mode: Off");
				}
			}
		};
    }
    
    private void setAccelerators() {
//        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control Z"), "actionUndo");
//        getRootPane().getActionMap().put("actionUndo", gameMaster.actionUndo);
//        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control Y"), "actionRedo");
//        getRootPane().getActionMap().put("actionRedo", gameMaster.actionRedo);
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control N"), "actionNextActor");
        getRootPane().getActionMap().put("actionNextActor", gameMaster.actionNextActor);
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control E"), "actionEndRound");
        getRootPane().getActionMap().put("actionEndRound", gameMaster.actionEndRound);
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control R"), "actionNextRound");
        getRootPane().getActionMap().put("actionNextRound", gameMaster.actionNextRound);
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("alt R"), "actionResetRound");
        getRootPane().getActionMap().put("actionResetRound", gameMaster.actionResetRound);
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("alt A"), "actionSizeColumns");
        getRootPane().getActionMap().put("actionSizeColumns", actionSizeColumns);
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control T"), "actionTagActors");
        getRootPane().getActionMap().put("actionTagActors", gameMaster.actionTagActors);
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control K"), "actionAttack");
        getRootPane().getActionMap().put("actionAttack", gameMaster.actionAttack);
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control D"), "actionDefend");
        getRootPane().getActionMap().put("actionDefend", gameMaster.actionDefend);
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("alt C"), "actionOpenCriticalTables");
        getRootPane().getActionMap().put("actionOpenCriticalTables", actionOpenCriticalTables);
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("alt G"), "actionOpenGroupManager");
        getRootPane().getActionMap().put("actionOpenGroupManager", actionOpenGroupManager);
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control Q"), "actionToggleCommandMode");
        getRootPane().getActionMap().put("actionToggleCommandMode", actionToggleCommandMode);
     }
    
    private void addMenuBar() {
        // The main menu bar
        JMenuBar menubar = new JMenuBar();
        // Edit menu
        JMenu menuFile = new JMenu("Edit");
        menuFile.setMnemonic(KeyEvent.VK_E);
        
        JMenuItem menuItem = new JMenuItem(gameMaster.actionUndo);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        menuFile.add(menuItem);
        menuItem = new JMenuItem(gameMaster.actionRedo);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        menuFile.add(menuItem);
            
        menuItem = new JMenuItem(new DefaultEditorKit.CutAction());
        menuItem.setText("Cut");
        menuItem.setMnemonic(KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        menuItem.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/cut.png"), "Cut"));
        menuFile.add(menuItem);

        menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
        menuItem.setText("Copy");
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        menuItem.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/page_copy.png"), "Copy"));
        menuFile.add(menuItem);

        menuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
        menuItem.setText("Paste");
        menuItem.setMnemonic(KeyEvent.VK_V);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        menuItem.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/paste_plain.png"), "Paste"));
        menuFile.add(menuItem);

        menuItem = new JMenuItem(actionOptions);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuFile.add(menuItem);
 
        menubar.add(menuFile);
        
        // View Menu
        menuFile = new JMenu("View");
        menuFile.setMnemonic(KeyEvent.VK_V);
        menuFile.add(new JMenuItem(initTable.actionShowColumnCustomizer));
        menuItem = new JMenuItem(initTable.actionIncreaseFontSize);
        menuItem.setText("Increase table font size");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
        menuFile.add(menuItem);
        // Add another shortcut for Ctrl+=
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control EQUALS"), "actionIncreaseFontSize");
        getRootPane().getActionMap().put("actionIncreaseFontSize", initTable.actionIncreaseFontSize);
        menuItem = new JMenuItem(initTable.actionDecreaseFontSize);
        menuItem.setText("Decrease table font size");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
        menuFile.add(menuItem);
        menubar.add(menuFile);
        
        // 'Window' Menu
        menuFile = new JMenu("Window");
        menuFile.setMnemonic(KeyEvent.VK_W);    
        menuItem = new JMenuItem(actionOpenGroupManager);
        menuItem.setMnemonic(KeyEvent.VK_G);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.ALT_MASK));
        menuFile.add(menuItem);
        menuFile.setMnemonic(KeyEvent.VK_W);    
        menuItem = new JMenuItem(actionOpenCriticalTables);
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
        menuFile.add(menuItem);
        menubar.add(menuFile);
        
        menubar.add(Box.createHorizontalGlue());
        
        // About menu button
        JButton menuButton = new JButton(actionAbout);
        menuButton.setOpaque(true);
        menuButton.setContentAreaFilled(false);
        menuButton.setBorderPainted(false);
        menuButton.setFocusable(false);
        menubar.add(menuButton);
        
        setJMenuBar(menubar);
    }
    
    private void addToolBar() {
    	// The top tool bar
        JToolBar toolbar = new JToolBar("Encounter Control Toolbar");

        // Round Control
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionNextActor));
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionEndRound));
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionNextRound));
        // Round counter labels
        JLabel label = new JLabel("Round:");
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 5));
        toolbar.add(label);
        roundCounter = new JLabel("0");
        roundCounter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        roundCounter.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        roundCounter.setPreferredSize(new java.awt.Dimension(20, 20));
        // Reset round counter buffer
        toolbar.add(roundCounter);
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionResetRound));
        
        // Auto-resize table columns
        toolbar.addSeparator();
        toolbar.add(MiscUtil.noTextButton(actionSizeColumns)); 
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionTagActors)); 
        
        // Attack / Defend
        toolbar.addSeparator();
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionAttack)); 
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionDefend)); 

        // Posture settings
        toolbar.addSeparator();
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionPostureStanding)); 
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionPostureKneeling)); 
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionPostureProne)); 
        
        // Statuses
        toolbar.addSeparator();
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionStatusTogglePhysicalStun)); 
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionStatusToggleMentalStun)); 
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionStatusToggleRecoveringStun)); 
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionStatusToggleAttacking)); 
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionStatusToggleDisarmed)); 
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionStatusToggleUnconscious)); 
        toolbar.add(MiscUtil.noTextButton(gameMaster.actionStatusToggleDead)); 
              
        // Command Mode
        toolbar.addSeparator();
        toolbar.add(commandModeButton = new JToggleButton(actionToggleCommandMode));
        
        // Search Bar
        toolbar.addSeparator();
        toolbar.add(searchSupport.getSearchToolBar());
        
        // Group manager button & horizontal glue
        toolbar.add(Box.createHorizontalGlue());
        toolbar.addSeparator();
        toolbar.add(MiscUtil.noTextButton(actionOpenCriticalTables));
        toolbar.add(MiscUtil.noTextButton(actionOpenGroupManager));
	
        toolbar.setRollover(true);
        getContentPane().add(toolbar, BorderLayout.PAGE_START);
    }

    private void refreshRoundText() {
    	roundCounter.setText(String.valueOf(gameMaster.getRound()));
    	int minimumWidth = roundCounter.getMinimumSize().width/10 * 10;
    	if (roundCounter.getMinimumSize().width + 1  % 10 != 0) { minimumWidth += 10; }
    	roundCounter.setPreferredSize(new Dimension(minimumWidth, 20));
    	if(LOG.isLoggable(Level.FINER)) {LOG.finer("Minimum round counter size = " + roundCounter.getMinimumSize().toString());}
    }

    /**
     * Save the propertyBag to a settings file. 
     * @return - Whether the operation succeeded or not.
     */
    private void loadProperties() {
    	// Property file should be the same name as the app
    	try {
    		final File propertyFile = new File("GitApp.props");
    		if (propertyFile.exists()) {
    			if(LOG.isLoggable(Level.INFO)) {LOG.info("Loading properties from file " + propertyFile.getAbsolutePath()); }
    			final InputStream propIn = new FileInputStream(propertyFile);
    			propertyBag.load(propIn);    			
     		} else {
       			if(LOG.isLoggable(Level.INFO)) {LOG.info("Properties file does not exist " + propertyFile.getAbsolutePath());}
    		}
		} catch (FileNotFoundException e) {
			if(LOG.isLoggable(Level.WARNING)) {LOG.warning("Caught exception: File not found: " + e.toString());}
		} catch (IOException e) {
			if(LOG.isLoggable(Level.SEVERE)) {LOG.log(Level.SEVERE, "Caught exception: Error reading file! " + e.toString(),e);}
		}
    }
    
    /**
     * Save the propertyBag to a settings file. 
     * @return - Whether the operation succeeded or not.
     */
    private boolean saveProperties() {
    	// Property file should be the same name as the app
    	try {
    		final File propertyFile = new File("GitApp.props");
    		if(LOG.isLoggable(Level.INFO)) {LOG.info("Saving properties to file " + propertyFile.getAbsolutePath());}
			final OutputStream propOut = new FileOutputStream(propertyFile);
			propertyBag.store(propOut, "GITApp Properties");
		} catch (FileNotFoundException e) {
			if(LOG.isLoggable(Level.SEVERE)) {LOG.log(Level.SEVERE, "File not found? " + e.toString(), e);}
			return false;
		} catch (IOException e) {
			if(LOG.isLoggable(Level.SEVERE)) {LOG.log(Level.SEVERE, "Error writing to file! " + e.toString(), e);}
			return false;
		}
    	return true;
    }
    

	 /**
	  * Set default properties if they are not already defined.
	  */
	 private void setDefaultProperties() {
		 if (!propertyBag.containsKey("GITApp.Manager.visible")) {
			 propertyBag.setProperty("GITApp.Manager.visible", "false"); }
		 if (!propertyBag.containsKey("GITApp.splitHorizontal.dividerLocation")) {
			 propertyBag.setProperty("GITApp.splitHorizontal.dividerLocation", "580"); }
		 if (!propertyBag.containsKey("GITApp.splitVertical.dividerLocation")) {
			 propertyBag.setProperty("GITApp.splitVertical.dividerLocation", "200"); }

		 if (!propertyBag.containsKey("GITApp.location.x")) {
			 propertyBag.setProperty("GITApp.location.x", "400"); }
		 if (!propertyBag.containsKey("GITApp.location.y")) {
			 propertyBag.setProperty("GITApp.location.y", "400"); }
		 if (!propertyBag.containsKey("GITApp.size.width")) {
			 propertyBag.setProperty("GITApp.size.width", "850"); }
		 if (!propertyBag.containsKey("GITApp.size.height")) {
			 propertyBag.setProperty("GITApp.size.height", "550"); }
		 if (!propertyBag.containsKey("GITApp.defense.location.x")) {
			 propertyBag.setProperty("GITApp.defense.location.x", "200"); }
		 if (!propertyBag.containsKey("GITApp.defense.location.y")) {
			 propertyBag.setProperty("GITApp.defense.location.y", "200"); }

		 if (!propertyBag.containsKey("GITApp.crittables.location.x")) {
			 propertyBag.setProperty("GITApp.crittables.location.x", "175"); }
		 if (!propertyBag.containsKey("GITApp.crittables.location.y")) {
			 propertyBag.setProperty("GITApp.crittables.location.y", "175"); }
		 if (!propertyBag.containsKey("GITApp.crittables.size.width")) {
			 propertyBag.setProperty("GITApp.crittables.size.width",  String.valueOf(new CriticalTablesDialog(this, false).getPreferredSize().width)); }
		 if (!propertyBag.containsKey("GITApp.crittables.size.height")) {
			 propertyBag.setProperty("GITApp.crittables.size.height",  String.valueOf(new CriticalTablesDialog(this, false).getPreferredSize().height)); }
	 }
	 
	 /**
	  * Update all the store-able properties to their current values
	  */
	 private void updateProperties() {
		 // Kept up-to-date with event listeners
		 propertyBag.setProperty("GITApp.defense.location.x", String.valueOf(defenseDialog.getLocation().x));
		 propertyBag.setProperty("GITApp.defense.location.y", String.valueOf(defenseDialog.getLocation().y));
		 propertyBag.setProperty("GITApp.Manager.visible", String.valueOf(groupManager.isVisible()));
		 propertyBag.setProperty("GITApp.splitHorizontal.dividerLocation", String.valueOf(jSplitPaneHorizontal.getDividerLocation()));
		 propertyBag.setProperty("GITApp.splitVertical.dividerLocation", String.valueOf(jSplitPaneVertical.getDividerLocation()));
		 propertyBag.setProperty("GITApp.location.x", String.valueOf(getLocation().x));
		 propertyBag.setProperty("GITApp.location.y", String.valueOf(getLocation().y));
		 propertyBag.setProperty("GITApp.size.width", String.valueOf(getSize().width));
		 propertyBag.setProperty("GITApp.size.height", String.valueOf(getSize().height));
		 propertyBag.setProperty("GITApp.crittables.location.x", String.valueOf(criticalTables.getLocation().x));
		 propertyBag.setProperty("GITApp.crittables.location.y", String.valueOf(criticalTables.getLocation().y));
		 propertyBag.setProperty("GITApp.crittables.size.width", String.valueOf(criticalTables.getSize().width));
		 propertyBag.setProperty("GITApp.crittables.size.height", String.valueOf(criticalTables.getSize().height));
		 // Optional properties
		// if (saveAsFile != null) { propertyBag.setProperty("GITApp.currentLoadedFile", saveAsFile.getAbsolutePath());}
		 //else { propertyBag.remove("GITApp.currentLoadedFile");}
	 }
	 
	 private void showAboutDialog() {
		 String famfamcredit = "<a href=\"http://www.famfamfam.com/lab/icons/silk/\">Silk</a> icons by <a href=\"http://www.famfamfam.com/\">Mark James</a> licensed under <a href=\"http://creativecommons.org/licenses/by/2.5/\">CC BY 2.5</a> / used with minor changes.";
		 String policy = "GURPS is a trademark of Steve Jackson Games, and its rules and art are copyrighted by Steve Jackson Games. All rights are reserved by Steve Jackson Games. This game aid is the original creation of Damian Small and is released for free distribution, and not for resale, under the permissions granted in the <a href=\"http://www.sjgames.com/general/online_policy.html\">Steve Jackson Games Online Policy</a>.";
		 // for copying style
		 JLabel label = new JLabel();
		 Font font = label.getFont();

		 // create some css from the label's font
		 StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
		 style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
		 style.append("font-size:" + font.getSize() + "pt;");

		 JEditorPane ep = new JEditorPane("text/html","<html><body style=\"" + style + "\"><b>GURPS Initiative Tool</b><br>Version: " + GIT_VERSION + "<br><p style='width: 300px;'>" + policy + "</p><p style='width: 300px;'>" + famfamcredit + "</p><br></body></html>");
		 ep.addHyperlinkListener(new HyperlinkListener() {       						
			 @Override
			 public void hyperlinkUpdate(HyperlinkEvent e) {
				 if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
					 try {
						 Desktop.getDesktop().browse(e.getURL().toURI());
					 } catch (IOException e1) {
						 e1.printStackTrace();
					 } catch (URISyntaxException e1) {
						 e1.printStackTrace();
					 }
			 }
		 });
		 ep.setEditable(false);ep.setBackground(new JLabel().getBackground());
		 JOptionPane.showMessageDialog(this, ep, "About GURPS Initiative Tool", JOptionPane.INFORMATION_MESSAGE);
	 }

	 /**
	  * Add a text line to the logTextArea
	  * @param line
	  */
	 protected void addLogLine(final String line) {    	
		 try {
			 kit.insertHTML(logTextDocument, logTextDocument.getLength(), line, 0, 0, null);
		 } catch (BadLocationException e) {
			 if(LOG.isLoggable(Level.SEVERE)) {LOG.log(Level.SEVERE, "BadLocationException trying to add line: " + line, e);}
		 } catch (IOException e) {
			 if(LOG.isLoggable(Level.SEVERE)) {LOG.log(Level.SEVERE, "IOException trying to add line: " + line, e);}
		 }
		 // Move cursor to the end
		 logTextArea.select(logTextDocument.getLength(), logTextDocument.getLength());
	 }

	 @Override
	 public void encounterLogMessageSent(EncounterLogEvent evt) {		 
		 addLogLine(evt.getLogMsg());
	 }

	 /**
	  * Handle table changed event from initTable
	  * @param event
	  */
	@Override
	public void tableChanged(final TableModelEvent event) {
		detailsPanel.setActor(initTable.getSelectedActor());
	}

	/**
	 * Handle list selection event from initTable
	 * @param event
	 */
	@Override
	public void valueChanged(final ListSelectionEvent event) {
		if (!event.getValueIsAdjusting()) {
			if(LOG.isLoggable(Level.FINE)) {LOG.fine("Table list selection event");}
			detailsPanel.setActor(initTable.getSelectedActor());
		}
	}
		
    /**
     * An Inner class to monitor the window events
     */
    private class GITAppWindowListener implements WindowListener {

		@Override
		public void windowActivated(WindowEvent arg0) {}
		@Override
		public void windowClosed(WindowEvent arg0) {}

		@Override
		public void windowClosing(WindowEvent evt) {
			if(LOG.isLoggable(Level.INFO)) {LOG.info("Tool closing");}
			// Update all the various properties:
			updateProperties();
			initTable.updateProperties();
			groupManager.updateProperties();
			optionsWindow.updateProperties();
			// Check to make sure everything is clean
			if(groupManager.querySaveChanges()) {
				saveProperties();
				System.exit(0);
			}
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {}
		@Override
		public void windowDeiconified(WindowEvent arg0) {}
		@Override
		public void windowIconified(WindowEvent arg0) {}
		@Override
		public void windowOpened(WindowEvent arg0) {}
    }

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals("Round")) {
			refreshRoundText();
		}
	}
}
