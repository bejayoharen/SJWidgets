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
 * Created on January 16, 2005 by bjorn
 *
 */
package com.xowave.sjwidget.help;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Hashtable;
import java.util.WeakHashMap;

import javax.swing.Timer;

import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.xowave.util.ListenerWeakHashSet;
import com.xowave.util.XMLUtil;

/**
 * @author bjorn
 *
 * This class keeps track of various components and associates them with
 * documentation.
 */

public class HelpComponentRegistry implements MouseListener {
	public static int TIMER_LENGTH = 3 * 1000 ; // 3 seconds
	
	/** Currently we are designed to use one per app. */
	private static HelpComponentRegistry instance;
	/** Most components simply map to a key, and the key maps to a pre-defined help data entry (read from
	 * the components.xml help file). This map translates components to keys.
	 */
	private WeakHashMap<Component,String> componentToKeyMap = new WeakHashMap<Component,String>();
	/** Most components simply map to a key, and the key maps to a pre-defined help data entry (read from
	 * the components.xml help file). This map translates keys to pre-defined help data entries.
	 */
	private final Hashtable<String,ComponentHelpData> keyToHelpDataMap = new Hashtable<String,ComponentHelpData>();
	/**This map is used for components that wish to have unique entries, rather than using pre-defined entries.
	 * This is useful, eg, for effect parameters and other dynamically created content.
	 */
	private WeakHashMap<Component,ComponentHelpData> componentToHelpDataMap = new WeakHashMap<Component,ComponentHelpData>();
	/**
	 * This is a set of listeners who want to know when the help text/data to be displayed to the user should change.
	 */
	private WeakHashMap<HelpComponentListener,Object> helpComponentListeners = new WeakHashMap<HelpComponentListener,Object>();
	/**Text used when the normal text has been overridden, eg, with a call to overrideWithText. */
	private String overrideText = null;
	private Component overrideComponentOwner = null;
	private String blinkText    = null;
	private Timer blinkTimer    = null;
	
	public static final boolean DEBUG = false;
	static private ListenerWeakHashSet<HelpComponentRegistry> hcrs = new ListenerWeakHashSet<HelpComponentRegistry>();
	static private String staticBlinkText = null;
	static private Timer  staticBlinkTimer = new Timer( TIMER_LENGTH, new ActionListener() {
		public void actionPerformed( ActionEvent ae ) {
			staticBlinkText = null;
			for( HelpComponentRegistry hcr : hcrs )
				hcr.updateListeners();
		}
	} );
	static {
		//setup static timer:
		staticBlinkTimer.setRepeats(false);
	}
	/** Sets up the singleton instance of the registry. This needs to only be called once,
	 * after that it is equivalent to calling getInstance(). At some point in the future, if
	 * it proves necessary to do so, the app can be expanded to support multiple registries.
	 * 
	 * @param resource a string referring to the location of the hcr flat file.
	 * @return the previously created registry if one exists or a newly created one.
	 * @throws JDOMException if the resource is badly formed.
	 * @throws IOException if the resource could not be read.
	 */
	public static HelpComponentRegistry initializeRegistry(String resource) throws JDOMException, IOException {
		if( instance == null ) {
			instance = new HelpComponentRegistry();
		}
		instance.load( resource );
		return instance;
	}
	/**
	 * Returns a previously initialized singleton registry. This is preferable to this
	 * initializeInstance method because, if the registry is known to be created, it does not
	 * throw an exception. Returns null if the registry has not been created.
	 */
	public static HelpComponentRegistry getInstance() {
		return instance;
	}
	
	private HelpComponentRegistry() {
		// -- setup the blink timer:
		blinkTimer = new Timer( TIMER_LENGTH, new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				blinkText = null;
				updateListeners();
			}
		} );
		blinkTimer.setRepeats(false);
		
		// -- register ourselves:
		hcrs.add( this );
	}
	
	private void load( String resource ) throws JDOMException, IOException {
		// -- read in the component data:
		java.net.URL url = ClassLoader.getSystemResource( resource ); //"help/components-old.xml" );
		System.out.println( resource +": " + url );
		url = HelpComponentRegistry.class.getClassLoader().getResource( resource ); //"help/components-old.xml" );
		System.out.println( resource +": " + url );
		Document doc;
		SAXBuilder parser = new SAXBuilder(false);
		doc = parser.build(url.openStream());
		Element root = doc.getRootElement();
		if( root.getName() != "ComponentHelp" )
			throw new RuntimeException("Invalid root component in components.xml");
		for( Object elt : root.getChildren("Component") ) {
			ComponentHelpData hd = new ComponentHelpData( (Element) elt );
			if( keyToHelpDataMap.containsKey(hd.getHelpKey()) )
				throw new RuntimeException( "Duplicate help key: " + hd.getHelpKey() );
			keyToHelpDataMap.put( hd.getHelpKey(), hd );
		}
	}
	
	/**Associates the given component with the given string, sets the tool tip and
	 * begins listening. Can only be called once per component. If the helpKey
	 * needs updating, use updateCompnentHelpKey. NOTE: that comment may be old. I think it's okay to re-register.*/
	public void register( Component c, String helpKey ) {
		if( helpKey == null ) {
			componentToKeyMap.remove( c );
			return;
		}
		if( keyToHelpDataMap.get(helpKey) == null ) {
			System.out.println( "WARNING: attempt to register "
					+ "component with missing helpKey: "
					+ helpKey
					+ ". Ignoring." );
			return;
		}
		//register or re-register it in our local look-up table:
		boolean alreadyRegistered = null != componentToKeyMap.put( c, helpKey );
		
		//add mouse listener to the component, but only do this once per component!
		if( !alreadyRegistered )
			c.addMouseListener( this );
		
		//If we wanted tool tips, this is where we'd set them

		if( c == curComp ) //update in case we are on it:
			updateListeners();
	}
	/**Allows special text determined by the app. to be assigned to a given component.*/
	public void registerText(Component c, String oneLineDescription, String name, String detail) {
		boolean alreadyRegistered = null != componentToHelpDataMap.put( c, new ComponentHelpData(null,oneLineDescription,name,detail) );
		if( !alreadyRegistered )
			c.addMouseListener( this );
		if( c == curComp )
			updateListeners();
	}
	/**Recursively searches through all children of component c and registers
	 * the given helpKey to all those components.*/
	public void registerWithChildren( Component c, String helpKey ) {
		register( c, helpKey );
		if( c instanceof Container ) {
			Container cont = (Container) c;
			Component[] comps = cont.getComponents();
			for( int i=0; i<comps.length; ++i )
				registerWithChildren( comps[i], helpKey );
		}
	}
	/** Allows some of the displayed data to be replaced with text.
	 * If text is null, previous override is released and the default
	 * text is displayed again.
	 * 
	 * owner allows one component to prevent other components from
	 * doing their own overrides until the text is set back to null.
	 *
	 * If owner or text is null, all overrides are released.
	 *
	 * @param owner
	 * @param text
	 */
	public void overrideWithText( Component owner, String text ) {
		if( overrideComponentOwner == null || overrideComponentOwner.equals(owner) || text == null ) {
			overrideComponentOwner = owner;
			overrideText = text;
			if( text == null )
				overrideComponentOwner = null;
			updateListeners();
		}
	}
	/** Shows the given text for a short amount of time. */
	public void blinkText( String text ) {
		blinkTimer.stop();
		blinkText = text;
		updateListeners();
		blinkTimer.start();
	}
//	/** Blinks on all HCRs */
	public static void blinkTextOnAll( String text ) {
		staticBlinkTimer.stop();
		staticBlinkText = text;
		for( HelpComponentRegistry hcr : hcrs )
			hcr.updateListeners();
		staticBlinkTimer.start();
	}
	/**Adds the given HelpComponentListener to the list of listeners that will be
	 * notified when the mouse moves and is over the given component.
	 */
	public void addHelpComponentListener( HelpComponentListener hcl ) {
		helpComponentListeners.put( hcl, null );
	}
	public interface HelpComponentListener {
		public void helpComponentChanged( Component comp, ComponentHelpData chd );
		public void noComponent();
	}
	/** @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent) */
	public void mouseClicked(MouseEvent e) {
	}
	/** @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent) */
	public void mousePressed(MouseEvent e) {
	}
	/** @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent) */
	public void mouseReleased(MouseEvent e) {
	}
	private HelpComponentListener[] hcls = new HelpComponentListener[0];
	private Component curComp = null;
	/** @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent) */
	public void mouseEntered(MouseEvent e) {
		curComp = (Component) e.getSource();
		updateListeners();
	}
	private void updateListeners() {
		if( curComp == null && overrideText == null && blinkText == null && staticBlinkText == null )
			return; //nothing to do
		
		String key = null;
		ComponentHelpData chd = null;
		
		//Load the key if found and help data, if found
		if( curComp != null )
			key = (String) componentToKeyMap.get( curComp );
		if( key != null ) //there is a key registered on the component, use data associated with that key:
			chd = (ComponentHelpData)keyToHelpDataMap.get(key);
		else if( curComp != null ) //No key registered on component, try the component map:
			chd = (ComponentHelpData)componentToHelpDataMap.get(curComp);
		
		//handle text overrides:
		if( overrideText != null )
			chd = new ComponentHelpData( null, overrideText, "", "" ) ;
		if( blinkText != null )
			chd = new ComponentHelpData( null, blinkText, "", "" ) ;
		if( staticBlinkText != null )
			chd = new ComponentHelpData( null, staticBlinkText, "", "" ) ;
		hcls = (HelpComponentListener[]) helpComponentListeners.keySet().toArray(hcls);
		for( int i=0; i<hcls.length && hcls[i]!=null; ++i )
			hcls[i].helpComponentChanged( curComp, chd );
	}
	/** @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent) */
	public void mouseExited(MouseEvent e) {
		if( e.getSource() != curComp )
			return;
		curComp = (Component) e.getSource();
		for( int i=0; i<hcls.length && hcls[i]!=null; ++i )
			hcls[i].noComponent();
	}

	public static void registerComponent(Component c, String helpKey) {
		if( instance != null )
			instance.register(c, helpKey);
	}
}
