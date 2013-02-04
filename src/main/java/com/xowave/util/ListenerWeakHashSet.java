/**
 *
 * This file is part of the SJWidget library.
 * (c) 2005-2012 Bjorn Roche
 * Development of this library has been supported by Indaba Media (http://www.indabamusic.com)
 * and XO Audio (http://www.xoaudio.com)
 *
 * for copyright and sharing permissions, please see the COPYING.txt file which you should
 * have recieved with this file.
 *
 */

package com.xowave.util;

import java.util.*;

/**
 * This class encapsulates a WeakHashMap and exposes only the methods that are safe and
 * required for use as a data structure for tracking listeners. Also provides a static method
 * iterating through the keys of a weakHashMap without fear that the keys will be garbage collected
 * during iteration, which would cause a ConcurrentModificationException.
 *
 */
public final class ListenerWeakHashSet<T> implements Iterable<T>
{
	WeakHashMap<T,Object> data = new WeakHashMap<T,Object>();
	
	public void add( T obj ) {
		data.put( obj, null );
	}
	public void remove( T obj ) {
		data.remove( obj );
	}
	/**
	 * This returns a statically allocated iterator that is backed by a vector. The vector
	 * makes it safe against concurrent modification exceptions, but after calling
	 * this function, you should iterate fully through the returned Iterator
	 * for garbage collection to be fully optimized.
	 * @return An Iterator that would otherwise return a concurrent modification
	 * exception. remove() is not supported.
	 */
	public Iterator<T> resetIterator() {
		while( true ) {
			try {
				reusedIt.reset( data );
				break;
			} catch (ConcurrentModificationException cme) {
				//this should never ever happen, actually.
				System.out.println( "WARNING: ConcurrentModificationException Occurred. Retrying..." );
			}
		}
		return reusedIt;
	}
	public Iterator<T> iterator() {
		return resetIterator();
	}
	public Object[] toArray() {
		return data.keySet().toArray();
	}
	MapIterator<T> reusedIt = new MapIterator<T>(true);
	/* @deprecated use MapIterator instead. */
	public static Iterator<UUID> safeKeyIterator( WeakHashMap<UUID,? extends Object> m ) {
		return new MapIterator<UUID>(m,false);
	}
	public boolean isEmpty() {
		return data.isEmpty();
	}
	public void clear() {
		data.clear();
	}
}
