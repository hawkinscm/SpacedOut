package model;

import java.util.LinkedList;

import unit.UnitTest;

/**
 * Unit tester for the Spaced Out Deck Factory class.
 */
public class SpacedOutDeckFactoryTest extends UnitTest {

	private final int TOTAL_CARDS_IN_DECK = 108;
	private final int NUMBER_CARD_RANGE = 10;
	
	public int testAll() {
		testBuildDeck();
		
		return errorCount;
	}
		
	private void testBuildDeck() {
		LinkedList<Card> deck = SpacedOutDeckFactory.buildDeck();
		assertEquals(deck.size(), TOTAL_CARDS_IN_DECK);
		
		int redCount = 0;
		int blueCount = 0;
		int greenCount = 0;
		int yellowCount = 0;
		int[] numberCardCount = new int[NUMBER_CARD_RANGE + 1];
		for (Card card : deck) {
			if (card.getDesign() != Card.Design.NUMBER)
				continue;
			
			numberCardCount[card.getPointValue()]++;
			
			switch (card.getColor()) {
			case RED :
				redCount++;
				break;
			case BLUE :
				blueCount++;
				break;
			case GREEN :
				greenCount++;
				break;
			case YELLOW :
				yellowCount++;
				break;
			default :
				fail("Should not be any Number cards of this color: " + card.getColor());
			}
		}
		assertEquals(redCount, NUMBER_CARD_RANGE * 2);
		assertEquals(blueCount, NUMBER_CARD_RANGE * 2);
		assertEquals(greenCount, NUMBER_CARD_RANGE * 2);
		assertEquals(yellowCount, NUMBER_CARD_RANGE * 2);
		for (int number = 1; number <= 10; number++)
			assertEquals(numberCardCount[number], 4 * 2);
		
		int totalCount = redCount + blueCount + greenCount + yellowCount;
		
		redCount = 0;
		blueCount = 0;
		greenCount = 0;
		yellowCount = 0;
		for (Card card : deck) {
			if (card.getDesign() != Card.Design.ASTEROIDS)
				continue;
			
			assertEquals(card.getPointValue(), 20);
			
			switch (card.getColor()) {
			case RED :
				redCount++;
				break;
			case BLUE :
				blueCount++;
				break;
			case GREEN :
				greenCount++;
				break;
			case YELLOW :
				yellowCount++;
				break;
			default :
				fail("Should not be any Asteroid cards of this color: " + card.getColor());
			}
		}
		assertEquals(redCount, 2);
		assertEquals(blueCount, 2);
		assertEquals(greenCount, 2);
		assertEquals(yellowCount, 2);
		
		totalCount += redCount + blueCount + greenCount + yellowCount;
		
		for (int typeCount = 1; typeCount <= 3; typeCount++) {
			Card.Design design;
			String designStr;
			int normalPointValue;
			int wildPointValue;
			if (typeCount == 1) {
				design = Card.Design.SHOOTING_STAR;
				designStr = "Shooting Star";
				normalPointValue = 20;
				wildPointValue = 30;
			}
			else if (typeCount == 2) {
				design = Card.Design.BLACK_HOLE;
				designStr = "Black Hole";
				normalPointValue = 30;
				wildPointValue = 50;
			}
			else {
				design = Card.Design.FORCE_FIELD;
				designStr = "Force Field";
				normalPointValue = 20;
				wildPointValue = 100;
			}
			
			redCount = 0;
			blueCount = 0;
			greenCount = 0;
			yellowCount = 0;
			int wildCount = 0;
			for (Card card : deck) {
				if (card.getDesign() != design)
					continue;
				
				int pointValue = normalPointValue;
				
				switch (card.getColor()) {
				case RED :
					redCount++;
					break;
				case BLUE :
					blueCount++;
					break;
				case GREEN :
					greenCount++;
					break;
				case YELLOW :
					yellowCount++;
					break;
				case WILD :
					wildCount++;
					pointValue = wildPointValue;
					break;
				default :
					fail("Should not be any " + designStr + " cards of this color: " + card.getColor());
				}
				
				assertEquals(card.getPointValue(), pointValue);
			}
			assertEquals(redCount, 1);
			assertEquals(blueCount, 1);
			assertEquals(greenCount, 1);
			assertEquals(yellowCount, 1);
			assertEquals(wildCount, 2);
			
			totalCount += redCount + blueCount + greenCount + yellowCount + wildCount;
		}
		
		int redBlueCount = 0;
		int greenYellowCount = 0;
		for (Card card : deck) {
			if (card.getDesign() != Card.Design.BIG_BANG)
				continue;
			
			assertEquals(card.getPointValue(), 100);
			
			switch (card.getColor()) {
			case BLUE_AND_RED :
				redBlueCount++;
				break;
			case GREEN_AND_YELLOW :
				greenYellowCount++;
				break;
			default :
				fail("Should not be any Big Bang cards of this color: " + card.getColor());
			}
		}
		assertEquals(redBlueCount, 1);
		assertEquals(greenYellowCount, 1);
		
		totalCount += redBlueCount + greenYellowCount;
		
		assertEquals(totalCount, TOTAL_CARDS_IN_DECK);
	}
}
