/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.neuron.NeuronConstants;

public class PotassiumLeakageChannel extends AbstractLeakChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = AxonMembrane.MEMBRANE_THICKNESS * 1.4; // In nanometers.
	private static final double CHANNEL_WIDTH = AxonMembrane.MEMBRANE_THICKNESS * 0.70; // In nanometers.
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	public PotassiumLeakageChannel(IParticleCapture modelContainingParticles) {
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT, modelContainingParticles);
	}

	@Override
	public Color getChannelColor() {
		return ColorUtils.darkerColor(NeuronConstants.POTASSIUM_COLOR, 0.2);
	}

	@Override
	public Color getEdgeColor() {
		return NeuronConstants.POTASSIUM_COLOR;
	}

	@Override
	public MembraneChannelTypes getChannelType() {
		return MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL;
	}
	
	@Override
	protected ParticleType getParticleTypeToCapture() {
		return ParticleType.POTASSIUM_ION;
	}
}
