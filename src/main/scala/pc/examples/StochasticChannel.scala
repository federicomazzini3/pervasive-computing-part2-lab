package pc.examples

import pc.modelling.CTMC

object StochasticChannel extends App {
  object state extends Enumeration {
    val IDLE,SEND,DONE,FAIL = Value
  }
  import state._
  type State = state.Value

  def stocChannel: CTMC[State] = CTMC.ofTransitions(
    (IDLE,1.0,SEND),
    (SEND,100000.0,SEND),
    (SEND,200000.0,DONE),
    (SEND,100000.0,FAIL),
    (FAIL,100000.0,IDLE),
    (DONE,1.0,DONE)
  )

  def stocChannel2: CTMC[State] = CTMC.ofTransitions(
    (IDLE,1.0,SEND),
    (SEND,2.0,SEND),
    (SEND,4.0,DONE),
    (SEND,1.0,FAIL),
    (FAIL,1.0,IDLE),
    (DONE,1.0,DONE)
  )

  // example run
  state.values.foreach(s => println(s,stocChannel.transitions(s)))
}
