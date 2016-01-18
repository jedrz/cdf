package cdf.matcher.distance

import cdf.offer.Offer

trait DistanceMeasure {
  def distance(offer1: Offer, offer2: Offer): Double
}
