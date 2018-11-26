package actor.child02

import akka.actor.Actor

object ChildActorApp extends App{
  // distributed word counting


  object WordCounterMaster{
     case class Initialize(nChildren: Int )

    case class WordCountTask(/*todo */text : String )
    case class WordCountReply(/*todo*/count : Int )
  }
  class WordCounterMaster extends Actor {
    override def receive: Receive = ???
  }

  class WordCounterWorker extends Actor {
    override def receive: Receive = ???
  }

    /*
    create WordCounterMaster
    send Initialize(10) to wordCounterMaster
    send "Akka is awesome" to wordCounterMaster
    wcm will send a WordCounterTask("...") to one of its children
    child replies with a WordCountReply(3) to the master
    master replies with 3 to the sender.

    requester -> wcm -> wcw
    requester <- wcm <-
     */
  // to achieve load balancing between children the simple and popular approach is to use
  // approach :  round robin logic
  // for example if we have 5 children and 7 tasks
  // we wait when all finish the tasks and then give the remaining 2 tasks first two children
  // 1,2,3,4,5 and 7 tasks
  // 1,2 and 2 tasks
}
