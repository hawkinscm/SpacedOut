package ai;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import model.Card;
import model.Card.Color;
import model.GM;
import model.GameDeck.PileLocation;
import model.Player;

/**
 * Represents a play that a player can make.
 */
public class PlayAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private enum ActionType {
		DRAW,
		PLAY,
		CLONE;
	}
	
	private GM gm;
	private Player player;
	private Card card;
	private PileLocation pile;
	private Color color;
	private ActionType type;
	
	/**
	 * Private constructor called from the static helper methods
	 * @param gm GM needed to perform actions
	 * @param player player to create an action for
	 */
	private PlayAction(GM gm, Player player) {
		this.gm = gm;
		this.player = player;
		this.card = null;
		this.pile = null;
		this.color = null;
		this.type = ActionType.DRAW;
	}
	
	/**
	 * Private constructor called from the static helper methods
	 * @param gm GM needed to perform actions
	 * @param player player to create an action for
	 * @param card card to create an action for
	 * @param pile play pile specified by location to perform an action on
	 * @param type action type to perform
	 */
	private PlayAction(GM gm, Player player, Card card, PileLocation pile, ActionType type) {
		this.gm = gm;
		this.player = player;
		this.card = card;
		this.pile = pile;
		this.color = card.getColor();
		this.type = type;
	}
	
	/**
	 * Creates a draw card(s) action for the given player.
	 * @param gm GM needed to perform actions
	 * @param player player to create a draw card(s) action for
	 * @return the created draw card(s) action
	 */
	public static PlayAction createDrawAction(GM gm, Player player) {
		PlayAction action = new PlayAction(gm, player);
		
		return action;
	}
	
	/**
	 * Creates a play card action for the given player.
	 * @param gm GM needed to perform actions
	 * @param player player to create a play card action for
	 * @param card card to create play card action for
	 * @param pile play pile specified by location to perform play card action on
	 * @return the created play card action
	 */
	public static PlayAction createPlayAction(GM gm, Player player, Card card, PileLocation pile) {
		PlayAction action = new PlayAction(gm, player, card, pile, ActionType.PLAY);
		
		return action;
	}
	
	/**
	 * Creates a play clone action for the given player.
	 * @param gm GM needed to perform actions
	 * @param player player to create a play clone action for
	 * @param card card to create play clone action for
	 * @param pile play pile specified by location to perform play clone action on
	 * @return the created play clone action
	 */
	public static PlayAction createCloneAction(GM gm, Player player, Card card, PileLocation pile) {
		PlayAction action = new PlayAction(gm, player, card, pile, ActionType.CLONE);
		
		return action;
	}
	
	/**
	 * Returns the card that is part of this action; null if no specific card is involved.
	 * @return the card that is part of this action; null if no specific card is involved
	 */
	public Card getCard() {
		return card;
	}
	
	/**
	 * Return the play pile where this action will be performed.
	 * @return the play pile, specified by location, where this action will be performed
	 */
	public PileLocation getPlayActionPile() {
		return pile;
	}
	
	/**
	 * Sets the color of the card to play (needed for wild card plays)
	 * @param color color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * Performs this action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(type) {
			case DRAW :
				gm.drawCardsIfAllowed(player);
				break;
			case PLAY :
				gm.playCardIfAllowed(player, card, pile, color);
				break;
			case CLONE :
				gm.playCloneIfAllowed(player, card, pile);
				break;
		}
	}
	
	@Override
	public String toString() {
		switch(type) {
			case DRAW :
				return (type.toString() + ":" + player.getName());
			default :
				return (type.toString() + ":" + player.getName() + ":" + card.toString() + ":" + pile.toString() + ":" + color.toString());
		}
	}
}
