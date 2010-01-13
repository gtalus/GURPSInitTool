package gurpsinittool.app;

import javax.swing.*;  

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Properties;

import gurpsinittool.data.*;
import gurpsinittool.ui.*;

public class GITApp // extends JPanel
	implements ActionListener {

	private static final boolean DEBUG = true;
	private InitTable initTable;
	private ActorDetailsPanel detailsPanel;
	private GroupManager groupManager;
	private Properties propertyBag;
	private JLabel roundCounter;
	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
         //Create and set up the window.
        JFrame frame = new JFrame("GURPS Initiative Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GITApp mainApp = new GITApp();
        //contentPanel.setOpaque(true);
        //frame.setContentPane(contentPanel);
        
        // The group Manager
        Properties propertyBag = new Properties();
        mainApp.propertyBag = propertyBag;
        mainApp.groupManager = new GroupManager(propertyBag);
        
        // The main menu bar
        JMenuBar menubar = new JMenuBar();
        JMenu menuFile = new JMenu("Test");
        menuFile.add("test item1");
        menuFile.add("test item2");
        menuFile.add("test item3");
        menubar.add(menuFile);
        frame.setJMenuBar(menubar);
  
        // The top tool bar
        JToolBar toolbar = new JToolBar("Encounter Control Toolbar");
        //first button
        JButton button = new JButton();
        button.setIcon(new ImageIcon("src/resources/images/control_play_blue.png", "Next Actor"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        //button.setText("Forward");
        button.setToolTipText("Step to next actor");
        button.setActionCommand("nextActor");
        button.setMnemonic(KeyEvent.VK_N);
        button.addActionListener(mainApp);
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
        mainApp.roundCounter = label; 
        toolbar.add(label);
        // Reset round counter buffer
        button = new JButton();
        button.setIcon(new ImageIcon("src/resources/images/control_start_blue.png", "Reset Encounter"));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
        button.setToolTipText("Resen the round counter");
        button.setActionCommand("resetRound");
        button.setMnemonic(KeyEvent.VK_R);
        button.addActionListener(mainApp);
        toolbar.add(button);
        //Group manager button & horizontal glue
        toolbar.add(Box.createHorizontalGlue());
        button = new JButton();
        button.setIcon(new ImageIcon("src/resources/images/group.png", "Group Manager"));
        button.setToolTipText("Manage Actor Groups");
        button.setActionCommand("openGroupManager");
        button.setMnemonic(KeyEvent.VK_N);
        button.addActionListener(mainApp);
        toolbar.add(button);
        toolbar.setRollover(true);
        frame.getContentPane().add(toolbar, BorderLayout.PAGE_START);
 
        // The actor table
        //InitTable initTable = new InitTable(new ActorTableModel());
        mainApp.initTable = new InitTable(true);
        // Connect Details Panel to the table/tableModel
        //mainApp.initTable.getSelectionModel().addListSelectionListener(mainApp.detailsPanel);
        JScrollPane tableScrollPane = new JScrollPane(mainApp.initTable); 

        // The actor info pane
        mainApp.detailsPanel = new ActorDetailsPanel(mainApp.initTable);
        JScrollPane actorDetailsPane = new JScrollPane(mainApp.detailsPanel);
         
        JSplitPane over_frame = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, actorDetailsPane);
        over_frame.setDividerLocation(460);
        over_frame.setResizeWeight(.95);
        //over_frame.setSize(300, 300);
        frame.getContentPane().add(over_frame, BorderLayout.CENTER);
       
        //Display the window.
        frame.setLocation(400,400);
        frame.setSize(760,480);
        //frame.setSize(200,200);
        //frame.pack();
        frame.setVisible(true);
        //mainApp.groupManager.setVisible(true);
    }

    public static void main(String[] args) {
    	try {
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
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
    	if ("resetRound".equals(e.getActionCommand())) {
    		roundCounter.setText("0");
    		initTable.resetEncounter();
    	}	
    	else if ("openGroupManager".equals(e.getActionCommand())) {
    		groupManager.setVisible(true);
    	}	
	}
}
