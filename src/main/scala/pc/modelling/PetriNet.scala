package pc.modelling

import pc.utils.MSet

trait PetriNet{

  type Transition[P]
  type PetriNet[P] = Set[Transition[P]]

  def apply[P](transitions: Transition[P]*): PetriNet[P] =
    transitions.toSet

  def toPartialFunction[P](pn: PetriNet[P]): PartialFunction[MSet[P],Set[MSet[P]]]

  def toSystem[P](pn: PetriNet[P]): System[MSet[P]] =
    System.ofFunction( toPartialFunction(pn))
}

object PetriNet extends PetriNet {
  // pre-conditions, effects, inhibition

  override type Transition[P] = (MSet[P],MSet[P],MSet[P])

  override def toPartialFunction[P](pn: PetriNet[P]): PartialFunction[MSet[P],Set[MSet[P]]] =
    {case m => for ((cond,eff,inh)<-pn;
                    if (m disjoined inh);
                    out <- m extract cond) yield out union eff }

  // Syntactic sugar to write transitions as:  MSet(a,b,c) ~~> MSet(d,e)
  implicit final class LeftTransitionRelation[P](private val self: MSet[P]){
    def ~~> (y: MSet[P]): Tuple3[MSet[P], MSet[P], MSet[P]] = Tuple3(self, y, MSet[P]())
  }
  // Syntactic sugar to write transitions as:  MSet(a,b,c) ~~> MSet(d,e) ^^^ MSet(f)
  implicit final class RightTransitionRelation[P](
    private val self: Tuple3[MSet[P],MSet[P],MSet[P]]
  ){
    def ^^^ (z: MSet[P]): Tuple3[MSet[P], MSet[P],MSet[P]] = Tuple3(self._1, self._2, z)
  }
}

trait Priority extends PetriNet {
  override type Transition[P] =(MSet[P], Int, MSet[P], MSet[P])

  override def toPartialFunction[P](pn: PetriNet[P]): PartialFunction[MSet[P],Set[MSet[P]]] =
  {case m =>
    for ((cond, p, eff,inh) <- pn;
         if m disjoined inh;
         if !pn.exists(tr => m.matches(tr._1) && tr._2 > p); //se non esiste una transizione con più alta priorità
         out <- m extract cond
         ) yield out union eff
     }
}

object PriorityPetriNet extends PetriNet with Priority
