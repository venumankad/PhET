/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.colormaps;

import edu.colorado.phet.qm.model.QWISplitModel;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.modules.intensity.HighIntensitySchrodingerPanel;
import edu.colorado.phet.qm.view.colorgrid.ColorMap;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 8:43:06 PM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SplitColorMap implements ColorMap {
    private QWISplitModel QWISplitModel;
    private ColorData rootColor;
    private double intensityScale = 20;
    private WaveValueAccessor waveValueAccessor = new WaveValueAccessor.Magnitude();

    public SplitColorMap( QWISplitModel QWISplitModel, HighIntensitySchrodingerPanel schrodingerPanelHigh ) {
        this.QWISplitModel = QWISplitModel;
        setRootColor( schrodingerPanelHigh.getRootColor() );
    }

    public Paint getColor( int i, int k ) {
        Rectangle[] areas = QWISplitModel.getDoubleSlitPotential().getSlitAreas();
        double abs = 0;
        if( contains( areas, i, k ) ) {
            abs += getValue( QWISplitModel.getLeftWavefunction(), i, k );
            abs += getValue( QWISplitModel.getRightWavefunction(), i, k );
        }
        else {
            abs += getValue( QWISplitModel.getLeftWavefunction(), i, k );
            abs += getValue( QWISplitModel.getRightWavefunction(), i, k );
            abs += getValue( QWISplitModel.getWavefunction(), i, k );       //relying on code elsewhere to zero out the other complementary parts of these waves
        }
        if( abs > 1 ) {
            abs = 1;
        }
        if( rootColor != null ) {
            return rootColor.toColor( abs );
        }
        else {
            return new Color( (float)abs, (float)abs, (float)abs );
        }
    }

    private boolean contains( Rectangle[] areas, int i, int k ) {
        for( int j = 0; j < areas.length; j++ ) {
            Rectangle area = areas[j];
            if( area.contains( i, k ) ) {
                return true;
            }
        }
        return false;
    }

    public WaveValueAccessor getWaveValueAccessor() {
        return waveValueAccessor;
    }

    public void setWaveValueAccessor( WaveValueAccessor waveValueAccessor ) {
        this.waveValueAccessor = waveValueAccessor;
        System.out.println( "SplitColorMap.setWaveValueAccessor, wva=" + waveValueAccessor );
    }

    private double getValue( Wavefunction wavefunction, int i, int k ) {
        if( wavefunction.containsLocation( i, k ) ) {
            return waveValueAccessor.getValue( wavefunction, i, k ) * intensityScale;
        }
        else {
            return 0;
        }
    }

    public void setRootColor( ColorData rootColor ) {
        this.rootColor = rootColor;
    }

}
