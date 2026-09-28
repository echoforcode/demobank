package com.echoforcode.demobank.repository;

import com.echoforcode.demobank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TransactionRepository
        extends JpaRepository<Transaction, String> {
}
