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

import java.awt.Graphics;

import javax.swing.JComponent;

import com.xowave.sjwidget.util.BackgroundPainter;
import com.xowave.sjwidget.util.WidgetUtil;

/**
 * This is an extension of JComponent. This is useful for designing custom components, or if you
 * want to do custom drawing using the drawing in your xml file as a starting point. You may use this
 * like any other SJWidget, then overwrite paintComponent (if you want the background to be painted
 * correctly, be sure to call super.paintComponent first!).
 * 
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
