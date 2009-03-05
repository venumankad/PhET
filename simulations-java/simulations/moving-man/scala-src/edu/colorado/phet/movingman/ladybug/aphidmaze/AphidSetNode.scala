package edu.colorado.phet.movingman.ladybug.aphidmaze

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import canvas.BugNode
import umd.cs.piccolo.PNode
import edu.colorado.phet.scalacommon.Predef._

class AphidSetNode(model: AphidMazeModel, transform: ModelViewTransform2D) extends PNode {
  val update = defineInvokeAndPass(model.addListenerByName){
    removeAllChildren
    model.aphids.foreach((aphid: Aphid) => addChild(new BugNode(aphid, transform,MovingManResources.loadBufferedImage("ladybug/valessiobrito_Bug_Buddy_Vec.png"))))
  }
}