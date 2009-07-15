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
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import gurpsinittool.data.*;

public class InitTable extends JTable 
	implements ActionListener {
	
	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = true;

	private JPopupMenu popupMenu;
	private ActorTableModel tableModel;
	
	/**
	 * Default Constructor
	 */
	public InitTable() {
		super(new ActorTableModel());
		tableModel = (ActorTableModel) dataModel;
		initialize();
	}
	
	public void initialize() {
		 //InitTable initTable = new InitTable(new ActorTableModel());
        setDefaultRenderer(new Object().getClass(), new InitTableCellRenderer());
        setDefaultRenderer(new Integer(0).getClass(), new InitTableCellRenderer());
        setTransferHandler(new initTableTransferHandler("name"));
        setPreferredScrollableViewportSize(new Dimension(800, 270));
        setFillsViewportHeight(true);
        //initTable.setRowSelectionAllowed(true);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setDragEnabled(true);
        setDropMode(DropMode.INSERT_ROWS);

        JComboBox initTableStateEditor = new JComboBox();
        initTableStateEditor.addItem("Active");
        initTableStateEditor.addItem("Waiting");
        initTableStateEditor.addItem("Disabled");
        initTableStateEditor.addItem("Unconscious");
        initTableStateEditor.addItem("Dead");
        getColumnModel().getColumn(3).setCellEditor(new InitTableComboCellEditor(initTableStateEditor));
        ((DefaultCellEditor) getColumnModel().getColumn(3).getCellEditor()).setClickCountToStart(2);
        JComboBox initTableTypeEditor = new JComboBox();
        initTableTypeEditor.addItem("PC");
        initTableTypeEditor.addItem("Ally");
        initTableTypeEditor.addItem("Enemy");
        initTableTypeEditor.addItem("Neutral");
        initTableTypeEditor.addItem("Special");
        getColumnModel().getColumn(4).setCellEditor(new InitTableComboCellEditor(initTableTypeEditor));
        ((DefaultCellEditor) getColumnModel().getColumn(4).getCellEditor()).setClickCountToStart(2);
                 
        // Table popup menu
        popupMenu = new JPopupMenu();
        popupMenu.add(createMenuItem("Delete", KeyEvent.VK_D));
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
        MousePopupListener popupListener = new MousePopupListener();
        addMouseListener(popupListener);
	}
	
    public void actionPerformed(ActionEvent e) {
    	if (DEBUG) { System.out.println("Received action command " + e.getActionCommand()); }
    	if ("Delete".equals(e.getActionCommand())) { // Delete selected rows
    		Actor[] actors = tableModel.getActors(getSelectedRows());
    		for (int i = 0; i < actors.length; i++) {
    		   	if (DEBUG) { System.out.println("Deleting actor: " + actors[i].Name); }   			
    			tableModel.removeActor(actors[i]); // Remove actor objects in case there are multiple entries in the table (maybe hasted?)
    		}
    	}
    	else if ("Active".equals(e.getActionCommand()) 
    			| "Waiting".equals(e.getActionCommand()) 
    			| "Disabled".equals(e.getActionCommand()) 
    			| "Unconscious".equals(e.getActionCommand()) 
    			| "Dead".equals(e.getActionCommand())) {
    		Actor[] actors = tableModel.getActors(getSelectedRows());
       		for (int i = 0; i < actors.length; i++) {
       			actors[i].State = Actor.ActorState.valueOf(e.getActionCommand());
       			tableModel.fireRefresh(actors[i]);
       		}
    	}
       	else if ("PC".equals(e.getActionCommand()) 
    			| "Ally".equals(e.getActionCommand()) 
    			| "Enemy".equals(e.getActionCommand()) 
    			| "Neutral".equals(e.getActionCommand()) 
    			| "Special".equals(e.getActionCommand())) {
    		Actor[] actors = tableModel.getActors(getSelectedRows());
       		for (int i = 0; i < actors.length; i++) {
       			actors[i].Type = Actor.ActorType.valueOf(e.getActionCommand());
       			tableModel.fireRefresh(actors[i]);
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
			
			if (row == table.getRowCount() -1) {
				c.setBackground(new Color(255,255,255));
				c.setForeground(new Color(128,128,128));
				c.setHorizontalAlignment(SwingConstants.LEFT);
				return c;
			}
			
			Actor a = ((ActorTableModel)table.getModel()).getActor(row);
			if (column == 0 && a.Active) {
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
			c.setForeground(new Color(0,0,0));
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
	    	if (e.isPopupTrigger()) {
	    		popupMenu.show(e.getComponent(), e.getX(), e.getY());
	        }
	    }
	}
	
}


