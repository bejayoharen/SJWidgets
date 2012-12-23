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

/*
 * Created on Jun 15, 2005 by bjorn
 *
 */
package com.xowave.util;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author bjorn
 *
 * Provides some utilities for working with XML. see also Base64 for binary to base64 conversion.
 */
public class XMLUtil {
	private static SAXBuilder parser = new SAXBuilder(false);
	private static XMLOutputter HTMLOutputter;
	private static XMLOutputter CompactXMLOutputter;
	private static XMLOutputter BeautifulXMLOutputter;
	private static XMLOutputter RawXMLOutputter;
	static {
		Format f = Format.getRawFormat();
		f.setOmitDeclaration(true);
		f.setOmitEncoding(true);
		f.setExpandEmptyElements(true);
		HTMLOutputter = new XMLOutputter(f);
		
		// note: default encoding is UTF-8
		f = Format.getCompactFormat();
		f.setOmitDeclaration(true);
		f.setOmitEncoding(true);
		f.setExpandEmptyElements(false);
		CompactXMLOutputter = new XMLOutputter(f);
		
		f = Format.getPrettyFormat();
		f.setOmitDeclaration(true);
		f.setOmitEncoding(true);
		f.setExpandEmptyElements(false);
		BeautifulXMLOutputter = new XMLOutputter(f);
		
		f = Format.getRawFormat();
		f.setOmitDeclaration(true);
		f.setOmitEncoding(true);
//		f.setExpandEmptyElements(false);
		RawXMLOutputter = new XMLOutputter(f);
		
		parser.setEntityResolver( new EntityResolver() {
			public InputSource resolveEntity(String arg0, String arg1) throws SAXException, IOException {
				return new InputSource( new StringReader("") );
			}
		});
	}
	public static synchronized Element getXMLForString( String string ) throws JDOMException {
		Document doc;
		try {
			doc = parser.build( new StringReader( string ) );
			Element elt = doc.getRootElement();
			elt.detach();
			return elt;
		} catch( EOFException eof) {
			throw new JDOMException( "Unexpected end of string." );
		} catch( IOException ioe ) {
			throw new RuntimeException("Unexpected IO Exception.");
		}
	}
	public static synchronized Element getXMLForStream( InputStream is ) throws JDOMException, IOException {
		Document doc;
		doc = parser.build( is );
		Element ret = doc.getRootElement();
		ret.detach();
		return ret;
	}
	public static synchronized Element getXMLForFile( File f ) throws JDOMException, IOException {
		Document doc;
		doc = parser.build( new BufferedInputStream(new FileInputStream(f)) );
		return doc.getRootElement();
	}
	public static synchronized String getCompactStringForXML( Element elt ) {
		if( elt == null )
			return null;
		Document doc = new Document( elt );
		String ret = CompactXMLOutputter.outputString(doc);
		doc.removeContent(elt);
		return ret;
	}
	public static synchronized void compactOutput( Element elt, OutputStream out ) throws IOException {
		CompactXMLOutputter.output( elt, out );
	}
	public static synchronized String getBeautifulStringForXML(Element elt) {
		if( elt == null )
			return null;
		Document doc = new Document( elt );
		String ret = BeautifulXMLOutputter.outputString(doc);
		doc.removeContent(elt);
		return ret;
	}
	public static String getRawStringForXML(Element elt) {
		if( elt == null )
			return null;
		Document doc = new Document( elt );
		String ret = RawXMLOutputter.outputString(doc);
		doc.removeContent(elt);
		return ret;
	}
	/**Returns a complete HTML document string represented by the given element. */
	public static synchronized String getHTML( Element elt ) {
		if( elt == null )
			return null;
		Element clone = (Element) elt.clone();
		clone.setName("body");
		Element root = new Element( "html" );
		root.addContent( clone );
		Document doc = new Document( root );
		String ret = HTMLOutputter.outputString(doc);
		doc.removeContent(elt);
		return ret;
	}
	/** Parses the text in the given element, or returns dflt if this could not be done.
	 * The element may be null. No warning messages are printed.
	 * @param elt the element whose text will be parsed
	 * @param dflt the value if the element is null, has no text, or if the text is un-parsable.
	 * @return the int value of the text of element or the given default value
	 */
	public int parseIntOr( Element elt, int dflt ) {
		return parseIntOrWarn( elt, dflt, false );
	}
	/** Parses the text in the given element, or returns dflt if this could not be done.
	 * The element may be null. Prints a warning if there's trouble parsing, but otherwise behaves the same
	 * as parseIntOr. Does not print a warning if the given element is null.
	 * @param elt the element whose text will be parsed
	 * @param dflt the value if the element is null, has no text, or if the text is un-parsable.
	 * @return the int value of the text of element or the given default value
	 */
	public static int parseIntOrWarn( Element elt, int dflt, boolean warn ) {
		if( elt == null )
			return dflt;
		try {
			return Integer.parseInt( elt.getText() );
		} catch ( NumberFormatException nfe ) {
			if( warn )
				System.out.println( "WARNING: integer expected, but found: " + elt.getText() );
			return dflt;
		}
	}
	public static String encodeHtml(String s)
	{
	    StringBuffer out = new StringBuffer();
	    for(int i=0; i<s.length(); i++)
	    {
	        char c = s.charAt(i);
	        if(c > 127 || c=='"' || c=='<' || c=='>')
	        {
	           out.append("&#"+(int)c+";");
	        }
	        else
	        {
	            out.append(c);
	        }
	    }
	    return out.toString();
	}
}
