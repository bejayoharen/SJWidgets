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

import java.awt.LayoutManager;

import javax.swing.JButton;

/**
 * A lightbox is a panel that be displayed modally in an SJRootPaneContainer. This is
 * useful for focusing a user's attention, or blocking user input while something is
 * happening in the background.
 * 
 * @author bjorn
 *
 */
public class SJLightbox extends SJPanel implements SJWidget {
	private SJRootPaneContainer sjrpc ;
	private JButton defaultButton, originalDefaultButton;

	/**
	 * Constructs a new SJLightbox using the given widget ID.
	 */
	public SJLightbox(String ID) {
		super( ID );
	}
	/**
	 * Constructs a new SJLightbox using the given layout manager and widget ID.
	 */
	public SJLightbox(LayoutManager layout, String ID) {
		super(layout,ID);
		setWidgetID( ID );
	}
	/**
	 * Constructs a new SJLightbox using the given widget ID. Double buffering
	 * is set based on the given argument. Double buffering is used by default,
	 * and usually desired, so it is seldom necessary to invoke this constructor.
	 */
	public SJLightbox(boolean isDoubleBuffered, String ID) {
		super(isDoubleBuffered,ID);
	}
	/**
	 * Constructs a new SJLightbox using the given layout manager and widget ID. Double buffering
	 * is set based on the given argument. Double buffering is used by default,
	 * and usually desired, so it is seldom necessary to invoke this constructor.
	 */
	public SJLightbox(LayoutManager layout, boolean isDoubleBuffered, String ID) {
		super(layout, isDoubleBuffered,ID);
		setWidgetID( ID );
	}
	
	/**
	 * Called by SJRootPaneContainers when this is made visible. End users
	 * generally don't need to call this function unless they are implementing
	 * similar behavior.
	 * 
	 * @param sjrpc
	 */
	public void setToSJRootPaneContainer( SJRootPaneContainer sjrpc ) {
		this.sjrpc = sjrpc;
		if( sjrpc != null ) {
			originalDefaultButton = sjrpc.getRootPane().getDefaultButton();
			sjrpc.getRootPane().setDefaultButton(defaultButton);
		}
	}
	/**
	 * Call this when the Lightbox is done and should be hidden. The SJLightbox
	 * will make arrangements with the SJRootPaneContainer to hide it from view.
	 * If no SJRootPaneContainer is currently showing this, then a
	 * RuntimeException will likely be thrown.
	 */
	public void lightboxComplete() {
		sjrpc.getRootPane().setDefaultButton(originalDefaultButton);
		sjrpc.lightboxComplete( this );
		sjrpc = null;
	}
	/**
	 * Sets the given button to be the default button when this root pane is showing.
	 */
	public void setDefaultButton(JButton b) {
		defaultButton = b;
		if( sjrpc != null )
			sjrpc.getRootPane().setDefaultButton(defaultButton);
	}
}
