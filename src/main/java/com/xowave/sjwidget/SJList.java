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
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;

import com.xowave.sjwidget.util.BackgroundPainter;
import com.xowave.sjwidget.util.WidgetUtil;

/**
 * SJList is like a standard swing JList except that it allows you
 * to set a widget ID (or assign one at construction).
 * 
 * This ID can be used to define features of the component, such as borders, background colors, icons and so on, in a separate XML file rather than code.
 * 
 * Keep in mind that SJList delegates rendering of its subcomponents
 * in the same way that swing does, though, so to take fullest advantage of this component, you should also set the renderer
 * (and editor, if applicable) of the component. In some cases, you may not even need to use SJList, just regular list. See the docs for more info.
 * 
 * @author bjorn
 *
 */
public class SJList extends JList implements SJWidget {
	private BackgroundPainter backgroundPainter;

	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param ID the widget ID
	 */
	public SJList(String id) {
		setWidgetID( id );
	}

	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param dataModel the ListModel for the list. 
	 * @param ID the widget ID
	 */
	public SJList(ListModel dataModel, String id) {
		super(dataModel);
		setWidgetID( id );
	}

	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param listData the initial list Data.
	 * @param ID the widget ID
	 */
	public SJList(Object[] listData, String id) {
		super(listData);
		setWidgetID( id );
	}
	/**
	 * Like the equivalent JLabel Constructor with an additional argument: ID, which sets the widget ID.
	 * @param listData the initial list Data.
	 * @param ID the widget ID
	 */
	public SJList(Vector<?> listData, String id) {
		super(listData);
		setWidgetID( id );
	}
	@Override
	public void paintComponent( Graphics g ) {
    	if( backgroundPainter != null )
			backgroundPainter.paintBackground(g,this);
		super.paintComponent(g);
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
	public SJList setWidgetID(String ID) {
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}
	@Override
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}
	@Override
	public void setBackgroundPainter(BackgroundPainter bp) {
		backgroundPainter = bp;
		setBackground( new Color( 0, 0, 0, 0 ) );
	}
	@Override
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
}
