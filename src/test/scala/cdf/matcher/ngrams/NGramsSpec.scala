package cdf.matcher.ngrams

import cdf.UnitSpec

class NGramsSpec extends UnitSpec {

  it should "count common n-grams" in {
    val ngrams1 = NGrams(Vector(Vector("w1", "w2"), Vector("w3", "w4"), Vector("w4", "w5")))
    val ngrams2 = NGrams(Vector(Vector("w3", "w4"), Vector("w2", "w3"), Vector("w1", "w2")))

    ngrams1.countCommonNGrams(ngrams2) should be (2)
  }
}
