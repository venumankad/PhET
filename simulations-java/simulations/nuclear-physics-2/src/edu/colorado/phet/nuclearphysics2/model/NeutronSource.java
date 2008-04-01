/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.nuclearphysics2.model.AlphaParticle.Listener;

/**
 * This class models the behavior of a neutron source, i.e. some sort of
 * device that can generate neutrons.
 *
 * @author John Blanco
 */
public class NeutronSource {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    private static final double NEUTRON_VELOCITY = 0.1; // In femtometers/tick.

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    private ArrayList _listeners = new ArrayList();
    
    // Location in space of this particle.
    private Point2D.Double _position;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public NeutronSource(double xPos, double yPos)
    {
        _position = new Point2D.Double(xPos, yPos);
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    public Point2D getPosition(){
        return new Point2D.Double(_position.getX(), _position.getY());
    }

    //------------------------------------------------------------------------
    // Listener Support
    //------------------------------------------------------------------------

    public void addListener(Listener listener)
    {
        if (_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        _listeners.add( listener );
    }
    
    public static interface Listener {
        void positionChanged();
        void neutronGenerated(Neutron newNeutron);
    }

    //------------------------------------------------------------------------
    // Other methods
    //------------------------------------------------------------------------

    /**
     * Commands the neutron source to generate a new neutron.
     */
    public void generateNeutron(){
        Neutron newNeutron = new Neutron(_position.x, _position.y, 1, 0, false);
        
        for (int i = 0; i < _listeners.size(); i++){
            // Notify listeners of new particle.
            ((NeutronSource.Listener)_listeners.get( i )).neutronGenerated( newNeutron );
        }
    }
}
