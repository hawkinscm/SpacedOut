package action;

import java.util.HashMap;

import model.Player;

import gui.ParticipantGUI;

public class AddRoundScoresAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public AddRoundScoresAction() {}
	
	/**
	 * Generates and returns an Add Round Scores Action message using the given data.
	 * @param playerScores the round scores for each player which will be written as data in the message
	 * @param shouldDisplay whether or not the scores should be displayed
	 * @return an Add Round Scores Action message generated from the given data
	 */
	public String createMessage(HashMap<Player, Integer> playerScores, boolean shouldDisplay) {
		String message = this.getClass().getName();
		message += MAIN_DELIM + shouldDisplay;
		for (Player player : playerScores.keySet())
			message += generateMessage(player) + INNER_DELIM + playerScores.get(player);
		
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
		boolean shouldDisplay = Boolean.parseBoolean(actionData[0]);
		HashMap<Player, Integer> playerScores = new HashMap<Player, Integer>();
		for (int actionIndex = 1; actionIndex < actionData.length; actionIndex++) {
			String playerRoundScore = actionData[actionIndex];
			String score = playerRoundScore.substring(playerRoundScore.lastIndexOf(INNER_DELIM) + 1);
			Player player = gui.getPlayer(parsePlayer(playerRoundScore).getName());
			playerScores.put(player, Integer.parseInt(score));
		}
		if (shouldDisplay)
			gui.addAndDisplayRoundScores(playerScores);
		else
			gui.addRoundScores(playerScores);
		
		return null;
	}
}
