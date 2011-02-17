// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.C;
import edu.colorado.phet.balancingchemicalequations.model.Atom.H;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * CH3OH molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class C2H6Node extends PComposite {

    public C2H6Node() {

        // atom nodes
        AtomNode leftNode = new AtomNode( new C() );
        AtomNode rightNode = new AtomNode( new C() );
        AtomNode smallTopLeftNode = new AtomNode( new H() );
        AtomNode smallBottomLeftNode = new AtomNode( new H() );
        AtomNode smallLeftNode = new AtomNode( new H() );
        AtomNode smallTopRightNode = new AtomNode( new H() );
        AtomNode smallBottomRightNode = new AtomNode( new H() );
        AtomNode smallRightNode = new AtomNode( new H() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( smallBottomRightNode );
        parentNode.addChild( smallTopRightNode );
        parentNode.addChild( rightNode );
        parentNode.addChild( smallRightNode );
        parentNode.addChild( smallLeftNode );
        parentNode.addChild( leftNode );
        parentNode.addChild( smallBottomLeftNode );
        parentNode.addChild( smallTopLeftNode );

        // layout
        double x = 0;
        double y = 0;
        leftNode.setOffset( x, y );
        x = leftNode.getFullBoundsReference().getMaxX() + ( 0.25 * rightNode.getFullBoundsReference().getWidth() );
        y = leftNode.getYOffset();
        rightNode.setOffset( x, y );
        x = leftNode.getXOffset();
        y = leftNode.getFullBoundsReference().getMinY();
        smallTopLeftNode.setOffset( x, y );
        x = smallTopLeftNode.getXOffset();
        y = leftNode.getFullBoundsReference().getMaxY();
        smallBottomLeftNode.setOffset( x, y );
        x = leftNode.getFullBoundsReference().getMinX();
        y = leftNode.getYOffset();
        smallLeftNode.setOffset( x, y );
        x = rightNode.getFullBoundsReference().getMaxX();
        y = rightNode.getYOffset();
        smallRightNode.setOffset( x, y );
        x = rightNode.getXOffset();
        y = rightNode.getFullBoundsReference().getMinY();
        smallTopRightNode.setOffset( x, y );
        x = rightNode.getXOffset();
        y = rightNode.getFullBoundsReference().getMaxY();
        smallBottomRightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
