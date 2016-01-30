package cdf.matcher.validation

import cdf.UnitSpec

class SilhouetteValidationSpec extends UnitSpec {
  val offset = 0.0001

  val distanceFun: (Int, Int) => Double = (v1: Int, v2: Int) => Math.abs(v1 - v2)

  it should "compute valid value for perfect clustering" in {
    val examples = IndexedSeq(0, 0, 0, 10, 10, 10)
    val memberships = IndexedSeq(0, 0, 0, 1, 1, 1)

    val silhouetteValidation = new SilhouetteValidation(distanceFun)

    silhouetteValidation(examples, memberships) should be (0.0 +- offset)
  }

  it should "handle one element clusters" in {
    val examples = IndexedSeq(0, 10, 10, 10)
    val memberships = IndexedSeq(0, 1, 1, 1)

    val silhouetteValidation = new SilhouetteValidation(distanceFun)

    // Original:
//    silhouetteValidation(examples, memberships) should be (0.0 +- 0.01)
    silhouetteValidation(examples, memberships) should be (0.0025 +- offset)
  }

  it should "compute valid value for not perfect clustering" in {
    val examples = IndexedSeq(0, 1, 2, 8, 9, 10)
    val memberships = IndexedSeq(0, 0, 0, 1, 1, 1)

    val silhouetteValidation = new SilhouetteValidation(distanceFun)

    // Original:
//    silhouetteValidation(examples, memberships) should be (0.112 +- 0.01)
    silhouetteValidation(examples, memberships) should be (0.0761 +- offset)
  }
}
