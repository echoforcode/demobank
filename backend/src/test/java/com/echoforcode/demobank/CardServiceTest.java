package com.echoforcode.demobank;

import com.echoforcode.demobank.dto.CardAmountDto;
import com.echoforcode.demobank.dto.CardOperationResponse;
import com.echoforcode.demobank.dto.CardResponse;
import com.echoforcode.demobank.entity.Account;
import com.echoforcode.demobank.entity.Card;
import com.echoforcode.demobank.entity.Transaction;
import com.echoforcode.demobank.entity.Type;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.repository.AccountRepository;
import com.echoforcode.demobank.repository.CardRepository;
import com.echoforcode.demobank.service.CardService;
import com.echoforcode.demobank.service.TransactionService;
import com.echoforcode.demobank.util.RandomUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    @Mock CardRepository cards;
    @Mock AccountRepository accounts;
    @Mock
    TransactionService transactions;
    @Mock RandomUtil random;
    @InjectMocks
    CardService service;

    private static final long USD_NUMBER = 1234567890L;
    private static final long CARD_NUMBER = 1234123412341234L;
    private final User user = User.builder().uid("user-1")
            .firstname("Ada").lastname("Lovelace").build();

    private Account prepareUsdAccount(BigDecimal balance) {
        Account usd = Account.builder().accountNumber(USD_NUMBER)
                .code("USD").balance(balance).owner(user).build();
        when(accounts.findNumberForOwner("USD", "user-1"))
                .thenReturn(Optional.of(USD_NUMBER));
        when(accounts.findLocked(USD_NUMBER)).thenReturn(Optional.of(usd));
        return usd;
    }

    @Test
    void getCard_withoutCardReturns404() {
        when(cards.findByOwnerUid("user-1")).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.getCard(user));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getCard_existingCardReturnsMaskedNumber() {
        Card card = Card.builder().cardId("card-1").owner(user)
                .cardHolder("Ada Lovelace").cardNumber(CARD_NUMBER)
                .balance(new BigDecimal("40.00")).build();
        when(cards.findByOwnerUid("user-1")).thenReturn(Optional.of(card));

        CardResponse result = service.getCard(user);

        assertEquals("card-1", result.cardId());
        assertEquals("1234", result.lastFour());
        assertEquals("Ada Lovelace", result.cardHolder());
    }

    @Test
    void create_belowMinimumReturns400() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.create(new CardAmountDto(new BigDecimal("1.99")), user));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verifyNoInteractions(cards, accounts, transactions);
    }

    @Test
    void create_existingCardReturns409() {
        prepareUsdAccount(new BigDecimal("100.00"));
        when(cards.existsByOwnerUid("user-1")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.create(new CardAmountDto(new BigDecimal("10.00")), user));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("You already have a card", ex.getReason());
        verifyNoInteractions(transactions);
    }

    @Test
    void create_successDeductsFeeAndPersistsThreeTransactions() {
        Account usd = prepareUsdAccount(new BigDecimal("100.00"));
        when(random.generateRandom(16)).thenReturn(CARD_NUMBER);
        Transaction issue = Transaction.builder().txId("tx-fee").build();
        Transaction funding = Transaction.builder().txId("tx-fund").build();
        Transaction credit = Transaction.builder().txId("tx-credit").build();
        when(transactions.accountEntry(eq(usd), eq(new BigDecimal("1.00")),
                eq(BigDecimal.ZERO), eq(Type.WITHDRAW), isNull(), isNull()))
                .thenReturn(issue);
        when(transactions.accountEntry(eq(usd), eq(new BigDecimal("9.00")),
                eq(BigDecimal.ZERO), eq(Type.WITHDRAW), isNull(), isNull()))
                .thenReturn(funding);
        when(transactions.cardEntry(any(Card.class), eq(new BigDecimal("9.00")),
                eq(Type.CREDIT))).thenReturn(credit);

        CardOperationResponse result = service.create(
                new CardAmountDto(new BigDecimal("10.00")), user);

        assertEquals("CREATE", result.operation());
        assertEquals(0, usd.getBalance().compareTo(new BigDecimal("90.00")));
        assertEquals(0, result.card().balance().compareTo(new BigDecimal("9.00")));
        assertEquals(new BigDecimal("1.00"), result.fee());
        assertEquals(List.of("tx-fee", "tx-fund", "tx-credit"), result.transactionIds());
        verify(cards).saveAndFlush(any(Card.class));
        verify(transactions).persist(List.of(issue, funding, credit));
    }

    @Test
    void credit_successTransfersUsdToCard() {
        Account usd = prepareUsdAccount(new BigDecimal("100.00"));
        Card card = Card.builder().cardId("card-1").cardNumber(CARD_NUMBER)
                .owner(user).balance(new BigDecimal("5.00")).build();
        when(cards.findLockedByOwnerUid("user-1")).thenReturn(Optional.of(card));
        Transaction withdrawal = Transaction.builder().txId("tx-withdraw").build();
        Transaction credit = Transaction.builder().txId("tx-card").build();
        when(transactions.accountEntry(eq(usd), eq(new BigDecimal("30.00")),
                eq(BigDecimal.ZERO), eq(Type.WITHDRAW), isNull(), isNull()))
                .thenReturn(withdrawal);
        when(transactions.cardEntry(eq(card), eq(new BigDecimal("30.00")), eq(Type.CREDIT)))
                .thenReturn(credit);

        CardOperationResponse result = service.credit(
                new CardAmountDto(new BigDecimal("30.00")), user);

        assertEquals("CREDIT", result.operation());
        assertEquals(0, usd.getBalance().compareTo(new BigDecimal("70.00")));
        assertEquals(0, card.getBalance().compareTo(new BigDecimal("35.00")));
        verify(transactions).persist(List.of(withdrawal, credit));
    }

    @Test
    void debit_successReturnsFundsFromCardToUsd() {
        Account usd = prepareUsdAccount(new BigDecimal("100.00"));
        Card card = Card.builder().cardId("card-1").cardNumber(CARD_NUMBER)
                .owner(user).balance(new BigDecimal("30.00")).build();
        when(cards.findLockedByOwnerUid("user-1")).thenReturn(Optional.of(card));
        Transaction deposit = Transaction.builder().txId("tx-deposit").build();
        Transaction debit = Transaction.builder().txId("tx-card-debit").build();
        when(transactions.accountEntry(eq(usd), eq(new BigDecimal("20.00")),
                eq(BigDecimal.ZERO), eq(Type.DEPOSIT), isNull(), isNull()))
                .thenReturn(deposit);
        when(transactions.cardEntry(eq(card), eq(new BigDecimal("20.00")), eq(Type.DEBIT)))
                .thenReturn(debit);

        CardOperationResponse result = service.debit(
                new CardAmountDto(new BigDecimal("20.00")), user);

        assertEquals("DEBIT", result.operation());
        assertEquals(0, usd.getBalance().compareTo(new BigDecimal("120.00")));
        assertEquals(0, card.getBalance().compareTo(new BigDecimal("10.00")));
        verify(transactions).persist(List.of(deposit, debit));
    }

    @Test
    void credit_insufficientUsdReturns409WithoutTransactions() {
        prepareUsdAccount(new BigDecimal("5.00"));
        Card card = Card.builder().owner(user).balance(new BigDecimal("2.00")).build();
        when(cards.findLockedByOwnerUid("user-1")).thenReturn(Optional.of(card));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.credit(new CardAmountDto(new BigDecimal("10.00")), user));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verifyNoInteractions(transactions);
    }

    @Test
    void debit_insufficientCardFundsReturns409() {
        prepareUsdAccount(new BigDecimal("100.00"));
        Card card = Card.builder().owner(user).balance(new BigDecimal("5.00")).build();
        when(cards.findLockedByOwnerUid("user-1")).thenReturn(Optional.of(card));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.debit(new CardAmountDto(new BigDecimal("20.00")), user));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Insufficient funds", ex.getReason());
        verifyNoInteractions(transactions);
    }
}
