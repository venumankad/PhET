// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.data.Option;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PTransformActivity;

/**
 * Base class for something that is stackable (such as number cards or shape pieces).
 *
 * @author Sam Reid
 */
public abstract class Stackable<T extends Stackable> extends PNode {
    private Option<Integer> positionInStack = Option.none();
    protected Stack<T> stack;

    public void setAllPickable( boolean pickable ) {
        setPickable( pickable );
        setChildrenPickable( pickable );
    }

    public Option<Integer> getPositionInStack() {
        return positionInStack;
    }

    public void setPositionInStack( Option<Integer> positionInStack ) {
        this.positionInStack = positionInStack;
    }

    public PTransformActivity animateTo( Vector2D v ) {
        return animateToPositionScaleRotation( v.x, v.y, getAnimateToScale(), 0, 400 );
    }

    protected double getAnimateToScale() {
        return 1.0;
    }

    public boolean isAtStackIndex( Integer site ) {
        return positionInStack.isSome() && positionInStack.some().equals( site );
    }

    public void setStack( final Stack<T> stack ) {
        assert this.stack == null;
        addPropertyChangeListener( PNode.PROPERTY_TRANSFORM, new PropertyChangeListener() {
            public void propertyChange( final PropertyChangeEvent evt ) {
                stack.cardMoved();
            }
        } );
        this.stack = stack;
    }
}