package com.echoforcode.demobank.service;

import com.echoforcode.demobank.dto.ConvertDto;
import com.echoforcode.demobank.dto.ConversionResponse;
import com.echoforcode.demobank.entity.*;
import com.echoforcode.demobank.repository.AccountRepository;
import com.echoforcode.demobank.util.FxMath;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
@Service
@RequiredArgsConstructor
public class ConversionService {
    private final AccountRepository accounts;
    private final TransactionService transactions;
    private final ExchangeRateService exchangeRateService;
    private static final BigDecimal MAX_BALANCE =
            new BigDecimal("99999999999999999.99");
    @Transactional
    public ConversionResponse convert(ConvertDto dto, User user) {
        String from = dto.fromCurrency().toUpperCase(Locale.ROOT);
        String to = dto.toCurrency().toUpperCase(Locale.ROOT);
        if (!FxMath.CODES.contains(from) || !FxMath.CODES.contains(to)) {
            throw error(HttpStatus.BAD_REQUEST, "Unsupported currency code");
        }
        if (from.equals(to)) {
            throw error(HttpStatus.BAD_REQUEST, "Choose different currencies");
        }
        long fromNumber = number(from, user.getUid());
        long toNumber = number(to, user.getUid());
// Same global lock order as TransferService.
        Account first = lock(Math.min(fromNumber, toNumber));
        Account second = lock(Math.max(fromNumber, toNumber));
        Account source = first.getAccountNumber() == fromNumber ? first : second;
        Account destination = source == first ? second : first;
        if (!source.getOwner().getUid().equals(user.getUid()) ||
                !destination.getOwner().getUid().equals(user.getUid())) {
            throw error(HttpStatus.FORBIDDEN, "Invalid account owner");
        }
        // Read ONE fresh cache snapshot; this makes no external HTTP call.
        var snapshot = exchangeRateService.current();
        BigDecimal fromRate = snapshot.rates().get(from);
        BigDecimal toRate = snapshot.rates().get(to);
        if (fromRate == null || toRate == null ||
                fromRate.signum() <= 0 || toRate.signum() <= 0) {
            throw error(HttpStatus.SERVICE_UNAVAILABLE, "Rates unavailable");
        }
        FxMath.Quote quote;
        try {
            quote = FxMath.calculate(dto.amount(), from, to, fromRate, toRate);
        } catch (IllegalArgumentException ex) {
            throw error(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        if (source.getBalance().compareTo(quote.totalDebited()) < 0) {
            throw error(HttpStatus.CONFLICT, "Insufficient funds including fee");
        }
        BigDecimal newBalance = destination.getBalance()
                .add(quote.convertedAmount());
        if (newBalance.compareTo(MAX_BALANCE) > 0) {
            throw error(HttpStatus.CONFLICT, "Destination balance limit exceeded");
        }
        source.setBalance(source.getBalance().subtract(quote.totalDebited()));
        destination.setBalance(newBalance);
        Transaction debit = entry(source, fromNumber, toNumber,
                quote.amount(), quote.fee(), Type.CONVERSION);
        Transaction credit = entry(destination, fromNumber, toNumber,
                quote.convertedAmount(), BigDecimal.ZERO, Type.DEPOSIT);
        transactions.persist(List.of(debit, credit));
        return new ConversionResponse(debit.getTxId(), credit.getTxId(),
                from, to, quote.amount(), quote.fee(), quote.totalDebited(),
                quote.convertedAmount(), source.getBalance(), newBalance,
                fromRate, toRate, snapshot.asOf(), "COMPLETED");
    }
    private long number(String code, String uid) {
        return accounts.findNumberForOwner(code, uid).orElseThrow(() ->
                error(HttpStatus.NOT_FOUND, "You have no " + code + " account"));
    }
    private Account lock(long number) {
        return accounts.findLocked(number).orElseThrow(() ->
                error(HttpStatus.NOT_FOUND, "Account not found"));
    }
    private Transaction entry(Account account, long from, long to,
                              BigDecimal amount, BigDecimal fee, Type type) {
        return transactions.accountEntry(account, amount, fee, type,
                Long.toString(from), Long.toString(to));
    }
    private ResponseStatusException error(HttpStatus status, String message) {
            return new ResponseStatusException(status, message);
    }

}
