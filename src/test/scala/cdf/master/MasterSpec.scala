package cdf.master

import akka.actor.{Props, ActorRef}
import akka.testkit.TestProbe
import cdf.ActorSpec
import cdf.finder.Finder
import cdf.matcher.Matcher
import cdf.offer.Offer

class MasterMock(query: String, val finder: ActorRef, val matcher: ActorRef) extends Master(query) with MasterComponent

class MasterSpec extends ActorSpec(classOf[MasterSpec]) {

  val finderProbe = TestProbe()
  val matcherProbe = TestProbe()
  val query = "query"
  val master = system.actorOf(Props(new MasterMock(query, finderProbe.ref, matcherProbe.ref)))

  it should "send find to finder on start" in {
    finderProbe.expectMsg(Finder.Find(query))
  }

  it should "send offers to match" in {
    val offers = List(Offer(title = "title", url = "url", price = BigDecimal.valueOf(10)))

    master ! Master.Offers(offers)

    matcherProbe.expectMsg(Matcher.Match(offers))
  }

}
