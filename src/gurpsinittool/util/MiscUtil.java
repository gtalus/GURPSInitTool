package gurpsinittool.util;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class MiscUtil {
	private static final boolean DEBUG = false;
	public static void validateOnScreen(Component c) {
		final Rectangle window = c.getBounds();

		Rectangle virtualscreen = new Rectangle();
		GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for (int j = 0; j < gs.length; j++) { 
			GraphicsConfiguration[] gc = gs[j].getConfigurations();
			for (int i=0; i < gc.length; i++) {
				virtualscreen = virtualscreen.union(gc[i].getBounds());
			}
		}
		if (DEBUG) System.out.println("Testing window position: screen: " + virtualscreen + " window: " + window);

		if (!virtualscreen.contains(window)) {
			// Snap position to screen
			// If top-left corner is to the left of the screen
			if (window.x < virtualscreen.x) {
				if (DEBUG) System.out.println("Window out of screen: translating +x");
				window.translate(virtualscreen.x - window.x, 0);
			}
			// top-left corner is above the screen
			if (window.y < virtualscreen.y) {
				if (DEBUG) System.out.println("Window out of screen: translating +y");
				window.translate(0, virtualscreen.y - window.y);
			}
			// Size bigger than window
			if (window.height > virtualscreen.height) {
				if (DEBUG) System.out.println("Window out of screen: resizing: smaller height");
				window.height = virtualscreen.height;
			}
			if (window.width > virtualscreen.width) {
				if (DEBUG) System.out.println("Window out of screen: resizing: smaller width");
				window.width = virtualscreen.width;
			}
			// bottom-right corner is to the right of the screen
			if ((window.x+window.width) > (virtualscreen.x+virtualscreen.width)) {
				if (DEBUG) System.out.println("Window out of screen: translating -x");
				window.translate((virtualscreen.x+virtualscreen.width)-(window.x+window.width),0);				 
			}
			// bottom-right corner is below the screen
			if ((window.y+window.height) > (virtualscreen.y+virtualscreen.height)) {
				if (DEBUG) System.out.println("Window out of screen: translating -y");
				window.translate(0,(virtualscreen.y+virtualscreen.height)-(window.y+window.height));				 
			}
		}
		c.setLocation(window.x, window.y);
	}
	public static JButton noTextButton(Action a) {
		JButton button = new JButton(a);
        button.setText(""); // clear action text
        return button;
	}
	public static void setLabelFontStyle(JLabel c, int style) {
		Font oldFont = c.getFont();
		Font newFont = new Font(oldFont.getName(), style, oldFont.getSize());
		c.setFont(newFont);
	}
	public static void setTextFieldFontStyle(JTextField c, int style) {
		Font oldFont = c.getFont();
		Font newFont = new Font(oldFont.getName(), style, oldFont.getSize());
		c.setFont(newFont);
	}
	
	public static void setLabelBold(JLabel c) {
		Font oldFont = c.getFont();
		Font newFont = new Font(oldFont.getName(), Font.BOLD, oldFont.getSize());
		c.setFont(newFont);
	}
	
	public static void setLabelItalic(JLabel c) {
		Font oldFont = c.getFont();
		Font newFont = new Font(oldFont.getName(), Font.ITALIC, oldFont.getSize());
		c.setFont(newFont);
	}
	
	public static void setLabelNormal(JLabel c) {
		Font oldFont = c.getFont();
		Font newFont = new Font(oldFont.getName(), Font.PLAIN, oldFont.getSize());
		c.setFont(newFont);
	}
	
	public static void setTextFieldItalic(JTextField c) {
		Font oldFont = c.getFont();
		Font newFont = new Font(oldFont.getName(), Font.ITALIC, oldFont.getSize());
		c.setFont(newFont);
	}
	
	public static void setTextFieldNormal(JTextField c) {
		Font oldFont = c.getFont();
		Font newFont = new Font(oldFont.getName(), Font.PLAIN, oldFont.getSize());
		c.setFont(newFont);
	}
}
