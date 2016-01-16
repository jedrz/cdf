package cdf.matcher.ngrams

class NGramsEvaluator(val n: Int) {
  def apply(words: Vector[String]): NGrams = {
    NGrams(words.sliding(n).toVector)
  }
}
