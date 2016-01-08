package cdf.finder.aros

import java.text.NumberFormat
import java.util.Locale

import cdf.offer.Offer
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.jsoup.nodes.Element

class ArosUtil {
  private val browser = new Browser

  private val searchResultSelector = """
      |html body div table:eq(0) tbody tr td table tbody tr td:eq(1)
      |table tbody tr:eq(1) td:eq(1) table tbody tr td
      |table tbody tr td:eq(3) div:eq(0) a""".stripMargin

  private val titleSelector = """
      |html body div table:eq(0) tbody tr td table tbody tr td:eq(1) table:eq(2) tbody
      |tr:eq(1) td:eq(1) table tbody tr td span table tbody tr:eq(0) td h1 span""".stripMargin

  private val beforeAndWithDecimalPointSelector = "tr:eq(1) td table tbody tr td:eq(0) font"
  private val afterDecimalPointSelector = "tr:eq(1) td table tbody tr td:eq(1) div:eq(0) font"

  def createSearchQuery(query: String): String = {
    val escapedQuery = query.replace(' ', '+')
    "http://aros.pl/szukaj/" + escapedQuery + "/0"
  }

  def parseSearchResults(source: String): List[String] = {
    val document = browser.parseString(source)
    val aElements: List[Element] = document >> elementList(searchResultSelector)
    val linksToBooks = aElements.map(_.attr("href"))
    linksToBooks
  }

  def parseToOffer(source: String, url: String): Offer = {
    val document = browser.parseString(source)
    val title = document >> text(titleSelector)
    val author = (document >> element("td:containsOwn(Autor:)")).parent() >> text("td:eq(1) a b")
    val beforeAndWithDecimalPoint = document >> text(beforeAndWithDecimalPointSelector)
    val afterDecimalPoint = document >> text(afterDecimalPointSelector)
    val price = NumberFormat.getInstance(Locale.forLanguageTag("PL"))
      .parse(beforeAndWithDecimalPoint + afterDecimalPoint)
      .toString
    Offer(
      title = title,
      url = url,
      price = BigDecimal(price),
      author = author
    )
  }
}
