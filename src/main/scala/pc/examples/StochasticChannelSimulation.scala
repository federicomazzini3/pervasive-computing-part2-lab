package pc.examples

import pc.modelling.CTMCSimulation
import java.util.Random

object StochasticChannelSimulation extends App {

  import pc.examples.StochasticChannel.state._

  val channel = StochasticChannel.stocChannel
  val channel2 = StochasticChannel.stocChannel2

  val channelAnalysis = CTMCSimulation(channel)

  val simulation = Simulation.fromTrace(10000,
    100,
    () => channelAnalysis.newSimulationTrace(IDLE, new Random()))

  val failTime = simulation.reduce{ run =>
    run.toList
      .takeWhile(_._2 != DONE)
      .sliding(2, 1)
      .collect { case (t1, FAIL) :: (t2, _) :: Nil => t2 - t1 }
      .sum
  }

  val doneTime = simulation.reduce(_.dropWhile(_._2 != DONE).head._1)

  println(
    "\nAVG DONE time: " + doneTime.sum / doneTime.size
      + "\nMAX DONE time: " + doneTime.max
      + "\nMIN DONE time: " + doneTime.min
      + "\nAVG FAIL time: " + failTime.sum / failTime.size
      + "\n% of FAIL time before DONE : " + ((failTime.sum / failTime.size) / (doneTime.sum / doneTime.size) * 100 ).round + "%")
}


object Simulation {
  def fromTrace[A](n: Int, maxTraceLength: Int, trace: () => Iterable[(Double, A)]): Simulation[A] =
    Simulation(n, maxTraceLength, trace)

  case class Simulation[A](n: Int, maxTraceLength: Int, trace: () => Iterable[(Double, A)]) {
    val traces: Iterable[Iterable[(Double, A)]] = (1 to n).map(_ => trace().take(maxTraceLength).toList)

    def reduce[B](f: (Iterable[(Double, A)] => B)): Iterable[B] = traces.map(f(_))
  }
}