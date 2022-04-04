package pc.examples

import org.scalatest.{FlatSpec, FunSuite, stats}
import pc.examples.PNReadersWriters.place._
import pc.modelling.SystemVerifies.PathsExtension
import pc.modelling._
import pc.utils.MSet

class PNReadersWritersTest extends FlatSpec {

  val PNRW = PNReadersWriters.readersWritersSystem()
  val PNRWReadSurely = PNReadersWriters.readersWritersSystemReadSurely()
  val PNRWWritePriority = PNReadersWriters.readersWritersSystemWritePriority()

  "PN for readers and writers" should "avoid more than 1 writer at the time and no reader" in {
    //without API
   PNRW.paths(MSet(START, START, START, PERMIT), 15)
     .foreach(path => path
       .foreach(state => ! (state matches MSet(WRITE, WRITE)) && //only one writer concurrently
                         ! (state matches MSet(WRITE, READ)) //no reader allowed when writer writes
   ))

    //with API
    PNRW.paths(MSet(START, START, START, PERMIT),  15) checkSafety {
      state => !(state matches MSet(WRITE, WRITE)) && //only one writer concurrently
               ! (state matches MSet(WRITE, READ))  //no reader allowed when writer writes
    }
  }

  //check liveness property
  //la proprietà non è verificata perchè nel sistema non vi è fairness
  "PN for readers and writers" should "permit to read at some point" in {
    PNRW.paths(MSet(ASKWRITE,ASKREAD,PERMIT),  10) checkLiveness  {
      state => state matches MSet(READ)
    }
  }

  //la proprietà è verificata
  "If a process says it wants to read" should "eventually (surely) does so" in {
    PNRWReadSurely.paths(MSet(START,ASKREAD,PERMIT),  10) checkLiveness  {
      state => state matches MSet(READ)
    }
  }

  "PN for readers and writers with write priority" should
    "give priority to process that want write" in {
    PNRWWritePriority.paths(MSet(ASKREAD, ASKWRITE, PERMIT), 10).foreach {
      path => path
        .sliding(2, 1).map(l => (l(0), l(1)))
        .filter(state => state._1 matches MSet(ASKWRITE, ASKREAD))
        .foreach(state => assert(!(state._2 matches MSet(ASKWRITE, READ))))
    }

    PNRWWritePriority.paths(MSet(ASKREAD, ASKWRITE, PERMIT), 10) checkLiveness {
      state => state matches MSet(WRITE)
    }
  }
}
