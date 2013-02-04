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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.metal.MetalButtonUI;

import com.xowave.sjwidget.util.BackgroundPainter;
import com.xowave.sjwidget.util.WidgetUtil;

/**
 * SJButton is like a standard swing JButton except that it allows you
 * to set a widget ID (or assign one at construction).
 * 
 * This ID can be used to define features of the component, such as borders, background colors, icons and so on, in a separate XML file rather than code.
 * 
 * @author bjorn
 *
 */
public class SJButton extends JButton implements SJWidget, PropertyChangeListener {
	private BackgroundPainter backgroundPainter;
	
	/**
	 * Like the equivalent JButton Constructor with an additional argument: ID, which sets the widget ID.
	 * @param ID the widget ID
	 */
	public SJButton(String ID) {
		setWidgetID( ID );
	}

	/**
	 * Like the equivalent JButton Constructor with an additional argument: ID, which sets the widget ID.
	 * @param icon the default icon
	 * @param ID the widget ID, set after the icon
	 */
	public SJButton(Icon icon, String ID) {
		super(icon);
		super.setSelectedIcon( icon );
		setWidgetID( ID );
	}

	/**
	 * Like the equivalent JButton Constructor with an additional argument: ID, which sets the widget ID.
	 * @param text the default text
	 * @param ID the widget ID, set after the text
	 */
	public SJButton(String text, String ID) {
		super(text);
		setWidgetID( ID );
	}

	/**
	 * Like the equivalent JButton Constructor with an additional argument: ID, which sets the widget ID.
	 * @param a the initial action
	 * @param ID the widget ID
	 */
	public SJButton(Action a, String ID) {
		super(a);
		setWidgetID( ID );
	}
	
	/**
	 * Like the equivalent JButton constructor except that the widget ID is retrieved from the SJAction.
	 * @param a the initial action, which also provides a widget ID. When the ID of this action changes,
	 * so does the ID of this button.
	 */
	public SJButton(SJAction a) {
		super(a);
		setWidgetID( a.getWidgetID() );
		a.addPropertyChangeListener(this);
	}

	/**
	 * Like the equivalent JButton Constructor with an additional argument: ID, which sets the widget ID.
	 * @param text the initial text
	 * @param icon the initial icon
	 * @param ID the widget ID, set after the icon and text
	 */
	public SJButton(String text, Icon icon, String ID) {
		super(text, icon);
		super.setSelectedIcon( icon );
		setWidgetID( ID );
	}

	/**
	 * Returns the current WidgetID associated with this button. May be null.
	 */
	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	/**
	 * Returns the current widget class associated with this button. May be null.
	 */
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

	/**
	 * Sets the widget id associated with this widget to the given value. Any properties
	 * bound to that ID, such as border, background, text, etc, will be changed as well.
	 */
	public SJButton setWidgetID(String ID) {
		Border b = getBorder();
		setBackground( TRANSPARENT );
		if( b != getBorder() )
			this.setUI( BasicButtonUI.createUI(this) );
		WidgetUtil.registerAndSetup(this, ID);
		setOpaque( getBackground().getAlpha() == 255 );
		return this;
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		if( backgroundPainter != null ) {
			backgroundPainter.paintBackground(g,this);
			super.paintComponent(g);
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
	public Dimension getMinimumSize() {
		Dimension d1 = super.getMinimumSize();
		Dimension d2 = backgroundPainter == null ? d1 : backgroundPainter.getRecommendedMinimumSize();
		if( d1.width < d2.width )
			d1.width = d2.width;
		if( d1.height < d2.height )
			d1.height = d2.height;
		return d1;
	}
	@Override
	public Dimension getPreferredSize() {
		Dimension d1 = super.getPreferredSize();
		Dimension d2 = backgroundPainter == null ? d1 : backgroundPainter.getRecommendedMinimumSize();
		if( d1.width < d2.width )
			d1.width = d2.width;
		if( d1.height < d2.height )
			d1.height = d2.height;
		return d1;
	}
	@Override
	public Dimension getMaximumSize() {
		Dimension d1 = super.getMaximumSize();
		Dimension d2 = backgroundPainter == null ? d1 : backgroundPainter.getRecommendedMinimumSize();
		if( d1.width < d2.width )
			d1.width = d2.width;
		if( d1.height < d2.height )
			d1.height = d2.height;
		return d1;
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

	public void setBackgroundPainter(BackgroundPainter bp) {
		backgroundPainter = bp;
	}
	/**
	 * overrides the value returned by getWidgetText().
	 * Generally, this function should not be used by clients;
	 * rather it is used internally when setting the text
	 * programatically because the widget's ID has changed.
	 */
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
	/**
	 * Retrieves the text originally associated with this
	 * widget by the given ID. This is useful in cases where
	 * the text in the widgets xml file is a template or
	 * prefix.
	 */
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		setWidgetID( ((SJAction)getAction()).getWidgetID() );
	}
}

class MyButtonUI extends MetalButtonUI {
    private final static MetalButtonUI myButtonUI = new MetalButtonUI(); 
 
    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c) {
        return myButtonUI;
    }
    
    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
    }
}
