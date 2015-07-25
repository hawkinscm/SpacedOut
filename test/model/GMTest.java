package model;

import java.util.LinkedList;

import model.Card.Color;

import unit.UnitTest;

/**
 * Unit tester for the GM class.
 */
public class GMTest extends UnitTest {
	private GM gm;
	
	private Player p1;
	private Player p2;
	private Player p3;
	private Player p4;
	private LinkedList<Player> players;
	
	public int testAll() {
		initialize();
		
		testNewGame();
		testDrawCards();
		testPlayCards();
		
		return errorCount;
	}
	
	private void initialize() {				
		p1 = new Player("p1", PlayerType.HOST);
		p2 = new Player("p2", PlayerType.COMPUTER_EASY);
		p3 = new Player("p3", PlayerType.COMPUTER_MEDIUM);
		p4 = new Player("p4", PlayerType.COMPUTER_HARD);
		players = new LinkedList<Player>();
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);
		
		gm = new GM() {
			@Override
			public boolean areAllPlayersReadyToBeginNewRound() {
				return true;
			}
		};
	}
	
	public void testNewGame() {		
		gm.newGame(players, 2);
		for (Player player : players)
			assertEquals(player.countCards(), 6);
		
		players.clear();
		players.add(p1);
		p1.discardAll();
		gm.newGame(players, 2);
		players.add(p2);
		players.add(p3);
		players.add(p4);
		for (Player player : players)
			assertEquals(player.countCards(), 6);
		
		assertEquals(GM.getEndGameScore(), 500);
		GM.setEndGameScore(-1);
		assertEquals(GM.getEndGameScore(), -1);
		GM.setEndGameScore(0);
		assertEquals(GM.getEndGameScore(), 0);
		GM.setEndGameScore(100);
		assertEquals(GM.getEndGameScore(), 100);
		GM.setEndGameScore(9836);
		assertEquals(GM.getEndGameScore(), 9836);
		GM.setEndGameScore(500);
		
		assertEquals(GM.getDealNumber(), 6);
		GM.setDealNumber(-1);
		assertEquals(GM.getDealNumber(), -1);
		GM.setDealNumber(0);
		assertEquals(GM.getDealNumber(), 0);
		GM.setDealNumber(1);
		assertEquals(GM.getDealNumber(), 1);
		GM.setDealNumber(5);
		assertEquals(GM.getDealNumber(), 5);
		GM.setDealNumber(100);
		assertEquals(GM.getDealNumber(), 100);
		for (Player player : players)
			assertEquals(player.countCards(), 6);
		
		GM.setDealNumber(6);
	}
	
	private void testDrawCards() {
		assertFalse(gm.drawCardsIfAllowed(p2));
		assertEquals(p2.countCards(), 6);
		assertFalse(gm.drawCardsIfAllowed(p3));
		assertEquals(p3.countCards(), 6);
		assertFalse(gm.drawCardsIfAllowed(p4));
		assertEquals(p4.countCards(), 6);
		assertTrue(gm.drawCardsIfAllowed(p1));
		assertEquals(p1.countCards(), 7);
		assertFalse(gm.drawCardsIfAllowed(p1));
		assertEquals(p1.countCards(), 7);
		assertFalse(gm.drawCardsIfAllowed(p3));
		assertEquals(p3.countCards(), 6);
		assertFalse(gm.drawCardsIfAllowed(p4));
		assertEquals(p4.countCards(), 6);
		assertTrue(gm.drawCardsIfAllowed(p2));
		assertEquals(p2.countCards(), 7);
		assertTrue(gm.drawCardsIfAllowed(p3));
		assertEquals(p3.countCards(), 7);
		assertTrue(gm.drawCardsIfAllowed(p4));
		assertEquals(p4.countCards(), 7);
		assertTrue(gm.drawCardsIfAllowed(p1));
		assertEquals(p1.countCards(), 8);
		assertTrue(gm.drawCardsIfAllowed(p2));
		assertEquals(p2.countCards(), 8);
		assertTrue(gm.drawCardsIfAllowed(p3));
		assertEquals(p3.countCards(), 8);
		assertTrue(gm.drawCardsIfAllowed(p4));
		assertEquals(p4.countCards(), 8);
	}
		
	private void testPlayCards() {
		final String NOT_TURN = "It's not your turn.";
		final String LIVE_PILE = "Must be WILD or of the same design or color as the LIVE PILE to play there.";
		final String DEAD_PILE = "Must be WILD or of the same design as the DEAD PILE to play there.";
		final String BAD_CLONE = "Not a proper clone.";
		final String BATTLE_LIVE_PILE = "Must continue battle on the LIVE PILE.";
		final String FORCE_FIELD = "Can only play FORCE FIELD on FORCE FIELD defense.";
		final String BIG_BANG = "Can only play SUPER FORCE FIELD on BIG BANG attack.";
		final String NO_BATTLE = "Cannot be played during a battle.";
		
		Card yA = new Card(Card.Design.ASTEROIDS, Card.Color.YELLOW, 20);
		Card gA = new Card(Card.Design.ASTEROIDS, Card.Color.GREEN, 20);
		Card rSS = new Card(Card.Design.SHOOTING_STAR, Card.Color.RED, 20);
		Card wSS = new Card(Card.Design.SHOOTING_STAR, Card.Color.WILD, 30);
		Card rbBB = new Card(Card.Design.BIG_BANG, Card.Color.BLUE_AND_RED, 100);
		Card gyBB = new Card(Card.Design.BIG_BANG, Card.Color.GREEN_AND_YELLOW, 100);
		Card rBH = new Card(Card.Design.BLACK_HOLE, Card.Color.RED, 30);
		Card wBH = new Card(Card.Design.BLACK_HOLE, Card.Color.WILD, 50);
		Card rFF = new Card(Card.Design.FORCE_FIELD, Card.Color.RED, 20);
		Card bFF = new Card(Card.Design.FORCE_FIELD, Card.Color.BLUE, 20);
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
		Card r10 = new Card(Card.Design.NUMBER, Card.Color.RED, 10);
		
		// standard
		LinkedList<Card> tempCards = new LinkedList<Card>();
		tempCards.add(wFF);
		p1.addCards(tempCards);
		p2.addCards(tempCards);
		p4.addCards(tempCards);
		assertEquals(gm.playCardIfAllowed(p2, wFF, GameDeck.PileLocation.LEFT, Color.GREEN), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, wFF, GameDeck.PileLocation.LEFT, Color.GREEN), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.LEFT, Color.GREEN), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p1, wFF, GameDeck.PileLocation.LEFT), "");
		assertEquals(gm.playCardIfAllowed(p1, wFF, GameDeck.PileLocation.LEFT, Color.WILD), "");
		assertEquals(gm.playCardIfAllowed(p1, wFF, GameDeck.PileLocation.LEFT, null), "");
		assertEquals(p1.countCards(), 9);
		assertNull(gm.playCardIfAllowed(p1, wFF, GameDeck.PileLocation.LEFT, Color.GREEN));
		assertEquals(p1.countCards(), 8);
		assertEquals(gm.playCardIfAllowed(p1, wFF, GameDeck.PileLocation.RIGHT, Color.YELLOW), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, wFF, GameDeck.PileLocation.RIGHT, Color.YELLOW), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, wFF, GameDeck.PileLocation.RIGHT, Color.YELLOW), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.RIGHT), "");
		assertEquals(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.RIGHT, Color.WILD), "");
		assertEquals(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.RIGHT, null), "");
		assertEquals(p4.countCards(), 9);
		assertNull(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.RIGHT, Color.YELLOW));
		assertEquals(p4.countCards(), 8);
		assertEquals(gm.playCardIfAllowed(p1, g5, GameDeck.PileLocation.LEFT), DEAD_PILE);
		assertEquals(gm.playCardIfAllowed(p1, g5, GameDeck.PileLocation.RIGHT), LIVE_PILE);
		assertEquals(gm.playCardIfAllowed(p1, y6, GameDeck.PileLocation.LEFT), DEAD_PILE);
		assertNull(gm.playCardIfAllowed(p1, y6, GameDeck.PileLocation.RIGHT));
		
		// clone
		assertEquals(gm.playCloneIfAllowed(p4, wFF, GameDeck.PileLocation.LEFT), BAD_CLONE);
		assertEquals(gm.playCardIfAllowed(p4, y6, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCloneIfAllowed(p4, y6, GameDeck.PileLocation.LEFT), BAD_CLONE);
		assertNull(gm.playCloneIfAllowed(p4, y6, GameDeck.PileLocation.RIGHT));
		assertEquals(gm.playCloneIfAllowed(p2, y6, GameDeck.PileLocation.RIGHT), "Can't clone during p4's extra turn.");
		assertEquals(gm.playCardIfAllowed(p4, rFF, GameDeck.PileLocation.RIGHT), LIVE_PILE);
		assertNull(gm.playCardIfAllowed(p4, rFF, GameDeck.PileLocation.LEFT));
		assertEquals(gm.playCloneIfAllowed(p3, rFF, GameDeck.PileLocation.LEFT), BAD_CLONE);
		assertNull(gm.playCardIfAllowed(p3, rFF, GameDeck.PileLocation.LEFT));
		assertNull(gm.playCloneIfAllowed(p2, y6, GameDeck.PileLocation.RIGHT));
		assertEquals(gm.playCardIfAllowed(p2, r9, GameDeck.PileLocation.LEFT), DEAD_PILE);
		assertNull(gm.playCardIfAllowed(p2, y7, GameDeck.PileLocation.RIGHT));
		
		// black hole
		assertEquals(gm.playCardIfAllowed(p3, rBH, GameDeck.PileLocation.LEFT), DEAD_PILE);
		assertNull(gm.playCardIfAllowed(p3, wBH, GameDeck.PileLocation.LEFT, Color.RED));
		assertNull(gm.playCardIfAllowed(p3, r3, GameDeck.PileLocation.LEFT));
		assertEquals(gm.playCardIfAllowed(p1, rBH, GameDeck.PileLocation.LEFT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, rBH, GameDeck.PileLocation.LEFT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, rBH, GameDeck.PileLocation.LEFT), NOT_TURN);
		assertNull(gm.playCardIfAllowed(p4, rBH, GameDeck.PileLocation.LEFT));
		assertNull(gm.playCardIfAllowed(p4, wBH, GameDeck.PileLocation.LEFT, Color.GREEN));
		assertEquals(gm.playCloneIfAllowed(p2, y7, GameDeck.PileLocation.RIGHT), "Can't clone during p4's extra turn.");
		assertNull(gm.playCardIfAllowed(p4, g2, GameDeck.PileLocation.LEFT, Color.GREEN));
		assertNull(gm.playCloneIfAllowed(p2, y7, GameDeck.PileLocation.RIGHT));
		assertFalse(gm.drawCardsIfAllowed(p1));
		assertFalse(gm.drawCardsIfAllowed(p3));
		assertFalse(gm.drawCardsIfAllowed(p4));
		assertTrue(gm.drawCardsIfAllowed(p2));
		assertEquals(p2.countCards(), 9 + 1);
		
		// asteroids
		assertEquals(gm.playCardIfAllowed(p1, yA, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, yA, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, yA, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, gA, GameDeck.PileLocation.LEFT), DEAD_PILE);
		assertNull(gm.playCardIfAllowed(p3, yA, GameDeck.PileLocation.RIGHT));
		assertEquals(gm.playCloneIfAllowed(p1, g2, GameDeck.PileLocation.LEFT), "Can't clone during a battle.");
		assertEquals(gm.playCardIfAllowed(p1, gA, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, gA, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, gA, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, gA, GameDeck.PileLocation.LEFT), BATTLE_LIVE_PILE);
		assertEquals(gm.playCardIfAllowed(p4, g2, GameDeck.PileLocation.RIGHT), "Can only play ASTEROIDS or YELLOW FORCE FIELD on YELLOW ASTEROIDS attack.");
		assertNull(gm.playCardIfAllowed(p4, gA, GameDeck.PileLocation.RIGHT));
		assertEquals(gm.playCardIfAllowed(p2, wFF, GameDeck.PileLocation.RIGHT, Color.GREEN), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, wFF, GameDeck.PileLocation.RIGHT, Color.GREEN), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.RIGHT, Color.GREEN), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p1, rFF, GameDeck.PileLocation.RIGHT), "Can only play ASTEROIDS or GREEN FORCE FIELD on GREEN ASTEROIDS attack.");
		assertEquals(gm.playCardIfAllowed(p1, wFF, GameDeck.PileLocation.LEFT, Color.GREEN), BATTLE_LIVE_PILE);
		assertNull(gm.playCardIfAllowed(p1, wFF, GameDeck.PileLocation.RIGHT, Color.GREEN));
		assertEquals(gm.playCardIfAllowed(p1, g2, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, g2, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, g2, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, g2, GameDeck.PileLocation.RIGHT), FORCE_FIELD);
		assertEquals(gm.playCardIfAllowed(p1, gA, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, gA, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, gA, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, gA, GameDeck.PileLocation.RIGHT), FORCE_FIELD);
		assertEquals(gm.playCardIfAllowed(p1, rFF, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, rFF, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, rFF, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertNull(gm.playCardIfAllowed(p4, rFF, GameDeck.PileLocation.RIGHT));
		assertFalse(gm.drawCardsIfAllowed(p2));
		assertFalse(gm.drawCardsIfAllowed(p3));
		assertFalse(gm.drawCardsIfAllowed(p4));
		assertEquals(gm.playCloneIfAllowed(p1, g2, GameDeck.PileLocation.LEFT), "Can't clone during a battle.");
		assertTrue(gm.drawCardsIfAllowed(p1));
		assertEquals(p1.countCards(), 8 + 4);
		assertNull(gm.playCloneIfAllowed(p1, g2, GameDeck.PileLocation.LEFT));
		assertTrue(gm.drawCardsIfAllowed(p1));
		assertEquals(p1.countCards(), 12 + 1);
		
		// shooting star
		assertEquals(gm.playCardIfAllowed(p1, wSS, GameDeck.PileLocation.RIGHT, Color.RED), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, wSS, GameDeck.PileLocation.RIGHT, Color.RED), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, wSS, GameDeck.PileLocation.RIGHT, Color.RED), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, rSS, GameDeck.PileLocation.RIGHT), DEAD_PILE);
		assertNull(gm.playCardIfAllowed(p2, wSS, GameDeck.PileLocation.RIGHT, Color.RED));
		assertEquals(gm.playCardIfAllowed(p1, rSS, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, rSS, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, rSS, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, rSS, GameDeck.PileLocation.LEFT), BATTLE_LIVE_PILE);
		assertNull(gm.playCardIfAllowed(p4, rSS, GameDeck.PileLocation.RIGHT));
		assertEquals(gm.playCardIfAllowed(p1, rBH, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, rBH, GameDeck.PileLocation.RIGHT), "Can only play SHOOTING STAR or RED FORCE FIELD on RED SHOOTING STAR attack.");
		assertEquals(gm.playCardIfAllowed(p3, rBH, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, rBH, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p1, wBH, GameDeck.PileLocation.RIGHT, Color.RED), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, wBH, GameDeck.PileLocation.RIGHT, Color.RED), "Can only play SHOOTING STAR or RED FORCE FIELD on RED SHOOTING STAR attack.");
		assertEquals(gm.playCardIfAllowed(p3, wBH, GameDeck.PileLocation.RIGHT, Color.RED), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, wBH, GameDeck.PileLocation.RIGHT, Color.RED), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, wSS,  GameDeck.PileLocation.LEFT, Color.BLUE), BATTLE_LIVE_PILE);
		assertNull(gm.playCardIfAllowed(p2, wSS,  GameDeck.PileLocation.RIGHT, Color.BLUE));
		assertEquals(gm.playCardIfAllowed(p1, wFF, GameDeck.PileLocation.RIGHT, Color.GREEN), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, wFF, GameDeck.PileLocation.RIGHT, Color.GREEN), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, wFF, GameDeck.PileLocation.RIGHT, Color.GREEN), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.LEFT, Color.GREEN), BATTLE_LIVE_PILE);
		assertNull(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.RIGHT, Color.GREEN));
		assertEquals(gm.playCardIfAllowed(p1, rFF, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, rFF, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, rFF, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, rFF, GameDeck.PileLocation.LEFT), BATTLE_LIVE_PILE);
		assertNull(gm.playCardIfAllowed(p2, rFF, GameDeck.PileLocation.RIGHT));
		assertEquals(gm.playCardIfAllowed(p1, wSS, GameDeck.PileLocation.RIGHT, Color.RED), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, wSS, GameDeck.PileLocation.RIGHT, Color.RED), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, wSS, GameDeck.PileLocation.RIGHT, Color.RED), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, wSS, GameDeck.PileLocation.RIGHT, Color.RED), FORCE_FIELD);
		assertEquals(gm.playCardIfAllowed(p1, rSS, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, rSS, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, rSS, GameDeck.PileLocation.RIGHT), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, rSS, GameDeck.PileLocation.RIGHT), FORCE_FIELD);
		assertNull(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.RIGHT, Color.YELLOW));
		assertFalse(gm.drawCardsIfAllowed(p1));
		assertFalse(gm.drawCardsIfAllowed(p3));
		assertFalse(gm.drawCardsIfAllowed(p4));
		assertTrue(gm.drawCardsIfAllowed(p2));
		assertEquals(p2.countCards(), 10 + 6);
		assertFalse(gm.drawCardsIfAllowed(p2));
		assertFalse(gm.drawCardsIfAllowed(p3));
		assertFalse(gm.drawCardsIfAllowed(p4));
		assertTrue(gm.drawCardsIfAllowed(p1));
		assertEquals(p1.countCards(), 13 + 1);
		assertTrue(gm.drawCardsIfAllowed(p4));
		assertEquals(p4.countCards(), 8 + 1);
		
		// big bang
		assertEquals(gm.playCardIfAllowed(p1, rbBB, GameDeck.PileLocation.LEFT), "Playing piles do not have BLUE AND RED BIG BANG colors.");
		assertEquals(gm.playCardIfAllowed(p1, rbBB, GameDeck.PileLocation.RIGHT), "Playing piles do not have BLUE AND RED BIG BANG colors.");
		assertNull(gm.playCardIfAllowed(p1, gyBB, GameDeck.PileLocation.LEFT));
		assertEquals(gm.playCardIfAllowed(p1, rbBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p1, gyBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p2, rbBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p2, gyBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p3, rbBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p3, gyBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p4, rbBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p4, gyBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p4, g5, GameDeck.PileLocation.LEFT), BIG_BANG);
		assertEquals(gm.playCardIfAllowed(p4, b1, GameDeck.PileLocation.LEFT), BIG_BANG);
		assertEquals(gm.playCardIfAllowed(p4, rFF, GameDeck.PileLocation.LEFT), BIG_BANG);
		assertEquals(gm.playCardIfAllowed(p4, wBH, GameDeck.PileLocation.LEFT, Color.YELLOW), BIG_BANG);
		assertEquals(gm.playCardIfAllowed(p4, yA, GameDeck.PileLocation.LEFT), BIG_BANG);
		assertEquals(gm.playCardIfAllowed(p1, wFF, GameDeck.PileLocation.LEFT, Color.BLUE), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p1, wFF, GameDeck.PileLocation.LEFT, Color.RED), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, wFF, GameDeck.PileLocation.LEFT, Color.YELLOW), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, wFF, GameDeck.PileLocation.LEFT, Color.GREEN), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, wFF, GameDeck.PileLocation.LEFT, Color.BLUE), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.RIGHT, Color.BLUE), BATTLE_LIVE_PILE);
		assertNull(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.LEFT, Color.BLUE));
		assertEquals(gm.playCardIfAllowed(p1, rbBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p1, gyBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p2, rbBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p2, gyBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p3, rbBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p3, gyBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p4, rbBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p4, gyBB, GameDeck.PileLocation.LEFT), NO_BATTLE);
		assertEquals(gm.playCardIfAllowed(p1, g5, GameDeck.PileLocation.LEFT), BIG_BANG);
		assertEquals(gm.playCardIfAllowed(p1, b1, GameDeck.PileLocation.LEFT), BIG_BANG);
		assertEquals(gm.playCardIfAllowed(p1, rFF, GameDeck.PileLocation.LEFT), BIG_BANG);
		assertEquals(gm.playCardIfAllowed(p1, wBH, GameDeck.PileLocation.LEFT, Color.YELLOW), BIG_BANG);
		assertEquals(gm.playCardIfAllowed(p1, yA, GameDeck.PileLocation.LEFT), BIG_BANG);
		assertFalse(gm.drawCardsIfAllowed(p2));
		assertFalse(gm.drawCardsIfAllowed(p3));
		assertFalse(gm.drawCardsIfAllowed(p4));
		assertTrue(gm.drawCardsIfAllowed(p1));
		assertEquals(p1.countCards(), 14 + 3);		
		assertEquals(gm.playCardIfAllowed(p1, wFF, GameDeck.PileLocation.LEFT, Color.BLUE), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, wFF, GameDeck.PileLocation.LEFT, Color.BLUE), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.LEFT, Color.BLUE), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, wFF, GameDeck.PileLocation.RIGHT, Color.BLUE), BATTLE_LIVE_PILE);
		assertNull(gm.playCardIfAllowed(p2, wFF, GameDeck.PileLocation.LEFT, Color.BLUE));
		assertEquals(p2.countCards(), 16 - 1);
		assertFalse(gm.drawCardsIfAllowed(p1));
		assertFalse(gm.drawCardsIfAllowed(p2));
		assertFalse(gm.drawCardsIfAllowed(p3));
		assertTrue(gm.drawCardsIfAllowed(p4));
		assertEquals(p4.countCards(), 9 + 3);
		assertEquals(gm.playCardIfAllowed(p1, wFF, GameDeck.PileLocation.LEFT, Color.RED), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p2, wFF, GameDeck.PileLocation.LEFT, Color.RED), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.LEFT, Color.RED), NOT_TURN);
		assertEquals(gm.playCardIfAllowed(p3, wFF, GameDeck.PileLocation.RIGHT, Color.RED), BATTLE_LIVE_PILE);
		assertNull(gm.playCardIfAllowed(p3, wFF, GameDeck.PileLocation.LEFT, Color.RED));
		assertFalse(gm.drawCardsIfAllowed(p1));
		assertFalse(gm.drawCardsIfAllowed(p3));
		assertFalse(gm.drawCardsIfAllowed(p4));
		assertTrue(gm.drawCardsIfAllowed(p2));
		assertEquals(p2.countCards(), 15 + 3);
		assertTrue(gm.drawCardsIfAllowed(p3));
		assertEquals(p3.countCards(), 8 + 1);
		
		// other
		assertNull(gm.playCardIfAllowed(p4, bFF, GameDeck.PileLocation.RIGHT));
		assertNull(gm.playCardIfAllowed(p3, b4, GameDeck.PileLocation.RIGHT));
		assertNull(gm.playCardIfAllowed(p2, b1, GameDeck.PileLocation.RIGHT));
		assertNull(gm.playCardIfAllowed(p1, bFF, GameDeck.PileLocation.LEFT));
		assertEquals(gm.playCardIfAllowed(p2, b10, GameDeck.PileLocation.RIGHT), DEAD_PILE);
		assertNull(gm.playCardIfAllowed(p2, b10, GameDeck.PileLocation.LEFT));
		assertNull(gm.playCardIfAllowed(p3, r10, GameDeck.PileLocation.LEFT));
		assertEquals(gm.playCardIfAllowed(p4, b4, GameDeck.PileLocation.LEFT), LIVE_PILE);
		
		// end round tests
		do {
			for (Player player : players)
				player.discardAll();
			gm.newGame(players, 2);
		} while (gm.playCardIfAllowed(p1, wBH, GameDeck.PileLocation.RIGHT, Color.BLUE) != null);
			
		assertNull(gm.playCardIfAllowed(p1, rBH, GameDeck.PileLocation.RIGHT));
		assertNull(gm.playCardIfAllowed(p1, wBH, GameDeck.PileLocation.LEFT, Color.RED));
		p1.discardAll();
		assertEquals(gm.getScoreKeeper().getNumberOfRoundsFinished(), 0);
		assertNull(gm.playCardIfAllowed(p1, r3, GameDeck.PileLocation.LEFT));
		int round = 1;
		assertEquals(gm.getScoreKeeper().getNumberOfRoundsFinished(), round);
		assertEquals(gm.getScoreKeeper().getPlayerRoundScore(p1, round), 3 + 30);
		gm.beginRound();
		for (Player player : players) {
			assertEquals(player.countCards(), 6);
			assertTrue(gm.getScoreKeeper().getPlayerRoundScore(player, round) > 0);
		}
		
		GM.setDealNumber(5);
		assertNull(gm.playCardIfAllowed(p2, wBH, GameDeck.PileLocation.LEFT, Color.GREEN));
		assertNull(gm.playCardIfAllowed(p2, g8, GameDeck.PileLocation.LEFT));
		assertNull(gm.playCardIfAllowed(p3, wFF, GameDeck.PileLocation.RIGHT, Color.YELLOW));
		p4.discardAll();
		assertNull(gm.playCloneIfAllowed(p4, g8, GameDeck.PileLocation.LEFT));
		round++;
		assertEquals(gm.getScoreKeeper().getNumberOfRoundsFinished(), round);
		assertEquals(gm.getScoreKeeper().getPlayerRoundScore(p4, round), 8 + 100);
		gm.beginRound();
		for (Player player : players) {
			assertEquals(player.countCards(), 5);
			assertTrue(gm.getScoreKeeper().getPlayerRoundScore(player, round) > 0);
		}
		
		GM.setDealNumber(3);
		assertNull(gm.playCardIfAllowed(p3, wFF, GameDeck.PileLocation.RIGHT, Color.GREEN));
		assertNull(gm.playCardIfAllowed(p2, g8, GameDeck.PileLocation.RIGHT));
		p1.discardAll();
		assertNull(gm.playCardIfAllowed(p1, wBH, GameDeck.PileLocation.LEFT, Color.RED));
		round++;
		assertEquals(gm.getScoreKeeper().getNumberOfRoundsFinished(), round);
		assertEquals(gm.getScoreKeeper().getPlayerRoundScore(p1, round), 8 + 50);
		gm.beginRound();
		for (Player player : players) {
			assertEquals(player.countCards(), 3);
			assertTrue(gm.getScoreKeeper().getPlayerRoundScore(player, round) > 0);
		}
		
		GM.setDealNumber(1);
		assertNull(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.LEFT, Color.BLUE));
		assertNull(gm.playCardIfAllowed(p3, b1, GameDeck.PileLocation.LEFT));
		assertNull(gm.playCardIfAllowed(p2, wBH, GameDeck.PileLocation.RIGHT, Color.RED));
		assertNull(gm.playCardIfAllowed(p2, rbBB, GameDeck.PileLocation.RIGHT));
		assertTrue(gm.drawCardsIfAllowed(p1));
		assertEquals(p1.countCards(), 3 + 3);
		p4.discardAll();
		assertNull(gm.playCardIfAllowed(p4, wFF, GameDeck.PileLocation.RIGHT, Color.RED));
		round++;
		assertEquals(gm.getScoreKeeper().getNumberOfRoundsFinished(), round);
		assertEquals(gm.getScoreKeeper().getPlayerRoundScore(p4, round), 1 + 100);
		gm.beginRound();
		for (Player player : players) {
			assertEquals(player.countCards(), 1);
			assertTrue(gm.getScoreKeeper().getPlayerRoundScore(player, round) > 0);
		}
		
		GM.setDealNumber(10);
		LinkedList<Card> cards = new LinkedList<Card>();
		cards.add(wBH);
		cards.add(rBH);
		p1.addCards(cards);
		assertNull(gm.playCardIfAllowed(p1, wBH, GameDeck.PileLocation.RIGHT, Color.YELLOW));
		assertNull(gm.playCardIfAllowed(p1, rBH, GameDeck.PileLocation.RIGHT));
		assertTrue(gm.drawCardsIfAllowed(p1));
		p2.discardAll();
		assertNull(gm.playCardIfAllowed(p2, wSS, GameDeck.PileLocation.LEFT, Color.YELLOW));
		round++;
		assertEquals(gm.getScoreKeeper().getNumberOfRoundsFinished(), round);
		assertEquals(gm.getScoreKeeper().getPlayerRoundScore(p2, round), 30 + 30);
		gm.beginRound();
		for (Player player : players) {
			assertEquals(player.countCards(), 10);
			assertTrue(gm.getScoreKeeper().getPlayerRoundScore(player, round) > 0);
		}
		
		assertNull(gm.playCardIfAllowed(p2, wFF, GameDeck.PileLocation.RIGHT, Color.BLUE));
		
		GM.setDealNumber(6);
	}
}
