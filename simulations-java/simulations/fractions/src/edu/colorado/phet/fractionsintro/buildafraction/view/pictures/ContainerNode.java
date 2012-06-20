package edu.colorado.phet.fractionsintro.buildafraction.view.pictures;

import fj.F;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.util.FJUtils;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Some copied from NumberNode, may need to be remerged.
 *
 * @author Sam Reid
 */
public class ContainerNode extends PNode {
    private double initialX;
    private double initialY;
    private final PictureSceneNode parent;
    private int number;
    private final ContainerContext context;

    public static final double width = 130;
    public static final double height = 55;
    private PImage splitButton;
    private final DynamicCursorHandler dynamicCursorHandler;
    private boolean inTargetCell = false;

    public ContainerNode( PictureSceneNode parent, final int number, final ContainerContext context ) {
        this.parent = parent;
        this.number = number;
        this.context = context;
        PNode content = new PNode() {{
            for ( int i = 0; i < number; i++ ) {
                final double pieceWidth = width / number;
                addChild( new PhetPPath( new Rectangle2D.Double( pieceWidth * i, 0, pieceWidth, height ), Color.white, new BasicStroke( 1 ), Color.black ) );
            }
            //Thicker outer stroke
            addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, width, height ), new BasicStroke( 2 ), Color.black ) );
            addInputEventListener( new SimSharingDragHandler( null, true ) {
                @Override protected void drag( final PInputEvent event ) {
                    super.drag( event );
                    final PDimension delta = event.getDeltaRelativeTo( getParent() );
                    ContainerNode.this.translate( delta.width, delta.height );
                }

                @Override protected void endDrag( final PInputEvent event ) {
                    super.endDrag( event );
                    context.endDrag( ContainerNode.this, event );
                }
            } );
            addInputEventListener( new CursorHandler() );
        }};

        addChild( content );

        splitButton = new PImage( Images.SPLIT_BLUE );
        addChild( splitButton );
        splitButton.setVisible( false );
        splitButton.setPickable( false );
        splitButton.translate( -splitButton.getFullBounds().getWidth(),
                               -splitButton.getFullBounds().getHeight() );
        dynamicCursorHandler = new DynamicCursorHandler( Cursor.HAND_CURSOR );
        splitButton.addInputEventListener( dynamicCursorHandler );
        splitButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( final PInputEvent event ) {
                SimSharingManager.sendButtonPressed( null );
                splitAll();
                dynamicCursorHandler.setCursor( Cursor.DEFAULT_CURSOR );
            }
        } );
    }

    private void splitAll() {
        int numPieces = getChildPieces().length();
        double separationBetweenPieces = 4;
        double totalDeltaSpacing = separationBetweenPieces * ( numPieces - 1 );
        int index = 0;
        LinearFunction f = new LinearFunction( 0, numPieces - 1, -totalDeltaSpacing / 2, totalDeltaSpacing / 2 );
        for ( RectangularPiece child : getChildPieces() ) {
            parent.splitPieceFromContainer( child, this, numPieces == 1 ? 0 : f.evaluate( index++ ) );
        }
        PInterpolatingActivity activity = splitButton.animateToTransparency( 0, 200 );
        activity.setDelegate( new PActivityDelegate() {
            public void activityStarted( final PActivity activity ) {
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {
                splitButton.setVisible( false );
                splitButton.setPickable( false );
                dynamicCursorHandler.setCursor( Cursor.DEFAULT_CURSOR );
            }
        } );
        context.syncModelFractions();
    }

    public void setAllPickable( final boolean b ) {
        setPickable( b );
        setChildrenPickable( b );
    }

    public void setInitialPosition( final double x, final double y ) {
        this.initialX = x;
        this.initialY = y;
        setOffset( x, y );
    }

    public double getInitialX() { return initialX; }

    public double getInitialY() { return initialY; }

    public void animateHome() { animateToPositionScaleRotation( getInitialX(), getInitialY(), 1, 0, 200 ); }

    public void addPiece( final RectangularPiece piece ) {
        Point2D offset = piece.getGlobalTranslation();
        addChild( piece );
        piece.setGlobalTranslation( offset );
        if ( !splitButton.getVisible() ) {
            splitButton.setVisible( true );
            splitButton.setPickable( true );
            splitButton.setTransparency( 0 );
            splitButton.animateToTransparency( 1, 200 );
            dynamicCursorHandler.setCursor( Cursor.HAND_CURSOR );
        }
    }

    public static Rectangle2D.Double createRect( int number ) {
        final double pieceWidth = width / number;
        return new Rectangle2D.Double( pieceWidth * number, 0, pieceWidth, height );
    }

    //How far over should a new piece be added in?
    public double getPiecesWidth() {
        List<RectangularPiece> children = getChildPieces();
        return children.length() == 0 ? 0 :
               fj.data.List.iterableList( children ).maximum( FJUtils.ord( new F<RectangularPiece, Double>() {
                   @Override public Double f( final RectangularPiece r ) {
                       return r.getFullBounds().getMaxX();
                   }
               } ) ).getFullBounds().getMaxX();
    }

    private List<RectangularPiece> getChildPieces() {
        ArrayList<RectangularPiece> children = new ArrayList<RectangularPiece>();
        for ( Object c : getChildrenReference() ) {
            if ( c instanceof RectangularPiece ) {
                children.add( (RectangularPiece) c );
            }
        }
        return List.iterableList( children );
    }

    public Fraction getFractionValue() {
        return Fraction.sum( getChildPieces().map( new F<RectangularPiece, Fraction>() {
            @Override public Fraction f( final RectangularPiece r ) {
                return r.toFraction();
            }
        } ) );
    }

    public static F<ContainerNode, Fraction> _getFractionValue = new F<ContainerNode, Fraction>() {
        @Override public Fraction f( final ContainerNode containerNode ) {
            return containerNode.getFractionValue();
        }
    };

    //Get rid of it because it disrupts the layout when dropping into the scoring cell.
    public void removeSplitButton() { removeChild( splitButton ); }

    public void addBackSplitButton() { addChild( splitButton ); }

    public boolean isAtStartingLocation() { return getXOffset() == initialX && getYOffset() == initialY; }

    public Boolean isInTargetCell() {return inTargetCell;}

    public static F<ContainerNode, Boolean> _isInTargetCell = new F<ContainerNode, Boolean>() {
        @Override public Boolean f( final ContainerNode containerNode ) {
            return containerNode.isInTargetCell();
        }
    };

    public void setInTargetCell( final boolean inTargetCell ) { this.inTargetCell = inTargetCell; }
}