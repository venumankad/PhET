// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class ImageButtonNodeWithText extends PNode {
    public ImageButtonNodeWithText( final BufferedImage image, final String text ) {
        final PImage imageNode = new PImage( image );
        addChild( imageNode );
        final PhetPText textNode = new PhetPText( text );
        textNode.scale( image.getWidth() / textNode.getFullWidth() * 0.8 );
        addChild( textNode );

        textNode.centerFullBoundsOnPoint( imageNode.getFullBounds().getCenter2D() );
    }
}