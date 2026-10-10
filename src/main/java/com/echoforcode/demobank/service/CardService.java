package com.echoforcode.demobank.service;

import com.echoforcode.demobank.dto.*;
import com.echoforcode.demobank.entity.*;
import com.echoforcode.demobank.repository.AccountRepository;
import com.echoforcode.demobank.repository.CardRepository;
import com.echoforcode.demobank.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cards;
    private final AccountRepository accounts;
    private final TransactionService transactions;
    private final RandomUtil random;
    private static final BigDecimal ISSUE_FEE = new BigDecimal("1.00");
    private static final BigDecimal MAX_BALANCE =
            new BigDecimal("99999999999999999.99");

    @Transactional(readOnly = true)
    public CardResponse getCard(User user) {
        return CardResponse.from(cards.findByOwnerUid(user.getUid())
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND,
                        "You have not created a card yet")));
    }

    /**
     * Debits USD once, issues one demo card and stores three history rows.
     */
    @Transactional
    public CardOperationResponse create(CardAmountDto dto, User user) {
        BigDecimal amount = amount(dto);
        if (amount.compareTo(new BigDecimal("2.00")) < 0) {
            throw error(HttpStatus.BAD_REQUEST, "Creation requires at least USD 2");
        }
        Account usd = lockUsd(user);
// The USD row serializes competing creations for this owner.
        if (cards.existsByOwnerUid(user.getUid())) {
            throw error(HttpStatus.CONFLICT, "You already have a card");
        }
        enough(usd.getBalance(), amount);
        BigDecimal funding = amount.subtract(ISSUE_FEE);
        Card card = Card.builder().owner(usd.getOwner())
                .cardHolder(user.getFirstname() + " " + user.getLastname())
                .cardNumber(newCardNumber()).balance(funding)
                .exp(LocalDateTime.now().plusYears(3)).build();
// Fictional identifier only; no CVV, PIN or payment-network integration.
        usd.setBalance(usd.getBalance().subtract(amount));
        cards.saveAndFlush(card);
        List<Transaction> entries = List.of(
                transactions.accountEntry(usd, ISSUE_FEE, BigDecimal.ZERO,
                        Type.WITHDRAW, null, null),
                transactions.accountEntry(usd, funding, BigDecimal.ZERO,
                        Type.WITHDRAW, null, null),
                transactions.cardEntry(card, funding, Type.CREDIT));
        transactions.persist(entries);
        return response("CREATE", amount, ISSUE_FEE, usd, card, entries);
    }

    @Transactional
    public CardOperationResponse credit(CardAmountDto dto, User user) {
        return move(dto, user, true);
    }

    @Transactional
    public CardOperationResponse debit(CardAmountDto dto, User user) {
        return move(dto, user, false);
    }

    private CardOperationResponse move(CardAmountDto dto, User user,
                                       boolean funding) {
        BigDecimal amount = amount(dto);
// All card writes lock USD first, then the card. Never reverse it.
        Account usd = lockUsd(user);
        Card card = cards.findLockedByOwnerUid(user.getUid())
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND,
                        "You have not created a card yet"));
        if (funding) {
            enough(usd.getBalance(), amount);
            limit(card.getBalance().add(amount));
            usd.setBalance(usd.getBalance().subtract(amount));
            card.setBalance(card.getBalance().add(amount));
        } else {
            enough(card.getBalance(), amount);
            limit(usd.getBalance().add(amount));
            card.setBalance(card.getBalance().subtract(amount));
            usd.setBalance(usd.getBalance().add(amount));
        }
        List<Transaction> entries = List.of(
                transactions.accountEntry(usd, amount, BigDecimal.ZERO,
                        funding ? Type.WITHDRAW : Type.DEPOSIT, null, null),
                transactions.cardEntry(card, amount,
                        funding ? Type.CREDIT : Type.DEBIT));
// These entities were loaded inside this transaction and are managed.
        transactions.persist(entries);
        return response(funding ? "CREDIT" : "DEBIT", amount,
                BigDecimal.ZERO, usd, card, entries);
    }
    private Account lockUsd(User user) {
        long number = accounts.findNumberForOwner("USD", user.getUid())
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND,
                        "Create your USD account first"));
        Account account = accounts.findLocked(number)
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "USD account missing"));
        if (!account.getOwner().getUid().equals(user.getUid())) {
            throw error(HttpStatus.FORBIDDEN, "Invalid account owner");
        }
        return account;
    }
    private BigDecimal amount(CardAmountDto dto) {
        if (dto.amount() == null || dto.amount().signum() <= 0) {
            throw error(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }
        try {
            return dto.amount().setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw error(HttpStatus.BAD_REQUEST, "Use at most two decimal places");
        }
    }
    private void enough(BigDecimal balance, BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw error(HttpStatus.CONFLICT, "Insufficient funds");
        }
    }
    private void limit(BigDecimal balance) {
        if (balance.compareTo(MAX_BALANCE) > 0) {
            throw error(HttpStatus.CONFLICT, "Destination balance limit exceeded");
        }
    }
    private long newCardNumber() {
        for (int attempt = 0; attempt < 20; attempt++) {
            long number = random.generateRandom(16);
            if (!cards.existsByCardNumber(number)) return number;
        }
        throw error(HttpStatus.SERVICE_UNAVAILABLE,
                "Could not allocate a demo card number; try again later");
    }
    private CardOperationResponse response(String operation,
                                           BigDecimal amount, BigDecimal fee, Account usd, Card card,
                                           List<Transaction> entries) {
        return new CardOperationResponse(operation, amount, fee,
                usd.getBalance(), CardResponse.from(card),
                entries.stream().map(Transaction::getTxId).toList());
    }
    private ResponseStatusException error(HttpStatus status, String message) {
            return new ResponseStatusException(status, message);
        }
    }



