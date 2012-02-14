// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractionsintro.intro.model.Container;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.view.CakeNode;
import edu.colorado.phet.fractionsintro.intro.view.Representation;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Representation control panel icon for cake.
 *
 * @author Sam Reid
 */
public class CakeIcon extends PNode implements RepresentationIcon {

    public CakeIcon( final SettableProperty<Representation> selected ) {
        addChild( new CakeNode( 1, new int[] { 1 }, new Property<ContainerSet>( new ContainerSet( 1, new Container[] { } ) ), 0, new int[] { 1 } ) );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                selected.set( getRepresentation() );
            }
        } );
        scale( 0.5 );
    }

    public PNode getNode() {
        return this;
    }

    public Representation getRepresentation() {
        return Representation.CAKE;
    }
}
