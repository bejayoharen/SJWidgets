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

/**
 * @author bjorn
 *
 */
public class SJButton extends JButton implements SJWidget, PropertyChangeListener {
	private BackgroundPainter backgroundPainter;
	
	/**
	 * 
	 */
	public SJButton(String ID) {
		setWidgetID( ID );
	}

	/**
	 * @param icon
	 */
	public SJButton(Icon icon, String ID) {
		super(icon);
		super.setSelectedIcon( icon );
		setWidgetID( ID );
	}

	/**
	 * @param text
	 */
	public SJButton(String text, String ID) {
		super(text);
		setWidgetID( ID );
	}

	/**
	 * @param a
	 */
	public SJButton(Action a, String ID) {
		super(a);
		setWidgetID( ID );
	}
	
	public SJButton(SJAction a) {
		super(a);
		setWidgetID( a.getWidgetID() );
		a.addPropertyChangeListener(this);
	}

	/**
	 * @param text
	 * @param icon
	 */
	public SJButton(String text, Icon icon, String ID) {
		super(text, icon);
		super.setSelectedIcon( icon );
		setWidgetID( ID );
	}

	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

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
	
	boolean pressedIconSet = false;
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
