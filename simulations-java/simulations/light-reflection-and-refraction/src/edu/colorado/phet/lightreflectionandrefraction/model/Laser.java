// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.lightreflectionandrefraction.view.LaserColor;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;

/**
 * @author Sam Reid
 */
public class Laser {
    public final Property<Double> distanceFromOrigin;
    public final Property<Double> angle = new Property<Double>( Math.PI * 3 / 4 );
    public final Property<Boolean> on = new Property<Boolean>( false );
    public final Property<LaserColor> color = new Property<LaserColor>( new LaserColor.OneColor( LRRModel.WAVELENGTH_RED * 1E9 ) );

    public Laser( double distFromOrigin ) {
        this.distanceFromOrigin = new Property<Double>( distFromOrigin );
    }

    public ImmutableVector2D getEmissionPoint() {
        return parseAngleAndMagnitude( distanceFromOrigin.getValue(), angle.getValue() );
    }
}
