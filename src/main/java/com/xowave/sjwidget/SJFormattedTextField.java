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
import java.text.Format;

import javax.swing.JFormattedTextField;

/**
 * @author bjorn
 *
 */
public class SJFormattedTextField extends JFormattedTextField implements SJWidget {
	private BackgroundPainter backgroundPainter;

	/**
	 * 
	 */
	public SJFormattedTextField( String id ) {
		super();
		setWidgetID( id );
	}

	/**
	 * @param value
	 */
	public SJFormattedTextField(Object value, String id ) {
		super(value);
		setWidgetID( id );
	}

	/**
	 * @param format
	 */
	public SJFormattedTextField(Format format, String id ) {
		super(format);
		setWidgetID( id );
	}

	/**
	 * @param formatter
	 */
	public SJFormattedTextField(AbstractFormatter formatter, String id ) {
		super(formatter);
		setWidgetID( id );
	}

	/**
	 * @param factory
	 */
	public SJFormattedTextField(AbstractFormatterFactory factory, String id ) {
		super(factory);
		setWidgetID( id );
	}

	/**
	 * @param factory
	 * @param currentValue
	 */
	public SJFormattedTextField(AbstractFormatterFactory factory, Object currentValue, String id ) {
		super(factory, currentValue);
		setWidgetID( id );
	}

	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

	public SJFormattedTextField setWidgetID(String ID) {
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}
	@Override
	public void paintComponent( Graphics g ) {
		if( backgroundPainter != null )
			backgroundPainter.paintBackground(g,this);
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
