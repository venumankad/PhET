//package edu.colorado.phet.motionseries.sims.theramp
//
//import edu.colorado.phet.motionseries.graphics.MotionSeriesCanvas
//import edu.colorado.phet.motionseries.model.{MotionSeriesModel}
//import edu.colorado.phet.motionseries.MotionSeriesResources._
//import edu.colorado.phet.motionseries.charts.{Graph, MotionSeriesChartNode}
//
//class RampForceEnergyChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
//  init(Graph("forces.energy-title".translate, energyGraph.toMinimizableControlChart, false) :: Nil)
//}
//
//class RampForceChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
//  init(Graph("forces.parallel-title".translate, forceGraph.toMinimizableControlChart, false) :: Nil)
//}