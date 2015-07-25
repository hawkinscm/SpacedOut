package action;

import model.Card;

import gui.ParticipantGUI;

public class BeginRoundAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public BeginRoundAction() {}
	
	/**
	 * Generates and returns a Begin Round Action message using the given data.
	 * @param playerCards the notified player's cards that will be written as data in the message
	 * @return an Begin Round Action message generated from the given data
	 */
	public String createMessage(Card[] playerCards) {
		String message = this.getClass().getName();
		for (Card card : playerCards)
			message += generateMessage(card);
		
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		String[] cardTexts = getMessageWithoutClassHeader().split(MAIN_DELIM);
		Card[] cards = new Card[cardTexts.length];
		for (int cardIndex = 0; cardIndex < cards.length; cardIndex++) {
			cards[cardIndex] = parseCard(cardTexts[cardIndex]);
		}
		gui.beginRound(cards);
		return null;
	}
}
