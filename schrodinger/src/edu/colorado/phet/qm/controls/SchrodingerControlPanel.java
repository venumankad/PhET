/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.*;
import edu.colorado.phet.qm.model.potentials.SimpleGradientPotential;
import edu.colorado.phet.qm.model.propagators.*;
import edu.colorado.phet.qm.view.SchrodingerPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:51:18 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerControlPanel extends ControlPanel {
    private SchrodingerModule module;
    private ClassicalWavePropagator classicalPropagator2ndOrder;
    private InitialConditionPanel initialConditionPanel;
    private AdvancedPanel advancedPanel;
//    private DoubleSlitControlPanel doubleSlitControlPanel;

    public SchrodingerControlPanel( final SchrodingerModule module ) {
        super( module );
        this.module = module;

        this.initialConditionPanel = createInitialConditionPanel();
//        doubleSlitControlPanel = new DoubleSlitControlPanel( getDiscreteModel(), module );
//        addControl( doubleSlitControlPanel );
        AdvancedPanel advancedICPanel = new AdvancedPanel( "Show>>", "Hide<<" );
        advancedICPanel.addControlFullWidth( this.initialConditionPanel );
        advancedICPanel.setBorder( BorderFactory.createTitledBorder( "Initial Conditions" ) );
        advancedICPanel.addListener( new AdvancedPanel.Listener() {
            public void advancedPanelHidden( AdvancedPanel advancedPanel ) {
                JFrame parent = (JFrame)SwingUtilities.getWindowAncestor( SchrodingerControlPanel.this );
                parent.invalidate();
                parent.validate();
                parent.repaint();
            }

            public void advancedPanelShown( AdvancedPanel advancedPanel ) {
            }
        } );

        JButton fireParticle = new JButton( "Fire Particle" );
        fireParticle.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireParticle();
            }
        } );
        try {
            HorizontalLayoutPanel rulerPanel = new HorizontalLayoutPanel();

            final JCheckBox ruler = new JCheckBox( "Ruler" );
            ImageIcon icon = new ImageIcon( ImageLoader.loadBufferedImage( "images/ruler-thumb.jpg" ) );
            rulerPanel.add( ruler );
            rulerPanel.add( new JLabel( icon ) );
            ruler.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    getSchrodingerPanel().setRulerVisible( ruler.isSelected() );
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        advancedPanel = new AdvancedPanel( "Advanced>>", "Hide Advanced<<" );

        VerticalLayoutPanel intensityScreen = new IntensityScreenPanel( this );
        AdvancedPanel advancedIntensityScreen = new AdvancedPanel( "Screen>>", "Screen<<" );
        advancedIntensityScreen.addControl( intensityScreen );
        advancedPanel.addControlFullWidth( advancedIntensityScreen );

        VerticalLayoutPanel simulationPanel = createSimulationPanel( module );
        AdvancedPanel advSim = new AdvancedPanel( "Simulation>>", "Hide Simulation<<" );
        advSim.addControlFullWidth( simulationPanel );
        advancedPanel.addControlFullWidth( advSim );

        final JSlider speed = new JSlider( JSlider.HORIZONTAL, 0, 1000, (int)( 1000 * 0.1 ) );
        speed.setBorder( BorderFactory.createTitledBorder( "Classical Wave speed" ) );
        speed.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double x = new ModelViewTransform1D( 0, 0.5, speed.getMinimum(), speed.getMaximum() ).viewToModel( speed.getValue() );
                System.out.println( "x = " + x );
                classicalPropagator2ndOrder.setSpeed( x );
            }
        } );

    }

    private WaveSetup getWaveSetup() {
        return initialConditionPanel.getWaveSetup();
    }

    protected VerticalLayoutPanel createPropagatorPanel() {
        VerticalLayoutPanel layoutPanel = new VerticalLayoutPanel();
        layoutPanel.setBorder( BorderFactory.createTitledBorder( "Propagator" ) );
        ButtonGroup buttonGroup = new ButtonGroup();

        JRadioButton richardson = createPropagatorButton( buttonGroup, "Richardson", new RichardsonPropagator( getDiscreteModel().getDeltaTime(), getDiscreteModel().getBoundaryCondition(), getDiscreteModel().getPotential() ) );
        layoutPanel.add( richardson );

        JRadioButton modified = createPropagatorButton( buttonGroup, "Modified Richardson", new ModifiedRichardsonPropagator( getDiscreteModel().getDeltaTime(), getDiscreteModel().getBoundaryCondition(), getDiscreteModel().getPotential() ) );
        layoutPanel.add( modified );

        JRadioButton crank = createPropagatorButton( buttonGroup, "Crank-Nicholson?", new CrankNicholsonPropagator( getDiscreteModel().getDeltaTime(), getDiscreteModel().getBoundaryCondition(), getDiscreteModel().getPotential() ) );
        layoutPanel.add( crank );

        JRadioButton light = createPropagatorButton( buttonGroup, "Avg", new AveragePropagator( getDiscreteModel().getPotential() ) );
        layoutPanel.add( light );

        classicalPropagator2ndOrder = new ClassicalWavePropagator( getDiscreteModel().getPotential() );
        JRadioButton lap = createPropagatorButton( buttonGroup, "finite difference", classicalPropagator2ndOrder );
        layoutPanel.add( lap );
        lap.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                initClassicalWave( classicalPropagator2ndOrder );
            }
        } );
        return layoutPanel;
    }

    private void initClassicalWave( ClassicalWavePropagator propagator2ndOrder ) {
        initialConditionPanel.initClassicalWave( propagator2ndOrder );
    }

    private JRadioButton createPropagatorButton( ButtonGroup buttonGroup, String s, final Propagator propagator ) {

        JRadioButton radioButton = new JRadioButton( s );
        buttonGroup.add( radioButton );
        if( getDiscreteModel().getPropagator().getClass().equals( propagator.getClass() ) ) {
            radioButton.setSelected( true );
        }
        radioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().setPropagator( propagator );
            }
        } );
        return radioButton;
    }

//    private VerticalLayoutPanel createBoundaryPanel() {
//        VerticalLayoutPanel layoutPanel = new VerticalLayoutPanel();
//        layoutPanel.setBorder( BorderFactory.createTitledBorder( "Boundary Condition" ) );
//
//        layoutPanel.add( createPlaneWaveBox() );
//        cylinderWaveBox = createCylinderWaveBox();
//        layoutPanel.add( cylinderWaveBox );
//
//        return layoutPanel;
//    }

//    private JCheckBox createPlaneWaveBox() {
//        final JCheckBox planeWaveCheckbox = new JCheckBox( "Plane Wave" );
//        double scale = 1.0;
//        double k = 1.0 / 10.0 * Math.PI * scale;
//        final PlaneWave planeWave = new PlaneWave( k, getDiscreteModel().getGridWidth() );
//
//        planeWave.setMagnitude( 0.015 );
//        int damping = getDiscreteModel().getDamping().getDepth();
//        int tubSize = 5;
//        final Rectangle rectangle = new Rectangle( damping, getWavefunction().getHeight() - damping - tubSize,
//                                                   getWavefunction().getWidth() - 2 * damping, tubSize );
//        final WaveSource waveSource = new WaveSource( rectangle, planeWave );
//
//        planeWaveCheckbox.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                if( planeWaveCheckbox.isSelected() ) {
//                    getDiscreteModel().addListener( waveSource );
//                }
//                else {
//                    getDiscreteModel().removeListener( waveSource );
//                }
//            }
//        } );
//        return planeWaveCheckbox;
//    }

//    private CylinderWaveCheckBox createCylinderWaveBox() {
//        return new CylinderWaveCheckBox( module, getDiscreteModel() );
//    }

    private Wavefunction getWavefunction() {
        return getDiscreteModel().getWavefunction();
    }

    private InitialConditionPanel createInitialConditionPanel() {
        return new InitialConditionPanel( this );
    }

    protected VerticalLayoutPanel createPotentialPanel( final SchrodingerModule module ) {
        VerticalLayoutPanel layoutPanel = new VerticalLayoutPanel();
        layoutPanel.setFillNone();
        layoutPanel.setBorder( BorderFactory.createTitledBorder( "Potential" ) );

        JButton clear = new JButton( "Remove All" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clearPotential();
            }
        } );

        JButton newBarrier = new JButton( "Add Barrier" );
        newBarrier.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.addPotential();
            }
        } );
        layoutPanel.add( newBarrier );
        layoutPanel.add( clear );

        return layoutPanel;
    }

    private void removePotential( Potential potential ) {
        getSchrodingerPanel().getDiscreteModel().removePotential( potential );
    }

    private void clearPotential() {
        module.clearPotential();
    }

    private Potential createSlopingPotential() {
        return new SimpleGradientPotential( 0.01 );
    }

    private void addPotential( Potential potential ) {
        getSchrodingerPanel().getDiscreteModel().addPotential( potential );
    }

    private VerticalLayoutPanel createSimulationPanel( final SchrodingerModule module ) {
        VerticalLayoutPanel simulationPanel = new VerticalLayoutPanel();
        simulationPanel.setBorder( BorderFactory.createTitledBorder( "Simulation" ) );

        final JSpinner gridWidth = new JSpinner( new SpinnerNumberModel( getDiscreteModel().getGridWidth(), 1, 1000, 10 ) );
        gridWidth.setBorder( BorderFactory.createTitledBorder( "Resolution" ) );
        gridWidth.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int val = ( (Integer)gridWidth.getValue() ).intValue();
                module.setGridSpacing( val, val );
            }
        } );
        simulationPanel.addFullWidth( gridWidth );

        final JSpinner timeStep = new JSpinner( new SpinnerNumberModel( 0.8, 0, 2, 0.1 ) );
        timeStep.setBorder( BorderFactory.createTitledBorder( "DT" ) );

        timeStep.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double t = ( (Number)timeStep.getValue() ).doubleValue();
                getDiscreteModel().setDeltaTime( t );
            }
        } );
        simulationPanel.addFullWidth( timeStep );
        return simulationPanel;
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return module.getSchrodingerPanel();
    }

    public void fireParticle() {
        WaveSetup waveSetup = getWaveSetup();
        module.fireParticle( waveSetup );
    }

    public DiscreteModel getDiscreteModel() {
        return module.getDiscreteModel();
    }

    public SchrodingerModule getModule() {
        return module;
    }

    public AdvancedPanel getAdvancedPanel() {
        return advancedPanel;
    }

    public JCheckBox getSlitAbsorptionCheckbox() {
        final JCheckBox absorbtiveSlit = new JCheckBox( "Absorbing Barriers", getDiscreteModel().isSlitAbsorptive() );
        absorbtiveSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().setSlitAbsorptive( absorbtiveSlit.isSelected() );
            }
        } );
        return absorbtiveSlit;
    }

//    public DoubleSlitControlPanel getDoubleSlitPanel() {
//        return doubleSlitControlPanel;
//    }

}
