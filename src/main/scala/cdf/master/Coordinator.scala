package cdf.master

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cdf.finder.Finder
import cdf.finder.aros.ArosFinder
import cdf.finder.empik.EmpikFinder
import cdf.finder.kw.KWFinder
import cdf.finder.matras.MatrasFinder
import cdf.matcher.Matcher
import cdf.offer.Offer

object Coordinator {
  case class Offers(list: Vector[Offer])

  sealed trait MatchResult
  case class SimilarityMatrix(matrix: Array[Array[Double]]) extends MatchResult
  case class OffersGroups(groups: Vector[Vector[Offer]]) extends MatchResult

  def props(query: String, replyTo: ActorRef, downloader: ActorRef): Props = {
    Props(classOf[DefaultCoordinator], query, replyTo, downloader)
  }
}

trait CoordinatorComponent {
  val finders: Vector[ActorRef]
  val matcher: ActorRef
}

class Coordinator(val query: String, replyTo: ActorRef) extends Actor with ActorLogging {
  this: CoordinatorComponent =>

  override def preStart(): Unit = {
    finders.foreach(_ ! Finder.Find(query))
  }

  override def receive: Receive = {
    waitingForOffers(Vector.empty)
  }

  def waitingForOffers(receivedOffersPackets: Vector[Vector[Offer]]): Receive = {
    case Coordinator.Offers(offers) =>
      log.info("Received offers {}", offers)
      val newReceivedOffersPackets = receivedOffersPackets :+ offers
      if (newReceivedOffersPackets.size == finders.size) {
        val offers = newReceivedOffersPackets.flatten
        logOffers(offers)
        matcher ! Matcher.Match(offers)
        context become waitingForMatchResult(offers)
      } else {
        context become waitingForOffers(newReceivedOffersPackets)
      }
  }

  private def logOffers(offers: Vector[Offer]): Unit = {
    val readableOffers = offers
      .zipWithIndex
      .map(_.swap)
      .mkString("\n")
    log.info("All received offers are\n{}\n", readableOffers)
  }

  def waitingForMatchResult(offers: Vector[Offer]): Receive = {
    case Coordinator.OffersGroups(groups) =>
      replyTo ! Master.OffersGroups(groups)
      context.stop(self)
  }
}

class DefaultCoordinator(query: String, replyTo: ActorRef, downloader: ActorRef) extends Coordinator(query, replyTo) with CoordinatorComponent {
  override val finders = Vector(
    context.actorOf(MatrasFinder.props(downloader), "matrasFinder"),
    context.actorOf(ArosFinder.props(downloader), "arosFinder"),
    context.actorOf(EmpikFinder.props(downloader), "empikFinder"),
    context.actorOf(KWFinder.props(downloader), "kwFinder")
  )
  override val matcher = context.actorOf(Matcher.props, "matcher")
}
