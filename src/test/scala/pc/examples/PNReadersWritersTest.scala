package pc.examples

import org.scalatest.{FlatSpec, FunSuite}
import pc.examples.PNReadersWriters.place._
import pc.utils.MSet

class PNReadersWritersTest extends FlatSpec {

  val PNRW = PNReadersWriters.readersWritersSystem()
  val PNRWWritePriority = PNReadersWriters.readersWritersSystemWritePriority()

  "PN for readers and writers" should "avoid more than 1 writer at the time and no reader" in {
    for(
      path <- PNRW.paths(MSet(START, START, START, START, START, PERMIT), 100).take(1000);
      state <- path
    ) yield assert(
        ! (state matches MSet(WRITE, WRITE)) && //only one writer concurrently
          ! (state matches MSet(WRITE, READ)) //no reader allowed when writer writes
      )
  }

  "PN for readers and writers with write priority" should "give priority to process that want write" in {
    for(
      path <- PNRWWritePriority.paths(MSet(ASKREAD, ASKWRITE, PERMIT), 100).take(1000);
      state <- path.sliding(2,1)
    ) yield assert(
      (state(0) matches MSet(ASKWRITE, ASKREAD)) &&
        ! (state(1) matches MSet(ASKWRITE, READ))
    )
  }
}
