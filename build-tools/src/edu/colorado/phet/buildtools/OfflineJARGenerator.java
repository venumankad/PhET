package edu.colorado.phet.buildtools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.application.JARLauncher;

/**
 * Takes a <project>_all.jar and generates offline jars for Java sims.
 * <p/>
 * If you make changes to this file, make sure you re-deploy build-tools to dev/ or prod/
 * Also, do not rename or repackage this utility without changing BuildScript.generateSimulationAndLanguageJARFilesJava
 *
 * @author Sam Reid
 */
public class OfflineJARGenerator {
    private String JAR_LAUNCHER_FILENAME = "jar-launcher.properties";

    public static void main( String[] args ) throws IOException, InterruptedException {
        System.out.println( "Started offline JAR generator" );
        if ( args.length < 2 ) {
            System.out.println( "Should specify: \n" +
                                "args[0] as the path to the offline <project>_all.jar\n" +
                                "args[1] as the path to a jar utility executable" );
        }
        new OfflineJARGenerator().generateOfflineJARs( new File( args[0] ), args[1] );
    }

    private void generateOfflineJARs( File jar, String pathToJARUtility ) throws IOException, InterruptedException {
        String[] flavors = getFlavors( jar );
        System.out.println( "Found flavors: " + Arrays.asList( flavors ) );

        Locale[] locales = getLocales( jar );
        System.out.println( "Found locales: " + Arrays.asList( locales ) );

        for ( int i = 0; i < locales.length; i++ ) {
            for ( int j = 0; j < flavors.length; j++ ) {
                generateOfflineJAR( jar, flavors[j], locales[i], pathToJARUtility );
            }
        }
    }

    public String getProjectName( File jar ) {
        StringTokenizer stringTokenizer = new StringTokenizer( jar.getName(), "_. " );//TODO: remove assumption that filename and project name match; could be moved to a main argument
        return stringTokenizer.nextToken();
    }

    private void generateOfflineJAR( File jar, String flavor, Locale locale, String pathToJARUtility ) throws IOException, InterruptedException {
        File dst = new File( jar.getParentFile(), flavor + "_" + locale + ".jar" );
        System.out.println( "Writing to: " + dst.getAbsolutePath() );
        FileUtils.copyTo( jar, dst );

        Properties properties = getJarLauncherProperties( jar );
        properties.put( JARLauncher.FLAVOR_KEY, flavor );
        properties.put( JARLauncher.LANGUAGE_KEY, locale.getLanguage() );
        String countryCode = locale.getCountry();
        //omit key for unspecified country
        if ( countryCode != null && countryCode.trim().length() > 0 ) {
            properties.put( JARLauncher.COUNTRY_KEY, countryCode );
        }

        File newPropertiesFile = getTempPropertiesFile( jar );
        properties.store( new FileOutputStream( newPropertiesFile ), "Generated by " + getClass().getName() );

        String command = pathToJARUtility + " uf " + dst.getAbsolutePath() + " -C " + newPropertiesFile.getParentFile().getAbsolutePath() + " " + newPropertiesFile.getName();
        System.out.println( "Running command: " + command );
        Process p = Runtime.getRuntime().exec( command );
        //TODO: redirect output to console
        p.waitFor();
        boolean deleted = getTempPropertiesFile( jar ).delete();
        if ( !deleted ) {
            getTempPropertiesFile( jar ).deleteOnExit();
            System.out.println( "Could not delete: " + getTempPropertiesFile( jar ) + ", attempting deleteOnExit" );
        }
    }

    private File getTempPropertiesFile( File jar ) {
        return new File( jar.getParentFile(), JAR_LAUNCHER_FILENAME );
    }

    private Locale[] getLocales( File jar ) throws IOException {
        JarFile jarFile = new JarFile( jar );
        Enumeration entries = jarFile.entries();
        HashSet locales = new HashSet();
        locales.add( new Locale("en") );//TODO: this can be removed if/when we add _en suffixes original phet localization files
        Pattern p = Pattern.compile( ".*" + getProjectName( jar ) + ".*strings.*" );//TODO: will dash character cause problems here?
        while ( entries.hasMoreElements() ) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            String name = zipEntry.getName();
            if ( p.matcher( name ).matches() ) {
                int index = name.indexOf( "_" );//TODO: assumes no _ in simulation name
                if ( index >= 0 ) {
                    String localeStr = name.substring( index + 1, name.indexOf( ".properties" ) );
                    locales.add( LocaleUtils.toLocale(localeStr ));
                }
            }
        }
        return (Locale[]) locales.toArray( new Locale[locales.size()] );
    }

    private String[] getFlavors( File jar ) throws IOException {
        Properties properties = getJarLauncherProperties( jar );
        Enumeration propertyNames = properties.propertyNames();
        HashSet flavors = new HashSet();
        while ( propertyNames.hasMoreElements() ) {
            String name = (String) propertyNames.nextElement();
            if ( name.startsWith( "project.flavor." ) ) {
                StringTokenizer st = new StringTokenizer( name, "." );
                st.nextToken();//project
                st.nextToken();//flavor
                String flavor = st.nextToken();
                flavors.add( flavor );
            }
        }
        return (String[]) flavors.toArray( new String[flavors.size()] );
    }

    private Properties getJarLauncherProperties( File jar ) throws IOException {
        JarFile jarFile = new JarFile( jar );
        ZipEntry e = jarFile.getEntry( JAR_LAUNCHER_FILENAME );
        InputStream in = jarFile.getInputStream( e );
        Properties properties = new Properties();
        properties.load( in );
        return properties;
    }
}