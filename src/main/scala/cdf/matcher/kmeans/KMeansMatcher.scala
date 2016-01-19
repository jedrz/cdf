package cdf.matcher.kmeans

import cdf.matcher.distance.DistanceMeasure
import cdf.matcher.{OfferMatcher, OffersClusteringResult}
import cdf.offer.Offer
import nak.cluster.Kmeans

class KMeansMatcher(val offers: Vector[Offer],
                    val distanceMeasure: DistanceMeasure) extends OfferMatcher[OffersClusteringResult] {

  override def compute: OffersClusteringResult = {
    val kRange = 2 to (offers.size / 2)
    val evaluations = kRange.map(run)
    val bestEvaluation = evaluations.maxBy(_._1)
    OffersClusteringResult(bestEvaluation._2)
  }

  private def run(k: Int): (Double, Vector[Vector[Offer]]) = {
    val kmeans = new Kmeans(offers, distanceMeasure(_, _))
    val (dispersion, centroids) = kmeans.run(k)
    val memberships = kmeans.computeClusterMemberships(centroids)._2
    val clusterIdxToOffersWithIndices = offers
      .zipWithIndex
      .groupBy { case (offer, index) =>
          memberships(index)
      }
    val clustering = clusterIdxToOffersWithIndices
      .values
      .map(cluster => cluster.map(_._1))
      .toVector
    (dispersion, clustering)
  }
}
