package com.xowave.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public final class MapIterator<T> implements Iterator<T>, Iterable<T> {
	private final Vector<T> vector = new Vector<T>();
	private int index=0;
	private final boolean reuse;
	public MapIterator(boolean reuse) {
		this.reuse = reuse;
	}
	/**
	 * This guards against possible concurrent modification exceptions that can happen when building from a weak
	 * hash map.
	 * @param m
	 */
	public MapIterator( Map<T,?> m, boolean reuse ) {
		this.reuse = reuse;
		while( true ) {
			try {
				reset( m ) ;
				break;
			} catch (ConcurrentModificationException cme) {
				System.out.println( "WARNING: ConcurrentModificationException Occurred. Retrying..." );
			}
		}
	}
	/**
	 * Sets up the iterator with the given data. Note that this can throw a concurrent mod exception
	 * with weak hash maps.
	 * @param m
	 */
	void reset( Map<T,?> m ) {
		vector.clear();
		vector.ensureCapacity( m.size() );
		for( Iterator<T> it = m.keySet().iterator(); it.hasNext(); ) {
			T obj;
			try {
				obj = it.next();
			} catch( Exception e ) {
				throw new ConcurrentModificationException( "Failed to get next element." );
			}
			if( obj != null )
				vector.add( obj );
		}
		if( vector.capacity() > vector.size() * 4 )
			vector.trimToSize();//Don't let size get out of control just because it got big once!
		index = 0;
	}
	public boolean hasNext() {
		return index < vector.size();
	}
	public T next() {
		++index;
		T ret = vector.get(index-1);
		vector.set(index-1, null); //gc
		if( index == vector.size() && !reuse)
			vector.clear();
		return ret;
	}
	public void remove() {
		throw new UnsupportedOperationException();
	}
	public Iterator<T> iterator() {
		return this;
	}
}
