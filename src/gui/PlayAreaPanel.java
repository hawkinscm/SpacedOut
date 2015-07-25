package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import model.Card;
import model.CardComparator;
import model.Player;
import model.SortedLinkedList;
import model.GameDeck.PileLocation;

/**
 * Displays and allows the playing (drag and drop) of a player's hand cards.
 */
public class PlayAreaPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static boolean playHelpsEnabled = true;
	
	private final int OUTER_PADDING = 38;
	private final int HAND_CARD_SPACING = 25;
	private int totalWidth = 800;
	private int rowCardMax = 100;
	
	private CardLabel leftPlayPileLabel;
	private JLabel leftPileDisplayLabel;
	private static JLabel leftPlayHelpsLabel;
	private Card leftPlayPileCard;
	private CardLabel drawDeckLabel;
	private CardLabel rightPlayPileLabel;
	private JLabel rightPileDisplayLabel;
	private static JLabel rightPlayHelpsLabel;
	private Card rightPlayPileCard;
	private JLabel messageLabel;
	private CustomButton readyButton;
	private LinkedList<CardLabel> handCardLabels;
	
	private Timer clearMessageTimer;
	private Timer fadeInTimer;
	private Timer fadeOutTimer;
	private Timer leftClearPileDisplayTimer;
	private Timer rightClearPileDisplayTimer;
	private Timer drawDeckHighlightTimer;

	private SpacedOutGUI parent;
	private Player displayedPlayer;
	private SortedLinkedList<Card> handCards;
	
	/**
	 * Creates a new hand cards panel for displaying and playing cards.
	 * @param comparator card comparator for the player's hand cards
	 */
	public PlayAreaPanel(SpacedOutGUI parent, CardComparator comparator) {
		this.parent = parent;
		totalWidth = parent.getSize().width - SpacedOutGUI.JFRAME_BORDER_WIDTH * 2;
		rowCardMax = 1 + (totalWidth - CardLabel.CARD_WIDTH) / HAND_CARD_SPACING;
		handCardLabels = new LinkedList<CardLabel>();
		
		buildTimers();
		
		setLayout(null);
		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
		
		buildPlayPilesDisplay();
		
		handCards = new SortedLinkedList<Card>(comparator);		
		resetDisplay();		
	}
	/**
	 * Sets whether or not to display play helps.
	 * @param enabled if true, display play helps; if false, hide play helps
	 */
	public static void setPlayHelpsEnabled(boolean enabled) {
		if (playHelpsEnabled == enabled)
			return;
		
		playHelpsEnabled = enabled;
		if (leftPlayHelpsLabel != null) {
			leftPlayHelpsLabel.setText("Play helps are on");
			leftPlayHelpsLabel.setVisible(playHelpsEnabled);
		}
		if (rightPlayHelpsLabel != null) {
			rightPlayHelpsLabel.setText("Play helps are on");
			rightPlayHelpsLabel.setVisible(playHelpsEnabled);
		}
	}
	
	/**
	 * Return whether or not play helps are being displayed.
	 * @return true if play helps are being displayed; false if they are not
	 */
	public static boolean arePlayHelpsEnabled() {
		return playHelpsEnabled;
	}
	
	/**
	 * Builds timers used in animating and displaying Play Area changes.
	 */
	private void buildTimers() {
		Action fadeInAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				int fadeAmount = 20;
				int fadeColor = messageLabel.getForeground().getBlue() - fadeAmount;
		    	messageLabel.setForeground(new Color(255, fadeColor, fadeColor));
		    	if (fadeColor < fadeAmount)
		    		fadeInTimer.stop();
		    }
		};
		fadeInTimer = new Timer(50, fadeInAction);
		
		Action fadeOutAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				int fadeAmount = 20;
				int fadeColor = messageLabel.getForeground().getBlue() + fadeAmount;
		    	messageLabel.setForeground(new Color(255, fadeColor, fadeColor));
		    	if (fadeColor > 205 - fadeAmount) {
		    		fadeOutTimer.stop();
			    	messageLabel.setText("");
		    	}
		    }
		};
		fadeOutTimer = new Timer(50, fadeOutAction);
		
		Action clearMessageAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				fadeOutTimer.start();
		    }
		};
		clearMessageTimer = new Timer(5000, clearMessageAction);
		clearMessageTimer.setRepeats(false);
		
		Action leftClearPileDisplayAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				leftPileDisplayLabel.setText("");
		    }
		};
		leftClearPileDisplayTimer = new Timer(1000, leftClearPileDisplayAction);
		leftClearPileDisplayTimer.setRepeats(false);
		
		Action rightClearPileDisplayAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				rightPileDisplayLabel.setText("");
		    }
		};
		rightClearPileDisplayTimer = new Timer(1000, rightClearPileDisplayAction);
		rightClearPileDisplayTimer.setRepeats(false);
		
		Action drawDeckHighlightAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed(ActionEvent e) {
				drawDeckLabel.setSize(new Dimension(CardLabel.CARD_WIDTH, CardLabel.CARD_HEIGHT));
				drawDeckLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			}
		};
		drawDeckHighlightTimer = new Timer(750, drawDeckHighlightAction);
		drawDeckHighlightTimer.setRepeats(false);
		
	}
	
	/**
	 * Builds the Play Piles display area.
	 */
	private void buildPlayPilesDisplay() {
		int innerPadding = 20;
		int playPilesWidth = 3 * CardLabel.CARD_WIDTH + 2 * innerPadding;
		int startX = (totalWidth - playPilesWidth) / 2;
		int startY = OUTER_PADDING;
		
		leftPileDisplayLabel = new JLabel();
		leftPileDisplayLabel.setHorizontalAlignment(JLabel.CENTER);
		leftPileDisplayLabel.setVerticalAlignment(JLabel.CENTER);
		leftPileDisplayLabel.setFont(leftPileDisplayLabel.getFont().deriveFont(15f));
		leftPileDisplayLabel.setForeground(Color.BLUE);
		leftPileDisplayLabel.setSize(CardLabel.CARD_WIDTH, 40);
		add(leftPileDisplayLabel);
		leftPileDisplayLabel.setLocation(startX, startY - 40);
		
		leftPlayHelpsLabel = new JLabel();
		leftPlayHelpsLabel.setHorizontalAlignment(JLabel.RIGHT);
		leftPlayHelpsLabel.setVerticalAlignment(JLabel.CENTER);
		leftPlayHelpsLabel.setForeground(Color.BLACK);
		leftPlayHelpsLabel.setSize(startX - 10, CardLabel.CARD_HEIGHT + 14);
		add(leftPlayHelpsLabel);
		leftPlayHelpsLabel.setLocation(5, startY - 7);
		leftPlayHelpsLabel.setVisible(playHelpsEnabled);
		
		leftPlayPileLabel = new CardLabel(null);
		add(leftPlayPileLabel);
		leftPlayPileLabel.setLocation(startX, startY);
		startX += CardLabel.CARD_WIDTH + innerPadding;		
		
		drawDeckLabel = new CardLabel();
		drawDeckLabel.addMouseListener(new MouseListener() {
			private boolean isInside = false;
			
			public void mouseClicked(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON1 || !isInside || !isEnabled())
					return;
				
				parent.drawCards();
			}
			public void mouseEntered(MouseEvent e) {isInside = true;}
			public void mouseExited(MouseEvent e) {isInside = false;}
		});
		add(drawDeckLabel);
		drawDeckLabel.setLocation(startX, startY);
		startX += CardLabel.CARD_WIDTH + innerPadding;
		
		rightPileDisplayLabel = new JLabel();
		rightPileDisplayLabel.setHorizontalAlignment(JLabel.CENTER);
		rightPileDisplayLabel.setVerticalAlignment(JLabel.CENTER);
		rightPileDisplayLabel.setFont(rightPileDisplayLabel.getFont().deriveFont(15f));
		rightPileDisplayLabel.setForeground(Color.BLUE);
		rightPileDisplayLabel.setSize(CardLabel.CARD_WIDTH, 40);
		add(rightPileDisplayLabel);
		rightPileDisplayLabel.setLocation(startX, startY - 40);
		
		rightPlayHelpsLabel = new JLabel();
		rightPlayHelpsLabel.setHorizontalAlignment(JLabel.LEFT);
		rightPlayHelpsLabel.setVerticalAlignment(JLabel.CENTER);
		rightPlayHelpsLabel.setForeground(Color.BLACK);
		rightPlayHelpsLabel.setSize((totalWidth - startX) - 10, CardLabel.CARD_HEIGHT + 14);
		add(rightPlayHelpsLabel);
		rightPlayHelpsLabel.setLocation(startX + CardLabel.CARD_WIDTH + 5, startY - 7);
		rightPlayHelpsLabel.setVisible(playHelpsEnabled);
		
		rightPlayPileLabel = new CardLabel(null);		
		add(rightPlayPileLabel);
		rightPlayPileLabel.setLocation(startX, startY);
				
		startX = 5;
		startY += CardLabel.CARD_HEIGHT + 5;		
		messageLabel = new JLabel();
		messageLabel.setHorizontalAlignment(JLabel.CENTER);
		messageLabel.setVerticalAlignment(JLabel.TOP);
		messageLabel.setFont(rightPileDisplayLabel.getFont().deriveFont(14f));
		messageLabel.setSize(totalWidth - 10, 50);
		add(messageLabel);
		messageLabel.setLocation(startX, startY);
		
		int readyButtonWidth = 72;
		startX = (totalWidth - readyButtonWidth) / 2;
		startY--;
		readyButton = new CustomButton("READY") {
			private static final long serialVersionUID = 1L;

			public void buttonPressed() {
				displayedPlayer.setReadyToPlay(true);
				setVisible(false);
			}
		};
		readyButton.setSize(readyButtonWidth, 30);
		add(readyButton);
		readyButton.setLocation(startX, startY);
		readyButton.setVisible(false);
	}
	
	private int clickX = 0;
	private int clickY = 0;
	private int selectedButton = 0;
	
	/**
	 * Displays and sets up the playing of hand cards.
	 */
	private void resetDisplay() {
		synchronized (handCardLabels) {
			for (CardLabel label : handCardLabels)
				remove(label);
			handCardLabels.clear();
		}
				
		int handCardsWidth;
		if (handCards.size() >= rowCardMax)
			handCardsWidth = (rowCardMax - 1) * HAND_CARD_SPACING + CardLabel.CARD_WIDTH;
		else
			handCardsWidth = (handCards.size() - 1) * HAND_CARD_SPACING + CardLabel.CARD_WIDTH;
		int startX = (totalWidth - handCardsWidth) / 2;
		int startY = OUTER_PADDING + CardLabel.CARD_HEIGHT + OUTER_PADDING;
		
		final JPanel self = this;
		synchronized (handCards) {
			for (final Card card : handCards) {
				final CardLabel cardLabel = new CardLabel(card);
				cardLabel.addMouseListener(new MouseListener() {
					Point startPosition = null;
					int order;
					
					public void mousePressed(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
							if (selectedButton != 0)
								return;
							
							selectedButton = e.getButton();
							
							startPosition = cardLabel.getLocation();
							order = self.getComponentZOrder(cardLabel);
							
							clickX = e.getX();
							clickY = e.getY();
							self.setComponentZOrder(cardLabel, 0);
							repaint();
						}
					}
					
					public void mouseReleased(MouseEvent e) {
						if (e.getButton() != selectedButton)
							return;
						
						if (startPosition != null) {
							PileLocation selectedPile = null;
							
							if (leftPlayPileLabel.isOverlappedBy(cardLabel)) {
								selectedPile = PileLocation.LEFT;
								cardLabel.setLocation(leftPlayPileLabel.getLocation());
							}
							else if (rightPlayPileLabel.isOverlappedBy(cardLabel)) {
								selectedPile = PileLocation.RIGHT;
								cardLabel.setLocation(rightPlayPileLabel.getLocation());
							}
							
							if (e.getButton() == MouseEvent.BUTTON1 && parent.playCard(card, selectedPile))
								removeCard();
							else if (e.getButton() == MouseEvent.BUTTON3 && parent.playClone(card, selectedPile))
								removeCard();
							else {
								self.setComponentZOrder(cardLabel, order);
								cardLabel.setLocation(startPosition);
							}
	
							repaint();
						}
						selectedButton = 0;
						startPosition = null;
					}
					
					public void removeCard() {
						synchronized (handCards) {
							handCards.remove(card);
							remove(cardLabel);
						}
						
						Point lastLocation = null;
						synchronized (handCardLabels) {
							for (CardLabel currentLabel : handCardLabels) {
								if (currentLabel == cardLabel)
									lastLocation = startPosition;
								else if (lastLocation != null) {
									Point tempLocation = currentLabel.getLocation();
									currentLabel.setLocation(lastLocation);
									lastLocation = tempLocation;					
								}
							}
							handCardLabels.remove(cardLabel);
						}
					}
	
					public void mouseClicked(MouseEvent e) {}
					public void mouseEntered(MouseEvent e) {}
					public void mouseExited(MouseEvent e) {}			
				});
				cardLabel.addMouseMotionListener(new MouseMotionListener() {
					public void mouseDragged(MouseEvent e) {
						if (selectedButton == 0)
							return;
						
						cardLabel.setLocation(cardLabel.getX() + e.getX() - clickX, cardLabel.getY() + e.getY() - clickY);
					}
	
					public void mouseMoved(MouseEvent arg0) {}				
				});
				add(cardLabel, 0);
				synchronized (handCardLabels) {
					handCardLabels.add(cardLabel);
				}
				if (startX + CardLabel.CARD_WIDTH > totalWidth) {				
					startX = (totalWidth - handCardsWidth) / 2;
					startY += CardLabel.CARD_HEIGHT + 5;
				}
				cardLabel.setLocation(startX, startY);
				startX += HAND_CARD_SPACING;
			}
		}
		
		leftClearPileDisplayTimer.restart();
		rightClearPileDisplayTimer.restart();
		validate();
		repaint();
	}
	
	/**
	 * Returns the location, or the upper-left point, of the specified pile relative to this play area panel.
	 * @param location play pile specified by its location relative to the draw deck; if null, specifies the draw deck
	 * @return the location, upper-left point, of the specified pile; if the specified pile is null returns the draw deck's location
	 */
	public Point getPileLocation(PileLocation location) {
		if (location == PileLocation.LEFT)
			return leftPlayPileLabel.getLocation();
		else if (location == PileLocation.RIGHT)
			return rightPlayPileLabel.getLocation();
		else
			return drawDeckLabel.getLocation();
	}
	
	/**
	 * Returns the maximum number of hand cards that can be appropriately displayed.
	 * @return the maximum number of hand cards that can be appropriately displayed
	 */
	public int getHandCardMax() {
		int rows = 2;
		return rowCardMax * rows;
	}
	
	/**
	 * Replace all cards from the hand with the given cards.
	 * @param cards new cards that will replace current cards
	 */
	public void setCards(Card[] cards) {
		synchronized (handCards) {
			handCards.clear();
			
			if (cards != null && cards.length > 0)
				for (Card card: cards)
					handCards.add(card);
		}
		
		resetDisplay();
	}
	
	/**
	 * Returns the card comparator for sorting the hand cards.
	 * @return the card comparator for sorting the hand cards
	 */
	public CardComparator getComparator() {
		return (CardComparator)handCards.getComparator();
	}
	
	/**
	 * Sets the card comparator for sorting the hand cards.
	 * @param comparator the card comparator to set
	 */
	public void setComparator(CardComparator comparator) {
		handCards.setComparator(comparator);
		resetDisplay();
	}
	
	/**
	 * Temporarily displays a message to the user.
	 * @param message message to display
	 */
	public void displayMessage(String message) {
		clearMessageTimer.stop();
		fadeInTimer.stop();
		fadeOutTimer.stop();
		
		messageLabel.setForeground(new Color(255, 205, 205));
		messageLabel.setText(message);
		if (message == null || message.isEmpty())
			return;
		
		fadeInTimer.start();
		clearMessageTimer.start();
	}
	
	/**
	 * Shows the ready button allowing the specified player to signal that he is ready to play.
	 */
	public void showReadyButton(Player player) {
		displayedPlayer = player;
		messageLabel.setText("");
		readyButton.setVisible(true);
	}
	
	/**
	 * Displays a clone message above the given pile.
	 * @param pile play pile specified by physical location 
	 */
	public void displayClone(PileLocation pile) {
		if (pile == PileLocation.LEFT) {
			leftPileDisplayLabel.setText("CLONE!");
			leftClearPileDisplayTimer.restart();			
		}
		else if (pile == PileLocation.RIGHT) {
			rightPileDisplayLabel.setText("CLONE!");
			rightClearPileDisplayTimer.restart();
		}
	}
	
	/**
	 * Sets the specified play pile to display the given card
	 * @param location play pile specified by physical location
	 * @param card card to set
	 */
	public void setPlayPileCard(PileLocation location, Card card) {
		if (location == PileLocation.LEFT)
			leftPlayPileLabel.setCard(card);
		else if (location == PileLocation.RIGHT)
			rightPlayPileLabel.setCard(card);
		else { return; }
		displayPlayHelps(location, true, card);
	}
	
	/**
	 * Sets the specified play pile to display the given wild card with its chosen color
	 * @param location play pile specified by physical location
	 * @param card card to set
	 * @param color color to set
	 */
	public void setPlayPileWildCard(PileLocation location, Card card, Card.Color color) {
		if (location == PileLocation.LEFT)
			leftPlayPileLabel.setImage(new ImageIcon(CardLabel.class.getResource("images/" + card.toString() + "_" + color + ".JPG")));
		else if (location == PileLocation.RIGHT)
			rightPlayPileLabel.setImage(new ImageIcon(CardLabel.class.getResource("images/" + card.toString() + "_" + color + ".JPG")));
		else { return; }
		displayPlayHelps(location, true, new Card(card.getDesign(), color, card.getPointValue()));
	}
	
	/**
	 * Appropriately displays the given pile location as the live pile.
	 * @param location play pile identified by physical location
	 */
	public void setLivePile(PileLocation location) {
		if (location == PileLocation.LEFT) {
			leftPlayPileLabel.setSize(new Dimension(CardLabel.CARD_WIDTH + 2, CardLabel.CARD_HEIGHT + 2));
			leftPlayPileLabel.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
		}
		else {
			leftPlayPileLabel.setSize(new Dimension(CardLabel.CARD_WIDTH, CardLabel.CARD_HEIGHT));
			leftPlayPileLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		}
		displayPlayHelps(PileLocation.LEFT, location == PileLocation.LEFT);
		
		if (location == PileLocation.RIGHT) {
			rightPlayPileLabel.setSize(new Dimension(CardLabel.CARD_WIDTH + 2, CardLabel.CARD_HEIGHT + 2));
			rightPlayPileLabel.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
		}
		else {
			rightPlayPileLabel.setSize(new Dimension(CardLabel.CARD_WIDTH, CardLabel.CARD_HEIGHT));
			rightPlayPileLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		}
		displayPlayHelps(PileLocation.RIGHT, location == PileLocation.RIGHT);
	}
	
	/**
	 * Displays that there is a battle currently going on requiring the given number of card to be drawn by the player being attacked.
	 * If the specified card count is 0, it will clear this display.
	 * @param cardCount number of cards that need to be drawn by the player being attacked.
	 * @param pile the pile location where the battle is taking place
	 */
	public void displayBattleCardCount(int cardCount, PileLocation pile) {
		if (pile == PileLocation.LEFT) {
			leftClearPileDisplayTimer.stop();
			if (cardCount > 0) {
				leftPileDisplayLabel.setText("<html><center>BATTLE<br>+" + cardCount + "&nbsp;</center></html>");
				leftPileDisplayLabel.setForeground(Color.BLUE);
			}
			else
				leftPileDisplayLabel.setText("");
		}
		else if (pile == PileLocation.RIGHT) {
			rightClearPileDisplayTimer.stop();
			if (cardCount > 0) {
				rightPileDisplayLabel.setText("<html><center>BATTLE<br>+" + cardCount + "&nbsp;</center></html>");
				rightPileDisplayLabel.setForeground(Color.BLUE);
			}
			else
				rightPileDisplayLabel.setText("");
		} 
		else { return; }
		displayPlayHelps(pile, true);
	}
	
	/**
	 * Display play helps if enabled for the given pile.
	 * @param pile the pile to display helps for
	 * @param isLivePile whether or not this pile is the live pile
	 */
	private void displayPlayHelps(PileLocation pile, boolean isLivePile) {
		displayPlayHelps(pile, isLivePile, null);
	}
	
	/**
	 * Display play helps if enabled for the given pile and card.
	 * @param pile the pile to display helps for
	 * @param isLivePile whether or not this pile is the live pile
	 * @param card card to display helps for
	 */
	private void displayPlayHelps(PileLocation pile, boolean isLivePile, Card card) {
		if (card != null) {
			if (card.getDesign() == Card.Design.BIG_BANG) {
				Card.Color pileColor = (pile == PileLocation.LEFT) ? leftPlayPileCard.getColor() : rightPlayPileCard.getColor();
				card = new Card(card.getDesign(), pileColor, card.getPointValue());
			}
			
			if (pile == PileLocation.LEFT)
				leftPlayPileCard = card;
			else
				rightPlayPileCard = card;
		}
		
		if (!playHelpsEnabled)
			return;
		
		JLabel playHelpsLabel;
		if (pile == PileLocation.LEFT) {
			playHelpsLabel = leftPlayHelpsLabel;
			card = leftPlayPileCard;
		}
		else {
			playHelpsLabel = rightPlayHelpsLabel;
			card = rightPlayPileCard;
		}
		if (card == null) {
			playHelpsLabel.setText("");
			return;
		}
		
		String message = "<html> ";
		boolean isBattle = leftPileDisplayLabel.getText().contains("BATTLE") || rightPileDisplayLabel.getText().contains("BATTLE");
		
		if (!isBattle) {
			if (playHelpsEnabled) {
				leftPlayHelpsLabel.setVisible(true);
				rightPlayHelpsLabel.setVisible(true);
			}				

			Card.Color bigBangColors = getBigBangColors();
			if (card.getDesign() == Card.Design.NUMBER || bigBangColors != null) {
				message += "If it's not someone else's extra turn you may: <br> ";
				
				if (card.getDesign() == Card.Design.NUMBER) {
					message += " &nbsp; clone (right-click & drag) with a ";
					message += card.getColor() + " " + card.getPointValue() + " <br> ";
				}
				if (bigBangColors == Card.Color.BLUE_AND_RED)
					message += " &nbsp; play a BLUE & RED BIG BANG <br> ";
				else if (bigBangColors == Card.Color.GREEN_AND_YELLOW)
					message += " &nbsp; play a GREEN & YELLOW BIG BANG <br> ";
			}
			
			message += "If it is your turn you may: <br> ";
			message += " &nbsp; draw <br> ";
			message += " &nbsp; play any SUPER or WILD card <br> ";
			if (isLivePile)
				message += " &nbsp; play any " + card.getColor() + " card <br> ";
			if (card.getDesign() == Card.Design.NUMBER)
				message += " &nbsp; play any color NUMBER " + card.getPointValue() + " <br> ";
			else if (card.getDesign() != Card.Design.BIG_BANG)
				message += " &nbsp; play any color " + card.getDesign().toString().replace('_', ' ') + " <br> ";
		}
		else {			
			if (pile == PileLocation.LEFT) {
				if (!leftPileDisplayLabel.getText().contains("BATTLE"))
					return;
				rightPlayHelpsLabel.setVisible(false);
			}
			else {
				if (!rightPileDisplayLabel.getText().contains("BATTLE"))
					return;
				leftPlayHelpsLabel.setVisible(false);
			}
			
			message += "If it is your turn you may: <br> ";
			message += " &nbsp; draw <br> ";
			if (card.getDesign() == Card.Design.ASTEROIDS || card.getDesign() == Card.Design.SHOOTING_STAR) {
				message += " &nbsp; play any color " + card.getDesign().toString().replace('_', ' ') + " <br> ";
				message += " &nbsp; play a " + card.getColor() + " FORCE FIELD <br> ";
				message += " &nbsp; play a SUPER FORCE FIELD <br> ";
			}
			else if (leftPileDisplayLabel.getText().contains("+3") || rightPileDisplayLabel.getText().contains("+3"))
				message += " &nbsp; play a SUPER FORCE FIELD <br> ";
			else if (card.getDesign() == Card.Design.FORCE_FIELD)
				message += " &nbsp; play any color FORCE FIELD <br> ";
				
		}		
			
		message += " </html>";
		playHelpsLabel.setText(message);
	}
	
	/**
	 * Returns the Big Bang colors of the current play piles.
	 * @return the Big Bang colors of the current play piles or null if they don't match either Big Bang colors.
	 */
	private Card.Color getBigBangColors() {
		if (leftPlayPileCard == null || rightPlayPileCard == null)
			return null;
		if (leftPlayPileCard.getColor() == Card.Color.BLUE && rightPlayPileCard.getColor() == Card.Color.RED)
			return Card.Color.BLUE_AND_RED;
		if (rightPlayPileCard.getColor() == Card.Color.BLUE && leftPlayPileCard.getColor() == Card.Color.RED)
			return Card.Color.BLUE_AND_RED;
		if (leftPlayPileCard.getColor() == Card.Color.GREEN && rightPlayPileCard.getColor() == Card.Color.YELLOW)
			return Card.Color.GREEN_AND_YELLOW;
		if (rightPlayPileCard.getColor() == Card.Color.GREEN && leftPlayPileCard.getColor() == Card.Color.YELLOW)
			return Card.Color.GREEN_AND_YELLOW;
		return null;
	}
}
