package cdf.finder.download

import akka.actor.{ActorLogging, Props, Actor, Terminated}
import akka.routing.{ActorRefRoutee, Router, SmallestMailboxRoutingLogic}

object Downloader {
  case class Download(id: Long, url: String)
  case class DownloadResult(id: Long, source: String)

  def props: Props = {
    Props[Downloader]
  }
}

class Downloader extends Actor with ActorLogging {
  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(DownloadWorker.props)
      context watch r
      ActorRefRoutee(r)
    }
    Router(SmallestMailboxRoutingLogic(), routees)
  }

  override def receive: Receive = {
    case download: Downloader.Download =>
      log.info("Received url {} to download", download.url)
      router.route(download, sender())
    case Terminated(actorRef) =>
      router = router.removeRoutee(actorRef)
      val r = context.actorOf(DownloadWorker.props)
      context watch r
      router = router.addRoutee(r)
  }
}
