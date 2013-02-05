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
import java.awt.Graphics;

import javax.swing.JMenuBar;

import com.xowave.sjwidget.util.BackgroundPainter;
import com.xowave.sjwidget.util.WidgetUtil;

/**
 * SJMenuBar is like a standard swing JMenuBar except that it allows you
 * to set a widget ID (or assign one at construction).
 * 
 * This ID can be used to define features of the component, such as borders, background colors, icons and so on, in a separate XML file rather than code.
 * It also contains workarounds to help honor background colors.
 * 
 * @author bjorn
 *
 */
public class SJMenuBar extends JMenuBar implements SJWidget {
	
	BackgroundPainter backgroundPainter;

	/**
	 * Like the equivalent JMenuBar Constructor with an additional argument: ID, which sets the widget ID.
	 * @param ID the widget ID
	 */
	public SJMenuBar(String ID) {
		setWidgetID(ID);
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		g.setColor( getBackground() );
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	@Override
	public String getWidgetID() {
		setOpaque( false );
		return (String) this.getClientProperty(ID_KEY);
	}
	
	@Override
	public void setBackground( Color bg ) {
		setOpaque( bg.getAlpha() == 255 );
		super.setBackground(bg);
	}
	@Override
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}
	@Override
	public SJMenuBar setWidgetID(String ID) {
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}
	@Override
	public void setBackgroundPainter(BackgroundPainter bp) {
		if( bp != null )
			setOpaque(true);
		backgroundPainter = bp;
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
