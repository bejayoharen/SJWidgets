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
 * Created on April 22, 2004
 *
 * 
 */
package com.xowave.util;

import java.io.*;
import java.net.URI;
import java.nio.channels.*;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bjorn
 *
 * Adds a copyTo function to java.io.file and some other utilities.
 * copyTo uses NIO, when safe, for actual copying so
 * it's pretty fast.
 */
public final class XFile extends File {
	private static final long serialVersionUID = 1L;

	/**
	 * @param pathname
	 */
	public XFile(String pathname) {
		super(pathname);
	}

	/**
	 * @param pathname
	 */
	public XFile(File fl) {
		super(fl.getPath());
	}

	/**
	 * @param parent
	 * @param child
	 */
	public XFile(String parent, String child) {
		super(parent, child);
	}

	/**
	 * @param parent
	 * @param child
	 */
	public XFile(File parent, String child) {
		super(parent, child);
	}

	/**
	 * @param uri
	 */
	public XFile(URI uri) {
		super(uri);
	}
	
	/** if the given file is already an XFile, it returns the file, properly cast.
	 * Otherwise, this returns a newly created XFile.
	 */
	public static XFile asXFile(File file) {
		if( file instanceof XFile )
			return (XFile) file;
		return new XFile( file );
	}

	/**returns a string representing the base name without extension or ".".
	 */
	public String getBaseName() {
		return getBaseNameFromFileName(getName());
	}
	
	/**
	 * Computes the Base name in the same way as getBaseName().
	 * @param s the string representing the filename.
	 * @return the base name without the extension or dot (".") or the full name if the file does not contain a dot.
	 */
	public final static String getBaseNameFromFileName(String s) {
		int index = s.lastIndexOf('.');
		if (index != -1)
			return s.substring(0, index);
		else
			return s;
	}
	
	/**
	 * Computes the Base name in the same way as getBaseName().
	 * @param f the file to derive the basename from.
	 * @return the base name without the extension or dot (".") or the full name if the file does not contain a dot.
	 */
	public final static String getBaseName(File f) {
		return getBaseNameFromFileName(f.getName());
	}
	
	/**
	 * the filename extension or an empty string if the filename does not contain a dot ".".
	 */
	public static final String getExtension(File f) {
		return getExtensionFromFilename(f.getName());
	}

	/**returns a string representing the extension of the file name without the basename or ".".
	 * if there is no extension, it returns an empty string.
	 */
	public String getExtension() {
		return getExtensionFromFilename(getName());
	}

	/**
	 * Computes the extension from a filename (not a full path) in the same way as getExtension.
	 * 
	 * @param s the filename
	 * @return the extension, or an empty string if the filename does not contain a dot (".").
	 */
	public final static String getExtensionFromFilename(String s) {
		int index = s.lastIndexOf('.');
		if (index == -1)
			return "";
		else
			return s.substring(index + 1);
	}
	
	/**
	 * creates a new File with the same path and base name as this file, but renames the extension to the
	 * given extension. If the given extension is "", the returned file will have no extension. the '.' part
	 * of the extension is not necessary.
	 */
	public XFile newFileWithExtension(String newExt) {
		if( newExt.startsWith( "." ) )
			newExt = newExt.substring(1);
		
		if( newExt.equals("") )
			return new XFile( this.getParentFile(), getBaseName() );
		
		return new XFile( this.getParentFile(), getBaseName() + "." + newExt );
	}
	public interface CopyProgressListener {
		/**
		 * called when the copy process has done some work. This function is called at
		 * least twice with values 0.0 and 1.0, unless there is an exception. Fast copies
		 * such as those that use NIO, may not be able to send these updates. Updates will
		 * be in the same thread as the copy!
		 * 
		 * @param complete a value between 0.0 and 1.0 indicating the amount of
		 *        data that has been copied.
		 * @return true to continue the copy, false to cancel. note that canceling
		 * 		may leave an incomplete file.
		 */
		public boolean updateCopyProgress( float complete );
	}
	/**
	 * Moves a file from its current location to target. This may involve a
	 * simple super.renameTo() or a more complex copy and deletion of target.
	 * In the latter case, a copyProgressListener is passed on to the copyTo()
	 * function. This function either succeeds or fails with an IOException.
	 * Best effort is made to minimize the impact of failure.
	 */
	public void moveTo(File target, CopyProgressListener cpl) throws IOException {
		if( cpl != null && !cpl.updateCopyProgress(0) )
			return; //canceled
		if( this.renameTo( target ) ) {
			//phew, that was easy!
			if( cpl != null )
				cpl.updateCopyProgress(1);
			return;
		}
		//Java refused to move the file for me. Lets do it manually:
		copyTo( target, cpl );
		//that succeeded so lets delete the source:
		if( !this.delete() ) {
			//we failed to delete, so lets delete the target, and throw an exception
			target.delete(); //if this fails, there's not much we can reasonably do
			throw new IOException( "Can't move: source file cannot be removed." );
		}
	}
	/**
	 * 
	 * Copies this file to the target. Before copying,
	 * existence of the target file is checked. If the target file exists,
	 * an IOException is thrown. If cpl is non-null, the operation can be canceled
	 * by returning false from updateCopyProgress. Note that this
	 * type of canceling is not always supported. Note that the copied file will be
	 * incomplete after cancellation, so it must be deleted by hand.
	 * 
	 * @param target the location of the new file
	 * @param cpl allows a client to receive updates about the progress and also
	 *    the ability to cancel. Note that updates are sent in the copy thread and,
	 *    thus, care must be taken to ensure thread safety, especially with
	 *    GUI items. Also, work done in the callbacks can slow down the copy progress.
	 * @throws IOException if target exists, this does not exist or some problem occurs
	 *  during read or write. Best effort is made to remove any partially copied file.
	 */
	public void copyTo(File target, CopyProgressListener cpl )
			throws IOException {
		if (target.exists())
			throw new IOException("File Exists: " + target);
		if (!this.exists())
			throw new FileNotFoundException();
		if( this.isDirectory() )
			throw new IOException( "Cannot copy directory: " + this );

		//NIO is bloody fast so we use it if it is safe to do so.
		final boolean useNIO = Environment.isNIOSafe();

		try {
			boolean shouldContinue = true;
			if( cpl != null )
				shouldContinue = cpl.updateCopyProgress(0);
			if( !shouldContinue )
				return;
			if (useNIO) {
				// Create channel on the source
				FileChannel srcChannel = new FileInputStream(this).getChannel();

				// Create channel on the destination
				FileChannel dstChannel = new FileOutputStream(target).getChannel();

				// Copy file contents from source to destination
				dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

				// Close the channels
				srcChannel.close();
				dstChannel.close();
			} else {
				// Create in/out streams
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(this));
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));

				long totalSize = this.length();
				// Declare variable for each byte read
				int ch;

				// copy file one byte at a time until we reach the end of the file or the user cancels
				if (cpl == null) {
					while ((ch = bis.read()) != -1)
						bos.write(ch);
				} else {
					long count = 0;
					while ((ch = bis.read()) != -1) {
						bos.write(ch);
						if( count % 1024 == 0 ) {
							shouldContinue = cpl.updateCopyProgress( ((float)count) / ((float)totalSize) ) ;
							if (!shouldContinue) {
								bis.close();
								bos.close();
								return;
							}
						}
						++count;
					}
				}

				bos.flush();

				try {
					bis.close();
				} catch (IOException ioe) {
					System.out
							.println("WARNING: Could not close file after copy(1).");
				}
				try {
					bos.close();
				} catch (IOException ioe) {
					System.out
							.println("WARNING: Could not close file after copy(2).");
				}
			}
			if( cpl != null )
				cpl.updateCopyProgress(1);
		} catch (IOException ioe) {
			target.delete();
			throw ioe;
		}
	}

	/**
	 * Matches characters that should not be used in a file name.
	 * This is not guaranteed to be safe, but should be alright on most OSes.
	 */
	public static final Pattern unsafeChars = Pattern
			.compile("[^a-zA-Z0-9]&&^ &&^-");

	/**
	 * creates a unique folder given the baseName, base path and the current date.
	 * Appends a number if necessary.
	 * 
	 * Note that the baseName will have all characters
	 * that match "[^a-zA-Z0-9]&&^ &&^-" removed.
	 * 
	 * note that mkdir() must be called on the created object for the
	 * file to exist.
	 * 
	 * */
	public static XFile getUniqueDatedFolder(File directory, String baseName) {
		Calendar cal = Calendar.getInstance();
		String initialName = (baseName == null || baseName.equals("")) ? "" : baseName + " ";
		initialName += cal.get(Calendar.YEAR) + "-";
		initialName += twoDigits(cal.get(Calendar.MONTH) + 1) + "-";
		initialName += twoDigits(cal.get(Calendar.DAY_OF_MONTH));
		initialName = unsafeChars.matcher(initialName).replaceAll("");
		int count = 2;
		String finalName = initialName;
		XFile mf;
		while ((mf = new XFile(directory, finalName)).exists()) {
			finalName = initialName + "-" + threeDigits(count);
			++count;
		}
		return mf;
	}

	private static String twoDigits(String s) {
		if (s.length() == 0)
			return "00";
		else if (s.length() == 1)
			return "0" + s;
		else
			return s;
	}

	private static String twoDigits(int i) {
		String s = i + "";
		return twoDigits(s);
	}

	private static String threeDigits(String s) {
		if (s.length() == 0)
			return "000";
		else if (s.length() == 1)
			return "00" + s;
		else if (s.length() == 2)
			return "0" + s;
		else
			return s;
	}

	private static String threeDigits(int i) {
		String s = i + "";
		return threeDigits(s);
	}

	/**
	 * Depending on the OS, this either strips charactors that should not be in a
	 * file-name (such as the path separator) or removes all special chataters.
	 * returns the stripted string.
	 * 
	 * 
	 * Returns a string with all special character removed, leaving only
	 * spaces, dashes, numbers, periods and letters. This should be fine for any
	 * sane OS.
	 */
	public static String getSafeFileName(String fileNameCandidate) {
		return fileNameCandidate.replaceAll("\\/|:;?<>*","-");
	}
	
	public static String getSafeFileNameLeavingStars(String fileNameCandidate) {
		return fileNameCandidate.replaceAll("\\/|:;?<>","-");
	}

	/**
	 * Recursively deletes this file and sub-files.
	 * On Failure, stops immediately and returns false.
	 */
	public boolean deleteTree() {
		return deleteTree(this);
	}

	private static boolean deleteTree(File f) {
		if (f.isDirectory()) {
			File[] fls = f.listFiles();
			for (int i = 0; i < fls.length; ++i)
				if (!deleteTree(fls[i]))
					return false;
		}
		return f.delete();
	}

	/** if the file exists and is a directory, returns true. If it does not exits,
	 * it tries to create it and returns true if successful. In all other cases,
	 * it returns false.
	 * Note this also returns true if we are not working locally.
	 */
	public boolean guaranteeDir() {
		if( exists() && isDirectory() )
			return true;
		if( exists() )
			return false;
		return mkdir();
	}

	public XFile incrementFilename() {
		String base = getBaseName();
		String ext = getExtension();
		Pattern pattern = Pattern.compile("-[\\d]+$");
		Matcher matcher = pattern.matcher(base);
		if( matcher.find() ) {
			int index = Integer.parseInt( base.substring(matcher.start()+1) );
			++index;
			base = base.substring(0,matcher.start()) + "-"+ index;
		} else {
			base = base + "-2";
		}
		String filename = base;
		if( getName().indexOf('.') != -1 )
			filename += "." + ext;
		return new XFile( getParentFile(), filename );
	}
	
	@Override
	public XFile getParentFile() {
		File f = super.getParentFile();
		if( f == null )
			return null;
		return new XFile( f );
	}
	
	@Override
	public XFile getAbsoluteFile() {
		return new XFile( super.getAbsoluteFile() );
	}
	
	@Override
	public XFile getCanonicalFile() throws IOException {
		return new XFile( super.getCanonicalFile() );
	}
	
	public XFile[] listXFiles() {
		File[] fs = listFiles();
		XFile[] ret = new XFile[ fs.length ];
		for( int i=0; i<fs.length; ++i )
			ret[i] = new XFile( fs[i] );
		return ret;
	}
	
	public XFile[] listXFiles( FilenameFilter ff) {
		File[] fs = listFiles( ff );
		XFile[] ret = new XFile[ fs.length ];
		for( int i=0; i<fs.length; ++i )
			ret[i] = new XFile( fs[i] );
		return ret;
	}
	
	public XFile[] listXFiles( FileFilter ff) {
		File[] fs = listFiles( ff );
		XFile[] ret = new XFile[ fs.length ];
		for( int i=0; i<fs.length; ++i )
			ret[i] = new XFile( fs[i] );
		return ret;
	}
	
	@Override
	public boolean equals( Object f ) {
		return super.equals(f);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
