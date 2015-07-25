package ai;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Timer;

import model.Card;
import model.GM;
import model.Player;
import model.PlayerType;
import model.Card.Color;
import model.GameDeck.PileLocation;

/**
 * Manager for AI logic and actions.
 */
public class AIManager implements Observer {

	private GM gm;
	private Map<Player, Timer> playerTimers;
	
	private boolean readyToPlay;
	
	/**
	 * Creates a new AIManager
	 * @param gm the GM needed by the AIManager to determine game state and perform actions
	 */
	public AIManager(GM gm) {
		this.gm = gm;
		gm.addObserver(this);
		playerTimers = new HashMap<Player, Timer>();
		readyToPlay = false;		           
	}
	
	@Override
	public void update(Observable observable, Object obj) {
		if (observable instanceof Player) {
			if (obj instanceof Boolean) {
				// if not all players are ready to play
				if (((Boolean)obj) == false) {
					readyToPlay = false;
					for (Timer timer : playerTimers.values())
						timer.stop();
				}
				// else a player has signaled that they are ready to play
				else if (gm.areAllPlayersReadyToBeginNewRound()) {
					readyToPlay = true;
					for (Player player : gm.getPlayers())
						makePlayDecision(player);
				}
			}
		}
		if (observable instanceof GM) {
			// if it's the next players turn
			if (obj instanceof Player && readyToPlay) {
				for (Timer timer : playerTimers.values())
					timer.stop();
				for (Player player : gm.getPlayers())
					makePlayDecision(player);
			}
		}
	}
	
	/**
	 * Adds a play action for the given player that will be performed after a random amount of time has passed.
	 * Random times are weighted based on the AI level.
	 * @param player AI Computer Player who will play the action if triggered
	 * @param action action to play if triggered by the time limit
	 */
	private void addTimedAction(Player player, PlayAction action) {
		int time = 1000;
		if (player.getPlayerType() == PlayerType.COMPUTER_EASY)
			time += Randomizer.getRandom(2000) + 2000;
		else if (player.getPlayerType() == PlayerType.COMPUTER_MEDIUM) {
			time += Randomizer.getRandom(2000) + 1000;
		}
		else if (player.getPlayerType() == PlayerType.COMPUTER_HARD) {
			time += Randomizer.getRandom(2000);
		}
		if (!gm.isPlayAllowedOutOfTurn())
			time = 1000;
			
		Timer timer = new Timer(time, action);
		timer.setActionCommand(action.toString());
		timer.setRepeats(false);
		if (playerTimers.containsKey(player) && playerTimers.get(player).isRunning())
			playerTimers.get(player).stop();
		playerTimers.put(player, new Timer(time, action));
		if (readyToPlay)
			timer.start();
	}
	
	/**
	 * Returns a list of all possible play actions for the given card by the given player.
	 * @param player player to evaluate
	 * @param card card to evaluate
	 * @return the list of all possible play actions for the given card by the given player
	 */
	private List<PlayAction> getPlayActionsForCard(Player player, Card card) {
		List<PlayAction> playActions = new LinkedList<PlayAction>();
		StringBuilder ignoreString = new StringBuilder();
		if (gm.canPlayCard(player, card, PileLocation.LEFT, ignoreString))
			playActions.add(PlayAction.createPlayAction(gm, player, card, PileLocation.LEFT));
		if (gm.canPlayCard(player, card, PileLocation.RIGHT, ignoreString))
			playActions.add(PlayAction.createPlayAction(gm, player, card, PileLocation.RIGHT));
		if (gm.canPlayClone(player, card, PileLocation.LEFT, ignoreString))
			playActions.add(PlayAction.createCloneAction(gm, player, card, PileLocation.LEFT));
		if (gm.canPlayClone(player, card, PileLocation.RIGHT, ignoreString))
			playActions.add(PlayAction.createCloneAction(gm, player, card, PileLocation.RIGHT));
		return playActions;
	}
	
	/**
	 * Returns a list of all play actions for the given player sorted from highest point cards to lowest point cards.
	 * @param player player to evaluate
	 * @return a descending card point sorted list of all play actions for the given player
	 */
	private List<PlayAction> getDescendingPlayActionsForPlayer(Player player) {
		List<PlayAction> playActions = new LinkedList<PlayAction>();
		for (Card card : player.getCards()) {
			if (playActions.isEmpty())
				playActions.addAll(getPlayActionsForCard(player, card));
			else {
				int numActions = playActions.size();
				for (int actionIndex = 0; actionIndex < numActions; actionIndex++) {
					if (card.getPointValue() > playActions.get(actionIndex).getCard().getPointValue()) {
						playActions.addAll(actionIndex, getPlayActionsForCard(player, card));
						break;
					}
				}
			}
		}
		return playActions;
	}
	
	/**
	 * Returns the lowest card count of any player but the given player.
	 * @param player the player not to check
	 * @return the lowest card count of any player but the given player
	 */
	private int getOtherPlayersLowestCardCount(Player player) {
		int lowestCardCount = 100;
		for (Player otherPlayer : gm.getPlayers()) {
			if (otherPlayer == player)
				continue;
			
			int cardCount = otherPlayer.countCards();
			if (cardCount < lowestCardCount)
				lowestCardCount = cardCount;
		}
		return lowestCardCount;
	}
	
	/**
	 * Returns the value to the player of changing the given play pile to the given color.
	 * @param player player to evaluate
	 * @param playPile play pile, specified by location, where the card would be played
	 * @param newLiveColor the color that the pile would be changing to
	 * @return
	 */
	private int getChangeColorValue(Player player, PileLocation playPile, Color newLiveColor) {
		PileLocation nonPlayPile = (playPile == PileLocation.LEFT) ? PileLocation.RIGHT : PileLocation.LEFT;
		Color oldLiveColor = gm.peekTopCard(nonPlayPile).getColor();
		if (gm.getLivePile() != null)
			oldLiveColor = gm.peekTopCard(gm.getLivePile()).getColor();
		if (newLiveColor == oldLiveColor && gm.getLivePile() == playPile)
			return 0;
		
		boolean hasBlueAndRedBigBang = false;
		boolean hasGreenAndYellowBigBang = false;
		boolean hasOldColorCards = false;
		int colorChangeValue = 0;
		for (Card card : player.getCards()) {
			if (card.getDesign() == Card.Design.BIG_BANG) {
				if (card.getColor() == Card.Color.BLUE_AND_RED)
					hasBlueAndRedBigBang = true;
				else if (card.getColor() == Card.Color.GREEN_AND_YELLOW)
					hasGreenAndYellowBigBang = true;
			}
			else if (card.getColor() == oldLiveColor) {
				colorChangeValue -= (card.getPointValue() + 2) / 3;
				hasOldColorCards = true;
			}
			else if (card.getColor() == newLiveColor)
				colorChangeValue += (card.getPointValue() + 2) / 3;
		}
		if (hasOldColorCards)
			colorChangeValue -= 10;
		
		Color oldColor = gm.peekTopCard(playPile).getColor();
		Color nonPlayPileColor = gm.peekTopCard(nonPlayPile).getColor();
		int bigBangColorGain = 0;
		if (hasBlueAndRedBigBang && oldColor != newLiveColor) {
			if (oldColor == Card.Color.BLUE && nonPlayPileColor != Card.Color.BLUE)
				bigBangColorGain -= 20;
			else if (oldColor == Card.Color.RED && nonPlayPileColor != Card.Color.RED)
				bigBangColorGain -= 20;
			
			if (newLiveColor == Card.Color.BLUE && nonPlayPileColor != Card.Color.BLUE) {
				bigBangColorGain += 20;
				if (nonPlayPileColor == Card.Color.RED)
					return 50;
			}
			else if (newLiveColor == Card.Color.RED && nonPlayPileColor != Card.Color.RED) {
				bigBangColorGain += 20;
				if (nonPlayPileColor == Card.Color.BLUE)
					return 50;
			}
		}
		if (hasGreenAndYellowBigBang && oldColor != newLiveColor) {
			if (oldColor == Card.Color.GREEN && nonPlayPileColor != Card.Color.GREEN)
				bigBangColorGain -= 20;
			else if (oldColor == Card.Color.YELLOW && nonPlayPileColor != Card.Color.YELLOW)
				bigBangColorGain -= 20;
			
			if (newLiveColor == Card.Color.GREEN && nonPlayPileColor != Card.Color.GREEN) {
				bigBangColorGain += 20;
				if (nonPlayPileColor == Card.Color.YELLOW)
					return 50;
			}
			else if (newLiveColor == Card.Color.YELLOW && nonPlayPileColor != Card.Color.YELLOW) {
				bigBangColorGain += 20;
				if (nonPlayPileColor == Card.Color.GREEN)
					return 50;
			}
		}
		
		return colorChangeValue + bigBangColorGain;
	}
	
	/**
	 * Returns the player who would be attacked by the attacking player in the current game state.
	 * @param attackingPlayer player who would attack
	 * @param numToSkip number of players that the attack skips
	 * @return the player who would be attacked by the attacking player in the current game state
	 */
	private Player getPlayerOfAttack(Player attackingPlayer, int numToSkip) {
		int attackIndexMove = (1 + numToSkip) * ((gm.isDirectionForward()) ? 1 : -1);
		List<Player> players = gm.getPlayers();
		int playerIndex = players.indexOf(attackingPlayer);
		int attackIndex = playerIndex + attackIndexMove;
		if (attackIndex < 0)
			attackIndex += players.size();
		else if (attackIndex >= players.size())
			attackIndex -= players.size();
		return players.get(attackIndex);
	}
	
	/**
	 * Returns the estimated value of the given player performing the given play action.
	 * Note: this will also set the color of wild card actions to the most valued color in regards to the player's hand. 
	 * @param player player to evaluate
	 * @param action play action to evaluate
	 * @return an estimated point value representing the worth to the player in performing the given action
	 */
	private int getPlayActionValue(Player player, PlayAction action) {
		Card actionCard = action.getCard();
		if (actionCard.getDesign() == Card.Design.BIG_BANG)
			return actionCard.getPointValue() * 3;
		
		boolean hasWild = false;
		boolean hasClone = false;
		for (Card card : player.getCards()) {
			if (card.getColor() == Card.Color.WILD)
				hasWild = true;
			else if (card.isClone(actionCard))
				hasClone = true;
		}
		
		int actionValue = actionCard.getPointValue();
		if (actionCard.getColor() == Card.Color.WILD) {
			actionValue /= getOtherPlayersLowestCardCount(player);
			
			int blueValue = getChangeColorValue(player, action.getPlayActionPile(), Color.BLUE);
			int redValue = getChangeColorValue(player, action.getPlayActionPile(), Color.RED);
			int greenValue = getChangeColorValue(player, action.getPlayActionPile(), Color.GREEN);
			int yellowValue = getChangeColorValue(player, action.getPlayActionPile(), Color.YELLOW);
			Color bestColor = Color.BLUE;
			int bestColorValue = blueValue;
			if (redValue > bestColorValue) {
				bestColor = Color.RED;
				bestColorValue = redValue;
			}
			if (greenValue > bestColorValue) {
				bestColor = Color.GREEN;
				bestColorValue = greenValue;
			}
			if (yellowValue > bestColorValue) {
				bestColor = Color.YELLOW;
				bestColorValue = yellowValue;
			}
			action.setColor(bestColor);
			actionValue += bestColorValue;
		}
		else 
			actionValue += getChangeColorValue(player, action.getPlayActionPile(), actionCard.getColor());
		
		if (actionCard.getDesign() == Card.Design.ASTEROIDS) {
			Player playerOfAttack = getPlayerOfAttack(player, 0);
			actionValue += 25 / playerOfAttack.countCards();
		}
		else if (actionCard.getDesign() == Card.Design.SHOOTING_STAR) {
			Player playerOfAttack = getPlayerOfAttack(player, 0);
			actionValue += 25 / playerOfAttack.countCards();
		}
		else if (actionCard.getDesign() == Card.Design.BLACK_HOLE) {
			boolean hasPlayableCard = false;
			for (Card card : player.getCards()) {
				if (card == actionCard)
					continue;
				
				if (hasWild || card.getDesign() == Card.Design.BLACK_HOLE || card.getColor() == actionCard.getColor()) {
					hasPlayableCard = true;
					break;
				}					
			}
			if (!hasPlayableCard)
				actionValue -= 13;
		}
		else if (hasClone)
			actionValue += actionCard.getPointValue();
		
		return actionValue;
	}
	
	/**
	 * Decides the best possible play to make for the given current player and triggers that play to happen after a randomized amount of time.
	 * @param player player whose turn it is
	 */
	private void makePlayDecision(Player player) {
		if (!player.isComputer())
			return;
		
		List<PlayAction> playActions = getDescendingPlayActionsForPlayer(player);
		if (playActions.isEmpty()) {
			if (gm.getCurrentTurnPlayer() == player)
				addTimedAction(player, PlayAction.createDrawAction(gm, player));
			return;
		}
		
		//Easy - play random playable card
		if (player.getPlayerType() == PlayerType.COMPUTER_EASY) {
			// 2/3 of the time easy computers will miss clone/big bang opportunities
			if (gm.getCurrentTurnPlayer() != player && Randomizer.getRandom(3) != 1)
				return;
			
			PlayAction action = playActions.get(Randomizer.getRandom(playActions.size()));
			
			if (action.getCard().getColor() == Color.WILD) {
				Color[] standardColors = Color.getStandardColors();
				action.setColor(standardColors[Randomizer.getRandom(standardColors.length)]);
			}
			addTimedAction(player, action);
		}
		//Medium - play highest point playable card
		else if (player.getPlayerType() == PlayerType.COMPUTER_MEDIUM) {
			// 1/3 of the time medium computers will miss clone/big bang opportunities
			if (gm.getCurrentTurnPlayer() != player && Randomizer.getRandom(3) == 1)
				return;
			
			PlayAction bestPlayAction = playActions.get(0);
			final int highestPointValue = bestPlayAction.getCard().getPointValue();
			int bestPlayValue = getPlayActionValue(player, bestPlayAction);
			
			for (PlayAction action : playActions) {
				if (action.getCard().getPointValue() < highestPointValue)
					break;
				
				int actionPlayValue = getPlayActionValue(player, action);
				if (actionPlayValue > bestPlayValue) {
					bestPlayAction = action;
					bestPlayValue = actionPlayValue;
				}
				else if (actionPlayValue == bestPlayValue) {
					PileLocation playPile = action.getPlayActionPile();
					PileLocation nonPlayPile = (playPile == PileLocation.LEFT) ? PileLocation.RIGHT : PileLocation.LEFT;
					if (gm.peekTopCard(nonPlayPile).getPointValue() < gm.peekTopCard(playPile).getPointValue()) {
						bestPlayAction = action;
						bestPlayValue = actionPlayValue;
					}
				}
			}			

			if (player.countCards() == 1) {
				PileLocation nonPlayPile = (bestPlayAction.getPlayActionPile() == PileLocation.LEFT) ? PileLocation.RIGHT : PileLocation.LEFT;
				int endScore = bestPlayAction.getCard().getPointValue() + gm.peekTopCard(nonPlayPile).getPointValue();
				if (endScore > 35) {
					if (gm.getCurrentTurnPlayer() == player)
						addTimedAction(player, PlayAction.createDrawAction(gm, player));
					return;
				}
			}
			
			addTimedAction(player, bestPlayAction);
		}
		//Hard - determine best card to play and play it
		else if (player.getPlayerType() == PlayerType.COMPUTER_HARD) {
			PlayAction bestPlayAction = playActions.get(0);
			int bestPlayValue = getPlayActionValue(player, bestPlayAction);
			
			for (PlayAction action : playActions) {
				int actionPlayValue = getPlayActionValue(player, action);
				if (actionPlayValue > bestPlayValue) {
					bestPlayAction = action;
					bestPlayValue = actionPlayValue;
				}
				else if (actionPlayValue == bestPlayValue) {
					PileLocation playPile = action.getPlayActionPile();
					PileLocation nonPlayPile = (playPile == PileLocation.LEFT) ? PileLocation.RIGHT : PileLocation.LEFT;
					if (gm.peekTopCard(nonPlayPile).getPointValue() < gm.peekTopCard(playPile).getPointValue()) {
						bestPlayAction = action;
						bestPlayValue = actionPlayValue;
					}
				}
			}

			if (player.countCards() == 1) {
				PileLocation nonPlayPile = (bestPlayAction.getPlayActionPile() == PileLocation.LEFT) ? PileLocation.RIGHT : PileLocation.LEFT;
				int endScore = bestPlayAction.getCard().getPointValue() + gm.peekTopCard(nonPlayPile).getPointValue();
				if (endScore >= 20) {
					if (gm.getCurrentTurnPlayer() == player)
						addTimedAction(player, PlayAction.createDrawAction(gm, player));
					return;
				}
			}
			
			if (bestPlayValue >= 0)
				addTimedAction(player, bestPlayAction);
			else if (gm.getCurrentTurnPlayer() == player)
				addTimedAction(player, PlayAction.createDrawAction(gm, player));
		}
	}
	
	
}
