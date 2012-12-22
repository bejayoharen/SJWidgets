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
 * This is a wrapper around an XML Element for an apple style dictionary, "dict", element.
 */
public class XMLDict {
	private Element elt = null;
	private Hashtable<String,Object> data = null;
	private Hashtable<String,String> types = null;
	private final boolean cleanSubTree;
	/**
	 * Parses the element into a local hashtable for fast retrieval later. creates and parses sub
	 * dicts as needed, but all other data is held and returned as strings.
	 * @param elt the dict element to parse.
	 * @param initilParseDepth data can be parsed now or later to the specified depth. setting this low will reduce initial memory bloat
	 * @param cleanSubTree if true, keeps the JDOM tree as clean as possible as it parses through the dictionary/array subtrees.
	 * @throws XMLException if the element is not a dict or if it is malformed or if there are multiple
	 * entries with the same key.
	 */
	public XMLDict( Element elt, int initialParseDepth, boolean cleanSubTree ) throws XMLException {
		if( !elt.getName().equals("dict") )
			throw new XMLException( "dict element expected" );
		
		this.elt = elt;
		this.cleanSubTree = cleanSubTree;
		if( initialParseDepth <=0 ) {
			return;
		} else {
			parseNow(initialParseDepth);
		}
	}
	private void parseIfNeeded() throws XMLException {
		if( data == null )
			parseNow(1);
	}
	private void parseNow(int parseDepth) throws XMLException {
		data = new Hashtable<String,Object>();
		types = new Hashtable<String,String>();
		
		Iterator<?> it = elt.getChildren().iterator();
		while( it.hasNext() ) {
			Element key = (Element) it.next();
			if( !key.getName().equals("key"))
				throw new XMLException( "Key expected in dict: " + key.getName() );
			if( !it.hasNext() )
				throw new XMLException( "Missing Value after key: " + key.getText() );
			Element value = (Element) it.next();
			if( data.get( key.getText() ) != null )
				throw new XMLException( "Duplicate key: " + key.getText() );
			
			if( value.getName().equals( "dict" ) ) {
				data.put( key.getText(), new XMLDict( value, parseDepth-1, cleanSubTree ) );
			} else if( value.getName().equals( "array" ) ) {
				data.put( key.getText(), new XMLArray( value, parseDepth-1, cleanSubTree ) );
			} else {
				data.put( key.getText(), value.getText() );
			}
			types.put( key.getText(), value.getName() );
		}
		if( cleanSubTree )
			elt.detach();
		elt = null;
	}
	public Set<String> keySet() {
		return data.keySet();
	}
	public String getExpectedType( String key ) {
		return (String) types.get(key);
	}
	/**
	 * returns the value corresponding to the key. If there is no such key,
	 * null is returned. If the type of the data
	 * is not the same as the expected type, an XMLException is thrown.
	 * If a dict is requested, a run-ime exception is thrown.
	 * @param key the key to look for
	 * @param expectedType the type that the4 value must match in order to not throw an exception.
	 * @return the value, or null if there is no such key.
	 * @throws XMLException if the type does not match the expected type.
	 */
	public String getStringValue( String key, String expectedType ) throws XMLException {
		if( expectedType.equals("dict") )
			throw new RuntimeException( "Dict requested where only string data is supported." );
		parseIfNeeded();
		if( data.get( key ) == null )
			return null;
		if( !types.get( key ).equals( expectedType ) )
			throw new XMLException( "Wrong type. " + expectedType
					+ " expected but " + types.get( key ) + "was used." );
		return (String) data.get( key );
	}
	/**
	 * returns an XMLDict object that is a sub dictionary of this dictionary with the given key.
	 * If there is no such key, null is returned. If there is such a key, but it is not a dict,
	 * an XMLException is thrown.
	 * 
	 * @param key the key to look for
	 * @return an XMLDict element referred to by the key, or null if there is no such key.
	 * @throws XMLException if the element exists but is not a dict.
	 */
	public XMLDict getSubDict( String key ) throws XMLException {
		parseIfNeeded();
		if( data.get( key ) == null )
			return null;
		if( !types.get( key ).equals( "dict" ) )
			throw new XMLException( key + " does not refer to a dict type: " + types.get( key ) );
		return (XMLDict) data.get( key );
	}
	/**
	 * returns an XMLArray object that is a sub array of this dictionary with the given key.
	 * If there is no such key, null is returned. If there is such a key, but it is not an array,
	 * an XMLException is thrown.
	 * 
	 * @param key the key to look for
	 * @return an XMLArray element referred to by the key, or null if there is no such key.
	 * @throws XMLException if the element exists but is not an array.
	 */
	public XMLArray getSubArray( String key ) throws XMLException {
		parseIfNeeded();
		if( data.get( key ) == null )
			return null;
		if( !types.get( key ).equals( "array" ) )
			throw new XMLException( key + " does not refer to an array type: " + types.get( key ) );
		return (XMLArray) data.get( key );
	}
	public void printData() throws XMLException {
		System.out.println( "========" );
		printData(0);
		System.out.println( "========" );
	}
	void printData( int indent ) throws XMLException {
		parseIfNeeded();
		Iterator<?> it = data.keySet().iterator();
		while( it.hasNext() ) {
			String key = (String) it.next();
			for( int i=0; i<indent; ++i )
				System.out.print( "\t" );
			System.out.print( key + "(" + types.get(key) + ") : " );
			if( data.get(key) instanceof XMLDict ) {
				System.out.println();
				((XMLDict)data.get(key)).printData(indent+1);
			} else if( data.get(key) instanceof XMLArray ) {
				System.out.println();
				((XMLArray)data.get(key)).printData(indent+1);
			} else {
				System.out.println( data.get(key) );
			}
		}
	}
}
