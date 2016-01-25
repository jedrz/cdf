package cdf.matcher.distance

import scalaz.Memo

class CosineWithTfIdfMeasure(val documents: Vector[Map[String, Int]]) extends DistanceMeasure {

  override def apply(words1: Vector[String], words2: Vector[String]): Double = {
    memoizedTfIdf((words1, words2))
  }

  private val memoizedTfIdf: ((Vector[String], Vector[String])) => Double =
    Memo.immutableHashMapMemo { case (words1, words2) =>
      val tfIdf1 = tfidf(words1)
      val tfIdf2 = tfidf(words2)
      val dotProduct = tfIdf1.foldLeft(0.0)((acc, elem) => {
        val (word, tfIdf1Value) = elem
        acc + tfIdf1Value * tfIdf2(word)
      })
      1 - dotProduct / (sumOfSquared(tfIdf1.values) + sumOfSquared(tfIdf2.values))
    }

  private def tfidf(words: Vector[String]): Map[String, Double] = {
    val tfMeasure = tf(words)
    val idfMeasure = idf(words)
    tfMeasure.map { case (word, tfValue) =>
      val idfValue = idfMeasure(word)
      (word, tfValue * idfValue)
    }
    .withDefaultValue(0)
  }

  private def tf(words: Vector[String]): Map[String, Double] = {
    words
      .map(word => (word, tf(word, words)))
      .toMap
      .withDefaultValue(0)
  }

  private def tf(word: String, words: Vector[String]): Double = {
    words.count(w => word == w).toDouble / words.size
  }

  private def idf(words: Vector[String]): Map[String, Double] = {
    words
      .map(word => (word, idf(word)))
      .toMap
      .withDefaultValue(0)
  }

  private def idf(word: String): Double = {
    Math.log(documents.size.toDouble / documents.count(document => document.contains(word)))
  }

  private def sumOfSquared(values: Iterable[Double]): Double = {
    values.map(v => v * v).sum
  }
}

object CosineWithTfIdfMeasure {
  def build(documents: Vector[Vector[String]]): CosineWithTfIdfMeasure = {
    val newDocuments = documents.map(occurrences)
    new CosineWithTfIdfMeasure(newDocuments)
  }

  private def occurrences(words: Seq[String]): Map[String, Int] = {
    words
      .groupBy(identity)
      .mapValues(_.size)
      .withDefaultValue(0)
  }
}
