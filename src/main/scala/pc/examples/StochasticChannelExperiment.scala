package pc.examples

import pc.utils.Time
import pc.modelling.CTMCAnalysis

object StochasticChannelExperiment extends App {

  import pc.examples.StochasticChannel._
  import pc.examples.StochasticChannel.state._

  val channel = StochasticChannel.stocChannel

  val channelAnalysis = CTMCAnalysis(channel)

  val untilProp = channelAnalysis.until[State](_ => true, _ == DONE)
  val eventuallyProp = channelAnalysis.eventually[State](_ == DONE)
  val globallyProp = channelAnalysis.globally[State](_ != DONE)

  val data = for (t <- (0.1 to 10.0 by 0.1).toParArray; //parallel execution
                  p = channelAnalysis.experiment(
                        runs = 19000,
                        prop = eventuallyProp,
                        s0 = IDLE,
                        timeBound = t)) yield (t, p)

  Time.timed{ println(data.mkString("\n")) }
  scalax.chart.api.XYLineChart(data).show() // with dependencies on scala-chart
}