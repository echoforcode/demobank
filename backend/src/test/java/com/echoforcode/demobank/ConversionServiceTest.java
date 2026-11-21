package com.echoforcode.demobank;

import com.echoforcode.demobank.dto.ConvertDto;
import com.echoforcode.demobank.dto.ConversionResponse;
import com.echoforcode.demobank.entity.Account;
import com.echoforcode.demobank.entity.Transaction;
import com.echoforcode.demobank.entity.Type;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.repository.AccountRepository;
import com.echoforcode.demobank.service.ConversionService;
import com.echoforcode.demobank.service.ExchangeRateService;
import com.echoforcode.demobank.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {
    @Mock AccountRepository accounts;
    @Mock
    TransactionService transactions;
    @Mock
    ExchangeRateService exchangeRateService;
    @InjectMocks
    ConversionService service;

    private final User user = User.builder().uid("user-1").build();
    private static final long USD_NUMBER = 1234567890L;
    private static final long EUR_NUMBER = 1234567891L;

    private Account[] prepareAccounts() {
        Account usd = Account.builder().accountNumber(USD_NUMBER).code("USD")
                .owner(user).balance(new BigDecimal("100.00")).build();
        Account eur = Account.builder().accountNumber(EUR_NUMBER).code("EUR")
                .owner(user).balance(new BigDecimal("5.00")).build();
        when(accounts.findNumberForOwner("USD", "user-1"))
                .thenReturn(Optional.of(USD_NUMBER));
        when(accounts.findNumberForOwner("EUR", "user-1"))
                .thenReturn(Optional.of(EUR_NUMBER));
        when(accounts.findLocked(USD_NUMBER)).thenReturn(Optional.of(usd));
        when(accounts.findLocked(EUR_NUMBER)).thenReturn(Optional.of(eur));
        return new Account[]{usd, eur};
    }

    private void prepareRates() {
        Instant now = Instant.now();
        when(exchangeRateService.current()).thenReturn(
                new ExchangeRateService.RateSnapshot("USD", now, now,
                        Map.of("USD", BigDecimal.ONE, "EUR", new BigDecimal("0.90"))));
    }

    @Test
    void convert_usdToEur_updatesBothBalancesAndPersistsTwoEntries() {
        Account[] pair = prepareAccounts();
        Account usd = pair[0];
        Account eur = pair[1];
        prepareRates();
        Transaction debit = Transaction.builder().txId("tx-debit").build();
        Transaction credit = Transaction.builder().txId("tx-credit").build();
        when(transactions.accountEntry(eq(usd), eq(new BigDecimal("10.00")),
                eq(new BigDecimal("0.10")), eq(Type.CONVERSION),
                eq("1234567890"), eq("1234567891"))).thenReturn(debit);
        when(transactions.accountEntry(eq(eur), eq(new BigDecimal("9.00")),
                eq(BigDecimal.ZERO), eq(Type.DEPOSIT),
                eq("1234567890"), eq("1234567891"))).thenReturn(credit);

        ConversionResponse result = service.convert(
                new ConvertDto("usd", "eur", new BigDecimal("10.00")), user);

        assertEquals("COMPLETED", result.status());
        assertEquals("USD", result.fromCurrency());
        assertEquals("EUR", result.toCurrency());
        assertEquals(0, result.fee().compareTo(new BigDecimal("0.10")));
        assertEquals(0, result.convertedAmount().compareTo(new BigDecimal("9.00")));
        assertEquals(0, usd.getBalance().compareTo(new BigDecimal("89.90")));
        assertEquals(0, eur.getBalance().compareTo(new BigDecimal("14.00")));
        verify(transactions).persist(java.util.List.of(debit, credit));
    }

    @Test
    void convert_sameCurrency_returns400WithoutDatabaseCalls() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.convert(new ConvertDto("USD", "USD", new BigDecimal("5.00")), user));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Choose different currencies", ex.getReason());
        verifyNoInteractions(accounts, transactions, exchangeRateService);
    }

    @Test
    void convert_unsupportedCurrency_returns400() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.convert(new ConvertDto("ABC", "EUR", new BigDecimal("5.00")), user));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Unsupported currency code", ex.getReason());
        verifyNoInteractions(accounts, transactions, exchangeRateService);
    }

    @Test
    void convert_insufficientFunds_returns409WithoutBalanceChange() {
        Account[] pair = prepareAccounts();
        prepareRates();
        BigDecimal beforeUsd = pair[0].getBalance();
        BigDecimal beforeEur = pair[1].getBalance();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.convert(new ConvertDto("USD", "EUR", new BigDecimal("100.00")), user));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Insufficient funds including fee", ex.getReason());
        assertEquals(beforeUsd, pair[0].getBalance());
        assertEquals(beforeEur, pair[1].getBalance());
        verifyNoInteractions(transactions);
    }

    @Test
    void convert_ratesUnavailable_returns503() {
        prepareAccounts();
        Instant now = Instant.now();
        when(exchangeRateService.current()).thenReturn(
                new ExchangeRateService.RateSnapshot("USD", now, now,
                        Map.of("USD", BigDecimal.ONE))); // EUR missing

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.convert(new ConvertDto("USD", "EUR", new BigDecimal("10.00")), user));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
        verifyNoInteractions(transactions);
    }
}
