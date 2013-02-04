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

package com.xowave.sjwidget.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;

import org.osgi.framework.Bundle;

/**
 * Utility for fetching resources, including images and audio, in the most convenient way possible.
 * 
 * @author bjorn
 */
public class ResourceUtil {
	private static Bundle bundle;
	public static void setBundle( Bundle bundle ) {
		ResourceUtil.bundle = bundle;
	}
    public static ImageIcon getImageIcon( Object classLoaderObj, String resource ) {
        URL url = getResource( classLoaderObj, resource);
        if( url == null )
            throw new RuntimeException( "Missing resource: " + resource );
        return new ImageIcon( url );
    }
	public static ImageIcon getImageIcon(Object classLoaderObj, String context, String relative) throws IOException {
		URL url = getResource( classLoaderObj, context);
		url = new URL( url, relative );
		ImageIcon ret = new ImageIcon( url );

		if( ret.getImageLoadStatus() == java.awt.MediaTracker.ERRORED )
			throw new RuntimeException( "Missing resource: " + url );
		return ret;
	}
    public static float[] load16BitMonoAudioIntoRAM( Object classLoaderObj, String resource ) throws IOException {
        float[] ret;
        URL url = getResource( classLoaderObj, resource);
        if( url == null )
            throw new RuntimeException( "Missing resource: " + resource );
        try {
            AudioInputStream is = AudioSystem.getAudioInputStream(url);
            if( !is.getFormat().isBigEndian() )
                throw new IOException( "Little Endian Not Supported" );
            if( is.getFormat().getSampleSizeInBits() != 16 )
                throw new IOException( "Only 16 bit files supported" );
            if( is.getFormat().getChannels() != 1 )
                throw new IOException( "Only Mono Supported" );
            if( is.getFormat().getEncoding() != AudioFormat.Encoding.PCM_SIGNED )
                throw new IOException( "Only Signed PCM" );
            final int length = (int) is.getFrameLength();
            try {
                ret = new float[length];
            } catch (OutOfMemoryError oome) {
                throw new IOException("Not enough memory to load sound.");
            }
            byte b[] = new byte[2];
            boolean hasNotSlept = true;
            for (int i = 0; i < length; ++i) {
                int r = 0;
            	while( r == 0 ) {
            		r = is.read(b);
                    if( r == 0 && hasNotSlept ) {
                    	try {
    						Thread.sleep( 500 );
    	                	hasNotSlept = false;
    					} catch (InterruptedException e) {}
                    } else if( r == 0 && !hasNotSlept ) {
                    	break;
                    }
            	}
                if( r != 2 )
                    throw new IOException( "could not read from audio file: " + r + " after " + i + "/" + length );
                short s = (short)((b[1]&0xff) | (b[0]<<8)) ;
                ret[i] = s > 0 ? s / 32767f : s / 32768f ;
            }
            return ret;
        } catch (UnsupportedAudioFileException usf) {
            throw new IOException("Unsuported Audio File Format.");
        }
    }
	public static InputStream getResourceAsStream(Object classLoaderObj, String path) throws IOException {
		URL url = getResource(classLoaderObj,path);
		if( url == null )
			throw new IOException( "Path not found: " + path );
		return url.openStream();
	}
	public static InputStream getResourceAsStream(Object classLoaderObj, String context, String relative) throws IOException {
		URL url = getResource( classLoaderObj, context );
		if( url == null )
			throw new IOException( "Path not found: " + context + " : " + relative );
		return new URL( url, relative ).openStream();
	}
	public static ImageIcon getTransparentImageFor(ImageIcon imageIcon) {
		// Create an image that supports transparent pixels
		int width = imageIcon.getIconWidth();
		int height = imageIcon.getIconHeight();
		BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		//set to transparent:
		int[] data = new int[width * height];
		int index = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				data[index++] = 0;
			}
		}
		bi.setRGB(0, 0, width, height, data, 0, width);

		return new ImageIcon( bi );
	}
	public static final boolean THREAD = false;
	public static URL getResource(Object classLoaderObj, String path) {
		if( THREAD ) {
     		ClassLoader cl = null;
			if( classLoaderObj != null )
				cl = classLoaderObj.getClass().getClassLoader();
			if( cl == null )
				return Thread.currentThread().getContextClassLoader().getResource(path);
			else
				return cl.getResource(path);
		} else {
			if( bundle != null )
				return bundle.getResource(path);
			
			ClassLoader cl = null;
			if( classLoaderObj != null )
				cl = classLoaderObj.getClass().getClassLoader();
			if( cl == null )
				return Thread.currentThread().getContextClassLoader().getResource(path);
			else
				return cl.getResource(path);
		}

	}
	public static Class<?> getClass(Object classLoaderObj, String path) throws ClassNotFoundException {
		if( THREAD ) {
			ClassLoader cl = null;
			if( classLoaderObj != null )
				cl = classLoaderObj.getClass().getClassLoader();
			if( cl == null )
				return Thread.currentThread().getContextClassLoader().loadClass(path);
			else
				return cl.loadClass(path);
		} else {
			if( bundle != null )
				return bundle.loadClass(path);
			
			ClassLoader cl = null;
			if( classLoaderObj != null )
				cl = classLoaderObj.getClass().getClassLoader();
			if( cl == null )
				return Thread.currentThread().getContextClassLoader().loadClass(path);
			else
				return cl.loadClass(path);
		}
	}
	public static boolean isBundle() {
		return bundle != null;
	}
}
