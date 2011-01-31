// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.greenhouse.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PLine;
import edu.umd.cs.piccolox.util.LineShape;
import edu.umd.cs.piccolox.util.XYArray;


/**
 * This class defines a dialog window that shows a representation of the
 * electromagnetic spectrum.
 *
 */
public class SpectrumDialog extends PaintImmediateDialog {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Make the spectrum diagram static so we don't have to regenerate it each
    // time the dialog is shown, since it takes some computation to create it.
    private static final SpectrumDiagram SPECTRUM_DIAGRAM = new SpectrumDiagram();

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public SpectrumDialog( Frame parentFrame ) {
        super( parentFrame, true );

        // Don't let the user resize this window.
        setResizable( false );

        // Create the panel that will contain the canvas and the "Close" button.
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) ); // top, left, bottom, right
        mainPanel.setLayout( new BoxLayout( mainPanel, BoxLayout.Y_AXIS ) );

        // Create the canvas and add it to the panel.
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setBackground( new Color( 233, 236, 174 ) );
        canvas.setPreferredSize( SpectrumDiagram.OVERALL_DIMENSIONS );
        canvas.setBorder( BorderFactory.createEtchedBorder() ); // top, left, bottom, right
        mainPanel.add( canvas );

        // Add the spectrum diagram to the canvas.
        canvas.addWorldChild( SPECTRUM_DIAGRAM );

        // Add an invisible panel that will create space between the diagram
        // and the close button.
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize( new Dimension( 1, 15) );
        mainPanel.add( spacerPanel );

        // Add the close button.
        // TODO: i18n
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                SpectrumDialog.this.dispose();
            }
        });
        closeButton.setAlignmentX( Component.CENTER_ALIGNMENT );
        mainPanel.add( closeButton );

        // Add to the dialog
        setContentPane( mainPanel );
        pack();
    }

    /**
     * Class that contains the diagram of the EM spectrum.  This is done as a
     * PNode in order to be translatable.  This class includes the arrows,
     * the spectrum strip, the wavelength indicator, etc.  In other words, it
     * is the top level node within which the constituent parts that make up
     * the entire diagram are contained.
     */
    private static class SpectrumDiagram extends PNode {

        private static final Dimension OVERALL_DIMENSIONS = new Dimension( 670, 400 );
        private static final double HORIZONTAL_INSET = 20;
        private static final double VERTICAL_INSET = 20;

        public SpectrumDiagram(){

            // Add the title.
            // TODO: i18n
            PText title = new PText("Light Spectrum");
            title.setFont( new PhetFont( 30 ) );
            title.setOffset( OVERALL_DIMENSIONS.getWidth() / 2 - title.getFullBoundsReference().width / 2, VERTICAL_INSET );
            addChild( title );

            // Add the frequency arrow.
            // TODO: i18n
            LabeledArrow frequencyArrow = new LabeledArrow( OVERALL_DIMENSIONS.getWidth() - HORIZONTAL_INSET * 2,
                    LabeledArrow.Orientation.POINTING_RIGHT, "Increasing frequency and energy", new Color( 98, 93, 169 ),
                    Color.WHITE );
            frequencyArrow.setOffset( HORIZONTAL_INSET, title.getFullBoundsReference().getMaxY() + 10 );
            addChild( frequencyArrow );

            // Add the spectrum portion.
            LabeledSpectrumNode spectrum = new LabeledSpectrumNode( OVERALL_DIMENSIONS.width - 2 * HORIZONTAL_INSET );
            spectrum.setOffset( HORIZONTAL_INSET, frequencyArrow.getFullBoundsReference().getMaxY() + 20 );
            addChild( spectrum );

            // Add the wavelength arrow.
            // TODO: i18n
            LabeledArrow wavelengthArrow = new LabeledArrow( OVERALL_DIMENSIONS.getWidth() - HORIZONTAL_INSET * 2,
                    LabeledArrow.Orientation.POINTING_LEFT, "Increasing wavelength", Color.WHITE,
                    new Color( 205, 99, 78 ) );
            wavelengthArrow.setOffset( HORIZONTAL_INSET, spectrum.getFullBoundsReference().getMaxY() + 10 );
            addChild( wavelengthArrow );

            // Add the diagram that depicts the wave that gets shorter.
            DecreasingWavelengthWaveNode decreasingWavelengthNode =
                new DecreasingWavelengthWaveNode( OVERALL_DIMENSIONS.width - 2 * HORIZONTAL_INSET );
            decreasingWavelengthNode.setOffset( HORIZONTAL_INSET, wavelengthArrow.getFullBoundsReference().getMaxY() + 20 );
            addChild( decreasingWavelengthNode );
        }
    }

    /**
     * Class that defines a labeled arrow node.
     */
    private static class LabeledArrow extends PNode {
        public static double ARROW_HEAD_HEIGHT = 40;
        private static double ARROW_HEAD_WIDTH = 40;
        private static double ARROW_TAIL_WIDTH = 20;
        private static Font LABEL_FONT = new PhetFont( 16 );
        private static Stroke STROKE = new BasicStroke( 2 );

        public enum Orientation { POINTING_LEFT, POINTING_RIGHT };

        public LabeledArrow ( double length, Orientation orientation, String captionText, Color topColor, Color bottomColor ){

            // Create the paint that will be used to depict the arrow.  It is
            // assumed that the arrow has a gradient that changes in the
            // vertical direction.
            Paint gradientPaint = new GradientPaint( 0, (float) -ARROW_HEAD_HEIGHT / 2, topColor, 0,
                    (float) ARROW_HEAD_HEIGHT / 2, bottomColor );

            // Create and add the arrow node.
            ArrowNode arrowNode;
            if ( orientation == Orientation.POINTING_RIGHT ) {
                arrowNode = new ArrowNode( new Point2D.Double( 0, 0 ), new Point2D.Double( length, 0 ),
                        ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
            }
            else {
                assert orientation == Orientation.POINTING_LEFT;
                arrowNode = new ArrowNode( new Point2D.Double( length, 0 ), new Point2D.Double( 0, 0 ),
                        ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
            }
            arrowNode.setPaint( gradientPaint );
            arrowNode.setStroke( STROKE );
            arrowNode.setOffset( 0, ARROW_HEAD_HEIGHT / 2 );
            addChild( arrowNode );

            // Create and add the textual label.
            PText label = new PText( captionText );
            label.setFont( LABEL_FONT );
            label.centerFullBoundsOnPoint( arrowNode.getFullBoundsReference().getCenterX(),
                    arrowNode.getFullBoundsReference().getCenterY() );
            addChild( label );
        }
    }

    /**
     * Class that depicts the frequencies and wavelengths of the EM spectrum
     * and labels the subsections (e.g. "Infrared").
     *
     * @author John Blanco
     */
    private static class LabeledSpectrumNode extends PNode {

        private static final double STRIP_HEIGHT = 65;
        private static final double MIN_FREQUENCY = 1E3;
        private static final double MAX_FREQUENCY = 1E21;

        private final double stripWidth;

        public LabeledSpectrumNode( double width ){
            stripWidth = width;
            // Create the "strip", which is the solid background portions that
            // contains the different bands and that has tick marks on the top
            // and bottom.
            PNode strip = new PhetPPath( new Rectangle2D.Double( 0, 0, width, STRIP_HEIGHT ), new Color(217, 223, 226),
                    new BasicStroke( 2f ), Color.BLACK );
            addChild( strip );
        }

        /**
         * Convert the given frequency to an offset from the left edge of the
         * spectrum strip.
         * @param frequency - Frequency in Hz.
         */
        private double getOffsetFromFrequency( double frequency ){
            assert frequency >= MIN_FREQUENCY && frequency <= MAX_FREQUENCY;
            double logarithmicRange = Math.log10( MAX_FREQUENCY ) - Math.log10( MIN_FREQUENCY );
            double logarithmicFrequency = Math.log10( frequency );
            return ( logarithmicFrequency - Math.log10( MIN_FREQUENCY ) )/ logarithmicRange * stripWidth;
        }

        /**
         * Convert the given wavelength to an offset from the left edge of the
         * spectrum strip.
         * @param wavelength - wavelength in meters
         */
        private double getOffsetFromWavelength( double wavelength ){
            return getOffsetFromFrequency( 299792458 / wavelength );
        }
    }

    /**
     * Class that depicts a wave that gets progressively shorter in wavelength
     * from left to right.
     */
    private static class DecreasingWavelengthWaveNode extends PNode {
        public DecreasingWavelengthWaveNode( double width ){
            // Create and add the boundary and background.
            double boundingBoxHeight = width * 0.1; // Arbitrary, adjust as needed.
            PNode boundingBox = new PhetPPath(new Rectangle2D.Double( 0, 0, width, width * 0.1 ),
                    new Color(217, 223, 226), new BasicStroke(2f), Color.black );
            addChild( boundingBox );

            // Create the line that represents the decreasing wavelength.
            int numPointsOnLine = 2000;
            XYArray pointArray = new XYArray(new double[numPointsOnLine * 2]);
            for ( int i = 0; i < numPointsOnLine; i++ ){
                double x = i * ( width / ( numPointsOnLine - 1 ) );
                pointArray.setX( i, x );
                double proportion = x / width;
                pointArray.setY( i, ( Math.sin( ((Math.pow( proportion, 4 ) * 25) + 3) * proportion * Math.PI ) * boundingBoxHeight * 0.40 + boundingBoxHeight / 2 ) );
            }
            LineShape lineShape = new LineShape( pointArray );
            PLine squigglyLine = new PLine( lineShape );
            squigglyLine.setStroke( new BasicStroke( 2f ) );
            addChild( squigglyLine );
        }
    }
}
