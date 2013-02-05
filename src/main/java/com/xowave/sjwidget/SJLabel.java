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

/**
 * 
 */
package com.xowave.sjwidget;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JLabel;

import com.xowave.sjwidget.util.BackgroundPainter;
import com.xowave.sjwidget.util.WidgetUtil;


/**
 * SJLabel is like a standard swing JLabel except that it allows you
 * to set a widget ID (or assign one at construction).
 * 
 * This ID can be used to define features of the component, such as borders, background colors, icons and so on, in a separate XML file rather than code.
 * 
 * @author bjorn
 *
 */
public class SJLabel extends JLabel implements SJWidget {
	private BackgroundPainter backgroundPainter;

	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param ID the widget ID
	 */
	public SJLabel(String id) {
		setWidgetID(id);
	}

	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param text the label text, which is set before the ID
	 * @param ID the widget ID
	 */
	public SJLabel(String text, String id) {
		super(text);
		setWidgetID(id);
		setText(text);
	}

	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param icon the label icon
	 * @param ID the widget ID
	 */
	public SJLabel(Icon icon, String id) {
		super(icon);
		setWidgetID(id);
		setIcon(icon);
	}

	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param text the label text, which is set before the ID
	 * @param the horizantalAlignment property, which is set before the ID
	 * @param ID the widget ID
	 */
	public SJLabel(String text, int horizontalAlignment, String id) {
		super(text, horizontalAlignment);
		setWidgetID(id);
	}

	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param icon the label icon, which is set before the ID
	 * @param the horizantalAlignment property, which is set before the ID
	 * @param ID the widget ID
	 */
	public SJLabel(Icon icon, int horizontalAlignment, String id) {
		super(icon, horizontalAlignment);
		setWidgetID(id);
	}

	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param text the label text, which is set before the ID
	 * @param icon the label icon, which is set before the ID
	 * @param the horizantalAlignment property, which is set before the ID
	 * @param ID the widget ID
	 */
	public SJLabel(String text, Icon icon, int horizontalAlignment, String id) {
		super(text, icon, horizontalAlignment);
		setWidgetID(id);
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
	public SJLabel setWidgetID(String ID) {
		setBackground( TRANSPARENT );
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}
	@Override
	public void paintComponent( Graphics g ) {
		if( backgroundPainter != null ) {
			backgroundPainter.paintBackground(g,this);
			super.paintComponent(g);
		} else {
			if( getBackground().getAlpha() != 0 ) {
				g.setColor(getBackground());
//				Insets in = getInsets();
//				g.fillRect(in.left, in.top, getWidth() - in.left - in.right, getHeight() - in.top - in.bottom );
				g.fillRect(0, 0, getWidth(), getHeight() );
			}
			super.paintComponent(g);
		}
	}

	@Override
	public void setBackgroundPainter(BackgroundPainter bp) {
		if( bp != null )
			setOpaque( getBackground().getAlpha() == 255 );
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
