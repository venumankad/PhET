/**
 * Class: MeasurementModule
 * Class: edu.colorado.phet.idealgas.controller
 * User: Ron LeMaster
 * Date: Sep 16, 2004
 * Time: 7:56:59 PM
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.view.RulerGraphic;
import edu.colorado.phet.idealgas.view.monitors.EnergyHistogramDialog;

public class MeasurementModule extends IdealGasModule {

    private EnergyHistogramDialog histogramDlg;
    private DefaultInteractiveGraphic rulerGraphic;

    public MeasurementModule( AbstractClock clock ) {
        super( clock, "Measurements" );

        setControlPanel( new MeasurementControlPanel( this ) );
        rulerGraphic = new RulerGraphic( getApparatusPanel() );
    }

    public void activate( PhetApplication application ) {
        super.activate( application );

        // Set up the energy histogramDlg. Note that we can't do this in the constructor
        // because we a reference to the application's Frame
        histogramDlg = new EnergyHistogramDialog( application.getApplicationView().getPhetFrame(),
                                                  (IdealGasModel)getModel() );
        histogramDlg.setVisible( true );
    }

    public void deactivate( PhetApplication app ) {
        histogramDlg.setVisible( false );
    }

    public void setRulerEnabed( boolean rulerEnabled ) {
        if( rulerEnabled ) {
            getApparatusPanel().addGraphic( rulerGraphic, Integer.MAX_VALUE );
        }
        else {
            getApparatusPanel().removeGraphic( rulerGraphic );
        }
        getApparatusPanel().repaint();
    }

}
