package edu.colorado.phet.fractionsintro.buildafraction_functional.model;

import fj.F;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.buildafraction_functional.controller.ModelUpdate;
import edu.colorado.phet.fractionsintro.common.util.DefaultP2;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;

/**
 * Immutable model state for build a fraction.
 *
 * @author Sam Reid
 */
public @Data class BuildAFractionState {

    //The empty container that the user can drag around
    public final List<Container> containers;

    //The pieces that go in the containers
    public final List<Piece> pieces;

    //The numbers that drag into the fraction numerator/denominator
    public final List<DraggableNumber> draggableNumbers;

    //The fractions that numbers get dragged into as numerator/denominator
    public final List<DraggableFraction> draggableFractions;

    public final Mode mode;

    public final double time;//Consider making this extrinsic

    //Target cells where the filled-in fractions can be deposited
    public final List<TargetCell> targetCells;

    public static final ModelUpdate RELEASE_ALL = new ModelUpdate() {
        public BuildAFractionState update( final BuildAFractionState state ) {
            return state.releaseAll();
        }
    };

    //Use a getter instead of inheritance to make it easy to match multiple different types
    public static <T> F<T, Boolean> matchID( final ObjectID id, final F<T, ObjectID> getID ) {
        return new F<T, Boolean>() {
            @Override public Boolean f( final T t ) {
                return getID.f( t ).equals( id );
            }
        };
    }

    public BuildAFractionState withContainers( List<Container> containers ) { return new BuildAFractionState( containers, pieces, draggableNumbers, draggableFractions, mode, time, targetCells ); }

    public BuildAFractionState withDraggableNumbers( List<DraggableNumber> numbers ) { return new BuildAFractionState( containers, pieces, numbers, draggableFractions, mode, time, targetCells );}

    public BuildAFractionState withDraggableFractions( List<DraggableFraction> draggableFractions ) { return new BuildAFractionState( containers, pieces, draggableNumbers, draggableFractions, mode, time, targetCells );}

    public BuildAFractionState withMode( final Mode mode ) { return new BuildAFractionState( containers, pieces, draggableNumbers, draggableFractions, mode, time, targetCells ); }

    public BuildAFractionState withTime( final double time ) { return new BuildAFractionState( containers, pieces, draggableNumbers, draggableFractions, mode, time, targetCells ); }

    public BuildAFractionState addEmptyContainer( final int numSegments, final Vector2D location ) { return addContainer( new Container( ContainerID.nextID(), new DraggableObject( location, true ), numSegments ) ); }

    public BuildAFractionState addContainer( Container container ) { return withContainers( containers.cons( container ) ); }

    public BuildAFractionState withTargetCells( final List<TargetCell> targetCells ) { return new BuildAFractionState( containers, pieces, draggableNumbers, draggableFractions, mode, time, targetCells ); }

    public BuildAFractionState dragContainers( final Vector2D delta ) {
        return withContainers( containers.map( new F<Container, Container>() {
            @Override public Container f( final Container container ) {
                return container.isDragging() ? container.translate( delta ) : container;
            }
        } ) );
    }

    public BuildAFractionState releaseAll() {
        return withContainers( containers.map( new F<Container, Container>() {
            @Override public Container f( final Container container ) {
                return container.withDragging( false );
            }
        } ) ).
                withDraggableNumbers( draggableNumbers.map( new F<DraggableNumber, DraggableNumber>() {
                    @Override public DraggableNumber f( final DraggableNumber f ) {
                        return f.withDragging( false );
                    }
                } ) ).
                withDraggableFractions( draggableFractions.map( new F<DraggableFraction, DraggableFraction>() {
                    @Override public DraggableFraction f( final DraggableFraction f ) {
                        return f.withDragging( false );
                    }
                } ) );
    }

    public BuildAFractionState releaseFraction( final FractionID id ) {
        return withDraggableFractions( draggableFractions.map( new F<DraggableFraction, DraggableFraction>() {
            @Override public DraggableFraction f( final DraggableFraction d ) {
                return d.equalsID( id ) ? d.withDragging( false ) : d;
            }
        } ) );
    }

    public Option<Container> getContainer( final ContainerID id ) { return containers.find( matchID( id, Container.ID ) ); }

    public BuildAFractionState startDraggingContainer( final ContainerID id ) {
        return withContainers( containers.map( new F<Container, Container>() {
            @Override public Container f( final Container container ) {
                return container.getID().equals( id ) ? container.withDragging( true ) : container;
            }
        } ) );
    }

    public BuildAFractionState startDraggingNumber( final DraggableNumberID id ) {
        return withDraggableNumbers( draggableNumbers.map( new F<DraggableNumber, DraggableNumber>() {
            @Override public DraggableNumber f( final DraggableNumber container ) {
                return container.getID().equals( id ) ? container.withDragging( true ) : container;
            }
        } ) );
    }

    public BuildAFractionState addNumber( final DraggableNumber n ) { return withDraggableNumbers( draggableNumbers.snoc( n ) ); }

    public Option<DraggableNumber> getDraggableNumber( final DraggableNumberID id ) { return draggableNumbers.find( matchID( id, DraggableNumber.ID ) ); }

    public BuildAFractionState dragNumbers( final Vector2D delta ) {
        return withDraggableNumbers( draggableNumbers.map( new F<DraggableNumber, DraggableNumber>() {
            @Override public DraggableNumber f( final DraggableNumber n ) {
                return n.isDragging() ? n.translate( delta ) : n;
            }
        } ) );
    }

    public BuildAFractionState addDraggableFraction( final DraggableFraction d ) { return withDraggableFractions( draggableFractions.snoc( d ) ); }

    public Option<DraggableFraction> getDraggableFraction( final FractionID id ) { return draggableFractions.find( matchID( id, DraggableFraction.ID ) ); }

    public BuildAFractionState dragFraction( final FractionID fractionID, final Vector2D delta ) {
        return withDraggableFractions( draggableFractions.map( new F<DraggableFraction, DraggableFraction>() {
            @Override public DraggableFraction f( final DraggableFraction f ) {
                return f.equalsID( fractionID ) ? f.translate( delta ) : f;
            }
        } ) ).withDraggableNumbers( draggableNumbers.map( new F<DraggableNumber, DraggableNumber>() {
            @Override public DraggableNumber f( final DraggableNumber d ) {
                return d.isAttachedTo( fractionID ) ? d.withPosition( getDraggableFraction( fractionID ).some().getPosition() ) :
                       d;
            }
        } ) );
    }

    public BuildAFractionState startDraggingFraction( final FractionID id ) {
        return withDraggableFractions( draggableFractions.map( new F<DraggableFraction, DraggableFraction>() {
            @Override public DraggableFraction f( final DraggableFraction f ) {
                return f.equalsID( id ) ? f.withDragging( true ) : f;
            }
        } ) );
    }

    public Option<Piece> getPiece( final PieceID id ) { return pieces.find( matchID( id, Piece.ID ) ); }

    //Remove the number from the model and signify that it is attached to the fraction.
    public BuildAFractionState attachNumberToFraction( final DraggableNumberID number, final FractionID fraction, final boolean numerator ) {
        return withDraggableFractions( draggableFractions.map( new F<DraggableFraction, DraggableFraction>() {
            @Override public DraggableFraction f( final DraggableFraction f ) {
                return f.getID().equals( fraction ) && numerator ? f.withNumerator( Option.some( new DefaultP2<DraggableNumberID, Double>( number, time ) ) ) :
                       f.getID().equals( fraction ) && !numerator ? f.withDenominator( Option.some( new DefaultP2<DraggableNumberID, Double>( number, time ) ) ) :
                       f;
            }
        } ) ).withDraggableNumbers( draggableNumbers.map( new F<DraggableNumber, DraggableNumber>() {
            @Override public DraggableNumber f( final DraggableNumber n ) {
                return n.getID().equals( number ) ? n.attachToFraction( fraction, numerator ) : n;
            }
        } ) );
    }

    public boolean isUserDraggingZero() {
        return draggableNumbers.exists( new F<DraggableNumber, Boolean>() {
            @Override public Boolean f( final DraggableNumber draggableNumber ) {
                return draggableNumber.isDragging() && draggableNumber.number == 0;
            }
        } );
    }

    //Takes a fraction that is part of this model and has both numerator and denominator and splits it up.
    public BuildAFractionState splitFraction( final FractionID id ) {
        //TODO: add error handling to check preconditions?
        final DraggableFraction fraction = getDraggableFraction( id ).some();
        final DraggableFraction newFraction = fraction.withNumerator( Option.<DefaultP2<DraggableNumberID, Double>>none() ).withDenominator( Option.<DefaultP2<DraggableNumberID, Double>>none() );
        final DraggableNumber numerator = getDraggableNumber( fraction.getNumerator().some()._1() ).some();
        final DraggableNumber denominator = getDraggableNumber( fraction.getDenominator().some()._1() ).some();
        return withDraggableFractions( draggableFractions.map( new F<DraggableFraction, DraggableFraction>() {
            @Override public DraggableFraction f( final DraggableFraction f ) {
                return f == fraction ? newFraction : f;
            }
        } ) ).withDraggableNumbers( draggableNumbers.map( new F<DraggableNumber, DraggableNumber>() {
            @Override public DraggableNumber f( final DraggableNumber n ) {
                return n == numerator ? numerator.detach() :
                       n == denominator ? denominator.detach() :
                       n;
            }
        } ) );
    }

    public boolean containsMatch( final int numerator, final int denominator ) {
        final double targetValue = ( (double) numerator ) / denominator;
        return draggableFractions.exists( new F<DraggableFraction, Boolean>() {
            @Override public Boolean f( final DraggableFraction f ) {
                Option<Double> value = evaluate( f );
                return value.isSome() && Math.abs( value.some() - targetValue ) < 1E-6;
            }
        } );
    }

    public List<Double> getMatchTimes( final int numerator, final int denominator ) {
        final double targetValue = ( (double) numerator ) / denominator;
        return draggableFractions.filter( new F<DraggableFraction, Boolean>() {
            @Override public Boolean f( final DraggableFraction f ) {
                Option<Double> value = evaluate( f );
                return value.isSome() && Math.abs( value.some() - targetValue ) < 1E-6;
            }
        } ).map( new F<DraggableFraction, Double>() {
            @Override public Double f( final DraggableFraction draggableFraction ) {
                return draggableFraction.getLastConnectionTime().orSome( 0.0 );
            }
        } );
    }

    private Option<Double> evaluate( final DraggableFraction f ) {
        if ( f.getNumerator().isSome() && f.getDenominator().isSome() ) {
            double numerator = getDraggableNumber( f.getNumerator().some()._1() ).some().number;
            double denominator = getDraggableNumber( f.getDenominator().some()._1() ).some().number;
            return Option.some( numerator / denominator );
        }
        return Option.none();
    }

    public BuildAFractionState stepInTime( final double dt ) { return withTime( time + dt ); }

    public Fraction getFractionValue( final FractionID id ) {
        DraggableFraction fraction = getDraggableFraction( id ).some();
        return new Fraction( getDraggableNumber( fraction.numerator.some()._1() ).some().number,
                             getDraggableNumber( fraction.denominator.some()._1() ).some().number );
    }

    public BuildAFractionState moveFractionToTargetCell( final FractionID id, final Vector2D position, final TargetCell targetCell ) {
        return withTargetCells( targetCells.map( new F<TargetCell, TargetCell>() {
            @Override public TargetCell f( final TargetCell t ) {
                return targetCell == t ? t.withFraction( id ) : t;
            }
        } ) ).
                withDraggableFractions( draggableFractions.snoc( DraggableFraction.createDefault() ) ).
                dragFraction( id, position.minus( getDraggableFraction( id ).some().draggableObject.position ) );
    }
}