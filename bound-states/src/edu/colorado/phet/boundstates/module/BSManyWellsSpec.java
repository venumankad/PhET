/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.module;

import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.BSDoubleRange;
import edu.colorado.phet.boundstates.model.BSIntegerRange;

/**
 * BSManyWellsSpec contains the information needed to set up the "Many Wells" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSManyWellsSpec extends BSAbstractModuleSpec {

    private static final BSWellType[] WELL_TYPES = { BSWellType.SQUARE, BSWellType.COULOMB_1D };
    private static final BSWellType DEFAULT_WELL_TYPE = BSWellType.SQUARE;
    
    private static final boolean SUPERPOSITION_CONTROLS_SUPPORTED = false;
    private static final boolean PARTICLE_CONTROLS_SUPPORTED = false;

    // Ranges (min, max, default, significantDecimalPlaces)
    private static final BSIntegerRange NUMBER_OF_WELLS_RANGE = new BSIntegerRange( 1, 10, 5 );
    private static final BSDoubleRange MASS_MULTIPLIER_RANGE = new BSDoubleRange( 0.5, 1.1, 1, 1 );
    private static final BSDoubleRange OFFSET_RANGE = new BSDoubleRange( -15, 5, 0, 0 ); // eV
    private static final BSDoubleRange DEPTH_RANGE = new BSDoubleRange( 0, 20, 10, 0 ); // eV
    private static final BSDoubleRange WIDTH_RANGE = new BSDoubleRange( 0.1, 0.5, 0.5, 1 ); // nm
    private static final BSDoubleRange SPACING_RANGE = new BSDoubleRange( 0.01, 1, 0.5, 2 ); // nm
    private static final BSDoubleRange SEPARATION_RANGE = new BSDoubleRange( 0.05, 0.2, 0.1, 2 ); // nm
    private static final BSDoubleRange ANGULAR_FREQUENCY_RANGE = new BSDoubleRange( 0, 0, 0, 0 ); // not supported for many wells

    public BSManyWellsSpec() {
        super();
        
        setWellTypes( WELL_TYPES );
        setDefaultWellType( DEFAULT_WELL_TYPE );
        
        setSuperpositionControlsSupported( SUPERPOSITION_CONTROLS_SUPPORTED );
        setParticleControlsSupported( PARTICLE_CONTROLS_SUPPORTED );
        
        setNumberOfWellsRange( NUMBER_OF_WELLS_RANGE );
        setMassMultiplierRange( MASS_MULTIPLIER_RANGE );
        setOffsetRange( OFFSET_RANGE );
        setDepthRange( DEPTH_RANGE );
        setWidthRange( WIDTH_RANGE );
        setSpacingRange( SPACING_RANGE );
        setSeparationRange( SEPARATION_RANGE );
        setAngularFrequencyRange( ANGULAR_FREQUENCY_RANGE );
    }
}
