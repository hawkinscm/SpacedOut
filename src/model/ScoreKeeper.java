package model;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Keeps and tallies the players' scores for each round played. 
 */
public class ScoreKeeper {
	
	private HashMap<Player, LinkedList<Integer>> roundScoreCard;
	private int numberOfRoundsFinished;
	
	/**
	 * Creates a new Score Keeper for the given players.
	 * @param players list of players to keep scores for
	 */
	public ScoreKeeper(LinkedList<Player> players) {
		numberOfRoundsFinished = 0;
		
		roundScoreCard = new HashMap<Player, LinkedList<Integer>>();
		for (Player player : players)
			roundScoreCard.put(player, new LinkedList<Integer>());
	}
	
	/**
	 * Tallies the players' scores for the round.
	 * @param playPilesScore the score from the play piles
	 */
	public void tallyRoundScores(int playPilesScore) {
		numberOfRoundsFinished++;
		
		for (Player player : roundScoreCard.keySet()) {
			int score = player.getCardScore();
			if (score == 0)
				score = playPilesScore;
	
			roundScoreCard.get(player).add(score);
		}
	}
	
	/**
	 * Returns the score the given player received for the specified round.
	 * @param player the player to get the round score for
	 * @param roundNum the round number to get the score for
	 * @return the score the given player received for the specified round; 0 when the round has not yet ended or roundNum is <= 0
	 */
	public int getPlayerRoundScore(Player player, int roundNum) {
		if (roundNum <= 0 || roundNum > numberOfRoundsFinished)
			return 0;
		
		return roundScoreCard.get(player).get(roundNum - 1);
	}
	
	public HashMap<Player, Integer> getRoundScores(int roundNum) {
		HashMap<Player, Integer> playerScores = new HashMap<Player, Integer>();
		for (Player player : roundScoreCard.keySet())
			playerScores.put(player, getPlayerRoundScore(player, roundNum));
		return playerScores;
	}
	
	/**
	 * Returns the total score for all rounds for the given player.
	 * @param player the player whose total score will be given
	 * @return the total score for the given player.
	 */
	public int getPlayerScore(Player player) {
		int totalScore = 0;
		for (Integer roundScore : roundScoreCard.get(player))
			totalScore += roundScore;
		
		return totalScore;
	}
	
	/**
	 * Returns the number of rounds that have been played to completion.
	 * @return the number of round that have been finished
	 */
	public int getNumberOfRoundsFinished() {
		return numberOfRoundsFinished;
	}
	
	/**
	 * Adds a round of scores for the players.
	 * @param playerScores mapping of players to their scores for the round.
	 */
	public void addPlayerRoundScores(HashMap<Player, Integer> playerScores) {
		numberOfRoundsFinished++;
		
		for (Player player : roundScoreCard.keySet()) {
			int score = playerScores.get(player);	
			roundScoreCard.get(player).add(score);
		}
	}
}
