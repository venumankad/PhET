package edu.colorado.phet.therampscala

import _root_.scala.collection.mutable.ArrayBuffer
import _root_.scala.swing.CheckBox
import common.phetcommon.application.Module
import common.phetcommon.math.MathUtil
import common.phetcommon.model.BaseModel
import common.phetcommon.view.controls.valuecontrol.LinearValueControl
import common.phetcommon.view.graphics.transforms.{TransformListener, ModelViewTransform2D}
import common.phetcommon.view.util.{PhetFont, BufferedImageUtils}
import common.phetcommon.view.{VerticalLayoutPanel, ResetAllButton}
import common.piccolophet.event.CursorHandler
import common.piccolophet.nodes.PhetPPath
import common.piccolophet.PhetPCanvas
import controls.RampControlPanel
import graphics.{RampCanvas, BeadNode, SkyNode, EarthNode}

import model.{BeadState, Bead}

import edu.colorado.phet.scalacommon.Predef._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}

import java.awt.geom._
import java.awt.image.BufferedImage
import java.text.DecimalFormat
import javax.swing._
import javax.swing.event.{ChangeListener, ChangeEvent}

import scalacommon.math.Vector2D
import scalacommon.record.{PlaybackSpeedSlider, RecordModel, RecordModelControlPanel}
import scalacommon.swing.MyRadioButton
import scalacommon.util.Observable
import swing.ScalaValueControl
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.{PPath, PImage, PText}
import umd.cs.piccolo.PNode
import scalacommon.{ScalaApplicationLauncher, ScalaClock}
import java.lang.Math._
import umd.cs.piccolo.util.PDimension
import umd.cs.piccolox.pswing.PSwing

case class RampSegmentState(startPoint: Vector2D, endPoint: Vector2D) { //don't use Point2D since it's not immutable
  def setStartPoint(newStartPoint: Vector2D) = new RampSegmentState(newStartPoint, endPoint)

  def setEndPoint(newEndPoint: Vector2D) = new RampSegmentState(startPoint, newEndPoint)

  def getUnitVector = (endPoint - startPoint).normalize

  def setAngle(angle: Double) = new RampSegmentState(startPoint, new Vector2D(angle) * (endPoint - startPoint).magnitude)
}
class RampSegment(_state: RampSegmentState) extends Observable {
  var state = _state;
  def this(startPt: Point2D, endPt: Point2D) = this (new RampSegmentState(startPt, endPt))

  def toLine2D = new Line2D.Double(state.startPoint, state.endPoint)

  def startPoint = state.startPoint

  def endPoint = state.endPoint

  def startPoint_=(pt: Vector2D) = {
    state = state.setStartPoint(pt)
    notifyListeners()
  }

  def endPoint_=(pt: Vector2D) = {
    state = state.setEndPoint(pt)
    notifyListeners()
  }

  def length = (endPoint - startPoint).magnitude

  def getUnitVector = state.getUnitVector

  def setAngle(angle: Double) = {
    state = state.setAngle(angle)
    notifyListeners()
  }
}

class Circle(center: Vector2D, radius: Double) extends Ellipse2D.Double(center.x - radius, center.y - radius, radius * 2, radius * 2)

class RampSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D) extends PNode {
  val line = new PhetPPath(new Color(184, 131, 24), new BasicStroke(2f), new Color(91, 78, 49))
  addChild(line)
  defineInvokeAndPass(rampSegment.addListenerByName) {
    line.setPathTo(mytransform.createTransformedShape(new BasicStroke(0.4f).createStrokedShape(rampSegment.toLine2D)))
  }
}

class RotatableSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D) extends RampSegmentNode(rampSegment, mytransform) {
  line.addInputEventListener(new CursorHandler(Cursor.N_RESIZE_CURSOR))
  line.addInputEventListener(new PBasicInputEventHandler {
    override def mouseDragged(event: PInputEvent) = {
      val modelPt = mytransform.viewToModel(event.getPositionRelativeTo(line.getParent))

      val deltaView = event.getDeltaRelativeTo(line.getParent)
      val deltaModel = mytransform.viewToModelDifferential(deltaView.width, deltaView.height)

      val oldPtModel = modelPt - deltaModel

      val oldAngle = (rampSegment.startPoint - oldPtModel).getAngle
      val newAngle = (rampSegment.startPoint - modelPt).getAngle

      val deltaAngle = newAngle - oldAngle

      //draw a ray from start point to new mouse point
      val newPt = new Vector2D(rampSegment.getUnitVector.getAngle + deltaAngle) * rampSegment.length
      val clamped =
      if (newPt.getAngle < 0) new Vector2D(0) * rampSegment.length
      else if (newPt.getAngle > PI / 2) new Vector2D(PI / 2) * rampSegment.length
      else newPt
      rampSegment.endPoint = clamped
    }
  })
}

object MyRandom extends scala.util.Random


class RampModel extends RecordModel[String] {
  def setPlaybackState(state: String) {}

  def handleRecordStartedDuringPlayback() {}

  def getMaxRecordPoints = 100

  val rampSegments = new ArrayBuffer[RampSegment]
  val beads = new ArrayBuffer[Bead]
  private var _walls = true
  private var _frictionless = false
  private var _selectedObject = RampDefaults.objects(0)

  def selectedObject = _selectedObject

  def selectedObject_=(obj: ScalaRampObject) = {
    _selectedObject = obj
    beads(0).mass = _selectedObject.mass
    notifyListeners()
  }

  def walls = _walls

  def frictionless = _frictionless

  def walls_=(b: Boolean) = {
    _walls = b
    notifyListeners()
  }

  def frictionless_=(b: Boolean) = {
    _frictionless = b
    notifyListeners()
  }

  rampSegments += new RampSegment(new Point2D.Double(-10, 0), new Point2D.Double(0, 0))
  rampSegments += new RampSegment(new Point2D.Double(0, 0), new Point2D.Double(10 * sin(PI / 4), 10 * sin(PI / 4)))

  def setRampAngle(angle: Double) = {
    rampSegments(1).setAngle(angle)
  }

  //TODO: this may need to be more general
  def positionMapper(particleLocation: Double) = {
    if (particleLocation <= 0) rampSegments(0).getUnitVector * (10 + particleLocation) + rampSegments(0).startPoint
    else rampSegments(1).getUnitVector * (particleLocation) + rampSegments(1).startPoint
  }

  def rampSegmentAccessor(particleLocation: Double) = {
    if (particleLocation <= 0) rampSegments(0) else rampSegments(1)
  }

  //Sends notification when any ramp segment changes
  object rampChangeAdapter extends Observable //todo: perhaps we should just pass the addListener method to the beads
  rampSegments(0).addListenerByName {rampChangeAdapter.notifyListeners}
  rampSegments(1).addListenerByName {rampChangeAdapter.notifyListeners}
  beads += new Bead(new BeadState(5, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)
  val tree = new Bead(new BeadState(-9, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)
  val leftWall = new Bead(new BeadState(-10, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)
  val rightWall = new Bead(new BeadState(10, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)

  val manBead = new Bead(new BeadState(2, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)

  def update(dt: Double) = {
    beads.foreach(b => newStepCode(b, dt))
  }

  case class WorkEnergyState(appliedWork: Double, gravityWork: Double, frictionWork: Double,
                            potentialEnergy: Double, kineticEnergy: Double, totalEnergy: Double)

  def newStepCode(b: Bead, dt: Double) = {
    val origState = b.state
    val forces = getForces(b)
    val netForce = forces.foldLeft(new Vector2D)((a, b) => {a + b})
    val parallelForce = netForce.dot(b.getRampUnitVector)
    val parallelAccel = parallelForce / b.mass
    b.setVelocity(b.velocity + parallelAccel * dt)

    val requestedPosition = b.position + b.velocity * dt

    //TODO: generalize boundary code
    if (requestedPosition <= -10) {
      b.setVelocity(0)
      b.setPosition(-10)
    }
    else if (requestedPosition >= 10) {
      b.setVelocity(0)
      b.setPosition(10)
    }
    else {
      b.setPosition(requestedPosition)
    }
    val justCollided = false

    if (b.getStaticFriction == 0 && b.getKineticFriction == 0) {
      val appliedWork = b.getTotalEnergy
      val gravityWork = -b.getPotentialEnergy
      val thermalEnergy = origState.thermalEnergy
      if (justCollided) {
        //        thermalEnergy += origState.kineticEnergy
      }
      val frictionWork = -thermalEnergy
      frictionWork
      new WorkEnergyState(appliedWork, gravityWork, frictionWork,
        b.getPotentialEnergy, b.getKineticEnergy, b.getTotalEnergy)
    } else {
      //      val dW=getAppliedWorkDifferential
      //      val appliedWork=origState.appliedWork
      //      val gravityWork=-getPotentialEnergy
      //      val etot=appliedWork
      //      val thermalEnergy=etot-kineticEnergy-potentialEnergy
      //      val frictionWork=-thermalEnergy

    }
  }

  def getForces(b: Bead) = {
    getGravityForce(b) :: b.appliedForce :: Nil
    //    getGravity :: getFriction(b) :: getWallForce(b) :: getNormalForce(b) :: Nil
    //    val netForce=getGravity+getFriction(b)+getNormal
  }

  def getGravityForce(b: Bead) = {
    new Vector2D(0, -9.8) * b.mass
  }
}

//see scala duck typing
//maybe we should replace this with a named trait
class ObjectSelectionNode(transform: ModelViewTransform2D, model: {def selectedObject: ScalaRampObject; def selectedObject_=(ro: ScalaRampObject): Unit; def addListenerByName(listener: => Unit): Unit}) extends PNode {
  val objects = RampDefaults.objects
  val rows = new ArrayBuffer[ArrayBuffer[PNode]]

  class ObjectSelectionIcon(o: ScalaRampObject) extends PNode {
    val textNode = new PText(o.name + " (" + o.mass + " kg)")
    val imageNode = new PImage(BufferedImageUtils.multiScaleToHeight(RampResources.getImage(o.imageFilename), 100))
    imageNode.scale(0.5f)
    textNode.scale(0.9f)
    textNode.setOffset(imageNode.getFullBounds.getWidth, 0)

    val backgroundNode = new PhetPPath(new BasicStroke(1f), new Color(0, 0, 0, 0))

    addChild(backgroundNode)
    addChild(imageNode)
    addChild(textNode)

    def updateSelected() = {
      if (model.selectedObject == o) {
        backgroundNode.setPaint(new Color(0, 0, 255, 128))
        textNode.setFont(new PhetFont(14, true))
      } else {
        backgroundNode.setPaint(new Color(0, 0, 0, 0))
        textNode.setFont(new PhetFont(14, false))
      }
    }
    addInputEventListener(new PBasicInputEventHandler {
      override def mousePressed(event: PInputEvent) = {
        model.selectedObject = o
      }
    })
    updateSelected()
    model.addListenerByName {updateSelected()}
  }

  val nodes = for (o <- objects) yield {
    new ObjectSelectionIcon(o)
  }

  val cellDim = nodes.foldLeft(new PDimension)((a, b) => new PDimension(max(a.width, b.getFullBounds.width), max(a.height, b.getFullBounds.height)))

  val modelCellDimPt = transform.viewToModelDifferential(cellDim.width, cellDim.height)
  //y is down, so modelCellDimPt.y is negative 
  for (i <- 0 until nodes.length) {
    val row = i / RampDefaults.objectsPerRow
    val column = i % RampDefaults.objectsPerRow

    val n = nodes(i)
    n.backgroundNode.setPathTo(new Rectangle2D.Double(0, 0, cellDim.width, cellDim.height))
    n.setOffset(transform.modelToView(column * modelCellDimPt.x - 11, -10 - row * modelCellDimPt.y - 2 * modelCellDimPt.y))
    addChild(n)
  }

}

class RampHeightIndicator(rampSegment: RampSegment, transform: ModelViewTransform2D) extends PNode {
  val line = new PhetPPath(new BasicStroke(2f), Color.black)
  addChild(line)

  val readout = new PText
  readout.setFont(new PhetFont(24))
  addChild(readout)
  def getLine = new Line2D.Double(new Vector2D(rampSegment.endPoint.x, 0), rampSegment.endPoint)
  defineInvokeAndPass(rampSegment.addListenerByName) {
    line.setPathTo(transform.createTransformedShape(getLine))
    readout.setOffset(line.getFullBounds.getMaxX + 10, line.getFullBounds.getCenterY)
    readout.setText("h = " + new DecimalFormat("0.0").format(rampSegment.endPoint.y) + " m")
  }
}

//todo: consider coalescing with RampHeightIndicator
class RampAngleIndicator(rampSegment: RampSegment, transform: ModelViewTransform2D) extends PNode {
  val line = new PhetPPath(new BasicStroke(2f), Color.black)
  val readout = new PText
  readout.setFont(new PhetFont(24))
  addChild(line)
  addChild(readout)
  def getDegrees = rampSegment.getUnitVector.getAngle.toDegrees

  def getPath = {
    val arc = new Arc2D.Double(rampSegment.startPoint.x - 3, rampSegment.startPoint.y - 3, 6, 6, 0, -getDegrees, Arc2D.OPEN)
    arc
  }
  defineInvokeAndPass(rampSegment.addListenerByName) {
    line.setPathTo(transform.createTransformedShape(getPath))
    readout.setOffset(transform.modelToView(0.5, -0.08))
    readout.setText("Angle = " + new DecimalFormat("0.0").format(getDegrees) + " \u00B0")
  }
}

class PusherNode(transform: ModelViewTransform2D, targetBead: Bead, manBead: Bead) extends BeadNode(manBead, transform, "standing-man.png") {
  defineInvokeAndPass(targetBead.addListenerByName) {
    if (targetBead.appliedForce.magnitude > 0) {
      val dx = if (targetBead.appliedForce.x > 0) -6 else 6
      manBead.setPosition(targetBead.position + dx)

      //images go 0 to 14
      val leanAmount = (abs(targetBead.appliedForce.x) * 13.0 / 50.0).toInt + 1
      var textStr = "" + leanAmount
      while (textStr.length < 2)
        textStr = "0" + textStr
      val im = RampResources.getImage("pusher-leaner-png/pusher-leaning-2_00" + textStr + ".png")
      val realIm = if (dx > 0) BufferedImageUtils.flipX(im) else im //todo: cache instead of flipping each time
      setImage(realIm)
    }
    else {
      setImage(RampResources.getImage("standing-man.png"))
    }
  }
  setPickable(false)
  setChildrenPickable(false)
}
class AppliedForceSliderNode(bead: Bead, transform: ModelViewTransform2D) extends PNode {
  val control = new ScalaValueControl(-50, 50, "Applied Force X", "0.0", "N",
    bead.appliedForce.x, value => bead.appliedForce = new Vector2D(value, 0), bead.addListener)

  val pswing = new PSwing(control)
  addChild(pswing)
  def updatePosition() = {
    val viewLoc = transform.modelToView(new Point2D.Double(0, -1))
    val scale = 1.2f
    pswing.setOffset(viewLoc - new Vector2D(pswing.getFullBounds.getWidth * scale, 0))
    pswing.setScale(scale)
  }
  updatePosition()
}

class WordModel extends Observable {
  var _physicsWords = true
  var _everydayWords = false

  def physicsWords_=(v: Boolean) = {
    _physicsWords = v
    _everydayWords = !_physicsWords

    notifyListeners()
  }

  def physicsWords = _physicsWords

  def everydayWords = _everydayWords

  def everydayWords_=(v: Boolean) = {
    _everydayWords = v
    _physicsWords = !v
    notifyListeners()
  }
}
class FreeBodyDiagramModel extends Observable {
  private var _visible = false

  def visible = _visible

  def visible_=(value: Boolean) = {
    _visible = value;
    notifyListeners()
  }

}
class CoordinateSystemModel extends Observable {
  private var _fixed = true

  def fixed = _fixed

  def adjustable = !_fixed

  def fixed_=(b: Boolean) = {
    _fixed = b
    notifyListeners()
  }

  def adjustable_=(b: Boolean) = {
    _fixed = !b
    notifyListeners()
  }
}

class VectorViewModel extends Observable {
  private var _originalVectors = true
  private var _parallelComponents = false
  private var _xyComponents = false
  private var _sumOfForcesVector = false

  def originalVectors = _originalVectors

  def parallelComponents = _parallelComponents

  def xyComponents = _xyComponents

  def sumOfForcesVector = _sumOfForcesVector

  def originalVectors_=(b: Boolean) = {
    _originalVectors = b
    notifyListeners()
  }

  def parallelComponents_=(b: Boolean) = {
    _parallelComponents = b
    notifyListeners()
  }

  def xyComponents_=(b: Boolean) = {
    _xyComponents = b
    notifyListeners()
  }

  def sumOfForcesVector_=(b: Boolean) = {
    _sumOfForcesVector = b
    notifyListeners()
  }
}
class RampModule(clock: ScalaClock) extends Module("Ramp", clock) {
  val model = new RampModel
  val wordModel = new WordModel
  val fbdModel = new FreeBodyDiagramModel
  val coordinateSystemModel = new CoordinateSystemModel
  val vectorViewModel = new VectorViewModel
  val canvas = new RampCanvas(model)
  setSimulationPanel(canvas)
  clock.addClockListener(model.update(_))
  setControlPanel(new RampControlPanel(model, wordModel, fbdModel, coordinateSystemModel, vectorViewModel))
  setClockControlPanel(new RecordModelControlPanel(model, canvas, () => {new PlaybackSpeedSlider(model)}, Color.blue, 20))
}

object RampApplication {
  def main(args: Array[String]) = {
    ScalaApplicationLauncher.launchApplication(args, "the-ramp", "the-ramp", () => new RampModule(new ScalaClock(30, 30 / 1000.0)))
  }
}