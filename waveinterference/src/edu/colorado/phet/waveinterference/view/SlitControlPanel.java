/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.model.SlitPotential;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 3:28:16 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class SlitControlPanel extends VerticalLayoutPanelWithDisable {
    private SlitPotential slitPotential;
    private ModelSlider slitSeparation;
    private ModelSlider slitWidthSlider;
    private ModelSlider slitLocationSlider;

    public SlitControlPanel( final SlitPotential slitPotential ) {
//        setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Barrier" ) );
//        setBorder( BorderFactory.createLineBorder( Color.black) );
        setBorder( BorderFactory.createEtchedBorder() );
        this.slitPotential = slitPotential;
        HorizontalLayoutPanel topPanel = new HorizontalLayoutPanel();
        final JCheckBox enable = new JCheckBox( "Enabled", slitPotential.isEnabled() );
        enable.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                slitPotential.setEnabled( enable.isSelected() );
            }
        } );
//        topPanel.add( enable );

        ButtonGroup buttonGroup = new ButtonGroup();
        final JRadioButton noBarrier = new JRadioButton( "No Barrier", !slitPotential.isEnabled() );
        final JRadioButton oneSlit = new JRadioButton( "One Slit", slitPotential.isOneSlit() && slitPotential.isEnabled() );
        final JRadioButton twoSlits = new JRadioButton( "Two Slits", slitPotential.isTwoSlits() && slitPotential.isEnabled() );
        buttonGroup.add( noBarrier );
        buttonGroup.add( oneSlit );
        buttonGroup.add( twoSlits );
        noBarrier.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setSlitEnabled( !noBarrier.isSelected() );
            }

        } );
        oneSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setSlitEnabled( true );
                slitPotential.setOneSlit();
                updateSeparationCheckbox();
            }
        } );
        twoSlits.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setSlitEnabled( true );
                slitPotential.setTwoSlits();
                updateSeparationCheckbox();
            }
        } );
        VerticalLayoutPanel eastPanel = new VerticalLayoutPanel();
        eastPanel.add( noBarrier );
        eastPanel.add( oneSlit );
        eastPanel.add( twoSlits );

        topPanel.add( eastPanel );
        add( topPanel );

        slitWidthSlider = new ModelSlider( "Slit Width", "", 0, 30, slitPotential.getSlitWidth() );
        slitWidthSlider.setTextFieldVisible( false );
        slitWidthSlider.setBorder( null );
        slitWidthSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                slitPotential.setSlitWidth( (int)slitWidthSlider.getValue() );
            }
        } );
        add( slitWidthSlider );

        slitLocationSlider = new ModelSlider( "Barrier Location", "", 0, 100, slitPotential.getLocation() );
        slitLocationSlider.setTextFieldVisible( false );
        slitLocationSlider.setBorder( null );
        slitLocationSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                slitPotential.setLocation( (int)slitLocationSlider.getValue() );
            }
        } );
        add( slitLocationSlider );

        slitSeparation = new ModelSlider( "Slit Separation", "", 0, 50, slitPotential.getSlitSeparation() );
        slitSeparation.setTextFieldVisible( false );
        slitSeparation.setBorder( null );
        slitSeparation.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                slitPotential.setSlitSeparation( (int)slitSeparation.getValue() );
            }
        } );
        add( slitSeparation );
        updateSeparationCheckbox();
        setSlitEnabled( slitPotential.isEnabled() );
    }

    private void updateSeparationCheckbox() {
        slitSeparation.setEnabled( !slitPotential.isOneSlit() && slitPotential.isEnabled() && getEnabledFlag() );
    }

    private void setSlitEnabled( boolean b ) {
        slitPotential.setEnabled( b );
        slitWidthSlider.setEnabled( b );
        slitLocationSlider.setEnabled( b );
        updateSeparationCheckbox();
    }

    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        updateSeparationCheckbox();
        setSlitEnabled( slitPotential.isEnabled() );
    }

}
