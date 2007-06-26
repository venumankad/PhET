package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartCursorNode;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.motion.model.ISimulationVariable;
import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.util.ArrayList;

/**
 * This subclass of ControlGraph is automatically connected to the supplied MotionModel
 * for update/notification messaging.
 *
 * @author Sam Reid
 */
public class MotionControlGraph extends ControlGraph {
    private ArrayList listeners = new ArrayList();
    private MotionModel motionModel;
    private JFreeChartCursorNode jFreeChartCursorNode;

    public MotionControlGraph( PhetPCanvas pSwingCanvas, final ISimulationVariable simulationVariable, String label, String title,
                               double min, double max, Color color, PNode thumb, final MotionModel motionModel,
                               boolean editable, TimeSeriesModel timeSeriesModel ) {
        this( pSwingCanvas, simulationVariable, label, title, min, max, color, thumb, motionModel, editable, timeSeriesModel, null );
    }

    public MotionControlGraph( PhetPCanvas pSwingCanvas, final ISimulationVariable simulationVariable, String label, String title,
                               double min, double max, Color color, PNode thumb, final MotionModel motionModel,
                               boolean editable, final TimeSeriesModel timeSeriesModel, final UpdateStrategy updateStrategy ) {
        this( pSwingCanvas, simulationVariable, label, title, min, max, color, thumb, motionModel, editable, timeSeriesModel, updateStrategy, 1000 );
    }

    public MotionControlGraph( PhetPCanvas pSwingCanvas, final ISimulationVariable simulationVariable, String label, String title,
                               double min, double max, Color color, PNode thumb, final MotionModel motionModel,
                               boolean editable, final TimeSeriesModel timeSeriesModel, final UpdateStrategy updateStrategy, int maxDomainValue ) {
        super( pSwingCanvas, simulationVariable, label, title, min, max, color, thumb, timeSeriesModel, maxDomainValue );
        this.motionModel = motionModel;
        addHorizontalZoomListener( new ZoomControlNode.ZoomListener() {
            public void zoomedOut() {
                notifyZoomChanged();
            }

            public void zoomedIn() {
                notifyZoomChanged();
            }
        } );
        setEditable( editable );

        jFreeChartCursorNode = new JFreeChartCursorNode( getJFreeChartNode() );
        addChild( jFreeChartCursorNode );
        timeSeriesModel.addPlaybackTimeChangeListener( new TimeSeriesModel.PlaybackTimeListener() {
            public void timeChanged() {
                jFreeChartCursorNode.setTime( timeSeriesModel.getTime() );
            }
        } );
        jFreeChartCursorNode.addListener( new JFreeChartCursorNode.Listener() {
            public void cursorTimeChanged() {
                timeSeriesModel.setPlaybackTime( jFreeChartCursorNode.getTime() );
            }
        } );
        motionModel.getTimeSeriesModel().addListener( new TimeSeriesModel.Adapter() {
            public void modeChanged() {
                updateCursorVisible();
            }

            public void pauseChanged() {
                updateCursorLocation();
                updateCursorVisible();
            }
        } );
        jFreeChartCursorNode.addListener( new JFreeChartCursorNode.Listener() {
            public void cursorTimeChanged() {
                motionModel.getTimeSeriesModel().setPlaybackMode();
                motionModel.getTimeSeriesModel().setPlaybackTime( jFreeChartCursorNode.getTime() );
                System.out.println( "playback time=" + jFreeChartCursorNode.getTime() );
            }
        } );
        motionModel.getTimeSeriesModel().addListener( new TimeSeriesModel.Adapter() {
            public void dataSeriesChanged() {
                jFreeChartCursorNode.setMaxDragTime( motionModel.getTimeSeriesModel().getRecordTime() );
                System.out.println( "max record time=" + motionModel.getTimeSeriesModel().getRecordTime() );
            }

            public void dataSeriesCleared() {
                clear();
            }
        } );
        updateCursorVisible();

//        motionModel.getTimeSeriesModel().addListener( new TimeSeriesModel.Adapter() {
//            public void dataSeriesChanged() {
//                addValue( motionModel.getTimeSeriesModel().getRecordTime(), simulationVariable.getValue() );
//            }
//        } );
        motionModel.addListener( getListener( simulationVariable ) );
        if( updateStrategy != null ) {
            addListener( new Adapter() {
                public void controlFocusGrabbed() {
                    motionModel.setUpdateStrategy( updateStrategy );
                }
            } );
        }
    }

    public void addSeries( final String title, Color color, String abbr, final ISimulationVariable simulationVariable ) {
        super.addSeries( title, color, abbr, simulationVariable );
        if( motionModel != null  ) {//main series handled in our constructor, this is for additional series.
            motionModel.addListener( getListener( simulationVariable ) );
        }
    }

    private MotionModel.Listener getListener( final ISimulationVariable simulationVariable ) {
        return new MotionModel.Listener() {
            public void steppedInTime() {
                TimeData timeData = simulationVariable.getData();
                addValue( getSeriesIndex( simulationVariable ), timeData.getTime(), timeData.getValue() );
            }
        };
    }

    private void updateCursorLocation() {
        jFreeChartCursorNode.setTime( motionModel.getTimeSeriesModel().getTime() );
    }

    private void updateCursorVisible() {
        jFreeChartCursorNode.setVisible( motionModel.getTimeSeriesModel().isPlaybackMode() || motionModel.getTimeSeriesModel().isPaused() );
    }

    public boolean hasListener( Listener listener ) {
        return listeners.contains( listener );
    }

    public static interface Listener {
        void horizontalZoomChanged( MotionControlGraph source );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void notifyZoomChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).horizontalZoomChanged( this );
        }
    }

}
