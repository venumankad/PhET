package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.motion.MotionResources;
import edu.colorado.phet.common.motion.model.SimulationVariable;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 8:55:43 AM
 */

public class GraphControlsNode extends PNode {
    private PSwing goStopButton;
    private PSwing clearButton;
    private PNode seriesLayer = new PNode();
    private boolean editable = true;
    private boolean constructed = false;
    private TimeSeriesModel graphTimeSeries;

    public GraphControlsNode( TimeSeriesModel graphTimeSeries ) {
        this.graphTimeSeries = graphTimeSeries;
        addChild( seriesLayer );

        goStopButton = new PSwing( new GoStopButton( graphTimeSeries ) );
        addChild( goStopButton );

        clearButton = new PSwing( new ClearButton( graphTimeSeries ) );
        addChild( clearButton );

        constructed = true;
        relayout();
    }

    public GraphControlsNode( String title, String abbr, SimulationVariable simulationVariable, TimeSeriesModel graphTimeSeries ) {
        this( title, abbr, simulationVariable, graphTimeSeries, Color.black );
    }

    public GraphControlsNode( String title, String abbr, SimulationVariable simulationVariable, TimeSeriesModel graphTimeSeries, Color color ) {
        this( graphTimeSeries );
        addVariable( title, abbr, color, simulationVariable );
        relayout();
    }

    public void addVariable( String title, String abbr, Color color, SimulationVariable simulationVariable ) {
        SeriesNode seriesNode = new SeriesNode( title, abbr, color, simulationVariable );
        seriesNode.setEditable( editable );
        seriesNode.setOffset( 0, seriesLayer.getFullBounds().getHeight() + 5 );
        seriesLayer.addChild( seriesNode );
        relayout();
    }

    static class SeriesNode extends PNode {
        private ShadowPText shadowPText;
        private PSwing textBox;
        private TextBox box;

        public SeriesNode( String title, String abbr, Color color, SimulationVariable simulationVariable ) {
            shadowPText = new ShadowPText( title );
            shadowPText.setFont( new Font( "Lucida Sans", Font.BOLD, 16 ) );
            shadowPText.setTextPaint( color );
            shadowPText.setShadowColor( Color.black );
            addChild( shadowPText );

            box = new TextBox( abbr, simulationVariable );
            textBox = new PSwing( box );
            addChild( textBox );
        }

        public void relayout( double dy ) {
            shadowPText.setOffset( 0, 0 );
            textBox.setOffset( 0, shadowPText.getFullBounds().getMaxY() + dy );
        }

        public void setEditable( boolean editable ) {
            box.setEditable( editable );
        }
    }

    private void relayout() {
        if( constructed ) {
            double dy = 5;
            seriesLayer.setOffset( 0, 0 );
            for( int i = 0; i < seriesLayer.getChildrenCount(); i++ ) {
                SeriesNode child = (SeriesNode)seriesLayer.getChild( i );
                child.relayout( dy );
            }
            goStopButton.setOffset( 0, seriesLayer.getFullBounds().getMaxY() + dy );
            clearButton.setOffset( 0, goStopButton.getFullBounds().getMaxY() + dy );
        }
    }

    public void setEditable( boolean editable ) {
        this.editable = editable;
        for( int i = 0; i < seriesLayer.getChildrenCount(); i++ ) {
            SeriesNode child = (SeriesNode)seriesLayer.getChild( i );
            child.setEditable( editable );
        }
    }

    public static class ClearButton extends JButton {
        private TimeSeriesModel graphTimeSeries;

        public ClearButton( final TimeSeriesModel graphTimeSeries ) {
            super( "Clear" );
            this.graphTimeSeries = graphTimeSeries;
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    graphTimeSeries.clear();
                }
            } );
            graphTimeSeries.addListener( new TimeSeriesModel.Adapter() {
                public void dataSeriesChanged() {
                    updateEnabledState();
                }
            } );

            updateEnabledState();
        }

        private void updateEnabledState() {
            setEnabled( graphTimeSeries.isThereRecordedData() );
        }
    }

    public static class GoStopButton extends JButton {
        private boolean goButton = true;
        private TimeSeriesModel graphTimeSeries;

        public GoStopButton( final TimeSeriesModel graphTimeSeries ) {
            super( "Go" );
            this.graphTimeSeries = graphTimeSeries;
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( isGoButton() ) {
                        graphTimeSeries.startRecording();
                    }
                    else {
                        graphTimeSeries.setPaused( true );
                    }
                }
            } );
            graphTimeSeries.addListener( new TimeSeriesModel.Adapter() {

                public void modeChanged() {
                    updateGoState();
                }

                public void pauseChanged() {
                    updateGoState();
                }
            } );
            updateGoState();
        }

        private void updateGoState() {
            setGoButton( !graphTimeSeries.isRecording() );
        }

        private void setGoButton( boolean go ) {
            this.goButton = go;
            setText( goButton ? "Go!" : "Stop" );
            try {
                setIcon( new ImageIcon( MotionResources.loadBufferedImage( goButton ? "go.png" : "stop.png" ) ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }

        private boolean isGoButton() {
            return goButton;
        }
    }

    static class TextBox extends JPanel {
        private JTextField textField;
        private DecimalFormat decimalFormat = new DecimalFormat( "0.00" );
        private SimulationVariable simulationVariable;

        public TextBox( String valueAbbreviation, final SimulationVariable simulationVariable ) {
            this.simulationVariable = simulationVariable;
            add( new JLabel( valueAbbreviation + " =" ) );
            textField = new JTextField( "0.0", 6 );
            textField.setHorizontalAlignment( JTextField.RIGHT );
            add( textField );
            setBorder( BorderFactory.createLineBorder( Color.black ) );
            simulationVariable.addListener( new SimulationVariable.Listener() {
                public void valueChanged() {
                    update();
                }
            } );
            textField.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    simulationVariable.setValue( Double.parseDouble( textField.getText() ) );
                }
            } );
            update();
        }

        private void update() {
            textField.setText( decimalFormat.format( simulationVariable.getValue() ) );
        }

        public void setEditable( boolean editable ) {
            textField.setEditable( editable );
        }
    }
}
