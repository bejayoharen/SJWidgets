package com.xowave.sjwidget;

import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * @author bjorn
 *
 */
public class SJComponent extends JComponent implements SJWidget {
	private BackgroundPainter backgroundPainter;

	/**
	 * 
	 */
	public SJComponent(String ID) {
		setWidgetID(ID);
	}

	public SJComponent setWidgetID( String ID ) {
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}
	
	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
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
		backgroundPainter = bp;
	}
	
	public BackgroundPainter getBackgroundPainter() {
		return backgroundPainter;
	}
	
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}
}
