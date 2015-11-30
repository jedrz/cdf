package cdf.hello

import akka.actor.{ActorLogging, Actor}

object Greeter {
  case object Greet
  case object Done
}

class Greeter extends Actor with ActorLogging {
  def receive: Receive = {
    case Greeter.Greet =>
      log.info("Received greet from {}", sender)
      sender ! Greeter.Done
  }
}
