package actor.child02

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object DistributedWordCountSolution extends App{
  // distributed word counting


  object WordCounterMaster{
     case class Initialize(nChildren: Int )

    case class WordCountTask(id : Int, text : String )
    case class WordCountReply(id : Int , count : Int )
  }
  class WordCounterMaster extends Actor {
    import WordCounterMaster._
    override def receive: Receive = {
      case Initialize(nChildren) =>
        println("[master] initializing ... ")
        val childrenRefs = for ( i <- 1 to nChildren ) yield context.actorOf(Props[WordCounterWorker], s"wcw_$i")
        context.become(withChildren(childrenRefs, 0 , 0 , Map()))

    }
    def withChildren(childrenRefs : Seq[ActorRef], currChildIndex : Int, currTaskId : Int , requestMap : Map[Int,ActorRef] ):Receive = {
      case text : String =>
        println(s"[master] I have received: $text - I will send it to child $currChildIndex")
        val originalSender = sender()
        val task = WordCountTask(currTaskId, text )
        val childRef = childrenRefs(currChildIndex)
        childRef ! task
        val nextChildIndex = (currChildIndex + 1) % childrenRefs.length
        val newTaskId = currTaskId + 1
        val newRequestMap = requestMap + (currTaskId -> originalSender)
        context.become(withChildren(childrenRefs,nextChildIndex, newTaskId, newRequestMap) )
      case WordCountReply(id, count) =>
        println(s"[master] I have received a reply for task id $id with $count ")
        val originalSender = requestMap(id)
        originalSender ! count
        context.become(withChildren(childrenRefs , currChildIndex, currTaskId, requestMap - id)  )
        // because of this
      //    requester -> wcm -> wcw
      //    requester <- wcm <-
      // we should send back our `count` to original requester
      // is it sender() ? - no !!! - sender() will be child

    }
  }

  class WordCounterWorker extends Actor {
    import WordCounterMaster._
    override def receive: Receive = {

      case WordCountTask(id, text) =>
        println(s"${self.path} I have received task $id with $text ")
        sender() ! WordCountReply(id, text.split(" ").length)

    }
  }

  class TestActor extends Actor {
    import WordCounterMaster._
    override def receive: Receive = {
      case "go" =>
        val master = context.actorOf(Props[WordCounterMaster], "master")
        master ! Initialize(3)
        val texts = List("I love akka" , "Scala is super dope", "yes", "me too")
        texts.foreach(text => master ! text )
      case count : Int =>
        println(s"[test actor ] I received a reply: $count")
    }
  }

  val system = ActorSystem("roundRobinWordCountDistributed")
  val testActor = system.actorOf(Props[TestActor],"testActor")

  testActor ! "go"


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
