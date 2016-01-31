package cdf.matcher.distance

trait DistanceMeasure[T] {
  /**
    * The smaller absolute value returned, the better clustering is.
    */
  def apply(v1: T, v2: T): Double
}
