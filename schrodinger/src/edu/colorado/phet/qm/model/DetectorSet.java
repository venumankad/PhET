/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Jun 21, 2005
 * Time: 9:41:09 PM
 * Copyright (c) Jun 21, 2005 by Sam Reid
 */

public class DetectorSet {
    private ArrayList detectors = new ArrayList();
    private static final Random random = new Random();
    private DiscreteModel.Listener listener;
    private Wavefunction wavefunction;
    private boolean autodetect = true;

    public DetectorSet( Wavefunction wavefunction ) {
        this.wavefunction = wavefunction;
        listener = new DetectorSet.MyListener();
    }

    public void reset() {
        for( int i = 0; i < detectors.size(); i++ ) {
            Detector detector = (Detector)detectors.get( i );
            detector.reset();
        }
    }

    public void addDetector( Detector detector ) {
        detectors.add( detector );
    }

    public void updateDetectorProbabilities() {
        for( int i = 0; i < detectors.size(); i++ ) {
            Detector detector = (Detector)detectors.get( i );
            detector.updateProbability( getWavefunction() );
        }
    }

    public void fireAllEnabledDetectors() {
        fireAllEnabledDetectors( new FireEnabled() );
    }

    public boolean containsDetector( Detector detector ) {
        return detectors.contains( detector );
    }

    public boolean isAutoDetect() {
        return autodetect;
    }

    public void gunFired() {
        enableAll();
    }

    private static interface FireStrategy {
        void fire( Detector detector, Wavefunction wavefunction, double norm );
    }

    private static class FireEnabled implements FireStrategy {
        public void fire( Detector detector, Wavefunction wavefunction, double norm ) {
            detector.fire( wavefunction, norm );
        }
    }

    private static class FireWhenReady implements FireStrategy {
        public void fire( Detector detector, Wavefunction wavefunction, double norm ) {
            if( detector.timeToFire() ) {
                detector.fire( wavefunction, norm );
            }
        }
    }

    private void fireAllEnabledDetectors( FireStrategy fireStrategy ) {
        double norm = 1.0;
        if( getWavefunction().getMagnitude() > 0 ) {
            for( int i = 0; i < detectors.size(); i++ ) {
                Detector detector = (Detector)detectors.get( i );
                if( detector.isEnabled() ) {
                    fireStrategy.fire( detector, getWavefunction(), norm );
                }
            }
        }
//        else {
//            if( oneShotDetectors ) {//todo what was this for?
//                for( int i = 0; i < detectors.size(); i++ ) {
//                    Detector detector = (Detector)detectors.get( i );
//                    detector.setEnabled( false );
//                }
//            }
//        }

    }

    public Point getCollapsePoint() {//todo call getCollapsePoint with the internal bounds.
        return new CollapseComputation().getCollapsePoint( wavefunction, wavefunction.getBounds() );
    }

    private Wavefunction getWavefunction() {
        return wavefunction;
    }

    public DiscreteModel.Listener getListener() {
        return listener;
    }

    private void notifyTimeStepped() {
        for( int i = 0; i < detectors.size(); i++ ) {
            Detector detector = (Detector)detectors.get( i );
            detector.notifyTimeStepped();
        }
    }

    public void setAutoDetect( boolean selected ) {
        this.autodetect = selected;
    }

    public void enableAll() {
        for( int i = 0; i < detectors.size(); i++ ) {
            Detector detector = (Detector)detectors.get( i );
            detector.setEnabled( true );
        }
    }

    public void removeDetector( Detector detector ) {
        detectors.remove( detector );
    }

    public class MyListener extends DiscreteModel.Adapter {
        public void finishedTimeStep( DiscreteModel model ) {
            notifyTimeStepped();
            updateDetectorProbabilities();
            if( autodetect && model.isDetectionCausesCollapse() ) {
                fireAllEnabledDetectors( new FireWhenReady() );
            }
        }
    }

}
