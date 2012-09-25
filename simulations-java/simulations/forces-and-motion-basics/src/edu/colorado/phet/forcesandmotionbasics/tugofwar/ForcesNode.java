// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.forcesandmotionbasics.common.ForceArrowNode;
import edu.colorado.phet.forcesandmotionbasics.common.TextLocation;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas.STAGE_SIZE;

/**
 * @author Sam Reid
 */
public class ForcesNode extends PNode {
    public void setForces( boolean transparent, final double leftForce, final double rightForce, final boolean showSumOfForces, final Boolean showValues ) {
        removeAllChildren();
        addChild( new ForceArrowNode( transparent, Vector2D.v( STAGE_SIZE.width / 2 - 2, 200 ), leftForce, "Left Force", new Color( 202, 164, 129 ), TextLocation.SIDE, showValues, 1.0 ) );
        addChild( new ForceArrowNode( transparent, Vector2D.v( STAGE_SIZE.width / 2 + 2, 200 ), rightForce, "Right Force", new Color( 202, 164, 129 ), TextLocation.SIDE, showValues, 1.0 ) );

        if ( showSumOfForces ) { addChild( new ForceArrowNode( transparent, Vector2D.v( STAGE_SIZE.width / 2, 125 ), leftForce + rightForce, "Sum of Forces", new Color( 143, 205, 154 ), TextLocation.TOP, showValues, 1.0 ) ); }
    }
}