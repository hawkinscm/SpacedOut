package action;

import model.GameDeck.PileLocation;

import gui.ParticipantGUI;

public class PlayCloneAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public PlayCloneAction() {}
	
	/**
	 * Generates and returns a Play Clone Action message using the given data.
	 * @param pileLocation the pile, specified by location, where the clone is played that will be written as data in the message
	 * @return a Play Clone Action message generated from the given data
	 */
	public String createMessage(PileLocation pileLocation) {
		String message = this.getClass().getName();
		message += MAIN_DELIM + pileLocation.toString();
		
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		PileLocation pileLocation = PileLocation.valueOf(getMessageWithoutClassHeader());
		gui.clonePlayed(pileLocation);
		return null;
	}
}
