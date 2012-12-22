package com.xowave.sjwidget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import com.xowave.util.Environment;

/**
 * @author bjorn
 *
 */
public class SJMenu extends JMenu implements SJWidget {
	private final SJPopupMenu popupMenu;
	private final String itemID;
	private final SJMenuPaintingDelegate delegate;

	public SJMenu(String name, String labelID, String menuID, String itemID ) {
		super(name);
		setWidgetID( labelID );
		popupMenu = new SJPopupMenu( menuID );
        popupMenu.setInvoker(this);
		this.itemID = itemID;
		delegate = new SJMenuPaintingDelegate(this);
		setIcon( buildIcon() );
	}
	
	private Icon buildIcon() {
		int height = getFontMetrics( getFont() ).getHeight();
		BufferedImage bi = new BufferedImage( height, height, BufferedImage.TYPE_INT_ARGB );
		
		int t = height/2;
		if( (t & 0x01) > 0 )
			++t;
		int h = ( height - t ) / 2 ;
		int s = 0 ; //getInsets().left + getWidth() - RIGHT_PADDING - h - (h+RIGHT_ICON_SPACE) ;
		Graphics2D g = bi.createGraphics();
		Environment.setBeautifulRendering(g);
		g.setColor(getForeground());
		g.fillPolygon(
				new int[] { s, s+t, s },
				new int[] { h, h+t/2, h+t},
				3);
		return new ImageIcon( bi );
	}
	
	@Override
	public void paintComponent(Graphics gg) {
		delegate.paintComponentHelper(gg);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return delegate.getPreferredSize();
	}
	@Override
	public Dimension getMaximumSize() {
		return delegate.getMaximumSize();
	}
	@Override
	public Dimension getMinimumSize() {
		return delegate.getMinimumSize();
	}	
    
    // --- MenuElement implementation --- //

    /**
     * Returns an array of <code>MenuElement</code>s containing the submenu 
     * for this menu component.  If popup menu is <code>null</code> returns
     * an empty array.  This method is required to conform to the
     * <code>MenuElement</code> interface.  Note that since
     * <code>JSeparator</code>s do not conform to the <code>MenuElement</code>
     * interface, this array will only contain <code>JMenuItem</code>s.
     *
     * @return an array of <code>MenuElement</code> objects
     */
    @Override
    public MenuElement[] getSubElements() {
        MenuElement result[] = new MenuElement[1];
        result[0] = popupMenu;
        return result;
    }

    @Override
    public boolean isPopupMenuVisible() {
        return popupMenu.isVisible();
    }

    /**
     * Appends a menu item to the end of this menu. 
     * Returns the menu item added.
     *
     * @param menuItem the <code>JMenuitem</code> to be added
     * @return the <code>JMenuItem</code> added
     */
    @Override
    public JMenuItem add(JMenuItem menuItem) {
        AccessibleContext ac = menuItem.getAccessibleContext();
        ac.setAccessibleParent(this);
        return popupMenu.add(menuItem);
    }

    /**
     * Appends a component to the end of this menu.
     * Returns the component added.
     *
     * @param c the <code>Component</code> to add
     * @return the <code>Component</code> added
     */
    @Override
    public Component add(Component c) {
	 	if (c instanceof JComponent) {	
		    AccessibleContext ac = ((JComponent) c).getAccessibleContext();
		    if (ac != null) {
			ac.setAccessibleParent(this);
		    }
		}
        popupMenu.add(c);
        return c;
    }

    /** 
     * Adds the specified component to this container at the given 
     * position. If <code>index</code> equals -1, the component will
     * be appended to the end.
     * @param     c   the <code>Component</code> to add
     * @param     index    the position at which to insert the component
     * @return    the <code>Component</code> added
     * @see	  #remove
     * @see java.awt.Container#add(Component, int)
     */
    @Override
    public Component add(Component c, int index) {
	 	if (c instanceof JComponent) {	
		    AccessibleContext ac = ((JComponent) c).getAccessibleContext();
		    if (ac != null) {
			ac.setAccessibleParent(this);
		    }
		}
        popupMenu.add(c, index);
        return c;
    }

    /**
     * Creates a new menu item with the specified text and appends
     * it to the end of this menu.
     *  
     * @param s the string for the menu item to be added
     */
    @Override
    public JMenuItem add(String s) {
        return add(new SJMenuItem(s, itemID));
    }

    /**
     * Creates a new menu item attached to the specified 
     * <code>Action</code> object and appends it to the end of this menu.
     * As of 1.3, this is no longer the preferred method for adding 
     * <code>Actions</code> to
     * a container. Instead it is recommended to configure a control with 
     * an action using <code>setAction</code>,
     * and then add that control directly 
     * to the <code>Container</code>.
     *
     * @param a the <code>Action</code> for the menu item to be added
     * @see Action
     */
    @Override
    public JMenuItem add(Action a) {
    	SJMenuItem mi = new SJMenuItem( a, itemID );
        mi.setAction(a);
        add(mi);
        return mi;
    }

    /**
     * Appends a new separator to the end of the menu.
     */
    @Override
    public void addSeparator()
    {
        popupMenu.addSeparator();
    }

    /**
     * Inserts a new menu item with the specified text at a 
     * given position.
     *
     * @param s the text for the menu item to add
     * @param pos an integer specifying the position at which to add the 
     *               new menu item
     * @exception IllegalArgumentException when the value of 
     *			<code>pos</code> < 0
     */
    @Override
    public void insert(String s, int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }

        popupMenu.insert(new JMenuItem(s), pos);
    }

    /**
     * Inserts the specified <code>JMenuitem</code> at a given position.
     *
     * @param mi the <code>JMenuitem</code> to add
     * @param pos an integer specifying the position at which to add the 
     *               new <code>JMenuitem</code>
     * @return the new menu item
     * @exception IllegalArgumentException if the value of 
     *			<code>pos</code> < 0
     */
    @Override
	public JMenuItem insert(JMenuItem mi, int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        AccessibleContext ac = mi.getAccessibleContext();
        ac.setAccessibleParent(this);
        popupMenu.insert(mi, pos);
        return mi;
    }

    /**
     * Inserts a new menu item attached to the specified <code>Action</code> 
     * object at a given position.
     *
     * @param a the <code>Action</code> object for the menu item to add
     * @param pos an integer specifying the position at which to add the 
     *               new menu item
     * @exception IllegalArgumentException if the value of 
     *			<code>pos</code> < 0
     */
    @Override
    public JMenuItem insert(Action a, int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }

        SJMenuItem mi = new SJMenuItem(a, itemID);
        popupMenu.insert(mi, pos);
        return mi;
    }

    /**
     * Inserts a separator at the specified position.
     *
     * @param       index an integer specifying the position at which to 
     *                    insert the menu separator
     * @exception   IllegalArgumentException if the value of 
     *                       <code>index</code> < 0
     */
    @Override
    public void insertSeparator(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        popupMenu.insertSeperator( index );
    }

    /** 
     * Returns the <code>JMenuItem</code> at the specified position.
     * If the component at <code>pos</code> is not a menu item,
     * <code>null</code> is returned.
     * This method is included for AWT compatibility.
     *
     * @param pos    an integer specifying the position
     * @exception   IllegalArgumentException if the value of 
     *                       <code>pos</code> < 0
     * @return  the menu item at the specified position; or <code>null</code>
     *		if the item as the specified position is not a menu item
     */
    @Override
    public JMenuItem getItem(int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }

        Component c = getMenuComponent(pos);
        if (c instanceof JMenuItem) {
            JMenuItem mi = (JMenuItem) c;
            return mi;
        }

        // 4173633
        return null;
    }

    /**
     * Removes the specified menu item from this menu.  If there is no
     * popup menu, this method will have no effect.
     *
     * @param    item the <code>JMenuItem</code> to be removed from the menu
     */
    @Override
    public void remove(JMenuItem item) {
        if (popupMenu != null)
	    popupMenu.remove(item);
    }

    /**
     * Removes the menu item at the specified index from this menu.
     *
     * @param       pos the position of the item to be removed
     * @exception   IllegalArgumentException if the value of 
     *                       <code>pos</code> < 0, or if <code>pos</code>
     *			     is greater than the number of menu items
     */
    @Override
    public void remove(int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        if (pos > getItemCount()) {
            throw new IllegalArgumentException("index greater than the number of items.");
        }
        if (popupMenu != null)
	    popupMenu.remove(pos);
    }

    /**
     * Removes the component <code>c</code> from this menu.
     *
     * @param       c the component to be removed
     */
    @Override
    public void remove(Component c) {
        if (popupMenu != null)
	    popupMenu.remove(c);
    }

    /**
     * Removes all menu items from this menu.
     */
    @Override
    public void removeAll() {
        if (popupMenu != null)
	    popupMenu.removeAll();
    }

    /**
     * Returns the number of components on the menu.
     *
     * @return an integer containing the number of components on the menu
     */
    @Override
    public int getMenuComponentCount() {
        int componentCount = 0;
        if (popupMenu != null)
            componentCount = popupMenu.getComponentCount();
        return componentCount;
    }

    /**
     * Returns the component at position <code>n</code>.
     *
     * @param n the position of the component to be returned
     * @return the component requested, or <code>null</code>
     *			if there is no popup menu
     *
     */
    @Override
    public Component getMenuComponent(int n) {
        if (popupMenu != null)
            return popupMenu.getComponent(n);
        
        return null;
    }

    /**
     * Returns an array of <code>Component</code>s of the menu's
     * subcomponents.  Note that this returns all <code>Component</code>s
     * in the popup menu, including separators.
     *
     * @return an array of <code>Component</code>s or an empty array
     *		if there is no popup menu
     */
    @Override
    public Component[] getMenuComponents() {
        if (popupMenu != null)
            return popupMenu.getComponents();
        
        return new Component[0];
    }

    /**
     * Returns true if the menu is a 'top-level menu', that is, if it is
     * the direct child of a menubar.
     *
     * @return true if the menu is activated from the menu bar;
     *         false if the menu is activated from a menu item
     *         on another menu
     */
    @Override
    public boolean isTopLevelMenu() {
        if (getParent() instanceof JMenuBar)
            return true;
        
        return false;
    }

    /**
     * Returns true if the specified component exists in the 
     * submenu hierarchy.
     *
     * @param c the <code>Component</code> to be tested
     * @return true if the <code>Component</code> exists, false otherwise
     */
    @Override
    public boolean isMenuComponent(Component c) {
        // Are we in the MenuItem part of the menu
        if (c == this)
            return true;
        // Are we in the PopupMenu?
        if (c instanceof JPopupMenu) {
            JPopupMenu comp = (JPopupMenu) c;
            if (comp == this.getPopupMenu())
                return true;
        }
        // Are we in a Component on the PopupMenu
        int ncomponents = this.getMenuComponentCount();
        Component[] component = this.getMenuComponents();
        for (int i = 0 ; i < ncomponents ; i++) {
            Component comp = component[i];
            // Are we in the current component?
            if (comp == c)
                return true;
            // Hmmm, what about Non-menu containers?

            // Recursive call for the Menu case
            if (comp instanceof JMenu) {
                JMenu subMenu = (JMenu) comp;
                if (subMenu.isMenuComponent(c))
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns the popupmenu associated with this menu.
     */
    @Override
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }
    
    // -- SJWidget -- //
	@Override
	public String getWidgetID() {
		setOpaque( false );
		return (String) this.getClientProperty(ID_KEY);
	}
	
	@Override
	public void setBackground( Color bg ) {
		setOpaque( bg.getAlpha() == 255 );
		super.setBackground(bg);
	}
	
	@Override
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

	@Override
	public SJMenu setWidgetID(String ID) {
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}

	@Override
	public void setBackgroundPainter(BackgroundPainter bp) {
		if( bp != null )
			setOpaque(true);
		delegate.setBackgroundPainter(bp);
	}
	
	@Override
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
	@Override
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}
}
