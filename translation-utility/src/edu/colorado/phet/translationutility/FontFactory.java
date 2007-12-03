/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;


public class FontFactory {

    /* not intended for instantiation */
    private FontFactory() {}
    
    /**
     * Creates a Font for a specified language code.
     * Preferred font names are specified in the project properties file (eg, font.ja).
     * If no preferred fonts are specified in the project properties file,
     * or if one of the preferred font is not found, then a default font is returned.
     * <p>
     * NOTE: This is probably not necessary on Macintosh, but doesn't hurt.
     * 
     * @param languageCode
     * @param style
     * @param size
     * @return Font
     */
    public static Font createFont( String languageCode, int style, int size ) {
        Font font = new PhetDefaultFont( style, size );
        String[] preferredFontNames = ProjectProperties.getPreferredFontNames( languageCode );
        if ( preferredFontNames != null ) {
            for ( int i = 0; i < preferredFontNames.length; i++ ) {
                String fontName = preferredFontNames[i];
                ArrayList fonts = new ArrayList( Arrays.asList( GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts() ) );
                for ( int j = 0; j < fonts.size(); j++ ) {
                    Font o = (Font) fonts.get( j );
                    if ( o.getName().equals( fontName ) ) {
                        font = new Font( o.getName(), style, size );
                    }
                }
            }
        }
        assert( font != null );
        return font;
    }
}
