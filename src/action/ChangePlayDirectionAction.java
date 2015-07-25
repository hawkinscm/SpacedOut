package action;

import gui.ParticipantGUI;

public class ChangePlayDirectionAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public ChangePlayDirectionAction() {}
	
	/**
	 * Generates and returns a Change Play Direction Action message.
	 * @return a Change Play Direction Action message
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
		gui.changePlayDirection();
		return null;
	}
}
