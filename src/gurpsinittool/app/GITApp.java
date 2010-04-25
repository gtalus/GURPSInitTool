package gurpsinittool.app;

import javax.swing.*;  

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import java.util.Properties;

import gurpsinittool.data.*;
import gurpsinittool.ui.*;
import gurpsinittool.util.FileChangeEventListener;

public class GITApp extends JFrame implements ActionListener {

	// Default SVUID
	private static final long serialVersionUID = 1L;
	
	private static final boolean DEBUG = true;
	
	private InitTable initTable;
	private ActorDetailsPanel detailsPanel;
	private GroupManager groupManager;
	private Properties propertyBag = new Properties();
	private JLabel roundCounter;
	private JSplitPane jSplitPaneHorizontal;
	
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
    	if ("nextActor".equals(e.getActionCommand())) {
    		if(initTable.nextActor()) {
    			Integer currentRound = Integer.valueOf(roundCounter.getText()) + 1;
    			roundCounter.setText(currentRound.toString());
    			int minimumWidth = roundCounter.getMinimumSize().width/10 * 10;
    			if (roundCounter.getMinimumSize().width + 1  % 10 != 0) { minimumWidth += 10; }
    			roundCounter.setPreferredSize(new Dimension(minimumWidth, 20));
    			if (DEBUG) { System.out.println("Minimum round cunter size = " + roundCounter.getMinimumSize().toString()); }
    		}
    	}	
    	else if ("resetRound".equals(e.getActionCommand())) {
    		roundCounter.setText("0");
    		initTable.resetEncounter();
    	}	
    	else if ("openGroupManager".equals(e.getActionCommand())) {
    		groupManager.setVisible(true);
    	}
       	else if ("sizeColumns".equals(e.getActionCommand())) {
    		initTable.autoSizeColumns();
    	}	

	}
    
    private void addComponentsToPane() {
        //contentPanel.setOpaque(true);
        //frame.setContentPane(contentPanel);
        
        // The group Manager
        groupManager = new GroupManager(propertyBag);
        setDefaultProperties();
        
        // The main menu bar
        JMenuBar menubar = new JMenuBar();
        JMenu menuFile = new JMenu("Test");
        menuFile.add("test item1");
        menuFile.add("test item2");
        menuFile.add("test item3");
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
        button.setToolTipText("Step to next actor");
        button.setActionCommand("nextActor");
        button.setMnemonic(KeyEvent.VK_N);
        button.addActionListener(this);
        toolbar.add(button);
        // Round counter labels
        JLabel label = new JLabel("Round:");
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 5));
        toolbar.add(label);
        label = new JLabel("0");
        //label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        //label.setMinimumSize(new java.awt.Dimension(20, 20));
        //label.setMaximumSize(new java.awt.Dimension(20, 20));
        label.setPreferredSize(new java.awt.Dimension(20, 20));
        roundCounter = label; 
        toolbar.add(label);
        // Reset round counter buffer
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/control_start_blue.png"), "Reset Encounter"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Reset the round counter");
        button.setActionCommand("resetRound");
        button.setMnemonic(KeyEvent.VK_R);
        button.addActionListener(this);
        toolbar.add(button);
        // Auto-resize table columns
        toolbar.addSeparator();
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/script_code.png"), "Auto-size columns"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Auto re-size the table columns to best fit");
        button.setActionCommand("sizeColumns");
        button.setMnemonic(KeyEvent.VK_A);
        button.addActionListener(this);
        toolbar.add(button);
        //Group manager button & horizontal glue
        toolbar.addSeparator();
        toolbar.add(Box.createHorizontalGlue());
        button = new JButton();
        button.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/group.png"), "Group Manager"));
        button.setToolTipText("Manage Actor Groups");
        button.setActionCommand("openGroupManager");
        button.setMnemonic(KeyEvent.VK_G);
        button.addActionListener(this);
        toolbar.add(button);
        toolbar.setRollover(true);
        getContentPane().add(toolbar, BorderLayout.PAGE_START);
 
        // The actor table
        //InitTable initTable = new InitTable(new ActorTableModel());
        initTable = new InitTable(true);
        // Connect Details Panel to the table/tableModel
        //mainApp.initTable.getSelectionModel().addListSelectionListener(mainApp.detailsPanel);
        JScrollPane tableScrollPane = new JScrollPane(initTable); 

        // The actor info pane
        detailsPanel = new ActorDetailsPanel(initTable);
        JScrollPane actorDetailsPane = new JScrollPane(detailsPanel);
         
        jSplitPaneHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, actorDetailsPane);
        jSplitPaneHorizontal.setDividerLocation(Integer.valueOf(propertyBag.getProperty("GITApp.splitHorizontal.dividerLocation")));
        jSplitPaneHorizontal.setResizeWeight(.95);
        getContentPane().add(jSplitPaneHorizontal, BorderLayout.CENTER);
       
        //Display the window.
        setLocation(Integer.valueOf(propertyBag.getProperty("GITApp.location.x")),
                Integer.valueOf(propertyBag.getProperty("GITApp.location.y")));
        setSize(Integer.valueOf(propertyBag.getProperty("GITApp.size.width")),
        		Integer.valueOf(propertyBag.getProperty("GITApp.size.height")));
        
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
		 //if (!propertyBag.containsKey("GITApp.splitVertical.dividerLocation")) {
		//	 propertyBag.setProperty("GITApp.splitVertical.dividerLocation", "200"); }

		 if (!propertyBag.containsKey("GITApp.location.x")) {
			 propertyBag.setProperty("GITApp.location.x", "400"); }
		 if (!propertyBag.containsKey("GITApp.location.y")) {
			 propertyBag.setProperty("GITApp.location.y", "400"); }
		 if (!propertyBag.containsKey("GITApp.size.width")) {
			 propertyBag.setProperty("GITApp.size.width", "760"); }
		 if (!propertyBag.containsKey("GITApp.size.height")) {
			 propertyBag.setProperty("GITApp.size.height", "480"); }

	 }
	 
	 /**
	  * Update all the store-able properties to their current values
	  */
	 public void updateProperties() {
		 // Kept up-to-date with event listeners
		 propertyBag.setProperty("GITApp.Manager.visible", String.valueOf(groupManager.isVisible()));
		 propertyBag.setProperty("GITApp.splitHorizontal.dividerLocation", String.valueOf(jSplitPaneHorizontal.getDividerLocation()));
		 //propertyBag.setProperty("GITApp.splitVertical.dividerLocation", String.valueOf(jSplitPaneVertical.getDividerLocation()));
		 propertyBag.setProperty("GITApp.location.x", String.valueOf(getLocation().x));
		 propertyBag.setProperty("GITApp.location.y", String.valueOf(getLocation().y));
		 propertyBag.setProperty("GITApp.size.width", String.valueOf(getSize().width));
		 propertyBag.setProperty("GITApp.size.height", String.valueOf(getSize().height));
		 // Optional properties
		// if (saveAsFile != null) { propertyBag.setProperty("GITApp.currentLoadedFile", saveAsFile.getAbsolutePath());}
		 //else { propertyBag.remove("GITApp.currentLoadedFile");}
	 }
	 
    /**
     * An Inner class to monitor the window events
     */
    class GITAppWindowListener implements WindowListener {

		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

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
		public void windowDeactivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}
    }
}
