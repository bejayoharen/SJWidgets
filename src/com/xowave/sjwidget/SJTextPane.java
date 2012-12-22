package com.xowave.sjwidget;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;

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
