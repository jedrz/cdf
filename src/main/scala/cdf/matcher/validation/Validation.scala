package cdf.matcher.validation

trait Validation[T] {
  /**
    * The smaller absolute value returned, the better clustering is.
    */
  def apply(examples: IndexedSeq[T], memberships: IndexedSeq[Int]): Double
}
