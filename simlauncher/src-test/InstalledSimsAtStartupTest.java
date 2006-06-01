/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.simlauncher.Configuration;
import edu.colorado.phet.simlauncher.Simulation;
import edu.colorado.phet.simlauncher.Catalog;

import java.util.List;

/**
 * InstalledSimsAtStartupTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstalledSimsAtStartupTest {

    public static void main( String[] args ) {
        List sims = new Catalog().getInstalledSimulations();
        for( int i = 0; i < sims.size(); i++ ) {
            Simulation simulation = (Simulation)sims.get( i );
            System.out.println( "simulation = " + simulation );
        }
    }
}
