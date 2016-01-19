package cdf.matcher

import akka.actor.{Actor, Props}
import cdf.master.Coordinator
import cdf.matcher.distance.DefaultNGramsMeasure
import cdf.matcher.kmeans.KMeansMatcher
import cdf.offer.Offer

object Matcher {
  case class Match(offers: Vector[Offer])

  def props: Props = {
    Props[DefaultMatcher]
  }
}

trait MatcherComponent {
  def offerMatcherFactory(offers: Vector[Offer]): OfferMatcher[OffersClusteringResult]
}

class Matcher extends Actor {
  this: MatcherComponent =>

  override def receive: Receive = {
    case Matcher.Match(offers) =>
      val offerMatcher = offerMatcherFactory(offers)
      val offerMatcherResult = offerMatcher.compute
      sender ! Coordinator.OffersGroups(offerMatcherResult.clusters)
  }
}

class DefaultMatcher extends Matcher with MatcherComponent {
  override def offerMatcherFactory(offers: Vector[Offer]): OfferMatcher[OffersClusteringResult] = {
    new KMeansMatcher(offers, new DefaultNGramsMeasure(n = 2))
  }
}
