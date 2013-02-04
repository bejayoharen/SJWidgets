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

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.xowave.sjwidget.help.HelpComponentRegistry;

/**
 * @author bjorn
 */
public final class WidgetUtil {
	private static HashMap<String,String> idToClass = new HashMap<String,String>();
	private static HashMap<String,WidgetDescription> descriptions = new HashMap<String,WidgetDescription>();
	private static HashMap<String,Font> fonts = new HashMap<String,Font>();
	private static HashMap<String,Color> colors = new HashMap<String,Color>();
	private static HashMap<String,String> optionPaneProperties = new HashMap<String,String>();
	private static boolean disableFocus = false;
	
	public static void loadDescriptions(String path) throws IOException {
		URL url = ResourceUtil.getResource(null, path);
		if( url == null )
			throw new IOException( "Could not find file: " + path );
		BufferedInputStream is = new BufferedInputStream(url.openStream());
		Element root = null;
        try {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(is);
            root = doc.getRootElement();
            // -- Read Disable Focus
            String s = root.getChildText("DisableFocus");
            if( s != null )
            	s = s.trim().toLowerCase();
            disableFocus = "true".equals( s );
            // -- Read Colors
            Element ce = root.getChild( "Colors" );
            if( ce != null ) {
	            List<?> elts = ce.getChildren( "Color" );
	            for( Object elt : elts ) {
	            	Element e = (Element) elt;
	            	Color c = Color.decode(e.getText());
	            	String alpha = e.getAttributeValue("alpha");
	            	try {
		            	if( alpha != null ) {
		            		double a = Double.parseDouble(alpha);
		            		if( a > 1 || a < 0 )
		            			throw new IOException( "Alpha Values must be between 1 and zero." );
		            		c = new Color( c.getRed(), c.getGreen(), c.getBlue(), (int) (a*255) );
		            	}
	            	} catch (NumberFormatException nfe) {
	            		throw new IOException( "Alpha value not formatted correctly." );
	            	}
	            	colors.put(e.getAttributeValue("name"), c);
	            }
            }

			// -- Read Fonts
			ce = root.getChild( "Fonts" );
			if( ce != null ) {
				List<?> elts = ce.getChildren( "CustomFont" );
				for( Object elt : elts ) {
					Element e = (Element) elt;
					String fontName = e.getAttributeValue("name");
					String fontPath = e.getValue();
	
					if (fontName == null) {
						throw new IOException("Font requires name attribute.");
					}
					
					if( fontName.indexOf(" ") != -1 )
						throw new IOException("Custom font names cannot contain spaces.");
					
					if( fontName.indexOf("-") != -1 )
						throw new IOException("Custom font names cannot contain dashes.");
	
					if (fontPath == null) {
						throw new IOException("Font requires path value.");
					}

					BufferedInputStream fontFileInputStream = new BufferedInputStream(ResourceUtil.getResourceAsStream(null, path, fontPath));
	
					Font trueTypeFont;
	
					try {
						trueTypeFont =
								Font.createFont(Font.TRUETYPE_FONT, fontFileInputStream);
					}
					catch (FontFormatException e1) {
						throw new IOException("Could not read the custom font " + fontName + "." );
					}

					fonts.put( fontName, trueTypeFont );
				}
				Element d = ce.getChild("DefaultFont");
				if( d != null ) {
					Font f = decodeFont( d.getValue() );
		            Hashtable<Object,Object> defaults = UIManager.getDefaults();
		            Enumeration<?> keys = defaults.keys();
		            while (keys.hasMoreElements()) {
		                Object key = keys.nextElement();
		                if ((key instanceof String) && (((String) key).endsWith(".font"))) {
		                    defaults.put(key, f);
		                }
		            }
				}
			}
			List<?> elts;
			// -- option pane:
			Element child = root.getChild("OptionPane");
			if( child != null ) {
				elts = child.getChildren("property");
				for( Object elt : elts ) {
	            	Element e = (Element) elt;
	            	String key = e.getAttributeValue("key");
	            	if( key == null )
	            		throw new IOException("OptionPane property is missing key");
	            	optionPaneProperties.put(key, e.getText());
	            }
			}
			
            // -- class/id relationships:
            elts = root.getChildren( "Class" );
            for( Object elt : elts ) {
            	Element e = (Element) elt;
            	String clazz = e.getAttributeValue("name");
            	if( clazz == null )
            		throw new IOException("Class is missing name");
            	List<?> ids = e.getChildren("id");
            	for( Object id : ids ) {
            		Element i = (Element) id;
            		idToClass.put(i.getText(), clazz);
            	}
            }
            // -- Read Widgets
            if( !root.getName().equals( "Widgets" ) )
            	throw new IOException("The widget description is corrupt: root element must be Widgets." );
            elts = root.getChildren( "Widget" );
            for( Object elt : elts ) {
            	Element e = (Element) elt;
            	String clazz = e.getAttributeValue("class");
            	String id    = e.getAttributeValue("id");
            	if( clazz == null && id == null )
            		throw new IOException( "Widget element must have either a class or id attribute." );
            	if( clazz != null && id != null )
            		throw new IOException( "Widget element may not have both id and class attribute." );
            	if( id != null )
            		clazz = id;
            	if( descriptions.containsKey( clazz ) )
            		throw new IOException( "Widget already defined with that id/class: " + clazz + "(from " + path + " prior widget from: " + descriptions.get(clazz).getContext() + ")" );
            	descriptions.put( clazz, new WidgetDescription(clazz, clazz,colors,e,path) );
            }
        } catch (JDOMException jde) {
            throw new IOException( "The widget description corrupt: " + jde.getMessage() );
        } finally {
            if( is != null )
                is.close();
        }
	}
	public static final boolean allowFocus() {
		return !disableFocus;
	}
	
	public static String getOptionPaneProperty( String key ) {
		return optionPaneProperties.get( key );
	}
	public static void registerAndSetup(SJWidget w, String id) {
		String clazz = idToClass.get(id);
		if( id != null && descriptions.get(id) == null )
			throw new RuntimeException( "Class ID not found: " + id );
		JComponent jc = (JComponent) w;

		if( !(jc instanceof JTextComponent) ) {
			if( disableFocus )
				jc.setFocusable(false);
		}
		jc.putClientProperty( SJWidget.CLASS_KEY, clazz);
		jc.putClientProperty( SJWidget.ID_KEY, id );
		WidgetDescription wdcz = getWidgetDescription( clazz );
		wdcz.setup( jc );
		WidgetDescription wdid = getWidgetDescription( id );
		wdid.setup( jc );
		if( wdid.registerHelp() && wdcz.registerHelp() )
			HelpComponentRegistry.registerComponent(jc, id);
	}
	
	public static void registerAndSetup(SJDialog w, String id) {
		String clazz = idToClass.get(id);
		if( id != null && descriptions.get(id) == null )
			throw new RuntimeException( "Class ID not found: " + id );
		w.getRootPane().putClientProperty( SJWidget.CLASS_KEY, clazz);
		w.getRootPane().putClientProperty( SJWidget.ID_KEY, id );
		getWidgetDescription( clazz ).setup( w );
		getWidgetDescription( id ).setup( w );
	}

	private static final WidgetDescription defaultWidgetDescription = new WidgetDescription("Default");

	public static WidgetDescription getWidgetDescription(String desc) {
		WidgetDescription ret = descriptions.get(desc);
		if( ret == null )
			return defaultWidgetDescription;
		return ret;
	}

	public static String getWidgetClass(String id) {
		return (String) idToClass.get(id);
	}

	public static List<ComponentInfo> getLayoutElements( SJWidget w ) {
		WidgetDescription desc = getWidgetDescription(w.getWidgetID());
		return desc.createComponentList();
	}

	public static Font decodeFont(String text) {
		String s[] = text.split("[ -]+");
		if( s.length == 2 ) {
			return fonts.get(s[0]).deriveFont(Float.parseFloat(s[1]));
		}
		return Font.decode( text );
	}
}
