package cdf.master

import java.util.concurrent.atomic.AtomicLong

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cdf.finder.download.Downloader
import cdf.offer.Offer

object Master {
  case class Query(value: String)

  sealed trait MatchResult
  case class SimilarityMatrix(offers: Vector[Offer], matrix: Array[Array[Double]]) extends MatchResult

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
    case Master.SimilarityMatrix(offers, matrix) =>
      val readableMatrix = matrix
        .map(row => row.map("%.3f".format(_)).mkString(" "))
        .mkString("\n")
      log.info("Similarity matrix\n{}\nfor offers {}", readableMatrix, offers)
  }
}

class DefaultMaster extends Master with MasterComponent {
  val downloader = context.actorOf(Downloader.props, "downloader")
}
