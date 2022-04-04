# Lab 02

## The verifier
Code and do some analysis on the Readers & Writers Petri Net. Add a test to check that in no path long at most 100 states mutual exclusion fails (no more than 1 writer, and no readers and writers together). Can you extract a small API for representing safety properties?

[API](https://github.com/federicomazzini3/pervasive-computing-part2-lab/blob/02-solutions/src/main/scala/pc/modelling/System.scala)

[Readers & Writers Model](https://github.com/federicomazzini3/pervasive-computing-part2-lab/blob/02-solutions/src/main/scala/pc/examples/PNReadersWriters.scala)

[Tests](https://github.com/federicomazzini3/pervasive-computing-part2-lab/blob/02-solutions/src/test/scala/pc/examples/PNReadersWritersTest.scala)

## The designer
Code and do some analysis on a variation of the Readers & Writers Petri Net: it should be the minimal variation you can think of, such that if a process says it wants to read, it eventually (surely) does so. How would you show evidence that your design is right?

[Readers & Writers variation](https://github.com/federicomazzini3/pervasive-computing-part2-lab/blob/02-solutions/src/main/scala/pc/examples/PNReadersWriters.scala)
[Tests](https://github.com/federicomazzini3/pervasive-computing-part2-lab/blob/02-solutions/src/test/scala/pc/examples/PNReadersWritersTest.scala)

## The simulator
Take the communication channel CTMC example in StochasticChannelSimulation. Compute the average time at which communication is done—across n runs. Compute the relative amount of time (0% to 100%) that the system is in fail state until communication is done—across n runs. Extract an API for nicely performing similar checks.

[API and checks](https://github.com/federicomazzini3/pervasive-computing-part2-lab/blob/02-solutions/src/main/scala/pc/examples/StochasticChannelSimulation.scala)

## The artist
Create a variation/extension of PetriNet design, with priorities: each transition is given a numerical priority, and no transition can fire if one with higher priority can fire. Show an example that your pretty new “abstraction” works.

[Petri Net with priority](https://github.com/federicomazzini3/pervasive-computing-part2-lab/blob/02-solutions/src/main/scala/pc/modelling/PetriNet.scala)

[Petri Net model and usage](https://github.com/federicomazzini3/pervasive-computing-part2-lab/blob/02-solutions/src/main/scala/pc/examples/PNPriorityUsage.scala)


