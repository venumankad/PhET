package edu.colorado.phet.rotation;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.model.RotationModel;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:32:36 AM
 */

public class RotationModule extends PiccoloModule {
    private RotationSimulationPanel rotationSimulationPanel;

    /*The Physical Model*/
    private RotationModel rotationModel;

    /*Other MVC model data structures*/
    private VectorViewModel vectorViewModel;

    public RotationModule() {
        super( "Rotation", createClock() );
        setModel( new BaseModel() );
        rotationModel = new RotationModel();
        vectorViewModel = new VectorViewModel();

        rotationSimulationPanel = new RotationSimulationPanel( this );
        setSimulationPanel( rotationSimulationPanel );

        setLogoPanel( null );
        setClockControlPanel( null );
        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                rotationModel.stepInTime( dt );
            }
        } );
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1.0 );
    }

    public GraphSetModel getGraphSetModel() {
        return rotationSimulationPanel.getGraphSetModel();
    }

    public VectorViewModel getVectorViewModel() {
        return vectorViewModel;
    }

    public RotationModel getRotationModel() {
        return rotationModel;
    }

    public RotationSimulationPanel getRotationSimulationPanel() {
        return rotationSimulationPanel;
    }
}
