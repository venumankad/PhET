/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.controller;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.control.SpectrumSliderKnob;
import edu.colorado.phet.control.SpectrumSliderWithSquareCursor;
import edu.colorado.phet.lasers.model.photon.Beam;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * SpectrumSliderWithReadout
 * <p/>
 * A spectrum slider that adds a readout of the wavelength to the slider knob. It is implemented
 * as a decorator for a simple SpectrumSlider.
 * <p/>
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpectrumSliderWithReadout extends SpectrumSliderWithSquareCursor {
    private Beam beam;
    private Point location;
    private WavelengthReadout readout;

    public SpectrumSliderWithReadout( Component component,
                                      final SpectrumSliderWithSquareCursor wrappedSliderWithSquareCursor,
                                      Beam beam,
                                      double minimumWavelength,
                                      double maximumWavelength,
                                      Point location ) {
        super( component, minimumWavelength, maximumWavelength );
        this.beam = beam;
        this.location = location;
        beam.addWavelengthChangeListener( new WavelengthChangeListener() );
        readout = new WavelengthReadout( component, wrappedSliderWithSquareCursor.getKnob(), location );

        // We have to add the readout directly to the apparatus panel, otherwise we can't
        // get it to respond like a JComponent, and type into it
        ( (ApparatusPanel)component ).addGraphic( readout, 1E14 );
        setKnob( wrappedSliderWithSquareCursor.getKnob() );

        // Add a listener that will move the readout along with the knob
        addChangeListener( readout );
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * A listener to update the slider if wavelength changes
     */
    public class WavelengthChangeListener implements Beam.WavelengthChangeListener {
        public void wavelengthChanged( Beam.WavelengthChangeEvent event ) {
            if( (int)event.getWavelength() != getValue() ) {
                SpectrumSliderWithReadout.this.setValue( (int)event.getWavelength() );
            }
        }
    }

    /**
     * The wavelength readout
     */
    public class WavelengthReadout extends GraphicLayerSet implements ChangeListener {
        private Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 12 );

        private JTextField readout;
        private PhetGraphic readoutGraphic;
        private Point baseLocation;

        public WavelengthReadout( final Component component, SpectrumSliderKnob knob, Point baseLocation ) {
            super( component );
            this.baseLocation = baseLocation;
            readout = new JTextField( 4 );
            readout.setHorizontalAlignment( JTextField.CENTER );
            readout.setFont( VALUE_FONT );
            readout.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    double wavelength = 0;
                    try {
                        String text = readout.getText().toLowerCase();
                        int nmLoc = text.indexOf( "nm" );
                        text = nmLoc >= 0 ? readout.getText().substring( 0, nmLoc ) : text;
                        wavelength = Double.parseDouble( text );
                        beam.setWavelength( wavelength );
                        update( wavelength );
                    }
                    catch( NumberFormatException e1 ) {
                        JOptionPane.showMessageDialog( SwingUtilities.getRoot( component ),
                                                       "Wavelength must be numeric, or a number followed by \"nm\"" );
                        setText( beam.getWavelength() );
                    }
                }
            } );
            readoutGraphic = PhetJComponent.newInstance( component, readout );
            addGraphic( readoutGraphic, 1E9 );

            update( 123 ); // dummy value
        }

        private void update( double wavelength ) {
            // Move to the right spot. The -15 is a total hack. I can't understand why I need it.
            int x = (int)( baseLocation.x + getKnob().getLocation().getX() - getBounds().getWidth() / 2 - 15 );
            int y = (int)( baseLocation.y - getHeight() - 5 );
            setLocation( x, y );
            // Update the text
            setText( wavelength );
        }

        private void setText( double wavelength ) {
            DecimalFormat voltageFormat = new DecimalFormat( "000" );
            readout.setText( voltageFormat.format( wavelength ) + " nm" );
        }

        void setValue( double wavelength ) {
            update( wavelength );
        }

        public void stateChanged( ChangeEvent e ) {
            if( e.getSource() == SpectrumSliderWithReadout.this ) {
                update( getValue() );
            }
        }
    }
}
