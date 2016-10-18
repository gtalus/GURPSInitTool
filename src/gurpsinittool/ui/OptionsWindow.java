/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gurpsinittool.ui;

import gurpsinittool.data.GameSettings;
import gurpsinittool.util.MiscUtil;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dcsmall
 */
@SuppressWarnings("serial")
public class OptionsWindow extends javax.swing.JFrame {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(OptionsWindow.class.getName());

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
			 currentSettings.autoAttack = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTO_ATTACK")); }
		 if (propertyBag.containsKey("Options.game.AUTO_KNOCKDOWNSTUN")) {
			 currentSettings.autoKnockdownStun = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTO_KNOCKDOWNSTUN")); }
		 if (propertyBag.containsKey("Options.game.AUTO_STUNRECOVERY")) {
			 currentSettings.autoStunRecovery = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTO_STUNRECOVERY")); }
		 if (propertyBag.containsKey("Options.game.AUTO_UNCONSCIOUS")) {
			 currentSettings.autoUnconscious = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTO_UNCONSCIOUS")); }
		 if (propertyBag.containsKey("Options.game.AUTOMATE_PC")) {
			 currentSettings.automatePC = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTOMATE_PC")); }
		 if (propertyBag.containsKey("Options.game.AUTOMATE_ENEMY")) {
			 currentSettings.automateEnemy = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTOMATE_ENEMY")); }
		 if (propertyBag.containsKey("Options.game.AUTOMATE_ALLY")) {
			 currentSettings.automateAlly = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTOMATE_ALLY")); }
		 if (propertyBag.containsKey("Options.game.AUTOMATE_NEUTRAL")) {
			 currentSettings.automateNeutral = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTOMATE_NEUTRAL")); }
		 if (propertyBag.containsKey("Options.game.AUTOMATE_SPECIAL")) {
			 currentSettings.automateSpecial = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTOMATE_SPECIAL")); }
		 if (propertyBag.containsKey("Options.game.AUTO_SHOCK")) {
			 currentSettings.autoShock = Boolean.valueOf(propertyBag.getProperty("Options.game.AUTO_SHOCK")); }
		 if (propertyBag.containsKey("Options.game.LOG_STATUSCHANGES")) {
			 currentSettings.logStatusChanges = Boolean.valueOf(propertyBag.getProperty("Options.game.LOG_STATUSCHANGES")); }
		 if (propertyBag.containsKey("Options.game.LOG_DEFENSEDETAILS")) {
			 currentSettings.logDefenseDetails = Boolean.valueOf(propertyBag.getProperty("Options.game.LOG_DEFENSEDETAILS")); }
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
		 propertyBag.setProperty("Options.game.AUTO_ATTACK", String.valueOf(currentSettings.autoAttack));
		 propertyBag.setProperty("Options.game.AUTO_KNOCKDOWNSTUN", String.valueOf(currentSettings.autoKnockdownStun));
		 propertyBag.setProperty("Options.game.AUTO_STUNRECOVERY", String.valueOf(currentSettings.autoStunRecovery));
		 propertyBag.setProperty("Options.game.AUTO_UNCONSCIOUS", String.valueOf(currentSettings.autoUnconscious));
		 propertyBag.setProperty("Options.game.AUTOMATE_PC", String.valueOf(currentSettings.automatePC));
		 propertyBag.setProperty("Options.game.AUTOMATE_ENEMY", String.valueOf(currentSettings.automateEnemy));
		 propertyBag.setProperty("Options.game.AUTOMATE_ALLY", String.valueOf(currentSettings.automateAlly));
		 propertyBag.setProperty("Options.game.AUTOMATE_NEUTRAL", String.valueOf(currentSettings.automateNeutral));
		 propertyBag.setProperty("Options.game.AUTOMATE_SPECIAL", String.valueOf(currentSettings.automateSpecial));
		 propertyBag.setProperty("Options.game.AUTO_SHOCK", String.valueOf(currentSettings.autoShock));
		 propertyBag.setProperty("Options.game.LOG_STATUSCHANGES", String.valueOf(currentSettings.logStatusChanges));
		 propertyBag.setProperty("Options.game.LOG_DEFENSEDETAILS", String.valueOf(currentSettings.logDefenseDetails));
	 }
	 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        combatAutomationPanel = new javax.swing.JPanel();
        autoAttack = new javax.swing.JCheckBox();
        autoUnconsciousness = new javax.swing.JCheckBox();
        autoKnockdown = new javax.swing.JCheckBox();
        autoStunrecovery = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        pcAutomated = new javax.swing.JCheckBox();
        enemyAutomated = new javax.swing.JCheckBox();
        allyAutomated = new javax.swing.JCheckBox();
        neutralAutomated = new javax.swing.JCheckBox();
        specialAutomated = new javax.swing.JCheckBox();
        okButton = new javax.swing.JButton();
        applyButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        logDefenseDetails = new javax.swing.JCheckBox();
        logStatusChanges = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        autoShock = new javax.swing.JCheckBox();

        setTitle("InitTool Settings");

        combatAutomationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Automation"));

        autoAttack.setText("Attack");
        autoAttack.setToolTipText("NPCs with the 'Attacking' status automatically perform their default attack when it is their turn, as long as they are not Stunned.");

        autoUnconsciousness.setText("Unconsciousness Checks");
        autoUnconsciousness.setToolTipText("NPCs check for unconsciousness at the start of each of their turns.");

        autoKnockdown.setText("Knockdown and Stunning");
        autoKnockdown.setToolTipText("NPCs check for knockdown and stunning when taking sufficient damage.");

        autoStunrecovery.setText("Stun Recovery");
        autoStunrecovery.setToolTipText("Stunned NPCs check for stun recovery at the start of their turns.");

        jLabel1.setText("Combatant types automated:");

        pcAutomated.setText("PC");
        pcAutomated.setToolTipText("NPCs with the 'Attacking' status automatically perform their default attack when it is their turn, as long as they are not Stunned.");

        enemyAutomated.setText("Enemy");
        enemyAutomated.setToolTipText("NPCs with the 'Attacking' status automatically perform their default attack when it is their turn, as long as they are not Stunned.");

        allyAutomated.setText("Ally");
        allyAutomated.setToolTipText("NPCs with the 'Attacking' status automatically perform their default attack when it is their turn, as long as they are not Stunned.");

        neutralAutomated.setText("Neutral");
        neutralAutomated.setToolTipText("NPCs with the 'Attacking' status automatically perform their default attack when it is their turn, as long as they are not Stunned.");

        specialAutomated.setText("Special");
        specialAutomated.setToolTipText("NPCs with the 'Attacking' status automatically perform their default attack when it is their turn, as long as they are not Stunned.");

        javax.swing.GroupLayout combatAutomationPanelLayout = new javax.swing.GroupLayout(combatAutomationPanel);
        combatAutomationPanel.setLayout(combatAutomationPanelLayout);
        combatAutomationPanelLayout.setHorizontalGroup(
            combatAutomationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(combatAutomationPanelLayout.createSequentialGroup()
                .addGroup(combatAutomationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(autoAttack)
                    .addComponent(autoUnconsciousness)
                    .addComponent(autoKnockdown)
                    .addComponent(autoStunrecovery)
                    .addGroup(combatAutomationPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(combatAutomationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pcAutomated)
                            .addComponent(enemyAutomated)
                            .addComponent(allyAutomated)
                            .addComponent(neutralAutomated)
                            .addComponent(specialAutomated)))
                    .addGroup(combatAutomationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        combatAutomationPanelLayout.setVerticalGroup(
            combatAutomationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(combatAutomationPanelLayout.createSequentialGroup()
                .addComponent(autoAttack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autoUnconsciousness)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autoKnockdown)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autoStunrecovery)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(1, 1, 1)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pcAutomated)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enemyAutomated)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(allyAutomated)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(neutralAutomated)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(specialAutomated)
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

        logDefenseDetails.setText("Defense details");
        logDefenseDetails.setToolTipText("Log defense details (retreat/EE/side/etc.)");

        logStatusChanges.setText("Verbose Status Changes");
        logStatusChanges.setToolTipText("Log all status changes");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logStatusChanges)
                    .addComponent(logDefenseDetails))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(logStatusChanges)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logDefenseDetails))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Game Rules"));

        autoShock.setText("Shock");
        autoShock.setToolTipText("Track and apply shock to all attack rolls");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(autoShock)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(autoShock)
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
        if (LOG.isLoggable(Level.FINE)) {LOG.fine("DefenseDialog: OK!");}
        copyComponentsToSettings();
    	setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
    	if (LOG.isLoggable(Level.FINE)) {LOG.fine("OptionsWindow: Apply!");}
        copyComponentsToSettings();
    }//GEN-LAST:event_applyButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void copySettingsToComponents() {
    	autoAttack.setSelected(currentSettings.autoAttack);
    	autoKnockdown.setSelected(currentSettings.autoKnockdownStun);
    	autoStunrecovery.setSelected(currentSettings.autoStunRecovery);
    	autoUnconsciousness.setSelected(currentSettings.autoUnconscious);
    	pcAutomated.setSelected(currentSettings.automatePC);
    	enemyAutomated.setSelected(currentSettings.automateEnemy);
    	allyAutomated.setSelected(currentSettings.automateAlly);
    	neutralAutomated.setSelected(currentSettings.automateNeutral);
    	specialAutomated.setSelected(currentSettings.automateSpecial);
    	autoShock.setSelected(currentSettings.autoShock);
        logStatusChanges.setSelected(currentSettings.logStatusChanges);
        logDefenseDetails.setSelected(currentSettings.logDefenseDetails);
    }
    
    public void copyComponentsToSettings() {
    	currentSettings.autoAttack = autoAttack.isSelected();
    	currentSettings.autoKnockdownStun = autoKnockdown.isSelected();
    	currentSettings.autoStunRecovery = autoStunrecovery.isSelected();
    	currentSettings.autoUnconscious = autoUnconsciousness.isSelected();   
    	currentSettings.autoShock = autoShock.isSelected();
    	currentSettings.automatePC = pcAutomated.isSelected();
       	currentSettings.automateEnemy = enemyAutomated.isSelected();
       	currentSettings.automateAlly = allyAutomated.isSelected();
       	currentSettings.automateNeutral = neutralAutomated.isSelected();
       	currentSettings.automateSpecial = specialAutomated.isSelected();
    	currentSettings.logStatusChanges = logStatusChanges.isSelected();
    	currentSettings.logDefenseDetails = logDefenseDetails.isSelected();
    }
    
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
    private javax.swing.JCheckBox allyAutomated;
    private javax.swing.JButton applyButton;
    private javax.swing.JCheckBox autoAttack;
    private javax.swing.JCheckBox autoKnockdown;
    private javax.swing.JCheckBox autoShock;
    private javax.swing.JCheckBox autoStunrecovery;
    private javax.swing.JCheckBox autoUnconsciousness;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel combatAutomationPanel;
    private javax.swing.JCheckBox enemyAutomated;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBox logDefenseDetails;
    private javax.swing.JCheckBox logStatusChanges;
    private javax.swing.JCheckBox neutralAutomated;
    private javax.swing.JButton okButton;
    private javax.swing.JCheckBox pcAutomated;
    private javax.swing.JCheckBox specialAutomated;
    // End of variables declaration//GEN-END:variables
}
