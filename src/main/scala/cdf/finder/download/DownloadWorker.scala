package cdf.finder.download

import java.net.URL

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
    case Downloader.Download(id, url, encoding) =>
      log.info("Received url {} to download", url)
      // We block inside actor because work is distributed by the parent.
      // See previous revision for a solution with futures.
      Try {
        downloadURL(url, encoding)
      } recover {
        case t =>
          log.error(t, "Downloading from url {} failed", url)
          ""
      } map(Downloader.DownloadResult(id, _)) foreach(sender ! _)
  }

  private def downloadURL(url: String, encoding: String): String = {
    val urlObject = new URL(url)
    val connection = urlObject.openConnection
    connection.setConnectTimeout(5000)
    connection.setReadTimeout(3000)
    Source.fromInputStream(connection.getInputStream, encoding).mkString
  }
}
