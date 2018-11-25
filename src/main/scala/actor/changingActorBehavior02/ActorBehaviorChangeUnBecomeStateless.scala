package actor.changingActorBehavior02

import actor.changingActorBehavior02.ActorBehaviorChangeUnBecomeStateless.MomUnbecome.MomStartStatelessUnbecome
import actor.changingActorBehavior02.ActorBehaviorChangeUnBecomeStateless.StatelessFussyKid.{KidAccept, KidReject}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * mother feed the child with different food
  * 1. if mother gives chocolate it makes child happy
  * 2. if mother gives vegetables it makes child sad
  *
  * *** Stateful version ***
  */
object ActorBehaviorChangeUnBecomeStateless extends App {
  object StatelessFussyKid {
    case object KidAccept
    case object KidReject
    val HAPPY = "happy"
    val SAD = "sad"
  }
  class StatelessFussyKidUnbecome extends Actor {
    import MomUnbecome._

    override def receive: Receive = happyReceive
    def happyReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false) // change my receive handler to sadReceive
      case Food(CHOCOLATE) =>
      case Ask(_) => sender() ! KidAccept
    }
    def sadReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false)
      case Food(CHOCOLATE) => context.unbecome()
      case Ask(_) => sender() ! KidReject
    }
  }
  object MomUnbecome {
    case class MomStartStatelessUnbecome(kidRef: ActorRef)
    case class Food(food: String)
    case class Ask(message: String) // do u wanna play ?
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }
  class MomUnbecome extends Actor {
    import MomUnbecome._

    override def receive: Receive = {
      case MomStartStatelessUnbecome(kidRef) =>
        // test our interaction
        kidRef ! Food(VEGETABLE)
        kidRef ! Food(VEGETABLE)
        kidRef ! Food(CHOCOLATE)
        //kidRef ! Food(CHOCOLATE)
        kidRef ! Ask("do u wanna play ? ")
      case KidAccept => println("Yay, my kid is happy!")
      case KidReject => println("My kid is sad , but at least he's health!")
    }
  }
  val system = ActorSystem("changingActorBehaviorDemo")
  val statelessFussyKid = system.actorOf(Props[StatelessFussyKidUnbecome], "myStatelessFussyKidUnbecome")
  val mom = system.actorOf(Props[MomUnbecome], "myMomUnbecome")
  mom ! MomStartStatelessUnbecome(statelessFussyKid)
  /* - process of causality
     - kid receive Food(veg) -> kid will change the handler to sadReceive
     - kid receive Ask(play?) -> kid reply with the sadReceive handler =>
     mom receive KidReject
   */
  /*
  in old version ( without false parameter in context. become
  Food(veg) -> message handler turn to sadReceive
  Food(Chocolate) -> become happyReceive
   */
  /*
    in new version ( with false parameter in context. become
    Food(veg) -> stack.push(sadReceive)
    Food(Chocolate) ->

    Stack :
    1. sadReceive
    2. happyReceive
      ---
       in new version ( with false parameter in context. become
    Food(veg) ->
    Food(Chocolate) -> stack.push(happyReceive)

    Stack :
    1. happyReceive
    2. sadReceive

    if receive two messages almost simultaneously (Chocolate, Veg) the stack will be look like

       Stack :
    1. happyReceive
    2. sadReceive
    3. happyReceive


  */
  /*
  new behavior
  Food(veg)
  Food(veg)
  Food(choco)
   */
}
