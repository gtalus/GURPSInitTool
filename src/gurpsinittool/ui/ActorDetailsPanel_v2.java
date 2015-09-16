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
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import gurpsinittool.app.*;
import gurpsinittool.data.Actor;
import gurpsinittool.data.ActorBase.ActorStatus;
import gurpsinittool.data.ActorBase.ActorType;
import gurpsinittool.data.ActorBase.BasicTrait;


/**
 *
 * @author dcsmall
 */
public class ActorDetailsPanel_v2 extends javax.swing.JPanel 
	implements PropertyChangeListener{

	// Default SVUID
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = false;
	
	private int actorLoading = 0; // Block for property updates while in the middle of an update
	private Actor actor;
	private AttackTableModel attackTableModel;
	private TraitTableModel traitTableModel;
	private TraitTableModel tempTableModel;
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add_attack;
    private javax.swing.JButton add_trait;
    private javax.swing.JPanel attacks;
    private javax.swing.JTable attacksTable;
    private javax.swing.JFormattedTextField block;
    private javax.swing.JFormattedTextField db;
    private javax.swing.JButton default_attack;
    private javax.swing.JFormattedTextField dodge;
    private javax.swing.JTextField dr;
    private javax.swing.JFormattedTextField dx;
    private javax.swing.JFormattedTextField fatigue;
    private javax.swing.JFormattedTextField fp;
    private javax.swing.JFormattedTextField hp;
    private javax.swing.JFormattedTextField ht;
    private javax.swing.JFormattedTextField injury;
    private javax.swing.JFormattedTextField iq;
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
    private javax.swing.JScrollPane jScrollPane1;
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
    private javax.swing.JFormattedTextField move;
    private javax.swing.JTextField name;
    private javax.swing.JTextArea notes;
    private javax.swing.JFormattedTextField parry;
    private javax.swing.JFormattedTextField per;
    private javax.swing.JButton refreshTempTable;
    private javax.swing.JButton remove_attack;
    private javax.swing.JButton remove_trait;
    private javax.swing.JButton resizeAttackTable;
    private javax.swing.JButton resizeTempTable;
    private javax.swing.JButton resizeTraitTable;
    private javax.swing.JPanel shieldPanel;
    private javax.swing.JFormattedTextField shield_dr;
    private javax.swing.JFormattedTextField shield_hp;
    private javax.swing.JCheckBox showTempCheckBox;
    private javax.swing.JFormattedTextField sm;
    private javax.swing.JFormattedTextField speed;
    private javax.swing.JFormattedTextField st;
    private javax.swing.JLabel status_label;
    private javax.swing.JPanel tempPanel;
    private javax.swing.JTable tempTable;
    private javax.swing.JPanel traits;
    private javax.swing.JTable traitsTable;
    private javax.swing.JComboBox type;
    private javax.swing.JFormattedTextField will;
    // End of variables declaration//GEN-END:variables
    

    /** Creates new form ActorDetailsPanel */
    public ActorDetailsPanel_v2() {
    	attackTableModel = new AttackTableModel();
    	traitTableModel = new TraitTableModel(false);
    	tempTableModel = new TraitTableModel(true);
    	
        initComponents();
        attacksTable.setDefaultRenderer(String.class, attackTableModel.new AttackTableCellRenderer());
        attacksTable.setDefaultRenderer(Integer.class, attackTableModel.new AttackTableCellRenderer());
        notes.getDocument().addDocumentListener(new ActorTextDocumentListener(textListenField.Notes));
        showTempCheckBox.setSelected(false); showTempCheckBoxActionPerformed(null); // Start hidden
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
        add_attack = new javax.swing.JButton();
        remove_attack = new javax.swing.JButton();
        default_attack = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        resizeAttackTable = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        attacksTable = new BasicTable();
        type = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        hp = new javax.swing.JFormattedTextField();
        injury = new javax.swing.JFormattedTextField();
        ht = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        notes = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        fp = new javax.swing.JFormattedTextField();
        move = new javax.swing.JFormattedTextField();
        fatigue = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        parry = new javax.swing.JFormattedTextField();
        name = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        block = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        dodge = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        shieldPanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        db = new javax.swing.JFormattedTextField();
        shield_dr = new javax.swing.JFormattedTextField();
        shield_hp = new javax.swing.JFormattedTextField();
        st = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        dx = new javax.swing.JFormattedTextField();
        jLabel21 = new javax.swing.JLabel();
        will = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        iq = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        per = new javax.swing.JFormattedTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        speed = new javax.swing.JFormattedTextField();
        jLabel26 = new javax.swing.JLabel();
        sm = new javax.swing.JFormattedTextField();
        traits = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        traitsTable = new BasicTable();
        jToolBar4 = new javax.swing.JToolBar();
        add_trait = new javax.swing.JButton();
        remove_trait = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        resizeTraitTable = new javax.swing.JButton();
        status_label = new javax.swing.JLabel();
        showTempCheckBox = new javax.swing.JCheckBox();
        tempPanel = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tempTable = new javax.swing.JTable();
        jToolBar5 = new javax.swing.JToolBar();
        refreshTempTable = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        resizeTempTable = new javax.swing.JButton();
        dr = new javax.swing.JTextField();

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

        add_attack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/add.png"))); // NOI18N
        add_attack.setFocusable(false);
        add_attack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add_attack.setIconTextGap(1);
        add_attack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        add_attack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_attackActionPerformed(evt);
            }
        });
        jToolBar1.add(add_attack);

        remove_attack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/delete.png"))); // NOI18N
        remove_attack.setFocusable(false);
        remove_attack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        remove_attack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        remove_attack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remove_attackActionPerformed(evt);
            }
        });
        jToolBar1.add(remove_attack);

        default_attack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/wrench_orange.png"))); // NOI18N
        default_attack.setFocusable(false);
        default_attack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        default_attack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        default_attack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                default_attackActionPerformed(evt);
            }
        });
        jToolBar1.add(default_attack);
        jToolBar1.add(jSeparator2);

        resizeAttackTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/script_code.png"))); // NOI18N
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

        hp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        hp.setText("99");
        hp.setName("HP"); // NOI18N
        hp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        hp.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        injury.setForeground(new java.awt.Color(220, 0, 0));
        injury.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        injury.setText("99");
        injury.setName("Injury"); // NOI18N
        injury.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        injury.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        ht.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        ht.setText("99");
        ht.setName("HT"); // NOI18N
        ht.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        ht.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel1.setText("Notes:");

        jScrollPane1.setPreferredSize(new java.awt.Dimension(50, 50));
        jScrollPane1.setRequestFocusEnabled(false);

        notes.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        notes.setLineWrap(true);
        notes.setRows(5);
        notes.setWrapStyleWord(true);
        notes.setName("Notes"); // NOI18N
        jScrollPane1.setViewportView(notes);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("HT:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Dodge");

        fp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        fp.setText("99");
        fp.setName("FP"); // NOI18N
        fp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        fp.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        move.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        move.setText("99");
        move.setName("Move"); // NOI18N
        move.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        move.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        fatigue.setForeground(new java.awt.Color(220, 0, 0));
        fatigue.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        fatigue.setText("99");
        fatigue.setName("Fatigue"); // NOI18N
        fatigue.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        fatigue.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        jLabel6.setFont(jLabel6.getFont().deriveFont(jLabel6.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel6.setText("Fatigue:");

        parry.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        parry.setText("99");
        parry.setName("Parry"); // NOI18N
        parry.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        parry.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

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
                nameFocusLost(evt);
            }
        });
        name.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nameKeyTyped(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Parry");

        block.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        block.setText("99");
        block.setName("Block"); // NOI18N
        block.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        block.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Block");

        dodge.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        dodge.setText("99");
        dodge.setName("Dodge"); // NOI18N
        dodge.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        dodge.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

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

        db.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        db.setText("99");
        db.setName("Shield_DB"); // NOI18N
        db.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        db.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        shield_dr.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        shield_dr.setText("99");
        shield_dr.setName("Shield_DR"); // NOI18N
        shield_dr.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        shield_dr.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        shield_hp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        shield_hp.setText("99");
        shield_hp.setName("Shield_HP"); // NOI18N
        shield_hp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        shield_hp.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout shieldPanelLayout = new javax.swing.GroupLayout(shieldPanel);
        shieldPanel.setLayout(shieldPanelLayout);
        shieldPanelLayout.setHorizontalGroup(
            shieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shieldPanelLayout.createSequentialGroup()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(db, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shield_dr, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shield_hp, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        shieldPanelLayout.setVerticalGroup(
            shieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shieldPanelLayout.createSequentialGroup()
                .addGroup(shieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(db, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(shieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(shield_hp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(shield_dr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );

        st.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        st.setText("99");
        st.setName("ST"); // NOI18N
        st.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        st.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        jLabel8.setFont(jLabel8.getFont().deriveFont(jLabel8.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel8.setText("ST:");

        jLabel14.setFont(jLabel14.getFont().deriveFont(jLabel14.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel14.setText("DX:");

        dx.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        dx.setText("99");
        dx.setName("DX"); // NOI18N
        dx.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        dx.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        jLabel21.setFont(jLabel21.getFont().deriveFont(jLabel21.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel21.setText("Will:");

        will.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        will.setText("99");
        will.setName("Will"); // NOI18N
        will.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        will.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        jLabel22.setFont(jLabel22.getFont().deriveFont(jLabel22.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel22.setText("IQ:");

        iq.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        iq.setText("99");
        iq.setName("IQ"); // NOI18N
        iq.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        iq.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        jLabel23.setFont(jLabel23.getFont().deriveFont(jLabel23.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel23.setText("Per:");

        per.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        per.setText("99");
        per.setName("Per"); // NOI18N
        per.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        per.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        jLabel24.setFont(jLabel24.getFont().deriveFont(jLabel24.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel24.setText("Move:");

        jLabel25.setFont(jLabel25.getFont().deriveFont(jLabel25.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel25.setText("Speed:");

        speed.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        speed.setText("99");
        speed.setToolTipText("");
        speed.setName("Speed"); // NOI18N
        speed.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        speed.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorFloatPropertyChange(evt);
            }
        });

        jLabel26.setFont(jLabel26.getFont().deriveFont(jLabel26.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel26.setText("SM:");

        sm.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        sm.setText("99");
        sm.setName("SM"); // NOI18N
        sm.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        sm.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actorIntPropertyChange(evt);
            }
        });

        traits.setBorder(javax.swing.BorderFactory.createTitledBorder("Traits"));

        traitsTable.setAutoCreateRowSorter(true);
        traitsTable.setModel(traitTableModel);
        jScrollPane5.setViewportView(traitsTable);

        jToolBar4.setBorder(null);
        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);

        add_trait.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/add.png"))); // NOI18N
        add_trait.setFocusable(false);
        add_trait.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add_trait.setIconTextGap(1);
        add_trait.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        add_trait.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_traitActionPerformed(evt);
            }
        });
        jToolBar4.add(add_trait);

        remove_trait.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/delete.png"))); // NOI18N
        remove_trait.setFocusable(false);
        remove_trait.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        remove_trait.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        remove_trait.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remove_traitActionPerformed(evt);
            }
        });
        jToolBar4.add(remove_trait);
        jToolBar4.add(jSeparator5);

        resizeTraitTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/script_code.png"))); // NOI18N
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

        status_label.setText("status_label");

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
        refreshTempTable.setFocusable(false);
        refreshTempTable.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshTempTable.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        refreshTempTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refresh_tempActionPerformed(evt);
            }
        });
        jToolBar5.add(refreshTempTable);
        jToolBar5.add(jSeparator6);

        resizeTempTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/script_code.png"))); // NOI18N
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
            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jToolBar5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tempPanelLayout.setVerticalGroup(
            tempPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tempPanelLayout.createSequentialGroup()
                .addComponent(jToolBar5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
        );

        dr.setText("99");
        dr.setName("DR"); // NOI18N
        dr.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                drFocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(name, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(dodge, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(parry, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(block, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                            .addComponent(dr))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fatigue, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(injury, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ht, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(iq, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dx, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(st, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fp))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(per))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hp))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(will, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sm, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(speed, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(move))))
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
                            .addComponent(status_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel1)))
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
                    .addComponent(status_label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(st, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(speed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(dx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(move, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(will, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(per, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(ht, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(fp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(sm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(fatigue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
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
                                .addComponent(block, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(parry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(dodge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(dr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(showTempCheckBox)
                    .addComponent(tempPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(jLabel1)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void actorIntPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_actorIntPropertyChange
    	if (actorLoading != 0 || actor == null) return; // Ignore
    	Object source = evt.getSource();
        if (JFormattedTextField.class.isInstance(source)) {
            if(evt.getPropertyName().equals("value")) {
                JFormattedTextField field = JFormattedTextField.class.cast(source);
                String name = field.getName();
                Object value = field.getValue();
                actorLoading++;
                setActorTraitValue(name, String.valueOf((Long) value));
                refreshActorIntField(field); // Refresh the field to allow for any actor-based filtering
                actorLoading--;
            }
        } else {
            System.err.println("ERROR: ActorDetailsPanel: property change from non-JFormattedTextField source! " + evt.toString());
        }
    }//GEN-LAST:event_actorIntPropertyChange
        
    private void actorFloatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_actorFloatPropertyChange
        if (actorLoading != 0 || actor == null) return; // Ignore
    	Object source = evt.getSource();
        if (JFormattedTextField.class.isInstance(source)) {
            if(evt.getPropertyName().equals("value")) {
                JFormattedTextField field = JFormattedTextField.class.cast(source);
                String name = field.getName();
                Object value = field.getValue();
                actorLoading++;
                setActorTraitValue(name, String.valueOf(value));
                refreshActorFloatField(field); // Refresh the field to allow for any actor-based filtering
                actorLoading--;
            }
        } else {
            System.err.println("ERROR: ActorDetailsPanel: property change from non-JFormattedTextField source! " + evt.toString());
        }
    }//GEN-LAST:event_actorFloatPropertyChange

    private void typeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeActionPerformed
        if(actorLoading == 0 && actor != null) {
        	setActorType(((JComboBox)evt.getSource()).getSelectedItem().toString());
        }
    }//GEN-LAST:event_typeActionPerformed

    private void fieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fieldFocusGained
    	if (DEBUG) { System.out.println("ActorDetailsPanel: Focus gained on " + evt.toString()); }
    	Component com =  evt.getComponent();
    	if (JFormattedTextField.class.equals(com.getClass())) {
    		if (DEBUG) { System.out.println("ActorDetailsPanel: fieldFocusGained on a JFormattedTextField!"); }
    		JFormattedTextField t = (JFormattedTextField) evt.getComponent();
    		t.setText(t.getText());
    		t.selectAll();
    	}
    	else if (JComboBox.class.equals(com.getClass())) {
    		if (DEBUG) { System.out.println("ActorDetailsPanel: fieldFocusGained on a JComboBox!"); }
    	}
    	else if (JTextField.class.equals(com.getClass())) {
    		if (DEBUG) { System.out.println("ActorDetailsPanel: fieldFocusGained on a JTextField!"); }
    	}
    	else if (JTextArea.class.equals(com.getClass())) {
    		if (DEBUG) { System.out.println("ActorDetailsPanel: fieldFocusGained on a JTextArea!"); }   		
    	}
    	else {
    		if (DEBUG) { System.out.println("ActorDetailsPanel: fieldFocusGained on a UNKNOWN component!"); }   		
    	}
    		
    }//GEN-LAST:event_fieldFocusGained

    private void nameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameKeyTyped
        // Consume Esc and Enter keys
    	char c = evt.getKeyChar();
    	if (c == KeyEvent.VK_ESCAPE) {
    	   	if (DEBUG) { System.out.println("ActorDetailsPanel: nameKeyTyped ESCAPE"); }
    	   	refreshActorStringField(name);
    	}
    	else if (c == KeyEvent.VK_ENTER) {
    	   	if (DEBUG) { System.out.println("ActorDetailsPanel: nameKeyTyped ENTER"); }
        	setActorTraitValue("Name", name.getText());
        }
    }//GEN-LAST:event_nameKeyTyped

    private void nameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameFocusLost
    	if (DEBUG) { System.out.println("ActorDetailsPanel: nameFocusLost" + evt.toString()); }
    	if (!evt.isTemporary() && actorLoading == 0) {
    		setActorTraitValue("Name", name.getText());
    	}
    }//GEN-LAST:event_nameFocusLost

    private void drFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_drFocusLost
    	if (DEBUG) { System.out.println("ActorDetailsPanel: drFocusLost" + evt.toString()); }
    	if (!evt.isTemporary() && actorLoading == 0) {
    		setActorTraitValue("DR", dr.getText());
    	}
    }//GEN-LAST:event_drFocusLost

    
    private void add_traitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_traitActionPerformed
        traitTableModel.addTrait();
        resizeTableInPanel(traits, traitsTable, traitTableModel);
    }//GEN-LAST:event_add_traitActionPerformed

    private void remove_traitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remove_traitActionPerformed
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
    }//GEN-LAST:event_remove_traitActionPerformed

    private void resizeTraitTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeTraitTableActionPerformed
    	resizeTableInPanel(traits, traitsTable, traitTableModel);
    }//GEN-LAST:event_resizeTraitTableActionPerformed

    private void resizeAttackTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeAttackTableActionPerformed
    	resizeTableInPanel(attacks, attacksTable, attackTableModel);
    }//GEN-LAST:event_resizeAttackTableActionPerformed

    private void default_attackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_default_attackActionPerformed
        int modelRow = attacksTable.getRowSorter().convertRowIndexToModel(attacksTable.getSelectedRow());
        attackTableModel.setDefaultAttack(modelRow);
        resizeTableInPanel(attacks, attacksTable, attackTableModel);
    }//GEN-LAST:event_default_attackActionPerformed

    private void remove_attackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remove_attackActionPerformed
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
    }//GEN-LAST:event_remove_attackActionPerformed

    private void add_attackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_attackActionPerformed
        attackTableModel.addAttack();
        resizeTableInPanel(attacks, attacksTable, attackTableModel);
    }//GEN-LAST:event_add_attackActionPerformed

    private void resizeTempTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeTempTableActionPerformed
        resizeTableInPanel(tempPanel, tempTable, tempTableModel);
    }//GEN-LAST:event_resizeTempTableActionPerformed

    private void refresh_tempActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refresh_tempActionPerformed
        tempTableModel.setActor(actor);
    }//GEN-LAST:event_refresh_tempActionPerformed

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




    /**
     * Disable the panel, setting all values to default
     */
    protected void disableAndClearPanel() {
    	actorLoading++;
    	st.setValue(null);
    	st.setEnabled(false);
    	hp.setValue(null);
    	hp.setEnabled(false);
    	speed.setValue(null);
    	speed.setEnabled(false);
    	dx.setValue(null);
    	dx.setEnabled(false);
    	will.setValue(null);
    	will.setEnabled(false);
    	move.setValue(null);
    	move.setEnabled(false);
    	iq.setValue(null);
    	iq.setEnabled(false);
    	per.setValue(null);
    	per.setEnabled(false);
    	ht.setValue(null);
    	ht.setEnabled(false);
    	fp.setValue(null);
    	fp.setEnabled(false);
    	sm.setValue(null);
    	sm.setEnabled(false);
    	fatigue.setValue(null);
    	fatigue.setEnabled(false);
    	injury.setValue(null);
    	injury.setEnabled(false);
    	dodge.setValue(null);
    	dodge.setEnabled(false);
    	parry.setValue(null);
    	parry.setEnabled(false);
    	block.setValue(null);
    	block.setEnabled(false);
    	dr.setText("");
    	dr.setEnabled(false);
    	db.setValue(null);
    	db.setEnabled(false);
    	shield_dr.setValue(null);
    	shield_dr.setEnabled(false);
    	shield_hp.setValue(null);
    	shield_hp.setEnabled(false);
    	
    	//status.setSelectedIndex(-1);
    	//status.setEnabled(false);
    	status_label.setText("");
    	type.setSelectedIndex(-1);
    	type.setEnabled(false);
    	name.setText("");
    	name.setBackground(new java.awt.Color(236, 233, 216));
    	name.setEnabled(false);
    	add_trait.setEnabled(false);
    	remove_trait.setEnabled(false);
    	resizeTraitTable.setEnabled(false);
    	add_attack.setEnabled(false);
       	remove_attack.setEnabled(false);
      	default_attack.setEnabled(false);
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
    	db.setEnabled(true);
    	shield_dr.setEnabled(true);
    	shield_hp.setEnabled(true);
    	//status.setEnabled(true);
    	type.setEnabled(true);
    	name.setEnabled(true);
    	add_attack.setEnabled(true);
       	remove_attack.setEnabled(true);
      	default_attack.setEnabled(true);
      	resizeAttackTable.setEnabled(true);
      	add_trait.setEnabled(true);
       	remove_trait.setEnabled(true);
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
        if (DEBUG) { System.out.println("resizeTableInPanel: starting."); }
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
     * Refresh the actor in the display
     */
    protected void refreshActor () {
    	if (actorLoading != 0) return;
		actorLoading++; // turn off property updates
		refreshActorName();
	
		// TODO: clean-up / replace with editor component
		status_label.setText(actor.getStatusesString());
		
	    type.setSelectedItem(actor.getType());
	    refreshActorIntField(st);
	    refreshActorIntField(hp);
	    refreshActorFloatField(speed);
	    refreshActorIntField(dx);
	    refreshActorIntField(will);
	    refreshActorIntField(move);
	    refreshActorIntField(iq);
	    refreshActorIntField(per);
	    refreshActorIntField(ht);
	    refreshActorIntField(fp);
	    refreshActorIntField(sm);
	    refreshActorIntField(fatigue);
	    refreshActorIntField(injury);
	    refreshActorIntField(dodge);
	    refreshActorIntField(parry);
	    refreshActorIntField(block);
	    refreshActorStringField(dr);
	    refreshActorIntField(db);
	    refreshActorIntField(shield_dr);
	    refreshActorIntField(shield_hp);
	    
	    refreshActorStringField(notes);
	             
		actorLoading--; // turn property updates back on
	}
	
	/**
	 * Refresh actor name field, which depends on the type and status
	 */
	protected void refreshActorName() {
		actorLoading++;
		refreshActorStringField(name);
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
		actorLoading--;
	}
	
	protected void refreshActorIntField(JFormattedTextField field) {
		actorLoading++;
		String traitName = field.getName();
		field.setValue(Integer.parseInt(actor.getTraitValue(traitName)));
		actorLoading--;
	}
	
	protected void refreshActorFloatField(JFormattedTextField field) {
		actorLoading++;
		String traitName = field.getName();
		field.setValue(Float.parseFloat(actor.getTraitValue(traitName)));
		actorLoading--;
	}
	
	protected void refreshActorStringField(JTextComponent field) {
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
            actorLoading++;
            actor.setTrait(name, value);
            actorLoading--;
	}
	
	/**
	 * Method to set an actor trait value. Disables refreshes while it occurs.
	 * @param name - the name of the trait
	 * @param value - the value to set
	 */
	protected void setActorType(String value){
			actorLoading++;
            actor.setType(ActorType.valueOf(value));
            refreshActorName(); // need to refresh actor name, since this may cause a change in formatting
            actorLoading--;
	}
	
	/**
	 * Set the actor used for displaying actors.
	 * @param actor : the actor to use.
	 */
	public void setActor(Actor newActor) {
		if (actor == newActor) return; // Don't be an idiot
		if (actorLoading != 0) { // Don't allow actor change in the middle of loading
			System.out.println("-W-: ActorDetailsPanel:setActor: actor in the middle of change/loading (" + actorLoading + ")");
			return;
		}
		System.out.println("ActorDetails: setActor w/ new actor! " + 
				((actor==null)?"[null]":"'"+actor.getTraitValue(BasicTrait.Name)+"'") + " => " +
				((newActor==null)?"[null]":"'"+newActor.getTraitValue(BasicTrait.Name)+"'"));
		if (actor != null) {
			// TODO: stop any in-progress edits somehow
			actorLoading++; // Prevent loop-back into this function due to flushed edits
			actor.removePropertyChangeListener(this);
			stopCellEditing(attacksTable);
			stopCellEditing(traitsTable);
			actorLoading--;
		}

		actor = newActor;
		attackTableModel.setActor(actor);
	    traitTableModel.setActor(actor);
	    tempTableModel.setActor(actor);
	    resizeTableInPanel(attacks, attacksTable, attackTableModel);
	    resizeTableInPanel(traits, traitsTable, traitTableModel);
	    resizeTableInPanel(tempPanel, tempTable, tempTableModel);
		if (actor != null) {
			actor.addPropertyChangeListener(this);
			enablePanel();
			refreshActor();
		} else {
			disableAndClearPanel();
		}      
		
	}
    
	protected enum textListenField {Name, Notes};

	/**
	 * Halt cell editing, if it is occurring.
	 */
	protected void stopCellEditing(JTable table) {
		// Don't allow editing to continue while the table is changed
		if(table.getCellEditor() != null)
			if (!table.getCellEditor().stopCellEditing())
				table.getCellEditor().cancelCellEditing();
	}
	
	/**
	 * Internal class to listen to the changes in text components
	 */
	protected class ActorTextDocumentListener implements DocumentListener {
		
		private textListenField lField;
		
		public ActorTextDocumentListener(textListenField field) {
			lField = field;
		}
	
	    public void insertUpdate(DocumentEvent e) {
	        processTextChanges(e);
	    }
	    public void removeUpdate(DocumentEvent e) {
	    	processTextChanges(e);
	    }
	    public void changedUpdate(DocumentEvent e) {
	    	processTextChanges(e);
	    }
	    private void processTextChanges(DocumentEvent e) {
	    	if (actorLoading == 0) {
	    		Document document = (Document)e.getDocument();
	    		try {
	    			switch (lField) {
	    			case Name:
	    				setActorTraitValue("Name", document.getText(0,document.getLength()));
	    				break;
	    			case Notes:
	    				// TODO: figure out a way to avoid triggering a refresh on every key typed!
	    				if (DEBUG) { System.out.println("ActorTextDocumentListener: processTextChanges: Notes: updating actor"); }
	    				setActorTraitValue("Notes", document.getText(0,document.getLength()));
	    				break;
	    			}
					
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
            }
	    }
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		// TODO: add some intelligence to this?
		System.out.println("ActorDetailsPanel: received actor property changed notification!");
		refreshActor();		
	} 

}
