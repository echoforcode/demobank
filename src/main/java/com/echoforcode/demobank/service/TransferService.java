package com.echoforcode.demobank.service;

import com.echoforcode.demobank.dto.TransferDto;
import com.echoforcode.demobank.dto.TransferResponse;
import com.echoforcode.demobank.entity.*;
import com.echoforcode.demobank.repository.AccountRepository;
import com.echoforcode.demobank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
@Service
@RequiredArgsConstructor
public class TransferService {
    private final AccountRepository accounts;
    private final TransactionRepository transactions;
    private static final BigDecimal FEE_RATE = new BigDecimal("0.01");
    private static final BigDecimal MAX_BALANCE =
            new BigDecimal("99999999999999999.99");
    @Transactional
    public TransferResponse transfer(TransferDto dto, User user) {
        String code = dto.code().toUpperCase(Locale.ROOT);
        long senderNumber = accounts.findNumberForOwner(code, user.getUid())
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND,
                        "You have no account in this currency"));
        long recipientNumber = dto.recipientAccountNumber();
        if (senderNumber == recipientNumber) {
            throw error(HttpStatus.BAD_REQUEST, "Choose another account");
        }
// Always lock the smaller number first, even in reverse transfers.
        Account first = lock(Math.min(senderNumber, recipientNumber));
        Account second = lock(Math.max(senderNumber, recipientNumber));
        Account sender = senderNumber == first.getAccountNumber()
                ? first : second;
        Account recipient = sender == first ? second : first;
        if (!sender.getOwner().getUid().equals(user.getUid())) {
            throw error(HttpStatus.FORBIDDEN, "Invalid account owner");
        }
        if (!sender.getCode().equals(recipient.getCode())) {
            throw error(HttpStatus.BAD_REQUEST,
                    "Transfer requires matching currencies");
        }
        int scale = code.equals("JPY") ? 0 : 2;
        BigDecimal amount;
        try {
            amount = dto.amount().setScale(scale, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw error(HttpStatus.BAD_REQUEST,
                    "JPY transfers must use whole yen");
        }
        if (amount.signum() <= 0) {
            throw error(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }
        BigDecimal fee = amount.multiply(FEE_RATE)
                .setScale(scale, RoundingMode.HALF_UP);
        BigDecimal debit = amount.add(fee);
        if (sender.getBalance().compareTo(debit) < 0) {
            throw error(HttpStatus.CONFLICT, "Insufficient funds including fee");
        }
        BigDecimal credited = recipient.getBalance().add(amount);
        if (credited.compareTo(MAX_BALANCE) > 0) {
            throw error(HttpStatus.CONFLICT, "Recipient balance limit exceeded");
        }
        sender.setBalance(sender.getBalance().subtract(debit));
        recipient.setBalance(credited);
// Both accounts are managed; JPA writes their changes on commit.
        Transaction withdrawal = entry(sender, sender, recipient,
                amount, fee, Type.WITHDRAW);
        Transaction deposit = entry(recipient, sender, recipient,
                amount, BigDecimal.ZERO, Type.DEPOSIT);
        transactions.saveAllAndFlush(List.of(withdrawal, deposit));
        return new TransferResponse(withdrawal.getTxId(), code, amount,
                fee, debit, sender.getBalance(), recipientNumber, "COMPLETED");
    }
    private Account lock(long number) {
        return accounts.findLocked(number).orElseThrow(() ->
                error(HttpStatus.NOT_FOUND, "Account not found"));
    }
    private Transaction entry(Account account, Account sender,
                              Account recipient, BigDecimal amount, BigDecimal fee, Type type) {
        return Transaction.builder()
                .account(account).owner(account.getOwner())
                .sender(Long.toString(sender.getAccountNumber()))
                .receiver(Long.toString(recipient.getAccountNumber()))
                .amount(amount).txFee(fee).type(type)
                .status(Status.COMPLETED).build();
    }
    private ResponseStatusException error(HttpStatus status, String message) {
            return new ResponseStatusException(status, message);
    }

}
