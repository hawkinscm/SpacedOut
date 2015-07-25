package model;

import java.util.LinkedList;

import model.Card.Color;
import model.Card.Design;

/**
 * A factory for building Spaced Out card decks.
 */
public class SpacedOutDeckFactory {

	private static final int NUMBER_CARD_RANGE = 10;
	
	private static final int ASTEROID_POINT_VALUE = 20;
	private static final int SHOOTING_STAR_POINT_VALUE = 20;
	private static final int WILD_SHOOTING_STAR_POINT_VALUE = 30;
	private static final int BLACK_HOLE_POINT_VALUE = 30;
	private static final int WILD_BLACK_HOLE_POINT_VALUE = 50;
	private static final int BIG_BANG_POINT_VALUE = 100;
	private static final int FORCE_FIELD_POINT_VALUE = 20;
	private static final int SUPER_FORCE_FIELD_POINT_VALUE = 100;
		
	/**
	 * Builds and returns a new list of all Spaced Out cards in a Spaced Out card deck.  
	 */
	public static LinkedList<Card> buildDeck() {
		// will contain 108 cards in total
		LinkedList<Card> deck = new LinkedList<Card>();
		
		// 80 number cards (1-10, 4 colors) x2
		for (int number = 1; number <= NUMBER_CARD_RANGE; number++) {
			for (int clone = 1; clone <= 2; clone++) {
				deck.add(new Card(Design.NUMBER, Color.BLUE, number));
				deck.add(new Card(Design.NUMBER, Color.RED, number));
				deck.add(new Card(Design.NUMBER, Color.GREEN, number));
				deck.add(new Card(Design.NUMBER, Color.YELLOW, number));
			}
		}			
			
		// 8 asteroids (4 colors) x2
		for (int clone = 1; clone <= 2; clone++) {
			deck.add(new Card(Design.ASTEROIDS, Color.BLUE, ASTEROID_POINT_VALUE));
			deck.add(new Card(Design.ASTEROIDS, Color.RED, ASTEROID_POINT_VALUE));
			deck.add(new Card(Design.ASTEROIDS, Color.GREEN, ASTEROID_POINT_VALUE));
			deck.add(new Card(Design.ASTEROIDS, Color.YELLOW, ASTEROID_POINT_VALUE));
		}	
		
		// 6 shooting stars (4 colors, 2 wild)
		deck.add(new Card(Design.SHOOTING_STAR, Color.BLUE, SHOOTING_STAR_POINT_VALUE));
		deck.add(new Card(Design.SHOOTING_STAR, Color.RED, SHOOTING_STAR_POINT_VALUE));
		deck.add(new Card(Design.SHOOTING_STAR, Color.GREEN, SHOOTING_STAR_POINT_VALUE));
		deck.add(new Card(Design.SHOOTING_STAR, Color.YELLOW, SHOOTING_STAR_POINT_VALUE));
		deck.add(new Card(Design.SHOOTING_STAR, Color.WILD, WILD_SHOOTING_STAR_POINT_VALUE));
		deck.add(new Card(Design.SHOOTING_STAR, Color.WILD, WILD_SHOOTING_STAR_POINT_VALUE));
		
		// 6 black holes (4 colors, 2 wild)
		deck.add(new Card(Design.BLACK_HOLE, Color.BLUE, BLACK_HOLE_POINT_VALUE));
		deck.add(new Card(Design.BLACK_HOLE, Color.RED, BLACK_HOLE_POINT_VALUE));
		deck.add(new Card(Design.BLACK_HOLE, Color.GREEN, BLACK_HOLE_POINT_VALUE));
		deck.add(new Card(Design.BLACK_HOLE, Color.YELLOW, BLACK_HOLE_POINT_VALUE));
		deck.add(new Card(Design.BLACK_HOLE, Color.WILD, WILD_BLACK_HOLE_POINT_VALUE));
		deck.add(new Card(Design.BLACK_HOLE, Color.WILD, WILD_BLACK_HOLE_POINT_VALUE));
		
		// 2 big bangs
		deck.add(new Card(Design.BIG_BANG, Color.BLUE_AND_RED, BIG_BANG_POINT_VALUE));
		deck.add(new Card(Design.BIG_BANG, Color.GREEN_AND_YELLOW, BIG_BANG_POINT_VALUE));
		
		// 6 force fields (4 colors, 2 wild [super force field])
		deck.add(new Card(Design.FORCE_FIELD, Color.BLUE, FORCE_FIELD_POINT_VALUE));
		deck.add(new Card(Design.FORCE_FIELD, Color.RED, FORCE_FIELD_POINT_VALUE));
		deck.add(new Card(Design.FORCE_FIELD, Color.GREEN, FORCE_FIELD_POINT_VALUE));
		deck.add(new Card(Design.FORCE_FIELD, Color.YELLOW, FORCE_FIELD_POINT_VALUE));
		deck.add(new Card(Design.FORCE_FIELD, Color.WILD, SUPER_FORCE_FIELD_POINT_VALUE));
		deck.add(new Card(Design.FORCE_FIELD, Color.WILD, SUPER_FORCE_FIELD_POINT_VALUE));
		
		return deck;
	}
}
