package com.learnvest.challenge;

import Helpers.PaymentBreakDown;
import Helpers.Helpers;

import com.learnvest.challenge.input.Collateral;
import com.learnvest.challenge.input.DebtTranche;
import com.learnvest.challenge.input.EquityTranche;
import com.learnvest.challenge.input.Tranche;
import com.learnvest.challenge.output.DebtTrancheOutput;
import com.learnvest.challenge.output.EquityTrancheOutput;
import com.learnvest.challenge.output.Output;
import scala.collection.convert.Wrappers;
import scala.collection.mutable.MultiMap;

import java.util.*;


/**
 * Possible weird cases:
 * 1) No equity tranche?
 * 2) Negative interest rate on the debt or loans?
 * 3) Complete repayment due to prepayment?
 * 4) negative years... should be an error here.
 * 5) negative dollar amounts... should probably only error on collateral
 */

/**
 * Now a working implementation, only used for sample test.
 *
 * Extend this trait and implement the method in your own code. We will be running tests against this method signature.
 * Lists of output objects should represent the same step in time. If there is no payment for a tranche in a specific time
 * step make sure to still output a value that include beginning and ending balance for the debt tranches or Zero for the
 * payment received for the equity tranche.
 */

public class DummyJavaImplementation implements JavaChallengeImplementation{

    private Map<Integer, PaymentBreakDown> paymentBreakDownMapByYear = new LinkedHashMap<>();

    //Precondition: at least 1 debt tranche, and only 1 equity tranche
    @Override
    public Map<Tranche, List<Output>> modelCDO(List<Tranche> tranches, Collateral collateral) {

        Map<Tranche, List<Output>> trancheListMap = new IdentityHashMap<>(); //insert order not maintained

        //method signature needs to be left intact; just return null for validation checks for now
        if (!InputsAreValid(collateral, tranches)){
            return null;
        }

        //create payment schedule
        CalculateAmortizationSchedule(collateral);

        //calculate tranche outputs
        for(Tranche item : tranches){
            if (item instanceof DebtTranche){
                DebtTranche temp = (DebtTranche)item;
               // DebtTranche temp2 = new DebtTranche((temp.initialPrincipal()), temp.rateOfReturn());
                trancheListMap.put(temp, (List<Output>)CalculateDebtTrancheOutput(temp, collateral));
            }
            else if (item instanceof EquityTranche){
                EquityTranche temp = (EquityTranche)item;
                trancheListMap.put(item, (List<Output>) CalculateEquityTranche(temp));
            }
        }

        return trancheListMap;
    }

    //Returns true/false:
    //true = all inputs are valid
    //false = at least one input is invalid
    private boolean InputsAreValid(Collateral collateral, List<Tranche> tranchesList){
        if ((collateral.numberOfYears() < 0 ) || (collateral.beginningBalance() < 0) || tranchesList.size() <= 0) {
            return false;
        }

        for (Tranche item:tranchesList){
            if (!((item instanceof DebtTranche) || (item instanceof EquityTranche))){
                return false;
            }
        }
        return true;
    }

    //Calculate equity tranche
    private List<? extends Output> CalculateEquityTranche (EquityTranche equityTranche){
        List<EquityTrancheOutput> trancheOutputs = new ArrayList<>();

        for (int i=1; i<=paymentBreakDownMapByYear.size(); i++)
        {
            trancheOutputs.add(new EquityTrancheOutput(Helpers.CurrencyRounding(paymentBreakDownMapByYear.get(i).interestPortionLeft + paymentBreakDownMapByYear.get(i).principalPortionLeft)));
        }

        return trancheOutputs;
    }


    //calculate debt tranche output
    private List<? extends Output> CalculateDebtTrancheOutput(DebtTranche aTranche, Collateral collateral)
    {
        List<DebtTrancheOutput> trancheOutputs = new ArrayList<>();
        PaymentBreakDown tempPayItem;

        double beginningTrancheBalance = aTranche.initialPrincipal();
        double expectedTrancheInterest,
                endingTrancheBalance,
                actualPayment = 0,
                interestMoneyLeft,
                principalMoneyLeft;

        //pay interest from interest part of payment
        //if money is left, pay down principal
        for (int i=1; i <= collateral.numberOfYears(); i++){

            tempPayItem = paymentBreakDownMapByYear.get(i);

            interestMoneyLeft = Helpers.CurrencyRounding(tempPayItem.interestPortionLeft);
            principalMoneyLeft = Helpers.CurrencyRounding(tempPayItem.principalPortionLeft);

            //For a Sequential Tranche (only ones we're handling), it receives nothing if it has no balance
            if (beginningTrancheBalance <= 0){
                trancheOutputs.add(new DebtTrancheOutput(0, 0, 0, 0));
                continue;
            }

            //our expected interest for this tranche+year
            expectedTrancheInterest = Helpers.CurrencyRounding((aTranche.rateOfReturn() * beginningTrancheBalance));
            endingTrancheBalance = beginningTrancheBalance;

            //handle interest portion
            endingTrancheBalance += expectedTrancheInterest; //add the expected interest into the end balance then subtract it out as it's paid
            if (interestMoneyLeft > 0){
                if (interestMoneyLeft >= expectedTrancheInterest){
                    actualPayment += expectedTrancheInterest;
                    interestMoneyLeft -=  expectedTrancheInterest;
                }
                else{
                    actualPayment += interestMoneyLeft;
                    endingTrancheBalance += (expectedTrancheInterest - interestMoneyLeft); //add to principal unpaid portion
                    interestMoneyLeft = 0;
                }
            }

            //handle principal portion - the most senior tranche receives all principal until it is paid off
            //then it receives neither interest nor principal
            if (beginningTrancheBalance >= principalMoneyLeft){
                actualPayment += principalMoneyLeft;
                endingTrancheBalance -= principalMoneyLeft;
                principalMoneyLeft = 0;
            }
            else{
                actualPayment += beginningTrancheBalance;
                endingTrancheBalance = 0;
                principalMoneyLeft -= beginningTrancheBalance;
            }

            //add output for this year of the tranche
            trancheOutputs.add(new DebtTrancheOutput(Helpers.CurrencyRounding(beginningTrancheBalance), Helpers.CurrencyRounding(expectedTrancheInterest),
                    Helpers.CurrencyRounding(actualPayment), Helpers.CurrencyRounding(endingTrancheBalance)));
            tempPayItem.principalPortionLeft = Helpers.CurrencyRounding(principalMoneyLeft);
            tempPayItem.interestPortionLeft = Helpers.CurrencyRounding(interestMoneyLeft);
            tempPayItem.collateralBalance -= principalMoneyLeft;

            paymentBreakDownMapByYear.put(i,tempPayItem);

            beginningTrancheBalance = Helpers.CurrencyRounding(endingTrancheBalance); //the next beginning balance for this tranche is the ending balance of the current year
        }
        return trancheOutputs;
    }

    //Amortization Schedule creation
    private void CalculateAmortizationSchedule(Collateral collateral){
        double currentBalance = 0;
        double totalPayment =0;
        double interestPortion,
                principalPortion;

        PaymentBreakDown tempPaymentBreakDownItem;

        for (int i =0; i<= collateral.numberOfYears(); i++){
            if (i==0){
                tempPaymentBreakDownItem = CalculateAmortization(collateral);
                currentBalance = collateral.beginningBalance();
                totalPayment = tempPaymentBreakDownItem.totalPayment;
            }
            else{
                interestPortion = collateral.rateOfReturn() * currentBalance;
                principalPortion =  totalPayment - interestPortion;
                currentBalance -= principalPortion;

                if (collateral.prePaymentRate()>0){
                    tempPaymentBreakDownItem = CalculateAmortization((collateral.numberOfYears() - i), collateral.rateOfReturn(), currentBalance, collateral.prePaymentRate());
                    paymentBreakDownMapByYear.put(i, tempPaymentBreakDownItem);
                    totalPayment = tempPaymentBreakDownItem.totalPayment;
                }
                else{
                    paymentBreakDownMapByYear.put(i, new PaymentBreakDown(Helpers.CurrencyRounding(interestPortion),
                            Helpers.CurrencyRounding(principalPortion),
                            Helpers.CurrencyRounding(currentBalance),
                            collateral.rateOfReturn(),
                            Helpers.CurrencyRounding(totalPayment)));
                }
            }

        }
    }

    //Calculate initial amortization with Collateral object
    private PaymentBreakDown CalculateAmortization(Collateral collateral){
        double interestPortion = Helpers.CurrencyRounding((collateral.rateOfReturn() * collateral.beginningBalance()));
        double rate = collateral.rateOfReturn(); //periodic rate = rate / periods which are the same for this

        //r(1+r)^n
        double numerator = collateral.rateOfReturn() * (Math.pow(1 + collateral.rateOfReturn(),collateral.numberOfYears()));
        double denominator = Math.pow(1+collateral.rateOfReturn(), collateral.numberOfYears()) -1;
        double payment = Helpers.CurrencyRounding((collateral.beginningBalance() * (numerator / denominator)));

        return new PaymentBreakDown(Helpers.CurrencyRounding(interestPortion),
                Helpers.CurrencyRounding((payment-interestPortion) + (collateral.beginningBalance() * collateral.prePaymentRate())),
                Helpers.CurrencyRounding(collateral.beginningBalance()),
                rate, Helpers.CurrencyRounding(payment));

    }

    private PaymentBreakDown CalculateAmortization(int yearsLeft, double rate, double currentBalance, double prePaymentRate){
        double interestPortion = Helpers.CurrencyRounding((rate * currentBalance));
        double newRate = rate;

        double numerator = rate * (Math.pow(1 + rate,yearsLeft));
        double denominator = Math.pow(1 + rate, yearsLeft) -1;
        double newPayment = Helpers.CurrencyRounding((currentBalance * (numerator / denominator)));

        return new PaymentBreakDown(Helpers.CurrencyRounding(interestPortion),
                Helpers.CurrencyRounding((newPayment - interestPortion) + (prePaymentRate * currentBalance)),
                Helpers.CurrencyRounding(currentBalance),
                newRate, Helpers.CurrencyRounding(newPayment));
    }
}