/*
 * Created on Mar 16, 2004
 */
package com.xowave.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import java.util.*;

import org.jdom.Element;

/**
 * @author bjorn
 *
 *Extends java.awt.Rectangle with added XML capabilities.
 */
public class XMLRectangle extends Rectangle {

    /**
     * 
     */
    public XMLRectangle() {
        super();
    }

    /**
     * @param r
     */
    public XMLRectangle(Rectangle r) {
        super(r);
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public XMLRectangle(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    /**Constructs a new XML Rectangle with the given defaults and overrides
     *  the defaults with whatever data is in the XML element. Finally,
     *  it checks the given rectangle to make sure it will be visible with the
     * given screenBounds.
     * @param elt  The "Rectangle" XML Element such as the one returned by this
     *  class' getXMLElement function. 
     * @param defaults the default value of the rectangle object. Usually only used if the XML element is missing information.
     * @param screenBounds the rectangle representing the screen. This is usually the value returned by
     * GraphicsConfiguration.getBounds();
     * @throws UserIllegalArgumentException if the XML Element is malformed or is not a "Rectangle" type.
     */
	public XMLRectangle( Element elt, Rectangle defaults, Rectangle screenBounds )  throws IllegalArgumentException  {
		this( elt, defaults );
		setSaneScreenBounds( screenBounds );
	}
    public XMLRectangle( Element elt, Rectangle defaults ) throws IllegalArgumentException {
    	super( defaults );
    	if( !elt.getName().equals( "Rectangle" ) )
    		throw new IllegalArgumentException( "Non-rectangle Element passed to XMLRectangle: " + elt.getName() );
    	Iterator<?> it = elt.getChildren().iterator();
    	while( it.hasNext() ) {
    		Element data = (Element) it.next();
            try {
            	String name = data.getName();
            	String text = data.getText();
                if( name.equals( "X" ) ) {
                	x = Integer.parseInt( text );
                } else if( name.equals( "Y" ) ) {
                	y = Integer.parseInt( text );
                } else if( name.equals( "Width" ) ) {
                	width = Integer.parseInt( text );
                } else if( name.equals( "Height" ) ) {
                	height = Integer.parseInt( text );
                } else {
                	System.out.println( "WARNING: unknown rectangle data: "+data.getName() );
                }
            } catch (NumberFormatException e) {
				throw new IllegalArgumentException( "Non-integer in Rectangle parameter." );
            }
    	}
    }

    /**
     * @param width
     * @param height
     */
    public XMLRectangle(int width, int height) {
        super(width, height);
    }

    /**
     * @param p
     * @param d
     */
    public XMLRectangle(Point p, Dimension d) {
        super(p, d);
    }

    /**
     * @param p
     */
    public XMLRectangle(Point p) {
        super(p);
    }

    /**
     * @param d
     */
    public XMLRectangle(Dimension d) {
        super(d);
    }
	/** Makes the bounds within the screen and sets parameters to
	 * something sane.
	 * @param screenBounds the bounds of the relevant screen.
	 */
	public void setSaneScreenBounds( Rectangle screenBounds ) {
		if( x>screenBounds.x + screenBounds.width - 5 ) x = screenBounds.x + screenBounds.width - width;
		if( y>screenBounds.y + screenBounds.height - 5 ) y = screenBounds.y + screenBounds.height - height;
		if( x<screenBounds.x ) x = screenBounds.x;
		if( y<screenBounds.y ) y = screenBounds.y;
//		if( width > screenBounds.width ) width = screenBounds.width - 10;
//		if( height > screenBounds.height ) height = screenBounds.height - 10 ;
	}
	/**
	 * Like setSaneScreenBounds except that it keeps the entire window in the screen rather than
	 * just making sure it's visible. This is appropriate for new Windows.
	 * @param screenBounds he bounds of the relevant screen.
	 */
	public void setNewWindowSaneScreenBounds( Rectangle screenBounds ) {
		if( x>screenBounds.x + screenBounds.width - 5 ) x = screenBounds.x + screenBounds.width - width;
		if( y>screenBounds.y + screenBounds.height - 5 ) y = screenBounds.y + screenBounds.height - height;
		if( x<screenBounds.x ) x = screenBounds.x;
		if( y<screenBounds.y ) y = screenBounds.y;
		if( width > screenBounds.width ) width = screenBounds.width - 10;
		if( height > screenBounds.height ) height = screenBounds.height - 10 ;
	}

	public Element getXMLElement() {
		Element elt = new Element( "Rectangle" );
		elt.addContent( new Element( "X" ).setText( Integer.toString(x) ) );
		elt.addContent( new Element( "Y" ).setText( Integer.toString(y)) );
		elt.addContent( new Element( "Width" ).setText( Integer.toString(width) ) );
		elt.addContent( new Element( "Height" ).setText( Integer.toString(height) ) );
		return elt;
	}
}
