package cdf.matcher.validation

// https://en.wikipedia.org/wiki/Silhouette_%28clustering%29
class SilhouetteValidation[T](val distanceFun: (T, T) => Double) extends Validation[T] {
  override def apply(examples: IndexedSeq[T], memberships: IndexedSeq[Int]): Double = {
    val silhouettes = examples
      .indices
      .map(exampleIdx => silhouetteForOne(
        exampleIdx, examples(exampleIdx), examples, memberships))
    avg(silhouettes)
  }

  private def silhouetteForOne(chosenExampleIdx: Int,
                               chosenExample: T,
                               examples: IndexedSeq[T],
                               memberships: IndexedSeq[Int]): Double = {
    val withinClusterMeasure = computeWithinClusterMeasure(chosenExampleIdx, chosenExample, examples, memberships)
    val outOfClusterMeasure = computeOutOfClusterMeasure(chosenExampleIdx, chosenExample, examples, memberships)
    val silhouetteValue = (outOfClusterMeasure - withinClusterMeasure) / Math.max(withinClusterMeasure, outOfClusterMeasure)
    // Adjust silhouette measure that is between -1 and 1. 1 is the best value.
    Math.abs(silhouetteValue - 1)
  }

  private def computeWithinClusterMeasure(chosenExampleIdx: Int,
                                          chosenExample: T,
                                          examples: IndexedSeq[T],
                                          memberships: IndexedSeq[Int]): Double = {
    val examplesWithinClusterIndices = examples
      .indices
      .filter(exampleIdx => memberships(exampleIdx) == memberships(chosenExampleIdx))
    if (examplesWithinClusterIndices.size == 1) {
      // One element cluster. Punish this.
      // Original silhouette returns 0.
      0.1
    } else {
      // Original silhouette returns avg only.
      avg(examplesWithinClusterIndices
        .map(exampleIdx => distanceFun(examples(exampleIdx), chosenExample))) / (1 + Math.log10(examplesWithinClusterIndices.size))
    }
  }

  private def computeOutOfClusterMeasure(chosenExampleIdx: Int,
                                         chosenExample: T,
                                         examples: IndexedSeq[T],
                                         memberships: IndexedSeq[Int]): Double = {
    val outOfClusterIndices = examples
      .indices
      .filter(exampleIdx => memberships(exampleIdx) != memberships(chosenExampleIdx))
    val groupedByCluster = outOfClusterIndices
      .groupBy(outOfClusterExampleIdx => memberships(outOfClusterExampleIdx))
      .mapValues(inClusterExampleIndices =>
        inClusterExampleIndices.map(inClusterExampleIdx => examples(inClusterExampleIdx)))
    val avgMeasureForCluster = groupedByCluster
      .mapValues(inClusterExamples =>
        avg(inClusterExamples.map(clusterExample => distanceFun(clusterExample, chosenExample))))
    avgMeasureForCluster.values.min
  }

  private def avg(seq: Seq[Double]): Double = {
    seq.sum / seq.size
  }
}
