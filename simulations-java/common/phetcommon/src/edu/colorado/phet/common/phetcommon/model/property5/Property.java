// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5;

//REVIEW this class has dependencies on property3

import edu.colorado.phet.common.phetcommon.model.property3.ChangeEvent;
import edu.colorado.phet.common.phetcommon.model.property3.ChangeObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Property<T> can be used to represent a value of type T in a MVC style pattern.  It remembers its default value and can be reset.
 * The wrapped type T should be immutable, or at least protected from external modification.
 * Notifications are sent to observers when they register with addObserver, and when the value changes.
 * Listeners can alternative register for callbacks that provide the new value with addObserver(VoidFunction1<T>).
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class Property<T> extends SettableProperty<T> {
    private T value;
    private final T initialValue;

    public Property( T value ) {
        super( value );
        this.initialValue = value;
        this.value = value;
    }

    public void reset() {
        setValue( initialValue );
    }

    @Override
    public T getValue() {
        return value;
    }

    public void setValue( T value ) {
        if ( !this.value.equals( value ) ) {
            T oldValue = this.value;
            this.value = value;
            notifyObservers( value, oldValue );
        }
    }

    /**
     * Getter for the initial value, protected to keep the API for Property as simple as possible for typical usage, but
     * possible to override for applications which require knowledge about the initial value such as Gravity and Orbits.
     *
     * @return the initial value
     */
    protected T getInitialValue() {
        return initialValue;
    }

    // test
    public static void main( String[] args ) {
        final Property<Boolean> enabled = new Property<Boolean>( true );
        enabled.addObserver( new SimpleObserver() {
            public void update() {
                System.out.println( "SimpleObserver enabled=" + enabled.getValue() );
            }
        } );
        enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean newValue ) {
                System.out.println( "VoidFunction1 newValue=" + newValue );
            }
        } );
        enabled.addObserver( new ChangeObserver<Boolean>() {
            public void update( ChangeEvent<Boolean> e ) {
                System.out.println( "VoidFunction1 newValue=" + e.value + " oldValue=" + e.oldValue );
            }
        } );
        enabled.setValue( !enabled.getValue() );
    }

    public ObservableProperty<Boolean> valueEquals( T salt ) {
        return new ValueEquals<T>( this, salt );
    }
}