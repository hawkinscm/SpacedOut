
package gui;

import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;

import model.Card;

/**
 * Allows the player to choose a color for the given wild card.
 */
public class ChooseColorDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	Card.Color selectedColor = null;
	
	/**
	 * Creates a new Choose Color Dialog
	 * @param owner Frame that created/owns this dialog
	 * @param title Title for the dialog
	 * @param wildCard wild card for which a color is being chosen
	 */
	public ChooseColorDialog(Frame owner, String title, Card wildCard) {
		super(owner, title);
		
		CardLabel label = new CardLabel();
		label.setImage(new ImageIcon(CardLabel.class.getResource("images/" + wildCard.toString() + "_YELLOW" + ".JPG")));
		label.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
				selectedColor = Card.Color.YELLOW;
				dispose();
			}
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}			
		});
		add(label, c);
		
		c.gridx++;
		label = new CardLabel();
		label.setImage(new ImageIcon(CardLabel.class.getResource("images/" + wildCard.toString() + "_BLUE" + ".JPG")));
		label.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
				selectedColor = Card.Color.BLUE;
				dispose();
			}
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}			
		});
		add(label, c);
		
		c.gridx = 0;
		c.gridy++;
		label = new CardLabel();
		label.setImage(new ImageIcon(CardLabel.class.getResource("images/" + wildCard.toString() + "_RED" + ".JPG")));
		label.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
				selectedColor = Card.Color.RED;
				dispose();
			}
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}			
		});
		add(label, c);
		
		c.gridx++;
		label = new CardLabel();
		label.setImage(new ImageIcon(CardLabel.class.getResource("images/" + wildCard.toString() + "_GREEN" + ".JPG")));
		label.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
				selectedColor = Card.Color.GREEN;
				dispose();
			}
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}			
		});
		add(label, c);
		
		refresh();
		setVisible(true);
	}
	
	/**
	 * Returns the chosen color.
	 * @return the chose color
	 */
	public Card.Color getColor() {
		return selectedColor;
	}
}
