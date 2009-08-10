package gurpsinittool.app;

import javax.swing.*;  
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import gurpsinittool.data.*;
import gurpsinittool.ui.*;

public class GITApp // extends JPanel
	implements ActionListener {

	private InitTable initTable;
	private ActorDetailsPanel detailsPanel;
	
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
      // button.setText("Forward");
        button.setToolTipText("Step to next actor");
        button.setActionCommand("nextActor");
        button.setMnemonic(KeyEvent.VK_N);
        button.addActionListener(mainApp);
        toolbar.add(button);
        //first button
        toolbar.add(Box.createHorizontalGlue());
        JButton button2 = new JButton();
        button2.setIcon(new ImageIcon("src/resources/images/control_play_blue.png", "Next Actor"));
      // button.setText("Forward");
        button2.setToolTipText("Step to next actor");
        button2.setActionCommand("nextActor");
        button2.setMnemonic(KeyEvent.VK_N);
        button2.addActionListener(mainApp);
        //button2.setAlignmentY(1000);
        //button2.setLocation(500,500);
        toolbar.add(button2);
        toolbar.setRollover(true);
        frame.getContentPane().add(toolbar, BorderLayout.PAGE_START);
 
        // The actor table
        //InitTable initTable = new InitTable(new ActorTableModel());
        mainApp.initTable = new InitTable();
        // Connect Details Panel to the table/tableModel
        //mainApp.initTable.getSelectionModel().addListSelectionListener(mainApp.detailsPanel);
        JScrollPane tableScrollPane = new JScrollPane(mainApp.initTable); 

        // The actor info pane
        mainApp.detailsPanel = new ActorDetailsPanel(mainApp.initTable);
        JScrollPane actorDetailsPane = new JScrollPane(mainApp.detailsPanel);
         
        JSplitPane over_frame = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, actorDetailsPane);
        over_frame.setDividerLocation(480);
        over_frame.setResizeWeight(.95);
        //over_frame.setSize(300, 300);
        frame.getContentPane().add(over_frame, BorderLayout.CENTER);
       
        //Display the window.
        frame.setLocation(400,400);
        frame.setSize(760,420);
        //frame.setSize(200,200);
        //frame.pack();
        frame.setVisible(true);
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
    		initTable.nextActor();
    	}	
	}
}
