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
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.xowave.sjwidget.util.WidgetUtil;

/**
 * 
 * Replaces some of the most common functions in JOptionPane.
 * 
 * There are some issues with dialogs that should also be worked out:
 * 
 * On Mac OS X 10.3, closing dialogs in the wrong order can result in
 * unusable menus: apple bug ID: 3618613 
 * 
 * This could be worked around by elaborately tracking all dialog and 
 * making sure they are opened and closed in the right order.
 * 
 * In X11, Dialogs can be obscured by Frames: sun bug ID 5095181
 * 
 * This could be worked around by making sure that the active dialog is
 * put on top every few seconds.
 * 
 */
public class SJOptionPane extends SJPanel implements ActionListener
{
    /** 
     * Type meaning Look and Feel should not supply any options -- only
     * use the options from the <code>XOOptionPane</code>.
     */
    /** Type used for <code>showConfirmDialog</code>. */
    public static final int         DEFAULT_OPTION = -1;
    /** Type used for <code>showConfirmDialog</code>. */
    public static final int         YES_NO_OPTION = 0;
    /** Type used for <code>showConfirmDialog</code>. */
    public static final int         YES_NO_CANCEL_OPTION = 1;
    /** Type used for <code>showConfirmDialog</code>. */
    public static final int         OK_CANCEL_OPTION = 2;

    //
    // Return values.
    //
    /** Return value from class method if YES is chosen. */
    public static final int         YES_OPTION = 0;
    /** Return value from class method if NO is chosen. */
    public static final int         NO_OPTION = 1;
    /** Return value from class method if CANCEL is chosen. */
    public static final int         CANCEL_OPTION = 2;
    /** Return value form class method if OK is chosen. */
    public static final int         OK_OPTION = 0;
    /** Return value from class method if user closes window without selecting
     * anything, more than likely this should be treated as either a
     * <code>CANCEL_OPTION</code> or <code>NO_OPTION</code>. */
    public static final int         CLOSED_OPTION = -1;

    //
    // Message types. Used by the UI to determine what icon to display,
    // and possibly what behavior to give based on the type.
    //
    /** Used for error messages. */
    public static final int  ERROR_MESSAGE = 0;
    /** Used for information messages. */
    public static final int  INFORMATION_MESSAGE = 1;
    /** Used for warning messages. */
    public static final int  WARNING_MESSAGE = 2;
    /** Used for questions. */
    public static final int  QUESTION_MESSAGE = 3;
    /** simple icon is used */
    public static final int   BASIC_MESSAGE = 4;
    /** No icon is used. */
    public static final int   PLAIN_MESSAGE = -1;
    /** Icons corresponding to messages */
    public static final String ICON_LABEL_IDS[] = new String[] {
    	WidgetUtil.getOptionPaneProperty("error label"),
    	WidgetUtil.getOptionPaneProperty("information label"),
    	WidgetUtil.getOptionPaneProperty("warning label"),
    	WidgetUtil.getOptionPaneProperty("question label"),
    	WidgetUtil.getOptionPaneProperty("basic label"),
    };
    private static boolean restrictedEnvironment = false;

    private int			selectedValue = CLOSED_OPTION;
    private SJButton	defaultButton = null;
    private final boolean		isRetSetByButton ;
    
    /** Setting this to true will set an internal variable which will
     * turn off special handling of global events. This is useful
     * for not loading special libraries in debugging or in restricted
     * environments like the crash report sender, but generally to be
     * avoided.
     */
    public static void setRestrictedEnvironment( boolean resEnv ) {
    	restrictedEnvironment = resEnv;
    }
    public static Icon getIconForId( String Id ) {
    	if( Id == null )
    		return null;
    	return new SJLabel( Id ).getIcon();
    }
    
    public SJOptionPane( Object message, int buttonOptions ) {
    	this( message, getButtonOptions(buttonOptions), getDefaultOption(buttonOptions), null, true );
    }
    public SJOptionPane( Object message, int buttonOptions, int messageType ) {
    	this( message, getButtonOptions(buttonOptions), getDefaultOption(buttonOptions), messageType < 0 ? null : getIconForId( ICON_LABEL_IDS[messageType] ), true );
    }
    public SJOptionPane( Object message, Object[] options, Object defaultOption, int messageType ) {
    	this( message, options, defaultOption, messageType < 0 ? null : getIconForId( ICON_LABEL_IDS[messageType] ), true );
    }
    public SJOptionPane( Object message, Object[] options, Object defaultOption, Icon icon, boolean optionsAreButtons ) {
    	super( new BorderLayout(), WidgetUtil.getOptionPaneProperty("Option Pane") );
    	Component comp = toComponent(message);
    	SJPanel p = SJPanel.createVerticalIPanel(null);
    	p.add( javax.swing.Box.createVerticalGlue() );
    	p.add( comp );

    	Object[] buttons;
    	Object defaultButtonObj;
    	// options are either displayed as buttons, a combo box, or a JList
    	if( optionsAreButtons ) {
    		buttons = options;
    		defaultButtonObj = defaultOption;
    	} else {
    		buttons = getButtonOptions(OK_CANCEL_OPTION);
    		defaultButtonObj = getDefaultOption(OK_CANCEL_OPTION);
    	}
    	if( !optionsAreButtons && options != null && options.length > 10 ) {
    		final JList list = new JList( options );
    		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    		list.addListSelectionListener( new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					selectedValue = list.getSelectedIndex();
				}
    		});
    		list.setSelectedValue(defaultOption, true);
    		list.addMouseListener( new MouseAdapter() {
    			@Override
				public void mouseClicked(MouseEvent e) {
					if( e.getClickCount() == 2 )
						for( ActionListener al : actionListeners.getListeners(ActionListener.class) )
							al.actionPerformed(null);
				}
    		} );
    		SJScrollPane scroller = new SJScrollPane(list,null);
    		if( options.length < 20 )
    			scroller.setPreferredSize( list.getPreferredSize() );
    		p.add( javax.swing.Box.createVerticalGlue() );
    		p.add( scroller );
    		selectedValue = list.getSelectedIndex();
    		isRetSetByButton = false;
    	} else if( !optionsAreButtons && options != null /*&& options.length > 4*/ ) {
    		final SJSelectionBox cb = new SJSelectionBox(null, null, null, options) ;
    		cb.setSelectedItem(defaultOption);
    		cb.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if( e.getStateChange() == ItemEvent.DESELECTED )
						return;
					selectedValue = cb.getSelectedIndex();
				}
    		});
    		p.add( javax.swing.Box.createVerticalGlue() );
    		cb.setAlignmentX(0);
    		selectedValue = cb.getSelectedIndex();
    		p.add( cb );
    		isRetSetByButton = false;
    	} else {
    		isRetSetByButton = true;
    	}
    	p.add( javax.swing.Box.createVerticalGlue() );
    	add( p );
    	//buttons
    	SJPanel bp = new SJPanel(null);
    	add( bp, BorderLayout.SOUTH );
    	for( int i=0; i<buttons.length; ++i ) {
    		if( buttons[i] == null )
    			continue;
    		SJButton b = new SJButton(buttons[i].toString(), WidgetUtil.getOptionPaneProperty("button"));
    		b.setFocusable( true );
    		b.setActionCommand( Integer.toString(i) );
    		b.addActionListener( this );
    		if( buttons[i].equals(defaultButtonObj) )
    			defaultButton = b;
    		else
    			bp.add( b );
    	}
    	if( defaultButton != null ) {
    		bp.add( defaultButton );
    	}
    	//Now, a nice Icon:
    	SJLabel l;
    	if( icon != null ) {
    		l = new SJLabel( icon, null );
    		l.setBorder( BorderFactory.createEmptyBorder(8,8,8,8) );
    		add( l, BorderLayout.WEST );
    	}
    	setBorder( BorderFactory.createEmptyBorder(4,4,4,4) );
    }
	public void actionPerformed(ActionEvent e) {
		if( isRetSetByButton )
			selectedValue = Integer.parseInt(e.getActionCommand());
		else if( Integer.parseInt(e.getActionCommand()) == CANCEL_OPTION )
			selectedValue = -1;
		for( ActionListener al : actionListeners.getListeners(ActionListener.class) )
			al.actionPerformed(e);
	}
	public int getValue() {
		return selectedValue;
	}
	public SJButton getDefaultButton() {
		return defaultButton;
	}
	private EventListenerList actionListeners = new EventListenerList();
	public void addActionListener( ActionListener al ) {
		actionListeners.add( ActionListener.class, al );
	}
	public void removeActionListener( ActionListener al ) {
		actionListeners.remove( ActionListener.class, al );
	}
    private Component toComponent( Object obj ) {
    	Component ret;
    	if( obj instanceof Object[] ) {
    		ret = SJPanel.createVerticalIPanel(null);
    		Object[] objs = (Object[]) obj;
    		for( int i=0; i<objs.length; ++i ) {
    			((SJPanel)ret).add( toComponent(objs[i]) );
    		}
    	} else if( obj instanceof String ) {
    		String str = (String) obj;
    		String strs[] = str.split("\n");
    		if( strs.length == 1 ) {
    			String s = (String) obj;
    			SJLabel xol;
    			if( s.length() == 0 ) {
    				xol = new SJLabel(" ", WidgetUtil.getOptionPaneProperty("main text filler"));
    				Dimension d = xol.getPreferredSize();
    				d.height /= 2;
    				xol.setPreferredSize( d );
    			} else {
    				xol = new SJLabel(s, WidgetUtil.getOptionPaneProperty("main text"));
    			}
    			xol.setAlignmentX(0);
    			return xol;
    		}
    		return toComponent( strs );
    	} else if( obj instanceof Icon ) {
    		SJLabel xol = new SJLabel((Icon)obj, WidgetUtil.getOptionPaneProperty("main text icon"));
			xol.setAlignmentX(0);
			return xol;
    	} else if( obj instanceof Component ) {
    		if( obj instanceof JComponent )
    			((JComponent)obj).setAlignmentX(0);
    		return (Component) obj;
    	} else {
    		ret = new SJLabel(obj.toString(), WidgetUtil.getOptionPaneProperty("main text"));
    	}
    	return ret;
    }
    private static String[] getButtonOptions(int buttonOptions) {
    	switch( buttonOptions ) {
    	default:
    	case DEFAULT_OPTION:
    		return new String[] { "Okay" };
    	case YES_NO_OPTION:
    		return new String[] { "Yes", "No" };
    	case YES_NO_CANCEL_OPTION:
    		return new String[] { "Yes", "No", "Cancel" };
    	case OK_CANCEL_OPTION:
    		return new String[] { "Okay", null, "Cancel" };
    	}
    }
    private static String getDefaultOption(int buttonOptions) {
    	switch( buttonOptions ) {
    	default:
    	case DEFAULT_OPTION:
    		return "Okay" ;
    	case YES_NO_OPTION:
    		return "Yes";
    	case YES_NO_CANCEL_OPTION:
    		return "Yes";
    	case OK_CANCEL_OPTION:
    		return "Okay";
    	}
    }
	public static int showConfirmDialog(Component parent, Object message, String title, int buttonOptions ) {
		return showConfirmDialog(parent, message, title, buttonOptions, QUESTION_MESSAGE );
	}
	public static int showConfirmDialog(Component parent, Object message, String title, int buttonOptions, int messageType ) {
		SJOptionPane op = new SJOptionPane( message, buttonOptions, messageType );
		op.createDialog(parent,title).setVisible(true);
		return op.getValue();
	}
	public static String showInputDialog( Component parent, Object message, String title ) {
		JTextField jtf = new JTextField( 35 );
		Object[] message2 = { message, SJPanel.createVerticalFiller(), jtf, SJPanel.createVerticalFiller() };
		SJOptionPane op = new SJOptionPane( message2, DEFAULT_OPTION, QUESTION_MESSAGE );
		op.createDialog(parent,title).setVisible(true);
		int ret = op.getValue();
		if( ret < 0 )
			return null;
		return jtf.getText();
	}
	public static Object showInputDialog( Component parent, Object message, String title, int messageType,Icon icon, Object[] options, Object defaultOption) {
		if( options == null ) {
			JTextField jtf = new JTextField(25);
			jtf.setText( defaultOption.toString() );
			jtf.setMaximumSize(jtf.getPreferredSize());
			message = new Object[] { message, jtf };
			SJOptionPane op = new SJOptionPane(
					message,
					getButtonOptions(OK_CANCEL_OPTION),
					getDefaultOption(OK_CANCEL_OPTION),
					getIcon(icon,messageType),
					true );
			op.createDialog(parent,title).setVisible(true);
			int ret = op.getValue();
			if( ret < 0 )
				return null;
			return jtf.getText();
		} else {
			SJOptionPane op = new SJOptionPane( message, options, defaultOption, getIcon(icon,messageType), false );
			op.createDialog(parent,title).setVisible(true);
			int ret = op.getValue();
			if( ret < 0 )
				return null;
			return options[ret];
		}
	}
	public static int showMessageDialog( Component parent, Object message, String title ) {
		return showMessageDialog( parent, message, title, 0 );
	}
	public static int showMessageDialog( Component parent, Object message, String title, int messageType ) {
		SJOptionPane op = new SJOptionPane( message, DEFAULT_OPTION, messageType );
		op.createDialog(parent,title).setVisible(true);
		return op.getValue();
	}
	/** 
	 * Thread-safe version of showMessageDialog. Shows a message asynchronously with the given parameters.
	 * No parent parameter is taken because the parent may not be visible by the time this is called.
	 * This function returns immediately and queues the display of the message for a future run of the swing
	 * thread using invokeLater.
	 * 
	 * @param message the message to display
	 * @param title the title of the window
	 * @param messageType the message type
	 */
	public static void showMessageDialogLater( final Object message, final String title, final int messageType ) {
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				showMessageDialog( null, message, title, messageType );
			}
		} ) ;
	}
	public static int showOptionDialog(Component parent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object defaultOption) {
		SJOptionPane op = new SJOptionPane( message, options, defaultOption, getIcon(icon,messageType), false );
		op.createDialog(parent,title).setVisible(true);
		return op.getValue();
	}
	public static int showOptionButtonDialog(Component parent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object defaultOption) {
		SJOptionPane op = new SJOptionPane( message,
				options == null ? getButtonOptions(optionType) : options,
				options == null ? getDefaultOption( optionType ) : defaultOption,
				getIcon(icon,messageType),
				true );
		op.createDialog(parent,title).setVisible(true);
		return op.getValue();
	}
	static Icon getIcon( Icon icon, int messageType ) {
		if( icon != null )
			return icon;
		if( messageType >= 0 )
			return getIconForId( ICON_LABEL_IDS[messageType] );
		return null;
	}
	public SJDialog createDialog(Component parent,String title) {
		Component top = parent == null ? null : SwingUtilities.getWindowAncestor(parent);

		SJDialog xodd;
		if( top instanceof Frame )
			xodd = new SJDialog((Frame)top,null);
		else if( top instanceof Dialog )
			xodd = new SJDialog((Dialog) top,null);
		else
			xodd = new SJDialog(null);
		final SJDialog xod = xodd;
		xod.setModal(true);
		xod.addWindowListener( new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				if( defaultButton != null ) {
					defaultButton.requestFocus();
				}
			}
			@Override
			public void windowClosing(WindowEvent e) {
				selectedValue = CLOSED_OPTION;
				xod.setVisible(false);
				xod.dispose();
			}
		});
		xod.setResizable(false);
		xod.setTitle(title);
		xod.getContentPane().add(this);
		this.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				xod.setVisible(false);
				xod.dispose();
			}
		});
		if( this.defaultButton != null ) {
			xod.getRootPane().setDefaultButton(this.defaultButton);
		}
		xod.pack();
		xod.setLocationRelativeTo(parent);
		xod.getContentPane().setFocusable( false ); //reverse hack
		if( !restrictedEnvironment )
			com.xowave.util.GlobalEventManager.suspendWhileVisible( xod );
		return xod;
	}
}
