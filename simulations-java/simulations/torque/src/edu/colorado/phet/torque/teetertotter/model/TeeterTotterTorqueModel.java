// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Primary model class for the tab that depicts torque on a plank, a.k.a. a teeter totter.
 *
 * @author John Blanco
 */
public class TeeterTotterTorqueModel {
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    //A list of all the weights in the model
    private final List<Weight> weights = new ArrayList<Weight>();

    //Listeners that are notified when a weight is added to the model
    private final ArrayList<VoidFunction1<Weight>> weightAddedListeners = new ArrayList<VoidFunction1<Weight>>();

    //Fulcrum that the plank pivots on
    private final Fulcrum fulcrum = new Fulcrum();

    //Plank that objects can be placed on that is (optionally) supported by pillars
    private final Plank plank = new Plank( Fulcrum.HEIGHT );

    public ConstantDtClock getClock() {
        return clock;
    }

    //Returns a list of the weights in the model
    public List<Weight> getWeights() {
        return new ArrayList<Weight>( weights );
    }

    //Adds a listener that is notified when a weight is added
    public void addWeightAddedListener( VoidFunction1<Weight> listener ) {
        weightAddedListeners.add( listener );
    }

    //Adds a weight to the model and notifies registered listeners
    public void addWeight( Weight weight ) {
        weights.add( weight );
        for ( VoidFunction1<Weight> weightAddedListener : weightAddedListeners ) {
            weightAddedListener.apply( weight );
        }
    }

    public Fulcrum getFulcrum() {
        return fulcrum;
    }

    public Plank getPlank() {
        return plank;
    }
}