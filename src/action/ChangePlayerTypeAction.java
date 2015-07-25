package action;

import model.Player;
import gui.ParticipantGUI;

public class ChangePlayerTypeAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public ChangePlayerTypeAction() {}
	
	/**
	 * Generates and returns a Change Player Type Action message using the given data.
	 * @param player the player whose player type changed that will be written as data in the message
	 * @return a Change Player Type Action message generated from the given data
	 */
	public String createMessage(Player player) {
		String message = this.getClass().getName();
		message += generateMessage(player);
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		Player player = parsePlayer(getMessageWithoutClassHeader());
		gui.getPlayer(player.getName()).setPlayerType(player.getPlayerType());
		return null;
	}
}
