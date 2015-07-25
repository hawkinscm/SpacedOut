package model;

import java.util.LinkedList;

import model.Card.Color;

/**
 * Represents a Spaced Out Battle
 */
public class Battle {

	/**
	 * List of players who can be involved in the battle.
	 */
	private LinkedList<Player> battlePlayers;
	
	/**
	 * Whether or not the battle turn direction is forwards.
	 */
	private boolean isBattleDirectionForward;
	
	/**
	 * Whether or not to skip a player when determining the next person attacked.
	 */
	private boolean skipPlayer;
	
	/**
	 * The last player who played a card in the battle.
	 */
	private Player lastPlayer;
	
	/**
	 * The last card that was played in the battle. 
	 */
	private Card lastBattleCard;
	
	/**
	 * The color of last battle card; needed for wild cards.
	 */
	private Color lastBattleColor;
	
	private boolean isBigBangBattle;
	
	private int totalCardsToDraw;
	
	/**
	 * Creates a new battle with the list of players who may be involved.
	 * @param players
	 */
	public Battle(LinkedList<Player> players, boolean isDirectionForward) {
		battlePlayers = new LinkedList<Player>();
		battlePlayers.addAll(players);
		isBattleDirectionForward = isDirectionForward;
		skipPlayer = false;
		lastPlayer = null;
		lastBattleCard = null;
		
		isBigBangBattle = false;
		totalCardsToDraw = 0;
	}
	
	/**
	 * Removes a player from any further involvement in the battle.
	 * @param player player to remove
	 */
	public void removePlayer(Player player) {
		battlePlayers.remove(player);
	}
	
	/**
	 * Returns whether or not the given card can be played in the battle at its current state.
	 * @param card card to evaluate
	 * @param illegalPlayReason will be populated with the reason the card can't be played; or null if valid play
	 * @return true if the card can be played; false otherwise
	 */
	public boolean canPlayCard(Card card, StringBuilder illegalPlayReason) {
		illegalPlayReason.setLength(0);
		
		if (lastBattleCard == null) {
			if (card.getDesign() != Card.Design.BIG_BANG && card.getDesign() != Card.Design.ASTEROIDS && 
			    card.getDesign() != Card.Design.SHOOTING_STAR) {
				illegalPlayReason.append("Invalid battle starting card: " + card);
			}				
		}
		else if (isBigBangBattle) {
			if (card.getDesign() != Card.Design.FORCE_FIELD || card.getColor() != Color.WILD)
				illegalPlayReason.append("Can only play SUPER FORCE FIELD on BIG BANG attack.");
		}
		else if (card.getDesign() == Card.Design.ASTEROIDS || card.getDesign() == Card.Design.SHOOTING_STAR) {
			if (card.getDesign() != lastBattleCard.getDesign()) {
				if (lastBattleCard.getDesign() == Card.Design.FORCE_FIELD)
					illegalPlayReason.append("Can only play FORCE FIELD on FORCE FIELD defense.");
				else {
					String design = lastBattleCard.getDesign().toString().replace('_', ' ');
					illegalPlayReason.append("Can only play " + design + " or " + lastBattleColor + 
							" FORCE FIELD on " + lastBattleColor + " " + design + " attack.");
				}
			}
		}
		else if (card.getDesign() == Card.Design.FORCE_FIELD) {
			if (lastBattleCard.getDesign() != Card.Design.FORCE_FIELD) {
				if (card.getColor() != Color.WILD && card.getColor() != lastBattleColor) {
					String design = lastBattleCard.getDesign().toString().replace('_', ' ');
					illegalPlayReason.append("Can only play " + design + " or " + lastBattleColor + 
							" FORCE FIELD on " + lastBattleColor + " " + design + " attack.");
				}
			}
		}
		else if (lastBattleCard.getDesign() == Card.Design.FORCE_FIELD)
			illegalPlayReason.append("Can only play FORCE FIELD on FORCE FIELD defense.");
		else {
			String design = lastBattleCard.getDesign().toString().replace('_', ' ');
			illegalPlayReason.append("Can only play " + design + " or " + lastBattleColor + 
					" FORCE FIELD on " + lastBattleColor + " " + design + " attack.");
		}
		
		return (illegalPlayReason.length() == 0);
	}
	
	/**
	 * Attempts to play the given card against the last battle card.
	 * @param player the player playing the card
	 * @param card the card to play
	 * @param cardColor the color of this card; needed for wild cards
	 * @return null if the card was played; otherwise return the reason why the card can't be played
	 */
	public String playCardIfAllowed(Player player, Card card, Color cardColor) {
		if (cardColor == Color.WILD)
			return "";
		
		StringBuilder illegalPlayReason = new StringBuilder();
		if (!canPlayCard(card, illegalPlayReason)) {
			return illegalPlayReason.toString();
		}
		
		playCard(player, card, cardColor);
		return null;
	}
	
	/**
	 * Plays the given card regardless of the rules.
	 * @param player the player playing the card
	 * @param card the card to play
	 * @param cardColor the color of this card; needed for wild cards
	 */
	public void playCard(Player player, Card card, Color cardColor) {
		// begin the battle if this is the first card played
		if (lastBattleCard == null) {
			if (card.getDesign() == Card.Design.BIG_BANG) {
				isBigBangBattle = true;
				totalCardsToDraw = 3;
			}
			else if (card.getDesign() == Card.Design.ASTEROIDS)
				totalCardsToDraw = 2;
			else if (card.getDesign() == Card.Design.SHOOTING_STAR) {
				totalCardsToDraw = 2;
				skipPlayer = true;
			}			
		}
		else if (isBigBangBattle) // Super Force Field was played
			isBattleDirectionForward = !isBattleDirectionForward;
		else if (card.getDesign() == Card.Design.ASTEROIDS || card.getDesign() == Card.Design.SHOOTING_STAR)
			totalCardsToDraw += 2;
		else if (card.getDesign() == Card.Design.FORCE_FIELD)
			isBattleDirectionForward = !isBattleDirectionForward;
				
		lastPlayer = player;
		lastBattleCard = card;
		lastBattleColor = cardColor;
	}
	
	/**
	 * Returns the next player who needs to defend or counter-attack in the battle.
	 * @return the next player who needs to defend or counter-attack; null, if battle players is empty
	 */
	public Player getNextAttackedPlayer() {
		if (battlePlayers.isEmpty() || lastPlayer == null)
			return null;
		
		int playerIndex = battlePlayers.indexOf(lastPlayer);
		int count = 1;
		if (skipPlayer)
			count = 2;
		
		if (isBattleDirectionForward)
			playerIndex += count;
		else 
			playerIndex -= count;
		
		if (playerIndex < 0)
			playerIndex += battlePlayers.size();
		else if (playerIndex >= battlePlayers.size())
			playerIndex -= battlePlayers.size();
		
		return battlePlayers.get(playerIndex);
	}
	
	/**
	 * Return the number of players still involved in the battle.
	 * @return the number of players still involved in the battle
	 */
	public int getPlayerCount() {
		return battlePlayers.size();
	}
	
	/**
	 * Returns whether or not this is a Big Bang battle
	 * @return true if this is a Big Bang battle; false, otherwise
	 */
	public boolean isBigBangBattle() {
		return isBigBangBattle;
	}
	
	/**
	 * Returns the total number of cards an attacked player needs to draw if he doesn't defend or counter-attack.
	 * @return the total number of cards an attacked player needs to draw
	 */
	public int getTotalCardsToDraw() {
		return totalCardsToDraw;
	}
	
}
