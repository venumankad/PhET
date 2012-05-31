package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.Effect;
import fj.F;
import fj.F2;
import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.buildafraction.controller.ModelUpdate;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableFraction;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableNumberID;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableObject;
import edu.colorado.phet.fractionsintro.buildafraction.model.FractionID;
import edu.colorado.phet.fractionsintro.buildafraction.model.Mode;
import edu.colorado.phet.fractionsintro.common.util.DefaultP2;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.view.UpdateNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState.RELEASE_ALL;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.Color.black;

/**
 * Main simulation canvas for "build a fraction" tab
 * TODO: duplicated code in pieceTool pieceGraphic DraggablePieceNode
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas {
    public static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );
    public final RichPNode picturesContainerLayer;
    public final RichPNode numbersContainerLayer;
    private final BuildAFractionModel model;

    private static final int rgb = 240;
    public static final Color CONTROL_PANEL_BACKGROUND = new Color( rgb, rgb, rgb );
    public static final Stroke controlPanelStroke = new BasicStroke( 2 );

    public BuildAFractionCanvas( final BuildAFractionModel model ) {
        this.model = model;
        setBackground( Color.white );

        final SettableProperty<Mode> mode = model.toProperty(
                new F<BuildAFractionState, Mode>() {
                    @Override public Mode f( final BuildAFractionState s ) {
                        return s.mode;
                    }
                },
                new F2<BuildAFractionState, Mode, BuildAFractionState>() {
                    @Override public BuildAFractionState f( final BuildAFractionState s, final Mode mode ) {
                        return s.withMode( mode );
                    }
                }
        );

        //The draggable containers
        picturesContainerLayer = new RichPNode();
        numbersContainerLayer = new RichPNode();

        //View to show when the user is guessing numbers (by creating pictures)
        final PNode numberView = new NumberView( model, mode, this );
        final PNode pictureView = new PictureView( model, mode, this );

        //Adding this listener before calling the update allows us to get the ChangeObserver callback.
        final DraggableFraction draggableFraction = new DraggableFraction( FractionID.nextID(), new DraggableObject( new Vector2D( 350, 350 ), true ), Option.<DefaultP2<DraggableNumberID, Double>>none(), Option.<DefaultP2<DraggableNumberID, Double>>none() );
        picturesContainerLayer.addChild( new DraggableFractionNode( draggableFraction.getID(), model, this ) );

        //Change the model
        model.update( new ModelUpdate() {
            public BuildAFractionState update( final BuildAFractionState state ) {
                return state.addDraggableFraction( draggableFraction );
            }
        } );

        //When the mode changes, update the toolboxes
        addChild( new UpdateNode( new Effect<PNode>() {
            @Override public void e( final PNode node ) {
                node.addChild( mode.get() == Mode.PICTURES ? numberView : pictureView );
            }
        }, mode ) );

        //Reset all button
        addChild( new ResetAllButtonNode( model, this, 18, Color.black, Color.orange ) {{
            setConfirmationEnabled( false );
            setOffset( STAGE_SIZE.width - this.getFullWidth() - INSET, STAGE_SIZE.height - this.getFullHeight() - INSET );
        }} );
    }

    public static PNode createModeControlPanel( final SettableProperty<Mode> mode ) {
        return new HBox( radioButton( Components.picturesRadioButton, Strings.PICTURES, mode, Mode.PICTURES ),
                         radioButton( Components.numbersRadioButton, Strings.NUMBERS, mode, Mode.NUMBERS ) ) {{
            setOffset( AbstractFractionsCanvas.INSET, AbstractFractionsCanvas.INSET );
        }};
    }

    public static PNode emptyFractionGraphic( boolean showNumeratorOutline, boolean showDenominatorOutline ) {
        final VBox box = new VBox( box( showNumeratorOutline ), divisorLine(), box( showDenominatorOutline ) );

        //Show a background behind it to make the entire shape draggable
        final PhetPPath background = new PhetPPath( RectangleUtils.expand( box.getFullBounds(), 5, 5 ), TRANSPARENT );
        return new RichPNode( background, box );
    }

    private static PNode divisorLine() { return new PhetPPath( new Line2D.Double( 0, 0, 50, 0 ), new BasicStroke( 4, CAP_ROUND, JOIN_MITER ), black ); }

    private static PhetPPath box( boolean showOutline ) {
        return new PhetPPath( new Rectangle2D.Double( 0, 0, 40, 50 ), new BasicStroke( 2, BasicStroke.CAP_SQUARE, JOIN_MITER, 1, new float[] { 10, 6 }, 0 ), showOutline ? Color.red : TRANSPARENT );
    }

    public static PNode radioButton( IUserComponent component, final String text, final SettableProperty<Mode> mode, Mode value ) {
        return new PSwing( new PropertyRadioButton<Mode>( component, text, mode, value ) {{
            setOpaque( false );
            setFont( AbstractFractionsCanvas.CONTROL_FONT );
        }} );
    }

    //Find what draggable fraction node the specified DraggableNumberNode is over for purposes of snapping/attaching
    public Option<DraggableFractionNode> getDraggableNumberNodeDropTarget( final DraggableNumberNode draggableNumberNode ) {
        for ( PNode node : picturesContainerLayer.getChildren() ) {
            //TODO: could split into 2 subnodes to segregate types
            if ( node instanceof DraggableFractionNode ) {
                DraggableFractionNode draggableFractionNode = (DraggableFractionNode) node;
                if ( draggableFractionNode.getGlobalFullBounds().intersects( draggableNumberNode.getGlobalFullBounds() ) ) {
                    return Option.some( draggableFractionNode );
                }
            }
        }
        return Option.none();
    }

    public DraggableFractionNode getDraggableFractionNode( final FractionID fractionID ) {
        for ( PNode node : picturesContainerLayer.getChildren() ) {
            //TODO: could split into 2 subnodes to segregate types
            if ( node instanceof DraggableFractionNode ) {
                DraggableFractionNode draggableFractionNode = (DraggableFractionNode) node;
                if ( draggableFractionNode.id.equals( fractionID ) ) {
                    return draggableFractionNode;
                }
            }
        }
        throw new RuntimeException( "Not found" );
    }

    //When the user drops a DraggableNumberNode (either from dragging from the toolbox or from a draggable node), this code
    //checks and attaches it to the target fractions (if any)
    public void draggableNumberNodeReleased( DraggableNumberNode node ) {

        Option<DraggableFractionNode> target = getDraggableNumberNodeDropTarget( node );
//                                System.out.println( "target = " + target );
        if ( target.isSome() ) {
            boolean numerator = node.getGlobalFullBounds().getCenterY() < target.some().getGlobalFullBounds().getCenterY();
//                                    System.out.println( "attaching, numerator = " + numerator );

            //Don't allow zero to attach to denominator
            final boolean triedToDivideByZero = !numerator && model.state.get().getDraggableNumber( node.id ).some().number == 0;

            //Make sure nothing already there
            final DraggableFraction targetModel = model.state.get().getDraggableFraction( target.some().id ).some();
            final boolean somethingInNumerator = targetModel.numerator.isSome();
            final boolean somethingInDenominator = targetModel.denominator.isSome();
            boolean somethingAlreadyThere = ( numerator && somethingInNumerator ) || ( !numerator && somethingInDenominator );

            if ( triedToDivideByZero || somethingAlreadyThere ) {
                //illegal, do not do
            }
            else {
                model.attachNumberToFraction( node.id, target.some().id, numerator );
            }
        }
        else {
            //                                model.draggableNumberNodeDropped( id );
            model.update( RELEASE_ALL );
        }
    }
}