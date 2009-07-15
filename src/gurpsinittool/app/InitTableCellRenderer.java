package gurpsinittool.app;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import gurpsinittool.data.*;

/**
 * Renderer to deal with all the customizations based on Actor state/type/etc.
 * Assumes that the table model being used is an ActorTableModel.
 * @author dsmall
 *
 */
public class InitTableCellRenderer extends DefaultTableCellRenderer {

	/**
	 * This class is not really serializable, I think.
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		//JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);	
		
		if (row == table.getRowCount() -1) {
			c.setBackground(new Color(255,255,255));
			c.setForeground(new Color(128,128,128));
			c.setHorizontalAlignment(SwingConstants.LEFT);
			return c;
		}
		
		Actor a = ((ActorTableModel)table.getModel()).getActor(row);
		if (column == 0 && a.Active) {
			c.setIcon(new ImageIcon("src/resources/images/go.png", "Current Actor"));  
		}
		else {
			c.setIcon(new ImageIcon());
		}
		
		if (isSelected) {
			switch (a.Type) {
			case PC:
				c.setBackground(new Color(128,255,128));
				break;
			case Ally:
				c.setBackground(new Color(128,128,255));
				break;
			case Enemy:
				c.setBackground(new Color(255,128,128));
				break;
			case Neutral:
				c.setBackground(new Color(128,128,128));
				break;
			case Special:
				c.setBackground(new Color(255,128,255));
				break;
			}
		}
		else {
			switch (a.Type) {
			case PC:
				c.setBackground(new Color(200,255,200));
				break;
			case Ally:
				c.setBackground(new Color(200,200,255));
				break;
			case Enemy:
				c.setBackground(new Color(255,200,200));
				break;
			case Neutral:
				c.setBackground(new Color(200,200,200));
				break;
			case Special:
				c.setBackground(new Color(255,200,255));
				break;
			}
		}
	
		c.setHorizontalAlignment(SwingConstants.LEFT);
		c.setForeground(new Color(0,0,0));
		switch (a.State) {
		case Active:
			break;
		case Waiting:
			c.setHorizontalAlignment(SwingConstants.RIGHT);
			break;
		case Disabled:
			break;
		case Unconscious:
		case Dead:
			c.setForeground(new Color(128,128,128));
			break;
		}
		
		/*if (hasFocus) {
			c.setFont(new Font("sansserif", Font.BOLD, 12));
		}*/

		return c;
	}

}
