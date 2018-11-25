package actor.p14changingActorBehavior3

import actor.Counter.Counter.{Decrement, Increment, Print}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object StatelessCounterDemo extends App {
  // TERM:  DOMAIN of the counter
  object StatelessCounterActor { //TERM : companion object for out actor
  case object Increment
    case object Decrement
    case object Print
  }
  class StatelessCounterActor extends Actor {
    var count = 0
    override def receive: Receive = {
      case Increment => count += 1
      case Decrement => count -= 1
      case Print => println(s"[counter] My current count is $count ")
    }
  }
  val system = ActorSystem("firstActorSystem")
  val counter = system.actorOf(Props[StatelessCounterActor], "myCounter")
  (1 to 5).foreach(_ => counter ! Increment)
  (1 to 3).foreach(_ => counter ! Decrement)
  counter ! Print
  /**
    * 2 - simplified voting system
    * Martin -> 1
    * Jonas -> 1
    * Roland -> 2
    */
  case class Vote(candidate: String)
  case object VoteStatusRequest
  case class VoteMessageReply(candidate: Option[String])
  class Citizen extends Actor {
    override def receive: Receive = ???
  }
  case class AggregateVotes(citizens: Set[ActorRef])
  class VoteAggregator extends Actor {
    override def receive: Receive = ???
  }

  val alice = system.actorOf(Props[Citizen])
  val bob = system.actorOf(Props[Citizen])
  val charlie = system.actorOf(Props[Citizen])
  val daniel = system.actorOf(Props[Citizen])

  alice ! Vote("Martin")
  bob ! Vote("Jonas")
  charlie ! Vote("Roland")
  daniel ! Vote("Roland")

  val voteAggregator = system.actorOf(Props[VoteAggregator])
  voteAggregator ! AggregateVotes(Set(alice, bob, charlie , daniel))


}
