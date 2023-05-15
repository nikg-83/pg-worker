package util;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

public class CurrencyUtilTest {

    public static void main(String[] args) throws ParseException {
        String amount1 = "500.00";
        String amount2 = "5,00.00";

        BigDecimal normalizedAmount1 = normalizeAmount(amount1);
        BigDecimal normalizedAmount2 = normalizeAmount(amount2);

        int comparisonResult = normalizedAmount1.compareTo(normalizedAmount2);

        if (comparisonResult < 0) {
            System.out.println(amount1 + " is less than " + amount2);
        } else if (comparisonResult > 0) {
            System.out.println(amount1 + " is greater than " + amount2);
        } else {
            System.out.println(amount1 + " is equal to " + amount2);
        }
    }

    private static BigDecimal normalizeAmount(String amountString) throws ParseException {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        BigDecimal amount = new BigDecimal(format.parse(amountString).toString());
        return amount.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
