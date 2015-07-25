package action;

import java.util.LinkedList;

import model.Player;

import gui.ParticipantGUI;

public class NewGameAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public NewGameAction() {}
	
	/**
	 * Generates and returns a New Game Action message using the given data.
	 * @param newPlayers the new game players that will be written as data in the message
	 * @param playerName the name of the player that the receiver of this message represents
	 * @return a New Game Action message generated from the given data
	 */
	public String createMessage(LinkedList<Player> newPlayers, String participantName) {
		String message = this.getClass().getName();
		message += MAIN_DELIM + participantName;
		for (Player player : newPlayers)
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
		LinkedList<Player> players = new LinkedList<Player>();
		String[] actionData = getMessageWithoutClassHeader().split(MAIN_DELIM);
		String participantName = actionData[0];
		for (int actionIndex = 1; actionIndex < actionData.length; actionIndex++)
			players.add(parsePlayer(actionData[actionIndex]));
		
		gui.newGame(players, participantName);
		return null;
	}
}
