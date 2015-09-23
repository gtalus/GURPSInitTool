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
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;

import gurpsinittool.data.Actor;
import gurpsinittool.data.Damage;
import gurpsinittool.data.Defense;
import gurpsinittool.data.Defense.DefenseResult;
import gurpsinittool.data.Defense.DefenseType;
import gurpsinittool.data.HitLocations;
import gurpsinittool.data.ActorBase.BasicTrait;
import gurpsinittool.data.DR;
import gurpsinittool.util.DieRoller;

/**
 *
 * @author dcsmall
 */
@SuppressWarnings("serial")
public class DefenseDialog extends javax.swing.JDialog {

	Actor actor; // The actor making the defense
	public Defense defense; // Defense options, inputs, and results (including the game logic to calculate them)

	public boolean valid = false;
	
	boolean updating = false;
	
	// Actions
	public Action cancelAction = new AbstractAction() {
		public void actionPerformed(ActionEvent actionEvent) {
			cancelButtonActionPerformed(actionEvent); } 
	};
	public Action okAction = new AbstractAction() {
		public void actionPerformed(ActionEvent actionEvent) {
			okButtonActionPerformed(actionEvent); } 
	};
	public Action eeAction = new AbstractAction() {
		public void actionPerformed(ActionEvent actionEvent) {
			eeCheck.doClick(); } 
	};
	public Action retreatAction = new AbstractAction() {
		public void actionPerformed(ActionEvent actionEvent) {
			retreatCheck.doClick(); } 
	};
	public Action sideAction = new AbstractAction() {
		public void actionPerformed(ActionEvent actionEvent) {
			sideCheck.doClick(); } 
	};
	public Action stunAction = new AbstractAction() {
		public void actionPerformed(ActionEvent actionEvent) {
			stunnedCheck.doClick(); } 
	};
	public Action shieldAction = new AbstractAction() {
		public void actionPerformed(ActionEvent actionEvent) {
			shieldCheckBox.doClick(); } 
	};
	public Action deceptiveAction = new AbstractAction() {
		public void actionPerformed(ActionEvent actionEvent) {
			otherSpinner.setValue(otherSpinner.getPreviousValue()); } 
	};
	public Action reverseDeceptiveAction = new AbstractAction() {
		public void actionPerformed(ActionEvent actionEvent) {
			otherSpinner.setValue(otherSpinner.getNextValue()); } 
	};
	public Action parryDefenseAction = new AbstractAction() {
		public void actionPerformed(ActionEvent actionEvent) {
			parryButton.doClick(); } 
	};
	public Action blockDefenseAction = new AbstractAction() {
		public void actionPerformed(ActionEvent actionEvent) {
			blockButton.doClick(); } 
	};
	public Action dodgeDefenseAction = new AbstractAction() {
		public void actionPerformed(ActionEvent actionEvent) {
			dodgeButton.doClick(); } 
	};
	public Action noDefenseAction = new AbstractAction() {
		public void actionPerformed(ActionEvent actionEvent) {
			noneButton.doClick(); } 
	};
    /**
     * Creates new form DefenseDialog
     */
    public DefenseDialog(java.awt.Window parent) {
        super(parent, DEFAULT_MODALITY_TYPE);
        setTitle("Defense");
        initComponents();
        initEnterEsc();
        initLocationCombo();
        initPositionCombo();
        // Setup doc listeners
    	JFormattedTextField field = (JFormattedTextField) otherSpinner.getEditor().getComponent(0);
    	DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
    	formatter.setCommitsOnValidEdit(true);
    	rollTextField.getDocument().addDocumentListener(new ValueDocumentListener());
    	drTextField.getDocument().addDocumentListener(new ValueDocumentListener());
    	damageTextField.getDocument().addDocumentListener(new ValueDocumentListener());
    }
    
    private void initLocationCombo() {
    	Vector<String> comboItems = new Vector<String>();
    	for (HitLocations.LocationType location: HitLocations.LocationType.values()) {
    		comboItems.add(HitLocations.getLocation(location).type.name());
    	}
    	locationCombo.setModel(new DefaultComboBoxModel<String>(comboItems));
    }
    
    private void initPositionCombo() {
    	Vector<String> comboItems = new Vector<String>();
    	comboItems.add("Standing");
    	comboItems.add("Kneeling");
    	comboItems.add("Prone");
    	postureCombo.setModel(new DefaultComboBoxModel<String>(comboItems));
    }

    private void initEnterEsc() {     
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
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control T"), "Stun");
        getRootPane().getActionMap().put("Stun", stunAction); 
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control D"), "Deceptive"); // => 'O' for other (and shift-'O' for minus other
        getRootPane().getActionMap().put("Deceptive", deceptiveAction); 
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control shift D"), "Reverse Deceptive"); // => 'O' for other (and shift-'O' for minus other
        getRootPane().getActionMap().put("Reverse Deceptive", reverseDeceptiveAction);         
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control I"), "Shield"); 
        getRootPane().getActionMap().put("Shield", shieldAction); 

        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control P"), "Parry"); 
        getRootPane().getActionMap().put("Parry", parryDefenseAction); 
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control B"), "Block"); 
        getRootPane().getActionMap().put("Block", blockDefenseAction); 
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control G"), "Dodge");
        getRootPane().getActionMap().put("Dodge", dodgeDefenseAction); 
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control N"), "None");
        getRootPane().getActionMap().put("None", noDefenseAction); 
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
        db = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        rollTextField = new javax.swing.JFormattedTextField();
        damageTextField = new javax.swing.JTextField();
        otherSpinner = new javax.swing.JSpinner();
        locationCombo = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        noneButton = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        stunnedCheck = new javax.swing.JCheckBox();
        postureCombo = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        drTextField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();

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
        shieldCheckBox.setText("Shield");
        shieldCheckBox.setToolTipText("Using Shield (Ctrl+I)");
        shieldCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkItemStateChanged(evt);
            }
        });

        buttonGroup1.add(blockButton);
        blockButton.setText("Block");
        blockButton.setToolTipText("Block (Ctrl+B)");
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
        parryButton.setToolTipText("Parry (Ctrl+P)");
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
        retreatCheck.setToolTipText("Retreat (Ctrl+R)");
        retreatCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkItemStateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Roll:");

        buttonGroup1.add(dodgeButton);
        dodgeButton.setText("Dodge");
        dodgeButton.setToolTipText("Dodge (Ctrl+G)");
        dodgeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });

        eeCheck.setText("Extra Effort");
        eeCheck.setToolTipText("Use Extra Effort (Ctrl+E)");
        eeCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkItemStateChanged(evt);
            }
        });

        sideCheck.setText("Side Attack");
        sideCheck.setToolTipText("Attack is from a side hex (Ctrl+S)");
        sideCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkItemStateChanged(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("<HTML>Effective<br>Defense</HTML>");

        effectiveDefense.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        effectiveDefense.setText("0");
        effectiveDefense.setToolTipText("");

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

        db.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        db.setText("DB: 2");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("DR:");

        rollTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        rollTextField.setText("98");

        otherSpinner.setToolTipText("Other modifiers, deceptive attack, etc. (Ctrl+D / Ctrl+Shift+D)");
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
        noneButton.setToolTipText("No Defense (Ctrl+N)");
        noneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Defense Type:");

        stunnedCheck.setText("Stunned");
        stunnedCheck.setToolTipText("Defender is stunned (Ctrl+T)");
        stunnedCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkItemStateChanged(evt);
            }
        });

        postureCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Standing" }));
        postureCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });
        postureCombo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                locationComboKeyReleased(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Posture:");

        drTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                drTextFieldFocusGained(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(name)
            .addComponent(result)
            .addComponent(jSeparator1)
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
                            .addComponent(jLabel3)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(postureCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(eeCheck)
                            .addComponent(retreatCheck)
                            .addComponent(sideCheck)
                            .addComponent(stunnedCheck)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(shieldCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(db))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(otherSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(33, 33, 33))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(damageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(locationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(drTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(noneButton)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(effectiveDefense)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(rollTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(rerollButton)))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                            .addComponent(dodgeNote)
                            .addComponent(stunnedCheck))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(noneButton)
                            .addComponent(shieldCheckBox)
                            .addComponent(db))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(postureCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(otherSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(effectiveDefense, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rollTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rerollButton))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(locationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(damageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(drTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(result, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton)))
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
    	valid = false;
    	setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void rerollButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rerollButtonActionPerformed
    	rollDefense();
    }//GEN-LAST:event_rerollButtonActionPerformed

    private void otherSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_otherSpinnerStateChanged
    	updateDefenseResults();
    }//GEN-LAST:event_otherSpinnerStateChanged

    private void locationComboKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_locationComboKeyReleased
    	updateDefenseResults();
    }//GEN-LAST:event_locationComboKeyReleased

    private void checkItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkItemStateChanged
    	updateDefenseResults();
    }//GEN-LAST:event_checkItemStateChanged

    private void radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonActionPerformed
    	updateDefenseResults();
    }//GEN-LAST:event_radioButtonActionPerformed

    private void drTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_drTextFieldFocusGained
        drTextField.selectAll();
    }//GEN-LAST:event_drTextFieldFocusGained

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
                DefenseDialog dialog = new DefenseDialog(new javax.swing.JWindow());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setActor(new Actor());
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton blockButton;
    private javax.swing.JLabel blockNote;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelButton;
    public javax.swing.JTextField damageTextField;
    private javax.swing.JLabel db;
    private javax.swing.JRadioButton dodgeButton;
    private javax.swing.JLabel dodgeNote;
    private javax.swing.JTextField drTextField;
    private javax.swing.JCheckBox eeCheck;
    private javax.swing.JLabel effectiveDefense;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    public javax.swing.JComboBox locationCombo;
    private javax.swing.JTextField name;
    private javax.swing.JRadioButton noneButton;
    private javax.swing.JButton okButton;
    private javax.swing.JSpinner otherSpinner;
    private javax.swing.JRadioButton parryButton;
    private javax.swing.JLabel parryNote;
    private javax.swing.JComboBox postureCombo;
    private javax.swing.JButton rerollButton;
    private javax.swing.JTextField result;
    private javax.swing.JCheckBox retreatCheck;
    private javax.swing.JFormattedTextField rollTextField;
    private javax.swing.JCheckBox shieldCheckBox;
    private javax.swing.JCheckBox sideCheck;
    private javax.swing.JCheckBox stunnedCheck;
    // End of variables declaration//GEN-END:variables
    
    public void setActor(Actor actor) {
    	updating = true;
    	this.actor = actor;
    	defense = new Defense();
    	defense.setInitialOptions(actor);

    	name.setText(actor.getTraitValue(BasicTrait.Name));
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
		}
    	
    	// Calculate effective base defense values
    	int effParry = actor.getCurrentDefenseValue(DefenseType.Parry);
    	int effBlock = actor.getCurrentDefenseValue(DefenseType.Block);
    	int effDodge = actor.getCurrentDefenseValue(DefenseType.Dodge);
    	int Parry = actor.getTraitValueInt(BasicTrait.Parry);
    	int Block = actor.getTraitValueInt(BasicTrait.Block);
    	int Dodge = actor.getTraitValueInt(BasicTrait.Dodge);

    	// Report base if different
    	parryNote.setText(effParry + (effParry!=Parry?" (base: " + Parry + ")":""));
    	blockNote.setText(effBlock + (effBlock!=Block?" (base: " + Block + ")":""));
    	dodgeNote.setText(effDodge + (effDodge!=Dodge?" (base: " + Dodge + ")":""));
    	
    	// Sync defense initial options to dialog components
    	switch (defense.type) {
    	case Parry:
    		parryButton.setSelected(true);
    		break;
    	case Block:
    		blockButton.setSelected(true);
    		break;
    	case Dodge:
    		dodgeButton.setSelected(true);
    		break;
    	case None:
    		noneButton.setSelected(true);
    		break;
    	}
    	
    	eeCheck.setSelected(defense.ee);
    	retreatCheck.setSelected(defense.retreat);
    	sideCheck.setSelected(defense.side);
    	stunnedCheck.setSelected(defense.stunned);
    	shieldCheckBox.setSelected(defense.shield);
    	
       	int ShieldDB = actor.getTraitValueInt(BasicTrait.Shield_DB);
        int ShieldDR = actor.getTraitValueInt(BasicTrait.Shield_DR);
    	int ShieldHP = actor.getTraitValueInt(BasicTrait.Shield_HP);
    	String DR = actor.getTraitValue(BasicTrait.DR);
    	db.setText("DB: " + ShieldDB);
    	db.setToolTipText("DR: " + ShieldDR + " HP: " + (ShieldHP-actor.getTempInt("shieldDamage")) + "/" + ShieldHP);
    	shieldCheckBox.setToolTipText("DR: " + ShieldDR + " HP: " + (ShieldHP-actor.getTempInt("shieldDamage")) + "/" + ShieldHP);
    	//shield_dr.setText("DR: " + ShieldDR);
    	//shield_hp.setText("HP: " + (ShieldHP-actor.getTempInt("shieldDamage")) + "/" + ShieldHP);
    	drTextField.setText(DR);
    	damageTextField.setText("");
    	
    	// Set position
    	// TODO: make this more flexible (perhaps based off of an ENUM???!!!)
    	postureCombo.setSelectedIndex(0);
    	if (defense.position.equals("Kneeling"))
    		postureCombo.setSelectedIndex(1);
    	if (defense.position.equals("Prone"))
    		postureCombo.setSelectedIndex(2);
    	
    	// TODO: otherMod? what would this be set to? some temp var?
    	otherSpinner.setValue(0);
    	locationCombo.setSelectedItem("Torso");
    	
    	rollDefense();
    	updating = false;
    	updateDefenseResults();
    	  	
        damageTextField.selectAll();
        damageTextField.requestFocusInWindow();
    }
    
    private void rollDefense() {
    	rollTextField.setText(String.valueOf(DieRoller.roll3d6()));
    }
    
    /**
     * Calculate the result of the attack
     */
    private void updateDefenseResults() {
    	if (actor == null || updating) // Exit immediately if actor not initialized yet or currently updating
    		return;

    	// Parse input fields. Return if any fail
    	if (!parseInputFields())
    		return;
    	
    	// Sync all the current options to the defense object
    	updateDefenseSettings();
    
    	// Now update the defense object results
    	defense.calcDefenseResults(actor);
    
    	// get effective defense value
    	effectiveDefense.setText(String.valueOf(defense.effectiveDefense));
    	
    	// Set the result message
    	setResultMessage();
    }
    
    /**
     * Sync dialog -> defense object
     */
    private void updateDefenseSettings() {
    	if (updating) return;
    	// Sync defense initial options to dialog components
    	if (parryButton.isSelected()) {
    		defense.type = DefenseType.Parry;
    	} else if (blockButton.isSelected()) {
    		defense.type = DefenseType.Block;
    	} else if (dodgeButton.isSelected()) {
    		defense.type = DefenseType.Dodge;
    	} else if (noneButton.isSelected()) {
    		defense.type = DefenseType.None;
    	} else {
    		System.out.println("-E- DefenseDialog: updateDefenseSettings: no defense selected!");
    	}
    	
    	defense.ee = eeCheck.isSelected();
    	defense.retreat = retreatCheck.isSelected();
    	defense.side = sideCheck.isSelected();
    	defense.stunned = stunnedCheck.isSelected();
    	defense.shield = shieldCheckBox.isSelected();
    	
    	defense.position = postureCombo.getSelectedItem().toString();
    	
    	try {
    		defense.otherMod = Integer.parseInt(otherSpinner.getValue().toString());
    	} catch (NumberFormatException e) {}	
    }
    
    /**
     * Parse the various input fields, verifying that they contain valid information
     * Set the defense object values
     * @return if the parsing was successful
     */
    private boolean parseInputFields() {
      	boolean parseSuccess = true;
    	try { // Get roll results- set text to red and print error message if fails
    		defense.roll = Integer.parseInt(rollTextField.getText());
    		rollTextField.setForeground(Color.BLACK);
    	} catch (Exception e) {
    		rollTextField.setForeground(Color.RED);
    		System.out.println("-W- DefenseDialog.parseInputFields: Error parsing roll field! '" + rollTextField.getText() + "': " + e.getMessage());
    		parseSuccess = false;
    	}    
    	try { // Get dr- set text to red and print error message if fails
    		defense.override_dr = DR.ParseDR(drTextField.getText());
    		drTextField.setForeground(Color.BLACK);
    	} catch (Exception e) {
    		drTextField.setForeground(Color.RED);
    		System.out.println("-W- DefenseDialog.parseInputFields: Error parsing dr field! '" + drTextField.getText() + "': " + e.getMessage());
    		parseSuccess = false;
    	}    	
    	try { // Get damage- set text to red and print error message if fails
    		defense.damage = Damage.ParseDamage(damageTextField.getText());
    		damageTextField.setForeground(Color.BLACK);
    	} catch (Exception e) {
   			damageTextField.setForeground(Color.RED);
    		System.out.println("-W- DefenseDialog.parseInputFields: Error parsing damage field! '" + damageTextField.getText() + "': " + e.getMessage());
    		parseSuccess = false;
    	}
    	defense.location = HitLocations.getLocationFromName((String) locationCombo.getSelectedItem());
    	return parseSuccess;
    }
    
    private void setResultMessage() {
    	String message = "";
    	switch (defense.result) {
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
    		break;
    	case CritFailure:
			message += "Hit (crit fail)";
			break;
    	}
    	
     	if (defense.injury != 0) {
			result.setBackground(new Color(255,200,200));
    		message += ": " + defense.injury;
    		if (defense.cripplingInjury)
    			message += " crippling";
    		else if (defense.majorWound)
    			message += " major";
    		message += " injury!";
    	}
    	else {
   			result.setBackground(new Color(200,200,200));
			message += ": no injury";
    	}
		if (defense.result == DefenseResult.ShieldHit) {
			if (defense.shieldDamage != 0 && defense.injury == 0) {
    			result.setBackground(new Color(255,255,200));
	    	}
			message += " (" + defense.shieldDamage + " shld dmg)";
		} else if (defense.result == DefenseResult.CritFailure) {
			result.setBackground(new Color(255,200,200));
		}
    	result.setText(message);
    }
    
	/**
	 * Internal class to listen to the changes in text components
	 */
	protected class ValueDocumentListener implements DocumentListener {
	    public void insertUpdate(DocumentEvent e) {
	    	updateDefenseResults();
	    }
	    public void removeUpdate(DocumentEvent e) {
	    	updateDefenseResults();
	    }
	    public void changedUpdate(DocumentEvent e) {
	    	updateDefenseResults();
	    }
	} 
	
	/**
	 * Internal class to listen to the changes in text components
	 */
	protected class ModDocumentListener implements DocumentListener {
	    public void insertUpdate(DocumentEvent e) {
	    	updateDefenseResults();
	    }
	    public void removeUpdate(DocumentEvent e) {
	    	updateDefenseResults();
	    }
	    public void changedUpdate(DocumentEvent e) {
	    	updateDefenseResults();
	    }
	} 
}
