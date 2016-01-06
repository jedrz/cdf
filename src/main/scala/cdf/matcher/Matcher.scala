package cdf.matcher

import akka.actor.{Props, Actor}
import akka.actor.Actor.Receive
import cdf.master.Master
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
      sender ! Master.MatchResult(groups)
  }
}
