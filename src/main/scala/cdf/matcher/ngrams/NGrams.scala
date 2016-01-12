package cdf.matcher.ngrams

class NGrams(val n: Int) {
  def apply(words: Vector[String]): Vector[Vector[String]] = {
    words.sliding(n).toVector
  }
}
