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

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.text.Document;

import com.xowave.sjwidget.util.BackgroundPainter;
import com.xowave.sjwidget.util.WidgetUtil;

/**
 * @author bjorn
 *
 */
public class SJTextField extends JTextField implements SJWidget {

	/**
	 * 
	 */
	public SJTextField( String id ) {
		setWidgetID( id );
	}

	/**
	 * @param text
	 */
	public SJTextField(String text,  String id ) {
		super(text);
		setWidgetID( id );
	}

	/**
	 * @param columns
	 */
	public SJTextField(int columns, String id ) {
		super(columns);
		setWidgetID( id );
	}

	/**
	 * @param text
	 * @param columns
	 */
	public SJTextField(String text, int columns, String id) {
		super(text, columns);
		setWidgetID( id );
	}

	/**
	 * @param doc
	 * @param text
	 * @param columns
	 */
	public SJTextField(Document doc, String text, int columns, String id) {
		super(doc, text, columns);
		setWidgetID( id );
	}

	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

	public SJTextField setWidgetID(String ID) {
		setBorder( BorderFactory.createEmptyBorder() );
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
