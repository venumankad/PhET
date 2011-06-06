// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.view;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.model.*;
import edu.colorado.phet.buildamolecule.model.CollectionList.Adapter;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.buildamolecule.BuildAMoleculeConstants.MODEL_VIEW_TRANSFORM;

/**
 * Common canvas for Build a Molecule. It features kits shown at the bottom. Can be extended to add other parts
 */
public class BuildAMoleculeCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // View
    private final PNode _rootNode;

    private CollectionBoxHintNode collectionBoxHintNode = null;

    // used for notifying others of the collection area attachment. TODO consider changing class initialization so this can be moved to MoleculeCollectingCanvas
    protected java.util.List<SimpleObserver> collectionAttachmentListeners = new LinkedList<SimpleObserver>();

    public final CollectionList collectionList;
    protected boolean singleCollectionMode; // TODO: find solution for LargerMoleculesCanvas so that we don't need this boolean and the separate constructor

    protected void addChildren() {

    }

    public BuildAMoleculeCanvas( CollectionList collectionList ) {
        this( collectionList, true );
    }

    public BuildAMoleculeCanvas( CollectionList collectionList, boolean singleCollectionMode ) {
        this.collectionList = collectionList;
        this.singleCollectionMode = singleCollectionMode;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAMoleculeConstants.STAGE_SIZE ) );

        setBackground( BuildAMoleculeConstants.CANVAS_BACKGROUND_COLOR );

        addChildren();

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );

        final KitCollection firstCollection = collectionList.currentCollection.get();

        addCollection( firstCollection );

        collectionList.addListener( new Adapter() {
            public void addedCollection( KitCollection collection ) {
                addCollection( collection );
            }
        } );

        /*---------------------------------------------------------------------------*
        * collection box hint arrow. add this only to the 1st collection
        *----------------------------------------------------------------------------*/

        for ( final Kit kit : firstCollection.getKits() ) {
            kit.addMoleculeListener( new Kit.MoleculeAdapter() {
                @Override public void addedMolecule( Molecule molecule ) {
                    CollectionBox targetBox = firstCollection.getFirstTargetBox( molecule );

                    // if a hint doesn't exist AND we have a target box, add it
                    if ( collectionBoxHintNode == null && targetBox != null ) {
                        collectionBoxHintNode = new CollectionBoxHintNode( molecule, targetBox );
                        addWorldChild( collectionBoxHintNode );
                    }
                    else if ( collectionBoxHintNode != null ) {
                        // otherwise clear any other hint nodes
                        collectionBoxHintNode.disperse();
                    }
                }

                @Override public void removedMolecule( Molecule molecule ) {
                    // clear any existing hint node on molecule removal
                    if ( collectionBoxHintNode != null ) {
                        collectionBoxHintNode.disperse();
                    }
                }
            } );

            // whenever a kit switch happens, remove the arrow
            kit.visible.addObserver( new SimpleObserver() {
                public void update() {
                    // clear any existing hint node on molecule removal
                    if ( collectionBoxHintNode != null ) {
                        collectionBoxHintNode.disperse();
                    }
                }
            } );
        }
    }

    public KitCollectionNode addCollection( KitCollection collection ) {
        KitCollectionNode result = new KitCollectionNode( collectionList, collection, this );
        addWorldChild( result );

        // return this so we can manipulate it in an override
        return result;
    }

    public KitCollection getCurrentCollection() {
        return collectionList.currentCollection.get();
    }

    /*
    * Updates the layout of stuff on the canvas.
    */
    @Override
    protected void updateLayout() {
        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
    }

    /**
     * Returns model bounds from a piccolo node, given local coordinates on a piccolo node.
     */
    public final Function1<PNode, Rectangle2D> toModelBounds = new Function1<PNode, Rectangle2D>() {
        public Rectangle2D apply( PNode node ) {
            // this requires getting local => global => view => model coordinates

            // our bounds relative to the root Piccolo canvas
            Rectangle2D globalBounds = node.getParent().localToGlobal( node.getFullBounds() );

            // pull out the upper-left corner and dimension so we can transform them
            Point2D upperLeftCorner = new Point2D.Double( globalBounds.getX(), globalBounds.getY() );
            PDimension dimensions = new PDimension( globalBounds.getWidth(), globalBounds.getHeight() );

            // transform the point and dimensions to world coordinates
            getPhetRootNode().globalToWorld( upperLeftCorner );
            getPhetRootNode().globalToWorld( dimensions );

            // our bounds relative to our simulation (BAM) canvas. Will be filled in
            Rectangle2D viewBounds = new Rectangle2D.Double( upperLeftCorner.getX(), upperLeftCorner.getY(), dimensions.getWidth(), dimensions.getHeight() );

            // return the model bounds
            return MODEL_VIEW_TRANSFORM.viewToModel( viewBounds ).getBounds2D();
        }
    };
}
