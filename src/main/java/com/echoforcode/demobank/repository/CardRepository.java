package com.echoforcode.demobank.repository;


import com.echoforcode.demobank.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CardRepository
        extends JpaRepository<Card, String> {
}
