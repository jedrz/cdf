package cdf.hello

import akka.testkit.TestProbe
import cdf.ActorSpec

class HelloWorldTest extends ActorSpec(classOf[HelloWorldTest]) {

  val greeterProbe = TestProbe()
  val helloWorld = system.actorOf(HelloWorld.props(greeterProbe.testActor))

  it should "send greet on start" in {
    greeterProbe.expectMsg(Greeter.Greet)
  }

  it should "stop itself when done received" in {
    watch(helloWorld)

    greeterProbe.send(helloWorld, Greeter.Done)

    expectTerminated(helloWorld)
  }
}
