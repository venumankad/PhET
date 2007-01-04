/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.controller;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.util.SwingThreadModelListener;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.control.IntensitySlider;
import edu.colorado.phet.control.SpectrumSliderWithSquareCursor;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;
import edu.colorado.phet.photoelectric.model.util.PhotoelectricModelUtil;
import edu.colorado.phet.quantum.model.Beam;
import edu.colorado.phet.quantum.model.PhotonSource;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * BeamControl
 * <p/>
 * A wavelength slider and intensity slider for controlling a CollimatedBeam.
 * <p/>
 * The intensity slider actaully controls the photon production rate of the beam.
 * It's range is 0 to beam.getMaxPhotonsPerSecond();
 * <p/>
 * Next to the intensity slider is an editable text field that shows the percentage
 * of maximum production that the beam is set to. This control is an instance of
 * IntesityReadout.
 * <p/>
 * Note that the slider works in photons per second, and the text field in percent.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BeamControl extends GraphicLayerSet implements SwingThreadModelListener,
                                                            Beam.RateChangeListener {
    private BeamControl.IntesitySliderChangeListener intesitySliderChangeListener;

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    public static class Mode {
        private Mode() {
        }

        ;
    }

    public static final Mode INTENSITY = new Mode();
    public static final Mode RATE = new Mode();

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private ApparatusPanel apparatusPanel;
    private IntensitySlider intensitySlider;

    private Point intensitySliderRelLoc = new Point( 68, 28 );
    private Dimension intensitySliderSize = new Dimension( 142, 16 );
    private Point intensitySliderLoc;

    private Point spectrumSliderLoc;
    private Point spectrumSliderRelLoc = new Point( 13, 93 );
    private Dimension spectrumSize = new Dimension( 145, 19 );
    private SpectrumSliderWithReadout wavelengthSlider;
    private Beam beam;
    private double maximumRate;
    private boolean selfUpdating;
    private Mode mode;

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();


    /**
     * @param apparatusPanel
     * @param location
     * @param beam
     * @param maximumRate
     */
    public BeamControl( ApparatusPanel apparatusPanel, Point location,
                        Beam beam, double maximumRate ) {
        this.apparatusPanel = apparatusPanel;
        this.beam = beam;
        this.maximumRate = maximumRate;

        // The background panel
        PhetImageGraphic panelGraphic = new PhetImageGraphic( apparatusPanel,
                                                              PhotoelectricConfig.BEAM_CONTROL_PANEL_IMAGE );
        panelGraphic.setRegistrationPoint( 100, 0 );
        addGraphic( panelGraphic );
        panelGraphic.setLocation( 0, 0 );

        intensitySliderLoc = new Point( location.x + intensitySliderRelLoc.x,
                                        location.y + intensitySliderRelLoc.y );
        spectrumSliderLoc = new Point( location.x + spectrumSliderRelLoc.x,
                                       location.y + spectrumSliderRelLoc.y );

        this.setLocation( location );

        addIntensitySlider( beam, maximumRate );
        addWavelengthSlider( beam );
        beam.addRateChangeListener( this );

        setMode( INTENSITY );
    }

    private void addWavelengthSlider( final Beam beam ) {
        // Make a spectrum wavelengthSlider
        final SpectrumSliderWithSquareCursor wavelengthSlider1 = new SpectrumSliderWithSquareCursor( apparatusPanel,
                                                                                                     PhotoelectricConfig.MIN_WAVELENGTH,
                                                                                                     PhotoelectricConfig.MAX_WAVELENGTH );
        wavelengthSlider = new SpectrumSliderWithReadout( apparatusPanel,
                                                          wavelengthSlider1,
                                                          beam,
                                                          PhotoelectricConfig.MIN_WAVELENGTH,
                                                          PhotoelectricConfig.MAX_WAVELENGTH,
                                                          spectrumSliderLoc );
        wavelengthSlider.setLocation( spectrumSliderRelLoc ); // default is (0,0)
        wavelengthSlider.setOrientation( SpectrumSliderWithSquareCursor.HORIZONTAL ); // default is HORIZONTAL
        wavelengthSlider.setTransmissionWidth( 1.0 ); // default is 0.0
        wavelengthSlider.setKnobSize( new Dimension( 20, 20 ) ); // default is (20,30)
        wavelengthSlider.setSpectrumSize( spectrumSize ); // default is (200,25)
        wavelengthSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                PhetUtilities.invokeLater( new Runnable() {
                    public void run() {
                        int wavelength = (int)MathUtil.clamp( PhotoelectricConfig.MIN_WAVELENGTH,
                                                              wavelengthSlider.getValue(),
                                                              PhotoelectricConfig.MAX_WAVELENGTH );
                        beam.setWavelength( (int)( wavelength ) );
                        intesitySliderChangeListener.stateChanged( new ChangeEvent( beam ) );
                    }
                } );
            }
        } );

        wavelengthSlider.setValue( (int)( beam.getWavelength() ) );
        addGraphic( wavelengthSlider, DischargeLampsConfig.CONTROL_LAYER );
    }

    private void addIntensitySlider( final Beam beam, double maximumRate ) {
        // Make a spectrum intensitySlider
        intensitySlider = new IntensitySlider( VisibleColor.wavelengthToColor( beam.getWavelength() ),
                                               IntensitySlider.HORIZONTAL, intensitySliderSize );
        intensitySlider.setMaximum( (int)maximumRate );
        intensitySlider.setLocation( intensitySliderLoc ); // default is (0,0)
        apparatusPanel.add( intensitySlider );

        IntensityReadout intensityReadout = new IntensityReadout( apparatusPanel, beam );
        intensityReadout.setLocation( (int)( intensitySliderLoc.getX() + intensitySlider.getWidth() ) + 4,
                                      (int)( intensitySliderLoc.getY() + intensitySlider.getHeight() / 2 - intensityReadout.getHeight() / 2 ) - 1 );
        addChangeListener( intensityReadout );
        apparatusPanel.addGraphic( intensityReadout, 1E14 );

        intensitySlider.setValue( 0 );
        intesitySliderChangeListener = new IntesitySliderChangeListener( beam );
        intensitySlider.addChangeListener( intesitySliderChangeListener );
        beam.setPhotonsPerSecond( intensitySlider.getValue() );
        beam.addWavelengthChangeListener( new PhotonSource.WavelengthChangeListener() {
            public void wavelengthChanged( PhotonSource.WavelengthChangeEvent event ) {
                Color color = VisibleColor.wavelengthToColor( event.getWavelength() );
                intensitySlider.setColor( color );
            }
        } );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        intensitySlider.setVisible( visible );
    }

    public void setMode( Mode mode ) {
        this.mode = mode;
        intesitySliderChangeListener.stateChanged( new ChangeEvent( beam ) );
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public Mode getMode() {
        return mode;
    }

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    public void rateChangeOccurred( Beam.RateChangeEvent event ) {
        if( !selfUpdating ) {
            intensitySlider.setValue( (int)beam.getPhotonsPerSecond() );
        }
    }

    private class IntesitySliderChangeListener implements ChangeListener {
        private final Beam beam;

        public IntesitySliderChangeListener( Beam beam ) {
            this.beam = beam;
        }

        public void stateChanged( ChangeEvent e ) {
            PhetUtilities.invokeLater( new Runnable() {
                public void run() {
                    selfUpdating = true;
                    int value = intensitySlider.getValue();

                    // If we're in intensity mode, then the photons/sec is proportional to
                    // the energy of each photon
                    if( mode == INTENSITY ) {
                        value = (int)PhotoelectricModelUtil.intensityToPhotonRate( value, beam.getWavelength() );
                    }
                    beam.setPhotonsPerSecond( value );
                    selfUpdating = false;
                }
            } );
        }
    }
}
