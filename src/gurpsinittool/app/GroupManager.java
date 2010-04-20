package gurpsinittool.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Properties;

import gurpsinittool.data.ActorGroupFile;
import gurpsinittool.ui.ActorDetailsPanel;
import gurpsinittool.util.FileChangeEvent;
import gurpsinittool.util.FileChangeEventListener;

import javax.swing.GroupLayout;
import javax.swing.JCheckBoxMenuItem;
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
	implements TreeSelectionListener, ActionListener, ItemListener {

	// Default SVUID
	private static final long serialVersionUID = 1L;

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
	
	private JFileChooser fileChooser;
	private File saveAsFile;
	private boolean tableIsClean = true;
	private boolean treeIsClean = true;
	private Properties propertyBag;
	
	public GroupManager(Properties propertyBag) {
		super("Group Manager");
		this.propertyBag = propertyBag;
		
        //Create and set up the window.	
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        // The file chooser
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.addChoosableFileFilter(new GroupFilter());
 
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
        jMenuBar.add(jMenu);
        jMenu = new JMenu("View");
        jMenu.setMnemonic(KeyEvent.VK_V);
        menuItem = new JCheckBoxMenuItem("Actor Details");
        //((JCheckBoxMenuItem) menuItem).setSelected(true);
        menuItem.setSelected(true);
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.addItemListener(this);
        jMenu.add(menuItem);
        jMenuBar.add(jMenu);
        setJMenuBar(jMenuBar);
        
        groupTable = new InitTable(false);
        groupTable.setVisible(false);
        groupTable.getActorTableModel().addFileChangeEventListener(new GroupFileChangeEventListener());
        actorDetailsPanel = new ActorDetailsPanel(groupTable);
        groupTree = new GroupTree(groupTable);
        groupTree.addTreeSelectionListener(this);
        groupTree.addFileChangeEventListener(new GroupFileChangeEventListener());
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
    	if (DEBUG) { System.out.println("GroupManager: actionPerformed: Received action command " + e.getActionCommand()); }
    	if ("Save As...".equals(e.getActionCommand())) {
    		saveGroupFile(null);
    	}
    	else if ("Save".equals(e.getActionCommand())) { // Save group list
         	saveGroupFile(saveAsFile);
    	}
       	else if ("Open".equals(e.getActionCommand()) && querySaveChanges()) { // Save group list
    		int retVal = fileChooser.showOpenDialog(this);
    		if (retVal == JFileChooser.APPROVE_OPTION) {
        		File openFile = fileChooser.getSelectedFile();
            	if (DEBUG) { System.out.println("GroupManager: actionPerformed: Opening file: " + openFile.getName()); }
        		ActorGroupFile.OpenActorGroup(groupTree, openFile);
        		saveAsFile = openFile;
        		groupTree.setClean();
        		groupTable.getActorTableModel().setClean();
        		super.setTitle("Group Manager - " + saveAsFile.getName());
    		}
    	}
    	else if ("New".equals(e.getActionCommand()) && querySaveChanges()) { // Create new group list
          	if (DEBUG) { System.out.println("GroupManager: Creating new group list"); }
        	groupTree.setModel(new DefaultTreeModel(new GroupTreeNode("Groups",true)));
        	saveAsFile = null;
        	groupTree.setClean();
    		groupTable.getActorTableModel().setClean();
        	super.setTitle("Group Manager");
    	}

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
    	if (DEBUG) { System.out.println("GroupManager: itemStateChanged: Received item state changed " + e.toString()); }
    	JMenuItem source = (JMenuItem) e.getSource();
    	if ("Actor Details".equals(source.getText())) { // Show/hide the actor details panel
         	boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
         	if (DEBUG) { System.out.println("GroupManager: itemStateChanged: View/Actor Details item state changed. Selected = " + selected); }
         	if (selected) {
                getContentPane().remove(jSplitPaneVertical);
                jSplitPaneHorizontal.setLeftComponent(jSplitPaneVertical);
                jSplitPaneHorizontal.setDividerLocation(Integer.valueOf(propertyBag.getProperty("Manager.splitHorizontal.dividerLocation")));

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
         	}
         	else {
         		propertyBag.setProperty("Manager.splitHorizontal.dividerLocation", String.valueOf(jSplitPaneHorizontal.getDividerLocation()));
            	getContentPane().remove(jSplitPaneHorizontal);
                GroupLayout layout = new GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                		layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                		.addComponent(jSplitPaneVertical, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                );
                layout.setVerticalGroup(
                	layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                	.addComponent(jSplitPaneVertical, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                );
         	}
    	}
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (DEBUG) { System.out.println("GroupManager: valueChanged: TreeSelectionEvent: " + e.toString()); }
		ActorTableModel tableModel = groupTable.getActorTableModel();
		if (groupTree.getLastSelectedPathComponent() != null) {
			if (DEBUG) { System.out.println("GroupManager: valueChanged: Current Selection: " + groupTree.getLastSelectedPathComponent().toString()); }
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
			if (DEBUG) { System.out.println("GroupManager: valueChanged - Current Selection: null"); }
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
   			int n = JOptionPane.showOptionDialog(this, "The group has been modified. Save Changes?", "Group Changed", 
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
	 * Save the group list to a file.
	 * @param file - the file to save as. Save As dialog used if file is null.
	 * @return whether the save was completed successfully.
	 */
	 public boolean saveGroupFile(File file) {
	    	if (file == null) { // Pick file
	    		int retVal = fileChooser.showSaveDialog(this);
            	if (DEBUG) { System.out.println("GroupManager: saveGroupFile: Displaying file chooser"); }
	    		if (retVal == JFileChooser.APPROVE_OPTION) {
	    			file = fileChooser.getSelectedFile();
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
         	if (DEBUG) { System.out.println("GroupManager: saveGroupFile: Saving group list as file: " + file.getName()); }
    		ActorGroupFile.SaveActorGroup(groupTree, file);
    		groupTree.setClean();
    		groupTable.getActorTableModel().setClean();
    		super.setTitle("Group Manager - " + file.getName());
    		saveAsFile = file;
    		
    		return true;
	 }
	    	
	 /**
     * An Inner class to monitor the file changes
     */
    class GroupFileChangeEventListener implements FileChangeEventListener {

		@Override
		public void fileChangeOccured(FileChangeEvent evt) {
			if (DEBUG) { System.out.println("GroupManager: FileChangeOccured."); }
		}
	
		@Override
		public void fileCleanStatusChanged(FileChangeEvent evt) {
			Object source = evt.getSource();
			if (source instanceof ActorTableModel) {
				if (DEBUG) { System.out.println("GroupManager: FileCleanStatusChanged: ActorTableModel " + evt.isClean); }
				tableIsClean = evt.isClean;
			}
			else if (source instanceof GroupTree) {
				if (DEBUG) { System.out.println("GroupManager: FileCleanStatusChanged: GroupTree " + evt.isClean); }
				treeIsClean = evt.isClean;
			}
			else {
				if (DEBUG) { System.out.println("GroupManager: FileCleanStatusChanged: UNKNOWN " + evt.isClean); }
			}
	
			if (tableIsClean && treeIsClean) {
				if (saveAsFile != null) {
					setTitle("Group Manager - " + saveAsFile.getName());
				}
				else {
					setTitle("Group Manager");
				}
			}
			else {
				if (saveAsFile != null) {
					setTitle("Group Manager - " + saveAsFile.getName() + "*");
				}
				else {
					setTitle("Group Manager *");
				}
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
			return "InitTool Group (*.igroup)";
		}
	}


}
