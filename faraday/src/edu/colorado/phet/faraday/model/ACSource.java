/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.model.ModelElement;


/**
 * ACSource is the model of an Alternating Current (AC) source.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ACSource extends AbstractVoltageSource implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private double MIN_STEPS_PER_CYCLE = 10;
        
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
//    private double _voltage; 
    private double _maxAmplitude; // 0...1
    private double _frequency; // 0...1
    private int _sign; // -1 or +1
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ACSource() {
        super();
//        _voltage = 0.0;
        _maxAmplitude = 1.0; // biggest
        _frequency = 1.0; // fastest
        _sign = +1; // positive voltage
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setMaxAmplitude( double maxAmplitude ) {
        assert( maxAmplitude >=0 && maxAmplitude <= 1 );
        if ( maxAmplitude != _maxAmplitude ) {
            _maxAmplitude = maxAmplitude;
            
            // Make sure the amplitude stays in range.
            double amplitude = getAmplitude();
            double clamplitude = MathUtil.clamp( -maxAmplitude, amplitude, maxAmplitude );
            setAmplitude( clamplitude );
            
            notifyObservers();
        }
    }
    
    public double getMaxAmplitude() {
        return _maxAmplitude;
    }
    
    public void setFrequency( double frequency ) {
        assert( frequency >= 0 && frequency <= 1 );
        if ( frequency != _frequency ) {
            _frequency = frequency;
            notifyObservers();
        }
    }
    
    public double getFrequency() {
        return _frequency;
    }
    
//    public double getVoltage() {
//        return _voltage;
//    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {
        if ( isEnabled() ) {

            double amplitude = 0.0;
            if ( _maxAmplitude != 0.0 ) {
                double delta = Math.abs( _frequency * _maxAmplitude / MIN_STEPS_PER_CYCLE );
                amplitude = getAmplitude() + ( _sign * delta );
                if ( amplitude > _maxAmplitude ) {
                    _sign = -1;
                    amplitude = _maxAmplitude - delta;
                }
                else if ( amplitude < -_maxAmplitude ) {
                    _sign = +1;
                    amplitude = _maxAmplitude - delta;
                }
            }
            setAmplitude( amplitude );
                
//                double max = Math.abs( amplitude * getMaxVoltage() );
//                double delta = Math.abs( _frequency * ( max / MIN_STEPS_PER_CYCLE ) );
//                double voltage = _voltage + ( _sign * delta );
//                if ( voltage > max ) {
//                    _sign = -1;
//                    voltage = max - delta;
//                }
//                else if ( voltage < -max ) {
//                    _sign = +1;
//                    voltage = -max + delta;
//                }
////                _voltage = voltage;
//            }
//            notifyObservers();

        }
    } 
}
