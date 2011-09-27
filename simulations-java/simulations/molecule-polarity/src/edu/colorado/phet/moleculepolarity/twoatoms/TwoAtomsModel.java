// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;
import edu.colorado.phet.moleculepolarity.common.model.MPModel2D;

/**
 * Model for the "Two Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsModel extends MPModel2D {

    public final DiatomicMolecule molecule;

    public TwoAtomsModel( IClock clock ) {
        //REVIEW - Why the hard-coded non-zero location?  Where do these numbers come from? Should at least document.
        super( clock, new DiatomicMolecule( new ImmutableVector2D( 350, 390 ), 0 ) );
        molecule = (DiatomicMolecule) getMolecule(); // hate to cast, but it facilitates moving shared code to base class
    }
}
