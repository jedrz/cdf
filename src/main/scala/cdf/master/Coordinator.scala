package cdf.master

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cdf.finder.aros.ArosFinder
import cdf.finder.matras.MatrasFinder
import cdf.finder.{Finder, SimpleFinder}
import cdf.matcher.Matcher
import cdf.offer.Offer

object Coordinator {
  case class Offers(list: List[Offer])
  case class MatchResult(groups: List[List[Offer]])

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

  def waitingForOffers(receivedOffers: Vector[List[Offer]]): Receive = {
    case Coordinator.Offers(offers) =>
      log.info("Received offers {}", offers)
      val newReceivedOffers = receivedOffers :+ offers
      if (newReceivedOffers.size == finders.size) {
        matcher ! Matcher.Match(newReceivedOffers.flatten.toList)
        context become waitingForMatchResult
      } else {
        context become waitingForOffers(newReceivedOffers)
      }
  }

  def waitingForMatchResult: Receive = {
    case matchResult: Coordinator.MatchResult =>
      replyTo ! matchResult
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
