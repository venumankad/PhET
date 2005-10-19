/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.view;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.shaper.ShaperConstants;
import edu.colorado.phet.shaper.charts.FourierSumPlot;
import edu.colorado.phet.shaper.model.FourierSeries;


/**
 * OutputPulseView displays the output pulse.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OutputPulseView extends GraphicLayerSet implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double TITLE_LAYER = 2;
    private static final double CHART_LAYER = 3;
    private static final double CONTROLS_LAYER = 4;

    // Background parameters
    private static final int MIN_HEIGHT = 150;
    private static final Dimension BACKGROUND_SIZE = new Dimension( 505, 190 );
    private static final Color BACKGROUND_COLOR = new Color( 215, 215, 215 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;
    
    // Title parameters
    private static final Font TITLE_FONT = new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    
    // Chart parameters
    private static final double L = ShaperConstants.L; // do not change!
    private static final double X_RANGE_START = ( L / 2 );
    private static final double X_RANGE_MIN = ( L / 4 );
    private static final double X_RANGE_MAX = ( 2 * L );
    private static final double Y_RANGE_START = ShaperConstants.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_RANGE_MIN = ShaperConstants.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_RANGE_MAX = 12.0;
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 420, 135 );
    
    // Wave parameters
    private static final Stroke USER_SUM_STROKE = new BasicStroke( 1f );
    private static final Color USER_SUM_COLOR = Color.BLACK;
    private static final Stroke RANDOM_SUM_STROKE = new BasicStroke( 3f );
    private static final Color RANDOM_SUM_COLOR = Color.MAGENTA;
    private static final double PIXELS_PER_POINT = 1;
    
    // Autoscaling
    private static final double AUTOSCALE_FACTOR = 1.5; // multiple max amplitude by this amount when autoscaling
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _userFourierSeries;
    private FourierSeries _randomFourierSeries;
    private PhetShapeGraphic _backgroundGraphic;
    private HTMLGraphic _titleGraphic;
    private PulseChart _chartGraphic;
    private FourierSumPlot _userSumPlot;
    private FourierSumPlot _randomSumPlot;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param userFourierSeries the Fourier series constructed by the user
     * @param randomFourierSeries the Fourier series that is randomly generated
     */
    public OutputPulseView( Component component, FourierSeries userFourierSeries, FourierSeries randomFourierSeries ) {
        super( component );

        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Model
        _userFourierSeries = userFourierSeries;
        _userFourierSeries.addObserver( this );
        _randomFourierSeries = randomFourierSeries;
        _randomFourierSeries.addObserver( this );
        
        // Background
        _backgroundGraphic = new PhetShapeGraphic( component );
        _backgroundGraphic.setShape( new RoundRectangle2D.Double( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height, 20, 20 ) );
        _backgroundGraphic.setPaint( BACKGROUND_COLOR );
        _backgroundGraphic.setStroke( BACKGROUND_STROKE );
        _backgroundGraphic.setBorderColor( BACKGROUND_BORDER_COLOR );
        addGraphic( _backgroundGraphic, BACKGROUND_LAYER );
        _backgroundGraphic.setLocation( 0, 0 );
        
        // Title
        String title = SimStrings.get( "OutputPulseView.title" );
        _titleGraphic = new HTMLGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        _titleGraphic.setRegistrationPoint( _titleGraphic.getWidth()/2, 0 );
        _titleGraphic.setLocation( BACKGROUND_SIZE.width / 2, 5 );
        addGraphic( _titleGraphic, TITLE_LAYER );
        
        // Chart
        {
            _chartGraphic = new PulseChart( component, CHART_RANGE, CHART_SIZE );
            addGraphic( _chartGraphic, CHART_LAYER );
            _chartGraphic.setRegistrationPoint( 0, 0 );
            _chartGraphic.setLocation( 35, 35 );
            _chartGraphic.setXAxisTitle( "t (ms)" ); 
         
            // Random sum plot
            _randomSumPlot = new FourierSumPlot( getComponent(), _chartGraphic, _randomFourierSeries );
            _randomSumPlot.setUseCosines( true );
            _randomSumPlot.setPeriod( L );
            _randomSumPlot.setPixelsPerPoint( PIXELS_PER_POINT );
            _randomSumPlot.setStroke( RANDOM_SUM_STROKE );
            _randomSumPlot.setBorderColor( RANDOM_SUM_COLOR );
            _chartGraphic.addDataSetGraphic( _randomSumPlot );
            _chartGraphic.autoscaleY( _randomSumPlot.getMaxAmplitude() * AUTOSCALE_FACTOR );
            
            // User's sum plot
            _userSumPlot = new FourierSumPlot( getComponent(), _chartGraphic, _userFourierSeries );
            _userSumPlot.setUseCosines( true );
            _userSumPlot.setPeriod( L );
            _userSumPlot.setPixelsPerPoint( PIXELS_PER_POINT );
            _userSumPlot.setStroke( USER_SUM_STROKE );
            _userSumPlot.setBorderColor( USER_SUM_COLOR );
            _chartGraphic.addDataSetGraphic( _userSumPlot );
        }
        
        // Interactivity
        setIgnoreMouse( true );
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _userFourierSeries.removeObserver( this );
        _userFourierSeries = null;
        _randomFourierSeries.removeObserver( this );
        _randomFourierSeries = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update() {
        if ( isVisible() ) {
            /* Update the plots to match their models.
             * Note: It would be more efficient to update only the data set 
             * that has changed, but we don't have that information.  So we 
             * update both data sets.  No one has complained about the 
             * performance of this. 
             */
            _userSumPlot.updateDataSet();
            _randomSumPlot.updateDataSet();
            
            // Auto scale the chart based on the random plot.
            _chartGraphic.autoscaleY( _randomSumPlot.getMaxAmplitude() * AUTOSCALE_FACTOR );
        }
    }
}
