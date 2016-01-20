package cdf.matcher.kmedoids

import cdf.matcher.distance.DistanceMeasure
import cdf.matcher.{OfferMatcher, OffersClusteringResult}
import cdf.offer.Offer

class KMedoidsMatcher(val offers: Vector[Offer],
                      val distanceMeasure: DistanceMeasure) extends OfferMatcher[OffersClusteringResult] {

  override def compute: OffersClusteringResult = {
    val kRange = 2 to (offers.size / 2)
    val evaluations = kRange.map(runKMedoids)
    val bestEvaluation = evaluations.maxBy(_._1)
    OffersClusteringResult(bestEvaluation._2)
  }

  private def runKMedoids(k: Int): (Double, Vector[Vector[Offer]]) = {
    val distanceFun: (Offer, Offer) => Double = distanceMeasure(_, _)
    val kmedoids = new KMedoids(offers, distanceFun)
    val medoids = kmedoids.run(k)
    val (overallDistance, memberships) = kmedoids.computeClusterMemberships(medoids)
    println(s"For k = $k clustering is $memberships with overall distance = $overallDistance")
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