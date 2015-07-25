package model;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;

import unit.UnitTest;

public class SortedLinkedListTest extends UnitTest {

	private class Name {
		public String first;
		public String last;
		public Name(String first, String last) {
			this.first = first;
			this.last = last;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o != null && o instanceof Name) {
				return (first.equals(((Name)o).first) && last.equals(((Name)o).last));
			}
			return false;
		}
		
		@Override
		public String toString() {
			return first + " " + last;
		}
	}
	
	private class ReverseIntegerComparator implements Comparator<Integer> {
		@Override
		public int compare(Integer int1, Integer int2) {
			return int2.compareTo(int1);
		}		
	}
	
	private class FirstNameComparator implements Comparator<Name> {
		@Override
		public int compare(Name name1, Name name2) {
			int firstCompare = name1.first.compareTo(name2.first);
			if (firstCompare == 0)
				return name1.last.compareTo(name2.last);
			return firstCompare;
		}
	}
	
	private class LastNameComparator implements Comparator<Name> {
		@Override
		public int compare(Name name1, Name name2) {
			int lastCompare = name1.last.compareTo(name2.last);
			if (lastCompare == 0)
				return name1.first.compareTo(name2.first);
			return lastCompare;
		}
	}
	
	public int testAll() {
		testUnsupportedMethods();
		testOverriddenSupportedMethods();
		testComparator();
		
		return errorCount;
	}
	
	private void testUnsupportedMethods() {
		SortedLinkedList<Integer> intList = new SortedLinkedList<Integer>(1);		
		try { 
			intList.add(0, 1);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		try { 
			Collection<Integer> ints = new LinkedList<Integer>();
			ints.add(1);
			ints.add(3);
			intList.addAll(0, ints);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		try { 
			intList.addFirst(4);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		try { 
			intList.addLast(6);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		try { 
			intList.offerFirst(8);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		try { 
			intList.offerLast(7);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		try { 
			intList.push(5);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		try { 
			intList.set(0, 9);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		
		SortedLinkedList<Name> nameList = new SortedLinkedList<Name>(new FirstNameComparator());
		Name name1 = new Name("Alpha", "Prime");
		Name name2 = new Name("Beta", "Secundus");
		try {			
			nameList.add(0, name1);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		try { 
			Collection<Name> names = new LinkedList<Name>();
			names.add(name1);
			names.add(name2);
			nameList.addAll(0, names);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		try { 
			nameList.addFirst(name2);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		try { 
			nameList.addLast(name1);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		try { 
			nameList.offerFirst(name1);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		try { 
			nameList.offerLast(name2);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		try { 
			nameList.push(name1);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
		try { 
			nameList.set(0, name2);
			fail("Should have thrown an unsupported operation exception.");
		} catch (UnsupportedOperationException ex) {}
	}	
	
	private void testOverriddenSupportedMethods() {
		SortedLinkedList<Integer> list = new SortedLinkedList<Integer>(1);
		list.add(3);
		assertEquals(list.getFirst().intValue(), 1);
		assertEquals(list.getLast().intValue(), 3);
		list.add(2);
		assertEquals(list.getFirst(), 1);
		assertEquals(list.get(1), 2);
		assertEquals(list.getLast(), 3);
		list.offer(0);
		for (int index = 0; index <= 3; index++)
			assertEquals(list.get(index), index);
		list.offer(4);
		for (int index = 0; index <= 4; index++)
			assertEquals(list.get(index), index);
		
		list = new SortedLinkedList<Integer>(list);
		List<Integer> ints = new LinkedList<Integer>();
		ints.add(6);
		ints.add(5);
		ints.add(8);
		ints.add(9);
		ints.add(7);
		list.addAll(ints);
		ints.add(20);
		ints.add(15);
		ints.add(17345095);
		ints.add(-9845);
		ints.add(-5);
		ints.add(20);
		list.addAll(ints);
		assertEquals(list.size(), 21);
		assertEquals(list.get(0), -9845);
		assertEquals(list.get(1), -5);
		assertEquals(list.get(2), 0);
		assertEquals(list.get(3), 1);
		assertEquals(list.get(4), 2);
		assertEquals(list.get(5), 3);
		assertEquals(list.get(6), 4);
		assertEquals(list.get(7), 5);
		assertEquals(list.get(8), 5);
		assertEquals(list.get(9), 6);
		assertEquals(list.get(10), 6);
		assertEquals(list.get(11), 7);
		assertEquals(list.get(12), 7);
		assertEquals(list.get(13), 8);
		assertEquals(list.get(14), 8);
		assertEquals(list.get(15), 9);
		assertEquals(list.get(16), 9);
		assertEquals(list.get(17), 15);
		assertEquals(list.get(18), 20);
		assertEquals(list.get(19), 20);
		assertEquals(list.get(20), 17345095);
		
		assertTrue(list.contains(0));
		assertTrue(list.contains(17345095));
		assertTrue(list.contains(20));
		assertTrue(list.contains(8));
		assertTrue(list.contains(3));
		assertTrue(list.contains(20));
		assertTrue(list.contains(15));
		assertTrue(list.contains(-5));
		assertTrue(list.contains(-9845));
		assertFalse(list.contains(-1));
		assertFalse(list.contains(10));
		assertFalse(list.contains(16));
		assertFalse(list.contains(11));
		
		assertEquals(list.indexOf(-9845), 0);
		assertEquals(list.indexOf(-5), 1);
		assertEquals(list.indexOf(0), 2);
		assertEquals(list.indexOf(1), 3);
		assertEquals(list.indexOf(2), 4);
		assertEquals(list.indexOf(3), 5);
		assertEquals(list.indexOf(4), 6);
		assertEquals(list.indexOf(5), 7);
		assertEquals(list.indexOf(6), 9);
		assertEquals(list.indexOf(7), 11);
		assertEquals(list.indexOf(8), 13);
		assertEquals(list.indexOf(9), 15);
		assertEquals(list.indexOf(15), 17);
		assertEquals(list.indexOf(20), 18);
		assertEquals(list.indexOf(17345095), 20);
		assertEquals(list.indexOf(-1), -1);
		assertEquals(list.indexOf(10), -1);
		assertEquals(list.indexOf(16), -1);
		assertEquals(list.indexOf(11), -1);
		
		assertTrue(list.remove((Integer)4));
		assertFalse(list.contains(4));
		assertFalse(list.remove((Integer)4));
		assertEquals(list.indexOf(20), 17);
		assertTrue(list.remove((Integer)20));
		assertTrue(list.contains(20));
		assertEquals(list.indexOf(20), 17);
		assertTrue(list.remove((Integer)20));
		assertFalse(list.contains(20));
		assertEquals(list.indexOf(20), -1);
		assertFalse(list.remove((Integer)20));
		assertEquals(list.indexOf(9), 14);
		assertTrue(list.removeFirstOccurrence((Integer)9));
		assertTrue(list.contains(9));
		assertEquals(list.indexOf(9), 14);
		assertTrue(list.removeFirstOccurrence((Integer)9));
		assertFalse(list.contains(9));
		assertEquals(list.indexOf(9), -1);
		assertFalse(list.removeFirstOccurrence((Integer)9));
		assertTrue(list.remove((Integer)(-9845)));
		assertTrue(list.removeFirstOccurrence((Integer)17345095));
		assertFalse(list.remove((Integer)(-1)));
		assertFalse(list.removeFirstOccurrence((Integer)10));
		assertFalse(list.remove((Integer)16));
		assertFalse(list.remove((Integer)11));
	}
	
	private void testComparator() {
		SortedLinkedList<Integer> singleStartList = new SortedLinkedList<Integer>(1);
		singleStartList.add(7);
		singleStartList.add(3);
		singleStartList.add(7);
		assertEquals(singleStartList.get(0), 1);
		assertEquals(singleStartList.get(1), 3);
		assertEquals(singleStartList.get(2), 7);
		assertEquals(singleStartList.get(3), 7);
		singleStartList.setComparator(null);
		assertEquals(singleStartList.get(0), 1);
		assertEquals(singleStartList.get(1), 3);
		assertEquals(singleStartList.get(2), 7);
		assertEquals(singleStartList.get(3), 7);
		assertNull(singleStartList.getComparator());
		Comparator<Integer> comparator = new ReverseIntegerComparator();
		singleStartList.setComparator(comparator);
		assertEquals(singleStartList.getComparator(), comparator);
		assertEquals(singleStartList.get(0), 7);
		assertEquals(singleStartList.get(1), 7);
		assertEquals(singleStartList.get(2), 3);
		assertEquals(singleStartList.get(3), 1);
		singleStartList.setComparator(null);
		assertNull(singleStartList.getComparator());
		assertEquals(singleStartList.get(0), 1);
		assertEquals(singleStartList.get(1), 3);
		assertEquals(singleStartList.get(2), 7);
		assertEquals(singleStartList.get(3), 7);
		
		List<Integer> ints = new LinkedList<Integer>();
		ints.add(5);
		ints.add(-2);
		ints.add(0);
		ints.add(1);
		ints.add(-2);
		SortedLinkedList<Integer> multiStartList = new SortedLinkedList<Integer>(ints);
		assertEquals(multiStartList.get(0), -2);
		assertEquals(multiStartList.get(1), -2);
		assertEquals(multiStartList.get(2), 0);
		assertEquals(multiStartList.get(3), 1);
		assertEquals(multiStartList.get(4), 5);		
		multiStartList.setComparator(new ReverseIntegerComparator());
		assertEquals(multiStartList.get(0), 5);
		assertEquals(multiStartList.get(1), 1);
		assertEquals(multiStartList.get(2), 0);
		assertEquals(multiStartList.get(3), -2);
		assertEquals(multiStartList.get(4), -2);
		multiStartList.setComparator(null);
		assertEquals(multiStartList.get(0), -2);
		assertEquals(multiStartList.get(1), -2);
		assertEquals(multiStartList.get(2), 0);
		assertEquals(multiStartList.get(3), 1);
		assertEquals(multiStartList.get(4), 5);
		
		SortedLinkedList<Integer> comparableComparatorStartList = new SortedLinkedList<Integer>(comparator);
		assertEquals(comparableComparatorStartList.getComparator(), comparator);
		comparableComparatorStartList.add(1);
		comparableComparatorStartList.add(2);
		comparableComparatorStartList.add(3);
		comparableComparatorStartList.add(3);
		comparableComparatorStartList.add(4);
		assertEquals(comparableComparatorStartList.get(0), 4);
		assertEquals(comparableComparatorStartList.get(1), 3);
		assertEquals(comparableComparatorStartList.get(2), 3);
		assertEquals(comparableComparatorStartList.get(3), 2);
		assertEquals(comparableComparatorStartList.get(4), 1);
		comparableComparatorStartList.setComparator(null);
		assertNull(comparableComparatorStartList.getComparator());
		assertEquals(comparableComparatorStartList.get(0), 1);
		assertEquals(comparableComparatorStartList.get(1), 2);
		assertEquals(comparableComparatorStartList.get(2), 3);
		assertEquals(comparableComparatorStartList.get(3), 3);
		assertEquals(comparableComparatorStartList.get(4), 4);
		
		Name aa = new Name("Alpha", "Alphaeus");
		Name az = new Name("Alpha", "Zetus");
		Name za = new Name("Zeta", "Alphaeus");
		Name zz = new Name("Zeta", "Zetus");		
		SortedLinkedList<Name> comparatorStartList = new SortedLinkedList<Name>(new FirstNameComparator());
		comparatorStartList.add(az);
		comparatorStartList.add(zz);
		comparatorStartList.add(za);
		comparatorStartList.add(aa);
		comparatorStartList.add(aa);
		comparatorStartList.add(za);
		assertEquals(comparatorStartList.get(0), aa);
		assertEquals(comparatorStartList.get(1), aa);
		assertEquals(comparatorStartList.get(2), az);
		assertEquals(comparatorStartList.get(3), za);
		assertEquals(comparatorStartList.get(4), za);
		assertEquals(comparatorStartList.get(5), zz);
		comparatorStartList.setComparator(new LastNameComparator());
		assertEquals(comparatorStartList.get(0), aa);
		assertEquals(comparatorStartList.get(1), aa);
		assertEquals(comparatorStartList.get(2), za);
		assertEquals(comparatorStartList.get(3), za);
		assertEquals(comparatorStartList.get(4), az);
		assertEquals(comparatorStartList.get(5), zz);
		try {
			comparatorStartList.setComparator(null);
			fail("Should have thrown an unsupported operation exception.");
		}
		catch (IllegalArgumentException ex) {}
	}
}
