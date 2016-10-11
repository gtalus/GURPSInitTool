/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gurpsinittool.ui;

import gurpsinittool.data.GameSettings;
import gurpsinittool.util.MiscUtil;

import java.util.Properties;

/**
 *
 * @author dcsmall
 */
public class OptionsWindow extends javax.swing.JFrame {

    @Override
	public void setVisible(boolean visible) {
    	if (visible) {
	    	MiscUtil.validateOnScreen(this); // Make sure the window is visible on screen!
	   		if (!isVisible()) { 
	   			copySettingsToComponents(); // If we're not visible and being made visible, get the current settings
	   		} else if (getState() == java.awt.Frame.ICONIFIED) {
	   			setState(java.awt.Frame.NORMAL); // If the window is iconified, make it visible
	   		}
    	}
		super.setVisible(visible);
	}

	public GameSettings currentSettings;
    private Properties propertyBag;
    
    /**
     * Creates new form OptionsWindow
     */
    public OptionsWindow(Properties propertyBag) {
        this.currentSettings = new GameSettings();
        this.propertyBag = propertyBag;
        initComponents();
        
        setDefaultProperties();
        setLocation(Integer.valueOf(propertyBag.getProperty("Options.location.x")),
                Integer.valueOf(propertyBag.getProperty("Options.location.y")));
//        setSize(Integer.valueOf(propertyBag.getProperty("Options.size.width")),
//        		Integer.valueOf(propertyBag.getProperty("Options.size.height")));
        
		 if (propertyBag.containsKey("Options.game.AUTO_ATTACK")) {
			 currentSettings.AUTO_ATTACK = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTO_ATTACK")); }
		 if (propertyBag.containsKey("Options.game.AUTO_KNOCKDOWNSTUN")) {
			 currentSettings.AUTO_KNOCKDOWNSTUN = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTO_KNOCKDOWNSTUN")); }
		 if (propertyBag.containsKey("Options.game.AUTO_STUNRECOVERY")) {
			 currentSettings.AUTO_STUNRECOVERY = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTO_STUNRECOVERY")); }
		 if (propertyBag.containsKey("Options.game.AUTO_UNCONSCIOUS")) {
			 currentSettings.AUTO_UNCONSCIOUS = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTO_UNCONSCIOUS")); }
		 if (propertyBag.containsKey("Options.game.AUTOMATE_PC")) {
			 currentSettings.AUTOMATE_PC = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTOMATE_PC")); }
		 if (propertyBag.containsKey("Options.game.AUTOMATE_ENEMY")) {
			 currentSettings.AUTOMATE_ENEMY = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTOMATE_ENEMY")); }
		 if (propertyBag.containsKey("Options.game.AUTOMATE_ALLY")) {
			 currentSettings.AUTOMATE_ALLY = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTOMATE_ALLY")); }
		 if (propertyBag.containsKey("Options.game.AUTOMATE_NEUTRAL")) {
			 currentSettings.AUTOMATE_NEUTRAL = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTOMATE_NEUTRAL")); }
		 if (propertyBag.containsKey("Options.game.AUTOMATE_SPECIAL")) {
			 currentSettings.AUTOMATE_SPECIAL = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTOMATE_SPECIAL")); }
		 if (propertyBag.containsKey("Options.game.AUTO_SHOCK")) {
			 currentSettings.AUTO_SHOCK = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTO_SHOCK")); }
		 if (propertyBag.containsKey("Options.game.LOG_STATUSCHANGES")) {
			 currentSettings.LOG_STATUSCHANGES = Boolean.valueOf(propertyBag.getProperty("Options.game.LOG_STATUSCHANGES")); }
		 if (propertyBag.containsKey("Options.game.LOG_DEFENSEDETAILS")) {
			 currentSettings.LOG_DEFENSEDETAILS = Boolean.valueOf(propertyBag.getProperty("Options.game.LOG_DEFENSEDETAILS")); }
    }

    /**
	  * Set default properties if they are not already defined.
	  */
	 private void setDefaultProperties() {
		 if (!propertyBag.containsKey("Options.location.x")) {
			 propertyBag.setProperty("Options.location.x", "500"); }
		 if (!propertyBag.containsKey("Options.location.y")) {
			 propertyBag.setProperty("Options.location.y", "300"); }
//		 if (!propertyBag.containsKey("Options.size.width")) {
//			 propertyBag.setProperty("Options.size.width",  String.valueOf(getPreferredSize().width)); }
//		 if (!propertyBag.containsKey("Options.size.height")) {
//			 propertyBag.setProperty("Options.size.height",  String.valueOf(getPreferredSize().height)); }
	 }
	 
	 /**
	  * Update all the store-able properties to their current values
	  */
	 public void updateProperties() {
		 // Kept up-to-date with event listeners
		 propertyBag.setProperty("Options.location.x", String.valueOf(getLocation().x));
		 propertyBag.setProperty("Options.location.y", String.valueOf(getLocation().y));
//		 propertyBag.setProperty("Options.size.width", String.valueOf(getSize().width));
//		 propertyBag.setProperty("Options.size.height", String.valueOf(getSize().height));
		 propertyBag.setProperty("Options.game.AUTO_ATTACK", String.valueOf(currentSettings.AUTO_ATTACK));
		 propertyBag.setProperty("Options.game.AUTO_KNOCKDOWNSTUN", String.valueOf(currentSettings.AUTO_KNOCKDOWNSTUN));
		 propertyBag.setProperty("Options.game.AUTO_STUNRECOVERY", String.valueOf(currentSettings.AUTO_STUNRECOVERY));
		 propertyBag.setProperty("Options.game.AUTO_UNCONSCIOUS", String.valueOf(currentSettings.AUTO_UNCONSCIOUS));
		 propertyBag.setProperty("Options.game.AUTOMATE_PC", String.valueOf(currentSettings.AUTOMATE_PC));
		 propertyBag.setProperty("Options.game.AUTOMATE_ENEMY", String.valueOf(currentSettings.AUTOMATE_ENEMY));
		 propertyBag.setProperty("Options.game.AUTOMATE_ALLY", String.valueOf(currentSettings.AUTOMATE_ALLY));
		 propertyBag.setProperty("Options.game.AUTOMATE_NEUTRAL", String.valueOf(currentSettings.AUTOMATE_NEUTRAL));
		 propertyBag.setProperty("Options.game.AUTOMATE_SPECIAL", String.valueOf(currentSettings.AUTOMATE_SPECIAL));
		 propertyBag.setProperty("Options.game.AUTO_SHOCK", String.valueOf(currentSettings.AUTO_SHOCK));
		 propertyBag.setProperty("Options.game.LOG_STATUSCHANGES", String.valueOf(currentSettings.LOG_STATUSCHANGES));
		 propertyBag.setProperty("Options.game.LOG_DEFENSEDETAILS", String.valueOf(currentSettings.LOG_DEFENSEDETAILS));
	 }
	 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        localSettings = new gurpsinittool.data.GameSettings();
        combatAutomationPanel = new javax.swing.JPanel();
        auto_attack = new javax.swing.JCheckBox();
        auto_unconsciousness = new javax.swing.JCheckBox();
        auto_knockdown = new javax.swing.JCheckBox();
        auto_stunrecovery = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        pc_automated = new javax.swing.JCheckBox();
        enemy_automated = new javax.swing.JCheckBox();
        ally_automated = new javax.swing.JCheckBox();
        neutral_automated = new javax.swing.JCheckBox();
        special_automated = new javax.swing.JCheckBox();
        okButton = new javax.swing.JButton();
        applyButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        log_defensedetails = new javax.swing.JCheckBox();
        log_statuschanges = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        auto_shock = new javax.swing.JCheckBox();

        setTitle("InitTool Settings");

        combatAutomationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Automation"));

        auto_attack.setText("Attack");
        auto_attack.setToolTipText("NPCs with the 'Attacking' status automatically perform their default attack when it is their turn, as long as they are not Stunned.");

        auto_unconsciousness.setText("Unconsciousness Checks");
        auto_unconsciousness.setToolTipText("NPCs check for unconsciousness at the start of each of their turns.");

        auto_knockdown.setText("Knockdown and Stunning");
        auto_knockdown.setToolTipText("NPCs check for knockdown and stunning when taking sufficient damage.");

        auto_stunrecovery.setText("Stun Recovery");
        auto_stunrecovery.setToolTipText("Stunned NPCs check for stun recovery at the start of their turns.");

        jLabel1.setText("Combatant types automated:");

        pc_automated.setText("PC");
        pc_automated.setToolTipText("NPCs with the 'Attacking' status automatically perform their default attack when it is their turn, as long as they are not Stunned.");

        enemy_automated.setText("Enemy");
        enemy_automated.setToolTipText("NPCs with the 'Attacking' status automatically perform their default attack when it is their turn, as long as they are not Stunned.");

        ally_automated.setText("Ally");
        ally_automated.setToolTipText("NPCs with the 'Attacking' status automatically perform their default attack when it is their turn, as long as they are not Stunned.");

        neutral_automated.setText("Neutral");
        neutral_automated.setToolTipText("NPCs with the 'Attacking' status automatically perform their default attack when it is their turn, as long as they are not Stunned.");

        special_automated.setText("Special");
        special_automated.setToolTipText("NPCs with the 'Attacking' status automatically perform their default attack when it is their turn, as long as they are not Stunned.");

        javax.swing.GroupLayout combatAutomationPanelLayout = new javax.swing.GroupLayout(combatAutomationPanel);
        combatAutomationPanel.setLayout(combatAutomationPanelLayout);
        combatAutomationPanelLayout.setHorizontalGroup(
            combatAutomationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(combatAutomationPanelLayout.createSequentialGroup()
                .addGroup(combatAutomationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(auto_attack)
                    .addComponent(auto_unconsciousness)
                    .addComponent(auto_knockdown)
                    .addComponent(auto_stunrecovery)
                    .addGroup(combatAutomationPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(combatAutomationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(combatAutomationPanelLayout.createSequentialGroup()
                                .addComponent(pc_automated)
                                .addGap(18, 18, 18))
                            .addComponent(enemy_automated)
                            .addComponent(ally_automated)
                            .addComponent(neutral_automated)
                            .addComponent(special_automated)))
                    .addGroup(combatAutomationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        combatAutomationPanelLayout.setVerticalGroup(
            combatAutomationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(combatAutomationPanelLayout.createSequentialGroup()
                .addComponent(auto_attack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(auto_unconsciousness)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(auto_knockdown)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(auto_stunrecovery)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(1, 1, 1)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pc_automated)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enemy_automated)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ally_automated)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(neutral_automated)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(special_automated)
                .addGap(0, 7, Short.MAX_VALUE))
        );

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        applyButton.setText("Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Logging"));

        log_defensedetails.setText("Defense details");
        log_defensedetails.setToolTipText("Log defense details (retreat/EE/side/etc.)");

        log_statuschanges.setText("Verbose Status Changes");
        log_statuschanges.setToolTipText("Log all status changes");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(log_statuschanges)
                    .addComponent(log_defensedetails))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(log_statuschanges)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(log_defensedetails))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Game Rules"));

        auto_shock.setText("Shock");
        auto_shock.setToolTipText("Track and apply shock to all attack rolls");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(auto_shock)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(auto_shock)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(combatAutomationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 44, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(applyButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(combatAutomationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(applyButton)
                    .addComponent(okButton)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        System.out.println("DefenseDialog: OK!");
        copyComponentsToSettings();
    	setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        System.out.println("OptionsWindow: Apply!");
        copyComponentsToSettings();
    }//GEN-LAST:event_applyButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void copySettingsToComponents() {
    	auto_attack.setSelected(currentSettings.AUTO_ATTACK);
    	auto_knockdown.setSelected(currentSettings.AUTO_KNOCKDOWNSTUN);
    	auto_stunrecovery.setSelected(currentSettings.AUTO_STUNRECOVERY);
    	auto_unconsciousness.setSelected(currentSettings.AUTO_UNCONSCIOUS);
    	pc_automated.setSelected(currentSettings.AUTOMATE_PC);
    	enemy_automated.setSelected(currentSettings.AUTOMATE_ENEMY);
    	ally_automated.setSelected(currentSettings.AUTOMATE_ALLY);
    	neutral_automated.setSelected(currentSettings.AUTOMATE_NEUTRAL);
    	special_automated.setSelected(currentSettings.AUTOMATE_SPECIAL);
    	auto_shock.setSelected(currentSettings.AUTO_SHOCK);
        log_statuschanges.setSelected(currentSettings.LOG_STATUSCHANGES);
        log_defensedetails.setSelected(currentSettings.LOG_DEFENSEDETAILS);
    }
    
    public void copyComponentsToSettings() {
    	currentSettings.AUTO_ATTACK = auto_attack.isSelected();
    	currentSettings.AUTO_KNOCKDOWNSTUN = auto_knockdown.isSelected();
    	currentSettings.AUTO_STUNRECOVERY = auto_stunrecovery.isSelected();
    	currentSettings.AUTO_UNCONSCIOUS = auto_unconsciousness.isSelected();   
    	currentSettings.AUTO_SHOCK = auto_shock.isSelected();
    	currentSettings.AUTOMATE_PC = pc_automated.isSelected();
       	currentSettings.AUTOMATE_ENEMY = enemy_automated.isSelected();
       	currentSettings.AUTOMATE_ALLY = ally_automated.isSelected();
       	currentSettings.AUTOMATE_NEUTRAL = neutral_automated.isSelected();
       	currentSettings.AUTOMATE_SPECIAL = special_automated.isSelected();
    	currentSettings.LOG_STATUSCHANGES = log_statuschanges.isSelected();
    	currentSettings.LOG_DEFENSEDETAILS = log_defensedetails.isSelected();
    }
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
            java.util.logging.Logger.getLogger(OptionsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OptionsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OptionsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OptionsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OptionsWindow(new Properties()).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox ally_automated;
    private javax.swing.JButton applyButton;
    private javax.swing.JCheckBox auto_attack;
    private javax.swing.JCheckBox auto_knockdown;
    private javax.swing.JCheckBox auto_shock;
    private javax.swing.JCheckBox auto_stunrecovery;
    private javax.swing.JCheckBox auto_unconsciousness;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel combatAutomationPanel;
    private javax.swing.JCheckBox enemy_automated;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private gurpsinittool.data.GameSettings localSettings;
    private javax.swing.JCheckBox log_defensedetails;
    private javax.swing.JCheckBox log_statuschanges;
    private javax.swing.JCheckBox neutral_automated;
    private javax.swing.JButton okButton;
    private javax.swing.JCheckBox pc_automated;
    private javax.swing.JCheckBox special_automated;
    // End of variables declaration//GEN-END:variables
}
