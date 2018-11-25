package actor.changingActorBehavior01

import actor.changingActorBehavior01.StatelessActorBehaviorChange.StatelessFussyKid.{KidAccept, KidReject}
import actor.changingActorBehavior01.StatelessActorBehaviorChange.Mom.MomStart
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * mother feed the child with different food
  * 1. if mother gives chocolate it makes child happy
  * 2. if mother gives vegetables it makes child sad
  *
  *  *** Stateful version ***
  */
object StatelessActorBehaviorChange extends App {

  object StatelessFussyKid{
    case object KidAccept
    case object KidReject
    val HAPPY = "happy"
    val SAD = "sad"
  }
  class StatelessFussyKid extends Actor {
    import StatelessFussyKid._
    import Mom._


      override def receive: Receive = happyReceive

    def happyReceive : Receive = {
      case Food(VEGETABLE) => context.become(sadReceive) // change my receive handler to sadReceive
      case Food(CHOCOLATE) =>
      case Ask(_) => sender() ! KidAccept
    }

    def sadReceive : Receive = {
      case Food(VEGETABLE) => // stay sad
      case Food(CHOCOLATE) => context.become(happyReceive)// change my receive handler to happyReceive
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
  val statelessFussyKid = system.actorOf(Props[StatelessFussyKid], "myFussyKid")
  val mom = system.actorOf(Props[Mom], "myMom")

  mom ! MomStart(statelessFussyKid)

  /* - process of causality
     - kid receive Food(veg) -> kid will change the handler to sadReceive
     - kid receive Ask(play?) -> kid reply with the sadReceive handler =>
     mom receive KidReject


   */
}
