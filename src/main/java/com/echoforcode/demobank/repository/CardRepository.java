package com.echoforcode.demobank.repository;


import com.echoforcode.demobank.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CardRepository
        extends JpaRepository<Card, String> {
    Optional<Card> findByOwnerUid(String uid);
    boolean existsByCardNumber(long cardNumber);
}
