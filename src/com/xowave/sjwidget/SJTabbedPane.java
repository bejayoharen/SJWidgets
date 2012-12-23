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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * @author bjorn
 *
 */
public class SJTabbedPane extends SJPanel {
	private final int tabLocation;
	private final CardLayout cardLayout;
	private final SJPanel mainPanel;
	private SJPanel tabPanel;
	private String current;
	private final String tabID;
	private final Hashtable<String,Component> tabButtons = new Hashtable<String,Component>();
	private final Hashtable<String,Component> tabs = new Hashtable<String,Component>();
	private final EventListenerList ell = new EventListenerList();
	
	public SJTabbedPane(String ID, String tabID) {
		this( ID, tabID, SwingConstants.TOP );
	}
	
	public void addChangeListener( ChangeListener tcl ) {
		ell.add(ChangeListener.class, tcl);
	}
	public void removeChangeListener( ChangeListener tcl ) {
		ell.remove(ChangeListener.class, tcl);
	}
	/**
	 * tabLocation is one of the SwingConstant. TOP, BOTTOM, LEFT or RIGHT.
	 * @param ID
	 * @param tabLocation
	 */
	public SJTabbedPane(String ID, String tabID, int tabLocation) {
		super( new BorderLayout(), ID );
		this.tabID = tabID;
		this.tabLocation = tabLocation;
		mainPanel = new SJPanel(cardLayout = new CardLayout(),null);
		setup();
	}
	/** the same as add tab except that it creates an SJToggleButton using the given ID, which is also used as the identifier. */
	public SJToggleButton addButtonTab( String ID, Component tabView ) {
		SJToggleButton b = new SJToggleButton( ID );
		addTab( b, tabView, ID );
		return b;
	}
	/**
	 * @param tabButton The button that gets put in with the other tabs. If it is an instance of
	 * an AbstractButton, an actionListener will be added to it so that the right tab is shown
	 * when it is clicked. Furthermore,
	 * if it is an instance of AbstractButton, setSelected(true/false) will be called on it
	 * when added and every time the selected tab changes. If it is not an AbstractButton,
	 * a mouseListener will be used and nothing will be done when the tab is shown/hidden.
	 * @param tabView the actual component shown when this tab is selected.
	 * @param identifier the identifier used internally and when calling showTab(). Must be unique within
	 * this SJTabbedPane.
	 * 	 */
	public void addTab( final Component tabButton, Component tabView, final String identifier ) {
		tabPanel.add(tabButton);
		mainPanel.add(tabView,identifier);
		tabButtons.put(identifier,tabButton);
		tabs.put(identifier, tabView);
		
		if( tabButton instanceof AbstractButton ) {
			((AbstractButton) tabButton).addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showTab( identifier );
				}
			} );
		} else {
			tabButton.addMouseListener( new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					showTab( identifier );
				}
			} );
		}
		if( current == null )
			showTab( identifier );
	}
	public Component getVisibleTab() {
		return tabs.get(current);
	}
	public String getVisibleTabID() {
		return current;
	}
	/** adds a component to a tab view without a corresponding tab. useful for spacers, etc. */
	public void addToTabView( Component comp ) {
		tabPanel.add(comp);
	}
	public void showTab( String identifier ) {
		current = identifier;
		cardLayout.show(mainPanel, identifier);
		for( Map.Entry<String,Component> entry : tabButtons.entrySet() ) {
			if( entry.getValue() instanceof AbstractButton )
				((AbstractButton)entry.getValue()).setSelected( identifier.equals(entry.getKey()) );
		}
		fireTabsChangedEvent();
	}
	protected void fireTabsChangedEvent() {
		ChangeEvent ce = new ChangeEvent(this);
		for( ChangeListener tcl : ell.getListeners(ChangeListener.class) ) {
			tcl.stateChanged(ce);
		}
	}
	
	private void setup() {
		removeAll();
		tabPanel = new SJPanel( null, tabID );
		switch( tabLocation ) {
		case SwingConstants.TOP:
			tabPanel.setLayout(new BoxLayout(tabPanel,BoxLayout.X_AXIS));
			add( tabPanel,BorderLayout.NORTH);
			break;
		case SwingConstants.BOTTOM:
			tabPanel.setLayout(new BoxLayout(tabPanel,BoxLayout.X_AXIS));
			add( tabPanel,BorderLayout.SOUTH);
			break;
		case SwingConstants.LEFT:
			tabPanel.setLayout(new BoxLayout(tabPanel,BoxLayout.Y_AXIS));
			add( tabPanel,BorderLayout.WEST);
			break;
		case SwingConstants.RIGHT:
			tabPanel.setLayout(new BoxLayout(tabPanel,BoxLayout.Y_AXIS));
			add( tabPanel,BorderLayout.EAST);
			break;
		}
		//TODO: if this gets called after construction, read-add tabs
		add( mainPanel,BorderLayout.CENTER);
	}
}
