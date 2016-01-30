package cdf.finder

import java.util.concurrent.atomic.AtomicLong

import akka.actor.{Actor, ActorLogging, ActorRef}
import cdf.finder.download.Downloader
import cdf.master.Coordinator
import cdf.offer.Offer

import scala.util.{Failure, Success}

object Finder {
  case class Find(query: String)
}


trait FinderComponent {
  val util: FinderUtil
  val encoding: String
  val shopName: String
}

class Finder(val downloader: ActorRef) extends Actor with ActorLogging {
  this: FinderComponent =>

  val maxOffersSize = 10
  val seqNumber = new AtomicLong()

  override def receive: Receive = {
    case Finder.Find(query) =>
      log.info("Received {} query to search in {}", query, shopName)
      val queryUrl = util.createSearchQuery(query)
      downloader ! Downloader.Download(seqNumber.incrementAndGet, queryUrl, encoding)
      context become waitingForSearchResults(sender)
  }

  def waitingForSearchResults(replyToWithOffers: ActorRef): Receive = {
    case Downloader.DownloadResult(_, source) =>
      val linksToBooks = util.parseSearchResults(source)
      log.info("Found {} links to books", linksToBooks)
      val idToLinkMap = linksToBooks.map((seqNumber.incrementAndGet, _)).toMap
      idToLinkMap foreach {
        case (id, link) =>
          downloader ! Downloader.Download(id, link, encoding)
      }
      context become collectingOffers(replyToWithOffers, idToLinkMap.keySet, idToLinkMap)
  }

  def collectingOffers(replyToWithOffers: ActorRef,
                       remainingIds: Set[Long],
                       idToLinkMap: Map[Long, String],
                       collectedIdsAndOffers: Vector[(Long, Offer)] = Vector.empty): Receive = {
    case Downloader.DownloadResult(id, source) =>
      val newIds = remainingIds - id
      val url = idToLinkMap(id)
      val newIdsAndOffers = parseOfferAndAdd(collectedIdsAndOffers, source, url, id)
      if (newIds.isEmpty) {
        val offersToSend = newIdsAndOffers.sortBy(_._1).map(_._2).take(maxOffersSize)
        replyToWithOffers ! Coordinator.Offers(offersToSend)
        // This is optional since we assume for now that only one search request is allowed.
        context become receive
      } else {
        context become collectingOffers(replyToWithOffers, newIds, idToLinkMap, newIdsAndOffers)
      }
  }

  private def parseOfferAndAdd(idsAndOffers: Vector[(Long, Offer)],
                               source: String,
                               url: String,
                               id: Long): Vector[(Long, Offer)] = {
    val offerTry = util.parseToOffer(source, url)
    val newIdsAndOffers = offerTry match {
      case Success(offer) => idsAndOffers :+ (id, offer)
      case Failure(exception) =>
        log.error(exception, "Parsing offer {} failed", url)
        idsAndOffers
    }
    newIdsAndOffers
  }
}


