package cdf.finder.aros

import akka.actor.{ActorRef, Props}
import cdf.finder.{Finder, FinderComponent}

object ArosFinder {
  def props(downloader: ActorRef): Props = {
    Props(classOf[ArosFinder], downloader)
  }
}

class ArosFinder(downloader: ActorRef) extends Finder(downloader) with FinderComponent {
  override val util = new ArosFinderUtil
  override val encoding = "ISO-8859-2"
  override val shopName = "Aros"
}

