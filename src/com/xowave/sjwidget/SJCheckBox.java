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

import com.xowave.sjwidget.help.HelpComponentRegistry;

/**
 * @author bjorn
 *
 */
public class SJCheckBox extends JCheckBox implements SJWidget, PropertyChangeListener {
	private BackgroundPainter backgroundPainter;
	
	/**
	 * 
	 */
	public SJCheckBox(String ID) {
		setWidgetID( ID );
	}

	/**
	 * @param icon
	 */
	public SJCheckBox(Icon icon, String ID) {
		super(icon);
		setWidgetID( ID );
	}

	/**
	 * @param text
	 */
	public SJCheckBox(String text, String ID) {
		super(text);
		setWidgetID( ID );
	}

	/**
	 * @param a
	 */
	public SJCheckBox(Action a, String ID) {
		super(a);
		setWidgetID( ID );
	}
	
	public SJCheckBox(SJAction a) {
		super(a);
		setWidgetID( a.getWidgetID() );
		a.addPropertyChangeListener(this);
	}

	/**
	 * @param text
	 * @param icon
	 */
	public SJCheckBox(String text, Icon icon, String ID, HelpComponentRegistry hcr) {
		super(text, icon);
		setWidgetID( ID );
	}

	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

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

	public void setBackgroundPainter(BackgroundPainter bp) {
		backgroundPainter = bp;
	}
	
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		setWidgetID( ((SJAction)getAction()).getWidgetID() );
	}
}
