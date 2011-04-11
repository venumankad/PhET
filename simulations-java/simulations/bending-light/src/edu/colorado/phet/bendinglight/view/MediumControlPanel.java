// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.model.*;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PComboBox;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.bendinglight.view.BendingLightCanvas.labelFont;

/**
 * Controls for changing and viewing the medium type, including its current index of refraction (depends on the laser wavelength through the dispersion function).
 *
 * @author Sam Reid
 */
public class MediumControlPanel extends PNode {

    private final MediumState CUSTOM = new MediumState( BendingLightStrings.CUSTOM, BendingLightModel.MYSTERY_B.index() + 1.2, false, true );
    private final Property<Medium> medium;
    private final Property<Double> laserWavelength;
    private static final int MIN = 1;
    private static final double MAX = 1.6;

    public MediumControlPanel( final PhetPCanvas phetPCanvas,
                               final Property<Medium> medium,
                               final String name,
                               final boolean textFieldVisible,
                               final Property<Double> laserWavelength,
                               final String format,
                               final int columns ) {
        this.medium = medium;
        this.laserWavelength = laserWavelength;
        final MediumState initialMediumState = medium.getValue().getMediumState();
        final PNode topLabel = new PNode() {{
            final PText materialLabel = new PText( name ) {{
                setFont( new PhetFont( labelFont.getSize(), true ) );
            }};
            final Object[] mediumStates = new Object[] {
                    BendingLightModel.AIR,
                    BendingLightModel.WATER,
                    BendingLightModel.GLASS,
                    BendingLightModel.MYSTERY_A,
                    BendingLightModel.MYSTERY_B,
                    CUSTOM,
            };
            final PComboBox comboBox = new PComboBox( mediumStates ) {
                {
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            MediumState selected = (MediumState) getSelectedItem();
                            if ( !selected.custom ) {
                                setMediumState( selected, medium );
                            }
                        }
                    } );
                    updateComboBox();
                    medium.addObserver( new SimpleObserver() {
                        public void update() {
                            updateComboBox();
                        }
                    } );
                    setFont( labelFont );
                    setMediumState( initialMediumState, medium );
                }

                //Updates the combo box to show which item is selected
                private void updateComboBox() {
                    int selected = -1;
                    for ( int i = 0; i < mediumStates.length; i++ ) {
                        MediumState mediumState = (MediumState) mediumStates[i];
                        if ( mediumState.dispersionFunction.getIndexOfRefraction( laserWavelength.getValue() ) == medium.getValue().getIndexOfRefraction( laserWavelength.getValue() ) ) {
                            selected = i;
                        }
                    }
                    if ( selected != -1 ) {
                        setSelectedIndex( selected );
                    }
                    else {
                        setSelectedItem( CUSTOM );
                    }
                }
            };
            final PSwing comboBoxPSwing = new PSwing( comboBox ) {{
                comboBox.setEnvironment( this, phetPCanvas );
                setOffset( materialLabel.getFullBounds().getMaxX() + 10, materialLabel.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 + 1 );
            }};
            addChild( materialLabel );
            addChild( comboBoxPSwing );
        }};
        addChild( topLabel );

        //Many efforts were made to make this control work with LinearValueControl, including writing custom layouts.
        //However, for unknown reasons, some text was always clipped off, and we decided to proceed by doing the layout in Piccolo, which resolved the problem.
        final PNode slider = new PNode() {{
            final PNode topComponent = new PNode() {{
                final PText label = new PText( textFieldVisible ? BendingLightStrings.INDEX_OF_REFRACTION_COLON : BendingLightStrings.INDEX_OF_REFRACTION ) {{
                    setFont( BendingLightCanvas.labelFont );
                }};
                addChild( label );
                if ( textFieldVisible ) {
                    addChild( new PSwing( new JTextField( new DecimalFormat( format ).format( medium.getValue().getIndexOfRefraction( laserWavelength.getValue() ) ), columns ) {{
                        setFont( BendingLightCanvas.labelFont );
                        addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                double value = Double.parseDouble( getText() );
                                if ( value > MIN && value < MAX ) {
                                    setCustomIndexOfRefraction( value );
                                }
                            }
                        } );
                        new RichSimpleObserver() {
                            public void update() {
                                setText( new DecimalFormat( format ).format( medium.getValue().getIndexOfRefraction( laserWavelength.getValue() ) ) );
                            }
                        }.observe( medium, laserWavelength );
                    }} ) {{
                        setOffset( label.getFullBounds().getMaxX() + 10, label.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                    }} );
                }
            }};
            addChild( topComponent );

            class LowHighLabel extends JLabel {
                LowHighLabel( String text, boolean visible ) {
                    super( text );
                    setFont( new PhetFont( 14 ) );
                    setVisible( visible );
                }
            }

            addChild( new PSwing( new JPanel() {{
                //Use a custom layout so that we can easily position the low and high labels aligned with the focus rectangle of the slider, so they
                //appear to the left and right of the slider thumb, not between the slider track and the slider labels
                setLayout( null );
                final LowHighLabel lowLabel = new LowHighLabel( BendingLightStrings.LOW, !textFieldVisible );
                final LowHighLabel highLabel = new LowHighLabel( BendingLightStrings.HIGH, !textFieldVisible );
                final JSlider slider = new JSlider( 0, 10000 ) {{
                    final Function.LinearFunction mapping = new Function.LinearFunction( getMinimum(), getMaximum(), MIN, MAX );
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            if ( isFocusOwner() ) {//Only send events if caused by user, otherwise selecting "mystery b" causes buggy behavior
                                final double indexOfRefraction = mapping.evaluate( getValue() );
                                setCustomIndexOfRefraction( indexOfRefraction );
                            }
                        }
                    } );
                    new RichSimpleObserver() {
                        public void update() {
                            setValue( (int) mapping.createInverse().evaluate( medium.getValue().getIndexOfRefraction( laserWavelength.getValue() ) ) );
                        }
                    }.observe( medium, laserWavelength );
                    setPaintTicks( true );
                    setPaintLabels( true );
                    setLabelTable( new Hashtable<Object, Object>() {{
                        put( (int) mapping.createInverse().evaluate( BendingLightModel.AIR.index() ), new TickLabel( BendingLightStrings.AIR ) );
                        put( (int) mapping.createInverse().evaluate( BendingLightModel.WATER.index() ), new TickLabel( BendingLightStrings.WATER ) );
                        put( (int) mapping.createInverse().evaluate( BendingLightModel.GLASS.index() ), new TickLabel( BendingLightStrings.GLASS ) );
                    }} );
                    setPreferredSize( new Dimension( Math.max( (int) topComponent.getFullBounds().getWidth(), 200 ), getPreferredSize().height ) );
                }};
                lowLabel.setBounds( 0, 0, lowLabel.getPreferredSize().width, lowLabel.getPreferredSize().height );
                slider.setBounds( lowLabel.getPreferredSize().width, 0, slider.getPreferredSize().width, slider.getPreferredSize().height );
                highLabel.setBounds( lowLabel.getPreferredSize().width + slider.getPreferredSize().width, 0, highLabel.getPreferredSize().width, highLabel.getPreferredSize().height );

                add( slider );
                add( lowLabel );
                add( highLabel );
                setPreferredSize( new Dimension( lowLabel.getPreferredSize().width + slider.getPreferredSize().width + highLabel.getPreferredSize().width, slider.getPreferredSize().height ) );
            }} ) {{
                setOffset( 0, topComponent.getFullBounds().getMaxY() );
            }} );
            setOffset( 0, topLabel.getFullBounds().getMaxY() + 10 );
            topComponent.setOffset( getFullBounds().getWidth() / 2 - topComponent.getFullBounds().getWidth() / 2, 0 );
        }};

        medium.addObserver( new SimpleObserver() {
            public void update() {
                slider.setVisible( !medium.getValue().isMystery() );
            }
        } );

        addChild( slider );

        final PText unknown = new PText( BendingLightStrings.N_UNKNOWN ) {{
            setFont( labelFont );
            centerFullBoundsOnPoint( slider.getFullBounds().getCenterX(), slider.getFullBounds().getCenterY() );
            medium.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( medium.getValue().isMystery() );
                }
            } );
        }};
        addChild( unknown );
        topLabel.setOffset( getFullBounds().getCenterX() - topLabel.getFullBounds().getWidth() / 2, 0 );
        topLabel.setOffset( getFullBounds().getCenterX() - topLabel.getFullBounds().getWidth() / 2, 0 );
    }

    private void setCustomIndexOfRefraction( double indexOfRefraction ) {
        final DispersionFunction dispersionFunction = new DispersionFunction( indexOfRefraction, laserWavelength.getValue() );
        medium.setValue( new Medium( medium.getValue().shape, new MediumState( BendingLightStrings.CUSTOM, dispersionFunction, false, false ), MediumColorFactory.getColor( dispersionFunction.getIndexOfRefractionForRed() ) ) );
    }

    //From the combo box
    private void setMediumState( MediumState mediumState, Property<Medium> medium ) {
        medium.setValue( new Medium( medium.getValue().shape, mediumState, MediumColorFactory.getColor( mediumState.index() ) ) );
    }
}