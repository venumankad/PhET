// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;

/**
 * Visual representation of a line in simplified point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PointSlopeLineNode extends LineNode {

    public PointSlopeLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        super( line, graph, mvt );
    }

    // Creates the line's equation in point-slope form.
    protected EquationNode createEquationNode( Line line, PhetFont font ) {
        return new PointSlopeEquationFactory().createSimplifiedNode( line, font );
    }
}
