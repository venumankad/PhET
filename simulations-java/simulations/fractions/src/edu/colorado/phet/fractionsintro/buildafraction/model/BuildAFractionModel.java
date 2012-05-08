package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import fj.F2;
import fj.data.List;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.buildafraction.controller.ModelUpdate;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for the "Build a Fraction" tab which contains a switchable immutable state.
 *
 * @author Sam Reid
 */
public class BuildAFractionModel implements Resettable {
    private final BuildAFractionState initialState = new BuildAFractionState( List.<Container>nil(), List.<Piece>nil(), List.<DraggableNumber>nil(), List.<DraggableFraction>nil(), Mode.NUMBERS );
    public final Property<BuildAFractionState> state = new Property<BuildAFractionState>( initialState );
    public final ConstantDtClock clock = new ConstantDtClock();

    public BuildAFractionModel() {
    }

    public void update( ModelUpdate update ) { state.set( update.update( state.get() ) ); }

    public void dragContainer( final PDimension delta ) { state.set( state.get().dragContainers( new Vector2D( delta ) ) ); }

    public void addObserver( ChangeObserver<BuildAFractionState> observer ) { state.addObserver( observer ); }

    public void removeObserver( ChangeObserver<BuildAFractionState> observer ) { state.removeObserver( observer ); }

    public void startDraggingContainer( final ContainerID container ) {
        update( new ModelUpdate() {
            @Override public BuildAFractionState update( final BuildAFractionState state ) {
                return state.startDraggingContainer( container );
            }
        } );
    }

    public SettableProperty<Mode> toProperty( final F<BuildAFractionState, Mode> getter, final F2<BuildAFractionState, Mode, BuildAFractionState> setter ) {
        final SettableProperty<Mode> settableProperty = new SettableProperty<Mode>( getter.f( state.get() ) ) {
            @Override public void set( final Mode value ) { state.set( setter.f( state.get(), value ) ); }

            @Override public Mode get() { return getter.f( state.get() ); }
        };
        state.addObserver( new SimpleObserver() {
            @Override public void update() {
                settableProperty.notifyIfChanged();
            }
        } );
        return settableProperty;
    }

    //TODO: should the argument type be DraggableNumber then extract the id from it?
    public void startDraggingNumber( final DraggableNumberID id ) {
        update( new ModelUpdate() {
            @Override public BuildAFractionState update( final BuildAFractionState state ) {
                return state.startDraggingNumber( id );
            }
        } );
    }

    public void startDraggingFraction( final FractionID id ) {
        update( new ModelUpdate() {
            @Override public BuildAFractionState update( final BuildAFractionState state ) {
                return state.startDraggingFraction( id );
            }
        } );
    }

    public void dragNumber( final PDimension delta ) {
        update( new ModelUpdate() {
            @Override public BuildAFractionState update( final BuildAFractionState state ) {
                return state.dragNumbers( new Vector2D( delta ) );
            }
        } );
    }

    public void dragFraction( final FractionID id, final PDimension delta ) {
        update( new ModelUpdate() {
            @Override public BuildAFractionState update( final BuildAFractionState state ) {
                return state.dragFraction( id, new Vector2D( delta ) );
            }
        } );
    }

    //TODO: improve typing for the ObjectID?  Could have DraggableNumberID, etc.
    public void attachNumberToFraction( final DraggableNumberID number, final FractionID fraction, final boolean numerator ) {
        update( new ModelUpdate() {
            @Override public BuildAFractionState update( final BuildAFractionState state ) {
                return state.attachNumberToFraction( number, fraction, numerator );
            }
        } );
    }

    @Override public void reset() { state.set( initialState ); }
}