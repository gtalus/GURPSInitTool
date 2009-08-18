package gurpsinittool.app;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import gurpsinittool.app.ActorTableModel.columns;
import gurpsinittool.data.*;
import gurpsinittool.test.RandomData;

public class InitTable extends JTable 
	implements ActionListener {
	
	/**
	 * Default Serial UID
	 */
	//private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = true;

	private JPopupMenu popupMenu;
	private ActorTableModel tableModel;
	private boolean isInitTable;
	
	/**
	 * Default Constructor
	 */
	public InitTable(boolean isInitTable) {
		super(new ActorTableModel());
		this.isInitTable = isInitTable;
		initialize();
		if (isInitTable) {
			tableModel = (ActorTableModel) dataModel;
			RandomData.RandomActors(tableModel);
		}
		else {
			setModel(null);
		}
	}

    public void actionPerformed(ActionEvent e) {
    	if (DEBUG) { System.out.println("Received action command " + e.getActionCommand()); }
    	if ("Delete".equals(e.getActionCommand())) { // Delete selected rows
    		int[] rows = getSelectedRows();
    		int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete these rows?", "Confirm Row Delete", JOptionPane.OK_CANCEL_OPTION);
    		if (result == JOptionPane.OK_OPTION) {
    			for (int i = rows.length-1; i >= 0; i--) {  // Go from bottom up to preserve numbering
    				if (DEBUG) { System.out.println("Deleting row: " + rows[i]); }   			
    				tableModel.removeActor(rows[i]); // Just remove the rows indicated: not all instances of clones
    			}
    		}
    	}
      	if ("Set Active".equals(e.getActionCommand())) { // Clone selected rows at the end (as "Haste" spell)
      		int[] rows = getSelectedRows();
      		if (DEBUG) { System.out.println("Setting active actor. Row: " + rows[0] + ", Actor: " + tableModel.getActor(rows[0]).Name); }   	
      		tableModel.setActiveRow(rows[0]); // sets actor as current active, and sets state to active
       		for (int i = 0; i < rows.length; i++) {
       			tableModel.setValueAt("Active", rows[i], ActorTableModel.columns.State.ordinal());
       		}
    	}
     	if ("Clone".equals(e.getActionCommand())) { // Clone selected rows at the end (as "Haste" spell)
     		Actor[] actors = tableModel.getActors(getSelectedRows());
    		for (int i = 0; i < actors.length; i++) {
    		   	if (DEBUG) { System.out.println("Cloning actor: " + actors[i].Name); }   			
    			tableModel.addActor(actors[i], tableModel.getRowCount()-1); // Add actor to the end of the table
    		}
    	}
    	else if ("Active".equals(e.getActionCommand()) 
    			| "Waiting".equals(e.getActionCommand()) 
    			| "Disabled".equals(e.getActionCommand()) 
    			| "Unconscious".equals(e.getActionCommand()) 
    			| "Dead".equals(e.getActionCommand())) {
       		int[] rows = getSelectedRows();
       		for (int i = 0; i < rows.length; i++) {
       			tableModel.setValueAt(e.getActionCommand(), rows[i], ActorTableModel.columns.State.ordinal());
       		}
    	}
       	else if ("PC".equals(e.getActionCommand()) 
    			| "Ally".equals(e.getActionCommand()) 
    			| "Enemy".equals(e.getActionCommand()) 
    			| "Neutral".equals(e.getActionCommand()) 
    			| "Special".equals(e.getActionCommand())) {
       		int[] rows = getSelectedRows();
       		for (int i = 0; i < rows.length; i++) {
       			tableModel.setValueAt(e.getActionCommand(), rows[i], ActorTableModel.columns.Type.ordinal());
       		}
    	}
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
        setDefaultRenderer(new Object().getClass(), new InitTableCellRenderer());
        setDefaultRenderer(new Integer(0).getClass(), new InitTableCellRenderer());
        setTransferHandler(new InitTableTransferHandler("name"));
        setPreferredScrollableViewportSize(new Dimension(800, 270));
        setFillsViewportHeight(true);
        //initTable.setRowSelectionAllowed(true);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setDragEnabled(true);
        setDropMode(DropMode.INSERT_ROWS);

        // Table popup menu
        popupMenu = new JPopupMenu();
        JMenu menuFile = new JMenu("Set Status");
        menuFile.setMnemonic(KeyEvent.VK_S);
        menuFile.add(createMenuItem("Active", KeyEvent.VK_A));
        menuFile.add(createMenuItem("Waiting", KeyEvent.VK_W));
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
        popupMenu.add(createMenuItem("Set Active", KeyEvent.VK_A));
        popupMenu.add(createMenuItem("Clone", KeyEvent.VK_C));
        popupMenu.add(createMenuItem("Delete", KeyEvent.VK_DELETE));
        MousePopupListener popupListener = new MousePopupListener();
        addMouseListener(popupListener);
	}
	
	/**
	 * Advance current Actor
	 */
	public void nextActor() {
		tableModel.nextActor();
	}
	
	/**
	 * Retrieve the currently active Actor
	 * @return The currently active Actor
	 */
	public Actor getCurrentActor() {
		return tableModel.getActor(tableModel.getActiveActor());
	}
	
	/**
	 * Retrieve the currently selected Actor
	 * @return The currently selected Actor
	 */
	public Actor getSelectedActor() {
		return tableModel.getActor(getSelectedRow());
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
	 * Set the table model & re-initializes column editors. If model is null, then table is set to hide.
	 * @param model : The model to use. Must be ActorTableModel or null
	 */
	@Override
	public void setModel(TableModel model) {
		if (model == null) {
			this.setVisible(false);
			super.setModel(new ActorTableModel());
			return;
		}
		tableModel = (ActorTableModel) model;
		super.setModel(model);
		
		// Set column editors
        JComboBox initTableStateEditor = new JComboBox();
        initTableStateEditor.addItem("Active");
        initTableStateEditor.addItem("Waiting");
        initTableStateEditor.addItem("Disabled");
        initTableStateEditor.addItem("Unconscious");
        initTableStateEditor.addItem("Dead");
        getColumnModel().getColumn(ActorTableModel.columns.State.ordinal()).setCellEditor(new InitTableComboCellEditor(initTableStateEditor));
        ((DefaultCellEditor) getColumnModel().getColumn(ActorTableModel.columns.State.ordinal()).getCellEditor()).setClickCountToStart(2);
        JComboBox initTableTypeEditor = new JComboBox();
        initTableTypeEditor.addItem("PC");
        initTableTypeEditor.addItem("Ally");
        initTableTypeEditor.addItem("Enemy");
        initTableTypeEditor.addItem("Neutral");
        initTableTypeEditor.addItem("Special");
        getColumnModel().getColumn(ActorTableModel.columns.Type.ordinal()).setCellEditor(new InitTableComboCellEditor(initTableTypeEditor));
        ((DefaultCellEditor) getColumnModel().getColumn(ActorTableModel.columns.Type.ordinal()).getCellEditor()).setClickCountToStart(2);
		this.setVisible(true);
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
			//JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);	
			ActorTableModel.columns col = ActorTableModel.columns.values()[column];
			if (row == table.getRowCount() -1) {
				c.setBackground(new Color(255,255,255));
				c.setForeground(new Color(128,128,128));
				c.setHorizontalAlignment(SwingConstants.LEFT);
				c.setIcon(new ImageIcon());
				return c;
			}
			
			Actor a = ((ActorTableModel)table.getModel()).getActor(row);
			if (column == 0 && (tableModel.getActiveActor() == row)) {
				c.setIcon(new ImageIcon("src/resources/images/go.png", "Current Actor"));  
			}
			else {
				c.setIcon(new ImageIcon());
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
		
			c.setHorizontalAlignment(SwingConstants.LEFT);
			if (col == columns.Damage) {
				c.setForeground(new Color(220,0,0));
			}
			else {
				c.setForeground(new Color(0,0,0));
			}
			switch (a.State) {
			case Active:
				break;
			case Waiting:
				c.setHorizontalAlignment(SwingConstants.RIGHT);
				break;
			case Disabled:
				break;
			case Unconscious:
			case Dead:
				c.setForeground(new Color(128,128,128));
				break;
			}
			
			/*if (hasFocus) {
				c.setFont(new Font("sansserif", Font.BOLD, 12));
			}*/

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
		public InitTableComboCellEditor(JComboBox comboBox) {
			super(comboBox);
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
			((JComboBox) c).setSelectedItem(value.toString());
			return c;
		}
		
	}
	
	/**
	 * An inner class to check whether mouse events are the pop-up trigger
	 */
	class MousePopupListener extends MouseAdapter {
	    	
	    public void mousePressed(MouseEvent e) { checkPopup(e); }
	    public void mouseClicked(MouseEvent e) { checkPopup(e); }
	    public void mouseReleased(MouseEvent e) { checkPopup(e); }
	 
	    private void checkPopup(MouseEvent e) {
	    	if (e.isPopupTrigger() & (getSelectedRows().length > 0)) {
	   			popupMenu.show(e.getComponent(), e.getX(), e.getY());
	        }
	    }
	}
	
}


