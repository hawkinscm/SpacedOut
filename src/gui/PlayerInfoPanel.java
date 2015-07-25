package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import model.Player;
import model.PlayerType;

/**
 * Displays a player's name and the number of cards he currently holds.
 * Will also observe that player and update the display any time the number of cards he holds changes.
 */
public class PlayerInfoPanel extends JPanel implements Observer {
	private static final long serialVersionUID = 1L;
	
	private JLabel cardCountLabel;
	private JLabel playerTypeLabel;

	private Timer unhighlightTimer;
	
	/**
	 * Creates a new Player Info Panel to display info for a player.
	 * @param player the player to display
	 */
	public PlayerInfoPanel(Player player) {
		player.addObserver(this);
		
		setBorder(BorderFactory.createLineBorder(getBackground(), 2));
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		
		cardCountLabel = new JLabel("");
		cardCountLabel.setOpaque(false);
		add(cardCountLabel, c);
		
		add(new CardLabel(), c);		
		
		c.gridy++;
		add(new JLabel(player.getName()), c);
		
		c.gridy++;
		playerTypeLabel = new JLabel(player.getPlayerType().toString());
		add(playerTypeLabel, c);
		
		Action unhighlightAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				cardCountLabel.setForeground(Color.ORANGE);
		    }
		};
		unhighlightTimer = new Timer(1000, unhighlightAction);
		unhighlightTimer.setRepeats(false);
		
		update(player, new Boolean(player.isComputer()));
	}
	
	@Override
	public void update(Observable observable, Object obj) {
		// if the player's cards have changed in any way
		if (observable instanceof Player) {
			Player player = (Player)observable;
			// player's type has changed
			if (obj instanceof PlayerType) 
				playerTypeLabel.setText(player.getPlayerType().toString());
			else if (obj instanceof Boolean) {
				// if player is not ready to play
				if (((Boolean)obj) == false) {
					cardCountLabel.setText("<html><center>NOT<br>READY</center></html>");
					unhighlightTimer.stop();
					cardCountLabel.setForeground(Color.RED);
					cardCountLabel.setFont(cardCountLabel.getFont().deriveFont((float)20));
				}
				// else player is ready to play
				else 
					cardCountLabel.setFont(cardCountLabel.getFont().deriveFont((float)26));
			}

			if (player.isReadyToPlay()) {
				cardCountLabel.setText("" + player.countCards());
				cardCountLabel.setForeground(Color.WHITE);
				unhighlightTimer.restart();
			}
		}
	}
}
