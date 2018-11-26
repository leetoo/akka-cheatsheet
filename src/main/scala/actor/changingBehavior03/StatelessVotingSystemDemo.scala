package actor.changingBehavior03


import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object StatelessVotingSystemDemo extends App {
  /**
    * NOT DONE YET - not work correctly
    * 2 - simplified voting system
    * Martin -> 1
    * Jonas -> 1
    * Roland -> 2
    */
  case class Vote(candidate: String)
  case object VoteStatusRequest
  case class VoteStatusReply(candidate: Option[String])



  class Citizen extends Actor {

    override def receive: Receive = {
      case Vote(c) => context.become(voted(c))// candidate = Some(c)
      case VoteStatusRequest => sender() ! VoteStatusReply(None)
    }

    def voted(candidate : String ) : Receive ={
      case VoteStatusRequest => sender() ! VoteStatusReply( Some(candidate))
    }
  }


  case class AggregateVotes(citizens: Set[ActorRef])
  class VoteAggregator extends Actor {

    override def receive: Receive = awaitingCommand

    def awaitingCommand: Receive = {
      case AggregateVotes(citizens) =>

        citizens.foreach(citizenRef => citizenRef ! VoteStatusRequest)
        context.become(awaitingStatuses(citizens, Map()))
    }

    def awaitingStatuses(stillWaiting: Set[ActorRef], currentStats : Map[String, Int]): Receive = {
      case VoteStatusReply(None) =>
        sender() ! VoteStatusRequest // this is might end up in an infinite loop
      case VoteStatusReply(Some(candidate)) =>
        val newStillWaiting = stillWaiting - sender()
        val currentVotesOfCandidate = currentStats.getOrElse(candidate , 0 )

        val newStats = currentStats + (candidate -> (currentVotesOfCandidate + 1 ))
        if (newStillWaiting.isEmpty){
          println(s"[aggregator] poll stats: $currentStats")
        } else {
          // still need to process some statuses
          context.become(awaitingStatuses(newStillWaiting,newStats))
        }
    }
  }
  val system = ActorSystem("firstActorSystem")
  val alice = system.actorOf(Props[Citizen])
  val bob = system.actorOf(Props[Citizen])
  val charlie = system.actorOf(Props[Citizen])
  val daniel = system.actorOf(Props[Citizen])
  alice ! Vote("Martin")
  bob ! Vote("Jonas")
  charlie ! Vote("Roland")
  daniel ! Vote("Roland")
  val voteAggregator = system.actorOf(Props[VoteAggregator])
  voteAggregator ! AggregateVotes(Set(alice, bob, charlie, daniel))
}
