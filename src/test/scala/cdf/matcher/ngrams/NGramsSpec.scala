package cdf.matcher.ngrams

import cdf.UnitSpec

class NGramsSpec extends UnitSpec {

  val words = Vector("w1", "w2", "w3", "w4")

  val data = Table(
    ("n", "result"),
    (1, words.map(Vector(_))),
    (2, Vector(Vector("w1", "w2"), Vector("w2", "w3"), Vector("w3", "w4"))),
    (3, Vector(Vector("w1", "w2", "w3"), Vector("w2", "w3", "w4")))
  )

  it should "create ngrams" in {
    forAll (data) { (n: Int, result: Vector[Vector[String]]) =>
      val ngrams = new NGrams(n)

      ngrams(words) should be (result)
    }
  }
}
