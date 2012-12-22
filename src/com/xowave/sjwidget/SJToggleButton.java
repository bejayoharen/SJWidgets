package com.xowave.sjwidget;

import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JToggleButton;

/**
 * @author bjorn
 *
 */
public class SJToggleButton extends JToggleButton implements SJWidget {
	private BackgroundPainter backgroundPainter;

	/**
	 * 
	 */
	public SJToggleButton(String ID) {
		super();
		setWidgetID( ID );
		setActionCommand( ID );
	}
	
	/**
	 * 
	 */
	public SJToggleButton(Icon selectedIcon, Icon deselectedIcon, String text, String ID) {
		super();
		if( text != null )
			setText( text );
		setActionCommand(ID);
		setIcon( deselectedIcon );
		setSelectedIcon( selectedIcon );
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
	public SJToggleButton setWidgetID(String ID) {
		setBackground( TRANSPARENT );
		WidgetUtil.registerAndSetup(this, ID);
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
}
