// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.linegraphing.LGColors;
import edu.colorado.phet.linegraphing.LGConstants;
import edu.colorado.phet.linegraphing.common.view.LGCanvas;
import edu.colorado.phet.linegraphing.intro.model.IntroModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Intro" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroCanvas extends LGCanvas implements Resettable {

    private final Property<Boolean> linesVisible = new Property<Boolean>( true );
    private final Property<Boolean> interactiveLineVisible = new Property<Boolean>( true );
    private final Property<Boolean> interactiveEquationVisible = new Property<Boolean>( true );
    private final Property<Boolean> slopeVisible = new Property<Boolean>( true );

    private final LineGraphNode graphNode;

    public IntroCanvas( final IntroModel model ) {
        setBackground( new Color( 255, 255, 225 ) );

        graphNode = new LineGraphNode( model.graph, model.mvt, model.interactiveLine, model.savedLines, model.standardLines,
                                       IntroModel.RISE_RANGE, IntroModel.RUN_RANGE, IntroModel.INTERCEPT_RANGE,
                                       interactiveEquationVisible, linesVisible, interactiveLineVisible, slopeVisible );
        PNode pointTool1 = new PointToolNode( model.pointTool1, model.mvt, model.graph, getStageBounds(), linesVisible );
        PNode pointTool2 = new PointToolNode( model.pointTool2, model.mvt, model.graph, getStageBounds(), linesVisible );
        EquationControls equationControls = new EquationControls( interactiveEquationVisible, model.interactiveLine,
                                                                  IntroModel.RISE_RANGE, IntroModel.RUN_RANGE, IntroModel.INTERCEPT_RANGE,
                                                                  model.savedLines, linesVisible );
        PNode graphControls = new GraphControls( linesVisible, slopeVisible, model.standardLines );
        PNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, null, LGConstants.CONTROL_FONT_SIZE, Color.BLACK, LGColors.RESET_ALL_BUTTON ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( graphNode );
            addChild( equationControls );
            addChild( graphControls );
            addChild( resetAllButtonNode );
            addChild( pointTool1 );
            addChild( pointTool2 );
        }

        // layout
        {
            // NOTE: Nodes that have corresponding model elements handle their own offsets.
            final double yMargin = 20;

            // upper-right of graph
            equationControls.setOffset( graphNode.getFullBoundsReference().getMaxX(), 35 );
            // centered below equation
            graphControls.setOffset( equationControls.getFullBoundsReference().getCenterX() - ( graphControls.getFullBoundsReference().getWidth() / 2 ),
                                     equationControls.getFullBoundsReference().getMaxY() + 25 );
            // buttons centered below control panel
            resetAllButtonNode.setOffset( graphControls.getFullBoundsReference().getCenterX() - ( resetAllButtonNode.getFullBoundsReference().getWidth() / 2 ),
                                          getStageSize().getHeight() - yMargin - resetAllButtonNode.getFullBoundsReference().getHeight() );
        }
    }

    public void reset() {
        interactiveLineVisible.reset();
        linesVisible.reset();
        slopeVisible.reset();
    }
}
