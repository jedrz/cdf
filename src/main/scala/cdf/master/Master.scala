package cdf.master

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cdf.finder.aros.ArosFinder
import cdf.finder.download.Downloader
import cdf.finder.{Finder, SimpleFinder}
import cdf.matcher.Matcher
import cdf.offer.Offer

object Master {
  case class Offers(list: List[Offer])
  case class MatchResult(groups: List[List[Offer]])

  def props(query: String): Props = {
    Props(classOf[DefaultMaster], query)
  }
}

trait MasterComponent {
  val finders: Vector[ActorRef]
  val matcher: ActorRef
}

class Master(val query: String) extends Actor with ActorLogging {
  this: MasterComponent =>

  var receivedOffers: Vector[List[Offer]] = Vector.empty

  override def preStart(): Unit = {
    finders.foreach(_ ! Finder.Find(query))
  }

  override def receive: Receive = {
    case Master.Offers(offers) =>
      log.info("Received offers {}", offers)
      receivedOffers = receivedOffers :+ offers
      if (receivedOffers.size == finders.size) {
        matcher ! Matcher.Match(receivedOffers.flatten.toList)
      }
    case matchResult: Master.MatchResult => log.info("Match result {}", matchResult)
  }
}

class DefaultMaster(query: String) extends Master(query) with MasterComponent {
  val downloader = context.actorOf(Downloader.props, "downloader")
  override val finders = Vector(
    context.actorOf(SimpleFinder.props, "simpleFinder"),
    context.actorOf(ArosFinder.props(downloader), "arosFinder")
  )
  override val matcher = context.actorOf(Matcher.props, "matcher")
}
