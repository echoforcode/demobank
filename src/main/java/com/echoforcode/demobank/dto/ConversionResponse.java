package com.echoforcode.demobank.dto;

import java.math.BigDecimal;
import java.time.Instant;
public record ConversionResponse(
        String sourceTransactionId, String destinationTransactionId,
        String fromCurrency, String toCurrency,
        BigDecimal amount, BigDecimal fee, BigDecimal totalDebited,
        BigDecimal convertedAmount,
        BigDecimal sourceBalance, BigDecimal destinationBalance,
        BigDecimal sourceRate, BigDecimal destinationRate,
        Instant rateAsOf, String status
) {}