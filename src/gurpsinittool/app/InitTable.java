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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import gurpsinittool.app.textfield.ParsingField;
import gurpsinittool.app.textfield.ParsingFieldParser;
import gurpsinittool.app.textfield.ParsingFieldParserFactory;
import gurpsinittool.data.Actor;
import gurpsinittool.data.ActorBase.ActorStatus;
import gurpsinittool.data.ActorBase.ActorType;
import gurpsinittool.data.ActorBase.BasicTrait;
import gurpsinittool.data.GameMaster;
import gurpsinittool.ui.ColumnCustomizer;
import gurpsinittool.util.MiscUtil;

@SuppressWarnings("serial")
public class InitTable extends BasicTable {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(InitTable.class.getName());

	private JPopupMenu popupMenu;
	private JPopupMenu headerPopupMenu;
	private Map<ActorStatus, Action> coordinatedStatusMenuItems;
	private InitTableModel tableModel;
	private boolean isInitTable;
	private GameMaster gameMaster;
	private ColumnCustomizer columnCustomizerWindow;
	private Properties propertyBag;
	private String propertyPrefix;
	
	/**
	 * Default Constructor
	 */
	public InitTable(GameMaster gameMaster, boolean isInitTable, Properties propertyBag) {
		super(new InitTableModel(gameMaster));
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		this.gameMaster = gameMaster;
		this.isInitTable = isInitTable;
		this.propertyBag = propertyBag;
		propertyPrefix = "InitTable." + (isInitTable?"init":"noninit");

		setDefaultProperties();
		tableModel = (InitTableModel) dataModel;
		gameMaster.addPropertyChangeListener(tableModel);
		columnCustomizerWindow = new ColumnCustomizer(tableModel);
		columnCustomizerWindow.setLocation(Integer.valueOf(propertyBag.getProperty(propertyPrefix + ".columnCustomizer.location.x")),
                Integer.valueOf(propertyBag.getProperty(propertyPrefix + ".columnCustomizer.location.y")));
		columnCustomizerWindow.setSize(Integer.valueOf(propertyBag.getProperty(propertyPrefix + ".columnCustomizer.size.width")),
        		Integer.valueOf(propertyBag.getProperty(propertyPrefix + ".columnCustomizer.size.height")));
		initialize();
		
		// initialize columns in data model
		ArrayList<String> columnArrayNames = new ArrayList<String>(Arrays.asList(propertyBag.getProperty(propertyPrefix + ".tableModel.columnNames").split(";")));
		tableModel.setColumnList(columnArrayNames);
		initializeColumnHandling();
	}
    
    /**
     * Auto re-size the column widths to optimally fit information
     */
    public void autoSizeColumns() {
    	if (LOG.isLoggable(Level.FINE)) {LOG.fine("Starting."); }
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
       	    if (isInitTable && getColumnName(i).equals("Act")) { // Set the 'Act' column size
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
    private JMenuItem createCoordinatedStatusMenuItem(Action action, final ActorStatus status) {
    	JMenuItem menuItem = new JMenuItem(action);
    	coordinatedStatusMenuItems.put(status, action);
    	return menuItem;
    }
    
    /**
     * Convenience method to create menu items for the table's menus.
     * @param text - Text of the menu item
     * @return
     */
    private void updateCoordinatedStatusMenuItems() {
    	for (Map.Entry<ActorStatus, Action> entry : coordinatedStatusMenuItems.entrySet()) {
    		ActorStatus status = entry.getKey();
    		Action action = entry.getValue();
    		
    		// Determine which way to go
    		boolean allSet = true;
    		boolean allUnset = true;
    		int[] rows = getSelectedRows();
        	Actor[] actors = tableModel.getActors(rows);
        	for (Actor a : actors) {
    			if (a.hasStatus(status))
    				allUnset = false;
    			else
    				allSet = false;    				
    		}
    		if (allSet)
    			action.putValue(Action.SMALL_ICON, new ImageIcon(GITApp.class.getResource("/resources/images/tick.png"), "Tick"));
    		else if (allUnset)
    			action.putValue(Action.SMALL_ICON, null);    		 
    		else
    			action.putValue(Action.SMALL_ICON, new ImageIcon(GITApp.class.getResource("/resources/images/shape_square.png"), "Squar"));    		 
    	}
    	
    }    
 
	private void initialize() {
        setDefaultRenderer(Object.class, new InitTableCellRenderer());
        setDefaultEditor(String.class, new InitTableStringCellEditor(2));
        
        setTransferHandler(new InitTableTransferHandler("name"));
        setPreferredScrollableViewportSize(new Dimension(800, 270));
        setFillsViewportHeight(true);
        //initTable.setRowSelectionAllowed(true);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setDragEnabled(true);
        setDropMode(DropMode.INSERT_ROWS);
        this.setSurrendersFocusOnKeystroke(true); // WHY??? orig false
        // Not doing 'asdf' ENTER, moves to next row
   
        // Header
        getTableHeader().setDefaultRenderer(new InitTableHeaderRenderer(getTableHeader().getDefaultRenderer()));
        
		// Table popup menu
        popupMenu = new JPopupMenu();
        coordinatedStatusMenuItems = new HashMap<ActorStatus, Action>();
        JMenu menuFile = new JMenu("Status");
        menuFile.setMnemonic(KeyEvent.VK_S);
        menuFile.add(createCoordinatedStatusMenuItem(gameMaster.actionCoordinateSelectedStatusAttacking, ActorStatus.Attacking));
        menuFile.add(createCoordinatedStatusMenuItem(gameMaster.actionCoordinateSelectedStatusDisabled, ActorStatus.Disabled));
        menuFile.add(createCoordinatedStatusMenuItem(gameMaster.actionCoordinateSelectedStatusDisarmed, ActorStatus.Disarmed));
        menuFile.add(createCoordinatedStatusMenuItem(gameMaster.actionCoordinateSelectedStatusWaiting, ActorStatus.Waiting));
        menuFile.add(createCoordinatedStatusMenuItem(gameMaster.actionCoordinateSelectedStatusMentalStun, ActorStatus.StunMental));
        menuFile.add(createCoordinatedStatusMenuItem(gameMaster.actionCoordinateSelectedStatusPhysicalStun, ActorStatus.StunPhys));
        menuFile.add(createCoordinatedStatusMenuItem(gameMaster.actionCoordinateSelectedStatusRecoveringStun, ActorStatus.StunRecovr));
        menuFile.add(createCoordinatedStatusMenuItem(gameMaster.actionCoordinateSelectedStatusUnconscious, ActorStatus.Unconscious));
        menuFile.add(createCoordinatedStatusMenuItem(gameMaster.actionCoordinateSelectedStatusDead, ActorStatus.Dead));
        popupMenu.add(menuFile);
        
        menuFile = new JMenu("Posture");
        menuFile.setMnemonic(KeyEvent.VK_S);
        menuFile.add(new JMenuItem(gameMaster.actionPostureStanding));
        menuFile.add(new JMenuItem(gameMaster.actionPostureKneeling));
        menuFile.add(new JMenuItem(gameMaster.actionPostureProne));
        popupMenu.add(menuFile);
        
        menuFile = new JMenu("Type");
        menuFile.setMnemonic(KeyEvent.VK_T);
        menuFile.add(new JMenuItem(gameMaster.actionSetSelectedTypePC));
        menuFile.add(new JMenuItem(gameMaster.actionSetSelectedTypeAlly));
        menuFile.add(new JMenuItem(gameMaster.actionSetSelectedTypeNeutral));
        menuFile.add(new JMenuItem(gameMaster.actionSetSelectedTypeEnemy));
        menuFile.add(new JMenuItem(gameMaster.actionSetSelectedTypeSpecial));
        popupMenu.add(menuFile);
 
        // Core functionality
        if (isInitTable) { popupMenu.add(new JMenuItem(gameMaster.actionSetSelectedActorActive)); }
        if (isInitTable) { popupMenu.add(new JMenuItem(gameMaster.actionAttack)); }
        if (isInitTable) { popupMenu.add(new JMenuItem(gameMaster.actionDefend)); }
        popupMenu.add(new JMenuItem(gameMaster.actionResetSelectedActors));
        popupMenu.add(new JMenuItem(gameMaster.actionDeleteSelectedActors));
        if (isInitTable) { popupMenu.add(new JMenuItem(gameMaster.actionTagSelectedActors)); }
        if (isInitTable) { popupMenu.add(new JMenuItem(gameMaster.actionRemoveTagSelectedActors)); }
        addMouseListener(new MousePopupListener(false));
        
        // Header popup menu
        headerPopupMenu = new JPopupMenu();
        headerPopupMenu.add(new AbstractAction("Customize Columns") {
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				showColumnCustomizer();
			}
		});
    	getTableHeader().addMouseListener(new MousePopupListener(true));
    	
		// Setup actor status editor
        JComboBox<ActorStatus> initTableStateEditor = new JComboBox<ActorStatus>();
        for (Actor.ActorStatus a : Actor.ActorStatus.values()) {
        	initTableStateEditor.addItem(a);
        }
	}
	
	/**
	 * Perform customizations to handling for each column
	 */
	private void initializeColumnHandling() {
		int actColumnIndex = -1;
		for (int i = 0; i < this.getColumnCount(); i++) {
			String columnName = getColumnName(i);
			// Special handling
			if ("Act".equals(columnName)) {
		        getColumnModel().getColumn(i).setResizable(false); // Freeze size
		        actColumnIndex = i;
			} else if (columnName.equals("Speed")) {
				getColumnModel().getColumn(i).setCellEditor(new InitTableCellEditor(ParsingFieldParserFactory.FloatParser()));
			} else if (columnName.equals("Injury") || columnName.equals("Fatigue")) {
				getColumnModel().getColumn(i).setCellEditor(new InitTableDamageCellEditor());			    
			} else if (columnName.equals("Status")) {
		        getColumnModel().getColumn(i).setCellEditor(new InitTableStatusListCellEditor());
			} else if (columnName.equals("Type")) {
				getColumnModel().getColumn(i).setCellEditor(new InitTableTypeListCellEditor());
			} else if (columnName.equals("Name")) {
				getColumnModel().getColumn(i).setCellEditor(new InitTableStringCellEditor(1));
			} else if (Actor.isBasicTrait(columnName)) { // All other basic traits are integers
				getColumnModel().getColumn(i).setCellEditor(new InitTableCellEditor(ParsingFieldParserFactory.IntegerParser()));
			} else {
				// use default: InitTableStringCellEditor(2)
			}
		}
		
        // Don't display 'Act' column in the group manager table
        // Removing this column changes the indexes in the column model only - be careful when using getColumn(i)
        if(!isInitTable && actColumnIndex != -1) { getColumnModel().removeColumn(getColumnModel().getColumn(actColumnIndex)); }
	}
	
	/**
	 * Return whether this table is the initTable
	 * @return : true is this table is the initTable, false if it is the groupTable
	 */
	public boolean isInitTable() {
		return isInitTable;
	}
	
	public GameMaster getGameMaster() {
		return gameMaster;
	}
	
	/**
	 * Access method for the actorTableModel.
	 * @return The ActorTableModel underlying the table.
	 */
	public InitTableModel getActorTableModel() {
		return tableModel;
	}
	
	/**
	 * Retrieve the currently selected Actor
	 * @return The currently selected Actor
	 */
	public Actor getSelectedActor() {
		int index = getSelectedRow();
		if (index < 0 || index >= tableModel.getRowCount() )
			return null;
		return tableModel.getActor(index);
	}
	
	@Override
	public int[] getSelectedRows() { // Exclude last row if selected
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
	
	public Actor[] getSelectedActors() {
		int[] rows = getSelectedRows();
		return tableModel.getActors(rows);
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

	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		if (LOG.isLoggable(Level.FINE)) {LOG.fine("Got event: " + e + ", type=" + e.getType()); }
		if (e.getType() == TableModelEvent.UPDATE && e.getFirstRow() == TableModelEvent.HEADER_ROW 
				&& e.getColumn() == TableModelEvent.ALL_COLUMNS) {
			if (LOG.isLoggable(Level.FINE)) {LOG.fine("    Detected data structure change"); }
			initializeColumnHandling();
			autoSizeColumns();
		}
	
	}
	/**
	 * Modify a component to match it's conditions
	 * @param component : the component to modify
	 * @param actor
	 */
	private static void formatComponentAlignment(JLabel c, Actor a, String columnName) {
		c.setHorizontalAlignment(SwingConstants.LEFT);
		c.setHorizontalTextPosition(JLabel.LEADING);

		if (a.hasStatus(ActorStatus.Waiting)) {
			c.setHorizontalAlignment(SwingConstants.RIGHT);
		}

		if (columnName.equals("Act")) {
			c.setHorizontalAlignment(SwingConstants.RIGHT);
		}
	}
	
	/**
	 * Modify a component to match it's conditions
	 * @param component : the component to modify
	 * @param actor
	 */
	private static void formatComponentAlignment(JTextField c, Actor a) {
		c.setHorizontalAlignment(SwingConstants.LEFT);	
		if (a.hasStatus(ActorStatus.Waiting)) {
			c.setHorizontalAlignment(SwingConstants.RIGHT);
		}
	}

	/**
	 * Modify a component to match it's conditions
	 * @param component : the component to modify
	 * @param actor : the underlying actor object for the current row
	 */
	private static void formatComponentColor(JComponent c, Actor a, boolean isSelected, boolean isEditable) {

		c.setForeground(Color.black);
		Color paleColor = new Color(255,255,255);
		Color greyColor = Color.gray;
		Color backgroundColor = new Color(255,255,255);
		switch (a.getType()) {
		case PC:
			backgroundColor = new Color(128,255,128);
			break;
		case Ally:
			backgroundColor = new Color(128,128,255);
			break;
		case Enemy:
			backgroundColor = new Color(255,128,128);
			break;
		case Neutral:
			backgroundColor = new Color(128,128,128);
			break;
		case Special:
			backgroundColor = new Color(255,128,255);
			break;
		}
		
		if (!isSelected) {
			backgroundColor = MiscUtil.setAlpha(backgroundColor, 150);
			backgroundColor = MiscUtil.blend(backgroundColor, paleColor);
		}
		if (!isEditable) {
			greyColor = MiscUtil.setAlpha(greyColor, 75);
			backgroundColor = MiscUtil.blend(backgroundColor, greyColor);
		}
		c.setBackground(backgroundColor);
		
		if (a.hasStatus(ActorStatus.Unconscious)
				|| a.hasStatus(ActorStatus.Disabled)
				|| a.hasStatus(ActorStatus.Dead)) {
			c.setForeground(Color.gray);
		}
	}
   
	private void formatEditField(JTextField c, boolean isSelected, boolean isEditable, int row) {
		if (row == getRowCount() -1) {
			c.setBackground(Color.white);
			c.setForeground(Color.gray);
			c.setHorizontalAlignment(SwingConstants.LEFT);
			return;
		}
		
		Actor a = tableModel.getActor(row);				
		formatComponentColor((JComponent)c, a, isSelected, isEditable);
		formatComponentAlignment(c, a);
		c.setBorder(new LineBorder(Color.white));
	}
	
	 /**
	  * Set default properties if they are not already defined.
	  */
	 private void setDefaultProperties() {
		 if (!propertyBag.containsKey(propertyPrefix + ".tableModel.columnNames")) {
			 propertyBag.setProperty(propertyPrefix + ".tableModel.columnNames", String.join(";", ColumnCustomizer.DEFAULT_COLUMNS)); }
		 // columnCustomizerWindow
		 if (!propertyBag.containsKey(propertyPrefix + ".columnCustomizer.location.x")) {
			 propertyBag.setProperty(propertyPrefix + ".columnCustomizer.location.x", "110"); }
		 if (!propertyBag.containsKey(propertyPrefix + ".columnCustomizer.location.y")) {
			 propertyBag.setProperty(propertyPrefix + ".columnCustomizer.location.y", "110"); }
		 if (!propertyBag.containsKey(propertyPrefix + ".columnCustomizer.size.width")) {
			 propertyBag.setProperty(propertyPrefix + ".columnCustomizer.size.width",  String.valueOf(new ColumnCustomizer(null).getPreferredSize().width)); }
		 if (!propertyBag.containsKey(propertyPrefix + ".columnCustomizer.size.height")) {
			 propertyBag.setProperty(propertyPrefix + ".columnCustomizer.size.height",  String.valueOf(new ColumnCustomizer(null).getPreferredSize().height)); }
	 }
	 
	 /**
	  * Update all the store-able properties to their current values
	  */
	 public void updateProperties() {
		 // Kept up-to-date with event listeners
		 propertyBag.setProperty(propertyPrefix + ".tableModel.columnNames", String.join(";", tableModel.getColumnNames()));
		 // columnCustomizerWindow
		 propertyBag.setProperty(propertyPrefix + ".columnCustomizer.location.x", String.valueOf(columnCustomizerWindow.getLocation().x));
		 propertyBag.setProperty(propertyPrefix + ".columnCustomizer.location.y", String.valueOf(columnCustomizerWindow.getLocation().y));
		 propertyBag.setProperty(propertyPrefix + ".columnCustomizer.size.width", String.valueOf(columnCustomizerWindow.getSize().width));
		 propertyBag.setProperty(propertyPrefix + ".columnCustomizer.size.height", String.valueOf(columnCustomizerWindow.getSize().height));
	 }
	 
	 /**
	  * Show the column customization window
	  */
	 public void showColumnCustomizer() {
		 columnCustomizerWindow.setVisible(true);
	 }
	 
	/**
	 * Renderer to deal with all the customizations based on Actor state/type/etc.
	 * Assumes that the table model being used is an ActorTableModel.
	 * @author dsmall
	 *
	 */
	class InitTableHeaderRenderer implements TableCellRenderer {
		private TableCellRenderer base;
		
		public InitTableHeaderRenderer (TableCellRenderer base) {
			this.base = base;
		}
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			
			JLabel c = (JLabel) base.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			String columnName = getColumnName(column);
			if (columnName.equals("Injury") || columnName.equals("Fatigue")) {
				c.setForeground(Color.red);
			}		
			return c;
		}
	}
	
	/**
	 * Renderer to deal with all the customizations based on Actor state/type/etc.
	 * Assumes that the table model being used is an ActorTableModel.
	 * @author dsmall
	 *
	 */
	class InitTableCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			
			JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			String columnName = getColumnName(column); // Get the name of the column from the table, which accounts for hidden columns
			c.setIcon(new ImageIcon()); // Clear out any icon
			if (row == table.getRowCount() -1) {
				c.setBackground(Color.white);
				c.setForeground(Color.gray);
				c.setHorizontalAlignment(SwingConstants.LEFT);
				if (columnName.equals("Name"))
					c.setText("new...");
				else if (columnName.equals("Status"))
					c.setText("");
				return c;
			}
			
			// Basic Formatting
			Actor a = tableModel.getActor(row);
			formatComponentColor((JComponent)c, a, isSelected, table.isCellEditable(row, column));
			formatComponentAlignment(c, a, columnName);
			if (columnName.equals("Act") && (gameMaster.getActiveActor() == row)) {
				c.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/go.png"), "Current Actor"));  
			} else if (columnName.equals("Status")) {
				c.setText(a.getStatusesString());
			}			

			// Check parsing: at some point may want to create 'ParsingComponent' which has label and textfield versions
			if (columnName.equals("Speed")) {				
				if (!ParsingFieldParserFactory.FloatParser().parseIsValid(c.getText()))
					c.setForeground(Color.red);
			} else if (columnName.equals("Move") || columnName.equals("HT") || columnName.equals("HP") || columnName.equals("Injury") || columnName.equals("FP") || columnName.equals("Fatigue")) {
				if (!ParsingFieldParserFactory.IntegerParser().parseIsValid(c.getText()))
					c.setForeground(Color.red);
			}
			
			// Special formatting
			// Custom rendering for various columns
			if (columnName.equals("Move") || columnName.equals("Dodge")) {				
				int injury = a.getTraitValueInt(BasicTrait.Injury);
				int fatigue = a.getTraitValueInt(BasicTrait.Fatigue);
				int hitPoints = a.getTraitValueInt(BasicTrait.HP);
				int fatiguePoints = a.getTraitValueInt(BasicTrait.FP);
				
				if (injury > 2*hitPoints/3 || fatigue > 2*fatiguePoints/3) {					
					int currentValue = a.getTraitValueInt(columnName);
					int newValue;
					if (injury > 2*hitPoints/3 && fatigue > 2*fatiguePoints/3) {
						newValue = (int) Math.ceil((double)currentValue/4);
						c.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/exclamation.png"), "Greatly reduced state"));
					} else {
						newValue = (int) Math.ceil((double)currentValue/2);
						c.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/error.png"), "Reduced state"));
					}
					c.setText(newValue + " (" + c.getText() + ")");
					MiscUtil.setLabelBold(c);
				}
			} else if (columnName.equals("HT")) {
				int injury = a.getTraitValueInt(BasicTrait.Injury);
				int hitPoints = a.getTraitValueInt(BasicTrait.HP);
				hitPoints = Math.max(hitPoints, 1); // Minimum HP = 1 for calc purposes
				if (injury >= hitPoints) {
					int penalty = (int) (-1*(Math.floor((double)injury/hitPoints)-1));
					if (penalty < 0) {
						MiscUtil.setLabelBold(c);
						c.setText(c.getText() + " [" + penalty + "]");
					}
					c.setIcon(new ImageIcon(GITApp.class.getResource("/resources/images/error.png"), "Must check to stay conscious"));
				}
			} else if (Actor.isCustomTrait(columnName)) {
				if (a.hasTrait(columnName)) {
					String traitValue = a.getTraitValue(columnName);
					if (traitValue.equals("")) {
						c.setText("<html><em>[yes]</em></html>");
					}
				}
			}
			
			return c;
		}
	}
	
	/**
	 * Inner class to provide CellEditor functionality
	 * Allow modification of the text cell editor
	 * @author dsmall
	 */
	class InitTableCellEditor extends DefaultCellEditor {
		
		/**
		 * Super does not define default constructor, so must define one.
		 * @param comboBox
		 */
		public InitTableCellEditor(ParsingFieldParser parser) {
			super(new ParsingField(parser));
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			ParsingField c = (ParsingField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
			if (isSelected)
			    c.selectAll();
			
			formatEditField(c, isSelected, table.isCellEditable(row, column), row);
			c.refreshForeground();
			return c;
		}
	}
	
	/**
	 * Inner class to provide CellEditor functionality
	 * Allow modification of the text cell editor
	 * @author dsmall
	 */
	class InitTableStringCellEditor extends DefaultCellEditor implements FocusListener {
		
		// The original actor name that the editor started with
		private String actorName;
		Actor actor;
		
		/**
		 * Super does not define default constructor, so must define one.
		 * @param comboBox
		 */
		public InitTableStringCellEditor(int clicksToStart) {
			super(new JTextField());
			setClickCountToStart(clicksToStart);
			editorComponent.addFocusListener(this);
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			JTextField c = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
			actor = ((InitTableModel)table.getModel()).getActor(row);
			actorName = actor.getTraitValue(BasicTrait.Name);
			formatEditField(c, isSelected, table.isCellEditable(row, column), row);
			return c;
		}
		
		@Override
	    public void focusGained(FocusEvent evt) {
			if (LOG.isLoggable(Level.FINE)) {LOG.fine("Focus gained on " + evt.toString()); }
	    	// check for modifications in the base Actor
	    	// This hack is only needed if setClickCountToStart = 1, since in that 
	    	// case the table is not updated in time when there is a modification on focus lost
	    	String selctedActorName = actor.getTraitValue(BasicTrait.Name);
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
			if (LOG.isLoggable(Level.FINE)) {LOG.fine("Focus lost on " + evt.toString()); }
			stopCellEditing();
		}
		
	}
	
	/**
	 * Inner class to provide CellEditor functionality
	 * Allow modification of the damage cell editor
	 * @author dsmall
	 */
	class InitTableDamageCellEditor extends DefaultCellEditor {
		ParsingField tf;
		
		public InitTableDamageCellEditor() {
			super(new ParsingField());
			DamageDocumentFilter df = new DamageDocumentFilter();
			tf = (ParsingField)getComponent();
			tf.setParser(new DamageFieldParser());
			tf.addFocusListener(df);
			((AbstractDocument) tf.getDocument()).setDocumentFilter(df);
		}
		
		//Make sure the value remains an Integer.
		@Override
	    public Object getCellEditorValue() {
			return tf.getParsedValue();	     
	    }
	    
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			tf = (ParsingField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
			formatEditField(tf, isSelected, table.isCellEditable(row, column), row);
			tf.refreshForeground();
			return tf;
		}
		
		private class DamageFieldParser extends ParsingFieldParser {
			Pattern pattern = Pattern.compile("^\\s*(-?\\d+)\\s*([\\+-])\\s*(-?\\d+)([\\s+-].*)?$");
			
			@Override
			public boolean parseIsValid(String text) {
				String result = internalParse(text);
				try {
		        	Integer.valueOf(result);
		        	return true;
		        } catch (NumberFormatException e) {
		        	return false;
		        }
			}

			@Override
			public Object parseText(String text) {	
				return internalParse(text);
			}	
			
			// Operates by reference, modifies working string and reports success
			private String internalParse(String working) {
				working = working.trim();
				Matcher matcher = pattern.matcher(working);
				while (matcher.matches()) {
					Integer first = Integer.valueOf(matcher.group(1));
					String operator = matcher.group(2);
					Integer second = Integer.valueOf(matcher.group(3));
					if (operator.equals("+")) { first += second; }
					else { first -= second; }
					// Check if we need to continue
					String extra = matcher.group(4);
					if (extra != null) {
						working = first.toString() + extra;
						matcher = pattern.matcher(working);
					} else {
						return first.toString();
					}					
				}
				return working;				
			}
		}
		
		private class DamageDocumentFilter extends DocumentFilter implements FocusListener {
			
			boolean startingNew = true;
			boolean firstEdit = true; // For some reason, the first edit is different than the rest
			boolean hasFocus = false;
			
			@Override
			public void remove(FilterBypass fb, int offs, int length) throws BadLocationException {
				if (LOG.isLoggable(Level.FINER)) {LOG.finer("Remove: offs: " + offs + ", len:" + length + ".");}
				startingNew = false;
				super.remove(fb, offs, length);
			}
			
			@Override
			public void insertString(FilterBypass fb, int offs, String str, javax.swing.text.AttributeSet a) throws BadLocationException {
				if (LOG.isLoggable(Level.FINER)) {LOG.finer("Insert:" + str + ".");}
				
				//if (str.matches("[\\d\\+-]+")) {
					super.insertString(fb, offs, str, a);
				//}
			}
			
			@Override
			public void replace(FilterBypass fb, int offs, int length, String str, javax.swing.text.AttributeSet a) throws BadLocationException {
				if (LOG.isLoggable(Level.FINER)) {LOG.finer("Replace: '" + str + "', Offs=" + offs + ", Length=" + length + ".");}
				
				//if (str.matches("[\\d\\+-]+")) {
					if (hasFocus || firstEdit || (length > 0)) {
						firstEdit = false;
						startingNew = true;
						super.replace(fb, offs, length, str, a);
					} else if (startingNew && str.matches("\\d")) {
						startingNew = false;
						super.replace(fb, offs, 0, "+", null);
						super.replace(fb, offs+1, 0, str, a);
					} else if (startingNew && str.matches("=")) {
						startingNew = false;
						super.replace(fb, offs, 0, "+", null);
					} else {
						startingNew = false;
						super.replace(fb, offs, 0, str, a);
					}
				//}
			}
	
			@Override
			public void focusGained(FocusEvent arg0) {
				hasFocus = true;
				if (LOG.isLoggable(Level.FINE)) {LOG.fine("TextField focus gained.");}
			}
	
			@Override
			public void focusLost(FocusEvent arg0) {
				hasFocus = false;
				if (LOG.isLoggable(Level.FINE)) {LOG.fine("TextField focus lost.");}
			}
		}
	}

	/**
	 * Inner class to provide CellEditor functionality
	 * Main purpose is to synchronize the combo box selected item with the current value in the cell
	 * @author dsmall
	 *
	 */
	class InitTableStatusListCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener, FocusListener, ComponentListener  {

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
	 * An inner class to check whether mouse events are the pop-up trigger
	 */
	class MousePopupListener extends MouseAdapter {
	    	
		private boolean isHeader;
		
		public MousePopupListener(boolean isHeader) { this.isHeader = isHeader; }
	    public void mousePressed(MouseEvent e) { checkPopup(e); }
	    public void mouseClicked(MouseEvent e) { checkPopup(e); checkResize(e);}
	    public void mouseReleased(MouseEvent e) { checkPopup(e); }
	 
	    private void checkPopup(MouseEvent e) {
	    	if (isHeader) {
	    		if (e.isPopupTrigger()) {
	    			headerPopupMenu.show(e.getComponent(), e.getX(), e.getY());
	    		}
	    	} else {
		    	if (e.isPopupTrigger() & (getSelectedRows().length > 0)) {
		    		updateCoordinatedStatusMenuItems();
		    		popupMenu.show(e.getComponent(), e.getX(), e.getY());
		        }
	    	}
	    }
	    
	    private void checkResize(MouseEvent e) {
	    	// See if it's a double click
	    	if (e.getClickCount() == 2) {
		        // Determine if cursor is the resize cursor
		        Cursor currentCursor = getTableHeader().getCursor();
		        if (LOG.isLoggable(Level.FINER)) {LOG.finer("Double-click detected. Type is " + currentCursor.getType() + " (" + currentCursor.toString() + ")");}
				
				if (currentCursor.getType() == Cursor.E_RESIZE_CURSOR) {
					autoSizeColumns();
					if (LOG.isLoggable(Level.FINER)) {LOG.finer("Auto-sizing columns.");}
				}
	    	}
	    }
	}
	
	abstract public class TableColumnAction extends AbstractAction {
		private TableColumn column;
		private String name;
		public TableColumnAction(TableColumn column, String text) {
			super(text);
			putValue(SELECTED_KEY, true);
			this.column = column;
			this.name = text;
		}
		public void actionPerformed(ActionEvent arg0) {
			JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) arg0.getSource();
			if (menuItem.isSelected()) {
				getColumnModel().addColumn(column);
			} else {
				getColumnModel().removeColumn(column);
			}
		}
	}
}

