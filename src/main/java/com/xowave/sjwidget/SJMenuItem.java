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
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Action;
import javax.swing.JMenuItem;

import com.xowave.sjwidget.util.BackgroundPainter;
import com.xowave.sjwidget.util.WidgetUtil;

/**
 * SJLabel is like a standard swing JLabel except that it allows you
 * to set a widget ID (or assign one at construction).
 * 
 * This ID can be used to define features of the component, such as borders, background colors, icons and so on, in a separate XML file rather than code.
 * 
 * Note, that at the moment a lot of drawing decisions, such as where to annotate the keyboard shortcuts, are made programatically rather than in code,
 * so more work still needs to be done.
 * 
 * @author bjorn
 *
 */
public class SJMenuItem extends JMenuItem implements SJWidget {
	private final SJMenuPaintingDelegate delegate;

	/**
	 * Like the equivalent JMenuItem constructor with an additional argument: ID, which sets the widget ID.
	 * @param action action which manages this MenuItem
	 * @param ID the widget ID
	 */
	public SJMenuItem(Action action, String id) {
		super(action);
		delegate = new SJMenuPaintingDelegate(this);
		setWidgetID(id);
	}
	/**
	 * Like the equivalent JMenuItem constructor with an additional argument: ID, which sets the widget ID.
	 * @param name the name of this item. Displayed as the default text unless overwritten by the ID.
	 * @param ID the widget ID
	 */
	public SJMenuItem(String name, String id) {
		super(name);
		delegate = new SJMenuPaintingDelegate(this);
		setWidgetID(id);
	}
	@Override
	public void paintComponent(Graphics gg) {
		delegate.paintComponentHelper(gg);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return delegate.getPreferredSize();
	}
	@Override
	public Dimension getMaximumSize() {
		return delegate.getMaximumSize();
	}
	@Override
	public Dimension getMinimumSize() {
		return delegate.getMinimumSize();
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
	public SJMenuItem setWidgetID(String ID) {
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}

	@Override
	public void setBackgroundPainter(BackgroundPainter bp) {
		if( bp != null )
			setOpaque(true);
		delegate.setBackgroundPainter(bp);
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
