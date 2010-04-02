/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.HorizontalLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Controls for the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BeakerControls extends JPanel {
    
    private final Beaker beaker;
    private final LinearValueControl widthControl, heightControl;
    
    public BeakerControls( Beaker beaker ) {
        setBorder( new TitledBorder( "Beaker" ) );
        
        this.beaker = beaker;
        
        // beaker width
        IntegerRange widthRange = ProtoConstants.BEAKER_WIDTH_RANGE;
        widthControl = new LinearValueControl( widthRange.getMin(), widthRange.getMax(), "width:", "##0", "", new HorizontalLayoutStrategy() );
        widthControl.setValue( beaker.getWidth() );
        widthControl.setUpDownArrowDelta( 1 );
        widthControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateBeaker();
            }
        });

        // beaker height
        IntegerRange heightRange = ProtoConstants.BEAKER_HEIGHT_RANGE;
        heightControl = new LinearValueControl( heightRange.getMin(), heightRange.getMax(), "height:", "##0", "", new HorizontalLayoutStrategy() );
        heightControl.setValue( beaker.getHeight() );
        heightControl.setUpDownArrowDelta( 1 );
        heightControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateBeaker();
            }
        });
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( widthControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( heightControl, row, column++ );
    }
    
    private void updateBeaker() {
        beaker.setWidth( (int)widthControl.getValue() );
        beaker.setHeight( (int)heightControl.getValue() );
    }
}
