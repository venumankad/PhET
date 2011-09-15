// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;

/**
 * Model of a make-believe diatomic (2 atoms) molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DiatomicMolecule implements IMolecule {

    private static final double ATOM_DIAMETER = 100;
    private static final double BOND_LENGTH = 1.5 * ATOM_DIAMETER;

    public final Atom atomA, atomB; // the atoms labeled A and B
    public final Bond bond; // the bond connecting atoms A and B
    public final Property<ImmutableVector2D> dipole; // the molecular dipole

    private final ImmutableVector2D location; // location is at the center of the bond
    private final Property<Double> angle; // angle of rotation about the location (zero is bond horizontal, atom A left, atom B right)

    private boolean dragging; // true when the user is dragging the molecule

    public DiatomicMolecule( ImmutableVector2D location ) {

        this.location = location;
        angle = new Property<Double>( 0d );
        atomA = new Atom( MPStrings.A, ATOM_DIAMETER, MPColors.ATOM_A, MPConstants.ELECTRONEGATIVITY_RANGE.getMin() );
        atomB = new Atom( MPStrings.B, ATOM_DIAMETER, MPColors.ATOM_B, MPConstants.ELECTRONEGATIVITY_RANGE.getMin() + ( MPConstants.ELECTRONEGATIVITY_RANGE.getLength() / 2 ) );
        bond = new Bond( atomA, atomB );
        dipole = new Property<ImmutableVector2D>( new ImmutableVector2D() );

        // update atom locations
        angle.addObserver( new VoidFunction1<Double>() {
            public void apply( Double angle ) {
                updateAtomLocations( angle );
            }
        } );

        // update molecular dipole
        bond.dipole.addObserver( new SimpleObserver() {
            public void update() {
                updateMolecularDipole();
            }
        } );

        // update partial charges
        RichSimpleObserver enObserver = new RichSimpleObserver() {
            @Override public void update() {
                final double deltaEN = atomA.electronegativity.get() - atomB.electronegativity.get();
                // in our simplified model, partial charge and deltaEN are equivalent. not so in the real world.
                atomA.partialCharge.set( -deltaEN );
                atomB.partialCharge.set( deltaEN );
            }
        };
        enObserver.observe( atomA.electronegativity, atomB.electronegativity );
    }

    public void reset() {
        angle.reset();
        atomA.reset();
        atomB.reset();
    }

    public ImmutableVector2D getDipole() {
        return dipole.get();
    }

    public void addDipoleObserver( SimpleObserver observer ) {
        dipole.addObserver( observer );
    }

    public Atom[] getAtoms() {
        return new Atom[] { atomA, atomB };
    }

    public ImmutableVector2D getLocation() {
        return location;
    }

    public void setAngle( double angle ) {
        this.angle.set( angle );
    }

    public double getAngle() {
        return angle.get();
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging( boolean dragging ) {
        this.dragging = dragging;
    }

    // repositions the atoms
    private void updateAtomLocations( double angle ) {
        final double radius = BOND_LENGTH / 2;
        // atom A
        double xA = PolarCartesianConverter.getX( radius, angle + Math.PI ) + location.getX();
        double yA = PolarCartesianConverter.getY( radius, angle + Math.PI ) + location.getY();
        atomA.location.set( new ImmutableVector2D( xA, yA ) );
        // atom B
        double xB = PolarCartesianConverter.getX( radius, angle ) + location.getX();
        double yB = PolarCartesianConverter.getY( radius, angle ) + location.getY();
        atomB.location.set( new ImmutableVector2D( xB, yB ) );
    }

    // molecular dipole is identical to the bond dipole
    private void updateMolecularDipole() {
        dipole.set( bond.dipole.get() );
    }
}
