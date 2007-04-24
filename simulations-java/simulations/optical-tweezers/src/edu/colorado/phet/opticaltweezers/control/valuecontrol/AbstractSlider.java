/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.valuecontrol;

import javax.swing.JSlider;

/**
 * AbstractSlider is the base class for all extensions of JSlider that provide
 * a mapping between slider values (integer precision) and model values (double precision).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractSlider extends JSlider {
    
    private AbstractMappingStrategy _strategy;
    
    protected AbstractSlider( AbstractMappingStrategy strategy ) {
        super();
        _strategy = strategy;
        setMinimum( strategy.getSliderMin() );
        setMaximum( strategy.getSliderMax() );
        setValue( strategy.getSliderMin() );
    }
    
    public void setModelValue( double modelValue ) {
        int sliderValue = _strategy.modelToSlider( modelValue );
        setValue( sliderValue );
    }
    
    public double getModelValue() {
        int sliderValue = getValue();
        return sliderToModel( sliderValue );
    }
    
    public double getModelMin() {
        return _strategy.getModelMin();
    }
    
    public double getModelMax() {
        return _strategy.getModelMax();
    }
    
    public int getSliderMin() {
        return _strategy.getSliderMin();
    }
    
    public int getSliderMax() {
        return _strategy.getSliderMax();
    }
    
    public double sliderToModel( int sliderValue ) {
        return _strategy.sliderToModel( sliderValue );
    }
    
    public int modelToSlider( double modelValue ) {
        return _strategy.modelToSlider( modelValue );
    }
}
