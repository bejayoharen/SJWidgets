package com.xowave.sjwidget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * @author bjorn
 *
 */
public class SJPanel extends JPanel implements SJWidget {
	private BackgroundPainter backgroundPainter;

	/**
	 * 
	 */
	public SJPanel(String ID) {
		setWidgetID( ID );
	}

	/**
	 * @param layout
	 */
	public SJPanel(LayoutManager layout, String ID) {
		super(layout);
		setWidgetID( ID );
	}

	/**
	 * @param isDoubleBuffered
	 */
	public SJPanel(boolean isDoubleBuffered, String ID) {
		super(isDoubleBuffered);
		setWidgetID( ID );
	}

	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public SJPanel(LayoutManager layout, boolean isDoubleBuffered, String ID) {
		super(layout, isDoubleBuffered);
		setWidgetID( ID );
		
	}
	
	public SJPanel setWidgetID( String ID ) {
		setOpaque( false );
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}
	
	@Override
	public void setBackground( Color bg ) {
		setOpaque( bg.getAlpha() == 255 );
		super.setBackground(bg);
	}
	
	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

	public static SJPanel createHorizontalIPanel(String ID) {
		SJPanel ret = new SJPanel( (LayoutManager) null, ID );
		ret.setLayout( new BoxLayout( ret, BoxLayout.X_AXIS ) );
		return ret;
	}
	public static SJPanel createVerticalIPanel(String ID) {
		SJPanel ret = new SJPanel( (LayoutManager) null, ID );
		ret.setLayout( new BoxLayout( ret, BoxLayout.Y_AXIS ) );
		return ret;
	}

	private static final Dimension SMALL = new Dimension( 0, 0 );
	private static final Dimension WIDE  = new Dimension( Integer.MAX_VALUE, 0 );
	private static final Dimension TALL  = new Dimension( 0, Integer.MAX_VALUE );
	public static Component createVerticalFiller() {
		return new Component() {
			@Override
			public Dimension getMinimumSize() {
				return SMALL;
			}
			@Override
			public Dimension getPreferredSize() {
				return SMALL;
			}
			@Override
			public Dimension getMaximumSize() {
				return TALL;
			}
		} ;
	}
	public static Component createHorizontalFiller() {
		return new Component() {
			@Override
			public Dimension getMinimumSize() {
				return SMALL;
			}
			@Override
			public Dimension getPreferredSize() {
				return SMALL;
			}
			@Override
			public Dimension getMaximumSize() {
				return WIDE;
			}
		} ;
	}
	public static Component createVerticalFiller( final int height ) {
		return new Component() {
			Dimension size = new Dimension(0, height);
			@Override
			public Dimension getMinimumSize() {
				return size;
			}
			@Override
			public Dimension getPreferredSize() {
				return size;
			}
			@Override
			public Dimension getMaximumSize() {
				return size;
			}
		} ;
	}
	public static Component createHorizontalFiller( final int width ) {
		return new Component() {
			Dimension size = new Dimension(width,0);
			@Override
			public Dimension getMinimumSize() {
				return size;
			}
			@Override
			public Dimension getPreferredSize() {
				return size;
			}
			@Override
			public Dimension getMaximumSize() {
				return size;
			}
		} ;
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		if( backgroundPainter != null ) {
			backgroundPainter.paintBackground(g,this);
		} else {
			super.paintComponent(g);
		}
	}

	public void setBackgroundPainter(BackgroundPainter bp) {
		if( bp != null )
			setOpaque(true);
		backgroundPainter = bp;
	}
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}
}
