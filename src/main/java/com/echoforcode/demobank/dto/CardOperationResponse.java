package com.echoforcode.demobank.dto;

import java.math.BigDecimal;
import java.util.List;
public record CardOperationResponse(
        String operation, BigDecimal amount, BigDecimal fee,
        BigDecimal usdBalance, CardResponse card,
        List<String> transactionIds
) {}