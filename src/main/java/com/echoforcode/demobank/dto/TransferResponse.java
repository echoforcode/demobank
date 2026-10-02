package com.echoforcode.demobank.dto;

import java.math.BigDecimal;
public record TransferResponse(
        String transactionId, String currency, BigDecimal amount,
        BigDecimal fee, BigDecimal totalDebited, BigDecimal senderBalance,
        long recipientAccountNumber, String status
) {}
