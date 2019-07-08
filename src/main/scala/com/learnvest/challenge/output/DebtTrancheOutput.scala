package com.learnvest.challenge.output

case class DebtTrancheOutput(beginningBalance: Double,
                             expectedInterest: Double,
                             actualPayment: Double,
                             endingBalance: Double
                            ) extends Output
