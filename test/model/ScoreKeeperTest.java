package model;

import java.util.LinkedList;

import unit.UnitTest;

/**
 * Unit tester for the Score Keeper class.
 */
public class ScoreKeeperTest extends UnitTest {
	
	public int testAll() {
		testScoreKeeper();
		
		return errorCount;
	}
	
	private void testScoreKeeper() {		
		Player p1 = new Player("p1", PlayerType.HOST);
		Player p2 = new Player("p2", PlayerType.COMPUTER_EASY);
		Player p3 = new Player("p3", PlayerType.COMPUTER_MEDIUM);
		Player p4 = new Player("p4", PlayerType.COMPUTER_HARD);
		
		Card yA = new Card(Card.Design.ASTEROIDS, Card.Color.YELLOW, 20);
		Card bA = new Card(Card.Design.ASTEROIDS, Card.Color.BLUE, 20);
		Card rSS = new Card(Card.Design.SHOOTING_STAR, Card.Color.RED, 20);
		Card wSS = new Card(Card.Design.SHOOTING_STAR, Card.Color.WILD, 30);
		Card rbBB = new Card(Card.Design.BIG_BANG, Card.Color.BLUE_AND_RED, 100);
		Card gyBB = new Card(Card.Design.BIG_BANG, Card.Color.GREEN_AND_YELLOW, 100);
		Card gBH = new Card(Card.Design.BLACK_HOLE, Card.Color.GREEN, 30);
		Card wBH = new Card(Card.Design.BLACK_HOLE, Card.Color.WILD, 50);
		Card rFF = new Card(Card.Design.FORCE_FIELD, Card.Color.RED, 20);
		Card wFF = new Card(Card.Design.FORCE_FIELD, Card.Color.WILD, 100);
		Card b1 = new Card(Card.Design.NUMBER, Card.Color.BLUE, 1);
		Card g2 = new Card(Card.Design.NUMBER, Card.Color.GREEN, 2);
		Card r3 = new Card(Card.Design.NUMBER, Card.Color.RED, 3);
		Card b4 = new Card(Card.Design.NUMBER, Card.Color.BLUE, 4);
		Card g5 = new Card(Card.Design.NUMBER, Card.Color.GREEN, 5);
		Card y6 = new Card(Card.Design.NUMBER, Card.Color.YELLOW, 6);
		Card y7 = new Card(Card.Design.NUMBER, Card.Color.YELLOW, 7);
		Card g8 = new Card(Card.Design.NUMBER, Card.Color.GREEN, 8);
		Card r9 = new Card(Card.Design.NUMBER, Card.Color.RED, 9);
		Card b10 = new Card(Card.Design.NUMBER, Card.Color.BLUE, 10);
		
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);
		
		ScoreKeeper keeper = new ScoreKeeper(players);
		assertEquals(keeper.getNumberOfRoundsFinished(), 0);
		keeper.tallyRoundScores(5);
		assertEquals(keeper.getNumberOfRoundsFinished(), 1);
		for (Player player : players) {
			assertEquals(keeper.getPlayerRoundScore(player, 0), 0);
			assertEquals(keeper.getPlayerRoundScore(player, -1), 0);
			assertEquals(keeper.getPlayerRoundScore(player, 2), 0);
			assertEquals(keeper.getPlayerRoundScore(player, 100), 0);
			assertEquals(keeper.getPlayerRoundScore(player, 1), 5);
			assertEquals(keeper.getPlayerScore(player), 5);
		}
		
		LinkedList<Card> cards = new LinkedList<Card>();
		cards.add(r3);
		p1.addCards(cards);
		int p1r2 = 3;
		
		cards.add(rbBB);
		cards.add(wFF);
		p2.addCards(cards);
		int p2r2 = 3 + 100 + 100;
		
		cards.add(gBH);
		cards.add(wBH);
		cards.add(y7);
		cards.add(b10);
		p3.addCards(cards);
		int p3r2 = 3 + 100 + 100 + 30 + 50 + 7 + 10;
		
		cards = new LinkedList<Card>();
		cards.add(rFF);
		cards.add(yA);
		cards.add(wSS);
		p4.addCards(cards);
		int p4r2 = 20 + 20 + 30;
		
		keeper.tallyRoundScores(9);
		assertEquals(keeper.getNumberOfRoundsFinished(), 2);
		for (Player player : players)
			assertEquals(keeper.getPlayerRoundScore(player, 1), 5);		
		assertEquals(keeper.getPlayerRoundScore(p1, 2), p1r2);
		assertEquals(keeper.getPlayerScore(p1), 5 + p1r2);
		assertEquals(keeper.getPlayerRoundScore(p2, 2), p2r2);
		assertEquals(keeper.getPlayerScore(p2), 5 + p2r2);
		assertEquals(keeper.getPlayerRoundScore(p3, 2), p3r2);
		assertEquals(keeper.getPlayerScore(p3), 5 + p3r2);
		assertEquals(keeper.getPlayerRoundScore(p4, 2), p4r2);
		assertEquals(keeper.getPlayerScore(p4), 5 + p4r2);
		
		p1.discardAll();
		p2.discardAll();
		p4.discardAll();
		
		cards = new LinkedList<Card>();
		cards.add(bA);
		cards.add(rSS);
		cards.add(r9);
		cards.add(b1);
		cards.add(g5);
		cards.add(g8);
		p1.addCards(cards);
		int p1r3 = 20 + 20 + 9 + 1 + 5 + 8;
				
		cards = new LinkedList<Card>();
		cards.add(gyBB);
		cards.add(g2);
		cards.add(b4);
		cards.add(y6);
		cards.add(y6);
		cards.add(y7);
		p3.addCards(cards);
		int p3r3 = p3r2 + 100 + 2 + 4 + 6 + 6 + 7;
		
		cards = new LinkedList<Card>();
		cards.add(yA);
		cards.add(bA);
		cards.add(rSS);
		cards.add(wSS);
		cards.add(rbBB);
		cards.add(gyBB);
		cards.add(gBH);
		cards.add(wBH);
		cards.add(rFF);
		cards.add(wFF);
		cards.add(b1);
		cards.add(g2);
		cards.add(r3);
		cards.add(b4);
		cards.add(g5);
		cards.add(y6);
		cards.add(y7);
		cards.add(g8);
		cards.add(r9);
		cards.add(b10);
		p4.addCards(cards);
		int p4r3 = 20 + 20 + 20 + 30 + 100 + 100 + 30 + 50 + 20 + 100 + 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10;
		
		keeper.tallyRoundScores(9);
		assertEquals(keeper.getNumberOfRoundsFinished(), 3);		
		assertEquals(keeper.getPlayerRoundScore(p1, 3), p1r3);
		assertEquals(keeper.getPlayerScore(p1), 5 + p1r2 + p1r3);
		assertEquals(keeper.getPlayerRoundScore(p2, 3), 9);
		assertEquals(keeper.getPlayerScore(p2), 5 + p2r2 + 9);
		assertEquals(keeper.getPlayerRoundScore(p3, 3), p3r3);
		assertEquals(keeper.getPlayerScore(p3), 5 + p3r2 + p3r3);
		assertEquals(keeper.getPlayerRoundScore(p4, 3), p4r3);
		assertEquals(keeper.getPlayerScore(p4), 5 + p4r2 + p4r3);
		
		p1.discardAll();
		p2.discardAll();
		p3.discardAll();
		p4.discardAll();
	}	
}
