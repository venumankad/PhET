/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.LaserConfig;

import java.awt.*;

/**
 * DischargeLampsApp
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampsApp extends PhetApplication {

    public DischargeLampsApp( ApplicationModel descriptor, String args[] ) {
        super( descriptor, args );
    }

    public DischargeLampsApp( String[] args ) {
        super( args, SimStrings.get( "DischargeLampsApplication.title" ),
               SimStrings.get( "DischargeLampsApplication.title" ),
               "0.01",
               new SwingTimerClock( DischargeLampsConfig.DT, DischargeLampsConfig.FPS, AbstractClock.FRAMES_PER_SECOND ),
               true );

        // Determine the resolution of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );
        if( dim.getWidth() == 1024 || dim.getHeight() == 768 ) {
            frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 1024, 768 ) );
        }
        setFrameSetup( frameSetup );

        DischargeLampModule singleAtomModule = new SingleAtomModule( SimStrings.get( "ModuleTitle.SingleAtomModule" ),
                                                                     getClock(),
                                                                     DischargeLampsConfig.NUM_ENERGY_LEVELS );

        double maxSpeed = 0.1;
        DischargeLampModule multipleAtomModule = new MultipleAtomModule( SimStrings.get( "ModuleTitle.MultipleAtomModule" ),
                                                                         getClock(), 30,
                                                                         DischargeLampsConfig.NUM_ENERGY_LEVELS,
                                                                         maxSpeed );
        setModules( new Module[]{singleAtomModule,
                                 multipleAtomModule} );
        setInitialModule( singleAtomModule );
    }


    private static class AppDesc extends ApplicationModel {
        public AppDesc() {
            super( SimStrings.get( "DischargeLampsApplication.title" ),
                   SimStrings.get( "DischargeLampsApplication.title" ),
                   "0.01" );

            setClock( new SwingTimerClock( DischargeLampsConfig.DT, DischargeLampsConfig.FPS, AbstractClock.FRAMES_PER_SECOND ) );

            // Determine the resolution of the screen
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );
            if( dim.getWidth() == 1024 || dim.getHeight() == 768 ) {
                frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 1024, 768 ) );
            }
            setFrameSetup( frameSetup );

            DischargeLampModule singleAtomModule = new SingleAtomModule( SimStrings.get( "ModuleTitle.SingleAtomModule" ),
                                                                         getClock(),
                                                                         DischargeLampsConfig.NUM_ENERGY_LEVELS );

            double maxSpeed = 0.1;
            DischargeLampModule multipleAtomModule = new MultipleAtomModule( SimStrings.get( "ModuleTitle.MultipleAtomModule" ),
                                                                             getClock(), 30,
                                                                             DischargeLampsConfig.NUM_ENERGY_LEVELS,
                                                                             maxSpeed );
            setModules( new Module[]{
                singleAtomModule,
                multipleAtomModule} );
//            setInitialModule( multipleAtomModule );
            setInitialModule( singleAtomModule );
        }
    }

    /**
     * @param args
     */
    public static void main( String[] args ) {

        // Tell SimStrings where the simulations-specific strings are
        SimStrings.setStrings( DischargeLampsConfig.localizedStringsPath );
        SimStrings.setStrings( LaserConfig.localizedStringsPath );

        DischargeLampsApp app = new DischargeLampsApp( args );
//        DischargeLampsApp app = new DischargeLampsApp( new AppDesc(), args );
        app.startApplication();
    }

}
