package gurpsinittool.app;

import javax.swing.*;  
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.UndoManager;

import sun.awt.HorizBagLayout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
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
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gurpsinittool.app.InitTableModel.columns;
import gurpsinittool.data.Actor;
import gurpsinittool.data.Actor.ActorState;
import gurpsinittool.data.Actor.ActorType;
import gurpsinittool.ui.*;
import gurpsinittool.ui.DefenseDialog.DefenseResult;
import gurpsinittool.ui.DefenseDialog.DefenseType;
import gurpsinittool.util.DieRoller;
import gurpsinittool.util.EncounterLogEvent;
import gurpsinittool.util.EncounterLogEventListener;

public class GITApp extends JFrame implements ActionListener, EncounterLogEventListener {

	// Default SVUID
	private static final long serialVersionUID = 1L;
	
	private static final boolean DEBUG = false;
	
	// Each enemy actor automatically attacks at the end of his turn
	private boolean AUTO_ATTACK = true;
	private boolean AUTO_UNCONSCIOUS = true;
	
	private InitTable initTable;
	private JTextPane logTextArea;
	private HTMLDocument logTextDocument;
	private HTMLEditorKit kit;
	private ActorDetailsPanel detailsPanel;
	private GroupManager groupManager;
	private CriticalTablesDialog criticalTables;
	private Properties propertyBag = new Properties();
	private JLabel roundCounter;
	private JSplitPane jSplitPaneVertical;
	private JSplitPane jSplitPaneHorizontal;
	
	private UndoManager undoManager;
	private JMenuItem undoMenuItem;
	private JMenuItem redoMenuItem;
	
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
       	else {
   			System.out.println("GITApp: -W- Unknown action performed: " + e.getActionCommand());
       	}
	}
    
    public void actorAttack() {
    	if (DEBUG) System.out.println("GITApp: ActorAttack");
    	Actor actor = initTable.getActiveActor();
    	if (actor == null) 
    		return;
    	addLogLine(actor.Attack());
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
    
    public void actorDefend() {
    	System.out.println("GITApp: ActorDefend: start");
    	// Verify valid actor
    	Actor actor = initTable.getSelectedActor();
    	if (actor == null)
    		return;
    	// Clear out edits in progress
    	initTable.stopCellEditing();
    	// Show Defense Dialog window
    	DefenseDialog defense = new DefenseDialog(actor, this, true);
        defense.setLocation(Integer.valueOf(propertyBag.getProperty("GITApp.defense.location.x")),
        					Integer.valueOf(propertyBag.getProperty("GITApp.defense.location.y")));
        validateOnScreen(defense);
    	defense.setVisible(true); // Modal call
    	// Process and log result!
    	if (defense.valid) {
    		ProcessActorDefense(actor, defense);
    		LogActorDefense(actor, defense);
    		KnockdownStunningCheck(actor, defense);
    	}
    	propertyBag.setProperty("GITApp.defense.location.x", String.valueOf(defense.getLocation().x));
    	propertyBag.setProperty("GITApp.defense.location.y", String.valueOf(defense.getLocation().y));
    }
    
    private void ProcessActorDefense(Actor actor, DefenseDialog defense) {
		// Process shield damage/injury/fatigue
		actor.ShieldDamage += defense.shieldDamage;
		actor.Injury += defense.injury;
		// Fatigue (report through table)
		initTable.setActorValue(actor, columns.Fatigue, actor.Fatigue + defense.fatigue);
		switch (defense.defenseType) { // Record defense attempts
		case Parry:
			++actor.numParry;
			break;
		case Block:
			++actor.numBlock;
			break;
		default:
		}
		initTable.getActorTableModel().fireRefresh(actor);
    }
    
    private void KnockdownStunningCheck(Actor actor, DefenseDialog defense) {
 		if (defense.cripplingInjury || defense.majorWound) {
 			int effHT = actor.HT + defense.location.knockdownPenalty;
 			int roll = DieRoller.roll3d6();
 			int MoS = effHT - roll;
 			String success = (MoS<0)?"<b>failed</b>":"succeeded";
 			addLogLine("<b>" + actor.Name + "</b> Knockdown/Stunning check: rolled " + roll + " against " + effHT + " (" + success + " by " + Math.abs(MoS) + ")");
 		}
    }
    
    private void LogActorDefense(Actor actor, DefenseDialog defense) {
		// Defense description
		String resultType = (defense.defenseResult == DefenseResult.CritSuccess)?"<b><font color=blue>critically</font></b>"
							:(defense.defenseResult == DefenseResult.Success)?"successfully"
							:(defense.defenseResult == DefenseResult.ShieldHit)?"partially"
							:"unsuccessfully";
		
		String defenseDescription = "";
		switch (defense.defenseType) {
		case Parry:
			defenseDescription = resultType + " parried blow.";
			break;
		case Block:
			defenseDescription = resultType + " blocked blow.";
			break;
		case Dodge:
			defenseDescription = resultType + " dodged blow.";
			break;
		case None:
			defenseDescription = "made no defense against blow.";
		}
		
		String damageDescription = "";
		if (defense.injury != 0) {
			damageDescription = " Sustained <b><font color=red>" + defense.injury + "</font></b> injury to the " + defense.location.description;
			String knockdownstunningPenalty = (defense.location.knockdownPenalty != 0)?" @ " + defense.location.knockdownPenalty:"";
			if (defense.cripplingInjury)
				damageDescription += " <b>(crippling" + knockdownstunningPenalty + ")</br>";
			else if (defense.majorWound)
				damageDescription += " <b>(major" + knockdownstunningPenalty + ")</br>";
			damageDescription += ".";
		}
		else if (defense.defenseResult == DefenseResult.ShieldHit || defense.defenseResult == DefenseResult.Failure) 
			damageDescription = " But took no damage.";
   		if (defense.shieldDamage != 0) 
   			damageDescription += " <b>Shield damaged " + defense.shieldDamage + ".</b>";
			
		addLogLine("<b>" + actor.Name + "</b> " + defenseDescription + damageDescription);
     }
    
    public boolean nextActor() {
    	Actor actor = initTable.getActiveActor();
    	if (actor != null && actor.Type == ActorType.Enemy) { // Do AUTO actions
			if (AUTO_UNCONSCIOUS && actor.Injury >= actor.HP
					&& (actor.State == ActorState.Active || actor.State == ActorState.Stunned)) { // What about disabled?
				int penalty = (int) (-1*(Math.floor((double)actor.Injury/actor.HP)-1));
				int result = DieRoller.roll3d6();
				String details = "(HT: " + actor.HT + ", penalty: " + penalty + ", roll: " + result + ")";
				if (result > actor.HT+penalty) {
					addLogLine("<b>" + actor.Name + "</b> <b><font color=red>failed</font></b> consciousness roll " + details);
					initTable.setActorValue(actor, columns.State, ActorState.Unconscious);
				} else {
					addLogLine("<b>" + actor.Name + "</b> passed consciousness roll " + details);
				}
			}
			if (AUTO_ATTACK 
					&& actor.State == ActorState.Active) {
				actorAttack();
			}
    	}
    	
    	if(initTable.nextActor()) {
			++round;
			refreshRoundText();
			addLogLine("<b>** Round " + roundCounter.getText() + " **</b>", false);
			return true;
		}
    	return false;
    }
    
    /**
     * Step through the actors until reaching the next round
     */
    public void nextRound() {
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
        
        // The group Manager
        groupManager = new GroupManager(propertyBag);
        criticalTables = new CriticalTablesDialog(this, false);
        setDefaultProperties();
        
        // The main menu bar
        JMenuBar menubar = new JMenuBar();
        JMenu menuFile = new JMenu("Edit");
        menuFile.setMnemonic(KeyEvent.VK_E);
        undoMenuItem = new JMenuItem("Undo", KeyEvent.VK_U);
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        undoMenuItem.getAccessibleContext().setAccessibleDescription("Undo the last reversible action");
        undoMenuItem.addActionListener(this);
        menuFile.add(undoMenuItem);
        redoMenuItem = new JMenuItem("Redo", KeyEvent.VK_R);
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        redoMenuItem.getAccessibleContext().setAccessibleDescription("Redo the last undone action");
        redoMenuItem.addActionListener(this);
        menuFile.add(redoMenuItem);

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

        menubar.add(menuFile);
        setJMenuBar(menubar);
  
        // The top tool bar
        JToolBar toolbar = new JToolBar("Encounter Control Toolbar");
        //first button
        JButton button = new JButton();
        java.net.URL imageURL = GITApp.class.getResource("/resources/images/control_play_blue.png");
        if (imageURL != null) {
        	button.setIcon(new ImageIcon(imageURL, "Next Actor")); 
        }
        //button.setIcon(new ImageIcon("src/resources/images/control_play_blue.png", "Next Actor"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        //button.setText("Forward");
        button.setToolTipText("Step to next actor (Alt+N)");
        Action action = new AbstractAction("nextActor") {
        	public void actionPerformed(ActionEvent e) { nextActor(); }
        };
        button.setMnemonic(KeyEvent.VK_N);
        button.addActionListener(action);
        toolbar.add(button);
        // Next round 
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/control_fastforward_blue.png"), "Next Round"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Skip to the next round (Ctrl+N)");
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
        button.setToolTipText("Active actor attacks (Ctrl+A)");
        action = new AbstractAction("actorAttack") {
        	public void actionPerformed(ActionEvent e) { actorAttack(); }
        };
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control A"), "actorAttack");
        button.getActionMap().put("actorAttack", action);
        //button.setMnemonic(KeyEvent.VK_A);
        button.addActionListener(action);
        toolbar.add(button);
        // Defend
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/shield.png"), "Defend"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Selected actor defends (Ctrl+D)");
        action = new AbstractAction("actorDefend") {
        	public void actionPerformed(ActionEvent e) { actorDefend(); }
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
        button.setToolTipText("Add enemy tags (Ctrl+T)");
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
        toolbar.add(Box.createHorizontalGlue());
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
        button.setToolTipText("Manage Actor Groups (Alt+G)");
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
        initTable = new InitTable(true);
        initTable.getActorTableModel().addEncounterLogEventListener(this);
        
        // Connect Details Panel to the table/tableModel
        JScrollPane tableScrollPane = new JScrollPane(initTable); 
        JScrollPane logScrollPane = new JScrollPane(logTextArea);
       
        // The actor info pane
        detailsPanel = new ActorDetailsPanel(initTable);
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
        undoManager = new UndoManager();
        refreshUndoRedo();
              
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
			if (DEBUG) { System.out.println("GITApp: loadProperties: Loading properties from file " + propertyFile.getAbsolutePath()); }
			InputStream propIn = new FileInputStream(propertyFile);
			propertyBag.load(propIn);
		} catch (FileNotFoundException e) {
			System.out.println("GITApp: loadProperties: File not found? " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("GITApp: loadProperties: Error reading file! " + e.toString());
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
		 if (!propertyBag.containsKey("GITApp.defense.location.x")) {
			 propertyBag.setProperty("GITApp.defense.location.x", "200"); }
		 if (!propertyBag.containsKey("GITApp.defense.location.y")) {
			 propertyBag.setProperty("GITApp.defense.location.y", "200"); }

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
		 // Optional properties
		// if (saveAsFile != null) { propertyBag.setProperty("GITApp.currentLoadedFile", saveAsFile.getAbsolutePath());}
		 //else { propertyBag.remove("GITApp.currentLoadedFile");}
	 }
	 
	 public void validateOnScreen(Component c) {
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
	 
	 /**
	  * This method is called after each undoable operation
	  * in order to refresh the presentation state of the
	  * undo/redo GUI
	  */
	  public void refreshUndoRedo() {
	     // refresh undo
		 undoMenuItem.setText(undoManager.getUndoPresentationName());
		 undoMenuItem.setEnabled(undoManager.canUndo());

	     // refresh redo
	     redoMenuItem.setText(undoManager.getRedoPresentationName());
	     redoMenuItem.setEnabled(undoManager.canRedo());
	  }

	 /**
	  * Accessor method for undoManager
	  */
	 public UndoManager getUndoManager() {
		 return undoManager;
	 }
	 
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
			// Check to make sure everything is clean
			if(groupManager.querySaveChanges() && saveProperties()) {
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
}
