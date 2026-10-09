package com.echoforcode.demobank.controller;

import com.echoforcode.demobank.dto.CardResponse;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    @GetMapping
    public CardResponse getCard(@AuthenticationPrincipal User user) {
        return cardService.getCard(user);
    }
}