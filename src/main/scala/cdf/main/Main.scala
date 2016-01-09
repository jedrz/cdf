package cdf.main

import akka.actor.ActorSystem
import cdf.master.Coordinator

object Main {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem()
    system.actorOf(Coordinator.props("ubik"))
  }
}
