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
 * Created on April 9, 2004
 *
 */
package com.xowave.util;

/**
 * @author bjorn
 *
 * Contains static classes and so on to support differences between OSX java
 * and java on other platforms. May be extended in the future to handle other
 * OSes.
 * 
 * The procedure for using the class is this:
 * first tell it what OS is being run (by calling setOS).
 * Then setup the user directory (by calling initializeWorkingDirectory)
 * it is then safe to use the functions and access the user.dir System property.
 */

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Environment {
	public static final int UNSET = -1;
	public static final int UNKNOWN = 0;
	public static final int WINDOWS = 1;
	public static final int MAC_OS_X = 2;
	private static int MAC_OS_VERSION[] = null;
//	private static boolean isDebug = false;
	private static boolean hasQuickTime = false;
	public static final String[] OS_NAMES = new String[] { "Unknown/Default", "Windows", "Mac OS X (Darwin)" };
	protected static XFile OSXAppRoot = null;
	protected static String serverPath = null;
	protected static XFile workingDirectory = null; 

	protected static int os = UNSET;

	public static void initialize() throws IOException {
		if (os != UNSET)
			throw new RuntimeException("Trying to change Environment.");
		os = UNKNOWN;
		if( System.getProperty("os.name").toLowerCase().startsWith("mac os x") )
			os = MAC_OS_X;
		else if( System.getProperty("os.name").startsWith("Windows") )
			os = WINDOWS;
		
		if( os == MAC_OS_X )
			determineMacOSVersion();
	}
	private static void determineMacOSVersion() throws IOException {
		File f = new File( "/System/Library/CoreServices/SystemVersion.plist" );
		MAC_OS_VERSION = null;
		try {
			SAXBuilder parser = new SAXBuilder(false);
			//this hack is intended to prevent SAXBuilder from connecting to the 'net 
			parser.setEntityResolver( new EntityResolver() {
				public InputSource resolveEntity(String arg0, String arg1) throws SAXException, IOException {
					return new InputSource( new StringReader("") );
				}
			});
//			FileInputStream fis = new FileInputStream(f);
//			{
//				System.out.println( "=====" );
//				int i=0;
//				while( (i=fis.read()) != -1 ) {
//					System.out.print( (char)i );
//				}
//				System.out.println( "=====" );
//			}
			Document doc = parser.build(new FileInputStream(f));
			Element root = doc.getRootElement();
			Element mainDictElt = root.getChild( "dict" );
			XMLDict mainDict = new XMLDict( mainDictElt, 0, true );
			String version = mainDict.getStringValue("ProductVersion", "string");
			String[] mv = version.split("\\.");
			if( mv.length < 3 )
				return;
			MAC_OS_VERSION = new int[3];
			for( int i=0; i<3; ++i) {
				MAC_OS_VERSION[i] = Integer.parseInt(mv[i]);
			}
			return;
		} catch (NullPointerException npe) {
			throw new IOException( "Missing OS X version info." );
		} catch (org.jdom.JDOMException jde) {
			throw new IOException( "Misformatted OS X version info.", jde );
		} catch (FileNotFoundException fne) {
			throw new IOException( "Missing OS X version info file." );
		} catch (OutOfMemoryError oome) {
			throw new IOException( "Ran out of memory reading Mac OS X version. Probably the file is corrupt." );
		} catch (XMLException xmle) {
			throw new IOException( "Misformatted OS X version info." );
		} catch (NumberFormatException nfe) {
			throw new IOException( "Misformatted OS X version info." );
		}
	}
	/** returns null if not mac os or if there was a problem determining OS version.
	 * othersiwe, returns an array of length three. eg. 10.4.10 would be
	 * int[0] = 10
	 * int[1] = 4
	 * int[2] = 10
	 * 
	 * @return
	 */
	public static int[] getMacOSVersion() {
		return MAC_OS_VERSION;
	}
	
	public static void shutdown() {
		os = UNSET;
	}

//	public static void setDebug() {
//		isDebug = true;
//	}
	public static boolean isOSSet() {
		return os != UNSET;
	}
	public static int getOS() {
		return os;
	}
	/** a string suitable for cgi indicating the general category of the os, such as "Mac_OS_X", "Windows" etc. */
	public static String getOSCategoryString() {
		checkInitialized();
		switch( os ) {
		case MAC_OS_X:
			return "Mac_OS_X";
		case WINDOWS:
			return "Windows";
		default:
			throw new RuntimeException("Unknown OS.");
		}
	}
	public static boolean hasQuickTime() {
		checkInitialized();
		return hasQuickTime;
	}
	protected static void checkInitialized() {
		if (os == UNSET)
			throw new RuntimeException("Environment class un-initialized.");
	}

	public static String getDefaultAcceleratorModifier() {
		checkInitialized();
		if (os == MAC_OS_X)
			return "meta";
		else
			return "control";
	}
	public static String getEnvironmentName() {
		checkInitialized();
		return OS_NAMES[os];
	}
//	public static String getDefaultSettingsFileName() {
//		checkInitialized();
//		return DEFAULT_SETTINGS_FILE_NAMES[os];
//	}

	/**
	 * This function should be called early in the program: before
	 * any calls to System.getProperty("user.dir") are made but after
	 * the call to setOSX().
	 * After calling this function, the working directory will be set to the
	 * user's Home directory and the initial working directory will be saved.
	 * This is necessary because, on the mac, the working directory
	 * gives the root of the XO WAVE.app, which is needed for launching XOmux and XOengine.
	 * After calling this function
	 * 
	 * @author bjorn
	 */
	public static void initializeDirectory() {
		checkInitialized();
		if (os == MAC_OS_X)
			OSXAppRoot = new XFile( System.getProperty("user.dir") );
		workingDirectory = new XFile( System.getProperty("user.home") );
	}

	/**
	 * Determines is, given the OS, a special Quit menu item needs to be shown.
	 * Some OSes, eg, MOS X, will display their own.
	 * 
	 * @return true if the OS does not provide its own Quit menu and the user should provide one.
	 */
	public static boolean shouldAddQuitToMenu() {
		checkInitialized();
		return os != MAC_OS_X;
	}
	public static String getEnvironmentAltKeyName() {
		checkInitialized();
		if( os == MAC_OS_X )
          return "opt";
		else
          return "alt";
	}
	public static String getEnvironmentAltKey() {
		checkInitialized();
		if( os == MAC_OS_X )
          return "\u2325";
		else
          return "alt";
	}
	public static String getEnvironmentMetaKeyName() {
		checkInitialized();
		if( os == MAC_OS_X )
          return "cmd";
		else
          return "meta";
	}
	public static char getEnvironmentMetaKeySymbol() {
		checkInitialized();
		//apple option is 2326
		//shift is 8679
		//KeyStrokeSelector may have more hints
		if( os == MAC_OS_X )
          return '\u2318';
		else
          return '\u2394';
	}
	public static boolean shouldAddPreferencesToMenu() {
		checkInitialized();
		return os != MAC_OS_X;
	}
	public static boolean isAWTFileChooserPreferred() {
		checkInitialized();
		return os == MAC_OS_X;
	}
	public static boolean shouldNonModalDialogsSetupMenus() {
		checkInitialized();
		if( os != MAC_OS_X )
			return false;
		// -- older versions of mac OS need this, 10.4 and newer do not.
		String versionString = System.getProperty("os.version") ;
//		int numVals = 0;
		String[] versionStrings = StringUtils.getTokenArray(versionString,0,"._, ");
		int[] vers = new int[ versionStrings.length ];
		for( int i=0; i<vers.length; ++i )
			vers[i] = Integer.parseInt( versionStrings[i] );
		//previously, apple bug #3682441 meant that menus in non-modal dialogs didn't work correctly.
		//this seems to have been fixed!
		if( vers[1] >= 6 )
			return true;
		if( vers[0] > 10 || ( vers[0] == 10 && vers[1] >= 4 ) )
			return false;
		
		return true;
	}
	public static boolean isMOSXPreTiger() {
		checkInitialized();
		if( os != MAC_OS_X )
			return false;
		String versionString = System.getProperty("os.version") ;
		String[] versionStrings = StringUtils.getTokenArray(versionString,0,"._, ");
		int[] vers = new int[ versionStrings.length ];
		for( int i=0; i<vers.length; ++i )
			vers[i] = Integer.parseInt( versionStrings[i] );
		if( vers[0] > 10 || ( vers[0] == 10 && vers[1] >= 4 ) )
			return false;
		
		return true;
	}
	/**
	 * Some OSes confirm that a file exists when trying to save.
	 * Use this function to determine if this is such an OS.
	 * 
	 * @return true if the OS does not confirm replace file. If this returns
	 *  true, then you must confirm replacement yourself.
	 */
	public static boolean shouldConfirmReplaceFile() {
		checkInitialized();
		return os != MAC_OS_X;
	}
	public static boolean requiresWindowBeVisible() {
		checkInitialized();
		return os != MAC_OS_X;
	}
	/**
	 * If appropriate for the OS, installs a listener that sends window  closing event to the
	 * given window listener when the user types Command-W and escape.
	 * It does so by installing an action on the contentpane's Action and input maps.
	 * Keep in mind that the correct behaviour may be managed by MyFileMenu.
	 * 
	 * @param wl
	 * @param jd
	 */
	public static void enableCommandWClose(
						final java.awt.Window w) {
		KeyListener kl = new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				boolean closeWindow = false;
				if( e.getKeyCode() == KeyEvent.VK_ESCAPE )
					closeWindow = true;
				if( os == MAC_OS_X && e.getKeyCode() == KeyEvent.VK_W && e.getModifiersEx() == KeyEvent.META_DOWN_MASK )
					closeWindow = true;
				if( closeWindow && w.isActive() ) {
					WindowListener wls[] = w.getWindowListeners();
					WindowEvent we = new WindowEvent(w, WindowEvent.WINDOW_CLOSING);
					for( int i=0; i<wls.length; ++i )
						wls[i].windowClosing( we );
				}
			}
			public void keyReleased(KeyEvent e) {}
		} ;
		if( w instanceof JDialog ) {
			final JDialog jd = (JDialog) w;
			enableCommandWClose(jd, (JComponent)jd.getContentPane());
			//sometimes the former doesn't work:
			jd.addKeyListener( kl );
		} else if( w instanceof JFrame ) {
			JFrame jf = (JFrame) w;
			enableCommandWClose(jf, (JComponent)jf.getContentPane());
		} else if( w instanceof JWindow ) {
			JWindow jw = (JWindow) w;
			enableCommandWClose(jw, (JComponent)jw.getContentPane());
			//sometimes the former doesn't work:
			jw.addKeyListener( kl );
		} else {
			throw new IllegalArgumentException();
		}
	}
	/**
	 * @param jf
	 * @param cp
	 */
	private static void enableCommandWClose(final Window w, JComponent jc) {
		final boolean debug=false;
		checkInitialized();
		ActionMap am = jc.getActionMap();
		InputMap im = jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		String key = "Close on Command-W";
		if( debug )
			System.out.println( "Adding ESC/Command-W listener to " + jc + " in window " + w + "." );
		am.put(key, new AbstractAction() {
			public void actionPerformed(java.awt.event.ActionEvent ae) {
				if( debug )
					System.out.println( "Received event. Checking to see if window " + w + " is active." );
				if( !w.isActive() ) {
					return;
				}
				if( debug )
					System.out.println( "Window is active. Attempting to close window " + w + "." );
				WindowListener wls[] = w.getWindowListeners();
				WindowEvent we = new WindowEvent(w, WindowEvent.WINDOW_CLOSING);
				for( int i=0; i<wls.length; ++i )
					wls[i].windowClosing( we );
			}
		});
		im.put(KeyStroke.getKeyStroke("ESCAPE"), key);
		if (os != MAC_OS_X)
			return;
		im.put(KeyStroke.getKeyStroke(getDefaultAcceleratorModifier() + " W"), key);
	}

	/**
	 * Firing events off in Global hot key manager causes a crash on the mac platform.
	 * 
	 * @return true if event dispatch in hot key manager may cause a crash
	 */
	public static boolean eventDispatchCausesCrash() {
		checkInitialized();
		return os == MAC_OS_X;
	}
	/**
	 * NIO is unsafe on the mac platform. (See bug #3839709) this checks to see
	 * if the current platform safely supports NIO.
	 * 
	 * Currently, it now returns false since several platforms experience
	 * problems such as having low priority threads block when using NIO.
	 * 
	 * @author bjorn
	 *
	 */
	public static boolean isNIOSafe() {
		checkInitialized();
		return false; //os != MAC_OS_X;
	}
	/**
	 * On the mac, shutdown hooks don't seem to work in this app. Hmm.
	 * 
	 * @return
	 */
	public static boolean doesShutdownHookWork() {
		if (os == UNSET)
			return false;
		else
			return os != MAC_OS_X;
	}
	
	/**
	 * call this function with a component that will either
	 * process or ignore the space bar and/or enter.
	 * 
	 * When focus is transferred, focus will be put onto the given component.
	 */
	public static void setFocusElement(Window w, final java.awt.Component c) {
		w.setFocusTraversalPolicy(new java.awt.FocusTraversalPolicy() {
			@Override
			public Component getComponentAfter(Container focusCycleRoot,
					Component aComponent) {
				return c;
			}

			@Override
			public Component getComponentBefore(Container focusCycleRoot,
					Component aComponent) {
				return c;
			}

			@Override
			public Component getDefaultComponent(Container focusCycleRoot) {
				return c;
			}

			@Override
			public Component getLastComponent(Container focusCycleRoot) {
				return c;
			}

			@Override
			public Component getFirstComponent(Container focusCycleRoot) {
				return c;
			}

			@Override
			public Component getInitialComponent(java.awt.Window win) {
				return c;
			}
		});
		c.requestFocusInWindow();
		((javax.swing.JComponent) c).setFocusCycleRoot(true);
	}
	/**
	 * @return
	 */
	public static boolean shrinkSomeComponents() {
		checkInitialized();
		return os == MAC_OS_X;
	}
	public static boolean hasiTunes() {
		checkInitialized();
		return os == MAC_OS_X;
	}
	public static boolean shouldUseMenuNmemonics() {
		checkInitialized();
		return os != MAC_OS_X;
	}
	private static RenderingHints FAST_RENDER_HINTS = new RenderingHints(null);
	private static RenderingHints BEAUTIFUL_RENDER_HINTS = new RenderingHints(null);
	static {
		FAST_RENDER_HINTS.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
		FAST_RENDER_HINTS.put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		FAST_RENDER_HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		FAST_RENDER_HINTS.put(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_DISABLE);
		FAST_RENDER_HINTS.put(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		FAST_RENDER_HINTS.put(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		FAST_RENDER_HINTS.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED);
		FAST_RENDER_HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
		FAST_RENDER_HINTS.put(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_SPEED);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_ENABLE);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED);
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
		BEAUTIFUL_RENDER_HINTS.put(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	}
	/** Sets the given graphics object to use the same rendering hints used by track canvas when CPU time is available */
	public static void setBeautifulRendering( Graphics2D g ) {
		g.setRenderingHints(BEAUTIFUL_RENDER_HINTS);
	}
	/** Sets the given graphics object to use the same rendering hints used by track canvas when CPU time is at a premium */
	public static void setPerformaceRendering( Graphics2D g ) {
		g.setRenderingHints(FAST_RENDER_HINTS);
	}
	public static XFile getDownloadDirectory() {
		checkInitialized();
		switch( os ) {
		case UNKNOWN:
			return new XFile( System.getProperty("user.home") );
		case WINDOWS:
			return new XFile( System.getProperty("user.home") );
		case MAC_OS_X:
			return new XFile( System.getProperty("user.home") + "/Downloads" );
		default:
			return new XFile( System.getProperty("user.home") );
		}
	}
}
