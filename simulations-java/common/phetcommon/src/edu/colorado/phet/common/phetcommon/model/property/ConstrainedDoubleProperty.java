// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.model.property;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * Double property that is constrained to a range.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConstrainedDoubleProperty extends ConstrainedProperty<Double> {

    private final double min, max;

    public ConstrainedDoubleProperty( String name, DoubleRange range ) {
        this( name, range.getMin(), range.getMax(), range.getDefault() );
    }

    public ConstrainedDoubleProperty( String name, double min, double max, double value ) {
        super( name, value );
        this.min = min;
        this.max = max;
        if ( !isValid( value ) ) {
            handleInvalidValue( value );
        }
    }

    @Override
    protected boolean isValid( Double value ) {
        return ( value >= min && value <= max );
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
