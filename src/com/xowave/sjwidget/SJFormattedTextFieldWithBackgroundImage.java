package com.xowave.sjwidget;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.text.Format;

import javax.swing.ImageIcon;

/**
 * @author howardshih
 *
 * Variant of SJFormattedTextField that allows you to set a background image behind your text field.
 * 
 */
public class SJFormattedTextFieldWithBackgroundImage extends SJFormattedTextField
{
	Image backgroundImage;


	public SJFormattedTextFieldWithBackgroundImage(AbstractFormatterFactory factory, Object currentValue, String id) {
		super(factory, currentValue, id);
	}

	public SJFormattedTextFieldWithBackgroundImage(String id) {
		super(id);
	}

	public SJFormattedTextFieldWithBackgroundImage(Object value, String id) {
		super(value, id);
	}

	public SJFormattedTextFieldWithBackgroundImage(Format format, String id) {
		super(format, id);
	}

	public SJFormattedTextFieldWithBackgroundImage(AbstractFormatter formatter, String id) {
		super(formatter, id);
	}

	public SJFormattedTextFieldWithBackgroundImage(AbstractFormatterFactory factory, String id) {
		super(factory, id);
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
			g.drawImage(backgroundImage,0,0,this);
		}

		super.paintComponent(g);
		
	}
}
