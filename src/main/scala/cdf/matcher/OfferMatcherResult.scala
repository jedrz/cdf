package cdf.matcher

import cdf.offer.Offer

sealed trait OfferMatcherResult

case class SimilarityMatrixResult(matrix: Array[Array[Double]]) extends OfferMatcherResult
case class OffersClusteringResult(clusters: Vector[Vector[Offer]]) extends OfferMatcherResult
