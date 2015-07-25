package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import socket.ParticipantSocket;

import action.Action;
import action.PlayerReadyAction;
import action.RequestCloneAction;
import action.RequestDrawAction;
import action.RequestPlayAction;

import model.Card;
import model.GM;
import model.PlayerType;
import model.GameDeck.PileLocation;
import model.Player;
import model.ScoreKeeper;

/**
 * GUI used by a player joining a hosted game.  Handles/directs all display for the participating player.
 */
public class ParticipantGUI extends SpacedOutGUI implements Observer {
	private static final long serialVersionUID = 1L;

	private ParticipantSocket socket;
	private LinkedList<Player> players;
	private ScoreKeeper scoreKeeper;
	
	private int currentBattleDrawCount = 0;
	private PileLocation battlePile = null;
	
	/**
	 * Creates a new Participant GUI.
	 */
	public ParticipantGUI() {
		socket = null;
		players = null;
		scoreKeeper = null;
		
		newGameMenuItem.setText("Join Game");
		newGameMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				joinGame();
			}
		});
		replacePlayerMenuItem.setVisible(false);
		
		joinGame();
	}
	
	/**
	 * Joins a new game.
	 */
	private void joinGame() {
		// If a game is already in process, prompt the user to see if they want to end it and begin anew
		if (socket != null && !socket.isCleanlyClosed()) {
			String message = "Would you like to drop the host and join a different game?";
			int choice = JOptionPane.showConfirmDialog(this, message, "New Game", JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice != JOptionPane.YES_OPTION)
				return;
		}
		
		if (socket != null)
			socket.close();
		String defaultPlayerName = (defaultPlayers.isEmpty()) ? null : defaultPlayers.getFirst().getName();
		JoinGameDialog dialog = new JoinGameDialog(this, defaultPlayerName, defaultIP);
		dialog.setVisible(true);
		
		socket = dialog.getParticipantSocket();
		if (socket == null)
			return;
		defaultIP = socket.getHost();
		
		isGameOver = false;
		controlledPlayer = new Player(dialog.getPlayerName(), PlayerType.NETWORK);
		controlledPlayer.addObserver(this);
		if (defaultPlayers.isEmpty())
			defaultPlayers.add(new Player(controlledPlayer.getName(), PlayerType.NETWORK));
		else
			defaultPlayers.set(0, new Player(controlledPlayer.getName(), PlayerType.NETWORK));
		
		Thread socketListenerThread = new Thread() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				while (true) {
					String actionMessage = socket.getActionMessage();
					if (actionMessage == null) {
						if (socket.isCleanlyClosed())
							break;
						
						String message = "Lost connection to host: would you like to reconnect?";
						int choice = JOptionPane.showConfirmDialog(ParticipantGUI.this, message, "Network Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
						if (choice != JOptionPane.YES_OPTION) {
							socket.close();
							break;
						}
						while (true) {
							try {
								socket.reconnect(controlledPlayer.getName());
								break;
							}
							catch (IOException ex) {
								message = "Unable to connect to host: would you like to try again?";
								choice = JOptionPane.showConfirmDialog(ParticipantGUI.this, message, "Network Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
								if (choice != JOptionPane.YES_OPTION) {
									socket.close();
									isGameOver = true;
									return;
								}
							}
						}
						continue;
					}
					
					@SuppressWarnings("rawtypes")
					Action action = Action.parseAction(actionMessage);
					String[] replyMessages = null;
					if (action.getActionTypeClass().equals(PlayAreaPanel.class))
						replyMessages = action.performAction(playAreaPanel);
					else
						replyMessages = action.performAction(ParticipantGUI.this);
					
					if (replyMessages != null)
						for (String replyMessage : replyMessages)
							socket.sendActionMessage(replyMessage);
				}
				
				isGameOver = true;
			}
		};
		socketListenerThread.start();
	}
	
	@Override
	protected void displayOptions() {
		(new OptionsDialog(this, GM.getEndGameScore(), GM.getDealNumber())).setVisible(true);
	}
	
	@Override
	protected void exitProgram() {
		if (socket != null && !isGameOver) {
			int choice = JOptionPane.showConfirmDialog(this, "Disconnect from host and exit program?", "Quit", 
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (choice != JOptionPane.YES_OPTION)
				return;
		}
		
		if (socket != null)
			socket.close();
		updateConfigFile();
				
		this.dispose();
		System.exit(0);
	}

	@Override
	protected void displayScoreSheet() {
		(new ScoreSheetDialog(this, scoreKeeper, players, isGameOver)).setVisible(true);
	}
	
	@Override
	protected LinkedList<Player> getPlayers() {
		return players;
	}

	@Override
	public boolean drawCards() {
		if (controlledPlayer == null)
			return false;
		
		playAreaPanel.displayMessage("");
		socket.sendActionMessage(new RequestDrawAction().createMessage());
		return false;
	}

	@Override
	public boolean playCard(Card card, PileLocation location) {
		if (location == null)
			return false;
		
		playAreaPanel.displayMessage("");
		if (card.getColor() == Card.Color.WILD) {
			ChooseColorDialog dialog = new ChooseColorDialog(this, "Choose A Color", card);
			socket.sendActionMessage(new RequestPlayAction().createMessage(card, location, dialog.getColor()));
		}
		
		socket.sendActionMessage(new RequestPlayAction().createMessage(card, location));
		return true;
	}

	@Override
	public boolean playClone(Card card, PileLocation location) {
		if (location == null)
			return false;
		
		playAreaPanel.displayMessage("");
		socket.sendActionMessage(new RequestCloneAction().createMessage(card, location));
		return true;
	}
	
	/**
	 * Starts a new game with the given players.
	 * @param players players in the new game
	 * @param controlledPlayerName the name of the player that represents this participant
	 */
	public void newGame(LinkedList<Player> players, String controlledPlayerName) {
		this.players = players;
		controlledPlayer = getPlayer(controlledPlayerName);
		controlledPlayer.addObserver(this);
		scoreKeeper = new ScoreKeeper(players);
		playersDisplayPanel.setPlayers(players);
	}
	
	/**
	 * Begins a new round and sets the controlled player's cards to a newly dealt hand.
	 * @param playerCards newly dealt cards for the controlled player
	 */
	public void beginRound(Card[] playerCards) {
		isGameOver = false;
		backwardDirectionPanel.setVisible(false);
		forwardDirectionPanel.setVisible(true);
		playAreaPanel.showReadyButton(controlledPlayer);
		playAreaPanel.setCards(playerCards);
		playAreaPanel.displayMessage("");
	}
	
	/**
	 * Sets the current turn to the given player.
	 * @param player name of the player whose turn it now is
	 */
	public void setCurrentPlayer(String playerName) {
		Player player = getPlayer(playerName);
		playersDisplayPanel.setCurrentPlayer(player);
		if (player == controlledPlayer && controlledPlayer.isReadyToPlay()) {
			playAreaPanel.displayMessage("It's your turn");
			playTurnNotifyClip();
		}
	}
	
	/**
	 * Sets the card count for the given player.
	 * @param playerName name of the player to change the card count of
	 * @param cardCount number of cards to set
	 */
	public void setPlayerCardCount(String playerName, int cardCount) {
		Player player = getPlayer(playerName);
		while (player.countCards() > cardCount) {
			player.discard(null);
		}
		while (player.countCards() < cardCount) {
			player.addCard(null);
		}
	}
	
	/**
	 * Sets the cards of the controlled player.
	 * @param cards cards to set
	 */
	public void setControlledPlayerCards(Card[] cards) {
		playAreaPanel.setCards(cards);
		if (controlledPlayer.countCards() == playAreaPanel.getHandCardMax())
			playAreaPanel.displayMessage("You've reached the maximum number of cards allowed and cannot draw any more.");
		else 
			playAreaPanel.displayMessage(null);
		battleUpdate(currentBattleDrawCount, battlePile);
	}
	
	/**
	 * Changes the direction of play.
	 */
	public void changePlayDirection() {
		backwardDirectionPanel.setVisible(!backwardDirectionPanel.isVisible());
		forwardDirectionPanel.setVisible(!forwardDirectionPanel.isVisible());
	}
	
	/**
	 * Signal that a clone was played on the given pile and display that action.
	 * @param pileCloned the pile, specified by location, where a clone was played
	 */
	public void clonePlayed(PileLocation pileCloned) {
		playAreaPanel.displayClone(pileCloned);
	}
	
	/**
	 * Update the display with the latest battle data.
	 * @param battleDrawCount the number of cards needed to be drawn to end the battle.
	 * @param livePile the live pile, specified by location
	 */
	public void battleUpdate(int battleDrawCount, PileLocation livePile) {
		currentBattleDrawCount = battleDrawCount;
		battlePile = livePile;
		playAreaPanel.displayBattleCardCount(battleDrawCount, livePile);
	}
	
	/**
	 * Adds the round scores for each player to the score keeper.
	 * @param playerScores map of each player to his score for the finished round.
	 */
	public void addRoundScores(HashMap<Player, Integer> playerScores) {
		scoreKeeper.addPlayerRoundScores(playerScores);
	}
	
	/**
	 * Adds the round scores for each player to the score keeper and displays the score sheet.
	 * @param playerScores map of each player to his score for the finished round.
	 */
	public void addAndDisplayRoundScores(HashMap<Player, Integer> playerScores) {
		scoreKeeper.addPlayerRoundScores(playerScores);
		displayScoreSheet();
		playAreaPanel.setCards(new Card[0]);
		controlledPlayer.setReadyToPlay(false);
	}
	
	/**
	 * Signals the end of the game.
	 */
	public void endGame() {
		isGameOver = true;
	}
	
	/**
	 * Sets the options values.
	 * @param endGameScore the set score that when reached or passed will end the game
	 * @param numDealCards the set number of cards that will be dealt at the beginning of each round
	 */
	public void setOptions(int endGameScore, int numDealCards) {
		GM.setEndGameScore(endGameScore);
		GM.setDealNumber(numDealCards);
	}
	
	public void update(Observable observable, Object obj) {
		if (observable instanceof Player && obj instanceof Boolean)
			if ((Player)observable == controlledPlayer)
				socket.sendActionMessage(new PlayerReadyAction().createMessage((Player)observable, (Boolean)obj));
	}
}
