package action;

import model.GM;
import model.Player;

public class ActionPlayerGM {

	public GM gm;
	public Player player;
	
	public ActionPlayerGM(GM gm, Player player) {
		this.gm = gm;
		this.player = player;
	}
}
