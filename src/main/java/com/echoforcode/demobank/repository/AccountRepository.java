package com.echoforcode.demobank.repository;

import com.echoforcode.demobank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByAccountNumber(long accountNumber);
    boolean existsByCodeAndOwnerUid(String code, String uid);
    List<Account> findAllByOwnerUidOrderByCodeAsc(String uid);
    // Introduced while preparing transfers near the end of this hour.
    Optional<Account> findByCodeAndOwnerUid(String code, String uid);
    Optional<Account> findByAccountNumber(long accountNumber);

}

