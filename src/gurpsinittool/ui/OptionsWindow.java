/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gurpsinittool.ui;

import gurpsinittool.data.GameSettings;
import gurpsinittool.data.GameSettings.GameSetting;
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
        
        getSettingProperty(currentSettings.autoAttack);
        getSettingProperty(currentSettings.autoUnconscious);
        getSettingProperty(currentSettings.autoKnockdownStun);
        getSettingProperty(currentSettings.autoStunRecovery);
        getSettingProperty(currentSettings.autoShock);
        getSettingProperty(currentSettings.automatePC);
        getSettingProperty(currentSettings.automateEnemy);
        getSettingProperty(currentSettings.automateAlly);
        getSettingProperty(currentSettings.automateNeutral);
        getSettingProperty(currentSettings.automateSpecial);
        getSettingProperty(currentSettings.logStatusChanges);
        getSettingProperty(currentSettings.logDefenseDetails);
    }
    // Get a GameSetting's value from the propertyBag
    private void getSettingProperty(final GameSetting setting) {
    	String settingPath = "Options.game." + setting.getName();
    	if (propertyBag.containsKey(settingPath)) {
    		setting.setValue(Boolean.valueOf(propertyBag.getProperty(settingPath))); }
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
		 saveSettingProperty(currentSettings.autoAttack);
		 saveSettingProperty(currentSettings.autoUnconscious);
		 saveSettingProperty(currentSettings.autoKnockdownStun);
		 saveSettingProperty(currentSettings.autoStunRecovery);
		 saveSettingProperty(currentSettings.autoShock);
		 saveSettingProperty(currentSettings.automatePC);
		 saveSettingProperty(currentSettings.automateEnemy);
		 saveSettingProperty(currentSettings.automateAlly);
		 saveSettingProperty(currentSettings.automateNeutral);
		 saveSettingProperty(currentSettings.automateSpecial);
		 saveSettingProperty(currentSettings.logStatusChanges);
		 saveSettingProperty(currentSettings.logDefenseDetails);
	 }
	 // Save a GameSetting's value to the propertyBag
	 private void saveSettingProperty(final GameSetting setting) {
		 propertyBag.setProperty("Options.game." + setting.getName(), String.valueOf(setting.isSet()));
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
        autoAttack.setToolTipText(currentSettings.autoAttack.getDescription());

        autoUnconsciousness.setText("Unconsciousness Checks");
        autoUnconsciousness.setToolTipText(currentSettings.autoUnconscious.getDescription());

        autoKnockdown.setText("Knockdown and Stunning");
        autoKnockdown.setToolTipText(currentSettings.autoKnockdownStun.getDescription());

        autoStunrecovery.setText("Stun Recovery");
        autoStunrecovery.setToolTipText(currentSettings.autoStunRecovery.getDescription());

        jLabel1.setText("Combatant types automated:");

        pcAutomated.setText("PC");
        pcAutomated.setToolTipText(currentSettings.automatePC.getDescription());

        enemyAutomated.setText("Enemy");
        enemyAutomated.setToolTipText(currentSettings.automateEnemy.getDescription());

        allyAutomated.setText("Ally");
        allyAutomated.setToolTipText(currentSettings.automateAlly.getDescription());

        neutralAutomated.setText("Neutral");
        neutralAutomated.setToolTipText(currentSettings.automateNeutral.getDescription());

        specialAutomated.setText("Special");
        specialAutomated.setToolTipText(currentSettings.automateSpecial.getDescription());

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
        logDefenseDetails.setToolTipText(currentSettings.logStatusChanges.getDescription());

        logStatusChanges.setText("Verbose Status Changes");
        logStatusChanges.setToolTipText(currentSettings.logStatusChanges.getDescription());

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
        autoShock.setToolTipText(currentSettings.autoShock.getDescription());

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
    	autoAttack.setSelected(currentSettings.autoAttack.isSet());
    	autoKnockdown.setSelected(currentSettings.autoKnockdownStun.isSet());
    	autoStunrecovery.setSelected(currentSettings.autoStunRecovery.isSet());
    	autoUnconsciousness.setSelected(currentSettings.autoUnconscious.isSet());
    	pcAutomated.setSelected(currentSettings.automatePC.isSet());
    	enemyAutomated.setSelected(currentSettings.automateEnemy.isSet());
    	allyAutomated.setSelected(currentSettings.automateAlly.isSet());
    	neutralAutomated.setSelected(currentSettings.automateNeutral.isSet());
    	specialAutomated.setSelected(currentSettings.automateSpecial.isSet());
    	autoShock.setSelected(currentSettings.autoShock.isSet());
        logStatusChanges.setSelected(currentSettings.logStatusChanges.isSet());
        logDefenseDetails.setSelected(currentSettings.logDefenseDetails.isSet());
    }
    
    public void copyComponentsToSettings() {
    	currentSettings.autoAttack.setValue(autoAttack.isSelected());
    	currentSettings.autoKnockdownStun.setValue(autoKnockdown.isSelected());
    	currentSettings.autoStunRecovery.setValue(autoStunrecovery.isSelected());
    	currentSettings.autoUnconscious.setValue(autoUnconsciousness.isSelected());   
    	currentSettings.autoShock.setValue(autoShock.isSelected());
    	currentSettings.automatePC.setValue(pcAutomated.isSelected());
       	currentSettings.automateEnemy.setValue(enemyAutomated.isSelected());
       	currentSettings.automateAlly.setValue(allyAutomated.isSelected());
       	currentSettings.automateNeutral.setValue(neutralAutomated.isSelected());
       	currentSettings.automateSpecial.setValue(specialAutomated.isSelected());
    	currentSettings.logStatusChanges.setValue(logStatusChanges.isSelected());
    	currentSettings.logDefenseDetails.setValue(logDefenseDetails.isSelected());
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
