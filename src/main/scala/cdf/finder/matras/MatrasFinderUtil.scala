package cdf.finder.matras

import java.text.NumberFormat
import java.util.Locale

import cdf.finder.FinderUtil
import cdf.offer.Offer
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.jsoup.nodes.Element

import scala.util.Try

class MatrasFinderUtil extends FinderUtil {
  private val browser = new Browser

  private val searchResultSelector = "h2.product-name a"
  private val titleSelector = "div.product-name h1"

  private val priceSelector = "p.special-price span.price"

  private val authorSelector = "div.product-info-list h3:eq(0) a"

  private val descriptionSelector = "div.short-description div"

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
      val description = document >> text(descriptionSelector)
      Offer(
        title = title,
        url = url,
        price = BigDecimal(price),
        author = author,
        description = description
      )
    }
  }
}
