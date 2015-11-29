package cdf.hello

import akka.actor.Props
import cdf.ActorSpec

class GreeterTest extends ActorSpec(classOf[GreeterTest]) {

  val greeter = system.actorOf(Props[Greeter])

  it should "send back done message on greet" in {
    greeter ! Greeter.Greet

    expectMsg(Greeter.Done)
  }
}
