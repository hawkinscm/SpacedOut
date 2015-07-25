
package model;

import unit.UnitTester;

public class SpaceOutUnitTester extends UnitTester {

	public SpaceOutUnitTester() {
		
		addUnitTest(new CardTest());
		addUnitTest(new SpacedOutDeckFactoryTest());
		addUnitTest(new GameDeckTest());
		addUnitTest(new PlayerTest());
		addUnitTest(new ScoreKeeperTest());
		addUnitTest(new BattleTest());
		
		addUnitTest(new SortedLinkedListTest());
		addUnitTest(new CardComparatorTest());
		
		addUnitTest(new GMTest());
		
		testAll();
	}
	
	public static void main(String args[]) {
		new SpaceOutUnitTester();
		System.exit(0);
	}
}
