/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler.ButtonEventListener;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class represents a button that is a PNode and thus can be placed 
 * within another PNode or on a PCanvas, and that is filled with a color
 * gradient to make it more "fun" looking (and thus suitable for adding to
 * the play area of the sims).
 *
 * @author John Blanco
 */
public class GradientButtonNode extends PhetPNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    // Constants that control the amount of padding (or space) around the text
    // on each side of the button.
    private static final int VERTICAL_PADDING = 3;
    private static final int HORIZONTAL_PADDING = 3;
    
    // Constant that controls where the shadow shows up and how far the button
    // translates when pushed.
    private static final int SHADOW_OFFSET = 3;
    
    // Defaults for values that might not be specified at construction.
    private static final Color DEFAULT_COLOR = Color.GRAY;
    private static final int DEFAULT_FONT_SIZE = 14;
    
    // Constants that control various visual aspects of the button.
    private static final double COLOR_SCALING_FACTOR = 0.5;
    private static final double BUTTON_CORNER_ROUNDEDNESS = 8;
    private static final float SHADOW_TRANSPARENCY = 0.2f;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private PPath _button;
    private HTMLNode _buttonText;
    private ArrayList _actionListeners;
    
    // Gradient for when the mouse is not over the button.
    private GradientPaint _mouseNotOverGradient;
    
    // Gradient for when the mouse is over the button.
    private GradientPaint _mouseOverGradient;
    
    // Gradient for when the button is armed.
    private GradientPaint _armedGradient;
    
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    /**
     * Construct a gradient button node.
     * 
     * @param label - Text that will appear on the button.
     * @param fontSize - Size of font for the label text.
     * @param buttonColor - Overall color of button from which gradient will
     * be created.
     */
    public GradientButtonNode(String label, int fontSize, Color buttonColor){
        
        // Initialize local data.
        _actionListeners = new ArrayList();
        
        // Create the label node first, since its size will be the basis for
        // the other components of this button.
        _buttonText = new HTMLNode(label);        
        _buttonText.setFont(new PhetDefaultFont(Font.BOLD, fontSize));
        _buttonText.setOffset(HORIZONTAL_PADDING, VERTICAL_PADDING);
        _buttonText.setPickable( false );

        // Create the gradients that will be used to color the button.
        _mouseNotOverGradient = new GradientPaint((float)_buttonText.getFullBounds().width / 2, 0f,
                getBrighterColor( buttonColor ), 
                (float)_buttonText.getFullBounds().width * 0.5f, (float)_buttonText.getFullBounds().height, 
                buttonColor);
        _mouseOverGradient = new GradientPaint((float)_buttonText.getFullBounds().width / 2, 0f,
                getBrighterColor(getBrighterColor( buttonColor )), 
                (float)_buttonText.getFullBounds().width * 0.5f, (float)_buttonText.getFullBounds().height, 
                getBrighterColor( buttonColor ));
        _armedGradient = new GradientPaint((float)_buttonText.getFullBounds().width / 2, 0f,
                buttonColor, 
                (float)_buttonText.getFullBounds().width * 0.5f, (float)_buttonText.getFullBounds().height, 
                getBrighterColor( buttonColor ));

        // Create the button node.
        RoundRectangle2D buttonShape = new RoundRectangle2D.Double(0, 0, 
                _buttonText.getFullBounds().width + 2 * HORIZONTAL_PADDING,
                _buttonText.getFullBounds().height + 2 * VERTICAL_PADDING,
                BUTTON_CORNER_ROUNDEDNESS, BUTTON_CORNER_ROUNDEDNESS);
                
        _button = new PPath(buttonShape);
        _button.setPaint( _mouseNotOverGradient );
        _button.addInputEventListener( new CursorHandler() ); // Does the finger pointer cursor thing.

        // Create the shadow node.
        PNode buttonShadow = new PPath(buttonShape);
        buttonShadow.setPaint( Color.BLACK );
        buttonShadow.setPickable( false );
        buttonShadow.setTransparency( SHADOW_TRANSPARENCY );
        buttonShadow.setOffset( SHADOW_OFFSET, SHADOW_OFFSET );
        
        // Register a handler to watch for button state changes.
        ButtonEventHandler handler = new ButtonEventHandler();
        _button.addInputEventListener( handler );
        handler.addButtonEventListener( new ButtonEventListener() {
            private boolean focus = false; // true if the button has focus
            public void setFocus( boolean focus ) {
                this.focus = focus;
                _button.setPaint( focus ? _mouseOverGradient : _mouseNotOverGradient);
            }
            public void setArmed( boolean armed ) {
                if ( armed ) {
                    _button.setPaint( _armedGradient );
                    _button.setOffset( SHADOW_OFFSET, SHADOW_OFFSET );
                    _buttonText.setOffset(HORIZONTAL_PADDING + SHADOW_OFFSET, VERTICAL_PADDING + SHADOW_OFFSET);
                }
                else {
                    _button.setPaint( focus ? _mouseOverGradient : _mouseNotOverGradient );
                    _button.setOffset( 0, 0 );
                    _buttonText.setOffset(HORIZONTAL_PADDING, VERTICAL_PADDING);
                }
            }
            public void fire() {
                ActionEvent event = new ActionEvent(this, 0, "BUTTON_FIRED");
                for (int i =0; i < _actionListeners.size(); i++){
                    ((ActionListener)_actionListeners.get(i)).actionPerformed( event );
                }
            }
        } );

        // Add the children to the node in the appropriate order so that they
        // appear as desired.
        addChild( buttonShadow );
        addChild( _button );
        addChild( _buttonText );
    }
    
    /**
     * Constructor for creating a default gradient button with only the label
     * specified.
     * 
     * @param label - Text that will appear on button.
     */
    GradientButtonNode(String label){
        this(label, DEFAULT_FONT_SIZE, DEFAULT_COLOR);
    }
    
    /**
     * Constructor for creating a button assuming the default font size.
     * 
     * @param label
     * @param color
     */
    GradientButtonNode(String label, Color color){
        this(label, DEFAULT_FONT_SIZE, color);
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------

    public void addActionListener( ActionListener listener ) {
        if (!_actionListeners.contains( listener )){
            _actionListeners.add( listener );
        }
    }

    public void removeActionListener( ActionListener listener ) {
        _actionListeners.remove( listener );
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private Color getBrighterColor(Color origColor){
        int red = origColor.getRed() + (int)Math.round( (double)(255 - origColor.getRed()) * COLOR_SCALING_FACTOR); 
        int green = origColor.getGreen() + (int)Math.round( (double)(255 - origColor.getGreen()) * COLOR_SCALING_FACTOR); 
        int blue = origColor.getBlue() + (int)Math.round( (double)(255 - origColor.getBlue()) * COLOR_SCALING_FACTOR); 
        return new Color ( red, green, blue );
    }
    
    
    //------------------------------------------------------------------------
    // Test Harness
    //------------------------------------------------------------------------
    
    public static void main( String[] args ) {
        
        ActionListener listener = new ActionListener(){
            public void actionPerformed(ActionEvent event){
                System.out.println("actionPerformed event= " + event);
            }
        };
        
        GradientButtonNode testButton01 = new GradientButtonNode("Test Me", 16, Color.GREEN);
        testButton01.setOffset( 5, 5 );
        testButton01.addActionListener( listener );
        
        GradientButtonNode testButton02 = new GradientButtonNode("<html>Test <br> Me Too</html>", 24, new Color(0x99cccc));
        testButton02.setOffset( 200, 5 );
        testButton02.addActionListener( listener );
        
        GradientButtonNode testButton03 = new GradientButtonNode("<html><center>Default Color<br>and Font<center></html>");
        testButton03.setOffset( 5, 200 );
        testButton03.addActionListener( listener );
        
        GradientButtonNode testButton04 = new GradientButtonNode("Default Font Size", new Color(0xcc3366));
        testButton04.setOffset( 200, 200 );
        testButton04.addActionListener( listener );
        
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.addScreenChild( testButton01 );
        canvas.addScreenChild( testButton02 );
        canvas.addScreenChild( testButton03 );
        canvas.addScreenChild( testButton04 );
        
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( 400, 300 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); 
        frame.setVisible( true );
    }
}
