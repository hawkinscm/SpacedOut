package action;

import gui.ParticipantGUI;

public class DrewCardsAction extends Action<ParticipantGUI>{

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public DrewCardsAction() {}
	
	/**
	 * Generates and returns a Drew Cards Action message using the given data.
	 * @param numDrawnCards the number of cards drawn that will be written as data in the message
	 * @return a Drew Cards Action message generated from the given data
	 */
	public String createMessage(int numDrawnCards) {
		String message = this.getClass().getName();
		message += MAIN_DELIM + numDrawnCards;
		
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		int numDrawnCards = Integer.parseInt(getMessageWithoutClassHeader());
		gui.displayCardDraw(numDrawnCards);
		return null;
	}
}
