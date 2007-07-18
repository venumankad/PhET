package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.view.RotationBodyNode;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 22, 2007, 11:37:56 PM
 */
public class RotationModel extends MotionModel implements RotationBodyNode.RotationBodyEnvironment, IPositionDriven {
    private RotationPlatform rotationPlatform;
    private ArrayList rotationBodies = new ArrayList();

    public RotationModel( ConstantDtClock clock ) {
        super( clock );
        rotationPlatform = new RotationPlatform();

        RotationBody body1 = new RotationBody( "ladybug.gif", true );
        addRotationBody( body1 );

        RotationBody body2 = new RotationBody( "beetle.gif" );
        addRotationBody( body2 );

        resetAll();
    }

    private void resetBody2( RotationBody body ) {
        body.setPosition( rotationPlatform.getCenter().getX() - rotationPlatform.getRadius() * Math.sqrt( 2 ) / 2.0,
                          rotationPlatform.getCenter().getY() + rotationPlatform.getRadius() );
        body.setOffPlatform();
        body.setOrientation( 0.0 );
    }

    private void resetBody1( RotationBody body ) {
        body.setPosition( rotationPlatform.getCenter().getX() + rotationPlatform.getRadius() / 2,
                          rotationPlatform.getCenter().getY() );
        body.setOnPlatform( rotationPlatform );
        body.setOrientation( 0.0 );
    }

    public void resetAll() {
        super.resetAll();
        rotationPlatform.reset();//has to be reset before bodies

        resetBody1( getRotationBody( 0 ) );
        resetBody2( getRotationBody( 1 ) );
        getTimeSeriesModel().setRecordMode();
        getTimeSeriesModel().setPaused( false );
    }

    private void setVelocityDriven( double value ) {
        rotationPlatform.setUpdateStrategy( rotationPlatform.getVelocityDriven() );
        rotationPlatform.getVVariable().setValue( value );
    }

    protected void setTime( double time ) {
        super.setTime( time );
        rotationPlatform.setTime( time );
        for( int i = 0; i < rotationBodies.size(); i++ ) {
            getRotationBody( i ).setTime( time );
        }
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        rotationPlatform.stepInTime( getTime(), dt );
        for( int i = 0; i < rotationBodies.size(); i++ ) {
            getRotationBody( i ).stepInTime( getTime(), dt );
        }
    }

    public void clear() {
        super.clear();
        rotationPlatform.clear();
        for( int i = 0; i < rotationBodies.size(); i++ ) {
            getRotationBody( i ).clear();
        }
    }

    public void dropBody( RotationBody rotationBody ) {
        if( rotationPlatform.containsPosition( rotationBody.getPosition() ) ) {
            rotationBody.setOnPlatform( rotationPlatform );
        }
        else {
            rotationBody.setOffPlatform();
        }
    }

    public boolean platformContains( double x, double y ) {
        return rotationPlatform.containsPosition( new Point2D.Double( x, y ) );
    }

    public RotationPlatform getRotationPlatform() {
        return rotationPlatform;
    }

    private void addRotationBody( RotationBody rotationBody ) {
        rotationBodies.add( rotationBody );
    }

    public int getNumRotationBodies() {
        return rotationBodies.size();
    }

    public RotationBody getRotationBody( int i ) {
        return (RotationBody)rotationBodies.get( i );
    }

    public void setPositionDriven() {
        rotationPlatform.setPositionDriven();
    }

    public PositionDriven getPositionDriven() {
        return rotationPlatform.getPositionDriven();
    }

    public ISimulationVariable getPlatformAngleVariable() {
        return rotationPlatform.getXVariable();
    }

    public ITimeSeries getPlatformAngleTimeSeries() {
        return rotationPlatform.getXTimeSeries();
    }

    public ISimulationVariable getPlatformVelocityVariable() {
        return rotationPlatform.getVVariable();
    }

    public ITimeSeries getPlatformVelocityTimeSeries() {
        return rotationPlatform.getVTimeSeries();
    }

    public UpdateStrategy getVelocityDriven() {
        return rotationPlatform.getVelocityDriven();
    }

    public ISimulationVariable getPlatformAccelVariable() {
        return rotationPlatform.getAVariable();
    }

    public ITimeSeries getPlatformAccelTimeSeries() {
        return rotationPlatform.getATimeSeries();
    }

    public UpdateStrategy getAccelDriven() {
        return rotationPlatform.getAccelDriven();
    }

}
