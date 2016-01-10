package cdf.finder.matras

import java.nio.charset.StandardCharsets

import akka.actor.{ActorRef, Props}
import cdf.finder.{Finder, FinderComponent}

object MatrasFinder {
  def props(downloader: ActorRef): Props = {
    Props(classOf[MatrasFinder], downloader)
  }
}

class MatrasFinder(downloader: ActorRef) extends Finder(downloader) with FinderComponent {
  override val util = new MatrasFinderUtil
  override val encoding = StandardCharsets.UTF_8.name()
  override val shopName = "Matras"
}
