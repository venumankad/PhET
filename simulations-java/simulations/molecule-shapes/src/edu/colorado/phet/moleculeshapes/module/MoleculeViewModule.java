// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.module;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.moleculeshapes.model.Molecule;

/**
 * Abstract class for modules that show a single molecule view
 */
public abstract class MoleculeViewModule extends JMEModule {

    private Property<Molecule> molecule = new Property<Molecule>( null );

    // whether bond angles should be shown
    public final Property<Boolean> showBondAngles = new Property<Boolean>( "Show bond angles", false );

    // whether lone pairs should be shown
    public final Property<Boolean> showLonePairs = new Property<Boolean>( "Show lone pairs", true );

    // whether terminal lone pairs should also be shown
    public final Property<Boolean> showAllLonePairs = new Property<Boolean>( "Show all lone pairs", false );

    public MoleculeViewModule( Frame parentFrame, String name, IClock clock ) {
        super( parentFrame, name, clock );
    }

    /**
     * @return Our relative screen display scale compared to the stage scale
     */
    public ImmutableVector2D getScale() {
        return new ImmutableVector2D( getCanvasSize().getWidth() / getStageSize().getWidth(),
                                      getCanvasSize().getHeight() / getStageSize().getHeight() );
    }

    public float getApproximateScale() {
        ImmutableVector2D scale = getScale();
        return (float) ( ( scale.getX() + scale.getY() ) / 2 );
    }

    public Molecule getMolecule() {
        return molecule.get();
    }

    public void setMolecule( Molecule molecule ) {
        this.molecule.set( molecule );
    }

    public Property<Molecule> getMoleculeProperty() {
        return molecule;
    }

    /*---------------------------------------------------------------------------*
    * options
    *----------------------------------------------------------------------------*/

    public boolean allowTogglingLonePairs() {
        return true;
    }

    public boolean allowTogglingAllLonePairs() {
        return true;
    }
}
