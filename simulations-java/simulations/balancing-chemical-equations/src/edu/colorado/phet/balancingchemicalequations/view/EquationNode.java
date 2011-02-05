// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.IntegerSpinner;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Displays a chemical equation.
 * Reactants are on the left-hand size, products are on the right-hand side.
 * When coefficients are editable, they are displayed as editable spinners.
 * When coefficients are not editable, they are displayed as PText.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquationNode extends PhetPNode  {

    private static final Font FONT = new PhetFont( 20 );
    private static final Color SYMBOL_COLOR = Color.BLACK;

    private static final Color COEFFICIENT_COLOR = Color.BLACK;

    private final IntegerRange coefficientRange;
    private boolean editable;
    private final HorizontalAligner aligner;
    private ArrayList<TermNode> termNodes;
    private Equation equation;
    private final SimpleObserver coefficientsObserver;
    private final RightArrowNode arrowNode;
    private final PNode termsParent;

    /**
     * Constructor.
     * @param equationProperty
     * @param coefficientRange
     * @param editable
     * @param leftRightSideLength horizontal space alloted for each side of the equation
     * @param leftRightSeparation separation between the left and right sides of the equation
     */
    public EquationNode( final Property<Equation> equationProperty, IntegerRange coefficientRange, boolean editable, HorizontalAligner aligner ) {
        super();

        this.coefficientRange = coefficientRange;
        this.editable = editable;
        this.aligner = aligner;
        this.termNodes = new ArrayList<TermNode>();

        arrowNode = new RightArrowNode( equationProperty.getValue().isBalanced() );
        addChild( arrowNode );
        double x = aligner.getCenterXOffset() - ( arrowNode.getFullBoundsReference().getWidth() / 2 );
        double y = ( SymbolNode.getCapHeight() / 2 );
        arrowNode.setOffset( x, y );

        termsParent = new PhetPNode();
        addChild( termsParent );

        // coefficient changes
        coefficientsObserver = new SimpleObserver() {
            public void update() {
                arrowNode.setHighlighted( equation.isBalanced() );
            }
        };
        // equation changes
        this.equation = equationProperty.getValue();
        equationProperty.addObserver( new SimpleObserver() {
            public void update() {
                EquationNode.this.equation.removeCoefficientsObserver( coefficientsObserver );
                EquationNode.this.equation = equationProperty.getValue();
                EquationNode.this.equation.addCoefficientsObserver( coefficientsObserver );
                updateNode();
            }
        } );
    }

    public void setEditable( boolean editable ) {
        if ( editable != this.editable ) {
            this.editable = editable;
            for ( TermNode termNode : termNodes ) {
                termNode.setEditable( editable );
            }
        }
    }

    public boolean isEditable() {
        return editable;
    }

    /*
     * Rebuilds the left and right sides of the equation.
     */
    private void updateNode() {

        termsParent.removeAllChildren();

        for ( TermNode node : termNodes ) {
            node.cleanup();
        }
        termNodes.clear();

        updateSideOfEquation( equation.getReactants(), aligner.getReactantXOffsets( equation ) );
        updateSideOfEquation( equation.getProducts(), aligner.getProductXOffsets( equation ) );
    }

    /*
     * Updates one side of the equation.
     * This layout algorithm depends on the fact that all terms contain at least 1 capital letter.
     * This allows us to align the baselines of HTML-formatted text.
     */
    private void updateSideOfEquation( EquationTerm[] terms, double[] xOffsets ) {
        assert( terms.length == xOffsets.length );
        for ( int i = 0; i < terms.length; i++ ) {

            // term
            TermNode termNode = new TermNode( coefficientRange, terms[i], editable );
            termNodes.add( termNode );
            termsParent.addChild( termNode );
            termNode.setOffset( xOffsets[i] - ( termNode.getFullBoundsReference().getWidth() / 2 ), 0 );

            // plus sign, centered between 2 terms
            if ( i > 0 ) {
                PlusNode plusNode = new PlusNode();
                termsParent.addChild( plusNode );
                double x =  xOffsets[i] - ( ( xOffsets[i] - xOffsets[i-1] ) / 2 ) - ( plusNode.getFullBoundsReference().getWidth() / 2 ); // centered between 2 offsets
                double y = ( SymbolNode.getCapHeight() / 2 ) - ( plusNode.getFullBoundsReference().getHeight() / 2 );
                plusNode.setOffset( x, y );
            }
        }
    }

    /*
     * A term in the equation, includes the coefficient and symbol.
     * The coefficient may or may not be editable.
     */
    private static class TermNode extends PhetPNode {

        private final CoefficientNode coefficientNode;

        public TermNode( IntegerRange coefficientRange, EquationTerm term, boolean editable ) {

            // coefficient
            coefficientNode = new CoefficientNode( coefficientRange, term.getActualCoefficientProperty(), editable );
            addChild( coefficientNode );

            // molecule symbol
            SymbolNode symbolNode = new SymbolNode( term.getMolecule().getSymbol() );
            addChild( symbolNode );

            // layout
            coefficientNode.setOffset( 0, 0 );
            symbolNode.setOffset( coefficientNode.getFullBoundsReference().getMaxX() + 5, 0 );
        }

        public void setEditable( boolean editable ) {
            coefficientNode.setEditable( editable );
        }

        public void cleanup() {
            coefficientNode.removeCoefficientObserver();
        }
    }

    /*
     * Coefficient, can be read-only or editable
     */
    private static class CoefficientNode extends PhetPNode {

        private final Property<Integer> coefficientProperty;
        private final SimpleObserver coefficientObserver;
        private final PText textNode;
        private final PSwing spinnerNode;

        public CoefficientNode( IntegerRange range, final Property<Integer> coefficientProperty, boolean editable ) {

            // read-only text
            textNode = new PText();
            textNode.setFont( FONT );
            textNode.setTextPaint( COEFFICIENT_COLOR );
            addChild( textNode );
            textNode.setVisible( !editable );

            // editable spinner
            final IntegerSpinner spinner = new IntegerSpinner( range );
            spinner.setFont( FONT );
            spinner.setForeground( COEFFICIENT_COLOR );
            spinner.setValue( coefficientProperty.getValue() );
            spinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    coefficientProperty.setValue( spinner.getIntValue() );
                }
            } );
            spinnerNode = new PSwing( spinner );
            addChild( spinnerNode );
            spinnerNode.setVisible( editable );

            // layout
            spinnerNode.setOffset( 0, 0 );
            textNode.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    textNode.setOffset( spinnerNode.getFullBoundsReference().getMaxX() - textNode.getFullBoundsReference().getWidth(), 0 );
                }
            } );

            // coefficient observer
            this.coefficientProperty = coefficientProperty;
            coefficientObserver = new SimpleObserver() {
                public void update() {
                    textNode.setText( String.valueOf( coefficientProperty.getValue() ) );
                    spinner.setIntValue( coefficientProperty.getValue() );
                }
            };
            coefficientProperty.addObserver( coefficientObserver );
        }

        public void setEditable( boolean editable ) {
            textNode.setVisible( !editable );
            spinnerNode.setVisible( editable );
        }

        public void removeCoefficientObserver() {
            coefficientProperty.removeObserver( coefficientObserver );
        }
    }

    /*
     * Molecule symbol
     */
    private static class SymbolNode extends HTMLNode {

        public SymbolNode( String html ) {
            super( html, SYMBOL_COLOR, FONT );
        }

        /**
         * Gets the height of a capital letter.
         */
        public static double getCapHeight() {
            return new SymbolNode( "T" ).getFullBounds().getHeight();
        }
    }
}
