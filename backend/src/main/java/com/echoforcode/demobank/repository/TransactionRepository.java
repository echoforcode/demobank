package com.echoforcode.demobank.repository;

import com.echoforcode.demobank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionRepository
        extends JpaRepository<Transaction, String> {
    Page<Transaction> findAllByOwnerUid(String uid, Pageable pageable);
    Page<Transaction> findAllByCardCardIdAndOwnerUid(
            String cardId, String uid, Pageable pageable);
    Page<Transaction> findAllByAccountAccountIdAndOwnerUid(
            String accountId, String uid, Pageable pageable);
}
