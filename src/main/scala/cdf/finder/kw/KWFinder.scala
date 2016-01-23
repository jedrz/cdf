package cdf.finder.kw

import java.nio.charset.StandardCharsets

import akka.actor.{ActorRef, Props}
import cdf.finder.{Finder, FinderComponent}

object KWFinder {
  def props(downloader: ActorRef): Props = {
    Props(classOf[KWFinder], downloader)
  }
}

class KWFinder(downloader: ActorRef) extends Finder(downloader) with FinderComponent {
  override val util = new KWFinderUtil
  override val encoding = StandardCharsets.UTF_8.name()
  override val shopName = "Księgarnia Warszawa"
}
