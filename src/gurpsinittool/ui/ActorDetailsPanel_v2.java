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
import java.util.Arrays;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import gurpsinittool.app.*;
import gurpsinittool.app.AttackTableModel.AttackTableCellRenderer;
import gurpsinittool.data.Actor;
import gurpsinittool.data.Actor.ActorStatus;


/**
 *
 * @author dcsmall
 */
public class ActorDetailsPanel_v2 extends javax.swing.JPanel 
	implements ListSelectionListener, TableModelListener{

	// Default SVUID
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = false;
	
	private InitTable initTable;
	private InitTableModel actorModel;
	private boolean actorLoaded = false;
	private int selectedActor = -1;
	private AttackTableModel attackTableModel;
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add_attack;
    private javax.swing.JPanel attacks;
    private javax.swing.JTable attacksTable;
    private javax.swing.JFormattedTextField block;
    private javax.swing.JFormattedTextField damage;
    private javax.swing.JFormattedTextField db;
    private javax.swing.JPanel debugPanel;
    private javax.swing.JButton default_attack;
    private javax.swing.JFormattedTextField dodge;
    private javax.swing.JFormattedTextField dr;
    private javax.swing.JFormattedTextField fatigue;
    private javax.swing.JFormattedTextField fp;
    private javax.swing.JFormattedTextField fp1;
    private javax.swing.JFormattedTextField hp;
    private javax.swing.JFormattedTextField ht;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JFormattedTextField move;
    private javax.swing.JFormattedTextField move1;
    private javax.swing.JTextField name;
    private javax.swing.JTextArea notes;
    private javax.swing.JLabel numBlockLabel;
    private javax.swing.JLabel numParryLabel;
    private javax.swing.JFormattedTextField parry;
    private javax.swing.JButton remove_attack;
    private javax.swing.JButton resizeAttackTable;
    private javax.swing.JLabel shieldDamageLabel;
    private javax.swing.JPanel shieldPanel;
    private javax.swing.JFormattedTextField shield_dr;
    private javax.swing.JFormattedTextField shield_hp;
    private javax.swing.JFormattedTextField st;
    private javax.swing.JFormattedTextField st1;
    private javax.swing.JFormattedTextField st2;
    private javax.swing.JFormattedTextField st3;
    private javax.swing.JFormattedTextField st4;
    private javax.swing.JFormattedTextField st5;
    private javax.swing.JComboBox status;
    private javax.swing.JComboBox type;
    // End of variables declaration//GEN-END:variables
    

    /** Creates new form ActorDetailsPanel */
    public ActorDetailsPanel_v2(InitTable initTable) {
    	this.initTable = initTable;
    	this.actorModel= (InitTableModel) initTable.getModel();
    	initTable.add(this);
    	initTable.getSelectionModel().addListSelectionListener(this);
    	actorModel.addTableModelListener(this);

    	attackTableModel = new AttackTableModel(actorModel);
    	
        initComponents();
        attacksTable.setDefaultRenderer(String.class, attackTableModel.new AttackTableCellRenderer());
        attacksTable.setDefaultRenderer(Integer.class, attackTableModel.new AttackTableCellRenderer());
        notes.getDocument().addDocumentListener(new ActorTextDocumentListener(textListenField.Notes));
        disablePanel();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        st5 = new javax.swing.JFormattedTextField();
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
        attacksTable = new javax.swing.JTable();
        status = new javax.swing.JComboBox();
        type = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        hp = new javax.swing.JFormattedTextField();
        damage = new javax.swing.JFormattedTextField();
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
        dr = new javax.swing.JFormattedTextField();
        shieldPanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        db = new javax.swing.JFormattedTextField();
        shield_dr = new javax.swing.JFormattedTextField();
        shield_hp = new javax.swing.JFormattedTextField();
        debugPanel = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        shieldDamageLabel = new javax.swing.JLabel();
        numParryLabel = new javax.swing.JLabel();
        numBlockLabel = new javax.swing.JLabel();
        st = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        st1 = new javax.swing.JFormattedTextField();
        jLabel21 = new javax.swing.JLabel();
        st2 = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        st3 = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        st4 = new javax.swing.JFormattedTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        move1 = new javax.swing.JFormattedTextField();
        jLabel26 = new javax.swing.JLabel();
        fp1 = new javax.swing.JFormattedTextField();

        jFormattedTextField1.setText("jFormattedTextField1");

        st5.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        st5.setText("99");
        st5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                st5fieldFocusGained(evt);
            }
        });
        st5.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                st5PropertyChange(evt);
            }
        });

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel2.setText("Status:");

        jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel3.setText("Type:");

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel4.setText("HP:");

        jLabel5.setFont(jLabel5.getFont().deriveFont(jLabel5.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel5.setText("Damage:");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addContainerGap())
        );

        status.setModel(new DefaultComboBoxModel(Actor.ActorStatus.values()));
        status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusActionPerformed(evt);
            }
        });
        status.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });

        type.setModel(new DefaultComboBoxModel(Actor.ActorType.values()));
        type.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeActionPerformed(evt);
            }
        });
        type.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });

        jLabel7.setFont(jLabel7.getFont().deriveFont(jLabel7.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel7.setText("FP:");

        hp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        hp.setText("99");
        hp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        hp.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                hpPropertyChange(evt);
            }
        });

        damage.setForeground(new java.awt.Color(220, 0, 0));
        damage.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        damage.setText("99");
        damage.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        damage.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                damagePropertyChange(evt);
            }
        });

        ht.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        ht.setText("99");
        ht.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        ht.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                htPropertyChange(evt);
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
        jScrollPane1.setViewportView(notes);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("HT:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Dodge");

        fp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        fp.setText("99");
        fp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        fp.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fpPropertyChange(evt);
            }
        });

        move.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        move.setText("99");
        move.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        move.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                movePropertyChange(evt);
            }
        });

        fatigue.setForeground(new java.awt.Color(220, 0, 0));
        fatigue.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        fatigue.setText("99");
        fatigue.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        fatigue.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fatiguePropertyChange(evt);
            }
        });

        jLabel6.setFont(jLabel6.getFont().deriveFont(jLabel6.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel6.setText("Fatigue:");

        parry.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        parry.setText("99");
        parry.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        parry.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                parryPropertyChange(evt);
            }
        });

        name.setBackground(new java.awt.Color(236, 233, 216));
        name.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        name.setText("name");
        name.setBorder(null);
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
        block.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        block.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                blockPropertyChange(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Block");

        dodge.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        dodge.setText("99");
        dodge.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        dodge.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dodgePropertyChange(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("DR");

        dr.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        dr.setText("99");
        dr.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        dr.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                drPropertyChange(evt);
            }
        });

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
        db.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        db.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dbPropertyChange(evt);
            }
        });

        shield_dr.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        shield_dr.setText("99");
        shield_dr.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        shield_dr.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                shield_drPropertyChange(evt);
            }
        });

        shield_hp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        shield_hp.setText("99");
        shield_hp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fieldFocusGained(evt);
            }
        });
        shield_hp.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                shield_hpPropertyChange(evt);
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

        debugPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Debug"));
        debugPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("Shield Damage:");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("numParry:");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("numBlock:");

        shieldDamageLabel.setText("asdf");

        numParryLabel.setText("asdf");

        numBlockLabel.setText("asdf");

        javax.swing.GroupLayout debugPanelLayout = new javax.swing.GroupLayout(debugPanel);
        debugPanel.setLayout(debugPanelLayout);
        debugPanelLayout.setHorizontalGroup(
            debugPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(debugPanelLayout.createSequentialGroup()
                .addGroup(debugPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(debugPanelLayout.createSequentialGroup()
                        .addGroup(debugPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(debugPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(numParryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(numBlockLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(debugPanelLayout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(shieldDamageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        debugPanelLayout.setVerticalGroup(
            debugPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(debugPanelLayout.createSequentialGroup()
                .addGroup(debugPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(shieldDamageLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(debugPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numParryLabel)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(debugPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(numBlockLabel)))
        );

        st.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        st.setText("99");
        st.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                stfieldFocusGained(evt);
            }
        });
        st.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                stPropertyChange(evt);
            }
        });

        jLabel8.setFont(jLabel8.getFont().deriveFont(jLabel8.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel8.setText("ST:");

        jLabel14.setFont(jLabel14.getFont().deriveFont(jLabel14.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel14.setText("DX:");

        st1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        st1.setText("99");
        st1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                st1fieldFocusGained(evt);
            }
        });
        st1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                st1PropertyChange(evt);
            }
        });

        jLabel21.setFont(jLabel21.getFont().deriveFont(jLabel21.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel21.setText("Will:");

        st2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        st2.setText("99");
        st2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                st2fieldFocusGained(evt);
            }
        });
        st2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                st2PropertyChange(evt);
            }
        });

        jLabel22.setFont(jLabel22.getFont().deriveFont(jLabel22.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel22.setText("IQ:");

        st3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        st3.setText("99");
        st3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                st3fieldFocusGained(evt);
            }
        });
        st3.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                st3PropertyChange(evt);
            }
        });

        jLabel23.setFont(jLabel23.getFont().deriveFont(jLabel23.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel23.setText("Per:");

        st4.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        st4.setText("99");
        st4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                st4fieldFocusGained(evt);
            }
        });
        st4.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                st4PropertyChange(evt);
            }
        });

        jLabel24.setFont(jLabel24.getFont().deriveFont(jLabel24.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel24.setText("Move:");

        jLabel25.setFont(jLabel25.getFont().deriveFont(jLabel25.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel25.setText("Speed:");

        move1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        move1.setText("99");
        move1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                move1fieldFocusGained(evt);
            }
        });
        move1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                move1PropertyChange(evt);
            }
        });

        jLabel26.setFont(jLabel26.getFont().deriveFont(jLabel26.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel26.setText("SM:");

        fp1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        fp1.setText("99");
        fp1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fp1fieldFocusGained(evt);
            }
        });
        fp1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fp1PropertyChange(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(name, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(attacks, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(debugPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(parry, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(block, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dodge, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dr, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(st3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel23)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(st4))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ht, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fp))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(st1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(st2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(st, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(move1)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel25)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(hp, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel24)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(move))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel26)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fp1))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(19, 19, 19)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(status, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(type, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(0, 32, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(fatigue, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(damage, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(shieldPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(st, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(move1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(st2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(st1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(move, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(st3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(st4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(ht, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(fp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(fp1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(fatigue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(damage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel10)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(block, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(parry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(dodge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(dr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(shieldPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(attacks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(debugPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void htPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_htPropertyChange
        if(actorLoaded && evt.getPropertyName().equals("value")) {
            setActorValue(InitTableModel.columns.HT.ordinal(), ((Long) ht.getValue()).intValue());
        }
    }//GEN-LAST:event_htPropertyChange

    private void hpPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_hpPropertyChange
        if(actorLoaded && evt.getPropertyName().equals("value")) {
            //if (DEBUG) { System.out.println("ActorDetailsPanel: HP Property change event (" + evt.getNewValue() + ") " + evt.getPropertyName() + ": " + evt.toString()); }
            setActorValue(InitTableModel.columns.HP.ordinal(), ((Long) hp.getValue()).intValue());
        }
    }//GEN-LAST:event_hpPropertyChange

    private void damagePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_damagePropertyChange
        if(actorLoaded && evt.getPropertyName().equals("value")) {
        	setActorValue(InitTableModel.columns.Damage.ordinal(), ((Long) damage.getValue()).intValue());
        }
    }//GEN-LAST:event_damagePropertyChange

    private void fpPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fpPropertyChange
        if(actorLoaded && evt.getPropertyName().equals("value")) {
        	setActorValue(InitTableModel.columns.FP.ordinal(), ((Long) fp.getValue()).intValue());
        }	
    }//GEN-LAST:event_fpPropertyChange

    private void fatiguePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fatiguePropertyChange
        if(actorLoaded && evt.getPropertyName().equals("value")) {
        	setActorValue(InitTableModel.columns.Fatigue.ordinal(), ((Long) fatigue.getValue()).intValue());
        }
    }//GEN-LAST:event_fatiguePropertyChange

    private void movePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_movePropertyChange
        if(actorLoaded && evt.getPropertyName().equals("value")) {
        	setActorValue(InitTableModel.columns.Move.ordinal(), ((Long) move.getValue()).intValue());
        }
    }//GEN-LAST:event_movePropertyChange

    private void dodgePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dodgePropertyChange
        if(actorLoaded && evt.getPropertyName().equals("value")) {
            setActorValue(InitTableModel.columns.Dodge.ordinal(), ((Long) dodge.getValue()).intValue());
       }
    }//GEN-LAST:event_dodgePropertyChange
    
    private void parryPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_parryPropertyChange
        if(actorLoaded && evt.getPropertyName().equals("value")) {
        	actorModel.getActor(selectedActor).Parry = ((Long) parry.getValue()).intValue();
			actorModel.setDirty();
        }
    }//GEN-LAST:event_parryPropertyChange

    private void blockPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_blockPropertyChange
        if(actorLoaded && evt.getPropertyName().equals("value")) {
        	actorModel.getActor(selectedActor).Block = ((Long) block.getValue()).intValue();
			actorModel.setDirty();
		}
    }//GEN-LAST:event_blockPropertyChange

    private void drPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_drPropertyChange
        if(actorLoaded && evt.getPropertyName().equals("value")) {
            actorModel.getActor(selectedActor).DR = ((Long) dr.getValue()).intValue();
            actorModel.setDirty();
        }
    }//GEN-LAST:event_drPropertyChange

    private void dbPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dbPropertyChange
        if(actorLoaded && evt.getPropertyName().equals("value")) {
            actorModel.getActor(selectedActor).DB = ((Long) db.getValue()).intValue();
            actorModel.setDirty();
        }
    }//GEN-LAST:event_dbPropertyChange
    
    private void shield_drPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_shield_drPropertyChange
        if(actorLoaded && evt.getPropertyName().equals("value")) {
            actorModel.getActor(selectedActor).ShieldDR = ((Long) shield_dr.getValue()).intValue();
            actorModel.setDirty();
        }
    }//GEN-LAST:event_shield_drPropertyChange

    private void shield_hpPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_shield_hpPropertyChange
        if(actorLoaded && evt.getPropertyName().equals("value")) {
            actorModel.getActor(selectedActor).ShieldHP = ((Long) shield_hp.getValue()).intValue();
            actorModel.setDirty();
        }
    }//GEN-LAST:event_shield_hpPropertyChange

    private void statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusActionPerformed
        if(actorLoaded) {
            //if (DEBUG) { System.out.println("ActorDetailsPanel: State action performed event " + evt.toString()); }
        	// TODO: Check this code for state/status change!
            //setActorValue(InitTableModel.columns.State.ordinal(), ((JComboBox)evt.getSource()).getSelectedItem().toString());
        }
    }//GEN-LAST:event_statusActionPerformed

    private void typeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeActionPerformed
        if(actorLoaded) {
        	setActorValue(InitTableModel.columns.Type.ordinal(), ((JComboBox)evt.getSource()).getSelectedItem().toString());
            refreshActor(); // need to refresh actor, since this may cause a change in formatting
        }
    }//GEN-LAST:event_typeActionPerformed

    private void fieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fieldFocusGained
    	if (DEBUG) { System.out.println("ActorDetailsPanel: Focus gained on " + evt.toString()); }
    	initTable.stopCellEditing();
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
    	   	name.setText(actorModel.getActor(selectedActor).Name);
    	}
    	else if (c == KeyEvent.VK_ENTER) {
    	   	if (DEBUG) { System.out.println("ActorDetailsPanel: nameKeyTyped ENTER"); }
        	setActorValue(InitTableModel.columns.Name.ordinal(), name.getText());
        }
    }//GEN-LAST:event_nameKeyTyped

    private void nameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameFocusLost
    	if (DEBUG) { System.out.println("ActorDetailsPanel: nameFocusLost" + evt.toString()); }
    	if (!evt.isTemporary()) {
    		setActorValue(InitTableModel.columns.Name.ordinal(), name.getText());
    	}
    }//GEN-LAST:event_nameFocusLost

    private void add_attackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_attackActionPerformed
    	attackTableModel.addAttack();
    	resizeAttacksTable();
    	actorModel.setDirty();
    }//GEN-LAST:event_add_attackActionPerformed

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
        resizeAttacksTable();
    	actorModel.setDirty();
    }//GEN-LAST:event_remove_attackActionPerformed

    private void default_attackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_default_attackActionPerformed
        int modelRow = attacksTable.getRowSorter().convertRowIndexToModel(attacksTable.getSelectedRow());
        attackTableModel.setDefaultAttack(modelRow);
        resizeAttacksTable();
    }//GEN-LAST:event_default_attackActionPerformed

    private void resizeAttackTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeAttackTableActionPerformed
        resizeAttacksTable();
    }//GEN-LAST:event_resizeAttackTableActionPerformed

    private void stfieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_stfieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_stfieldFocusGained

    private void stPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_stPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_stPropertyChange

    private void st1fieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_st1fieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_st1fieldFocusGained

    private void st1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_st1PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_st1PropertyChange

    private void st2fieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_st2fieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_st2fieldFocusGained

    private void st2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_st2PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_st2PropertyChange

    private void st3fieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_st3fieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_st3fieldFocusGained

    private void st3PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_st3PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_st3PropertyChange

    private void st4fieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_st4fieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_st4fieldFocusGained

    private void st4PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_st4PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_st4PropertyChange

    private void st5fieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_st5fieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_st5fieldFocusGained

    private void st5PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_st5PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_st5PropertyChange

    private void move1fieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_move1fieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_move1fieldFocusGained

    private void move1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_move1PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_move1PropertyChange

    private void fp1fieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fp1fieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_fp1fieldFocusGained

    private void fp1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fp1PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_fp1PropertyChange


    /**
     * Disable the panel, setting all values to default
     */
    public void disablePanel() {
    	ht.setValue(10);
    	ht.setEnabled(false);
    	hp.setValue(10);
    	hp.setEnabled(false);
    	damage.setValue(0);
    	damage.setEnabled(false);
    	fp.setValue(10);
    	fp.setEnabled(false);
    	fatigue.setValue(0);
    	fatigue.setEnabled(false);
    	move.setValue(5);
    	move.setEnabled(false);
    	parry.setValue(9);
    	parry.setEnabled(false);
    	block.setValue(9);
    	block.setEnabled(false);
     	dodge.setValue(8);
    	dodge.setEnabled(false);
     	dr.setValue(0);
    	dr.setEnabled(false);
     	db.setValue(2);
    	db.setEnabled(false);
     	shield_dr.setValue(4);
    	shield_dr.setEnabled(false);
     	shield_hp.setValue(20);
    	shield_hp.setEnabled(false);
    	status.setSelectedIndex(0);
    	status.setEnabled(false);
    	type.setSelectedIndex(0);
    	type.setEnabled(false);
    	name.setText("");
    	name.setBackground(new java.awt.Color(236, 233, 216));
    	name.setEnabled(false);
    	add_attack.setEnabled(false);
       	remove_attack.setEnabled(false);
      	default_attack.setEnabled(false);
      	resizeAttackTable.setEnabled(false);
      	attackTableModel.setActor(null);
      	resizeAttacksTable();
  	//add_timer.setEnabled(false);
    	notes.setText("");
    	notes.setEnabled(false);
    }

    /**
     * Enable the panel fields, only called when the actor is refreshed with good data
     */
    private void enablePanel() {
    	ht.setEnabled(true);
    	hp.setEnabled(true);
    	damage.setEnabled(true);
    	fp.setEnabled(true);
    	fatigue.setEnabled(true);
    	move.setEnabled(true);
    	parry.setEnabled(true);
    	block.setEnabled(true);
    	dodge.setEnabled(true);
    	dr.setEnabled(true);
    	db.setEnabled(true);
    	shield_dr.setEnabled(true);
    	shield_hp.setEnabled(true);
    	status.setEnabled(true);
    	type.setEnabled(true);
    	name.setEnabled(true);
    	add_attack.setEnabled(true);
       	remove_attack.setEnabled(true);
      	default_attack.setEnabled(true);
      	resizeAttackTable.setEnabled(true);
    	//add_timer.setEnabled(true);
    	notes.setEnabled(true);
    }

    /** 
     * Resize the attacks table to fit all rows
     */
    public void resizeAttacksTable () {
        attacks.setPreferredSize(new Dimension(0, attacks.getMinimumSize().height+attacksTable.getRowHeight()*(attackTableModel.getRowCount())));
        if (DEBUG) { System.out.println("ActorDetailsPanel:autoSizeColumns: starting."); }
    	//this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    	TableColumn column = null;
    	for (int i = 0; i < attacksTable.getColumnCount(); i++) {
    	    column = attacksTable.getColumnModel().getColumn(i);
    	    
    	    // Get width of column header 
    	    TableCellRenderer renderer = column.getHeaderRenderer(); 
    	    if (renderer == null) 
    	    { renderer = attacksTable.getTableHeader().getDefaultRenderer(); } 
    	    Component comp = renderer.getTableCellRendererComponent( attacksTable, column.getHeaderValue(), false, false, 0, 0); 
    	    int width = comp.getPreferredSize().width; 
     	    
    	    // Check width of all the cells
    	    // Get maximum width of column data
    	    for (int j=0; j < attacksTable.getRowCount(); j++) {
    	        renderer = attacksTable.getCellRenderer(j, i);
    	        comp = renderer.getTableCellRendererComponent(attacksTable, attacksTable.getValueAt(j, i), false, false, j, i);
    	        width = Math.max(width, comp.getPreferredSize().width);
    	    }
    	    
       	    column.setPreferredWidth(width);
    	}
    	//this.resizeAndRepaint();
        attacks.revalidate();
    }
    
    /**
     * Refresh the actor in the display
     */
	public void refreshActor () {
		actorLoaded = false; // turn off property updates
		if (initTable.getSelectedRow() == -1) { 
			disablePanel();
			return; 
		} 
		enablePanel();
		selectedActor = initTable.getSelectedRow();
		Actor actor = actorModel.getActor(initTable.getSelectedRow());
		name.setText(actor.Name);
		switch (actor.Type) {
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
		if (actor.Status.contains(ActorStatus.Unconscious) || actor.Status.contains(ActorStatus.Dead)) {
			name.setForeground(new Color(128,128,128));
		}
	
	    status.setSelectedItem(actor.Status);
	    type.setSelectedItem(actor.Type);
	    ht.setValue(((Integer)actor.HT).longValue());
	    hp.setValue(((Integer)actor.HP).longValue());
	    damage.setValue(((Integer)actor.Injury).longValue());
	    fp.setValue(((Integer)actor.FP).longValue());
	    fatigue.setValue(((Integer)actor.Fatigue).longValue());
	    move.setValue(((Integer)actor.Move).longValue());
	    parry.setValue(((Integer)actor.Parry).longValue());
	    block.setValue(((Integer)actor.Block).longValue());
	    dodge.setValue(((Integer)actor.Dodge).longValue());
	    dr.setValue(((Integer)actor.DR).longValue());
	    db.setValue(((Integer)actor.DB).longValue());
	    shield_dr.setValue(((Integer)actor.ShieldDR).longValue());
	    shield_hp.setValue(((Integer)actor.ShieldHP).longValue());
	    notes.setText(actor.Notes);
	    attackTableModel.setActor(actor);
            
        // Debug
	    shieldDamageLabel.setText(String.valueOf(actor.ShieldDamage));
	    numParryLabel.setText(String.valueOf(actor.numParry));
	    numBlockLabel.setText(String.valueOf(actor.numBlock));
	    //turnLabel.setText(String.valueOf(actor.turn));
            
	    resizeAttacksTable();
		actorLoaded = true; // turn property updates back on
	}

	/**
	 * Method to set a value in the actorModel. Disables refreshes while it occurs.
	 * @param column - the column of the table to modify
	 * @param value - the value to set
	 */
	protected void setActorValue(int column, Object value){
		actorLoaded = false;
        actorModel.setValueAt(value, selectedActor, column);
        actorLoaded = true;
	}
	
	/**
	 * Set the actor table model used for displaying actors.
	 * @param model : the model to use.
	 */
	public void setActorModel(InitTableModel model) {
		if (actorModel != null)
			actorModel.removeTableModelListener(this);
		if(model != null) 
			model.addTableModelListener(this);
		
		actorModel = model;
		refreshActor();
	}
    
 	@Override
	public void tableChanged(TableModelEvent e) {
		if (DEBUG) { System.out.println("ActorDetailsPanel: Table Model event: type = " + e.getType() + ", " + e.toString()); }
		if (actorLoaded) { refreshActor();}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (DEBUG) { System.out.println("ActorDetailsPanel: List Selection event: " + e.getSource().toString()); }
		if(!e.getValueIsAdjusting()) {
			refreshActor();
		}
	}

	protected enum textListenField {Name, Notes};

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
	    	if (actorLoaded) {
	    		Document document = (Document)e.getDocument();
	    		try {
	    			switch (lField) {
	    			case Name:
	    				setActorValue(InitTableModel.columns.Name.ordinal(), document.getText(0,document.getLength()));
	    				break;
	    			case Notes:
	    				// Need to be careful about triggering infinite updates. 
	    				// Right now, the notes field in the Actor does not fire a refresh event when it is updated.
	    				// If it did, this might trigger a text change, which would re-trigger this change, etc.
	    				if (DEBUG) { System.out.println("ActorTextDocumentListener: processTextChanges: Notes: updating actor"); }
	    				actorModel.getActor(selectedActor).Notes = document.getText(0,document.getLength());
	    				actorModel.setDirty();
	    				//setActorValue(ActorTableModel.columns.Notes.ordinal(), document.getText(0,document.getLength()));
	    				break;
	    			}
					
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
            }
	    }
	} 

}
