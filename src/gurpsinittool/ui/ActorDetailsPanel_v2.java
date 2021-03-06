/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ActorDetailsPanel.java
 *
 * Created on Jul 19, 2009, 4:46:10 PM
 */

package gurpsinittool.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import gurpsinittool.app.*;
import gurpsinittool.app.textfield.ParsingField;
import gurpsinittool.app.textfield.ParsingFieldParserFactory;
import gurpsinittool.data.Actor;
import gurpsinittool.data.ActorBase.ActorStatus;
import gurpsinittool.data.ActorBase.ActorType;
import gurpsinittool.data.ActorBase.BasicTrait;
import gurpsinittool.data.StrengthTables;
//import gurpsinittool.data.GameMaster;


/**
 *
 * @author dcsmall
 */
@SuppressWarnings("serial")
public class ActorDetailsPanel_v2 extends javax.swing.JPanel 
	implements PropertyChangeListener{
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(ActorDetailsPanel_v2.class.getName());
	
	private boolean isInit; // Whether we are attached to the init Table, or just the group table
	private int actorLoading = 0; // Block for property updates while in the middle of an update
	private Actor actor;
	private AttackTableModel attackTableModel;
	private TraitTableModel traitTableModel;
	private TraitTableModel tempTableModel;
	private DefaultTableModel strengthTableModel;
	private PopupStatusEditList popupStatusEditList;
	
	// GameMaster object
	//public GameMaster gameMaster;
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAttack;
    private javax.swing.JButton addTrait;
    private javax.swing.JPanel attacks;
    private javax.swing.JTable attacksTable;
    private gurpsinittool.app.textfield.ParsingField block;
    private javax.swing.JButton defaultAttack;
    private gurpsinittool.app.textfield.ParsingField dodge;
    private gurpsinittool.app.textfield.ParsingField dr;
    private gurpsinittool.app.textfield.ParsingField dx;
    private javax.swing.JButton executeAttack;
    private gurpsinittool.app.textfield.ParsingField fatigue;
    private gurpsinittool.app.textfield.ParsingField fp;
    private gurpsinittool.app.textfield.ParsingField hp;
    private gurpsinittool.app.textfield.ParsingField ht;
    private gurpsinittool.app.textfield.ParsingField injury;
    private gurpsinittool.app.textfield.ParsingField iq;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private gurpsinittool.app.textfield.ParsingField move;
    private javax.swing.JTextField name;
    private javax.swing.JScrollPane noteScrollPane;
    private javax.swing.JSeparator noteSeparator;
    private javax.swing.JTextArea notes;
    private gurpsinittool.app.textfield.ParsingField parry;
    private gurpsinittool.app.textfield.ParsingField per;
    private javax.swing.JButton refreshTempTable;
    private javax.swing.JButton removeAttack;
    private javax.swing.JButton removeTrait;
    private javax.swing.JButton resizeAttackTable;
    private javax.swing.JButton resizeTempTable;
    private javax.swing.JButton resizeTraitTable;
    private gurpsinittool.app.textfield.ParsingField shieldDB;
    private gurpsinittool.app.textfield.ParsingField shieldDR;
    private gurpsinittool.app.textfield.ParsingField shieldHP;
    private javax.swing.JPanel shieldPanel;
    private javax.swing.JCheckBox showStrengthTablesCheckBox;
    private javax.swing.JCheckBox showTempCheckBox;
    private gurpsinittool.app.textfield.ParsingField sm;
    private gurpsinittool.app.textfield.ParsingField speed;
    private gurpsinittool.app.textfield.ParsingField st;
    private javax.swing.JButton statusButton;
    private javax.swing.JTable strengthTable;
    private javax.swing.JPanel strengthTablesPanel;
    private javax.swing.JPanel tempPanel;
    private javax.swing.JTable tempTable;
    private javax.swing.JPanel traits;
    private javax.swing.JTable traitsTable;
    private javax.swing.JComboBox type;
    private gurpsinittool.app.textfield.ParsingField will;
    // End of variables declaration//GEN-END:variables
    

    /** Creates new form ActorDetailsPanel */
    public ActorDetailsPanel_v2(boolean isInit) {
    	this.isInit = isInit;
    	attackTableModel = new AttackTableModel();
    	traitTableModel = new TraitTableModel(false);
    	tempTableModel = new TraitTableModel(true);
    	popupStatusEditList = new PopupStatusEditList();
    	
        initComponents();
    	strengthTableModel = (DefaultTableModel) strengthTable.getModel();

        attacksTable.setDefaultRenderer(String.class, attackTableModel.new AttackTableCellRenderer());
        attacksTable.setDefaultRenderer(Integer.class, attackTableModel.new AttackTableCellRenderer());
        attacksTable.getColumnModel().getColumn(AttackTableModel.columns.Damage.ordinal()).setCellEditor(attackTableModel.new AttackTableCellEditor(ParsingFieldParserFactory.DamageExpressionParser()));

        showTempCheckBox.setSelected(false); showTempCheckBoxActionPerformed(null); // Start hidden
        showStrengthTablesCheckBox.setSelected(false); showStrengthTablesCheckBoxActionPerformed(null); // Start hidden
        disableAndClearPanel();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        attacks = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        addAttack = new javax.swing.JButton();
        removeAttack = new javax.swing.JButton();
        defaultAttack = new javax.swing.JButton();
        executeAttack = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        resizeAttackTable = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        attacksTable = new BasicTable();
        type = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        noteScrollPane = new javax.swing.JScrollPane();
        notes = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        shieldPanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        shieldDB = new gurpsinittool.app.textfield.ParsingField();
        shieldDR = new gurpsinittool.app.textfield.ParsingField();
        shieldHP = new gurpsinittool.app.textfield.ParsingField();
        jLabel8 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        traits = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        traitsTable = new BasicTable();
        jToolBar4 = new javax.swing.JToolBar();
        addTrait = new javax.swing.JButton();
        removeTrait = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        resizeTraitTable = new javax.swing.JButton();
        showTempCheckBox = new javax.swing.JCheckBox();
        tempPanel = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tempTable = new javax.swing.JTable();
        jToolBar5 = new javax.swing.JToolBar();
        refreshTempTable = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        resizeTempTable = new javax.swing.JButton();
        st = new gurpsinittool.app.textfield.ParsingField();
        dr = new gurpsinittool.app.textfield.ParsingField();
        dx = new gurpsinittool.app.textfield.ParsingField();
        iq = new gurpsinittool.app.textfield.ParsingField();
        ht = new gurpsinittool.app.textfield.ParsingField();
        hp = new gurpsinittool.app.textfield.ParsingField();
        will = new gurpsinittool.app.textfield.ParsingField();
        per = new gurpsinittool.app.textfield.ParsingField();
        fp = new gurpsinittool.app.textfield.ParsingField();
        speed = new gurpsinittool.app.textfield.ParsingField();
        sm = new gurpsinittool.app.textfield.ParsingField();
        move = new gurpsinittool.app.textfield.ParsingField();
        dodge = new gurpsinittool.app.textfield.ParsingField();
        parry = new gurpsinittool.app.textfield.ParsingField();
        block = new gurpsinittool.app.textfield.ParsingField();
        fatigue = new gurpsinittool.app.textfield.ParsingField();
        injury = new gurpsinittool.app.textfield.ParsingField();
        strengthTablesPanel = new javax.swing.JPanel();
        strengthTable = new javax.swing.JTable();
        showStrengthTablesCheckBox = new javax.swing.JCheckBox();
        noteSeparator = new javax.swing.JSeparator();
        statusButton = new javax.swing.JButton();

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel2.setText("Status:");

        jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel3.setText("Type:");

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel4.setText("HP:");

        jLabel5.setFont(jLabel5.getFont().deriveFont(jLabel5.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel5.setText("Injury:");

        attacks.setBorder(javax.swing.BorderFactory.createTitledBorder("Attacks"));

        jToolBar1.setBorder(null);
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        addAttack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/add.png"))); // NOI18N
        addAttack.setToolTipText("Add new attack");
        addAttack.setFocusable(false);
        addAttack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addAttack.setIconTextGap(1);
        addAttack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addAttack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAttackActionPerformed(evt);
            }
        });
        jToolBar1.add(addAttack);

        removeAttack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/delete.png"))); // NOI18N
        removeAttack.setToolTipText("Remove selected attack");
        removeAttack.setFocusable(false);
        removeAttack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeAttack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeAttack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAttackActionPerformed(evt);
            }
        });
        jToolBar1.add(removeAttack);

        defaultAttack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/wrench_orange.png"))); // NOI18N
        defaultAttack.setToolTipText("Set default attack");
        defaultAttack.setFocusable(false);
        defaultAttack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        defaultAttack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        defaultAttack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultAttackActionPerformed(evt);
            }
        });
        jToolBar1.add(defaultAttack);

        executeAttack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/sword.png"))); // NOI18N
        executeAttack.setToolTipText("Execute selected attack");
        executeAttack.setFocusable(false);
        executeAttack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        executeAttack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        executeAttack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeAttackActionPerformed(evt);
            }
        });
        jToolBar1.add(executeAttack);
        jToolBar1.add(jSeparator2);

        resizeAttackTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/script_code.png"))); // NOI18N
        resizeAttackTable.setToolTipText("Atuo-size columns");
        resizeAttackTable.setFocusable(false);
        resizeAttackTable.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resizeAttackTable.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        resizeAttackTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resizeAttackTableActionPerformed(evt);
            }
        });
        jToolBar1.add(resizeAttackTable);

        attacksTable.setAutoCreateRowSorter(true);
        attacksTable.setModel(attackTableModel);
        attacksTable.setAutoscrolls(false);
        attacksTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(attacksTable);

        javax.swing.GroupLayout attacksLayout = new javax.swing.GroupLayout(attacks);
        attacks.setLayout(attacksLayout);
        attacksLayout.setHorizontalGroup(
            attacksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        attacksLayout.setVerticalGroup(
            attacksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attacksLayout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
        );

        type.setModel(new DefaultComboBoxModel(Actor.ActorType.values()));
        type.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        type.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeActionPerformed(evt);
            }
        });

        jLabel7.setFont(jLabel7.getFont().deriveFont(jLabel7.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel7.setText("FP:");

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel1.setText("Notes:");

        noteScrollPane.setPreferredSize(new java.awt.Dimension(50, 50));
        noteScrollPane.setRequestFocusEnabled(false);

        notes.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);
        notes.setName("Notes"); // NOI18N
        notes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });
        noteScrollPane.setViewportView(notes);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("HT:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Dodge");

        jLabel6.setFont(jLabel6.getFont().deriveFont(jLabel6.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel6.setText("Fatigue:");

        name.setBackground(new java.awt.Color(236, 233, 216));
        name.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        name.setText("name");
        name.setBorder(null);
        name.setName("Name"); // NOI18N
        name.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Parry");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Block");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("DR");

        shieldPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Shield"));
        shieldPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("DB:");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("DR:");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("HP:");

        shieldDB.setName("Shield_DB"); // NOI18N
        shieldDB.setParser(ParsingFieldParserFactory.IntegerParser());
        shieldDB.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        shieldDR.setName("Shield_DR"); // NOI18N
        shieldDR.setParser(ParsingFieldParserFactory.IntegerParser());
        shieldDR.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        shieldHP.setName("Shield_HP"); // NOI18N
        shieldHP.setParser(ParsingFieldParserFactory.IntegerParser());
        shieldHP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        javax.swing.GroupLayout shieldPanelLayout = new javax.swing.GroupLayout(shieldPanel);
        shieldPanel.setLayout(shieldPanelLayout);
        shieldPanelLayout.setHorizontalGroup(
            shieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shieldPanelLayout.createSequentialGroup()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shieldDB, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shieldDR, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shieldHP, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        shieldPanelLayout.setVerticalGroup(
            shieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shieldPanelLayout.createSequentialGroup()
                .addGroup(shieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(shieldDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(shieldDR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(shieldHP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jLabel8.setFont(jLabel8.getFont().deriveFont(jLabel8.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel8.setText("ST:");

        jLabel14.setFont(jLabel14.getFont().deriveFont(jLabel14.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel14.setText("DX:");

        jLabel21.setFont(jLabel21.getFont().deriveFont(jLabel21.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel21.setText("Will:");

        jLabel22.setFont(jLabel22.getFont().deriveFont(jLabel22.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel22.setText("IQ:");

        jLabel23.setFont(jLabel23.getFont().deriveFont(jLabel23.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel23.setText("Per:");

        jLabel24.setFont(jLabel24.getFont().deriveFont(jLabel24.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel24.setText("Move:");

        jLabel25.setFont(jLabel25.getFont().deriveFont(jLabel25.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel25.setText("Speed:");

        jLabel26.setFont(jLabel26.getFont().deriveFont(jLabel26.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel26.setText("SM:");

        traits.setBorder(javax.swing.BorderFactory.createTitledBorder("Traits"));

        traitsTable.setAutoCreateRowSorter(true);
        traitsTable.setModel(traitTableModel);
        jScrollPane5.setViewportView(traitsTable);

        jToolBar4.setBorder(null);
        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);

        addTrait.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/add.png"))); // NOI18N
        addTrait.setToolTipText("Add new trait");
        addTrait.setFocusable(false);
        addTrait.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addTrait.setIconTextGap(1);
        addTrait.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addTrait.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTraitActionPerformed(evt);
            }
        });
        jToolBar4.add(addTrait);

        removeTrait.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/delete.png"))); // NOI18N
        removeTrait.setToolTipText("Remove selected trait");
        removeTrait.setFocusable(false);
        removeTrait.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeTrait.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeTrait.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeTraitActionPerformed(evt);
            }
        });
        jToolBar4.add(removeTrait);
        jToolBar4.add(jSeparator5);

        resizeTraitTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/script_code.png"))); // NOI18N
        resizeTraitTable.setToolTipText("Auto-size columns");
        resizeTraitTable.setFocusable(false);
        resizeTraitTable.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resizeTraitTable.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        resizeTraitTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resizeTraitTableActionPerformed(evt);
            }
        });
        jToolBar4.add(resizeTraitTable);

        javax.swing.GroupLayout traitsLayout = new javax.swing.GroupLayout(traits);
        traits.setLayout(traitsLayout);
        traitsLayout.setHorizontalGroup(
            traitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jToolBar4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        traitsLayout.setVerticalGroup(
            traitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, traitsLayout.createSequentialGroup()
                .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        showTempCheckBox.setSelected(true);
        showTempCheckBox.setToolTipText("");
        showTempCheckBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/bullet_toggle_plus.png"))); // NOI18N
        showTempCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        showTempCheckBox.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/bullet_toggle_minus.png"))); // NOI18N
        showTempCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showTempCheckBoxActionPerformed(evt);
            }
        });

        tempPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Temp / Debug"));
        tempPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        tempTable.setAutoCreateRowSorter(true);
        tempTable.setModel(tempTableModel);
        jScrollPane6.setViewportView(tempTable);

        jToolBar5.setBorder(null);
        jToolBar5.setFloatable(false);
        jToolBar5.setRollover(true);

        refreshTempTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/arrow_refresh_small.png"))); // NOI18N
        refreshTempTable.setToolTipText("Refresh values");
        refreshTempTable.setFocusable(false);
        refreshTempTable.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshTempTable.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        refreshTempTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshTempActionPerformed(evt);
            }
        });
        jToolBar5.add(refreshTempTable);
        jToolBar5.add(jSeparator6);

        resizeTempTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/script_code.png"))); // NOI18N
        resizeTempTable.setToolTipText("Auto-size columns");
        resizeTempTable.setFocusable(false);
        resizeTempTable.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resizeTempTable.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        resizeTempTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resizeTempTableActionPerformed(evt);
            }
        });
        jToolBar5.add(resizeTempTable);

        javax.swing.GroupLayout tempPanelLayout = new javax.swing.GroupLayout(tempPanel);
        tempPanel.setLayout(tempPanelLayout);
        tempPanelLayout.setHorizontalGroup(
            tempPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
            .addComponent(jToolBar5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tempPanelLayout.setVerticalGroup(
            tempPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tempPanelLayout.createSequentialGroup()
                .addComponent(jToolBar5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
        );

        st.setName("ST"); // NOI18N
        st.setParser(ParsingFieldParserFactory.IntegerParser());
        st.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        dr.setName("DR"); // NOI18N
        dr.setParser(ParsingFieldParserFactory.DRParser());
        dr.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        dx.setName("DX"); // NOI18N
        dx.setParser(ParsingFieldParserFactory.IntegerParser());
        dx.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        iq.setName("IQ"); // NOI18N
        iq.setParser(ParsingFieldParserFactory.IntegerParser());
        iq.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        ht.setName("HT"); // NOI18N
        ht.setParser(ParsingFieldParserFactory.IntegerParser());
        ht.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        hp.setName("HP"); // NOI18N
        hp.setParser(ParsingFieldParserFactory.IntegerParser());
        hp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        will.setName("Will"); // NOI18N
        will.setParser(ParsingFieldParserFactory.IntegerParser());
        will.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        per.setName("Per"); // NOI18N
        per.setParser(ParsingFieldParserFactory.IntegerParser());
        per.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        fp.setName("FP"); // NOI18N
        fp.setParser(ParsingFieldParserFactory.IntegerParser());
        fp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        speed.setName("Speed"); // NOI18N
        speed.setParser(ParsingFieldParserFactory.FloatParser());
        speed.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        sm.setName("SM"); // NOI18N
        sm.setParser(ParsingFieldParserFactory.IntegerParser());
        sm.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        move.setName("Move"); // NOI18N
        move.setParser(ParsingFieldParserFactory.IntegerParser());
        move.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        dodge.setName("Dodge"); // NOI18N
        dodge.setParser(ParsingFieldParserFactory.IntegerParser());
        dodge.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        parry.setName("Parry"); // NOI18N
        parry.setParser(ParsingFieldParserFactory.IntegerParser());
        parry.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        block.setName("Block"); // NOI18N
        block.setParser(ParsingFieldParserFactory.IntegerParser());
        block.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        fatigue.setBackground(new java.awt.Color(255, 220, 220));
        fatigue.setName("Fatigue"); // NOI18N
        fatigue.setParser(ParsingFieldParserFactory.IntegerParser());
        fatigue.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        injury.setBackground(new java.awt.Color(255, 220, 220));
        injury.setName("Injury"); // NOI18N
        injury.setParser(ParsingFieldParserFactory.IntegerParser());
        injury.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldFocusLost(evt);
            }
        });

        strengthTablesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Strength Tables"));
        strengthTablesPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        strengthTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        strengthTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Basic Thrust", null},
                {"Basic Swing", null},
                {"Basic Lift", null},
                {"<html><b>Encumbrance</b></html>", null},
                {"None", null},
                {"Light", null},
                {"Medium", null},
                {"Heavy", null},
                {"Extra-Heavy", null}
            },
            new String [] {
                "Name", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        strengthTable.setIntercellSpacing(new java.awt.Dimension(2, 2));

        javax.swing.GroupLayout strengthTablesPanelLayout = new javax.swing.GroupLayout(strengthTablesPanel);
        strengthTablesPanel.setLayout(strengthTablesPanelLayout);
        strengthTablesPanelLayout.setHorizontalGroup(
            strengthTablesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(strengthTablesPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(strengthTable, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        strengthTablesPanelLayout.setVerticalGroup(
            strengthTablesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(strengthTablesPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(strengthTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        showStrengthTablesCheckBox.setSelected(true);
        showStrengthTablesCheckBox.setToolTipText("");
        showStrengthTablesCheckBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/bullet_toggle_plus.png"))); // NOI18N
        showStrengthTablesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        showStrengthTablesCheckBox.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/bullet_toggle_minus.png"))); // NOI18N
        showStrengthTablesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showStrengthTablesCheckBoxActionPerformed(evt);
            }
        });

        noteSeparator.setCursor(new java.awt.Cursor(java.awt.Cursor.N_RESIZE_CURSOR));
        noteSeparator.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                noteSeparatorMouseDragged(evt);
            }
        });

        statusButton.setText("status_button1");
        statusButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        statusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(name, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(noteScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(shieldPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(attacks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(traits, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(showTempCheckBox)
                .addGap(0, 0, 0)
                .addComponent(tempPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ht, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(iq, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dx, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(st, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(will, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(fp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(5, 5, 5)
                                        .addComponent(per, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sm, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel25)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(speed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel24)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(move, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 10, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dodge, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(parry, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(block, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dr, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(19, 19, 19)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(type, 0, 152, Short.MAX_VALUE)
                            .addComponent(statusButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(injury, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fatigue, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGroup(layout.createSequentialGroup()
                .addComponent(showStrengthTablesCheckBox)
                .addGap(0, 0, 0)
                .addComponent(strengthTablesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(noteSeparator)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(statusButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(st, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(speed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(will, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(move, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(per, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel7)
                    .addComponent(jLabel26)
                    .addComponent(ht, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5)
                    .addComponent(fatigue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(injury, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(dr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(dodge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(parry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(block, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shieldPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(attacks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(traits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showStrengthTablesCheckBox)
                    .addComponent(strengthTablesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showTempCheckBox)
                    .addComponent(tempPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noteSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noteScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
            
    private void typeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeActionPerformed
        if(actorLoading == 0 && actor != null) {
        	setActorType(((JComboBox)evt.getSource()).getSelectedItem().toString());
        }
    }//GEN-LAST:event_typeActionPerformed

    private void fieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fieldFocusGained
    	if(LOG.isLoggable(Level.FINE)) {LOG.fine("Focus gained on " + evt.toString()); }
    	Component com =  evt.getComponent();
    	if (JFormattedTextField.class.equals(com.getClass())) {
    		if(LOG.isLoggable(Level.FINER)) {LOG.finer("on a JFormattedTextField!"); }
    		JFormattedTextField t = (JFormattedTextField) evt.getComponent();
    		t.setText(t.getText());
    		t.selectAll();
    	} else if (ParsingField.class.equals(com.getClass())) {
    		if(LOG.isLoggable(Level.FINER)) {LOG.finer("on a ActorField!"); }
    		ParsingField t = (ParsingField) evt.getComponent();
    		t.selectAll();
    	} else if (JComboBox.class.equals(com.getClass())) {
    		if(LOG.isLoggable(Level.FINER)) {LOG.finer("on a JComboBox!"); }
    	}
    	else if (JTextField.class.equals(com.getClass())) {
    		if(LOG.isLoggable(Level.FINER)) {LOG.finer("on a JTextField!"); }
    	}
    	else if (JTextArea.class.equals(com.getClass())) {
    		if(LOG.isLoggable(Level.FINER)) {LOG.finer("on a JTextArea!"); }   		
    	}
    	else {
    		if(LOG.isLoggable(Level.FINER)) {LOG.finer("on a UNKNOWN component!"); }   		
    	}
    		
    }//GEN-LAST:event_fieldFocusGained

    private void fieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fieldFocusLost
    	if(LOG.isLoggable(Level.FINE)) {LOG.fine("actorLoading=" + actorLoading + ") " + evt.toString()); }
    	if (!evt.isTemporary() && actorLoading == 0) {
    		Component source = evt.getComponent();
    		if (ParsingField.class.equals(source.getClass())) {
    			ParsingField field = (ParsingField)source;
    			setActorTraitValue(field.getName(), field.getText());
    		} else if ( JTextField.class.equals(source.getClass())) {
    			JTextField field = (JTextField)source;
    			setActorTraitValue(field.getName(), field.getText());
    		} else if ( JTextArea.class.equals(source.getClass())) {
    			JTextArea field = (JTextArea)source;
    			setActorTraitValue(field.getName(), field.getText());
    		} else {        
    			if(LOG.isLoggable(Level.WARNING)) {LOG.warning("unsupported source! " + source.getClass().toString());}
    		}
    	}
    }//GEN-LAST:event_fieldFocusLost
    
    private void addTraitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTraitActionPerformed
    	flushEdits();
    	final String traitName = traitTableModel.addTrait();
        // Figure out what new row is
    	final int row = traitTableModel.getTraitRow(traitName);
        resizeTableInPanel(traits, traitsTable, traitTableModel);

        // Move focus to new trait name cell and delete default name
        // Need to use invokeLater to handle asynchronous issues with editCell and requestFocus functions
    	SwingUtilities.invokeLater(new Runnable() {
    		@Override
    		public void run() {
    	    	traitsTable.changeSelection(row, TraitTableModel.columns.Name.ordinal(), false, false);
    	    	traitsTable.editCellAt(row, TraitTableModel.columns.Name.ordinal());
    			Component editor = traitsTable.getEditorComponent();
    			if (editor != null) {
    				editor.requestFocusInWindow();
    				((JTextComponent)editor).selectAll();
    			}
    		}
    	});
    }//GEN-LAST:event_addTraitActionPerformed

    private void removeTraitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeTraitActionPerformed
    	flushEdits();
    	// Convert from sorted row numbers to model row numbers
        // Most of this is not necessary, since the table has a single row selection model
        int viewRows[] = traitsTable.getSelectedRows();
        int modelRows[] = new int[viewRows.length];
        for (int i = 0; i < viewRows.length; ++i) {
            modelRows[i] = traitsTable.getRowSorter().convertRowIndexToModel(viewRows[i]);
        }
        Arrays.sort(modelRows);
        traitTableModel.removeTraits(modelRows);
        resizeTableInPanel(traits, traitsTable, traitTableModel);
    }//GEN-LAST:event_removeTraitActionPerformed

    private void resizeTraitTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeTraitTableActionPerformed
    	resizeTableInPanel(traits, traitsTable, traitTableModel);
    }//GEN-LAST:event_resizeTraitTableActionPerformed

    private void resizeAttackTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeAttackTableActionPerformed
    	resizeTableInPanel(attacks, attacksTable, attackTableModel);
    }//GEN-LAST:event_resizeAttackTableActionPerformed

    private void defaultAttackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultAttackActionPerformed
    	flushEdits();
		if(attacksTable.getSelectedRow() == -1)
			return;
        int modelRow = attacksTable.getRowSorter().convertRowIndexToModel(attacksTable.getSelectedRow());
        attackTableModel.setDefaultAttack(modelRow);
        resizeTableInPanel(attacks, attacksTable, attackTableModel);
    }//GEN-LAST:event_defaultAttackActionPerformed

    private void removeAttackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAttackActionPerformed
    	flushEdits();
        // Convert from sorted row numbers to model row numbers
        // Most of this is not necessary, since the table has a single row selection model
        int viewRows[] = attacksTable.getSelectedRows();
        int modelRows[] = new int[viewRows.length];
        for (int i = 0; i < viewRows.length; ++i) {
            modelRows[i] = attacksTable.getRowSorter().convertRowIndexToModel(viewRows[i]);
        }
        Arrays.sort(modelRows);
        attackTableModel.removeAttacks(modelRows);
        resizeTableInPanel(attacks, attacksTable, attackTableModel);
    }//GEN-LAST:event_removeAttackActionPerformed

    private void addAttackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAttackActionPerformed
    	flushEdits();
        attackTableModel.addAttack();
        resizeTableInPanel(attacks, attacksTable, attackTableModel);
    }//GEN-LAST:event_addAttackActionPerformed

    private void executeAttackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeAttackActionPerformed
    	if (attacksTable.getSelectedRow() == -1) return;
    	int modelRow = attacksTable.getRowSorter().convertRowIndexToModel(attacksTable.getSelectedRow());
    	stopCellEditing(attacksTable);
    	actor.attack(modelRow);
       	//gameMaster.new AttackNumAction(modelRow).actionPerformed(null);
    }//GEN-LAST:event_executeAttackActionPerformed

    private void showTempCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showTempCheckBoxActionPerformed
       	tempPanel.setVisible(showTempCheckBox.isSelected());
       	if (showTempCheckBox.isSelected()) {
       		showTempCheckBox.setText("");
       	} else {
       		showTempCheckBox.setText("show Temp / Debug");
       	}
        tempPanel.doLayout();
        this.doLayout();
    }//GEN-LAST:event_showTempCheckBoxActionPerformed

    private void resizeTempTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeTempTableActionPerformed
        resizeTableInPanel(tempPanel, tempTable, tempTableModel);
    }//GEN-LAST:event_resizeTempTableActionPerformed

    private void refreshTempActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshTempActionPerformed
        tempTableModel.rebuildTable();
    }//GEN-LAST:event_refreshTempActionPerformed

    private void showStrengthTablesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showStrengthTablesCheckBoxActionPerformed
    	strengthTablesPanel.setVisible(showStrengthTablesCheckBox.isSelected());
       	if (showStrengthTablesCheckBox.isSelected()) {
       		showStrengthTablesCheckBox.setText("");
       	} else {
       		showStrengthTablesCheckBox.setText("show Strength Tables");
       	}
       	strengthTablesPanel.doLayout();
        this.doLayout();
    }//GEN-LAST:event_showStrengthTablesCheckBoxActionPerformed

    private void noteSeparatorMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_noteSeparatorMouseDragged
    	int y = -1*evt.getY();
    	int currentHeight = noteScrollPane.getPreferredSize().height;
    	int minimumHeight = noteScrollPane.getMinimumSize().height;
    	int newY = currentHeight+y;
    	newY = Math.max(minimumHeight, newY); // not less than minimum!
    	
    	//System.out.println("HERE: mouseDragged event, y=" + evt.getY() + ", x=" + evt.getX());
    	Dimension preferredSize = new Dimension(noteScrollPane.getPreferredSize().width, newY);
    	noteScrollPane.setPreferredSize(preferredSize);
    	
    	revalidate();
    	//doLayout();
    	//repaint();
    	scrollRectToVisible(new Rectangle(0,getHeight()-1,1,1));
    }//GEN-LAST:event_noteSeparatorMouseDragged

    private void statusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusButtonActionPerformed
    	popupStatusEditList.show(actor, statusButton, 0, 0);
    }//GEN-LAST:event_statusButtonActionPerformed

 

    /**
     * Disable the panel, setting all values to default
     */
    private void disableAndClearPanel() {
    	actorLoading++;
    	st.setText("");
    	st.setEnabled(false);
    	hp.setText("");
    	hp.setEnabled(false);
    	speed.setText("");
    	speed.setEnabled(false);
    	dx.setText("");
    	dx.setEnabled(false);
    	will.setText("");
    	will.setEnabled(false);
    	move.setText("");
    	move.setEnabled(false);
    	iq.setText("");
    	iq.setEnabled(false);
    	per.setText("");
    	per.setEnabled(false);
    	ht.setText("");
    	ht.setEnabled(false);
    	fp.setText("");
    	fp.setEnabled(false);
    	sm.setText("");
    	sm.setEnabled(false);
    	fatigue.setText("");
    	fatigue.setEnabled(false);
    	injury.setText("");
    	injury.setEnabled(false);
    	dodge.setText("");
    	dodge.setEnabled(false);
    	parry.setText("");
    	parry.setEnabled(false);
    	block.setText("");
    	block.setEnabled(false);
    	dr.setText("");
    	dr.setEnabled(false);
    	shieldDB.setText("");
    	shieldDB.setEnabled(false);
    	shieldDR.setText("");
    	shieldDR.setEnabled(false);
    	shieldHP.setText("");
    	shieldHP.setEnabled(false);
    	statusButton.setText(" ");
    	statusButton.setEnabled(false);
    	type.setSelectedIndex(-1);
    	type.setEnabled(false);
    	name.setText("");
    	name.setBackground(new java.awt.Color(236, 233, 216));
    	name.setEnabled(false);
    	addTrait.setEnabled(false);
    	removeTrait.setEnabled(false);
    	resizeTraitTable.setEnabled(false);
    	addAttack.setEnabled(false);
       	removeAttack.setEnabled(false);
      	defaultAttack.setEnabled(false);
      	executeAttack.setEnabled(false);
      	resizeAttackTable.setEnabled(false);
      	attackTableModel.setActor(null);
      	resizeTempTable.setEnabled(false);
      	refreshTempTable.setEnabled(false);
      	//resizeAttacksTable(); ??needed??
      	//add_timer.setEnabled(false);
    	notes.setText("");
    	notes.setEnabled(false);
    	actorLoading--;
    }

    /**
     * Enable the panel fields, only called when the actor is refreshed with good data
     */
    protected void enablePanel() {
    	st.setEnabled(true);
    	hp.setEnabled(true);
    	speed.setEnabled(true);
    	dx.setEnabled(true);
    	will.setEnabled(true);
    	move.setEnabled(true);
    	iq.setEnabled(true);
    	per.setEnabled(true);
    	ht.setEnabled(true);
    	fp.setEnabled(true);
    	sm.setEnabled(true);
    	fatigue.setEnabled(true);
    	injury.setEnabled(true);
    	dodge.setEnabled(true);
    	parry.setEnabled(true);
    	block.setEnabled(true);
    	dr.setEnabled(true);
    	shieldDB.setEnabled(true);
    	shieldDR.setEnabled(true);
    	shieldHP.setEnabled(true);
    	statusButton.setEnabled(true);
    	type.setEnabled(true);
    	name.setEnabled(true);
    	addAttack.setEnabled(true);
       	removeAttack.setEnabled(true);
      	defaultAttack.setEnabled(true);
      	if (isInit) executeAttack.setEnabled(true);
      	resizeAttackTable.setEnabled(true);
      	addTrait.setEnabled(true);
       	removeTrait.setEnabled(true);
      	resizeTraitTable.setEnabled(true);
      	resizeTempTable.setEnabled(true);
      	refreshTempTable.setEnabled(true);
    	//add_timer.setEnabled(true);
    	notes.setEnabled(true);
    }
    
    /** 
     * Resize the attacks table to fit all rows
     */
    protected void resizeTableInPanel(JPanel panel, JTable table, AbstractTableModel tableModel) {
        panel.setPreferredSize(new Dimension(0, panel.getMinimumSize().height+table.getRowHeight()*(tableModel.getRowCount())));
        if(LOG.isLoggable(Level.FINER)) {LOG.finer("resizing."); }
    	//this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    	TableColumn column = null;
    	for (int i = 0; i < table.getColumnCount(); i++) {
    	    column = table.getColumnModel().getColumn(i);
    	    
    	    // Get width of column header 
    	    TableCellRenderer renderer = column.getHeaderRenderer(); 
    	    if (renderer == null) 
    	    { renderer = table.getTableHeader().getDefaultRenderer(); } 
    	    Component comp = renderer.getTableCellRendererComponent( table, column.getHeaderValue(), false, false, 0, 0); 
    	    int width = comp.getPreferredSize().width; 
     	    
    	    // Check width of all the cells
    	    // Get maximum width of column data
    	    for (int j=0; j < table.getRowCount(); j++) {
    	        renderer = table.getCellRenderer(j, i);
    	        comp = renderer.getTableCellRendererComponent(table, table.getValueAt(j, i), false, false, j, i);
    	        width = Math.max(width, comp.getPreferredSize().width);
    	    }
       	    column.setPreferredWidth(width);
    	}
    	//this.resizeAndRepaint();
        panel.revalidate();
    }
    
    /**
     * Refresh the actor in the display, discarding any potential edits
     */
    protected void refreshActor () {
    	if (actorLoading != 0 || actor == null) return;
		actorLoading++; // turn off property updates
	    
		refreshActorField(name);
		formatActorName();
		refreshActorStatus();
	    type.setSelectedItem(actor.getType());
	    refreshActorField(st);
	    refreshActorField(hp);
	    refreshActorField(speed);
	    refreshActorField(dx);
	    refreshActorField(will);
	    refreshActorField(move);
	    refreshActorField(iq);
	    refreshActorField(per);
	    refreshActorField(ht);
	    refreshActorField(fp);
	    refreshActorField(sm);
	    refreshActorField(fatigue);
	    refreshActorField(injury);
	    refreshActorField(dodge);
	    refreshActorField(parry);
	    refreshActorField(block);
	    refreshActorField(dr);
	    refreshActorField(shieldDB);
	    refreshActorField(shieldDR);
	    refreshActorField(shieldHP);	 
	    refreshActorField(notes);
	    attackTableModel.setActor(actor);
	    traitTableModel.setActor(actor);
	    //tempTableModel.setActor(actor);
	    // GUI re-size update
	    SwingUtilities.invokeLater(new Runnable() {
	    	@Override
	    	public void run() {
	    		resizeTableInPanel(attacks, attacksTable, attackTableModel);
	    		resizeTableInPanel(traits, traitsTable, traitTableModel);
	    		//resizeTableInPanel(tempPanel, tempTable, tempTableModel);
	    	}
	    });
	    refreshActorSecondaryValues();
		actorLoading--; // turn property updates back on
	}
    
    protected void refreshActorStatus() {
    	// TODO: maybe resize dynamically based on viewport size?
    	int width = 130;//getWidth()-100;
		String statusString = actor.getStatusesString();
		if ("".equals(statusString))
			statusString = " "; // avoid odd button display
		else 
			statusString = "<html><body style='width: " + width + "'><p>" + statusString + "</p></body></html>";
		statusButton.setText(statusString);
    }
    
    /**
     * Refresh the actor's calculated values in the display
     */
    protected void refreshActorSecondaryValues () {
    	//Strength stuff:
	    strengthTableModel.setValueAt(actor.getTraitValue("BasicThrust"), 0, 1);
	    strengthTableModel.setValueAt(actor.getTraitValue("BasicSwing"), 1, 1);
	    strengthTableModel.setValueAt(actor.getTraitValue("BasicLift") + " lbs", 2, 1);
	    Double basicLift = Double.parseDouble(actor.getTraitValue("BasicLift"));
	    for (int enc =0; enc <= 4; enc++) {
	    	strengthTableModel.setValueAt(StrengthTables.getEncumbrance(enc, basicLift) + " lbs", 4+enc, 1);
	    }
//	    strengthTable.setTableHeader(null);
//    	//strengthTable.getTableHeader().setVisible(false);
//    	//strengthTable.getTableHeader().setUI(null);
// 
//	    strengthTable.revalidate();
//	    strengthTable.doLayout();
//
//	    Dimension panelSize = new Dimension(0, strengthTablesPanel.getMinimumSize().height+10);
//	   
//	    strengthTablesPanel.setPreferredSize(panelSize);
//	    strengthTablesPanel.doLayout();
//	    strengthTablesPanel.revalidate();
    }
	
	/**
	 * Refresh actor name field, which depends on the type and status
	 */
	protected void formatActorName() {
		switch (actor.getType()) {
		case PC:
			name.setBackground(new Color(200,255,200));
			break;
		case Ally:
			name.setBackground(new Color(200,200,255));
			break;
		case Enemy:
			name.setBackground(new Color(255,200,200));
			break;
		case Neutral:
			name.setBackground(new Color(200,200,200));
			break;
		case Special:
			name.setBackground(new Color(255,200,255));
			break;
		}
		name.setForeground(new Color(0,0,0));
		if (actor.hasStatus(ActorStatus.Unconscious) || actor.hasStatus(ActorStatus.Dead)) {
			name.setForeground(new Color(128,128,128));
		}
	}
	
	protected void refreshActorField(JTextComponent field) {
		actorLoading++;
		String traitName = field.getName();
		field.setText(actor.getTraitValue(traitName));
		actorLoading--;
	}

	/**
	 * Method to set an actor trait value. Disables refreshes while it occurs.
	 * @param name - the name of the trait
	 * @param value - the value to set
	 */
	protected void setActorTraitValue(String name, String value){
		if (actor == null) return;
		actorLoading++;
		actor.setTrait(name, value);
		actorLoading--;
		refreshActorSecondaryValues(); // only secondary values for now
	}
	
	/**
	 * Method to set an actor trait value. Disables refreshes while it occurs.
	 * @param name - the name of the trait
	 * @param value - the value to set
	 */
	protected void setActorType(String value){
			actorLoading++;
            actor.setType(ActorType.valueOf(value));
            formatActorName(); // need to refresh actor name, since this may cause a change in formatting
            actorLoading--;
	}
	
	/**
	 * Set the actor used for displaying actors.
	 * @param actor : the actor to use.
	 */
	public void setActor(Actor newActor) {
		if (actor == newActor) return; // Don't be an idiot
		if (actorLoading != 0) { // Don't allow actor change in the middle of loading
			if(LOG.isLoggable(Level.INFO)) {LOG.info("Actor in the middle of change/loading (" + actorLoading + ")"); }
			return;
		}
		if(LOG.isLoggable(Level.FINE)) {LOG.fine("Set new actor! " + 
				((actor==null)?"[null]":"'"+actor.getTraitValue(BasicTrait.Name)+"'") + " => " +
				((newActor==null)?"[null]":"'"+newActor.getTraitValue(BasicTrait.Name)+"'"));}
		if (actor != null) {
			actorLoading++; // Prevent loop-back into this function due to flushed edits
			actor.removePropertyChangeListener(this);
			flushEdits();
			actorLoading--;
		}

		actor = newActor;
		attackTableModel.setActor(actor);
	    traitTableModel.setActor(actor);
	    tempTableModel.setActor(actor);
	    resizeTableInPanel(tempPanel, tempTable, tempTableModel);
	    //resizeTableInPanel(strengthTablesPanel, strengthTable, strengthTableModel);
		if (actor != null) {
			actor.addPropertyChangeListener(this);
			enablePanel();
			refreshActor();
		} else {
		    resizeTableInPanel(attacks, attacksTable, attackTableModel);
		    resizeTableInPanel(traits, traitsTable, traitTableModel);
			disableAndClearPanel();
		}      
	}
    
	protected enum textListenField {Name, Notes};

	
	/**
	 * flush any edits in progress
	 */
	public void flushEdits() {
		stopCellEditing(attacksTable);
		stopCellEditing(traitsTable);
		flushFieldEdits();
	}
	
	/**
	 * flush any field edits
	 */
	protected void flushFieldEdits() {
		Component fcomponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
		if (fcomponent != null && this.isAncestorOf(fcomponent)) {
			if (JTextComponent.class.isInstance(fcomponent)) {
				JTextComponent comp = (JTextComponent) fcomponent;
				setActorTraitValue(comp.getName(), comp.getText());
			}
		}
	}
	
	/**
	 * Halt cell editing, if it is occurring.
	 */
	protected void stopCellEditing(JTable table) {
		// Don't allow editing to continue while the table is changed
		if(table.getCellEditor() != null)
			if (!table.getCellEditor().stopCellEditing())
				table.getCellEditor().cancelCellEditing();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		// TODO: add some intelligence to this? Especially attack and trait tables
		// Will potentially need to create component map which allows 
		// a 'getComponentByName(String name)' type function to operate
		if(LOG.isLoggable(Level.FINER)) {LOG.finer("actor property '" + e.getPropertyName() + "' changed from '" + e.getOldValue() + "' to '" + e.getNewValue() + "'");}
		
		refreshActor();
	} 

}
