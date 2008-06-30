package edu.colorado.phet.phscale.view;

import java.awt.image.BufferedImage;

import javax.swing.SwingConstants;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.phscale.PHScaleImages;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;


public class FaucetNode extends PNode {
    
    // We expect the image to be this size, warn if not this size.
    public static final PDimension IMAGE_SIZE = new PDimension( 125, 90 );
    
    public static final int ORIENTATION_LEFT = SwingConstants.LEFT;
    public static final int ORIENTATION_RIGHT = SwingConstants.RIGHT;
    
    public FaucetNode( int orientation ) {
        BufferedImage image = null;
        if ( orientation == ORIENTATION_RIGHT ) {
            image = PHScaleImages.FAUCET;
        }
        else {
            image = BufferedImageUtils.flipX( PHScaleImages.FAUCET );
        }
        PImage faucetImage = new PImage( image );
        addChild( faucetImage );
        
        if ( IMAGE_SIZE.getWidth() != faucetImage.getFullBoundsReference().getWidth() ||
             IMAGE_SIZE.getHeight() != faucetImage.getFullBoundsReference().getHeight() ) {
            System.out.println( "WARNING: in FaucetNode, image is not the excepted size, layout may be off" );
        }
    }

}
