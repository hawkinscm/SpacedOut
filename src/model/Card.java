package model;

/**
 * Represents a Spaced Out card.
 */
public class Card {

	/**
	 * Enumerator for all card design types. The order of enum types defines the Design sorting order
	 */
	public static enum Design {
		NUMBER,
		ASTEROIDS,
		SHOOTING_STAR,
		BLACK_HOLE,
		BIG_BANG,
		FORCE_FIELD;
	}
	
	/**
	 * Enumerator for all card colors. The order of enum types defines the Color sorting order
	 */
	public static enum Color {
		BLUE,
		RED,
		GREEN,
		YELLOW,
		BLUE_AND_RED,
		GREEN_AND_YELLOW,
		WILD;
		
		public static Color[] getStandardColors() {
			return new Color[] {BLUE, RED, GREEN, YELLOW};
		}
	}
	
	private Design cardDesign;
	private Color cardColor;
	private int pointValue;
	
	/**
	 * Creates a new Space Out card.
	 * @param design design of the card
	 * @param color color of the card
	 * @param value point value of the card
	 */
	public Card(Design design, Color color, int value) {
		cardDesign = design;
		cardColor = color;
		pointValue = value;
	}
	
	@Override
	public String toString() {
		if (cardDesign == Design.NUMBER)
			return cardColor + "_" + pointValue;
		else if (cardColor == Color.WILD && cardDesign == Design.FORCE_FIELD)
			return "SUPER_" + cardDesign;
		else if (cardDesign == Design.BIG_BANG) {
			if (cardColor == Color.BLUE || cardColor == Color.RED)
				return Color.BLUE_AND_RED + "_" + cardDesign;
			else if (cardColor == Color.GREEN || cardColor == Color.YELLOW)
				return Color.GREEN_AND_YELLOW + "_" + cardDesign;
		}
		return cardColor + "_" + cardDesign;
	}
		
	/**
	 * Returns the design of this card.
	 * @return the design of this card
	 */
	public Design getDesign() {
		return cardDesign;
	}
	
	/**
	 * Returns the color of this card.
	 * @return the color of this card
	 */
	public Color getColor() {
		return cardColor;
	}
	
	/**
	 * Returns the point value of this card.
	 * @return the point value of this card
	 */
	public int getPointValue() {
		return pointValue;
	}
	
	/**
	 * Returns true if the given card is a "clone" of this card
	 * @param card the possible "clone" card to check
	 * @return true if the given card is a "clone" of this card; false, otherwise
	 */
	public boolean isClone(Card card) {
		if (card.getDesign() != Design.NUMBER || cardDesign != Design.NUMBER)
			return false;
		
		if (card.getColor() == cardColor && card.getPointValue() == pointValue)
			return true;
		
		return false;
	}
	
	/**
	 * Returns whether or not the given card matches this cards design.
	 * @param card the card to compare
	 * @return true if the card's design matches this card's design
	 */
	public boolean matchesDesign(Card card) {
		if (card.getDesign() != cardDesign)
			return false;
		
		if (cardDesign == Design.NUMBER)
			return (card.getPointValue() == pointValue);
		
		return true;
	}
}
