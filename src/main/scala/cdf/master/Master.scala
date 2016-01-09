package cdf.master

import java.util.concurrent.atomic.AtomicLong

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cdf.finder.download.Downloader

object Master {
  case class Query(value: String)

  def props: Props = {
    Props[DefaultMaster]
  }
}

trait MasterComponent {
  val downloader: ActorRef
}

class Master extends Actor with ActorLogging {
  this: MasterComponent =>

  val seqNumber = new AtomicLong()

  override def receive: Receive = {
    case Master.Query(query) =>
      val coordinatorId = seqNumber.incrementAndGet
      context.actorOf(Coordinator.props(query, self, downloader), s"coordinator$coordinatorId")
    case matchResult: Coordinator.MatchResult =>
      log.info("Match result {}", matchResult)
  }
}

class DefaultMaster extends Master with MasterComponent {
  val downloader = context.actorOf(Downloader.props, "downloader")
}
