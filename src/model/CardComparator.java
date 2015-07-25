package model;

import java.util.Comparator;

/**
 * Comparator for Spaced Out cards: sorting by Color, Design, or point value.  
 * All types of sorting can be done in ascending or descending point value order.  
 */
public class CardComparator implements Comparator<Card> {
	/**
	 * Enumerator for how Spaced Out cards can be sorted.
	 */
	public static enum CardTypeSort {
		COLOR,
		DESIGN,
		POINT_VALUE;
	}

	private CardTypeSort cardTypeSort;
	private boolean inAscendingOrder;
	
	/**
	 * Creates a new Spaced Out card comparator that compares cards for sorting.
	 * @param cardTypeSort type of sorting to use
	 * @param inAscendingOrder whether the sorting should be in ascending order or not
	 */
	public CardComparator(CardTypeSort cardTypeSort, boolean inAscendingOrder) {
		this.cardTypeSort = cardTypeSort;
		this.inAscendingOrder = inAscendingOrder;
	}
	
	/**
	 * Returns the comparator's card type sort.
	 * @return the comparator's card type sort
	 */
	public CardTypeSort getCardTypeSort() {
		return cardTypeSort;
	}
	
	/**
	 * Returns whether or not the comparator is in ascending order (lowest card point value to highest)
	 * @return true if the comparator is in ascending order; false otherwise
	 */
	public boolean isInAscendingOrder() {
		return inAscendingOrder;
	}
	
	@Override
	public int compare(Card card1, Card card2) {
		if (card1 == null) {
			if (card2 == null)
				return 0;
			else
				return -1;
		}
		else if (card2 == null)
			return 1;
		
		int compareValue = 0;
		switch (cardTypeSort) {
			case COLOR :
				compareValue = card1.getColor().compareTo(card2.getColor());
				if (compareValue == 0) { compareValue = card1.getDesign().compareTo(card2.getDesign()); }
				if (compareValue == 0) { compareValue = card1.getPointValue() - card2.getPointValue(); }
				break;
			case DESIGN :
				compareValue = card1.getDesign().compareTo(card2.getDesign());
				if (compareValue == 0) { compareValue = card1.getColor().compareTo(card2.getColor()); }
				if (compareValue == 0) { compareValue = card1.getPointValue() - card2.getPointValue(); }
				break;
			case POINT_VALUE :
				compareValue = card1.getPointValue() - card2.getPointValue();
				if (compareValue == 0) { compareValue = card1.getDesign().compareTo(card2.getDesign()); }
				if (compareValue == 0) { compareValue = card1.getColor().compareTo(card2.getColor()); }
				break;
		}		

		if (!inAscendingOrder) {
			compareValue *= -1;
		}
			
		return compareValue;
	}
}
