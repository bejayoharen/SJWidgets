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

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class SJPopupMenu extends JPopupMenu implements SJWidget {

	BackgroundPainter backgroundPainter;
	public SJPopupMenu(String id) {
		super();
		setBorder( BorderFactory.createLineBorder(Color.BLACK) );
		setWidgetID(id);
	}
	
	@Override
	public void addSeparator() {
		JComponent jc = new Separator() ;
		super.add( jc );
	}
	
	@Override
	public String getWidgetID() {
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
	public SJPopupMenu setWidgetID(String ID) {
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if( backgroundPainter != null )
			backgroundPainter.paintBackground(g, this);
		else
			super.paintComponent(g);
	}

	@Override
	public void setBackgroundPainter(BackgroundPainter bp) {
		if( bp != null )
			setOpaque(true);
		backgroundPainter = bp;
	}

	public void insertSeperator(int index) {
		insert( new Separator(), index );
	}
	
	private final class Separator extends JComponent {
		@Override
		protected void paintComponent(Graphics g) {
			g.setColor( SJPopupMenu.this.getForeground() );
			g.fillRect(0, 0, getWidth()/2, getHeight());
		}

		@Override
		public boolean isOpaque() {
			return false;
		}

		@Override
		public Dimension getMinimumSize() {
			return new Dimension( 0, 2 );
		}

		@Override
		public Dimension getMaximumSize() {
			return new Dimension( Integer.MAX_VALUE, 3 );
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension( 1, 3 );
		}
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
