package gurpsinittool.app;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import gurpsinittool.app.InitTableModel.columns;
import gurpsinittool.data.*;
//import gurpsinittool.test.RandomData;
import gurpsinittool.data.Actor.ActorState;
import gurpsinittool.data.Actor.ActorType;

public class InitTable extends JTable 
	implements ActionListener {
	
	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = true;

	private JPopupMenu popupMenu;
	private InitTableModel tableModel;
	private boolean isInitTable;
	
	/**
	 * Default Constructor
	 */
	public InitTable(boolean isInitTable) {
		super(new InitTableModel());
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
       		if (DEBUG) { System.out.println("InitTable: Resetting actor. Row: " + rows[0] + ", Actor: " + tableModel.getActor(rows[0]).Name); }   	
       		for (int i = 0; i < rows.length; i++) {
       			Actor actor = tableModel.getActor(rows[i]);
       			actor.Reset();
       			tableModel.fireRefresh(actor);
       		}
    	}
    	else if ("Set Active".equals(e.getActionCommand())) { // Clone selected rows at the end (as "Haste" spell)
      		int[] rows = getSelectedRows();
      		if (DEBUG) { System.out.println("InitTable: Setting active actor. Row: " + rows[0] + ", Actor: " + tableModel.getActor(rows[0]).Name); }   	
      		tableModel.setActiveRow(rows[0]); // sets actor as current active, and sets state to active
       		for (int i = 0; i < rows.length; i++) {
       			tableModel.setValueAt("Active", rows[i], InitTableModel.columns.State.ordinal());
       		}
    	}
    	else if ("Tag".equals(e.getActionCommand())) { // Add tag to selected rows
      		int[] rows = getSelectedRows();
      		if (DEBUG) { System.out.println("InitTable: Tagging actor. Row: " + rows[0] + ", Actor: " + tableModel.getActor(rows[0]).Name); }   	
       		for (int i = 0; i < rows.length; i++) {
       			tableModel.tagActor(tableModel.getActor(rows[i]));
       		}
    	}
    	else if ("Remove Tag".equals(e.getActionCommand())) { // Remove tag from selected rows
      		int[] rows = getSelectedRows();
      		if (DEBUG) { System.out.println("InitTable: Un-tagging actor. Row: " + rows[0] + ", Actor: " + tableModel.getActor(rows[0]).Name); }   	
       		for (int i = 0; i < rows.length; i++) {
       			tableModel.removeTag(tableModel.getActor(rows[i]));
       		}
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
            for (Actor.ActorState s : Actor.ActorState.values()) {
            	if (s.toString().equals(e.getActionCommand())) {
            		stopCellEditing();
              		int[] rows = getSelectedRows();
               		for (int i = 0; i < rows.length; i++) {
               			tableModel.setValueAt(s.toString(), rows[i], InitTableModel.columns.State.ordinal());
               		}
            	}
            }
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
        JComboBox<ActorState> initTableStateEditor = new JComboBox<ActorState>();
        for (Actor.ActorState a : Actor.ActorState.values()) {
        	initTableStateEditor.addItem(a);
        }
        getColumnModel().getColumn(InitTableModel.columns.State.ordinal()).setCellEditor(new InitTableComboCellEditor(initTableStateEditor));
        ((DefaultCellEditor) getColumnModel().getColumn(InitTableModel.columns.State.ordinal()).getCellEditor()).setClickCountToStart(2);
        JComboBox<ActorType> initTableTypeEditor = new JComboBox<ActorType>();
        for (Actor.ActorType a : Actor.ActorType.values()) {
        	initTableTypeEditor.addItem(a);
        }
        getColumnModel().getColumn(InitTableModel.columns.Type.ordinal()).setCellEditor(new InitTableComboCellEditor(initTableTypeEditor));
        ((DefaultCellEditor) getColumnModel().getColumn(InitTableModel.columns.Type.ordinal()).getCellEditor()).setClickCountToStart(2);

        getColumnModel().getColumn(InitTableModel.columns.Damage.ordinal()).setCellEditor(new InitTableDamageCellEditor());
        getColumnModel().getColumn(InitTableModel.columns.Fatigue.ordinal()).setCellEditor(new InitTableDamageCellEditor());
        //((DefaultCellEditor) getColumnModel().getColumn(ActorTableModel.columns.Damage.ordinal()).getCellEditor()).setClickCountToStart(1);

		// Table popup menu
        popupMenu = new JPopupMenu();
        JMenu menuFile = new JMenu("Set Status");
        menuFile.setMnemonic(KeyEvent.VK_S);
        menuFile.add(createMenuItem("Active", KeyEvent.VK_A));
        menuFile.add(createMenuItem("Waiting", KeyEvent.VK_W));
        menuFile.add(createMenuItem("Stunned", KeyEvent.VK_S));
        menuFile.add(createMenuItem("Disabled", KeyEvent.VK_D));
        menuFile.add(createMenuItem("Unconscious", KeyEvent.VK_U));
        menuFile.add(createMenuItem("Dead", KeyEvent.VK_E));
        popupMenu.add(menuFile);
        menuFile = new JMenu("Set Type");
        menuFile.setMnemonic(KeyEvent.VK_T);
        menuFile.add(createMenuItem("PC", KeyEvent.VK_C));
        menuFile.add(createMenuItem("Ally", KeyEvent.VK_A));
        menuFile.add(createMenuItem("Enemy", KeyEvent.VK_E));
        menuFile.add(createMenuItem("Neutral", KeyEvent.VK_N));
        menuFile.add(createMenuItem("Special", KeyEvent.VK_S));
        popupMenu.add(menuFile);
        if (isInitTable) { popupMenu.add(createMenuItem("Set Active", KeyEvent.VK_A)); }
        //popupMenu.add(createMenuItem("Clone", KeyEvent.VK_C));
        popupMenu.add(createMenuItem("Reset", KeyEvent.VK_R));
        popupMenu.add(createMenuItem("Delete", KeyEvent.VK_DELETE));
        popupMenu.add(createMenuItem("Tag", KeyEvent.VK_T));
        popupMenu.add(createMenuItem("Remove Tag", KeyEvent.VK_V));
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
	 * @return Whether a new round has started.
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
	 * Reset the encounter. Set the active actor to -1
	 */
	public void resetEncounter() {
		tableModel.resetEncounter();
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
		switch (a.State) {
		case Waiting:
			c.setHorizontalAlignment(SwingConstants.RIGHT);
		default:
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
		switch (a.State) {
		case Waiting:
			c.setHorizontalAlignment(SwingConstants.RIGHT);
		default:
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
			switch (a.Type) {
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
			switch (a.Type) {
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
		
		switch (a.State) {
		case Unconscious:
		case Dead:
			c.setForeground(new Color(128,128,128));
		default:
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
				return c;
			}
			
			//ActorTableModel.columns col = ActorTableModel.columns.values()[column];
			Actor a = ((InitTableModel)table.getModel()).getActor(row);
			if (col == columns.Act && (tableModel.getActiveActorIndex() == row)) {
				c.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/go.png"), "Current Actor"));  
			}
			else if ((col == columns.Move || col == columns.Dodge) && (a.Injury > 2*a.HP/3 && a.Fatigue > 2*a.FP/3)) {
				int currentValue = Integer.parseInt(c.getText());
				int newValue = (int) Math.ceil((double)currentValue/4);
				c.setText("<html>" + c.getText() + " <strong>(" + newValue + ")</strong></html>");
				c.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/exclamation.png"), "Greatly reduced state"));
			}
			else if ((col == columns.Move || col == columns.Dodge) && (a.Injury > 2*a.HP/3 || a.Fatigue > 2*a.FP/3)) {
				int currentValue = Integer.parseInt(c.getText());
				int newValue = (int) Math.ceil((double)currentValue/2);
				c.setText("<html>" + c.getText() + " <strong>(" + newValue + ")</strong></html>");
				c.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/error.png"), "Reduced state"));
			}
			else if (col == columns.HT && a.Injury >= a.HP) {
				int penalty = (int) (-1*(Math.floor((double)a.Injury/a.HP)-1));
				if (penalty < 0) {
					c.setText("<html>" + c.getText() + " <strong>(" + penalty + ")</strong></html>");
				}
				c.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/error.png"), "Must check to stay conscious"));
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
	class InitTableComboCellEditor extends DefaultCellEditor {

		/**
		 * Default serial UID
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Super does not define default constructor, so must define one.
		 * @param comboBox
		 */
		public InitTableComboCellEditor(JComboBox<?> comboBox) {
			super(comboBox);
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
			((JComboBox<?>) c).setSelectedItem(value.toString());
			return c;
		}
		
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
			actorName = a.Name;
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
	    	if (!getSelectedActor().Name.equals(actorName)) {
	    		JTextField t = (JTextField) evt.getComponent();
	    		actorName = getSelectedActor().Name;
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

