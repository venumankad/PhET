// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.nio.FloatBuffer;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.CanvasTransform;
import edu.colorado.phet.lwjglphet.CanvasTransform.StageCenteringCanvasTransform;
import edu.colorado.phet.lwjglphet.GLNode;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.GLOptions.RenderPass;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.LWJGLTab;
import edu.colorado.phet.lwjglphet.OrthoComponentNode;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.math.Ray3F;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.platetectonics.util.LWJGLModelViewTransform;

import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.framesPerSecondLimit;
import static org.lwjgl.opengl.GL11.*;

/**
 * General plate tectonics module that consolidates common behavior between the various tabs
 * <p/>
 * TODO: implement different cameras somehow?
 */
public abstract class PlateTectonicsTab extends LWJGLTab {
    public static final String MAP_LEFT = "CameraLeft";
    public static final String MAP_RIGHT = "CameraRight";
    public static final String MAP_UP = "CameraUp";
    public static final String MAP_DOWN = "CameraDown";
    public static final String MAP_LMB = "CameraDrag";

    // frustum properties
    public static final float fieldOfViewDegrees = 40;
    public static final float nearPlane = 1;
    public static final float farPlane = 5000;

    public final LWJGLTransform sceneProjectionTransform = new LWJGLTransform();

    private Dimension stageSize;

    public final VoidNotifier mouseEventNotifier = new VoidNotifier();
    public final VoidNotifier keyboardEventNotifier = new VoidNotifier();
    public final VoidNotifier beforeFrameRender = new VoidNotifier();

    private LWJGLTransform debugCameraTransform = new LWJGLTransform();
    protected CanvasTransform canvasTransform;
    private LWJGLModelViewTransform modelViewTransform;
    private long lastSeenTime;
    public final GLNode rootNode = new GLNode();
    private boolean showWireframe = false;

    // in seconds
    private float timeElapsed;

    // recorded amount
    public final Property<Double> framesPerSecond = new Property<Double>( 0.0 );
    private final LinkedList<Long> timeQueue = new LinkedList<Long>();

    private boolean initialized = false;

    public PlateTectonicsTab( LWJGLCanvas canvas, String title, float kilometerScale ) {
        super( canvas, title );

        // TODO: better initialization for this model view transform (for each module)
        modelViewTransform = new LWJGLModelViewTransform( ImmutableMatrix4F.scaling( kilometerScale / 1000 ) );
    }

    public void initialize() {
        stageSize = initialCanvasSize;
        canvasTransform = new StageCenteringCanvasTransform( canvasSize, stageSize );

        // show both sides
        glPolygonMode( GL_FRONT, GL_FILL );
        glPolygonMode( GL_BACK, GL_FILL );

        /*---------------------------------------------------------------------------*
        * debug camera controls
        *----------------------------------------------------------------------------*/
        mouseEventNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        if ( Keyboard.isKeyDown( Keyboard.KEY_Q ) ) {
                            int dx = Mouse.getEventDX();
                            if ( Mouse.isButtonDown( 0 ) ) {
                                int dy = Mouse.getEventDY();
                                debugCameraTransform.prepend( ImmutableMatrix4F.rotateY( (float) dx / 100 ) );
                                debugCameraTransform.prepend( ImmutableMatrix4F.rotateX( (float) -dy / 100 ) );
                            }
                            else if ( Mouse.isButtonDown( 1 ) ) {
                                debugCameraTransform.prepend( ImmutableMatrix4F.rotateZ( (float) dx / 100 ) );
                            }
                        }
                    }
                }, false );
        keyboardEventNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        if ( Keyboard.getEventKey() == Keyboard.KEY_F ) {
                            showWireframe = Keyboard.getEventKeyState();
                        }
                    }
                }, false );
        beforeFrameRender.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        if ( Keyboard.isKeyDown( Keyboard.KEY_W ) ) {
                            debugCameraTransform.prepend( ImmutableMatrix4F.translation( 0, 0, 1 ) );
                        }
                        if ( Keyboard.isKeyDown( Keyboard.KEY_S ) ) {
                            debugCameraTransform.prepend( ImmutableMatrix4F.translation( 0, 0, -1 ) );
                        }
                        if ( Keyboard.isKeyDown( Keyboard.KEY_A ) ) {
                            debugCameraTransform.prepend( ImmutableMatrix4F.translation( 1, 0, 0 ) );
                        }
                        if ( Keyboard.isKeyDown( Keyboard.KEY_D ) ) {
                            debugCameraTransform.prepend( ImmutableMatrix4F.translation( -1, 0, 0 ) );
                        }
                    }
                }, false );

        mouseEventNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        System.out.println( "x: " + Mouse.getEventX() + ", y: " + Mouse.getEventY() );

                        // LMB down
                        if ( Mouse.getEventButton() == 0 && Mouse.getEventButtonState() ) {
                            System.out.println( "click" );
                            System.out.println( getCameraRay( Mouse.getEventX(), Mouse.getEventY() ) );
                        }
                    }
                }, false );
    }

    @Override public void start() {
        if ( !initialized ) {
            initialize();
            initialized = true;
        }

        lastSeenTime = System.currentTimeMillis();
    }

    @Override public void loop() {
        // delay if we need to, limiting our FPS
        Display.sync( framesPerSecondLimit.get() );

        // calculate FPS
        int framesToCount = 10;
        long current = System.currentTimeMillis();
        timeQueue.add( current );
        if ( timeQueue.size() == framesToCount + 1 ) {
            long previous = timeQueue.poll();
            framesPerSecond.set( (double) ( 1000 * ( (float) framesToCount ) / ( (float) ( current - previous ) ) ) );
        }

        beforeFrameRender.updateListeners();

        // Clear the screen and depth buffer
        glClearColor( 0.85f, 0.95f, 1f, 1.0f );
        glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );

        // reset the modelview matrix
        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();

        // calculate time elapsed
        long newTime = System.currentTimeMillis();
        timeElapsed = Math.min(
                1000f / (float) framesPerSecondLimit.get(), // don't let our time elapsed go over the frame rate limit value
                (float) ( newTime - lastSeenTime ) / 1000f ); // take elapsed milliseconds => seconds

        // walk through all of the mouse events that occurred
        while ( Mouse.next() ) {
            mouseEventNotifier.updateListeners();
        }
        while ( Keyboard.next() ) {
            keyboardEventNotifier.updateListeners();
        }

        // TODO: improve area where the model is updated. Should happen after mouse events (here)

        GLOptions options = new GLOptions();

        if ( showWireframe ) {
            options.forWireframe = true;
            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        }
        else {
            glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
        }

        glViewport( 0, 0, getCanvasWidth(), getCanvasHeight() );
        setupGuiTransformations();

        options.renderPass = RenderPass.REGULAR;
        rootNode.render( options );
        options.renderPass = RenderPass.TRANSPARENCY;
        rootNode.render( options );

        Display.update();
    }

    @Override public void stop() {
    }

    public LWJGLModelViewTransform getModelViewTransform() {
        return modelViewTransform;
    }

    public CanvasTransform getCanvasTransform() {
        return canvasTransform;
    }

    public float getTimeElapsed() {
        return timeElapsed;
    }

    public Dimension getStageSize() {
        return stageSize;
    }

    public void loadCameraMatrices() {
        glMatrixMode( GL_PROJECTION );
        glLoadIdentity();
        sceneProjectionTransform.set( getSceneProjectionMatrix() );
        sceneProjectionTransform.apply();

        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();

        // TODO: refactor this into getSceneModelViewMatrix, and keep a transform as above. axe debugCameraTransform
        debugCameraTransform.apply();
        glRotatef( 13, 1, 0, 0 );
        glTranslatef( 0, -80, -400 );
    }

    public ImmutableMatrix4F getSceneProjectionMatrix() {
        AffineTransform affineCanvasTransform = canvasTransform.transform.get();

        // TODO: scale is still off. examine history here
        ImmutableMatrix4F scalingMatrix = ImmutableMatrix4F.scaling(
                (float) affineCanvasTransform.getScaleX(),
                (float) affineCanvasTransform.getScaleY(),
                1 );

        ImmutableMatrix4F perspectiveMatrix = getGluPerspective( fieldOfViewDegrees,
                                                                 (float) canvasSize.get().width / (float) canvasSize.get().height,
                                                                 nearPlane, farPlane );
        return scalingMatrix.times( perspectiveMatrix );
    }

    public static ImmutableMatrix4F getGluPerspective( float fovy, float aspect, float zNear, float zFar ) {
        float fovAngle = (float) ( fovy / 2 * Math.PI / 180 );
        float cotangent = (float) Math.cos( fovAngle ) / (float) Math.sin( fovAngle );

        return ImmutableMatrix4F.rowMajor( cotangent / aspect, 0, 0, 0,
                                           0, cotangent, 0, 0,
                                           0, 0, ( zFar + zNear ) / ( zNear - zFar ), ( 2 * zFar * zNear ) / ( zNear - zFar ),
                                           0, 0, -1, 0 );
    }

    // we don't want to create these for each function call.
    private FloatBuffer rayProjectionBuffer = BufferUtils.createFloatBuffer( 16 );
    private FloatBuffer rayModelViewBuffer = BufferUtils.createFloatBuffer( 16 );

    public Ray3F getCameraRay( int mouseX, int mouseY ) {
        loadCameraMatrices();

        // TODO: get rid of the slow glGet calls and use our home-grown matrices
        rayProjectionBuffer.clear();
        glGetFloat( GL_PROJECTION_MATRIX, rayProjectionBuffer );
        rayModelViewBuffer.clear();
        glGetFloat( GL_MODELVIEW_MATRIX, rayModelViewBuffer );

        ImmutableMatrix4F projectionMatrix = ImmutableMatrix4F.fromGLBuffer( rayProjectionBuffer );
        ImmutableMatrix4F modelViewMatrix = ImmutableMatrix4F.fromGLBuffer( rayModelViewBuffer );

        System.out.println( "projection:\n" + projectionMatrix );
        System.out.println( "modelView:\n" + projectionMatrix );
        System.out.println( "P*M:\n" + ( projectionMatrix.times( modelViewMatrix ) ) );

        ImmutableMatrix4F inverseTransform = ( projectionMatrix.times( modelViewMatrix ) ).inverted();

        ImmutableVector3F position = inverseTransform.times( getNormalizedDeviceCoordinates( new ImmutableVector3F( mouseX, mouseY, 0 ) ) );
        ImmutableVector3F farPlanePosition = inverseTransform.times( getNormalizedDeviceCoordinates( new ImmutableVector3F( mouseX, mouseY, farPlane ) ) );
        ImmutableVector3F direction = farPlanePosition.minus( position ).normalized();
        return new Ray3F( position, direction );
    }

    public static String bufferString( FloatBuffer buffer ) {
        StringBuilder builder = new StringBuilder();
        buffer.rewind();
        while ( buffer.hasRemaining() ) {
            builder.append( buffer.get() + " " );
        }
        return builder.toString();
    }

    // similar to gluUnProject
    public ImmutableVector3F getNormalizedDeviceCoordinates( ImmutableVector3F screenCoordinates ) {
        return new ImmutableVector3F( 2 * screenCoordinates.x / (float) getCanvasWidth() - 1,
                                      2 * screenCoordinates.y / (float) getCanvasHeight() - 1,
                                      2 * screenCoordinates.z - 1 );
    }

    private FloatBuffer specular = LWJGLUtils.floatBuffer( new float[] { 0, 0, 0, 0 } );
    private FloatBuffer shininess = LWJGLUtils.floatBuffer( new float[] { 50 } );
    private FloatBuffer sunDirection = LWJGLUtils.floatBuffer( new float[] { 1, 3, -2, 0 } );
    private FloatBuffer moonDirection = LWJGLUtils.floatBuffer( new float[] { -2, 1, -1, 0 } );

    public void loadLighting() {
        /*
        final DirectionalLight sun = new DirectionalLight();
        sun.setDirection( new Vector3f( 1, 3f, -2 ).normalizeLocal() );
        sun.setColor( new ColorRGBA( 1, 1, 1, 1.3f ) );
        node.addLight( sun );

        final DirectionalLight moon = new DirectionalLight();
        moon.setDirection( new Vector3f( -2, 1, -1 ).normalizeLocal() );
        moon.setColor( new ColorRGBA( 1, 1, 1, 0.5f ) );
        node.addLight( moon );
         */

        glMaterial( GL_FRONT, GL_SPECULAR, specular );
//            glMaterial( GL_FRONT, GL_SHININESS, shininess );
        glLight( GL_LIGHT0, GL_POSITION, sunDirection );
        glLight( GL_LIGHT1, GL_POSITION, moonDirection );
//        glEnable( GL_LIGHTING );
        glEnable( GL_LIGHT0 );
        glEnable( GL_LIGHT1 );
    }

    public OrthoComponentNode createFPSReadout( final Color color ) {
        JPanel fpsPanel = new JPanel() {{
            setPreferredSize( new Dimension( 100, 30 ) );
            setOpaque( true );
            add( new JLabel( "(FPS here)" ) {{
                setForeground( color );
                framesPerSecond.addObserver( new SimpleObserver() {
                    public void update() {
                        final double fps = Math.round( framesPerSecond.get() * 10 ) / 10;
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                setText( "FPS: " + fps );
                            }
                        } );
                    }
                } );
            }} );
        }};
        return new OrthoComponentNode( fpsPanel, this, canvasTransform,
                                       new Property<ImmutableVector2D>( new ImmutableVector2D( stageSize.getWidth() - fpsPanel.getPreferredSize().getWidth(), stageSize.getHeight() - fpsPanel.getPreferredSize().getHeight() ) ), mouseEventNotifier ) {{
            updateOnEvent( beforeFrameRender );
        }};
    }
}
