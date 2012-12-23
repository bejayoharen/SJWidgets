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
import java.awt.event.MouseAdapter;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SJApplet extends JApplet implements SJRootPaneContainer {
	Component originalGlassPane = getGlassPane();
	boolean showingLightbox = false;

	public void showLightbox(SJLightbox sjLightbox) {
		if( showingLightbox )
			throw new RuntimeException("Can only show one lightbox at a time.");
		showingLightbox = true;
		originalGlassPane = getGlassPane();
		JPanel p = SJPanel.createVerticalIPanel(null);
		JPanel p2 = SJPanel.createHorizontalIPanel(null);
		p.add( SJPanel.createVerticalFiller() );
		p.add( p2 );
		p.add( SJPanel.createVerticalFiller() );
		p2.add( SJPanel.createHorizontalFiller() );
		p2.add( sjLightbox );
		p2.add( SJPanel.createHorizontalFiller() );

		p.setBackground( new java.awt.Color(0, 0, 0, .6f) );
		p.setOpaque(true);
		p.addMouseListener( new MouseAdapter() {} );
		p.addMouseMotionListener( new MouseAdapter() {} );

		sjLightbox.setToSJRootPaneContainer(this);
		setGlassPane( p );
		p.setVisible(true);
	}

	public void lightboxComplete(SJLightbox sjLightbox) {
		getGlassPane().setVisible(false);
		sjLightbox.setToSJRootPaneContainer(null);
		setGlassPane( originalGlassPane );
		showingLightbox = false;
	}

}
