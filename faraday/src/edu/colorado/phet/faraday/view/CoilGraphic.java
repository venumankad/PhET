/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.Component;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.Coil;


/**
 * CoilGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CoilGraphic extends CompositePhetGraphic implements SimpleObserver {

    private Coil _coilModel;
    
    /**
     * @param component
     */
    public CoilGraphic( Component component, Coil coilModel ) {
        super( component );
        _coilModel = coilModel;
        _coilModel.addObserver( this );  /// XXX this doesn't belong here
        update();
    }
    
    public void update() {
        
        Component component = super.getComponent();
        super.clear(); // remove all children
        
        // Set the number of loops
        int numberOfLoops = _coilModel.getNumberOfLoops();
        int spacing = 25; // XXXX _coilModel.getSpacing
        for ( int i = 0; i < numberOfLoops; i++ ) {
            PhetImageGraphic loop = new PhetImageGraphic( component, FaradayConfig.LOOP_IMAGE );
            super.addGraphic( loop );
            loop.translate( i * spacing, 0 );
        }
        
        // Determine how to scale the image.
        PhetImageGraphic loop = new PhetImageGraphic( component, FaradayConfig.LOOP_IMAGE ); //XXX wasteful
        double scale = (2 * _coilModel.getRadius()) / loop.getImage().getHeight(); //XXX assumes vertical loop
        
        // Set the area
        super.clearTransform();
        super.scale( scale, scale );
    }

}
