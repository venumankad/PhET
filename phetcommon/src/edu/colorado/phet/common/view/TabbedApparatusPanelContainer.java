/* Copyright University of Colorado, 2003 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleObserver;
import edu.colorado.phet.common.application.PhetApplication;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * An on-screen container for the modules in an application. It displays the
 * modules' apparatus panels in tabbed panes. It is only used for applications
 * that have more than one module.
 */
public class TabbedApparatusPanelContainer extends JTabbedPane implements ModuleObserver {
    Module current;
    private PhetApplication application;

    public TabbedApparatusPanelContainer( final PhetApplication application ) {
        this.application = application;
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int selectedIdx = getSelectedIndex();
                current = application.moduleAt( selectedIdx );
                application.setActiveModule( selectedIdx );
            }
        } );
        application.addModuleObserver( this );
    }

    public void moduleAdded( Module module ) {
        addTab( module.getName(), module.getApparatusPanel() );
    }

    public void activeModuleChanged( Module m ) {
        if( current != m ) {
            int index = application.indexOf( m );
            setSelectedIndex( index );
        }
    }

}


