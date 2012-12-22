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
