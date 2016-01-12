package cdf.matcher

import cdf.offer.{Offer, OffersGroups}

trait OfferMatcher {
  def withOffer(offer: Offer): OfferMatcher
  def compute: OffersGroups
}
