// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SaltShaker;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToHeight;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.RESOURCES;

/**
 * Sugar dispenser which can be rotated to pour out an endless supply of sugar.
 *
 * @author Sam Reid
 */
public class SaltShakerNode extends DispenserNode<SaltShaker> {
    public SaltShakerNode( final ModelViewTransform transform, final SaltShaker model ) {
        super( transform, model );
        final BufferedImage fullImage = multiScaleToHeight( RESOURCES.getImage( "salt_1.png" ), 200 );
        final BufferedImage emptyImage = multiScaleToHeight( RESOURCES.getImage( "salt_empty.png" ), 200 );

        //Hide the sugar dispenser if it is not enabled (selected by the user)
        model.enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                setVisible( enabled );
            }
        } );

        model.moreAllowed.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean moreAllowed ) {
                imageNode.setImage( moreAllowed ? fullImage : emptyImage );
            }
        } );

        //Have to update the transform once after the image size changes (since it goes from null to non-null) in the auto-callback above
        updateTransform();
    }
}