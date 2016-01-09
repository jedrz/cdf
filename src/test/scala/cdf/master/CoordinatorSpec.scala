package cdf.master

import akka.actor.{ActorRef, Props}
import akka.testkit.TestProbe
import cdf.ActorSpec
import cdf.finder.Finder
import cdf.matcher.Matcher
import cdf.offer.Offer

class CoordinatorWithMocks(query: String, replyTo: ActorRef, val finders: Vector[ActorRef], val matcher: ActorRef)
  extends Coordinator(query, replyTo)
  with CoordinatorComponent

class CoordinatorSpec extends ActorSpec(classOf[CoordinatorSpec]) {
  val finder1Probe = TestProbe()
  val finder1Offers = List(Offer(title = "title", url = "url", price = BigDecimal.valueOf(10)))
  val finder2Probe = TestProbe()
  val finder2Offers = List(
    Offer(title = "title 2", url = "url 2", price = BigDecimal.valueOf(20)),
    Offer(title = "title 3", url = "url 3", price = BigDecimal.valueOf(30))
  )
  val finderProbes = Vector(finder1Probe, finder2Probe)
  val findersOffers = finder1Offers ++ finder2Offers
  val matcherProbe = TestProbe()
  val matchedOffers = findersOffers.grouped(1).toList
  val query = "query"

  it should "do all the things" in {
    system.actorOf(Props(new CoordinatorWithMocks(query, self, finderProbes.map(_.ref), matcherProbe.ref)))

    finderProbes.foreach(_.expectMsg(Finder.Find(query)))

    finder1Probe.reply(Coordinator.Offers(finder1Offers))

    matcherProbe.expectNoMsg()

    finder2Probe.reply(Coordinator.Offers(finder2Offers))

    matcherProbe.expectMsg(Matcher.Match(findersOffers))

    matcherProbe.reply(Coordinator.MatchResult(matchedOffers))

    expectMsg(Coordinator.MatchResult(matchedOffers))
  }
}
