package com.echoforcode.demobank.dto;

import java.util.List;
import org.springframework.data.domain.Page;
import com.echoforcode.demobank.entity.Transaction;
public record TransactionPage(
        List<TransactionResponse> items, int page, int size,
        long totalItems, int totalPages
) {
    public static TransactionPage from(Page<Transaction> result) {
        return new TransactionPage(result.getContent().stream()
                .map(TransactionResponse::from).toList(),
                result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }
}