package cdf.matcher

import cdf.offer.Offer

trait PreprocessorComponent {
  val stemmer: Stemmer
}

class Preprocessor {
  this: PreprocessorComponent =>

  def apply(offer: Offer): Vector[String] = {
    val completeDescription = offer.completeDescription
    val tokens = tokenize(completeDescription)
    tokens.map(stemmer(_))
  }

  private def tokenize(text: String): Vector[String] = {
    text
      .replaceAll("\\W", " ")
      .split("\\s+")
      .toVector
  }
}

class DefaultPreprocessor extends Preprocessor with PreprocessorComponent {
  override val stemmer: Stemmer = new Stemmer
}