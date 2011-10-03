// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.colorado.phet.jmephet.hud.SwingJMENode;
import edu.colorado.phet.platetectonics.util.JMEModelViewTransform;

import com.jme3.renderer.queue.RenderQueue.Bucket;

/**
 * Displays a ruler in the 3D play area space
 */
public class PlateRulerNode extends PiccoloJMENode {

    // how much we subsample the piccolo ruler in texture construction
    private static final float PICCOLO_PIXELS_TO_VIEW_UNIT = 3;

    // how much larger should the ruler construction values be to get a good look? we scale by the inverse to remain the correct size
    private static final float RULER_PIXEL_SCALE = 3f;

    public PlateRulerNode( final JMEModelViewTransform transform, final JMEModule module ) {
        // TODO: i18n
        super( new RulerNode( 100 * RULER_PIXEL_SCALE, 10 * RULER_PIXEL_SCALE,
                              new String[] { "0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100" }, "km", 1, 9 ) {{

            // make it vertical
            rotate( -Math.PI / 2 );

            // scale it so that we achieve adherence to the model scale
            float kmToViewUnit = transform.modelToViewDeltaX( 1000 );
            scale( PICCOLO_PIXELS_TO_VIEW_UNIT * kmToViewUnit / RULER_PIXEL_SCALE );

            // don't show things below the "0" mark
            setInsetWidth( 0 );
        }}, module.getInputHandler(), module, SwingJMENode.getDefaultTransform() );

        // scale the node to handle the subsampling
        scale( 1 / PICCOLO_PIXELS_TO_VIEW_UNIT );

        // allow antialiasing for a cleaner look
        antialiased.set( true );

        // allow parts to see through
        setQueueBucket( Bucket.Transparent );
    }
}
