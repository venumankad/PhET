package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.forces1d.model.Block;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 22, 2004
 * Time: 8:34:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class OffscreenPointerGraphic extends CompositePhetGraphic {
    private static final Font font = new Font( "Lucida Sans", Font.BOLD, 28 );
    private DecimalFormat decimalFormat = new DecimalFormat( "#0.0" );
    private PhetTextGraphic textGraphic;
    private PhetShapeGraphic shapeGraphic;
    private BlockGraphic blockGraphic;
    private WalkwayGraphic container;
    private int y = 50;
    private int maxArrowLength = 150;

    public OffscreenPointerGraphic( final Force1DPanel component, final BlockGraphic blockGraphic, final WalkwayGraphic container ) {
        super( component );
        this.blockGraphic = blockGraphic;
        this.container = container;

//        textGraphic = new PhetShadowTextGraphic( component, "", font, 0, 0, Color.blue, 2, 2, Color.white );
        textGraphic = new PhetTextGraphic( component, font, "", Color.blue, 0, 0 );
        Stroke stroke = new BasicStroke( 1.0f );
        shapeGraphic = new PhetShapeGraphic( component, null, Color.yellow, stroke, Color.black );
        addGraphic( textGraphic );
        addGraphic( shapeGraphic );


//        final PhetShapeGraphic debugShapeGraphic=new PhetShapeGraphic( component,null,new BasicStroke( 3.0f),Color.green );
//        component.addGraphic( debugShapeGraphic,Double.POSITIVE_INFINITY );
//        final PhetShapeGraphic containerDebugGraphic=new PhetShapeGraphic( component,null,new BasicStroke( 3.0f),Color.yellow );
//        component.addGraphic( containerDebugGraphic,Double.POSITIVE_INFINITY );
        blockGraphic.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                boolean neg = blockGraphic.getX() <= 0;
                boolean pos = blockGraphic.getX() >= component.getWidth();
                int insetX = 30;
                if( neg || pos ) {
                    Block block = blockGraphic.getBlock();
                    double x = block.getPosition();
                    String locStr = decimalFormat.format( x );
                    textGraphic.setText( locStr + " meters" );
//                    setBoundsDirty();

                    int yRel = 10;
                    Point2D.Double source = new Point2D.Double( 0, yRel );
                    Point2D.Double dst = new Point2D.Double( x, yRel );
                    AbstractVector2D arrowVector = new Vector2D.Double( source, dst );
                    if( arrowVector.getMagnitude() > maxArrowLength ) {
                        arrowVector = arrowVector.getInstanceOfMagnitude( maxArrowLength );
                    }
                    Arrow arrow = new Arrow( source, arrowVector.getDestination( source ), 20, 20, 10, 0.2, true );
                    Shape shape = arrow.getShape();
                    shapeGraphic.setShape( shape );
                    setBoundsDirty();
                    Rectangle bounds = getBounds();

//                    debugShapeGraphic.setShape( bounds );
//                    containerDebugGraphic.setShape( container.getBounds() );
                    if( pos ) {
                        setLocation( container.getX() + container.getWidth() - bounds.width - insetX, y );
                    }
                    else {
                        setLocation( container.getX() + insetX, y );
                    }
                    setVisible( true );
                }
                else {
                    setVisible( false );
                }
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
            }
        } );
    }
}
