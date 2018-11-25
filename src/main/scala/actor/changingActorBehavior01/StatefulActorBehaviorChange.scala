package actor.changingActorBehavior01

import actor.changingActorBehavior01.StatefulActorBehaviorChange.Mom.MomStartFirst
import actor.changingActorBehavior01.StatefulActorBehaviorChange.StatefulFussyKid.{KidAccept, KidReject}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * mother feed the child with different food
  * 1. if mother gives chocolate it makes child happy
  * 2. if mother gives vegetables it makes child sad
  */
object StatefulActorBehaviorChange extends App {
  object StatefulFussyKid {
    case object KidAccept
    case object KidReject
    val HAPPY = "happy"
    val SAD = "sad"
  }
  class StatefulFussyKid extends Actor {
    import Mom._
    import StatefulFussyKid._

    // internal state of the kid
    var state = HAPPY
    override def receive: Receive = {
      case Food(VEGETABLE) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(_) =>
        if (state == HAPPY) sender() ! KidAccept
        else sender() ! KidReject
    }
  }
  object Mom {
    case class MomStartFirst(kidRef: ActorRef)
    case class Food(food: String)
    case class Ask(message: String) // do u wanna play ?
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }
  class Mom extends Actor {
    import actor.changingActorBehavior01.StatefulActorBehaviorChange.Mom._
    override def receive: Receive = {
      case MomStartFirst(kidRef) =>
        // test our interaction
        kidRef ! Food(VEGETABLE)
        kidRef ! Ask("do u wanna play ? ")
      case KidAccept => println("Yay, my kid is happy!")
      case KidReject => println("My kid is sad , but at least he's health!")
    }
  }
  val system = ActorSystem("changingActorBehaviorDemo")
  val fussyKid = system.actorOf(Props[StatefulFussyKid], "myFussyKid")
  val mom = system.actorOf(Props[Mom], "myMom")
  mom ! MomStartFirst(fussyKid)
}


