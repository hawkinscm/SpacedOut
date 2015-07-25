package action;

import model.Card;
import model.GameDeck.PileLocation;

public class RequestCloneAction extends Action<ActionPlayerGM> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public RequestCloneAction() {}
	
	/**
	 * Generates and returns a Request Clone Action message using the given data.
	 * @param card the card to try to clone that will be written as data in the message
	 * @param pile the pile, specified by location, to try to play the clone that will be written as data in the message
	 * @return a Request Clone Action message generated from the given data
	 */
	public String createMessage(Card card, PileLocation pile) {
		String message = this.getClass().getName();
		message += generateMessage(card);
		message += MAIN_DELIM + pile.toString();
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
		
		String illegalPlayMessage = actionPlayerGM.gm.playCloneIfAllowed(actionPlayerGM.player, card, pile);
		if (illegalPlayMessage != null) {
			String[] replyMessages = new String[2];
			replyMessages[0] = new SetPlayerCardsAction().createMessage(actionPlayerGM.player.getCards());
			replyMessages[1] = new DisplayPlayMessageAction().createMessage(illegalPlayMessage);
			return replyMessages;
		}
		return null;
	}
}
