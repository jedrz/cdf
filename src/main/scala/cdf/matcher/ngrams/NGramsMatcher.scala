package cdf.matcher.ngrams

import cdf.matcher._
import cdf.offer.Offer

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

  override def compute: SimilarityMatrixResult = {
    val nGramsEvaluator = nGramsEvaluatorFactory(n)
    val offerToNGrams = offers
      .map(offer => (offer, nGramsEvaluator(preprocessor(offer))))
      .toMap
    val matrix = offers
      .map(offer => createRow(offer, offers, offerToNGrams))
      .toArray
    SimilarityMatrixResult(matrix)
  }

  private def createRow(offer: Offer,
                        offers: Vector[Offer],
                        offerToNGrams: Map[Offer, NGrams]): Array[Double] = {
    offers
      .map(otherOffer => computeSimilarityOfNGrams(offerToNGrams(offer), offerToNGrams(otherOffer)))
      .toArray
  }

  private def computeSimilarityOfNGrams(nGrams1: NGrams, nGrams2: NGrams): Double = {
    val commonNGrams = nGrams1.countCommonNGrams(nGrams2).toDouble
    commonNGrams * 2 / (nGrams1.value.size + nGrams2.value.size)
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
