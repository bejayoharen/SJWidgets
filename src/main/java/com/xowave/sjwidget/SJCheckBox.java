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

/**
 * 
 */
package com.xowave.sjwidget;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

import com.xowave.sjwidget.util.BackgroundPainter;
import com.xowave.sjwidget.util.WidgetUtil;

/**
 * @author bjorn
 *
 */
public class SJCheckBox extends JCheckBox implements SJWidget, PropertyChangeListener {
	private BackgroundPainter backgroundPainter;
	
	/**
	 * Like the equivalent JCheckBox Constructor with an additional argument: ID, which sets the widget ID.
	 * @param ID the widget ID
	 */
	public SJCheckBox(String ID) {
		setWidgetID( ID );
	}

	/**
	 * Like the equivalent JCheckBox Constructor with an additional argument: ID, which sets the widget ID.
	 * @param icon the icon. set before the widget ID
	 * @param ID the widget ID
	 */
	public SJCheckBox(Icon icon, String ID) {
		super(icon);
		setWidgetID( ID );
	}

	/**
	 * Like the equivalent JCheckBox Constructor with an additional argument: ID, which sets the widget ID.
	 * @param text the text. set before the widget ID.
	 * @param ID the widget ID
	 */
	public SJCheckBox(String text, String ID) {
		super(text);
		setWidgetID( ID );
	}

	/**
	 * Like the equivalent SJCheckBox Constructor with an additional argument: ID, which sets the widget ID.
	 * @param a the initial action
	 * @param ID the widget ID
	 */
	public SJCheckBox(Action a, String ID) {
		super(a);
		setWidgetID( ID );
	}
	/**
	 * Like the equivalent JCheckBox constructor except that the widget ID is retrieved from the SJAction.
	 * @param a the initial action, which also provides a widget ID. When the ID of this action changes,
	 * so does the ID of this button.
	 */
	public SJCheckBox(SJAction a) {
		super(a);
		setWidgetID( a.getWidgetID() );
		a.addPropertyChangeListener(this);
	}
	/**
	 * Returns the current WidgetID associated with this button. May be null.
	 */
	@Override
	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	/**
	 * Returns the current widget class associated with this button. May be null.
	 */
	@Override
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}
	/**
	 * Sets the widget id associated with this widget to the given value. Any properties
	 * bound to that ID, such as border, background, text, etc, will be changed as well.
	 */
	@Override
	public SJCheckBox setWidgetID(String ID) {
		setFocusable( WidgetUtil.allowFocus() );
		setBackground( TRANSPARENT );
		WidgetUtil.registerAndSetup(this, ID);
		setOpaque( getBackground().getAlpha() == 255 );
		return this;
	}
	@Override
	public void paintComponent( Graphics g ) {
		if( backgroundPainter != null ) {
			backgroundPainter.paintBackground(g,this);
		} else {
			super.paintComponent(g);
		}
	}
	
	boolean pressedIconSet = false;
	@Override
	public void setPressedIcon( Icon icn ) {
		super.setPressedIcon(icn);
		pressedIconSet = true;
	}
	
	@Override
	public void setIcon( Icon icn ) {
		super.setIcon(icn);
		if( !pressedIconSet && icn != null ) {
			//default pressed icon is 50% transparent version of regular icon.
			BufferedImage img  = new BufferedImage(icn.getIconWidth(), icn.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = img.getGraphics();
			icn.paintIcon(this, g, 0, 0);
			for( int i=0; i<img.getWidth(); ++i ) {
				for( int j=0; j<img.getHeight(); ++j ) {
					int rgb = img.getRGB(i, j);
					img.setRGB(i, j, ( ( ( ( rgb >> 24 ) & 0x000000ff ) / 2 ) << 24 )
							| ( rgb & 0x00ff0000 )
							| ( rgb & 0x0000ff00 )
							| ( rgb & 0x000000ff )  );
				}
			}
			super.setPressedIcon( new ImageIcon(img) );
		}
	}

	@Override
	public void setBackgroundPainter(BackgroundPainter bp) {
		backgroundPainter = bp;
	}
	/**
	 * overrides the value returned by getWidgetText().
	 * Generally, this function should not be used by clients;
	 * rather it is used internally when setting the text
	 * programatically because the widget's ID has changed.
	 */
	@Override
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
	/**
	 * Retrieves the text originally associated with this
	 * widget by the given ID. This is useful in cases where
	 * the text in the widgets xml file is a template or
	 * prefix.
	 */
	@Override
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		setWidgetID( ((SJAction)getAction()).getWidgetID() );
	}
}
