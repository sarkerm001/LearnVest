package com.learnvest.challenge

import com.learnvest.challenge.input.{Collateral, DebtTranche, EquityTranche, Tranche}
import com.learnvest.challenge.output.Output
import org.scalatest.FunSuite

class SampleScalaTest extends FunSuite {

  test("A sample test"){
    val tranches = List(
      DebtTranche(100000, .05),
      DebtTranche(100000, .05),
      DebtTranche(100000, .05),
      DebtTranche(100000, .05),
      EquityTranche(50000)
    )

    val collateral = Collateral(500000, 25, .07, .10)

    val results: Map[Tranche, List[Output]] = new DummyScalaImplementation().modelCDO(tranches, collateral)
    assert(results.isEmpty)
  }
}
