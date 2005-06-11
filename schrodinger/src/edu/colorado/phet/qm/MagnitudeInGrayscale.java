/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 2:04:07 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */
public class MagnitudeInGrayscale implements ColorMap {
    private SchrodingerPanel schrodingerPanel;
    public double colorScale;
    public double intensityScale;

    public MagnitudeInGrayscale( SchrodingerPanel schrodingerPanel ) {
        this.schrodingerPanel = schrodingerPanel;
        intensityScale = 50;
        colorScale = 20;
    }

    public Paint getPaint( int i, int k ) {
        Complex[][] wavefunction = schrodingerPanel.getDiscreteModel().getWavefunction();
        double abs = wavefunction[i][k].abs() * intensityScale;
        if( abs > 1 ) {
            abs = 1;
        }
        Color color = new Color( (float)abs, (float)abs, (float)abs );
        double potval = getPotential().getPotential( i, k, 0 );
        if( potval > 0 ) {
            color = new Color( 100, color.getGreen(), color.getBlue() );
        }
        return color;
//        double h = Math.abs( wavefunction[i][k].getReal() ) * colorScale;
//        double s = Math.abs( wavefunction[i][k].getImaginary() ) * colorScale;
//        double b = getBrightness( wavefunction[i][k].abs() );
//        if( h > 1 ) {
//            h = 1;
//        }
//        if( s > 1 ) {
//            s = 1;
//        }
//        Color color = new Color( Color.HSBtoRGB( (float)h, (float)s, (float)b ) );
//        double potval = schrodingerPanel.getDiscreteModel().getPotential().getPotential( i, k, 0 );
//        if( potval > 0 ) {
//            color = new Color( 100, color.getGreen(), color.getBlue() );
//        }
//        return color;
    }

    protected double getBrightness( double x ) {
        double b = x * intensityScale;
        if( b > 1 ) {
            b = 1;
        }
        return b;
    }

    public Potential getPotential() {
        return schrodingerPanel.getDiscreteModel().getPotential();
    }
}
