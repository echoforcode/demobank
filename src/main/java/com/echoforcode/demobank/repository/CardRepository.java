package com.echoforcode.demobank.repository;


import com.echoforcode.demobank.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface CardRepository
        extends JpaRepository<Card, String> {
    Optional<Card> findByOwnerUid(String uid);
    boolean existsByCardNumber(long cardNumber);
    boolean existsByOwnerUid(String uid);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Card c where c.owner.uid = :uid")
    Optional<Card> findLockedByOwnerUid(@Param("uid") String uid);
}
