package cdf.matcher.kmedoids

import scala.util.Random

class KMedoids[T](examples: IndexedSeq[T],
                  distanceFun: (T, T) => Double) {
  val random = new Random()

  def run(k: Int, maxIterations: Int = 20): IndexedSeq[T] = {
    def run(maxIterations: Int, medoids: IndexedSeq[T]): IndexedSeq[T] = {
      if (maxIterations < 0) {
        medoids
      } else {
        runOnce(medoids) match {
          case (false, _) => medoids
          case (true, newMedoids) => run(maxIterations - 1, newMedoids)
        }
      }
    }
    run(maxIterations, chooseRandomMedoids(k))
  }

  private def chooseRandomMedoids(k: Int): IndexedSeq[T] = {
    random.shuffle(examples).take(k)
  }

  private def runOnce(initialMedoids: IndexedSeq[T]): (Boolean, IndexedSeq[T]) = {
    val productSeq = KMedoidsUtil.cartesianProduct(initialMedoids.indices, examples.indices)
    val (initialOverallDistance, _) = computeClusterMemberships(initialMedoids)
    val initialChanged = false
    val (resultChanged, resultMedoids, _) =
      productSeq.foldLeft((initialChanged, initialMedoids, initialOverallDistance))((acc, product) =>
        (acc, product) match {
          case ((changed, medoids, overallDistance), (medoidIdx, exampleIdx)) =>
            val maybeUpdatedMedoids = trySwap(medoidIdx, exampleIdx, medoids, overallDistance)
            maybeUpdatedMedoids match {
              case Some((updatedMedoids, newOverallDistance)) => (true, updatedMedoids, newOverallDistance)
              case None => (changed, medoids, overallDistance)
            }
        })
    (resultChanged, resultMedoids)
  }

  private def trySwap(medoidIdx: Int,
                      exampleIdx: Int,
                      medoids: IndexedSeq[T],
                      overallDistance: Double): Option[(IndexedSeq[T], Double)] = {
    if (exampleIdx == medoidIdx) {
      None
    } else {
      val newMedoids = medoids.updated(medoidIdx, examples(exampleIdx))
      val (newOverallDistance, _) = computeClusterMemberships(newMedoids)
      if (newOverallDistance < overallDistance) {
        Some((newMedoids, newOverallDistance))
      } else {
        None
      }
    }
  }

  def computeClusterMemberships(medoids: IndexedSeq[T]): (Double, IndexedSeq[Int]) = {
    val membershipsAndDistances = examples
      .map(example => medoids
        .indices
        .map(medoidIdx => (medoidIdx, distanceFun(medoids(medoidIdx), example)))
        .minBy(_._2)
      )
    val (memberships, distances) = membershipsAndDistances.unzip
    (distances.sum, memberships.toIndexedSeq)
  }
}
