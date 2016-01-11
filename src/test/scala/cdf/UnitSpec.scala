package cdf

import org.scalamock.scalatest.MockFactory
import org.scalatest.prop.PropertyChecks
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpecLike, Matchers}

trait UnitSpec
  extends FlatSpecLike
  with Matchers
  with BeforeAndAfter
  with BeforeAndAfterAll
  with PropertyChecks
  with MockFactory
