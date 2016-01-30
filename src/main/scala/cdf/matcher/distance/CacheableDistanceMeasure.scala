package cdf.matcher.distance

import scalaz.Memo

class CacheableDistanceMeasure[T](val distanceMeasure: DistanceMeasure[T]) extends DistanceMeasure[T] {
  private val memoizedDistanceMeasure: ((T, T)) => Double =
    Memo.immutableHashMapMemo { case (v1, v2) =>
      distanceMeasure(v1, v2)
    }

  override def apply(v1: T, v2: T): Double = {
    memoizedDistanceMeasure((v1, v2))
  }
}
