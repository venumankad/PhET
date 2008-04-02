/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.util.Vector2D;


/**
 * CompassGridGraphic is the graphical representation of a "compass grid".
 * As an alternative to a field diagram, the grid shows the strength
 * and orientation of a magnetic field (B-field) at a grid of points in 2D space.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class CompassGridGraphic extends PhetGraphic {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Determines how the magnetic field decreases with the distance from the magnet.
    private static final double DISTANCE_EXPONENT = 3.0;
    
    // Strategy that uses alpha to indicated field strength.
    private static final int ALPHA_STRATEGY = 0;
    
    // Strategy that uses color saturation to indicated field strength.
    private static final int SATURATION_STRATEGY = 1;
    
    // Threshold for applying rescaling of field strength. 
    private static final double RESCALE_THRESHOLD = 0.8;  // 0 ... 1
    
    // Exponent used for rescaling field strength.
    private static final double RESCALE_EXPONENT = 0.4;   // 0.3 ... 0.8
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractMagnet _magnetModel;
    
    // Controls rescaling of field strength.
    private boolean _rescalingEnabled;
    
    // The spacing between compass needles, in pixels.
    private int _xSpacing, _ySpacing;
    
    // Descriptions of the needles that are in the grid (array of NeedleDescriptor).
    private ArrayList _needleDescriptors;
    
    // Cache of needle Shapes and Colors
    private CompassNeedleCache _needleCache;
    
    // Strategy used to indicate field strength;
    private int _strengthStrategy;
    
    // Needles with a strength below this value are not drawn.
    private double _strengthThreshold;
    
    // The grid's bounds.
    private Rectangle _gridBounds;
    
    // Rendering hints.
    private final RenderingHints _renderingHints;
    
    // A reusable point
    private Point _point;
    
    // A reusable vector
    private Vector2D _fieldVector;
    
    // Is the grid in the 2D plane of the magnet, or slightly outside the 2D plane?
    private final boolean _inMagnetPlane;
    
    // Lightweight data structure for holding a needle description.
    protected class NeedleDescriptor {
        public double x, y;
        public double direction;
        public double strength;
    }
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param magnetModel
     * @param xSpacing space between grid points in the X direction
     * @param ySpacing space between grid points in the Y direction
     * @param inMagnetPlane
     */
    public CompassGridGraphic( Component component, AbstractMagnet magnetModel, int xSpacing, int ySpacing, boolean inMagnetPlane ) {
        super( component );
        assert( component != null );
        
        setIgnoreMouse( true );
        
        _magnetModel = magnetModel;
        _xSpacing = xSpacing;
        _ySpacing = ySpacing;
        _inMagnetPlane = inMagnetPlane;
        
        _rescalingEnabled = false;
        
        _strengthStrategy = ALPHA_STRATEGY;  // works on any background color
        _strengthThreshold = FaradayConstants.GRID_BFIELD_THRESHOLD;
        
        _needleCache = new CompassNeedleCache();
        _needleCache.setNeedleSize( 40, 20 );
        _needleCache.setAlphaEnabled( _strengthStrategy == ALPHA_STRATEGY );
        _needleCache.populate();
        
        _gridBounds = new Rectangle( 0, 0, component.getWidth(), component.getHeight() );
        _point = new Point();
        _fieldVector = new Vector2D();
        
        _renderingHints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    }
    
    //----------------------------------------------------------------------------
    // Abstract
    //----------------------------------------------------------------------------
    
    /*
     * Creates the description of the needles (grid points) in the grid.
     * This method must be implemented by all subclasses.
     */
    protected abstract ArrayList createNeedleDescriptors();
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Enables and disables rescaling, used to make values look better when displayed.
     * 
     * @param rescalingEnabled true or false
     */
    public void setRescalingEnabled( boolean rescalingEnabled ) {
        if ( rescalingEnabled != _rescalingEnabled ) {
            _rescalingEnabled = rescalingEnabled;
            updateNeedleDescriptors();
        }
    }
    
    /**
     * Determines whether rescaling is enabled.
     * 
     * @return true or false
     */
    public boolean isRescalingEnabled() {
        return _rescalingEnabled;
    }
    
    /**
     * Sets the spacing between points on the grid.
     * 
     * @param xSpacing space between grid points in the X direction
     * @param ySpacing space between grid points in the Y direction
     */
    public void setSpacing( int xSpacing, int ySpacing ) {
        _xSpacing = xSpacing;
        _ySpacing = ySpacing;
        updateNeedleDescriptors();
    }
    
    /**
     * Gets the spacing between grid point in the X direction.
     * 
     * @return X spacing
     */
    public int getXSpacing() {
        return _xSpacing;
    }
    
    /**
     * Gets the spacing between grid point in the Y direction.
     * 
     * @return Y spacing
     */
    public int getYSpacing() {
        return _ySpacing;
    }

    /**
     * Sets the size of all needles.
     * 
     * @param needleSize the needle size
     */
    public void setNeedleSize( final Dimension needleSize ) {
        assert( needleSize != null );
        _needleCache.setNeedleSize( needleSize );
        _needleCache.populate();
        updateNeedleDescriptors();
    }
    
    /**
     * Gets the size of all needles.
     * 
     * @return the size
     */
    public Dimension getNeedleSize() {
        return new Dimension( _needleCache.getNeedleSize() );
    }
   
    /**
     * Convenience method for setting the strategy used to represent field strength.
     * If the color is black, then color saturation is used.
     * See setStrengthStrategy.
     * 
     * @param color
     */
    public void setGridBackground( Color color ) {
        if ( color.equals( Color.BLACK ) ) {
            setStrengthStrategy( SATURATION_STRATEGY );
        }
        else {
            setStrengthStrategy( ALPHA_STRATEGY );
        }
    }
    
    /*
     * Sets the strategy used to represent field strength when rendering needles.
     * <p>
     * ALPHA_STRATEGY uses the alpha component and will work on any background color.
     * SATURATION_STRATEGY uses color saturation and will work only on a black background.
     * 
     * @param strengthStrategy ALPHA_STRATEGY or SATURATION_STRATEGY
     */
    private void setStrengthStrategy( int strengthStrategy ) {
        assert( strengthStrategy == ALPHA_STRATEGY || strengthStrategy == SATURATION_STRATEGY );
        _needleCache.setAlphaEnabled( strengthStrategy == ALPHA_STRATEGY );
        repaint();
    }
    
    /*
     * Sets the bounds for the grid.
     */
    protected void setGridBounds( int x, int y, int width, int height ) {
        _gridBounds.setBounds( x, y, width, height );
        updateNeedleDescriptors();
    }
    
    /*
     * Gets a reference to the bounds for the grid.
     * Do not modify the bounds returned; use setBounds to modify.
     */
    protected Rectangle getGridBoundsReference() {
        return _gridBounds;
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
        if ( visible ) {
            updateNeedleDescriptors();
        }
    }
    
    /**
     * Draws all of the needles in the grid that have a strength above the threshold.
     * <p>
     * This method is optimized with the following assumptions:
     * <ul>
     * <li>the grid's location is (0,0)
     * <li>the grid's registration point is (0,0)
     * <li>the grid has no transforms applied to it
     * </ul>
     * 
     * @param g2 the graphics context
     */
    public void paint( Graphics2D g2 ) {
        if ( isVisible() ) { 
            Color northColor, southColor;
            Shape northShape, southShape;
            
            super.saveGraphicsState( g2 );
            g2.setRenderingHints( _renderingHints );
            
            // Draw the needles.
            double previousX, previousY;
            previousX = previousY = 0;
            for ( int i = 0; i < _needleDescriptors.size(); i++ ) {
                NeedleDescriptor needleDescriptor = (NeedleDescriptor)_needleDescriptors.get(i);
                
                northColor = _needleCache.getNorthColor( needleDescriptor.strength );
                southColor = _needleCache.getSouthColor( needleDescriptor.strength );
                northShape = _needleCache.getNorthShape( needleDescriptor.direction );
                southShape = _needleCache.getSouthShape( needleDescriptor.direction );
                
                if ( needleDescriptor.strength >= _strengthThreshold ) {
                    
                    g2.translate( needleDescriptor.x - previousX, needleDescriptor.y - previousY );
                    g2.setPaint( northColor );
                    g2.fill( northShape );
                    g2.setPaint( southColor );
                    g2.fill( southShape );
                    
                    previousX = needleDescriptor.x;
                    previousY = needleDescriptor.y;
                }
            }
            
            super.restoreGraphicsState();
        }
    }
    
    /**
     * Determines the bounds of the grid.
     * The bounds are based on the apparatus panel's canvas size, as set in canvasSizeChanged.
     * 
     * @return the bounds of the grid
     */
    protected Rectangle determineBounds() {
        return _gridBounds;
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------

    /*
     * Updates all aspects of the needles.
     */
    protected void updateNeedleDescriptors() {
        updatePositions();
        updateStrengthAndOrientation();
        repaint();
    }
    
    /*
     * Updates the position of needle descriptors to match the current bounds and spacing.
     */
    protected void updatePositions() {
        _needleDescriptors = createNeedleDescriptors();
    }
    
    /*
     * Updates the strength and direction of needle descriptors to match the magnet.
     * 
     * @param inPlane true=in 2D plane of magnet, false=outside 2D plane of magnet
     */
    protected void updateStrengthAndOrientation() {
        
        if ( isVisible() ) {
            
            // For each needle in the grid...
            for ( int i = 0; i < _needleDescriptors.size(); i++ ) {

                // Next needle...
                NeedleDescriptor needleDescriptor = (NeedleDescriptor)_needleDescriptors.get(i);

                // Get the magnetic field information at the needle's location.
                _point.setLocation( needleDescriptor.x, needleDescriptor.y );
                if ( _inMagnetPlane ) {
                    _magnetModel.getStrength( _point, _fieldVector /* output */, DISTANCE_EXPONENT );
                }
                else {
                    _magnetModel.getStrengthOutsidePlane( _point, _fieldVector /* output */, DISTANCE_EXPONENT );
                }
                double angle = _fieldVector.getAngle();
                double magnitude = _fieldVector.getMagnitude();
                
                // Set the needle's direction.
                needleDescriptor.direction = angle;
                
                // Set the needle's strength.
                {
                    // Convert the field strength to a value in the range 0...+1.
                    double magnetStrength = _magnetModel.getStrength();
                    double scale = 0;
                    if ( magnetStrength != 0 ) {
                        
                        // Start with a scale relative to the current strength of the magnet.
                        scale = ( magnitude / magnetStrength );
                        scale = MathUtil.clamp( 0, scale, 1 );
                        
                        // Adjust the scale to improve the visual effect.
                        if ( _rescalingEnabled ) {
                            scale = rescale( scale );
                        }
                        
                        // Adjust the scale to the maximum magnet strength.
                        scale = scale * ( magnetStrength / _magnetModel.getMaxStrength() );
                    }
                    
                    // Set the needle strength.
                    needleDescriptor.strength = scale;
                }
            }
        }
    }
    
    /**
     * Rescales a "field strength" scale value.
     * Since the field strength drops off with the cube of the distance,
     * the grid disappears very quickly around the magnet. 
     * Rescaling makes the grid more visually appealing, but less physically accurate.
     * 
     * @param scale a scale value, in the range 0...1
     * @return the modified scale, in the range 0...1
     */
    private double rescale( double scale ) {
        assert( scale >=0 && scale <= 1 );
        double newStrength = 1.0;
        if ( scale != 0 && scale <= RESCALE_THRESHOLD ) {
            newStrength = Math.pow( scale / RESCALE_THRESHOLD, RESCALE_EXPONENT );
        }
        return newStrength;
    }
}
