package edu.colorado.phet.therampscala.forcesandfriction

import common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher}
import common.piccolophet.PiccoloPhetApplication
import javax.swing.JFrame
import scalacommon.ScalaClock
import RampResources._

class IntroModule(frame: JFrame, clock: ScalaClock) extends BasicRampModule(frame, clock, "Intro", false, false, -6, false, 0.0)
class FrictionModule(frame: JFrame, clock: ScalaClock) extends BasicRampModule(frame, clock, "Friction", false, false, -6, false, 0.0)
class GraphingModule(frame: JFrame, clock: ScalaClock) extends BasicRampModule(frame, clock, "Graphing", false, false, -6, false, 0.0)
class RobotMovingCompany1DModule(frame: JFrame, clock: ScalaClock) extends BasicRampModule(frame, clock, "Robot Moving Company", false, false, -6, false, 0.0)

class ForcesAndFrictionApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(RampDefaults.DELAY, RampDefaults.DT_DEFAULT)
  addModule(new IntroModule(getPhetFrame, newClock))
  addModule(new FrictionModule(getPhetFrame, newClock))
  addModule(new GraphingModule(getPhetFrame, newClock))
  addModule(new RobotMovingCompany1DModule(getPhetFrame, newClock))
}

object ForcesAndFrictionApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "the-ramp".literal, "forces-and-friction".literal, classOf[ForcesAndFrictionApplication])
}
