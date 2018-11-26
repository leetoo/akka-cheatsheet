package actor.child


import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActors extends App {
  object Parent {
    case class CreateChild(name: String)
    case class TellChild(message: String)
  }
  // Actors can create other actors
  class Parent extends Actor {
    import Parent._

    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"${self.path} creating child ")
        // create a new actor right HERE
        val childRef = context.actorOf(Props[Child], name)
        context.become(withChild(childRef))
    }
    def withChild(childRef: ActorRef): Receive = {
      case TellChild(message) => childRef forward message
    }
  }
  class Child extends Actor {
    override def receive: Receive = {
      case message => println(s"${self.path} I got: $message ")
    }
  }
  import Parent._

  val system = ActorSystem("ParentChildDemo")
  val parent = system.actorOf(Props[Parent], "parent")
  parent ! CreateChild("child")
  parent ! TellChild("hey Kid!")
  // actor hierarchies
  // parent -> child -> grandChild
  //        -> child2 ->
  // why create "parent" ? no
  /*
      Guardian actors ( top - level )
      every akka actor system has 3 guardian actors
      - /system = system guardian
      - /user = user-level guardian ... for example akka://ParentChildDemo/user/parent creating child
                                                                    look here  -> ||
      - / = the root guardian - sear on the very top ... if this guy throw exception all system die out


   */
  /** let's discuss the feature that akka use the find the actor bypath
    * it calls Actor selection
    *
    */
  val childSelection = system.actorSelection("/user/parent/child")
  childSelection ! "I found you! " // output : akka://ParentChildDemo/user/parent/child I got: I found you!
  val childSelectionError = system.actorSelection("/user/parent/child2")
  childSelectionError ! "I found you! "
  /**
    * output : [INFO] [11/26/2018 15:30:36.959] [ParentChildDemo-akka.actor.default-dispatcher-4]
    * [akka://ParentChildDemo/user/parent/child2] Message [java.lang.String] without sender to
    * Actor[akka://ParentChildDemo/user/parent/child2] was not delivered. [1] dead letters encountered.
    * If this is not an expected behavior, then [Actor[akka://ParentChildDemo/user/parent/child2]]
    * may have terminated unexpectedly, This logging can be turned off or adjusted with configuration
    * settings 'akka.log-dead-letters' and 'akka.log-dead-letters-during-shutdown'.
    */

}
