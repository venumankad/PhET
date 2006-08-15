/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.GradientElectromagnet;
import edu.colorado.phet.mri.util.ControlBorderFactory;
import edu.colorado.phet.mri.util.SliderControl;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * GradientMagnetControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GradientMagnetControl extends SliderControl {
    public final static double VIEW_TO_MODEL_FACTOR = 10;

    public GradientMagnetControl( final GradientElectromagnet horizontalMagnet, String title ) {
        super( 0,
               0,
               MriConfig.MAX_GRADIENT_COIL_FIELD,
               0.02, 2, 2,
               SimStrings.get( "ControlPanel.MagneticField" ) + ":",
               SimStrings.get( "ControlPanel.Tesla" ),
               5,
               new Insets( 0, 0, 0, 0 )
        );

        setTextEditable( true );
        setBorder( ControlBorderFactory.createSecondaryBorder( title ) );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                horizontalMagnet.setFieldStrength( getValue() * VIEW_TO_MODEL_FACTOR );
            }
        } );
        horizontalMagnet.setFieldStrength( 0 );
    }
}
