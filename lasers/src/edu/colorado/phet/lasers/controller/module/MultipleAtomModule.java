/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.BeamControl2;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.UniversalAtomControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.HighEnergyState;
import edu.colorado.phet.lasers.model.atom.MiddleEnergyState;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.view.LampGraphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
public class MultipleAtomModule extends BaseLaserModule {

    private double s_maxSpeed = 0.1;
    private ArrayList atoms;
    private double middleStateMeanLifetime = LaserConfig.MIDDLE_ENERGY_STATE_MAX_LIFETIME;
    private double highStateMeanLifetime = LaserConfig.HIGH_ENERGY_STATE_MAX_LIFETIME;

    /**
     *
     */
    public MultipleAtomModule( PhetFrame frame, AbstractClock clock ) {
        super( SimStrings.get( "ModuleTitle.MultipleAtomModule" ), frame, clock );

        setThreeEnergyLevels( true );

        // Set the control panel
        setControlPanel( new UniversalAtomControlPanel( this ) );
//        setControlPanel(new MultipleAtomControlPanel(this));

        // Set the size of the cavity
        ResonatingCavity cavity = getCavity();
        Rectangle2D cavityBounds = cavity.getBounds();

        // Set up the beams
        Point2D beamOrigin = new Point2D.Double( s_origin.getX(),
                                                 s_origin.getY() );
        CollimatedBeam seedBeam = ( (LaserModel)getModel() ).getSeedBeam();

        Rectangle2D.Double seedBeamBounds = new Rectangle2D.Double( beamOrigin.getX(), beamOrigin.getY(),
                                                                    s_boxWidth + s_laserOffsetX * 2, s_boxHeight );
        seedBeam.setBounds( seedBeamBounds );
        seedBeam.setDirection( new Vector2D.Double( 1, 0 ) );
        seedBeam.setPhotonsPerSecond( 1 );

        CollimatedBeam pumpingBeam = ( (LaserModel)getModel() ).getPumpingBeam();
        Rectangle2D.Double pumpingBeamBounds = new Rectangle2D.Double( cavity.getBounds().getX() + Photon.RADIUS,
                                                                       cavity.getBounds().getY() / 2,
                                                                       cavityBounds.getWidth() - Photon.RADIUS * 2,
                                                                       s_boxHeight + s_laserOffsetX * 2 );
        pumpingBeam.setBounds( pumpingBeamBounds );
        pumpingBeam.setDirection( new Vector2D.Double( 0, 1 ) );
        // Set the max pumping rate
        pumpingBeam.setMaxPhotonsPerSecond( LaserConfig.MAXIMUM_PUMPING_PHOTON_RATE );
        // Start with the beam turned all the way down
        pumpingBeam.setPhotonsPerSecond( 0 );

        // Only the pump beam is enabled
        seedBeam.setEnabled( false );
        pumpingBeam.setEnabled( true );

        // Set up the graphics
        BufferedImage gunBI = null;
        try {
            gunBI = ImageLoader.loadBufferedImage( LaserConfig.RAY_GUN_IMAGE_FILE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // Pumping beam lamps
        int numLamps = 8;
        double yOffset = 10;
        // The lamps should span the cavity
        double pumpScaleX = ( ( pumpingBeamBounds.getY() ) - yOffset ) / gunBI.getWidth();
        double pumpScaleY = ( pumpingBeamBounds.getWidth() / numLamps ) / gunBI.getHeight();
        AffineTransformOp atxOp2 = new AffineTransformOp( AffineTransform.getScaleInstance( pumpScaleX, pumpScaleY ), AffineTransformOp.TYPE_BILINEAR );
        BufferedImage pumpBeamImage = atxOp2.filter( gunBI, null );
        for( int i = 0; i < numLamps; i++ ) {
            AffineTransform tx = new AffineTransform();
            tx.translate( pumpingBeamBounds.getX() + pumpBeamImage.getHeight() * ( i + 1 ),
                          yOffset );
            tx.rotate( Math.PI / 2 );
            BufferedImage img = new AffineTransformOp( new AffineTransform(), AffineTransformOp.TYPE_BILINEAR ).filter( pumpBeamImage, null );
            PhetImageGraphic imgGraphic = new LampGraphic( pumpingBeam, getApparatusPanel(), img, tx );
            addGraphic( imgGraphic, LaserConfig.PHOTON_LAYER + 1 );
        }

        // Add the beam control
        Point pumpControlLocation = new Point( (int)( cavity.getBounds().getX() - 150 ), 10 );
        BeamControl2 pumpBeamControl = new BeamControl2( getApparatusPanel(), pumpControlLocation, pumpingBeam,
                                                         LaserConfig.MAXIMUM_PUMPING_PHOTON_RATE,
                                                         null, null );
        getApparatusPanel().addGraphic( pumpBeamControl );

        // Set the averaging time for the energy levels display
        setEnergyLevelsAveragingPeriod( 2000 );


    }

    /**
     *
     */
    public void activate( PhetApplication app ) {
        super.activate( app );

        super.setThreeEnergyLevels( true );
        Rectangle2D cavityBounds = getCavity().getBounds();

        Atom atom = null;
        atoms = new ArrayList();
        int numAtoms = 20;
//        int numAtoms = 8;
//        int numAtoms = 20;
        for( int i = 0; i < numAtoms; i++ ) {
            atom = new Atom( getModel() );
            boolean placed = false;

            // Place atoms so they don't overlap
            int tries = 0;
            do {
                placed = true;
                atom.setPosition( ( cavityBounds.getX() + ( Math.random() ) * ( cavityBounds.getWidth() - atom.getRadius() * 4 ) + atom.getRadius() * 2 ),
                                  ( cavityBounds.getY() + ( Math.random() ) * ( cavityBounds.getHeight() - atom.getRadius() * 4 ) ) + atom.getRadius() * 2 );
                atom.setVelocity( (float)( Math.random() - 0.5 ) * s_maxSpeed,
                                  (float)( Math.random() - 0.5 ) * s_maxSpeed );
                for( int j = 0; j < atoms.size(); j++ ) {
                    Atom atom2 = (Atom)atoms.get( j );
                    double d = atom.getPosition().distance( atom2.getPosition() );
//                    if (d <= (atom.getRadius() + atom2.getRadius()) * 1.5) {
//                        placed = false;
//                        break;
//                    }
                }
                if( tries > 1000 ) {
                    System.out.println( "Unable to place all atoms" );
                    break;
                }
            } while( !placed );
            atoms.add( atom );
            addAtom( atom );
        }

        MiddleEnergyState.instance().setMeanLifetime( middleStateMeanLifetime );
        HighEnergyState.instance().setMeanLifetime( highStateMeanLifetime );
    }

    /**
     *
     */
    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        for( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom)atoms.get( i );
            getLaserModel().removeModelElement( atom );
            atom.removeFromSystem();
        }
        atoms.clear();

        middleStateMeanLifetime = MiddleEnergyState.instance().getMeanLifeTime();
        highStateMeanLifetime = HighEnergyState.instance().getMeanLifeTime();
    }
}
