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
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import gurpsinittool.data.Actor;
import gurpsinittool.data.ActorBase.ActorStatus;
import gurpsinittool.ui.*;
import gurpsinittool.util.EncounterLogEvent;
import gurpsinittool.util.EncounterLogEventListener;

public class GITApp extends JFrame 
	implements ActionListener, EncounterLogEventListener, ListSelectionListener, TableModelListener {

	// Default SVUID
	private static final long serialVersionUID = 1L;
	
	public static final String version = "1.2.1";
	private static final boolean DEBUG = false;
	
	private InitTable initTable;
	private JTextPane logTextArea;
	private HTMLDocument logTextDocument;
	private HTMLEditorKit kit;
	private ActorDetailsPanel_v2 detailsPanel;
	private GroupManager groupManager;
	private OptionsWindow optionsWindow;
	private CriticalTablesDialog criticalTables;
	private Properties propertyBag = new Properties();
	private JLabel roundCounter;
	private JSplitPane jSplitPaneVertical;
	private JSplitPane jSplitPaneHorizontal;
	
	//private UndoManager undoManager;
	//private JMenuItem undoMenuItem;
	//private JMenuItem redoMenuItem;
	
	private Integer round = 0;
	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        GITApp mainApp = new GITApp("GURPS Initiative Tool");
        mainApp.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // previously EXIT_ON_CLOSE
        mainApp.loadProperties();
        mainApp.addComponentsToPane();
        
        //Display the window.
        //mainApp.pack();
        if (Boolean.valueOf(mainApp.propertyBag.getProperty("GITApp.Manager.visible"))) {
        	mainApp.groupManager.setVisible(true); }
        mainApp.setVisible(true);
    }

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
                createAndShowGUI();
            }
        });
    }
    
    public GITApp(String name) {
    	super(name);
    }
    
    //@Override
    public void actionPerformed(ActionEvent e) {
    	if ("resetRound".equals(e.getActionCommand())) {
			round = 0;
    		refreshRoundText();
    		initTable.resetEncounter();
    		nextActor();
    	}	
    	else if ("openGroupManager".equals(e.getActionCommand())) {
    		validateOnScreen(groupManager);
    		groupManager.setVisible(true);
    	}
       	else if ("openCriticalTables".equals(e.getActionCommand())) {
    		validateOnScreen(criticalTables);
    		criticalTables.setVisible(true);
    	}
       	else if ("sizeColumns".equals(e.getActionCommand())) {
    		initTable.autoSizeColumns();
    	}	
       	else if ("actorsStand".equals(e.getActionCommand())) {
       		initTable.modifyStatusOfSelectedActors(ActorStatus.Kneeling, false);
       		initTable.modifyStatusOfSelectedActors(ActorStatus.Prone, false);
       	}
       	else if ("actorsKneel".equals(e.getActionCommand())) {
       		initTable.modifyStatusOfSelectedActors(ActorStatus.Kneeling, true);
       		initTable.modifyStatusOfSelectedActors(ActorStatus.Prone, false);
       	}
       	else if ("actorsProne".equals(e.getActionCommand())) {
       		initTable.modifyStatusOfSelectedActors(ActorStatus.Kneeling, false);
       		initTable.modifyStatusOfSelectedActors(ActorStatus.Prone, true);
       	}
       	else if ("actorsStunPhysToggle".equals(e.getActionCommand())) {
       		initTable.toggleStatusOfSelectedActors(ActorStatus.StunPhys);
       	}
       	else if ("actorsStunMentalToggle".equals(e.getActionCommand())) {
       		initTable.toggleStatusOfSelectedActors(ActorStatus.StunMental);
       	}
       	else if ("actorsDisarmToggle".equals(e.getActionCommand())) {
       		initTable.toggleStatusOfSelectedActors(ActorStatus.Disarmed);
       	}
       	else if ("actorsUnconsciousToggle".equals(e.getActionCommand())) {
       		initTable.toggleStatusOfSelectedActors(ActorStatus.Unconscious);
       	}
       	else if ("actorsDeadToggle".equals(e.getActionCommand())) {
       		initTable.toggleStatusOfSelectedActors(ActorStatus.Dead);
       	}
       	else if ("Options".equals(e.getActionCommand())) {
       		optionsWindow.setVisible(true);
       	}
       	else if ("About".equals(e.getActionCommand())) {   
       		showAboutDialog();
       	}
       	else {
   			System.out.println("GITApp: -W- Unknown action performed: " + e.getActionCommand());
       	}
	}
    

	@Override
	public void encounterLogMessageSent(EncounterLogEvent evt) {
		addLogLine(evt.logMsg);
	}
	
	public void addLogLine(String line) {
		addLogLine(line, true);
	}
	
    public void addLogLine(String line, boolean addRound) {
    	if (round <= 0) return; // Don't print log messages before round 0
    	try {
    		String round = addRound?"Round " + roundCounter.getText() + ": ":"";
			kit.insertHTML(logTextDocument, logTextDocument.getLength(), round + line, 0, 0, null);
		} catch (BadLocationException e) {
			System.out.println("-E- addLogLine: BadLocationException trying to add line: " + line);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("-E- addLogLine: IOException trying to add line: " + line);
			e.printStackTrace();
		}
    	// Move cursor to the end.
    	logTextArea.select(logTextDocument.getLength(), logTextDocument.getLength());
    }
    
    /**
     * Step to the next actor, taking auto-actions for the new actor if appropriate
     * @return Whether the round has ended
     */
    public boolean nextActor() {
    	// Check for start of round
    	if (initTable.getActorTableModel().getActiveActorIndex() == -1) {
			++round;
			refreshRoundText();
			addLogLine("<b>** Round " + roundCounter.getText() + " **</b>", false);
    	}
    	
    	// Change to next active actor
    	return initTable.nextActor();
    }
    
    /**
     * Step through the actors until reaching the next round
     */
    public void nextRound() {
    	if (initTable.getActiveActor() != null) endRound(); // Step to the end of the round if not there already
    	nextActor(); // And then into the new round
    }
    
    /**
     * Step through the actors until reaching the end of the current round
     */
    public void endRound() {
    	while(!nextActor()) {}
    }
    
    private void refreshRoundText() {
		roundCounter.setText(round.toString());
		int minimumWidth = roundCounter.getMinimumSize().width/10 * 10;
		if (roundCounter.getMinimumSize().width + 1  % 10 != 0) { minimumWidth += 10; }
		roundCounter.setPreferredSize(new Dimension(minimumWidth, 20));
		if (DEBUG) { System.out.println("GITApp: Minimum round counter size = " + roundCounter.getMinimumSize().toString()); }
   }
    
    private void addComponentsToPane() {
        //contentPanel.setOpaque(true);
        //frame.setContentPane(contentPanel);
    	// Options window
      	optionsWindow = new OptionsWindow(propertyBag);
        optionsWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // Don't close the program when closing the options window
      	Actor.settings = optionsWindow.currentSettings;
    	
        // The group Manager
        groupManager = new GroupManager(propertyBag);
        criticalTables = new CriticalTablesDialog(this, false);
        // Here because the defaults depend on the crit table's default location
        setDefaultProperties(); // TODO: figure out when we actually set the defaults
        criticalTables.setLocation(Integer.valueOf(propertyBag.getProperty("GITApp.crittables.location.x")),
                Integer.valueOf(propertyBag.getProperty("GITApp.crittables.location.y")));
        criticalTables.setSize(Integer.valueOf(propertyBag.getProperty("GITApp.crittables.size.width")),
        		Integer.valueOf(propertyBag.getProperty("GITApp.crittables.size.height")));

        
        // The main menu bar
        JMenuBar menubar = new JMenuBar();
        JMenu menuFile = new JMenu("Edit");
        menuFile.setMnemonic(KeyEvent.VK_E);
//        undoMenuItem = new JMenuItem("Undo", KeyEvent.VK_U);
//        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
//        undoMenuItem.getAccessibleContext().setAccessibleDescription("Undo the last reversible action");
//        undoMenuItem.addActionListener(this);
//        menuFile.add(undoMenuItem);
//        redoMenuItem = new JMenuItem("Redo", KeyEvent.VK_R);
//        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
//        redoMenuItem.getAccessibleContext().setAccessibleDescription("Redo the last undone action");
//        redoMenuItem.addActionListener(this);
//        menuFile.add(redoMenuItem);

        JMenuItem menuItem = new JMenuItem(new DefaultEditorKit.CutAction());
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
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        menuItem.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/paste_plain.png"), "Paste"));
        menuFile.add(menuItem);

        menuItem = new JMenuItem("Options", KeyEvent.VK_O);
        menuItem.setText("Options");
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        menuFile.add(menuItem);
        
        menubar.add(menuFile);
        menubar.add(Box.createHorizontalGlue());
        
        JButton menuButton = new JButton("About");
        menuButton.setOpaque(true);
        menuButton.setContentAreaFilled(false);
        menuButton.setBorderPainted(false);
        menuButton.setFocusable(false);
        menuButton.addActionListener(this);
        menubar.add(menuButton);
        
       // menuItem = new JMenuItem("About");
        //menuItem.setText("About");
       // menuItem.setMaximumSize( menuItem.getPreferredSize() );
        //menuFile.setMnemonic(KeyEvent.VK_A);
       // menuItem.addActionListener(this);
        //menubar.add(menuItem);
        
        setJMenuBar(menubar);
  
        // The top tool bar
        JToolBar toolbar = new JToolBar("Encounter Control Toolbar");
        // Next Actor (first button)
        JButton button = new JButton();
        java.net.URL imageURL = GITApp.class.getResource("/resources/images/control_play_blue.png");
        if (imageURL != null) {
        	button.setIcon(new ImageIcon(imageURL, "Next Actor")); 
        }
        //button.setIcon(new ImageIcon("src/resources/images/control_play_blue.png", "Next Actor"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        //button.setText("Forward");
        button.setToolTipText("Step to next combatant (Alt+N)");
        Action action = new AbstractAction("nextActor") {
        	public void actionPerformed(ActionEvent e) { nextActor(); }
        };
        button.setMnemonic(KeyEvent.VK_N);
        button.addActionListener(action);
        toolbar.add(button);
        // Step to end of round 
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/control_end_blue.png"), "End Round"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Step to the end of this round (Ctrl+E)");
        action = new AbstractAction("endRound") {
        	public void actionPerformed(ActionEvent e) { endRound(); }
        };
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control E"), "endRound");
        button.getActionMap().put("endRound", action);
        button.addActionListener(action);
        toolbar.add(button);
        // Next round 
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/control_fastforward_blue.png"), "Next Round"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Step to the next round (Ctrl+N)");
        action = new AbstractAction("nextRound") {
        	public void actionPerformed(ActionEvent e) { nextRound(); }
        };
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control N"), "nextRound");
        button.getActionMap().put("nextRound", action);
        button.addActionListener(action);
        toolbar.add(button);
        // Round counter labels
        JLabel label = new JLabel("Round:");
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 5));
        toolbar.add(label);
        label = new JLabel("0");
        //label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        label.setPreferredSize(new java.awt.Dimension(20, 20));
        roundCounter = label; 
        toolbar.add(label);
        // Reset round counter buffer
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/control_start_blue.png"), "Reset Encounter"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Reset the round counter (Alt+R)");
        button.setActionCommand("resetRound");
        button.setMnemonic(KeyEvent.VK_R);
        button.addActionListener(this);
        toolbar.add(button);
        // Auto-resize table columns
        toolbar.addSeparator();
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/script_code.png"), "Auto-size columns"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Auto re-size the table columns to best fit (Alt+A)");
        button.setActionCommand("sizeColumns");
        button.setMnemonic(KeyEvent.VK_A);
        button.addActionListener(this);
        toolbar.add(button);
        // Attack
        toolbar.addSeparator();
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/wrench_orange.png"), "Attack"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Selected combatants attack (Ctrl+A)");
        action = new AbstractAction("selectedActorsAttack") {
        	public void actionPerformed(ActionEvent e) { initTable.selectedActorsAttack(); }
        };
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control A"), "actorsAttack");
        button.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("control A"), "actorsAttack");
        button.getActionMap().put("actorsAttack", action);
        //button.setMnemonic(KeyEvent.VK_A);
        button.addActionListener(action);
        toolbar.add(button);
        // Defend
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/shield.png"), "Defend"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Selected combatants defends (Ctrl+D)");
        action = new AbstractAction("selectedActorDefend") {
        	public void actionPerformed(ActionEvent e) { initTable.selectedActorDefend(); }
        };
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control D"), "actorDefend");
        button.getActionMap().put("actorDefend", action);
        //button.setMnemonic(KeyEvent.VK_D);
        button.addActionListener(action);
        toolbar.add(button);
        // Tag
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/tag_blue_add.png"), "Tag"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Add NPC tags (Ctrl+T)");
        action = new AbstractAction("tagActors") {
        	public void actionPerformed(ActionEvent e) { initTable.getActorTableModel().autoTagActors(); initTable.autoSizeColumns(); }
        };
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control T"), "tagActors");
        button.getActionMap().put("tagActors", action);
        //button.setMnemonic(KeyEvent.VK_D);
        button.addActionListener(action);
        toolbar.add(button);
        //Group manager button & horizontal glue
        toolbar.addSeparator();
        toolbar.addSeparator();
        // Postures: Standing
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/arrow_up.png"), "Standing"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Standing");
        button.setActionCommand("actorsStand");
        button.addActionListener(this);
        toolbar.add(button);
        // Postures: Kneeling
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/arrow_down_right.png"), "Kneeling"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Kneeling");
        button.setActionCommand("actorsKneel");
        button.addActionListener(this);
        toolbar.add(button);
        // Postures: Prone
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/arrow_down.png"), "Prone"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Prone");
        button.setActionCommand("actorsProne");
        button.addActionListener(this);
        toolbar.add(button);
        toolbar.addSeparator();
        // Stunned Physical
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/transmit.png"), "Physical Stun"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Physically Stunned");
        button.setActionCommand("actorsStunPhysToggle");
        button.addActionListener(this);
        toolbar.add(button);
        // Stunned Mental
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/transmit_blue.png"), "Mental Stun"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Mentally Stunned");
        button.setActionCommand("actorsStunMentalToggle");
        button.addActionListener(this);
        toolbar.add(button);
        // Disarmed
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/plus_blue.png"), "Disarmed"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Disarmed");
        button.setActionCommand("actorsDisarmToggle");
        button.addActionListener(this);       
        toolbar.add(button);
        // Unconscious
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/cross_yellow.png"), "Unconscious"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setActionCommand("actorsUnconsciousToggle");
        button.addActionListener(this);
        button.setToolTipText("Unconscious");
        toolbar.add(button);    
        // Dead
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/cross.png"), "Dead"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Dead");
        button.setActionCommand("actorsDeadToggle");
        button.addActionListener(this);
        toolbar.add(button);
        toolbar.addSeparator();
        toolbar.add(Box.createHorizontalGlue());
        toolbar.addSeparator();
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/table_error.png"), "Critical Tables"));
        button.setToolTipText("Open Critical Tables (Alt+C)");
        button.setActionCommand("openCriticalTables");
        button.setMnemonic(KeyEvent.VK_C);
        button.addActionListener(this);
        toolbar.add(button);
        toolbar.addSeparator();
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/group.png"), "Group Manager"));
        button.setToolTipText("Open Group Manager (Alt+G)");
        button.setActionCommand("openGroupManager");
        button.setMnemonic(KeyEvent.VK_G);
        button.addActionListener(this);
        toolbar.add(button);
        toolbar.setRollover(true);
        getContentPane().add(toolbar, BorderLayout.PAGE_START);
 
        // Encounter Log
        logTextArea = new JTextPane();
        logTextDocument = new HTMLDocument();
         kit = new HTMLEditorKit();
        logTextArea.setEditorKit(kit);
        logTextArea.setDocument(logTextDocument);
        logTextArea.setEditable(false);
        logTextArea.setFont(new java.awt.Font("Tahoma", 0, 11));

        // The actor table
        initTable = new InitTable(propertyBag, true);
        initTable.getSelectionModel().addListSelectionListener(this);
        initTable.getActorTableModel().addEncounterLogEventListener(this);
        initTable.getActorTableModel().addTableModelListener(this);
        // Replace Ctrl+A = select all map for init table to actors attack
        initTable.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control A"), "actorsAttack");
        action = new AbstractAction("selectedActorsAttack") {
        	public void actionPerformed(ActionEvent e) { initTable.selectedActorsAttack(); }
        };
        initTable.getActionMap().put("actorsAttack", action);
        
        // Connect Details Panel to the table/tableModel
        JScrollPane tableScrollPane = new JScrollPane(initTable); 
        JScrollPane logScrollPane = new JScrollPane(logTextArea);
        
        // The actor info pane
        detailsPanel = new ActorDetailsPanel_v2();
        JScrollPane actorDetailsPane = new JScrollPane(detailsPanel);
        actorDetailsPane.setMinimumSize(new Dimension(detailsPanel.getPreferredSize().width+20,0));

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
        validateOnScreen(this); // Make sure it's on the screen!
        addWindowListener(new GITAppWindowListener());

    }
    
    /**
     * Save the propertyBag to a settings file. 
     * @return - Whether the operation succeeded or not.
     */
    private void loadProperties() {
    	// Property file should be the same name as the app
    	try {
    		File propertyFile = new File("GitApp.props");
    		if (!propertyFile.exists()) {
    			if (DEBUG) { System.out.println("GITApp: loadProperties: properties file does not exist " + propertyFile.getAbsolutePath()); }
    		} else {
    			if (DEBUG) { System.out.println("GITApp: loadProperties: Loading properties from file " + propertyFile.getAbsolutePath()); }
    			InputStream propIn = new FileInputStream(propertyFile);
    			propertyBag.load(propIn);
    		}
		} catch (FileNotFoundException e) {
			System.out.println("GITApp: loadProperties: Caught exception: File not found: " + e.toString());
		} catch (IOException e) {
			System.out.println("GITApp: loadProperties: Caught exception: Error reading file! " + e.toString());
			e.printStackTrace();
		}
    }
    
    /**
     * Save the propertyBag to a settings file. 
     * @return - Whether the operation succeeded or not.
     */
    private boolean saveProperties() {
    	// Property file should be the same name as the app
    	try {
    		File propertyFile = new File("GitApp.props");
			if (DEBUG) { System.out.println("GITApp: saveProperties: Saving properties to file " + propertyFile.getAbsolutePath()); }
			OutputStream propOut = new FileOutputStream(propertyFile);
			propertyBag.store(propOut, "GITApp Properties");
		} catch (FileNotFoundException e) {
			System.out.println("GITApp: saveProperties: File not found? " + e.toString());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.out.println("GITApp: saveProperties: Error writing to file! " + e.toString());
			e.printStackTrace();
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
			 propertyBag.setProperty("GITApp.splitHorizontal.dividerLocation", "460"); }
		 if (!propertyBag.containsKey("GITApp.splitVertical.dividerLocation")) {
			 propertyBag.setProperty("GITApp.splitVertical.dividerLocation", "200"); }

		 if (!propertyBag.containsKey("GITApp.location.x")) {
			 propertyBag.setProperty("GITApp.location.x", "400"); }
		 if (!propertyBag.containsKey("GITApp.location.y")) {
			 propertyBag.setProperty("GITApp.location.y", "400"); }
		 if (!propertyBag.containsKey("GITApp.size.width")) {
			 propertyBag.setProperty("GITApp.size.width", "760"); }
		 if (!propertyBag.containsKey("GITApp.size.height")) {
			 propertyBag.setProperty("GITApp.size.height", "480"); }
	     // TODO: this is a hack of the property bag system: fix!
		 if (!propertyBag.containsKey("GITApp.defense.location.x")) {
			 propertyBag.setProperty("GITApp.defense.location.x", "200"); }
		 if (!propertyBag.containsKey("GITApp.defense.location.y")) {
			 propertyBag.setProperty("GITApp.defense.location.y", "200"); }

		 if (!propertyBag.containsKey("GITApp.crittables.location.x")) {
			 propertyBag.setProperty("GITApp.crittables.location.x", "175"); }
		 if (!propertyBag.containsKey("GITApp.crittables.location.y")) {
			 propertyBag.setProperty("GITApp.crittables.location.y", "175"); }
		 if (!propertyBag.containsKey("GITApp.crittables.size.width")) {
			 propertyBag.setProperty("GITApp.crittables.size.width",  String.valueOf(criticalTables.getPreferredSize().width)); }
		 if (!propertyBag.containsKey("GITApp.crittables.size.height")) {
			 propertyBag.setProperty("GITApp.crittables.size.height",  String.valueOf(criticalTables.getPreferredSize().height)); }
	 }
	 
	 /**
	  * Update all the store-able properties to their current values
	  */
	 public void updateProperties() {
		 // Kept up-to-date with event listeners
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
	 
	 public static void validateOnScreen(Component c) {
		 final Rectangle window = c.getBounds();
		 
		 Rectangle virtualscreen = new Rectangle();
		 GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		 for (int j = 0; j < gs.length; j++) { 
			 GraphicsConfiguration[] gc = gs[j].getConfigurations();
			 for (int i=0; i < gc.length; i++) {
				 virtualscreen = virtualscreen.union(gc[i].getBounds());
			 }
		 }
		 if (DEBUG) System.out.println("Testing window position: screen: " + virtualscreen + " window: " + window);
  
		 if (!virtualscreen.contains(window)) {
			 // Snap position to screen
			 // If top-left corner is to the left of the screen
			 if (window.x < virtualscreen.x) {
				 if (DEBUG) System.out.println("Window out of screen: translating +x");
				 window.translate(virtualscreen.x - window.x, 0);
			 }
			 // top-left corner is above the screen
			 if (window.y < virtualscreen.y) {
				 if (DEBUG) System.out.println("Window out of screen: translating +y");
				 window.translate(0, virtualscreen.y - window.y);
			 }
			 // Size bigger than window
			 if (window.height > virtualscreen.height) {
				 if (DEBUG) System.out.println("Window out of screen: resizing: smaller height");
				 window.height = virtualscreen.height;
			 }
			 if (window.width > virtualscreen.width) {
				 if (DEBUG) System.out.println("Window out of screen: resizing: smaller width");
				 window.width = virtualscreen.width;
			 }
			 // bottom-right corner is to the right of the screen
			 if ((window.x+window.width) > (virtualscreen.x+virtualscreen.width)) {
				 if (DEBUG) System.out.println("Window out of screen: translating -x");
				 window.translate((virtualscreen.x+virtualscreen.width)-(window.x+window.width),0);				 
			 }
			 // bottom-right corner is below the screen
			 if ((window.y+window.height) > (virtualscreen.y+virtualscreen.height)) {
				 if (DEBUG) System.out.println("Window out of screen: translating -y");
				 window.translate(0,(virtualscreen.y+virtualscreen.height)-(window.y+window.height));				 
			 }
		 }
		 c.setLocation(window.x, window.y);
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

		 JEditorPane ep = new JEditorPane("text/html","<html><body style=\"" + style + "\"><b>GURPS Initiative Tool</b><br>Version: " + version + "<br><p style='width: 300px;'>" + policy + "</p><p style='width: 300px;'>" + famfamcredit + "</p><br></body></html>");
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
	 
//	 /**
//	  * This method is called after each undoable operation
//	  * in order to refresh the presentation state of the
//	  * undo/redo GUI
//	  */
//	  public void refreshUndoRedo() {
//	     // refresh undo
//		 undoMenuItem.setText(undoManager.getUndoPresentationName());
//		 undoMenuItem.setEnabled(undoManager.canUndo());
//
//	     // refresh redo
//	     redoMenuItem.setText(undoManager.getRedoPresentationName());
//	     redoMenuItem.setEnabled(undoManager.canRedo());
//	  }

//	 /**
//	  * Accessor method for undoManager
//	  */
//	 public UndoManager getUndoManager() {
//		 return undoManager;
//	 }
	 
    /**
     * An Inner class to monitor the window events
     */
    class GITAppWindowListener implements WindowListener {

		@Override
		public void windowActivated(WindowEvent arg0) {}
		@Override
		public void windowClosed(WindowEvent arg0) {}

		@Override
		public void windowClosing(WindowEvent evt) {
			// Update all the various properties:
			updateProperties();
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
	public void tableChanged(TableModelEvent e) {
		detailsPanel.setActor(initTable.getSelectedActor());
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			detailsPanel.setActor(initTable.getSelectedActor());
		}
	}
}
