/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.model;

import edu.colorado.phet.dischargelamps.model.ElectronSource;
import edu.colorado.phet.dischargelamps.model.ElectronSink;
import edu.colorado.phet.dischargelamps.model.Electron;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.util.*;

/**
 * PhotoelectricTarget
 * <p/>
 * The plate in the photoelectric model that is bombarded with light
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricTarget extends ElectronSource {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------
    static public final double ELECTRON_MASS = 9.11E-31;
    static private final double SPEED_SCALE_FACTOR = 5E-16;
    static private final double MINIMUM_SPEED = 0.1;

    static public final Object ZINC = new String( "Zinc" );
    static public final Object COPPER = new String( "Copper" );
    static public final Object SODIUM = new String( "Sodium" );
    static public final Object MAGNESIUM = new String( "Magnesium" );
    static public final HashSet MATERIALS = new HashSet( );
    static {
        MATERIALS.add( ZINC );
        MATERIALS.add( COPPER );
        MATERIALS.add( SODIUM );
        MATERIALS.add( MAGNESIUM );
    }

    static public final HashMap WORK_FUNCTIONS = new HashMap();
    static {
        WORK_FUNCTIONS.put( ZINC, new Double( 4.3 ) );
        WORK_FUNCTIONS.put( COPPER, new Double( 4.7 ) );
        WORK_FUNCTIONS.put( SODIUM, new Double( 2.3 ) );
        WORK_FUNCTIONS.put( MAGNESIUM, new Double( 3.7 ) );
    }

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private Random random = new Random();
    // The line segment defined by the target
    private Line2D line;
    // The work function for the target material
    private double workFunction;
    private Object targetMaterial;

    /**
     * @param model
     * @param p1
     * @param p2
     */
    public PhotoelectricTarget( BaseModel model, Point2D p1, Point2D p2 ) {
        super( model, p1, p2 );
        line = new Line2D.Double( p1, p2 );
    }

    /**
     * @param p1
     * @param p2
     */
    public void setEndpoints( Point2D p1, Point2D p2 ) {
        line = new Line2D.Double( p1, p2 );
        super.setEndpoints( new Point2D[]{p1, p2} );
    }

    /**
     * Produces an electron of appropriate energy if the specified photon has enough energy.
     *
     * @param photon
     */
    public void handlePhotonCollision( Photon photon ) {

        double de = photon.getEnergy() - workFunction;
        if( de > 0 ) {
            // Determine where the electron will be emitted from
            // The location of the electron is coincident with where the photon hit the plate
            Point2D p = MathUtil.getLineSegmentsIntersection( line.getP1(), line.getP2(),
                                                              photon.getPosition(), photon.getPositionPrev() );
            Electron electron = new Electron( p.getX() + 1, p.getY() );
            electron.setPosition( p.getX() + 1, p.getY() );

            // Determine the speed of the new electron
            double speed = determineNewElectronSpeed( de );
            electron.setVelocity( speed, 0 );

            // Tell all the listeners
            getElectronProductionListenerProxy().electronProduced( new ElectronProductionEvent( this, electron ) );
        }
    }

    /**
     * Determines the initial speed of an electron that is kicked off the target by a photon
     * @param energy
     * @return
     */
    private double determineNewElectronSpeed( double energy ) {
        double maxSpeed = Math.sqrt( 2 * energy / ELECTRON_MASS ) * SPEED_SCALE_FACTOR;

        // Speed is randomly distributed between the max speed and a minimum speed.
        double speed = maxSpeed * random.nextDouble();
        speed = Math.max( speed, MINIMUM_SPEED );
        return speed;
    }

    /**
     * Tells if the target has been hit by a specified photon in the last time step
     *
     * @param photon
     * @return
     */
    public boolean isHitByPhoton( Photon photon ) {
        boolean result = line.intersectsLine( photon.getPosition().getX(), photon.getPosition().getY(),
                                              photon.getPositionPrev().getX(), photon.getPositionPrev().getY() );
        return result;
    }

    //----------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------
    public void setWorkFunction( double workFunction ) {
        this.workFunction = workFunction;
    }

    public void setWorkFunction( Object workFunction ) {
        if( !( workFunction instanceof Double )) {
            throw new RuntimeException( "Invalid parameter type" );
        }
        setWorkFunction( ((Double)workFunction).doubleValue() );
    }

    public void setMaterial( Object material ) {
        this.targetMaterial = material;
        if( !WORK_FUNCTIONS.keySet().contains(  material )) {
            throw new RuntimeException( "Invalid parameter");
        }
        setWorkFunction( WORK_FUNCTIONS.get( material ));
        materialChangeListenerProxy.materialChanged( new MaterialChangeEvent( this ) );
    }

    public Object getMaterial() {
        return targetMaterial;
    }

    //----------------------------------------------------------------
    // Event and listener definitions
    //----------------------------------------------------------------
    public class MaterialChangeEvent extends EventObject {
        public MaterialChangeEvent( Object source ) {
            super( source );
        }

        public PhotoelectricTarget getPhotoelectricTarget() {
            return (PhotoelectricTarget)getSource();
        }

        public Object getMaterial() {
            return getPhotoelectricTarget().getMaterial();
        }
    }

    public interface MaterialChangeListener extends EventListener {
        void materialChanged( MaterialChangeEvent event );
    }

    private EventChannel materialChangeEventChannel = new EventChannel( MaterialChangeListener.class );
    private MaterialChangeListener materialChangeListenerProxy =
            (MaterialChangeListener)materialChangeEventChannel.getListenerProxy();

    public void addMaterialChangeListener( MaterialChangeListener listener ) {
        materialChangeEventChannel.addListener( listener );
    }

    public void removeMaterialChangeListener( MaterialChangeListener listener ) {
        materialChangeEventChannel.removeListener( listener );
    }
}
