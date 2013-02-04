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
import javax.swing.JTextPane;

import com.xowave.sjwidget.util.BackgroundPainter;
import com.xowave.sjwidget.util.WidgetUtil;

/**
 * @author bjorn
 *
 */
public class SJTextPane extends JTextPane implements SJWidget {

	/**
	 * 
	 */
	public SJTextPane( String id ) {
		setWidgetID( id );
	}

	@Override
	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	
	@Override
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

	@Override
	public SJTextPane setWidgetID(String ID) {
		setBorder( BorderFactory.createEmptyBorder() );
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}
	
	@Override
	public void setBackgroundPainter(BackgroundPainter bp) {
	}
	
	@Override
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
	@Override
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}
}
