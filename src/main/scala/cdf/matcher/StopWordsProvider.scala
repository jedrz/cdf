package cdf.matcher

import java.nio.charset.StandardCharsets

import scala.io.Source

class StopWordsProvider {
  val words: Set[String] = {
    val inputStream = getClass.getResourceAsStream("/stopwords.txt")
    Source.fromInputStream(inputStream, StandardCharsets.UTF_8.name())
      .getLines
      .map(_.trim.toLowerCase)
      .toSet
  }

  def contains(word: String): Boolean = {
    words.contains(word.toLowerCase)
  }
}
