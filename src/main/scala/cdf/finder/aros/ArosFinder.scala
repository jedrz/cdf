package cdf.finder.aros

import java.util.concurrent.atomic.AtomicLong

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cdf.finder.Finder
import cdf.finder.download.Downloader
import cdf.master.Coordinator
import cdf.offer.Offer

import scala.util.{Failure, Success}

object ArosFinder {
  def props(downloader: ActorRef): Props = {
    Props(classOf[DefaultArosFinder], downloader)
  }
}

trait ArosFinderComponent {
  val arosUtil: ArosUtil
}

class ArosFinder(val downloader: ActorRef) extends Actor with ActorLogging {
  this: ArosFinderComponent =>

  val seqNumber = new AtomicLong()
  val encoding = "ISO-8859-2"

  override def receive: Receive = {
    case Finder.Find(query) =>
      log.info("Received {} query to search in aros", query)
      val queryUrl = arosUtil.createSearchQuery(query)
      downloader ! Downloader.Download(seqNumber.incrementAndGet, queryUrl, encoding)
      context become waitingForSearchResults(sender)
  }

  def waitingForSearchResults(replyToWithOffers: ActorRef): Receive = {
    case Downloader.DownloadResult(_, source) =>
      val linksToBooks = arosUtil.parseSearchResults(source)
      log.info("Found {} links to books", linksToBooks)
      val idToLinkMap = linksToBooks.map((seqNumber.incrementAndGet, _)).toMap
      idToLinkMap foreach {
        case (id, link) =>
          downloader ! Downloader.Download(id, link, encoding)
      }
      context become collectingOffers(replyToWithOffers, idToLinkMap.keySet, idToLinkMap)
  }

  def collectingOffers(replyToWithOffers: ActorRef,
                       ids: Set[Long],
                       idToLinkMap: Map[Long, String],
                       offers: List[Offer] = Nil): Receive = {
    case Downloader.DownloadResult(id, source) =>
      val newIds = ids - id
      val url = idToLinkMap(id)
      val newOffers = parseOfferAndAdd(offers, source, url)
      if (newIds.isEmpty) {
        replyToWithOffers ! Coordinator.Offers(newOffers)
        // This is optional since we assume for now that only one search request is allowed.
        context become receive
      } else {
        context become collectingOffers(replyToWithOffers, newIds, idToLinkMap, newOffers)
      }
  }

  private def parseOfferAndAdd(offers: List[Offer], source: String, url: String): List[Offer] = {
    val offerTry = arosUtil.parseToOffer(source, url)
    val newOffers = offerTry match {
      case Success(offer) => offer :: offers
      case Failure(exception) =>
        log.error(exception, "Parsing offer {} failed", url)
        offers
    }
    newOffers
  }
}

class DefaultArosFinder(downloader: ActorRef) extends ArosFinder(downloader) with ArosFinderComponent {
  override val arosUtil = new ArosUtil
}
