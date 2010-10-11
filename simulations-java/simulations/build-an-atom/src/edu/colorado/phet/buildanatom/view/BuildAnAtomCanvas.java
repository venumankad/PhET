/* Copyright 2007, University of Colorado */

package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.model.ElectronShell;
import edu.colorado.phet.buildanatom.model.Proton;
import edu.colorado.phet.buildanatom.module.BuildAnAtomDefaults;
import edu.colorado.phet.common.phetcommon.model.MutableBoolean;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas for the tab where the user builds an atom.
 */
public class BuildAnAtomCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private final BuildAnAtomModel model;

    // View
    private final PNode rootNode;

    // Transform.
    private final ModelViewTransform2D mvt;

    // Layers on the canvas.
    private final PNode backLayer = new PNode();
    private final PNode particleLayer = new PNode();
    private final PNode frontLayer = new PNode();

    // Reset button.
    private final GradientButtonNode resetButtonNode;
    private final ArrayList<MaximizeControlNode> maximizeControlNodeArrayList=new ArrayList<MaximizeControlNode>( );

    final MutableBoolean viewOrbitals = new MutableBoolean( true ){
        //Automatically calls back to observers when they are added
        //so clients do not need to call myObserver.update() each time
        //they use this pattern.
        @Override
        public void addObserver( SimpleObserver so ) {
            super.addObserver( so );
            so.update();
        }
    };

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildAnAtomCanvas( final BuildAnAtomModel model ) {

        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this, BuildAnAtomDefaults.STAGE_SIZE ) );

        //        mvt = new ModelViewTransform2D( model.getModelViewport(),
        //                new Rectangle2D.Double( 0, BuildAnAtomDefaults.STAGE_SIZE.getHeight() * ( 1 - 0.8 ),
        //                BuildAnAtomDefaults.STAGE_SIZE.getWidth() * 0.7, BuildAnAtomDefaults.STAGE_SIZE.getHeight() * 0.7 ) );

        // Set up the model-canvas transform.  IMPORTANT NOTES: The multiplier
        // factors for the point in the view can be adjusted to shift the
        // center right or left, and the scale factor can be adjusted to zoom
        // in or out (smaller numbers zoom out, larger ones zoom in).
        mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.30 ), (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.45 ) ),
                2.0,
                true );

        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Layers on the canvas.
        rootNode.addChild( backLayer );
        rootNode.addChild( particleLayer );
        rootNode.addChild( frontLayer );

        // Put up the bounds of the model.
        //        rootNode.addChild( new PhetPPath( mvt.createTransformedShape( model.getModelViewport() ), Color.PINK, new BasicStroke( 3f ), Color.BLACK ) );

        // Add the atom's nucleus location to the canvas.
        //        Shape nucleusOutlineShape = mvt.createTransformedShape( new Ellipse2D.Double(
        //                -model.getAtom().getNucleusRadius(),
        //                -model.getAtom().getNucleusRadius(),
        //                model.getAtom().getNucleusRadius() * 2,
        //                model.getAtom().getNucleusRadius() * 2 ) );
        //        PNode nucleusOutlineNode = new PhetPPath( nucleusOutlineShape, new BasicStroke(1f), Color.RED );
        //        backLayer.addChild( nucleusOutlineNode );
        DoubleGeneralPath nucleusXMarkerModelCoords = new DoubleGeneralPath();
        double xMarkerSize = Proton.RADIUS; // Arbitrary, adjust as desired.
        nucleusXMarkerModelCoords.moveTo( model.getAtom().getPosition().getX() - xMarkerSize / 2,
                model.getAtom().getPosition().getY() - xMarkerSize / 2 );
        nucleusXMarkerModelCoords.lineTo( model.getAtom().getPosition().getX() + xMarkerSize / 2,
                model.getAtom().getPosition().getY() + xMarkerSize / 2 );
        nucleusXMarkerModelCoords.moveTo( model.getAtom().getPosition().getX() - xMarkerSize / 2,
                model.getAtom().getPosition().getY() + xMarkerSize / 2 );
        nucleusXMarkerModelCoords.lineTo( model.getAtom().getPosition().getX() + xMarkerSize / 2,
                model.getAtom().getPosition().getY() - xMarkerSize / 2 );
        Shape nucleusXMarkerShape = mvt.createTransformedShape( nucleusXMarkerModelCoords.getGeneralPath() );
        PNode nucleusXMarkerNode = new PhetPPath( nucleusXMarkerShape, new BasicStroke( 4f ), new Color( 255, 0, 0, 75 ) );
        backLayer.addChild( nucleusXMarkerNode );

        // Add the atom's electron shells to the canvas.
        for ( ElectronShell electronShell : model.getAtom().getElectronShells() ) {
            backLayer.addChild( new ElectronShellNode( mvt, viewOrbitals, model.getAtom(), electronShell ) );
        }

        // Add the buckets that hold the sub-atomic particles.
        BucketNode electronBucketNode = new BucketNode( model.getElectronBucket(), mvt );
        electronBucketNode.setOffset( mvt.modelToViewDouble( model.getElectronBucket().getPosition() ) );
        backLayer.addChild( electronBucketNode.getHoleLayer() );
        frontLayer.addChild( electronBucketNode.getContainerLayer() );
        BucketNode protonBucketNode = new BucketNode( model.getProtonBucket(), mvt );
        protonBucketNode.setOffset( mvt.modelToViewDouble( model.getProtonBucket().getPosition() ) );
        backLayer.addChild( protonBucketNode.getHoleLayer() );
        frontLayer.addChild( protonBucketNode.getContainerLayer() );
        BucketNode neutronBucketNode = new BucketNode( model.getNeutronBucket(), mvt );
        neutronBucketNode.setOffset( mvt.modelToViewDouble( model.getNeutronBucket().getPosition() ) );
        backLayer.addChild( neutronBucketNode.getHoleLayer() );
        frontLayer.addChild( neutronBucketNode.getContainerLayer() );

        // Add the subatomic particles.
        for ( int i = 0; i < model.numElectrons(); i++ ) {
            final int finalI = i;
            particleLayer.addChild( new ElectronNode( mvt, model.getElectron( i ) ){{
                final SimpleObserver updateVisibility = new SimpleObserver() {
                    public void update() {
                        setVisible( viewOrbitals.getValue() || !model.getAtom().containsElectron( model.getElectron( finalI ) ) );
                    }
                };
                viewOrbitals.addObserver( updateVisibility );
                model.getAtom().addObserver( updateVisibility );
            }} );
        }

        for ( int i = 0; i < Math.max( model.numProtons(), model.numNeutrons() ); i++ ) {
            if ( i < model.numProtons() ) {
                particleLayer.addChild( new ProtonNode( mvt, model.getProton( i ) ) );
            }
            if ( i < model.numNeutrons() ) {
                particleLayer.addChild( new NeutronNode( mvt, model.getNeutron( i ) ) );
            }
        }

        // Show the name of the element.
        ElementNameIndicator elementNameIndicator = new ElementNameIndicator( model.getAtom() );
        // Position the name indicator above the nucleus
        elementNameIndicator.setOffset( mvt.modelToViewX( 0 ), mvt.modelToViewY( Atom.ELECTRON_SHELL_1_RADIUS * 3.0 / 4.0 ) + elementNameIndicator.getFullBounds().getHeight() / 2 );
        rootNode.addChild( elementNameIndicator );

        // Show whether the nucleus is stable.
        StabilityIndicator stabilityIndicator = new StabilityIndicator( model.getAtom() );
        // Position the stability indicator under the nucleus
        stabilityIndicator.setOffset( mvt.modelToViewX( 0 ) - stabilityIndicator.getFullBounds().getWidth() / 2, mvt.modelToViewY( -Atom.ELECTRON_SHELL_1_RADIUS * 3.0 / 4.0 ) - stabilityIndicator.getFullBounds().getHeight() );
        rootNode.addChild( stabilityIndicator );

        // Show the legend/particle count indicator in the top left.
        ParticleCountLegend particleCountLegend = new ParticleCountLegend( model.getAtom() );
        particleCountLegend.setOffset( 20, 20 );//top left corner, but with some padding
        rootNode.addChild( particleCountLegend );

        final PDimension windowSize = new PDimension( 400, 100 );//for the 3 lower windows
        final double verticalSpacingBetweenWindows = 20;
        int indicatorWindowPosX = 600;

        // Element indicator
        PDimension elementIndicatorNodeWindowSize = new PDimension( 400, 250 - verticalSpacingBetweenWindows * 2 );
        ElementIndicatorNode elementIndicatorNode = new ElementIndicatorNode( model.getAtom() );
        // TODO: i18n
        MaximizeControlNode elementIndicatorWindow = new MaximizeControlNode( "Element", elementIndicatorNodeWindowSize, elementIndicatorNode, true );
        elementIndicatorNode.setOffset( elementIndicatorNodeWindowSize.width / 2 - elementIndicatorNode.getFullBounds().getWidth() / 2, elementIndicatorNodeWindowSize.getHeight() / 2 - elementIndicatorNode.getFullBounds().getHeight() / 2 );
        elementIndicatorWindow.setOffset( indicatorWindowPosX, verticalSpacingBetweenWindows );
        elementIndicatorNode.translate( 0, 10 );//fudge factor since centering wasn't quite right
        rootNode.addChild( elementIndicatorWindow );

        // Symbol indicator
        SymbolIndicatorNode symbolNode = new SymbolIndicatorNode( model.getAtom(), 83, 83 );//has to be big enough to hold Ne with 2 digit numbers on both sides
        // TODO: i18n
        MaximizeControlNode symbolWindow = new MaximizeControlNode( "Symbol", windowSize, symbolNode, true );
        //PDebug.debugBounds = true;//helps get the layout and bounds correct
        final double insetX = 20;
        symbolNode.setOffset( insetX, windowSize.height / 2 - symbolNode.getFullBounds().getHeight() / 2 );
        symbolWindow.setOffset( indicatorWindowPosX, 250 );
        rootNode.addChild( symbolWindow );

        // Mass indicator
        // TODO: i18n
        final MaximizeControlNode massWindow = new MaximizeControlNode( "Mass", windowSize, new MassIndicatorNode( model.getAtom() ){{
            setOffset( insetX, windowSize.height / 2 - getFullBounds().getHeight() / 2 );
        }}, true );
        massWindow.setOffset( indicatorWindowPosX, symbolWindow.getFullBounds().getMaxY() + verticalSpacingBetweenWindows );
        rootNode.addChild( massWindow );

        // Charge indicator
        final ChargeIndicatorNode chargeIndicatorNode = new ChargeIndicatorNode( model.getAtom() );
        // TODO: i18n
        final MaximizeControlNode chargeWindow = new MaximizeControlNode( "Charge", windowSize, chargeIndicatorNode, true );
        chargeIndicatorNode.setOffset( insetX, windowSize.height / 2 - chargeIndicatorNode.getFullBounds().getHeight() / 2 );
        chargeWindow.setOffset( indicatorWindowPosX, massWindow.getFullBounds().getMaxY() + verticalSpacingBetweenWindows );
        rootNode.addChild( chargeWindow );

        // "Reset All" button.
        resetButtonNode = new GradientButtonNode( "Reset All", 16, new Color( 255, 153, 0 ) );
        double desiredResetButtonWidth = 100;
        resetButtonNode.setScale( desiredResetButtonWidth / resetButtonNode.getFullBoundsReference().width );
        rootNode.addChild( resetButtonNode );
        resetButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                BuildAnAtomCanvas.this.model.reset();
                reset();
            }
        } );
        resetButtonNode.setOffset(
                chargeWindow.getFullBounds().getCenterX() - resetButtonNode.getFullBounds().getWidth() / 2,
                chargeWindow.getFullBounds().getMaxY() + verticalSpacingBetweenWindows );

        maximizeControlNodeArrayList.add( chargeWindow );
        maximizeControlNodeArrayList.add( elementIndicatorWindow );
        maximizeControlNodeArrayList.add( massWindow );
        maximizeControlNodeArrayList.add( symbolWindow );

        //Add the Selection control for how to view the orbitals
        rootNode.addChild( new OrbitalViewControl( viewOrbitals ){{
            setOffset( chargeWindow.getFullBounds().getMinX()-getFullBounds().getWidth()-20,chargeWindow.getFullBounds().getY()-verticalSpacingBetweenWindows );
        }} );

        //Make the "orbits" button not focused by default, by focusing the canvas
        setFocusable( true );
        requestFocus();
    }
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------

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
        else if ( BuildAnAtomConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        //XXX lay out nodes
    }

    public void reset() {
        for ( MaximizeControlNode maximizeControlNode : maximizeControlNodeArrayList ) {
            maximizeControlNode.setMaximized( true );
        }
        viewOrbitals.reset();
    }
}
