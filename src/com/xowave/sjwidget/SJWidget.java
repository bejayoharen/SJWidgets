package com.xowave.sjwidget;

import java.awt.Color;

public interface SJWidget {
	public static final Color TRANSPARENT = new Color( 0, 0, 0, 0 );
	public static final String WIDGET_TEXT_KEY = "Widget Text";
	public static final String CLASS_KEY = "Widget Class" ;
	public static final String ID_KEY = "Widget ID";
	/** return self for chaining */
	public SJWidget setWidgetID( String ID );
	public String getWidgetID();
	public String getWidgetClass();
	public void setWidgetText(String text);
	public String getWidgetText();
	void setBackgroundPainter( BackgroundPainter bp );
}
