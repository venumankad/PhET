/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.energyskatepark.test.phys1d.ParametricFunction2D;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:03:16 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EnergySkateParkModel {
    private double time = 0;
    private ArrayList history = new ArrayList();
    private ArrayList bodies = new ArrayList();
    private ArrayList splines = new ArrayList();

    private Floor floor;

    private double gravity = G_EARTH;
    private double zeroPointPotentialY;
    private ArrayList listeners = new ArrayList();
    private boolean recordPath = false;
    private double initZeroPointPotentialY;
    private PotentialEnergyMetric potentialEnergyMetric;

    public static final double G_SPACE = 0.0;
    public static final double G_EARTH = -9.81;
    public static final double G_MOON = -1.62;
    public static final double G_JUPITER = -25.95;
    private int maxNumHistoryPoints = 100;
    private static boolean thermalLanding = true;


    public EnergySkateParkModel( double zeroPointPotentialY ) {
        this.zeroPointPotentialY = zeroPointPotentialY;
        this.initZeroPointPotentialY = zeroPointPotentialY;
        potentialEnergyMetric = new PotentialEnergyMetric() {
            public double getPotentialEnergy( Body body ) {
                double h = EnergySkateParkModel.this.zeroPointPotentialY - body.getCenterOfMass().getY();
                return body.getMass() * gravity * h;
            }

            public double getGravity() {
                return gravity;
            }

            public PotentialEnergyMetric copy() {
                return this;
            }
        };
        updateFloorState();
    }

    public int numSplineSurfaces() {
        return splines.size();
    }

    public double getTime() {
        return time;
    }

    public void setRecordPath( boolean selected ) {
        this.recordPath = selected;
    }

    public boolean isRecordPath() {
        return recordPath;
    }

    public boolean containsBody( Body body ) {
        return bodies.contains( body );
    }

    public void clearPaths() {
        history.clear();
    }

    public void clearHeat() {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.clearHeat();
        }
    }

    public void setGravity( double value ) {
        if( this.gravity != value ) {
            this.gravity = value;
            for( int i = 0; i < listeners.size(); i++ ) {
                EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
                energyModelListener.gravityChanged();
            }
            updateFloorState();
        }
    }

    public void removeEnergyModelListener( EnergyModelListener energyModelListener ) {
        listeners.remove( energyModelListener );
    }

    public boolean isSplineUserControlled() {
        for( int i = 0; i < splines.size(); i++ ) {
            EnergySkateParkSpline splineSurface = (EnergySkateParkSpline)splines.get( i );
            if( splineSurface.isUserControlled() ) {
                return true;
            }
        }
        return false;
    }

    public void splineTranslated( EnergySkateParkSpline spline, double dx, double dy ) {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            if( body.isOnSpline( spline ) ) {
                body.translate( dx, dy );
                body.notifyDoRepaint();
            }
        }
    }

    public PotentialEnergyMetric getPotentialEnergyMetric() {
        return potentialEnergyMetric;
    }

    public void removeAllSplineSurfaces() {
        while( splines.size() > 0 ) {
            removeSplineSurface( splineSurfaceAt( 0 ) );
        }
    }

    public void removeAllBodies() {
        while( bodies.size() > 0 ) {
            removeBody( 0 );
        }
    }

    public void removeBody( int i ) {
        bodies.remove( i );
    }

    public void updateFloorState() {
        int desiredNumFloors = Math.abs( getGravity() ) > 0 ? 1 : 0;
        if( desiredNumFloors == 1 ) {
            floor = new Floor( this );
        }
        else {
            floor = null;
        }
//        while( getNumFloorSplines() > desiredNumFloors ) {
//            removeFloorSpline();
//        }
//        while( getNumFloorSplines() < desiredNumFloors ) {
//            addFloorSpline();
//        }
//        while( getFloorCount() < desiredNumFloors ) {
//            addFloor( new Floor( this ) );
//        }
//        while( getFloorCount() > desiredNumFloors ) {
//            removeFloor( 0 );
//        }
//        if( getFloorCount() == 2 || getNumFloorSplines() == 2 ) {
//            System.out.println( "getFloorCount() = " + getFloorCount() );
//            System.out.println( "getNumFloorSplines() = " + getNumFloorSplines() );
//        }
    }

    public Floor getFloor() {
        return floor;
    }

    public static boolean isThermalLanding() {
        return thermalLanding;
    }

    public static void setThermalLanding( boolean selected ) {
        thermalLanding = selected;
    }

//    private void removeFloors() {
//        while( floors.size() > 0 ) {
//            removeFloor( 0 );
//        }
//    }

//    private void removeFloor( int i ) {
//        floors.remove( i );
//    }

//    private void removeFloorSpline() {
//        for( int i = 0; i < splines.size(); i++ ) {
//            SplineSurface splineSurface = (SplineSurface)p.get( i );
//            if( splineSurface.getSpline() instanceof FloorSpline ) {
//                removeSplineSurface( splineSurface );
//                i--;
//            }
//        }
//    }

//    public boolean hasFloor() {
//        return getFloorCount() > 0;
//    }
//
//    public int getFloorCount() {
//        return floors.size();
//    }

//    private int getNumFloorSplines() {
//        int sum = 0;
//        for( int i = 0; i < splines.size(); i++ ) {
//            SplineSurface splineSurface = (SplineSurface)splineSurfaces.get( i );
//            if( splineSurface.getSpline() instanceof FloorSpline ) {
//                sum++;
//            }
//        }
//        return sum;
//    }
//
//    private boolean hasFloorSpline() {
//        return getNumFloorSplines() > 0;
//    }

//    private void addFloorSpline() {
//        addSplineSurface( new SplineSurface( new FloorSpline(), false ) );
//    }

    static interface Listener {
        public void numBodiesChanged();

        public void numFloorsChanged();

        public void numSplinesChanged();

        public void paramChanged();
    }

    public EnergySkateParkModel copyState() {
        EnergySkateParkModel copy = new EnergySkateParkModel( zeroPointPotentialY );
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            copy.bodies.add( body.copyState() );
        }
//        for( int i = 0; i < floors.size(); i++ ) {
//            Floor floor = (Floor)floors.get( i );
//            copy.floors.add( floor.copyState() );
//        }
        for( int i = 0; i < splines.size(); i++ ) {
            EnergySkateParkSpline surface = splineSurfaceAt( i );
            copy.splines.add( surface.copy() );
        }
        copy.history = new ArrayList( history );
        copy.time = time;
        copy.gravity = gravity;
        copy.maxNumHistoryPoints = maxNumHistoryPoints;
        return copy;
    }

    public void setState( EnergySkateParkModel model ) {
        bodies.clear();
//        floors.clear();
        splines.clear();
        for( int i = 0; i < model.bodies.size(); i++ ) {
            bodies.add( model.bodyAt( i ).copyState() );
        }
        for( int i = 0; i < model.splines.size(); i++ ) {
            splines.add( model.splineSurfaceAt( i ).copy() );
        }
//        for( int i = 0; i < model.floors.size(); i++ ) {
//            floors.add( model.floorAt( i ).copyState() );
//        }
        this.history.clear();
        this.history.addAll( model.history );
        this.time = model.time;
        this.maxNumHistoryPoints = model.maxNumHistoryPoints;
        setGravity( model.gravity );
        //todo: some model objects are not getting copied over correctly, body's spline strategy could refer to different splines
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.setPotentialEnergyMetric( getPotentialEnergyMetric() );
            if( body.isSplineMode() ) {
                ParametricFunction2D spline = body.getSpline();
                EnergySkateParkSpline esps = getEnergySkateParkSpline( spline );
                if( !containsSpline( spline ) ) {
//                    new RuntimeException( "Skater is on a track that the model doesn't currently know about" ).printStackTrace();
//                    EnergySkateParkSpline bestMatch = getBestSplineMatch( esps );
//                    if( bestMatch == null ) {
//                        System.out.println( "\"Skater is on a track that the model doesn't currently know about\" = " + "Skater is on a track that the model doesn't currently know about" );
//                    }
//                    else {
                    body.stayInSplineModeNewSpline( esps );
//                    }
                }
            }
        }
        updateFloorState();
    }

    private EnergySkateParkSpline getEnergySkateParkSpline( ParametricFunction2D spline ) {
        for( int i = 0; i < splines.size(); i++ ) {
            EnergySkateParkSpline energySkateParkSpline = (EnergySkateParkSpline)splines.get( i );
            if( energySkateParkSpline.getParametricFunction2D() == spline ) {
                return energySkateParkSpline;
            }
        }
        return null;
    }

    private boolean containsSpline( ParametricFunction2D spline ) {
        for( int i = 0; i < splines.size(); i++ ) {
            EnergySkateParkSpline energySkateParkSpline = (EnergySkateParkSpline)splines.get( i );
            if( energySkateParkSpline.getParametricFunction2D() == spline ) {
                return true;
            }
        }
        return false;
    }
//
//    private EnergySkateParkSpline getBestSplineMatch( EnergySkateParkSpline spline ) {
//        if( containsSpline( spline ) ) {
//            return spline;
//        }
//        else {
//            double bestScore = Double.POSITIVE_INFINITY;
//            AbstractSpline best = null;
//            for( int i = 0; i < splines.size(); i++ ) {
//                EnergySkateParkSpline splineSurface = (EnergySkateParkSpline)splines.get( i );
//                double score = spline.getDistance( splineSurface.getSpline() );
//                if( score < bestScore ) {
//                    bestScore = score;
//                    best = splineSurface.getSpline();
//                }
//            }
//            return best;
//        }
//    }

//    private boolean containsSpline( EnergySkateParkSpline spline ) {
//        for( int i = 0; i < splines.size(); i++ ) {
//            EnergySkateParkSpline splineSurface = (EnergySkateParkSpline)splines.get( i );
//            if( splineSurface == spline ) {
//                return true;
//            }
//        }
//        return false;
//    }

    public double timeSinceLastHistory() {
        if( history.size() == 0 ) {
            return time;
        }
        return time - historyPointAt( history.size() - 1 ).getTime();
    }

    public void stepInTime( double dt ) {
        time += dt;
        if( recordPath && numBodies() > 0 && timeSinceLastHistory() > 0.1 ) {
            history.add( new HistoryPoint( this, bodyAt( 0 ) ) );
        }
        if( history.size() > maxNumHistoryPoints ) {
            history.remove( 0 );
        }
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.preStep( dt );
        }
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.stepInTime( dt );
        }
        if( floor != null ) {
            floor.stepInTime( dt );
        }
//        for( int i = 0; i < floors.size(); i++ ) {
//            floorAt( i ).stepInTime( dt );
//        }
    }

    public ArrayList getAllSplines() {
        ArrayList list = new ArrayList();
        for( int i = 0; i < splines.size(); i++ ) {
            EnergySkateParkSpline splineSurface = (EnergySkateParkSpline)splines.get( i );
            list.add( splineSurface );
//            list.add( splineSurface.getBottom() );
        }
        return list;
    }

    public EnergySkateParkSpline splineSurfaceAt( int i ) {
        return (EnergySkateParkSpline)splines.get( i );
    }

//    public Floor floorAt( int i ) {
//        return (Floor)floors.get( i );
//    }

    public void addSplineSurface( EnergySkateParkSpline energySkateParkSpline ) {
        splines.add( energySkateParkSpline );
        notifySplinesChanged();
    }

    private void notifySplinesChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.splinesChanged();
        }
    }

    public void addBody( Body body ) {
        bodies.add( body );
        if( bodies.size() == 1 ) {//The zero point potential now occurs at the center of mass of the skater.
            zeroPointPotentialY = body.getShape().getBounds2D().getHeight() / 2;
            initZeroPointPotentialY = zeroPointPotentialY;
        }
    }

    public int numBodies() {
        return bodies.size();
    }

    public Body bodyAt( int i ) {
        return (Body)bodies.get( i );
    }

//    private void addFloor( Floor floor ) {
//        floors.add( floor );
//    }

    public double getGravity() {
        return gravity;
    }

    public void removeSplineSurface( EnergySkateParkSpline splineSurface ) {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            if( body.isOnSpline( splineSurface ) ) {
                body.setFreeFallMode();
            }
        }
        notifyBodiesSplineRemoved( splineSurface );
//        notifyBodiesSplineRemoved( splineSurface.getBottom() );
        splines.remove( splineSurface );
        notifySplinesChanged();
    }

    private void notifyBodiesSplineRemoved( EnergySkateParkSpline spline ) {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.splineRemoved( spline );
        }
    }

    public double getZeroPointPotentialY() {
        return zeroPointPotentialY;
    }

    public void setZeroPointPotentialY( double zeroPointPotentialY ) {
        this.zeroPointPotentialY = zeroPointPotentialY;
    }

    public void translateZeroPointPotentialY( double dy ) {
        setZeroPointPotentialY( getZeroPointPotentialY() + dy );
    }

    public void reset() {
        bodies.clear();
        splines.clear();
        history.clear();
        setGravity( G_EARTH );
        zeroPointPotentialY = initZeroPointPotentialY;
        updateFloorState();
    }

    public static class EnergyModelListenerAdapter implements EnergyModelListener {

        public void preStep( double dt ) {
        }

        public void gravityChanged() {
        }

        public void splinesChanged() {
        }
    }

    public static interface EnergyModelListener {
        void preStep( double dt );

        void gravityChanged();

        void splinesChanged();
    }

    public void addEnergyModelListener( EnergyModelListener listener ) {
        listeners.add( listener );
    }

    public int numHistoryPoints() {
        return history.size();
    }

    public HistoryPoint historyPointAt( int i ) {
        return (HistoryPoint)history.get( i );
    }
}
