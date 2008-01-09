/* Copyright 2007-2008, University of Colorado */ 

package edu.colorado.phet.glaciers.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.JPanel;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.control.ToolboxNode;
import edu.colorado.phet.glaciers.model.AbstractModel;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.IToolProducer.ToolProducerListener;
import edu.colorado.phet.glaciers.view.Viewport.ViewportListener;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;

/**
 * PlayArea
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlayArea extends JPanel {
    
    private static final double TOP_PANEL_HEIGHT = 75; // height of top panel will be constrained to this many pixels
    private static final double TOP_SCALE = 0.2;
    private static final double BOTTOM_SCALE = 1;
    private static final Color CANVAS_BACKGROUND = new Color( 180, 158, 134 ); // tan, should match the ground color in the valley image
    private static final float VIEWPORT_STROKE_WIDTH = 4;
    
    // Model
    private AbstractModel _model;
    private Viewport _birdsEyeViewport;
    private Viewport _zoomedViewport;
    
    // View
    private PhetPCanvas _birdsEyeCanvas, _zoomedCanvas;
    private PLayer _valleyLayer, _glacierLayer, _toolboxLayer, _toolsLayer, _viewportLayer;
    private ToolboxNode _toolboxNode;
    private PNode _penguinNode;
    private HashMap _toolsMap; // key=AbstractTool, value=AbstractToolNode

    public PlayArea( AbstractModel model ) {
        super();
        
        _model = model;
        
        _birdsEyeViewport = new Viewport(); // bounds will be set when top canvas is resized
        
        _zoomedViewport = new Viewport(); // bounds will be set when bottom canvas is resized
        _zoomedViewport.addListener( new ViewportListener() {
            public void boundsChanged() {
                handleViewportBoundsChanged();
            }
        });
        
        // top canvas shows "birds-eye" view, has a fixed height
        _birdsEyeCanvas = new PhetPCanvas();
        _birdsEyeCanvas.setBackground( CANVAS_BACKGROUND );
        _birdsEyeCanvas.getCamera().setViewScale( TOP_SCALE );
        JPanel topPanel = new JPanel( new BorderLayout() );
        topPanel.add( Box.createVerticalStrut( (int) TOP_PANEL_HEIGHT ), BorderLayout.WEST );
        topPanel.add( _birdsEyeCanvas, BorderLayout.CENTER );
        
        // bottom canvas shows "zoomed" view
        _zoomedCanvas = new PhetPCanvas();
        _zoomedCanvas.setBackground( CANVAS_BACKGROUND );
        _zoomedCanvas.getCamera().setViewScale( BOTTOM_SCALE );
        
        // Layout
        setLayout( new BorderLayout() );
        add( topPanel, BorderLayout.NORTH );
        add( _zoomedCanvas, BorderLayout.CENTER );
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                handlePlayAreaResized();
            }
        });
        
        // Layers, back to front
        _valleyLayer = new PLayer();
        _glacierLayer = new PLayer();
        _toolboxLayer = new PLayer();
        _toolsLayer = new PLayer();
        _viewportLayer = new PLayer();
        addToTopAndBottom( _valleyLayer );
        addToTopAndBottom( _glacierLayer );
        addToBottom( _toolboxLayer );
        addToTopAndBottom( _toolsLayer );
        addToTop( _viewportLayer );
        
        // viewport in the top canvas determines what is shown in the bottom canvas
        ViewportNode viewportNode = new ViewportNode( _zoomedViewport, VIEWPORT_STROKE_WIDTH );
        _viewportLayer.addChild( viewportNode );
        
        // Valley
        PNode valleyNode = new ValleyNode();
        _valleyLayer.addChild( valleyNode );
        
        // Toolbox
        _toolsMap = new HashMap();
        _toolboxNode = new ToolboxNode( _model );
        _toolboxLayer.addChild( _toolboxNode );
        _model.addListener( new ToolProducerListener() {
            
            public void toolAdded( AbstractTool tool ) {
                PNode node = ToolNodeFactory.createNode( tool );
                _toolsLayer.addChild( node );
                _toolsMap.put( tool, node );
            }

            public void toolRemoved( AbstractTool tool ) {
                AbstractToolNode toolNode = (AbstractToolNode)_toolsMap.get( tool );
                _toolsLayer.removeChild( toolNode );
                _toolsMap.remove( tool );
            }
        });
        
        // Penguin
        {
            _penguinNode = new PenguinNode( _birdsEyeViewport, _zoomedViewport );
            _viewportLayer.addChild( _penguinNode );
            _penguinNode.setOffset( 100, 0 );
        }
        
        // initialize
        handlePlayAreaResized();
        handleViewportBoundsChanged();
    }
    
    public void addToTop( PLayer layer ) {
        _birdsEyeCanvas.getCamera().addLayer( layer );
        _birdsEyeCanvas.getRoot().addChild( layer );
    }
    
    public void addToBottom( PLayer layer ) {
        _zoomedCanvas.getCamera().addLayer( layer );
        _zoomedCanvas.getRoot().addChild( layer );
    }
    
    public void addToTopAndBottom( PLayer layer ) {
        addToTop( layer );
        addToBottom( layer );
    }
    
    /*
     * When the viewport bounds change, translate the camera.
     */
    private void handleViewportBoundsChanged() {
        
        // translate the bottom canvas' camera
        Rectangle2D viewportBounds = _zoomedViewport.getBounds();
        double scale = _zoomedCanvas.getCamera().getViewScale();
        _zoomedCanvas.getCamera().setViewOffset( -viewportBounds.getX() * scale, -viewportBounds.getY() * scale );
        
        // move the toolbox
        updateToolboxPosition();
    }
    
    /*
     * When the play area is resized...
     */
    private void handlePlayAreaResized() {
        
        Dimension2D screenSize = _birdsEyeCanvas.getScreenSize();
        if ( screenSize.getWidth() <= 0 || screenSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( GlaciersConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "PlayArea.handleTopCanvasResized screenSize=" + screenSize );//XXX
        }
        
        // set the bounds of the birds-eye viewport, based on the screen size
        {
            double w = _birdsEyeCanvas.getScreenSize().getWidth() / _birdsEyeCanvas.getCamera().getViewScale();
            double h = _birdsEyeCanvas.getScreenSize().getHeight() / _birdsEyeCanvas.getCamera().getViewScale();
            _birdsEyeViewport.setBounds( new Rectangle2D.Double( 0, 0, w, h ) );
        }
        
        // set the bounds of the zoomed viewport, based on the size of the zoomed canvas
        {
            Rectangle2D canvasBounds = _zoomedCanvas.getBounds();
            double scale = _zoomedCanvas.getCamera().getViewScale();
            Rectangle2D viewportBounds = _zoomedViewport.getBounds();
            double x = viewportBounds.getX();
            double y = viewportBounds.getY();
            double w = canvasBounds.getWidth() / scale;
            double h = canvasBounds.getHeight() / scale;
            _zoomedViewport.setBounds( new Rectangle2D.Double( x, y, w, h ) );
        }
        
        // keep the viewport inside the birds-eye view's bounds
        Rectangle2D wb = _birdsEyeViewport.getBoundsReference();
        Rectangle2D vb = _zoomedViewport.getBoundsReference();
        if ( !wb.contains( vb ) ) {
            double dx = wb.getMaxX() - vb.getMaxX(); // viewport only moves horizontally
            _zoomedViewport.translate( dx, 0 );
        }
        
        // move the toolbox
        updateToolboxPosition();
    }
    
    /*
     * Moves the toolbox to lower-left corner of the bottom canvas
     */
    private void updateToolboxPosition() {
        Rectangle2D viewportBounds = _zoomedViewport.getBounds();
        double xOffset = viewportBounds.getX() + 5;
        double yOffset = viewportBounds.getY() + viewportBounds.getHeight() - _toolboxNode.getFullBoundsReference().getHeight() - 5;
        _toolboxNode.setOffset( xOffset, yOffset );
    }
}
