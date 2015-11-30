package cdf.hello

import akka.actor.{ActorRef, Actor, Props}

object HelloWorld {
  def props(greeter: ActorRef): Props = {
    Props(new HelloWorld(greeter))
  }
}

class HelloWorld(val greeterOption: Option[ActorRef]) extends Actor {

  def this() = {
    this(None)
  }

  def this(greeter: ActorRef) {
    this(Some(greeter))
  }

  override def preStart(): Unit = {
    val greeter = greeterOption getOrElse context.actorOf(Props[Greeter])
    greeter ! Greeter.Greet
  }

  def receive: Receive = {
    case Greeter.Done => context.stop(self)
  }
}
