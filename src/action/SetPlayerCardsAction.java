package action;

import model.Card;
import gui.ParticipantGUI;

public class SetPlayerCardsAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public SetPlayerCardsAction() {}
	
	/**
	 * Generates and returns a Set Player Cards Action message using the given data.
	 * @param cards cards to set for the controlled player that will be written as data in the message
	 * @return a Set Player Cards Action message generated from the given data
	 */
	public String createMessage(Card[] cards) {
		String message = this.getClass().getName();
		for (Card card : cards)
			message += this.generateMessage(card);
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		String[] cardData = getMessageWithoutClassHeader().split(MAIN_DELIM);
		Card[] cards = new Card[cardData.length];
		for (int cardIndex = 0; cardIndex < cards.length; cardIndex++)
			cards[cardIndex] = parseCard(cardData[cardIndex]);
		gui.setControlledPlayerCards(cards);
		return null;
	}
}
