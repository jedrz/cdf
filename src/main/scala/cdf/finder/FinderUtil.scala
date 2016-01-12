package cdf.finder

import cdf.offer.Offer

import scala.util.Try

trait FinderUtil {
  def createSearchQuery(query: String): String

  def parseSearchResults(source: String): List[String]

  def parseToOffer(source: String, url: String): Try[Offer]
}
