/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:41:14 PM
 * Copyright (c) Feb 5, 2006 by Sam Reid
 */

public class DGPlotPanel extends PSwingCanvas {
    private XYDataset dataset;
    private int width = 700;
    private int height = 300;
    private DGModule dgModule;
    private XYSeries series;
    private JFreeChartNode jFreeChartNode;
    private IndicatorGraphic indicatorGraphic;
    private JFreeChart chart;
    private DGIntensityReader intensityReader;

    public DGPlotPanel( DGModule dgModule ) {
        this.dgModule = dgModule;
        intensityReader = new EdgeIntensityReader( dgModule.getDGModel() );
        intensityReader = new RadialIntensityReader( dgModule.getDGModel() );
        series = new XYSeries( "series1" );
        dataset = new XYSeriesCollection( series );

        chart = ChartFactory.createScatterPlot( "Intensity Plot", "Angle (degrees)", "Intensity (units)", dataset, PlotOrientation.VERTICAL, false, false, false );
//        chart.getXYPlot().setDomainGridlinesVisible( false );
//        chart.getXYPlot().setRangeGridlinesVisible( false );
        chart.getXYPlot().getDomainAxis().setRange( 0, 90 );
        chart.getXYPlot().getRangeAxis().setRange( 0, 0.1 );
        jFreeChartNode = new JFreeChartNode( chart );
        jFreeChartNode.setBounds( 0, 0, width, height );
        setPreferredSize( new Dimension( width, height ) );
        getLayer().addChild( jFreeChartNode );
        setPanEventHandler( null );
        setZoomEventHandler( null );
        new Timer( 100, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                replotAll();
            }
        } ).start();
        indicatorGraphic = new IndicatorGraphic();
        getLayer().addChild( indicatorGraphic );
    }

    public void setIndicatorVisible( boolean visible ) {
        indicatorGraphic.setVisible( visible );
    }

    public void setIndicatorAngle( double angle ) {
        Point2D pt = jFreeChartNode.plotToNode( new Point2D.Double( angle, chart.getXYPlot().getRangeAxis().getUpperBound() ) );
        Point2D bottom = jFreeChartNode.plotToNode( new Point2D.Double( angle, chart.getXYPlot().getRangeAxis().getLowerBound() ) );
        indicatorGraphic.setOffset( bottom );
        indicatorGraphic.setIndicatorHeight( pt.getY() - bottom.getY() );
//        jFreeChartNode.setVisible( false );
        paintImmediately( 0, 0, getWidth(), getHeight() );
        repaint( 0, 0, getWidth(), getHeight() );
//        repaint();
//        indicatorGraphic.repaint();
    }

    class IndicatorGraphic extends PhetPNode {
        private PPath path;

        public IndicatorGraphic() {
            path = new PPath();
            setIndicatorHeight( 0 );
            addChild( path );
        }

        public void setIndicatorHeight( double height ) {
            path.setPathTo( new Line2D.Double( 0, 0, 0, height ) );
        }
    }

    public void replotAll() {
        series.clear();
        double dAngle = 1;
        for( double angle = 0; angle <= 90; angle += dAngle ) {
            double intensity = getIntensity( angle );
            series.add( angle, intensity );
        }
        jFreeChartNode.repaint();
    }

    protected Wavefunction getWavefunction() {
        return dgModule.getDiscreteModel().getWavefunction();
    }

    private double getIntensity( double angle ) {
        return intensityReader.getIntensity( angle );
    }

}
