package cdf.finder.download

import cdf.ActorSpec

import scala.concurrent.duration._

class DownloadWorkerSpec extends ActorSpec(classOf[DownloadWorkerSpec]) {
  val downloadWorker = system.actorOf(DownloadWorker.props)

  // Ignored because I don't want to download real web page.
  ignore should "download page" in {
    val id = 7L
    val url = "http://wykop.pl"

    downloadWorker ! Downloader.Download(id, url)

    expectMsgPF(20.seconds) {
      case Downloader.DownloadResult(receivedId, source) =>
        receivedId should equal (id)
        source should not be empty
    }
  }
}
