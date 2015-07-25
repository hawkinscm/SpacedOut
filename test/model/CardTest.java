package model;

import model.Card.Design;
import model.Card.Color;

import unit.UnitTest;

/**
 * Unit tester for the Spaced Out card class.
 */
public class CardTest extends UnitTest {

	public int testAll() {
		testConstructor();
		testClone();
		testMatchDesign();
		
		return errorCount;
	}
		
	private void testConstructor() {
		Card card = new Card(Design.NUMBER, Color.BLUE, 7);
		assertTrue(card.getDesign() == Design.NUMBER);
		assertTrue(card.getColor() == Color.BLUE);
		assertEquals(card.getPointValue(), 7);
		assertEquals(card.toString(), "BLUE_7");
		
		card = new Card(Design.SHOOTING_STAR, Color.GREEN, 20);
		assertTrue(card.getDesign() == Design.SHOOTING_STAR);
		assertTrue(card.getColor() == Color.GREEN);
		assertEquals(card.getPointValue(), 20);
		assertEquals(card.toString(), "GREEN_SHOOTING_STAR");
		
		card = new Card(Design.BIG_BANG, Color.BLUE_AND_RED, 100);
		assertTrue(card.getDesign() == Design.BIG_BANG);
		assertTrue(card.getColor() == Color.BLUE_AND_RED);
		assertEquals(card.getPointValue(), 100);
		assertEquals(card.toString(), "BLUE_AND_RED_BIG_BANG");
		
		card = new Card(Design.BLACK_HOLE, Color.WILD, 50);
		assertTrue(card.getDesign() == Design.BLACK_HOLE);
		assertTrue(card.getColor() == Color.WILD);
		assertEquals(card.getPointValue(), 50);
		assertEquals(card.toString(), "WILD_BLACK_HOLE");
		
		card = new Card(Design.FORCE_FIELD, Color.GREEN_AND_YELLOW, 60);
		assertTrue(card.getDesign() == Design.FORCE_FIELD);
		assertTrue(card.getColor() == Color.GREEN_AND_YELLOW);
		assertEquals(card.getPointValue(), 60);
		assertEquals(card.toString(), "GREEN_AND_YELLOW_FORCE_FIELD");
		
		card = new Card(Design.ASTEROIDS, Color.WILD, -5);
		assertTrue(card.getDesign() == Design.ASTEROIDS);
		assertTrue(card.getColor() == Color.WILD);
		assertEquals(card.getPointValue(), -5);
		assertEquals(card.toString(), "WILD_ASTEROIDS");
		
		card = new Card(Design.NUMBER, Color.YELLOW, 1390123);
		assertTrue(card.getDesign() == Design.NUMBER);
		assertTrue(card.getColor() == Color.YELLOW);
		assertEquals(card.getPointValue(), 1390123);
		assertEquals(card.toString(), "YELLOW_1390123");
		
		card = new Card(null, Color.RED, 34);
		assertNull(card.getDesign());
		assertTrue(card.getColor() == Color.RED);
		assertEquals(card.getPointValue(), 34);
		assertEquals(card.toString(), "RED_null");
		
		card = new Card(null, null, 0);
		assertNull(card.getDesign());
		assertNull(card.getColor());
		assertEquals(card.getPointValue(), 0);
		assertEquals(card.toString(), "null_null");
	}
		
	private void testClone() {
		Card card = new Card(Design.NUMBER, Color.RED, 3);
		assertTrue(card.isClone(card));
		Card newCard = new Card(Design.NUMBER, Color.RED, 3);
		assertTrue(card.isClone(newCard));
		assertTrue(newCard.isClone(card));
		newCard = new Card(Design.NUMBER, Color.BLUE, 3);
		assertFalse(newCard.isClone(card));
		newCard = new Card(Design.NUMBER, Color.WILD, 3);
		assertFalse(newCard.isClone(card));
		newCard = new Card(Design.NUMBER, Color.BLUE_AND_RED, 3);
		assertFalse(card.isClone(newCard));
		newCard = new Card(Design.NUMBER, Color.RED, 2);
		assertFalse(newCard.isClone(card));
		newCard = new Card(Design.NUMBER, Color.RED, -3);
		assertFalse(card.isClone(newCard));
		newCard = new Card(Design.NUMBER, Color.RED, 4);
		assertFalse(card.isClone(newCard));
		newCard = new Card(Design.ASTEROIDS, Color.RED, 3);
		assertFalse(card.isClone(newCard));
		assertFalse(newCard.isClone(card));
		
		card = new Card(Design.BIG_BANG, Color.RED, 3);
		assertFalse(card.isClone(card));
		newCard = new Card(Design.BIG_BANG, Color.RED, 3);
		assertFalse(card.isClone(newCard));
		
		card = new Card(Design.ASTEROIDS, Color.RED, 3);
		assertFalse(card.isClone(card));
		newCard = new Card(Design.ASTEROIDS, Color.RED, 3);
		assertFalse(card.isClone(newCard));
	}
	
	private void testMatchDesign() {
		Card card = new Card(Design.NUMBER, Color.RED, 3);
		assertTrue(card.matchesDesign(card));
		Card newCard = new Card(Design.NUMBER, Color.RED, 3);
		assertTrue(card.matchesDesign(newCard));
		assertTrue(newCard.matchesDesign(card));
		newCard = new Card(Design.NUMBER, Color.BLUE, 3);
		assertTrue(newCard.matchesDesign(card));
		newCard = new Card(Design.NUMBER, Color.WILD, 3);
		assertTrue(newCard.matchesDesign(card));
		newCard = new Card(Design.NUMBER, Color.BLUE_AND_RED, 3);
		assertTrue(card.matchesDesign(newCard));
		newCard = new Card(Design.NUMBER, Color.RED, 2);
		assertFalse(newCard.matchesDesign(card));
		newCard = new Card(Design.NUMBER, Color.RED, -3);
		assertFalse(card.matchesDesign(newCard));
		newCard = new Card(Design.NUMBER, Color.RED, 4);
		assertFalse(card.matchesDesign(newCard));
		newCard = new Card(Design.ASTEROIDS, Color.RED, 3);
		assertFalse(card.matchesDesign(newCard));
		assertFalse(newCard.matchesDesign(card));
		
		card = new Card(Design.BIG_BANG, Color.RED, 3);
		assertTrue(card.matchesDesign(card));
		newCard = new Card(Design.BIG_BANG, Color.RED, 3);
		assertTrue(card.matchesDesign(newCard));
		card = new Card(Design.BIG_BANG, Color.GREEN_AND_YELLOW, 100);
		assertTrue(card.matchesDesign(card));
		newCard = new Card(Design.BIG_BANG, Color.RED, 50);
		assertTrue(card.matchesDesign(newCard));
		
		card = new Card(Design.ASTEROIDS, Color.RED, 50);
		assertFalse(card.matchesDesign(newCard));
		assertTrue(card.matchesDesign(card));
		newCard = new Card(Design.ASTEROIDS, Color.BLUE, 20);
		assertTrue(card.matchesDesign(newCard));
		
		card = new Card(Design.FORCE_FIELD, Color.BLUE, 20);
		assertFalse(card.matchesDesign(newCard));
		assertTrue(card.matchesDesign(card));
		newCard = new Card(Design.FORCE_FIELD, Color.WILD, 100);
		assertTrue(card.matchesDesign(newCard));
	}
}
