// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.util.Logger;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Tests integration of Jmol with Piccolo.
 * <p/>
 * Jmol doc at http://jmol.sourceforge.net/docs
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestJmolPiccolo {

    private static class TestCanvas extends PhetPCanvas {
        public TestCanvas() {
            setPreferredSize( new Dimension( 1024, 768 ) );
            setBackground( Color.LIGHT_GRAY );

            TestNode moleculeNode = new TestNode( new Ammonia() );
            getLayer().addChild( moleculeNode );
            moleculeNode.setOffset( 100, 100 );
        }
    }

    private static class TestNode extends PSwing {
        public TestNode( IMolecule molecule ) {
            super( new TestPanel( molecule ) );
        }
    }

    //TODO replace this with "extends JmolPanel"
    private static class TestPanel extends JPanel {

        private JmolViewer viewer = null;
        private final String loadingString;

        public TestPanel( final IMolecule molecule ) {

            this.loadingString = "Loading Jmol..."; //XXX i18n, and I don't see this rendered

            // create the 3D view after we have shown the "loading" text and dialog
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    // don't dump everything out into the console
                    Logger.setLogLevel( Logger.LEVEL_WARN );

                    // create the 3D view
                    JmolViewer viewer = JmolViewer.allocateViewer( TestPanel.this, new SmarterJmolAdapter(), null, null, null, "-applet", null );
                    viewer.setBooleanProperty( "antialiasDisplay", true );
                    viewer.setBooleanProperty( "autoBond", false );

                    String errorString = viewer.openStringInline( molecule.getData() );
                    if ( errorString != null ) {
                        throw new RuntimeException( "Jmol problem: " + errorString );
                    }

                    // set the visible colors to our model colors
                    molecule.adjustColors( viewer );

                    // store reference to this AFTER we have processed the molecule data, so that the "Loading" text shows up until the molecule is loaded
                    TestPanel.this.viewer = viewer;

                    //TODO reverse engineer page source at http://www.chemtube3d.com/ElectrostaticSurfacesPolar.html

                    // working...
                    viewer.script( "wireframe 0.2; spacefill 25%" ); // ball-and-stick view
                    viewer.script( "unbind \"_popupMenu\"" ); // hide the right-click popup menu
                    viewer.script( "frank off" ); // hide the "Jmol" watermark in the lower-right corner
                    viewer.script( "label" ); // label the atoms
                    viewer.script( "dipole bonds on" ); // show bond dipoles
                    viewer.script( "dipole molecular on" ); // show molecular dipole
                    viewer.script( "isosurface resolution 6 solvent map mep translucent" ); // molecular electrostatic potential (MEP)
//                    viewer.script( "color partialCharge" ); // color atoms based on partial charge

                    // not working...
                    viewer.script( "background black" );//XXX how to make this transparent?

                    repaint();
                }
            } );

            setOpaque( false );
            setBackground( new Color( 0, 0, 0, 0 ) );
            setPreferredSize( new Dimension( 400, 400 ) );//XXX how to determine this dynamically?
        }

        @Override public void paint( Graphics g ) {
            // Jmol's canonical example of embedding in other Java is to override the paint method, so we do it here

            if ( viewer == null ) {
                // if we have no viewer yet, we show the loading text on a black background
                super.paint( g );

                // create a piccolo node (helpful for centering and styling)
                PText text = new PText( loadingString ) {{
                    setTextPaint( Color.RED ); //XXX this should depend on the background color
                    setFont( new PhetFont( 20 ) );

                    // center in the panel
                    setOffset( ( getWidth() - getFullBounds().getWidth() ) / 2, ( getHeight() - getFullBounds().getHeight() ) / 2 );
                }};

                // paint the piccolo node onto the panel
                text.fullPaint( new PPaintContext( (Graphics2D) g ) );
            }
            else {
                // copied from Jmol's Integration.java
                Dimension currentSize = new Dimension();
                getSize( currentSize ); // stores size in currentSize
                Rectangle clipBounds = new Rectangle();
                g.getClipBounds( clipBounds );
                viewer.renderScreenImage( g, currentSize, clipBounds );
            }
        }
    }

    private interface IMolecule {
        public String getSymbol();

        public String getDisplayName();

        public String getData();

        public void adjustColors( JmolViewer viewer );
    }

    private static class AbstractMolecule implements IMolecule {

        private final String symbol, name, resourceFilename;

        public AbstractMolecule( String symbol, String name, String resourceFilename ) {

            this.symbol = symbol;
            this.name = name;
            this.resourceFilename = resourceFilename;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getDisplayName() {
            return name;
        }

        public String getData() {
            try {
                PhetResources resources = new PhetResources( MPConstants.PROJECT_NAME );
                BufferedReader structureReader = new BufferedReader( new InputStreamReader( resources.getResourceAsStream( resourceFilename ) ) );
                String s = "";
                String line = structureReader.readLine();
                while ( line != null ) {
                    s = s + line + "\n";
                    line = structureReader.readLine();
                }
                return s;
            }
            catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        }

        public void adjustColors( JmolViewer viewer ) {
            // default does nothing
        }
    }

    private static class Acetone extends AbstractMolecule {
        public Acetone() {
            super( "X", "Acetone", "jmol/acetone.smol" );
        }
    }

    private static class Ammonia extends AbstractMolecule {
        public Ammonia() {
            super( "X", "Ammonia", "jmol/ammonia.smol" );
        }
    }

    private static class BoronTriflouride extends AbstractMolecule {
        public BoronTriflouride() {
            super( "X", "Boron Triflouride", "jmol/borontriflouride.smol" );
        }
    }

    private static class Formaldehyde extends AbstractMolecule {
        public Formaldehyde() {
            super( "X", "Formaldehyde", "jmol/formaldehyde.smol" );
        }
    }

    private static class HydrogenFlouride extends AbstractMolecule {
        public HydrogenFlouride() {
            super( "X", "Hydrogen Flouride", "jmol/hydrogenflouride.smol" );
        }
    }

    private static class Methane extends AbstractMolecule {
        public Methane() {
            super( "X", "Methane", "jmol/methane.smol" );
        }
    }

    private static class Methylflouride extends AbstractMolecule {
        public Methylflouride() {
            super( "X", "Methylflouride", "jmol/methylflouride.smol" );
        }
    }

    private static class Water extends AbstractMolecule {
        public Water() {
            super( "X", "Water", "jmol/water.smol" );
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame() {{
            setContentPane( new TestCanvas() );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }};
        frame.setVisible( true );
    }
}
