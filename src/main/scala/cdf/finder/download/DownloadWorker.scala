package cdf.finder.download

import java.nio.charset.StandardCharsets

import akka.actor.{Actor, ActorLogging, Props}

import scala.concurrent.Future
import scala.io.Source

object DownloadWorker {
  def props: Props = {
    Props[DownloadWorker]
  }
}

class DownloadWorker extends Actor with ActorLogging {
  // For Future executionContext implicit.
  import context.dispatcher

  override def receive: Receive = {
    case Downloader.Download(id, url) =>
      val sendTo = sender()
      Future {
        Source.fromURL(url, StandardCharsets.ISO_8859_1.name()).mkString
      } recover {
        case t =>
          log.error(t, "Downloading from url {} failed", url)
          ""
      } map(Downloader.DownloadResult(id, _)) foreach(sendTo ! _)
  }
}
