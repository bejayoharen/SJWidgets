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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.xowave.sjwidget.util.BackgroundPainter;
import com.xowave.sjwidget.util.WidgetUtil;
import com.xowave.util.Environment;

/**
 * This class behaves like a JDialog except for a few things:
 * <ul>
 * <li> Some bugs are fixed/worked around, such as bug 7240026 on macs
 * <li> You can assign appearace features to the root pane with a widget ID
 * <li> escape and command-w (mac only) should work as expected.
 * </ul>
 * 
 * @author bjorn
 *
 */
public class SJDialog extends JDialog implements SJWidget {
	private String ID;
	private String widgetText;
	private JFrame ownerHack;
	static JFrame getOwnerHack() {
		JFrame ownerHack = new JFrame("7240026");
		// let's be invisible!
		ownerHack.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		// and small!
		ownerHack.setSize(1, 1);
		// and undecorated!
		ownerHack.setUndecorated(true);

		return ownerHack;
	}

	private boolean once = false;
	@Override
	public void setVisible( boolean vis ) {
		if( vis && ownerHack != null ) {
			if( !ownerHack.isVisible() )
				ownerHack.setLocationByPlatform(true);
			ownerHack.setVisible(true);
			if( !once )
				addWindowListener( new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
						ownerHack.setVisible( false );
					}
				});
			once = true;
			super.setVisible(vis);
		} else {
			super.setVisible(vis);
		}
	}

	/**
	 * @throws HeadlessException
	 */
	public SJDialog(String ID) throws HeadlessException {
		super(getOwnerHack());
		ownerHack = (JFrame) getOwner();
		setup();
		setWidgetID(ID);
	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public SJDialog(Frame owner, String ID) throws HeadlessException {
		super(owner);
		setup();
		setWidgetID(ID);
	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public SJDialog(Dialog owner, String ID) throws HeadlessException {
		super(owner);
		setup();
		setWidgetID(ID);
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public SJDialog(Frame owner, boolean modal, String ID) throws HeadlessException {
		super(owner, modal);
		setModal( modal );
		setup();
		setWidgetID(ID);
	}

	/**
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public SJDialog(Frame owner, String title, String ID) throws HeadlessException {
		super(owner, title);
		setup();
		setWidgetID(ID);
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public SJDialog(Dialog owner, boolean modal, String ID) throws HeadlessException {
		super(owner, modal);
		setModal( modal );
		setup();
		setWidgetID(ID);
	}

	/**
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public SJDialog(Dialog owner, String title, String ID) throws HeadlessException {
		super(owner, title);
		setup();
		setWidgetID(ID);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public SJDialog(Frame owner, String title, boolean modal, String ID) throws HeadlessException {
		super(owner, title, modal);
		setModal( modal );
		setup();
		setWidgetID(ID);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public SJDialog(Dialog owner, String title, boolean modal, String ID) throws HeadlessException {
		super(owner, title, modal);
		setModal( modal );
		setup();
		setWidgetID(ID);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public SJDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc, String ID) {
		super(owner, title, modal, gc);
		setModal( modal );
		setup();
		setWidgetID(ID);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 * @throws HeadlessException
	 */
	public SJDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc, String ID) throws HeadlessException {
		super(owner, title, modal, gc);
		setModal( modal );
		setup();
		setWidgetID(ID);
	}

	public String getWidgetID() {
		return ID;
	}
	
	public String getWidgetClass() {
		return ID;
	}

	public SJDialog setWidgetID(String ID) {
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}
	
	private void setup() {
		getContentPane().setFocusable(true);//This hack seems to allow setDefaultButton() to work.
		getContentPane().requestFocus();
		Environment.enableCommandWClose(this);
	}
	
	public void paintComponent( Graphics g ) {
	}
	
	public void setBackgroundPainter(BackgroundPainter bp) {
		throw new RuntimeException( "Not implemented." );
	}
	
	public void setWidgetText(String text) {
		widgetText = text;
	}
	public String getWidgetText() {
		return widgetText;
	}
}
