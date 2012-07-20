// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;

/**
 * Class that represents a block whose specific heat can be changed.
 *
 * @author John Blanco
 */
public class ConfigurableSpecificHeatBlock extends Block {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double INITIAL_SPECIFIC_HEAT = 840; // In J/kg-K, source = design document.
    private static final double DENSITY = 2000; // In kg/m^3.

    public static final double MIN_SPECIFIC_HEAT = 100;
    public static final double MAX_SPECIFIC_HEAT = 1000;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Specific heat, which is configurable in this block.
    public final Property<Double> specificHeat = new Property<Double>( INITIAL_SPECIFIC_HEAT );

    // Color changes as specific heat changes.
    public final Property<Color> color = new Property<Color>( new Color( 200, 22, 11 ) );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected ConfigurableSpecificHeatBlock( ConstantDtClock clock, ImmutableVector2D initialPosition, BooleanProperty energyChunksVisible ) {
        super( clock, initialPosition, DENSITY, INITIAL_SPECIFIC_HEAT, energyChunksVisible );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Color getColor() {
        return color.get();
    }

    @Override public String getLabel() {
        // TODO: i18n
        return "Custom";
    }

    public EnergyContainerCategory getEnergyContainerCategory() {
        return EnergyContainerCategory.CUSTOM;
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.brick;
    }

    @Override public void reset() {
        specificHeat.reset();
        super.reset();
    }
}
