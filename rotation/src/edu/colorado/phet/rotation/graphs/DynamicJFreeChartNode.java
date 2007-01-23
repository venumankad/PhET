package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PClip;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * This class extends the functionality of JFreeChartNode by providing different strategies for rendering the data.
 * It is assumed that the chart's plot is XYPlot, and some functionality is lost in rendering, since we
 * have our own rendering strategies here.
 * <p/>
 * Data is added to the chart through addValue() methods, not through the underlying XYSeriesCollection dataset.
 */
public class DynamicJFreeChartNode extends JFreeChartNode {
    private ArrayList seriesDataList = new ArrayList();
    private ArrayList seriesViewList = new ArrayList();
    private ArrayList listeners = new ArrayList();
    private PhetPCanvas phetPCanvas;
    private PhetPPath debugBufferRegion;

    private SeriesViewFactory jfreeChartSeriesFactory = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new JFreeChartSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };
    private SeriesViewFactory piccoloSeriesFactory = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new PiccoloSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };
    private SeriesViewFactory bufferedSeriesFactory = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new BufferedSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };
    private SeriesViewFactory viewFactory = jfreeChartSeriesFactory;

    public DynamicJFreeChartNode( PhetPCanvas phetPCanvas, JFreeChart chart ) {
        super( chart );
        this.phetPCanvas = phetPCanvas;
        debugBufferRegion = new PhetPPath( new BasicStroke( 1.0f ), Color.green );
        addChild( debugBufferRegion );
    }

    public void addValue( double x, double y ) {
        addValue( 0, x, y );
    }

    private void addValue( int series, double x, double y ) {
        getSeries( series ).addValue( x, y );
    }

    private SeriesData getSeries( int series ) {
        return (SeriesData)seriesDataList.get( series );
    }

    public void addSeries( String title, Color color ) {
        SeriesData seriesData = new SeriesData( title, color );
        seriesDataList.add( seriesData );
        updateSeriesViews();
    }

    public void clear() {
        for( int i = 0; i < seriesDataList.size(); i++ ) {
            SeriesData seriesData = (SeriesData)seriesDataList.get( i );
            seriesData.clear();
        }
    }

    public static interface Listener {
        void dataAreaChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.dataAreaChanged();
        }
    }

    private void repaintPanel( Rectangle2D bounds ) {
        phetPCanvas.repaint( new PBounds( bounds ) );
        debugBufferRegion.setPathTo( bounds );
    }

    static interface SeriesViewFactory {
        SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData );
    }

    public void setJFreeChartSeries() {
        viewFactory = jfreeChartSeriesFactory;
        updateSeriesViews();
    }

    public void setPiccoloSeries() {
        viewFactory = piccoloSeriesFactory;
        updateSeriesViews();
    }

    public void setBufferedSeries() {
        viewFactory = bufferedSeriesFactory;
        updateSeriesViews();
    }

    private void updateSeriesViews() {
        removeAllSeriesViews();
        clearBuffer();
        addAllSeriesViews();
        updateChartRenderingInfo();
    }

    private void addAllSeriesViews() {
        for( int i = 0; i < seriesDataList.size(); i++ ) {
            SeriesData seriesData = (SeriesData)seriesDataList.get( i );
            SeriesView seriesDataView = viewFactory.createSeriesView( this, seriesData );
            seriesDataView.install();
            seriesViewList.add( seriesDataView );
        }
    }

    private void removeAllSeriesViews() {
        while( seriesViewList.size() > 0 ) {
            SeriesView seriesView = (SeriesView)seriesViewList.get( 0 );
            seriesView.uninstall();
            seriesViewList.remove( seriesView );
        }
    }

    static abstract class SeriesView {
        DynamicJFreeChartNode dynamicJFreeChartNode;
        SeriesData seriesData;
        private SeriesData.Listener listener = new SeriesData.Listener() {
            public void dataAdded() {
                SeriesView.this.dataAdded();
            }
        };

        public SeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            this.dynamicJFreeChartNode = dynamicJFreeChartNode;
            this.seriesData = seriesData;
        }

        public abstract void dataAdded();

        public void uninstall() {
            seriesData.removeListener( listener );
        }

        public void install() {
            seriesData.addListener( listener );
        }

        public DynamicJFreeChartNode getDynamicJFreeChartNode() {
            return dynamicJFreeChartNode;
        }

        public XYSeries getSeries() {
            return seriesData.getSeries();
        }

        public SeriesData getSeriesData() {
            return seriesData;
        }
    }

    static class JFreeChartSeriesView extends SeriesView {

        public JFreeChartSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            super( dynamicJFreeChartNode, seriesData );
        }

        public void dataAdded() {
            //painting happens automatically due to changes in the JFreeChart
        }

        public void uninstall() {
            super.uninstall();
            XYSeriesCollection xySeriesCollection = (XYSeriesCollection)dynamicJFreeChartNode.getChart().getXYPlot().getDataset();
            xySeriesCollection.removeSeries( seriesData.getSeries() );
        }

        public void install() {
            super.install();
            XYSeriesCollection xySeriesCollection = (XYSeriesCollection)dynamicJFreeChartNode.getChart().getXYPlot().getDataset();
            xySeriesCollection.addSeries( seriesData.getSeries() );
        }
    }

    static class PiccoloSeriesView extends SeriesView {

        private PNode root = new PNode();
        private PhetPPath pathNode;
        private PClip pathClip;
        private DynamicJFreeChartNode.Listener listener = new Listener() {
            public void dataAreaChanged() {
                updateClip();
            }
        };

        public PiccoloSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            super( dynamicJFreeChartNode, seriesData );

            pathClip = new PClip();
            pathClip.setStrokePaint( null );//set to non-null for debugging clip area
//            pathClip.setStrokePaint( Color.blue );//set to non-null for debugging clip area
            root.addChild( pathClip );

            pathNode = new PhetPPath( new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f ), seriesData.getColor() );
            pathClip.addChild( pathNode );

            updateClip();
        }

        private void updateClip() {
            pathClip.setPathTo( dynamicJFreeChartNode.getDataArea() );
        }

        public void updateSeriesGraphic() {
            GeneralPath path = new GeneralPath();
            if( super.getSeries().getItemCount() > 0 ) {
                Point2D d = getNodePoint( 0 );
                path.moveTo( (float)d.getX(), (float)d.getY() );
                for( int i = 1; i < getSeries().getItemCount(); i++ ) {
                    Point2D nodePoint = getNodePoint( i );
                    path.lineTo( (float)nodePoint.getX(), (float)nodePoint.getY() );
                }
            }
            pathNode.setPathTo( path );
            if( dynamicJFreeChartNode.isBuffered() ) {
//                pathClip.setOffset( dynamicJFreeChartNode.getBounds().getX(), dynamicJFreeChartNode.getBounds().getY() );
                pathNode.setOffset( dynamicJFreeChartNode.getBounds().getX(), dynamicJFreeChartNode.getBounds().getY() );
            }
            else {
//                pathClip.setOffset( 0, 0 );
                pathNode.setOffset( 0, 0 );
            }
        }

        public Point2D.Double getPoint( int i ) {
            return new Point2D.Double( getSeries().getX( i ).doubleValue(), getSeries().getY( i ).doubleValue() );
        }

        public Point2D getNodePoint( int i ) {
            return getDynamicJFreeChartNode().plotToNode( getPoint( i ) );
        }

        public void setClip( Rectangle2D clip ) {
            pathClip.setPathTo( clip );
        }

        public void uninstall() {
            super.uninstall();
            super.getDynamicJFreeChartNode().removeChild( root );
            dynamicJFreeChartNode.removeListener( listener );
        }

        public void install() {
            super.install();
            getDynamicJFreeChartNode().addChild( root );
            dynamicJFreeChartNode.addListener( listener );
            updateClip();
            updateSeriesGraphic();
        }

        public void dataAdded() {
            updateSeriesGraphic();
        }

    }

    private void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    //todo:  move this to parent class
    public void updateChartRenderingInfo() {
        super.updateChartRenderingInfo();
        if( listeners != null ) {
            notifyListeners();
        }
    }

    static class BufferedSeriesView extends SeriesView {

        public BufferedSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            super( dynamicJFreeChartNode, seriesData );
        }

        public void dataAdded() {
            if( getSeries().getItemCount() >= 2 ) {
                BufferedImage image = dynamicJFreeChartNode.getBuffer();
                if( image != null ) {
                    Graphics2D graphics2D = image.createGraphics();
                    graphics2D.setPaint( getSeriesData().getColor() );
                    BasicStroke stroke = new BasicStroke( 2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f );
                    graphics2D.setStroke( stroke );
                    int itemCount = getSeries().getItemCount();
                    Line2D.Double modelLine = new Line2D.Double( getSeries().getX( itemCount - 2 ).doubleValue(), getSeries().getY( itemCount - 2 ).doubleValue(), getSeries().getX( itemCount - 1 ).doubleValue(), getSeries().getY( itemCount - 1 ).doubleValue() );
                    Line2D.Double viewLine = new Line2D.Double( dynamicJFreeChartNode.plotToNode( modelLine.getP1() ), dynamicJFreeChartNode.plotToNode( modelLine.getP2() ) );
                    graphics2D.draw( viewLine );

                    Shape sh = stroke.createStrokedShape( viewLine );
                    Rectangle2D bounds = sh.getBounds2D();
                    if( dynamicJFreeChartNode.isBuffered() ) {
                        bounds = new Rectangle2D.Double( bounds.getX() + dynamicJFreeChartNode.getBounds().getX(), bounds.getY() + dynamicJFreeChartNode.getBounds().getY(), bounds.getWidth(), bounds.getHeight() );
                    }
                    dynamicJFreeChartNode.localToGlobal( bounds );
                    dynamicJFreeChartNode.phetPCanvas.getPhetRootNode().globalToScreen( bounds );
                    dynamicJFreeChartNode.repaintPanel( bounds );
                }
            }
        }

        public void uninstall() {
            super.uninstall();
        }

        public void install() {
            super.install();
        }
    }


    public static class SeriesData {
        private String title;
        private Color color;
        private XYSeries series;
        private ArrayList listeners = new ArrayList();
        private static int index = 0;

        public SeriesData( String title, Color color ) {
            this( title, color, new XYSeries( title + " " + ( index++ ) ) );
        }

        public SeriesData( String title, Color color, XYSeries series ) {
            this.title = title;
            this.color = color;
            this.series = series;
        }

        public String getTitle() {
            return title;
        }

        public Color getColor() {
            return color;
        }

        public XYSeries getSeries() {
            return series;
        }

        public void addValue( double time, double value ) {
            series.add( time, value );
            notifyDataChanged();
        }

        public void removeListener( Listener listener ) {
            listeners.remove( listener );
        }

        public void clear() {
            series.clear();
            notifyDataChanged();
        }

        public static interface Listener {
            void dataAdded();
        }

        public void addListener( Listener listener ) {
            listeners.add( listener );
        }

        public void notifyDataChanged() {
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.dataAdded();
            }
        }
    }

    //Todo: provide support for this in the parent class.
    public void clearBuffer() {
        super.chartChanged( null );
    }

    public void setBuffered( boolean buffered ) {
        super.setBuffered( buffered );
        updateChartRenderingInfo();
        updateSeriesViews();
        notifyListeners();
    }
}
