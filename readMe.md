Financial Algorithm Challenge
=======================

Welcome to your challenge project!

For this challenge, we ask that you implement a solution at home in your own time. When you're ready, please send us your code or a link to your github repo where we can clone the results.


The Details
-----------
Your challenge is to model a basic Sequential CDO structure. A CDO is a financial instrument composed of tranches. Each tranche is defined by it's expected cash flow. Those expected cashflows are paid from the payments produced by some pool of collateral. The CDO you will be modeling has two types of tranches.

* Debt Tranche

A debt tranche is defined by its principal balance and yield. Each year you will need to calculate the expected payment due and reduce the principal balance on the tranche. There will be one or more debt tranches in the CDO.


* Equity Tranche

An equity tranche is designed to absorb the losses on other tranches. It is paid after all other tranches have received their interest payments, if all other tranche principal balances have been paid off then it also receives the principal payment. At the end of the year if there is money left over the equity tranche receives the entire payment. It is defined only through a face value paid for the tranche. There is a single equity tranche for the entire CDO.

Within the CDO structure each Debt tranche has a priority that does not change.
Debt tranches are paid in the order of their priority.
You will be generating a payment coming into the CDO based on Collateral information.
You will need to calculate the portion of the payment coming from interest and principal from the collateral.
If the interest received doesn't cover the interest owed then the highest priority tranches are paid first, some tranches may not receive any payments.

After the interest has been paid the principal is distributed.
The principal is paid off in order of highest priority until the most senior tranche is paid completely off.
Any remaining payments will go to the next most senior tranche, until all tranches are paid off.
The equity tranches takes any remaining payments if all debt tranches have been paid off.

* Collateral

The collateral information you will receive has a balance, interest rate, and prepayment rate.
 The payments are generated off this collateral are amortized (https://www.vertex42.com/ExcelArticles/amortization-calculation.html) and can be assumed to be made annually.

The pre-payment rate directly reduces the principal balance of the mortgage.
So a 10% pre-payment rate on 100k would assume that 10K of the principal has been paid off in the current year.
This is in addition to the principal paid down due to amortization.
In the following year you will need to calculate a new amortization based on the new balance, if there were prepayments.
If there were no pre-payments then the prior payment schedule is still valid.

Output
------
You should output for each year for each tranche the following fields:
* Beginning Balance of the Tranche
* Expected Payment of the Tranche (interest only based on the beginning balance)
* Actual Payment of the Tranche (interest and principal)
* Ending Balance 


Starter Kit
-----------
This starter kit is an sbt project. You will need to have scala and sbt
on your machine. You can implement the solution in either scala or java.
For a java implement the JavaChallengeImplementation interface. For
scala extend the ScalaChallengeImplementation trait.

The tranche objects, collateral, and Output classes are what we will pass
you. You can change these classes and add functionality but please don't
change the primary constructor signatures as
we will be using them to test your solution.


What we are looking for
--------------------
It's ok if you've never seen a CDO before. Many members of our team haven't. What we are looking for is the ability to write clean extendable code that is easy to modify for more complex structures and the ability to understand financial concepts. 