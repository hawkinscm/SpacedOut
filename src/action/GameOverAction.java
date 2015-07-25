package action;

import gui.ParticipantGUI;

public class GameOverAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public GameOverAction() {}
	
	/**
	 * Generates and returns a Game Over Action message.
	 * @return a Game Over Action message
	 */
	public String createMessage() {
		setMessage(this.getClass().getName() + MAIN_DELIM);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		gui.endGame();
		return null;
	}
}
