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

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JScrollPane;


class ComponentInfo {
	private final String id;
	private final int x, y;
	private final String constraints;
	private final Component comp;
	private final JScrollPane scrollPane;
	public ComponentInfo(String id, int x, int y, String constraints, JScrollPane scrollPane) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.constraints = constraints;
		this.comp = null;
		this.scrollPane = scrollPane;
	}
	public ComponentInfo(int w, int h, String constraints, JScrollPane scrollPane) {
		this.id = null;
		this.x = w;
		this.y = h;
		this.constraints = constraints;
		this.comp = null;
		this.scrollPane = scrollPane;
	}
	public ComponentInfo(Component c, String constraints, JScrollPane scrollPane) {
		this.id = null;
		this.x = -1;
		this.y = -1;
		this.constraints = constraints;
		this.scrollPane = scrollPane;
		if( scrollPane != null && c != null ) {
			scrollPane.setViewportView(c);
			comp = scrollPane;
		} else {
			comp = c;
		}
	}
//	public Component create() {
//		if( comp != null ) {
//			return comp;
//		} else if( id == null ) {
//			// filler
//			final Dimension min = new Dimension( x == -1 ? 0 : x, y == -1 ? 0 : y );
//			final Dimension max = new Dimension( min );
//			if( max.width == Integer.MAX_VALUE )
//				min.width = 0;
//			if( max.height == Integer.MAX_VALUE )
//				min.width = 0;
//			Component c = new Component() {
//				public Dimension getMinimumSize() {
//					return min;
//				}
//				public Dimension getPreferredSize() {
//					return min;
//				}
//				public Dimension getMaximumSize() {
//					return max;
//				}
//			} ;
//			if( scrollPane != null ) {
//				scrollPane.setViewportView(c);
//				return scrollPane;
//			} else {
//				return c;
//			}
//		} else {
//			SJWidget w = WidgetUtil.getWidget(id);
//			Component c = (Component) w;
////			if( w == null )
////				return new javax.swing.JLabel( id );
//			if( w == null )
//				throw new RuntimeException( "Component not found: " + id );
//			WidgetDescription wd = WidgetUtil.getWidgetDescription(w.getWidgetID());
////			wd.layoutConstraints = constraints ;
//			if( x != -1 && y != -1 && wd.size != null )
//				c.setBounds( x, y, wd.size.width, wd.size.height );
//			JScrollPane scrollPane = this.scrollPane;
//			if( scrollPane == null )
//				scrollPane = wd.scrollPane;
//			if( scrollPane != null ) {
//				scrollPane.setViewportView(c);
//				return scrollPane;
//			} else {
//				return c;
//			}
//		}
//	}
	public String getLayoutConstraints() {
		if( constraints == null )
			return null;
		if( constraints.equals( "north" ) ) 
			return BorderLayout.NORTH;
		if( constraints.equals( "south" ) ) 
			return BorderLayout.SOUTH;
		if( constraints.equals( "east" ) ) 
			return BorderLayout.EAST;
		if( constraints.equals( "west" ) ) 
			return BorderLayout.WEST;
		if( constraints.equals( "center" ) ) 
			return BorderLayout.CENTER;

		return constraints;
	}
}
