package edu.colorado.phet.molecularreactions.view.charts;

import org.jfree.data.Range;

/**
 * Created by IntelliJ IDEA.
 * User: jdegoes
 * Date: Apr 4, 2007
 * Time: 8:41:50 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ResizableChart {
    void setYRange( int minY, int maxY );

    Range getYRange();
}
