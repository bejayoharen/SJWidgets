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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * @author howardshih
 */
public class SJLabelWithBackgroundImage extends SJLabel
{
	Image backgroundImage;
	boolean stretchImage = false;
	boolean centerImage = false;

	public SJLabelWithBackgroundImage(String id) {
		super(id);
	}

	public SJLabelWithBackgroundImage(String text, String id) {
		super(text, id);
	}

	public SJLabelWithBackgroundImage(Icon image, String id) {
		super(image, id);
	}

	public SJLabelWithBackgroundImage(String text, int horizontalAlignment, String id) {
		super(text, horizontalAlignment, id);
	}

	public SJLabelWithBackgroundImage(Icon image, int horizontalAlignment, String id) {
		super(image, horizontalAlignment, id);
	}

	public SJLabelWithBackgroundImage(String text, Icon icon, int horizontalAlignment, String id) {
		super(text, icon, horizontalAlignment, id);
	}

	public void setIcon(ImageIcon icon) {
		backgroundImage = icon.getImage();

		Dimension bgImageDimesnion = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		setMinimumSize(bgImageDimesnion);
		setMaximumSize(bgImageDimesnion);
		setPreferredSize(bgImageDimesnion);
	}

	@Override
	public void paintComponent(Graphics g) {
		// paint the texture
		if (backgroundImage != null) {
			setBackground(TRANSPARENT);

			if (!stretchImage) {

				int x = 0;
				int y = 0;

				if (centerImage) {
					x = (getWidth() - backgroundImage.getWidth(null)) / 2;
					y = (getHeight() - backgroundImage.getHeight(null)) / 2;
				}

				g.drawImage(backgroundImage, x, y, this);
			}
			else {


				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
			}

		}

		super.paintComponent(g);

	}

	public boolean isStretchImage() {
		return stretchImage;
	}

	public void setStretchImage(boolean stretchImage) {
		this.stretchImage = stretchImage;
	}

	public void setCenterImage(boolean centerImage) {
		this.centerImage = centerImage;
	}
}
