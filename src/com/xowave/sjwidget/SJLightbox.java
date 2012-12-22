package com.xowave.sjwidget;

import java.awt.LayoutManager;

import javax.swing.JButton;

/**
 * @author bjorn
 *
 */
public class SJLightbox extends SJPanel implements SJWidget {
	private SJRootPaneContainer sjrpc ;
	private JButton defaultButton, originalDefaultButton;

	/**
	 * 
	 */
	public SJLightbox(String ID) {
		super( ID );
	}

	public SJLightbox(LayoutManager layout, String ID) {
		super(layout,ID);
		setWidgetID( ID );
	}

	public SJLightbox(boolean isDoubleBuffered, String ID) {
		super(isDoubleBuffered,ID);
	}

	public SJLightbox(LayoutManager layout, boolean isDoubleBuffered, String ID) {
		super(layout, isDoubleBuffered,ID);
		setWidgetID( ID );
	}
	
	public void setToSJRootPaneContainer( SJRootPaneContainer sjrpc ) {
		this.sjrpc = sjrpc;
		if( sjrpc != null ) {
			originalDefaultButton = sjrpc.getRootPane().getDefaultButton();
			sjrpc.getRootPane().setDefaultButton(defaultButton);
		}
	}
	
	public void lightboxComplete() {
		sjrpc.getRootPane().setDefaultButton(originalDefaultButton);
		sjrpc.lightboxComplete( this );
		sjrpc = null;
	}
	
	public void setDefaultButton(JButton b) {
		defaultButton = b;
		if( sjrpc != null )
			sjrpc.getRootPane().setDefaultButton(defaultButton);
	}
}
