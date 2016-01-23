package cdf.finder.empik

import java.nio.charset.StandardCharsets

import akka.actor.{ActorRef, Props}
import cdf.finder.{Finder, FinderComponent}

object EmpikFinder {
  def props(downloader: ActorRef): Props = {
    Props(classOf[EmpikFinder], downloader)
  }
}

class EmpikFinder(downloader: ActorRef) extends Finder(downloader) with FinderComponent {
  override val util = new EmpikFinderUtil
  override val encoding = StandardCharsets.UTF_8.name()
  override val shopName = "Empik"
}
