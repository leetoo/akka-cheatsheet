package actor.configuration

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object SomeAkkaConfig extends App {
  class SimpleLoggingActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }
  /**
    * 1 - inline configuration
    */
  val configString =
    """|
       | akka {
       |  loglevel = "DEBUG"
       | }
    """.stripMargin
  val config = ConfigFactory.parseString(configString)
  val system = ActorSystem("ConfigurationDemo", ConfigFactory.load(config))
  val actor = system.actorOf(Props[SimpleLoggingActor])
  actor ! " A message to remember "
  /**
    * 2 conf way( config file )
    * - folder resources plus create file in it applicaiton.conf
    * -> akka will be look at this conf file by default
    */
  val defaultConfigFileSystem = ActorSystem("DefaultConfigFileDemo")
  val defaultConfigActor = defaultConfigFileSystem.actorOf(Props[SimpleLoggingActor])
  actor ! "remember me "
  /**
    * if you have a big sistem with different akka system you probably wanna configure those system in
    * different ways how to do this ?
    *
    */
  /**
    * 3 - separate configuration in the same file
    */
  val specialConfig = ConfigFactory.load().getConfig("mySpecialConfig")
  val specialConfigSystem = ActorSystem("SpecialConfigDemo", specialConfig)
  val specialConfigActor = specialConfigSystem.actorOf(Props[SimpleLoggingActor])
  actor ! "Remember me, I am special"
  /**
    * 4 - separate config in another file
    */

  val separateConfig = ConfigFactory.load("secretFolder/secretConfiguration.conf")
  println(s"separate config log level ${separateConfig.getString("akka.loglevel")}")
  /**
    * 5 - different file formats
    * JSON, Properties
    */

  val jsonConfig = ConfigFactory.load("json/jsonConfig.json")
  println(s"json config: ${jsonConfig.getString("aJsonProperty")}")
  println(s"json config: ${jsonConfig.getString("akka.loglevel")}")


  val propsConfig  = ConfigFactory.load("props/propsConfiguration.properties")
  println(s"properties config : ${propsConfig.getString("mySimpleProperty")}")
  println(s"property config : ${propsConfig.getString("akka.loglevel")}")



}
