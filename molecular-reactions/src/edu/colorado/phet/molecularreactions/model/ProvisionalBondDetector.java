/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.model.collision.MoleculeMoleculeCollisionSpec;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;

import java.util.ArrayList;
import java.util.List;

/**
 * ProvisionalBondDetector
 * <p/>
 * Detects when provisional bonds should be created. It does not destroy provional bonds.
 * That is done by the bonds themselves
 * <p>
 * This is a ModelElement. At each time step, it looks at all the molecules in the model
 * to see if any qualify for provisional bonds.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ProvisionalBondDetector implements ModelElement, PublishingModel.ModelListener {
    private MRModel model;
    private List bondedMolecules = new ArrayList();

    public ProvisionalBondDetector( MRModel model ) {
        this.model = model;
        model.addListener( this );

        // Scan the model for existing Bonds. SimpleMolecules participating in hard bonds are
        // not eligible to participate in provisional bonds
        List modelElements = model.getModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            Object o = modelElements.get( i );
            if( o instanceof Bond ) {
                Bond bond = (Bond)o;
                bondedMolecules.add( bond.getParticipants()[0] );
                bondedMolecules.add( bond.getParticipants()[1] );
            }
        }
    }

    public void stepInTime( double dt ) {

        // Determine how far apart two molecules can be and have a provisional bond
        double provisionalBondMaxLength = model.getEnergyProfile().getThresholdWidth() / 2;

        // Look for SimpleMolecules that qualify for tentative bonds. If there is not a tentative bond
        // for a pair, then create one.
        List modelElements = model.getModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            Object o = modelElements.get( i );
            if( o instanceof SimpleMolecule ) {
                SimpleMolecule sm1 = (SimpleMolecule)o;

                for( int j = 0; j < modelElements.size(); j++ ) {
                    Object o1 = modelElements.get( j );

                    // If we have two SimpleMolecules, see if they're close enough to qualify for a
                    // provisional bond
                    if( o1 instanceof SimpleMolecule && o != o1 ) {
                        SimpleMolecule sm2 = (SimpleMolecule)o1;

                        // NOTE!!! This code only works for A_AB_BC_C reactions. It counts on the fact that
                        // there are only two components in a composite molecule, and that
                        // reaction.getReactionCriteria().criteriaMet() only returns true for the component
                        // that the simple molecule can knock off the composite
                        MoleculeMoleculeCollisionSpec collisionSpec = new MoleculeMoleculeCollisionSpec( null,
                                                                                                         null,
                                                                                                         sm1,
                                                                                                         sm2 );
                        Reaction reaction = model.getReaction();
                        Molecule m1 = sm1.isPartOfComposite() ? sm1.getParentComposite() : sm1;
                        Molecule m2 = sm2.isPartOfComposite() ? sm2.getParentComposite() : sm2;
                        if( reaction.getReactionCriteria().criteriaMet( m1,
//                        if( !reaction.getReactionCriteria().criteriaMet( m1,
                                                                         m2,
                                                                         collisionSpec ) ) {
                            continue;
                        }


                        double moleculeSeparation = sm1.getPosition().distance( sm2.getPosition() ) - sm1.getRadius() - sm2.getRadius();
                        if( moleculeSeparation <= provisionalBondMaxLength ) {

                            // If no provisional bond exists for either of these two simple molecules, create one
                            if( !bondedMolecules.contains( sm1 ) || !bondedMolecules.contains( sm2 ) ) {
                                ProvisionalBond bond = new ProvisionalBond( sm1, sm2, provisionalBondMaxLength, model );
                                model.addModelElement( bond );
                                bondedMolecules.add( sm1 );
                                bondedMolecules.add( sm2 );
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * If a ProvisionalBond is removed from the model, make its participants eligible for provisional bonding
     *
     * @param element
     */
    public void modelElementRemoved( ModelElement element ) {
        if( element instanceof ProvisionalBond ) {
            ProvisionalBond bond = (ProvisionalBond)element;
            bondedMolecules.remove( bond.getMolecules()[0] );
            bondedMolecules.remove( bond.getMolecules()[1] );
        }
    }

    /**
     * If a Bond is added to the model, its participants should become eligible for provisional bonding
     *
     * @param element
     */
    public void modelElementAdded( ModelElement element ) {
        if( element instanceof Bond ) {
            Bond bond = (Bond)element;
            bondedMolecules.remove( bond.getParticipants()[0] );
            bondedMolecules.remove( bond.getParticipants()[1] );
        }
    }
}
