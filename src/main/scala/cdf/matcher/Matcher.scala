package cdf.matcher

import akka.actor.{Actor, Props}
import cdf.master.Coordinator
import cdf.matcher.ngrams.DefaultNGramsMatcher
import cdf.offer.Offer

object Matcher {
  case class Match(offers: List[Offer])

  def props: Props = {
    Props[DefaultMatcher]
  }
}

trait MatcherComponent {
  def offerMatcherFactory(offers: Vector[Offer]): OfferMatcher
}

class Matcher extends Actor {
  this: MatcherComponent =>

  override def receive: Receive = {
    case Matcher.Match(offers) =>
      val offerMatcher = offerMatcherFactory(offers.toVector)
      val offersGroups = offerMatcher.compute
      sender ! Coordinator.MatchResult(offersGroups.groups.map(_.toList).toList)
  }
}

class DefaultMatcher extends Matcher with MatcherComponent {
  override def offerMatcherFactory(offers: Vector[Offer]): OfferMatcher = {
    new DefaultNGramsMatcher(offers, n = 2)
  }
}
