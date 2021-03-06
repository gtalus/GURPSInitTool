/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gurpsinittool.ui;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import gurpsinittool.app.InitTableModel;
import gurpsinittool.data.ActorBase.BasicTrait;
import gurpsinittool.data.ActorBase.CalculatedTrait;
import gurpsinittool.util.MiscUtil;

/**
 * UI component used for customizing columns in an InitTable
 * @author dcsmall
 */
@SuppressWarnings("serial")
public class ColumnCustomizer extends javax.swing.JFrame {

	public static final ArrayList<String> DEFAULT_COLUMNS = new ArrayList<String>(Arrays.asList("Act", "Name", "Speed", "Move", "HT", "HP", "Injury", "FP", "Fatigue", "Status"));
	private InitTableModel tableModel;
	
	private DefaultListModel<String> availableTraitsModel;
	private DefaultListModel<String> displayedTraitsModel;
	private ArrayList<String> visibleColumns;
	
    /**
     * Creates new form ColumnCustomizer
     */
    public ColumnCustomizer(InitTableModel tableModel) {
    	this.tableModel = tableModel;
        initComponents();
        availableTraitsModel = (DefaultListModel<String>) (availableTraitsList.getModel());
        displayedTraitsModel = (DefaultListModel<String>) (displayedTraitsList.getModel());
    }

    @Override
	public void setVisible(boolean visible) {
    	if (visible) {
	    	MiscUtil.validateOnScreen(this); // Make sure the window is visible on screen!
	   		if (!isVisible()) { 
	   			getVisibleColumnsFromModel(); // Get the current visible columns from the model
	   			initializeColumnLists(); // update the column lists
	   		} else if (getState() == java.awt.Frame.ICONIFIED) {
	   			setState(java.awt.Frame.NORMAL); // If the window is iconified, make it visible
	   		}
    	}
		super.setVisible(visible);
	}
    
    private void getVisibleColumnsFromModel() {
    	// Currently visible columns
    	visibleColumns = tableModel.getColumnNames();
    	visibleColumns.remove("Act"); // get rid of "Act" column, if present
    }
    
    private void initializeColumnLists() {
    	// All columns
    	ArrayList<String> availableColumns = new ArrayList<String>();
    	for (BasicTrait basicTrait : BasicTrait.values()) {
    		availableColumns.add(basicTrait.name());
    	}
    	for (CalculatedTrait calcTrait : CalculatedTrait.values()) {
    		availableColumns.add(calcTrait.name());
    	}
    	// Special columns:
    	availableColumns.add("Type");
    	//availableColumns.add("currHP/HP");
       	//availableColumns.add("currFP/FP");
    	
    	// Remove visible from available
    	for (String visibleColumn: visibleColumns) {
    		availableColumns.remove(visibleColumn);
    	}
    	
    	// TODO: Sort?
    	
    	// Set column Lists
    	displayedTraitsModel.clear();
    	for(String value: visibleColumns) 
    		displayedTraitsModel.addElement(value);
    	availableTraitsModel.clear();
    	for (String value: availableColumns)
    		availableTraitsModel.addElement(value);
     }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        availableTraitsList = new javax.swing.JList<String>();
        jScrollPane2 = new javax.swing.JScrollPane();
        displayedTraitsList = new javax.swing.JList<String>();
        addButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        removeButton = new javax.swing.JButton();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        customTraitButton = new javax.swing.JButton();

        setTitle("InitTool Column Customizer");

        availableTraitsList.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        availableTraitsList.setModel(new DefaultListModel<String>());
        availableTraitsList.setMinimumSize(new java.awt.Dimension(50, 80));
        jScrollPane1.setViewportView(availableTraitsList);

        displayedTraitsList.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        displayedTraitsList.setModel(new DefaultListModel<String>());
        jScrollPane2.setViewportView(displayedTraitsList);

        addButton.setText("Add >");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Available Basic Traits");

        removeButton.setText("< Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        moveUpButton.setText("Move Up");
        moveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpButtonActionPerformed(evt);
            }
        });

        moveDownButton.setText("Move Down");
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownButtonActionPerformed(evt);
            }
        });

        resetButton.setText("Reset to Default");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Displayed Traits Order");

        customTraitButton.setText("Add Custom Trait");
        customTraitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customTraitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(moveDownButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(moveUpButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(addButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(removeButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(customTraitButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(customTraitButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(moveUpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(moveDownButton)
                        .addGap(0, 131, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton)
                    .addComponent(resetButton)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
     	for (String selected: availableTraitsList.getSelectedValuesList()) {
    		availableTraitsModel.removeElement(selected);
    		displayedTraitsModel.addElement(selected);
    	}
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
     	for (String selected: displayedTraitsList.getSelectedValuesList()) {
    		displayedTraitsModel.removeElement(selected);
    		availableTraitsModel.addElement(selected);
    	}
    }//GEN-LAST:event_removeButtonActionPerformed

    private void customTraitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customTraitButtonActionPerformed
    	// Show dialog
    	String customTraitName = (String)JOptionPane.showInputDialog(this, "Trait Name:", "Add Custom Trait", JOptionPane.PLAIN_MESSAGE);
    	if (customTraitName != null) {
    		// Add the trait to the display list
    		displayedTraitsModel.addElement(customTraitName);
    	}
    }//GEN-LAST:event_customTraitButtonActionPerformed

    private void moveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpButtonActionPerformed
    	// Start at the top, and move everything up one
    	// but careful when we already have the top item selected!
    	displayedTraitsList.getSelectedIndices();
    	int topNonSelIndex = 0; // Top non-selected index. Can't move selected items above this any higher!
    	int [] selected = displayedTraitsList.getSelectedIndices();
    	displayedTraitsList.getSelectionModel().clearSelection(); // Clear out selection, seems buggy otherwise
    	for (int selIndex: selected) {
    		if (selIndex == topNonSelIndex) {
    			topNonSelIndex++;
    			displayedTraitsList.getSelectionModel().addSelectionInterval(selIndex, selIndex);
    		} else {
    			// Remove
    			String item = displayedTraitsModel.remove(selIndex);
    			// Add
    			displayedTraitsModel.add(selIndex-1, item);
    			// Select
    			displayedTraitsList.getSelectionModel().addSelectionInterval(selIndex-1, selIndex-1);
    		}
    	}
    }//GEN-LAST:event_moveUpButtonActionPerformed

    private void moveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownButtonActionPerformed
    	// Start at the bottom, and move everything down one
    	// but careful when we already have the bottom item selected!
    	displayedTraitsList.getSelectedIndices();
    	int bottomNonSelIndex = displayedTraitsModel.getSize()-1; // Bottom non-selected index. Can't move selected items below this any lower!
    	int [] selected = displayedTraitsList.getSelectedIndices();
    	displayedTraitsList.getSelectionModel().clearSelection(); // Clear out selection, seems buggy otherwise
    	for (int i = selected.length-1; i >= 0; i--) {
    		int selIndex = selected[i];
    		if (selIndex == bottomNonSelIndex) {
    			bottomNonSelIndex--;
    			displayedTraitsList.getSelectionModel().addSelectionInterval(selIndex, selIndex);
    		} else {
    			// Remove
    			String item = displayedTraitsModel.remove(selIndex);
    			// Add
    			displayedTraitsModel.add(selIndex+1, item);
    			// Select
    			displayedTraitsList.getSelectionModel().addSelectionInterval(selIndex+1, selIndex+1);
    		}
    	}
    }//GEN-LAST:event_moveDownButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        visibleColumns.clear();
        visibleColumns.addAll(DEFAULT_COLUMNS);
        visibleColumns.remove("Act"); // get rid of "Act" column, if present
        initializeColumnLists();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    	Object [] displayedObjects = displayedTraitsModel.toArray();
    	String [] displayedStrings = Arrays.copyOf(displayedObjects, displayedObjects.length, String[].class);
        ArrayList<String> newVisibleColumns = new ArrayList<String>(Arrays.asList(displayedStrings));
        newVisibleColumns.add(0, "Act"); // Add "Act" column back in!
    	tableModel.setColumnList(newVisibleColumns);
    	setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JList<String> availableTraitsList;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton customTraitButton;
    private javax.swing.JList<String> displayedTraitsList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JButton okButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton resetButton;
    // End of variables declaration//GEN-END:variables
}
