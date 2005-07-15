/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.Color;
import java.awt.event.*;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.control.sliders.AbstractFourierSlider;
import edu.colorado.phet.fourier.control.sliders.DefaultFourierSlider;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.util.EasyGridBagLayout;


/**
 * SoundPanel is a panel that contains sound controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SoundPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int TITLED_BORDER_WIDTH = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeries;
    private AbstractFourierSlider _fundamentalFrequencySlider;
    private JCheckBox _playSoundCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SoundPanel( FourierSeries fourierSeries ) {
        super();

        assert( fourierSeries != null );
        _fourierSeries = fourierSeries;
        
        //  Title
        Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, TITLED_BORDER_WIDTH );
        String title = SimStrings.get( "SoundPanel.title" );
        TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, title );
        setBorder( titleBorder );

        // Fundamental frequency
        {
            String format = SimStrings.get( "SoundPanel.fundamentalFrequency" );
            _fundamentalFrequencySlider = new DefaultFourierSlider( format );
            _fundamentalFrequencySlider.getSlider().setMaximum( 1200 );
            _fundamentalFrequencySlider.getSlider().setMinimum( 200 );
            _fundamentalFrequencySlider.getSlider().setMajorTickSpacing( 250 );
            _fundamentalFrequencySlider.getSlider().setMinorTickSpacing( 50 );
            _fundamentalFrequencySlider.getSlider().setSnapToTicks( false );
            _fundamentalFrequencySlider.getSlider().setPaintTicks( true );
            _fundamentalFrequencySlider.getSlider().setPaintLabels( true );
        }

        // Play Sound
        _playSoundCheckBox = new JCheckBox( SimStrings.get( "SoundPanel.playSound" ) );

        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        layout.addComponent( _fundamentalFrequencySlider, row++, 0 );
        layout.addComponent( _playSoundCheckBox, row++, 0 );
        
        reset();
        
        // Event handling
        EventListener listener = new EventListener();
        _playSoundCheckBox.addActionListener( listener );
        _fundamentalFrequencySlider.addChangeListener( listener );
    }
    
    public void reset() {
        _fundamentalFrequencySlider.setValue( (int) _fourierSeries.getFundamentalFrequency() );
        _playSoundCheckBox.setSelected( false );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /**
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     */
    private class EventListener implements ActionListener, ChangeListener {

        public EventListener() {}
        
        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _playSoundCheckBox ) {
                handlePlaySound();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
        
        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _fundamentalFrequencySlider ) {
                handleFundamentalFrequency();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
    }
    
    private void handlePlaySound() {
        //XXX implement
    }
    
    private void handleFundamentalFrequency() {
        int fundamentalFrequency = (int)_fundamentalFrequencySlider.getValue();
        _fourierSeries.setFundamentalFrequency( fundamentalFrequency );
    }
}
