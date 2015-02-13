/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gurpsinittool.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import gurpsinittool.data.CriticalTables;
import gurpsinittool.data.CriticalTables.Entry;
import gurpsinittool.util.DieRoller;

/**
 *
 * @author dcsmall
 */
public class CriticalTablesDialog extends javax.swing.JDialog {
	
    /**
     * Creates new form CriticalTablesDialog
     */
    public CriticalTablesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initCritTable(hitTable, CriticalTables.critical_hit);
        initCritTable(missTable, CriticalTables.critical_miss);
        initCritTable(headTable, CriticalTables.critical_head_hit);
        initCritTable(unarmedTable, CriticalTables.critical_miss_unarmed);
        initCritTable(locationTable, CriticalTables.hit_location);
        }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusPanel = new javax.swing.JPanel();
        rollLabel = new javax.swing.JLabel();
        rollButton = new javax.swing.JButton();
        jTabbedPane = new javax.swing.JTabbedPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        hitTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        headTable = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        missTable = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        unarmedTable = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        locationTable = new javax.swing.JTable();

        setName("CriticalTableDialog"); // NOI18N
        setType(java.awt.Window.Type.UTILITY);

        statusPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        rollLabel.setText("Roll:");

        rollButton.setText("Roll");
        rollButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rollButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(rollLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rollButton))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(rollLabel)
                .addComponent(rollButton))
        );

        getContentPane().add(statusPanel, java.awt.BorderLayout.SOUTH);

        hitTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Roll", "Entry"
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
        jScrollPane5.setViewportView(hitTable);

        jTabbedPane.addTab("Critical Hit", jScrollPane5);

        headTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Roll", "Entry"
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
        jScrollPane2.setViewportView(headTable);

        jTabbedPane.addTab("Critical Head Blow", jScrollPane2);

        missTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Roll", "Entry"
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
        jScrollPane3.setViewportView(missTable);

        jTabbedPane.addTab("Critical Miss", jScrollPane3);

        unarmedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Roll", "Entry"
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
        jScrollPane4.setViewportView(unarmedTable);

        jTabbedPane.addTab("Unarmed Critical Miss", jScrollPane4);

        locationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Roll", "Entry"
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
        jScrollPane6.setViewportView(locationTable);

        jTabbedPane.addTab("Hit Locations", jScrollPane6);

        getContentPane().add(jTabbedPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rollButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rollButtonActionPerformed
    	int result = DieRoller.roll3d6();
    	rollLabel.setText("Roll: " + result);
    	// Determine current data/ table
    	JScrollPane pane = (JScrollPane) jTabbedPane.getSelectedComponent();
    	JTable current_table = (JTable) pane.getViewport().getView();
    	
    	// Original data is stored as property of table. Kind of an ugly hack. 
    	ArrayList<Entry> current_data = (ArrayList<Entry>) current_table.getClientProperty(0);
    	
    	// Search through table for entry that matches result
    	for(int i = 0; i < current_data.size(); ++i) {
    		for (int j = 0; j < current_data.get(i).rolls.size(); ++j) {
    			if (current_data.get(i).rolls.get(j) == result) {
    				current_table.getSelectionModel().setSelectionInterval(i, i);
    				current_table.scrollRectToVisible(current_table.getCellRect(i, 0, true));
    			}
    		}
    	}
    }//GEN-LAST:event_rollButtonActionPerformed

    private void initCritTable(JTable table, ArrayList<Entry> data) {
    	DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
    	
    	table.putClientProperty(0, data);
    	// Build table
    	for (int i = 0; i < data.size(); ++i) {
    		String numbers = "";
    		CriticalTables.Entry entry = data.get(i);
    		for (int j = 0; j < entry.rolls.size(); ++j) {
    			if (j > 0)
    				numbers += ", ";
    			numbers += entry.rolls.get(j);
    		}
    		tableModel.addRow(new Object [] {numbers, entry.notes});
    	}
    	table.revalidate();
    	
    	// Auto-size based on first column width
    	TableColumn column = table.getColumnModel().getColumn(0);
	    // Get width of column header 
	    TableCellRenderer renderer = column.getHeaderRenderer(); 
	    if (renderer == null) 
	    { renderer = table.getTableHeader().getDefaultRenderer(); } 
	    Component comp = renderer.getTableCellRendererComponent( table, column.getHeaderValue(), false, false, 0, 0); 
	    int width = comp.getPreferredSize().width; 
	    
	    // Check width of all the cells
	    // Get maximum width of column data
	    for (int j=0; j < table.getRowCount(); j++) {
	        renderer = table.getCellRenderer(j, 0);
	        comp = renderer.getTableCellRendererComponent(table, table.getValueAt(j, 0), false, false, j, 0);
	        width = Math.max(width, comp.getPreferredSize().width);
	    }   
      	table.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(width);

    	table.setDefaultRenderer(String.class,  new MultiLineTableCellRenderer());
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
            java.util.logging.Logger.getLogger(CriticalTablesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CriticalTablesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CriticalTablesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CriticalTablesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CriticalTablesDialog dialog = new CriticalTablesDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JTable headTable;
    private javax.swing.JTable hitTable;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTable locationTable;
    private javax.swing.JTable missTable;
    private javax.swing.JButton rollButton;
    private javax.swing.JLabel rollLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTable unarmedTable;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Multiline Table Cell Renderer.
     */
    public class MultiLineTableCellRenderer extends JTextArea 
      implements TableCellRenderer {
      private List<List<Integer>> rowColHeight = new ArrayList<List<Integer>>();
     
      public MultiLineTableCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
      }
     
      public Component getTableCellRendererComponent(
          JTable table, Object value, boolean isSelected, boolean hasFocus,
          int row, int column) {
        if (isSelected) {
          setForeground(table.getSelectionForeground());
          setBackground(table.getSelectionBackground());
        } else {
          setForeground(table.getForeground());
          setBackground(table.getBackground());
        }
        setFont(table.getFont());
        if (hasFocus) {
          setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
          if (table.isCellEditable(row, column)) {
            setForeground(UIManager.getColor("Table.focusCellForeground"));
            setBackground(UIManager.getColor("Table.focusCellBackground"));
          }
        } else {
          setBorder(new EmptyBorder(1, 2, 1, 2));
        }
        if (value != null) {
          setText(value.toString());
        } else {
          setText("");
        }
        adjustRowHeight(table, row, column);
        return this;
      }
     
      /**
       * Calculate the new preferred height for a given row, and sets the height on the table.
       */
      private void adjustRowHeight(JTable table, int row, int column) {
        //The trick to get this to work properly is to set the width of the column to the
        //textarea. The reason for this is that getPreferredSize(), without a width tries
        //to place all the text in one line. By setting the size with the with of the column,
        //getPreferredSize() returns the proper height which the row should have in
        //order to make room for the text.
        int cWidth = table.getTableHeader().getColumnModel().getColumn(column).getWidth();
        setSize(new Dimension(cWidth, 1000));
        int prefH = getPreferredSize().height;
        while (rowColHeight.size() <= row) {
          rowColHeight.add(new ArrayList<Integer>(column));
        }
        List<Integer> colHeights = rowColHeight.get(row);
        while (colHeights.size() <= column) {
          colHeights.add(0);
        }
        colHeights.set(column, prefH);
        int maxH = prefH;
        for (Integer colHeight : colHeights) {
          if (colHeight > maxH) {
            maxH = colHeight;
          }
        }
        if (table.getRowHeight(row) != maxH) {
          table.setRowHeight(row, maxH);
        }
      }
    }
}
