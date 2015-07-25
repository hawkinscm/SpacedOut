package action;

import model.Player;

import gui.ParticipantGUI;

public class ChangePlayerCardsAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public ChangePlayerCardsAction() {}
	
	/**
	 * Generates and returns a Change Player Cards Action message using the given data.
	 * @param player the player whose cards have changed that will be written as data in the message
	 * @return a Change Player Cards Action message generated from the given data
	 */
	public String createMessage(Player player) {
		String message = this.getClass().getName();
		message += MAIN_DELIM + player.getName();
		message += MAIN_DELIM + player.countCards();
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
		String playerName = actionData[0];
		String cardCount = actionData[1];
		gui.setPlayerCardCount(playerName, Integer.parseInt(cardCount));
		return null;
	}
}
