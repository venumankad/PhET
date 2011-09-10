// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Not;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.GroundNode;
import edu.colorado.phet.common.piccolophet.nodes.background.SkyNode;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishRuler;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowCanvas;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowControlPanelNode;
import edu.colorado.phet.fluidpressureandflow.common.view.MeterStick;
import edu.colorado.phet.fluidpressureandflow.common.view.PressureSensorNode;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureCanvas;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel;
import edu.colorado.phet.fluidpressureandflow.watertower.WaterTowerModule;
import edu.colorado.phet.fluidpressureandflow.watertower.model.WaterDrop;
import edu.colorado.phet.fluidpressureandflow.watertower.model.WaterTowerModel;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createSinglePointScaleInvertedYMapping;

/**
 * Canvas for the water tower tab.
 *
 * @author Sam Reid
 */
public class WaterTowerCanvas extends FluidPressureAndFlowCanvas<WaterTowerModel> {
    private static final double modelHeight = 50;
    private static final double scale = STAGE_SIZE.getHeight() / modelHeight;
    private final PNode waterDropLayer = new PNode();
    private final FPAFMeasuringTape measuringTape;
    final static Color TRANSPARENT = new Color( 0, 0, 0, 0 );

    //Font size to use for "reset all" button and "fill" button
    public static int FLOATING_BUTTON_FONT_SIZE = (int) ( FluidPressureCanvas.CONTROL_FONT.getSize() * 1.3 );

    public WaterTowerCanvas( final WaterTowerModule module ) {
        super( createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width * 0.225, STAGE_SIZE.height * 0.75 ), scale ) );

        addChild( new SkyNode( transform, new Rectangle2D.Double( -1000, 0, 2000, 2000 ), 20 ) );

        //Show the ground before the hose will go in front
        addChild( new GroundNode( transform, new Rectangle2D.Double( -1000, -2000, 2000, 2000 ), 5 ) );

        //Show the optional hose that takes the water from the water tower
        HoseNode hoseNode = new HoseNode( transform, module.model.hose );
        addChild( hoseNode );

        //Show the water tower node, should be after the hose so that the red door will go in front of the hose
        addChild( new WaterTowerNode( transform, module.model.getWaterTower(), module.model.liquidDensity ) );
        addChild( waterDropLayer );

        //Add a button that will fill the tank
        addChild( new FillTankButton( module.model.getWaterTower().full, module.model.getWaterTower().fill ) {{
            module.model.getWaterTower().tankBottomCenter.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D bottomCenter ) {

                    //Show the button to the left center of the tank, and move when the tank moves
                    final Point2D leftCenterOfWaterTower = transform.modelToView( module.model.getWaterTower().getTankShape().getX(),
                                                                                  bottomCenter.getY() + module.model.getWaterTower().getTankShape().getHeight() / 2 );
                    setOffset( leftCenterOfWaterTower.getX() - getFullBounds().getWidth() - INSET, leftCenterOfWaterTower.getY() - getFullBounds().getHeight() / 2 );
                }
            } );
        }} );

        //Add the faucet
        addChild( new FPAFFaucetNode( module.model.getFaucetFlowRate(), new Not( module.model.getWaterTower().full ) ) );

        module.model.addDropAddedListener( new VoidFunction1<WaterDrop>() {
            public void apply( final WaterDrop waterDrop ) {
                waterDropLayer.addChild( new WaterDropNode( transform, waterDrop, module.model.liquidDensity ) {{
                    final WaterDropNode waterDropNode = this;
                    waterDrop.addRemovalListener( new SimpleObserver() {
                        public void update() {
                            waterDropLayer.removeChild( waterDropNode );
                        }
                    } );
                }} );
            }
        } );

        //TODO: this is duplicated in FluidFlowCanvas
        // Control Panel
        final FluidPressureAndFlowControlPanelNode controlPanelNode = new FluidPressureAndFlowControlPanelNode( new WaterTowerControlPanel( module ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - INSET, INSET );
        }};
        addChild( controlPanelNode );
        addChild( new ResetAllButtonNode( module, this, FLOATING_BUTTON_FONT_SIZE, FluidPressureControlPanel.FOREGROUND, FluidPressureControlPanel.BACKGROUND ) {{
            setConfirmationEnabled( false );
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + INSET * 2 );
        }} );

        //Create and show the fluid density controls
        addFluidDensityControl( module );

        //Some nodes go behind the pool so that it looks like they submerge
        final Point2D.Double rulerModelOrigin = new Point2D.Double( 0, 0 );
        final MeterStick meterStick = new MeterStick( transform, module.meterStickVisible, module.rulerVisible, rulerModelOrigin, true, module.model );
        final EnglishRuler englishRuler = new EnglishRuler( transform, module.yardStickVisible, module.rulerVisible, rulerModelOrigin, true, module.model );
        synchronizeRulerLocations( meterStick, englishRuler );

        addChild( meterStick );
        addChild( englishRuler );

        measuringTape = new FPAFMeasuringTape( transform, module.measuringTapeVisible, module.model.units );
        addChild( measuringTape );

        //Add the floating clock controls and sim speed slider at the bottom of the screen
        addClockControls( module );

        //Add the draggable sensors in front of the control panels so they can't get lost behind the control panel
        for ( PressureSensor pressureSensor : module.model.getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, pressureSensor, module.model.units, visibleModelBounds ) );
        }

        //Add the sensor toolbox node, which also adds the velocity and pressure sensors
        //Doing this last ensures that the draggable sensors will appear in front of everything else
        addSensorToolboxNode( module.model, controlPanelNode );
    }

    //Additionally reset the measuring tape since not reset elsewhere
    public void reset() {
        measuringTape.reset();
    }
}