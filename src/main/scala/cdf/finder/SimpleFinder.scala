package cdf.finder

import akka.actor.{Actor, Props}
import cdf.master.Coordinator
import cdf.offer.Offer

object SimpleFinder {
  def props: Props = {
    Props[SimpleFinder]
  }
}

class SimpleFinder extends Actor {
  override def receive: Receive = {
    case Finder.Find(query) =>
      val offers = List(
        Offer(title = "1", url = "http://" + query, price = BigDecimal.valueOf(35)),
        Offer(title = "2", url = "http://" + query, price = BigDecimal.valueOf(30))
      )
      sender ! Coordinator.Offers(offers)
  }
}
