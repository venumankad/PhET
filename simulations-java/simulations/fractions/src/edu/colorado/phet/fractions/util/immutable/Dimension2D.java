// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.util.immutable;

import lombok.Data;

import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;

/**
 * Immutable class for a dimension (width x height)
 *
 * @author Sam Reid
 */
public @Data class Dimension2D {
    public final double width;
    public final double height;

    public Dimension2DDouble toDimension2DDouble() { return new Dimension2DDouble( width, height ); }
}