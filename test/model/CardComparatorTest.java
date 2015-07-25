package model;

import java.util.Iterator;
import java.util.NoSuchElementException;

import model.Card.Color;

import unit.UnitTest;

public class CardComparatorTest extends UnitTest {
	private SortedLinkedList<Card> cards;
	
	public int testAll() {
		initialize();
		
		testColorAscending();
		testColorDescending();
		testDesignAscending();
		testDesignDescending();
		testPointValueAscending();
		testPointValueDescending();

		return errorCount;
	}
	
	private void initialize() {
		cards = new SortedLinkedList<Card>(new CardComparator(CardComparator.CardTypeSort.COLOR, false));
		cards.addAll(SpacedOutDeckFactory.buildDeck());
	}

	private void testColorAscending() {
		CardComparator comparator = new CardComparator(CardComparator.CardTypeSort.COLOR, true);
		cards.setComparator(comparator);
		Iterator<Card> iterator = cards.iterator();
		
		try {
			// for each 4 colors
			for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
				Card.Color color = Card.Color.values()[colorIndex];
				// Number cards
				for (int number = 1; number <= 10; number++) {
					// 2 of each
					for (int duplicate = 1; duplicate <= 2; duplicate++) {
						Card card = iterator.next();
						assertEquals(card.getDesign(), Card.Design.NUMBER);
						assertEquals(card.getColor(), color);
						assertEquals(card.getPointValue(), number);
					}
				}
				
				// Asteroid cards
				// 2 of each
				for (int duplicate = 1; duplicate <= 2; duplicate++) {
					Card card = iterator.next();
					assertEquals(card.getDesign(), Card.Design.ASTEROIDS);
					assertEquals(card.getColor(), color);
				}
				
				// Shooting Star cards
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.SHOOTING_STAR);
				assertEquals(card.getColor(), color);
				
				// Black Hole cards
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.BLACK_HOLE);
				assertEquals(card.getColor(), color);
				
				// Force Field cards
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.FORCE_FIELD);
				assertEquals(card.getColor(), color);
			}
			
			// Big Bang cards
			Card card = iterator.next();
			assertEquals(card.getDesign(), Card.Design.BIG_BANG);
			assertEquals(card.getColor(), Color.BLUE_AND_RED);
			card = iterator.next();
			assertEquals(card.getDesign(), Card.Design.BIG_BANG);
			assertEquals(card.getColor(), Color.GREEN_AND_YELLOW);
				
			// Wild Shooting Star cards
			// 2 of each
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.SHOOTING_STAR);
				assertEquals(card.getColor(), Color.WILD);
			}
			
			// Wild Black Hole cards
			// 2 of each
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.BLACK_HOLE);
				assertEquals(card.getColor(), Color.WILD);
			}
			
			// Super Force Field cards
			// 2 of each
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.FORCE_FIELD);
				assertEquals(card.getColor(), Color.WILD);
			}
			
			assertFalse(iterator.hasNext());
		}
		catch (NoSuchElementException ex) {
			fail("Expected more cards in the sorted linked list iterator.");
		}
	}
	
	private void testColorDescending() {
		CardComparator comparator = new CardComparator(CardComparator.CardTypeSort.COLOR, false);
		cards.setComparator(comparator);
		Iterator<Card> iterator = cards.iterator();
		
		try {
			// Super Force Field cards
			// 2 of each
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.FORCE_FIELD);
				assertEquals(card.getColor(), Color.WILD);
			}
			
			// Wild Black Hole cards
			// 2 of each
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.BLACK_HOLE);
				assertEquals(card.getColor(), Color.WILD);
			}
				
			// Wild Shooting Star cards
			// 2 of each
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.SHOOTING_STAR);
				assertEquals(card.getColor(), Color.WILD);
			}
			
			// Big Bang cards
			Card card = iterator.next();
			assertEquals(card.getDesign(), Card.Design.BIG_BANG);
			assertEquals(card.getColor(), Color.GREEN_AND_YELLOW);
			card = iterator.next();
			assertEquals(card.getDesign(), Card.Design.BIG_BANG);
			assertEquals(card.getColor(), Color.BLUE_AND_RED);
			
			// for each 4 colors
			for (int colorIndex = 3; colorIndex >= 0; colorIndex--) {
				Card.Color color = Card.Color.values()[colorIndex];
				
				// Force Field cards
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.FORCE_FIELD);
				assertEquals(card.getColor(), color);
				
				// Black Hole cards
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.BLACK_HOLE);
				assertEquals(card.getColor(), color);
				
				// Shooting Star cards
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.SHOOTING_STAR);
				assertEquals(card.getColor(), color);
				
				// Asteroid cards
				// 2 of each
				for (int duplicate = 1; duplicate <= 2; duplicate++) {
					card = iterator.next();
					assertEquals(card.getDesign(), Card.Design.ASTEROIDS);
					assertEquals(card.getColor(), color);
				}
				
				// Number cards
				for (int number = 10; number >= 1; number--) {
					// 2 of each
					for (int duplicate = 1; duplicate <= 2; duplicate++) {
						card = iterator.next();
						assertEquals(card.getDesign(), Card.Design.NUMBER);
						assertEquals(card.getColor(), color);
						assertEquals(card.getPointValue(), number);
					}
				}
			}
			
			assertFalse(iterator.hasNext());
		}
		catch (NoSuchElementException ex) {
			fail("Expected more cards in the sorted linked list iterator.");
		}
	}
	
	private void testDesignAscending() {
		CardComparator comparator = new CardComparator(CardComparator.CardTypeSort.DESIGN, true);
		cards.setComparator(comparator);
		Iterator<Card> iterator = cards.iterator();
		
		try {
			// Number cards
			// for each 4 colors		
			for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
				Card.Color color = Card.Color.values()[colorIndex];
				for (int number = 1; number <= 10; number++) {				
					// 2 of each
					for (int duplicate = 1; duplicate <= 2; duplicate++) {
						Card card = iterator.next();
						assertEquals(card.getDesign(), Card.Design.NUMBER);
						assertEquals(card.getColor(), color);
						assertEquals(card.getPointValue(), number);
					}
				}
			}				
				
			// Asteroid cards
			// for each 4 colors
			for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
				Card.Color color = Card.Color.values()[colorIndex];
				// 2 of each
				for (int duplicate = 1; duplicate <= 2; duplicate++) {
					Card card = iterator.next();
					assertEquals(card.getDesign(), Card.Design.ASTEROIDS);
					assertEquals(card.getColor(), color);
				}
			}
				
			// Shooting Star cards
			// for each 4 colors
			for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.SHOOTING_STAR);
				assertEquals(card.getColor(), Card.Color.values()[colorIndex]);
			}
			// 2 of each wild
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.SHOOTING_STAR);
				assertEquals(card.getColor(), Card.Color.WILD);
			}
				
			// Black Hole cards
			// for each 4 colors
			for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.BLACK_HOLE);
				assertEquals(card.getColor(), Card.Color.values()[colorIndex]);
			}
			// 2 of each wild
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.BLACK_HOLE);
				assertEquals(card.getColor(), Card.Color.WILD);
			}
			
			// Big Bang cards
			Card card = iterator.next();
			assertEquals(card.getDesign(), Card.Design.BIG_BANG);
			assertEquals(card.getColor(), Color.BLUE_AND_RED);
			card = iterator.next();
			assertEquals(card.getDesign(), Card.Design.BIG_BANG);
			assertEquals(card.getColor(), Color.GREEN_AND_YELLOW);
				
			// Force Field cards
			// for each 4 colors
			for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.FORCE_FIELD);
				assertEquals(card.getColor(), Card.Color.values()[colorIndex]);
			}
			// 2 of each super
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.FORCE_FIELD);
				assertEquals(card.getColor(), Card.Color.WILD);
			}
						
			assertFalse(iterator.hasNext());
		}
		catch (NoSuchElementException ex) {
			fail("Expected more cards in the sorted linked list iterator.");
		}
	}
	
	private void testDesignDescending() {
		CardComparator comparator = new CardComparator(CardComparator.CardTypeSort.DESIGN, false);
		cards.setComparator(comparator);
		Iterator<Card> iterator = cards.iterator();
		
		try {			
			// Force Field cards
			// 2 of each super
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.FORCE_FIELD);
				assertEquals(card.getColor(), Card.Color.WILD);
			}
			// for each 4 colors
			for (int colorIndex = 3; colorIndex >= 0; colorIndex--) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.FORCE_FIELD);
				assertEquals(card.getColor(), Card.Color.values()[colorIndex]);
			}
			
			// Big Bang cards
			Card card = iterator.next();
			assertEquals(card.getDesign(), Card.Design.BIG_BANG);
			assertEquals(card.getColor(), Color.GREEN_AND_YELLOW);
			card = iterator.next();
			assertEquals(card.getDesign(), Card.Design.BIG_BANG);
			assertEquals(card.getColor(), Color.BLUE_AND_RED);
				
			// Black Hole cards
			// 2 of each wild
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.BLACK_HOLE);
				assertEquals(card.getColor(), Card.Color.WILD);
			}
			// for each 4 colors
			for (int colorIndex = 3; colorIndex >= 0; colorIndex--) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.BLACK_HOLE);
				assertEquals(card.getColor(), Card.Color.values()[colorIndex]);
			}
				
			// Shooting Star cards
			// 2 of each wild
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.SHOOTING_STAR);
				assertEquals(card.getColor(), Card.Color.WILD);
			}
			// for each 4 colors
			for (int colorIndex = 3; colorIndex >= 0; colorIndex--) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.SHOOTING_STAR);
				assertEquals(card.getColor(), Card.Color.values()[colorIndex]);
			}				
				
			// Asteroid cards
			// for each 4 colors
			for (int colorIndex = 3; colorIndex >= 0; colorIndex--) {
				Card.Color color = Card.Color.values()[colorIndex];
				// 2 of each
				for (int duplicate = 1; duplicate <= 2; duplicate++) {
					card = iterator.next();
					assertEquals(card.getDesign(), Card.Design.ASTEROIDS);
					assertEquals(card.getColor(), color);
				}
			}
			
			// Number cards
			// for each 4 colors		
			for (int colorIndex = 3; colorIndex >= 0; colorIndex--) {
				Card.Color color = Card.Color.values()[colorIndex];
				for (int number = 10; number >= 1; number--) {
					// 2 of each
					for (int duplicate = 1; duplicate <= 2; duplicate++) {
						card = iterator.next();
						assertEquals(card.getDesign(), Card.Design.NUMBER);
						assertEquals(card.getColor(), color);
						assertEquals(card.getPointValue(), number);
					}
				}
			}
			
			assertFalse(iterator.hasNext());
		}
		catch (NoSuchElementException ex) {
			fail("Expected more cards in the sorted linked list iterator.");
		}
	}
	
	private void testPointValueAscending() {
		CardComparator comparator = new CardComparator(CardComparator.CardTypeSort.POINT_VALUE, true);
		cards.setComparator(comparator);
		Iterator<Card> iterator = cards.iterator();
		
		try {
			// Number cards
			for (int number = 1; number <= 10; number++) {
				// for each 4 colors		
				for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
					Card.Color color = Card.Color.values()[colorIndex];
					// 2 of each
					for (int duplicate = 1; duplicate <= 2; duplicate++) {
						Card card = iterator.next();
						assertEquals(card.getDesign(), Card.Design.NUMBER);
						assertEquals(card.getColor(), color);
						assertEquals(card.getPointValue(), number);
					}
				}
			}	
				
			// Asteroid cards
			// for each 4 colors
			for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
				Card.Color color = Card.Color.values()[colorIndex];
				// 2 of each
				for (int duplicate = 1; duplicate <= 2; duplicate++) {
					Card card = iterator.next();
					assertEquals(card.getDesign(), Card.Design.ASTEROIDS);
					assertEquals(card.getColor(), color);
				}
			}
				
			// Shooting Star cards
			// for each 4 colors
			for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.SHOOTING_STAR);
				assertEquals(card.getColor(), Card.Color.values()[colorIndex]);
			}			
			
			// Force Field cards
			// for each 4 colors
			for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.FORCE_FIELD);
				assertEquals(card.getColor(), Card.Color.values()[colorIndex]);
			}
			
			// Wild Shooting Star cards
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.SHOOTING_STAR);
				assertEquals(card.getColor(), Card.Color.WILD);
			}
				
			// Black Hole cards
			// for each 4 colors
			for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.BLACK_HOLE);
				assertEquals(card.getColor(), Card.Color.values()[colorIndex]);
			}
			
			// Wild Black Hole cards
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.BLACK_HOLE);
				assertEquals(card.getColor(), Card.Color.WILD);
			}
			
			// Big Bang cards
			Card card = iterator.next();
			assertEquals(card.getDesign(), Card.Design.BIG_BANG);
			assertEquals(card.getColor(), Color.BLUE_AND_RED);
			card = iterator.next();
			assertEquals(card.getDesign(), Card.Design.BIG_BANG);
			assertEquals(card.getColor(), Color.GREEN_AND_YELLOW);
				
			// Super Force Field cards
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.FORCE_FIELD);
				assertEquals(card.getColor(), Card.Color.WILD);
			}
						
			assertFalse(iterator.hasNext());
		}
		catch (NoSuchElementException ex) {
			fail("Expected more cards in the sorted linked list iterator.");
		}
	}
	
	private void testPointValueDescending() {
		CardComparator comparator = new CardComparator(CardComparator.CardTypeSort.POINT_VALUE, false);
		cards.setComparator(comparator);
		Iterator<Card> iterator = cards.iterator();
		
		try {
			// Super Force Field cards
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				Card card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.FORCE_FIELD);
				assertEquals(card.getColor(), Card.Color.WILD);
			}
			
			// Big Bang cards
			Card card = iterator.next();
			assertEquals(card.getDesign(), Card.Design.BIG_BANG);
			assertEquals(card.getColor(), Color.GREEN_AND_YELLOW);
			card = iterator.next();
			assertEquals(card.getDesign(), Card.Design.BIG_BANG);
			assertEquals(card.getColor(), Color.BLUE_AND_RED);
			
			// Wild Black Hole cards
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.BLACK_HOLE);
				assertEquals(card.getColor(), Card.Color.WILD);
			}
			
			// Black Hole cards
			// for each 4 colors
			for (int colorIndex = 3; colorIndex >= 0; colorIndex--) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.BLACK_HOLE);
				assertEquals(card.getColor(), Card.Color.values()[colorIndex]);
			}
			
			// Wild Shooting Star cards
			for (int duplicate = 1; duplicate <= 2; duplicate++) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.SHOOTING_STAR);
				assertEquals(card.getColor(), Card.Color.WILD);
			}

			// Force Field cards
			// for each 4 colors
			for (int colorIndex = 3; colorIndex >= 0; colorIndex--) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.FORCE_FIELD);
				assertEquals(card.getColor(), Card.Color.values()[colorIndex]);
			}
				
			// Shooting Star cards
			// for each 4 colors
			for (int colorIndex = 3; colorIndex >= 0; colorIndex--) {
				card = iterator.next();
				assertEquals(card.getDesign(), Card.Design.SHOOTING_STAR);
				assertEquals(card.getColor(), Card.Color.values()[colorIndex]);
			}	
				
			// Asteroid cards
			// for each 4 colors
			for (int colorIndex = 3; colorIndex >= 0; colorIndex--) {
				Card.Color color = Card.Color.values()[colorIndex];
				// 2 of each
				for (int duplicate = 1; duplicate <= 2; duplicate++) {
					card = iterator.next();
					assertEquals(card.getDesign(), Card.Design.ASTEROIDS);
					assertEquals(card.getColor(), color);
				}
			}

			// Number cards
			for (int number = 10; number >= 1; number--) {
				// for each 4 colors		
				for (int colorIndex = 3; colorIndex >= 0; colorIndex--) {
					Card.Color color = Card.Color.values()[colorIndex];
					// 2 of each
					for (int duplicate = 1; duplicate <= 2; duplicate++) {
						card = iterator.next();
						assertEquals(card.getDesign(), Card.Design.NUMBER);
						assertEquals(card.getColor(), color);
						assertEquals(card.getPointValue(), number);
					}
				}
			}
			
			assertFalse(iterator.hasNext());
		}
		catch (NoSuchElementException ex) {
			fail("Expected more cards in the sorted linked list iterator.");
		}
	}	
}
