package cdf.finder.download

import java.nio.charset.StandardCharsets

import akka.actor.{Actor, ActorLogging, Props}

import scala.io.Source
import scala.util.Try

object DownloadWorker {
  def props: Props = {
    Props[DownloadWorker]
  }
}

class DownloadWorker extends Actor with ActorLogging {
  override def receive: Receive = {
    case Downloader.Download(id, url) =>
      log.info("Received url {} to download", url)
      // We block inside actor because work is distributed by the parent.
      // See previous revision for a solution with futures.
      Try {
        Source.fromURL(url, StandardCharsets.ISO_8859_1.name()).mkString
      } recover {
        case t =>
          log.error(t, "Downloading from url {} failed", url)
          ""
      } map(Downloader.DownloadResult(id, _)) foreach(sender ! _)
  }
}
