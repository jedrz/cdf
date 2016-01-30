package cdf.matcher.kmedoids

import cdf.matcher.distance.{CacheableDistanceMeasure, CosineWithTfIdfMeasure, DistanceMeasure}
import cdf.matcher.validation.{SilhouetteValidation, Validation}
import cdf.matcher.{DefaultPreprocessor, OfferMatcher, OffersClusteringResult, Preprocessor}
import cdf.offer.Offer

trait KMedoidsMatcherComponent {
  val preprocessor: Preprocessor
  val distanceMeasure: DistanceMeasure[Vector[String]]
  val validation: Validation[Vector[String]]
}

class KMedoidsMatcher(val offers: Vector[Offer]) extends OfferMatcher[OffersClusteringResult] {
  this: KMedoidsMatcherComponent =>

  override def compute: OffersClusteringResult = {
    val kRange = 2 to (offers.size * 0.6).ceil.toInt
    val preprocessedOffers = offers.map(preprocessor(_))
    val evaluations = kRange.par.map(k => runKMedoids(k, preprocessedOffers))
    val bestEvaluation = evaluations.minBy(_._1)
    OffersClusteringResult(bestEvaluation._2)
  }

  private def runKMedoids(k: Int, preprocessedOffers: Vector[Vector[String]]): (Double, Vector[Vector[Offer]]) = {
    val distanceFun: (Vector[String], Vector[String]) => Double = distanceMeasure(_, _)
    // TODO: Kmedoids should be injected.
    val kmedoids = new KMedoids(preprocessedOffers, distanceFun)
    val medoids = kmedoids.run(k)
    val (overallDistance, memberships) = kmedoids.computeClusterMemberships(medoids)
    val validationValue = validation(preprocessedOffers, memberships)
    println(s"For k = $k clustering is $memberships with overall distance = $overallDistance and validation = $validationValue for offers $offers")
    val clusterIdxToOffersWithIndices = offers
      .zipWithIndex
      .groupBy { case (offer, index) =>
        memberships(index)
      }
    val clustering = clusterIdxToOffersWithIndices
      .values
      .map(cluster => cluster.map(_._1))
      .toVector
    (validationValue, clustering)
  }
}

// This sucks. Matcher should take elements and distance measure to compare. Here this is not the case.
// To fix this and retain vector of offers, distance measures should implement some caching at least.
class DefaultKMedoidsMatcher(offers: Vector[Offer]) extends KMedoidsMatcher(offers) with KMedoidsMatcherComponent {
  override val preprocessor = new DefaultPreprocessor
  override val distanceMeasure = {
    val preprocessedOffers = offers.map(preprocessor(_))
    new CacheableDistanceMeasure(CosineWithTfIdfMeasure.build(preprocessedOffers))
  }
  override val validation = new SilhouetteValidation[Vector[String]](distanceMeasure(_, _))
}
