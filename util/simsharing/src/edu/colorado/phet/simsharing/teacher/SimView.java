// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.simsharing.SimState;
import edu.colorado.phet.common.phetcommon.simsharing.SimsharingApplication;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.simsharing.messages.GetSample;
import edu.colorado.phet.simsharing.messages.GetSamplesAfter;
import edu.colorado.phet.simsharing.messages.SampleBatch;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.socket.Sample;
import edu.colorado.phet.simsharing.socketutil.Client;

/**
 * @author Sam Reid
 */
public class SimView<U extends SimState, T extends SimsharingApplication<U>> {
    private final Thread thread;
    private final TimeControlFrame timeControl;
    private final T application;
    private final SessionID sessionID;
    private Client client;
    private boolean running = true;
    private ArrayList<U> states = new ArrayList<U>();
    private int index = 0;
    private boolean debugElapsedTime = false;

    public SimView( final SessionID sessionID, boolean playbackMode, final T application ) {
        this.sessionID = sessionID;

        //Create a new Client (including a new thread on the server) to avoid synchronization problems with other client
        try {
            this.client = new Client();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        this.application = application;
        timeControl = new TimeControlFrame( sessionID );
        timeControl.setVisible( true );
        thread = new Thread( new Runnable() {
            public void run() {
                while ( running ) {
                    step();
                }
            }
        } );
        timeControl.live.set( !playbackMode );
        if ( playbackMode ) {
            timeControl.playing.set( true );
        }

        application.setExitStrategy( new VoidFunction0() {
            public void apply() {
                //Just let the window close but do not system.exit the whole VM
                running = false;
                timeControl.setVisible( false );//TODO: detach listeners
            }
        } );

        //Timer that shows the loaded states in order
        new Timer( 33, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( index < states.size() ) {
                    final U state = states.get( index );
                    long t = System.currentTimeMillis();
                    application.setState( state );
                    long t2 = System.currentTimeMillis();
                    index++;
                    if ( debugElapsedTime ) {
                        System.out.println( "elapsed: " + ( t2 - t ) );
                    }
                }
            }
        } ).start();

        //Acquire new states from the server
        new Thread( new Runnable() {
            public void run() {
                while ( running ) {
                    try {
                        long time = -1;
                        if ( states.size() > 0 ) {
                            time = states.get( states.size() - 1 ).getTime();
                        }
                        final SampleBatch<U> sample = (SampleBatch<U>) client.ask( new GetSamplesAfter( sessionID, time ) );
                        for ( U u : sample ) {
                            if ( !states.contains( u ) ) {
                                states.add( u );
                            }
                        }
//                        System.out.println( "original size = " + sizeBeforeAdd + ", received = " + sample.states.size() + ", total = " + states.size() );

                        //Download a batch of states this often
                        Thread.sleep( 1000 );
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
        } ).start();
    }

    private void step() {

        //Read samples when not live.  When live, data is pushed to the SimView
        if ( !timeControl.live.get() ) {
            try {
                SwingUtilities.invokeAndWait( new Runnable() {
                    public void run() {
                        try {
                            if ( timeControl.playing.get() ) {//TODO: may need a sleep
                                timeControl.frameToDisplay.set( Math.min( timeControl.frameToDisplay.get() + 1, timeControl.maxFrames.get() ) );
                            }
                            final int sampleIndex = timeControl.live.get() ? -1 : timeControl.frameToDisplay.get();
                            final Sample<U> sample = (Sample<U>) client.ask( new GetSample( sessionID, sampleIndex ) );

                            application.setState( sample.state );
                            timeControl.maxFrames.set( sample.totalSampleCount );
                        }
                        catch ( Exception e ) {
                            e.printStackTrace();
                        }
                    }
                } );
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    private void alignControls() {
        timeControl.setLocation( application.getPhetFrame().getX(), application.getPhetFrame().getY() + application.getPhetFrame().getHeight() + 1 );
        timeControl.setSize( application.getPhetFrame().getWidth(), timeControl.getPreferredSize().height );
    }

    public void start() {
        application.getPhetFrame().setTitle( application.getPhetFrame().getTitle() + ": Teacher Edition" );
        application.getPhetFrame().addComponentListener( new ComponentAdapter() {
            @Override
            public void componentMoved( ComponentEvent e ) {
                alignControls();
            }

            @Override
            public void componentResized( ComponentEvent e ) {
                alignControls();
            }
        } );
        alignControls();
        application.setTeacherMode( true );
        thread.start();
    }
}