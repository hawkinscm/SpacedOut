package action;

import model.Card;
import model.Card.Color;
import model.GameDeck.PileLocation;

public class RequestPlayAction extends Action<ActionPlayerGM> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public RequestPlayAction() {}
	
	/**
	 * Generates and returns a Request Play Action message using the given data.
	 * @param card the card to try to play that will be written as data in the message
	 * @param pile the pile, specified by location, to try to play the card that will be written as data in the message
	 * @return a Request Play Action message generated from the given data
	 */
	public String createMessage(Card card, PileLocation pile) {
		String message = this.getClass().getName();
		message += generateMessage(card);
		message += MAIN_DELIM + pile.toString();
		setMessage(message);
		return getMessage();
	}
	
	/**
	 * Generates and returns a Request Play Action message using the given data.
	 * @param card the card to try to play that will be written as data in the message
	 * @param pile the pile, specified by location, to try to play the card that will be written as data in the message
	 * @param color the chosen color of the wild card to try to play that will be written as data in the message
	 * @return a Request Play Action message generated from the given data
	 */
	public String createMessage(Card card, PileLocation pile, Color color) {
		String message = this.getClass().getName();
		message += generateMessage(card);
		message += MAIN_DELIM + pile.toString();
		if (color != null)
			message += MAIN_DELIM + color.toString();
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<ActionPlayerGM> getActionTypeClass() {
		return ActionPlayerGM.class;
	}
	
	@Override
	public String[] performAction(ActionPlayerGM actionPlayerGM) {
		String[] actionData = getMessageWithoutClassHeader().split(MAIN_DELIM);
		Card card = parseCard(actionData[0]);
		for (Card playerCard : actionPlayerGM.player.getCards()) {
			if (card.toString().equals(playerCard.toString())) {
				card = playerCard;
				break;
			}
		}		
		PileLocation pile = PileLocation.valueOf(actionData[1]);
		
		String illegalPlayMessage;
		if (actionData.length < 3)
			illegalPlayMessage = actionPlayerGM.gm.playCardIfAllowed(actionPlayerGM.player, card, pile);
		else
			illegalPlayMessage = actionPlayerGM.gm.playCardIfAllowed(actionPlayerGM.player, card, pile, Color.valueOf(actionData[2]));
		if (illegalPlayMessage != null) {
			String[] replyMessages = new String[2];
			replyMessages[0] = new SetPlayerCardsAction().createMessage(actionPlayerGM.player.getCards());
			replyMessages[1] = new DisplayPlayMessageAction().createMessage(illegalPlayMessage);
			return replyMessages;
		}
		return null;
	}
}
