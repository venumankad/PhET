package edu.colorado.phet.functionalscenegraph;

import fj.data.Option;
import lombok.Data;

import java.awt.Cursor;
import java.awt.Paint;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public abstract @Data class SNode {
    abstract public void render( final DrawableGraphicsContext context );

    abstract public ImmutableRectangle2D getBounds( GraphicsContext context );

    public SNode translate( final double dx, final double dy ) {
        return new WithTransform( AffineTransform.getTranslateInstance( dx, dy ), this );
    }

    public ImmutableRectangle2D getBounds() { return getBounds( createMockState() );}

    public SNode scale( final int s ) { return new WithTransform( AffineTransform.getScaleInstance( s, s ), this ); }

    public SNode withPaint( final Paint paint ) { return new WithPaint( paint, this );}

    public Option<? extends SNode> pick( final Vector2D vector2D ) { return pick( vector2D, createMockState() ); }

    private MockState createMockState() {return new MockState( SCanvas.DEFAULT_FONT );}

    protected abstract Option<? extends SNode> pick( final Vector2D vector2D, final MockState mockState );

    //TODO: move to property effect
    public Cursor getCursor() {
        return Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );
    }
}