package cdf.matcher.kmedoids

import cdf.matcher.distance.{CosineWithTfIdfMeasure, DistanceMeasure}
import cdf.matcher.{DefaultPreprocessor, OfferMatcher, OffersClusteringResult, Preprocessor}
import cdf.offer.Offer

trait KMedoidsMatcherComponent {
  val preprocessor: Preprocessor
  val distanceMeasure: DistanceMeasure
}

class KMedoidsMatcher(val offers: Vector[Offer]) extends OfferMatcher[OffersClusteringResult] {
  this: KMedoidsMatcherComponent =>

  override def compute: OffersClusteringResult = {
    val kRange = 2 to (offers.size / 2 + 1)
    val preprocessedOffers = offers.map(preprocessor(_))
    val evaluations = kRange.par.map(k => runKMedoids(k, preprocessedOffers))
    val bestEvaluation = evaluations.minBy(_._1)
    OffersClusteringResult(bestEvaluation._2)
  }

  private def runKMedoids(k: Int, preprocessedOffers: Vector[Vector[String]]): (Double, Vector[Vector[Offer]]) = {
    val distanceFun: (Vector[String], Vector[String]) => Double = distanceMeasure(_, _)
    val kmedoids = new KMedoids(preprocessedOffers, distanceFun)
    val medoids = kmedoids.run(k)
    val (overallDistance, memberships) = kmedoids.computeClusterMemberships(medoids)
    println(s"For k = $k clustering is $memberships with overall distance = $overallDistance for offers $offers")
    val clusterIdxToOffersWithIndices = offers
      .zipWithIndex
      .groupBy { case (offer, index) =>
        memberships(index)
      }
    val clustering = clusterIdxToOffersWithIndices
      .values
      .map(cluster => cluster.map(_._1))
      .toVector
    (overallDistance, clustering)
  }
}

// This sucks. Matcher should take elements and distance measure to compare. Here this is not the case.
// To fix this and retain vector of offers, distance measures should implement some caching at least.
class DefaultKMedoidsMatcher(offers: Vector[Offer]) extends KMedoidsMatcher(offers) with KMedoidsMatcherComponent {
  override val preprocessor = new DefaultPreprocessor
  override val distanceMeasure = {
    val preprocessedOffers = offers.map(preprocessor(_))
    CosineWithTfIdfMeasure.build(preprocessedOffers)
  }
}
