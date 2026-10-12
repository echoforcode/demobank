package com.echoforcode.demobank.service;

import com.echoforcode.demobank.dto.TransactionPage;
import com.echoforcode.demobank.entity.*;
import com.echoforcode.demobank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {
    private final TransactionRepository transactions;
    public TransactionPage all(String page, User user) {
        return TransactionPage.from(transactions.findAllByOwnerUid(
                user.getUid(), pageable(page)));
    }
    public TransactionPage byCard(String id, String page, User user) {
        return TransactionPage.from(
                transactions.findAllByCardCardIdAndOwnerUid(
                        id, user.getUid(), pageable(page)));
    }
    public TransactionPage byAccount(String id, String page, User user) {
        return TransactionPage.from(
                transactions.findAllByAccountAccountIdAndOwnerUid(
                        id, user.getUid(), pageable(page)));
    }
    private Pageable pageable(String value) {
        int page;
        try {
            page = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "page must be a non-negative integer");
        }
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "page must be a non-negative integer");
        }
        return PageRequest.of(page, 10,
                Sort.by(Sort.Direction.DESC, "createdAt", "txId"));
    }
    public Transaction accountEntry(Account account, BigDecimal amount,
                                    BigDecimal fee, Type type, String sender, String receiver) {
        return Transaction.builder().account(account).owner(account.getOwner())
                .amount(amount).txFee(fee).type(type).status(Status.COMPLETED)
                .sender(sender).receiver(receiver).build();
    }
    public Transaction cardEntry(Card card, BigDecimal amount, Type type) {
        return Transaction.builder().card(card).owner(card.getOwner())
                .amount(amount).txFee(BigDecimal.ZERO).type(type)
                .status(Status.COMPLETED).build();
    }
    /** Joins the caller's money operation; never commits history alone. */
    @Transactional(propagation = Propagation.MANDATORY)
    public void persist(List<Transaction> entries) {
        transactions.saveAllAndFlush(entries);
    }
}