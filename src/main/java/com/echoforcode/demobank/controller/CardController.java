package com.echoforcode.demobank.controller;

import com.echoforcode.demobank.dto.CardResponse;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.service.CardService;
import com.echoforcode.demobank.dto.CardAmountDto;
import com.echoforcode.demobank.dto.CardOperationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CardOperationResponse create(@Valid @RequestBody CardAmountDto dto,
                                        @AuthenticationPrincipal User user) {
        return cardService.create(dto, user);
    }
    @PostMapping("/credit")
    public CardOperationResponse credit(@Valid @RequestBody CardAmountDto dto,
                                        @AuthenticationPrincipal User user) {
        return cardService.credit(dto, user);
    }
    @PostMapping("/debit")
    public CardOperationResponse debit(@Valid @RequestBody CardAmountDto dto,
                                       @AuthenticationPrincipal User user) {
        return cardService.debit(dto, user);
    }
}