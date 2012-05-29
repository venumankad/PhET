// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.EquationNodeFactory;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Factory that creates a node for displaying a point-slope equation in reduced form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PointSlopeEquationFactory extends EquationNodeFactory {

    public EquationNode createNode( StraightLine line, PhetFont font ) {
        if ( MathUtil.round( line.run ) == 0 ) {
            return new SlopeUndefinedNode( line, font );
        }
        else {
            return new VerboseNode( line, font ); //TODO this is for debugging, remove when other reductions are implemented
        }
//        else if ( MathUtil.round( line.rise ) == 0 ) {
//            return new SlopeZeroNode( line, font );
//        }
//        else if ( Math.abs( line.getReducedRise() ) == Math.abs( line.getReducedRun() ) ) {
//            return new SlopeOneNode( line, font );
//        }
//        else if ( Math.abs( line.getReducedRun() ) == 1 ) {
//            return new SlopeIntegerNode( line, font );
//        }
//        else {
//            return new SlopeFractionFraction( line, font );
//        }
    }

    // Verbose form of point-slope, not reduced, for debugging.
    private static class VerboseNode extends ReducedEquationNode {
        public VerboseNode( StraightLine line, PhetFont font ) {
            addChild( new PhetPText( MessageFormat.format( "(y - {0}) = ({1}/{2})(x - {3})", line.y1, line.rise, line.run, line.x1 ), font, line.color ) );
        }
    }

    //TODO this is slope-intercept, change to point-slope
    /*
     * Forms when slope is zero.
     * y = b
     * y = -b
     */
    private static class SlopeZeroNode extends ReducedEquationNode {

        public SlopeZeroNode( StraightLine line, PhetFont font ) {

            // y = b
            PText yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PText equalsNode = new PhetPText( "=", font, line.color );
            PText interceptNode = new PhetPText( String.valueOf( MathUtil.round( line.yIntercept ) ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( interceptNode );

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getWidth() + X_SPACING, yNode.getYOffset() );
            interceptNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
        }
    }

    //TODO this is slope-intercept, change to point-slope
    /*
     * Forms where abs slope is 1.
     * y = x
     * y = -x
     * y = x + b
     * y = x - b
     * y = -x + b
     * y = -x - b
    */
    private static class SlopeOneNode extends ReducedEquationNode {

        public SlopeOneNode( StraightLine line, PhetFont font ) {

            final boolean slopeIsPositive = ( line.rise * line.run ) >= 0;

            // y = x + b
            PText yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PText equalsNode = new PhetPText( "=", font, line.color );
            PText xNode = new PhetPText( slopeIsPositive ? Strings.SYMBOL_X : "-" + Strings.SYMBOL_X, font, line.color );
            PText interceptSignNode = new PhetPText( line.yIntercept > 0 ? "+" : "-", font, line.color );
            PText interceptNode = new PhetPText( String.valueOf( MathUtil.round( Math.abs( line.yIntercept ) ) ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( xNode );
            if ( line.yIntercept != 0 ) {
                addChild( interceptSignNode );
                addChild( interceptNode );
            }

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getWidth() + X_SPACING, yNode.getYOffset() );
            xNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
            interceptSignNode.setOffset( xNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
            interceptNode.setOffset( interceptSignNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
        }
    }

    //TODO this is slope-intercept, change to point-slope
    /*
     * Forms where the slope is an integer.
     * y = rise x
     * y = -rise x
     * y = rise x + b
     * y = rise x - b
     * y = -rise x + b
     * y = -rise x - b
     */
    private static class SlopeIntegerNode extends ReducedEquationNode {

        public SlopeIntegerNode( StraightLine line, PhetFont font ) {

            // y = rise x + b
            PText yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PText equalsNode = new PhetPText( "=", font, line.color );
            PText riseNode = new PhetPText( String.valueOf( line.getReducedRise() / line.getReducedRun() ), font, line.color );
            PText xNode = new PhetPText( Strings.SYMBOL_X, font, line.color );
            PText signNode = new PhetPText( line.yIntercept > 0 ? "+" : "-", font, line.color );
            PText interceptNode = new PhetPText( String.valueOf( MathUtil.round( Math.abs( line.yIntercept ) ) ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( riseNode );
            addChild( xNode );
            if ( line.yIntercept != 0 ) {
                addChild( signNode );
                addChild( interceptNode );
            }

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getWidth() + X_SPACING, yNode.getYOffset() );
            riseNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
            xNode.setOffset( riseNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
            signNode.setOffset( xNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
            interceptNode.setOffset( signNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
        }
    }

    //TODO this is slope-intercept, change to point-slope
    /*
    * Forms where the slope is a fraction.
    * y = (rise/run) x + b
    * y = (rise/run) x - b
    * y = -(rise/run) x + b
    * y = -(rise/run) x - b
    */
    private static class SlopeFractionFraction extends ReducedEquationNode {

        public SlopeFractionFraction( StraightLine line, PhetFont font ) {

            final int reducedRise = Math.abs( line.getReducedRise() );
            final int reducedRun = Math.abs( line.getReducedRun() );
            final boolean slopeIsPositive = ( line.rise * line.run ) >= 0;

            // y = -(reducedRise/reducedRun)x + b
            PText yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PText equalsNode = new PhetPText( "=", font, line.color );
            PText slopeSignNode = new PhetPText( slopeIsPositive ? "" : "-", font, line.color );
            PText riseNode = new PhetPText( String.valueOf( Math.abs( reducedRise ) ), font, line.color );
            PText runNode = new PhetPText( String.valueOf( Math.abs( reducedRun ) ), font, line.color );
            PPath lineNode = new PhetPPath( new Line2D.Double( 0, 0, Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getHeight() ), 0 ), new BasicStroke( 1f ), line.color );
            PText xNode = new PhetPText( Strings.SYMBOL_X, font, line.color );
            PText interceptSignNode = new PhetPText( line.yIntercept > 0 ? "+" : "-", font, line.color );
            PText interceptNode = new PhetPText( String.valueOf( MathUtil.round( Math.abs( line.yIntercept ) ) ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            if ( !slopeIsPositive ) {
                addChild( slopeSignNode );
            }
            addChild( riseNode );
            addChild( lineNode );
            addChild( runNode );
            addChild( xNode );
            if ( line.yIntercept != 0 ) {
                addChild( interceptSignNode );
                addChild( interceptNode );
            }

            // layout
            final double yFudgeFactor = 2; // fudge factor to align fraction dividing line with the center of the equals sign, visually tweaked
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getWidth() + X_SPACING, yNode.getYOffset() );
            if ( !slopeIsPositive ) {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                         equalsNode.getYOffset() - ( slopeSignNode.getFullBoundsReference().getHeight() / 2 ) + yFudgeFactor );
                lineNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) + yFudgeFactor );
            }
            else {
                lineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) + yFudgeFactor );
            }
            riseNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - Y_SPACING );
            runNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                               lineNode.getFullBoundsReference().getMaxY() + Y_SPACING );
            xNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + X_SPACING,
                             equalsNode.getYOffset() );
            interceptSignNode.setOffset( xNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                         equalsNode.getYOffset() );
            interceptNode.setOffset( interceptSignNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                     equalsNode.getYOffset() );
        }
    }
}
