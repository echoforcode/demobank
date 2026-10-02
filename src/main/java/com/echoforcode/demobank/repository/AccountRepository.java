package com.echoforcode.demobank.repository;

import com.echoforcode.demobank.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByAccountNumber(long accountNumber);
    boolean existsByCodeAndOwnerUid(String code, String uid);
    List<Account> findAllByOwnerUidOrderByCodeAsc(String uid);
    // Introduced while preparing transfers near the end of this hour.
    Optional<Account> findByCodeAndOwnerUid(String code, String uid);
    Optional<Account> findByAccountNumber(long accountNumber);
    @Query("select a.accountNumber from Account a " +
            "where a.code = :code and a.owner.uid = :uid")
    Optional<Long> findNumberForOwner(@Param("code") String code,
                                      @Param("uid") String uid);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountNumber = :number")
    Optional<Account> findLocked(@Param("number") long number);

}

