package action;

import gui.ParticipantGUI;

public class ChangeOptionsAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public ChangeOptionsAction() {}
	
	/**
	 * Generates and returns a Change Options Action message using the given data.
	 * @param endGameScore the set score that will end the game when reached or passed that will be written as data in the message
	 * @param numDealCards the set number of cards that will be dealt at the beginning of each round that will be written as data in the message
	 * @return a Change Options Action message generated from the given data
	 */
	public String createMessage(int endGameScore, int numDealCards) {
		String message = this.getClass().getName();
		message += MAIN_DELIM + endGameScore;
		message += MAIN_DELIM + numDealCards;
		
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
		int endGameScore = Integer.parseInt(actionData[0]);
		int numDealCards = Integer.parseInt(actionData[1]);
		gui.setOptions(endGameScore, numDealCards);
		return null;
	}
}
