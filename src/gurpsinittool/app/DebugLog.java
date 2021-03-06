package gurpsinittool.app;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Handler;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class DebugLog  extends JFrame 
	implements ActionListener {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(DebugLog.class.getName());
	private final static Logger ROOTLOG = Logger.getLogger("gurpsinittool.");
	
	private Properties propertyBag;
	private JTextArea logArea = null;
	private JScrollPane logPane = null;
	private JMenuBar jMenuBar;
	private JMenu jMenu;
	
	private JFileChooser saveFileChooser;
	
	public class TextAreaHandler extends Handler {

		@Override
		public void close() throws SecurityException {}

		@Override
		public void flush() {}

		@Override
		public void publish(final LogRecord record) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					// Try to filter out messages generated by debug log to prevent infinite loop of logging (possibly redundant)
					if (record.getSourceClassName().startsWith("gurpsinittool.")) {
						StringWriter text = new StringWriter();
						PrintWriter out = new PrintWriter(text);
						//out.println(logArea.getText());
						out.printf("[%7s] %s.%s: %s\n", record.getLevel(),
								record.getSourceClassName(),
								record.getSourceMethodName(), record.getMessage());
						//logArea.setText(text.toString());
						logArea.append(text.toString());
					}
				}

			});
		}
	}
	
	private TextAreaHandler textAreaHandler;
	
	/**
	 * Early constructor: only creates log-related items
	 */
	public DebugLog() {
		super("Debug Log");
		logArea = new JTextArea();
		// Log handler
		textAreaHandler = new TextAreaHandler();
		ROOTLOG.addHandler(textAreaHandler);
	}
	
	/**
	 * Fully initialize window
	 * @param propertyBag
	 */
	public void Initialize(Properties propertyBag) {
		this.propertyBag = propertyBag;
		setDefaultProperties();
		
        jMenuBar = new JMenuBar();
        
        jMenu = new JMenu("File");
        jMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem menuItem = new JMenuItem("Save", KeyEvent.VK_S); 
        menuItem.addActionListener(this);
        jMenu.add(menuItem);
        jMenuBar.add(jMenu);
        
        jMenu = new JMenu("Log Level");
        jMenu.setMnemonic(KeyEvent.VK_L);
        ButtonGroup logLevelGroup = new ButtonGroup();
        menuItem = new JRadioButtonMenuItem("SEVERE"); 
        logLevelGroup.add(menuItem);
        menuItem.addActionListener(this);
        jMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("WARNING"); 
        logLevelGroup.add(menuItem);
        menuItem.addActionListener(this);
        jMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("INFO"); 
        logLevelGroup.add(menuItem);
        menuItem.addActionListener(this);
        jMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("CONFIG"); 
        logLevelGroup.add(menuItem);
        menuItem.addActionListener(this);
        jMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("FINE"); 
        logLevelGroup.add(menuItem);
        menuItem.addActionListener(this);
        jMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("FINER"); 
        logLevelGroup.add(menuItem);
        menuItem.addActionListener(this);
        jMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("FINEST");
        logLevelGroup.add(menuItem);
        menuItem.addActionListener(this);
        jMenu.add(menuItem);
        jMenuBar.add(jMenu);
      
        setJMenuBar(jMenuBar);
        

		logArea.setEditable(false);
		logArea.setFont(new Font("monospaced", Font.PLAIN, 12));
		logPane = new JScrollPane(logArea);
		getContentPane().add(logPane);
		


		saveFileChooser = new JFileChooser();

		setLocation(Integer.valueOf(propertyBag.getProperty("DebugLog.location.x")),
				Integer.valueOf(propertyBag.getProperty("DebugLog.location.y")));
		setSize(Integer.valueOf(propertyBag.getProperty("DebugLog.size.width")),
				Integer.valueOf(propertyBag.getProperty("DebugLog.size.height")));    
	}

	public void addText(String text) {
		logArea.append(text);
		this.getContentPane().validate();
	}
	
	private void setDefaultProperties() {
		if (!propertyBag.containsKey("DebugLog.location.x")) {
			propertyBag.setProperty("DebugLog.location.x", "400"); }
		if (!propertyBag.containsKey("DebugLog.location.y")) {
			propertyBag.setProperty("DebugLog.location.y", "200"); }
		if (!propertyBag.containsKey("DebugLog.size.width")) {
			propertyBag.setProperty("DebugLog.size.width", "400"); }
		if (!propertyBag.containsKey("DebugLog.size.height")) {
			propertyBag.setProperty("DebugLog.size.height", "400"); }
	}
	
	 /**
	  * Update all the store-able properties to their current values
	  */
	 public void updateProperties() {
		 // Kept up-to-date with event listeners
		 propertyBag.setProperty("DebugLog.location.x", String.valueOf(getLocation().x));
		 propertyBag.setProperty("DebugLog.location.y", String.valueOf(getLocation().y));
		 propertyBag.setProperty("DebugLog.size.width", String.valueOf(getSize().width));
		 propertyBag.setProperty("DebugLog.size.height", String.valueOf(getSize().height));
	 }

	 private void userSaveFile() {
		 int retVal = saveFileChooser.showSaveDialog(this);
		 if (LOG.isLoggable(Level.FINE)) {LOG.fine("Displaying file chooser"); }
		 if (retVal == JFileChooser.APPROVE_OPTION) {
			 File file = saveFileChooser.getSelectedFile();
			 if (!file.toString().contains(".")) { file = new File(file.toString() + ".txt"); }
			 if (file.exists()) {
				 int response = JOptionPane.showConfirmDialog (null,
						 "Overwrite existing file?","Confirm Overwrite",
						 JOptionPane.OK_CANCEL_OPTION,
						 JOptionPane.QUESTION_MESSAGE);
				 if (response == JOptionPane.CANCEL_OPTION) return;
			 }
			 // save log
			 if (LOG.isLoggable(Level.FINE)) {LOG.fine("Saving log to file: " + file.getName()); }
			 try {
				 FileWriter fstream = new FileWriter(file);
				 BufferedWriter out = new BufferedWriter(fstream);
				 logArea.write(out);
				 out.close();
			 }
			 catch (Exception e) {
				 if (LOG.isLoggable(Level.SEVERE)) {LOG.log(Level.SEVERE, "Error saving log: " + e.getMessage(), e);}
			 }
		 }
	 }
		 
	@Override
	public void actionPerformed(ActionEvent e) {
    	if (LOG.isLoggable(Level.INFO)) {LOG.info("Received info action command " + e.getActionCommand()); }
    	if ("WARNING".equals(e.getActionCommand())) { // set log level warning
    		ROOTLOG.setLevel(Level.WARNING);
    	} else if ("SEVERE".equals(e.getActionCommand())) {
    		ROOTLOG.setLevel(Level.SEVERE);
    	} else if ("WARNING".equals(e.getActionCommand())) {
    		ROOTLOG.setLevel(Level.WARNING);
    	} else if ("INFO".equals(e.getActionCommand())) {
    		ROOTLOG.setLevel(Level.INFO);
    	} else if ("CONFIG".equals(e.getActionCommand())) { 
    		ROOTLOG.setLevel(Level.CONFIG);
    	} else if ("FINE".equals(e.getActionCommand())) { 
    		ROOTLOG.setLevel(Level.FINE);
    	} else if ("FINER".equals(e.getActionCommand())) { 
    		ROOTLOG.setLevel(Level.FINER);
    	} else if ("FINEST".equals(e.getActionCommand())) { 
    		ROOTLOG.setLevel(Level.FINEST);
    	} else if ("Save".equals(e.getActionCommand())) { 
    		userSaveFile();
    	} 
	}
	
}
