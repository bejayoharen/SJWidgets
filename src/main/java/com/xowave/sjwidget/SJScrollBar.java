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

import javax.swing.JScrollBar;

import com.xowave.sjwidget.util.BackgroundPainter;
import com.xowave.sjwidget.util.WidgetUtil;

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
