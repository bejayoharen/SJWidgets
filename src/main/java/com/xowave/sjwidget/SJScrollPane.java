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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import com.xowave.sjwidget.util.BackgroundPainter;
import com.xowave.sjwidget.util.WidgetUtil;

/**
 * @author bjorn
 *
 */
public class SJScrollPane extends JScrollPane implements SJWidget {
	BackgroundPainter backgroundPainter;

	/**
	 * 
	 */
	public SJScrollPane(String id) {
		setup(id);
	}

	/**
	 * @param view
	 */
	public SJScrollPane(Component view,String id) {
		super(view);
		setup(id);
	}

	/**
	 * @param vsbPolicy
	 * @param hsbPolicy
	 */
	public SJScrollPane(int vsbPolicy, int hsbPolicy,String id) {
		super(vsbPolicy, hsbPolicy);
		setup(id);
	}

	/**
	 * @param view
	 * @param vsbPolicy
	 * @param hsbPolicy
	 */
	public SJScrollPane(Component view, int vsbPolicy, int hsbPolicy,String id) {
		super(view, vsbPolicy, hsbPolicy);
		setup(id);
	}

	private void setup(String id) {
		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder());
		WidgetUtil.registerAndSetup(this, id);
	}

	public void presetScrollPosition(Point scrollPosition) {
		if (this.isShowing())
			setScrollPositionPrivate(scrollPosition);
	}

	private void setScrollPositionPrivate(Point scrollPosition) {
		if (scrollPosition == null)
			return;
		super.getHorizontalScrollBar().setValue(scrollPosition.x);
		super.getVerticalScrollBar().setValue(scrollPosition.y);
	}

	public Point getScrollPosition() {
		return new Point(
				super.getHorizontalScrollBar().getValue(),
				super.getVerticalScrollBar().getValue());
	}
	
	public SJScrollPane setWidgetID( String ID ) {
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}
	
	@Override
	public void setBackground( Color bg ) {
		setOpaque( bg.getAlpha() == 255 );
		super.setBackground(bg);
	}
	
	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		if( backgroundPainter != null ) {
			backgroundPainter.paintBackground(g,this);
		} else {
			super.paintComponent(g);
		}
	}

	public void setBackgroundPainter(BackgroundPainter bp) {
		if( bp != null )
			setOpaque(true);
		backgroundPainter = bp;
	}
	
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}
}
