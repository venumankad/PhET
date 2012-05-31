// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Line2D;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode.RiseSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode.RunSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode.X1SpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode.Y1SpinnerNode;
import edu.colorado.phet.linegraphing.pointslope.model.PointSlopeModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Interface for manipulating a point-slope equation.
 * This version uses spinner buttons to increment/decrement rise, run, x1 and y1.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PointSlopeEquationNodeSpinners extends PhetPNode {

    private static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    private final Property<Double> rise, run, x1, y1;

    public PointSlopeEquationNodeSpinners( final Property<StraightLine> interactiveLine,
                                           Property<DoubleRange> riseRange,
                                           Property<DoubleRange> runRange,
                                           Property<DoubleRange> x1Range,
                                           Property<DoubleRange> y1Range,
                                           PhetFont font ) {

        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );
        this.x1 = new Property<Double>( interactiveLine.get().x1 );
        this.y1 = new Property<Double>( interactiveLine.get().y1 );

        // determine the max width of the rise and run spinners, based on the extents of their range
        double maxSlopeWidth;
        {
            PNode maxRiseNode = new RiseSpinnerNode( UserComponents.riseSpinner, new Property<Double>( riseRange.get().getMax() ), riseRange, font, FORMAT );
            PNode minRiseNode = new RiseSpinnerNode( UserComponents.riseSpinner, new Property<Double>( riseRange.get().getMin() ), riseRange, font, FORMAT );
            double maxRiseWidth = Math.max( maxRiseNode.getFullBoundsReference().getWidth(), minRiseNode.getFullBoundsReference().getWidth() );
            PNode maxRunNode = new RunSpinnerNode( UserComponents.riseSpinner, new Property<Double>( runRange.get().getMax() ), runRange, font, FORMAT );
            PNode minRunNode = new RunSpinnerNode( UserComponents.riseSpinner, new Property<Double>( runRange.get().getMin() ), runRange, font, FORMAT );
            double maxRunWidth = Math.max( maxRunNode.getFullBoundsReference().getWidth(), minRunNode.getFullBoundsReference().getWidth() );
            maxSlopeWidth = Math.max( maxRiseWidth, maxRunWidth );
        }

        // (y-y1) = m(x-x1)
        PText yNode = new PhetPText( "y", font );
        final PText y1SignNode = new PhetPText( "-", font );
        PNode y1Node = new ZeroOffsetNode( new Y1SpinnerNode( UserComponents.y1Spinner, this.y1, y1Range, font, FORMAT ) );
        PText equalsNode = new PhetPText( "=", font );
        PNode riseNode = new ZeroOffsetNode( new RiseSpinnerNode( UserComponents.riseSpinner, this.rise, riseRange, font, FORMAT ) );
        PNode runNode = new ZeroOffsetNode( new RunSpinnerNode( UserComponents.runSpinner, this.run, runRange, font, FORMAT ) );
        final PPath lineNode = new PPath( new Line2D.Double( 0, 0, maxSlopeWidth, 0 ) ) {{
            setStroke( new BasicStroke( 3f ) );
        }};
        PText xNode = new PhetPText( "x", font );
        final PText x1SignNode = new PhetPText( "-", font );
        PNode x1Node = new ZeroOffsetNode( new X1SpinnerNode( UserComponents.x1Spinner, this.x1, x1Range, font, FORMAT ) );
        PText yLeftParenNode = new PhetPText( "(", font );
        PText yRightParenNode = new PhetPText( ")", font );
        PText xLeftParenNode = new PhetPText( "(", font );
        PText xRightParenNode = new PhetPText( ")", font );

        // rendering order
        {
            addChild( yLeftParenNode );
            addChild( yNode );
            addChild( y1SignNode );
            addChild( y1Node );
            addChild( yRightParenNode );
            addChild( equalsNode );
            addChild( riseNode );
            addChild( lineNode );
            addChild( runNode );
            addChild( xLeftParenNode );
            addChild( xNode );
            addChild( x1SignNode );
            addChild( x1Node );
            addChild( xRightParenNode );
        }

        // layout
        {
            // NOTE: x spacing varies and was tweaked to look good
            final double ySpacing = 6;
            yLeftParenNode.setOffset( 0, 0 );
            yNode.setOffset( yLeftParenNode.getFullBoundsReference().getMaxX() + 2,
                             yLeftParenNode.getYOffset() );
            y1SignNode.setOffset( yNode.getFullBoundsReference().getMaxX() + 5,
                                  yNode.getYOffset() );
            y1Node.setOffset( y1SignNode.getFullBoundsReference().getMaxX() + 5,
                                  yNode.getFullBoundsReference().getCenterY() - ( y1Node.getFullBoundsReference().getHeight() / 2 ) );
            yRightParenNode.setOffset( y1Node.getFullBoundsReference().getMaxX() + 2,
                                       yNode.getYOffset() );
            equalsNode.setOffset( yRightParenNode.getFullBoundsReference().getMaxX() + 10,
                                  yNode.getYOffset() );
            lineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + 10,
                                equalsNode.getFullBoundsReference().getCenterY() );
            riseNode.setOffset( lineNode.getFullBoundsReference().getMaxX() - riseNode.getFullBoundsReference().getWidth(),
                                lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
            runNode.setOffset( lineNode.getFullBoundsReference().getMaxX() - runNode.getFullBoundsReference().getWidth(),
                               lineNode.getFullBoundsReference().getMinY() + ySpacing );
            xLeftParenNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + 2,
                                      yNode.getYOffset() );
            xNode.setOffset( xLeftParenNode.getFullBoundsReference().getMaxX() + 2,
                             yNode.getYOffset() );
            x1SignNode.setOffset( xNode.getFullBoundsReference().getMaxX() + 10,
                                  xNode.getYOffset() );
            x1Node.setOffset( x1SignNode.getFullBoundsReference().getMaxX() + 5,
                              xNode.getFullBoundsReference().getCenterY() - ( x1Node.getFullBoundsReference().getHeight() / 2 ) );
            xRightParenNode.setOffset( x1Node.getFullBoundsReference().getMaxX() + 2,
                                      yNode.getYOffset() );
        }

        // sync the model with the controls
        RichSimpleObserver lineUpdater = new RichSimpleObserver() {
            @Override public void update() {
                interactiveLine.set( new StraightLine( rise.get(), run.get(), x1.get(), y1.get(), interactiveLine.get().color, interactiveLine.get().highlightColor ) );
            }
        };
        lineUpdater.observe( rise, run, x1, y1 );

        // sync the controls with the model
        interactiveLine.addObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {
                rise.set( line.rise );
                run.set( line.run );
                x1.set( line.x1 );
                y1.set( line.y1 );
            }
        } );
    }

    // test
    public static void main( String[] args ) {

        // model
        PointSlopeModel model = new PointSlopeModel();

        // equation
        PointSlopeEquationNodeSpinners equationNode =
                new PointSlopeEquationNodeSpinners( model.interactiveLine, model.riseRange, model.runRange, model.x1Range, model.y1Range, new PhetFont( Font.BOLD, 38 ) );
        equationNode.setOffset( 50, 100 );

        // canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 600, 400 ) );
        canvas.getLayer().addChild( equationNode );

        // frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}