package edu.colorado.phet.wickettest.content;

import java.util.Locale;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.panels.SimulationDisplayPanel;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.PhetRegularPage;

public class SimulationDisplay extends PhetRegularPage {
    public SimulationDisplay( PageParameters parameters ) {
        super( parameters );

        addTitle( new ResourceModel( "simulationDisplay.simulations" ) );

        add( new SimulationDisplayPanel( "simulation-display-panel", getPageContext() ) );

    }

    public static String getMappingString() {
        return "^simulations$";
    }

    public static PhetLink createLink( String id, Locale locale ) {
        String str = "/" + LocaleUtils.localeToString( locale ) + "/simulations";
        return new PhetLink( id, str );
    }
}