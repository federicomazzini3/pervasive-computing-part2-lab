package lab.demo

import it.unibo.scafi.incarnations.BasicAbstractIncarnation
import it.unibo.scafi.simulation.s2.frontend.incarnation.scafi.bridge.ExportEvaluation.EXPORT_EVALUATION
import it.unibo.scafi.simulation.s2.frontend.incarnation.scafi.bridge.SimulationInfo
import it.unibo.scafi.simulation.s2.frontend.incarnation.scafi.configuration.{ScafiProgramBuilder, ScafiWorldInformation}
import it.unibo.scafi.simulation.s2.frontend.incarnation.scafi.world.ScafiWorldInitializer.Random
import it.unibo.scafi.simulation.s2.frontend.incarnation.scafi.bridge.ScafiWorldIncarnation.EXPORT
import it.unibo.scafi.simulation.s2.frontend.view.{ViewSetting, WindowConfiguration}
import lab.gui.patch.RadiusLikeSimulation
import it.unibo.scafi.space.graphics2D.BasicShape2D.Circle

object Incarnation extends BasicAbstractIncarnation
import lab.demo.Incarnation._ //import all stuff from an incarnation

object Simulation extends App {

  val formatter_evaluation: EXPORT_EVALUATION[Any] = (e : EXPORT) => formatter(e.root[Any]())

  val formatter: Any => Any = (e) => e match {
    case b: Boolean => b
    case (a,b) => (formatter(a),formatter(b))
    case (a,b,c) => (formatter(a),formatter(b),formatter(c))
    case (a,b,c,d) => (formatter(a),formatter(b),formatter(c),formatter(d))
    case l:Iterable[_] => l.map(formatter(_)).toString
    case i: java.lang.Number if (i.doubleValue()>100000) => "Inf"
    case i: java.lang.Number if (-i.doubleValue()>100000) => "-Inf"
    case i: java.lang.Double => f"${i.doubleValue()}%1.2f"
    case x => x.toString
  }

  val programClass = classOf[PartitionExample]
  val nodes = 100
  val neighbourRange = 200
  val (width, height) = (1080, 720)
  ViewSetting.windowConfiguration = WindowConfiguration(width, height)
  ScafiProgramBuilder (
    Random(nodes, width, height),
    SimulationInfo(programClass,exportEvaluations = List(formatter_evaluation)),
    RadiusLikeSimulation(neighbourRange),
    ScafiWorldInformation(shape = Some(Circle(5,5))),
    neighbourRender = true,
  ).launch()
}

abstract class AggregateProgramSkeleton extends AggregateProgram with StandardSensors {
  def sense1 = sense[Boolean]("sens1")
  def sense2 = sense[Boolean]("sens2")
  def sense3 = sense[Boolean]("sens3")
  def boolToInt(b: Boolean) = mux(b){1}{0}
}

class Main extends AggregateProgramSkeleton {
  override def main() = 1
}

class Main1 extends AggregateProgramSkeleton {
  override def main() = 1
}

class Main2 extends AggregateProgramSkeleton {
  override def main() = 2+3
}

class Main3 extends AggregateProgramSkeleton {
  override def main() = (10,20)
}

class Main4 extends AggregateProgramSkeleton {
  override def main() = Math.random()
}

class Main5 extends AggregateProgramSkeleton {
  override def main() = sense1
}

class Main6 extends AggregateProgramSkeleton {
  override def main() = if (sense1) 10 else 20
}

class Main7 extends AggregateProgramSkeleton {
  override def main() = mid()
}

class Main8 extends AggregateProgramSkeleton {
  override def main() = minHoodPlus(nbrRange)
}

class Main9 extends AggregateProgramSkeleton {
  override def main() = rep(0){_+1}
}

class Main10 extends AggregateProgramSkeleton {
  override def main() = rep(Math.random()){x=>x}
}

class Main11 extends AggregateProgramSkeleton {
  override def main() = rep[Double](0.0){x => x + rep(Math.random()){y=>y}}
}

class Main12 extends AggregateProgramSkeleton {
  import Builtins.Bounded.of_i

  override def main() = maxHoodPlus(boolToInt(nbr{sense1}))
}

class Main13 extends AggregateProgramSkeleton {
  override def main() = foldhoodPlus(0)(_+_){nbr{1}}
}

class Main14 extends AggregateProgramSkeleton {
  import Builtins.Bounded.of_i

  override def main() = rep(0){ x => boolToInt(sense1) max maxHoodPlus( nbr{x}) }
}

class Main15 extends AggregateProgramSkeleton {
  override def main() = rep(Double.MaxValue){ d => mux[Double](sense1){0.0}{minHoodPlus(nbr{d}+1.0)} }
}

class Main16 extends AggregateProgramSkeleton {
  override def main() = rep(Double.MaxValue){ d => mux[Double](sense1){0.0}{minHoodPlus(nbr{d}+nbrRange)} }
}

class Case9 extends AggregateProgramSkeleton {
  /**
   * where sense1 is active count from 0 to 1000 and then stay freezed at 1000, otherwise 0
   * use mux externally, see what happens with multiple clicks, and then use branch instead
   */
  override def main() = /*branch*/mux(sense1)(rep(0){x => mux(x <1000)(x+1)(x)})(0)
}

class Case12 extends AggregateProgramSkeleton {
  /**
   * gather in each node the set of neighbours’ IDs..
   * used foldhood, help type inference (it’s a Set[ID])
   */
  override def main():Set[Int] = foldhood(Set[ID]())(_ ++ _)(nbr{Set(mid())})
}

class Case8 extends AggregateProgramSkeleton {
  /**
   * have in each node the ID of the closest neighbour
   * used minHoodPlus, construct a pair of distance (nbrRange) and id (idnbrm), note minHoodPlus correctly works on pairs
   */
  override def main():(Int, ID) = minHoodPlus(nbrRange.toInt, nbr{mid()})
}

class Case14 extends AggregateProgramSkeleton {
  /**
   * gossip the maximum value of ID (type Int)
   * note a problem: it won’t correctly repair upon network changes +
   * use max and maxHoodPlus smoothly
   */
  override def main() = rep(mid()){x => mid() max maxHoodPlus(nbr(x)) }
}

class Case16 extends AggregateProgramSkeleton {
  /**
   * define a gradient that stretches distances so that where sense2 is true they become 5 times larger
   * tweak your usage of nbrRange
   */
    def gradient(src: Boolean)(stretch: Boolean)(factor: Int): Double =
      rep(Double.MaxValue){ d => mux(src){0.0}{minHoodPlus((nbr{d} + nbrRange) * mux(stretch)(factor)(1))}}

  override def main() =
    gradient(sense1)(sense2)(5)
}

class PartitionExample extends AggregateProgramSkeleton{

  import Builtins.Bounded._
  def distanceFromSource(d: (Double, ID)) = minHoodPlus(nbr{d._1} + nbrRange, nbr{d._2})

  def partition(src:Boolean, mid: ID): (Double, ID) =
    rep(Double.MaxValue, mid){ds => mux(src){(0.0, mid)}{distanceFromSource(ds)}}//._2

  override def main(): (Double, ID) = partition(sense1, mid())
}

class Channel extends AggregateProgramSkeleton {
  def gradient(src:Boolean): Double =
    rep(Double.MaxValue){d => mux[Double](src){0.0}{minHoodPlus(nbr{d} + nbrRange)}}

  def broadcast(src: Boolean, input: Double): Double = {
    //inputs: source field, input field (it was mid for partition)
    //output: the result of broadcasting input field outward sources
    rep((Double.MaxValue, input)){ dv => mux(src){(0.0, input)}{minHoodPlus((nbr{dv}._1 + nbrRange, nbr{dv}._2))}}._2
  }

  def distance(src: Boolean, dst: Boolean): Double = {
    //distance is achieved by broadcast the result of gradient at destination
    broadcast(src, gradient(dst))
  }

  def channel(src: Boolean, dst: Boolean) =
    gradient(src) + gradient(dst) <= distance(src, dst)

  def main() = channel(sense1, sense2)
}