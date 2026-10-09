package com.echoforcode.demobank.service;

import com.echoforcode.demobank.dto.CardResponse;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cards;
    @Transactional(readOnly = true)
    public CardResponse getCard(User user) {
        return cards.findByOwnerUid(user.getUid()).map(CardResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "You have not created a card yet"));
    }
}