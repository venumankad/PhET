// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Resources (images and translated strings) for "GeneExpressionBasics" are loaded eagerly to make sure everything exists on sim startup, see #2967.
 * Automatically generated by edu.colorado.phet.buildtools.preprocessor.ResourceGenerator
 */
public class GeneExpressionBasicsResources {
    public static final String PROJECT_NAME = "gene-expression-basics";
    public static final PhetResources RESOURCES = new PhetResources( PROJECT_NAME );

    //Strings
    public static class Strings {
        public static final String BIOMOLECULE_TOOLBOX = RESOURCES.getLocalizedString( "biomoleculeToolbox" );
        public static final String MRNA_DESTROYER = RESOURCES.getLocalizedString( "mrnaDestroyer" );
        public static final String NEGATIVE_TRANSCRIPTION_FACTOR = RESOURCES.getLocalizedString( "negativeTranscriptionFactor" );
        public static final String POSITIVE_TRANSCRIPTION_FACTOR = RESOURCES.getLocalizedString( "positiveTranscriptionFactor" );
        public static final String RIBOSOME = RESOURCES.getLocalizedString( "ribosome" );
        public static final String RNA_POLYMERASE = RESOURCES.getLocalizedString( "rnaPolymerase" );
    }

    //Images
    public static class Images {
        public static final BufferedImage ECOLI = RESOURCES.getImage( "ecoli.jpg" );
        public static final BufferedImage GRAY_ARROW = RESOURCES.getImage( "gray-arrow.png" );
    }
}