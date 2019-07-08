package com.learnvest.challenge
import com.learnvest.challenge.input.{Collateral, Tranche}
import com.learnvest.challenge.output.Output

/**
  * Not a working implementation, only used for sample test.
  */
class DummyScalaImplementation extends ScalaChallengeImplementation {
  override def modelCDO(tranches: List[Tranche], collateral: Collateral): Map[Tranche, List[Output]] = {
       Map()
  }
}
