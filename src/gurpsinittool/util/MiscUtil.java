package gurpsinittool.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

/**
 * Set of various utility functions
 * @author dcsmall
 *
 */
public final class MiscUtil {
	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(MiscUtil.class.getName());
	
	private MiscUtil() {} // Utility Class
	
	/**
	 * Convert an RTF string into a raw text string
	 * @param rtfString - RTF-encoded string
	 * @return decoded string, or null if there was a problem
	 */
	public static String decodeRTFNotes(String rtfString) {
		String decoded = null;
		RTFEditorKit rtfParser = new RTFEditorKit();
		Document document = rtfParser.createDefaultDocument();
		try {
			rtfParser.read(new ByteArrayInputStream(rtfString.getBytes()), document, 0);
			decoded = document.getText(0, document.getLength());
		} catch (IOException e) {
			if (LOG.isLoggable(Level.SEVERE)) {LOG.log(Level.SEVERE, e.getMessage(), e);}
		} catch (BadLocationException e) {
			if (LOG.isLoggable(Level.SEVERE)) {LOG.log(Level.SEVERE, e.getMessage(), e);}
		}
		return decoded;
	}
	/**
	 * Attempt to convert a string to an Integer, without any annoying exceptions!
	 * Also handles 'x.0'
	 * @param text
	 * @return
	 */
	public static int parseIntSafe(String text) {
		if (text == null || text.isEmpty())
			return 0;
		if (text.endsWith(".0"))
			text = text.substring(0, text.length()-2);
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			if (LOG.isLoggable(Level.INFO)) {LOG.info("Error parsing value: '" + text + "'");}
			return 0;
		}
	}
	/**
	 * Blend two colors, using the alpha channel of each color as the weight
	 * @param color0 - the first color to blend
	 * @param color1 - the second color to blend
	 * @return the blended color
	 * @author Cameron Behar
	 */
	public static Color blend(final Color color0, final Color color1) {
		final double totalAlpha = color0.getAlpha() + color1.getAlpha();
		final double weight0 = color0.getAlpha() / totalAlpha;
		final double weight1 = color1.getAlpha() / totalAlpha;

		final double red = weight0 * color0.getRed() + weight1 * color1.getRed();
		final double green = weight0 * color0.getGreen() + weight1 * color1.getGreen();
		final double blue = weight0 * color0.getBlue() + weight1 * color1.getBlue();
		final double alpha = Math.max(color0.getAlpha(), color1.getAlpha());

		return new Color((int) red, (int) green, (int) blue, (int) alpha);
	}
	
	/**
	 * Create a new copy of the provided color with the specified alpha value
	 * @param color - the color to use as the base
	 * @param alpha - the new alpha value
	 * @return a new color object with the original values and new alpha
	 */
	public static Color setAlpha(final Color color, final int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}
	
	/**
	 * Check that the specified component is fully within a valid screen.
	 * If not, it will adjust the component to be in a valid location.
	 * @param component - the component to validate and adjust
	 */
	public static void validateOnScreen(final Component component) {
		final Rectangle window = component.getBounds();

		Rectangle virtualscreen = new Rectangle();
		final GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for (int j = 0; j < graphicsDevices.length; j++) { 
			final GraphicsConfiguration[] graphicsConfig = graphicsDevices[j].getConfigurations();
			for (int i=0; i < graphicsConfig.length; i++) {
				virtualscreen = virtualscreen.union(graphicsConfig[i].getBounds());
			}
		}
		if (LOG.isLoggable(Level.FINER)) { LOG.finer("Testing window position: screen: " + virtualscreen + " window: " + window); }

		if (!virtualscreen.contains(window)) {
			// Snap position to screen
			verifyWindowTopLeft(window,virtualscreen);
			verifyWindowSize(window, virtualscreen);
			verifyWindowBottomRight(window, virtualscreen);
		}
		component.setLocation(window.x, window.y);
	}
	private static void verifyWindowTopLeft(final Rectangle window, final Rectangle virtualscreen) {
		// If top-left corner is to the left of the screen
		if (window.x < virtualscreen.x) {
			if (LOG.isLoggable(Level.FINER)) { LOG.finer("Window out of screen: translating +x"); }
			window.translate(virtualscreen.x - window.x, 0);
		}
		// top-left corner is above the screen
		if (window.y < virtualscreen.y) {
			if (LOG.isLoggable(Level.FINER)) { LOG.finer("Window out of screen: translating +y"); }
			window.translate(0, virtualscreen.y - window.y);
		}
	}
	private static void verifyWindowSize(final Rectangle window, final Rectangle virtualscreen) {
		// Size bigger than window
		if (window.height > virtualscreen.height) {
			if (LOG.isLoggable(Level.FINER)) { LOG.finer("Window out of screen: resizing: smaller height"); }
			window.height = virtualscreen.height;
		}
		if (window.width > virtualscreen.width) {
			if (LOG.isLoggable(Level.FINER)) { LOG.finer("Window out of screen: resizing: smaller width"); }
			window.width = virtualscreen.width;
		}
	}
	private static void verifyWindowBottomRight(final Rectangle window, final Rectangle virtualscreen) {
		// bottom-right corner is to the right of the screen
		if ((window.x+window.width) > (virtualscreen.x+virtualscreen.width)) {
			if (LOG.isLoggable(Level.FINER)) { LOG.finer("Window out of screen: translating -x"); }
			window.translate((virtualscreen.x+virtualscreen.width)-(window.x+window.width),0);				 
		}
		// bottom-right corner is below the screen
		if ((window.y+window.height) > (virtualscreen.y+virtualscreen.height)) {
			if (LOG.isLoggable(Level.FINER)) { LOG.finer("Window out of screen: translating -y"); }
			window.translate(0,(virtualscreen.y+virtualscreen.height)-(window.y+window.height));				 
		}
	}
	
	/**
	 * Create a JButton using the specified action but with no text
	 * @param action - the action to create the button using
	 * @return a JButton object
	 */
	public static JButton noTextButton(final Action action) {
		final JButton button = new JButton(action);
        button.setText(""); // clear action text
        return button;
	}
	/**
	 * Apply the specified style to the provided label
	 * @param label - the label to apply the style to
	 * @param style - the style to apply
	 */
	public static void setLabelFontStyle(final JLabel label, final int style) {
		final Font oldFont = label.getFont();
		final Font newFont = new Font(oldFont.getName(), style, oldFont.getSize());
		label.setFont(newFont);
	}
	/**
	 * Apply the specified style to the provide text field
	 * @param field - the text field to apply the style to
	 * @param style - the style to apply
	 */
	public static void setTextFieldFontStyle(final JTextField field, final int style) {
		final Font oldFont = field.getFont();
		final Font newFont = new Font(oldFont.getName(), style, oldFont.getSize());
		field.setFont(newFont);
	}
	/**
	 * Set the label text to bold
	 * @param label - the label to adjust
	 */
	public static void setLabelBold(final JLabel label) {
		final Font oldFont = label.getFont();
		final Font newFont = new Font(oldFont.getName(), Font.BOLD, oldFont.getSize());
		label.setFont(newFont);
	}
	/**
	 * Set the label text to italic
	 * @param label - the label to adjust
	 */
	public static void setLabelItalic(final JLabel label) {
		final Font oldFont = label.getFont();
		final Font newFont = new Font(oldFont.getName(), Font.ITALIC, oldFont.getSize());
		label.setFont(newFont);
	}
	/**
	 * Set the label text to normal
	 * @param label - the label to adjust
	 */
	public static void setLabelNormal(final JLabel label) {
		final Font oldFont = label.getFont();
		final Font newFont = new Font(oldFont.getName(), Font.PLAIN, oldFont.getSize());
		label.setFont(newFont);
	}
	/**
	 * Set the text field text to italic
	 * @param field - the text field to adjust
	 */
	public static void setTextFieldItalic(final JTextField field) {
		final Font oldFont = field.getFont();
		final Font newFont = new Font(oldFont.getName(), Font.ITALIC, oldFont.getSize());
		field.setFont(newFont);
	}
	/**
	 * Set the text field text to normal
	 * @param field - the text field to adjust
	 */
	public static void setTextFieldNormal(final JTextField field) {
		final Font oldFont = field.getFont();
		final Font newFont = new Font(oldFont.getName(), Font.PLAIN, oldFont.getSize());
		field.setFont(newFont);
	}
}
