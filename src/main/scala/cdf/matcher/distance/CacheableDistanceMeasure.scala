package cdf.matcher.distance

import scalaz.Memo

class CacheableDistanceMeasure(val distanceMeasure: DistanceMeasure) extends DistanceMeasure {
  private val memoizedDistanceMeasure: ((Vector[String], Vector[String])) => Double =
    Memo.immutableHashMapMemo { case (words1, words2) =>
      distanceMeasure(words1, words2)
    }

  override def apply(words1: Vector[String], words2: Vector[String]): Double = {
    memoizedDistanceMeasure((words1, words2))
  }
}
