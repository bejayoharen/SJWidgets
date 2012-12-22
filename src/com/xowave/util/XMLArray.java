/*
 * Created on Jun 15, 2005 by bjorn
 *
 */
package com.xowave.util;

import java.util.*;

import org.jdom.Element;

/**
 * @author bjorn
 *
 * This is a wrapper around an XML Element for an apple style array, "array", element.
 */
public class XMLArray {
	private Element elt=null;
	private Vector<Object> data=null;
	private final boolean cleanSubTree;
	/**
	 * Parses the element into a local vector for fast retrieval later. Creates and parses sub
	 * dicts/arrays as needed, but all other data is held and returned as strings.
	 * @param elt the array element to parse.
	 * @throws XMLException if the element is not an array or if it is malformed.
	 */
	public XMLArray( Element elt, int initialParseDepth, boolean cleanSubTree ) throws XMLException {
		if( !elt.getName().equals("array") )
			throw new XMLException( "array element expected" );
		this.cleanSubTree = cleanSubTree;
		this.elt = elt;
		if( initialParseDepth <= 0 ) {
			return;
		}
		parseNow(initialParseDepth);
	}
	private void parseIfNeeded() throws XMLException {
		if( data == null )
			parseNow(1);
	}
	private void parseNow(int parseDepth) throws XMLException {
		data = new Vector<Object>( elt.getChildren().size() );
		Iterator<?> it = elt.getChildren().iterator();
		while( it.hasNext() ) {
			Element e = (Element) it.next();
			
			if( e.getName().equals( "array") )
				data.add( new XMLArray(e, parseDepth-1, cleanSubTree) );
			else if( e.getName().equals( "dict" ) )
				data.add( new XMLDict(e, parseDepth-1, cleanSubTree) );
			else
				data.add( e );
		}
		if( cleanSubTree )
			elt.detach();
		elt = null;
	}
	public int getSize() {
		if( data != null )
			return data.size();
		return elt.getChildren().size();
	}
	/**
	 * returns an XMLDict object that is a sub dictionary of this dictionary with the given key.
	 * If the given element is not a dict,
	 * an XMLException is thrown.
	 * 
	 * @param key the key to look for
	 * @return an XMLDict element referred to by the key, or null if there is no such key.
	 * @throws XMLException if the element exists but is not a dict.
	 */
	public XMLDict getSubDict( int i ) throws XMLException {
		parseIfNeeded();
		if( data.get( i ) instanceof XMLDict )
			return (XMLDict) data.get(i);
		throw new XMLException( i + " is not the index of a dict type: " + data.get( i ).getClass() );
	}
	public void printData() throws XMLException {
		System.out.println( "========" );
		printData(0);
		System.out.println( "========" );
	}
	void printData( int indent ) throws XMLException {
		parseIfNeeded();
		for( int i=0; i<data.size(); ++i ) {
			Object obj = data.get( i );
			for( int j=0; j<indent; ++j )
				System.out.print( "\t" );
			System.out.print( "[" + i + "]:" );
			if( obj instanceof XMLDict ) {
				System.out.println();
				((XMLDict)obj).printData(indent+1);
			} else if( obj instanceof XMLArray ) {
				System.out.println();
				((XMLArray)obj).printData(indent+1);
			} else {
				System.out.println( obj );
			}
		}
	}
}
