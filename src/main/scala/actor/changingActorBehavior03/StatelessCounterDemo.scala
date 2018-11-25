package actor.changingActorBehavior03

import actor.Counter.Counter.{Decrement, Increment, Print}
import akka.actor.{Actor, ActorSystem, Props}

object StatelessCounterDemo extends App {
  // TERM:  DOMAIN of the counter
  object StatelessCounterActor { //TERM : companion object for out actor
  case object Increment
    case object Decrement
    case object Print
  }
  class StatelessCounterActor extends Actor {
    var count = 0
    def countReceive(currentCount: Int): Receive = {
      case Increment =>
        println(s"[ $currentCount ] incrementing")
        context.become(countReceive(currentCount + 1 ))
      case Decrement =>
        println(s"[ $currentCount ]  decrementing ")
        context.become(countReceive(currentCount - 1 ))
      case Print => println(s"[counter] my current count is $currentCount")
    }
    override def receive: Receive = countReceive(0)
    


  }
  val system = ActorSystem("firstActorSystem")
  val counter = system.actorOf(Props[StatelessCounterActor], "myCounter")
  (1 to 5).foreach(_ => counter ! Increment)
  (1 to 3).foreach(_ => counter ! Decrement)
  counter ! Print



}
