package cdf.finder.empik

import java.net.URLEncoder
import java.text.NumberFormat
import java.util.Locale

import cdf.finder.FinderUtil
import cdf.offer.Offer
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.jsoup.nodes.Element

import scala.util.Try

class EmpikFinderUtil extends FinderUtil {
  private val browser = new Browser

  private val searchResultSelector = "div.productBox-450Desc a.productBox-450Title"
  private val titleSelector = "h1.productMainTitle span[itemprop=name]"
  private val priceSelector = "span.currentPrice span[itemprop=price]"
  private val authorSelector = "span.pDAuthorList a[itemprop=author]"
  private val descriptionSelector = "div.longDescription"

  def createSearchQuery(query: String): String = {
    val encodedQuery = URLEncoder.encode(query, "UTF-8")
    "http://www.empik.com/ksiazki,31,s?q=" + encodedQuery
  }

  def parseSearchResults(source: String): List[String] = {
    val document = browser.parseString(source)
    val aElements: List[Element] = document >> elementList(searchResultSelector)
    val linksToBooks = aElements.map("http://www.empik.com" + _.attr("href"))
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
