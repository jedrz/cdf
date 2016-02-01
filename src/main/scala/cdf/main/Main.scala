package cdf.main

import akka.actor.ActorSystem
import cdf.master.Master

object Main {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem()
    val master = system.actorOf(Master.props, "master")

//    master ! Master.Query("ubik")
//    master ! Master.Query("nocny patrol")
//    master ! Master.Query("pustynna włócznia")

    args.foreach(arg => master ! Master.Query(arg))
  }
}
