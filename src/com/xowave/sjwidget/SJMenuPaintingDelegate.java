package com.xowave.sjwidget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.InputEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import com.xowave.util.UIUtil;
import com.xowave.util.XColor;

class SJMenuPaintingDelegate {
	/** leave this many pixels on the left for icons like check-marks and so on. */
	public static int LEFT_ICON_SPACE = 10 ;
	/** leave this many pixels on between icon and main text */
	public static int ICON_TEXT_PADDING = 4 ;
	/** leave this many pixels on the right after all text */
	public static int RIGHT_PADDING = 10;
	/** padding between text and keyboard shortcut */
	public static int TEXT_PADDING = 20;
	/** maximum icon height */
	public static int ICON_HEIGHT = 12;
	/** padding between text and right icon */
	public static int RIGHT_ICON_SPACE = 10;
	
	private int width = -1;
	private int keyShortcutWidth = -1;
	private String keyShortcutText = null;
	private final JMenuItem item;

	public SJMenuPaintingDelegate(JMenuItem item) {
		this.item = item;
		item.setBorder( BorderFactory.createEmptyBorder(2,0,2,0) );
		computeAllText();
	}
	public void paintComponentHelper(Graphics gg) {
		Graphics2D g = (Graphics2D) gg;
		Color f = item.getForeground();
		Color b = item.getBackground();
		FontMetrics fm = item.getFontMetrics( item.getFont() );
		Insets insets = item.getInsets();
		final int h = item.getHeight();
		final int w = item.getWidth();

		UIUtil.setBeautifulRendering(g);
		g.setColor( item.isArmed() || item.isSelected() ? Color.BLACK : b );
		g.fillRect(0, 0, w, h);
		g.setColor( item.isEnabled() ? f : XColor.blend(f, b, .5f));
		computeAllText();
		g.drawString(item.getText(), insets.left + LEFT_ICON_SPACE + ICON_TEXT_PADDING, insets.top + fm.getAscent());
		
		//from the right now:
//		if( isMenu ) {
//			int t = getFont().getSize()/2;
//			if( (t & 0x01) > 0 )
//				++t;
//			int h = ( getHeight() - t ) / 2 ;
//			int s = getInsets().left + getWidth() - RIGHT_PADDING - h - (h+RIGHT_ICON_SPACE) ;
//			g.fillPolygon(
//					new int[] { s, s+t, s },
//					new int[] { h, h+t/2, h+t},
//					3);
//		} else {
			Icon icn = item.getIcon();
			int iconSpace = icn == null ? 0 : icn.getIconWidth() + RIGHT_ICON_SPACE ;
			
			if( keyShortcutText != null ) {
				g.setColor( XColor.blend(f, Color.BLUE, .75f) );
				int tw = fm.stringWidth(keyShortcutText);
				g.drawString(keyShortcutText, insets.left + w - RIGHT_PADDING - tw - iconSpace, insets.top + fm.getAscent());
			}
			if( icn != null ) {
				icn.paintIcon(item, g, insets.left + w - RIGHT_PADDING - iconSpace, ( h - icn.getIconHeight() ) / 2 ) ;
			}
//		}
	}
	
	private void computeAllText() {
		Insets insets = item.getInsets();
		
		if( item.getAccelerator() != null ) {
			keyShortcutText = new String(new char[] {(char)item.getAccelerator().getKeyCode()});
			int mod = item.getAccelerator().getModifiers();
			if( (mod & InputEvent.META_DOWN_MASK) > 0 )
				keyShortcutText = '\u2318' + " " + keyShortcutText;
			if( (mod & InputEvent.CTRL_DOWN_MASK) > 0 )
				keyShortcutText = '^' + " " + keyShortcutText;
			if( (mod & InputEvent.ALT_DOWN_MASK) > 0 )
				keyShortcutText = '\u2326' + " " + keyShortcutText;
			if( (mod & InputEvent.SHIFT_DOWN_MASK) > 0 )
				keyShortcutText = '\u21e7' + " " + keyShortcutText;
		} else {
			keyShortcutText = null;
		}
		FontMetrics fm = item.getFontMetrics( item.getFont() );
		keyShortcutWidth = keyShortcutText == null ? 0 : fm.stringWidth(keyShortcutText) ;
		
		width = LEFT_ICON_SPACE + ICON_TEXT_PADDING + fm.stringWidth(item.getText()) + TEXT_PADDING + keyShortcutWidth + RIGHT_PADDING ;
		width += insets.left + insets.right ;
		Icon icn = item.getIcon();
//		if( isMenu )
//			width += getHeight() + RIGHT_ICON_SPACE ;
		if( icn != null )
			width += icn.getIconWidth() + RIGHT_ICON_SPACE;
	}
	
	public Dimension getPreferredSize() {
		Insets insets = item.getInsets();
		computeAllText();
		FontMetrics fm = item.getFontMetrics( item.getFont() );
		int height = Math.max( fm.getHeight(), ICON_HEIGHT ) + insets.top + insets.bottom;
		return new Dimension( width, height );
	}
	public Dimension getMaximumSize() {
		Dimension d = getPreferredSize();
		d.width = Integer.MAX_VALUE;
		return d;
	}
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	public void setBackgroundPainter(BackgroundPainter bp) {
		//No op for now.
	}
}
