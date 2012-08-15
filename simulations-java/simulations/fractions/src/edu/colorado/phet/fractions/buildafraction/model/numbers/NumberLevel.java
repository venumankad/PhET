// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import fj.F;
import fj.data.List;

import edu.colorado.phet.fractions.buildafraction.model.Level;
import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.common.math.Fraction;

import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberTarget._mixedFraction;
import static edu.colorado.phet.fractions.common.math.Fraction.*;
import static fj.Ord.intOrd;

/**
 * Level for the build a fraction game.
 *
 * @author Sam Reid
 */
public class NumberLevel extends Level {

    //The numbers that the user can use, sorted in increasing order
    public final List<Integer> numbers;

    //The targets the user must match.
    public final List<NumberTarget> targets;

    public NumberLevel( final List<Integer> numbers, final List<NumberTarget> targets ) {
        super( targets.length() );
        this.numbers = numbers.sort( intOrd );
        this.targets = targets;
    }

    //Infer the number cards from the list of targets, using exactly what is necessary
    public NumberLevel( final List<NumberTarget> targets ) {
        this( targets.map( _mixedFraction ).map( MixedFraction._numerator ).append(
                targets.map( _mixedFraction ).map( MixedFraction._denominator ).append(
                        targets.map( _mixedFraction ).map( MixedFraction._whole ).filter( new F<Integer, Boolean>() {
                            @Override public Boolean f( final Integer integer ) {
                                return integer > 0;
                            }
                        } ) ) ), targets );
    }

    //Create a Number Level from the reduced fractions
    public static NumberLevel numberLevelReduced( final List<NumberTarget> targets ) {
        List<Fraction> reduced = targets.map( MixedFraction._fractionPart ).map( _reduce );
        List<Integer> cards = reduced.map( _numerator ).append( reduced.map( _denominator ) );
        return new NumberLevel( cards, targets );
    }

    public boolean hasValuesGreaterThanOne() {
        return targets.exists( new F<NumberTarget, Boolean>() {
            @Override public Boolean f( final NumberTarget numberTarget ) {
                return Fraction._greaterThanOne.f( numberTarget.mixedFraction.toFraction() );
            }
        } );
    }

    public boolean hasMixedNumbers() {
        return targets.exists( new F<NumberTarget, Boolean>() {
            @Override public Boolean f( final NumberTarget numberTarget ) {
                return numberTarget.mixedFraction.whole > 0;
            }
        } );
    }
}