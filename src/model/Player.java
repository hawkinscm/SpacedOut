package model;

import java.util.LinkedList;
import java.util.Observable;

/**
 * Represents a Spaced Out player.  
 * Will notify all observers whenever the player's cards change in any way.
 */
public class Player extends Observable {

	private String playerName;
	private PlayerType playerType;
	
	private LinkedList<Card> cards;
	
	private boolean readyToPlay;
	
	/**
	 * Creates a new Spaced Out player with a name and player type.
	 * @param name name of the player
	 * @param type type of player
	 */
	public Player(String name, PlayerType type) {
		playerName = name;
		playerType = type;
		
		cards = new LinkedList<Card>();
		
		if (isComputer())
			readyToPlay = true;
		else
			readyToPlay = false;
	}
	
	@Override
	public String toString() {
		return playerName;
	}
	
	/**
	 * Returns the player's name.
	 * @return the player's name
	 */
	public String getName() {
		return playerName;
	}
	
	/**
	 * Returns the player's type.
	 * @return returns the player's type
	 */
	public PlayerType getPlayerType() {
		return playerType;
	}
	
	/**
	 * Sets the player's type to the given type.
	 * @param type player type to set
	 */
	public void setPlayerType(PlayerType type) {
		if (playerType == type)
			return;
		
		playerType = type;
		setChanged();
		notifyObservers(type);
	}
	
	/**
	 * Returns whether or not the player is a computer.
	 * @return true if the player is a computer; false, otherwise
	 */
	public boolean isComputer() {
		if (playerType == PlayerType.COMPUTER_EASY)
			return true;
		else if (playerType == PlayerType.COMPUTER_MEDIUM)
			return true;
		else if (playerType == PlayerType.COMPUTER_HARD)
			return true;
		
		return false;
	}
	
	/**
	 * Returns the number of cards in the player's hand.
	 * @return the number of cards in the player's hand
	 */
	public int countCards() {
		return cards.size();
	}
	
	/**
	 * Adds the given card to player's cards.
	 * Notifies observers when cards successfully added.
	 * @param card card to add
	 */
	public void addCard(Card card) {
		cards.add(card);
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Adds the given cards to player's cards.
	 * Notifies observers when cards successfully added.
	 * @param cards cards to add
	 */
	public void addCards(LinkedList<Card> newCards) {
		if (newCards == null || newCards.isEmpty())
			return;
		
		cards.addAll(newCards);
		setChanged();
		notifyObservers();
	}
		
	/**
	 * Removes a card from the player's hand.
	 * Notifies observers when card successfully discarded.
	 * @param card the card to remove
	 * @return true if the card was in the player's hand; false otherwise
	 */
	public boolean discard(Card card) {
		if (cards.remove(card) == false)
			return false;
		
		setChanged();
		notifyObservers();
		return true;
	}
	
	/**
	 * Discards all the player's cards and returns the list of discarded cards.
	 * Notifies observers when cards successfully discarded.
	 * @return the list of discarded cards
	 */
	public LinkedList<Card> discardAll() {
		LinkedList<Card> discards = cards;
		cards = new LinkedList<Card>();
		
		if (!discards.isEmpty()) {
			setChanged();
			notifyObservers();
		}
		
		return discards;
	}
	
	/**
	 * Returns an array of the player's cards.
	 * @return an array of the player's cards
	 */
	public Card[] getCards() {
		return cards.toArray(new Card[0]);
	}
	
	/**
	 * Returns the total point value of this player's cards.
	 * @return the total point value of this player's cards
	 */
	public int getCardScore() {
		int score = 0;
		for (Card card : cards)
			score += card.getPointValue();
		
		return score;
	}
	
	/**
	 * Returns whether or not the player is marked as ready to play.
	 * @return true if the player is marked as ready to play; false otherwise
	 */
	public boolean isReadyToPlay() {
		return readyToPlay;
	}
	
	/**
	 * Sets whether the player is marked as ready to play or not.
	 * @param ready whether or not the player is marked as ready to play
	 */
	public void setReadyToPlay(boolean ready) {
		if (ready == false && isComputer())
			return;
		
		readyToPlay = ready;
		setChanged();
		notifyObservers(new Boolean(ready));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Player)
			return playerName.equals(((Player)obj).getName());
		return false;
	}
}
