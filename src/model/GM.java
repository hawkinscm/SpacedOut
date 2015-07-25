package model;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Random;

import model.Card.Color;
import model.GameDeck.PileLocation;

/**
 * Managing class that does the main handling of a game.
 * Notifies observing classes any time something significant changes:
 *   a round ends
 *   a player's turn begins
 *   a battle begins/changes/ends
 *   cards were drawn
 *   a card was played
 *   a card was cloned
 *   play direction is reversed *   
 */
public class GM extends Observable {
		
	private static int endGameScore = 500;
	private static int dealNumber = 6;
	
	private int handCardMax;
	
	private ScoreKeeper scoreKeeper;
	private GameDeck gameDeck;	
	private PileLocation livePile;
	private LinkedList<Player> gamePlayers;
	private Player startingPlayer;
	private Player currentTurnPlayer;
	
	private Battle battle;
	
	private boolean isDirectionForward;
	private boolean isExtraTurn;
	
	/**
	 * Creates a new GM to handle core game functionality.
	 */
	public GM() {
		handCardMax = 100;
		
		scoreKeeper = null;
		gameDeck = null;	
		livePile = null;
		gamePlayers = new LinkedList<Player>();
		startingPlayer = null;
		currentTurnPlayer = null;
		
		battle = null;
		
		isDirectionForward = true;
		isExtraTurn = false;
	}
	
	/**
	 * Sets the current turn to be for the given player.
	 * @param player player whose turn it now is
	 */
	private void setCurrentTurn(Player player) {
		currentTurnPlayer = player;
		
		setChanged();
		notifyObservers(currentTurnPlayer);
	}
	
	/**
	 * Return whether or not there is a battle taking place.
	 * @return true if there is currently a battle taking place; false, otherwise
	 */
	private boolean isBattle() {
		return (battle != null);
	}
	
	/**
	 * Determine who the next player is and set them as the current player.
	 */
	private void nextPlayerTurn() {
		isExtraTurn = false;
		
		if (battle != null) {
			setCurrentTurn(battle.getNextAttackedPlayer());			
			return;
		}
		
		int playerIndex = gamePlayers.indexOf(currentTurnPlayer);
		if (isDirectionForward)
			playerIndex++;
		else
			playerIndex--;
		
		if (playerIndex < 0)
			playerIndex = gamePlayers.size() - 1;
		else if (playerIndex >= gamePlayers.size())
			playerIndex = 0;
		
		setCurrentTurn(gamePlayers.get(playerIndex));
	}
	
	/**
	 * Deals out cards to each player to begin a new round.
	 */
	private void dealCards() {
		for (Player player : gamePlayers) {
			LinkedList<Card> drawnCards = new LinkedList<Card>();
			for (int count = 1; count <= dealNumber; count++)
				drawnCards.add(gameDeck.drawCard());
			
			player.addCards(drawnCards);
		}
		
		do {
			Card card = gameDeck.drawCard();
			gameDeck.playCard(card, GameDeck.PileLocation.LEFT, card.getColor());
		} while (gameDeck.getTopCard(GameDeck.PileLocation.LEFT).getDesign() == Card.Design.BIG_BANG ||
				 gameDeck.getTopCard(GameDeck.PileLocation.LEFT).getColor() == Card.Color.WILD);
		
		do {
			Card card = gameDeck.drawCard();
			gameDeck.playCard(card, GameDeck.PileLocation.RIGHT, card.getColor());
		} while (gameDeck.getTopCard(GameDeck.PileLocation.RIGHT).getDesign() == Card.Design.BIG_BANG ||
				 gameDeck.getTopCard(GameDeck.PileLocation.RIGHT).getColor() == Card.Color.WILD);
		
		setChanged();
		notifyObservers(gameDeck);
	}
	
	/**
	 * Ends the round, tallies the scores, and re-shuffles all cards back into the deck.
	 */
	private void endRound() {
		for (Player player : gamePlayers)
			player.setReadyToPlay(false);
		
		if (battle != null) {
			setChanged();
			notifyObservers(new Battle(new LinkedList<Player>(), true));
			battle = null;
		}
		
		scoreKeeper.tallyRoundScores(gameDeck.getPlayPilesScore());
		
		LinkedList<Card> discards = new LinkedList<Card>();
		for (Player player : gamePlayers)
			discards.addAll(player.discardAll());
		gameDeck.returnCardsAndReshuffle(discards);
		
		setChanged();
		notifyObservers(scoreKeeper);
	}
	
	/**
	 * Sets the maximum number of cards a player is allowed to have in hand at one time.
	 * @param max number to set
	 */
	public void setHandCardMax(int max) {
		handCardMax = max;
	}
	
	/**
	 * Resets values and deals out cards to start a new round.
	 */
	public void beginRound() {
		livePile = null;
		isDirectionForward = true;
		
		currentTurnPlayer = startingPlayer;
		nextPlayerTurn();
		isExtraTurn = true;
		startingPlayer = currentTurnPlayer;
						
		dealCards();
	}
	
	/**
	 * Returns the minimum value score that will end the game when reached or passed.
	 * @return the minimum value score that will end the game when reached or passed
	 */
	public static int getEndGameScore() {
		return endGameScore;
	}
	
	/**
	 * Sets the minimum value score that will end the game when reached or passed.
	 */
	public static void setEndGameScore(int score) {
		endGameScore = score;
	}
	
	/**
	 * Returns the number of cards that will be dealt at the start of a round.
	 * @return the number of cards that will be dealt at the start of a round
	 */
	public static int getDealNumber() {
		return dealNumber;
	}
	
	/**
	 * Sets the number of cards that will be dealt at the start of a round.
	 */
	public static void setDealNumber(int number) {
		dealNumber = number;
	}
	
	/**
	 * Returns whether or not the play is going forward.
	 * @return true if the play is going forward; false if it has been reversed
	 */
	public boolean isDirectionForward() {
		return isDirectionForward;
	}
	
	/**
	 * Returns the live pile.
	 * @return the live pile specified by its physical location
	 */
	public PileLocation getLivePile() {
		return livePile;
	}
	
	/**
	 * Returns a copy of the card on top of the specified pile. 
	 * Note that if it is a wild card, the card's color will be set to the selected color.
	 * @param pile play pile specified by location
	 * @return a copy of the card on top of the specified pile
	 */
	public Card peekTopCard(PileLocation pile) {
		Card card = gameDeck.getTopCard(pile);
		return new Card(card.getDesign(), gameDeck.getColor(pile), card.getPointValue());
	}
	
	/**
	 * Returns the player whose turn it currently is.
	 * @return the player whose turn it currently is
	 */
	public Player getCurrentTurnPlayer() {
		return currentTurnPlayer;
	}
	
	/**
	 * Returns the list of players in this game.
	 * @return the list of players in this game
	 */
	public LinkedList<Player> getPlayers() {
		return gamePlayers;
	}
	
	/**
	 * Returns this game's ScoreKeeper.
	 * @return this game's ScoreKeeper
	 */
	public ScoreKeeper getScoreKeeper() {
		return scoreKeeper;
	}
	
	/**
	 * Returns whether or not all the players are ready to begin a new round
	 */
	public boolean areAllPlayersReadyToBeginNewRound() {
		for (Player player : gamePlayers)
			if (!player.isReadyToPlay())
				return false;
		
		return true;
	}
	
	/**
	 * Returns whether or not a player (with the right card) can play out of turn; 
	 * i.e. its not an extra turn or a battle.
	 * @return true if a player can play out of turn; false otherwise
	 */
	public boolean isPlayAllowedOutOfTurn() {
		return (!isBattle() && !isExtraTurn);
	}
	
	/**
	 * Resets all game data and starts a new game.
	 * @param players list of players for the new game
	 * @param numberOfDecks how many spaced out decks to use for this game 
	 */
	public void newGame(LinkedList<Player> players, int numberOfDecks) {
		scoreKeeper = new ScoreKeeper(players);
		
		gameDeck = new GameDeck(numberOfDecks);
		livePile = null;
		gamePlayers = players;
		Random generator = new Random();
		startingPlayer = players.get(generator.nextInt(players.size()));
		setCurrentTurn(startingPlayer);
		
		battle = null;
		
		isDirectionForward = true;
		isExtraTurn = true;
		
		dealCards();
	}
	
	/**
	 * Draws cards for the given player according to the game status and the rules.  
	 * Returns whether or not cards were drawn.
	 * @param player player requesting a drawn card
	 * @return true if the player was allowed to draw card(s); false, if the player isn't allowed to draw (i.e. not the player's turn)
	 */
	public synchronized boolean drawCardsIfAllowed(Player player) {
		if (!areAllPlayersReadyToBeginNewRound())
			return false;
		
		if (currentTurnPlayer == player) {
			int numberToDraw = 1;
			if (battle != null) {
				numberToDraw = battle.getTotalCardsToDraw();
				if (battle.isBigBangBattle()) {
					battle.removePlayer(player);
					if (battle.getPlayerCount() <= 1)
						battle = null;
				}
				else
					battle = null;
				
				if (battle == null) {
					setChanged();
					notifyObservers(new Battle(new LinkedList<Player>(), true));
				}
			}
			
			if (player.countCards() + numberToDraw > handCardMax)
				numberToDraw = handCardMax - player.countCards();
			
			LinkedList<Card> drawnCards = new LinkedList<Card>();
			for (int count = 1; count <= numberToDraw; count++)
				drawnCards.add(gameDeck.drawCard());
			player.addCards(drawnCards);
			
			setChanged();
			notifyObservers(new Integer(numberToDraw));
			nextPlayerTurn();
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns whether or not the given card can be played by the specified player on the specified pile.
	 * @param player the player to evaluate
	 * @param card card to evaluate
	 * @param illegalPlayReason will be populated with the reason the card can't be played; or null if valid play
	 * @return true if the card can currently be played; false otherwise
	 */
	public boolean canPlayCard(Player player, Card card, PileLocation pile, StringBuilder illegalPlayReason) {
		illegalPlayReason.setLength(0);
		Card topCard = gameDeck.getTopCard(pile);
		
		if (card.getDesign() == Card.Design.BIG_BANG) {
			if (isBattle())
				illegalPlayReason.append("Cannot be played during a battle.");
			else if (isExtraTurn && currentTurnPlayer != player)
				illegalPlayReason.append("Cannot be played during " + currentTurnPlayer + "'s extra turn.");
			else if (!gameDeck.hasBigBangColors(card.getColor()))
				illegalPlayReason.append("Playing piles do not have " + card.toString().replace('_', ' ') + " colors.");
		}
		else if (currentTurnPlayer != player)
			illegalPlayReason.append("It's not your turn.");
		else if (isBattle()) {
			if (pile != livePile)
				illegalPlayReason.append("Must continue battle on the LIVE PILE.");
			else
				return battle.canPlayCard(card, illegalPlayReason);
		}
		else if (card.getColor() != Color.WILD && !card.matchesDesign(topCard)) {
			if (livePile != null && pile != livePile)
				illegalPlayReason.append("Must be WILD or of the same design as the DEAD PILE to play there.");
			else if (card.getColor() != gameDeck.getColor(pile))
				illegalPlayReason.append("Must be WILD or of the same design or color as the LIVE PILE to play there.");
		}
		
		return (illegalPlayReason.length() == 0);
	}
		
	/**
	 * Attempts to play a card on the given pile for the given player.  
	 * Returns true when successfully played according to the rules.
	 * @param player requesting to play the card
	 * @param card the card to play
	 * @param pile pile (identified by location) to play the card on
	 * @return null if the card was played; otherwise return the reason why the card can't be played
	 */
	public String playCardIfAllowed(Player player, Card card, PileLocation pile) {
		return playCardIfAllowed(player, card, pile, card.getColor());
	}
	
	/**
	 * Attempts to play a card on the given pile for the given player.  
	 * Returns true when successfully played according to the rules.
	 * @param player requesting to play the card
	 * @param card the card to play
	 * @param pile pile (identified by location) to play the card on
	 * @param color the color of the card to play (needed for wild cards)
	 * @return null if the card was played; otherwise return the reason why the card can't be played
	 */
	public synchronized String playCardIfAllowed(Player player, Card card, PileLocation pile, Color color) {
		if (!areAllPlayersReadyToBeginNewRound())
			return (player.isReadyToPlay() ? "Not all players are ready to begin a new round." : "");
		
		// this is an internal error, display nothing to user
		if (color == null || color == Color.WILD)
			return "";
		
		StringBuilder illegalPlayReason = new StringBuilder();
		if (!canPlayCard(player, card, pile, illegalPlayReason))
			return illegalPlayReason.toString();
		
		if (card.getDesign() == Card.Design.BIG_BANG) {
			livePile = pile;
			battle = new Battle(gamePlayers, isDirectionForward);
			player.discard(card);
			battle.playCardIfAllowed(player, card, color);
			gameDeck.playCard(card, pile, null);
			setCurrentTurn(player);
			
			setChanged();
			notifyObservers(battle);
		}
		else if (isBattle()) {
			battle.playCard(player, card, color);
			player.discard(card);
			gameDeck.playCard(card, pile, color);
			
			setChanged();
			notifyObservers(battle);
		}
		else {
			player.discard(card);
			gameDeck.playCard(card, pile, color);
			livePile = pile;
		
			if (card.getDesign() == Card.Design.ASTEROIDS || card.getDesign() == Card.Design.SHOOTING_STAR) {
				battle = new Battle(gamePlayers, isDirectionForward);
				battle.playCardIfAllowed(player, card, color);
				
				setChanged();
				notifyObservers(battle);
			}
			else if (card.getDesign() == Card.Design.BLACK_HOLE) {
				isExtraTurn = true;		
				setChanged();
				notifyObservers(gameDeck);
				
				setCurrentTurn(player);
				
				if (player.countCards() == 0)
					endRound();
				return null;
			}
		}
		
		if (card.getDesign() == Card.Design.FORCE_FIELD) {
			isDirectionForward = !isDirectionForward;
			setChanged();
			notifyObservers(card);
		}
		
		setChanged();
		notifyObservers(gameDeck);
		
		if (player.countCards() == 0)
			endRound();
		else
			nextPlayerTurn();
		return null;
	}
		
	/**
	 * Returns whether or not the given card can be "cloned" by the specified player on the specified pile.
	 * @param player the player to evaluate
	 * @param card card to evaluate
	 * @param illegalPlayReason will be populated with the reason the card can't be played; or null if valid play
	 * @return true if the card can currently be played as a clone; false otherwise
	 */
	public boolean canPlayClone(Player player, Card clone, PileLocation pile, StringBuilder illegalPlayReason) {
		illegalPlayReason.setLength(0);
		if (isExtraTurn) {
			if (livePile == null)
				illegalPlayReason.append("Can't clone until the first card of the hand has been played.");
			else
				illegalPlayReason.append("Can't clone during " + currentTurnPlayer + "'s extra turn.");
		}
		else if (isBattle())
			illegalPlayReason.append("Can't clone during a battle.");
		else if (!clone.isClone(gameDeck.getTopCard(pile)))
			illegalPlayReason.append("Not a proper clone.");
		
		return (illegalPlayReason.length() == 0);
	}
	
	/**
	 * Attempts to play a "clone" on the given pile for the given player.  
	 * Returns true when successfully played according to the rules.
	 * @param player requesting to play the card
	 * @param clone the "clone" card to play
	 * @param pile pile (identified by location) to play the card on
	 * @return null if the card was played; otherwise return the reason why the card can't be played
	 */
	public synchronized String playCloneIfAllowed(Player player, Card clone, PileLocation pile) {
		if (!areAllPlayersReadyToBeginNewRound())
			return (player.isReadyToPlay() ? "Not all players are ready to begin a new round." : "");
		
		StringBuilder illegalPlayReason = new StringBuilder();
		if (!canPlayClone(player, clone, pile, illegalPlayReason))
			return illegalPlayReason.toString();
		
		Card topCard = gameDeck.getTopCard(pile);
		player.discard(clone);
		gameDeck.playCard(topCard, pile, clone.getColor());
		livePile = pile;
		isExtraTurn = true;
						
		setCurrentTurn(player);
		setChanged();
		notifyObservers(gameDeck);
			
		setChanged();
		notifyObservers(pile);
			
		if (player.countCards() == 0)
			endRound();
		return null;
	}
}
