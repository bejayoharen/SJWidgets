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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.event.EventListenerList;

/**
 * puts much of the functionality of an option pane in a lightbox.
 * Instead of blocking, this calls back with an action listener.
 * Use getValue to get the results.
 * 
 */
public class SJLightboxOptionPane extends SJLightbox
{   
    private final SJOptionPane optionPane;
	private EventListenerList actionListeners = new EventListenerList();
	public int getResultValue() {
		return optionPane.getValue();
	}
	public SJButton getDefaultButton() {
		return optionPane.getDefaultButton();
	}
	public void addActionListener( ActionListener al ) {
		actionListeners.add( ActionListener.class, al );
	}
	public void removeActionListener( ActionListener al ) {
		actionListeners.remove( ActionListener.class, al );
	}
	public static SJLightboxOptionPane showConfirmDialog(SJRootPaneContainer parent, Object message, int buttonOptions ) {
		return showConfirmDialog(parent, message, buttonOptions, SJOptionPane.QUESTION_MESSAGE );
	}
	public static SJLightboxOptionPane showConfirmDialog(SJRootPaneContainer parent, Object message, int buttonOptions, int messageType ) {
		SJLightboxOptionPane sjop = new SJLightboxOptionPane( new SJOptionPane( message, buttonOptions, messageType ) );
		sjop.showIt( parent );
		return sjop;
	}
//	public static String showInputDialog( SJRootPaneContainer parent, Object message, String title ) {
//		JTextField jtf = new JTextField( 35 );
//		Object[] message2 = { message, SJPanel.createVerticalFiller(), jtf, SJPanel.createVerticalFiller() };
//		SJLightboxOptionPane op = new SJLightboxOptionPane( message2, DEFAULT_OPTION, QUESTION_MESSAGE );
//		op.createDialog(parent,title).setVisible(true);
//		int ret = op.getValue();
//		if( ret < 0 )
//			return null;
//		return jtf.getText();
//	}
//	public static Object showInputDialog( SJRootPaneContainer parent, Object message, String title, int messageType,Icon icon, Object[] options, Object defaultOption) {
//		if( options == null ) {
//			JTextField jtf = new JTextField(25);
//			jtf.setText( defaultOption.toString() );
//			jtf.setMaximumSize(jtf.getPreferredSize());
//			message = new Object[] { message, jtf };
//			SJLightboxOptionPane op = new SJLightboxOptionPane(
//					message,
//					getButtonOptions(OK_CANCEL_OPTION),
//					getDefaultOption(OK_CANCEL_OPTION),
//					getIcon(icon,messageType),
//					true );
//			op.createDialog(parent,title).setVisible(true);
//			int ret = op.getValue();
//			if( ret < 0 )
//				return null;
//			return jtf.getText();
//		} else {
//			SJLightboxOptionPane op = new SJLightboxOptionPane( message, options, defaultOption, getIcon(icon,messageType), false );
//			op.createDialog(parent,title).setVisible(true);
//			int ret = op.getValue();
//			if( ret < 0 )
//				return null;
//			return options[ret];
//		}
//	}
	public static SJLightboxOptionPane showInputDialog( SJRootPaneContainer parent, Object message, int messageType, Icon icon, Object[] options, Object defaultOption) {
		SJLightboxOptionPane ret = new SJLightboxOptionPane( new SJOptionPane( message, options, defaultOption, SJOptionPane.getIcon(icon,messageType), true) );
		ret.showIt(parent);
		return ret;
	}
	public static SJLightboxOptionPane showMessageDialog( SJRootPaneContainer parent, Object message ) {
		SJLightboxOptionPane ret = new SJLightboxOptionPane( new SJOptionPane( message, SJOptionPane.DEFAULT_OPTION, SJOptionPane.PLAIN_MESSAGE) );
		ret.showIt(parent);
		return ret;
	}
	public static SJLightboxOptionPane showMessageDialog( SJRootPaneContainer parent, Object message, int messageType ) {
		SJLightboxOptionPane ret = new SJLightboxOptionPane( new SJOptionPane( message, SJOptionPane.DEFAULT_OPTION, messageType ) );
		ret.showIt(parent);
		return ret;
	}
//	public static int showOptionDialog(SJRootPaneContainer parent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object defaultOption) {
//		SJLightboxOptionPane op = new SJLightboxOptionPane( message, options, defaultOption, getIcon(icon,messageType), false );
//		op.createDialog(parent,title).setVisible(true);
//		return op.getValue();
//	}
//	public static int showOptionButtonDialog(SJRootPaneContainer parent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object defaultOption) {
//		SJLightboxOptionPane op = new SJLightboxOptionPane( message,
//				options == null ? getButtonOptions(optionType) : options,
//				options == null ? getDefaultOption( optionType ) : defaultOption,
//				getIcon(icon,messageType),
//				true );
//		op.createDialog(parent,title).setVisible(true);
//		return op.getValue();
//	}
	
	
	private SJLightboxOptionPane( SJOptionPane optionPane ) {
		super( null );
		this.optionPane = optionPane;
		add( optionPane );
	}
	private void showIt( final SJRootPaneContainer parent ) {
		optionPane.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				parent.lightboxComplete(SJLightboxOptionPane.this);
				for( ActionListener al : actionListeners.getListeners(ActionListener.class) )
					al.actionPerformed(ae);
			}
		});
		parent.getRootPane().setDefaultButton( optionPane.getDefaultButton() );
		parent.showLightbox(this);
	}
}
