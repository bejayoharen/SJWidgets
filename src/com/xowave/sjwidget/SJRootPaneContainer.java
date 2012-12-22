package com.xowave.sjwidget;

import javax.swing.RootPaneContainer;

public interface SJRootPaneContainer extends RootPaneContainer {
	public void showLightbox( SJLightbox sjLightbox );
	public void lightboxComplete(SJLightbox sjLightbox);
}
