/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.KirkhoffListener;
import edu.colorado.phet.cck3.circuit.PathBranch;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 13, 2004
 * Time: 8:06:21 PM
 * Copyright (c) Jun 13, 2004 by Sam Reid
 */
public class Filament extends PathBranch {
    Junction shellJunction;
    Junction tailJunction;
    int numPeaks;
    double resistorDY;//the pin is the assumed origin.
    double resistorWidth;
    private double zigHeight;
    private AbstractVector2D northDir;
    private AbstractVector2D eastDir;
    private Point2D pin;

    public Filament( KirkhoffListener kl, Junction tailJunction, Junction shellJunction, int numPeaks, double pivotToResistorDY, double resistorWidth, double zigHeight ) {
        super( kl, tailJunction, shellJunction );
        this.shellJunction = shellJunction;
        this.tailJunction = tailJunction;
        this.numPeaks = numPeaks;
        this.resistorDY = pivotToResistorDY;
        this.resistorWidth = resistorWidth;
        this.zigHeight = zigHeight;
        recompute();
    }

    public void setStartJunction( Junction newJunction ) {
        super.setStartJunction( newJunction );
        this.tailJunction = newJunction;
        recompute();
    }

    public void setEndJunction( Junction newJunction ) {
        super.setEndJunction( newJunction );
        this.shellJunction = newJunction;
        recompute();
    }

    private Point2D getPoint( double east, double north ) {
        AbstractVector2D e = eastDir.getScaledInstance( east );
        AbstractVector2D n = northDir.getScaledInstance( north );
        AbstractVector2D sum = e.getAddedInstance( n );
        return sum.getDestination( pin );
    }

    private AbstractVector2D getVector( double east, double north ) {
        AbstractVector2D e = eastDir.getScaledInstance( east );
        AbstractVector2D n = northDir.getScaledInstance( north );
        AbstractVector2D sum = e.getAddedInstance( n );
        return sum;
    }

    public GeneralPath getPath() {
        DoubleGeneralPath path = new DoubleGeneralPath( segmentAt( 0 ).getStart() );
        for( int i = 0; i < numSegments(); i++ ) {
            Segment seg = segmentAt( i );
            path.lineTo( seg.getEnd() );
        }
        return path.getGeneralPath();
    }

    boolean isNaN( AbstractVector2D vector ) {
        return Double.isNaN( vector.getX() ) || Double.isNaN( vector.getY() );
    }

    public void recompute() {
        northDir = new Vector2D.Double( tailJunction.getPosition(), shellJunction.getPosition() ).getNormalizedInstance();
        eastDir = northDir.getNormalVector().getNormalizedInstance();
        if( isNaN( northDir ) || isNaN( eastDir ) ) {
            System.out.println( "Bulb basis set is not a number." );
            return;
        }
        pin = shellJunction.getPosition();

        Point2D pt = getPoint( -resistorWidth / 2, resistorDY );
        if( Double.isNaN( pt.getX() ) || Double.isNaN( pt.getY() ) ) {
            throw new RuntimeException( "Point was nan: " + pt );
        }
        super.reset( tailJunction.getPosition(), pt );
//        super.addPoint( pt );

        //zig zags go here.
        int numQuarters = ( numPeaks - 1 ) * 4 + 2;
        double numWaves = numQuarters / 4.0;
        double wavelength = resistorWidth / numWaves;
        double quarterWavelength = wavelength / 4.0;
        double halfWavelength = wavelength / 2.0;
        addPoint( getVector( quarterWavelength, zigHeight ) );
        for( int i = 0; i < numPeaks - 1; i++ ) {
            addPoint( getVector( halfWavelength, -2 * zigHeight ) );
            addPoint( getVector( halfWavelength, 2 * zigHeight ) );
        }
        addPoint( getVector( quarterWavelength, -zigHeight ) );

//        addPoint( getPoint( resistorWidth / 2, resistorDY ) );
        addPoint( pin );
    }

    public boolean isHiddenBranch( Location loc ) {
        Segment seg = loc.getSegment();
        int index = indexOf( seg );
        if( index == 0 || index == 1 ) {
            return true;
        }
        return false;
    }

    private int indexOf( Segment seg ) {
        return segments.indexOf( seg );
    }
}
