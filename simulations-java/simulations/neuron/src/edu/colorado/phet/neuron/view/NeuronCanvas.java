/* Copyright 2007, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.model.Atom;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas on which the neuron simulation is depicted.
 *
 * @author John Blanco
 */
public class NeuronCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	// Initial size of the reference coordinates that are used when setting up
	// the canvas transform strategy.  These were empirically determined to
	// roughly match the expected initial size of the canvas.
    private static final int INITIAL_INTERMEDIATE_COORD_WIDTH = 786;
    private static final int INITIAL_INTERMEDIATE_COORD_HEIGHT = 786;
    private static final Dimension INITIAL_INTERMEDIATE_DIMENSION = new Dimension( INITIAL_INTERMEDIATE_COORD_WIDTH,
    		INITIAL_INTERMEDIATE_COORD_HEIGHT );

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // Model
    private AxonModel model;
    
    // Model to view transform.
    private ModelViewTransform2D mvt;
    
    // Layers for the canvas.
    private PNode atomLayer;
    private PNode axonCrossSectionLayer;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public NeuronCanvas( AxonModel model ) {

    	this.model = model;

    	// Set up the canvas-screen transform.
    	setWorldTransformStrategy(new PhetPCanvas.CenterWidthScaleHeight(this, INITIAL_INTERMEDIATE_DIMENSION));
    	
    	// Set up the model-canvas transform.
        mvt = new ModelViewTransform2D(
        		new Point2D.Double(0, 0), 
        		new Point(INITIAL_INTERMEDIATE_COORD_WIDTH / 2, 
        				(int)Math.round(INITIAL_INTERMEDIATE_COORD_HEIGHT /2 )),
        		9,
        		true);

        setBackground( NeuronConstants.CANVAS_BACKGROUND );

        // Create the layers in the desired order.
        axonCrossSectionLayer = new PNode();
        addWorldChild(axonCrossSectionLayer);
        atomLayer = new PNode();
        addWorldChild(atomLayer);
        
        // Add the axon cross section.
        AxonMembraneNode axonMembraneNode = new AxonMembraneNode(model.getAxonMembrane(), mvt);
        axonCrossSectionLayer.addChild(axonMembraneNode);
        
        // Add the atoms.
        for (Atom atom : model.getAtoms()){
        	addAtom(atom);
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( NeuronConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "getSize() = " + getSize() );
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
    
    private void addAtom(Atom atomToBeAdded){
    	atomLayer.addChild(new AtomNode(atomToBeAdded, atomLayer, mvt));
    }
}
