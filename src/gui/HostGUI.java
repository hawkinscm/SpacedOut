package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import socket.PlayerSocket;

import action.*;
import ai.AIManager;

import model.Battle;
import model.Card;
import model.GM;
import model.GameDeck;
import model.Player;
import model.PlayerType;
import model.ScoreKeeper;
import model.GameDeck.PileLocation;

/**
 * GUI used by the player hosting the game. Handles/directs all display for the
 * host.
 */
public class HostGUI extends SpacedOutGUI implements Observer {
	private static final long serialVersionUID = 1L;

	public static final int MAX_NUM_PLAYERS = 8;

	private LinkedList<PlayerSocket> playerSockets;

	private GM gm;

	/**
	 * Constructor for the Host GUI
	 */
	public HostGUI() {
		playerSockets = new LinkedList<PlayerSocket>();

		newGameMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newGame();
			}
		});
		replacePlayerMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				replacePlayer(null);
			}
		});

		gm = new GM();
		gm.addObserver(this);
		gm.setHandCardMax(playAreaPanel.getHandCardMax());

		newGame();
	}

	/**
	 * Starts a new game.
	 */
	private void newGame() {
		// If a game is already in process, prompt the user to see if they want
		// to end it and begin anew
		if (!isGameOver) {
			String message = "End current game?";
			int choice = JOptionPane.showConfirmDialog(this, message, "New Game", JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice != JOptionPane.YES_OPTION)
				return;
		}

		// Prompt the user for how many people will play
		int numPlayers = 0;
		while (numPlayers < 3 || numPlayers > MAX_NUM_PLAYERS) {
			try {
				String message = "How Many Will Play (3-" + MAX_NUM_PLAYERS	+ ")?";
				String result = JOptionPane.showInputDialog(this, message, "New Game", JOptionPane.QUESTION_MESSAGE);
				if (result == null)
					return;
				numPlayers = Integer.parseInt(result);
			} 
			catch (NumberFormatException ex) {}
		}

		// Lets the user define the players for the game
		LinkedList<PlayerSocket> connectedPlayerSockets = new LinkedList<PlayerSocket>();
		for (PlayerSocket playerSocket : playerSockets)
			if (!playerSocket.isCleanlyClosed())
				connectedPlayerSockets.add(playerSocket);
		NewGameDialog playerInput = new NewGameDialog(this, numPlayers,	defaultPlayers, connectedPlayerSockets);
		playerInput.setVisible(true);
		if (playerInput.getPlayers() == null) {
			newGame();
			return;
		}

		LinkedList<PlayerSocket> oldPlayerSockets = playerSockets;
		playerSockets = playerInput.getPlayerSockets();

		isGameOver = false;
		for (final PlayerSocket playerSocket : playerSockets) {
			if (oldPlayerSockets.contains(playerSocket))
				oldPlayerSockets.remove(playerSocket);
			else
				startPlayerSocketListener(playerSocket);
			playerSocket.sendActionMessage(new NewGameAction().createMessage(playerInput.getPlayers(), playerSocket.getPlayer().getName()));
		}
		for (PlayerSocket playerSocket : oldPlayerSockets)
			playerSocket.close();

		gm.newGame(playerInput.getPlayers(), playerInput.getNumDecksToUse());
		LinkedList<Player> gamePlayers = gm.getPlayers();
		AIManager aiManager = new AIManager(gm);
		for (int playerIdx = 0; playerIdx < gamePlayers.size(); playerIdx++) {
			Player currentPlayer = gamePlayers.get(playerIdx);
			currentPlayer.addObserver(aiManager);
			currentPlayer.addObserver(this);
			notifyPlayerSockets(new ChangePlayerCardsAction().createMessage(currentPlayer));

			if (currentPlayer.getPlayerType() == PlayerType.HOST)
				controlledPlayer = currentPlayer;

			if (playerIdx < defaultPlayers.size())
				defaultPlayers.set(playerIdx, currentPlayer);
			else
				defaultPlayers.add(currentPlayer);
		}

		for (PlayerSocket playerSocket : playerSockets)
			playerSocket.sendActionMessage(new BeginRoundAction().createMessage(playerSocket.getPlayer().getCards()));

		playersDisplayPanel.setPlayers(gm.getPlayers());
		backwardDirectionPanel.setVisible(false);
		forwardDirectionPanel.setVisible(true);
		playAreaPanel.showReadyButton(controlledPlayer);
		playAreaPanel.setCards(controlledPlayer.getCards());
	}

	/**
	 * Replaces the given player with a new player type. 
	 * @param player player to change; if null, asks to user to select a player to change
	 * @param whether or not the player should stay the same type;
	 * @return returns true if the player was replaced, false otherwise.
	 */
	private boolean replacePlayer(Player player) {
		ChangePlayerTypeDialog dialog;
		if (player != null)
			dialog = new ChangePlayerTypeDialog(HostGUI.this, player);
		else if (gm == null || gm.getPlayers().isEmpty()) {
			Messenger.display("There are no players yet to replace.", "Replace Player");
			return false;
		} 
		else
			dialog = new ChangePlayerTypeDialog(HostGUI.this, gm.getPlayers());

		dialog.setVisible(true);
		player = dialog.getChangedPlayer();
		if (player == null)
			return false;

		if (player.isComputer()) {
			Iterator<PlayerSocket> playerSocketIter = playerSockets.iterator();
			while (playerSocketIter.hasNext()) {
				PlayerSocket playerSocket = playerSocketIter.next();
				if (player == playerSocket.getPlayer()) {
					playerSocket.close();
					playerSocketIter.remove();
					playerSocket.getPlayer().setReadyToPlay(true);
					break;
				}
			}
		} 
		else {
			if (!connect(player)) {
				player.setPlayerType(dialog.getOldPlayerType());
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Connects to the given player and sends him all of the current game data.
	 * @param player player to connect to.
	 * @return true if a user was successfully connected to; false otherwise
	 */
	private boolean connect(Player player) {
		PlayerSocket playerSocket = null;
		Iterator<PlayerSocket> playerSocketIter = playerSockets.iterator();
		while (playerSocketIter.hasNext()) {
			PlayerSocket currentPlayerSocket = playerSocketIter.next();
			if (currentPlayerSocket.getPlayer() == player) {
				playerSocket = currentPlayerSocket;
				playerSocketIter.remove();
				break;
			}
		}
		if (playerSocket == null)
			playerSocket = new PlayerSocket(gm.getPlayers().indexOf(player));
		
		try {
			playerSocket.close();
			playerSocket.reconnect();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			Messenger.error("Timed Out - Unable to connect to participant",	"Replace Player");
			playerSocket.close();
			return false;
		}
		
		playerSocket.setPlayer(player);
		playerSockets.add(playerSocket);

		startPlayerSocketListener(playerSocket);
		playerSocket.sendActionMessage(new NewGameAction().createMessage(gm.getPlayers(), player.getName()));
		for (Player currentPlayer : gm.getPlayers()) {
			playerSocket.sendActionMessage(new ChangePlayerCardsAction().createMessage(currentPlayer));
			if (!currentPlayer.isComputer() && currentPlayer != player)
				playerSocket.sendActionMessage(new PlayerReadyAction().createMessage(currentPlayer, currentPlayer.isReadyToPlay()));
		}
		setPlayerReadyStatus(player, true);
		setPlayerReadyStatus(player, false);
		playerSocket.sendActionMessage(new BeginRoundAction().createMessage(playerSocket.getPlayer().getCards()));
		playerSocket.sendActionMessage(new ChangePlayerTurnAction().createMessage(gm.getCurrentTurnPlayer().getName()));
		if (!gm.isDirectionForward())
			playerSocket.sendActionMessage(new ChangePlayDirectionAction().createMessage());
		Card leftPileCard = gm.peekTopCard(PileLocation.LEFT);
		Card rightPileCard = gm.peekTopCard(PileLocation.RIGHT);
		if (leftPileCard != null && rightPileCard != null)
			playerSocket.sendActionMessage(new PlayedCardAction().createMessage(
				false, leftPileCard, leftPileCard.getColor(), rightPileCard, rightPileCard.getColor(), gm.getLivePile()));
		playerSocket.sendActionMessage(new ChangeOptionsAction().createMessage(GM.getEndGameScore(), GM.getDealNumber()));
		int numRoundsFinished = gm.getScoreKeeper().getNumberOfRoundsFinished();
		for (int round = 1; round <= numRoundsFinished; round++)
			playerSocket.sendActionMessage(new AddRoundScoresAction().createMessage(gm.getScoreKeeper().getRoundScores(round), false));
		return true;
	}

	private void startPlayerSocketListener(final PlayerSocket playerSocket) {
		final HostGUI self = this;
		Thread socketListenerThread = new Thread() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				while (true) {
					String actionMessage = playerSocket.getActionMessage();
					if (actionMessage == null) {
						if (playerSocket.isCleanlyClosed())
							break;

						setPlayerReadyStatus(playerSocket.getPlayer(), false);
						while (true) {
							String message = "Lost connection to participant \"" + playerSocket.getPlayer().getName() + "\": would you like to reconnect?";
							int choice = JOptionPane.showConfirmDialog(HostGUI.this, message, "Network Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
						    if (choice == JOptionPane.YES_OPTION) { 
						    	if (connect(playerSocket.getPlayer()))
						    		return;
							}
						    else if (replacePlayer(playerSocket.getPlayer()))
						    	return;
						}
					}

					@SuppressWarnings("rawtypes")
					Action action = Action.parseAction(actionMessage);
					String[] replyMessages = null;
					if (action.getActionTypeClass().equals(ActionPlayerGM.class))
						replyMessages = action.performAction(new ActionPlayerGM(gm, playerSocket.getPlayer()));
					else
						replyMessages = action.performAction(self);

					if (replyMessages != null)
						for (String replyMessage : replyMessages)
							playerSocket.sendActionMessage(replyMessage);
				}
				if (!playerSocket.isCleanlyClosed()) {
					Messenger.error("Lost connection to network player '" + playerSocket.getPlayer().getName() + "'.", "Network Error");
					playerSocket.close();
				}
			}
		};
		socketListenerThread.start();
	}

	@Override
	protected void displayOptions() {
		int highestScore = 0;
		LinkedList<Player> gamePlayers = gm.getPlayers();
		ScoreKeeper scoreKeeper = gm.getScoreKeeper();

		if (gamePlayers != null && scoreKeeper != null)
			for (Player player : gamePlayers) {
				int playerScore = scoreKeeper.getPlayerScore(player);
				if (playerScore > highestScore)
					highestScore = playerScore;
			}

		(new OptionsDialog(this, highestScore)).setVisible(true);
		notifyPlayerSockets(new ChangeOptionsAction().createMessage(GM.getEndGameScore(), GM.getDealNumber()));

		if (gamePlayers != null && scoreKeeper != null && isGameOver && GM.getEndGameScore() > highestScore) {
			isGameOver = false;
			gm.beginRound();

			for (PlayerSocket playerSocket : playerSockets)
				playerSocket.sendActionMessage(new BeginRoundAction().createMessage(playerSocket.getPlayer().getCards()));

			backwardDirectionPanel.setVisible(false);
			forwardDirectionPanel.setVisible(true);
			playAreaPanel.showReadyButton(controlledPlayer);
			playAreaPanel.setCards(controlledPlayer.getCards());
		}
	}

	@Override
	protected void exitProgram() {
		if (!isGameOver) {
			int choice = JOptionPane.showConfirmDialog(null, "End current game?", "Quit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (choice != JOptionPane.YES_OPTION)
				return;
		}

		updateConfigFile();
		for (PlayerSocket playerSocket : playerSockets)
			playerSocket.close();

		this.dispose();
		System.exit(0);
	}

	@Override
	protected void displayScoreSheet() {
		(new ScoreSheetDialog(this, gm.getScoreKeeper(), gm.getPlayers(), isGameOver)).setVisible(true);
	}

	/**
	 * Notifies all player sockets of a specified action.
	 * 
	 * @param actionMessage
	 *            action message to send to all player sockets
	 */
	private void notifyPlayerSockets(String actionMessage) {
		for (PlayerSocket playerSocket : playerSockets)
			playerSocket.sendActionMessage(actionMessage);
	}

	/**
	 * Displays the round scores and the total scores and handles end of game if
	 * reached.
	 */
	private void endRound() {
		for (Player player : gm.getPlayers()) {
			if (gm.getScoreKeeper().getPlayerScore(player) >= GM.getEndGameScore()) {
				isGameOver = true;
				notifyPlayerSockets(new GameOverAction().createMessage());
				break;
			}
		}
		
		try { Thread.sleep(1000); } catch (InterruptedException e) {}

		ScoreKeeper scoreKeeper = gm.getScoreKeeper();
		int endedRound = scoreKeeper.getNumberOfRoundsFinished();
		notifyPlayerSockets(new AddRoundScoresAction().createMessage(scoreKeeper.getRoundScores(endedRound), true));

		if (isGameOver)
			playAreaPanel.setCards(new Card[0]);
		else {
			gm.beginRound();

			for (PlayerSocket playerSocket : playerSockets)
				playerSocket.sendActionMessage(new BeginRoundAction().createMessage(playerSocket.getPlayer().getCards()));

			backwardDirectionPanel.setVisible(false);
			forwardDirectionPanel.setVisible(true);
			playAreaPanel.showReadyButton(controlledPlayer);
			playAreaPanel.setCards(controlledPlayer.getCards());
		}

		displayScoreSheet();
	}

	@Override
	public void update(Observable observable, final Object obj) {
		if (observable instanceof GM) {
			// if round has ended
			if (obj instanceof ScoreKeeper)
				endRound();
			// if it's now the next player's turn
			else if (obj instanceof Player) {
				Player player = (Player) obj;
				notifyPlayerSockets(new ChangePlayerTurnAction().createMessage(player.getName()));
				playersDisplayPanel.setCurrentPlayer(player);
				if (player == controlledPlayer && controlledPlayer.isReadyToPlay()) {
					playAreaPanel.displayMessage("It's your turn");
					playTurnNotifyClip();
				}
			}
			// if direction reversed, change play direction indicator
			else if (obj instanceof Card) {
				notifyPlayerSockets(new ChangePlayDirectionAction().createMessage());
				if (((Card) obj).getDesign() == Card.Design.FORCE_FIELD) {
					backwardDirectionPanel.setVisible(!backwardDirectionPanel.isVisible());
					forwardDirectionPanel.setVisible(!forwardDirectionPanel.isVisible());
				}
			}
			// if the play piles have changed, display need to be updated
			else if (obj instanceof GameDeck) {				
				GameDeck deck = (GameDeck) obj;
				final Card leftCard = deck.getTopCard(PileLocation.LEFT);
				final Card.Color leftColor = deck.getColor(PileLocation.LEFT);
				final Card rightCard = deck.getTopCard(PileLocation.RIGHT);
				final Card.Color rightColor = deck.getColor(PileLocation.RIGHT);
				PileLocation livePile = gm.getLivePile();

				Player currentPlayer = gm.getCurrentTurnPlayer();
				boolean shouldDisplayCardPlay = gm.areAllPlayersReadyToBeginNewRound();
				for (PlayerSocket playerSocket : playerSockets) {
					boolean shouldParticipantDisplayCardPlay = shouldDisplayCardPlay && (playerSocket.getPlayer() != currentPlayer);
					playerSocket.sendActionMessage(new PlayedCardAction().createMessage(
							shouldParticipantDisplayCardPlay, leftCard, leftColor, rightCard, rightColor, livePile));
				}

				if (shouldDisplayCardPlay && currentPlayer != controlledPlayer)
					displayCardPlay(leftCard, leftColor, rightCard, rightColor, livePile);
				else
					updatePlayPileDisplay(leftCard, leftColor, rightCard, rightColor, livePile);
			}
			// if cards have been drawn
			else if (obj instanceof Integer) {
				Player currentPlayer = gm.getCurrentTurnPlayer();
				for (PlayerSocket playerSocket : playerSockets)
					if (playerSocket.getPlayer() != currentPlayer)
						playerSocket.sendActionMessage(new DrewCardsAction().createMessage((Integer) obj));

				if (currentPlayer != controlledPlayer)
					displayCardDraw((Integer) obj);
			}
			// if a clone has been played on the given pile
			else if (obj instanceof PileLocation) {
				notifyPlayerSockets(new PlayCloneAction().createMessage((PileLocation) obj));
				playAreaPanel.displayClone((PileLocation) obj);
			}
			// if battle has changed
			else if (obj instanceof Battle) {
				notifyPlayerSockets(new BattleAction().createMessage(((Battle) obj).getTotalCardsToDraw(), gm.getLivePile()));
				playAreaPanel.displayBattleCardCount(((Battle) obj).getTotalCardsToDraw(), gm.getLivePile());
			}
		}
		// if the player's cards have changed in any way
		if (observable instanceof Player) {
			Player player = (Player) observable;
			if (obj instanceof PlayerType) {
				notifyPlayerSockets(new ChangePlayerTypeAction().createMessage(player));
				return;
			}

			notifyPlayerSockets(new ChangePlayerCardsAction()
					.createMessage(player));
			if (obj instanceof Boolean) {
				for (PlayerSocket playerSocket : playerSockets) {
					if (playerSocket.getPlayer() != player)
						playerSocket.sendActionMessage(new PlayerReadyAction().createMessage(player, (Boolean) obj));
				}
			}
		}
	}

	@Override
	protected LinkedList<Player> getPlayers() {
		return gm.getPlayers();
	}

	@Override
	public boolean drawCards() {
		if (controlledPlayer == null)
			return false;

		playAreaPanel.displayMessage("");
		if (gm.drawCardsIfAllowed(controlledPlayer)) {
			playAreaPanel.setCards(controlledPlayer.getCards());
			if (controlledPlayer.countCards() == playAreaPanel.getHandCardMax())
				playAreaPanel.displayMessage("You've reached the maximum number of cards allowed and cannot draw any more.");
			else
				playAreaPanel.displayMessage(null);
			return true;
		}

		if (controlledPlayer.isReadyToPlay()) {
			if (gm.areAllPlayersReadyToBeginNewRound())
				playAreaPanel.displayMessage("It's not your turn.");
			else
				playAreaPanel.displayMessage("Not all players are ready to begin a new round.");
		}
		return false;
	}

	@Override
	public boolean playCard(Card card, PileLocation location) {
		if (location == null)
			return false;

		playAreaPanel.displayMessage("");
		if (card.getColor() == Card.Color.WILD) {
			ChooseColorDialog dialog = new ChooseColorDialog(this,
					"Choose A Color", card);
			String playError = gm.playCardIfAllowed(controlledPlayer, card, location, dialog.getColor());
			playAreaPanel.displayMessage(playError);
			return (playError == null);
		}

		String playError = gm.playCardIfAllowed(controlledPlayer, card,	location);
		playAreaPanel.displayMessage(playError);
		return (playError == null);
	}

	@Override
	public boolean playClone(Card card, PileLocation location) {
		if (location == null)
			return false;

		playAreaPanel.displayMessage("");
		String playError = gm.playCloneIfAllowed(controlledPlayer, card, location);
		playAreaPanel.displayMessage(playError);
		return (playError == null);
	}
}
