package action;

import model.GameDeck.PileLocation;

import gui.ParticipantGUI;

public class BattleAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public BattleAction() {}
	
	/**
	 * Generates and returns a Play Clone Action message using the given data.
	 * @param battleDrawCount the number of cards needed to be drawn to end the battle that will be written as data in the message
	 * @param livePile the live pile, specified by location that will be written as data in the message
	 * @return a Play Clone Action message generated from the given data
	 */
	public String createMessage(int battleDrawCount, PileLocation livePile) {
		String message = this.getClass().getName();
		message += MAIN_DELIM + battleDrawCount;
		message += MAIN_DELIM + livePile.toString();
		
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
		int battleDrawCount = Integer.parseInt(actionData[0]);
		PileLocation pileLocation = PileLocation.valueOf(actionData[1]);
		gui.battleUpdate(battleDrawCount, pileLocation);
		return null;
	}
}
