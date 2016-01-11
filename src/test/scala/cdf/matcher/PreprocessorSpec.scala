package cdf.matcher

import cdf.UnitSpec
import cdf.offer.Offer

class PreprocessorSpec extends UnitSpec {
  // Because case classes can't be mocked in scala mock.
  class MockableOffer extends Offer(title = "", url = "", price = BigDecimal.valueOf(1), author = "", description = "")

  val stemmerStub = stub[Stemmer]
  val preprocessor = new Preprocessor with PreprocessorComponent {
    override val stemmer = stemmerStub
  }
  val offerStub = stub[MockableOffer]

  before {
    (stemmerStub.apply _).when(*).onCall(identity[String] _)
  }

  val data = Table(
    ("word", "result"),
    ("word", Vector("word")),
    ("word1   word2 word3", Vector("word1", "word2", "word3")),
    ("word1.  word2, \t word3! \n word4? \n", Vector("word1", "word2", "word3", "word4")),
    ("w1-w2", Vector("w1", "w2"))
  )

  it should "preprocess offer" in {
    forAll (data) { (description: String, result: Vector[String]) =>
      (offerStub.completeDescription _).when().returns(description)

      val result = preprocessor(offerStub)

      result should contain theSameElementsAs result
    }
  }
}
