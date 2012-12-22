package com.xowave.sjwidget;

import java.awt.Graphics;

import javax.swing.JPasswordField;
import javax.swing.text.Document;

/**
 * 
 * 
 * @author bjorn
 *
 */
public class SJPasswordField extends JPasswordField implements SJWidget {
	private BackgroundPainter backgroundPainter;

	public SJPasswordField(String ID) {
		WidgetUtil.registerAndSetup(this, ID);
	}

	public SJPasswordField(String text, String ID) {
		super(text);
		WidgetUtil.registerAndSetup(this, ID);
	}

	public SJPasswordField(int columns, String ID) {
		super(columns);
		WidgetUtil.registerAndSetup(this, ID);
	}

	public SJPasswordField(String text, int columns, String ID) {
		super(text, columns);
		WidgetUtil.registerAndSetup(this, ID);
	}

	public SJPasswordField(Document doc, String text, int columns, String ID) {
		super(doc, text, columns);
		WidgetUtil.registerAndSetup(this, ID);
	}

	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

	public SJPasswordField setWidgetID(String ID) {
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
