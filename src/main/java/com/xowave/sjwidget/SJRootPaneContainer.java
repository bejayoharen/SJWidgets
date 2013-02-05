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

import javax.swing.RootPaneContainer;

/**
 * An SJRootPaneContainer is like a standard swing
 * RootPaneContainer except that it allows you to add lightboxes.
 * This is usually done by constructing a lightbox and showing the lightbox
 * using the showLightbox() method. Note that the lightboxComplete() method is
 * usually called from within the lightbox, and therefore it is usually not
 * Necessary to call this directly. To hide the lightbox, use functions in the
 * SJLightbox API.
 * 
 * 
 * @author bjorn
 *
 */
public interface SJRootPaneContainer extends RootPaneContainer {
	public void showLightbox( SJLightbox sjLightbox );
	public void lightboxComplete(SJLightbox sjLightbox);
}
