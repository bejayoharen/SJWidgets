package com.xowave.sjwidget;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JMenuBar;

/**
 * SJMenuBar
 * An improved JMenuBar. the built-in JMenuBar doesn't respect color or font requests, so this will do that.
 *
 * @author bjorn
 *
 */
public class SJMenuBar extends JMenuBar implements SJWidget {
	
	BackgroundPainter backgroundPainter;

	/**
	 * 
	 */
	public SJMenuBar(String ID) {
		setWidgetID(ID);
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		g.setColor( getBackground() );
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	
	public String getWidgetID() {
		setOpaque( false );
		return (String) this.getClientProperty(ID_KEY);
	}
	
	@Override
	public void setBackground( Color bg ) {
		setOpaque( bg.getAlpha() == 255 );
		super.setBackground(bg);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

	public SJMenuBar setWidgetID(String ID) {
		WidgetUtil.registerAndSetup(this, ID);
		return this;
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
