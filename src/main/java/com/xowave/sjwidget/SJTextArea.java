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
import javax.swing.JTextArea;

/**
 * @author bjorn
 *
 */
public class SJTextArea extends JTextArea implements SJWidget {

	/**
	 * 
	 */
	public SJTextArea( String id ) {
		setWidgetID( id );
	}

	/**
	 * @param text
	 */
	public SJTextArea(String text,  String id ) {
		super(text);
		setWidgetID( id );
	}

	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

	public SJTextArea setWidgetID(String ID) {
		setBorder( BorderFactory.createEmptyBorder() );
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}
	
	public void setBackgroundPainter(BackgroundPainter bp) {
	}
	
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}
}
