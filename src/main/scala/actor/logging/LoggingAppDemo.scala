package actor.logging

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging

object LoggingAppDemo extends App {

  class SimpleActorWithExplicitLogger extends Actor {
    // logging method #1 - explicit logging
    val logger = Logging(context.system, this )
    override def receive: Receive = {
        /*
        1 - DEBUG
        2 - INFO
        3 - WARNING/WARN
        4 - ERROR
         */
      case message =>
        logger.info(message.toString) // Log it


    }
  }
  val system = ActorSystem("LoggingDemo")
  val actor = system.actorOf(Props[SimpleActorWithExplicitLogger])

  actor ! "logging a simple message"

  // #2 - ActorLogging
  class ActorWithLogging extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
      case (a, b ) => log.info("Two things: {} and {} " , a ,b )
    }
  }

  val simpleActor = system.actorOf(Props[ActorWithLogging])
  simpleActor ! "Logging a simple message by extending the trait "
  simpleActor ! (23,42)

}
