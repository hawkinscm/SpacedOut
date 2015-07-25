package action;

public class RequestDrawAction extends Action<ActionPlayerGM> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public RequestDrawAction() {}
	
	/**
	 * Generates and returns a Request Draw Action message.
	 * @return a Request Draw Action message
	 */
	public String createMessage() {
		setMessage(this.getClass().getName());
		return getMessage();
	}
	
	@Override
	public Class<ActionPlayerGM> getActionTypeClass() {
		return ActionPlayerGM.class;
	}
	
	@Override
	public String[] performAction(ActionPlayerGM actionPlayerGM) {
		boolean drewCards = actionPlayerGM.gm.drawCardsIfAllowed(actionPlayerGM.player);
		if (drewCards)
			return new String [] {new SetPlayerCardsAction().createMessage(actionPlayerGM.player.getCards())};
		else if (!actionPlayerGM.player.isReadyToPlay())
			return null;
		
		if (actionPlayerGM.gm.areAllPlayersReadyToBeginNewRound())
			return new String [] {new DisplayPlayMessageAction().createMessage("It's not your turn.")};
		else
			return new String [] {new DisplayPlayMessageAction().createMessage("Not all players are ready to begin a new round.")};
	}
}
