package com.learnvest.challenge;

import com.learnvest.challenge.input.Collateral;
import com.learnvest.challenge.input.DebtTranche;
import com.learnvest.challenge.input.EquityTranche;
import com.learnvest.challenge.input.Tranche;
import com.learnvest.challenge.output.Output;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

public class SampleJavaTest {

    @Test
    public void sampleTest(){
        Tranche AAA =  new DebtTranche(100000, .05);
        Tranche AA = new DebtTranche(100000, .05);
        Tranche A = new DebtTranche(100000, .05);
        Tranche BBB = new DebtTranche(100000, .05);
        Tranche equity = new EquityTranche(50000);

        List<Tranche> allTranches = new LinkedList<>();
        allTranches.add(AAA);
        allTranches.add(AA);
        allTranches.add(A);
        allTranches.add(BBB);
        allTranches.add(equity);

        Collateral collateral = new Collateral(500000, 25, .07, .10);

        Map<Tranche, List<Output>> results = new DummyJavaImplementation().modelCDO(allTranches, collateral);
        assertTrue(results.isEmpty());
    }
}
