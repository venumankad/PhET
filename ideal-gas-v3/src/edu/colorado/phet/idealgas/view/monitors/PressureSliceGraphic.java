/**
 *
 * Author: Another Guy
 * Date: Jan 13, 2004
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.idealgas.PressureSlice;
import edu.colorado.phet.idealgas.model.PressureSensingBox;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.EventListener;
import java.util.EventObject;

public class PressureSliceGraphic extends CompositePhetGraphic implements PressureSensingBox.ChangeListener {

    private float s_overlayTransparency = 0.3f;

    private double y;
    private Rectangle2D.Double boundingRect = new Rectangle2D.Double();
    private RoundRectangle2D.Double readoutRectangle = new RoundRectangle2D.Double();
    private Stroke pressureSliceStroke = new BasicStroke();
    private int pressureSliceHeight = 10;
    private double boxLeftEdge;
    private double boxLowerEdge;
    private double boxRightEdge;
    private PressureSensingBox box;
//    private Box2D box;
    private PressureSlice pressureSlice;
    private SimpleObservable pressureReadingSource;
    private NumberFormat temperatureFormatter = new DecimalFormat( "#" );
    private NumberFormat pressureFormatter = new DecimalFormat( "0.00" );
    private NumberFormat heightFormatter = new DecimalFormat( "0.00" );
    private double temperature;
    private double pressure;
    private Font font = new Font( "Lucida Sans", Font.BOLD, 12 );
    private PhetGraphic internalGraphic;
    private Area drawingArea = new Area();

    /**
     * @param component
     * @param pressureSlice
     * @param box
     */
    public PressureSliceGraphic( Component component, final PressureSlice pressureSlice,
                                 final PressureSensingBox box ) {
        super( component );
        this.pressureSlice = pressureSlice;
        pressureReadingSource = box;
        box.addChangeListener( this );

        internalGraphic = new InternalGraphic( component );
        this.setCursorHand();
        this.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent event ) {
                double newY = Math.min( box.getMaxY(),
                                        Math.max( y + event.getDy(), box.getMinY() ) );
                y = newY;
                pressureSlice.setY( y );
                listenerProxy.moved( new Event( PressureSliceGraphic.this ) );
                internalGraphic.setBoundsDirty();
                internalGraphic.repaint();
            }
        } );
        this.box = box;
        y = box.getMinY() + ( box.getMaxY() - box.getMinY() ) / 2;
        pressureSlice.setY( y );
    }

    protected PhetGraphic getHandler( Point p ) {
        if( isVisible() && drawingArea.contains( p ) ) {
            return this;
        }
        else {
            return null;
        }
    }

    protected Rectangle determineBounds() {
        return internalGraphic.getBounds();
    }

    public void paint( Graphics2D g2 ) {
        internalGraphic.paint( g2 );
    }

    private class InternalGraphic extends PhetShapeGraphic implements SimpleObserver {

        InternalGraphic( Component component ) {
            super( component, null, null );
            setShape( drawingArea );
            pressureSlice.addObserver( this );
            update();
        }

        public boolean contains( int x, int y ) {
            return boundingRect.contains( x, y ) || readoutRectangle.contains( x, y );
        }

        public void update() {
            if( pressureReadingSource == box ) {
                pressure = box.getPressure();
            }
            else {
                pressure = pressureSlice.getPressure();
            }
//            pressure = pressureSlice.getPressure();
            temperature = pressureSlice.getTemperature();

//            pressAve.update( pressure );
//            tempAve.update( temperature );

            // Clear the drawing area and rebuild it
            drawingArea.exclusiveOr( drawingArea );
            drawingArea.add( new Area( boundingRect ) );
            drawingArea.add( new Area( readoutRectangle ) );
            setBoundsDirty();
            repaint();
        }

        //todo: remove after debug
//        Averager pressAve = new Averager( "slice press = " );
//        Averager tempAve = new Averager( "slice temp = ");


        public void paint( Graphics2D g2 ) {
            saveGraphicsState( g2 );

            g2.setFont( font );
            FontMetrics fontMetrics = g2.getFontMetrics();
            int readoutWidth = 95;
            int borderThickness = 8;
            int readoutHeight = fontMetrics.getHeight() * 3 + fontMetrics.getMaxDescent();// + 2 * borderThickness;

            g2.setStroke( pressureSliceStroke );

            // Figure out the size of the box that holds the readouts
            boxLeftEdge = box.getMinX();
            boxRightEdge = box.getMaxX();
            boxLowerEdge = box.getMaxY();
            boundingRect.setRect( boxLeftEdge, y - pressureSliceHeight / 2,
                                  boxRightEdge - boxLeftEdge,
                                  pressureSliceHeight );
            readoutRectangle.setRoundRect( (int)boundingRect.getMinX() - ( readoutWidth + borderThickness * 2 ),
                                           (int)boundingRect.getMinY() - ( readoutHeight / 2 ) - borderThickness,
                                           ( readoutWidth + borderThickness * 2 ), readoutHeight + 2 * borderThickness, 10, 10 );

            // Draw the slice itself, over the box
            g2.setColor( Color.YELLOW );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, s_overlayTransparency ) );
            g2.fill( boundingRect );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
            g2.draw( boundingRect );

            // Draw the framing rectangle for the readout
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, s_overlayTransparency ) );
            g2.fill( readoutRectangle );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
            g2.draw( readoutRectangle );

            // Draw the readouts
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

            Point readoutLocation = new Point( (int)( readoutRectangle.getMinX() + borderThickness ),
                                               (int)( readoutRectangle.getMinY() + borderThickness ) );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
            g2.drawRoundRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight, 5, 5 );

            g2.setColor( Color.WHITE );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC, s_overlayTransparency ) );
            g2.fillRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight );

            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
            g2.setColor( Color.yellow );
            g2.drawRoundRect( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight, 5, 5 );
            int strLocY = readoutLocation.y + fontMetrics.getAscent() / 2;
            String pressureStr = "P = " + pressureFormatter.format( pressure ) + " Atm";
            g2.setColor( Color.black );
            strLocY += borderThickness;
            g2.drawString( pressureStr, readoutLocation.x + 5, strLocY );

            if( Double.isInfinite( temperature ) || Double.isNaN( temperature ) ) {
                temperature = 0.0;
            }
            String temperatureStr = "T = " + temperatureFormatter.format( temperature ) + " K";
            g2.setColor( Color.black );
            strLocY += fontMetrics.getHeight();
            g2.drawString( temperatureStr, readoutLocation.x + 5, strLocY );

            // y location must be converted to units compatible with the graphic ruler
            String heightStr = "height = " + heightFormatter.format( ( ( ( boxLowerEdge - y ) - 3.3 ) / 70.857 ) );
            g2.setColor( Color.black );
            strLocY += fontMetrics.getHeight();
            g2.drawString( heightStr, readoutLocation.x + 5, strLocY );

            // Reset the alpha vaule, just in case the next client of the Graphics2D
            // assumes it is 1.
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );

            restoreGraphicsState();
        }
    }

    //----------------------------------------------------------------
    // PressureSensingBox.ChangeListener implementation
    //----------------------------------------------------------------

    /**
     * If the pressure sensing box is basing its readings on multiple slices,
     * then we want to get our pressure readings from it, rather than our own
     * slice. This keeps the pressure readings we display consistent with those
     * on the dial gauge. If the box is only sending out readings from a single slice,
     * that means gravity is on, and we want to use our own slice for our readings.
     * This may not be the best way to organize this code, but what the hell...
     * @param event
     */
    public void stateChanged( PressureSensingBox.ChangeEvent event ) {
        PressureSensingBox box = event.getPressureSensingBox();
        if( box.getMultipleSlicesEnabled() ) {
            pressureReadingSource = box;
        }
        else {
            pressureReadingSource = pressureSlice;
        }
    }

    //-----------------------------------------------------------------------------------------
    // Event and Listener definitions
    //-----------------------------------------------------------------------------------------
    public class Event extends EventObject {

        public Event( Object source ) {
            super( source );
        }

        public PressureSliceGraphic getPressureSliceGraphic() {
            return (PressureSliceGraphic)super.getSource();
        }

        public int getY() {
            return (int)getPressureSliceGraphic().y;
        }
    }

    public interface Listener extends EventListener {
        void moved( Event event );
    }

    private EventChannel channel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)channel.getListenerProxy();

    public void addListener( Listener listener ) {
        channel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        channel.removeListener( listener );
    }
}

