package pc.examples

import pc.modelling.PetriNet
import pc.modelling.PetriNet._
import pc.utils.MSet

object PNReadersWriters extends App{

  object place extends Enumeration {
    val START, CHOICE, ASKREAD, ASKWRITE, PERMIT, READ, WRITE = Value
  }
  type Place = place.Value
  import place._

  def readersWritersSystem() = toSystem(PetriNet[Place](
    MSet(START) ~~> MSet(CHOICE),
    MSet(CHOICE) ~~> MSet(ASKREAD),
    MSet(CHOICE) ~~> MSet(ASKWRITE),
    MSet(ASKREAD, PERMIT) ~~> MSet(PERMIT, READ),
    MSet(ASKWRITE, PERMIT) ~~> MSet(WRITE) ^^^ MSet(READ),
    MSet(READ) ~~> MSet(START),
    MSet(WRITE) ~~> MSet(PERMIT, START)
  )
  )

  /** Variation of reader and writers problem such that if a process says it wants to read, it eventually (surely) does so */
  def readersWritersSystemReadSurely() = toSystem(PetriNet[Place](
    MSet(START) ~~> MSet(CHOICE) ^^^ MSet(ASKREAD),
    MSet(CHOICE) ~~> MSet(ASKREAD),
    MSet(CHOICE) ~~> MSet(ASKWRITE),
    MSet(ASKREAD, PERMIT) ~~> MSet(PERMIT, READ),
    MSet(ASKWRITE, PERMIT) ~~> MSet(WRITE) ^^^ MSet(READ),
    MSet(READ) ~~> MSet(START),
    MSet(WRITE) ~~> MSet(PERMIT, START)
  )
  )

  /** Variation of readers and writers problem in which writer processes have priority over reader processes*/
  def readersWritersSystemWritePriority() = toSystem(PetriNet[Place](
    MSet(START) ~~> MSet(CHOICE),
    MSet(CHOICE) ~~> MSet(ASKREAD),
    MSet(CHOICE) ~~> MSet(ASKWRITE),
    MSet(ASKREAD, PERMIT) ~~> MSet(PERMIT, READ) ^^^ MSet(ASKWRITE),
    MSet(ASKWRITE, PERMIT) ~~> MSet(WRITE) ^^^ MSet(READ),
    MSet(READ) ~~> MSet(START),
    MSet(WRITE) ~~> MSet(PERMIT, START)
  )
  )

  //println(readersWritersSystem().paths(MSet(START, START, PERMIT), 7).toList.mkString("\n"))
  //println(readersWritersSystemWritePriority().paths(MSet(ASKWRITE, ASKREAD, PERMIT), 7).toList.mkString("\n"))
  println(readersWritersSystemReadSurely().paths(MSet(START, START, START, PERMIT), 7).toList.mkString("\n"))
}
