package model;

import java.util.EmptyStackException;
import java.util.LinkedList;

import model.Card.Color;
import model.GameDeck.PileLocation;

import unit.UnitTest;

/**
 * Unit tester for the Spaced Out Game Deck class.
 */
public class GameDeckTest extends UnitTest {

	private final int NUM_CARDS_IN_DECK = 108;
	
	public int testAll() {
		testConstructor();
		testPlayCard();
		testDrawCard();
		testReturnAndReshuffle();
		testHasBigBangColors();
		testPlayPilesScore();
		
		return errorCount;
	}
		
	private void testConstructor() {
		GameDeck deck = new GameDeck(0);
		try {
			deck.drawCard();
			fail("No cards in deck. Should be empty stack exception");
		}
		catch(EmptyStackException ex) {}
		
		deck = new GameDeck(-1);
		try {
			deck.drawCard();
			fail("No cards in deck. Should be empty stack exception");
		}
		catch(EmptyStackException ex) {}
		
		for (int numOfDecks = 1; numOfDecks <= 5; numOfDecks++) {
			deck = new GameDeck(numOfDecks);
			int cardCount = 0;
			try {
				while (true) {
					deck.drawCard();
					cardCount++;
				}
			}
			catch(EmptyStackException ex) {}
			assertEquals(cardCount, NUM_CARDS_IN_DECK * numOfDecks);
		}		
		
		deck = new GameDeck(10);
		int cardCount = 0;
		try {
			while (true) {
				deck.drawCard();
				cardCount++;
			}
		}
		catch(EmptyStackException ex) {}
		assertEquals(cardCount, NUM_CARDS_IN_DECK * 10);
		
		// test shuffle, make sure random order (2 different game decks do not have same order of cards)
		deck = new GameDeck(1);
		GameDeck newDeck = new GameDeck(1);
		int numOfMatches = 0;
		for (int cardNum = 1; cardNum <= 10; cardNum++) {
			if (deck.drawCard() == newDeck.drawCard())
				numOfMatches++;
		}
		if (numOfMatches > 5)
			fail(numOfMatches + " out of 10 cards were in the same order; This is NOT very random.");
	}
	
	private void testPlayCard() {
		GameDeck deck = new GameDeck(1);
		LinkedList<Card> allCards = new LinkedList<Card>();
		try {
			while (true)
				allCards.add(deck.drawCard());
		}
		catch(EmptyStackException ex) {}
		
		deck.playCard(allCards.get(0), PileLocation.LEFT, Color.BLUE);
		assertTrue(deck.getTopCard(PileLocation.LEFT) == allCards.get(0));
		assertTrue(deck.getColor(PileLocation.LEFT) == Color.BLUE);
		
		deck.playCard(allCards.get(107), PileLocation.RIGHT, Color.RED);
		assertTrue(deck.getTopCard(PileLocation.RIGHT) == allCards.get(107));
		assertTrue(deck.getColor(PileLocation.RIGHT) == Color.RED);
		assertTrue(deck.getTopCard(PileLocation.LEFT) == allCards.get(0));
		assertTrue(deck.getColor(PileLocation.LEFT) == Color.BLUE);
		
		deck.playCard(allCards.get(2), PileLocation.LEFT, Color.GREEN);
		assertTrue(deck.getTopCard(PileLocation.LEFT) == allCards.get(2));
		assertTrue(deck.getColor(PileLocation.LEFT) == Color.GREEN);
		assertTrue(deck.getTopCard(PileLocation.RIGHT) == allCards.get(107));
		assertTrue(deck.getColor(PileLocation.RIGHT) == Color.RED);
		
		deck.playCard(allCards.get(35), PileLocation.RIGHT, Color.YELLOW);
		assertTrue(deck.getTopCard(PileLocation.RIGHT) == allCards.get(35));
		assertTrue(deck.getColor(PileLocation.RIGHT) == Color.YELLOW);
		
		deck.playCard(allCards.get(62), PileLocation.RIGHT, Color.WILD);
		assertTrue(deck.getTopCard(PileLocation.RIGHT) == allCards.get(62));
		assertTrue(deck.getColor(PileLocation.RIGHT) == Color.WILD);
		assertTrue(deck.getTopCard(PileLocation.LEFT) == allCards.get(2));
		assertTrue(deck.getColor(PileLocation.LEFT) == Color.GREEN);
		
		deck.playCard(allCards.get(2), PileLocation.LEFT, Color.BLUE_AND_RED);
		assertTrue(deck.getTopCard(PileLocation.LEFT) == allCards.get(2));
		assertTrue(deck.getColor(PileLocation.LEFT) == Color.BLUE_AND_RED);
		
		deck.playCard(allCards.get(62), PileLocation.LEFT, Color.GREEN_AND_YELLOW);
		assertTrue(deck.getTopCard(PileLocation.LEFT) == allCards.get(62));
		assertTrue(deck.getColor(PileLocation.LEFT) == Color.GREEN_AND_YELLOW);
		assertTrue(deck.getTopCard(PileLocation.RIGHT) == allCards.get(62));
		assertTrue(deck.getColor(PileLocation.RIGHT) == Color.WILD);
	}
	
	private void testDrawCard() {
		GameDeck deck = new GameDeck(1);
		LinkedList<Card> allCards = new LinkedList<Card>();
		try {
			while (true)
				allCards.add(deck.drawCard());
		}
		catch(EmptyStackException ex) {}
		
		deck.playCard(allCards.get(0), PileLocation.LEFT, Color.BLUE_AND_RED);
		deck.playCard(allCards.get(1), PileLocation.RIGHT, Color.RED);
		deck.playCard(allCards.get(2), PileLocation.LEFT, Color.YELLOW);
		deck.playCard(allCards.get(3), PileLocation.LEFT, Color.GREEN);
		deck.playCard(allCards.get(4), PileLocation.RIGHT, Color.GREEN_AND_YELLOW);
		deck.playCard(allCards.get(5), PileLocation.RIGHT, Color.WILD);
		deck.playCard(allCards.get(6), PileLocation.LEFT, Color.BLUE);
		deck.playCard(allCards.get(7), PileLocation.LEFT, Color.BLUE);
		deck.playCard(allCards.get(8), PileLocation.RIGHT, Color.BLUE_AND_RED);
		deck.playCard(allCards.get(9), PileLocation.RIGHT, Color.WILD);
		
		int cardCount = 0;
		// Check for random shuffle
		boolean matchForwards = true;
		boolean matchBackwards = true;
		while (true) {
			Card card = deck.drawCard();
			if (cardCount == 10)
				break;
				
			if (card != allCards.get(cardCount))
				matchForwards = false;
				
			cardCount++;
			
			if (card != allCards.get(10 - cardCount))
				matchBackwards = false;
		}
		assertEquals(cardCount, 10);
		assertTrue(deck.getTopCard(PileLocation.LEFT) == allCards.get(7));
		assertTrue(deck.getTopCard(PileLocation.RIGHT) == allCards.get(9));
		assertFalse(matchForwards);
		assertFalse(matchBackwards);
	}
	
	private void testReturnAndReshuffle() {
		GameDeck deck = new GameDeck(2);
		LinkedList<Card> allCards = new LinkedList<Card>();
		try {
			while (true)
				allCards.add(deck.drawCard());
		}
		catch(EmptyStackException ex) {}
		
		deck.playCard(allCards.remove(29), PileLocation.RIGHT, Color.RED);
		deck.playCard(allCards.remove(28), PileLocation.LEFT, Color.YELLOW);
		deck.playCard(allCards.remove(27), PileLocation.LEFT, Color.GREEN);
		deck.playCard(allCards.remove(26), PileLocation.RIGHT, Color.GREEN_AND_YELLOW);
		deck.playCard(allCards.remove(25), PileLocation.RIGHT, Color.WILD);
		deck.playCard(allCards.remove(24), PileLocation.LEFT, Color.BLUE);
		deck.playCard(allCards.remove(23), PileLocation.LEFT, Color.BLUE);
		deck.playCard(allCards.remove(22), PileLocation.RIGHT, Color.BLUE_AND_RED);
		deck.playCard(allCards.remove(21), PileLocation.RIGHT, Color.WILD);
		deck.playCard(allCards.remove(20), PileLocation.LEFT, Color.GREEN);
		deck.playCard(allCards.remove(19), PileLocation.RIGHT, Color.GREEN_AND_YELLOW);
		deck.playCard(allCards.remove(18), PileLocation.RIGHT, Color.WILD);
		deck.playCard(allCards.remove(17), PileLocation.LEFT, Color.BLUE);
		deck.playCard(allCards.remove(16), PileLocation.RIGHT, Color.RED);
		deck.playCard(allCards.remove(15), PileLocation.LEFT, Color.BLUE_AND_RED);
		
		deck.returnCardsAndReshuffle(allCards);
		
		int cardCount = 0;
		try {
			// check for randomness from re-shuffling
			int cardIndex1 = allCards.indexOf(deck.drawCard());
			cardCount++;
			int cardIndex2 = allCards.indexOf(deck.drawCard());
			cardCount++;
			int cardIndex3 = allCards.indexOf(deck.drawCard());
			cardCount++;
			assertFalse(cardIndex1 == -1 && cardIndex2 == -1 && cardIndex3 == -1);
			assertFalse(Math.abs(cardIndex1 - cardIndex2) == 1 && Math.abs(cardIndex2 - cardIndex3) == 1);
			
			while (true) {
				deck.drawCard();
				cardCount++;
			}
		}
		catch(EmptyStackException ex) {}
		assertEquals(cardCount, NUM_CARDS_IN_DECK * 2);
	}
	
	private void testHasBigBangColors() {
		GameDeck deck = new GameDeck(1);
		LinkedList<Card> allCards = new LinkedList<Card>();
		try {
			while (true)
				allCards.add(deck.drawCard());
		}
		catch(EmptyStackException ex) {}
		
		deck.playCard(allCards.get(0), PileLocation.LEFT, Color.RED);
		deck.playCard(allCards.get(1), PileLocation.RIGHT, Color.BLUE);
		assertTrue(deck.hasBigBangColors(Color.BLUE_AND_RED));
		assertFalse(deck.hasBigBangColors(Color.GREEN_AND_YELLOW));
		deck.playCard(allCards.get(2), PileLocation.LEFT, Color.BLUE);
		deck.playCard(allCards.get(3), PileLocation.RIGHT, Color.RED);
		assertTrue(deck.hasBigBangColors(Color.BLUE_AND_RED));
		assertFalse(deck.hasBigBangColors(Color.GREEN_AND_YELLOW));
		deck.playCard(allCards.get(4), PileLocation.RIGHT, Color.BLUE);
		assertFalse(deck.hasBigBangColors(Color.BLUE_AND_RED));
		assertFalse(deck.hasBigBangColors(Color.GREEN_AND_YELLOW));
		deck.playCard(allCards.get(5), PileLocation.RIGHT, Color.GREEN);
		assertFalse(deck.hasBigBangColors(Color.BLUE_AND_RED));
		assertFalse(deck.hasBigBangColors(Color.GREEN_AND_YELLOW));
		deck.playCard(allCards.get(6), PileLocation.RIGHT, Color.YELLOW);
		assertFalse(deck.hasBigBangColors(Color.BLUE_AND_RED));
		assertFalse(deck.hasBigBangColors(Color.GREEN_AND_YELLOW));
		deck.playCard(allCards.get(7), PileLocation.RIGHT, Color.WILD);
		assertFalse(deck.hasBigBangColors(Color.BLUE_AND_RED));
		assertFalse(deck.hasBigBangColors(Color.GREEN_AND_YELLOW));
		deck.playCard(allCards.get(8), PileLocation.RIGHT, Color.BLUE_AND_RED);
		assertFalse(deck.hasBigBangColors(Color.BLUE_AND_RED));
		assertFalse(deck.hasBigBangColors(Color.GREEN_AND_YELLOW));
		deck.playCard(allCards.get(9), PileLocation.LEFT, Color.BLUE_AND_RED);
		assertFalse(deck.hasBigBangColors(Color.BLUE_AND_RED));
		assertFalse(deck.hasBigBangColors(Color.GREEN_AND_YELLOW));
		deck.playCard(allCards.get(10), PileLocation.LEFT, Color.WILD);
		deck.playCard(allCards.get(11), PileLocation.RIGHT, Color.WILD);
		assertFalse(deck.hasBigBangColors(Color.BLUE_AND_RED));
		assertFalse(deck.hasBigBangColors(Color.GREEN_AND_YELLOW));
		deck.playCard(allCards.get(12), PileLocation.LEFT, Color.GREEN);
		deck.playCard(allCards.get(13), PileLocation.RIGHT, Color.YELLOW);
		assertFalse(deck.hasBigBangColors(Color.BLUE_AND_RED));
		assertTrue(deck.hasBigBangColors(Color.GREEN_AND_YELLOW));
		deck.playCard(allCards.get(13), PileLocation.LEFT, Color.YELLOW);
		deck.playCard(allCards.get(12), PileLocation.RIGHT, Color.GREEN);
		assertFalse(deck.hasBigBangColors(Color.BLUE_AND_RED));
		assertTrue(deck.hasBigBangColors(Color.GREEN_AND_YELLOW));
	}
	
	private void testPlayPilesScore() {
		GameDeck deck = new GameDeck(1);
		deck.playCard(new Card(Card.Design.ASTEROIDS, Color.BLUE, 20), PileLocation.LEFT, Color.BLUE);
		deck.playCard(new Card(Card.Design.BIG_BANG, Color.GREEN_AND_YELLOW, 100), PileLocation.RIGHT, Color.GREEN_AND_YELLOW);
		assertEquals(deck.getPlayPilesScore(), 20 + 100);
		deck.playCard(new Card(Card.Design.BLACK_HOLE, Color.RED, 30), PileLocation.LEFT, Color.RED);
		assertEquals(deck.getPlayPilesScore(), 30 + 100);
		deck.playCard(new Card(Card.Design.BLACK_HOLE, Color.WILD, 50), PileLocation.RIGHT, Color.YELLOW);
		assertEquals(deck.getPlayPilesScore(), 30 + 50);
		deck.playCard(new Card(Card.Design.BLACK_HOLE, Color.GREEN, 75), PileLocation.LEFT, Color.YELLOW);
		assertEquals(deck.getPlayPilesScore(), 75 + 50);
		deck.playCard(new Card(Card.Design.FORCE_FIELD, Color.BLUE_AND_RED, 99), PileLocation.RIGHT, Color.YELLOW);
		assertEquals(deck.getPlayPilesScore(), 75 + 99);
		deck.playCard(new Card(Card.Design.FORCE_FIELD, Color.WILD, 100), PileLocation.RIGHT, Color.GREEN);
		assertEquals(deck.getPlayPilesScore(), 75 + 100);
		deck.playCard(new Card(Card.Design.SHOOTING_STAR, Color.WILD, 30), PileLocation.LEFT, Color.BLUE);
		assertEquals(deck.getPlayPilesScore(), 30 + 100);
		deck.playCard(new Card(Card.Design.SHOOTING_STAR, Color.RED, 20), PileLocation.RIGHT, Color.RED);
		assertEquals(deck.getPlayPilesScore(), 30 + 20);
		deck.playCard(new Card(Card.Design.NUMBER, Color.YELLOW, 3), PileLocation.LEFT, Color.YELLOW);
		assertEquals(deck.getPlayPilesScore(), 3 + 20);
		deck.playCard(new Card(Card.Design.NUMBER, Color.YELLOW, 7), PileLocation.RIGHT, Color.YELLOW);
		assertEquals(deck.getPlayPilesScore(), 3 + 7);
		deck.playCard(new Card(Card.Design.NUMBER, Color.BLUE, 10), PileLocation.LEFT, Color.BLUE);
		assertEquals(deck.getPlayPilesScore(), 10 + 7);
		deck.playCard(new Card(Card.Design.NUMBER, Color.RED, 1), PileLocation.LEFT, Color.RED);
		assertEquals(deck.getPlayPilesScore(), 1 + 7);		
	}
}
