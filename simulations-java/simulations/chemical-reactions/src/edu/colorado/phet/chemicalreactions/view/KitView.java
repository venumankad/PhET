// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.view;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.chemicalreactions.model.Kit;
import edu.colorado.phet.chemicalreactions.model.Molecule;
import edu.colorado.phet.chemicalreactions.model.MoleculeBucket;
import edu.colorado.phet.chemicalreactions.model.MoleculeShape;
import edu.colorado.phet.chemicalreactions.model.Reaction;
import edu.colorado.phet.chemicalreactions.module.ChemicalReactionsCanvas;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.ARROW_VIEW_HEAD_LENGTH;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.ARROW_VIEW_HEAD_WIDTH;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.ARROW_VIEW_PADDING;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.ARROW_VIEW_THICKNESS;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.OPERATOR_BORDER_COLOR;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.OPERATOR_COLOR;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.PLUS_VIEW_LENGTH;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.PLUS_VIEW_THICKNESS;

/**
 * Shows a kit (series of buckets full of different types of atoms), along with the atoms in the play area
 */
public class KitView {
    private PNode debugOverlayLayer = new PNode();
    private PNode topLayer = new PNode();
    private PNode metadataLayer = new PNode();
    private PNode atomLayer = new PNode();
    private PNode bottomLayer = new PNode();

    private final Kit kit;
    private final ChemicalReactionsCanvas canvas;

    private final Map<MoleculeBucket, MoleculeBucketNode> bucketMap = new HashMap<MoleculeBucket, MoleculeBucketNode>();

    // store the node-atom relationships
//    private Map<Atom, LabeledAtomNode> atomNodeMap = new HashMap<Atom, LabeledAtomNode>();

    public KitView( final Kit kit, ChemicalReactionsCanvas canvas ) {
        this.kit = kit;
        this.canvas = canvas;

        for ( MoleculeBucket bucket : kit.getBuckets() ) {
            MoleculeBucketNode bucketView = new MoleculeBucketNode( bucket );
            bucketMap.put( bucket, bucketView );

            topLayer.addChild( bucketView.getFrontNode() );
            bottomLayer.addChild( bucketView.getHoleNode() );

            for ( final Molecule molecule : bucket.getMolecules() ) {
                atomLayer.addChild( new MoleculeNode( molecule ) {{
                    addInputEventListener( new PDragEventHandler() {
                        @Override public void mouseDragged( PInputEvent event ) {
                            final PDimension delta = event.getDeltaRelativeTo( getParent() );
                            final Dimension2D modelDelta = MODEL_VIEW_TRANSFORM.viewToModelDelta( delta );
                            molecule.setPosition( molecule.position.get().plus( modelDelta ) );
                        }

                        @Override protected void startDrag( PInputEvent event ) {
                            super.startDrag( event );
                            kit.dragStart( molecule );
                            molecule.userControlled.set( true );
                        }

                        @Override protected void endDrag( PInputEvent event ) {
                            super.endDrag( event );
                            kit.dragEnd( molecule );
                            molecule.userControlled.set( false );
                        }
                    } );
                }} );
            }
        }

        // pluses between groups of reactant or product buckets
        for ( List<MoleculeBucket> moleculeBuckets : Arrays.asList( kit.getReactantBuckets(), kit.getProductBuckets() ) ) {
            for ( int i = 1; i < moleculeBuckets.size(); i++ ) {
                addPlusBetween( moleculeBuckets.get( i - 1 ), moleculeBuckets.get( i ) );
            }
        }

        // add an arrow between last reactant bucket and first product bucket
        addArrowBetween( kit.getReactantBuckets().get( kit.getReactantBuckets().size() - 1 ),
                         kit.getProductBuckets().get( 0 ) );

        // update visibility based on the kit visibility
        kit.visible.addObserver( new SimpleObserver() {
            public void update() {
                Boolean visible = kit.visible.get();
                debugOverlayLayer.setVisible( visible );
                topLayer.setVisible( visible );
                metadataLayer.setVisible( visible );
                atomLayer.setVisible( visible );
                bottomLayer.setVisible( visible );
            }
        } );

        kit.stepCompleted.addUpdateListener( new UpdateListener() {
            public void update() {
                debugOverlayLayer.removeAllChildren();
                for ( Reaction reaction : kit.getReactions() ) {
                    for ( int i = 0; i < reaction.reactants.size(); i++ ) {
                        Molecule molecule = reaction.reactants.get( i );
                        ImmutableVector2D targetPosition = reaction.getTarget().transformedTargets.get( i );
                        double angle = reaction.getTarget().rotation + reaction.getShape().reactantSpots.get( i ).rotation;

                        debugOverlayLayer.addChild( createDebugCircle( molecule.getPosition(),
                                                                       molecule.shape.getBoundingCircleRadius(),
                                                                       new Color( 0xAAAAFF ) ) );

                        ImmutableVector2D currentViewCenter = MODEL_VIEW_TRANSFORM.modelToView( molecule.getPosition() );
                        ImmutableVector2D targetViewCenter = MODEL_VIEW_TRANSFORM.modelToView( targetPosition );
                        debugOverlayLayer.addChild( new PhetPPath( new Line2D.Double( currentViewCenter.getX(), currentViewCenter.getY(),
                                                                                      targetViewCenter.getX(), targetViewCenter.getY() ),
                                                                   null, new BasicStroke( 1 ), Color.BLACK ) );

                        MoleculeShape moleculeShape = molecule.shape;

                        for ( MoleculeShape.AtomSpot spot : moleculeShape.spots ) {
                            ImmutableVector2D spotLocalPosition = spot.position;
                            ImmutableVector2D rotatedPosition = spotLocalPosition.getRotatedInstance( angle );
                            ImmutableVector2D translatedPosition = rotatedPosition.plus( targetPosition );
                            Color color = spot.element.getColor();
                            debugOverlayLayer.addChild( createDebugCircle( translatedPosition,
                                                                           spot.element.getRadius(),
                                                                           new Color(
                                                                                   ( color.getRed() + 128 ) / 2,
                                                                                   ( color.getGreen() + 128 ) / 2,
                                                                                   ( color.getBlue() + 128 ) / 2
                                                                           ) ) );
                        }
                    }
                }
            }
        }, false );
    }

    private PhetPPath createDebugCircle( ImmutableVector2D modelPoint, double modelRadius, Color color ) {
        ImmutableVector2D viewPoint = MODEL_VIEW_TRANSFORM.modelToView( modelPoint );
        double viewRadius = Math.abs( MODEL_VIEW_TRANSFORM.modelToViewDeltaX( modelRadius ) );

        return new PhetPPath( new Ellipse2D.Double( viewPoint.getX() - viewRadius, viewPoint.getY() - viewRadius,
                                                    viewRadius * 2, viewRadius * 2 ), null, new BasicStroke( 1 ), color );
    }

    public void addPlusBetween( MoleculeBucket leftBucket, MoleculeBucket rightBucket ) {
        // get the space in between our buckets
        double leftX = bucketMap.get( leftBucket ).getFrontNode().getFullBounds().getMaxX();
        double rightX = bucketMap.get( rightBucket ).getFrontNode().getFullBounds().getMinX();

        // center of the plus
        final double centerX = ( leftX + rightX ) / 2;
        final double centerY = kit.getLayoutBounds().getAvailableKitViewBounds().getCenterY();

        // construct it from two areas
        bottomLayer.addChild( new PhetPPath( new Area() {{
            add( new Area( new Rectangle2D.Double( centerX - PLUS_VIEW_THICKNESS / 2, centerY - PLUS_VIEW_LENGTH / 2,
                                                   PLUS_VIEW_THICKNESS, PLUS_VIEW_LENGTH ) ) );
            add( new Area( new Rectangle2D.Double( centerX - PLUS_VIEW_LENGTH / 2, centerY - PLUS_VIEW_THICKNESS / 2,
                                                   PLUS_VIEW_LENGTH, PLUS_VIEW_THICKNESS ) ) );
        }}, OPERATOR_COLOR, new BasicStroke( 1 ), OPERATOR_BORDER_COLOR ) );
    }

    public void addArrowBetween( MoleculeBucket leftBucket, MoleculeBucket rightBucket ) {
        // get the space in between our buckets (with padding, so the arrow bounds touch these)
        double leftX = bucketMap.get( leftBucket ).getFrontNode().getFullBounds().getMaxX() + ARROW_VIEW_PADDING;
        double rightX = bucketMap.get( rightBucket ).getFrontNode().getFullBounds().getMinX() - ARROW_VIEW_PADDING;

        final double centerY = kit.getLayoutBounds().getAvailableKitViewBounds().getCenterY();


        bottomLayer.addChild( new ArrowNode(
                new Point2D.Double( leftX, centerY ),
                new Point2D.Double( rightX, centerY ),
                ARROW_VIEW_HEAD_LENGTH,
                ARROW_VIEW_HEAD_WIDTH,
                ARROW_VIEW_THICKNESS
        ) {{
            setPaint( OPERATOR_COLOR );
            setStroke( new BasicStroke( 1 ) );
            setStrokePaint( OPERATOR_BORDER_COLOR );
        }} );
    }

    public PNode getDebugOverlayLayer() {
        return debugOverlayLayer;
    }

    public PNode getTopLayer() {
        return topLayer;
    }

    public PNode getMetadataLayer() {
        return metadataLayer;
    }

    public PNode getAtomLayer() {
        return atomLayer;
    }

    public PNode getBottomLayer() {
        return bottomLayer;
    }
}
