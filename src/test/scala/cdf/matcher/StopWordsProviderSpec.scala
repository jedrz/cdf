package cdf.matcher

import cdf.UnitSpec

class StopWordsProviderSpec extends UnitSpec {
  val provider = new StopWordsProvider

  it should "contain word from list" in {
    provider.contains("albo") should be (true)
  }

  it should "ignore case" in {
    provider.contains("KToŚ") should be (true)
  }

  it should "not contain word from list" in {
    provider.contains("cdf") should be (false)
  }
}
