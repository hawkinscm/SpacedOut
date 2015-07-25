package gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import model.Card;

/**
 * Label for displaying a card.
 */
public class CardLabel extends JLabel {
	private static final long serialVersionUID = 1L;
	
	public final static int CARD_WIDTH = 74; //68;
	public final static int CARD_HEIGHT = 111; //105;
	
	private final int BORDER = 2;
	
	private Card card;
	
	/**
	 * Creates a new card label that will display the back of a Spaced Out Card.
	 */
	public CardLabel() {
		ImageIcon icon = new ImageIcon(CardLabel.class.getResource("images/Spaced_Out_back.JPG"));
		setImage(icon);
				
		initialize();
	}
	
	/**
	 * Creates new card label for the given card.
	 * @param card card to display
	 */
	public CardLabel(Card card) {
		setCard(card);		
		
		initialize();
	}
	
	/**
	 * Perform standard operations for all constructors
	 */
	private void initialize() {
		setSize(CARD_WIDTH, CARD_HEIGHT);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	/**
	 * Returns the card displayed by this label or null if not attached to a card.
	 * @return the card displayed by this label or null if not attached to a card
	 */
	public Card getCard() {
		return card;
	}
	
	/**
	 * Sets the card to display.
	 * @param card new card to display.
	 */
	public void setCard(Card card) {
		this.card = card;
		
		ImageIcon icon;
		if (card == null)
			icon = new ImageIcon(CardLabel.class.getResource("images/blank.JPG"));
		else
			icon = new ImageIcon(CardLabel.class.getResource("images/" + card.toString() + ".JPG"));
		setImage(icon);
	}
	
	/**
	 * Sets the card labels image.
	 * @param icon icon image to set
	 */
	public void setImage(ImageIcon icon) {
		Image resizedImage = icon.getImage().getScaledInstance(CARD_WIDTH - BORDER, CARD_HEIGHT - BORDER, Image.SCALE_SMOOTH);
		setIcon(new ImageIcon(resizedImage));
		repaint();
	}
	
	/**
	 * Returns whether or not the given cardLabel overlaps this card label at any point.
	 * @param cardLabel card label to check
	 * @return true if the cardLabel overlaps this card label at any point; false otherwise
	 */
	public boolean isOverlappedBy(CardLabel cardLabel) {
		Point thisStartPoint = getLocation();
		Point thisEndPoint = new Point(thisStartPoint.x + CARD_WIDTH, thisStartPoint.y + CARD_HEIGHT);
				
		Point thatStartPoint = cardLabel.getLocation();
		Point thatEndPoint = new Point(thatStartPoint.x + CARD_WIDTH, thatStartPoint.y + CARD_HEIGHT);
		
		if (thatStartPoint.x >= thisEndPoint.x) return false;
		if (thatStartPoint.y >= thisEndPoint.y) return false;
		if (thatEndPoint.x <= thisStartPoint.x) return false;
		if (thatEndPoint.y <= thisStartPoint.y) return false;
		
		return true;
	}
}
