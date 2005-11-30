/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;


/**
 * ChartNode is a Piccolo node that draws a JFreeChart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ChartNode extends PNode implements ChartChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JFreeChart _chart; // chart associated with the node
    private ChartRenderingInfo _info; // the chart's rendering info
    private boolean _renderInfoValid; // is chart's render info valid?
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param chart
     */
    public ChartNode( JFreeChart chart ) {
        super();
        _info = new ChartRenderingInfo();
        _chart = chart;
        _chart.addChangeListener( this );
        _renderInfoValid = false; // not valid until paint has been called
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the chart that is associated with this node.
     * 
     * @return JFreeChart
     */
    public JFreeChart getChart() {
        return _chart;
    }
    
    /**
     * Gets the chart's rendering info. This information is valid only 
     * after the node has been painted at least once.  If the node hasn't
     * been painted yet, this method returns null.
     * 
     * @return ChartRenderingInfo, possibly null
     */
    public ChartRenderingInfo getChartRenderingInfo() {
        if ( _renderInfoValid ) {
            return _info;
        }
        else {
            return null;
        }
    }
    
    //----------------------------------------------------------------------------
    // PNode overrides
    //----------------------------------------------------------------------------
    
    /*
     * Paints the node.
     * The node's bounds (stored in the node's local coordinate system) are used
     * to determine the size and location of the chart.
     * Painting the node also updates the chart's rendering info.
     */
    protected void paint( PPaintContext paintContext ) {
        Graphics2D g2 = paintContext.getGraphics();
        _chart.draw( g2, getBoundsReference(), _info );
        _renderInfoValid = true;
    }
    
    //----------------------------------------------------------------------------
    // ChartChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Repaints the node when the chart (or any of its components) changes.
     * 
     * @param event
     */
    public void chartChanged( ChartChangeEvent event ) {
        /* 
         * Do not look at event.getSource(), since the source of the event is
         * likely to be one of the chart's components rather than the chart itself.
         */
        repaint();
    }
}
