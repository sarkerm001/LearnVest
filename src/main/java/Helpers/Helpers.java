package Helpers;

public class Helpers {
    public static double CurrencyRounding(double value){
        return (((int)(Math.round(value* 100.0 ))) / 100.0);
    }
}
