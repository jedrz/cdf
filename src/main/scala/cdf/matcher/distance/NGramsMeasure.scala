package cdf.matcher.distance

import cdf.matcher.ngrams.{NGrams, NGramsEvaluator}
import cdf.matcher.{DefaultPreprocessor, Preprocessor}
import cdf.offer.Offer

trait NGramsMeasureComponent {
  val preprocessor: Preprocessor
  val nGramsEvaluator: NGramsEvaluator
}

class NGramsMeasure extends DistanceMeasure {
  this: NGramsMeasureComponent =>

  override def apply(offer1: Offer, offer2: Offer): Double = {
    val nGrams1 = nGramsEvaluator(preprocessor(offer1))
    val nGrams2 = nGramsEvaluator(preprocessor(offer2))
    1 - computeSimilarityOfNGrams(nGrams1, nGrams2)
  }

  private def computeSimilarityOfNGrams(nGrams1: NGrams, nGrams2: NGrams): Double = {
    val commonNGrams = nGrams1.countCommonNGrams(nGrams2).toDouble
    commonNGrams * 2 / (nGrams1.value.size + nGrams2.value.size)
  }
}

class DefaultNGramsMeasure(n: Int = 2) extends NGramsMeasure with NGramsMeasureComponent {
  override val preprocessor = new DefaultPreprocessor
  override val nGramsEvaluator = new NGramsEvaluator(n)
}
