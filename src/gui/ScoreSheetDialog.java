package gui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.GM;
import model.Player;
import model.ScoreKeeper;

/**
 * Displays the current scores for all players, for each round and in total
 */
public class ScoreSheetDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	private boolean noDisplay = false;
	
	/**
	 * Creates a new Dialog to display player scores.
	 * @param owner the frame that called this dialog
	 * @param scoreKeeper the ScoreKeeper with all the player scores
	 * @param players list of all players in the game
	 * @param isGameOver true if the game is over; false, otherwise
	 */
	public ScoreSheetDialog(Frame owner, ScoreKeeper scoreKeeper, LinkedList<Player> players, boolean isGameOver) {
		super(owner, "Player Scores");
		
		if (scoreKeeper == null || players == null || players.isEmpty()) {
			noDisplay = true;
			return;
		}
				
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.WEST;
		getContentPane().add(new JLabel(" "), c);
		
		for (int round = 1; round <= scoreKeeper.getNumberOfRoundsFinished(); round++) {
			c.insets = new Insets(0, 5, 0, 5);
			c.gridy++;
			getContentPane().add(new JLabel("Round " + round), c);
		}

		c.insets = new Insets(0, 5, 0, 5);
		c.gridy++;
		c.gridwidth = players.size() + 1;
		JPanel linePanel = new JPanel();
		getContentPane().add(linePanel, c);
		c.gridwidth = 1;
		
		c.gridy++;
		JLabel totalLabel = new JLabel("TOTAL");
		totalLabel.setFont(totalLabel.getFont().deriveFont(13f));
		getContentPane().add(totalLabel, c);
		
		c.anchor = GridBagConstraints.CENTER;
		for (Player player : players) {
			c.gridy = 0;
			c.gridx++;
			getContentPane().add(new JLabel(player.getName()), c);
			
			for (int round = 1; round <= scoreKeeper.getNumberOfRoundsFinished(); round++) {
				c.insets = new Insets(0, 5, 0, 5);
				c.gridy++;
				getContentPane().add(new JLabel("" + scoreKeeper.getPlayerRoundScore(player, round)), c);
			}
			c.insets = new Insets(5, 5, 5, 5);
			
			int playerScore = scoreKeeper.getPlayerScore(player);
			
			c.gridy += 2;
			totalLabel = new JLabel("" + playerScore);
			totalLabel.setFont(totalLabel.getFont().deriveFont(13f));
			if (playerScore >= GM.getEndGameScore())
				totalLabel.setForeground(Color.RED);
			getContentPane().add(totalLabel, c);
			
			c.gridy++;
			int playerRank = 1;
			for (Player currentPlayer : players)
				if (playerScore > scoreKeeper.getPlayerScore(currentPlayer))
					playerRank++;
			JLabel playerRankLabel = new JLabel();
			String playerRankStr = "" + playerRank;
			if (playerRank == 1 && isGameOver) {
				playerRankStr = "WINNER";
				playerRankLabel.setForeground(Color.BLUE);
			}
			else if (playerRank == 1)
				playerRankStr += "st";
			else if (playerRank == 2)
				playerRankStr += "nd";
			else if (playerRank == 3)
				playerRankStr += "rd";
			else
				playerRankStr += "th";
			playerRankLabel.setText(playerRankStr);
			getContentPane().add(playerRankLabel, c);
		}
		
		refresh();
	}
	
	@Override
	public void setVisible(boolean b) {
		if (noDisplay)
			return;
		super.setVisible(b);
	}
}
