package ir.ham3da.darya.utility;

public class RateType
{

    public static final String PLUAS_RATE_COL = "plus_rate";
    public static final String NAGATIVE_RATE_COL = "negative_rate";

    public int plusRate;
    public int negativeRate;

    public RateType(int plus_Rate, int negative_Rate)
    {
        plusRate =  plus_Rate;
        negativeRate = negative_Rate;
    }

}
