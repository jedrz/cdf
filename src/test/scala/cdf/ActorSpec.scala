package cdf

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}

abstract class ActorSpec(actorSystem: ActorSystem) extends TestKit(actorSystem) with ImplicitSender with UnitSpec {

  def this(klass: Class[_]) = {
    this(ActorSystem(klass.getSimpleName))
  }

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(actorSystem)
  }

}
