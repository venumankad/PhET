/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.util.Properties;
import java.util.jar.*;

import edu.colorado.phet.common.phetcommon.application.JARLauncher;
import edu.colorado.phet.translationutility.TUConstants;
import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.util.Command;
import edu.colorado.phet.translationutility.util.PropertiesIO;
import edu.colorado.phet.translationutility.util.Command.CommandException;
import edu.colorado.phet.translationutility.util.PropertiesIO.PropertiesIOException;

/**
 * JavaSimulation supports of Java-based simulations.
 * Java simulations use Java properties files to store localized strings.
 * There is one properties file per language.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JavaSimulation extends AbstractSimulation {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // regular expression that matches localization string files
    private static final String REGEX_LOCALIZATION_FILES = ".*-strings.*\\.properties";
    
    // project properties file and properties
    private static final String JAR_LAUNCHER_PROPERTIES_FILENAME = JARLauncher.getPropertiesFileName();
    private static final String PROJECT_NAME_PROPERTY = "project.name";
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public JavaSimulation( String jarFileName ) throws SimulationException {
        super( jarFileName );
    }
    
    //----------------------------------------------------------------------------
    // Public interface
    //----------------------------------------------------------------------------
    
    public void testStrings( Properties properties, String languageCode ) throws SimulationException {
        String propertiesFileName = getPropertiesResourceName( getProjectName(), languageCode );
        writePropertiesToJarCopy( getJarFileName(), TEST_JAR, getManifest(), propertiesFileName, properties );
        try {
            String[] cmdArray = { "java", "-jar", 
                    "-Duser.language=" + languageCode, /* TODO: delete after IOM, #1246 */
                    "-Djavaws.phet.locale=" + languageCode, /* TODO: delete after IOM, #1246 */
                    "-Djavaws.language=" + languageCode, /* TODO: delete after IOM, #1246 */
                    TEST_JAR };
            Command.run( cmdArray, false /* waitForCompletion */ );
        }
        catch ( CommandException e ) {
            throw new SimulationException( e );
        }
    }

    public Properties getStrings( String languageCode ) throws SimulationException {
        
        // Load strings from a resource file.
        String propertiesFileName = getPropertiesResourceName( getProjectName(), languageCode );
        Properties properties = readPropertiesFromJar( getJarFileName(), propertiesFileName );
        
        // English strings may be in a fallback resource file.
        if ( properties == null && languageCode.equals( TUConstants.ENGLISH_LANGUAGE_CODE ) ) {
            propertiesFileName = getFallbackPropertiesResourceName( getProjectName() );
            properties = readPropertiesFromJar( getJarFileName(), propertiesFileName );
        }
        
        return properties;
    }

    public Properties loadStrings( File file ) throws SimulationException {
        Properties properties = null;
        try {
            properties = PropertiesIO.read( file );
        }
        catch ( PropertiesIOException e ) {
            throw new SimulationException( e );
        }
        return properties;
    }

    public void saveStrings( Properties properties, File file ) throws SimulationException {
        try {
            String projectName = getActualProjectName( getJarFileName() );
            String projectVersion = getProjectVersion( projectName + TUConstants.RESOURCE_PATH_SEPARATOR + projectName + ".properties" ); // eg, faraday/faraday.properties
            String header = getTranslationFileHeader( file.getName(), projectName, projectVersion );
            PropertiesIO.write( properties, header, file );
        }
        catch ( PropertiesIOException e ) {
            throw new SimulationException( e );
        }
    }
    
    public String getSubmitBasename( String languageCode ) {
        return getPropertiesResourceBasename( getProjectName(), languageCode );
    }
    
    /*
     * Gets the project name for the simulation, adjusted to handle common strings.
     * <p>
     * PhET common strings are bundled into their own JAR file for use with translation utility.
     * Because the PhET build process is so inflexible, the JAR file must be built & deployed
     * via a dummy sim named "common-strings", found in trunk/simulations-java/simulations.
     * If the project name is "common-strings", we really want to load the common strings
     * which are in files with basename "phetcommon-strings".  So we use "phetcommon" 
     * as the project name.
     */
    protected String getProjectName( String jarFileName ) throws SimulationException {
        String projectName = getActualProjectName( jarFileName );
        if ( projectName.equals( "common-strings" ) ) {
            projectName = "phetcommon";
        }
        return projectName;
    }
    
    /*
     * Gets the project name for the simulation.
     * 
     * @param jarFileName
     */
    private String getActualProjectName( String jarFileName ) throws SimulationException {
        
        String projectName = null;
        Properties projectProperties = null;

        //TODO: delete this block after IOM, #1222
        projectProperties = readPropertiesFromJar( jarFileName, "project.properties" );
        if ( projectProperties != null ) {
            projectName = projectProperties.getProperty( PROJECT_NAME_PROPERTY );
        }

        // The project name is identified in the properties file read by JARLauncher
        projectProperties = readPropertiesFromJar( jarFileName, JAR_LAUNCHER_PROPERTIES_FILENAME );
        if ( projectProperties != null ) {
            projectName = projectProperties.getProperty( PROJECT_NAME_PROPERTY );
        }
        
        //TODO: delete this block after IOM, #1223
        // For older sims (or if PROJECT_NAME_PROPERTY is missing), discover the project name
        if ( projectName == null ) {
            projectName = discoverProjectName( jarFileName );
        }
        
        if ( projectName == null ) {
            throw new SimulationException( "could not determine this simulation's project name: " + jarFileName );
        }
        
        return projectName;
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Gets the full name of the fallback JAR resource that contains 
     * English strings for a specified project.
     */
    private static String getFallbackPropertiesResourceName( String projectName ) {
        return getPropertiesResourceName( projectName, null /* languageCode */ );
    }
    
    /*
     * Gets the full name of the JAR resource that contains localized strings for 
     * a specified project and language. If language code is null, the fallback resource
     * name is returned. 
     */
    private static String getPropertiesResourceName( String projectName, String languageCode ) {
        String basename = getPropertiesResourceBasename( projectName, languageCode );
        return projectName + TUConstants.RESOURCE_PATH_SEPARATOR + "localization" + TUConstants.RESOURCE_PATH_SEPARATOR + basename;
    }
    
    /*
     * Gets the basename of the JAR resource that contains localized strings for 
     * a specified project and language. For example, faraday-strings_es.properties
     * <p>
     * If language code is null, the basename of the fallback resource is returned.
     * The fallback name does not contain a language code, and contains English strings.
     * For example: faraday-strings.properties
     * <p>
     * NOTE: Support for the fallback name is provided for backward compatibility.
     * All Java simulations should migrate to the convention of including "en" in the 
     * resource name of English localization files.
     * 
     * @param projectName
     * @param languageCode
     * @return
     */
    private static String getPropertiesResourceBasename( String projectName, String languageCode ) {
        String basename = null;
        if ( languageCode == null ) {
            basename = projectName + "-strings" + ".properties"; // fallback basename contains no language code
        }
        else {
            basename = projectName + "-strings_" + languageCode + ".properties";
        }
        return basename;
    }
    
    /*
     * Discovers the name of the project that was used to build the JAR file.
     * We search for localization files in the JAR file.
     * The first localization file that does not belong to a common project is assumed
     * to belong to the simulation, and we extract the project name from the localization file name.
     * 
     * @param jarFileName
     * @return String
     * @throws SimulationException
     */
    private static String discoverProjectName( String jarFileName ) throws SimulationException {
        
        String[] commonProjectNames = TUResources.getCommonProjectNames();
        
        String projectName = null;
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFileName );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( "jar file not found: " + jarFileName, e );
        }
        
        JarInputStream jarInputStream = null;
        try {
            jarInputStream = new JarInputStream( inputStream );
            
            // look for the properties files
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                String jarEntryName = jarEntry.getName();
                if ( jarEntryName.matches( REGEX_LOCALIZATION_FILES ) ) {
                    boolean commonMatch = false;
                    for ( int i = 0; i < commonProjectNames.length; i++ ) {
                        String commonProjectFileName = ".*" + commonProjectNames[i] + "-strings.*\\.properties";
                        if ( jarEntryName.matches( commonProjectFileName ) ) {
                            commonMatch = true;
                            break;
                        }
                    }
                    if ( !commonMatch ) {
                        int index = jarEntryName.indexOf( '/' );
                        projectName = jarEntryName.substring( 0, index );
                        break;
                    }
                }
                jarEntry = jarInputStream.getNextJarEntry();
            }
            
            jarInputStream.close();
        }
        catch ( IOException e ) {
            throw new SimulationException( "error reading jar file: " + jarFileName, e );
        }
        
        return projectName;
    }
    
    /*
     * Copies a JAR file and adds (or replaces) a properties file.
     * The properties file contains localized strings.
     * The original JAR file is not modified.
     * 
     * @param originalJarFileName
     * @param newJarFileName
     * @param manifest
     * @param propertiesFileName
     * @param properties
     */
    private static void writePropertiesToJarCopy( 
            String originalJarFileName, String newJarFileName, Manifest manifest, 
            String propertiesFileName, Properties properties ) throws SimulationException {
        
        if ( originalJarFileName.equals( newJarFileName  ) ) {
            throw new IllegalArgumentException( "originalJarFileName and newJarFileName must be different" );
        }
        
        File jarFile = new File( originalJarFileName );
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFile );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( "jar file not found: " + originalJarFileName, e );
        }
        
        File testFile = new File( newJarFileName );
        testFile.deleteOnExit(); // temporary file, delete when the VM exits
        try {
            // input comes from the original JAR file
            JarInputStream jarInputStream = new JarInputStream( inputStream ); // throws IOException
            
            // output goes to test JAR file
            OutputStream outputStream = new FileOutputStream( testFile );
            JarOutputStream testOutputStream = new JarOutputStream( outputStream, manifest );
            
            // copy all entries from input to output, skipping the properties file & manifest
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                if ( !jarEntry.getName().equals( propertiesFileName ) && !jarEntry.getName().equals( JarFile.MANIFEST_NAME ) ) {
                    testOutputStream.putNextEntry( jarEntry );
                    byte[] buf = new byte[1024];
                    int len;
                    while ( ( len = jarInputStream.read( buf ) ) > 0 ) {
                        testOutputStream.write( buf, 0, len );
                    }
                    testOutputStream.closeEntry();
                }
                jarEntry = jarInputStream.getNextJarEntry();
            }
            
            // add properties file to output
            jarEntry = new JarEntry( propertiesFileName );
            testOutputStream.putNextEntry( jarEntry );
            String header = propertiesFileName;
            properties.store( testOutputStream, header );
            testOutputStream.closeEntry();
            
            // close the streams
            jarInputStream.close();
            testOutputStream.close();
        }
        catch ( IOException e ) {
            testFile.delete();
            e.printStackTrace();
            throw new SimulationException( "cannot add localized strings to jar file: " + newJarFileName, e );
        }
    }
}
