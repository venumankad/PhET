/** Sam Reid*/
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * User: Sam Reid
 * Date: Sep 21, 2004
 * Time: 6:25:31 AM
 * Copyright (c) Sep 21, 2004 by Sam Reid
 */
public class Grid extends AbstractGrid {

    public Grid( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing, double crossesOtherAxisAt ) {
        super( chart, orientation, stroke, color, tickSpacing, crossesOtherAxisAt );
        chart.addListener( new Chart.Listener() {
            public void transformChanged( Chart chart ) {
                update();
            }
        } );
        update();
    }

    protected void update() {
        clear();
        int orientation = super.getOrientation();
        Chart chart = super.getChart();
        double[] gridLines = getGridlines();
//        System.out.println( "gridLines.length = " + gridLines.length );
        if( orientation == VERTICAL ) {

//            double[] gridLines = getGridLines( getCrossesOtherAxisAt(), chart.getRange().getMinX(), chart.getRange().getMaxX(), getSpacing() );
            for( int i = 0; i < gridLines.length; i++ ) {
                double gridLineX = gridLines[i];
                if( gridLineX >= chart.getRange().getMinX() && gridLineX <= chart.getRange().getMaxX() ) {//TODO this is a big bug.
//                    System.out.println( "gridLineX = " + gridLineX );
                    Point src = chart.transform( gridLineX, chart.getRange().getMinY() );
//                    System.out.println( "chart.getModelViewTransform() = " + chart.getModelViewTransform() );
//                    System.out.println( "src = " + src );
                    Point dst = chart.transform( gridLineX, chart.getRange().getMaxY() );
//                    System.out.println( "dst = " + dst );
                    Line2D.Double line = new Line2D.Double( src.x, src.y, dst.x, dst.y );
                    PhetShapeGraphic lineGraphic = new PhetShapeGraphic( chart.getComponent(), line, getStroke(), getColor() );
//                    System.out.println( "lineGraphic.getBounds() = " + lineGraphic.getBounds() );
                    addGraphic( lineGraphic );
                }
            }
        }
        else if( orientation == HORIZONTAL ) {
//            double[] gridLines = getGridLines( getCrossesOtherAxisAt(), chart.getRange().getMinY(), chart.getRange().getMaxY(), getSpacing() );
            for( int i = 0; i < gridLines.length; i++ ) {//TODO this is a big bug.
                double gridLineY = gridLines[i];
                if( gridLineY >= chart.getRange().getMinY() && gridLineY <= chart.getRange().getMaxY() ) {
                    Point src = chart.transform( chart.getRange().getMinX(), gridLineY );
                    Point dst = chart.transform( chart.getRange().getMaxX(), gridLineY );
                    Line2D.Double line = new Line2D.Double( src.x, src.y, dst.x, dst.y );
                    PhetShapeGraphic lineGraphic = new PhetShapeGraphic( chart.getComponent(), line, getStroke(), getColor() );
                    addGraphic( lineGraphic );
                }
            }
        }
    }

}
