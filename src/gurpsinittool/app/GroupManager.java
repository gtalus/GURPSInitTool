package gurpsinittool.app;

import gurpsinittool.ui.ActorDetailsPanel;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

public class GroupManager extends JFrame {
	
	private JSplitPane jSplitPaneVertical;
	private JSplitPane jSplitPaneHorizontal;
	private JScrollPane jScrollPaneTable;
	private JScrollPane jScrollPaneTree;
	private JScrollPane jScrollPaneDetails;
	private InitTable jTable;
	private ActorDetailsPanel actorDetailsPanel;
	private JMenuBar jMenuBar;
	private JMenu jMenu;
	private GroupTree groupTree;
	
	public GroupManager() {
		super("Group Manager");
        //Create and set up the window.	
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
 
        // The menu bar
        jMenu = new JMenu("File");
        jMenu.add("New");
        jMenu.add("Open");
        jMenu.add("Save");
        jMenuBar = new JMenuBar();
        jMenuBar.add(jMenu);
        setJMenuBar(jMenuBar);
        
        jTable = new InitTable();
        groupTree = new GroupTree(jTable);
        actorDetailsPanel = new ActorDetailsPanel(jTable);
        jScrollPaneTable = new JScrollPane(jTable);
        jScrollPaneDetails = new JScrollPane(actorDetailsPanel);
        jScrollPaneTree = new JScrollPane(groupTree);
        jSplitPaneVertical= new JSplitPane(JSplitPane.VERTICAL_SPLIT, jScrollPaneTree, jScrollPaneTable);
        jSplitPaneHorizontal= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jSplitPaneVertical, jScrollPaneDetails);
        
        jSplitPaneVertical.setDividerLocation(100);
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
        
        //pack();
	}

}
