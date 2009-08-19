package gurpsinittool.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import gurpsinittool.data.ActorGroupFile;
import gurpsinittool.ui.ActorDetailsPanel;

import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultTreeModel;

public class GroupManager extends JFrame 
	implements TreeSelectionListener, ActionListener {
	
	private static final boolean DEBUG = true;

	private JSplitPane jSplitPaneVertical;
	private JSplitPane jSplitPaneHorizontal;
	private JScrollPane jScrollPaneTable;
	private JScrollPane jScrollPaneTree;
	private JScrollPane jScrollPaneDetails;
	private InitTable groupTable;
	private ActorDetailsPanel actorDetailsPanel;
	private JMenuBar jMenuBar;
	private JMenu jMenu;
	private GroupTree groupTree;
	
	private File saveAsFile;
	
	public GroupManager() {
		super("Group Manager");
        //Create and set up the window.	
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
 
        // The menu bar
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
        jMenuBar = new JMenuBar();
        jMenuBar.add(jMenu);
        setJMenuBar(jMenuBar);
        
        groupTable = new InitTable(false);
        groupTable.setVisible(false);
        actorDetailsPanel = new ActorDetailsPanel(groupTable);
        groupTree = new GroupTree(groupTable, actorDetailsPanel);
        groupTree.addTreeSelectionListener(this);
        jScrollPaneTable = new JScrollPane(groupTable);
        jScrollPaneDetails = new JScrollPane(actorDetailsPanel);
        jScrollPaneTree = new JScrollPane(groupTree);
        jSplitPaneVertical= new JSplitPane(JSplitPane.VERTICAL_SPLIT, jScrollPaneTree, jScrollPaneTable);
        jSplitPaneHorizontal= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jSplitPaneVertical, jScrollPaneDetails);
        
        jSplitPaneVertical.setDividerLocation(200);
        jSplitPaneVertical.setContinuousLayout(true);
        jSplitPaneVertical.setResizeWeight(.95);
 
        jSplitPaneHorizontal.setDividerLocation(340);
        jSplitPaneHorizontal.setContinuousLayout(true);
        jSplitPaneHorizontal.setResizeWeight(.95);
        
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
        		layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        		.addComponent(jSplitPaneHorizontal, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addComponent(jSplitPaneHorizontal, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
        
        setLocation(500,300);
        setSize(620,450);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
    	if (DEBUG) { System.out.println("GroupManager: Received action command " + e.getActionCommand()); }
    	if ("Save As...".equals(e.getActionCommand()) || ("Save".equals(e.getActionCommand()) && saveAsFile == null)) { // Pick file & save group list
     		JFileChooser chooser = new JFileChooser();
    		chooser.setCurrentDirectory(new File("."));
    		chooser.addChoosableFileFilter(new GroupFilter());
    		int retVal = chooser.showSaveDialog(this);
    		if (retVal == JFileChooser.APPROVE_OPTION) {
        		saveAsFile = chooser.getSelectedFile();
        		if (!saveAsFile.toString().contains(".")) { saveAsFile = new File(saveAsFile.toString() + ".igroup"); }
            	if (DEBUG) { System.out.println("GroupManager: Saving group list as file: " + saveAsFile.getName()); }
        		ActorGroupFile.SaveActorGroup(groupTree, saveAsFile);
    		}
    	}
    	else if ("Save".equals(e.getActionCommand()) && saveAsFile != null) { // Save group list
         	if (DEBUG) { System.out.println("GroupManager: Saving group list as file: " + saveAsFile.getName()); }
    		ActorGroupFile.SaveActorGroup(groupTree, saveAsFile);
    	}
       	else if ("Open".equals(e.getActionCommand())) { // Save group list
     		JFileChooser chooser = new JFileChooser();
    		chooser.setCurrentDirectory(new File("."));
    		chooser.addChoosableFileFilter(new GroupFilter());
    		int retVal = chooser.showOpenDialog(this);
    		if (retVal == JFileChooser.APPROVE_OPTION) {
        		File openFile = chooser.getSelectedFile();
            	if (DEBUG) { System.out.println("GroupManager: Opening file: " + openFile.getName()); }
        		ActorGroupFile.OpenActorGroup(groupTree, openFile);
        		saveAsFile = openFile;
    		}
    	}
    	else if ("New".equals(e.getActionCommand())) { // Create new group list
    		// Need to add warning about loosing current data
         	if (DEBUG) { System.out.println("GroupManager: Creating new group list"); }
        	groupTree.setModel(new DefaultTreeModel(new GroupTreeNode("Groups",true)));
        	saveAsFile = null;
    	}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (DEBUG) { System.out.println("GroupManager: Event: " + e.toString()); }
		ActorTableModel tableModel = (ActorTableModel) groupTable.getModel();
		if (groupTree.getLastSelectedPathComponent() != null) {
			if (DEBUG) { System.out.println("GroupManager: Current Selection: " + groupTree.getLastSelectedPathComponent().toString()); }
			GroupTreeNode node = (GroupTreeNode) groupTree.getLastSelectedPathComponent();
			if (!node.isFolder()) {
				tableModel.setActorList(node.getActorList());
				groupTable.setVisible(true);
			}
			else {
				groupTable.setVisible(false);
				tableModel.setActorList(null);
			}
		}
		else {
			if (DEBUG) { System.out.println("GroupManager: Current Selection: null"); }
			groupTable.setVisible(false);
			tableModel.setActorList(null);
		}
	}

	private class GroupFilter extends FileFilter {
		@Override
		public boolean accept(File f) { 
			return (f.isDirectory() || f.toString().endsWith(".igroup"));
		}
		@Override
		public String getDescription() {
			return "InitTool Group (*.igroup)";
		}
	}
}
