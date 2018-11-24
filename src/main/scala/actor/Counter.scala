package actor

import actor.Counter.Counter.{Decrement, Increment, Print}
import akka.actor.{Actor, ActorSystem, Props}


object Counter extends App {
  // TERM:  DOMAIN of the counter
  object Counter { //TERM : companion object for out actor
  case object Increment
    case object Decrement
    case object Print
  }
  class Counter extends Actor {

    import Counter._

    var count = 0
    override def receive: Receive = {
      case Increment => count += 1
      case Decrement => count -= 1
      case Print => println(s"[counter] My current count is $count ")
    }
  }
  val system = ActorSystem("firstActorSystem")
  val counter = system.actorOf(Props[Counter], "myCounter")
  (1 to 5).foreach(_ => counter ! Increment)
  (1 to 3).foreach(_ => counter ! Decrement)
  counter ! Print
}
