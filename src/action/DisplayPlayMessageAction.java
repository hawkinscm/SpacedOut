package action;

import gui.PlayAreaPanel;

public class DisplayPlayMessageAction extends Action<PlayAreaPanel> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public DisplayPlayMessageAction() {}
	
	/**
	 * Generates and returns a Display Play Message Action message using the given data.
	 * @param message message to send
	 * @return a Display Play Message Action message generated from the given data
	 */
	public String createMessage(String message) {
		setMessage(this.getClass().getName() + MAIN_DELIM + message);
		return getMessage();
	}
	
	@Override
	public Class<PlayAreaPanel> getActionTypeClass() {
		return PlayAreaPanel.class;
	}
	
	@Override
	public String[] performAction(PlayAreaPanel playPanel) {
		String message = getMessageWithoutClassHeader();
		playPanel.displayMessage(message);
		return null;
	}
}
