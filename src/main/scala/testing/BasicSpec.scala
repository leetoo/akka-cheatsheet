package testing

import akka.actor.ActorSystem

class BasicSpec extends TestKit(ActorSystem()
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {



  override def afterAll = {
    TestKit.shutdownActorSystem(system )

  }
}
