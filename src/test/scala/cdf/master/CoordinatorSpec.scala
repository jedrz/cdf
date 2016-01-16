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
  val finder1Offers = Vector(Offer(title = "title", url = "url", price = BigDecimal.valueOf(10)))
  val finder2Probe = TestProbe()
  val finder2Offers = Vector(
    Offer(title = "title 2", url = "url 2", price = BigDecimal.valueOf(20)),
    Offer(title = "title 3", url = "url 3", price = BigDecimal.valueOf(30))
  )
  val finderProbes = Vector(finder1Probe, finder2Probe)
  val findersOffers = finder1Offers ++ finder2Offers
  val matcherProbe = TestProbe()
  val similarityMatrix = {
    val matrix = Array.ofDim[Double](2, 2)
    matrix(0)(0) = 1.0
    matrix(0)(1) = 2.0
    matrix(1)(0) = 3.0
    matrix(1)(1) = 4.0
    matrix
  }
  val query = "query"

  it should "do all the things" in {
    val coordinator = system.actorOf(Props(new CoordinatorWithMocks(query, self, finderProbes.map(_.ref), matcherProbe.ref)))
    watch(coordinator)

    finderProbes.foreach(_.expectMsg(Finder.Find(query)))

    finder1Probe.reply(Coordinator.Offers(finder1Offers))

    matcherProbe.expectNoMsg()

    finder2Probe.reply(Coordinator.Offers(finder2Offers))

    matcherProbe.expectMsg(Matcher.Match(findersOffers))

    matcherProbe.reply(Coordinator.SimilarityMatrix(similarityMatrix))

    expectMsg(Master.SimilarityMatrix(findersOffers, similarityMatrix))
    expectTerminated(coordinator)
  }
}
