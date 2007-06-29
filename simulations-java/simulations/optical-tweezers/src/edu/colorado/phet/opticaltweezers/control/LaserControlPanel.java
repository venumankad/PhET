/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * LaserControlPanel is the panel used to control laser properties. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LaserControlPanel extends PhetPNode implements Observer {

    private class DragHandler extends BoundedDragHandler {

        public DragHandler( PNode dragNode, PNode boundingNode ) {
            super( dragNode, boundingNode );
        }
        
        public void mouseDragged( PInputEvent event ) {
            PNode pickedNode = event.getPickedNode();
            if ( pickedNode != _startStopButtonWrapper && pickedNode != _powerControlWrapper ) {
                super.mouseDragged( event );
            }
        }
    }
    
    public void initDragHandler( PNode dragNode, PNode boundingNode ) {
        addInputEventListener( new DragHandler( dragNode, boundingNode ) );
    }
    
    public void initCursors( Cursor cursor ) {
        _backgroundNode.addInputEventListener( new CursorHandler( cursor ) );
        _signNode.addInputEventListener( new CursorHandler( cursor ) );
    }
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int X_MARGIN = 10;
    private static final int Y_MARGIN = 10;
    private static final int X_SPACING = 20; // horizontal spacing between components, in pixels
    
    private static final Stroke PANEL_STROKE = new BasicStroke( 1f );
    private static final Color PANEL_STROKE_COLOR = Color.BLACK;
    private static final Color PANEL_FILL_COLOR = Color.DARK_GRAY;
    
    private static final Dimension POWER_CONTROL_SLIDER_SIZE = new Dimension( 150, 25 );
    private static final String POWER_CONTROL_PATTERN = "0";
    private static final int POWER_CONTROL_COLUMNS = 4;
        
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Laser _laser;
    
    private JButton _startStopButton;
    private LaserPowerControl _powerControl;
    private PNode _signNode;
    private PPath _backgroundNode;
    private ChangeListener _powerControlListener;
    
    private PSwing _startStopButtonWrapper;
    private PSwing _powerControlWrapper;
    
    private Icon _startIcon, _stopIcon;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param font
     * @param minPanelWidth
     * @param laser
     * @param powerRange
     */
    public LaserControlPanel( Laser laser, Font font, double minPanelWidth ) {
        super();
        
        _laser = laser;
        _laser.addObserver( this );
        
        // Warning sign
        _signNode = new PImage( OTResources.getImage( OTConstants.IMAGE_LASER_SIGN  ) );
        
        // Start/Stop button
        _startIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_OFF_BUTTON  ) );
        _stopIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_ON_BUTTON  ) );
        _startStopButton = new JButton( _laser.isRunning() ? _stopIcon : _startIcon );
        _startStopButton.setOpaque( false );
        _startStopButton.setMargin( new Insets( 0, 0, 0, 0 ) );
        _startStopButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _laser.setRunning( !_laser.isRunning() );
                _startStopButton.setIcon( _laser.isRunning() ? _stopIcon : _startIcon );
            }
        } );
        _startStopButtonWrapper = new PSwing( _startStopButton );
        
        // Power control
        DoubleRange powerRange = _laser.getPowerRange();
        String label = OTResources.getString( "label.power" );
        String units = OTResources.getString( "units.power" );
        double wavelength = laser.getVisibleWavelength();
        _powerControl = new LaserPowerControl( powerRange, label, units, POWER_CONTROL_PATTERN, POWER_CONTROL_COLUMNS, wavelength, POWER_CONTROL_SLIDER_SIZE, font );
        _powerControl.setLabelForeground( Color.WHITE );
        _powerControl.setUnitsForeground( Color.WHITE );
        _powerControlListener = new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                double power = _powerControl.getPower();
                _laser.setPower( power );
            }
        };
        _powerControl.addChangeListener( _powerControlListener );
        _powerControlWrapper = new PSwing( _powerControl );
        
        // Panel background
        double xMargin = X_MARGIN;
        double panelWidth = X_MARGIN + _signNode.getWidth() + X_SPACING + _startStopButtonWrapper.getFullBounds().getWidth() + 
            X_SPACING + _powerControlWrapper.getFullBounds().getWidth() + X_MARGIN;
        if ( panelWidth < minPanelWidth ) {
            xMargin = ( minPanelWidth - panelWidth ) / 2;
            panelWidth = minPanelWidth;
        }
        double panelHeight = Y_MARGIN + 
            Math.max( Math.max( _signNode.getHeight(), _startStopButtonWrapper.getFullBounds().getHeight() ), _powerControlWrapper.getFullBounds().getHeight() ) +
            Y_MARGIN;
        _backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, panelWidth, panelHeight ) );
        _backgroundNode.setStroke( PANEL_STROKE );
        _backgroundNode.setStrokePaint( PANEL_STROKE_COLOR );
        _backgroundNode.setPaint( PANEL_FILL_COLOR );
        
        // Layering
        addChild( _backgroundNode );
        addChild( _signNode );
        addChild( _startStopButtonWrapper );
        addChild( _powerControlWrapper );
        
        // Hand cursor on Swing controls
        _startStopButtonWrapper.addInputEventListener( new CursorHandler() );
        
        // Positioning, all components vertically centered
        final double bgHeight = _backgroundNode.getFullBounds().getHeight();
        double x = 0;
        double y = 0;
        _backgroundNode.setOffset( x, y );
        x += xMargin;
        y = ( bgHeight - _startStopButtonWrapper.getHeight() ) / 2;
        _startStopButtonWrapper.setOffset( x, y );
        x += _startStopButtonWrapper.getWidth() + X_SPACING;
        y = ( bgHeight - _powerControlWrapper.getFullBounds().getHeight() ) / 2;
        _powerControlWrapper.setOffset( x, y );
        x += _powerControlWrapper.getFullBounds().getWidth() + X_SPACING;
        y = ( bgHeight - _signNode.getFullBounds().getHeight() ) / 2;
        _signNode.setOffset( x, y );
    }
    
    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public double getMinPower() {
        return _powerControl.getMinPower();
    }
    
    public double getMaxPower() {
        return _powerControl.getMaxPower();
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_RUNNING ) {
                _startStopButton.setIcon( _laser.isRunning() ? _stopIcon : _startIcon );
            }
            else if ( arg == Laser.PROPERTY_POWER ) {
                double power = _laser.getPower();
                _powerControl.removeChangeListener( _powerControlListener );
                _powerControl.setPower( (int) power );
                _powerControl.addChangeListener( _powerControlListener );
            }
        }
    }
}
