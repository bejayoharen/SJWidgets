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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

import com.xowave.util.Environment;
import com.xowave.util.XFile;

import java.io.*;
import java.util.Vector;


/**
 * This class contains static functions that allow for opening and saving without having to
 * write custom switches to do different behavior on the mac vs pc. It also works around some
 * bugs.
 * 
 * You may want to use quaqua in addition to this.
 * 
 * @author bjorn
 *
 */
public class SJFileChooser {
	public static final int LOAD = FileDialog.LOAD;
	public static final int SAVE = FileDialog.SAVE;
    public static final int FILES_ONLY = JFileChooser.FILES_ONLY;
    public static final int DIRECTORIES_ONLY = JFileChooser.DIRECTORIES_ONLY;
    public static final int FILES_AND_DIRECTORIES = JFileChooser.FILES_AND_DIRECTORIES;

	static File lastDir = null;

	public static void setDirectory(File dir) {
		lastDir = dir;
	}

	/** Allows the user to select multiple files to open.
	 * 
	 * @param parent the component to use for layout and modality.
	 * @param title the title of the dialog.
	 * @param mff an optional filter.
	 * @return an array of files, or null if they cancelled.
	 */
	public static XFile[] showMultiFileOpenDialog(Component parent, String title, SJFileFilter mff) {
		//use swing dialog as it supports multiFiles and folders
		JFileChooser dialog = new JFileChooser();
		if (mff != null)
			dialog.setFileFilter(mff);
		dialog.setDialogTitle(title);
		if (lastDir != null)
			dialog.setCurrentDirectory(lastDir);
		int res;
		
		dialog.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
		dialog.setApproveButtonText( title );
		dialog.setMultiSelectionEnabled(true);
		final MultiFileAccessory mfa = new MultiFileAccessory(dialog, mff);
		mfa.addComponentListener( new ComponentAdapter() {
			boolean once = false;
			@Override
			public void componentResized(ComponentEvent e) {
				if( once ) return;
				once = true;
				Window w = (Window)mfa.getTopLevelAncestor();
				w.pack();
				int h = (int) (w.getHeight() * 1.5);
				h = Math.min( h, (int) ( w.getGraphicsConfiguration().getBounds().height * .9 ) );
				w.setSize( w.getWidth(), h );
				w.setLocationRelativeTo(null);
				w.validate();
				mfa.requestFocusInWindow(); //seems to make escape work from the get-go
			}
		} );
		dialog.setAccessory( mfa );

		res = dialog.showOpenDialog(parent);

		File fls[] = null;
		
		if (res != JFileChooser.APPROVE_OPTION )
			return null;

		fls = dialog.getSelectedFiles();
		
		if( fls == null || fls.length == 0 )
			return null;
		
		lastDir = dialog.getCurrentDirectory();

		XFile[] ret = deriveFilesFromFoldersAndFilter(mff, fls);

		return ret;
	}

	static XFile[] deriveFilesFromFoldersAndFilter(SJFileFilter mff, File[] fl) {
		if( fl == null )
			return new XFile[0];
		Vector<XFile> fileVector = new Vector<XFile>( fl.length );
		for( int i=0; i<fl.length; ++i ) {
			if( !fl[i].exists() )
				continue;
			if( fl[i].isDirectory() ) {
				XFile[] f = (new XFile(fl[i])).listXFiles( mff );
				if( f != null )
					for( int j=0; j<f.length; ++j ) {
						if( f[j].exists() && !f[j].isDirectory() && !f[j].isHidden() )
							fileVector.add( f[j] );
					}
			} else {
				if( mff.accept(fl[i]) )
					fileVector.add( new XFile(fl[i]) );
			}
		}
		
		XFile[] ret = new XFile[ fileVector.size() ];
		fileVector.copyInto( ret );
		return ret;
	}

	/**
	 * shows a save or open dialog.
	 */
	public static XFile showDialog(int type, Component parent, String title, SJFileFilter fnf ) {
		return showDialog( type, parent, title, fnf, null );
	}
	/**
	 * shows a save or open dialog.
	 */
	public static XFile showDialog(int type, Component parent, String title, SJFileFilter fnf, File initialFile) {
		return showDialog( type, parent, title, fnf, initialFile, null, JFileChooser.FILES_ONLY  );
	}
	/**
	 * shows a save or open dialog with all options.
	 * 
	 * @param type load or save?
	 * @param parent parent component for layout
	 * @param title the title of the dialog
	 * @param fnf optional filter
	 * @param initialFile which file are we initially open on?
	 * @param accessory an accessory component to how in the dialog.
	 * @param selectionMode directories, files or both?
	 * @return the selected file or null if none was selected.
	 */
	public static XFile showDialog(int type, Component parent, String title, SJFileFilter fnf, File initialFile, final JComponent accessory, int selectionMode ) {
		Window w = getParentWindow(parent);

		if (Environment.isAWTFileChooserPreferred() && accessory == null && selectionMode == JFileChooser.FILES_ONLY ) {
			//  ---------- AWT DIALOG
			FileDialog dialog = null;
			if (w instanceof Frame)
				dialog = new FileDialog((Frame) w);
			else if( w instanceof Dialog )
				dialog = new FileDialog((Dialog) w);
			else {
				System.out.println("WARNING: MyFileChooser could not find an appropriate parent window.");
				dialog = new FileDialog((Frame) null);
			}
			
			if (fnf != null)
				dialog.setFilenameFilter(fnf);
			dialog.setTitle(title);
			dialog.setMode(type);
			if (lastDir != null)
				dialog.setDirectory(lastDir.getPath());
			if( initialFile != null ) {
				if( initialFile.isDirectory() ) {
					dialog.setDirectory( initialFile.getPath() );
				} else {
					dialog.setDirectory( initialFile.getParent() );
					dialog.setFile( initialFile.getName() );
				}
			}
			dialog.setVisible(true);
			if (dialog.getFile() != null && dialog.getDirectory() != null) {
				lastDir = new File(dialog.getDirectory());
				dialog.setDirectory(lastDir.getPath()); //this seems to help
														// the mac store the
														// location after
														// quitting
				return new XFile(lastDir, dialog.getFile());
			} else {
				return null;
			}
		} else { // --------- SWING DIALOG
			JFileChooser dialog = new JFileChooser();
			if( selectionMode != JFileChooser.FILES_ONLY )
				dialog.setFileSelectionMode( selectionMode );
			if( accessory != null ) {
				dialog.setAccessory(accessory);
				accessory.addComponentListener( new ComponentAdapter() {
					boolean once = false;
					@Override
					public void componentResized(ComponentEvent e) {
						if( once ) return;
						once = true;
						Window w1 = (Window)accessory.getTopLevelAncestor();
						w1.pack();
						int h = (int) (w1.getHeight() * 1.5);
						h = Math.min( h, (int) ( w1.getGraphicsConfiguration().getBounds().height * .9 ) );
						w1.setSize( w1.getWidth(), h );
						w1.setLocationRelativeTo(null);
						w1.validate();
					}
				} );
			}

			if (fnf != null)
				dialog.setFileFilter(fnf);
			dialog.setDialogTitle(title);
			if (lastDir != null)
				dialog.setCurrentDirectory(lastDir);
			
			if( initialFile != null ) {
				if( initialFile.isDirectory() ) {
					dialog.setCurrentDirectory( initialFile );
				} else {
					dialog.setCurrentDirectory( initialFile.getParentFile() );
					dialog.setSelectedFile( initialFile );
				}
			}
			
			int ret;
			
			dialog.setApproveButtonText( title );
			
			if (type == FileDialog.SAVE)
				ret = dialog.showSaveDialog(parent);
			else
				ret = dialog.showOpenDialog(parent);

			XFile fl = null;
			if (ret == JFileChooser.APPROVE_OPTION) {
				fl = new XFile( dialog.getSelectedFile() );
				lastDir = fl.getParentFile();
			}
			return fl;
		}
	}

	private static Window getParentWindow(Component parent) {
		Window w = null;
		// try and find a reasonable "parent" Frame/Dialog.
		Component p = parent;
		if (p == null) { //just find the first visible window:
			Frame f[] = Frame.getFrames();
			for (int i = 0; i < f.length; ++i)
				if (f[i].isVisible()) {
					p = f[i];
					break;
				}
		}
		if (p == null) {
			System.out.println("MINOR WARNING: MyFileChooser could not find an appropriate parent window.");
		}
		if( p != null ) {
			if( p instanceof Window )
				w =  (Window) p;
			else
				w = SwingUtilities.getWindowAncestor(p);
		}
		return w;
	}
	/**
	 * Useful utility function for creating a filter from an extension only.
	 * 
	 * @param extension the file extension to filter for. The filtering will be done on a case-insensitive basis.
	 * @param description the description of the filter.
	 */
	public static SJFileFilter createSimpleFileFilter(final String extension, final String description) {
		return new SJFileFilter() {
			@Override
			public String getDescription() {
				return description;
			}
	
			@Override
			public boolean accept(File f) {
				boolean b = f.getName().toLowerCase().endsWith(extension) || f.isDirectory();
				return b;
			}
	
			public boolean accept(File dir, String name) {
				String lower = name.toLowerCase();
				boolean b = lower.endsWith(extension);
				return b;
			}
		} ;
	}
}

class MultiFileAccessory extends JPanel
 				implements PropertyChangeListener, ItemListener {
	JFileChooser jfc;
	JList list;
	JComboBox locationChooser;
	SJFileFilter mff;
	
	MultiFileAccessory( JFileChooser jfc, SJFileFilter mff ) {
		super( new BorderLayout() );
		this.jfc = jfc;
		this.mff = mff;
		jfc.addPropertyChangeListener(this);
		
		list = new JList();
		list.setEnabled( false );
		list.setFocusable(false);
		JScrollPane sp = new JScrollPane( list );
		sp.setBorder( BorderFactory.createTitledBorder("Files to import:") );
		sp.setFocusable(false);
		
		this.add( sp );
		
		update();
	}
	
	private void update() {
		if( locationChooser != null )
			locationChooser.setSelectedIndex(0);
		File[] files = SJFileChooser.deriveFilesFromFoldersAndFilter( mff, jfc.getSelectedFiles() );
		String[] names = new String[ files.length ];
		for( int i=0; i<files.length; ++i )
			names[i] = files[i].getName();
		files = null;
		list.setListData(names);
	}

	public void propertyChange(PropertyChangeEvent evt) {
	    String prop = evt.getPropertyName();
	    
	    //If the directory changed, or the selected file(s) changed,
	    //   update the gui.
	    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
	        update();
	    } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
	        update();
	    } else if (JFileChooser.SELECTED_FILES_CHANGED_PROPERTY.equals(prop)) {
	        update();
	    }
	}

	public void itemStateChanged(ItemEvent e) {
		String target = (String) e.getItem();
		if( target.equals( "Home" ) ) {
			jfc.setCurrentDirectory( new File( System.getProperty("user.home") ) );
		} else if( target.equals( "Desktop" ) ) {
			jfc.setCurrentDirectory( new File( System.getProperty("user.home"), "Desktop" ) );
		} else if( target.equals("Volumes") ) {
			jfc.setCurrentDirectory( new File( "/Volumes" ) );
		}
	}
}
