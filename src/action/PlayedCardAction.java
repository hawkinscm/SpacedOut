package action;

import model.Card;
import model.Card.Color;
import model.GameDeck.PileLocation;
import gui.ParticipantGUI;

public class PlayedCardAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public PlayedCardAction() {}
		
	/**
	 * /**
	 * Generates and returns a Played Card Action message using the given data.
	 * @param shouldDisplayCardPlay true if the card play should be displayed; false otherwise
	 * @param leftCard card on the left play pile
	 * @param leftColor color of the card on the left play pile
	 * @param rightCard card on the right play pile
	 * @param rightColor color of the card on the right play pile
	 * @param livePile the live pile, specified by location
	 * @return a Played Card Action message generated from the given data
	 */
	public String createMessage(boolean shouldDisplayCardPlay, Card leftCard, Color leftColor, Card rightCard, Color rightColor, PileLocation livePile) {
		String message = this.getClass().getName();
		message += MAIN_DELIM + shouldDisplayCardPlay;
		message += generateMessage(leftCard);
		message += MAIN_DELIM + leftColor.toString();
		message += generateMessage(rightCard);
		message += MAIN_DELIM + rightColor.toString();
		if (livePile != null)
			message += MAIN_DELIM + livePile.toString();
		
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		String[] actionData = getMessageWithoutClassHeader().split(MAIN_DELIM);
		boolean shouldDisplayCardPlay = Boolean.parseBoolean(actionData[0]);
		Card leftCard = parseCard(actionData[1]);
		Color leftColor = Color.valueOf(actionData[2]);
		Card rightCard = parseCard(actionData[3]);
		Color rightColor = Color.valueOf(actionData[4]);
		PileLocation livePile = null;
		if (actionData.length > 5)
			livePile = PileLocation.valueOf(actionData[5]);
		if (shouldDisplayCardPlay)
			gui.displayCardPlay(leftCard, leftColor, rightCard, rightColor, livePile);
		else
			gui.updatePlayPileDisplay(leftCard, leftColor, rightCard, rightColor, livePile);
		return null;
	}
}
