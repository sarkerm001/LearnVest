package Helpers;

public class PaymentBreakDown{
    public double interestPortionLeft;
    public double principalPortionLeft;
    public double collateralBalance;
    public double periodicRate;
    public double totalPayment;

    public PaymentBreakDown(double interest, double principal, double acollateralBalance, double rate, double payment){
        interestPortionLeft = interest;
        principalPortionLeft = principal;
        collateralBalance = acollateralBalance;
        periodicRate = rate;
        totalPayment = payment;
    }
}