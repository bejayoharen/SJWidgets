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

package com.xowave.sjwidget;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;

import com.xowave.sjwidget.help.HelpComponentRegistry;
import com.xowave.sjwidget.util.BackgroundPainter;
import com.xowave.sjwidget.util.WidgetUtil;

/**
 * SJRadioButton is like a standard swing JRadioButton except that it allows you
 * to set a widget ID (or assign one at construction).
 * 
 * This ID can be used to define features of the component, such as borders, background colors, icons and so on, in a separate XML file rather than code.
 * 
 * @author bjorn
 *
 */
public class SJRadioButton extends JRadioButton implements SJWidget, PropertyChangeListener {
	private BackgroundPainter backgroundPainter;
	
	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param ID the widget ID
	 */
	public SJRadioButton(String ID) {
		setWidgetID( ID );
	}

	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param icon the icon. Set before the ID.
	 * @param ID the widget ID
	 */
	public SJRadioButton(Icon icon, String ID) {
		super(icon);
		setWidgetID( ID );
	}

	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param text the text. Set before the ID.
	 * @param ID the widget ID
	 */
	public SJRadioButton(String text, String ID) {
		super(text);
		setWidgetID( ID );
	}

	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param a the action. Set before the ID.
	 * @param ID the widget ID
	 */
	public SJRadioButton(Action a, String ID) {
		super(a);
		setWidgetID( ID );
	}
	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param a the SJAction. The ID is retrieved from this and monitored in case of changes.
	 */
	public SJRadioButton(SJAction a) {
		super(a);
		setWidgetID( a.getWidgetID() );
		a.addPropertyChangeListener(this);
	}

	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param text the text. Set before the ID.
	 * @param icon the icon. Set before the ID.
	 * @param a the SJAction. The ID is retrieved from this and monitored in case of changes.
	 */
	public SJRadioButton(String text, Icon icon, String ID) {
		super(text, icon);
		setWidgetID( ID );
	}
	@Override
	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	@Override
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}
	@Override
	public SJRadioButton setWidgetID(String ID) {
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
	
	private boolean pressedIconSet = false;
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
	@Override	
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
	@Override
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		setWidgetID( ((SJAction)getAction()).getWidgetID() );
	}
}
