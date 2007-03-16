package edu.colorado.phet.energyskatepark.test.apptests;

import edu.colorado.phet.energyskatepark.EC3LookAndFeel;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.test.phys1d.CubicSpline2D;
import edu.colorado.phet.energyskatepark.model.spline.CubicSpline;
import edu.colorado.phet.energyskatepark.model.spline.SplineSurface;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;

/**
 * User: Sam Reid
 * Date: Oct 9, 2006
 * Time: 11:42:52 PM
 * Copyright (c) Oct 9, 2006 by Sam Reid
 */

public class TestLoop {
    private EnergySkateParkApplication energySkateParkApplication;

    public TestLoop( String[] args ) {
        energySkateParkApplication = new EnergySkateParkApplication( args );
    }

    public static void main( String[] args ) {
        EnergySkateParkStrings.init( args, "localization/EnergySkateParkStrings" );
        new EC3LookAndFeel().initLookAndFeel();
        new TestLoop( args ).start();
    }

    private void start() {
        energySkateParkApplication.startApplication();
        energySkateParkApplication.getModule().getEnergySkateParkModel().removeAllSplineSurfaces();
        CubicSpline spline = new CubicSpline();
//add control points
        spline.addControlPoint( 2.856047700170355, 6.399488926746162 );
        spline.addControlPoint( 7.202725724020448, 0.6311754684838161 );
        spline.addControlPoint( 11.856047955241685, 2.3107228886329763 );
        spline.addControlPoint( 10.617640530839696, 4.842325397610053 );
        spline.addControlPoint( 8.365511016504522, 4.101089191193068 );
        spline.addControlPoint( 9.250519214740605, 1.213525333233952 );
        spline.addControlPoint( 11.60145654446832, 0.43431858330000445 );
        spline.addControlPoint( 13.62360305213442, 1.073160150591316 );

        energySkateParkApplication.getModule().getEnergySkateParkModel().addSplineSurface( new EnergySkateParkSpline( new CubicSpline2D( spline.getControlPoints( )) ) );
    }
}
