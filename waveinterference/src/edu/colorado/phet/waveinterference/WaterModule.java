/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.tests.ModuleApplication;
import edu.colorado.phet.waveinterference.tests.RotationWaveGraphic;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:07:30 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class WaterModule extends WaveInterferenceModule {
    private WaterSimulationPanel waterSimulationPanel;
    private WaterModel waterModel;
    private WaterControlPanel waterControlPanel;

    public WaterModule() {
        super( "Water" );
        waterModel = new WaterModel();
        waterSimulationPanel = new WaterSimulationPanel( this );
        waterControlPanel = new WaterControlPanel( this );

        setSimulationPanel( waterSimulationPanel );
        setControlPanel( waterControlPanel );
    }

    public WaterSimulationPanel getWaterSimulationPanel() {
        return waterSimulationPanel;
    }

    public static void main( String[] args ) {
        ModuleApplication.startApplication( args, new WaterModule() );
    }

    public RotationWaveGraphic getRotationWaveGraphic() {
        return waterSimulationPanel.getRotationWaveGraphic();
    }

    public WaveModel getWaveModel() {
        return waterModel.getWaveModel();
    }
}
