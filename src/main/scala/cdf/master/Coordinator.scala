package cdf.master

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cdf.finder.aros.ArosFinder
import cdf.finder.matras.MatrasFinder
import cdf.finder.{Finder, SimpleFinder}
import cdf.matcher.Matcher
import cdf.offer.Offer

object Coordinator {
  case class Offers(list: Vector[Offer])

  sealed trait MatchResult
  case class SimilarityMatrix(matrix: Array[Array[Double]]) extends MatchResult

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
        matcher ! Matcher.Match(offers)
        context become waitingForMatchResult(offers)
      } else {
        context become waitingForOffers(newReceivedOffersPackets)
      }
  }

  def waitingForMatchResult(offers: Vector[Offer]): Receive = {
    case Coordinator.SimilarityMatrix(matrix) =>
      replyTo ! Master.SimilarityMatrix(offers, matrix)
      context.stop(self)
  }
}

class DefaultCoordinator(query: String, replyTo: ActorRef, downloader: ActorRef) extends Coordinator(query, replyTo) with CoordinatorComponent {
  override val finders = Vector(
    context.actorOf(SimpleFinder.props, "simpleFinder"),
    context.actorOf(MatrasFinder.props(downloader), "matrasFinder"),
    context.actorOf(ArosFinder.props(downloader), "arosFinder")
  )
  override val matcher = context.actorOf(Matcher.props, "matcher")
}
