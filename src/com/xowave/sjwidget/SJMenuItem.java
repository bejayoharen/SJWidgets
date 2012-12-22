package com.xowave.sjwidget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Action;
import javax.swing.JMenuItem;

public class SJMenuItem extends JMenuItem implements SJWidget {
	private final SJMenuPaintingDelegate delegate;

	public SJMenuItem(Action action, String id) {
		super(action);
		delegate = new SJMenuPaintingDelegate(this);
		setWidgetID(id);
	}
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
