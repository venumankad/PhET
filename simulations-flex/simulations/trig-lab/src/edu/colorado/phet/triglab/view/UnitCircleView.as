/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.view {
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.triglab.model.TrigModel;
import edu.colorado.phet.triglab.util.Util;

import flash.display.CapsStyle;

import flash.display.Graphics;
import flash.display.MovieClip;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.events.TimerEvent;
import flash.geom.Point;
import flash.utils.Timer;
import flash.utils.clearInterval;

public class UnitCircleView extends Sprite {

    private var myMainView: MainView;
    private var myTrigModel: TrigModel;

    private var unitCircleGraph: Sprite ; //unit circle centered on xy axes
    private var triangleDiagram: Sprite;  //triangle drawn on unit circle, the ratio of the sides are the trig functions
    private var gridLines: Sprite ;       //optional gridlines on unit circle
    private var labelsLayer: Sprite;      //optional labels x, y, 1, theta
    private var angleArc: Sprite;         //arc showing the angle theta
    private var angleHandle: Sprite;//grabbable handle for setting angle on unit Circle
    private var radius:Number;      //radius of unit circle in pixels
    private var previousAngle: Number;
    private var smallAngle: Number;   //angle between -pi and +pi in radians
    private var totalAngle: Number;
    private var _trigMode: int;      //0 when displaying cos graph, 1 for sin, 2 for tan
    //private var nbrHalfTurns:Number;

    //Labels
    private var x_lbl: NiceLabel;       //x-label on x-axis
    private var y_lbl: NiceLabel;       //y-label on y-axis
    private var x2_lbl: NiceLabel;      //x-label on xyr triangle
    private var y2_lbl: NiceLabel;      //y-lable on xyr triangle
    private var one_lbl: NiceLabel;     //1 label on radius
    private var theta_lbl: NiceLabel;   //angle label

    //internationalized strings
    private var x_str: String;
    private var y_str: String;
    private var one_str: String;
    private var theta_str: String;

    private var grabbed: Boolean;   //true if angle selection ball is grabbed

    private var stageW: int;
    private var stageH: int;



    public function UnitCircleView( myMainView:MainView, myTrigModel:TrigModel) {
        this.myMainView = myMainView;
        this.myTrigModel = myTrigModel;
        this.myTrigModel.registerView( this );
        this.unitCircleGraph = new Sprite();
        this.triangleDiagram = new Sprite();
        this.gridLines = new Sprite();
        this.labelsLayer = new Sprite();
        this.angleArc = new Sprite();
        this.angleHandle = new Sprite();
        this.grabbed = false;
        this.internationalizeStrings();
        this.x_lbl = new NiceLabel( 25, x_str );
        this.y_lbl = new NiceLabel( 25, y_str )
        this.x2_lbl = new NiceLabel( 25, x_str );
        this.y2_lbl = new NiceLabel( 25, y_str )
        this.one_lbl = new NiceLabel( 25, one_str );
        this.theta_lbl = new NiceLabel( 25, theta_str );
        this.initialize();
    }

    private function internationalizeStrings():void{
        this.x_str = FlexSimStrings.get( "x", "x");
        this.y_str = FlexSimStrings.get( "y", "y");
        this.one_str = FlexSimStrings.get( "one", "1");
        this.theta_str = FlexSimStrings.get( "theta", "theta" );   //greek letter theta
    }

    private function initialize():void{
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        this.radius = 200;
        this.addChild( gridLines );
        this.addChild( unitCircleGraph );
        this.unitCircleGraph.addChild( triangleDiagram );
        this.unitCircleGraph.addChild( labelsLayer );
        this.unitCircleGraph.addChild( angleArc );
        this.unitCircleGraph.addChild( this.angleHandle );
        this.unitCircleGraph.addChild( x_lbl );
        this.unitCircleGraph.addChild( y_lbl );
        this.labelsLayer.addChild( one_lbl );
        this.labelsLayer.addChild( x2_lbl );
        this.labelsLayer.addChild( y2_lbl );
        this.drawUnitCircle();
        this.drawGridLines();
        this.drawAngleHandle();
        this.makeAngleHandleGrabbable();
        this.smallAngle = 0;
        this.previousAngle = 0;
        this.totalAngle = 0;
        this._trigMode = 0;      //start with trigMode = cos
        //this.nbrHalfTurns = 0;
    } //end of initialize


    private function drawUnitCircle():void{
        var g:Graphics = this.unitCircleGraph.graphics;
        var f: Number = 1.3;     //extent of each axis is -f*radius to +f*radius
        with ( g ){
            clear();
            lineStyle( 2, 0x000000, 1 );  //black
            //draw xy axes
            moveTo( -f*radius,  0 );
            lineTo( +f*radius,  0 );
            moveTo( 0, -f*radius );
            lineTo( 0, f*radius );
            //draw arrow heads on axes
            var length:Number = 10;
            var halfWidth:Number = 6
            //x-axis arrow
            beginFill( 0x000000, 1 );
            moveTo( f*radius - length,  halfWidth );
            lineTo( f*radius, 0 );
            lineTo( f*radius - length,  -halfWidth );
            lineTo( f*radius - length,  halfWidth );
            endFill();
            //y-axis arrow
            beginFill( 0x000000, 1)
            moveTo( -halfWidth, -f*radius + length);
            lineTo( 0, -f*radius );
            lineTo( +halfWidth, -f*radius + length );
            lineTo( -halfWidth, -f*radius + length);
            endFill();
            //draw unit circle
            lineStyle( 2, 0x000000, 1 );  //black
            drawCircle( 0, 0, radius );
        }
        x_lbl.x = f*radius - x_lbl.width - 10;
        x_lbl.y = 5;
        y_lbl.x = -y_lbl.width - 10;
        y_lbl.y = -f*radius + 5;
    }  //end drawUnitCircle()

    private function drawGridLines():void{
        //grid spacing = 0.5
        var spacing: Number = 0.5*this.radius;
        var gGrid: Graphics = this.gridLines.graphics;
        gGrid.lineStyle( 2, 0xaaaaaa, 1 ); //light gray color
        for( var i:int = -2; i <= 2; i++) {
            with(gGrid){
                //draw horizontal lines
                moveTo(-2*spacing,  i*spacing );
                lineTo(+2*spacing,  i*spacing );
                //draw vertical lines
                moveTo( i*spacing, -2*spacing );
                lineTo( i*spacing, +2*spacing );
            }//end with
        }//end for
    }//end drawGridLines

    private function drawTriangle():void{
        var gTriangle: Graphics = this.triangleDiagram.graphics;
        var xLeg: Number = this.radius*myTrigModel.cos;
        var yLeg: Number = -this.radius*myTrigModel.sin;
        var xColor: uint = 0x000000;
        var yColor: uint = 0x000000;
        var hypColor: uint = 0x000000;
        var xStroke: int = 6;
        var yStroke: int = 2;
        if( _trigMode == 0 ){
            xColor = Util.COSCOLOR;
            xStroke = 6;
        }else if ( _trigMode == 1 ){
            yColor = Util.SINCOLOR;
            yStroke = 6;
        }else if ( _trigMode == 2 ){
            // do nothing
        }
        with( gTriangle ){
            clear();
            //draw hypotenuse
            lineStyle( 2, hypColor, 1, false, "normal", "none" );
            moveTo( 0, 0 );
            lineTo( xLeg, yLeg );
            //draw x-leg
            lineStyle( xStroke, xColor,  1, false, "normal", "none"  ) ;
            moveTo( 0, 0 );
            lineTo( xLeg,  0 );
            //draw y-leg
            lineStyle( yStroke, yColor,  1, false, "normal", "none") ;
            moveTo( xLeg, 0 );
            lineTo( xLeg,  yLeg );
        }
    }//end drawTriangle

    private function drawAngleArc():void{
        var gArc: Graphics = this.angleArc.graphics;
        var r: Number = 0.3*radius;
        var sign: Number;
        totalAngle = myTrigModel.totalAngle;
        if( totalAngle != 0 ){
            sign = Math.abs( totalAngle )/totalAngle;
        }else{
            sign = 0;
        }
        //trace( "UnitCircleView.drawAngleArc  sign of angle = " + sign + "totalAngle = " + totalAngle );
        with( gArc ){
            clear();
            lineStyle( 1, 0x000000, 1 );
            moveTo( r, 0 );
            if( sign > 0 ){
                for( var ang: Number = 0; ang <= totalAngle; ang += 0.02 ){
                    r -= 0.02;
                    lineTo( r*Math.cos( ang ), -r*Math.sin( ang ) )
                }
            }else if( sign < 0 ){
                for( var ang: Number = 0; ang >= totalAngle; ang -= 0.02 ){
                    r -= 0.02;
                    lineTo( r*Math.cos( ang ), -r*Math.sin( ang ) )
                }
            }
        } //end with
    }//end drawAngleArc()

    private function drawAngleHandle():void{
        var gBall: Graphics = this.angleHandle.graphics;
        var ballRadius: Number = 5;
        with( gBall ){
            clear();
            lineStyle( 1, 0x0000ff, 1.0 )
            beginFill( 0xff0000, 1.0 )    //ball is red
            drawCircle( 0, 0, ballRadius );
            endFill();
        }
        var grabArea: Sprite = new Sprite();
        this.angleHandle.addChild( grabArea );
        //draw invisible grab area
        var gGrab: Graphics = grabArea.graphics;
        var grabRadius: Number = 50;
        with( gGrab ){
            clear();
            lineStyle( 1, 0xffffff, 0 );
            beginFill( 0x00ff00,0 );
            drawCircle( 0, 0, grabRadius );
            endFill();
        }
    }//end drawAngleHandle();



    private function makeAngleHandleGrabbable():void{
        var thisObject:Object = this;
        var clickOffset: Point;
        this.angleHandle.buttonMode = true;
        this.angleHandle.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );

        function startTargetDrag( evt: MouseEvent ): void {
            thisObject.grabbed = true;
            clickOffset = new Point( evt.localX, evt.localY );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        }


        function stopTargetDrag( evt: MouseEvent ): void {
            thisObject.grabbed = false;
            clickOffset = null;
            evt.updateAfterEvent();
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        }

        function dragTarget( evt: MouseEvent ): void {
            var xInPix: Number = thisObject.unitCircleGraph.mouseX - clickOffset.x;
            var yInPix: Number = thisObject.unitCircleGraph.mouseY - clickOffset.y;
            var angleInRads: Number = Math.atan2( yInPix,  xInPix );
            thisObject.smallAngle = -angleInRads;   //minus-sign to be consistent with convention: CCW = +angle, CW = -angle
            thisObject.myTrigModel.smallAngle = -angleInRads;
            evt.updateAfterEvent();
        }//end of dragTarget()
    }//end makeAngleHandleGrabbable



    private function positionAngleHandle( angleInRads: Number ):void{
        var xInPix: Number = this.radius*Math.cos( angleInRads );
        var yInPix: Number = this.radius*Math.sin( angleInRads );
        this.angleHandle.x = xInPix;
        this.angleHandle.y = -yInPix;
        this.positionLabels();
    }

    private function positionLabels():void{
        var angle: Number = myTrigModel.smallAngle;
        //position one-label
        var angleOffset: Number = 10*Math.PI/180;
        var sign: int = 1;
        var pi: Number = Math.PI;
        if( ( angle > pi/2 && angle < pi ) ||( angle > -pi/2 && angle < 0 )){
            sign = -1;
        }
        var xInPix: Number = this.radius*Math.cos( angle + sign*angleOffset );
        var yInPix: Number = this.radius*Math.sin( angle + sign*angleOffset );
        this.one_lbl.x = 0.6*xInPix - 0.5*one_lbl.width;
        this.one_lbl.y = - 0.6*yInPix -0.5*one_lbl.height;
        //position x-label
        var xPos: Number = 0.5*radius*Math.cos( angle ) - 0.5*x2_lbl.width;
        var yPos: Number = 0;
        if( angle < 0 ){ yPos = -x2_lbl.height }
        x2_lbl.x = xPos;
        x2_lbl.y = yPos;
        //position y-label
        sign = 1;
        if( ( angle > pi/2 && angle < pi ) ||( angle > -pi && angle < -pi/2 )){
            sign = -1;
        }
        xPos = radius*Math.cos(angle) - 0.5*x2_lbl.width + sign*x2_lbl.width;
        yPos = -0.5*radius*Math.sin( angle ) - 0.5*y2_lbl.height;
        y2_lbl.x = xPos;
        y2_lbl.y = yPos;
    }

    public function set trigMode( mode: int):void {
        this._trigMode = mode;  //0 for cos, 1 for sin, 2 for tan
        this.drawTriangle();
    }

    public function setGridLinesVisibility( tOrF: Boolean ):void{
        this.gridLines.visible = tOrF;
    }

    public function setLabelsVisibility( tOrF: Boolean ):void{
        this.labelsLayer.visible = tOrF;
    }

    public function update():void{
        this.positionAngleHandle( myTrigModel.smallAngle );
        this.drawTriangle();
        this.drawAngleArc();
    }

}  //end of class
}  //end of package
