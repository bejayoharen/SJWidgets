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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;

/**
 * XColor - extends color to have some basic methods that Color should have,
 * and maybe, if needed some extra utility methods.
 *
 * @created Aug 11, 2009
 * @author bjorn
 */
public class XColor extends Color {
	public static final Color TRANSPARENT = new Color( 0, 0, 0, 0 );
    public XColor( Color c ) {
        super( c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() );
    }
    public XColor( float r, float g, float b ) {
        super( r, g, b );
    }
    public XColor( float r, float g, float b, float a ) {
        super( r, g, b, a );
    }
    public XColor( int r, int g, int b, int a ) {
        super( r, g, b, a );
    }
	public static Color decodeWithAlpha(int colorSpec, float f) {
    	Color c = new Color( colorSpec );
    	return new Color( c.getRed(), c.getGreen(), c.getBlue(), (int)(f*255+.5f) );
	}
    public static Color decodeWithAlpha(String colorSpec, float f) {
    	Color c = Color.decode( colorSpec );
    	return new Color( c.getRed(), c.getGreen(), c.getBlue(), (int)(f*255+.5f) );
	}
	/** Opposite of decode. Convenient for html format. #XXXXXX format */
    public String encode() {
    	return encode( this );
    }
    public static String encode( Color c ) {
        return "#" + twoChar( Integer.toHexString(c.getRed()) )
                   + twoChar( Integer.toHexString(c.getGreen()) )
                   + twoChar( Integer.toHexString(c.getBlue()) ) ;
    }

    private static String twoChar( String in ) {
        if( in.length() >= 2 )
            return in;
        return "0" + in;
    }
    /** returns a blended color. the larger the ratio, the more of a, and the less b. The smaller the ratio, the less a, and the more b. */
	public static XColor blend( Color a, Color b, float ratio ) {
		float ratiob = 1 - ratio ;
		if( a == null )
			a = Color.BLACK;
		if( b == null )
			b = Color.BLACK;
		return new XColor(
				(int) ( a.getRed()   * ratio + b.getRed()   * ratiob ),
				(int) ( a.getGreen() * ratio + b.getGreen() * ratiob ),
				(int) ( a.getBlue()  * ratio + b.getBlue()  * ratiob ),
				(int) ( a.getAlpha() * ratio + b.getAlpha() * ratiob )
				);
	}
	public static Color setAlpha(Color c, int newAlpha ) {
		return new Color( c.getRed(), c.getGreen(), c.getBlue(), newAlpha );
	}
	public static Color setAlpha(Color c, float newAlpha ) {
		return new Color( c.getRed(), c.getGreen(), c.getBlue(), (int)( newAlpha*255 + .5 ) );
	}
	public static Color simpleOposite(Color f) {
		return new Color( 255 - f.getRed(), 255 - f.getGreen(), 255 - f.getBlue() );
	}
	public static BufferedImage blendImages(ImageIcon iconA, ImageIcon iconB, float iconARatio, ImageObserver io ) {
		final int w = Math.max( iconA.getIconWidth(), iconB.getIconWidth() );
		final int h = Math.max( iconA.getIconHeight(), iconB.getIconHeight() );
		BufferedImage bi1 = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		bi1.getGraphics().drawImage(iconA.getImage(), 0, 0, io);
		BufferedImage bi2 = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		bi2.getGraphics().drawImage(iconB.getImage(), 0, 0, io);
		BufferedImage ret = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		for( int j=0; j<w; ++j ) {
			for( int k=0; k<h; ++k ) {
				int rgb1 = bi1.getRGB(j, k);
				int rgb2 = bi2.getRGB(j, k);
				float iconBRatio = 1 - iconARatio;
				int a = (int) ( ( ( ( (float) ( ( rgb1 >> 24 ) & 0x000000ff ) ) ) * iconARatio + ( (float) ( ( rgb2 >> 24 ) & 0x000000ff ) ) * iconBRatio ) );
				int r = (int) ( ( ( ( (float) ( ( rgb1 >> 16 ) & 0x000000ff ) ) ) * iconARatio + ( (float) ( ( rgb2 >> 16 ) & 0x000000ff ) ) * iconBRatio ) );
				int g = (int) ( ( ( ( (float) ( ( rgb1 >>  8 ) & 0x000000ff ) ) ) * iconARatio + ( (float) ( ( rgb2 >>  8 ) & 0x000000ff ) ) * iconBRatio ) );
				int b = (int) ( ( ( ( (float) ( ( rgb1 >>  0 ) & 0x000000ff ) ) ) * iconARatio + ( (float) ( ( rgb2 >>  0 ) & 0x000000ff ) ) * iconBRatio ) );
				int rgb = a << 24 | r << 16 | g <<  8 | b <<  0 ;
				ret.setRGB(j, k, rgb);
			}
		}
		return ret;
	}
	/** returns a float from 0 to 255 representing the gray level of the given color. */
	public static float grayLevel(Color c) {
		return ( c.getRed() + c.getGreen() + c.getBlue() ) / 3f;
	}
}
