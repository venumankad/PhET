// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.graphtheline;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.SlopeDragHandler;
import edu.colorado.phet.linegraphing.common.view.X1Y1DragHandler;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.graphtheline.GTL_Challenge;
import edu.colorado.phet.linegraphing.linegame.view.GameConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * View component for a "Graph the Line" (GTL) challenge.
 * Given an equation in point-slope (PS) form, graph the line by manipulating the Point and Slope.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GTL_PS_PointSlope_ChallengeNode extends GTL_PS_ChallengeNode {

    public GTL_PS_PointSlope_ChallengeNode( final LineGameModel model, GTL_Challenge challenge, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, challenge, audioPlayer, challengeSize );
    }

    // Creates the graph portion of the view.
    @Override protected GTL_GraphNode createGraphNode( GTL_Challenge challenge ) {
        return new ThisGraphNode( challenge );
    }

    // Graph for this challenge
    private static class ThisGraphNode extends GTL_PS_GraphNode {

        private final LineNode answerNode;
        private final LineManipulatorNode pointManipulatorNode, slopeManipulatorNode;

        public ThisGraphNode( final GTL_Challenge challenge ) {
            super( challenge );

            // parent for the guess node, to maintain rendering order
            final PNode guessNodeParent = new PComposite();

            // the correct answer, initially hidden
            answerNode = createAnswerLineNode( challenge.answer, challenge.graph, challenge.mvt );
            answerNode.setEquationVisible( false );
            answerNode.setVisible( false );

            // dynamic ranges
            final Property<DoubleRange> x1Range = new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) );
            final Property<DoubleRange> y1Range = new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) );
            final Property<DoubleRange> riseRange = new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) );
            final Property<DoubleRange> runRange = new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) );

            final double manipulatorDiameter = challenge.mvt.modelToViewDeltaX( GameConstants.MANIPULATOR_DIAMETER );

            // point manipulator
            pointManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_X1_Y1 );
            pointManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                             pointManipulatorNode, challenge.mvt, challenge.guess, x1Range, y1Range,
                                                                             true /* constantSlope */ ) );

            // slope manipulator
            slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE );
            slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                              slopeManipulatorNode, challenge.mvt, challenge.guess, riseRange, runRange ) );

            // Rendering order
            addChild( guessNodeParent );
            addChild( answerNode );
            addChild( pointManipulatorNode );
            addChild( slopeManipulatorNode );

            // Show the user's current guess
            challenge.guess.addObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {

                    // draw the line
                    guessNodeParent.removeAllChildren();
                    LineNode guessNode = createGuessLineNode( line, challenge.graph, challenge.mvt );
                    guessNode.setEquationVisible( false );
                    guessNodeParent.addChild( guessNode );

                    // move the manipulators
                    pointManipulatorNode.setOffset( challenge.mvt.modelToView( line.x1, line.y1 ) );
                    slopeManipulatorNode.setOffset( challenge.mvt.modelToView( line.x2, line.y2 ) );

                    //TODO this was copied from LineFormsModel constructor
                    // range of the graph axes
                    final int xMin = challenge.graph.xRange.getMin();
                    final int xMax = challenge.graph.xRange.getMax();
                    final int yMin = challenge.graph.yRange.getMin();
                    final int yMax = challenge.graph.yRange.getMax();

                    //TODO this was copied from LineFormsModel constructor
                    // x1
                    final double x1Min = Math.max( xMin, xMin - line.run );
                    final double x1Max = Math.min( xMax, xMax - line.run );
                    x1Range.set( new DoubleRange( x1Min, x1Max ) );

                    //TODO this was copied from LineFormsModel constructor
                    // y1
                    final double y1Min = Math.max( yMin, yMin - line.rise );
                    final double y1Max = Math.min( yMax, yMax - line.rise );
                    y1Range.set( new DoubleRange( y1Min, y1Max ) );

                    //TODO this was copied from LineFormsModel constructor
                    // rise
                    final double riseMin = yMin - line.y1;
                    final double riseMax = yMax - line.y1;
                    riseRange.set( new DoubleRange( riseMin, riseMax ) );

                    //TODO this was copied from LineFormsModel constructor
                    // run
                    final double runMin = xMin - line.x1;
                    final double runMax = xMax - line.x1;
                    runRange.set( new DoubleRange( runMin, runMax ) );
                }
            } );
        }

        // Sets the visibility of the correct answer. When answer is visible, manipulators are hidden.
        public void setAnswerVisible( boolean visible ) {
            answerNode.setVisible( visible );
        }
    }
}
