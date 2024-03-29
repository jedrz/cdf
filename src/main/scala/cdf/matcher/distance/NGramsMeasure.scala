package cdf.matcher.distance

import cdf.matcher.ngrams.{NGrams, NGramsEvaluator}

trait NGramsMeasureComponent {
  val nGramsEvaluator: NGramsEvaluator
}

class NGramsMeasure extends DistanceMeasure[Vector[String]] {
  this: NGramsMeasureComponent =>

  override def apply(words1: Vector[String], words2: Vector[String]): Double = {
    val nGrams1 = nGramsEvaluator(words1)
    val nGrams2 = nGramsEvaluator(words2)
    1 - computeSimilarityOfNGrams(nGrams1, nGrams2)
  }

  private def computeSimilarityOfNGrams(nGrams1: NGrams, nGrams2: NGrams): Double = {
    val commonNGrams = nGrams1.countCommonNGrams(nGrams2).toDouble
    commonNGrams * 2 / (nGrams1.value.size + nGrams2.value.size)
  }
}

class DefaultNGramsMeasure(n: Int = 2) extends NGramsMeasure with NGramsMeasureComponent {
  override val nGramsEvaluator = new NGramsEvaluator(n)
}
