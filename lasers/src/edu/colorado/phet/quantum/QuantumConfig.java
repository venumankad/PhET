/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum;

import edu.colorado.phet.common.view.util.VisibleColor;

/**
 * QuatumConfig
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class QuantumConfig {

    public static final double STIMULATION_LIKELIHOOD = 0.2;
    public static boolean ENABLE_ALL_STIMULATED_EMISSIONS = true;

    // Tolerances used to determine if a photon matches with an atomic state energy
    public static final double ENERGY_TOLERANCE = 0.01;

    public static final double MIN_WAVELENGTH = VisibleColor.MIN_WAVELENGTH;
    public static final double MAX_WAVELENGTH = VisibleColor.MAX_WAVELENGTH;
}
