package cdf.matcher

import cdf.offer.Offer

trait OfferMatcher {
  def withOffer(offer: Offer): OfferMatcher
  def compute: SimilarityMatrixResult
}
