package cdf.finder.aros

import java.text.NumberFormat
import java.util.Locale

import cdf.finder.FinderUtil
import cdf.offer.Offer
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.jsoup.nodes.{Document, Element, Node, TextNode}
import org.jsoup.select.NodeVisitor

import scala.util.Try

class ArosFinderUtil extends FinderUtil {
  private val browser = new Browser

  private val searchResultSelector = """
      |html body div table:eq(0) tbody tr td table tbody tr td:eq(1)
      |table tbody tr:eq(1) td:eq(1) table tbody tr td
      |table tbody tr td:eq(3) div:eq(0) a""".stripMargin

  private val titleSelector = "span[itemprop=name]"

  private val authorSelector = "span[itemprop=offerDetails] table tbody tr:eq(0) td:eq(1) a b"

  private val beforeAndWithDecimalPointSelector = "tr:eq(1) td table tbody tr td:eq(0) font:matches(\\d+)"
  private val afterDecimalPointSelector = "tr:eq(1) td table tbody tr td:eq(1) div:eq(0) font:matches(\\d+)"

  private val descriptionSelector = "span[itemprop=productDetails]"

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

  def parseToOffer(source: String, url: String): Try[Offer] = {
    Try {
      val document = browser.parseString(source)
      val title = document >> text(titleSelector)
      val author = document >> text(authorSelector)
      val beforeAndWithDecimalPoint = document >> text(beforeAndWithDecimalPointSelector)
      val afterDecimalPoint = document >> text(afterDecimalPointSelector)
      val price = NumberFormat.getInstance(Locale.forLanguageTag("PL"))
        .parse(beforeAndWithDecimalPoint + afterDecimalPoint)
        .toString
      Offer(
        title = title,
        url = url,
        price = BigDecimal(price),
        author = author,
        description = parseDescription(document)
      )
    }
  }

  private def parseDescription(document: Document): String = {
    val offerElement: Element = document >> element(descriptionSelector)
    val descriptionElements = offerElement.childNodes.toList.drop(2)
    val descriptionBuilder = new StringBuilder
    descriptionElements.foreach(_.traverse(new NodeVisitor {
      override def tail(node: Node, depth: Int): Unit = {
        node match {
          case textNode: TextNode =>
            descriptionBuilder.append(textNode.text).append('\n')
          case _ =>
        }
      }
      override def head(node: Node, depth: Int): Unit = ()
    }))
    descriptionBuilder.toString
  }
}
