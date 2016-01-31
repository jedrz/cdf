package cdf.finder.kw

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

class KWFinderUtil extends FinderUtil {
  private val browser = new Browser

  private val searchResultSelector = "div.book_item div.book_item_info2 div.book_title a"
  private val titleSelector = "div.book_item_info div.book_title2"
  private val priceSelector = "span.price"
  private val authorSelector = "div.book_item_left2 div.book_item_desc:eq(0) a"
  private val descriptionSelector = "div#opis div.description"

  def createSearchQuery(query: String): String = {
    val encodedQuery = URLEncoder.encode(query, "UTF-8")
    "https://ksiegarniawarszawa.pl/szukaj?phrase=" + encodedQuery + "&criteria=title"
  }

  def parseSearchResults(source: String): List[String] = {
    val document = browser.parseString(source)
    val aElements: List[Element] = document >> elementList(searchResultSelector)
    val linksToBooks = aElements.map("https://ksiegarniawarszawa.pl" + _.attr("href"))
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
