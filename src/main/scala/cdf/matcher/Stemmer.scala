package cdf.matcher

import morfologik.stemming.polish.PolishStemmer

import scala.collection.JavaConversions._

class Stemmer {
  private val polishStemmer = new PolishStemmer

  def apply(word: String): String = {
    synchronized {
      polishStemmer
        .lookup(word)
        .map(_.getStem)
        .headOption
        .getOrElse(word)
        .toString
    }
  }
}
