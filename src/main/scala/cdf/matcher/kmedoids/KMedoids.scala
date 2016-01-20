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
    val initialChanged = false
    productSeq.foldLeft((initialChanged, initialMedoids))((acc, product) =>
      (acc, product) match {
        case ((changed, medoids), (medoidIdx, exampleIdx)) =>
          val maybeUpdatedMedoids = trySwap(medoidIdx, exampleIdx, medoids)
          maybeUpdatedMedoids match {
            case Some(updatedMedoids) => (true, updatedMedoids)
            case None => (changed, medoids)
          }
      })
  }

  private def trySwap(medoidIdx: Int, exampleIdx: Int, medoids: IndexedSeq[T]): Option[IndexedSeq[T]] = {
    if (exampleIdx == medoidIdx) {
      None
    } else {
      // This should be a param.
      val (overallDistance, _) = computeClusterMemberships(medoids)
      val newMedoids = medoids.updated(medoidIdx, examples(exampleIdx))
      val (newOverallDistance, _) = computeClusterMemberships(newMedoids)
      if (newOverallDistance < overallDistance) {
        Some(newMedoids)
      } else {
        None
      }
    }
  }

  def computeClusterMemberships(medoids: IndexedSeq[T]): (Double, IndexedSeq[Int]) = {
    val memberships = examples
      .map(example => medoids
        .zipWithIndex
        .minBy { case (medoid, index) =>
          distanceFun(medoid, example)
        }
        ._2
      )
    val overallDistance = memberships.zip(examples).map { case (closestMedoidIdx, example) =>
      distanceFun(medoids(closestMedoidIdx), example)
    }.sum
    (overallDistance, memberships)
  }
}
