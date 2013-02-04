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

import java.awt.FontMetrics;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringShortener {
	//FIXME: 1.1: This algorithm is optimized for Romance languages. try to extend.
	static final String vowels = "AEIOUYaeiouÀÁÂÃÄÅĀĄĂÆÈÉÊËĒĘĚĔĖÌÍÎÏĪĨĬĮİÒÓÔÕÖØŌŐŎŒÙÚÛÜŪŮŰŬŨŲÝŶŸàáâãäåāąăæèéêëēęěĕėìíîïīĩĭįıòóôõöøōŏœùúûüūůűŭũųýÿŷ";
	static Pattern endsWithNumbers = Pattern.compile("\\d+$");
	static Pattern graphemePattern = Pattern.compile("\\P{M}\\p{M}*");
	public static String getShortString( String source, int targetLength ) {
		if( source.length() <= targetLength )
			return source;
		
		// -- break the string up into graphemes
		// as described here: http://www.regular-expressions.info/unicode.html
		Vector<char[]> graphemes = new Vector<char[]>();
		//strip off initial combining marks:
		Matcher graphemeMatcher = graphemePattern.matcher( source );
		while( graphemeMatcher.find() )
			graphemes.add( graphemeMatcher.group().toCharArray() );

		if( graphemes.size() == 0 )
			return source;

		//trim white-space:
		source = source.trim();

		//first, remove lowercase vowels, unless they are the first in a word!
		String tmp = new String((char[])graphemes.get(0));
		int needToRemove = graphemes.size() - targetLength;
		int removed = 0;
		for( int i=1; i<graphemes.size() ; ++i )
			if( removed < needToRemove
				&& !Character.isWhitespace( ((char[]) graphemes.get(i-1))[0] )
				&& isVowel( ((char[]) graphemes.get(i))[0] ) )
				++removed;
			else
				tmp += ((char[]) graphemes.get(i))[0];

		source = tmp;
		if( source.length() <= targetLength )
			return source;

		//now, try removing non-whitespace, converting first letter to upper case and others to lower
		tmp = "";
		boolean shouldCap = true;
		needToRemove = source.length() - targetLength;
		removed = 0;
		for( int i=0; i<source.length(); ++i ) {
			if( !Character.isWhitespace( source.charAt(i) ) || removed >= needToRemove )
				tmp += shouldCap ? Character.toUpperCase(source.charAt(i)) : Character.toLowerCase(source.charAt(i));
			else
				++removed;	
			shouldCap = Character.isWhitespace( source.charAt(i) );
		}
		source = tmp;
		if( source.length() <= targetLength )
			return source;
		
		
		//nothing else worked, so just cut stuff. Be sure to include end numbers if possible
		tmp="";
		String numbers;
		Matcher matcher = endsWithNumbers.matcher(source);
		if( matcher.find() )
			numbers = source.substring( matcher.start() );
		else
			numbers = "";
		if( numbers.length() > targetLength )
			return numbers.substring( numbers.length() - targetLength );
		
		for( int i=0; tmp.length() + numbers.length() < targetLength; ++i )
			tmp += source.charAt(i);
		tmp += numbers;

		return tmp;
	}
	public static String getShortendStringThatFits(String string, int width, FontMetrics fm) {
		//we can ask the label's fontMetrics for a max advance, but it's ridiculously huge, so we do this instead:
		int numChars = string.length();
		while( numChars > 0 ) {
			String ret = getShortString( string, numChars );
			if( fm.stringWidth(ret) < width )
				return ret;
			numChars--;
		}
		return "";
	}
	public static boolean isVowel(char c) {
		for( int i=0; i<vowels.length(); ++i )
			if( c == vowels.charAt(i) )
				return true;
		return false;
	}
}
