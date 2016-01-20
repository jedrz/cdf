package cdf.matcher.kmedoids

object KMedoidsUtil {
  def cartesianProduct[A, B](firstSeq: Seq[A], secondSeq: Seq[B]): Seq[(A, B)] = {
    for {
      fromFirst <- firstSeq
      fromSecond <- secondSeq
    } yield (fromFirst, fromSecond)
  }
}
