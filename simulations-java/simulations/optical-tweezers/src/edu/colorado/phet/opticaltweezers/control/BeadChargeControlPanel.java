/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTResources;

/**
 * BeadChargeControlPanel controls the view of charge on a bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeadChargeControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JRadioButton _hiddenRadioButton;
    private JRadioButton _distributionRadioButton;
    private JRadioButton _excessRadioButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param titleFont
     * @param controlFont
     */
    public BeadChargeControlPanel( Font titleFont, Font controlFont ) {
        super();

        JLabel titleLabel = new JLabel( OTResources.getString( "title.beadChargeControlPanel" ) );
        titleLabel.setFont( titleFont );
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleChargeChoice();
            }
        };

        // "no charts" choice
        _hiddenRadioButton = new JRadioButton( OTResources.getString( "choice.noCharge" ) );
        _hiddenRadioButton.setFont( controlFont );
        _hiddenRadioButton.addActionListener( actionListener );

        // Position Histogram
        _distributionRadioButton = new JRadioButton( OTResources.getString( "choice.chargeDistribution" ) );
        _distributionRadioButton.setFont( controlFont );
        _distributionRadioButton.addActionListener( actionListener );

        // Potential Energy chart
        _excessRadioButton = new JRadioButton( OTResources.getString( "choice.excessCharge" ) );
        _excessRadioButton.setFont( controlFont );
        _excessRadioButton.addActionListener( actionListener );

        ButtonGroup bg = new ButtonGroup();
        bg.add( _hiddenRadioButton );
        bg.add( _distributionRadioButton );
        bg.add( _excessRadioButton );

        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        layout.addComponent( titleLabel, row++, 0 );
        layout.addComponent( _hiddenRadioButton, row++, 0 );
        layout.addComponent( _distributionRadioButton, row++, 0 );
        layout.addComponent( _excessRadioButton, row++, 0 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );

        // Default state
        _hiddenRadioButton.setSelected( true );
        
        //XXX not implemented
        _distributionRadioButton.setForeground( Color.RED );
        _excessRadioButton.setForeground( Color.RED );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public boolean isHiddenSelected() {
        return _hiddenRadioButton.isSelected();
    }
    
    public void setDistributionSelected( boolean b ) {
        _hiddenRadioButton.setSelected( !b );
        _distributionRadioButton.setSelected( b );
        handleChargeChoice();
    }
    
    public boolean isDistributionSelected() {
        return _distributionRadioButton.isSelected();
    }
    
    public void setExcessSelected( boolean b ) {
        _hiddenRadioButton.setSelected( !b );
        _excessRadioButton.setSelected( b );
        handleChargeChoice();
    }
    
    public boolean isExcessSelected() {
        return _excessRadioButton.isSelected();
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    /*
     * Sets the view of bead charge to match the controls.
     */
    private void handleChargeChoice() {
        //XXX
    }
}
