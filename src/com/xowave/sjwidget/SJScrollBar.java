package com.xowave.sjwidget;

import javax.swing.JScrollBar;

/**
 * Extends Scrollbars.
 * 
 * @author bjorn
 *
 */
public class SJScrollBar extends JScrollBar implements SJWidget {

	/**
	 * 
	 */
	public SJScrollBar(String ID) {
		setWidgetID( ID );
	}

	/**
	 * @param orientation
	 */
	public SJScrollBar(int orientation, String ID) {
		super(orientation);
		setWidgetID( ID );
	}

	/**
	 * @param orientation
	 * @param value
	 * @param extent
	 * @param min
	 * @param max
	 */
	public SJScrollBar(int orientation, int value, int extent, int min, int max, String ID) {
		super(orientation, value, extent, min, max);
		setWidgetID( ID );
	}

	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

	public SJScrollBar setWidgetID(String ID) {
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}
	
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}
	
	public void setBackgroundPainter(BackgroundPainter bp) {
	}
}
