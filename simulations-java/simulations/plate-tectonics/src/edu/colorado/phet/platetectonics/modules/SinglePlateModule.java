// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.jmephet.JMEView;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.colorado.phet.platetectonics.control.MyCrustPanel;
import edu.colorado.phet.platetectonics.model.BlockCrustPlateModel;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.view.PlateView;
import edu.colorado.phet.platetectonics.view.RulerNode3D;
import edu.colorado.phet.platetectonics.view.RulerNode3D.RulerNode2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import com.jme3.renderer.Camera;

import static edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings.*;

// TODO: better name?
public class SinglePlateModule extends PlateTectonicsModule {

    private BlockCrustPlateModel model;

    public SinglePlateModule( Frame parentFrame ) {
        super( parentFrame, ONE_PLATE );
    }

    @Override public void updateState( float tpf ) {
        super.updateState( tpf );
        model.update( tpf );
    }

    @Override public void initialize() {
        super.initialize();

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D(
                Bounds3D.fromMinMax( -150000, 150000,
                                     -150000, 150000,
                                     -50000, 0 ),
                512, 512, 32 );

        // create the model and terrain
        model = new BlockCrustPlateModel();
        mainView.getScene().attachChild( new PlateView( model, this, grid ) );

        /*---------------------------------------------------------------------------*
        * test ruler
        *----------------------------------------------------------------------------*/
        mainView.getScene().attachChild( new RulerNode3D( getModelViewTransform(), this ) {{
            setLocalTranslation( -100, -100, 1 );
        }} );

        /*---------------------------------------------------------------------------*
        * "Test" GUI
        *----------------------------------------------------------------------------*/

        JMEView guiView = createFrontGUIView( "GUI" );

        // toolbox
        guiView.getScene().attachChild( new PiccoloJMENode( new ControlPanelNode( new PNode() {{
            ZeroOffsetNode rulerNode2D = new ZeroOffsetNode( new RulerNode2D( 0.75f ) ); // wrap it in a zero-offset node, since we are rotating and scaling it (bad origin)
            PText toolboxLabel = new PText( "Toolbox" ) {{
                setFont( new PhetFont( 16, true ) );
            }};

            addChild( rulerNode2D ); // approximate scaling to get the size right
            addChild( toolboxLabel );

            toolboxLabel.setOffset( rulerNode2D.getFullBounds().getWidth() + 10, 0 ); // TODO: change positioning once we have added other toolbox elements
        }} ), getInputHandler(), this, canvasTransform ) {{
            position.set( new ImmutableVector2D( 10, 10 ) );
        }} );

        // "my crust" control
        guiView.getScene().attachChild( new PiccoloJMENode( new ControlPanelNode( new MyCrustPanel( model ) ), getInputHandler(), this, canvasTransform ) {{
            // layout the panel if its size changes (and on startup)
            onResize.addUpdateListener( new UpdateListener() {
                public void update() {
                    position.set( new ImmutableVector2D(
                            Math.ceil( ( getStageSize().width - getComponentWidth() ) / 2 ), // center horizontally
                            getStageSize().height - getComponentHeight() - 10 ) ); // offset from top
                }
            }, true ); // TODO: default to this?
        }} );

        // "oceanic crust" label
        guiView.getScene().attachChild( new PiccoloJMENode( new PText( OCEANIC_CRUST ) {{
            setFont( new PhetFont( 16, true ) );
        }}, getInputHandler(), this, canvasTransform ) {{
            // TODO: improve positioning to handle i18n?
            position.set( new ImmutableVector2D( 30,
                                                 getStageSize().getHeight() * 0.6 ) );
        }} );

        // "continental crust" label
        guiView.getScene().attachChild( new PiccoloJMENode( new PText( CONTINENTAL_CRUST ) {{
            setFont( new PhetFont( 16, true ) );
        }}, getInputHandler(), this, canvasTransform ) {{
            // TODO: improve positioning to handle i18n?
            position.set( new ImmutableVector2D( getStageSize().getWidth() - getComponentWidth() - 30,
                                                 getStageSize().getHeight() * 0.6 ) );
        }} );
    }

    @Override public Camera getDebugCamera() {
        return mainView.getCamera();
    }

}
