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
 * SJScrollBar is like a standard swing JScrollBar except that it allows you
 * to set a widget ID (or assign one at construction).
 * 
 * This ID can be used to define features of the component, such as borders, background colors, icons and so on, in a separate XML file rather than code.
 * 
 * @todo Need to review how much of this actually works. eg can I set forground/background/borders/etc?
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
