package cdf.main

import akka.actor.ActorSystem
import cdf.master.Master

object Main {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem()
    system.actorOf(Master.props("query"))
  }
}
