// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import edu.umd.cs.piccolo.PNode;

/**
 * Node that translates its content so that the origin is at (0,0).  This is to help with layouts for nodes that don't default to this coordinate frame.
 *
 * @author Sam Reid
 */
public class StandardizedNode extends PNode {
    public StandardizedNode( PNode node ) {
        addChild( node );
        node.setOffset( -node.getFullBounds().getX(), -node.getFullBounds().getY() );
    }
}
