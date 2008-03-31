/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.collision.CollisionDetector;
import edu.colorado.phet.faraday.collision.ICollidable;
import edu.colorado.phet.faraday.model.BarMagnet;

/**
 * BarMagnetGraphic is the graphical representation of a bar magnet.
 * The registration point is at the center of the image.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BarMagnetGraphic extends PhetImageGraphic
    implements SimpleObserver, ICollidable, ApparatusPanel2.ChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Composite COMPOSITE = 
        AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.9f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BarMagnet _barMagnetModel;
    private boolean _seeInsideEnabled;
    private CollisionDetector _collisionDetector;
    private Rectangle[] _collisionBounds;
    private FaradayMouseHandler _mouseHandler;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param barMagnetModel model of the bar magnet
     */
    public BarMagnetGraphic( Component component, BarMagnet barMagnetModel ) {
        super( component, FaradayResources.getImage( FaradayConstants.BAR_MAGNET_IMAGE ) );
        
        assert( component != null );
        assert( barMagnetModel != null );
        
        // Save a reference to the model.
        _barMagnetModel = barMagnetModel;
        _barMagnetModel.addObserver( this );
        
        // Registration point is the center of the image.
        centerRegistrationPoint();
        
        // Setup interactivity.
        _mouseHandler = new FaradayMouseHandler( _barMagnetModel, this );
        _collisionDetector = new CollisionDetector( this );
        _mouseHandler.setCollisionDetector( _collisionDetector );
        super.setCursorHand();
        super.addMouseInputListener( _mouseHandler );
        
        // "See Inside" feature is off by default.
        setSeeInsideEnabled( false );
        
        // Synchronize view with model.
        update();
    }

    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _barMagnetModel.removeObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Override inherited methods
    //----------------------------------------------------------------------------
    
    /**
     * Updates when we become visible.
     * 
     * @param visible true for visible, false for invisible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        update();
    }
    
    /** 
     * Enables and disables the "see inside" feature.
     * 
     * @param enabled true or false
     */
    public void setSeeInsideEnabled( boolean enabled ) {
        _seeInsideEnabled = enabled;
        repaint();
    }
    
    /**
     * Is the "see inside" feature enabled?
     * 
     * @return true or false
     */
    public boolean isSeeInsideEnabled() {
        return _seeInsideEnabled;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {
        
        if ( isVisible() ) {
            
            clearTransform();

            // Size
            final double xScale = _barMagnetModel.getWidth() / getWidth();
            final double yScale = _barMagnetModel.getHeight() / getHeight();
            scale( xScale, yScale );
            
            // Rotation
            if ( _barMagnetModel.getDirection() != 0 ) {
                rotate( _barMagnetModel.getDirection() );
            }
            
            // Location
            setLocation( (int) _barMagnetModel.getX(), (int) _barMagnetModel.getY() );
            
            repaint();
        }
    }
    
    //----------------------------------------------------------------------------
    // PhetImageGraphic overrides
    //----------------------------------------------------------------------------

    /**
     * Draws the magnet.  
     * If transparency is enabled, use alpha compositing
     * to make the magnet slightly transparent.
     * 
     * @param g2 the graphics context
     */
    public void paint( Graphics2D g2 ) {
        if ( isVisible() ) {
            if ( _seeInsideEnabled ) {
                Composite oldComposite = g2.getComposite(); // save
                g2.setComposite( COMPOSITE );
                super.paint( g2 );
                g2.setComposite( oldComposite ); // restore
            }
            else {
                super.paint( g2 );
            }
        }   
    }
    
    //----------------------------------------------------------------------------
    // ICollidable implementation
    //----------------------------------------------------------------------------

    /*
     * @see edu.colorado.phet.faraday.view.ICollidable#getCollisionDetector()
     */
    public CollisionDetector getCollisionDetector() {
        return _collisionDetector;
    }

    /*
     * @see edu.colorado.phet.faraday.view.ICollidable#getCollisionBounds()
     */
    public Rectangle[] getCollisionBounds() {
        if ( isVisible() ) {
            if ( _collisionBounds == null ) {
                _collisionBounds = new Rectangle[1];
                _collisionBounds[0] = new Rectangle();
            }
            _collisionBounds[0].setBounds( getBounds() );
            return _collisionBounds;
        }
        else {
            return null;
        }
    }
    
    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /*
     * Informs the mouse handler of changes to the apparatus panel size.
     */
    public void canvasSizeChanged( ChangeEvent event ) {
        _mouseHandler.setDragBounds( 0, 0, event.getCanvasSize().width, event.getCanvasSize().height );   
    }
}
