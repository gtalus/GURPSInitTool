package gurpsinittool.app;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import gurpsinittool.app.InitTableModel.columns;
import gurpsinittool.data.*;
//import gurpsinittool.test.RandomData;
import gurpsinittool.data.Actor.ActorStatus;
import gurpsinittool.data.Actor.ActorType;
import gurpsinittool.data.Actor.BasicTrait;
import gurpsinittool.ui.DefenseDialog;

public class InitTable extends JTable 
	implements ActionListener {
	
	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = false;

	private JPopupMenu popupMenu;
	private Map<ActorStatus, JMenuItem> coordinatedStatusMenuItems;
	private InitTableModel tableModel;
	private boolean isInitTable;
	private Properties propertyBag;
	
	/**
	 * Default Constructor
	 */
	public InitTable(Properties propertyBag, boolean isInitTable) {
		super(new InitTableModel());
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		this.propertyBag = propertyBag;
		this.isInitTable = isInitTable;
		tableModel = (InitTableModel) dataModel;
		initialize();
	    
//		if (isInitTable) {
//			RandomData.RandomActors(tableModel);
//		}
	}

    public void actionPerformed(ActionEvent e) {
    	if (DEBUG) { System.out.println("InitTable: Received action command " + e.getActionCommand()); }
    	if ("Delete".equals(e.getActionCommand())) { // Delete selected rows
    		stopCellEditing();
    		int[] rows = getSelectedRows();
    		int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete these rows?", "Confirm Row Delete", JOptionPane.OK_CANCEL_OPTION);
    		if (result == JOptionPane.OK_OPTION) {
    			for (int i = rows.length-1; i >= 0; i--) {  // Go from bottom up to preserve numbering
    				if (DEBUG) { System.out.println("InitTable: Deleting row: " + rows[i]); }   			
    				tableModel.removeActor(rows[i]); // Just remove the rows indicated: not all instances of clones
    			}
    		}
    	}
    	else if ("Reset".equals(e.getActionCommand())) { // Delete selected rows
    		stopCellEditing();
      		int[] rows = getSelectedRows();
       		if (DEBUG) { System.out.println("InitTable: Resetting actor. Row: " + rows[0] + ", Actor: " + tableModel.getActor(rows[0]).getTraitValue(BasicTrait.Name)); }   	
       		for (int i = 0; i < rows.length; i++) {
       			Actor actor = tableModel.getActor(rows[i]);
       			actor.Reset();
       		}
    	}
    	else if ("Set Active".equals(e.getActionCommand())) { // Clone selected rows at the end (as "Haste" spell)
      		int[] rows = getSelectedRows();
      		if (DEBUG) { System.out.println("InitTable: Setting active actor. Row: " + rows[0] + ", Actor: " + tableModel.getActor(rows[0]).getTraitValue(BasicTrait.Name)); }   	
      		tableModel.setActiveRow(rows[0]); // sets actor as current active, and sets state to active
//       		for (int i = 0; i < rows.length; i++) {
//       			tableModel.setValueAt("Active", rows[i], InitTableModel.columns.State.ordinal());
//       		}
    	}
    	else if ("Tag".equals(e.getActionCommand())) { // Add tag to selected rows
      		int[] rows = getSelectedRows();
      		if (DEBUG) { System.out.println("InitTable: Tagging actor. Row: " + rows[0] + ", Actor: " + tableModel.getActor(rows[0]).getTraitValue(BasicTrait.Name)); }   	
       		for (int i = 0; i < rows.length; i++) {
       			tableModel.tagActor(tableModel.getActor(rows[i]));
       		}
    	}
    	else if ("Remove Tag".equals(e.getActionCommand())) { // Remove tag from selected rows
      		int[] rows = getSelectedRows();
      		if (DEBUG) { System.out.println("InitTable: Un-tagging actor. Row: " + rows[0] + ", Actor: " + tableModel.getActor(rows[0]).getTraitValue(BasicTrait.Name)); }   	
       		for (int i = 0; i < rows.length; i++) {
       			tableModel.removeTag(tableModel.getActor(rows[i]));
       		}
    	}
    	else if ("Attack".equals(e.getActionCommand())) {
    		selectedActorsAttack();
    	}
    	else if ("Defend".equals(e.getActionCommand())) {
    		selectedActorDefend();
    	}
//     	if ("Clone".equals(e.getActionCommand())) { // Clone selected rows at the end (as "Haste" spell)
//     		Actor[] actors = tableModel.getActors(getSelectedRows());
//    		for (int i = 0; i < actors.length; i++) {
//    		   	if (DEBUG) { System.out.println("InitTable: Cloning actor: " + actors[i].Name); }   			
//    			tableModel.addActor(actors[i], tableModel.getRowCount()-1); // Add actor to the end of the table
//    		}
//    	}
    	else {
    		// Check actor states and types
//            for (Actor.ActorState s : Actor.ActorState.values()) {
//            	if (s.toString().equals(e.getActionCommand())) {
//            		stopCellEditing();
//              		int[] rows = getSelectedRows();
//               		for (int i = 0; i < rows.length; i++) {
//               			tableModel.setValueAt(s.toString(), rows[i], InitTableModel.columns.State.ordinal());
//               		}
//            	}
//            }
            for (Actor.ActorType t : Actor.ActorType.values()) {
            	if (t.toString().equals(e.getActionCommand())) {
            		stopCellEditing();
            		int[] rows = getSelectedRows();
               		for (int i = 0; i < rows.length; i++) {
               			tableModel.setValueAt(t.toString(), rows[i], InitTableModel.columns.Type.ordinal());
               		}
            	}
            }
    	}
    }
    
    /**
     * Auto re-size the column widths to optimally fit information
     */
    public void autoSizeColumns() {
    	if (DEBUG) { System.out.println("autoSizeColumns: starting."); }
    	//this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    	TableColumn column = null;
    	for (int i = 0; i < this.getColumnCount(); i++) {
    	    column = this.getColumnModel().getColumn(i);
    	    
    	    // Get width of column header 
    	    TableCellRenderer renderer = column.getHeaderRenderer(); 
    	    if (renderer == null) 
    	    { renderer = this.getTableHeader().getDefaultRenderer(); } 
    	    Component comp = renderer.getTableCellRendererComponent( this, column.getHeaderValue(), false, false, 0, 0); 
    	    int width = comp.getPreferredSize().width; 
     	    
    	    // Check width of all the cells
    	    // Get maximum width of column data
    	    for (int j=0; j < getRowCount(); j++) {
    	        renderer = getCellRenderer(j, i);
    	        comp = renderer.getTableCellRendererComponent(this, getValueAt(j, i), false, false, j, i);
    	        width = Math.max(width, comp.getPreferredSize().width);
    	    }
    	    
       	    column.setPreferredWidth(width);
       	    if (isInitTable && i == 0) { // Set the 'Act' column size
       	    	column.setMaxWidth(width);
       	    	column.setMinWidth(width);
       	    }
    	}
    	//this.resizeAndRepaint();
    }
    
    /**
     * Convenience method to create menu items for the table's menus.
     * @param text - Text of the menu item
     * @return
     */
    private JMenuItem createMenuItem(String text, int mnemonic) {
    	JMenuItem menuItem = new JMenuItem(text, mnemonic);
    	menuItem.addActionListener(this);
    	return menuItem;
    }
    
    /**
     * Convenience method to create menu items for the table's menus.
     * @param text - Text of the menu item
     * @return
     */
    private JMenuItem createCoordinatedStatusMenuItem(final ActorStatus status, int mnemonic) {
    	JMenuItem menuItem = new JMenuItem(status.toString(), mnemonic);
    	menuItem.setAction(new AbstractAction(status.toString()) {
    		public void actionPerformed(ActionEvent ae) {
    			coordinatedChangeStatusOfSelectedActors(status);
    		}   		
    	});
    	coordinatedStatusMenuItems.put(status, menuItem);
    	return menuItem;
    }
    
    /**
     * Convenience method to create menu items for the table's menus.
     * @param text - Text of the menu item
     * @return
     */
    private void updateCoordinatedStatusMenuItems() {
    	for (Map.Entry<ActorStatus, JMenuItem> entry : coordinatedStatusMenuItems.entrySet()) {
    		ActorStatus status = entry.getKey();
    		JMenuItem menuItem = entry.getValue();
    		
    		// Determine which way to go
    		boolean all_set = true;
    		boolean all_unset = true;
    		for (int row: getSelectedRows()) {
    			Actor actor = tableModel.getActor(row);
    			if (actor.hasStatus(status))
    				all_unset = false;
    			else
    				all_set = false;
    					
    		}
    		if (all_set)
    			menuItem.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/tick.png"), "Tick"));
    		else if (all_unset)
    			menuItem.setIcon(null);
    		else
    			menuItem.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/shape_square.png"), "Square"));
    	}
    	
    }
    
 
	public void initialize() {
		 //InitTable initTable = new InitTable(new ActorTableModel());
        setDefaultRenderer(Object.class, new InitTableCellRenderer());
        setDefaultRenderer(new Integer(0).getClass(), new InitTableCellRenderer());
        setDefaultEditor(String.class, new InitTableTextCellEditor());
        setDefaultEditor(new Integer(0).getClass(), new InitTableIntegerCellEditor());
        setTransferHandler(new InitTableTransferHandler("name"));
        setPreferredScrollableViewportSize(new Dimension(800, 270));
        setFillsViewportHeight(true);
        //initTable.setRowSelectionAllowed(true);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setDragEnabled(true);
        setDropMode(DropMode.INSERT_ROWS);
        this.setSurrendersFocusOnKeystroke(false);

        // Freeze the 'Act' column size
        getColumnModel().getColumn(InitTableModel.columns.Act.ordinal()).setResizable(false);       
        
		// Set column editors
        JComboBox<ActorStatus> initTableStateEditor = new JComboBox<ActorStatus>();
        for (Actor.ActorStatus a : Actor.ActorStatus.values()) {
        	initTableStateEditor.addItem(a);
        }
        getColumnModel().getColumn(InitTableModel.columns.Status.ordinal()).setCellEditor(new InitTableStatusListCellEditor());
        getColumnModel().getColumn(InitTableModel.columns.Type.ordinal()).setCellEditor(new InitTableTypeListCellEditor());
        getColumnModel().getColumn(InitTableModel.columns.Damage.ordinal()).setCellEditor(new InitTableDamageCellEditor());
        getColumnModel().getColumn(InitTableModel.columns.Fatigue.ordinal()).setCellEditor(new InitTableDamageCellEditor());

		// Table popup menu
        popupMenu = new JPopupMenu();
        coordinatedStatusMenuItems = new HashMap<ActorStatus, JMenuItem>();
        JMenu menuFile = new JMenu("Status");
        menuFile.setMnemonic(KeyEvent.VK_S);
        menuFile.add(createCoordinatedStatusMenuItem(ActorStatus.Attacking, KeyEvent.VK_A));
        menuFile.add(createCoordinatedStatusMenuItem(ActorStatus.Waiting, KeyEvent.VK_W));
        menuFile.add(createCoordinatedStatusMenuItem(ActorStatus.StunPhys, KeyEvent.VK_P));
        menuFile.add(createCoordinatedStatusMenuItem(ActorStatus.StunMental, KeyEvent.VK_M));
        menuFile.add(createCoordinatedStatusMenuItem(ActorStatus.StunRecovr, KeyEvent.VK_S));
        menuFile.add(createCoordinatedStatusMenuItem(ActorStatus.Disarmed, KeyEvent.VK_R));
        menuFile.add(createCoordinatedStatusMenuItem(ActorStatus.Kneeling, KeyEvent.VK_K));
        menuFile.add(createCoordinatedStatusMenuItem(ActorStatus.Prone, KeyEvent.VK_P));
        menuFile.add(createCoordinatedStatusMenuItem(ActorStatus.Disabled, KeyEvent.VK_D));
        menuFile.add(createCoordinatedStatusMenuItem(ActorStatus.Unconscious, KeyEvent.VK_U));
        menuFile.add(createCoordinatedStatusMenuItem(ActorStatus.Dead, KeyEvent.VK_E));
        popupMenu.add(menuFile);
        menuFile = new JMenu("Type");
        menuFile.setMnemonic(KeyEvent.VK_T);
        menuFile.add(createMenuItem("PC", KeyEvent.VK_C));
        menuFile.add(createMenuItem("Ally", KeyEvent.VK_A));
        menuFile.add(createMenuItem("Enemy", KeyEvent.VK_E));
        menuFile.add(createMenuItem("Neutral", KeyEvent.VK_N));
        menuFile.add(createMenuItem("Special", KeyEvent.VK_S));
        popupMenu.add(menuFile);
        if (isInitTable) { popupMenu.add(createMenuItem("Set Active", KeyEvent.VK_A)); }
        if (isInitTable) { popupMenu.add(createMenuItem("Attack", KeyEvent.VK_K)); }
        if (isInitTable) { popupMenu.add(createMenuItem("Defend", KeyEvent.VK_D)); }
        //popupMenu.add(createMenuItem("Clone", KeyEvent.VK_C));
        popupMenu.add(createMenuItem("Reset", KeyEvent.VK_R));
        popupMenu.add(createMenuItem("Delete", KeyEvent.VK_DELETE));
        if (isInitTable) { popupMenu.add(createMenuItem("Tag", KeyEvent.VK_T)); }
        if (isInitTable) { popupMenu.add(createMenuItem("Remove Tag", KeyEvent.VK_V)); }
        MousePopupListener popupListener = new MousePopupListener();
        addMouseListener(popupListener);
        getTableHeader().addMouseListener(popupListener);
        
        // Don't display 'Act' column in the group manager table
        // Removing this column changes the indexes in the column model only - be careful when using getColumn(i)
        if(!isInitTable) { getColumnModel().removeColumn(getColumnModel().getColumn(InitTableModel.columns.Act.ordinal())); }
        
        // Create initial column sizing
        autoSizeColumns();
	}
	
	/**
	 * Return whether this table is the initTable
	 * @return : true is this table is the initTable, false if it is the groupTable
	 */
	public boolean isInitTable() {
		return isInitTable;
	}
	
	/**
	 * Advance current Actor. Should only be called for initTable, not groupTable.
	 * @return Whether the round has ended
	 */
	public boolean nextActor() {	
		return tableModel.nextActor();
	}
	
	/**
	 * Access method for the actorTableModel.
	 * @return The ActorTableModel underlying the table.
	 */
	public InitTableModel getActorTableModel() {
		return tableModel;
	}
	
	/**
	 * Retrieve the currently active Actor
	 * @return The currently active Actor
	 */
	public Actor getActiveActor() {
		return tableModel.getActiveActor();
	}
	
	/**
	 * Retrieve the currently selected Actor
	 * @return The currently selected Actor
	 */
	public Actor getSelectedActor() {
		int index = getSelectedRow();
		if (index < 0)
			return null;
		return tableModel.getActor(index);
	}
	
	@Override
	public int[] getSelectedRows() {
		int[] rows = super.getSelectedRows();
		if (rows.length > 0 && rows[rows.length-1] == getRowCount() -1) {
			if (rows.length == 1)
				return new int[0];
			int[] newrows = new int[rows.length-1];
			System.arraycopy( rows, 0, newrows, 0, newrows.length );
			return newrows;
		}	
		return rows;
	}
	
	/**
	 * Convenience method to set an actor's value through the model instead of directly
	 * @param actor The actor to modify
	 * @param field The field to change
	 * @param newValue The new value
	 */
	public void setActorValue(Actor actor, InitTableModel.columns field, Object newValue) {
		tableModel.setValueAt(newValue, tableModel.getActorRows(actor)[0], field.ordinal());
	}
	
	/**
	 * Add or remove the specified status indicator to all selected actors
	 * @param status The status to add or remove
	 * @param add True if add, false if remove
	 */
	public void modifyStatusOfSelectedActors(ActorStatus status, boolean add) {
		for (int row: getSelectedRows()) {
			Actor actor = tableModel.getActor(row);
			if (add)
				actor.addStatus(status);
			else 
				actor.removeStatus(status);
		}
	}
	
	/**
	 * Toggle the specified status indicator to all selected actors individually
	 * @param status The status to toggle
	 */
	public void toggleStatusOfSelectedActors(ActorStatus status) {
		for (int row: getSelectedRows()) {
			Actor actor = tableModel.getActor(row);
			if (actor.hasStatus(status))
				actor.removeStatus(status);
			else
				actor.addStatus(status);
		}
	}
	
	/**
	 * Adjust all the statuses in a coordinated fashion.
	 * If any are unset: all are set
	 * If all are set: unset
	 * @param status The status to toggle
	 */
	public void coordinatedChangeStatusOfSelectedActors(ActorStatus status) {
		boolean all_set = true;
		// First, determine which way to go
		for (int row: getSelectedRows()) {
			Actor actor = tableModel.getActor(row);
			if (!actor.hasStatus(status)) {
				all_set = false;
				break;
			}
		}
		for (int row: getSelectedRows()) {
			Actor actor = tableModel.getActor(row);
			if (all_set)
				actor.removeStatus(status);
			else
				actor.addStatus(status);
		}
	}
	
	/**
	 * Reset the encounter. Set the active actor to -1
	 */
	public void resetEncounter() {
		tableModel.resetEncounter();
	}
	
    public void activeActorAttack() {
    	if (DEBUG) System.out.println("InitTable: activeActorAttack");
    	Actor actor = getActiveActor();
    	if (actor == null) 
    		return;
    	actor.Attack();
    }
    
    public void selectedActorsAttack() {
    	for (int row: getSelectedRows()) {
			Actor actor = tableModel.getActor(row);
			actor.Attack();
    	}
    }
    
    /**
     * Show the Defense Dialog and forward to the actor for processing if valid
     */
    public void selectedActorDefend() {
    	System.out.println("InitTable: selectedActorDefend: start");
    	// Verify valid actor
    	Actor actor = getSelectedActor();
    	if (actor == null)
    		return;
    	// Clear out edits in progress
    	stopCellEditing();
    	// Show Defense Dialog window
    	DefenseDialog defenseDialog = new DefenseDialog(actor, SwingUtilities.getWindowAncestor(this));
    	// TODO: this is a hack of the property bag system: fix!
    	defenseDialog.setLocation(Integer.valueOf(propertyBag.getProperty("GITApp.defense.location.x")),
        					Integer.valueOf(propertyBag.getProperty("GITApp.defense.location.y")));
        GITApp.validateOnScreen(defenseDialog);
        defenseDialog.setVisible(true); // Modal call
    	// Process and log result!
    	if (defenseDialog.valid) {
    		actor.Defend(defenseDialog.defense);
    	}
    	// TODO: this is a hack of the property bag system: fix!
    	propertyBag.setProperty("GITApp.defense.location.x", String.valueOf(defenseDialog.getLocation().x));
    	propertyBag.setProperty("GITApp.defense.location.y", String.valueOf(defenseDialog.getLocation().y));
    }
    
	/**
	 * Halt cell editing, if it is occurring.
	 */
	public void stopCellEditing() {
		// Don't allow editing to continue while the table is changed
		if(getCellEditor() != null)
			if (!getCellEditor().stopCellEditing())
				getCellEditor().cancelCellEditing();
	}
	
	/**
	 * Modify a component to match it's conditions
	 * @param component : the component to modify
	 * @param actor
	 */
	public static void formatComponentAlignment(JLabel c, Actor a, InitTableModel.columns col) {
		c.setHorizontalAlignment(SwingConstants.LEFT);
		c.setHorizontalTextPosition(JLabel.LEADING);

		if (a.hasStatus(ActorStatus.Waiting)) {
			c.setHorizontalAlignment(SwingConstants.RIGHT);
		}

		if (col == columns.Act) {
			c.setHorizontalAlignment(SwingConstants.RIGHT);
		}
	}
	
	/**
	 * Modify a component to match it's conditions
	 * @param component : the component to modify
	 * @param actor
	 */
	public static void formatComponentAlignment(JTextField c, Actor a) {
		c.setHorizontalAlignment(SwingConstants.LEFT);	
		if (a.hasStatus(ActorStatus.Waiting)) {
			c.setHorizontalAlignment(SwingConstants.RIGHT);
		}
	}

		/**
	 * Modify a component to match it's conditions
	 * @param component : the component to modify
	 * @param actor
	 */
	public static void formatComponentColor(JComponent c, Actor a, boolean isSelected, InitTableModel.columns col) {
		
		if (col == columns.Damage || col == columns.Fatigue) {
			c.setForeground(new Color(220,0,0));
        	//if (DEBUG) { System.out.println("formatComponentColor: Setting column to have red foreground."); }
		}
		else {
			c.setForeground(new Color(0,0,0));
		}
		
		if (isSelected) {
			switch (a.getType()) {
			case PC:
				c.setBackground(new Color(128,255,128));
				break;
			case Ally:
				c.setBackground(new Color(128,128,255));
				break;
			case Enemy:
				c.setBackground(new Color(255,128,128));
				break;
			case Neutral:
				c.setBackground(new Color(128,128,128));
				break;
			case Special:
				c.setBackground(new Color(255,128,255));
				break;
			}
		}
		else {
			switch (a.getType()) {
			case PC:
				c.setBackground(new Color(200,255,200));
				break;
			case Ally:
				c.setBackground(new Color(200,200,255));
				break;
			case Enemy:
				c.setBackground(new Color(255,200,200));
				break;
			case Neutral:
				c.setBackground(new Color(200,200,200));
				break;
			case Special:
				c.setBackground(new Color(255,200,255));
				break;
			}
		}
		
		if (a.hasStatus(ActorStatus.Unconscious)
				|| a.hasStatus(ActorStatus.Disabled)
				|| a.hasStatus(ActorStatus.Dead)) {
			c.setForeground(new Color(128,128,128));
		}

		/*if (hasFocus) {
			c.setFont(new Font("sansserif", Font.BOLD, 12));
		}*/
	}
   
	/**
	 * Renderer to deal with all the customizations based on Actor state/type/etc.
	 * Assumes that the table model being used is an ActorTableModel.
	 * @author dsmall
	 *
	 */
	class InitTableCellRenderer extends DefaultTableCellRenderer {

		/**
		 * This class is not really serializable, I think.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			
			JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			InitTableModel.columns col = InitTableModel.columns.valueOf(table.getColumnName(column));
			if (row == table.getRowCount() -1) {
				c.setBackground(new Color(255,255,255));
				c.setForeground(new Color(128,128,128));
				c.setHorizontalAlignment(SwingConstants.LEFT);
				c.setIcon(new ImageIcon());
				if (col == columns.Name)
					c.setText("new...");
				else if (col == columns.Status)
					c.setText("");
				return c;
			}
			
			// Custom rendering for various columns
			//ActorTableModel.columns col = ActorTableModel.columns.values()[column];
			Actor a = ((InitTableModel)table.getModel()).getActor(row);
			int Injury = a.getTraitValueInt(BasicTrait.Injury);
			int Fatigue = a.getTraitValueInt(BasicTrait.Fatigue);
			int HP = a.getTraitValueInt(BasicTrait.HP);
			int FP = a.getTraitValueInt(BasicTrait.FP);
			if (col == columns.Act && (tableModel.getActiveActorIndex() == row)) {
				c.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/go.png"), "Current Actor"));  
			}
			else if ((col == columns.Move || col == columns.Dodge) && (Injury > 2*HP/3 && Fatigue > 2*FP/3)) {
				int currentValue = Integer.parseInt(c.getText());
				int newValue = (int) Math.ceil((double)currentValue/4);
				c.setText("<html>" + c.getText() + " <strong>(" + newValue + ")</strong></html>");
				c.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/exclamation.png"), "Greatly reduced state"));
			}
			else if ((col == columns.Move || col == columns.Dodge) && (Injury > 2*HP/3 || Fatigue > 2*FP/3)) {
				int currentValue = Integer.parseInt(c.getText());
				int newValue = (int) Math.ceil((double)currentValue/2);
				c.setText("<html>" + c.getText() + " <strong>(" + newValue + ")</strong></html>");
				c.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/error.png"), "Reduced state"));
			}
			else if (col == columns.HT && Injury >= HP) {
				int penalty = (int) (-1*(Math.floor((double)Injury/HP)-1));
				if (penalty < 0) {
					c.setText("<html>" + c.getText() + " <strong>(" + penalty + ")</strong></html>");
				}
				c.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/error.png"), "Must check to stay conscious"));
			}
			else if (col == columns.Status) { // comma separated, ordered by enum order
				c.setText(a.getStatusesString());
				c.setIcon(new ImageIcon());
			}
			else {
				c.setIcon(new ImageIcon());
			}
			
			formatComponentColor((JComponent)c, a, isSelected, col);
			formatComponentAlignment(c, a, col);
			return c;
		}
	}
	
	/**
	 * Renderer to deal with all the customizations based on Actor state/type/etc.
	 * Assumes that the table model being used is an ActorTableModel.
	 * @author dsmall
	 *
	 */
	class InitTableIntegerCellEditor extends DefaultCellEditor {

		/**
		 * Default serial UID
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Super does not define default constructor, so must define one.
		 * @param comboBox
		 */
		public InitTableIntegerCellEditor() {
			super(new JTextField());
		}
		
		//Make sure the value remains an Integer.
		@Override
	    public Object getCellEditorValue() {
	        JTextField tf = (JTextField)getComponent();
	        try {
	        	Integer value = new Integer(tf.getText());
	        	return value;
	        } catch (NumberFormatException e) {
	        	return 0;
	        }
	    }

		// Do a final check to make sure everything is ok.
	    public boolean stopCellEditing() {
	        JTextField tf = (JTextField)getComponent();
	        String text = tf.getText();
	        try {
	        	new Integer(text);
	        	tf.setText(text);
	        	return super.stopCellEditing();
	        } catch (NumberFormatException e) {
	        	tf.setBorder(new LineBorder(new Color(220,0,0)));
	        	
	        	return false;
	        }
	    }
	    
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			
			JTextField c = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
			if (isSelected) {
			    c.selectAll();
			}
			
			if (row == table.getRowCount() -1) {
				c.setBackground(new Color(255,255,255));
				c.setForeground(new Color(128,128,128));
				c.setHorizontalAlignment(SwingConstants.LEFT);
				return c;
			}
			
			InitTableModel.columns col = InitTableModel.columns.valueOf(table.getColumnName(column));
			Actor a = ((InitTableModel)table.getModel()).getActor(row);
						
			formatComponentColor((JComponent)c, a, isSelected, col);
			formatComponentAlignment(c, a);
			c.setBorder(new LineBorder(new Color(255,255,255)));
			return c;
		}
	}
	
	
	/**
	 * Inner class to provide CellEditor functionality
	 * Main purpose is to synchronize the combo box selected item with the current value in the cell
	 * @author dsmall
	 *
	 */
	class InitTableStatusListCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener, FocusListener, ComponentListener  {

		/**
		 * Default serial UID
		 */
		private static final long serialVersionUID = 1L;

		JList<ActorStatus> list;
		JScrollPane pane;
		JButton button;
		JDialog dialog = null;
		boolean isEditing = false;
		
		public InitTableStatusListCellEditor() {
			button = new JButton();
	        button.setBorderPainted(false);
		    button.setFocusPainted(false);
		    button.setContentAreaFilled(false);
	        button.setActionCommand("EDIT");
	        button.addActionListener(this);
		    button.addFocusListener(this);

			list = new JList<ActorStatus>(Actor.ActorStatus.values());
			list.setVisibleRowCount(Actor.ActorStatus.values().length);
			list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			list.setSelectionModel(new DefaultListSelectionModel() {
				boolean gestureStarted = false;
				boolean adding = true;
			    @Override
			    public void setSelectionInterval(int index0, int index1) {
			    	if (getValueIsAdjusting() && !gestureStarted && index0 != -1 && index1 != -1) {
			    		adding = super.isSelectedIndex(index0)?false:true;
			    		gestureStarted = true;
			    	}
			    
			        if(!getValueIsAdjusting() || adding) {
			            super.addSelectionInterval(index0, index1);
			        }
			        else {
			            super.removeSelectionInterval(index0, index1);
			        }
			    }
			    @Override
			    public void setValueIsAdjusting(boolean isAdjusting) {
			    	if (isAdjusting == false) {
			    	    gestureStarted = false;
			    	}
			    	super.setValueIsAdjusting(isAdjusting);
			    }
			});
            pane = new JScrollPane(list);
		}
        
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			if (dialog == null) {
				dialog = new JDialog((Frame)InitTable.this.getTopLevelAncestor(), false);
				dialog.add(pane);
				//dialog.getRootPane().setBorder(BorderFactory.createLineBorder(Color.black));
				dialog.setUndecorated(true);
				dialog.setBounds(0, 0, 10, 10);
				dialog.setModal(false);
				//dialog.setFocusable(false);
				dialog.setFocusableWindowState(false);
				InitTable.this.getTopLevelAncestor().addComponentListener(this);
			}
			list.clearSelection();
			for (ActorStatus status: (HashSet<ActorStatus>) value) {
				list.setSelectedValue(status, true);
			}
			return button;
		}

		@Override
		public Object getCellEditorValue() {
			HashSet<ActorStatus> statuses = new HashSet<ActorStatus>(list.getSelectedValuesList());
			return statuses;
		}

		@Override
		public boolean stopCellEditing() {
			boolean retval = super.stopCellEditing();
			dialog.setVisible(false);
			isEditing = false;
			return retval; 
		}
		
		@Override 
		public void cancelCellEditing() {
			super.cancelCellEditing();
			dialog.setVisible(false);
			isEditing = false;
		}
		
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getActionCommand().equals("EDIT")) {
				dialog.setLocation(button.getLocationOnScreen());
				dialog.setSize(button.getWidth(),dialog.getPreferredSize().height);
				dialog.setVisible(true);
				isEditing = true;
			}
		}

		@Override
		public void focusGained(FocusEvent e) {}

		@Override
		public void focusLost(FocusEvent e) {
			if (isEditing)
				stopCellEditing();
		}

		@Override
		public void componentHidden(ComponentEvent e) {}

		@Override
		public void componentMoved(ComponentEvent e) {
			if (isEditing)
				stopCellEditing();
		}

		@Override
		public void componentResized(ComponentEvent e) {}

		@Override
		public void componentShown(ComponentEvent e) {}
	}
	
	
	/**
	 * Inner class to provide CellEditor functionality
	 * Main purpose is to synchronize the combo box selected item with the current value in the cell
	 * @author dsmall
	 *
	 */
	class InitTableTypeListCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener, MouseListener, FocusListener, ComponentListener  {

		/**
		 * Default serial UID
		 */
		private static final long serialVersionUID = 1L;

		JList<ActorType> list;
		JScrollPane pane;
		JButton button;
		JDialog dialog = null;
		boolean isEditing = false;
		
		public InitTableTypeListCellEditor() {
			button = new JButton();
	        button.setBorderPainted(false);
		    button.setFocusPainted(false);
		    button.setContentAreaFilled(false);
	        button.setActionCommand("EDIT");
	        button.addActionListener(this);
		    button.addFocusListener(this);

			list = new JList<ActorType>(Actor.ActorType.values());
			list.setVisibleRowCount(Actor.ActorType.values().length);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.addMouseListener(this);
            pane = new JScrollPane(list);
		}
        
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			if (dialog == null) {
				dialog = new JDialog((Frame)InitTable.this.getTopLevelAncestor(), false);
				dialog.add(pane);
				//dialog.getRootPane().setBorder(BorderFactory.createLineBorder(Color.black));
				dialog.setUndecorated(true);
				dialog.setBounds(0, 0, 10, 10);
				dialog.setModal(false);
				//dialog.setFocusable(false);
				dialog.setFocusableWindowState(false);
				InitTable.this.getTopLevelAncestor().addComponentListener(this);
			}
			list.setSelectedValue(value, true);
			return button;
		}

		@Override
		public Object getCellEditorValue() {
			return list.getSelectedValue();
		}

		@Override
		public boolean stopCellEditing() {
			boolean retval = super.stopCellEditing();
			dialog.setVisible(false);
			isEditing = false;
			return retval; 
		}
		
		@Override 
		public void cancelCellEditing() {
			super.cancelCellEditing();
			dialog.setVisible(false);
			isEditing = false;
		}
		
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getActionCommand().equals("EDIT")) {
				dialog.setLocation(button.getLocationOnScreen());
				dialog.setSize(button.getWidth(),dialog.getPreferredSize().height);
				dialog.setVisible(true);
				isEditing = true;
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (isEditing)
				stopCellEditing();
		}

		@Override
		public void focusGained(FocusEvent e) {}

		@Override
		public void focusLost(FocusEvent e) {
			if (isEditing)
				cancelCellEditing();
		}

		@Override
		public void componentHidden(ComponentEvent e) {}

		@Override
		public void componentMoved(ComponentEvent e) {
			if (isEditing)
				cancelCellEditing();
		}

		@Override
		public void componentResized(ComponentEvent e) {}

		@Override
		public void componentShown(ComponentEvent e) {}
	}
	
	/**
	 * Inner class to provide CellEditor functionality
	 * Allow modification of the text cell editor
	 * @author dsmall
	 */
	class InitTableTextCellEditor extends DefaultCellEditor implements FocusListener {

		/**
		 * Default serial UID
		 */
		private static final long serialVersionUID = 1L;
		
		// The original actor name that the editor started with
		private String actorName;

		/**
		 * Super does not define default constructor, so must define one.
		 * @param comboBox
		 */
		public InitTableTextCellEditor() {
			super(new JTextField());
			setClickCountToStart(1);
			editorComponent.addFocusListener(this);
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			
			JTextField c = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);

			if (row == table.getRowCount() -1) {
				c.setBackground(new Color(255,255,255));
				c.setForeground(new Color(128,128,128));
				c.setHorizontalAlignment(SwingConstants.LEFT);
				//c.setIcon(new ImageIcon());
				//c.setText("");
				return c;
			}
			
			InitTableModel.columns col = InitTableModel.columns.valueOf(table.getColumnName(column));
			Actor a = ((InitTableModel)table.getModel()).getActor(row);
			actorName = a.getTraitValue(BasicTrait.Name);
			formatComponentColor(c, a, isSelected, col);
			formatComponentAlignment(c, a);
	    	
			return c;
		}
		
		@Override
	    public void focusGained(FocusEvent evt) {
	    	if (DEBUG) { System.out.println("InitTable: Focus gained on " + evt.toString()); }
	    	// check for modifications in the base Actor
	    	// This hack is only needed if setClickCountToStart = 1, since in that 
	    	// case the table is not updated in time when there is a modification on focus lost
	    	String selctedActorName = getSelectedActor().getTraitValue(BasicTrait.Name);
	    	if (!selctedActorName.equals(actorName)) {
	    		JTextField t = (JTextField) evt.getComponent();
	    		actorName = selctedActorName;
	    		t.setText(actorName);
	    	}
	    	// Clear the 'new...' if this is the last row 
	    	//if (getSelectedRow() == getRowCount() -1) {
	    	//	JTextField t = (JTextField) evt.getComponent();
	    	//	t.setText("");
	    	//}
	    }

		@Override
		public void focusLost(FocusEvent evt) {
			if (DEBUG) { System.out.println("InitTable: Focus lost on " + evt.toString()); }
			stopCellEditing();
		}
		
	}
	
	/**
	 * Inner class to provide CellEditor functionality
	 * Allow modification of the damage cell editor
	 * @author dsmall
	 */
	class InitTableDamageCellEditor extends DefaultCellEditor {

		/**
		 * Default serial UID
		 */
		private static final long serialVersionUID = 1L;
		private static final boolean DEBUG = true;
		
		public InitTableDamageCellEditor() {
			super(new JTextField());
			DamageDocumentFilter df = new DamageDocumentFilter();
			JTextField tf = (JTextField)getComponent();
			tf.addFocusListener(df);
			((AbstractDocument) tf.getDocument()).setDocumentFilter(df);
		}
		
		//Make sure the value remains an Integer.
		@Override
	    public Object getCellEditorValue() {
	        JTextField tf = (JTextField)getComponent();
	        try {
	        	Integer value = new Integer(tf.getText());
	        	return value;
	        } catch (NumberFormatException e) {
	        	return 0;
	        }
	    }

		// Parse through the value, and perform any operations. Do a final check to make sure everything is ok.
		// Keep track of all the intermediate damage steps
	    public boolean stopCellEditing() {
	        JTextField tf = (JTextField)getComponent();
	        String text = tf.getText();
	        Pattern pattern = Pattern.compile("^(-?\\d+)([\\+-])(\\d+)(.*)$");
	        Matcher matcher = pattern.matcher(text);
	        while (matcher.matches()) {
	        	Integer first = new Integer(matcher.group(1));
	        	String operator = matcher.group(2);
	        	Integer second = new Integer(matcher.group(3));
	        	Integer result;
	        	if (operator.equals("+")) { result = first + second; }
	        	else { result = first - second; }
	        	text = matcher.group(4);
	        	if (DEBUG) { System.out.println("InitTableDamageCellEditor: Calculating damage: " + first + " : " + operator + " : " + second + " = " + result + " (" + text + ")."); }
	        	text = result + text;
	        	matcher = pattern.matcher(text);
	        }
	        try {
	        	new Integer(text);
	        	tf.setText(text);
	        	return super.stopCellEditing();
	        } catch (NumberFormatException e) {
	        	tf.setBorder(new LineBorder(new Color(220,0,0)));
	        	return false;
	        }
	    }
	    
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			
			JTextField c = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);

			if (row == table.getRowCount() -1) {
				c.setBackground(new Color(255,255,255));
				c.setForeground(new Color(128,128,128));
				c.setHorizontalAlignment(SwingConstants.LEFT);
				return c;
			}
			
			InitTableModel.columns col = InitTableModel.columns.valueOf(table.getColumnName(column));
			Actor a = ((InitTableModel)table.getModel()).getActor(row);
						
			formatComponentColor((JComponent)c, a, isSelected, col);
			formatComponentAlignment(c, a);
			c.setBorder(new LineBorder(new Color(255,255,255)));
			return c;
		}
		
		private class DamageDocumentFilter extends DocumentFilter implements FocusListener {
			
			boolean startingNew = true;
			boolean firstEdit = true; // For some reason, the first edit is different than the rest
			boolean hasFocus = false;
			
			@Override
			public void remove(FilterBypass fb, int offs, int length) throws BadLocationException {
				System.out.println("InitTableDamageCellEditor: DamageDocumentFilter: Remove: offs: " + offs + ", len:" + length + ".");
				startingNew = false;
				super.remove(fb, offs, length);
			}
			
			@Override
			public void insertString(FilterBypass fb, int offs, String str, javax.swing.text.AttributeSet a) throws BadLocationException {
				System.out.println("InitTableDamageCellEditor: DamageDocumentFilter: Insert:" + str + ".");
				
				if (str.matches("[\\d\\+-]+")) {
					super.insertString(fb, offs, str, a);
				}
			}
			
			@Override
			public void replace(FilterBypass fb, int offs, int length, String str, javax.swing.text.AttributeSet a) throws BadLocationException {
				System.out.println("InitTableDamageCellEditor: DamageDocumentFilter: Replace: '" + str + "', Offs=" + offs + ", Length=" + length + ".");
				
				if (str.matches("[\\d\\+-]+")) {
					if (hasFocus || firstEdit || (length > 0)) {
						firstEdit = false;
						startingNew = true;
						super.replace(fb, offs, length, str, a);
					}
					else if (startingNew && str.matches("\\d")) {
						startingNew = false;
						super.replace(fb, offs, 0, "+", null);
						super.replace(fb, offs+1, 0, str, a);
					}
					else {
						startingNew = false;
						super.replace(fb, offs, 0, str, a);
					}
				}
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				hasFocus = true;
				System.out.println("InitTableDamageCellEditor: DamageDocumentFilter: TextField focus gained.");
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				hasFocus = false;
				System.out.println("InitTableDamageCellEditor: DamageDocumentFilter: TextField focus lost.");
			}
		}
	}
	
	/**
	 * An inner class to check whether mouse events are the pop-up trigger
	 */
	class MousePopupListener extends MouseAdapter {
	    	
	    public void mousePressed(MouseEvent e) { checkPopup(e); }
	    public void mouseClicked(MouseEvent e) { checkPopup(e); checkResize(e);}
	    public void mouseReleased(MouseEvent e) { checkPopup(e); }
	 
	    private void checkPopup(MouseEvent e) {
	    	if (e.isPopupTrigger() & (getSelectedRows().length > 0)) {
	    		updateCoordinatedStatusMenuItems();
	   			popupMenu.show(e.getComponent(), e.getX(), e.getY());
	        }
	    }
	    
	    private void checkResize(MouseEvent e) {
	    	// See if it's a double click
	    	if (e.getClickCount() == 2) {
		        // Determine if cursor is the resize cursor
		        Cursor currentCursor = getTableHeader().getCursor();
				System.out.println("MouseClickListener: checkResize: double-click detected. Type is " + currentCursor.getType() + " (" + currentCursor.toString() + ")");
				
				if (currentCursor.getType() == Cursor.E_RESIZE_CURSOR) {
					autoSizeColumns();
					System.out.println("MouseClickListener: checkResize: auto-sizing columns.");
				}
	    	}
	    }
	}
}

