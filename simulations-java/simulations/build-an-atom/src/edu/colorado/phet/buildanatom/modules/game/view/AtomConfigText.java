/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class defines a PNode that is a textual list of an atom's
 * configuration.
 *
 * @author John Blanco
 */
public class AtomConfigText extends PNode {

    private static final Font LABEL_FONT = new PhetFont(30);
    private static final double VERTICAL_LABEL_SPACING = 10;
    private static final double MIN_LABEL_TO_VALUE_SPACING = 20;

    public AtomConfigText( int numProtons, int numNeutrons, int numElectrons ) {
        PText protonLabel = new PText("Protons:"){{ setFont( LABEL_FONT ); }};
        PText protonValue = new PText( Integer.toString( numProtons ) ){{ setFont( LABEL_FONT ); }};
        PText neutronLabel = new PText("Neutrons:"){{ setFont( LABEL_FONT ); }};
        PText neutronValue = new PText( Integer.toString( numNeutrons ) ){{ setFont( LABEL_FONT ); }};
        PText electronLabel = new PText("Electrons:"){{ setFont( LABEL_FONT ); }};
        PText electronValue = new PText( Integer.toString( numElectrons ) ){{ setFont( LABEL_FONT ); }};
        double width = Math.max(
                protonLabel.getFullBoundsReference().width + protonValue.getFullBoundsReference().width + MIN_LABEL_TO_VALUE_SPACING,
                neutronLabel.getFullBoundsReference().width + neutronValue.getFullBoundsReference().width + MIN_LABEL_TO_VALUE_SPACING );
        width = Math.max( width, electronLabel.getFullBoundsReference().width + electronValue.getFullBoundsReference().width + MIN_LABEL_TO_VALUE_SPACING );
        protonLabel.setOffset( 0, 0 );
        protonValue.setOffset( width - protonValue.getFullBoundsReference().width, 0 );
        neutronLabel.setOffset( 0, protonLabel.getFullBoundsReference().getMaxY() + VERTICAL_LABEL_SPACING );
        neutronValue.setOffset( width - neutronValue.getFullBoundsReference().width, neutronLabel.getOffset().getY() );
        electronLabel.setOffset( 0, neutronLabel.getFullBoundsReference().getMaxY() + VERTICAL_LABEL_SPACING );
        electronValue.setOffset( width - electronValue.getFullBoundsReference().width, electronLabel.getOffset().getY() );

        addChild( protonLabel );
        addChild( protonValue );
        addChild( neutronLabel );
        addChild( neutronValue );
        addChild( electronLabel );
        addChild( electronValue );
    }
}
