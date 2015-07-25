package action;

import gui.ParticipantGUI;

public class ChangePlayerTurnAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public ChangePlayerTurnAction() {}
	
	/**
	 * Generates and returns a Change Player Turn Action message using the given data.
	 * @param player the name of the player whose turn it now is that will be written as data in the message
	 * @return a Change Player Turn Action message generated from the given data
	 */
	public String createMessage(String playerName) {
		String message = this.getClass().getName();
		message += MAIN_DELIM + playerName;
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		gui.setCurrentPlayer(getMessageWithoutClassHeader());
		return null;
	}
}
