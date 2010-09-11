package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.events.MouseEvent;

import mx.controls.Label;
import mx.controls.RadioButton;

/**
 * Main canvas for the buoyancy sim. Contains all of the sim-related UI
 */
public class BuoyancyCanvas extends AbstractDensityAndBuoyancyCanvas {

    private var densityModule:DensityModule;
    private var customButton:RadioButton;

    public function BuoyancyCanvas() {
        super();

        addBackground();

        densityModule = new DensityModule();
        addChild( densityModule );

        var modeControlPanel:DensityVBox = new DensityVBox();
        modeControlPanel.setStyle( "right", DensityConstants.CONTROL_INSET );
        modeControlPanel.y = DensityConstants.CONTROL_INSET;

        var label:Label = new Label();
        label.text = FlexSimStrings.get( 'mode.title', 'Objects' );
        label.setStyle( "fontWeight", "bold" );
        modeControlPanel.addChild( label );

        customButton = new RadioButton();
        customButton.groupName = "modes";
        customButton.label = FlexSimStrings.get( 'mode.customObject', 'Custom' );
        customButton.addEventListener( MouseEvent.CLICK, function():void {
            densityModule.switchToCustomObject()
        } );
        customButton.selected = true;
        modeControlPanel.addChild( customButton );

        var sameMassButton:RadioButton = new RadioButton();
        sameMassButton.groupName = "modes";
        sameMassButton.label = FlexSimStrings.get( 'mode.objectsOfSameMass', 'Same Mass' );
        sameMassButton.addEventListener( MouseEvent.CLICK, function():void {
            densityModule.switchToSameMass()
        } );
        modeControlPanel.addChild( sameMassButton );

        var sameVolumeButton:RadioButton = new RadioButton();
        sameVolumeButton.groupName = "modes";
        sameVolumeButton.label = FlexSimStrings.get( 'mode.objectsOfSameVolume', 'Same Volume' );
        sameVolumeButton.addEventListener( MouseEvent.CLICK, function():void {
            densityModule.switchToSameVolume()
        } );
        modeControlPanel.addChild( sameVolumeButton );

        var sameDensityButton:RadioButton = new RadioButton();
        sameDensityButton.groupName = "modes";
        sameDensityButton.label = FlexSimStrings.get( 'mode.objectsOfSameDensity', 'Same Density' );
        sameDensityButton.addEventListener( MouseEvent.CLICK, function():void {
            densityModule.switchToSameDensity()
        } );
        modeControlPanel.addChild( sameDensityButton );

        var mysteryObjectsButton:RadioButton = new RadioButton();
        mysteryObjectsButton.groupName = "modes";
        mysteryObjectsButton.label = FlexSimStrings.get( 'mode.mysteryObjects', 'Mystery' );
        mysteryObjectsButton.addEventListener( MouseEvent.CLICK, function():void {
            densityModule.switchToMysteryObjects()
        } );
        modeControlPanel.addChild( mysteryObjectsButton );

        addChild( modeControlPanel );

        addResetAll();

        addLogo();
    }


    override public function init():void {
        super.init();

        // TODO: why multiple initialization functions? - JO
        densityModule.init();
        densityModule.doInit( this );
        densityModule.switchToCustomObject();

        densityModule.addEventListener( MouseEvent.MOUSE_DOWN, refocusCallback );

        densityModule.start();
    }

    override public function resetAll():void {
        super.resetAll();
        customButton.selected = true;
            densityModule.resetAll();
    }
}
}