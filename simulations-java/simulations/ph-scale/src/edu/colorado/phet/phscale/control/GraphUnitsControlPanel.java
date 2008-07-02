/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleStrings;

/**
 * GraphUnitsControlPanel contains the controls for switching the graph's units.
 * The units can be either concentration (moles/L) or moles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphUnitsControlPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font CONTROL_FONT = new PhetFont( Font.PLAIN, 18 );;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList _listeners;
    private final JRadioButton _concentrationRadioButton;
    private final JRadioButton _molesRadioButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GraphUnitsControlPanel() {
        super();
        setOpaque( false );

        _listeners = new ArrayList();
        
        _concentrationRadioButton = new JRadioButton( PHScaleStrings.getConcentrationString() );
        _concentrationRadioButton.setOpaque( false );
        _concentrationRadioButton.setFont( CONTROL_FONT );
        _concentrationRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifySelectionChanged();
            }
        } );
        
        _molesRadioButton = new JRadioButton( PHScaleStrings.getNumberOfMolesString() );
        _molesRadioButton.setOpaque( false );
        _molesRadioButton.setFont( CONTROL_FONT );
        _molesRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifySelectionChanged();
            }
        } );
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _concentrationRadioButton );
        buttonGroup.add( _molesRadioButton );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        int row = 0;
        int col = 0;
        layout.addComponent( _concentrationRadioButton, row++, col );
        layout.addComponent( _molesRadioButton, row++, col );
        
        _concentrationRadioButton.setSelected( true );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public boolean isConcentrationSelected() {
        return _concentrationRadioButton.isSelected();
    }
    
    public void setConcentrationSelected( boolean selected ) {
        if ( selected != isConcentrationSelected() ) {
            _concentrationRadioButton.setSelected( selected );
            notifySelectionChanged();
        }
    }
    
    public boolean isMolesSelected() {
        return _molesRadioButton.isSelected();
    }
    
    public void setMolesSelected( boolean selected ) {
        if ( selected != isMolesSelected() ) {
            _molesRadioButton.setSelected( selected );
            notifySelectionChanged();
        }
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface GraphUnitsControlPanelListener {
        public void selectionChanged();
    }
    
    public void addGraphUnitsControlPanelListener( GraphUnitsControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeGraphUnitsControlPanelListener( GraphUnitsControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifySelectionChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (GraphUnitsControlPanelListener) i.next() ).selectionChanged();
        }
    }
}
