/**
 * 
 */
package com.xowave.sjwidget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.xowave.sjwidget.SJComponent;
import com.xowave.util.XColor;

/**
 * @author bjorn
 *
 */
public class SJProgressBar extends SJComponent implements SwingConstants, ActionListener {
	private static final boolean SINE = true;
	private final BoundedRangeModel model;
	
	private MyTimer timer = new MyTimer( 70, this );
	class MyTimer extends javax.swing.Timer implements ChangeListener, ComponentListener {
		public MyTimer(int delay, ActionListener listener) {
			super(delay, listener);
			super.setCoalesce(true);
			super.setRepeats(true);
			SJProgressBar.this.addComponentListener(this);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			repaint();
			check();
		}

		public void check() {
			boolean isRunning = timer.isRunning();
			boolean shouldRun = getPercentComplete() != 0 && getPercentComplete() != 1;
			if( isIndeterminate() )
				shouldRun = true;
			// only run if we are visible on screen:
			shouldRun &= isVisible() && isTopLevelAnsestorVisible() ;
			if( shouldRun && !isRunning )
				start();
			else if( !shouldRun && isRunning )
				stop();
		}

		@Override
		public void componentHidden(ComponentEvent e) {
			stop();
		}

		@Override
		public void componentMoved(ComponentEvent e) {}

		@Override
		public void componentResized(ComponentEvent e) {}

		@Override
		public void componentShown(ComponentEvent e) {
			check();
		}
	}
	
	/**
	 * 
	 */
	public SJProgressBar(String ID) {
		super(ID);
		model = new DefaultBoundedRangeModel(0,0,0,10000);
		model.addChangeListener( timer );
		setup();
		setWidgetID(ID);
	}

	/**
	 * @param newModel
	 */
	public SJProgressBar(BoundedRangeModel newModel, String ID) {
		super(ID);
		model = newModel;
		model.addChangeListener( timer );
		setup();
		setWidgetID(ID);
	}

	/**
	 * @param min
	 * @param max
	 */
	public SJProgressBar(int min, int max, String ID) {
		super(ID);
		model = new DefaultBoundedRangeModel(min,min,min, max);
		model.addChangeListener( timer );
		setup();
		setWidgetID(ID);
	}
	private void setup() {
		setMaximumSize( new Dimension( Integer.MAX_VALUE, 13) );
		setPreferredSize( new Dimension( 160, 13 ) );
		setMinimumSize( new Dimension( 1, 13 ) );
	}
	
	@Override
	public SJProgressBar setWidgetID( String ID ) {
		super.setWidgetID(ID);
		wavy = null;
		setOpaque( getForeground().getAlpha() == 255 && getBackground().getAlpha() == 255 );
		return this;
	}

	BufferedImage wavy = null;
	double lastVal = -1;
	@Override
	public void paintComponent(Graphics gr) {
		timer.check();
		Graphics2D g = (Graphics2D) gr;
		Shape originalClip = g.getClip();
		g.setClip(
				getInsets().left,
				getInsets().top,
				getWidth()-getInsets().left-getInsets().right,
				getHeight()-getInsets().top-getInsets().bottom );
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// -- build the wavy block for fast blitting
		if( wavy == null || wavy.getHeight() != getHeight() ) {
			wavy = new BufferedImage( 20, getHeight(), BufferedImage.TYPE_INT_RGB );
			int ww = wavy.getWidth();
			Color fg = getForeground();
			Color bg = getBackground();
			int a1 = fg.getAlpha();
			int r1 = fg.getRed();
			int g1 = fg.getGreen();
			int b1 = fg.getBlue();
			int a2 = bg.getAlpha();
			int r2 = bg.getRed();
			int g2 = bg.getGreen();
			int b2 = bg.getBlue();
			
			
			for( int i=0; i<ww; ++i ) {
				double ro;
				if(SINE) {
					ro = ( Math.sin( 2*i*Math.PI/ww ) + 1 ) / 2;
				} else {
					int wwb2 = ww/2;
					if( i<=wwb2)
						ro = i/((double)wwb2);
					else
						ro = -(i-ww)/((double)wwb2);
				}
				for( int j=0; j<wavy.getHeight(); ++j ) {
					double ro2 = 1 - ro * ( Math.sin( j*Math.PI/wavy.getHeight() ) );
					int aa = (int) ( a1 * (1-ro2) + a2 * ro2 );
					int rr = (int) ( r1 * (1-ro2) + r2 * ro2 );
					int gg = (int) ( g1 * (1-ro2) + g2 * ro2 );
					int bb = (int) ( b1 * (1-ro2) + b2 * ro2 );
					int rgb = ( 0xff000000 & ( aa << 24 ) )
					        | ( 0x00ff0000 & ( rr << 16 ) )
					        | ( 0x0000ff00 & ( gg << 8  ) )
					        | ( 0x000000ff & ( bb << 0  ) );
					wavy.setRGB(i, j, rgb);
				}
			}
		}

		int width = getWidth();

			if( isIndeterminate() ) {
				long time = System.currentTimeMillis()/45; //a bit slower for indeterminate
				int shift = (int)(time%wavy.getWidth()) ;
				for( int i=0; i<width+shift; i+=wavy.getWidth() )
					g.drawImage(wavy, i-shift, 0, this);
			} else {
				if( isAnimated() ) {
					if( this.getPercentComplete() == 1 ) {
						g.setColor( XColor.blend( getBackground(), getForeground(), .5f ) );
						Rectangle2D rect = new Rectangle2D.Double(
								0,
								0,
								getWidth(),
								getHeight() );
						g.fill(rect);
					} else {
						long time = System.currentTimeMillis()/30;
						int shift = (int)(time%wavy.getWidth()) ;
						double w = ( width ) * this.getPercentComplete();
						
						//smooth out big transitions:
						if( lastVal != -1 && w > lastVal + 2 && timer.isRunning())
							w = w * .1 + lastVal * .9 ;
						lastVal = w;
			
						int wup = (int) Math.ceil(w) ;
						
						for( int i=0; i<wup+shift*2; i+=wavy.getWidth() )
							g.drawImage(wavy, i-shift, 0, this);
			
						g.setColor(getBackground());
						Rectangle2D.Double rect = new Rectangle2D.Double(
								w,
								0,
								getWidth()-w,
								getHeight() );
						g.fill(rect);
					}
				} else {
					double w = ( width ) * this.getPercentComplete();
					
					//smooth out big transitions:
					if( lastVal != -1 && w > lastVal + 2 && timer.isRunning())
						w = w * .1 + lastVal * .9 ;
					lastVal = w;
		
					int wup = (int) Math.ceil(w) ;
					
					Shape c2 = g.getClip();
					Shape clip = new Rectangle2D.Double(0, 0, w, getHeight());
					g.clip(clip);
					if( getBackgroundPainter() != null ) {
						getBackgroundPainter().paintBackground(g, this);
					} else {
						g.setColor(getForeground());
						g.fillRect(0, 0, wup, getHeight());
					}
					
					g.setClip(c2);
		
					g.setColor(getBackground());
					Rectangle2D.Double rect = new Rectangle2D.Double(
							w,
							0,
							getWidth()-w,
							getHeight() );
					g.fill(rect);
				}
			}

		g.setClip(originalClip);
		g.setColor(getForeground());
	}
	
	boolean isIndeterminate = false;
	boolean isAnimated = true;

	public void setAnimated(boolean animated) {
		isAnimated = animated;
	}
	public boolean isAnimated() {
		return isAnimated;
	}
	public void setIndeterminate(boolean indeterminate) {
		isIndeterminate = indeterminate;
	}
	public boolean isIndeterminate() {
		return isIndeterminate;
	}
	public void setMaximum( int newMaximum ) {
		model.setMaximum(newMaximum);
	}
	public void setMinimum( int newMinimum ) {
		model.setMinimum(newMinimum);
	}
	public void setValue( int newValue ) {
		model.setValue(newValue);
	}
	/** use a value between 0 and 1. outside this range sets indeterminate. */
	public void setPercentValue( float f ) {
		if( f > 1 || f < 0 ) {
			setIndeterminate(true);
		} else {
			setIndeterminate(false);
			int v = (int) ( model.getMinimum() + ( model.getMaximum() - model.getMinimum() ) * f + .5f ) ;
			model.setValue( v );
		}
	}
	public int getMaximum() {
		return model.getMaximum();
	}
	public int getMinimum() {
		return model.getMinimum();
	}
	public int getValue() {
		return model.getValue();
	}
	
	/** returns a value between 0 and 1 indicating the amount of progress made.
	 * Note that this is a bit of a misnomer as percent is between 0 and 100.
	 * */
	double getPercentComplete() {
		long span = model.getMaximum() - model.getMinimum();
		double currentValue = model.getValue();
		double pc = (currentValue - model.getMinimum()) / span;
		return pc;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		timer.check();
		repaint();
	}

	private boolean isTopLevelAnsestorVisible() {
		return getTopLevelAncestor() != null && getTopLevelAncestor().isShowing();
	}
}
