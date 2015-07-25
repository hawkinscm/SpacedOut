package action;

import model.Player;
import gui.SpacedOutGUI;

public class PlayerReadyAction extends Action<SpacedOutGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public PlayerReadyAction() {}
	
	/**
	 * Generates and returns a Player Ready Action message using the given data.
	 * Note this will also update the player's type if it has changed.
	 * @param player the player whose ready status has changed that will be written as data in the message
	 * @return a Player Ready Action message generated from the given data
	 */
	public String createMessage(Player player, boolean isReady) {
		String message = this.getClass().getName();
		message += generateMessage(player);
		message += MAIN_DELIM + isReady;
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<SpacedOutGUI> getActionTypeClass() {
		return SpacedOutGUI.class;
	}
	
	@Override
	public String[] performAction(SpacedOutGUI gui) {
		String[] actionData = getMessageWithoutClassHeader().split(MAIN_DELIM);
		Player parsedPlayer = parsePlayer(actionData[0]);
		Player foundPlayer = gui.getPlayer(parsedPlayer.getName());
		foundPlayer.setPlayerType(parsedPlayer.getPlayerType());
		String isReady = actionData[1];
		gui.setPlayerReadyStatus(foundPlayer, Boolean.parseBoolean(isReady));
		return null;
	}
}
