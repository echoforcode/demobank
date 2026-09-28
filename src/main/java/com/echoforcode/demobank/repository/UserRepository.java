package com.echoforcode.demobank.repository;

import com.echoforcode.demobank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsernameIgnoreCase(String username);
}
