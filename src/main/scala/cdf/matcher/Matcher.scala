package cdf.matcher

import akka.actor.{Actor, Props}
import cdf.master.Coordinator
import cdf.offer.Offer

object Matcher {
  case class Match(offers: List[Offer])

  def props: Props = {
    Props[Matcher]
  }
}

class Matcher extends Actor {
  override def receive: Receive = {
    case Matcher.Match(offers) =>
      val groups = offers.grouped(1).toList
      sender ! Coordinator.MatchResult(groups)
  }
}
