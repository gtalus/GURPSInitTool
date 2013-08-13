/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gurpsinittool.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;

import gurpsinittool.app.GITApp;
import gurpsinittool.data.Actor;
import gurpsinittool.data.Damage;
import gurpsinittool.data.HitLocations;
import gurpsinittool.data.Damage.DamageType;
import gurpsinittool.data.HitLocations.HitLocation;
import gurpsinittool.util.DieRoller;

/**
 *
 * @author dcsmall
 */
public class DefenseDialog extends javax.swing.JDialog {

	Actor actor;
	int effParry; // Parry with multiple-parry penalty
	int effBlock; // Block with multiple-block penalty
	int effDodge; // Dodge with fatigue/injury penalty
	int effectiveDefenseValue;

	public boolean valid = false;
	
    /**
     * Creates new form DefenseDialog
     */
    public DefenseDialog(Actor actor, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initEnterEsc();
        initLocationCombo();
        setActor(actor);
        damageTextField.selectAll();
        damageTextField.requestFocusInWindow();
    }
    
    private void initLocationCombo() {
    	Vector<String> comboItems = new Vector<String>();
    	for (HitLocations.LocationType location: HitLocations.LocationType.values()) {
    		comboItems.add(HitLocations.locations.get(location).description);
    	}
    	locationCombo.setModel(new DefaultComboBoxModel<String>(comboItems));
    }

    private void initEnterEsc() {
    	Action cancelAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                cancelButtonActionPerformed(actionEvent); } };
        Action okAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                okButtonActionPerformed(actionEvent); } };
        Action eeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                eeCheck.doClick(); } };
        Action retreatAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                retreatCheck.doClick(); } };
        Action sideAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                sideCheck.doClick(); } };
        Action deceptiveAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
               otherSpinner.setValue(otherSpinner.getPreviousValue()); } };
                                               
                
        damageTextField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "OK");
        damageTextField.getActionMap().put("OK", okAction); 
        damageTextField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CANCEL");
        damageTextField.getActionMap().put("CANCEL", cancelAction); 
        locationCombo.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "OK");
        locationCombo.getActionMap().put("OK", okAction); 
        locationCombo.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CANCEL");
        locationCombo.getActionMap().put("CANCEL", cancelAction); 
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "OK");
        getRootPane().getActionMap().put("OK", okAction); 
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CANCEL");
        getRootPane().getActionMap().put("CANCEL", cancelAction); 
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control E"), "ExtraEffort");
        getRootPane().getActionMap().put("ExtraEffort", eeAction); 
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control R"), "Retreat");
        getRootPane().getActionMap().put("Retreat", retreatAction); 
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control S"), "Side");
        getRootPane().getActionMap().put("Side", sideAction); 
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control D"), "Deceptive");
        getRootPane().getActionMap().put("Deceptive", deceptiveAction); 
     
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        okButton = new javax.swing.JButton();
        blockNote = new javax.swing.JLabel();
        dodgeNote = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        parryNote = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        shieldCheckBox = new javax.swing.JCheckBox();
        blockButton = new javax.swing.JRadioButton();
        cancelButton = new javax.swing.JButton();
        parryButton = new javax.swing.JRadioButton();
        rerollButton = new javax.swing.JButton();
        retreatCheck = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        dodgeButton = new javax.swing.JRadioButton();
        eeCheck = new javax.swing.JCheckBox();
        sideCheck = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        effectiveDefense = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        result = new javax.swing.JTextField();
        shield_dr = new javax.swing.JLabel();
        db = new javax.swing.JLabel();
        shield_hp = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        rollTextField = new javax.swing.JFormattedTextField();
        drTextField = new javax.swing.JFormattedTextField();
        damageTextField = new javax.swing.JTextField();
        otherSpinner = new javax.swing.JSpinner();
        locationCombo = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        noneButton = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);

        okButton.setMnemonic('k');
        okButton.setText("OK");
        okButton.setToolTipText("");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        blockNote.setText("(10-4=12)");

        dodgeNote.setText("(10)");

        jLabel4.setText("Other");

        parryNote.setText("(10-8=12)");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Damage:");

        shieldCheckBox.setSelected(true);
        shieldCheckBox.setText("Shield:");
        shieldCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkItemStateChanged(evt);
            }
        });

        buttonGroup1.add(blockButton);
        blockButton.setText("Block");
        blockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });

        cancelButton.setMnemonic('c');
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(parryButton);
        parryButton.setSelected(true);
        parryButton.setText("Parry");
        parryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });

        rerollButton.setText("Reroll");
        rerollButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        rerollButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rerollButtonActionPerformed(evt);
            }
        });

        retreatCheck.setText("Retreat");
        retreatCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkItemStateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Roll:");

        buttonGroup1.add(dodgeButton);
        dodgeButton.setText("Dodge");
        dodgeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });

        eeCheck.setText("Extra Effort");
        eeCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkItemStateChanged(evt);
            }
        });

        sideCheck.setText("Side Attack");
        sideCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkItemStateChanged(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("<HTML>Effective<br>Defense</HTML>");

        effectiveDefense.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        effectiveDefense.setText("97");

        name.setEditable(false);
        name.setBackground(new java.awt.Color(236, 233, 216));
        name.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        name.setText("name");
        name.setBorder(null);
        name.setFocusable(false);

        result.setEditable(false);
        result.setBackground(new java.awt.Color(236, 233, 216));
        result.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        result.setText("result");
        result.setBorder(null);
        result.setFocusable(false);

        shield_dr.setText("DR: 4");

        db.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        db.setText("DB: 2");

        shield_hp.setText("HP: 20/20");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("DR:");

        rollTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        rollTextField.setText("98");

        drTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));

        otherSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                otherSpinnerStateChanged(evt);
            }
        });

        locationCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        locationCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });
        locationCombo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                locationComboKeyReleased(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Location:");

        buttonGroup1.add(noneButton);
        noneButton.setText("None");
        noneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Defense Type:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(name)
            .addComponent(result)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addGap(1, 1, 1)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(dodgeButton)
                                        .addComponent(parryButton, javax.swing.GroupLayout.Alignment.LEADING))
                                    .addComponent(blockButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(blockNote)
                                    .addComponent(dodgeNote)
                                    .addComponent(parryNote)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(noneButton, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(3, 3, 3)
                                .addComponent(effectiveDefense, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(retreatCheck)
                            .addComponent(sideCheck)
                            .addComponent(eeCheck)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(shieldCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(db))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(otherSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(shield_dr)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(shield_hp))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel8)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(drTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(damageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(locationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rollTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rerollButton))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(shieldCheckBox)
                            .addComponent(db))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(shield_dr)
                            .addComponent(shield_hp)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(eeCheck)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(retreatCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sideCheck))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(parryButton)
                                    .addComponent(parryNote))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(blockButton)
                                    .addComponent(blockNote))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(dodgeButton)
                                    .addComponent(dodgeNote))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(noneButton)))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(otherSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 6, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(effectiveDefense, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(rollTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rerollButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(drTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(damageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(result, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(okButton)
                            .addComponent(cancelButton)))
                    .addComponent(locationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    	System.out.println("DefenseDialog: OK!");
    	valid = true;
    	setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    	System.out.println("DefenseDialog: CANCEL!");
    	setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void rerollButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rerollButtonActionPerformed
    	rollDefense();
    }//GEN-LAST:event_rerollButtonActionPerformed

    private void otherSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_otherSpinnerStateChanged
        calcResult();
    }//GEN-LAST:event_otherSpinnerStateChanged

    private void locationComboKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_locationComboKeyReleased
        calcResult();
    }//GEN-LAST:event_locationComboKeyReleased

    private void checkItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkItemStateChanged
        calcResult();
    }//GEN-LAST:event_checkItemStateChanged

    private void radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonActionPerformed
        calcResult();
    }//GEN-LAST:event_radioButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DefenseDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DefenseDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DefenseDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DefenseDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DefenseDialog dialog = new DefenseDialog(null, new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton blockButton;
    private javax.swing.JLabel blockNote;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField damageTextField;
    private javax.swing.JLabel db;
    private javax.swing.JRadioButton dodgeButton;
    private javax.swing.JLabel dodgeNote;
    private javax.swing.JFormattedTextField drTextField;
    private javax.swing.JCheckBox eeCheck;
    private javax.swing.JLabel effectiveDefense;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox locationCombo;
    private javax.swing.JTextField name;
    private javax.swing.JRadioButton noneButton;
    private javax.swing.JButton okButton;
    private javax.swing.JSpinner otherSpinner;
    private javax.swing.JRadioButton parryButton;
    private javax.swing.JLabel parryNote;
    private javax.swing.JButton rerollButton;
    private javax.swing.JTextField result;
    private javax.swing.JCheckBox retreatCheck;
    private javax.swing.JFormattedTextField rollTextField;
    private javax.swing.JCheckBox shieldCheckBox;
    private javax.swing.JLabel shield_dr;
    private javax.swing.JLabel shield_hp;
    private javax.swing.JCheckBox sideCheck;
    // End of variables declaration//GEN-END:variables
    
    public void setActor(Actor actor) {
    	this.actor = actor;
    	
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
    	
    	// Calculate effective base defense values
    	effParry = actor.Parry - actor.numParry * 4;
    	effBlock = actor.Block - actor.numBlock * 5;
    	effDodge = actor.Dodge;
    	if (actor.Injury > 2*actor.HP/3)
    		effDodge = (int) Math.ceil(effDodge/2.0);
    	if (actor.Fatigue > 2*actor.FP/3)
    		effDodge = (int) Math.ceil(effDodge/2.0);
    	
    	parryNote.setText(effParry + (effParry!=actor.Parry?" (base: " + actor.Parry + ")":""));
    	blockNote.setText(effBlock + (effBlock!=actor.Block?" (base: " + actor.Block + ")":""));
    	dodgeNote.setText(effDodge + (effDodge!=actor.Dodge?" (base: " + actor.Dodge + ")":""));
    	
    	// Pick the best
    	if (effParry > effDodge) {
    		if (effBlock > effParry) {
    			blockButton.setSelected(true);
    		} else {
    			parryButton.setSelected(true);
    		}
    	} else if (effBlock > effDodge) {
    		blockButton.setSelected(true);
    	} else {
    		dodgeButton.setSelected(true);
    	}
    	
    	shieldCheckBox.setSelected(actor.DB > 0 && actor.ShieldDamage < actor.ShieldHP);
    	db.setText("DB: " + actor.DB);
    	shield_dr.setText("DR: " + actor.ShieldDR);
    	shield_hp.setText("HP: " + (actor.ShieldHP-actor.ShieldDamage) + "/" + actor.ShieldHP);
    	drTextField.setText("" + actor.DR);
    	
    	rollDefense();
    	calcResult();

    	
    	// Setup doc listeners
    	JFormattedTextField field = (JFormattedTextField) otherSpinner.getEditor().getComponent(0);
    	DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
    	formatter.setCommitsOnValidEdit(true);
    	rollTextField.getDocument().addDocumentListener(new ValueDocumentListener());
    	drTextField.getDocument().addDocumentListener(new ValueDocumentListener());
    	damageTextField.getDocument().addDocumentListener(new ValueDocumentListener());
    }
    
    private void rollDefense() {
    	rollTextField.setText(String.valueOf(DieRoller.roll3d6()));
    }
    
    // Result reporting variables
	public int shieldDamage;
	public int injury;
	public int fatigue;
	public HitLocation location;

	public boolean majorWound;
	public boolean cripplingInjury;

	public enum DefenseResult {CritSuccess, Success, ShieldHit, Failure};
	public DefenseResult defenseResult;

	public enum DefenseType {Parry, Block, Dodge, None};
	public DefenseType defenseType;
	
	// Parsed results from the input text fields
	int roll;
	int dr;
	Damage damage;
 
    /**
     * Calculate the result of the attack
     */
    private void calcResult() {
    	if (actor == null) // Exit immediately if actor not initialized yet
    		return;

    	// Calculate effective defense value
    	calcEffectiveDefense();
    	
    	// Parse input fields. Return if any fail
    	if (!parseInputFields())
    		return;
    	
    	// Calculate the result of the defense
    	calcDefenseResult();
    	
    	// Calculate any injury
    	calcInjury();
    	
    	// Set the result message
    	setResultMessage();
    }
    
    /**
     * Calculate effective defense value
     */
    private void calcEffectiveDefense() {
    	if (actor == null) return;
    	
    	effectiveDefenseValue = 0;
    	fatigue = 0;
    	
    	if (parryButton.isSelected()) {
    		defenseType = DefenseType.Parry;
    		effectiveDefenseValue = effParry;
    	} else if (blockButton.isSelected()) {
    		defenseType = DefenseType.Block;
    		effectiveDefenseValue = effBlock;
    	} else if (dodgeButton.isSelected()) {
    		defenseType = DefenseType.Dodge;
    		effectiveDefenseValue = effDodge;
    	} else if (noneButton.isSelected()) {
    		defenseType = DefenseType.None;
    	} else {
    		System.out.println("-E- DefenseDialog: calcEffectiveDefense: no defense selected!");
    	}
    	
    	if (defenseType != DefenseType.None) {
	    	if (eeCheck.isSelected()) {
	    		effectiveDefenseValue += 2;
	    		fatigue = 1;
	    	}
	    	if (retreatCheck.isSelected())
	    		effectiveDefenseValue += dodgeButton.isSelected() ? 3 : 1;
	    	if (sideCheck.isSelected())
	    		effectiveDefenseValue -= 2;
	    	if (shieldCheckBox.isSelected())
	    		effectiveDefenseValue += actor.DB;
	    	
	    	// Other
	    	try {
	    		effectiveDefenseValue += Integer.parseInt(otherSpinner.getValue().toString());
	    	} catch (NumberFormatException e) {}
    	}
    	
    	effectiveDefense.setText(String.valueOf(effectiveDefenseValue));
    }
    
    /**
     * Parse the various input fields, verifying that they contain valid information;
     * @return if the parsing was successful
     */
    private boolean parseInputFields() {
      	boolean parseSuccess = true;
    	try { // Get roll results- set text to red and print error message if fails
    		roll = Integer.parseInt(rollTextField.getText());
    		rollTextField.setForeground(Color.BLACK);
    	} catch (Exception e) {
   		rollTextField.setForeground(Color.RED);
    		System.out.println("-E- DefenseDialog.parseInputFields: Error parsing roll field! '" + rollTextField.getText() + "': " + e.getMessage());
    		parseSuccess = false;
    	}    
    	try { // Get dr- set text to red and print error message if fails
    		dr = Integer.parseInt(drTextField.getText());
    		drTextField.setForeground(Color.BLACK);
    	} catch (Exception e) {
    		drTextField.setForeground(Color.RED);
    		System.out.println("-E- DefenseDialog.parseInputFields: Error parsing dr field! '" + drTextField.getText() + "': " + e.getMessage());
    		parseSuccess = false;
    	}    	
    	try { // Get damage- set text to red and print error message if fails
    		damage = Damage.ParseDamage(damageTextField.getText());
    		damageTextField.setForeground(Color.BLACK);
    	} catch (Exception e) {
   			damageTextField.setForeground(Color.RED);
    		System.out.println("-E- DefenseDialog.parseInputFields: Error parsing damage field! '" + damageTextField.getText() + "': " + e.getMessage());
    		parseSuccess = false;
    	}
    	return parseSuccess;
    }
    
    /**
     * Determine whether there was a hit (Shield/full)
     */
    private void calcDefenseResult() {
       	int shield_db = shieldCheckBox.isSelected() ? actor.DB : 0;
 
       	// CritSuccess, Success, ShieldHit, Failure
       	if (defenseType == DefenseType.None) { // No attempted defense
       		defenseResult = DefenseResult.Failure;
       	} else if (DieRoller.isCritSuccess(roll, effectiveDefenseValue)) { // Crit Defense!
    		defenseResult = DefenseResult.CritSuccess;
     	} else if (DieRoller.isFailure(roll, effectiveDefenseValue)) { // Hit
    		defenseResult = DefenseResult.Failure;
     	} else if (DieRoller.isFailure(roll, effectiveDefenseValue - shield_db)) { // Shield Hit
         	defenseResult = DefenseResult.ShieldHit;
    	}  else {
    		defenseResult = DefenseResult.Success;
     	}
    }
    
    /** 
     * Calculate any injury
     */
    private void calcInjury() {
		location = HitLocations.getLocationFromName((String) locationCombo.getSelectedItem());    
		int coverDR = 0;
		
		// Set defaults
    	injury = 0;
    	shieldDamage = 0;
    	majorWound = false;
    	cripplingInjury = false;

    	// Calculate injury
    	// 
    	switch (defenseResult) {
    	case CritSuccess:
    	case Success:
    		return;
    	case ShieldHit:
    		// Calculate shield damage
    		int shieldBasicDamage = (int) (damage.BasicDamage - Math.floor(actor.ShieldDR/damage.ArmorDivisor));
    		// Apply min/max values
    		shieldBasicDamage = Math.min((int)Math.ceil(actor.ShieldHP/4), shieldBasicDamage);
    		shieldBasicDamage = Math.max(0, shieldBasicDamage);
    		shieldDamage = (int) (Math.floor(shieldBasicDamage*damage.DamageMultiplierHomogenous()));
    		// Min damage 1 if any got through DR
    		shieldDamage = (shieldDamage <= 0 && shieldBasicDamage > 0)?1:shieldDamage; 
    		// Calculate total cover DR provided (including armor divisor)
    		coverDR = (int) (Math.floor(actor.ShieldDR/damage.ArmorDivisor) + Math.ceil(actor.ShieldHP/4));
    	case Failure:
    		// Calculate actual basic damage to the target, including any cover DR
			int totalDR = dr + location.extraDR;
    		int basicDamage = (int) (damage.BasicDamage - coverDR - Math.floor(totalDR/damage.ArmorDivisor));
			basicDamage = Math.max(0, basicDamage);
			// Calculate injury to the target
			double damageMultiplier = location.DamageMultiplier(damage.Type);
 			injury = (int) (basicDamage*damageMultiplier); 
    		injury = (injury <= 0 && basicDamage > 0)?1:injury; // Min damage 1 if any got through DR
    		// Check for crippling
    		if (location.cripplingThreshold != 0) {
    			int cripplingThreshold = (int) Math.floor(actor.HP * location.cripplingThreshold + 1.00001);
    			if (injury >= cripplingThreshold) {
    				injury = cripplingThreshold;
    				cripplingInjury = true;
    				majorWound = true;
    			}
    		} 
    		else { // Check for major wound
    			int majorWoundThreshold = (int) Math.floor(actor.HP * 1/2 + 1.00001);
    			if (injury >= majorWoundThreshold) {
    				majorWound = true;
    			}
    		}
    	}
    }
    
    private void setResultMessage() {
    	String message = "";
    	switch (defenseResult) {
       	case CritSuccess:
       		result.setText("Critical Defense!");
    		result.setBackground(new Color(200,200,255));
    		return;
    	case Success:
    		result.setText("Defended!");
        	result.setBackground(new Color(200,200,200));
        	return;
    	case ShieldHit:
    		message = "Shield ";
    	case Failure:
			message += "Hit";
    	}
    	
     	if (injury != 0) {
			result.setBackground(new Color(255,200,200));
    		message += ": " + injury;
    		if (cripplingInjury)
    			message += " crippling";
    		else if (majorWound)
    			message += " major";
    		message += " injury!";
    	}
    	else {
   			result.setBackground(new Color(200,200,200));
			message += ": no injury";
    	}
		if (defenseResult == DefenseResult.ShieldHit) {
			if (shieldDamage != 0 && injury == 0) {
    			result.setBackground(new Color(255,255,200));
	    	}
			message += " (" + shieldDamage + " shld dmg)";
		}
  
    	result.setText(message);
    }
    
	/**
	 * Internal class to listen to the changes in text components
	 */
	protected class ValueDocumentListener implements DocumentListener {
	    public void insertUpdate(DocumentEvent e) {
	    	calcResult();
	    }
	    public void removeUpdate(DocumentEvent e) {
	    	calcResult();
	    }
	    public void changedUpdate(DocumentEvent e) {
	    	calcResult();
	    }
	} 
	
	/**
	 * Internal class to listen to the changes in text components
	 */
	protected class ModDocumentListener implements DocumentListener {
	    public void insertUpdate(DocumentEvent e) {
	    	calcResult();
	    }
	    public void removeUpdate(DocumentEvent e) {
	    	calcResult();
	    }
	    public void changedUpdate(DocumentEvent e) {
	    	calcResult();
	    }
	} 
}
