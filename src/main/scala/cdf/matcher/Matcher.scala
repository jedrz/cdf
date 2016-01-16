package cdf.matcher

import akka.actor.{Actor, Props}
import cdf.master.Coordinator
import cdf.matcher.ngrams.DefaultNGramsMatcher
import cdf.offer.Offer

object Matcher {
  case class Match(offers: Vector[Offer])

  def props: Props = {
    Props[DefaultMatcher]
  }
}

trait MatcherComponent {
  def offerMatcherFactory(offers: Vector[Offer]): OfferMatcher[SimilarityMatrixResult]
}

class Matcher extends Actor {
  this: MatcherComponent =>

  override def receive: Receive = {
    case Matcher.Match(offers) =>
      val offerMatcher = offerMatcherFactory(offers)
      val offerMatcherResult = offerMatcher.compute
      sender ! Coordinator.SimilarityMatrix(offerMatcherResult.matrix)
  }
}

class DefaultMatcher extends Matcher with MatcherComponent {
  override def offerMatcherFactory(offers: Vector[Offer]): OfferMatcher[SimilarityMatrixResult] = {
    new DefaultNGramsMatcher(offers, n = 2)
  }
}
