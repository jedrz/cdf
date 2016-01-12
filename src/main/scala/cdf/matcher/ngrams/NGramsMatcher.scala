package cdf.matcher.ngrams

import cdf.matcher.{DefaultPreprocessor, OfferMatcher, Preprocessor}
import cdf.offer.{Offer, OffersGroups}

trait NGramsMatcherComponent {
  val preprocessor: Preprocessor
  def nGramsEvaluatorFactory(n: Int): NGramsEvaluator
  def copy(offers: Vector[Offer]): NGramsMatcher
}

class NGramsMatcher(val offers: Vector[Offer] = Vector.empty, val n: Int = 2) extends OfferMatcher {
  this: NGramsMatcherComponent =>

  override def withOffer(offer: Offer): OfferMatcher = {
    copy(offers :+ offer)
  }

  override def compute: OffersGroups = {
    val nGramsEvaluator = nGramsEvaluatorFactory(n)
    val offerToNGrams = offers
      .map(offer => (offer, nGramsEvaluator(preprocessor(offer))))
      .toMap
    val groups = offers.map(offer => Vector(offer, findClosest(offer, offerToNGrams)))
    OffersGroups(groups)
  }

  private def findClosest(offer: Offer, offerToNGrams: Map[Offer, NGrams]): Offer = {
    val offerNGrams = offerToNGrams(offer)
    val withoutOffer = offerToNGrams - offer
    val closest = withoutOffer
      .mapValues(nGrams => nGrams.countCommonNGrams(offerNGrams))
      .maxBy(_._2)
    // TODO: Configure slf4j to use logging in not actor classes.
    println(s"Closest to ${offer.url} is ${closest._1.url} with score ${closest._2}")
    closest._1
  }
}

class DefaultNGramsMatcher(offers: Vector[Offer] = Vector.empty, n: Int = 2) extends NGramsMatcher(offers, n) with NGramsMatcherComponent {
  override val preprocessor = new DefaultPreprocessor
  override def nGramsEvaluatorFactory(n: Int): NGramsEvaluator = {
    new NGramsEvaluator(n)
  }
  override def copy(offers: Vector[Offer]): DefaultNGramsMatcher = {
    new DefaultNGramsMatcher(offers, n)
  }
}
