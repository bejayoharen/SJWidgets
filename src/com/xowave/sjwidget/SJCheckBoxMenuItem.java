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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;

import com.xowave.util.UIUtil;
import com.xowave.util.XColor;

public class SJCheckBoxMenuItem extends JCheckBoxMenuItem implements SJWidget {
	/** leave this many pixels on the left for icons like checkmarks and so on. */
	public static int ICON_SPACE = 17 ;
	/** leave this many pixels on between icon and main text */
	public static int ICON_TEXT_PADDING = 4 ;
	/** leave this many pixels on the right after all text */
	public static int RIGHT_PADDING = 10;
	/** padding between text and keyboard shortcut */
	public static int TEXT_PADDING = 20;
	/** maximum icon height */
	public static int ICON_HEIGHT = 12;
	
	int width = -1;
	int keyShortcutWidth = -1;
	String keyShortcutText = null;
	BackgroundPainter backgroundPainter;
	
	
	public SJCheckBoxMenuItem(String name) {
		super(name);
		setBorder( BorderFactory.createEmptyBorder(2,0,2,0) );
		computeAllText();
		setWidgetID(null);
	}
	public SJCheckBoxMenuItem(Action action) {
		super(action);
		setBorder( BorderFactory.createEmptyBorder(2,0,2,0) );
		computeAllText();
		setWidgetID(null);
	}
	public SJCheckBoxMenuItem(String name, String id) {
		super(name);
		setBorder( BorderFactory.createEmptyBorder(2,0,2,0) );
		computeAllText();
		setWidgetID(id);
	}
	@Override
	public void paintComponent(Graphics gg) {
		computeAllText();
		Graphics2D g = (Graphics2D) gg;
		UIUtil.setBeautifulRendering(g);
		g.setColor( this.isArmed() ? Color.BLACK : getBackground() );
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor( isEnabled() ? getForeground() : XColor.blend(getForeground(), getBackground(), .5f));
		float diam = getFont().getSize() ;
		Rectangle2D.Float e = new Rectangle2D.Float( (getHeight()-diam)/2f, (getHeight()-diam)/2f, diam-1, diam-1 );
		g.draw( e );
		if( isSelected() ) {
			diam /= 2 ;
			e.setFrame((getHeight()-diam)/2f, (getHeight()-diam)/2f, diam, diam);
			g.fill( e );
		}
		g.drawString(getText(), getInsets().left + ICON_SPACE + ICON_TEXT_PADDING, getInsets().top + getFontMetrics(getFont()).getAscent());
		if( keyShortcutText != null ) {
			g.setColor( XColor.blend(getForeground(), Color.BLUE, .75f) );
			int w = getFontMetrics(getFont()).stringWidth(keyShortcutText);
			g.drawString(keyShortcutText, getInsets().left + getWidth() - RIGHT_PADDING - w, getInsets().top + getFontMetrics(getFont()).getAscent());
		}

	}
	
	private void computeAllText() {
		if( getAccelerator() != null ) {
			keyShortcutText = new String(new char[] {(char)this.getAccelerator().getKeyCode()});
			int mod = this.getAccelerator().getModifiers();
			if( (mod & KeyEvent.META_DOWN_MASK) > 0 )
				keyShortcutText = '\u2318' + " " + keyShortcutText;
			if( (mod & KeyEvent.CTRL_DOWN_MASK) > 0 )
				keyShortcutText = '^' + " " + keyShortcutText;
			if( (mod & KeyEvent.ALT_DOWN_MASK) > 0 )
				keyShortcutText = '\u2326' + " " + keyShortcutText;
			if( (mod & KeyEvent.SHIFT_DOWN_MASK) > 0 )
				keyShortcutText = '\u8679' + " " + keyShortcutText;
		} else {
			keyShortcutText = null;
		}
		
		keyShortcutWidth = keyShortcutText == null ? 0 : getFontMetrics(getFont()).stringWidth(keyShortcutText) ;
		
		width = ICON_SPACE + ICON_TEXT_PADDING + getFontMetrics(getFont()).stringWidth(getText()) + TEXT_PADDING + keyShortcutWidth + RIGHT_PADDING ;
		width += getInsets().left + getInsets().right ;
	}
	
	@Override
	public Dimension getPreferredSize() {
		computeAllText();
		int height = Math.max( getFontMetrics(getFont()).getHeight(), ICON_HEIGHT ) + getInsets().top + getInsets().bottom;
		return new Dimension( width, height );
	}
	@Override
	public Dimension getMaximumSize() {
		Dimension d = getPreferredSize();
		d.width = Integer.MAX_VALUE;
		return d;
	}
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	public String getWidgetID() {
		setOpaque( false );
		return (String) this.getClientProperty(ID_KEY);
	}
	@Override
	public void setBackground( Color bg ) {
		setOpaque( bg.getAlpha() == 255 );
		super.setBackground(bg);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

	public SJCheckBoxMenuItem setWidgetID(String ID) {
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}

	public void setBackgroundPainter(BackgroundPainter bp) {
		if( bp != null )
			setOpaque(true);
		backgroundPainter = bp;
	}
	
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}
}
