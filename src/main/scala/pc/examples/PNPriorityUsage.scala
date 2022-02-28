package pc.examples

import pc.modelling.{PetriNet, PriorityPetriNet}
import pc.utils.MSet

object PNPriorityUsage extends App{
  object place extends Enumeration {
    val A,B,C,D = Value
  }

  type Place = place.Value
  import place._

  def system() = PetriNet.toSystem(PetriNet[Place](
    (MSet(A), MSet(B), MSet()),
    (MSet(A), MSet(C), MSet()),
    (MSet(B), MSet(D), MSet()),
    (MSet(C), MSet(D), MSet()),
    (MSet(D), MSet(), MSet()))
  )

  def prioritySystem() = PriorityPetriNet.toSystem(PriorityPetriNet[Place](
    (MSet(A), 4, MSet(B), MSet()),
    (MSet(A), 4, MSet(C), MSet()),
    (MSet(B), 1, MSet(D), MSet()),
    (MSet(C), 2, MSet(D), MSet()),
    (MSet(D), 0, MSet(), MSet()))
  )

  println("Classic \n" + system().paths(MSet(A,A), depth = 7).toList.mkString(("\n")) + "\n")
  println("With priority \n" + prioritySystem().paths(MSet(A,A), depth = 7).toList.mkString(("\n")))
}
