/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Module for the Magnifying Glass prototype.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MGPModule extends PiccoloModule {

    public MGPModule( JFrame parentFrame ) {
        super( "Magnifying Glass", new ConstantDtClock( 1000, 1 ), true /* startsPaused */);
        MGPModel model = new MGPModel();
        MGPCanvas canvas = new MGPCanvas( model );
        setSimulationPanel( canvas );
        MGPControlPanel controlPanel = new MGPControlPanel( parentFrame, canvas, model );
        setControlPanel( controlPanel );
        setClockControlPanel( null );
        setLogoPanel( null );
    }
}
