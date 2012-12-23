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

/*
 * Created on Aug 1, 2004
 * 
 * Based on code by Mathew Schmidt
 * http://www.javalobby.org/kb/entry.jspa?categoryID=48&entryID=120
 * 
 * This is code is modified to give the normal Event handling mechanism a
 * chance to handle each event and only fires the registered actions if
 * the event is not consumed.
 * 
 * Also keeps track of the current mouse position - something that is otherwise impossible
 * to get during a drag and drop operation.
 * 
 */
package com.xowave.util;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;

import javax.swing.*;

final public class GlobalEventManager extends EventQueue { //implements TabletListener {
	public static int POINTER_TYPE_UNKNOWN = 0;
	public static int POINTER_TYPE_PEN     = 1;
	public static int POINTER_TYPE_CURSOR  = 2;
	public static int POINTER_TYPE_ERASER  = 3;
	private static final boolean DEBUG = false;
	private static final GlobalEventManager instance = new GlobalEventManager();
	private final HashMap<KeyStroke, WeakHashMap<Action, ActionInfo>> keyStrokes = new HashMap<KeyStroke, WeakHashMap<Action, ActionInfo>>(); //a map from keystrokes to weakHashSets of action infos
	private static Point mousePosition = null;
//	private static TabletWrapper tabletWrapper = new TabletWrapper();
	private static Point2D.Double tilt = new Point2D.Double(0,0);
	private static float pressure = 0;
	private static int pointerType = POINTER_TYPE_CURSOR;
	
	public static Point getMousePosition() {
		return mousePosition;
	}
	public static Point2D.Double getPointerTilt() {
		return tilt;
	}
	public static float getPenPressure() {
		return pressure;
	}
	public static int getPointerType() {
		return pointerType;
	}
	
	/**Creates a newly allocated point whose position is the
	 * mouse position relative to component c.
	 * @param c the component to translate mouse position to
	 * @return a newly allocated point corresponding to the mouse position
	 * over component c.
	 */
	public static Point getMousePosition(Component c) {
		if( mousePosition == null )
			return null;
		Point p = new Point( mousePosition );
		SwingUtilities.convertPointFromScreen( p, c );
		return p;
	}
	/**
	 * Returns true if the mouse is currently inside the given component.
	 */
	public static boolean isMouseIn(Component c) {
		if( mousePosition == null )
			return false;
		Point p = getMousePosition( c );
		if( p.x < 0 || p.y < 0 || p.x > c.getWidth() || p.y > c.getHeight() )
			return false;
		return true;
	}

	/**Allows suspension of global event responses while
	 * the given window is visible. This is especially useful
	 * for dialogs.
	 * 
	 * Note that the suspension will take effect each and
	 * every time the window is displayed and works regardless
	 * of weather the window is already visible, and doesn't get
	 * screwed up, for example, when multiple windows are
	 * displayed.
	 * 
	 */
	public static void suspendWhileVisible(final Window window) {
		//It might be overkill to have all these listeners, but it's unclear from the docs
		//  which events are fired, eg, when the parent of a window is hidden.
		//  This should cover all cases. Note: I couldn't get WindowStateEvents to fire at all.
		instance.autoSuspendOnWindow( window );
		window.addComponentListener( new ComponentListener() {
			public void componentHidden(ComponentEvent e) {
				instance.autoSuspendOnWindow( window );
			}
			public void componentMoved(ComponentEvent e) {
				instance.autoSuspendOnWindow( window );
			}
			public void componentResized(ComponentEvent e) {
				instance.autoSuspendOnWindow( window );
			}
			public void componentShown(ComponentEvent e) {
				instance.autoSuspendOnWindow( window );
			}
		});
		window.addWindowListener( new WindowListener() {
			public void windowActivated(WindowEvent e) {
				instance.autoSuspendOnWindow( window );
			}
			public void windowClosed(WindowEvent e) {
				instance.autoSuspendOnWindow( window );
			}
			public void windowClosing(WindowEvent e) {
				instance.autoSuspendOnWindow( window );
			}
			public void windowDeactivated(WindowEvent e) {
				instance.autoSuspendOnWindow( window );
			}
			public void windowDeiconified(WindowEvent e) {
				instance.autoSuspendOnWindow( window );
			}
			public void windowIconified(WindowEvent e) {
				instance.autoSuspendOnWindow( window );
			}
			public void windowOpened(WindowEvent e) {
				instance.autoSuspendOnWindow( window );
			}
		} );
	}
	
	static {
		// here we register ourselves as a new link in the chain of
		// responsibility
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(instance);
		// and register for tablet events:
//		tabletWrapper.addTabletListener(instance);
	}
	
	/**
	 * Globally Associates a given keystroke with an action command and an action, so that when that
	 * keystroke is pressed anywhere in the app, the given action will be fired with the given actionCommand.
	 * Multiple actionCommands and keystrokes may be associated with the same action.
	 * 
	 * @param ks the keystroke to cause the action to fire
	 * @param actionCommand the command to be sent to the keystroke.
	 * @param a the action to be fired when the key is pressed.
	 * @param isSuspendable if false, the keystroke will be immune to suspension. Usually, this should be true.
	 * 
	 */
	public static void addKeyStrokeAction( KeyStroke ks, String actionCommand, Action a, boolean isSuspendable ) {
		WeakHashMap<Action, ActionInfo> whs = instance.keyStrokes.get(ks);
		if( whs == null ) {
			whs = new WeakHashMap<Action, ActionInfo>();
			instance.keyStrokes.put(ks,whs);
		}
		whs.put( a, new ActionInfo( a, actionCommand, isSuspendable ) );
	}
	/**
	 * Removes the action from the action map, so after this, the keystroke
	 * is still associated with an action command, but the command is not
	 * associated with any action.
	 * 
	 * @param ks
	 *            the KeyStroke previously passed to
	 *            addKeyStrokeAction, who's corresponding action
	 *            and actionCommand should be removed.
	 */
	public static void removeKeyStrokeAction( KeyStroke ks, String actionCommand, Action action ) {
		WeakHashMap<Action,ActionInfo> whs = instance.keyStrokes.get(ks);
		if( whs == null )
			return;
		Iterator<ActionInfo> it = whs.values().iterator();
		int count = 0;
		while( it.hasNext() ) {
			ActionInfo ai = (ActionInfo) it.next();
			if( ai.action.equals( action ) && ai.command.equals( actionCommand ) ) {
				it.remove();
				++count;
			}
		}
		if( count != 1 )
			System.exit(0);
	}

	private GlobalEventManager() {} // We only ever create one of these

	public static GlobalEventManager getInstance() {
		return instance;
	}
	
	private String[] eventRingBuffer = new String[ 100 ]; //access to this should be synchronized, but to avoid deadlock, we'll take the risk of corruption!
	private int eventRingBufferPosition = 0;

	public static String getEventRingBufferText() {
		return instance.getEventRingBufferTextPrivate();
	}
	private String getEventRingBufferTextPrivate() {
		try {
			String ret = "";
			for( int i=0; i<eventRingBuffer.length; ++i ) {
				ret += eventRingBuffer[ ( i + eventRingBufferPosition ) % eventRingBuffer.length ] + "\n";
			}
			return ret;
		} catch (Exception e) {
			//could run out of memory or something
			return "Exception when getting ring-buffer test" ;
		}
	}

	protected void dispatchEvent(final AWTEvent event) {
		eventRingBuffer[ eventRingBufferPosition ] = event == null ? "<NULL event>" : event.toString();
		eventRingBufferPosition = ( eventRingBufferPosition + 1 ) % eventRingBuffer.length ;

		if (event instanceof KeyEvent) {
			if (DEBUG)
				System.out.println(event);
			super.dispatchEvent(event);
			if (((KeyEvent) event).isConsumed()) {
				if (DEBUG) {
					System.out.println("Event is Consumed");
					System.out.println("Likely Consumer: "
							+ FocusManager.getCurrentManager().getFocusOwner());
				}
				return;
			}
			KeyStroke ks = KeyStroke.getKeyStrokeForEvent((KeyEvent) event);
			if (DEBUG)
				System.out.println("KeyStroke=" + ks);

			WeakHashMap<Action,ActionInfo> whs = instance.keyStrokes.get(ks);
			if( whs == null )
				return;
			Iterator<ActionInfo> it = whs.values().iterator();
			while( it.hasNext() ) {
				final ActionInfo actionInfo = (ActionInfo) it.next();
				if ( shouldProcessEvent(actionInfo) ) {
					if (DEBUG)
						System.out.println("Action Command=" + actionInfo.command);
					//final KeyStrokeActionInfo actionInfo = (KeyStrokeActionInfo) keyStrokes.get(actionKey);
					if (DEBUG)
						System.out.println(actionInfo);
					if (actionInfo != null && actionInfo.action.isEnabled()) {
						if (DEBUG)
							System.out.println("Dispatching Event.");
						if( event.getSource() == null ) {
							System.out.println( "WARNING: null event source." );
						} else {
							if (Environment.eventDispatchCausesCrash()) {
								final ActionEvent ae = new ActionEvent(event
										.getSource(), event.getID(), actionInfo.command,
										((KeyEvent) event).getModifiers());
								SwingUtilities.invokeLater( new Runnable() {
									public void run() {
										actionInfo.action.actionPerformed(ae);
									}
								} );
							} else {
								actionInfo.action.actionPerformed(new ActionEvent(event
										.getSource(), event.getID(), actionInfo.command,
										((KeyEvent) event).getModifiers()));
							}
						}
						((KeyEvent) event).consume();
					}
				}
			}
			return;
		} else if( event instanceof MouseEvent ) {
			if (DEBUG)
				System.out.println(event);
			Point mousePosition = ((MouseEvent) event).getPoint();
			if( event.getSource() instanceof Component ) {
				SwingUtilities.convertPointToScreen( mousePosition, (Component) event.getSource() );
				GlobalEventManager.mousePosition = mousePosition;
			}
		}
		if( DEBUG ) System.out.println( "Passing Event: " + event );
		try {
			super.dispatchEvent(event); // let the next in chain handle event
		} catch( IllegalComponentStateException icse ) {
			//this seems to happen on some client machines. Possibly quad-processor related.
		} catch( ArrayIndexOutOfBoundsException aioobe ) {
			//This catch is a work-around for apple bug # 4429622
			//macs sometimes throw this exception when a display is added.
			try {
				StackTraceElement ste[] = aioobe.getStackTrace();
				if( ste[0].getMethodName().equals("displayChanged") && ste[0].getClassName().equals( "apple.awt.CWindow" ) ) {
					System.out.println( "WARNING: Detected an exception caused by mac bug #4429622. Ignoring." );
					return;
				}
			} catch( Exception e ) {
				System.out.println( "Trying to detect exception type caused: " + e.getLocalizedMessage() );
			}
			throw aioobe;
		}
	}
	private boolean shouldProcessEvent( ActionInfo ai ) {
		if( ai == null )
			return false;
		return !isSuspended() || !ai.isSuspendable;
	}
	private void autoSuspendOnWindow(Component comp) {
		if( comp.isShowing() )
			instance.addSuspensionObject( comp );
		else
			instance.removeSuspensionObject( comp );
	}
	private HashSet<Object> suspensionObjects = new HashSet<Object>(1);
	private void addSuspensionObject( Object obj ) {
		suspensionObjects.add( obj );
	}
	private void removeSuspensionObject( Object obj ) {
		suspensionObjects.remove(obj);
	}
	private boolean isSuspended() {
		return suspensionObjects.size() > 0 ;
	}
//	public void tabletEvent(TabletEvent e) {
//		tilt.x = e.getTiltX();
//		tilt.y = e.getTiltY();
//		pressure = e.getPressure();
//	}
//	public void tabletProximity(TabletProximityEvent e) {
//		pointerType = e.getPointingDeviceType();
//	}
}
class ActionInfo {
	final Action action;
	final String command;
	final boolean isSuspendable;
	ActionInfo( Action action, String command, boolean isSuspendable ) {
		this.action = action;
		this.command = command;
		this.isSuspendable = isSuspendable;
	}
	public String toString() {
		return "Fire action " + command ;
	}
}
