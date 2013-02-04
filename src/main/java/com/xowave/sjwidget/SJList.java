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
 * @author bjorn
 *
 */
public class SJList extends JList implements SJWidget {
	private BackgroundPainter backgroundPainter;

	/**
	 * 
	 */
	public SJList(String id) {
		setWidgetID( id );
	}

	/**
	 * @param dataModel
	 */
	public SJList(ListModel dataModel, String id) {
		super(dataModel);
		setWidgetID( id );
	}

	/**
	 * @param listData
	 */
	public SJList(Object[] listData, String id) {
		super(listData);
		setWidgetID( id );
	}
	/**
	 * @param listData
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

	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

	public SJList setWidgetID(String ID) {
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}

	public void setBackgroundPainter(BackgroundPainter bp) {
		backgroundPainter = bp;
		setBackground( new Color( 0, 0, 0, 0 ) );
	}
	
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}
}
