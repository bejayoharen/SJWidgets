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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.jdom.Content;
import org.jdom.Element;

import com.xowave.sjwidget.BackgroundPainter.ImageStyle;
import com.xowave.util.XMLUtil;

import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

public class WidgetDescription {
	private final Color TRANSPARENT = new Color(0,0,0,0);
	private final String name;
	private final boolean help;
	private final PropertiesWeCareAbout pwca;
	private final float alignmentX, alignmentY;
	private final List<ComponentInfo> components = new ArrayList<ComponentInfo>();
	private final Dimension min, max, pref;
	final Dimension size;
	final Point pos;
	final JScrollPane scrollPane;
	
	WidgetDescription(String name) {
		this.name = name;
		this.help = true;
		this.pwca = new PropertiesWeCareAbout();
		this.alignmentX = -1;
		this.alignmentY = -1;
		this.size = null;
		this.pos = null;
		this.min  = null;
		this.max  = null;
		this.pref = null;
		this.scrollPane = null;
	}

	public WidgetDescription(String mainid, String name, HashMap<String, Color> colors, Element e, String context) throws IOException {
		if( !e.getName().equals("Widget") )
			throw new RuntimeException("Widget expected");
		this.name = name;
		this.help = e.getChild("nohelp") == null ;
		pwca = new PropertiesWeCareAbout(mainid, colors, context, e);
		scrollPane = getScrollBar(e);

		try {
			size = parseSize(e.getChild("Size"));
			pos = parsePosition( e.getChild("Position") );
			max = parseSize(e.getChild("Maximum"));
			min = parseSize(e.getChild("Minimum") );
			pref = parseSize( e.getChild("Preferred") );
			Element align = e.getChild("Alignment");
			if( align != null ) {
				String s = align.getAttributeValue("x");
				if( s != null )
					alignmentX = Float.parseFloat(s);
				else
					alignmentX = -1;
				s = align.getAttributeValue("y");
				if( s != null )
					alignmentY = Float.parseFloat(s);
				else
					alignmentY = -1;
			} else {
				alignmentX = alignmentY = -1;
			}
		} catch( NumberFormatException nfe ) {
			throw new IOException( "Badly formatted number in " + name );
		}
	}

	/**
	 * @param e
	 */
	private JScrollPane getScrollBar(Element e) throws IOException {
		Element scrollPaneElt = e.getChild("ScrollPane");
		if( scrollPaneElt != null ) {
			int horizontal = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
			int vertical   = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
			String s = scrollPaneElt.getAttributeValue("horizontal");
			if( s != null ) {
				if( s.equals("always") )
					horizontal = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS;
				else if( s.equals("never") )
					horizontal = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
				else if( s.equals("asNeeded") )
					horizontal = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
				else throw new IOException( "unknown horizontal attribute value: " + s );
			}
			s = scrollPaneElt.getAttributeValue("vertical");
			if( s != null ) {
				if( s.equals("always") )
					vertical = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;
				else if( s.equals("never") )
					vertical = JScrollPane.VERTICAL_SCROLLBAR_NEVER;
				else if( s.equals("asNeeded") )
					vertical = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
				else throw new IOException( "unknown vertical attribute value: " + s );
			}
			JScrollPane jsp = new JScrollPane(vertical,horizontal);
			jsp.setBorder(null);
			return jsp;
		} else {
			return null;
		}
	}

	private Color decodeColor(HashMap<String, Color> colors, String text) throws IOException {
		Color background;
		if( text.startsWith("#") ) {
			background = Color.decode( text );
		} else {
			background = colors.get(text);
			if( background == null )
				throw new IOException( "Color not found: " + text );
		}
		return background;
	}
	private int decodeHorizontalAlignment(String text) throws IOException {
		if( text == null )
			throw new IOException( "Missing Horizontal Alignment." );
		if( text.equals("leading") ) {
			return SwingConstants.LEADING;
		} else if( text.equals("left") ) {
			return SwingConstants.LEFT;
		} else if( text.equals("right") ) {
			return SwingConstants.RIGHT;
		} else if( text.equals("trailing") ) {
			return SwingConstants.TRAILING;
		} else if( text.equals("center") ) {
			return SwingConstants.CENTER;
		}
		throw new IOException( "Unknown Horizontal Alignment: " + text );
	}
	
	private int decodeVerticalAlignment(String text) throws IOException {
		if( text == null )
			throw new IOException( "Missing Vertical Alignment." );
		if( text.equals("top") ) {
			return SwingConstants.TOP;
		} else if( text.equals("bottom") ) {
			return SwingConstants.BOTTOM;
		} else if( text.equals("center") ) {
			return SwingConstants.CENTER;
		}
		throw new IOException( "Unknown Vertical Alignment: " + text );
	}

	private Dimension parseSize(Element child) throws NumberFormatException {
		if( child == null )
			return null;
		int w = p( child.getAttributeValue("w") );
		int h = p( child.getAttributeValue("h") );
		if( w < 0 || h < 0 )
			throw new NumberFormatException( "Invalid negative or missing width or height value." );
		return new Dimension( w, h );
	}
	private Point parsePosition(Element child) throws NumberFormatException {
		if( child == null )
			return null;
		int x = p( child.getAttributeValue("x") );
		int y = p( child.getAttributeValue("y") );
//		if( x < 0 || y < 0 )
//			throw new NumberFormatException( "Invalid negative or missing x or y value." );
		return new Point(x,y);
	}

	private int p( String val ) throws NumberFormatException {
		try {
			if( val == null )
				return -1;
			if( val.equals("stretch") )
				return Integer.MAX_VALUE;
			return Integer.parseInt(val);
		} catch( NumberFormatException nfe ) {
			throw new NumberFormatException( "Invalid Number (" + val + "), or perhaps you wanted \"stretch\"." );
		}
	}
	public void setup(JComponent component) {
		pwca.setup(component);
		if( min != null )
			component.setMinimumSize(min);
		if( max != null )
			component.setMaximumSize(max);
		if( pref != null )
			component.setPreferredSize(pref);
		if( size != null )
			component.setSize(size);
		if( pos != null ) {
			if( size == null)
				component.setBounds( pos.x, pos.y, component.getPreferredSize().width, component.getPreferredSize().height );
			else
				component.setBounds( pos.x, pos.y, size.width, size.height);
		}
		if( alignmentX != -1 )
			component.setAlignmentX( alignmentX );
		if( alignmentY != -1 )
			component.setAlignmentY( alignmentY );
	}
	public void setup( SJDialog dialog ) {
		pwca.setup(dialog);
		if( min != null )
			dialog.setMinimumSize(min);
		if( max != null )
			dialog.setMaximumSize(max);
		if( pref != null )
			dialog.setPreferredSize(pref);
		if( size != null )
			dialog.setSize(size);
		if( pos != null ) {
			if( size == null)
				dialog.setBounds( pos.x, pos.y, dialog.getPreferredSize().width, dialog.getPreferredSize().height );
			else
				dialog.setBounds( pos.x, pos.y, size.width, size.height);
		}
//		if( alignmentX != -1 )
//			throw new RuntimeException( "Can't set alignmentX on windows." );
//		if( alignmentY != -1 )
//			throw new RuntimeException( "Can't set alignmentY on windows." );
	}
	public List<ComponentInfo> createComponentList() {
		List<ComponentInfo> ret = new ArrayList<ComponentInfo>( components.size() );
		for( ComponentInfo ci : components )
			ret.add( ci );
		return ret;
	}
	public String getContext() {
		return pwca.context;
	}
	@Override
	public String toString() {
		return "WidgetDescription: " + name ;
	}
	class PropertiesWeCareAbout {
		private final String ID;
		private final Color background;
		private final Color foreground;
		private final String text;
		private final String type; //FIXME: currently ignored. you can't set content type on most swing components.
		private final ImageIcon icon;
		private final ImageIcon selectedIcon;
		private final ImageIcon pressedIcon;
		private final ImageIcon disabledIcon;
		private final ImageIcon disabledSelectedIcon;
		private final Font font;
		private final int horizontalAlignment;
		private final int horizontalTextPosition;
		private final int verticalTextPosition;
		private final BackgroundPainter backgroundPainter;
		private final Border border;
		private final URL url;
		private final String context;

		private PropertiesWeCareAbout(String id, HashMap<String, Color> colors, String context, Element element) throws IOException {
			this.context = context;
			try {
				List<?> elts = element.getChildren("property");
				Color background = null;
				Color foreground = null;
				String text = null;
				String type = null;
				ImageIcon backgroundIcon = null;
				ImageIcon icon = null;
				ImageIcon selectedIcon = null;
				ImageIcon disabledIcon = null;
				ImageIcon disabledSelectedIcon = null;
				ImageIcon pressedIcon = null;
				
				ImageStyle backgroundImageStyle = BackgroundPainter.ImageStyle.TILE;
				Font font = null;
				int horizontalAlignment = -1;
				int horizontalTextPosition = -1;
				int verticalTextPosition = -1;
				URL url = null;
				Color gtop = null, gbot = null, gleft = null, gright = null;
				int gcombo = 0;
				String borderType = null;
				Color borderColor = TRANSPARENT;
				int[] borderWidths = null;
				AlphaComposite composite = null;
				for( Object elt : elts ) {
					Element e = (Element) elt;
					String key = e.getAttributeValue("key");
					if( key == null )
						throw new IOException( "Property is missing key." );
					if( key.equals("foreground") ) {
						foreground = decodeColor(colors, e.getText());
					} else if( key.equals("background") ) {
						background = decodeColor(colors, e.getText());
					} else if( key.equals("type") ) {
						type = e.getText();
					} else if( key.equals("text") ) {
						String s = "";
						@SuppressWarnings("unchecked")
						List<Content> cc = e.getContent();
						for( Content c : cc ) {
							if( c instanceof Element ) {
								Element cloned = (Element) c.clone();
								s += XMLUtil.getCompactStringForXML(cloned);
							} else if( c instanceof org.jdom.Text ) {
								s += ((org.jdom.Text)c).getText();
							}
						}
						text = s;
//					} else if( key.equals("backgroundIcon") ) {
//						backgroundIcon = ResourceUtil.getImageIcon(context, e.getText());
					} else if( key.equals("icon") ) {
						icon = ResourceUtil.getImageIcon(this, context, e.getText());
					} else if( key.equals("selected icon") ) {
						selectedIcon = ResourceUtil.getImageIcon(this, context, e.getText());
					} else if( key.equals("disabled icon") ) {
						disabledIcon = ResourceUtil.getImageIcon(this, context, e.getText());
					} else if( key.equals("disabled selected icon") ) {
						disabledSelectedIcon = ResourceUtil.getImageIcon(this, context, e.getText());
					} else if( key.equals("pressed icon") ) {
						pressedIcon = ResourceUtil.getImageIcon(this, context, e.getText());
					} else if( key.equals("gradient top") ) {
						gtop   = decodeColor(colors, e.getText());
						gcombo |= 0x01;
					} else if( key.equals("gradient bottom") ) {
						gbot   = decodeColor(colors, e.getText());
						gcombo |= 0x02;
					} else if( key.equals("gradient left") ) {
						gleft  = decodeColor(colors, e.getText());
						gcombo |= 0x04;
					} else if( key.equals("gradient right") ) {
						gright = decodeColor(colors, e.getText());
						gcombo |= 0x08;
					} else if( key.equals("background composite") ) {
						String s[] = e.getText().split(" |,");
						if( s.length != 2 )
							throw new IOException( "background composite must contain two elements (type,alpha): " + e.getText() );
						float f = Float.parseFloat(s[1]);
						if( s[0].equals("CLEAR") )
							composite = AlphaComposite.getInstance( AlphaComposite.CLEAR, f );
						else if( s[0].equals("SRC") )
							composite = AlphaComposite.getInstance( AlphaComposite.SRC, f );
						else if( s[0].equals("DST") )
							composite = AlphaComposite.getInstance( AlphaComposite.DST, f );
						else if( s[0].equals("SRC_OVER") )
							composite = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, f );
						else if( s[0].equals("DST_OVER") )
							composite = AlphaComposite.getInstance( AlphaComposite.DST_OVER, f );
						else if( s[0].equals("SRC_IN") )
							composite = AlphaComposite.getInstance( AlphaComposite.SRC_IN, f );
						else if( s[0].equals("DST_IN") )
							composite = AlphaComposite.getInstance( AlphaComposite.DST_IN, f );
						else if( s[0].equals("SRC_OUT") )
							composite = AlphaComposite.getInstance( AlphaComposite.SRC_OUT, f );
						else if( s[0].equals("DST_OUT") )
							composite = AlphaComposite.getInstance( AlphaComposite.DST_OUT, f );
						else if( s[0].equals("SRC_ATOP") )
							composite = AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, f );
						else if( s[0].equals("DST_ATOP") )
							composite = AlphaComposite.getInstance( AlphaComposite.DST_ATOP, f );
						else if( s[0].equals("XOR") )
							composite = AlphaComposite.getInstance( AlphaComposite.XOR, f );
						else
							throw new IOException( "Unknown composite type: " + s[0] );
					} else if( key.equals("border type") ) {
						borderType = e.getText();
					} else if( key.equals("border color") ) {
						borderColor = decodeColor(colors, e.getText());
					} else if( key.equals("border size") ) {
						borderWidths = new int[4];
						String s[] = e.getText().split("[ ,]+");
						// mimic border sizes order from css:
						if( s.length == 1 ) {
							borderWidths[0] = Integer.parseInt( s[0] );
							borderWidths[1] = borderWidths[0];
							borderWidths[2] = borderWidths[0];
							borderWidths[3] = borderWidths[0];
						} else if( s.length == 2 ) {
							borderWidths[0] = Integer.parseInt( s[0] );
							borderWidths[1] = Integer.parseInt( s[1] );
							borderWidths[2] = borderWidths[0];
							borderWidths[3] = borderWidths[1];
						} else if( s.length == 3 ) {
							// top right bottom left=right
							borderWidths[0] = Integer.parseInt( s[0] );
							borderWidths[1] = Integer.parseInt( s[1] );
							borderWidths[2] = Integer.parseInt( s[2] );
							borderWidths[3] = borderWidths[1];
						} else if( s.length == 4 ) {
							// top right bottom left
							borderWidths[0] = Integer.parseInt( s[0] );
							borderWidths[1] = Integer.parseInt( s[3] );
							borderWidths[2] = Integer.parseInt( s[2] );
							borderWidths[3] = Integer.parseInt( s[1] );
						} else {
							throw new IOException( "need 1, 2, 3, or 4 sizes for border. You specified: " + s.length );
						}
					} else if( key.equals("background image") ) {
						backgroundIcon = ResourceUtil.getImageIcon(this, context, e.getText());
					} else if( key.equals("background image style") ) {
						backgroundImageStyle = BackgroundPainter.ImageStyle.valueOf(e.getText().toUpperCase());
					} else if( key.equals("font") ) {
						font = WidgetUtil.decodeFont( e.getText() );
					} else if( key.equals("horizontal alignment") ) {
						horizontalAlignment = decodeHorizontalAlignment( e.getText() );
					} else if( key.equals("horizontal text position") ) {
						horizontalTextPosition = decodeHorizontalAlignment( e.getText() );
					} else if( key.equals("vertical text position") ) {
						verticalTextPosition = decodeVerticalAlignment( e.getText() );
					} else if( key.equals("url") ) {
						url = new URL( e.getText() );
					} else {
						throw new IOException( "Unknown widget property: " + key );
					}
				}
				Border border;
				if( borderType == null ) {
					if( borderWidths == null ) {
						border = null;
					} else {
						throw new IOException( "Border type not specified in " + id );
					}
				} else {
					if( borderType.equals( "none" ) ) {
						border = BorderFactory.createEmptyBorder();
					} else if( borderWidths == null ) {
						throw new IOException( "Border widths not specified in " + id );
					} else if( borderType.equals("empty") ) {
						border = BorderFactory.createEmptyBorder(borderWidths[0], borderWidths[1], borderWidths[2], borderWidths[3]);
					} else if( borderType.equals("tansparent") ) {
						border = BorderFactory.createMatteBorder(borderWidths[0], borderWidths[1], borderWidths[2], borderWidths[3], TRANSPARENT);
					} else if( borderType.equals("solid") ) {
						if( borderColor == null )
							throw new IOException( "Border color required for solid borders " + id );
						border = BorderFactory.createMatteBorder(borderWidths[0], borderWidths[1], borderWidths[2], borderWidths[3], borderColor);
					} else {
						throw new IOException( "Unkown border type in " + id );
					}
				}

				BackgroundPainter bp;
				if( gcombo == 3 ) {
					bp = new BackgroundPainter( SwingConstants.VERTICAL, gtop, gbot, background, backgroundIcon, backgroundImageStyle, composite );
				} else if( gcombo == 12 ) {
					bp = new BackgroundPainter( SwingConstants.HORIZONTAL, gleft, gright, background, backgroundIcon, backgroundImageStyle, composite );
				} else if( gcombo == 0 ) {
					if( backgroundIcon != null )
						bp = new BackgroundPainter( SwingConstants.HORIZONTAL, gleft, gright, background, backgroundIcon, backgroundImageStyle, composite );
					else
						bp = null;
				} else {
					throw new IOException( "Unknown gradient/background combination." );
				}
				{ // -- now we set our finals
					this.ID = id;
					this.background = background;
					this.foreground = foreground;
					this.text = text;
					this.type = type;
					this.icon = icon;
					this.selectedIcon = selectedIcon;
					this.pressedIcon  = pressedIcon;
					this.disabledIcon = disabledIcon;
					this.disabledSelectedIcon = disabledSelectedIcon;
					this.font = font;
					this.horizontalAlignment = horizontalAlignment;
					this.horizontalTextPosition = horizontalTextPosition;
					this.verticalTextPosition = verticalTextPosition;
					this.backgroundPainter = bp;
					this.border = border;
					this.url = url;
				}
			} catch( NumberFormatException nfe ) {
				throw new IOException( "Could not parse number or color in " + id + ": " + nfe.getMessage() );
			}
			
		}
		public PropertiesWeCareAbout() {
			this.ID = null;
			this.background = null;
			this.foreground = null;
			this.text = null;
			this.type = null;
			this.icon = null;
			this.selectedIcon = null;
			this.pressedIcon = null;
			this.disabledIcon = null;
			this.disabledSelectedIcon = null;
			this.font = null;
			this.horizontalAlignment = -1;
			this.horizontalTextPosition = -1;
			this.verticalTextPosition = -1;
			this.backgroundPainter = null;
			this.border = null;
			this.url = null;
			this.context = null;
		}
		/**
		 * @param component
		 */
		private void setup(JComponent component) {
			if( foreground != null )
				component.setForeground(foreground);
			if( background != null )
				component.setBackground(background);
			if( text != null ) {
				if( component instanceof SJWidget )
					((SJWidget) component).setWidgetText(text);

				if( component instanceof JLabel )
					((JLabel) component).setText(text);
				else if( component instanceof AbstractButton )
					((AbstractButton) component).setText(text);
				else if( component instanceof JTextField)
					((JTextField) component).setText(text);
				else
					throw new RuntimeException( "Can't set text on this type of component: " + component.getClass() );
			}
			if( icon != null ) {
//				if (component instanceof ILabelWithBackgroundImage) //FIXME: reimplement
//					((ILabelWithBackgroundImage) component).setIcon(icon);
				if( component instanceof JLabel )
					((JLabel) component).setIcon(icon);
				else if( component instanceof AbstractButton )
					((AbstractButton) component).setIcon(icon);
//				else if( component instanceof ISlider ) //FIXME: reimplement
//					((ISlider) component).setIcon(icon);
//				else if (component instanceof IFormattedTextFieldWithBackgroundImage) //FIXME: reimplement
//					((IFormattedTextFieldWithBackgroundImage) component).setIcon(icon);
				else
					throw new RuntimeException( "Can't set icon on this type of component: " + ID + " : " + component.getClass() );
			}
			if( selectedIcon != null ) {
				if( component instanceof AbstractButton )
					((AbstractButton)component).setSelectedIcon(selectedIcon);
			}
			if( pressedIcon != null ) {
				if( component instanceof AbstractButton )
					((AbstractButton) component).setPressedIcon(pressedIcon);
			}
			if( disabledIcon != null ) {
				if( component instanceof AbstractButton )
					((AbstractButton) component).setDisabledIcon(disabledIcon);
			}
			if( disabledSelectedIcon != null ) {
				if( component instanceof AbstractButton )
					((AbstractButton) component).setDisabledSelectedIcon(disabledSelectedIcon);
			}
			if( font != null )
				component.setFont(font);
			if( horizontalAlignment != -1 ) {
				if( component instanceof JTextField )
					((JTextField)component).setHorizontalAlignment( horizontalAlignment );
				else if( component instanceof JLabel )
					((JLabel)component).setHorizontalAlignment( horizontalAlignment );
				else if( component instanceof AbstractButton )
					((AbstractButton)component).setHorizontalAlignment( horizontalAlignment );
				else
					throw new RuntimeException( "Can't set horizontal alignment on this type of component: " + component.getClass() );
			}
			if( horizontalTextPosition != -1 ) {
				if( component instanceof AbstractButton )
					((AbstractButton)component).setHorizontalTextPosition( horizontalTextPosition );
				if( component instanceof JLabel )
					((JLabel)component).setHorizontalTextPosition( horizontalTextPosition );
			}
			if( verticalTextPosition != -1 ) {
				if( component instanceof AbstractButton )
					((AbstractButton)component).setVerticalTextPosition( verticalTextPosition );
				if( component instanceof JLabel )
					((JLabel)component).setVerticalTextPosition( verticalTextPosition );
			}
			if( component instanceof SJWidget ) {
				if( backgroundPainter != null )
					((SJWidget)component).setBackgroundPainter(backgroundPainter);
			}
			if( border != null )
				component.setBorder(border);
			if( url != null ) {
				if( component instanceof AbstractButton )
					((AbstractButton) component).addActionListener( new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							edu.stanford.ejalbert.BrowserLauncher launcher;
							try {
								launcher = new edu.stanford.ejalbert.BrowserLauncher();
								launcher.openURLinBrowser(url.toExternalForm());
							} catch (BrowserLaunchingInitializingException e) {
								SJOptionPane.showMessageDialog(null, "Could not show URL:\n"+url.toExternalForm() + "\n" + e.getMessage(), "Error Opening Browser", SJOptionPane.ERROR_MESSAGE);
							} catch (UnsupportedOperatingSystemException e) {
								throw new RuntimeException();
							}
						}
					} );
			}
		}
		private void setup(SJDialog dialog) {
			if( foreground != null )
				dialog.getContentPane().setForeground(foreground);
			if( background != null )
				dialog.getContentPane().setBackground(background);
			if( text != null ) {
				dialog.setTitle(text);
			}
			if( icon != null ) {
				throw new RuntimeException( "Must set window icon in code. :(" );
			}
			if( font != null )
				dialog.setFont(font);
			if( horizontalAlignment != -1 ) {
				throw new RuntimeException( "Can't set horizontal Alignment on dialog." );
			}
		}
	}
//	public void postLayout(Container c) {
//		layout.postLayout(c, rows, cols);
//	}
	public boolean registerHelp() {
		return help;
	}
}
