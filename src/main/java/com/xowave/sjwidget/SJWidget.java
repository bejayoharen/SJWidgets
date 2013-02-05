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

import java.awt.Color;

import com.xowave.sjwidget.util.BackgroundPainter;

public interface SJWidget {
	public static final Color TRANSPARENT = new Color( 0, 0, 0, 0 );
	public static final String WIDGET_TEXT_KEY = "Widget Text";
	public static final String CLASS_KEY = "Widget Class" ;
	public static final String ID_KEY = "Widget ID";
	/** Sets the ID of the widget and returns self which might be useful for chaining */
	public SJWidget setWidgetID( String ID );
	/** returns the previosuly set widget ID */
	public String getWidgetID();
	/** returns the class of the current widget. */
	public String getWidgetClass();
	/** 
	 * gets the text originally associated with this widget when
	 * setWidgetID was called. This is useful if the text in the
	 * Widgets.xml file is a template or prefix. By calling this,
	 * you don't need to store the original separately from the
	 * widget.
	 */
	public String getWidgetText();
	/**
	 * Overrides the value returned by getWidgetText()
	 * This should generally not be called by ordinary code.
	 * It is intended for use within the SJWidgets library.
	 */
	public void setWidgetText(String text);
	/**
	 * If an alternative background painter is required, it can be set here.
	 */
	void setBackgroundPainter( BackgroundPainter bp );
}
