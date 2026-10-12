package com.echoforcode.demobank.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
public final class FxMath {
    public static final Set<String> CODES =
            Set.of("USD", "EUR", "GBP", "JPY", "NGN", "INR");
    private static final BigDecimal FEE_RATE = new BigDecimal("0.01");
    private FxMath() {}
    public record Quote(BigDecimal amount, BigDecimal fee,
                        BigDecimal totalDebited, BigDecimal convertedAmount) {}
    public static Quote calculate(BigDecimal amount,
                                  String from, String to, BigDecimal sourceRate,
                                  BigDecimal destinationRate) {
        if (!CODES.contains(from) || !CODES.contains(to)) {
            throw new IllegalArgumentException("Unsupported currency code");
        }
        if (from.equals(to)) {
                throw new IllegalArgumentException("Choose different currencies");}
        if (amount.signum() <= 0) {
                    throw new IllegalArgumentException("Amount must be positive");
        }

        //for JPY,the smallest number is 1 Yen
        int fromScale = from.equals("JPY") ? 0 : 2;
        int toScale = to.equals("JPY") ? 0 : 2;
        try {
            amount = amount.setScale(fromScale, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException(
                    "Amount has too many decimals for the source currency");
        }
        if (sourceRate.signum() <= 0 || destinationRate.signum() <= 0) {
            throw new IllegalArgumentException("Rates must be positive");
        }
        BigDecimal fee = amount.multiply(FEE_RATE)
                .setScale(fromScale, RoundingMode.HALF_UP);
        BigDecimal converted = amount.multiply(destinationRate)
                .divide(sourceRate, toScale, RoundingMode.HALF_UP);
        if (converted.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Amount is too small in the destination currency");
        }
        return new Quote(amount, fee, amount.add(fee), converted);
    }
}