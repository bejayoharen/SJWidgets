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

package com.xowave.sjwidget.util;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ImageCapabilities;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

/**
 * Background painter is a class that assists in painting complex backgrounds like gradients.
 * 
 * @author bjorn
 */
public final class BackgroundPainter {
	/**
	 * Describes how background images should be tiled.
	 * 
	 * @author bjorn
	 */
	public enum ImageStyle {
		/** Tile the background image */
		TILE,
		/** Center the background image. Also generally ensures that the
		 * component's minimum size is at least as large as the image.
		 */
		CENTER,
		/** Center the image but don't enfore any component sizing based on the image size */
		CENTER_IGNORE_SIZE;
		Dimension getRecommendedMinimumSize(ImageIcon ii) {
			switch(this) {
			case TILE:
			case CENTER_IGNORE_SIZE:
				return new Dimension(0,0);
			case CENTER:
				return new Dimension(ii.getIconWidth(),ii.getIconHeight());
			}
			throw new RuntimeException();
		}
	}
	private VolatileImage vImage;
	private final int orientation;
	private final Color backgroundColor, color1, color2;
	private final ImageIcon backgroundIcon;
	private final ImageStyle imageStyle;
	private final AlphaComposite composite;
	private GradientPaint gp;
	private TexturePaint tp;
	
	/**
	 * 
	 * @param orientation vertical or horizontal gradient. SwingConstants.VERTICAL or SwingConstants.HORIZONTAL
	 * @param topLeft either the top color or the left color
	 * @param bottomRight either the bottom or the right color
	 * @param backgroundColor the background color if a gradient is not used
	 * @param backgroundIcon The icon to be used for the bakcground
	 * @param imageStyle Is the icon to be tiled, centered, etc?
	 * @param composite How do we composite the background color and the background images?
	 */
	public BackgroundPainter(int orientation, Color topLeft, Color bottomRight, Color backgroundColor, ImageIcon backgroundIcon, ImageStyle imageStyle, AlphaComposite composite ) {
		this.orientation = orientation;
		this.backgroundColor = backgroundColor;
		this.color1 = topLeft;
		this.color2 = bottomRight;
		this.backgroundIcon = backgroundIcon;
		this.imageStyle = imageStyle;
		this.composite = composite;
	}
	/**
	 * Determine the minimum size based on the the background image. If no background image is set, returns (0,0)
	 */
	public Dimension getRecommendedMinimumSize() {
		if( backgroundIcon == null )
			return new Dimension(0,0);
		else
			return imageStyle.getRecommendedMinimumSize(backgroundIcon);
	}

	/**
	 * Here is where the actual painting happens. If sizing hasn't changed,
	 * the image can often be blitted onto the screen.
	 */
	public void paintBackground(Graphics gg, Component c) {
		Graphics2D g = (Graphics2D) gg;
		int w = c.getWidth();
		int h = c.getHeight();
//		Shape clip = g.getClip();
//		if( c instanceof javax.swing.JComponent ) {
//			Insets in = ((javax.swing.JComponent)c).getInsets();
//			g.clipRect(in.left, in.top, w - in.left - in.right, h - in.top - in.bottom );
//		}
		
		if( vImage == null || vImage.getWidth() != w || vImage.getHeight() != h )
			drawImage(g,c,w,h);

		do {
			if( vImage.contentsLost() ) {
				vImage = null;
				drawImage(g,c,w,h);
			}
			g.drawImage(vImage, 0, 0, c);
		} while( vImage.contentsLost() );
//		g.setClip(clip);
	}
	
	private void drawImage( Graphics2D gg, Component c, int w, int h ) {
		if( w <= 0 )
			w = 1;
		if( h <= 0 )
			h = 1;
		if( vImage == null || vImage.getWidth() != w || vImage.getHeight() != h )
			try {
				vImage = gg.getDeviceConfiguration().createCompatibleVolatileImage( w, h, new ImageCapabilities(true), Transparency.TRANSLUCENT);
			} catch (AWTException e) {
				try {
					vImage = gg.getDeviceConfiguration().createCompatibleVolatileImage( w, h, new ImageCapabilities(false), Transparency.TRANSLUCENT);
				} catch(AWTException e2) {
					throw new RuntimeException();
				}
			}

		Graphics2D g = vImage.createGraphics();
		
		Composite orig = g.getComposite();
		// These commands cause the Graphics2D object to clear to (0,0,0,0).
		g.setComposite(AlphaComposite.Src);
		g.setColor(new Color(0,0,0,0));
		g.fillRect(0, 0, vImage.getWidth(), vImage.getHeight());
		
		g.setComposite(composite == null ? orig : composite);

		// now draw the background:
		if( backgroundColor != null ) {
			g.setColor(backgroundColor);
			g.fillRect(0, 0, w, h);
		}
		if( backgroundIcon != null ) {
			switch( imageStyle ) {
			case TILE:
				if( tp == null ) {
					BufferedImage bi = new BufferedImage(
							backgroundIcon.getIconWidth(),
							backgroundIcon.getIconHeight(),
							BufferedImage.TYPE_INT_ARGB );
					bi.getGraphics().drawImage(backgroundIcon.getImage(), 0, 0, null);
					tp = new TexturePaint(bi, new Rectangle(backgroundIcon.getIconWidth(),backgroundIcon.getIconHeight()));
				}
				if( tp != null ) {
					g.setPaint(tp);
					g.fillRect(0, 0, w, h);
				}
				break;
			case CENTER:
				int x = ( c.getWidth()  - backgroundIcon.getIconWidth()  ) / 2 ;
				int y = ( c.getHeight() - backgroundIcon.getIconHeight() ) / 2 ;
				g.drawImage(backgroundIcon.getImage(), x, y, null);
				break;
			}
		}
		
		if( gp == null && color1 != null && color2 != null ) {
			if( orientation == SwingConstants.VERTICAL )
				gp = new GradientPaint(0, 0, color1, 0, h, color2);
			else
				gp = new GradientPaint(0, 0, color1, w, 0, color2);
		}
		if( gp != null ) {
			g.setPaint(gp);
			g.fillRect(0, 0, w, h);
		}
	}
}