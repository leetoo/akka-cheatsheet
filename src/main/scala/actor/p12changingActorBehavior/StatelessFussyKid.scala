package actor.p12changingActorBehavior

import actor.p12changingActorBehavior.StatefulFussyKid.FussyKid.{KidAccept, KidReject}
import actor.p12changingActorBehavior.StatefulFussyKid.Mom.MomStart
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * mother feed the child with different food
  * 1. if mother gives chocolate it makes child happy
  * 2. if mother gives vegetables it makes child sad
  *
  *  *** Stateful version ***
  */
object StatefulFussyKid extends App {

  object FussyKid{
    case object KidAccept
    case object KidReject
    val HAPPY = "happy"
    val SAD = "sad"
  }
  class FussyKid extends Actor {
    import FussyKid._
    import Mom._
    // internal state of the kid
    var state = HAPPY // <- using state !!! this is shame !
      override def receive: Receive = {
        case Food(VEGETABLE) => state = SAD
        case Food(CHOCOLATE) => state = HAPPY
        case Ask(_) =>
          if(state == HAPPY) sender() ! KidAccept
          else sender() ! KidReject
      }

    def happyReceive : Receive = {
      case Food(VEGETABLE) => // change my receive handler to sadReceive
      case Food(CHOCOLATE) =>
      case Ask(_) => sender() ! KidAccept
    }

    def sadReceive : Receive = {
      case Food(VEGETABLE) => // stay sad
      case Food(CHOCOLATE) => // change my receive handler to happyReceive
      case Ask(_) => sender() ! KidReject
    }

    }
  object Mom {
    case class MomStart(kidRef : ActorRef)
    case class Food(food: String )
    case class Ask(message :String ) // do u wanna play ?
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }
  class Mom extends Actor {
    import Mom._
    override def receive: Receive = {
      case MomStart(kidRef) =>
        // test our interaction
      kidRef ! Food(VEGETABLE)
        kidRef ! Ask("do u wanna play ? ")
      case KidAccept => println("Yay, my kid is happy!")
      case KidReject => println("My kid is sad , but at least he's health!")
    }

  }

  val system = ActorSystem("changingActorBehaviorDemo")
  val fussyKid = system.actorOf(Props[FussyKid], "myFussyKid")
  val mom = system.actorOf(Props[Mom], "myMom")

  mom ! MomStart(fussyKid)
}
