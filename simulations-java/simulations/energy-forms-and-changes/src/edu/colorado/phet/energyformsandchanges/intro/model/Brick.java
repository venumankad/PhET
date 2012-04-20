// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;

/**
 * @author John Blanco
 */
public class Brick extends Block {

    protected Brick( ImmutableVector2D initialPosition ) {
        super( initialPosition );
    }

    @Override public Color getColor() {
        return new Color( 200, 22, 11 );
    }

    @Override public String getLabel() {
        // TODO: i18n
        return "Brick";
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.brick;
    }
}
