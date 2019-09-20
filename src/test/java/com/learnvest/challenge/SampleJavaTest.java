package com.learnvest.challenge;

import com.learnvest.challenge.input.Collateral;
import com.learnvest.challenge.input.DebtTranche;
import com.learnvest.challenge.input.EquityTranche;
import com.learnvest.challenge.input.Tranche;
import com.learnvest.challenge.output.EquityTrancheOutput;
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
        assertFalse(results.isEmpty());
    }

    /********
     * Note:Inserted scenarios are not correctly asserted/checked
     * only used to step thru code for each scenario.
     */
    //tranche interest = interest in and only 1 year duration
    @Test
    public void SingleTrancheTest(){
        Tranche AAA =  new DebtTranche(1000, .01);
        Tranche equity = new EquityTranche(100);

        List<Tranche> allTranches = new LinkedList<>();
        allTranches.add(AAA);
        allTranches.add(equity);

        Collateral collateral = new Collateral(1000, 1, .01, .00);

        Map<Tranche, List<Output>> results = new DummyJavaImplementation().modelCDO(allTranches, collateral);
        assertFalse(results.isEmpty());
    }

    //equity should only increase
    @Test
    public void EquityTrancheIncrementTest(){
        Tranche AAA =  new DebtTranche(0, .05);
        Tranche AA = new DebtTranche(0, .1);
        Tranche equity = new EquityTranche(5000);

        List<Tranche> allTranches = new LinkedList<>();
        allTranches.add(AAA);
        allTranches.add(AA);
        allTranches.add(equity);

        Collateral collateral = new Collateral(2000, 5, .05, .00);

        Map<Tranche, List<Output>> results = new DummyJavaImplementation().modelCDO(allTranches, collateral);
        assertFalse(results.isEmpty());
    }

    //prepayment
    @Test
    public void PrepayTest(){
        Tranche AAA =  new DebtTranche(0, .05);
        Tranche AA = new DebtTranche(2000, .1);
        Tranche equity = new EquityTranche(1000);

        List<Tranche> allTranches = new LinkedList<>();
        allTranches.add(AAA);
        allTranches.add(AA);
        allTranches.add(equity);

        Collateral collateral = new Collateral(2000, 5, .05, .10);

        Map<Tranche, List<Output>> results = new DummyJavaImplementation().modelCDO(allTranches, collateral);
        assertFalse(results.isEmpty());
    }

}
