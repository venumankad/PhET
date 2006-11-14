/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * AnimationBoxNode is the box in which animation
 * of atoms, photons and alpha particles takes place.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AnimationBoxNode extends PClip {

    public static final Stroke STROKE = new BasicStroke( 2f );
    public static final Color STROKE_COLOR = Color.WHITE;
    
    private PLayer _bottomLayer;
    private PLayer _middleLayer;
    private PLayer _topLayer;
    
    public AnimationBoxNode( Dimension size ) {
        super();
        
        Shape clip = new Rectangle2D.Double( 0, 0, size.width, size.height );
        setPathTo( clip );
        setPaint(  HAConstants.ANIMATION_BOX_COLOR );
        setStroke( STROKE );
        setStrokePaint( HAConstants.ANIMATION_BOX_STROKE_COLOR );
        
        _bottomLayer = new PLayer();
        addChild( _bottomLayer );
        _middleLayer = new PLayer();
        addChild( _middleLayer );
        _topLayer = new PLayer();
        addChild( _topLayer);
    }
    
    public PLayer getBottomLayer() {
        return _bottomLayer;
    }
    
    public PLayer getMiddleLayer() {
        return _middleLayer;
    }
    
    public PLayer getTopLayer() {
        return _topLayer;
    }
}
