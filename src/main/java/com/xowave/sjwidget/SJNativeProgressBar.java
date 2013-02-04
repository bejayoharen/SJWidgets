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

import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;

/**
 * Slightly more attractive progress bar.
 *
 * @author bjorn
 *
 */
public class SJNativeProgressBar extends JProgressBar implements SJWidget {
	private BackgroundPainter backgroundPainter;
	/**
	 * 
	 */
	public SJNativeProgressBar( String ID ) {
		setWidgetID(ID);
	}

	/**
	 * @param orient
	 */
	public SJNativeProgressBar(int orient, String ID ) {
		super(orient);
		setWidgetID(ID);
	}

	/**
	 * @param newModel
	 */
	public SJNativeProgressBar(BoundedRangeModel newModel, String ID ) {
		super(newModel);
		setWidgetID(ID);
	}

	/**
	 * @param min
	 * @param max
	 */
	public SJNativeProgressBar(int min, int max, String ID ) {
		super(min, max);
		setWidgetID(ID);
	}

	/**
	 * @param orient
	 * @param min
	 * @param max
	 */
	public SJNativeProgressBar(int orient, int min, int max, String ID ) {
		super(orient, min, max);
		setWidgetID(ID);
	}


	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

	public SJNativeProgressBar setWidgetID(String ID) {
		setBackground( TRANSPARENT );
		WidgetUtil.registerAndSetup(this, ID);
		setOpaque( getBackground().getAlpha() == 255 );
		return this;
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		if( backgroundPainter != null ) {
			backgroundPainter.paintBackground(g,this);
		}
		super.paintComponent(g);
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
}
