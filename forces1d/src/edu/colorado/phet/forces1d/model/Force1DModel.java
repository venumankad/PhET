/** Sam Reid*/
package edu.colorado.phet.forces1d.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.forces1d.Force1DModule;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceModel;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 9:54:55 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Force1DModel implements ModelElement {

    private static final double MAX_TIME = 20.0;
    private static final double EARTH_GRAVITY = 9.8;
    private double gravity = EARTH_GRAVITY;
    private double appliedForce;
    public double frictionForce;
    private double netForce;
    private double wallForce = 0.0;

    private Block block;
    private SmoothDataSeries appliedForceDataSeries;
    private SmoothDataSeries netForceDataSeries;
    private SmoothDataSeries accelerationDataSeries;
    private SmoothDataSeries frictionForceDataSeries;
    private SmoothDataSeries velocityDataSeries;
    private SmoothDataSeries positionDataSeries;
    private ArrayList listeners = new ArrayList();
    private Force1DPlotDeviceModel plotDeviceModel;
    private BoundaryCondition open = new Open();
    private BoundaryCondition walls = new Walls();
    private BoundaryCondition boundaryCondition = open;
    private ArrayList boundaryConditionListeners = new ArrayList();
    private SmoothDataSeries gravitySeries;
    private SmoothDataSeries staticSeries;
    private SmoothDataSeries kineticSeries;
    private SmoothDataSeries massSeries;

    public Force1DModel( Force1DModule module ) {
        block = new Block( this );
        int numSmoothingPoints = 8;
        appliedForceDataSeries = new SmoothDataSeries( numSmoothingPoints );
        netForceDataSeries = new SmoothDataSeries( numSmoothingPoints );
        accelerationDataSeries = new SmoothDataSeries( numSmoothingPoints );
        frictionForceDataSeries = new SmoothDataSeries( numSmoothingPoints );
        velocityDataSeries = new SmoothDataSeries( numSmoothingPoints );
        positionDataSeries = new SmoothDataSeries( numSmoothingPoints );
        plotDeviceModel = new Force1DPlotDeviceModel( module, this, MAX_TIME, 1 / 50.0 );
        gravitySeries = new SmoothDataSeries( numSmoothingPoints );
        staticSeries = new SmoothDataSeries( numSmoothingPoints );
        kineticSeries = new SmoothDataSeries( numSmoothingPoints );
        massSeries = new SmoothDataSeries( numSmoothingPoints );
    }

    public double getNetForce() {
        return netForce;
    }

    public interface BoundaryCondition {
        public void apply();

        double getWallForce( double appliedForce, double frictionForce );
    }

    public void addBoundaryConditionListener( BoundaryConditionListener boundaryConditionListener ) {
        boundaryConditionListeners.add( boundaryConditionListener );
    }

    public static interface BoundaryConditionListener {
        void boundaryConditionOpen();

        void boundaryConditionWalls();
    }

    public class Open implements BoundaryCondition {

        public void apply() {
            setWallForce( 0.0 );
        }

        public double getWallForce( double appliedForce, double frictionForce ) {
            return 0.0;
        }
    }

    public class Walls implements BoundaryCondition {

        public void apply() {
            if( block.getPosition() > 10 ) {
                block.setPosition( 10 );
                block.setAcceleration( 0.0 );
                block.setVelocity( 0.0 );
            }
            else if( block.getPosition() < -10 ) {
                block.setPosition( -10 );
                block.setAcceleration( 0.0 );
                block.setVelocity( 0.0 );
            }
        }

        public double getWallForce( double appliedForce, double frictionForce ) {
            boolean right = block.getPosition() >= 10.0 && appliedForce > 0;
            boolean left = block.getPosition() <= -10.0 && appliedForce < 0;
            if( right || left ) {
                return -( appliedForce + frictionForce );
            }
            else {
                return 0.0;
            }
        }
    }

    private void setWallForce( double wallForce ) {
        if( this.wallForce != wallForce ) {
            this.wallForce = wallForce;
        }
    }

    public double getWallForce() {
        return wallForce;
    }

    public void setBoundsOpen() {
        this.boundaryCondition = open;
        for( int i = 0; i < boundaryConditionListeners.size(); i++ ) {
            BoundaryConditionListener boundaryConditionListener = (BoundaryConditionListener)boundaryConditionListeners.get( i );
            boundaryConditionListener.boundaryConditionOpen();
        }
    }

    public void setBoundsWalled() {
        this.boundaryCondition = walls;
        for( int i = 0; i < boundaryConditionListeners.size(); i++ ) {
            BoundaryConditionListener boundaryConditionListener = (BoundaryConditionListener)boundaryConditionListeners.get( i );
            boundaryConditionListener.boundaryConditionWalls();
        }
    }

    public void setPlaybackIndex( int index ) {
        int numDataPoints = netForceDataSeries.numSmoothedPoints();
        if( index < numDataPoints ) {
            if( index == 0 ) {
                this.netForce = 0;
                this.frictionForce = 0;//TODO this could cause more problems than it solves.
            }
            else {
                this.netForce = netForceDataSeries.smoothedPointAt( index );
                this.frictionForce = frictionForceDataSeries.smoothedPointAt( index );
            }
            setAppliedForce( appliedForceDataSeries.smoothedPointAt( index ) );
//        setGravity( );//TODO do we want provisions for changing gravity?
            block.setAcceleration( accelerationDataSeries.smoothedPointAt( index ) );
            block.setVelocity( velocityDataSeries.smoothedPointAt( index ) );
            block.setPosition( positionDataSeries.smoothedPointAt( index ) );
            this.appliedForceChanged();
            double newGravity = gravitySeries.smoothedPointAt( index );
            if( newGravity != gravity ) {
                this.gravity = newGravity;
                fireGravityChanged();
            }
            double newKinetic = kineticSeries.smoothedPointAt( index );
            if( newKinetic != block.getStaticFriction() ) {
                block.setKineticFriction( newKinetic );
            }
            double newStatic = staticSeries.smoothedPointAt( index );
            if( newStatic != block.getStaticFriction() ) {
                block.setStaticFriction( newStatic );
            }
            double newMass = massSeries.smoothedPointAt( index );
            if( newMass != block.getMass() ) {
                block.setMass( newMass );
            }

        }//TODO else we should stop playback

    }

    public void stepInTime( double dt ) {
        plotDeviceModel.stepInTime( dt );
    }

    public void stepRecord( double dt ) {
        updateBlockAcceleration();
        block.stepInTime( dt );
        boundaryCondition.apply();

        netForceDataSeries.addPoint( netForce );
        frictionForceDataSeries.addPoint( frictionForce );
        appliedForceDataSeries.addPoint( getAppliedForce() );

        accelerationDataSeries.addPoint( block.getAcceleration() );
        velocityDataSeries.addPoint( block.getVelocity() );
        positionDataSeries.addPoint( block.getPosition() );
        gravitySeries.addPoint( getGravity() );
        kineticSeries.addPoint( block.getKineticFriction() );
        staticSeries.addPoint( block.getStaticFriction() );
        massSeries.addPoint( block.getMass() );
    }

    public void stepPlayback( double time, int playbackIndex ) {
        plotDeviceModel.cursorMovedToTime( time, playbackIndex );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public double getStoredFrictionForceValue() {
        return frictionForce;
    }
    //TODO this was previously used by ArrowSetGraphic.ForceArrow
//    public double getTotalForce() {
//        return getStoredFrictionForceValue() + getAppliedForce();
//    }

    public PlotDeviceModel getPlotDeviceModel() {
        return plotDeviceModel;
    }

    public void reset() {
        appliedForce = 0;
        frictionForce = 0;
        netForce = 0;
        block.setPosition( 0.0 );
        block.setVelocity( 0.0 );
        plotDeviceModel.doReset();
        netForceDataSeries.reset();
        appliedForceDataSeries.reset();
        frictionForceDataSeries.reset();
        accelerationDataSeries.reset();
        positionDataSeries.reset();
        velocityDataSeries.reset();
        gravitySeries.reset();
        kineticSeries.reset();
        staticSeries.reset();
        massSeries.reset();
        updateBlockAcceleration();
    }

    public DataSeries getNetForceSeries() {
        return netForceDataSeries.getSmoothedDataSeries();
    }

    public DataSeries getFrictionForceSeries() {
        return frictionForceDataSeries.getSmoothedDataSeries();
    }

    public void setPlaybackMode() {
        plotDeviceModel.setPlaybackMode();
    }

    public void setPaused( boolean paused ) {
        plotDeviceModel.setPaused( paused );
    }

    public static interface Listener {
        void appliedForceChanged();

        void gravityChanged();

//        void wallForceChanged();
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity( double gravity ) {
        this.gravity = gravity;
        updateBlockAcceleration();
        fireGravityChanged();
    }

    private void fireGravityChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.gravityChanged();
        }
    }

    public double getAppliedForce() {
        return appliedForce;
    }

    public void setAppliedForce( double appliedForce ) {
        if( appliedForce != this.appliedForce ) {
            this.appliedForce = appliedForce;
            updateBlockAcceleration();
            appliedForceChanged();
        }
    }

    private void appliedForceChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.appliedForceChanged();
        }
    }

    void updateBlockAcceleration() {
        frictionForce = getFrictionForce();
        wallForce = boundaryCondition.getWallForce( appliedForce, frictionForce );
        netForce = appliedForce + frictionForce + wallForce;
        double acc = netForce / block.getMass();
        block.setAcceleration( acc );
//        if( wallForce == -netForce && netForce != 0.0 ) {
//            System.out.println( "acc = " + acc );
//        }
    }

    public double getFrictionForce() {
        if( block.isMoving() ) {
            double sign = block.getVelocity() >= 0 ? -1 : 1;
            double kineticFrictionForce = sign * block.getKineticFriction() * block.getMass() * gravity;
            return kineticFrictionForce;
        }
        else {//block was stationary
            double u = Math.max( block.getKineticFriction(), block.getStaticFriction() );
            double maxStaticFrictionForce = u * block.getMass() * gravity;
            if( Math.abs( maxStaticFrictionForce ) > Math.abs( appliedForce ) ) {
                //block stays at rest, friction balances applied force.
                return -appliedForce;
            }
            else { //applied force overcomes friction force, block starts moving
                double sign = appliedForce >= 0 ? -1 : 1;
                double frictionForce = u * block.getMass() * gravity * sign;
                return frictionForce; //should be less than applied force
            }
        }
    }

    public Block getBlock() {
        return block;
    }

    public SmoothDataSeries getAppliedForceDataSeries() {
        return appliedForceDataSeries;
    }

    public DataSeries getAccelerationDataSeries() {
        return accelerationDataSeries.getSmoothedDataSeries();
    }

    public SmoothDataSeries getVelocityDataSeries() {
        return velocityDataSeries;
    }

    public SmoothDataSeries getPositionDataSeries() {
        return positionDataSeries;
    }
}
