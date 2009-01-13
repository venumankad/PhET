package edu.colorado.phet.movingman.ladybug

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor
import edu.colorado.phet.common.phetcommon.application.Module
import edu.colorado.phet.common.phetcommon.application.PhetApplication
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import edu.umd.cs.piccolo.nodes.PText
import edu.umd.cs.piccolo.PNode
import java.awt.Color
import java.awt.Font
import javax.swing.JLabel

object Ladybug2DApplication {
  class ScalaModelElement(element: Double => Unit) extends ClockAdapter {
    override def simulationTimeChanged(clockEvent: ClockEvent) = {
      element(clockEvent.getSimulationTimeChange)
    }
  }
  class ScalaClock(delay: Int, dt: Double) extends ConstantDtClock(delay, dt) {
    def addClockListener(exp: Double => Unit) {
      super.addClockListener(new ClockAdapter() {
        override def simulationTimeChanged(clockEvent: ClockEvent) = exp(clockEvent.getSimulationTimeChange)
      })
    }
  }
  def main(args: Array[String]) = {
    println("started")

    val clock = new ScalaClock(30, 1)
    class ScalaModule extends Module("my module", clock) {
      val model = new LadybugModel
      val canvas = new PhetPCanvas
      setSimulationPanel(canvas)

      val ptext = new PText("hello")
      ptext setFont new Font("Lucida Sans", Font.BOLD, 30)
      ptext.setOffset(300, 200)
      canvas addScreenChild ptext
      canvas setBackground new Color(200, 255, 240)
      clock.addClockListener((dt: Double) => ptext.translate(1 * dt, 0))
      clock.addClockListener((dt: Double) => ptext.translate(0, 2 * dt))
//      clock.addClockListener((dt: Double) => model.ladybug.rotate(java.lang.Math.PI / 32))

      clock.addClockListener(model.update(_))

      canvas.addScreenChild(new LadybugNode(model.ladybug))

      //      canvas.addScreenChild(new RampObjectNode(model.getObject(0)))
      //      canvas.addScreenChild(new RampTrackNode(model.getTrack(0)))
    }

    new PhetApplicationLauncher().launchSim(
      new PhetApplicationConfig(args, "moving-man", "ladybug-2d"),
      new ApplicationConstructor() {
        override def getApplication(a: PhetApplicationConfig): PhetApplication = new PhetApplication(a) {
          addModule(new ScalaModule)
        }
      })
    println("finished")
  }
}