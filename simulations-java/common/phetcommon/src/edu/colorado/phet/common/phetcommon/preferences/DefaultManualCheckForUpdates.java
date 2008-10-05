package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.updates.UpdateManager;

public class DefaultManualCheckForUpdates implements IManuallyCheckForUpdates {
    private PhetVersion currentVersion;
    private String humanReadableSimName;
    private Window window;
    private String projectName;

    public DefaultManualCheckForUpdates( Window window, String projectName, PhetVersion currentVersion, String humanReadableSimName ) {
        this.window = window;
        this.projectName = projectName;
        this.currentVersion = currentVersion;
        this.humanReadableSimName = humanReadableSimName;
    }

    public void checkForUpdates() {
        UpdateManager updateManager = new UpdateManager( projectName, currentVersion );
        UpdateManager.Listener listener = new UpdateManager.Listener() {
            public void discoveredRemoteVersion( PhetVersion remoteVersion ) {
            }

            public void newVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
                String title = "New version available";
                String text = "<html>Your current version of " + humanReadableSimName + " is " + currentVersion.formatForTitleBar() + ".  A newer version (" + remoteVersion.formatForTitleBar() + ") is available.<br>" +
                              "A web browser is being opened to the PhET website, where you can get the new version.<html>";
                UpdateResultDialog.showDialog( window, title, text );
            }

            public void exceptionInUpdateCheck( final IOException e ) {
                String title = "Error during update check";
                String text = "<html>" + "An error was encountered while trying to access the PhET website.<br>Please try again later, or visit <a href=\"http://phet.colorado.edu\">http://phet.colorado.edu</a>.<br>If the problem persists, please contact <a href=\"mailto:phethelp@colorado.edu\">phethelp@colorado.edu</a>." + "<html>";
                final JButton details = new JButton( "Details..." );
                details.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent event ) {
                        final StringWriter result = new StringWriter();
                        final PrintWriter printWriter = new PrintWriter( result );
                        e.printStackTrace( printWriter );
                        JOptionPane.showMessageDialog( details, result.getBuffer() );
                    }
                } );
                UpdateResultDialog updateResultDialog = UpdateResultDialog.createDialog( window, title, text );
                updateResultDialog.addComponent( details );
                updateResultDialog.pack();
                updateResultDialog.setVisible( true );
            }

            public void noNewVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
                UpdateResultDialog.showDialog( window, "Up to date", "<html>" + "You have the current version (" + currentVersion.formatForTitleBar() + ") of " + humanReadableSimName + "." + "<html>" );
            }
        };
        updateManager.addListener( listener );
        updateManager.checkForUpdates();
        updateManager.removeListener( listener );
    }
}
