package gurpsinittool.app;

import javax.swing.*;  
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import gurpsinittool.data.*;


public class GITApp // extends JPanel
	implements ActionListener {

	public ActorTableModel actorTableModel;
	
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
        JToolBar toolbar = new JToolBar("The one and only tool bar");
        //first button
        JButton button = new JButton();
        button.setIcon(new ImageIcon("src/resources/images/control_play_blue.png", "Next Actor"));
      // button.setText("Forward");
        button.setToolTipText("Step to next actor");
        button.setActionCommand("nextActor");
        button.setMnemonic(KeyEvent.VK_N);
        button.addActionListener(mainApp);
        toolbar.add(button);
        toolbar.setRollover(true);
        frame.getContentPane().add(toolbar, BorderLayout.PAGE_START);

        //Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Hello World");
        JScrollPane actorDetailsPane = new JScrollPane(label);
        
        //InitTable initTable = new InitTable(new ActorTableModel());
        JTable initTable = new JTable(new ActorTableModel());
        mainApp.actorTableModel = (ActorTableModel) initTable.getModel();
        initTable.setDefaultRenderer(new Object().getClass(), new InitTableCellRenderer());
        initTable.setDefaultRenderer(new Integer(0).getClass(), new InitTableCellRenderer());
        initTable.setTransferHandler(new initTableTransferHandler("name"));
        initTable.setPreferredScrollableViewportSize(new Dimension(800, 270));
        initTable.setFillsViewportHeight(true);
        //initTable.setRowSelectionAllowed(true);
        initTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        initTable.setDragEnabled(true);
        initTable.setDropMode(DropMode.INSERT_ROWS);

        JComboBox initTableStateEditor = new JComboBox();
        initTableStateEditor.addItem("Active");
        initTableStateEditor.addItem("Waiting");
        initTableStateEditor.addItem("Disabled");
        initTableStateEditor.addItem("Unconscious");
        initTableStateEditor.addItem("Dead");
        initTable.getColumnModel().getColumn(3).setCellEditor(new InitTableComboCellEditor(initTableStateEditor));
        ((DefaultCellEditor) initTable.getColumnModel().getColumn(3).getCellEditor()).setClickCountToStart(2);
        JComboBox initTableTypeEditor = new JComboBox();
        initTableTypeEditor.addItem("PC");
        initTableTypeEditor.addItem("Ally");
        initTableTypeEditor.addItem("Enemy");
        initTableTypeEditor.addItem("Neutral");
        initTableTypeEditor.addItem("Special");
        initTable.getColumnModel().getColumn(4).setCellEditor(new InitTableComboCellEditor(initTableTypeEditor));
        ((DefaultCellEditor) initTable.getColumnModel().getColumn(4).getCellEditor()).setClickCountToStart(2);
         JScrollPane tableScrollPane = new JScrollPane(initTable);

        JSplitPane over_frame = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, actorDetailsPane);
        over_frame.setDividerLocation(600);
        over_frame.setResizeWeight(0.7);
        frame.getContentPane().add(over_frame, BorderLayout.CENTER);
       
        //Display the window.
        frame.setLocation(400,400);
        //frame.setSize(200,200);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
    		actorTableModel.nextActor();
    	}
    }

    
}
