/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.simlauncher.SimContainer;
import edu.colorado.phet.simlauncher.Simulation;
import edu.colorado.phet.simlauncher.SimListContainer;

import java.util.*;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * InstallOrUpdateSimAction
 * <p/>
 * Installs a simulation if it's not installed. If it is installed, updates the simulation.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstallOrUpdateSimAction extends AbstractAction {
    private SimContainer simContainer;
    private Component component;
    private JDialog waitDlg;

    public InstallOrUpdateSimAction( SimContainer simContainer, Component component ) {
        this.component = component;
        this.simContainer = simContainer;
    }

    public void actionPerformed( ActionEvent e ) {
        Simulation[] sims = simContainer.getSimulations();
        final List simsToUpdate = new ArrayList();
        for( int i = 0; i < sims.length; i++ ) {
            Simulation sim = sims[i];
            if( sim.isInstalled() && !sim.isCurrent() ) {
                simsToUpdate.add( sim );
            }
            else if( !sim.isInstalled() ){
                AbstractAction installAction = new InstallSimAction( sim, component );
                installAction.actionPerformed( e );
            }
        }

        // If any sims need to be updated, do it now
        if( simsToUpdate.size() > 0 ) {
            SimContainer simsForUpdateContainer = new SimListContainer( simsToUpdate );
            AbstractAction updateAction = new UpdateSimAction( simsForUpdateContainer, component );
            updateAction.actionPerformed( e );
        }
    }
}
