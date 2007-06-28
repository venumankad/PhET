/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.control.AdvancedControlPanel;
import edu.colorado.phet.opticaltweezers.control.ForcesControlPanel;
import edu.colorado.phet.opticaltweezers.control.SimulationSpeedControlPanel;
import edu.colorado.phet.opticaltweezers.control.developer.DeveloperControlPanel;
import edu.colorado.phet.opticaltweezers.model.DNAModel;

/**
 * DNAControlPanel is the control panel for DNAModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAControlPanel extends AbstractControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DNACanvas _canvas;
    
    private SimulationSpeedControlPanel _simulationSpeedControlPanel;
    private ForcesControlPanel _forcesControlPanel;
    private AdvancedControlPanel _advancedControlPanel;
    private DeveloperControlPanel _developerControlPanel;
    
    private JCheckBox _rulerCheckBox;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public DNAControlPanel( DNAModule module) {
        super( module );

        _canvas = module.getDNACanvas();

        // Set the control panel's minimum width.
        int minimumWidth = OTResources.getInt( "int.minControlPanelWidth", 215 );
        setMinumumWidth( minimumWidth );
        
        // Sub-panels
        DNAModel model = module.getDNAModel();
        _simulationSpeedControlPanel = new SimulationSpeedControlPanel( TITLE_FONT, CONTROL_FONT, model.getClock() );
        _forcesControlPanel = new ForcesControlPanel( TITLE_FONT, CONTROL_FONT, 
                model.getBead(), model.getFluid(),
                _canvas.getTrapForceNode(), _canvas.getDragForceNode(), _canvas.getDNAForceNode() );
        _advancedControlPanel = new AdvancedControlPanel( TITLE_FONT, CONTROL_FONT, module.getFrame(), model.getFluid() );
        List forceVectorNodes = new ArrayList();
        forceVectorNodes.add( _canvas.getTrapForceNode() );
        forceVectorNodes.add( _canvas.getDragForceNode() );
        forceVectorNodes.add( _canvas.getDNAForceNode() );
        _developerControlPanel = new DeveloperControlPanel( TITLE_FONT, CONTROL_FONT, module.getFrame(),
                model.getBead(), model.getLaser(), 
                model.getDNAStrand(), _canvas.getDNAStrandNode(),
                forceVectorNodes, _canvas.getLaserNode() );
        
        _rulerCheckBox = new JCheckBox( OTResources.getString( "label.showRuler" ) );
        _rulerCheckBox.setFont( CONTROL_FONT );
        _rulerCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleRulerCheckBox();
            }
        });
        Icon rulerIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_RULER ) );
        JLabel rulerLabel = new JLabel( rulerIcon );
        Box rulerPanel = new Box( BoxLayout.X_AXIS );
        rulerPanel.add( _rulerCheckBox );
        rulerPanel.add( Box.createHorizontalStrut( 5 ) );
        rulerPanel.add( rulerLabel );
        
        // Turn off some features
        _forcesControlPanel.setBrownianMotionCheckBoxVisible( false );
        _advancedControlPanel.setFluidVacuumPanelVisible( false );
        
        // Layout
        {
            addControlFullWidth( _simulationSpeedControlPanel );
            addSeparator();
            addControlFullWidth( _forcesControlPanel );
            addSeparator();
            addControlFullWidth( rulerPanel );
            addSeparator();
            addControlFullWidth( _advancedControlPanel );
            addSeparator();
            if ( System.getProperty( OTConstants.PROPERTY_PHET_DEVELOPER ) != null ) {
                addControlFullWidth( _developerControlPanel );
                addSeparator();
            }
            addResetButton();
        }
        
        // Default state
        _rulerCheckBox.setSelected( false );
        //XXX enable & disable controls based on clock speed
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setRulerSelected( boolean b ) {
        _rulerCheckBox.setSelected( b );
        handleRulerCheckBox();
    }
    
    public boolean isRulerSelected() {
        return _rulerCheckBox.isSelected();
    }
    
    public void closeAllDialogs() {
        _advancedControlPanel.setFluidControlsSelected( false );
    }
    
    //----------------------------------------------------------------------------
    // Access to subpanels
    //----------------------------------------------------------------------------
    
    public DeveloperControlPanel getDeveloperControlPanel() {
        return _developerControlPanel;
    }
    
    public SimulationSpeedControlPanel getSimulationSpeedControlPanel() {
        return _simulationSpeedControlPanel;
    }
    
    public ForcesControlPanel getForcesControlPanel() {
        return _forcesControlPanel;
    }
    
    public AdvancedControlPanel getAdvancedControlPanel() {
        return _advancedControlPanel;
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleRulerCheckBox() {
        final boolean selected = _rulerCheckBox.isSelected();
        _canvas.getRulerNode().setVisible( selected );
    }
}
