package cdf.finder.download

import akka.actor.{Actor, ActorLogging, Props, Terminated}
import akka.routing.{ActorRefRoutee, Router, SmallestMailboxRoutingLogic}

object Downloader {
  case class Download(id: Long, url: String, encoding: String)
  case class DownloadResult(id: Long, source: String)

  def props: Props = {
    Props[Downloader]
  }
}

class Downloader extends Actor with ActorLogging {
  var router = {
    val routees = (1 to 5).map(i => {
      val r = context.actorOf(DownloadWorker.props, s"downloadWorker$i")
      context watch r
      ActorRefRoutee(r)
    })
    Router(SmallestMailboxRoutingLogic(), routees)
  }

  override def receive: Receive = {
    case download: Downloader.Download =>
      log.info("Received url {} to download. Routing", download.url)
      router.route(download, sender)
    case Terminated(actorRef) =>
      router = router.removeRoutee(actorRef)
      val r = context.actorOf(DownloadWorker.props)
      context watch r
      router = router.addRoutee(r)
  }
}
