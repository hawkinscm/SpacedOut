package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import model.Player;

/**
 * Panel for displaying player order and the number of cards in each player's hands.
 */
public class PlayersDisplayPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private HashMap<Player, PlayerInfoPanel> playerPanels;
	private Player currentPlayer;
	
	/**
	 * Creates a new Players Display Panel.
	 */
	public PlayersDisplayPanel() {
		setLayout(new GridBagLayout());
		playerPanels = new HashMap<Player, PlayerInfoPanel>();
		currentPlayer = null;
	}
	
	/**
	 * Returns the location of the display for the current player.
	 * @return the location, (x,y) point relative to this players display panel, for the current player
	 */
	public Point getCurrentPlayerDisplayLocation() {
		if (currentPlayer == null || !playerPanels.containsKey(currentPlayer))
			return null;
			
		PlayerInfoPanel infoPanel = playerPanels.get(currentPlayer);		
		Point location = infoPanel.getLocation();
		location.x += (infoPanel.getWidth() - CardLabel.CARD_WIDTH) / 2;
		location.y += 2;
		return location;
	}	
	
	/**
	 * Sets the players to display
	 * @param players
	 */
	public void setPlayers(List<Player> players) {
		removeAll();
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		for (Player player : players) {
			PlayerInfoPanel panel = new PlayerInfoPanel(player);
			add(panel, c);
			playerPanels.put(player, panel);
			c.gridx++;
		}
		
		revalidate();
		setCurrentPlayer(currentPlayer);
	}
	
	/**
	 * Sets the current player to the given player and highlights him as the new current player
	 * @param player player to select.
	 */
	public void setCurrentPlayer(Player player) {
		if (currentPlayer != null && currentPlayer != player)
			playerPanels.get(currentPlayer).setBorder(BorderFactory.createLineBorder(getBackground(), 2));
		
		currentPlayer = player;
		if (playerPanels.containsKey(player))
			playerPanels.get(player).setBorder(BorderFactory.createLineBorder(new Color(255, 0, 0), 2));
	}
}
