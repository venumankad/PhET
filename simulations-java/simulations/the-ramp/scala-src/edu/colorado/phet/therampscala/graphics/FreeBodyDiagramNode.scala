package edu.colorado.phet.therampscala.graphics

import common.phetcommon.view.util.PhetFont
import common.piccolophet.nodes.ArrowNode
import common.piccolophet.PhetPCanvas
import java.awt.Color
import java.awt.geom.{Point2D, Rectangle2D}
import javax.swing.JFrame
import scalacommon.math.Vector2D
import scalacommon.util.Observable
import umd.cs.piccolo.nodes.{PText, PPath}
import umd.cs.piccolo.PNode

abstract class Vector extends Observable {
  def getValue: Vector2D
}
class FreeBodyDiagramNode(val width: Int, val height: Int, val vectors: Vector*) extends PNode {
  val background = new PPath(new Rectangle2D.Double(0, 0, width, height))
  addChild(background)
  val arrowInset = 4

  class AxisNode(x0: Double, y0: Double, x1: Double, y1: Double, label: String) extends PNode {
    val axisNode = new ArrowNode(new Point2D.Double(x0, y0), new Point2D.Double(x1, y1), 5, 5, 2)
    axisNode.setStroke(null)
    axisNode.setPaint(Color.black)
    addChild(axisNode)
    val text = new PText(label)
    text.setFont(new PhetFont(16, true))
    text.setOffset(x1 - text.getFullBounds.getWidth * 1.5, y1)
    addChild(text)
  }
  addChild(new AxisNode(0 + arrowInset, height / 2, width - arrowInset, height / 2, "x"))
  addChild(new AxisNode(width / 2, height - arrowInset, width / 2, 0 + arrowInset, "y"))
}

object TestFBD extends Application {
  val frame = new JFrame
  val canvas = new PhetPCanvas
  canvas.addScreenChild(new FreeBodyDiagramNode(200, 200))
  frame.setContentPane(canvas)
  frame.setSize(800, 600)
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.setVisible(true)
}