package cdf.matcher

sealed trait OfferMatcherResult

case class SimilarityMatrixResult(matrix: Array[Array[Double]]) extends OfferMatcherResult
