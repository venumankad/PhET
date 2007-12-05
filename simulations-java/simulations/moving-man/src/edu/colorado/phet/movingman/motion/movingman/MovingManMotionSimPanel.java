package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.colorado.phet.common.timeseries.ui.TimeSeriesControlPanel;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:44:35 PM
 */
public class MovingManMotionSimPanel extends BufferedPhetPCanvas {
    private MovingManNode movingManNode;
    private GraphSetNode graphSetNode;

    public MovingManMotionSimPanel( MovingManMotionModel manMotionModel ) {
        final SingleBodyMotionModel motionModel = manMotionModel.getMotionModel();
        movingManNode = new MovingManNode( motionModel );
        addScreenChild( movingManNode );
        ControlGraphSeries[] s = manMotionModel.getControlGraphSeriesArray();
        for ( int i = 0; i < s.length; i++ ) {
            ControlGraphSeries controlGraphSeries = s[i];
            MovingManGraph graph = new MovingManGraph(
                    this, controlGraphSeries, controlGraphSeries.getTitle(), -10, 10,
                    motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getPositionDriven(), MovingManMotionModel.MAX_T, motionModel );
        }

        MovingManGraph xGraph = new MovingManGraph(
                this, manMotionModel.getXSeries(), "x", -10, 10,
                motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getPositionDriven(), MovingManMotionModel.MAX_T, motionModel );

        MovingManGraph vGraph = new MovingManGraph(
                this, manMotionModel.getVSeries(), "x", -0.1, 0.1,
                motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getVelocityDriven(), MovingManMotionModel.MAX_T, motionModel );

        MovingManGraph aGraph = new MovingManGraph(
                this, manMotionModel.getASeries(), "x", -0.01, 0.01,
                motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getAccelDriven(), MovingManMotionModel.MAX_T, motionModel );

        graphSetNode = new GraphSetNode( new GraphSetModel( new GraphSuite( new MinimizableControlGraph[]{
                new MinimizableControlGraph( SimStrings.get( "variables.position.abbreviation" ), xGraph ),
                new MinimizableControlGraph( SimStrings.get( "variables.velocity.abbreviation" ), vGraph ),
                new MinimizableControlGraph( SimStrings.get( "variables.acceleration.abbreviation" ), aGraph )
        } ) ) );

        graphSetNode.setAlignedLayout();
        addScreenChild( graphSetNode );
        requestFocus();
        addKeyListener( new PDebugKeyHandler() );

        TimeSeriesControlPanel timeControlPanel = new TimeSeriesControlPanel( motionModel.getTimeSeriesModel(), 0.1, 1.0 );
        add( timeControlPanel, BorderLayout.SOUTH );
        updateLayout();
    }

    protected void updateLayout() {
        super.updateLayout();
        movingManNode.setTransform( 22.0, getWidth() );

        int insetX = 2;
        graphSetNode.setBounds( insetX, movingManNode.getFullBounds().getMaxY(), getWidth() - 2 * insetX, getHeight() - movingManNode.getFullBounds().getMaxY() );
    }
}
