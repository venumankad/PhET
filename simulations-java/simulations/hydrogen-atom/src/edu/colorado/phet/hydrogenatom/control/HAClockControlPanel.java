/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.HADefaults;
import edu.colorado.phet.hydrogenatom.HAResources;
import edu.colorado.phet.hydrogenatom.model.HAClock;


/**
 * HAClockControlPanel is a custom clock control panel.
 * It has control buttons (Play, Pause, Step) and a time speed slider.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAClockControlPanel extends ClockControlPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HAClock _clock;
    private JSlider _clockIndexSlider;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock
     */
    public HAClockControlPanel( HAClock clock ) {
        super( clock );
        
        // Clock
        double dt = HAConstants.DEFAULT_CLOCK_STEP;
        _clock = clock;
        _clock.setDt( dt );
        
        // Clock icon
//        Icon clockIcon = new ImageIcon( HAResources.getCommonImage( PhetCommonResources.IMAGE_CLOCK ) );
//        JLabel clockLabel = new JLabel( clockIcon );

        // Speed slider
        {
            _clockIndexSlider = new JSlider();
            _clockIndexSlider.setMinimum( 0 );
            _clockIndexSlider.setMaximum( HAConstants.CLOCK_STEPS.length - 1 );
            _clockIndexSlider.setMajorTickSpacing( 1 );
            _clockIndexSlider.setPaintTicks( true );
            _clockIndexSlider.setPaintLabels( true );
            _clockIndexSlider.setSnapToTicks( true );
            _clockIndexSlider.setValue( HADefaults.CLOCK_INDEX );
            
            // Label the min "normal", the max "fast".
            String normalString = HAResources.getString( "label.clockSpeed.slow" );
            String fastString = HAResources.getString( "label.clockSpeed.fast" );
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Integer( _clockIndexSlider.getMinimum() ), new JLabel( normalString ) );
            labelTable.put( new Integer( _clockIndexSlider.getMaximum() ), new JLabel( fastString ) );
            _clockIndexSlider.setLabelTable( labelTable );
            
            // Set the slider's physical width
            Dimension preferredSize = _clockIndexSlider.getPreferredSize();
            Dimension size = new Dimension( 150, (int) preferredSize.getHeight() );
            _clockIndexSlider.setPreferredSize( size );
        }
        
        // Layout
        addControlToLeft( _clockIndexSlider );
//        addControlToLeft( clockLabel );
        
        // Interactivity
        _clockIndexSlider.addChangeListener( new ChangeListener() { 
            public void stateChanged( ChangeEvent event ) {
                handleClockIndexChange();
            }
        } );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the clock index (speed) component, used for attaching help items.
     * 
     * @return JComponent
     */
    public JComponent getClockIndexComponent() {
        return _clockIndexSlider;
    }
    
    /**
     * Gets the clock index.
     * 
     * @return clock index
     */
    public int getClockIndex() {
        return _clockIndexSlider.getValue();
    }
    
    /**
     * Sets the clock index.
     * 
     * @param index
     */
    public void setClockIndex( int index ) {
        _clockIndexSlider.setValue( index );
        handleClockIndexChange();
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleClockIndexChange() {
        int index = _clockIndexSlider.getValue();
        double dt = HAConstants.CLOCK_STEPS[index];
        _clock.setDt( dt );
    }
}
