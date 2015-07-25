package model;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * A sorted linked list generic class.  Requires that the generic object is a type of Comparable or that a Comparator is specified.
 * If the object is of type Comparable and a Comparator is given, the Comparator will be used for sorting.
 * Duplicate values are allowed.
 */
public class SortedLinkedList<E> extends LinkedList<E> {
	private static final long serialVersionUID = 1L;
	
	Comparator<? super E> comparator = null;
	
	/**
	 * Creates a new sorted linked list with the given comparator
	 * @param comparator comparator that can compare two objects of type E
	 */
	public SortedLinkedList(Comparator<? super E> comparator) {
		super();
		this.comparator = comparator;
	}
	
	/**
	 * Creates a new sorted linked list with one object of type Comparable.
	 * @param comparable Comparable object of type E
	 */
	@SuppressWarnings("unchecked")
	public SortedLinkedList(Comparable<? super E> comparable) {
		super();
		add((E)comparable);
	}
	
	/**
	 * Creates a new sorted linked list with a collection of Comparable object.
	 * @param comparables collection of Comparable objects of type E
	 */
	@SuppressWarnings("unchecked")
	public SortedLinkedList(Collection<? extends Comparable<? super E>> comparables) {
		super();
		for (Comparable<? super E> element : comparables)
			add((E)element);
	}
	
	/**
	 * Returns the comparator being used.
	 * @return the comparator being used or null if empty
	 */
	public Comparator<? super E> getComparator() {
		return comparator;
	}
	
	/**
	 * Sets the comparator used for sorting to the given comparator and re-sorts the list.
	 * If the given comparator is null, sorts according to the Comparable object's compare method.
	 * @param comparator comparator to set
	 * @throws IllegalArgumentException if the given comparator is null and type E is not a type of Comparable
	 */
	@SuppressWarnings("unchecked")
	public void setComparator(Comparator<? super E> comparator) {
		try {
			if (comparator == null && this.comparator != null)
				((Comparator<Comparable<? super E>>)this.comparator).getClass();
			this.comparator = comparator;
			Collections.sort(this, comparator);
		}
		catch (ClassCastException ex) {
			String message = "Cannot set the Comparator to null because the collection object does not implement Comparable; one of these two is required for the SortedLinkedList";
			throw new IllegalArgumentException("null Comparator:" + message);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean add(E e) {
		int insertIndex;
		if (comparator != null)
			insertIndex = Collections.binarySearch(this, e, comparator);
		else
			insertIndex = Collections.binarySearch((SortedLinkedList<Comparable<? super E>>)this, e);
		if (insertIndex < 0)
			insertIndex = insertIndex * -1 - 1;

		super.add(insertIndex, e);
		return true;
	}
	
	@Override
	public void add(int index, E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		for (E element : c)
			add(element);
		return true;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addFirst(E e) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addLast(E e) {
		throw new UnsupportedOperationException();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		try {
			E e = (E)o;		
			if (comparator != null)
				return (Collections.binarySearch(this, e, comparator) >= 0);
			else
				return (Collections.binarySearch((SortedLinkedList<Comparable<? super E>>)this, e) >= 0);
		}
		catch (ClassCastException ex) { return false; }
	}
	
	@Override
	public boolean offerFirst(E e) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean offerLast(E e) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void push(E e) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeFirstOccurrence(Object o) {
		return remove(o);
	}
	
	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}
}
