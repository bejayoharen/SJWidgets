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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.xowave.sjwidget.util.BackgroundPainter;
import com.xowave.sjwidget.util.WidgetUtil;
import com.xowave.util.UIUtil;

/**
 * Like a JSlider with custom UI.
 * 
 * @author bjorn
 */
public class SJSlider extends JPanel implements SJWidget, SwingConstants, MouseListener, MouseMotionListener {
    public static final int SLIDER_Y_PADDING = 2;
	public static final int DEFAULT_SLIDER_LEFT_PADDING = 2;
    public static final int DEFAULT_SLIDER_RIGHT_PADDING = 2;
    public static final int SLIDER_FILL_X_OFFSET = 2;
    public static final int SLIDER_FILL_Y_OFFSET = 2;
	public static final int MINOR_TICK_LENGTH = 3;
	public static final int MAJOR_TICK_LENGTH = 6;
	public static final int DEFAULT_SLIDER_WIDTH = 16;
	public static final int DEFAULT_SLIDER_HEIGHT = 8;
	private BackgroundPainter backgroundPainter;
    private ImageIcon sliderImage = null;
    private int sliderWidth = DEFAULT_SLIDER_WIDTH;
    private int sliderHeight = DEFAULT_SLIDER_HEIGHT;
    private BoundedRangeModel boundedRangeModel;
    private final int orientation;
    private final boolean snapToTicks = false;
    private int sliderLeftPadding = DEFAULT_SLIDER_LEFT_PADDING;
    private int sliderRightPadding = DEFAULT_SLIDER_RIGHT_PADDING;
    private final int minorTickSpacing = -1;
    private final int majorTickSpacing = -1;
    private final Rectangle sliderArea = new Rectangle( -1, -1, -1, -1 );
    public static final Color TRANSPARENT = new Color( 0, 0, 0, 0 );
    private boolean showBar = true ;
    
    public SJSlider( int orientation, int min, int max, int defaultValue, String id ) { //we will start with horizontal only
    	super( null );
        this.orientation = orientation;
        setBorder(BorderFactory.createEmptyBorder());
        boundedRangeModel = new DefaultBoundedRangeModel( defaultValue, 0, min, max );
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        setBackground( TRANSPARENT );
        setOpaque(false);
        setWidgetID( id );
    }
    
    private int getEffectiveWidth() {
    	int w = orientation == VERTICAL ? getHeight() : getWidth() ;
    	return w - sliderWidth - sliderLeftPadding - sliderRightPadding ;
    }
    
    static final Stroke LINE_STROKE_1 = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    static final Color  LINE_COLOR_1  = new Color( 1f, 1f, 1f, .6f );
    static final Stroke LINE_STROKE_2 = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final Color  LINE_COLOR_2  = new Color( .4f, .4f, .5f, .6f );
    static final Color  SHADOW_COLOR  = new Color( 0, 0, 0, 64 );
    static final Stroke SHADOW_STROKE = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

    @Override
    public void paintComponent( Graphics gg ) {
    	if( labelTable != null && labelHeight == -1 ) {
    		//FIXME: labelTable only works for vertical sliders ATM
    		labelHeight = 0;
	    	for( Integer i : labelTable.keySet() ) {
	    		JComponent c = labelTable.get(i);
	    		int x = getPos( i.intValue() );
	    		int w = c.getPreferredSize().width;
	    		int h = c.getPreferredSize().height;
	    		x += (sliderWidth-w)/2;
	    		c.setBounds(x, 1, w, h);
	    		add( c );
	    		labelHeight = Math.max(labelHeight, h);
	    	}
    	}
    	
    	
    	Graphics2D g = (Graphics2D) gg;
    	int W, H;
    	if( orientation == VERTICAL ) {
    		g.translate(getWidth()/2, getHeight()/2);
    		g.rotate(-Math.PI/2);
    		g.translate(-getHeight()/2,  -getWidth()/2);
    		W = getHeight();
    		H = getWidth();
    	} else {
    		W = getWidth();
    		H = getHeight();
    	}
    	
    	if( backgroundPainter != null ) {
			backgroundPainter.paintBackground(gg,this);
		} else {
			super.paintComponent(gg);
		}
        int val = getPos(boundedRangeModel.getValue()) ;
        
        float h;
        UIUtil.setBeautifulRendering(g);
        Stroke origStroke = g.getStroke();
        GeneralPath gp = new GeneralPath();
        if( showBar ) {
	        h = labelHeight + ( H - labelHeight ) / 2.0f ;
	        g.setColor ( LINE_COLOR_1 );
	        g.setStroke( LINE_STROKE_1 );
	        gp.moveTo( sliderWidth/2, h);
	        gp.lineTo( sliderWidth/2+W-sliderWidth, h);
	        g.draw(gp);
	        g.setColor ( LINE_COLOR_2 );
	        g.setStroke( LINE_STROKE_2 );
	        gp.reset();
	        gp.moveTo( sliderWidth/2+1, h+1);
	        gp.lineTo( sliderWidth/2+W-sliderWidth, h+1);
	        g.draw(gp);
        }
		Color f = this.getForeground();
		f = new Color(f.getRed(), f.getGreen(), f.getBlue(), 200 );
		h = labelHeight + ( H - sliderHeight - labelHeight ) / 2.0f ;
		if( sliderImage == null ) {
			RoundRectangle2D roundedRectangle;
	        //drop shadow:
	        g.setStroke( SHADOW_STROKE );
	        g.setColor( SHADOW_COLOR );
	        
	        roundedRectangle = new RoundRectangle2D.Float( val - 2f, h + 2f, sliderWidth+2, sliderHeight, 6, 6 );
	        g.fill(roundedRectangle);
	        
	        g.setStroke( LINE_STROKE_2 );
	        g.setColor( f );
	        //draw the shape:
	        roundedRectangle.setRoundRect( val, h, sliderWidth, sliderHeight, 3, 3 );
	        g.fill(roundedRectangle);
			g.setColor( new Color( 255, 255, 255, 64 ) );
			//highlight the top third:
			roundedRectangle.setRoundRect( val, h, sliderWidth, ( 2 * sliderHeight ) / 3, 3, 3 );
	        g.fill(roundedRectangle);
	        //highlight the sides:
	        gp.reset();
	        gp.moveTo(val+1, h );
	        gp.lineTo(val+1, h+sliderHeight);
	        g.draw(gp);
	        gp.reset();
	        gp.moveTo(val+sliderWidth-1, h );
	        gp.lineTo(val+sliderWidth-1, h+sliderHeight);
	        g.draw(gp);
	        g.setColor( new Color(255-f.getRed(), 255-f.getGreen(), 255-f.getBlue(), 255 ) );
	        //notch in the middle:
	        gp.reset();
	        gp.moveTo(val+sliderWidth/2f, h + 2 );
	        gp.lineTo(val+sliderWidth/2f, h + sliderHeight - 2);
	        g.draw(gp);
		} else {
			g.drawImage( sliderImage.getImage(), val, (int)(h+.5), this );
		}
		
		if( orientation == VERTICAL ) {
			sliderArea.setBounds( (int)(h+.5), W - (val) - sliderWidth - 2, sliderHeight, sliderWidth+2 );
		} else {
			sliderArea.setBounds( val, (int)(h+.5), sliderWidth+2, sliderHeight );
		}

        g.setStroke(origStroke);
    }
	/**
	 * Converts Bounded Range value to screen x position.
	 * 
	 * @return
	 */
	private int getPos(int val) {
		return sliderLeftPadding + (int) ( getEffectiveWidth() * ( ( val - boundedRangeModel.getMinimum() ) / (float)( boundedRangeModel.getMaximum() - boundedRangeModel.getMinimum()) ) );
	}
	
	/** you usually want to call this and set it to false, but showing the bar is useful for positioning */
	public void setShowBar( boolean showBar ) {
		this.showBar = showBar;
	}
    
	private Hashtable<Integer,JComponent> labelTable = null;
	private int labelHeight = -1;
    public void setLabelTable( Hashtable<Integer,JComponent> labelTable ) {
    	this.labelTable = labelTable;
    	removeAll();
    	labelHeight = -1;
    }

    public int getValue() {
        return boundedRangeModel.getValue();
    }
    
    public void setSliderPadding( int left, int right ) {
    	sliderLeftPadding = left;
    	sliderRightPadding = right;
    	if( isVisible() )
    		repaint();
    }

    public int getMaximum() {
        return boundedRangeModel.getMaximum();
    }

    public int getMinimum() {
        return boundedRangeModel.getMinimum();
    }

    public void setMaximum(int val) {
        boundedRangeModel.setMaximum(val);
        repaint();
    }

    public void setMinimum(int val) {
        boundedRangeModel.setMinimum(val);
        repaint();
    }

    public void setValue( int val ) {
        boundedRangeModel.setValue(val);
        repaint();
    }

    HashSet<ChangeListener> listeners = new HashSet<ChangeListener>();
    public void addChangeListener( ChangeListener cl ) {
        listeners.add(cl);
    }

    public void mouseClicked(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {}

    private int mouseOffset = -1;
	private int mouseCoord = -1;
	public void mousePressed(MouseEvent e) {
		mouseOffset = -1;
		if( sliderArea.contains(e.getPoint()) ) {
			if( orientation == HORIZONTAL ) {
				mouseCoord = e.getX();
				mouseOffset = mouseCoord - sliderArea.x ;
			} else {
				mouseCoord = e.getY();
				mouseOffset = mouseCoord - ( sliderArea.y + sliderArea.height ) ;
			}
		}
    }
	public void mouseDragged(MouseEvent e) {
		if( mouseOffset != -1 ) {
			double pos = 0;
			if( orientation == HORIZONTAL ) {
				mouseCoord = e.getX();

				pos = (mouseCoord - sliderLeftPadding - mouseOffset) / ((double)getEffectiveWidth()) ;
			} else {
                mouseCoord = e.getY();

                pos = (getHeight() - mouseCoord + mouseOffset) / ((double)getEffectiveWidth()) ;
			}

			// calculate appropriate value for parameter:
			pos *= boundedRangeModel.getMaximum() - boundedRangeModel.getMinimum();
			int val ;
			if( snapToTicks ) {
				int spacing = 1;
				if( minorTickSpacing != -1 )
					spacing = minorTickSpacing ;
				if( majorTickSpacing != -1 && majorTickSpacing < minorTickSpacing )
					spacing = majorTickSpacing ;
				val = ( (int) ( pos + spacing/2.0 + .5 ) ) ;
				val -= val % spacing ;
			} else {
				val = (int) ( pos + .5 );
			}
            val += boundedRangeModel.getMinimum();
			if( val > boundedRangeModel.getMaximum() )
				val = boundedRangeModel.getMaximum();
			if( val < boundedRangeModel.getMinimum() )
				val = boundedRangeModel.getMinimum();

            boundedRangeModel.setValue(val);
            repaint();
		}
        ChangeEvent ce = new ChangeEvent(this);
        for( ChangeListener l : listeners )
            l.stateChanged(ce);
	}

	public void mouseReleased(MouseEvent e) {
		mouseOffset = -1;
		mouseCoord  = -1;
    }

	public String getWidgetID() {
		return (String) this.getClientProperty(ID_KEY);
	}
	
	public String getWidgetClass() {
		return (String) this.getClientProperty(CLASS_KEY);
	}

	public SJSlider setWidgetID(String ID) {
		WidgetUtil.registerAndSetup(this, ID);
		return this;
	}
	
	public void setIcon( ImageIcon sliderImage ) {
		if( orientation == VERTICAL ) {
			BufferedImage bi = new BufferedImage( sliderImage.getIconHeight(), sliderImage.getIconWidth(), BufferedImage.TYPE_INT_ARGB) ;
			Graphics2D g = (Graphics2D) bi.getGraphics();
    		g.translate(bi.getWidth()/2, bi.getHeight()/2);
    		g.rotate(Math.PI/2);
    		g.translate(-bi.getHeight()/2,  -bi.getWidth()/2);
    		g.drawImage(sliderImage.getImage(), 0, 0, null );
    		
			this.sliderImage = new ImageIcon( bi );
		} else {
			this.sliderImage = sliderImage;
		}
		if( this.sliderImage == null ) {
			sliderWidth = DEFAULT_SLIDER_WIDTH;
			sliderHeight = DEFAULT_SLIDER_HEIGHT;
		} else {
			sliderWidth = this.sliderImage.getIconWidth();
			sliderHeight = this.sliderImage.getIconHeight();
		}
	}
	
	public ImageIcon getIcon() {
		return sliderImage ;
	}
	
	public void setWidgetText(String text) {
		this.putClientProperty(WIDGET_TEXT_KEY, text);
	}
	public String getWidgetText() {
		return (String) this.getClientProperty(WIDGET_TEXT_KEY);
	}

	public void setBackgroundPainter(BackgroundPainter bp) {
		backgroundPainter = bp;
	}
}
