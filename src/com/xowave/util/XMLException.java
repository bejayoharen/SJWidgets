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
 * Created on Jun 15, 2005 by bjorn
 *
 */
package com.xowave.util;

/**
 * @author bjorn
 *
 * Exception thrown by XMLArray and XMLDict if, for example, a dictionary element is
 * expected but another type of element is found.
 */
public class XMLException extends Exception {
	public XMLException() {
		super();
	}
	public XMLException(String arg0) {
		super(arg0);
	}
}
