package cdf.matcher.distance

trait DistanceMeasure {
  def apply(words1: Vector[String], words2: Vector[String]): Double
}
