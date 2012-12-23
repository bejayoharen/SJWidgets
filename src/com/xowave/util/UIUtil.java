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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

public class UIUtil {
	
	private static RenderingHints FAST_RENDER_HINTS = new RenderingHints(null);
	private static RenderingHints BEAUTIFUL_RENDER_HINTS = new RenderingHints(null);
	static {
		FAST_RENDER_HINTS.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
		FAST_RENDER_HINTS.put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		FAST_RENDER_HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		FAST_RENDER_HINTS.put(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_DISABLE);
		FAST_RENDER_HINTS.put(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		FAST_RENDER_HINTS.put(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		FAST_RENDER_HINTS.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED);
		FAST_RENDER_HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
		FAST_RENDER_HINTS.put(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_SPEED);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_ENABLE);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	}
	
	/** performs the annoying calculation required for centering text in a box of size d.
	 * Try to reuse this result as calculating string bounds is not terribly efficient.
	 * */
	public static Point2D getCenteredText( Graphics g, Font f, Dimension d, String s ) {
		// Find the size of string s in font f in the current Graphics context g.
		FontMetrics fm   = g.getFontMetrics(f);
		java.awt.geom.Rectangle2D rect = fm.getStringBounds(s, g);
	
		// Center text horizontally and vertically
		double x = (d.getWidth()  - rect.getWidth())  / 2;
		double y = (d.getHeight() - rect.getHeight()) / 2  + fm.getAscent();
	
		return new Point2D.Double( x, y );
	}
	
	/** Sets the given graphics object to draw with anti-aliasing and other beautiful options. */
	public static void setBeautifulRendering( Graphics2D g ) {
		g.setRenderingHints(BEAUTIFUL_RENDER_HINTS);
	}
	/** Sets the given graphics object to draw with options that will probably go as fast as possible. */
	public static void setPerformaceRendering( Graphics2D g ) {
		g.setRenderingHints(FAST_RENDER_HINTS);
	}
}
