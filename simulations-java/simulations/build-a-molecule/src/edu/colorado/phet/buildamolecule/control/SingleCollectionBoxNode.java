// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Allows the collection of a single molecule
 */
public class SingleCollectionBoxNode extends SwingLayoutNode {
    public SingleCollectionBoxNode( final BuildAMoleculeCanvas canvas, final CollectionBox box ) {
        super( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;

        // TODO: i18nize
        addChild( new HTMLNode( box.getMoleculeType().getMoleculeStructure().getMolecularFormulaHTMLFragment() + " (" + box.getMoleculeType().getCommonName() + ")" ) {{
            setFont( new PhetFont( 16, true ) );
        }}, c );

        c.gridy = 1;
        c.insets = new Insets( 3, 0, 0, 0 ); // some padding between the two
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 160, 50 ), Color.BLACK ) {{
            canvas.addFullyLayedOutObserver( new SimpleObserver() {
                public void update() {
                    Rectangle2D globalBounds = localToGlobal( getFullBounds() );
                    Rectangle2D viewBounds = new Rectangle2D.Double();
                    Point2D upperLeftCorner = new Point2D.Double( globalBounds.getX(), globalBounds.getY() );
                    PDimension dimensions = new PDimension( globalBounds.getWidth(), globalBounds.getHeight() );
                    canvas.getPhetRootNode().globalToWorld( upperLeftCorner );
                    canvas.getPhetRootNode().globalToWorld( dimensions );
                    viewBounds.setFrame( upperLeftCorner, dimensions );
                    box.setDropBounds( canvas.getModelViewTransform().viewToModel( viewBounds ).getBounds2D() );

                    System.out.println( "box: " + box.getMoleculeType().getCommonName() );
                    System.out.println( "getFullBounds() = " + getFullBounds() );
                    System.out.println( "globalBounds = " + globalBounds );
                    System.out.println( "viewBounds = " + viewBounds );
                    System.out.println( "box.getDropBounds() = " + box.getDropBounds() );

                    canvas.addWorldChild( new PhetPPath( viewBounds, Color.RED ) {{
                        setTransparency( 0.5f );
                    }} );

                    addChild( new PhetPPath( getFullBounds(), Color.GREEN ) {{
                        setTransparency( 0.5f );
                    }} );
                }
            } );
//            canvas.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
//                public void propertyChange( PropertyChangeEvent evt ) {
//
//                }
//            } );
        }}, c );
    }
}
