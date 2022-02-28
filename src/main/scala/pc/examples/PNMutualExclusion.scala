package pc.examples

import pc.modelling.PetriNet.{LeftTransitionRelation, RightTransitionRelation, toSystem}
import pc.modelling.{PetriNet, Priority, PriorityPetriNet}
import pc.utils.MSet

object PNMutualExclusion extends App {

  object place extends Enumeration {
    val N,T,C = Value
  }
  type Place = place.Value
  import place._


  // DSL-like specification of A Petri Net
  def mutualExclusionSystemDSL() = toSystem(PetriNet[Place](
    MSet(N) ~~> MSet(T),
    MSet(T) ~~> MSet(C) ^^^ MSet(C),
    MSet(C) ~~> MSet())
  )

  // DSL-like specification of A Petri Net
  def mutualExclusionSystem() = toSystem(PetriNet[Place](
    (MSet(N), MSet(T), MSet()),
    (MSet(T), MSet(C), MSet(C)),
    (MSet(C), MSet(), MSet()))
  )

  def prioritySystem() = PriorityPetriNet.toSystem(PriorityPetriNet[Place](
    (MSet(N), 3, MSet(T), MSet()),
    (MSet(T), 2, MSet(C), MSet(C)),
    (MSet(C), 1, MSet(), MSet()))
  )

  // example usage
  println(mutualExclusionSystemDSL().paths(MSet(N,N),7).toList.mkString("\n") + "\n")
  println(mutualExclusionSystem().paths(MSet(N,N),7).toList.mkString("\n") + "\n")
  println(prioritySystem().paths(MSet(N,N),7).toList.mkString("\n") + "\n")
}
