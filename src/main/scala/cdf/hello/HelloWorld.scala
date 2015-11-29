package cdf.hello

import akka.actor.{ActorRef, Actor, Props}

object HelloWorld {
  def props(greeter: ActorRef): Props = {
    Props(new HelloWorld(greeter))
  }
}

class HelloWorld(val greeter: ActorRef) extends Actor {

  override def preStart(): Unit = {
    greeter ! Greeter.Greet
  }

  def receive: Receive = {
    case Greeter.Done => context.stop(self)
  }
}
