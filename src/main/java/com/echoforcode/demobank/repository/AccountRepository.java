package com.echoforcode.demobank.repository;

import com.echoforcode.demobank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {

}

