// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.functions;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Resources (images and translated strings) for "Functions" are loaded eagerly to make sure everything exists on sim startup, see #2967.
 * Automatically generated by edu.colorado.phet.buildtools.preprocessor.ResourceGenerator
 */
public class FunctionsResources {
    public static final String PROJECT_NAME = "functions";
    public static final PhetResources RESOURCES = new PhetResources( PROJECT_NAME );

    //Strings
    public static class Strings {

    }

    //Images
    public static class Images {
        public static final BufferedImage GRID_ICON = RESOURCES.getImage( "grid-icon.png" );
        public static final BufferedImage KEY = RESOURCES.getImage( "key.png" );
        public static final BufferedImage ROTATE_RIGHT = RESOURCES.getImage( "rotate-right.png" );
    }
}