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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/**
 * @author bjorn
 *
 */
public class SJPopupEditor extends JTextField implements ActionListener, MouseListener, WindowListener {
	private final JWindow w;
	private final Component focusParent, glassPane;
	private final ActionListener actionListener;

	/**
	 * 
	 */
	public SJPopupEditor( Component owner, Component focusParent, String text, ActionListener al ) {
		super( text );
		this.actionListener = al;
		setBorder( BorderFactory.createLineBorder(Color.BLACK) );
		
		// we can't use regular pop-ups, b/c they don't support focus
		// create our window:
		w = new JWindow( SwingUtilities.windowForComponent(owner) ) ;
		
		//figure our dimensions and stuff:
		Dimension d = getPreferredSize();
		d.width = Math.max( owner.getWidth(), owner.getHeight() );
		setPreferredSize(d);
		Point p = owner.getLocationOnScreen();
		p.y += ( owner.getHeight() - d.height ) / 2 ;
		w.getContentPane().add( this );
		w.setAlwaysOnTop(true);
		w.pack();
		w.setLocation(p.x, p.y);
		w.requestFocus();
		this.requestFocusInWindow();
		com.xowave.util.GlobalEventManager.suspendWhileVisible(w);
		w.addWindowListener( this );
		
		this.focusParent = focusParent;
		Window s = SwingUtilities.windowForComponent(focusParent);
		JRootPane jrp = SwingUtilities.getRootPane(focusParent);
		glassPane = jrp.getGlassPane();
		glassPane.setVisible(true);
		glassPane.addMouseListener(this);
		if( s != null )
			s.addWindowListener( this );

		w.setVisible(true);
		
		addActionListener( this );
	}
	
	boolean success = false;
	boolean once = false;
	public void close( @SuppressWarnings("hiding") boolean success ) {
		if( once )
			return;
		once = true;
		
		this.success = success;
		w.dispose();
		glassPane.setVisible(false);
		glassPane.removeMouseListener(this);
		if( actionListener != null )
			actionListener.actionPerformed( new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getText()) );
	}
	
	public boolean success() {
		return success;
	}

	public void actionPerformed(ActionEvent e) {
		close(true);
	}

	public void windowActivated(WindowEvent e) {}

	public void windowClosed(WindowEvent e) {
		close(false);
		Window s = SwingUtilities.windowForComponent(focusParent);
		if( s != null ) {
			s.removeWindowListener( this );
			s.requestFocus();
		}
		focusParent.requestFocusInWindow();
	}

	public void windowClosing(WindowEvent e) {
		close(false);
	}

	public void windowDeactivated(WindowEvent e) {}

	public void windowDeiconified(WindowEvent e) {}

	public void windowIconified(WindowEvent e) {
		close(false);
	}

	public void windowOpened(WindowEvent e) {}

	public void mouseClicked(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
		close(false);
	}

	public void mouseReleased(MouseEvent e) {}
}
