package cdf.finder.matras

import java.text.NumberFormat
import java.util.Locale

import cdf.offer.Offer
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.jsoup.nodes.Element

import scala.util.Try

class MatrasUtil {
  private val browser = new Browser
  """html body div:eq(2) div div:eq(5) div div:eq(0) div:eq(1) div:eq(0) div:eq(0)
 div:eq(4) div"""
  private val searchResultSelector = """
      |html body div:eq(4) div div:eq(5) div div:eq(1) div:eq(1) div:eq(2) ol li div div:eq(0) div:eq(0)
      |div:eq(0) h2 a""".stripMargin
  private val titleSelector = """
      |html body div:eq(2) div div:eq(5) div div:eq(0) div:eq(1) div:eq(0) div:eq(0) div:eq(0) div:eq(0) h1""".stripMargin

  private val priceSelector = """html body div:eq(2) div div:eq(5) div div:eq(0) div:eq(1) div:eq(0) div:eq(0) div:eq(1)
     | form div:eq(1) div p:eq(0) span:eq(1)""".stripMargin
  
  private val authorSelector =
    """ html body div:eq(3) div div:eq(6) div div:eq(1) div:eq(2) div:eq(1) div:eq(1)
      | div:eq(1) div:eq(2) h3:eq(1) a""".stripMargin

  def createSearchQuery(query: String): String = {
    val escapedQuery = query.replace(' ', '+')
    "http://matras.pl/szukaj?query=" + escapedQuery + "&kategoria=65"
  }

  def parseSearchResults(source: String): List[String] = {
    val document = browser.parseString(source)
    val aElements: List[Element] = document >> elementList(searchResultSelector)
    val linksToBooks = aElements.map(_.attr("href"))
    linksToBooks
  }

  def parseToOffer(source: String, url: String): Try[Offer] = {
    Try {
      val document = browser.parseString(source)
      val title = document >> text(titleSelector)
      val author = document >> text(authorSelector)
      val price = NumberFormat.getInstance(Locale.forLanguageTag("PL"))
        .parse(document >> text(priceSelector))
        .toString
      Offer(
        title = title,
        url = url,
        price = BigDecimal(price),
        author = author
      )
    }
  }
}
