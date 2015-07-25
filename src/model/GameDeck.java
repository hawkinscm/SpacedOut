package model;

import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

import model.Card.Color;

/**
 * Represents the draw pile and the 2 playing piles in a Spaced Out game.
 */
public class GameDeck {

	/**
	 * Identifies a playing pile by its location relative to the draw pile.
	 */
	public static enum PileLocation {
		LEFT,
		RIGHT;
	}
	
	private Stack<Card> drawPile;
	private Stack<Card> leftPile;
	private Stack<Card> rightPile;
	
	private Color leftPileColor;
	private Color rightPileColor;
	
	private Random randomGenerator;
	
	/**
	 * Creates a new game deck with all cards in the draw pile.
	 * @param numOfDecks number of decks to use for this game.
	 */
	public GameDeck(int numOfDecks) {
		randomGenerator = new Random();
		
		drawPile = new Stack<Card>();
		leftPile = new Stack<Card>();
		rightPile = new Stack<Card>();
		
		for (int count = 1; count <= numOfDecks; count++)
			drawPile.addAll(SpacedOutDeckFactory.buildDeck());
		
		shuffle();
	}
	
	/**
	 * Shuffles the cards in the draw pile in a random order.
	 */
	private void shuffle() {
		LinkedList<Card> tempDeck = new LinkedList<Card>();
		tempDeck.addAll(drawPile);
		drawPile.clear();
		
		for (int numCards = tempDeck.size(); numCards > 0; numCards--) {
			int cardNumber = randomGenerator.nextInt(numCards);
			drawPile.push(tempDeck.remove(cardNumber));
		}
	}
	
	/**
	 * Returns the card from the top of the draw pile.
	 * @return the card from the top of the draw pile
	 */
	public Card drawCard() {
		if (drawPile.isEmpty()) {
			Card topCard = leftPile.pop();
			drawPile.addAll(leftPile);
			leftPile.clear();
			leftPile.push(topCard);
			
			topCard = rightPile.pop();
			drawPile.addAll(rightPile);
			rightPile.clear();
			rightPile.push(topCard);
			
			// In rare case that all cards are in the player's hands, add a new deck
			if (drawPile.isEmpty())
				drawPile.addAll(SpacedOutDeckFactory.buildDeck());
			
			shuffle();
		}
		
		return drawPile.pop();
	}
	
	/**
	 * Places the given card on the top of the identified pile.
	 * @param card card to play
	 * @param pile pile to play the card, identified by its location relative to the draw pile
	 * @param color color that the pile will now be
	 */
	public void playCard(Card card, PileLocation pile, Color color) {
		if (pile == PileLocation.LEFT) {
			leftPile.push(card);
			if (color != null)
				leftPileColor = color;
		}
		else if (pile == PileLocation.RIGHT) {
			rightPile.push(card);
			if (color != null)
				rightPileColor = color;
		}
	}
	
	/**
	 * Returns all cards to the draw deck and re-shuffles.
	 * @param cards cards to return to the deck
	 */
	public void returnCardsAndReshuffle(LinkedList<Card> cards) {
		if (cards != null)
			drawPile.addAll(cards);
		drawPile.addAll(leftPile);
		drawPile.addAll(rightPile);
		
		leftPile.clear();
		rightPile.clear();
		
		shuffle();
	}
	
	/**
	 * Returns the top card from the given pile without removing it from the pile.
	 * @param pile pile to play the card, identified by its location relative to the draw pile
	 * @return the top card from the given pile
	 */
	public Card getTopCard(PileLocation pile) {
		if (pile == PileLocation.LEFT)
			return (leftPile.isEmpty()) ? null : leftPile.peek();
		else if (pile == PileLocation.RIGHT)
			return (rightPile.isEmpty()) ? null : rightPile.peek();
		
		return null;
	}
	
	/**
	 * Returns the current color of the given pile.
	 * @param pile pile to play the card, identified by its location relative to the draw pile
	 * @return the current color of the given pile
	 */
	public Color getColor(PileLocation pile) {
		if (pile == PileLocation.LEFT)
			return leftPileColor;
		else if (pile == PileLocation.RIGHT)
			return rightPileColor;
		
		return null;
	}
	
	/**
	 * Returns whether or not the play piles have the given Big Bang colors.
	 * @param Color bigBangColors
	 * @return true if the play piles match the given Bag Bang colors; false, otherwise
	 */
	public boolean hasBigBangColors(Color bigBangColors) {
		if (bigBangColors == Color.BLUE_AND_RED) {
			if (leftPileColor == Color.RED && rightPileColor == Color.BLUE)
				return true;
			if (rightPileColor == Color.RED && leftPileColor == Color.BLUE)
				return true;
			
			return false;
		}
		else if (bigBangColors == Color.GREEN_AND_YELLOW) {
			if (leftPileColor == Color.GREEN && rightPileColor == Color.YELLOW)
				return true;
			if (rightPileColor == Color.GREEN && leftPileColor == Color.YELLOW)
				return true;
			
			return false;
		}
		
		return false;
	}
	
	/**
	 * Returns the combined point score of the top card from each play pile.
	 * @return the combined point score of the top card from each play pile
	 */
	public int getPlayPilesScore() {
		return leftPile.peek().getPointValue() + rightPile.peek().getPointValue();
	}
}
