package com.echoforcode.demobank.dto;

import com.echoforcode.demobank.entity.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record TransactionResponse(
        String txId, String type, String status, String currency,
        BigDecimal amount, BigDecimal txFee,
        String accountId, String cardId,
        String sender, String receiver, LocalDateTime createdAt
) {
    public static TransactionResponse from(Transaction tx) {
        var account = tx.getAccount();
        var card = tx.getCard();
        return new TransactionResponse(tx.getTxId(),
                tx.getType() == null ? null : tx.getType().name(),
                tx.getStatus() == null ? null : tx.getStatus().name(),
                account != null ? account.getCode() : card != null ? "USD" : null,
                tx.getAmount(), tx.getTxFee(),
                account == null ? null : account.getAccountId(),
                card == null ? null : card.getCardId(),
                tx.getSender(), tx.getReceiver(), tx.getCreatedAt());
    }
}
