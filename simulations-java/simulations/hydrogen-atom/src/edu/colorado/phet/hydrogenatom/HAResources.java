/* Copyright 2007, University of Colorado */

package edu.colorado.phet.hydrogenatom;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.application.PhetApplicationConfig;
import edu.colorado.phet.common.resources.PhetCommonResources;
import edu.colorado.phet.common.resources.PhetResources;
import edu.umd.cs.piccolo.nodes.PImage;


public class HAResources {
    
    private static final PhetResources RESOURCES = PhetResources.forProject( "hydrogen-atom" );
    
    /* not intended for instantiation */
    private HAResources() {}
    
    public static final PhetResources getResourceLoader() {
        return RESOURCES;
    }
    
    public static final String getString( String name ) {
        return RESOURCES.getLocalizedString( name  );
    }
    
    public static final char getChar( String name, char defaultValue ) {
        return RESOURCES.getLocalizedChar( name, defaultValue );
    }

    public static final int getInt( String name, int defaultValue ) {
        return RESOURCES.getLocalizedInt( name, defaultValue );
    }
    
    public static final BufferedImage getImage( String name ) {
        return RESOURCES.getImage( name );
    }
    
    public static final PImage getImageNode( String name ) {
        return new PImage( RESOURCES.getImage( name ) );
    }
    
    public static final String getCommonString( String name ) {
        return PhetCommonResources.getInstance().getLocalizedString( name );
    }
    
    public static final BufferedImage getCommonImage( String name ) {
        return PhetCommonResources.getInstance().getImage( name );
    }
}
