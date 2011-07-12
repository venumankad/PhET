// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.control;

import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;

/**
 * "Test" control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPanel extends GridPanel {

    public TestPanel( Property<Boolean> eFieldEnabled ) {
        setBorder( new TitledBorder( MPStrings.TEST ) {{
            setTitleFont( MPConstants.TITLED_BORDER_FONT );
        }} );
        add( new JLabel( MessageFormat.format( MPStrings.PATTERN_0LABEL, MPStrings.ELECTRIC_FIELD ) ) );
        add( new PropertyRadioButton<Boolean>( MPStrings.ON, eFieldEnabled, true ) );
        add( new PropertyRadioButton<Boolean>( MPStrings.OFF, eFieldEnabled, false ) );
    }
}
