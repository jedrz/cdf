package cdf.master

import akka.actor.{ActorRef, Props}
import akka.testkit.TestProbe
import cdf.ActorSpec
import cdf.finder.Finder
import cdf.matcher.Matcher
import cdf.offer.Offer

class MasterWithMocks(query: String, val finders: Vector[ActorRef], val matcher: ActorRef) extends Master(query) with MasterComponent

class MasterSpec extends ActorSpec(classOf[MasterSpec]) {
  val finder1Probe = TestProbe()
  val finder1Offers = List(Offer(title = "title", url = "url", price = BigDecimal.valueOf(10)))
  val finder2Probe = TestProbe()
  val finder2Offers = List(
    Offer(title = "title 2", url = "url 2", price = BigDecimal.valueOf(20)),
    Offer(title = "title 3", url = "url 3", price = BigDecimal.valueOf(30))
  )
  val finderProbes = Vector(finder1Probe, finder2Probe)
  val matcherProbe = TestProbe()
  val query = "query"

  it should "do all the things" in {
    system.actorOf(Props(new MasterWithMocks(query, finderProbes.map(_.ref), matcherProbe.ref)))

    finderProbes.foreach(_.expectMsg(Finder.Find(query)))

    finder1Probe.reply(Master.Offers(finder1Offers))

    matcherProbe.expectNoMsg()

    finder2Probe.reply(Master.Offers(finder2Offers))

    matcherProbe.expectMsg(Matcher.Match(finder1Offers ++ finder2Offers))
  }
}
