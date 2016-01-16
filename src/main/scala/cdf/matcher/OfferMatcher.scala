package cdf.matcher

trait OfferMatcher[R <: OfferMatcherResult] {
  def compute: R
}
