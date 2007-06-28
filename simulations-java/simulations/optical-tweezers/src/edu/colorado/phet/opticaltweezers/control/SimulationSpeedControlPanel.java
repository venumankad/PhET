/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.OTClock;


public class SimulationSpeedControlPanel extends JPanel {

    private OTClock _clock;
    private SimulationSpeedSlider _slider;
    private JLabel _valueLabel;
    
    public SimulationSpeedControlPanel( Font titleFont, Font controlFont, OTClock clock ) {
        super();
        
        JLabel titleLabel = new JLabel( OTResources.getString( "label.simulationSpeed" ) );
        titleLabel.setFont( titleFont );
        
        _clock = clock;
        _clock.addClockListener( new ClockAdapter() {
            //XXX _slider.setValue when the clock's timing strategy (dt) is changed
        });
        
        _slider = new SimulationSpeedSlider( clock.getSlowRange(), clock.getFastRange(), clock.getDt() );
        _slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handleSliderChange();
            }
        });
        
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setBackground( getBackground() );
        canvas.setBorder( null );
        canvas.getLayer().addChild( _slider );
        int margin = 2;
        int xOffset = (int) -_slider.getFullBounds().getX() + margin;
        int yOffset = (int) -_slider.getFullBounds().getY() + margin;
        _slider.setOffset( xOffset, yOffset );
        int w = (int) _slider.getFullBounds().getWidth() + ( 2 * margin );
        int h = (int) _slider.getFullBounds().getHeight() + ( 2 * margin );
        canvas.setPreferredSize( new Dimension( w, h ) );
        
        _valueLabel = new JLabel( _slider.getFormattedValue() + " " + OTResources.getString( "units.time" ) );
        _valueLabel.setFont( controlFont );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        int row = 0;
        int column = 0;
        layout.addComponent( titleLabel, row, column++ );
        layout.addComponent( Box.createHorizontalStrut( 5 ), row, column++ );
        layout.addComponent( _valueLabel, row, column++ );
        row++;
        column = 0;
        layout.addComponent( canvas, row, column, 3, 1 );
        
    }
    
    public void setSimulationSpeed( double dt ) {
        _slider.setValue( dt );
        handleSliderChange();
    }
    
    public double getSimulationSpeed() {
        return _slider.getValue();
    }
    
    private void handleSliderChange() {
        _valueLabel.setText( _slider.getFormattedValue() + " " + OTResources.getString( "units.time" ) );
        _clock.setDt( _slider.getValue() );
    }
}
