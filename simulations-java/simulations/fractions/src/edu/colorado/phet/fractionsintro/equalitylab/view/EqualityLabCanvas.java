// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.equalitylab.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.RadioButtonStripControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.RadioButtonStripControlPanelNode.Element;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.common.view.Colors;
import edu.colorado.phet.fractionsintro.equalitylab.model.EqualityLabModel;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.view.FractionControlNode;
import edu.colorado.phet.fractionsintro.intro.view.NumberLineNode;
import edu.colorado.phet.fractionsintro.intro.view.NumberLineNode.Vertical;
import edu.colorado.phet.fractionsintro.intro.view.Representation;
import edu.colorado.phet.fractionsintro.intro.view.RepresentationNode;
import edu.colorado.phet.fractionsintro.intro.view.WaterGlassSetNode;
import edu.colorado.phet.fractionsintro.intro.view.pieset.MovableSliceLayer;
import edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.HorizontalBarIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.NumberLineIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.PieIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.WaterGlassIcon;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractionsintro.equalitylab.model.EqualityLabModel.scaledFactorySet;
import static edu.colorado.phet.fractionsintro.intro.view.Representation.*;
import static edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode.CreateEmptyCellsNode;
import static edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode.NodeToShape;
import static java.awt.Color.black;
import static java.awt.Color.orange;

/**
 * Canvas for "Equality Lab" tab.
 *
 * @author Sam Reid
 */
public class EqualityLabCanvas extends AbstractFractionsCanvas {

    public EqualityLabCanvas( final EqualityLabModel model ) {

        final SettableProperty<Representation> leftRepresentation = model.leftRepresentation;
        final SettableProperty<Representation> rightRepresentation = model.rightRepresentation;
        final SettableProperty<Boolean> sameAsLeft = model.sameAsLeft;

        //Control panel for choosing different representations, can be split into separate controls for each display
        final double representationControlPanelScale = 1.0;
        final int padding = 7;
        final RichPNode leftRepresentationControlPanel = new ZeroOffsetNode( new RadioButtonStripControlPanelNode<Representation>( leftRepresentation, getIcons( leftRepresentation, FractionsIntroSimSharing.green ), padding ) {{ scale( representationControlPanelScale ); }} ) {{
            setOffset( 114, INSET );
        }};

        // Instead of "same" and "number line" radio buttons, what we envisioned on the right hand side
        // was one button that is the same as the representation on the left, and another that was the number line.
        // So for instance if you were on the green circle, and clicked on the red square,
        // the button on the right would change to the red square.
        final RichPNode rightRepresentationControlPanel = new ZeroOffsetNode( new PNode() {{
            leftRepresentation.addObserver( new VoidFunction1<Representation>() {
                public void apply( final Representation representation ) {
                    removeAllChildren();

                    final List<Element<Representation>> icons = getIcons( leftRepresentation, FractionsIntroSimSharing.blue );
                    final List<Element<Representation>> keepIcons = new ArrayList<Element<Representation>>();
                    for ( Element<Representation> icon : icons ) {
                        if ( icon.value == model.leftRepresentation.get() || icon.value == NUMBER_LINE ) {
                            keepIcons.add( icon );
                        }
                    }
                    PNode content = new RadioButtonStripControlPanelNode<Representation>( leftRepresentation, keepIcons, padding ) {{ scale( representationControlPanelScale ); }};
                    addChild( content );
                }
            } );
        }} ) {{
            setOffset( leftRepresentationControlPanel.getMaxX() + 120, leftRepresentationControlPanel.getCenterY() - getFullBounds().getHeight() / 2 );
        }};

        addChildren( leftRepresentationControlPanel, rightRepresentationControlPanel );

        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
            }
        }, this, CONTROL_FONT, black, orange ) {{
            setConfirmationEnabled( false );
            setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - INSET, STAGE_SIZE.height - getFullBounds().getHeight() - INSET );
        }};
        addChild( resetAllButtonNode );

        //Show the icon text on the left so that it will be far from the main fraction display in the play area
        boolean iconTextOnTheRight = false;

        addPrimaryRepresentationNodes( model, leftRepresentation, model.pieSet, iconTextOnTheRight );

        //Show the pie set node when selected for the right-side
        addChild( new RepresentationNode( rightRepresentation, PIE, new PNode() {{
            model.scaledPieSet.addObserver( new SimpleObserver() {
                public void update() {
                    removeAllChildren();
                    addChild( CreateEmptyCellsNode.f( model.scaledPieSet.get() ) );
                    addChild( new MovableSliceLayer( model.scaledPieSet.get(), NodeToShape, model.scaledPieSet, rootNode, null ) );
                }
            } );
            setChildrenPickable( false );
        }} ) );

        //Show the horizontal bar set node when selected for the right-side
        addChild( new RepresentationNode( rightRepresentation, HORIZONTAL_BAR, new PNode() {{
            model.rightHorizontalBars.addObserver( new SimpleObserver() {
                public void update() {
                    removeAllChildren();
                    addChild( CreateEmptyCellsNode.f( model.rightHorizontalBars.get() ) );
                    addChild( new MovableSliceLayer( model.rightHorizontalBars.get(), NodeToShape, model.rightHorizontalBars, rootNode, null ) );
                }
            } );
            setChildrenPickable( false );
        }} ) );

        //Show the water glasses when selected for the right-side
        addChild( new RepresentationNode( rightRepresentation, WATER_GLASSES, new PNode() {{
            model.rightWaterGlasses.addObserver( new SimpleObserver() {
                public void update() {
                    removeAllChildren();
                    final Shape shape = scaledFactorySet.waterGlassSetFactory.createSlicesForBucket( model.denominator.get(), 1, model.getRandomSeed() ).head().getShape();
                    addChild( WaterGlassSetNode.createEmptyCellsNode( Colors.LIGHT_BLUE, shape.getBounds2D().getWidth(), shape.getBounds2D().getHeight() ).f( model.rightWaterGlasses.get() ) );
                }
            } );
            setChildrenPickable( false );
        }} ) );

        //Number line

        //Experiment with making semi-transparent
//        addChild( new NumberLineNode( model.scaledNumerator, null, model.scaledDenominator, model.rightRepresentation.valueEquals( NUMBER_LINE ), model.maximum, new Vertical(), 15, new Color( LIGHT_BLUE.getRed(), LIGHT_BLUE.getGreen(), LIGHT_BLUE.getBlue(), 200 ) ) {{
        addChild( new NumberLineNode( model.scaledNumerator, null, model.scaledDenominator, model.rightRepresentation.valueEquals( NUMBER_LINE ), model.maximum, new Vertical(), 15, new Color( Colors.LIGHT_BLUE.getRed(), Colors.LIGHT_BLUE.getGreen(), Colors.LIGHT_BLUE.getBlue(), 200 ), true ) {{
            setOffset( 385 + 200, 445 );

            //Can't interact with right-side representations
            setPickable( false );
            setChildrenPickable( false );
        }} );

        //The fraction control node.  In front so the user doesn't accidentally press a flying pie slice when they are trying to toggle the spinner
        final ZeroOffsetNode fractionControl = new ZeroOffsetNode( new FractionControlNode( model.numerator, model.denominator, model.maximum, 6 ) {{
            setScale( 0.75 );
        }} ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullWidth() - 50, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }};
        addChild( fractionControl );

        final PhetPText equalsSign = new PhetPText( "=", new PhetFont( 120 ) ) {{
            setOffset( fractionControl.getMaxX() + 10, fractionControl.getCenterY() - getFullHeight() / 2 );
        }};
        addChild( equalsSign );

        addChild( new ZeroOffsetNode( new ScaledUpFractionNode( model.numerator, model.denominator, model.scale ) {{setScale( 0.75 );}} ) {{
            setOffset( equalsSign.getMaxX() + 10, equalsSign.getCenterY() - getFullHeight() / 2 );
        }} );
    }

    //Add representations for the left side
    private void addPrimaryRepresentationNodes( final EqualityLabModel model,
                                                final SettableProperty<Representation> representation,
                                                SettableProperty<PieSet> pieSet, boolean iconTextOnTheRight ) {
        //Show the pie set node when pies are selected
        addChild( new RepresentationNode( representation, PIE, new PieSetNode( pieSet, rootNode, iconTextOnTheRight ) ) );

        //For horizontal bars
        addChild( new RepresentationNode( representation, HORIZONTAL_BAR, new PieSetNode( model.horizontalBarSet, rootNode, iconTextOnTheRight ) ) );

        //For water glasses
        final Rectangle2D b = model.primaryFactorySet.waterGlassSetFactory.createEmptyPies( 1, 1 ).head().cells.head().getShape().getBounds2D();
        addChild( new RepresentationNode( representation, WATER_GLASSES, new WaterGlassSetNode( model.waterGlassSet, rootNode, Colors.LIGHT_GREEN, b.getWidth(), b.getHeight(), iconTextOnTheRight ) ) );

        //Number line
        addChild( new NumberLineNode( model.numerator, model.numerator, model.denominator, representation.valueEquals( NUMBER_LINE ), model.maximum, new Vertical(), 15, Colors.LIGHT_GREEN, true ) {{
            setOffset( 385, 445 );
        }} );
    }

    private List<Element<Representation>> getIcons( SettableProperty<Representation> representation, String type ) {
        return Arrays.asList( new Element<Representation>( new PieIcon( representation, Colors.CIRCLE_COLOR ), PIE, UserComponentChain.chain( Components.pieRadioButton, type ) ),
                              new Element<Representation>( new HorizontalBarIcon( representation, Colors.HORIZONTAL_SLICE_COLOR ) {{scale( 0.8 );}}, HORIZONTAL_BAR, UserComponentChain.chain( Components.horizontalBarRadioButton, type ) ),
                              new Element<Representation>( new WaterGlassIcon( representation, Colors.CUP_COLOR ) {{scale( 0.8 );}}, WATER_GLASSES, UserComponentChain.chain( Components.waterGlassesRadioButton, type ) ),
                              new Element<Representation>( new NumberLineIcon( representation ), NUMBER_LINE, UserComponentChain.chain( Components.numberLineRadioButton, type ) ) );
    }
}