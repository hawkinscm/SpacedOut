package model;

import java.util.LinkedList;

import unit.UnitTest;

/**
 * Unit tester for the Player class.
 */
public class PlayerTest extends UnitTest {

	public int testAll() {
		testPlayerType();
		testConstructor();
		testAddRemoveCards();
		testDiscardCards();
		testCardScore();
		
		return errorCount;
	}
	
	private void testPlayerType() {
		for (PlayerType type : PlayerType.values()) {
			switch (type) {
			case HOST :
				assertEquals(type.toString(), "HOST");
				break;
			case NETWORK :
				assertEquals(type.toString(), "NETWORK");
				break;
			case COMPUTER_EASY :
				assertEquals(type.toString(), "EASY COM");
				break;
			case COMPUTER_MEDIUM :
				assertEquals(type.toString(), "MEDIUM COM");
				break;
			case COMPUTER_HARD :
				assertEquals(type.toString(), "HARD COM");
				break;
			default :
				fail("Unhandled Player Type");
			}
		}
	}
	
	private void testConstructor() {
		String name = "name";
		Player player = new Player(name, PlayerType.HOST);
		assertEquals(name, player.getName());
		assertEquals(name, player.toString());
		assertEquals(player.countCards(), 0);
		assertEquals(player.getCards().length, 0);
		assertTrue(player.getPlayerType() == PlayerType.HOST);
		
		name = "New name";
		player = new Player(name, PlayerType.COMPUTER_EASY);
		assertEquals(name, player.getName());
		assertEquals(name, player.toString());
		assertEquals(player.countCards(), 0);
		assertEquals(player.getCards().length, 0);
		assertTrue(player.getPlayerType() == PlayerType.COMPUTER_EASY);
		
		name = "";
		player = new Player(name, PlayerType.COMPUTER_MEDIUM);
		assertEquals(name, player.getName());
		assertEquals(name, player.toString());
		assertEquals(player.countCards(), 0);
		assertEquals(player.getCards().length, 0);
		assertTrue(player.getPlayerType() == PlayerType.COMPUTER_MEDIUM);
		
		name = "COM";
		player = new Player(name, PlayerType.COMPUTER_HARD);
		assertEquals(name, player.getName());
		assertEquals(name, player.toString());
		assertEquals(player.countCards(), 0);
		assertEquals(player.getCards().length, 0);
		assertTrue(player.getPlayerType() == PlayerType.COMPUTER_HARD);
	}
	
	private void testAddRemoveCards() {
		Player player = new Player("name", PlayerType.HOST);
		assertEquals(player.countCards(), 0);
		LinkedList<Card> cards = new LinkedList<Card>();
		player.addCards(cards);
		assertEquals(player.countCards(), 0);
		Card a = new Card(Card.Design.ASTEROIDS, Card.Color.BLUE, 20);
		cards.add(a);
		player.addCards(cards);
		assertEquals(player.countCards(), 1);
		cards.clear();
		Card ss = new Card(Card.Design.SHOOTING_STAR, Card.Color.BLUE, 20);
		Card bb = new Card(Card.Design.BIG_BANG, Card.Color.GREEN_AND_YELLOW, 100);
		Card bh = new Card(Card.Design.BLACK_HOLE, Card.Color.RED, 30);
		Card ff = new Card(Card.Design.FORCE_FIELD, Card.Color.WILD, 100);
		Card n = new Card(Card.Design.NUMBER, Card.Color.YELLOW, 9);
		cards.add(ss);
		cards.add(bb);
		cards.add(bh);
		cards.add(ff);
		cards.add(n);
		player.addCards(cards);
		assertEquals(player.countCards(), 6);
		Card[] cardArray = player.getCards();
		assert(cardArray[0] == a);
		assert(cardArray[1] == ss);
		assert(cardArray[2] == bb);
		assert(cardArray[3] == bh);
		assert(cardArray[4] == ff);
		assert(cardArray[5] == n);
		
		player.discard(ss);
		player.discard(bh);
		assertEquals(player.countCards(), 4);
		cardArray = player.getCards();
		assert(cardArray[0] == a);
		assert(cardArray[1] == bb);
		assert(cardArray[2] == ff);
		assert(cardArray[3] == n);
		
		player.discard(a);
		player.discard(n);
		assertEquals(player.countCards(), 2);
		cardArray = player.getCards();
		assert(cardArray[0] == bb);
		assert(cardArray[1] == ff);
		
		player.discard(bb);
		player.discard(ff);
		assertEquals(player.countCards(), 0);
	}
		
	private void testDiscardCards() {
		Player player = new Player("name", PlayerType.HOST);
		assertEquals(player.countCards(), 0);
		LinkedList<Card> cards = new LinkedList<Card>();
		Card ss = new Card(Card.Design.SHOOTING_STAR, Card.Color.WILD, 30);
		Card bb = new Card(Card.Design.BIG_BANG, Card.Color.BLUE_AND_RED, 100);
		Card a = new Card(Card.Design.ASTEROIDS, Card.Color.BLUE, 20);
		Card bh = new Card(Card.Design.BLACK_HOLE, Card.Color.WILD, 50);
		Card ff = new Card(Card.Design.FORCE_FIELD, Card.Color.GREEN, 20);
		Card n = new Card(Card.Design.NUMBER, Card.Color.BLUE, 2);
		cards.add(ss);
		cards.add(bb);
		cards.add(a);
		cards.add(bh);
		cards.add(ff);
		cards.add(n);
		player.addCards(cards);		
		assertEquals(player.countCards(), 6);
		player.discardAll();
		assertEquals(player.countCards(), 0);
	}
	
	private void testCardScore() {
		Player player = new Player("name", PlayerType.HOST);
		assertEquals(player.getCardScore(), 0);
		LinkedList<Card> cards = new LinkedList<Card>();
		cards.add(new Card(Card.Design.NUMBER, Card.Color.BLUE, 2));
		cards.add(new Card(Card.Design.SHOOTING_STAR, Card.Color.WILD, 30));
		cards.add(new Card(Card.Design.BIG_BANG, Card.Color.BLUE_AND_RED, 100));
		cards.add(new Card(Card.Design.ASTEROIDS, Card.Color.BLUE, 20));
		cards.add(new Card(Card.Design.BLACK_HOLE, Card.Color.WILD, 50));
		cards.add(new Card(Card.Design.FORCE_FIELD, Card.Color.GREEN, 20));
		cards.add(new Card(Card.Design.NUMBER, Card.Color.RED, 7));
		cards.add(new Card(Card.Design.NUMBER, Card.Color.YELLOW, 5));
		player.addCards(cards);	
		
		assertEquals(player.getCardScore(), 2 + 30 + 100 + 20 + 50 + 20 + 7 + 5);
	}
}
