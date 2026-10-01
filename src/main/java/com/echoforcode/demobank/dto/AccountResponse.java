package com.echoforcode.demobank.dto;

import com.echoforcode.demobank.entity.Account;
import java.math.BigDecimal;

public record AccountResponse(String accountId, long accountNumber,
                              BigDecimal balance, String code, String label, char symbol) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getAccountId(),
                account.getAccountNumber(), account.getBalance(),
                account.getCode(), account.getLabel(), account.getSymbol());
    }
}
