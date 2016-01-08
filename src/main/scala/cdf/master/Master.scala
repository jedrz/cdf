package cdf.master

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cdf.finder.Finder
import cdf.finder.aros.ArosFinder
import cdf.finder.download.Downloader
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
  // TODO: it should be a list of finders
  val finder: ActorRef
  val matcher: ActorRef
}

class Master(val query: String) extends Actor with ActorLogging {
  this: MasterComponent =>

  override def preStart(): Unit = {
    finder ! Finder.Find(query)
  }

  override def receive: Receive = {
    case offers: Master.Offers => matcher ! Matcher.Match(offers.list)
    case matchResult: Master.MatchResult => log.info("Match result {}", matchResult)
  }
}

class DefaultMaster(query: String) extends Master(query) with MasterComponent {
  val downloader = context.actorOf(Downloader.props)
  override val finder = context.actorOf(ArosFinder.props(downloader), "arosFinder")
  override val matcher = context.actorOf(Matcher.props, "matcher")
}
