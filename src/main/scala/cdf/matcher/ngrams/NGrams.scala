package cdf.matcher.ngrams

case class NGrams(value: Vector[Vector[String]]) {
  def countCommonNGrams(other: NGrams): Int = {
    val commonNGrams = value.toSet & other.value.toSet
    commonNGrams.size
  }
}
