package cdf.master

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
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
  override val finder = context.actorOf(SimpleFinder.props, "finder")
  override val matcher = context.actorOf(Matcher.props, "matcher")
}
