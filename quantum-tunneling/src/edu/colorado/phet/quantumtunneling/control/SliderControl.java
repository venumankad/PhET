/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;


/**
 * SliderControl combines a JSlider and JLabel into one panel that 
 * can be treated like a JSlider.  As the slider value is changed, the label 
 * automatically updates to reflect the new value.
 * The default "look" is to have labels at the min and max tick marks.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SliderControl extends JPanel implements ChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JSlider _slider;  // slider that the user moves
    private JLabel _label; // value that updates as the slider is moved
    private double _min, _max;
    private double _multiplier;
    private DecimalFormat _numberFormat;
    private String _labelFormat; // format that specifies how the value is displayed
    private EventListenerList _listenerList; // notification of slider changes
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
 
    /**
     * Constructor.
     * 
     * @param min minimum value (model coordinates)
     * @param max maximum value (model coordinates)
     * @param tickSpacing space between tick marks (model coordinates)
     * @param numberOfSignificantDecimalPlaces
     * @param labelFormat formatter used to format the label
     * @param insets insets
     * @throws IllegalArgumentException
     */
    public SliderControl( 
            double min, 
            double max, 
            double tickSpacing,
            int numberOfSignificantDecimalPlaces, 
            String labelFormat, 
            Insets insets ) {
        
        super();
        
        // Process arguments
        {
            if ( min >= max ) {
                throw new IllegalArgumentException( "min >= max" );
            }
            if ( numberOfSignificantDecimalPlaces < 0 ) {
                throw new IllegalArgumentException( "multiplier must be >= 0" );
            }

            _min = min;
            _max = max;
            
            _multiplier = Math.pow( 10, numberOfSignificantDecimalPlaces );
            
            String format = "0.";
            for ( int i = 0; i < numberOfSignificantDecimalPlaces; i++ ) {
                format += "0";
            }
            _numberFormat = new DecimalFormat( format );
            
            _labelFormat = labelFormat;
        }
        
        _listenerList = new EventListenerList();
        
        // Label
        _label = new JLabel();
        
        // Slider
        {
            _slider = new JSlider();
            _slider.setMinimum( (int) ( _min * _multiplier ) );
            _slider.setMaximum( (int) ( _max * _multiplier ) );

            // Ticks
            _slider.setMajorTickSpacing( (int) ( ( _max - _min ) * _multiplier ) );
            _slider.setMinorTickSpacing( (int) ( tickSpacing * _multiplier ) );
            _slider.setPaintTicks( true );

            // Labels at min & max
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Integer( (int) ( _max * _multiplier ) ), new JLabel( String.valueOf( _max ) ) );
            labelTable.put( new Integer( (int) ( _min * _multiplier ) ), new JLabel( String.valueOf( _min ) ) );
            getSlider().setLabelTable( labelTable );
            getSlider().setPaintLabels( true );
        }
        
        // Layout
        {
            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            if ( insets != null ) {
                layout.setInsets( insets );
            }
            setLayout( layout );
            layout.addAnchoredComponent( _label, 0, 0, GridBagConstraints.WEST );
            layout.addAnchoredComponent( _slider, 1, 0, GridBagConstraints.WEST );
        }
        
        // Interactivity
        _slider.addChangeListener( this );
        
        updateLabel();
    }
    
    /**
     * Constructor. Same as other constructor, but uses default insets.
     * 
     * @param min
     * @param max
     * @param tickSpacing
     * @param numberOfSignificantDecimalPlaces
     * @param labelFormat
     */
    public SliderControl( 
            double min, 
            double max, 
            double tickSpacing,
            int numberOfSignificantDecimalPlaces, 
            String labelFormat  ) {
        this( min, max, tickSpacing, numberOfSignificantDecimalPlaces, labelFormat, null );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the value.
     * 
     * @param value
     */
    public void setValue( double value ) {
        int sliderValue = 0;
        if ( isInverted() ) {
            sliderValue = (int) ( ( ( _max + _min ) * _multiplier ) - ( value * _multiplier ) );
        }
        else {
            sliderValue = (int) ( value * _multiplier );
        }
        _slider.setValue( sliderValue );
        updateLabel();
    }
    
    /**
     * Gets the value.
     * 
     * @return the value
     */
    public double getValue() {
        int sliderValue = getSlider().getValue();
        double value = 0;
        if ( isInverted() ) {
            value = ( ( (_max + _min) * _multiplier ) - sliderValue ) / _multiplier;
            if ( value > _max ) {
                value = _max; // adjust for rounding error
            }
        }
        else {
            value = sliderValue / _multiplier;
        }
        return value;
    }
   
    /**
     * Inverts the slider.
     * <p>
     * You may want to invert the slider if the distance between your lowest two 
     * ticks is smaller than the distance between all other ticks.  In order
     * to pull this off with a JSlider, we need to invert the slider
     * and make use of both major and minor tick spacing.
     * 
     * @param inverted true or false
     */
    public void setInverted( boolean inverted ) {
        if ( isInverted() != inverted ) {
            if ( inverted ) {
                _slider.setInverted( true );
                Hashtable labelTable = new Hashtable();
                labelTable.put( new Integer( (int) ( _max * _multiplier ) ), new JLabel( String.valueOf( _min ) ) );
                labelTable.put( new Integer( (int) ( _min * _multiplier ) ), new JLabel( String.valueOf( _max ) ) );
                getSlider().setLabelTable( labelTable );
                getSlider().setPaintLabels( true );
            }
            else {
                _slider.setInverted( false );
                Hashtable labelTable = new Hashtable();
                labelTable.put( new Integer( (int) ( _max * _multiplier ) ), new JLabel( String.valueOf( _max ) ) );
                labelTable.put( new Integer( (int) ( _min * _multiplier ) ), new JLabel( String.valueOf( _min ) ) );
                getSlider().setLabelTable( labelTable );
                getSlider().setPaintLabels( true );
            }
        }
    }
    
    /**
     * Is the slider inverted?
     * 
     * @return true or false
     */
    public boolean isInverted() {
        return _slider.getInverted();
    }
    
    /**
     * Provides access to the JSlider.
     * 
     * @return the JSlider
     */
    public JSlider getSlider() {
        return _slider;
    }
    
    /**
     * Gets the JLabel that displays the value.
     * 
     * @return the JLabel
     */
    protected JLabel getLabel() {
        return _label;
    }
    
    /**
     * Sets the format used to format the label.
     * See MessageFormat.
     * 
     * @param format
     */
    public void setLabelFormat( String format ) {
        assert( format != null );
        _labelFormat = format;
        updateLabel();
    }

    /**
     * Determine whether the slider value is being adjusted.
     * The result is true if the user is dragging the slider
     * but has not yet released it.
     * 
     * @return true or false
     */
    public boolean isAdjusting() {
        return _slider.getValueIsAdjusting();
    }
    
    /*
     * Updates the label when the slider is changed.
     */
    protected void updateLabel() {
        double value = getValue();
        String valueString = _numberFormat.format( value );
        Object[] args = { valueString };
        String text = MessageFormat.format( _labelFormat, args );
        getLabel().setText( text );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * ChangeListener implementation.
     * 
     * @param e the event
     */
    public void stateChanged( ChangeEvent e ) {
        updateLabel();
        fireChangeEvent( new ChangeEvent( this ) );
    }
 
    /**
     * Adds a ChangeListener.
     * 
     * @param listener the listener
     */
    public void addChangeListener( ChangeListener listener ) {

        _listenerList.add( ChangeListener.class, listener );
    }

    /**
     * Removes a ChangeListener.
     * 
     * @param listener the listener
     */
    public void removeChangeListener( ChangeListener listener ) {

        _listenerList.remove( ChangeListener.class, listener );
    }

    /**
     * Fires a ChangeEvent.
     * 
     * @param event the event
     */
    private void fireChangeEvent( ChangeEvent event ) {

        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener) listeners[i + 1] ).stateChanged( event );
            }
        }
    }
}
