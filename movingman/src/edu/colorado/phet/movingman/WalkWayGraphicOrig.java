//package edu.colorado.phet.movingman;
//
//import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
//import edu.colorado.phet.common.view.util.GraphicsState;
//import edu.colorado.phet.common.view.util.ImageLoader;
//import edu.colorado.phet.common.view.util.SimStrings;
//import edu.colorado.phet.movingman.common.LinearTransform1d;
//
//import java.awt.*;
//import java.awt.geom.Rectangle2D;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.text.DecimalFormat;
//
///**
// * Created by IntelliJ IDEA.
// * User: Sam Reid
// * Date: Jul 5, 2003
// * Time: 5:34:02 PM
// * To change this template use Options | File Templates.
// */
//public class WalkWayGraphicOrig extends PhetGraphic {
//    private int numTickMarks = 21;
//    private double treex;
//    private double housex;
//    private MovingManModule module;
//    private DecimalFormat format = new DecimalFormat( "##" );
//    private Font font = MMFontManager.getFontSet().getWalkwayFont();
//    private BufferedImage tree;
//    private BufferedImage house;
//    private Stroke borderStroke = new BasicStroke( 1 );
//    private BufferedImage wallImage;
//
//    public WalkWayGraphicOrig( MovingManModule module, int numTickMarks ) throws IOException {
//        this( module, numTickMarks, -8, 8 );
//    }
//
//    public WalkWayGraphicOrig( MovingManModule module, int numTickMarks, double treex, double housex ) throws IOException {
//        super( module.getApparatusPanel() );
//        this.module = module;
//        this.numTickMarks = numTickMarks;
//        this.treex = treex;
//        this.housex = housex;
//        tree = ImageLoader.loadBufferedImage( "images/tree.gif" );
//        house = ImageLoader.loadBufferedImage( "images/cottage.gif" );
//        wallImage = ImageLoader.loadBufferedImage( "images/barrier.jpg" );
//    }
//
//    public void setTreeX( double treex ) {
//        this.treex = treex;
//    }
//
//    public void setHouseX( double housex ) {
//        this.housex = housex;
//    }
//
//    public void paint( Graphics2D graphics2D ) {
//        GraphicsState graphicsState = new GraphicsState( graphics2D );
////        graphics2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
//        LinearTransform1d transform = module.getManPositionTransform();
//        double modelRange = transform.getInputRange();
//        double modelDX = modelRange / ( numTickMarks - 1 );
//        graphics2D.setColor( Color.black );
//        graphics2D.setFont( font );
//        int height = 134;
//
//        graphics2D.setColor( module.getPurple() );
//        Rectangle rect = new Rectangle( 0, 0, module.getApparatusPanel().getWidth(), height + 30 );
//        graphics2D.fill( rect );
//        graphics2D.setColor( Color.blue );
//        graphics2D.setStroke( borderStroke );
//        graphics2D.drawLine( 0, rect.y + rect.height, rect.width, rect.y + rect.height );
//        graphics2D.setColor( Color.black );
//
//        for( int i = 0; i < numTickMarks; i++ ) {
//            double modelx = transform.getMinInput() + i * modelDX;
//            int viewx = (int)transform.transform( modelx );
//
//            Point dst = new Point( viewx, height - 20 );
//            graphics2D.drawLine( viewx, height, dst.x, dst.y );
//
//            String str = format.format( modelx );
//            if( str.equals( "0" ) ) {
//                str = "0 " + SimStrings.get( "WalkWayGraphic.MetersText" );
//            }
//            Rectangle2D bounds = font.getStringBounds( str, graphics2D.getFontRenderContext() );
//            graphics2D.drawString( str, viewx - (int)( bounds.getWidth() / 2 ), height + (int)bounds.getHeight() );
//        }
//        //Tree at -10.
//        int treex = getImageLocation( tree, this.treex );
//        int treey = 10;
//        int housex = getImageLocation( house, this.housex );
//        int housey = 10;
//        graphics2D.drawImage( tree, treex, treey, null );
//        graphics2D.drawImage( house, housex, housey, null );
//
//        int leftWallY = treey;
//        int rightWallY = treey;
//        int leftWallX = getImageLocation( wallImage, -10 );
//        int rightWallX = getImageLocation( wallImage, 10 );
//        graphics2D.drawImage( wallImage, leftWallX, leftWallY, null );
//        graphics2D.drawImage( wallImage, rightWallX, rightWallY, null );
//        graphicsState.restoreGraphics();
//    }
//
//    private int getImageLocation( BufferedImage image, double modelX ) {
//        LinearTransform1d transform = module.getManPositionTransform();
//        int val = (int)( transform.transform( modelX ) - image.getWidth() / 2 );
//        return val;
//    }
//
//    protected Rectangle determineBounds() {
//        return getComponent().getBounds();//TODO wrong bounds.
//    }
//}
